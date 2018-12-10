package com.wisdom.app.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.wisdom.app.activity.ManualCheckLoadActivity;
import com.wisdom.app.activity.ParameterSettingActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.activityResult.ZouZiResultActivity;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.Blue;
import com.wisdom.app.utils.ByteUtil;
import com.wisdom.app.utils.DataService;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.bean.DianbiaoZouZiBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.IZouZiDao;
import com.wisdom.dao.ZouZiDao;
import com.wisdom.layout.ITypePopupWindow;
import com.wisdom.layout.TitleLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author JinJingYun 虚负荷手动校验-走字试验
 */
public class MCL_ZouZiTestFragment extends Fragment {
	private String TAG = "MCNL_ZouZiTestFragment";
	@Bind(R.id.title_MCNL_zouzi)
	TitleLayout title;
	// 按钮
	@Bind(R.id.dianbiao_canshu)
	Button btn_dianbiao_canshu;
	@Bind(R.id.jiaoyan_jieguo)
	Button btn_jiaoyan_jieguo;
	@Bind(R.id.start_test)
	Button btn_start_test;// 启动校验
	@Bind(R.id.stop_test)
	Button btn_stop_test;// 停止校验
	@Bind(R.id.meter1)
	Button btn_meter1;
	@Bind(R.id.meter2)
	Button btn_meter2;
	@Bind(R.id.meter3)
	Button btn_meter3;

	// 电压电流处
	@Bind(R.id.chooser_u)
	TextView tv_chooser_u;// 电压左侧
	@Bind(R.id.chooser_i)
	TextView tv_chooser_i;// 电流左侧

	@Bind(R.id.chooser_p)
	TextView tv_jiaodu;// 项角
	@Bind(R.id.chooser_f)
	TextView tv_chooser_f;// 频率

	@Bind(R.id.show_p)
	TextView tv_show_p;// A项有功
	@Bind(R.id.show_q)
	TextView tv_show_q;// A项无功
	@Bind(R.id.show_s)
	TextView tv_show_s;// 总攻
	@Bind(R.id.show_pf)
	TextView tv_show_pf;// 功率因数

	//上四行
	@Bind(R.id.zouzi_fangshi)
	Spinner sp_zouzifangshi;
	@Bind(R.id.zouzi_related_column_value)
	EditText et_yuzhidianneng;
	@Bind(R.id.input_beilv)
	EditText et_beilv;
	@Bind(R.id.standard_energy)
	TextView tv_biaozhundianneng;
	//上右侧
	@Bind(R.id.start_energy1)
	TextView tv_qishi1;
	@Bind(R.id.end_energy1)
	TextView tv_jieshu1;
	@Bind(R.id.actual_energy1)
	TextView tv_shiji1;
	@Bind(R.id.zouzi_error1)
	TextView tv_zouzi1;
	
	@Bind(R.id.start_energy2)
	TextView tv_qishi2;
	@Bind(R.id.end_energy2)
	TextView tv_jieshu2;
	@Bind(R.id.actual_energy2)
	TextView tv_shiji2;
	@Bind(R.id.zouzi_error2)
	TextView tv_zouzi2;
	
