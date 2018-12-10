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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.wisdom.app.activity.ParameterSettingActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.activityResult.QiDongResultActivity;
import com.wisdom.app.utils.*;
import com.wisdom.bean.QiDongBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.IQidongDao;
import com.wisdom.dao.QiDongDao;
import com.wisdom.layout.TitleLayout;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author JinJingYun
 * 虚负荷手动校验-起动试验
 * */
public class MCNL_QiDongTestFragment extends Fragment{
	private String TAG="qidong";
	@Bind(R.id.title_MCNL_qidong)
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

	@Bind(R.id.option_u)
	TextView option_u;// 电压设置
	@Bind(R.id.option_i)
	TextView option_i;// 电流设置
	@Bind(R.id.option_p)
	TextView option_p;// 
	
	@Bind(R.id.option_f)
	EditText option_f;// 
	
	
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

	@Bind(R.id.yuan_shuchu)
	Button btn_yuan_shuchu;
	@Bind(R.id.yuan_tingzhi)
	Button btn_yuan_tingzhi;
	//上三行
	@Bind(R.id.meter_constant) EditText et_dianbiaochangshu;
	@Bind(R.id.base_current) EditText et_jibendianliu;
	@Bind(R.id.start_current) Spinner sp_qidongdianliu;
	
	@Bind(R.id.start_time) EditText et_qidongshijian;
	@Bind(R.id.run_time) TextView tv_shengyushijian;
	//中三行
	@Bind(R.id.result1) TextView tv_result1;
	@Bind(R.id.result2) TextView tv_result2;
	@Bind(R.id.result3) TextView tv_result3;
	@Bind(R.id.dianya_yuan)TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)TextView tv_dianliuyuan;
	@Bind(R.id.progressbar)
	ProgressBar bar;
	private SharedPreferences sharedPreferences;
	private SharedPreferences jibenwucha;
	private SharedPreferences dianbiaocanshu;
	
    //Dialog
    private ChooseUDialog chooseUDialog; 
    private ChooseIDialog chooseIDialog;
    private ChooseQuanDialog chooseQuanDialog;

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
	private String Qjibendianliu="0";
	
	//部分电表参数,校验结果
	private double Mdianya=220;
	private double Mdianliu=10;
	private String Mjiaodu="0";
	private String Mpinlv="50";
	
	
	private String dianya;
	private String dianliu;
	private String jiaodu;
	private String pinlv;
	
	private double calc_time;
	private String result1;
	private String result2;
	private String result3;
	
	private IQidongDao dao;
	private TaitiCeLiangShuJuBean bean;
	
	QiDongBean qidongBean=null;
	private boolean send_state=false;
	private boolean recv_state=false;
	private boolean test_state=false;
	private int meter_count=1;
	int time=0;//每一次校验的总时间
	int maxtime=0;
	int iOFF=0;
	public static String maichongchangshu = "0";
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x001) {
            	try
            	{
            	bean=(TaitiCeLiangShuJuBean) msg.obj;
            	if(bean!=null)
            	{
					if (Double.valueOf(bean.getU()) > 0||Double.valueOf(bean.getI()) > 0) {
						tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
						tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
					}
					if ((Double.valueOf(bean.getU()) > 0) ||(Double.valueOf(bean.getI()) > 0)) {
						btn_yuan_shuchu.setEnabled(false);
						btn_yuan_tingzhi.setEnabled(true);
						/*if(dianya.equals("0")&&dianliu.equals("0"))
							btn_start_test.setEnabled(false);
						else if(test_state==false&&!btn_stop_test.isEnabled()&&Double.valueOf(tv_chooser_u.getText().toString())>Double.valueOf(option_u.getText().toString())-2&&Double.valueOf(tv_chooser_u.getText().toString())<Double.valueOf(option_u.getText().toString())+2)
						btn_start_test.setEnabled(true);*/
						if(test_state)
						{
							btn_start_test.setEnabled(false);
							btn_stop_test.setEnabled(true);
						}
					}
					if ((Double.valueOf(bean.getU()) == 0) && (Double.valueOf(bean.getI()) == 0)) {
						iOFF++;
						if (iOFF == 3)// 断定源已停止
						{
							btn_start_test.setEnabled(true);
							btn_stop_test.setEnabled(false);
							btn_yuan_shuchu.setEnabled(false);
							btn_yuan_tingzhi.setEnabled(true);
							tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
							tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));
							MissionSingleInstance.getSingleInstance().setTestState(false);
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
            		//判断合格的标准，待修改
            		if(test_state)
            		{
            			if(time==0&&(Integer.valueOf(bean.getTime())>20))
						{
							time=Integer.valueOf(bean.getTime());
							if(maxtime<time)
								maxtime=time;
						}
						setBarProgress(Integer.valueOf(bean.getTime()));
						tv_shengyushijian.setText(bean.getTime());
            			
                		if(Integer.valueOf(bean.getJieguo())==2&&(Integer.valueOf(bean.getTime())==0)&&(maxtime>0))
            			{
                			//停源
    						dianya = "0";
    						dianliu = "0";
        					recv_state = false;
        					send_state = true;
            				test_state=false;
            				fnSendState();
            				MissionSingleInstance.getSingleInstance().setTestState(test_state);
        					getTaitiData();
                			
							btn_start_test.setEnabled(true);
							btn_stop_test.setEnabled(false);
							
            				Toast.makeText(getActivity(), getText(R.string.job_done), Toast.LENGTH_SHORT).show();
            				
            				//recv_state=false;
            				bar.setProgress(100);
            				time=0;
            				maxtime=0;
                		try
    					{
                			if(Integer.valueOf(bean.getJieguo())==2&&Double.valueOf(bean.getWucha1()).intValue()==0)
                    		{
                    			result1=getText(R.string.qualified).toString();
                    			tv_result1.setText(result1);
                    		}
                    		else if(Integer.valueOf(bean.getJieguo())==2&&Double.valueOf(bean.getWucha1()).intValue()!=0)
                    		{
                    			result1=getText(R.string.unqualified).toString();
                    			tv_result1.setText(result1);
                    		}
                    		if(Integer.valueOf(bean.getJieguo())==2&&(Double.valueOf(bean.getWucha2()).intValue()==0)&&ByteUtil.getMeterCount(getActivity())>1)
                    		{
                    			result2=getText(R.string.qualified).toString();
                    			tv_result2.setText(result2);
                    		}
                    		else if(Integer.valueOf(bean.getJieguo())==2&&(Double.valueOf(bean.getWucha2()).intValue()!=0)&&ByteUtil.getMeterCount(getActivity())>1)
                    		{
                    			result2=getText(R.string.unqualified).toString();
                    			tv_result2.setText(result2);
                    		}
                    		if(Integer.valueOf(bean.getJieguo())==2&&(Double.valueOf(bean.getWucha3()).intValue()==0)&&ByteUtil.getMeterCount(getActivity())==3)
                    		{
                    			result3=getText(R.string.qualified).toString();
                    			tv_result3.setText(result3);
                    		}
                    		else if(Integer.valueOf(bean.getJieguo())==2&&(Double.valueOf(bean.getWucha3()).intValue()!=0)&&ByteUtil.getMeterCount(getActivity())==3)
                    		{
                    			result3=getText(R.string.unqualified).toString();
                    			tv_result3.setText(result3);
                    		}
    					qidongBean=new QiDongBean();
    					qidongBean.setDate(ByteUtil.GetNowDate());
    					qidongBean.setU(String.valueOf(Mdianya));
    					qidongBean.setI(String.valueOf(Mdianliu));
    					qidongBean.setChangshu(Qchangshu);
    					qidongBean.setQidongshijian(String.valueOf(calc_time));
    					qidongBean.setQidongshiyan1(result1);
    					qidongBean.setQidongshiyan2(result2);
    					qidongBean.setQidongshiyan3(result3);
    					qidongBean.setType("0");
    					
    					//dao.add(qidongBean);
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
            	
            }
            else if (msg.what == 0x10) {
				Toast.makeText(getActivity(), getText(R.string.voltage_overload),Toast.LENGTH_SHORT).show();
			}
			else if (msg.what == 0x11) {
				Toast.makeText(getActivity(), getText(R.string.current_overload),Toast.LENGTH_SHORT).show();
			}
			else if (msg.what == 0x12) {
				Toast.makeText(getActivity(), getText(R.string.format_error),Toast.LENGTH_SHORT).show();
			}
        }
    };
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_mcnl_qidong_test, null);
		ButterKnife.bind(this, view);
		title=(TitleLayout)view.findViewById(R.id.title_MCNL_qidong);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.qidong_test).toString()));
		
        Log.i(TAG,"电表个数："+MissionSingleInstance.getSingleInstance().getMeterCount());
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
		 sp_qidongdianliu.setOnItemSelectedListener(new OnItemSelectedListener(){

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					Mdianliu=Integer.valueOf(et_jibendianliu.getText().toString());
					Qdianliu=getDoubleUn(sp_qidongdianliu.getSelectedItem().toString(),Mdianliu);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
	        	
	        });
	       
	        btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					Intent intent=new Intent(getActivity(),QiDongResultActivity.class);
					intent.putExtra("qidongBean", qidongBean);  
					startActivity(intent);
				
				}
			});
	        btn_calc_time.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Qdianliu=getDoubleUn(sp_qidongdianliu.getSelectedItem().toString(),Mdianliu);
					double changshu=Double.valueOf(et_dianbiaochangshu.getText().toString());
					//double time=((600*1000)/(changshu*Mdianya*Mdianliu*0.001));
					Log.i("clac_time1",Qdianliu.toString()+"pp"+changshu+"ii");
					calc_time=(1.2*60000)/(220.0*changshu*Double.valueOf(Qdianliu));
					et_qidongshijian.setText(String.valueOf(String.format("%.3f",calc_time)));
					
				}
			});
	        btn_start_test.setOnClickListener(new View.OnClickListener() {
	    		
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					saveTaitiData();
					initResult();
					//option_u.setText(Qdianya.toString());
					option_i.setText(Qdianliu.toString());
					recv_state=false;
					test_state=true;
					MissionSingleInstance.getSingleInstance().setTestState(test_state);
					send_state=true;
					bar.setProgress(0);
					Toast.makeText(getActivity(), getText(R.string.start_testing), Toast.LENGTH_SHORT).show();
					btn_start_test.setEnabled(false);
					btn_stop_test.setEnabled(true);
					MissionSingleInstance.getSingleInstance().setYuan_state(true);
					fnSendState();
				}
			});
	        btn_stop_test.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					recv_state = false;
					send_state = true;
					test_state = false;
					//MissionSingleInstance.getSingleInstance().setTestState(test_state);
					
					try {
						dianya = "0";
						dianliu = "0";
						jiaodu = "0";
						pinlv = "0";
						getTaitiData();
						fnSendState();
					} catch (Exception ex) {

					}
				}
			});
		option_u.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseUDialog = new ChooseUDialog(getActivity());  
				chooseUDialog.setTitle(getText(R.string.dialog_change_voltage));
				//chooseUDialog.setEditUStr(sharedPreferences.getString("tv_chooser_u", ""));
				chooseUDialog.setYesOnclickListener(getText(R.string.dialog_confirm).toString(), new ChooseUDialog.onYesOnclickListener() {  
                    @Override  
                    public void onYesClick() {  
                    	option_u.setText(chooseUDialog.getEditUStr());
                    	saveTaitiData();
                    	chooseUDialog.dismiss();  
                    }  
                });  
				chooseUDialog.setNoOnclickListener(getText(R.string.dialog_cancel).toString(), new ChooseUDialog.onNoOnclickListener() {  
                    @Override  
                    public void onNoClick() {  
                        chooseUDialog.dismiss();  
                    }  
                });  
				chooseUDialog.show();  
			}
		});
		
		option_i.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseIDialog = new ChooseIDialog(getActivity());  
				chooseIDialog.setTitle(getText(R.string.dialog_change_current));   
				chooseIDialog.setYesOnclickListener(getText(R.string.dialog_confirm).toString(), new ChooseIDialog.onYesOnclickListener() {  
                    @Override  
                    public void onYesClick() {  
                    	option_i.setText(chooseIDialog.getEditIStr());
                    	saveTaitiData();
                    	chooseIDialog.dismiss();  
                    }
                });  
				chooseIDialog.setNoOnclickListener(getText(R.string.dialog_cancel).toString(), new ChooseIDialog.onNoOnclickListener() {  
                    @Override  
                    public void onNoClick() {  
                      chooseIDialog.dismiss();  
                    }  
                });  
				chooseIDialog.show(); 
			}
		});
		btn_yuan_shuchu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveTaitiData();
				MissionSingleInstance.getSingleInstance().setYuan_state(true);
				recv_state = false;
				send_state = true;
				fnSendState();
			}
		});
		btn_yuan_tingzhi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					dianya = "0";
					dianliu = "0";
					jiaodu = "0";
					pinlv = "0";
					recv_state = false;
					send_state = true;
					test_state = false;
					//MissionSingleInstance.getSingleInstance().setTestState(test_state);
					getTaitiData();
					fnSendState();
				} catch (Exception ex) {

				}
			}
		});
		option_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseQuanDialog = new ChooseQuanDialog(getActivity());  
				chooseQuanDialog.setTitle(getText(R.string.dialog_change_quan));   
				chooseQuanDialog.setYesOnclickListener(getText(R.string.dialog_confirm).toString(), new ChooseQuanDialog.onYesOnclickListener() {  
                    @Override  
                    public void onYesClick() {  
                    	option_p.setText(chooseQuanDialog.getEditQuanStr());
                    	saveTaitiData();
                    	chooseQuanDialog.dismiss();  
                    }  
                });  
				chooseQuanDialog.setNoOnclickListener(getText(R.string.dialog_cancel).toString(), new ChooseQuanDialog.onNoOnclickListener() {  
                    @Override  
                    public void onNoClick() {  
                    	chooseQuanDialog.dismiss();  
                    }  
                });  
				chooseQuanDialog.show(); 
			}
		});
		return view;
	}
	private void initView() {
		jibenwucha=getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE);
		sharedPreferences = getActivity().getSharedPreferences("MCNL_QiDong", Context.MODE_PRIVATE); // 私有数据
		dianbiaocanshu=getActivity().getSharedPreferences("ParameterSetting",Context.MODE_PRIVATE);
		option_u.setText(jibenwucha.getString("tv_chooser_u", "220.0"));
		option_i.setText(jibenwucha.getString("tv_chooser_i", "5.0"));
		String levels =dianbiaocanshu.getString("meter_level", getText(R.string.level_02).toString());
		if(levels.equals(getText(R.string.level_05))){
			setSpinnerSelection(sp_qidongdianliu, "0.1%Ib");
		}else if(levels.equals(getText(R.string.level_1))){
			setSpinnerSelection(sp_qidongdianliu, "0.4%Ib");
		}else if(levels.equals(getText(R.string.level_2))){
			setSpinnerSelection(sp_qidongdianliu, "0.5%Ib");
		}else if(levels.equals(getText(R.string.level_02))){
			setSpinnerSelection(sp_qidongdianliu, "0.1%Ib");
		}
		
		if(MissionSingleInstance.getSingleInstance().isYuan_state())
    	{
			tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
			tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
			btn_start_test.setEnabled(false);
			btn_yuan_shuchu.setEnabled(false);
			btn_yuan_tingzhi.setEnabled(true);
    	}
    	else if(!MissionSingleInstance.getSingleInstance().isYuan_state())
    	{
    		tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
			tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));
			btn_start_test.setEnabled(true);
			btn_yuan_shuchu.setEnabled(false);
			btn_yuan_tingzhi.setEnabled(true);
    	} 
	}
	/*
	 * 初始化结果控件
	 * */
	private void initResult()
	{
		tv_result1.setText("");
		tv_result2.setText("");
		tv_result3.setText("");
	}
	private void initData() {
		try
		{
	    dao=new QiDongDao(getActivity());
		meter_count=ByteUtil.getMeterCount(getActivity());
		// tv_chooser_u.setText(getDouble(sp_U.getSelectedItem().toString(),220));
		// int length=sp_chooser_i.getSelectedItem().toString().length();
		// tv_chooser_i.setText(getDouble(sp_I.getSelectedItem().toString(),Integer.valueOf(sp_chooser_i.getSelectedItem().toString().substring(0,length-1))));
	     //蓝牙更改 谷
			/*	BluetoothGattService service = MainActivity.mBluetoothLeService.getGattService(BLEActivity.SERVICE_UUID);
		characteristic = service.getCharacteristic(UUID.fromString(BLEActivity.HEART_RATE_MEASUREMENT));
		MainActivity.mBluetoothLeService.setCharacteristicNotification(characteristic);
		getActivity().registerReceiver(mGattUpdateReceiver, MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
		*/
		Mjiaodu=MissionSingleInstance.getSingleInstance().getJiaodu();
		Mpinlv=MissionSingleInstance.getSingleInstance().getPinlv();
		//timer=new Timer();
    	//timer.schedule(new SendDataTask(), 1000, 1000);
		Comm.getInstance().init(handler);
		Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0, Integer.valueOf(maichongchangshu),
				Integer.valueOf(maichongchangshu), Utils.fnGetMeterInfo(getActivity()).getMeterNumbers(),Utils.fnGetMeterInfo(getActivity()).getMeter1_no(),
				Utils.fnGetMeterInfo(getActivity()).getMeter2_no(), Utils.fnGetMeterInfo(getActivity()).getMeter3_no());
		
		Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
		recv_state=true;
		getTaitiData();
		}catch(Exception ex)
		{
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}
	private void getTaitiData()
	{
		String str_TU = sharedPreferences.getString("sp_qidongdianliu", "");
        Log.e("ttttttttttttttt",str_TU );
		String str_Tchangshu = sharedPreferences.getString("et_dianbiaochangshu", "");
		String str_Tzuidadianliu = sharedPreferences.getString("et_jibendianliu", "");
		String str_qidongshijian=sharedPreferences.getString("et_qidongshijian", "300");
		String str_jibendianliu=sharedPreferences.getString("et_jibendianliu", "5");
		
		String str_OptionU=jibenwucha.getString("tv_chooser_u", "220.0");
		String str_OptionI=jibenwucha.getString("tv_chooser_i", "5.0");
		String str_OptionP=jibenwucha.getString("sp_chooser_p", "0.0");
		
		maichongchangshu = dianbiaocanshu.getString("meter1_constant", "1600");
		if(maichongchangshu.equals(""))
			maichongchangshu="0";
		Mjiaodu=MissionSingleInstance.getSingleInstance().getJiaodu();
		Mpinlv=MissionSingleInstance.getSingleInstance().getPinlv();
		if (str_TU != "")
			setSpinnerSelection(sp_qidongdianliu, str_TU);
		if(str_OptionU!="")
	        option_u.setText(str_OptionU); 
		if(str_OptionI!="")
	        option_i.setText(str_OptionI); 
		if(str_OptionP!=""){
			option_p.setText(str_OptionP);
		}
		if (str_Tchangshu != "")
			et_dianbiaochangshu.setText(str_Tchangshu);
	
		if (str_qidongshijian != "")
			et_qidongshijian.setText(str_qidongshijian);
		
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
		
		if(base_current!="")
			et_jibendianliu.setText(base_current);
		
		if(meter1_constant!="")
			et_dianbiaochangshu.setText(meter1_constant);
	
	}
	private void saveTaitiData() {
		try {
			
			dianya=option_u.getText().toString();
			dianliu =option_i.getText().toString();
			jiaodu=option_p.getText().toString();
			pinlv=option_f.getText().toString();
			
			MissionSingleInstance.getSingleInstance().setU(dianya);
			MissionSingleInstance.getSingleInstance().setI(dianliu);
			MissionSingleInstance.getSingleInstance().setJiaodu(jiaodu);
			MissionSingleInstance.getSingleInstance().setPinlv(pinlv);
			
			Editor editor1 =jibenwucha.edit();
			Editor editor = sharedPreferences.edit();// 获取编辑器
			Mdianliu=Integer.valueOf(et_jibendianliu.getText().toString());
			Qdianliu=getDoubleUn(sp_qidongdianliu.getSelectedItem().toString(),Mdianliu);
			
			Qchangshu=et_dianbiaochangshu.getText().toString();
			Qjibendianliu=et_jibendianliu.getText().toString();
			String s_shijian=et_qidongshijian.getText().toString();
			if(s_shijian.equals(""))
				calc_time=0;
			else
				calc_time=Double.valueOf(s_shijian);
			
			editor.putString("et_jibendianliu", Qjibendianliu);
			editor.putString("et_qidongshijian", et_qidongshijian.getText().toString());
			editor.putString("sp_qidongdianliu", sp_qidongdianliu.getSelectedItem().toString());
			editor.putString("et_dianbiaochangshu", Qchangshu);
			
			editor.commit();
			editor1.putString("tv_chooser_u", option_u.getText().toString());
			editor1.putString("tv_chooser_i", option_i.getText().toString());
			editor1.putString("sp_chooser_p", option_p.getText().toString());
			editor1.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			//getActivity().unregisterReceiver(mGattUpdateReceiver);
			//timer.cancel();
			}catch(Exception ex)
			{
				
			}
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) { 
			saveTaitiData();
			// 不在最前端显示 相当于调用了onPause();
			Log.i(TAG,"onHidden()");
			try{
				//getActivity().unregisterReceiver(mGattUpdateReceiver);
				//timer.cancel();
			}catch(Exception ex)
				{
					
				}
            return;
        }else{  // 在最前端显示 相当于调用了onResume();
        	Log.i(TAG,"onHiddenShow()");
        	getTaitiData();
			Comm.getInstance().init(handler);
			Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0, Integer.valueOf(maichongchangshu),
					Integer.valueOf(maichongchangshu), Utils.fnGetMeterInfo(getActivity()).getMeterNumbers(),Utils.fnGetMeterInfo(getActivity()).getMeter1_no(),
					Utils.fnGetMeterInfo(getActivity()).getMeter2_no(), Utils.fnGetMeterInfo(getActivity()).getMeter3_no());
			
        	Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
        	//timer=new Timer();
        	//timer.schedule(new SendDataTask(), 1000, 1000);
        	if(MissionSingleInstance.getSingleInstance().isYuan_state())
        	{
				tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
				tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
    			
    			btn_yuan_shuchu.setEnabled(false);
				btn_yuan_tingzhi.setEnabled(true);
    			
        	}
        	else if(!MissionSingleInstance.getSingleInstance().isYuan_state())
        	{
        		tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
				tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));
    			btn_yuan_shuchu.setEnabled(false);
				btn_yuan_tingzhi.setEnabled(true);
				btn_start_test.setEnabled(true);
				btn_stop_test.setEnabled(false);
        		tv_chooser_u.setText("0.000");
        		tv_chooser_i.setText("0.000");
        		tv_chooser_f.setText("50.0");
        		tv_show_p.setText("0.000000");
        		tv_show_q.setText("0.000000");
        		tv_show_s.setText("0.000000");
        		tv_show_pf.setText("1.000");
        		tv_jiaodu.setText("360.0");
    			
        	} 
        }
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		saveTaitiData();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getTaitiData();
		getParamettingSettings();
	}
	/**
	 * 百分号Ib字符串乘int，结果保留3位小数
	 */
	private String getDoubleUn(String str, double mdianya2) {
		DecimalFormat df = new DecimalFormat("######0.000");
		String data = "";
		double num = 0;
		
		try {
			num = Double.parseDouble(str.replace("%Ib", "")) * 0.01 * mdianya2;
			Log.i(TAG,"num:"+num);
			data = df.format(num);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Log.i(TAG,"data:"+data);
		return data;
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
	private void fnSendState()
	{
		if(test_state)
		{ 
			Comm.getInstance().status_one=altek.fnTaitiShuChu(Float.parseFloat(dianya), Float.parseFloat(Qdianliu), 0, Float.parseFloat(Mjiaodu), Float.parseFloat(Mpinlv), 0, 0, 0, 1, 1,(int)(calc_time*60), 9, 0);
		    Log.i(TAG,"时间："+calc_time);
		}
		else{

			if(dianya.equals("")||dianliu.equals("")||pinlv.equals(""))
			{
				Message msg = new Message();
				msg.what = 0x12;
				handler.sendMessage(msg);
				send_state = false;
				return;
			}
			float f_dianya=Float.parseFloat(dianya);
			float f_dianliu=Float.parseFloat(dianliu);
			float f_jiaodu=Float.parseFloat(jiaodu);
			float f_pinlv=Float.parseFloat(pinlv);
			if(f_dianya>264)
			{
				Message msg = new Message();
				msg.what = 0x10;
				handler.sendMessage(msg);
				send_state = false;
				return;
			}
			else if(f_dianliu>100)
			{
				Message msg = new Message();
				msg.what = 0x11;
				handler.sendMessage(msg);
				send_state = false;
				return;
			}
			Comm.getInstance().status_one=altek.fnTaitiShuChu(f_dianya, f_dianliu, 0,
					f_jiaodu, f_pinlv, 0, 0, 0, 0,
					0, 0, 0, 0);
		}

	}

}
