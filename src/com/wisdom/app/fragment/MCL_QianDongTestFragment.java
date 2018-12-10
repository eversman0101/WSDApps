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
import com.wisdom.app.activityResult.QianDongResultActivity;
import com.wisdom.app.utils.*;
import com.wisdom.bean.QianDongBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.IQiandongDao;
import com.wisdom.dao.QianDongDao;
import com.wisdom.layout.ITypePopupWindow;
import com.wisdom.layout.TitleLayout;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
/**
 * @author JinJingYun 
 * 虚负荷手动校验-潜动试验
 * */
public class MCL_QianDongTestFragment extends Fragment{
	private String TAG="MCNL_QianDongTestFragment";
	@Bind(R.id.title_MCNL_qiandong)
	TitleLayout title;

	@Bind(R.id.calc_time)
	Button btn_calc_time;
	@Bind(R.id.dianbiao_canshu)
	Button btn_dianbiao_canshu;
	@Bind(R.id.jiaoyan_jieguo)
	Button btn_jiaoyan_jieguo;
	@Bind(R.id.start_test)
	Button btn_start_test;// 启动校验
	@Bind(R.id.stop_test)
	Button btn_stop_test;// 停止校验

	//电压电流处
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
	@Bind(R.id.dianya_yuan)TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)TextView tv_dianliuyuan;
	//上三行
	@Bind(R.id.creeping_voltage) Spinner sp_qiandongdianya;
	@Bind(R.id.creeping_current) Spinner sp_qiandongdianliu;
	@Bind(R.id.meter_constant) EditText et_dianbiaochangshu;
	@Bind(R.id.max_current) EditText et_zuidadianliu;
	@Bind(R.id.creeping_time) EditText et_qiandongshijian;
	@Bind(R.id.run_time) TextView tv_yunxingshijian;
	//中三行
	@Bind(R.id.result1) TextView tv_result1;
	@Bind(R.id.result2) TextView tv_result2;
	@Bind(R.id.result3) TextView tv_result3;
	@Bind(R.id.btn_select_i_type)
	Button btn_select_i_type;
	private SharedPreferences sharedPreferences;
	private SharedPreferences jibenwucha;
	private SharedPreferences dianbiaocanshu;

	//收发数据
	private BluetoothGattCharacteristic characteristic;
	private ALTEK altek = new ALTEK();
	private DataService ds = new DataService();
	int[] iTempBuf = new int[10];
	int iTxLen = 0;
	int iStart = 0, iEnd = 0, iLen = 0, i = 0;
	String strCurTx = null;
	String str = null;
	private Timer timer;  
	
	//基础数据-顶部
	private String Qdianya="0";
	private String Qdianliu="0";
	private String Qchangshu="0";
	private String Qzuidadianliu="0";
	
	//部分电表参数,校验结果
	private double Mdianya=220;
	private double Mdianliu=10;
	private String Mjiaodu="0";
	private String Mpinlv="50";
	
	private int calc_time;
	private String result1="未完成";
	private String result2="未完成";
	private String result3="未完成";
	
	private IQiandongDao dao;
	private TaitiCeLiangShuJuBean bean;
	private ITypePopupWindow popupwindow;
	private boolean send_state=false;
	private boolean recv_state=false;
	private boolean test_state=false;
	private int yuan_state=0;//源状态，0关闭 1输出
	int iOFF = 0;// 用来判断源是否真得停止
	int iType = 0;// 电流采样方式,-1为设置
	int maxtime=0;
	@Bind(R.id.progressbar)
	ProgressBar bar;
	int time=0;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x001) {
            	try
            	{
            	bean=(TaitiCeLiangShuJuBean) msg.obj;
            	if(bean!=null)
            	{
            		if (Double.valueOf(bean.getU()) > 0) {
						tv_dianyayuan.setText("电压源:已输出");

					}
					if (Double.valueOf(bean.getI()) > 0) {
						tv_dianliuyuan.setText("电流源:已输出");
					}
					if ((Double.valueOf(bean.getU()) == 0) && (Double.valueOf(bean.getI()) == 0)) {
						iOFF++;
						if (iOFF == 3)// 断定源已停止
						{
							
							tv_dianyayuan.setText("电压源:已停止");
							tv_dianliuyuan.setText("电流源:已停止");
							MissionSingleInstance.getSingleInstance().setYuan_state(false);
							iOFF = 0;
						}
					}
            		tv_chooser_u.setText(bean.getU());
            		tv_chooser_i.setText(bean.getI());
            		tv_chooser_f.setText(bean.getPinlv());
            		tv_show_p.setText(bean.getYougong());
            		tv_show_q.setText(bean.getWugong());
            		tv_show_s.setText(bean.getZonggong());
            		tv_show_pf.setText(bean.getGonglvyinshu());
            		tv_jiaodu.setText(bean.getJiaodu());
            		//上面三排
            		tv_yunxingshijian.setText(bean.getTime());
            		if(test_state)
            		{
            			if(time==0&&(Integer.valueOf(bean.getTime())>20))
						{
							time=Integer.valueOf(bean.getTime());
							if(maxtime<time)
								maxtime=time;
						}
						setBarProgress(Integer.valueOf(bean.getTime()));
						tv_yunxingshijian.setText(bean.getTime());
            			
                		if(Integer.valueOf(bean.getJieguo())==2)
            			{
                			Toast.makeText(getActivity(), "测试完成", Toast.LENGTH_SHORT).show();
            				test_state=false;
            				btn_start_test.setEnabled(true);
            				//recv_state=false;
            				bar.setProgress(100);
            				time=0;
            				maxtime=0;
            				try
            				{
            					if(Integer.valueOf(bean.getJieguo())==2&&Double.valueOf(bean.getWucha1()).intValue()==0)
                        		{
                        			result1="合格";
                        			tv_result1.setText(result1);
                        		}
                        		else if(Integer.valueOf(bean.getJieguo())==2&&Double.valueOf(bean.getWucha1()).intValue()==255)
                        		{
                        			result1="不合格";
                        			tv_result1.setText(result1);
                        		}
                        		if(Integer.valueOf(bean.getJieguo())==2&&Double.valueOf(bean.getWucha2()).intValue()==0)
                        		{
                        			if(ByteUtil.getMeterCount(getActivity())<2)
                        				result2="";
                        			else
                        			result2="合格";
                        			tv_result2.setText(result2);
                        		}
                        		else if(Integer.valueOf(bean.getJieguo())==2&&Double.valueOf(bean.getWucha2()).intValue()==255)
                        		{
                        			if(ByteUtil.getMeterCount(getActivity())<2)
                        				result2="";
                        			else
                        			result2="不合格";
                        			tv_result2.setText(result2);
                        		}
                        		if(Integer.valueOf(bean.getJieguo())==2&&Double.valueOf(bean.getWucha3()).intValue()==0)
                        		{
                        			if(ByteUtil.getMeterCount(getActivity())<3)
                        				result3="";
                        			else
                        			result3="合格";
                        			tv_result3.setText(result3);
                        		}
                        		else if(Integer.valueOf(bean.getJieguo())==2&&Double.valueOf(bean.getWucha3()).intValue()==255)
                        		{
                        			if(ByteUtil.getMeterCount(getActivity())<3)
                        				result3="";
                        			else
                        			result3="不合格";
                        			tv_result3.setText(result3);
                        		}
            				QianDongBean qiandongBean=new QianDongBean();
            				qiandongBean.setDate(ByteUtil.GetNowDate());
            				qiandongBean.setU(Qdianya);
            				qiandongBean.setI(Qdianliu);
            				qiandongBean.setChangshu(Qchangshu);
            				Log.i("TAG", "潜动时间："+calc_time);
            				qiandongBean.setQiandongshijian(String.valueOf(calc_time));
            				qiandongBean.setQiandongshiyan1(result1);
            				qiandongBean.setQiandongshiyan2(result2);
            				qiandongBean.setQiandongshiyan3(result3);
            				dao.add(qiandongBean);
            				}catch(Exception ex)
            				{
            					
            				}
            			}
                
            		}
            	}
            	}catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }
            else if(msg.what==0x002)
            {
            	
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
		View view=inflater.inflate(R.layout.fragment_mcl_qiandong_test, null);
		ButterKnife.bind(this, view);
		title=(TitleLayout)view.findViewById(R.id.title_MCNL_qiandong);
        title.setTitleText("实负荷手动校验->潜动测试");
		initView();
		initData();
        btn_dianbiao_canshu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ParameterSettingActivity.class);
				startActivity(intent);	
			}
		});
        sp_qiandongdianya.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Qdianya=getDoubleUn(sp_qiandongdianya.getSelectedItem().toString(),Mdianya);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        sp_qiandongdianliu.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Qdianliu=getDoubleIQ(sp_qiandongdianliu.getSelectedItem().toString(),Mdianliu);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
        btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Intent intent=new Intent(getActivity(),QianDongResultActivity.class);
				startActivity(intent);
				
			}
		});
        btn_calc_time.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				double changshu=Double.valueOf(et_dianbiaochangshu.getText().toString());
				double time=((600*1000)/(changshu*Mdianya*Mdianliu*0.001));
				calc_time=(int)time;
				//et_qiandongshijian.setText(String.format("%.1f", time));
				//calc_time=20*60;
				et_qiandongshijian.setText(String.valueOf(calc_time));
			}
		});
        btn_start_test.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveTaitiData();
				recv_state=false;
				test_state=true;
				send_state=true;
				bar.setProgress(0);
				Toast.makeText(getActivity(), "开始试验", Toast.LENGTH_SHORT).show();
			}
		});
        btn_stop_test.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				test_state=false;
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
	private void initView() {
		jibenwucha=getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE);
		sharedPreferences = getActivity().getSharedPreferences("MCNL_QianDong", Context.MODE_PRIVATE); // 私有数据
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
		dao=new QianDongDao(getActivity());
		
		// tv_chooser_u.setText(getDouble(sp_U.getSelectedItem().toString(),220));
		// int length=sp_chooser_i.getSelectedItem().toString().length();
		// tv_chooser_i.setText(getDouble(sp_I.getSelectedItem().toString(),Integer.valueOf(sp_chooser_i.getSelectedItem().toString().substring(0,length-1))));
		/*BluetoothGattService service = MainActivity.mBluetoothLeService.getGattService(BLEActivity.SERVICE_UUID);
		characteristic = service.getCharacteristic(UUID.fromString(BLEActivity.HEART_RATE_MEASUREMENT));
		MainActivity.mBluetoothLeService.setCharacteristicNotification(characteristic);
		getActivity().registerReceiver(mGattUpdateReceiver, MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
		*/
		Mdianliu=Double.valueOf(dianbiaocanshu.getString("base_current","10"));
		Mjiaodu=MissionSingleInstance.getSingleInstance().getJiaodu();
		Mpinlv=MissionSingleInstance.getSingleInstance().getPinlv();
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
		String str_TU = sharedPreferences.getString("sp_qiandongdianya", "");
		String str_TI = sharedPreferences.getString("sp_qiandongdianliu", "");
		String str_Tchangshu = sharedPreferences.getString("et_dianbiaochangshu", "");
		String str_Tzuidadianliu = sharedPreferences.getString("et_zuidadianliu", "");
		String str_qiandongshijian = sharedPreferences.getString("et_qiandongshijian", "");
		Mjiaodu=MissionSingleInstance.getSingleInstance().getJiaodu();
		Mpinlv=MissionSingleInstance.getSingleInstance().getPinlv();
		if (str_TU != "")
			setSpinnerSelection(sp_qiandongdianya, str_TU);
		if (str_TI != "")
			setSpinnerSelection(sp_qiandongdianliu, str_TI);
		
	
		if (str_Tchangshu != "")
			et_dianbiaochangshu.setText(str_Tchangshu);
		if (str_Tzuidadianliu != "")
			et_zuidadianliu.setText(str_Tzuidadianliu);
		if (str_qiandongshijian != "")
			et_qiandongshijian.setText(str_qiandongshijian);
		
	}
	private void getParamettingSettings()
	{
		String meter_count=dianbiaocanshu.getString("meter_count","");
		String ref_voltage=dianbiaocanshu.getString("ref_voltage","");
		String base_current=dianbiaocanshu.getString("base_current","");
		String max_current=dianbiaocanshu.getString("max_current","");
		String meter1_no=dianbiaocanshu.getString("meter1_no","");
		String meter1_constant=dianbiaocanshu.getString("meter1_constant","");
		String meter2_no=dianbiaocanshu.getString("meter2_no","");
		String meter2_constant=dianbiaocanshu.getString("meter2_constant","");
		String meter3_no=dianbiaocanshu.getString("meter3_no","");
		String meter3_constant=dianbiaocanshu.getString("meter3_constant","");
		
		if(max_current!="")
			et_zuidadianliu.setText(max_current);
		
		if(meter1_constant!="")
			et_dianbiaochangshu.setText(meter1_constant);
	
	}
	private void saveTaitiData() {
		try {
			Editor editor = sharedPreferences.edit();// 获取编辑器
			Qdianya=getDoubleUn(sp_qiandongdianya.getSelectedItem().toString(),Mdianya);
			
			Qdianliu=getDoubleIQ(sp_qiandongdianliu.getSelectedItem().toString(),Mdianliu);
			Qchangshu=et_dianbiaochangshu.getText().toString();
			Qzuidadianliu=et_zuidadianliu.getText().toString();
		
			String s_shijian=et_qiandongshijian.getText().toString();
			if(s_shijian.equals(""))
				calc_time=0;
			else
				calc_time=Double.valueOf(s_shijian).intValue();editor.putString("sp_qiandongdianya",sp_qiandongdianya.getSelectedItem().toString());
			editor.putString("sp_qiandongdianliu", sp_qiandongdianliu.getSelectedItem().toString());
			editor.putString("et_dianbiaochangshu", Qchangshu);
			editor.putString("et_zuidadianliu", Qzuidadianliu);
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
				byte[] data = ds.fnReceiveData(strRx, iRxDataLen);
				if (data != null) {
					if ((data[2] & 0x7f) == 0x61)// 实时台体测量数据
					{
						TaitiCeLiangShuJuBean bean = ds.fnGetTaitiData(data);
						if (bean != null) {
							Message msg = new Message();
							msg.what = 0x001;
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
		try{
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
		}catch(Exception ex)
		{
			
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
		getParamettingSettings();
	}
	/**
	 * 百分号Un字符串乘int，结果保留两位小数
	 */
	private String getDoubleUn(String str, double mdianya2) {
		DecimalFormat df = new DecimalFormat("######0.00");
		String data = "";
		double num = 0;
		
		try {
			num = Double.parseDouble(str.replace("%Un", "")) * 0.01 * mdianya2;
			Log.i(TAG,"num:"+num);
			data = df.format(num);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Log.i(TAG,"data:"+data);
		return data;
	}
	/**
	 * 百分号IQ字符串乘int，结果保留两位小数
	 */
	private String getDoubleIQ(String str, double value) {
		DecimalFormat df = new DecimalFormat("######0.000");
		String data = "";
		double num = 0;
		try {
			num = Double.parseDouble(str.replace("%IQ", "")) * 0.01 * value;
			Log.i(TAG,"num:"+num);
			data = df.format(num);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Log.i(TAG,"data:"+data);
		return data;
	}
	private void setBarProgress(int leftSecond)
	{
		if(time!=0)
		{
		int quarter=10000/time;
		bar.setProgress((time-leftSecond)*quarter);
		}
	}
	private class SendDataTask extends TimerTask{
			@Override
			public void run() {
				if (iType == -1) {
					Blue.send(altek.fnTaitiCeShiLeiXingPeiZhiCanshu(ManualCheckLoadActivity.METHOD, 0,
							Integer.valueOf(MCL_JiBenWuChaFragment.maichongchangshu), Integer.valueOf(MCL_JiBenWuChaFragment.maichongchangshu), ByteUtil.getMeterCount(getActivity()),
							MCL_JiBenWuChaFragment.meter1_no, MCL_JiBenWuChaFragment.meter2_no, MCL_JiBenWuChaFragment.meter3_no),handler);
					iType++;
				}
				if(recv_state)
				{
					if(MissionSingleInstance.getSingleInstance().isYuan_state())
					Blue.send(altek.fnGetFrameByFunctionCode((byte)0x61),handler);
				}
				else if(send_state)//执行一次后，执行查数据命令
				{
					Blue.send(altek.fnTaitiShuChu(Float.parseFloat(Qdianya), Float.parseFloat(Qdianliu), 0, Float.parseFloat(Mjiaodu), Float.parseFloat(Mpinlv), 0, 0, 0, 1, 1,calc_time, 10, 0),handler);
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