	@Bind(R.id.start_energy3)
	TextView tv_qishi3;
	@Bind(R.id.end_energy3)
	TextView tv_jieshu3;
	@Bind(R.id.actual_energy3)
	TextView tv_shiji3;
	@Bind(R.id.zouzi_error3)
	TextView tv_zouzi3;
	@Bind(R.id.zouzi_related_column)
	TextView tv_zouzi_related_column;//预置电能文本框
	@Bind(R.id.dianya_yuan)TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)TextView tv_dianliuyuan;
	@Bind(R.id.btn_select_i_type)
	Button btn_select_i_type;
	@Bind(R.id.run_time)TextView tv_shengyushijian;
	@Bind(R.id.progressbar)
	ProgressBar bar;
	private ITypePopupWindow popupwindow;
	private SharedPreferences sharedPreferences;
	private SharedPreferences jibenwucha;
	private SharedPreferences dianbiaocanshu;
	private Timer timer;
	private boolean send_state=false;
	private boolean recv_state=false;
	private boolean test_state=false;
	private boolean save_zouzi=false;
	//收发数据
	private BluetoothGattCharacteristic characteristic;
	private ALTEK altek = new ALTEK();
	private DataService ds = new DataService();
	int[] iTempBuf = new int[10];
	int iTxLen = 0;
	int iStart = 0, iEnd = 0, iLen = 0, i = 0;
	String strCurTx = null;
	String str = null;
	
	//基础数据
	private int iZouzifangshi;
	private int iYuzhidianneng;
	private int iBeilv;
	
	//部分电表参数,校验结果
	private double Mdianya=220;
	private double Mdianliu=10;
	private String Mjiaodu="0";
	private String Mpinlv="0";
	
	private int calc_time;
	private String result1;
	private String result2;
	private String result3;
	int iType = 0;// 电流采样方式,-1为设置
	private IZouZiDao dao;
	private TaitiCeLiangShuJuBean bean;
	private DianbiaoZouZiBean qiandongBean=new DianbiaoZouZiBean();
	int time=0;
	int maxtime=0;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x001) {
            	try
            	{
            	bean=(TaitiCeLiangShuJuBean) msg.obj;
            	if(bean!=null)
            	{
            		tv_chooser_u.setText(bean.getU());
            		tv_chooser_i.setText(bean.getI());
            		tv_chooser_f.setText(bean.getPinlv());
            		tv_show_p.setText(bean.getYougong());
            		tv_show_q.setText(bean.getWugong());
            		tv_show_s.setText(bean.getZonggong());
            		tv_show_pf.setText(bean.getGonglvyinshu());
            		tv_jiaodu.setText(bean.getJiaodu());
            	}
            	}catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }
            else if(msg.what==0x002)//走字
            {
            	try
            	{
            	DianbiaoZouZiBean zouziBean=(DianbiaoZouZiBean)msg.obj;
            	if(zouziBean!=null)
            	{
            		if(test_state)
            		{
            			if(time==0&&(Integer.valueOf(zouziBean.getTime())>20))
						{
							time=Integer.valueOf(zouziBean.getTime());
							if(maxtime<time)
								maxtime=time;
						}
						setBarProgress(Integer.valueOf(zouziBean.getTime()));
						tv_shengyushijian.setText(zouziBean.getTime());
            		//上右侧四行
            		tv_biaozhundianneng.setText(zouziBean.getBiaozhundianneng());
            		tv_qishi1.setText(zouziBean.getQishi1());
            		tv_qishi2.setText(zouziBean.getQishi2());
            		tv_qishi3.setText(zouziBean.getQishi3());
            		tv_jieshu1.setText(zouziBean.getJieshu1());
            		tv_jieshu2.setText(zouziBean.getJieshu2());
            		tv_jieshu3.setText(zouziBean.getJieshu3());
            		tv_shiji1.setText(zouziBean.getShiji1());
            		tv_shiji2.setText(zouziBean.getShiji2());
            		tv_shiji3.setText(zouziBean.getShiji3());
            		tv_zouzi1.setText(zouziBean.getWucha1());
            		tv_zouzi2.setText(zouziBean.getWucha2());
            		tv_zouzi3.setText(zouziBean.getWucha3());
            		if(Integer.valueOf(zouziBean.getJieguo())==1)
            		{
            			btn_start_test.setEnabled(true);
            			Toast.makeText(getActivity(), "测试完成", Toast.LENGTH_SHORT).show();
            			time=0;
            			maxtime=0;
        				bar.setProgress(100);
            			qiandongBean.setU(MissionSingleInstance.getSingleInstance().getU());
                		qiandongBean.setI(MissionSingleInstance.getSingleInstance().getI());
                		qiandongBean.setJiaodu(MissionSingleInstance.getSingleInstance().getJiaodu());
                		qiandongBean.setYougong(bean.getYougong());
                		qiandongBean.setWugong(bean.getWugong());
                		qiandongBean.setGonglvyinshu(bean.getGonglvyinshu());
                		qiandongBean.setZouzifangshi(sp_zouzifangshi.getSelectedItem().toString());
    					qiandongBean.setYuzhidianneng(et_yuzhidianneng.getText().toString());
    					qiandongBean.setBeilv(et_beilv.getText().toString());
    					
    					qiandongBean.setBiaozhundianneng(tv_biaozhundianneng.getText().toString());
    					qiandongBean.setQishi1(tv_qishi1.getText().toString());
    					qiandongBean.setQishi2(tv_qishi2.getText().toString());
    					qiandongBean.setQishi3(tv_qishi3.getText().toString());
    					
    					qiandongBean.setJieshu1(tv_jieshu1.getText().toString());
    					qiandongBean.setJieshu2(tv_jieshu2.getText().toString());
    					qiandongBean.setJieshu3(tv_jieshu3.getText().toString());
    					
    					qiandongBean.setShiji1(tv_shiji1.getText().toString());
    					qiandongBean.setShiji2(tv_shiji2.getText().toString());
    					qiandongBean.setShiji3(tv_shiji3.getText().toString());
    					
    					qiandongBean.setWucha1(tv_zouzi1.getText().toString());
    					qiandongBean.setWucha2(tv_zouzi2.getText().toString());
    					qiandongBean.setWucha3(tv_zouzi3.getText().toString());
    					dao.add(qiandongBean);
                		test_state=false;
            		}
            		}
            	}
            	
            	}catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }
            else if(msg.what==0x003)
            {
				if (ManualCheckLoadActivity.METHOD == 1)
					Toast.makeText(getActivity(), "直接接入模式已打开", Toast.LENGTH_SHORT).show();
				else if (ManualCheckLoadActivity.METHOD == 3)
					Toast.makeText(getActivity(), "100A电流钳模式已打开", Toast.LENGTH_SHORT).show();
				else if (ManualCheckLoadActivity.METHOD == 7)
					Toast.makeText(getActivity(), "5A电流钳模式已打开", Toast.LENGTH_SHORT).show();

			}
          
        }
    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_mcl_zouzi_test, null);
		ButterKnife.bind(this, view);
		title = (TitleLayout) view.findViewById(R.id.title_MCNL_zouzi);
		title.setTitleText("实负荷手动校验->走字试验");
		initView();
		initData();
		btn_meter1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv_qishi1.setVisibility(View.VISIBLE);
				tv_jieshu1.setVisibility(View.VISIBLE);
				tv_shiji1.setVisibility(View.VISIBLE);
				tv_zouzi1.setVisibility(View.VISIBLE);
				
				tv_qishi2.setVisibility(View.GONE);
				tv_jieshu2.setVisibility(View.GONE);
				tv_shiji2.setVisibility(View.GONE);
				tv_zouzi2.setVisibility(View.GONE);
				
				tv_qishi3.setVisibility(View.GONE);
				tv_jieshu3.setVisibility(View.GONE);
				tv_shiji3.setVisibility(View.GONE);
				tv_zouzi3.setVisibility(View.GONE);
			}
		});
		btn_meter2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv_qishi1.setVisibility(View.GONE);
				tv_jieshu1.setVisibility(View.GONE);
				tv_shiji1.setVisibility(View.GONE);
				tv_zouzi1.setVisibility(View.GONE);
				
				tv_qishi2.setVisibility(View.VISIBLE);
				tv_jieshu2.setVisibility(View.VISIBLE);
				tv_shiji2.setVisibility(View.VISIBLE);
				tv_zouzi2.setVisibility(View.VISIBLE);
				
				tv_qishi3.setVisibility(View.GONE);
				tv_jieshu3.setVisibility(View.GONE);
				tv_shiji3.setVisibility(View.GONE);
				tv_zouzi3.setVisibility(View.GONE);
			}
		});
		btn_meter3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv_qishi1.setVisibility(View.GONE);
				tv_jieshu1.setVisibility(View.GONE);
				tv_shiji1.setVisibility(View.GONE);
				tv_zouzi1.setVisibility(View.GONE);
				
				tv_qishi2.setVisibility(View.GONE);
				tv_jieshu2.setVisibility(View.GONE);
				tv_shiji2.setVisibility(View.GONE);
				tv_zouzi2.setVisibility(View.GONE);
				
				tv_qishi3.setVisibility(View.VISIBLE);
				tv_jieshu3.setVisibility(View.VISIBLE);
				tv_shiji3.setVisibility(View.VISIBLE);
				tv_zouzi3.setVisibility(View.VISIBLE);
			}
		});
		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(getActivity(),ZouZiResultActivity.class);
					startActivity(intent);
				
				}
			});
	      
	        btn_start_test.setOnClickListener(new View.OnClickListener() {
			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String yuzhidianneng=et_yuzhidianneng.getText().toString();
					String beilv=et_beilv.getText().toString();
					if(yuzhidianneng.equals("")||beilv.equals(""))
						Toast.makeText(getActivity(), "参数不能为空", Toast.LENGTH_SHORT).show();
					else
					{
					initTopView();
					saveTaitiData();
					recv_state=false;
					test_state=true;
					send_state=true;
					bar.setProgress(0);
					Toast.makeText(getActivity(), "测试开始", Toast.LENGTH_SHORT).show();
					//btn_start_test.setEnabled(false);
					}
				}
			});
	        btn_stop_test.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try{
						test_state=false;
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
	    sp_zouzifangshi.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					iZouzifangshi=sp_zouzifangshi.getSelectedItemPosition();
					if(iZouzifangshi==0)
					{
						tv_zouzi_related_column.setText("预置电能[0.01kW·h]");
					}
					else if(iZouzifangshi==1)
					{
						tv_zouzi_related_column.setText("时长[s]");
					}
					else if(iZouzifangshi==2)
					{
						tv_zouzi_related_column.setText("脉冲数");
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
	        	
	        });
	    btn_dianbiao_canshu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ParameterSettingActivity.class);
				startActivity(intent);
			}
		});
	    btn_select_i_type.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupwindow = new ITypePopupWindow(getActivity(), itemsOnClick);
				// 显示窗口
				popupwindow.showAtLocation(getActivity().findViewById(R.id.mcl_layout),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
	        return view;
	}
	private void initTopView()
	{
		tv_shengyushijian.setText("");
		//上右侧四行
		tv_biaozhundianneng.setText("");
		tv_qishi1.setText("");
		tv_qishi2.setText("");
		tv_qishi3.setText("");
		tv_jieshu1.setText("");
		tv_jieshu2.setText("");
		tv_jieshu3.setText("");
		tv_shiji1.setText("");
		tv_shiji2.setText("");
		tv_shiji3.setText("");
		tv_zouzi1.setText("");
		tv_zouzi2.setText("");
		tv_zouzi3.setText("");
	}
	private void initView() {
		jibenwucha=getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE);
		sharedPreferences = getActivity().getSharedPreferences("MCNL_ZouZi", Context.MODE_PRIVATE); // 私有数据
		dianbiaocanshu=getActivity().getSharedPreferences("ParameterSetting",Context.MODE_PRIVATE);
		if(MissionSingleInstance.getSingleInstance().isYuan_state())
    	{
    		tv_dianyayuan.setText("电压源:已输出");
			tv_dianliuyuan.setText("电流源:已输出");
			
    	}
    	else if(!MissionSingleInstance.getSingleInstance().isYuan_state())
    	{
    		tv_dianyayuan.setText("电压源:已停止");
			tv_dianliuyuan.setText("电流源:已停止");
			
    	} 
	}
	private void initData() {
		try
		{
	    dao=new ZouZiDao(getActivity());
		
		// tv_chooser_u.setText(getDouble(sp_U.getSelectedItem().toString(),220));
		// int length=sp_chooser_i.getSelectedItem().toString().length();
		// tv_chooser_i.setText(getDouble(sp_I.getSelectedItem().toString(),Integer.valueOf(sp_chooser_i.getSelectedItem().toString().substring(0,length-1))));
	/*	BluetoothGattService service = MainActivity.mBluetoothLeService.getGattService(BLEActivity.SERVICE_UUID);
		characteristic = service.getCharacteristic(UUID.fromString(BLEActivity.HEART_RATE_MEASUREMENT));
		MainActivity.mBluetoothLeService.setCharacteristicNotification(characteristic);
		getActivity().registerReceiver(mGattUpdateReceiver, MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
		*/
	    //Mdianliu=Double.valueOf(dianbiaocanshu.getString("base_current","10"));
		//Mjiaodu=jibenwucha.getString("sp_chooser_p", "0");
		//Mpinlv=jibenwucha.getString("et_chooser_f", "50");
		timer=new Timer();
    	timer.schedule(new SendDataTask(), 1000, 1000);
    	recv_state=true;
		getTaitiData();
		}catch(Exception ex)
		{
			Toast.makeText(getActivity(), "请先连接蓝牙设备", Toast.LENGTH_SHORT).show();
		}
	}
	private void getTaitiData()
	{
		String str_zouzifangshi = sharedPreferences.getString("sp_zouzifangshi", "起止电量");
		String str_yuzhidianneng = sharedPreferences.getString("et_yuzhidianneng", "1200");
		String str_beilv = sharedPreferences.getString("et_beilv", "1");
		
		if (str_zouzifangshi != "")
			setSpinnerSelection(sp_zouzifangshi, str_zouzifangshi);
		
		if (str_yuzhidianneng != "")
			et_yuzhidianneng.setText(str_yuzhidianneng);
		if (str_beilv != "")
			et_beilv.setText(str_beilv);
	}
	private void saveTaitiData() {
		try {
			Editor editor = sharedPreferences.edit();// 获取编辑器
			String zouzifangshi=sp_zouzifangshi.getSelectedItem().toString();
			
			//iZouzifangshi=Integer.valueOf(zouzifangshi);
			String yuzhidianneng=et_yuzhidianneng.getText().toString();
			iYuzhidianneng=(int) (Double.valueOf(yuzhidianneng)*100);
			String beilv=et_beilv.getText().toString();
			iBeilv=Integer.valueOf(beilv);
			editor.putString("sp_zouzifangshi",zouzifangshi);
			editor.putString("et_yuzhidianneng",yuzhidianneng);
			editor.putString("et_beilv",beilv);
			editor.commit();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/*
	 * 设置Spinner默认item
	 * item 字符串，需要与spinner的item相同
	 */
	private void setSpinnerSelection(Spinner spinner, String item) {
		SpinnerAdapter apsAdapter = spinner.getAdapter();
		int k = apsAdapter.getCount();
		for (int i = 0; i < k; i++) {
			if (item.equals(apsAdapter.getItem(i).toString())) {
				spinner.setSelection(i, true);
				break;
			}
		}
	}
	private void setBarProgress(int leftSecond)
	{
		if(time!=0)
		{
		int quarter=10000/time;
		bar.setProgress((time-leftSecond)*quarter);
		}
	}
	/**
	 * 获取走字方式的int格式
	 * @return  0起止电量 1走字定时 2脉冲计数
	 */
	private int getZouZiInt(String str) {
		return 1;
	}
/*	private void fnSendBytes(byte[] frame) {
		iTxLen = 0;
		str = ByteUtil.byte2HexStr(frame);
		str = str.toUpperCase();
		iLen = str.length();
		iStart = 0;
		iEnd = 0;
		// 一次最多发送20字节，分开发送
		for (i = iStart; i < iLen;) {
			if (iLen < (i + 40)) {
				strCurTx = str.substring(i, iLen);
			} else {
				strCurTx = str.substring(i, i + 40);
			}
			if (characteristic != null) {
				characteristic.setValue(ByteUtil.hexStringToByte(strCurTx));
				MainActivity.mBluetoothLeService.writeCharacteristic(characteristic);
			}
			i += 40;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (i > iLen)
				break;
			else
				continue;
		}
	}*/
	/**
	 * 这个是真的广播接收器
	 */
/*	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		String strRx = null, str2 = null;
		int iRxDataLen = 0;

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// 连接成功更新界面顶部字体
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))// Gatt连接成功
			{

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))// Gatt连接失败
			{

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))// 发现GATT服务器
			{
			
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))// 有效数据
			{
				// 处理发送过来的数据
				strRx = intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
				// displayData(strRx); //接收报文区域//接收处理
				iRxDataLen = strRx.length();

				byte[] data=ds.fnReceiveData(strRx, iRxDataLen);
				if(data!=null)
				{
					if((data[2]&0x7f)==0x61)//实时台体测量数据
		    		{
		    			TaitiCeLiangShuJuBean bean=ds.fnGetTaitiData(data);
		    			if(bean!=null)
						{
							Message msg = new Message();
							msg.what = 0x001;
							msg.obj = bean;
							handler.sendMessage(msg);
						}
		    		}
					if((data[2]&0x7f)==0x6B)//电表走字测试
		    		{
		    			DianbiaoZouZiBean bean=ds.fnGetZouZiData(data);
		    			if(bean!=null)
						{
							Message msg = new Message();
							msg.what = 0x002;
							msg.obj = bean;
							handler.sendMessage(msg);
						}
		    		}
					if ((data[2] & 0x7f) == 0x5E)// 台体测试类型配置参数
					{
						if (ManualCheckLoadActivity.METHOD == 1)
							Toast.makeText(getActivity(), "直接接入模式已打开", Toast.LENGTH_SHORT).show();
						else if (ManualCheckLoadActivity.METHOD == 3)
							Toast.makeText(getActivity(), "100A电流钳模式已打开", Toast.LENGTH_SHORT).show();
						else if (ManualCheckLoadActivity.METHOD == 7)
							Toast.makeText(getActivity(), "5A电流钳模式已打开", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};*/
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) { 
			// 不在最前端显示 相当于调用了onPause();
			Log.i(TAG,"onHidden()");
			try{
				//getActivity().unregisterReceiver(mGattUpdateReceiver);
				timer.cancel();
			}catch(Exception ex)
				{
					
				}
            return;
        }else{  // 在最前端显示 相当于调用了onResume();
        	Log.i(TAG,"onHiddenShow()");
        	//getActivity().registerReceiver(mGattUpdateReceiver, MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
        	timer=new Timer();
        	timer.schedule(new SendDataTask(), 1000, 1000);
        	if(MissionSingleInstance.getSingleInstance().isYuan_state())
        	{
        		tv_dianyayuan.setText("电压源:已输出");
    			tv_dianliuyuan.setText("电流源:已输出");
    			
        	}
        	else if(!MissionSingleInstance.getSingleInstance().isYuan_state())
        	{
        		tv_dianyayuan.setText("电压源:已停止");
    			tv_dianliuyuan.setText("电流源:已停止");
    			
        	} 
        }
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG,"onDestroy()");
		try{
			//getActivity().unregisterReceiver(mGattUpdateReceiver);
			timer.cancel();
		}catch(Exception ex)
			{
				
			}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG,"onPause()");
		saveTaitiData();
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume()");
		getTaitiData();
	}
	int x=0;
	private class SendDataTask extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (iType == -1) {
				Blue.send(altek.fnTaitiCeShiLeiXingPeiZhiCanshu(ManualCheckLoadActivity.METHOD, 0,
						Integer.valueOf(MCL_JiBenWuChaFragment.maichongchangshu), Integer.valueOf(MCL_JiBenWuChaFragment.maichongchangshu), ByteUtil.getMeterCount(getActivity()),
						MCL_JiBenWuChaFragment.meter1_no, MCL_JiBenWuChaFragment.meter2_no, MCL_JiBenWuChaFragment.meter3_no),handler);
				iType++;
			}
			if(recv_state)
			{
				
				if(MissionSingleInstance.getSingleInstance().isYuan_state())
				{
					if(x%2==0)
					{
						if(test_state)
						Blue.send(altek.fnDianbiaoZouZi(1,iZouzifangshi,iYuzhidianneng, iBeilv),handler);
					}
					else
						Blue.send(altek.fnGetFrameByFunctionCode((byte)0x61),handler);
					x++;
				}
			}
			else if(send_state)//执行一次后，执行查数据命令
			{
				Blue.send(altek.fnDianbiaoZouZi(0,iZouzifangshi,iYuzhidianneng, iBeilv),handler);
				send_state=false;
				recv_state=true;
				
			}
		
		}
 }
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			popupwindow.dismiss();
			switch (v.getId()) {
			case R.id.tv_zhijiejieru:
				btn_select_i_type.setText("直接接入式");
				ManualCheckLoadActivity.METHOD = 1;
				iType = -1;
				break;
			case R.id.tv_100A:
				btn_select_i_type.setText("100A电流钳");
				ManualCheckLoadActivity.METHOD = 3;
				iType = -1;
				break;
			case R.id.tv_5A:
				btn_select_i_type.setText("5A电流钳");
				ManualCheckLoadActivity.METHOD = 7;
				iType = -1;
				break;
			default:
				break;
			}

		}

	};
}
