package com.wisdom.app.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import butterknife.Bind;
import butterknife.ButterKnife;

import com.wisdom.app.activity.ManualCheckLoadActivity;
import com.wisdom.app.activity.ParameterSettingActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.activityResult.JiBenWuChaResultActivity;
import com.wisdom.app.utils.*;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.JiBenWuChaDao;
import com.wisdom.layout.HzEditText;
import com.wisdom.layout.TitleLayout;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author wisdom's JiangYuPeng
 * @version 创建时间：2017-11-9 下午5:16:27
 * @version 修改时间：2017-11-29下午17:22 JinJingYun 虚负荷手动校验基本误差
 */
public class MCNL_JiBenWuChaFragment extends Fragment  {
	private String TAG = "MCNL_JiBenWuChaFragment";
	@Bind(R.id.title_MCNL_jibenwucha)
	TitleLayout title;
	@Bind(R.id.dianbiao_canshu)
	Button btn_dianbiao_canshu;
	@Bind(R.id.jiaoyan_jieguo)
	Button btn_jiaoyan_jieguo;
	@Bind(R.id.start_test)
	Button btn_start_test;// 启动校验
	@Bind(R.id.stop_test)
	Button btn_stop_test;// 停止校验

	@Bind(R.id.meter1_circle)
	TextView tv_meter1_circle;// 电表1圈数
	@Bind(R.id.meter1_ev)
	TextView tv_meter1_ev;// 电能误差
	@Bind(R.id.meter1_count)
	TextView tv_meter1_count;// 次数
	@Bind(R.id.meter1_st)
	TextView tv_meter1_st;// 标准偏差

	@Bind(R.id.tv_f)
	TextView tv_f;

	@Bind(R.id.meter2_circle)
	TextView tv_meter2_circle;
	@Bind(R.id.meter2_ev)
	TextView tv_meter2_ev;
	@Bind(R.id.meter2_count)
	TextView tv_meter2_count;
	@Bind(R.id.meter2_st)
	TextView tv_meter2_st;

	@Bind(R.id.meter3_circle)
	TextView tv_meter3_circle;
	@Bind(R.id.meter3_ev)
	TextView tv_meter3_ev;
	@Bind(R.id.meter3_count)
	TextView tv_meter3_count;
	@Bind(R.id.meter3_st)
	TextView tv_meter3_st;

	@Bind(R.id.meter_circle)
	EditText et_meter_circle;// 校验圈数
	@Bind(R.id.meter_count)
	EditText et_meter_count;// 校验次数

	@Bind(R.id.chooser_u)
	TextView et_chooser_u;// 电压左侧
	@Bind(R.id.tv_U)
	TextView tv_U;// 电压右侧

	@Bind(R.id.chooser_i)
	TextView et_chooser_i;// 电流左侧

	@Bind(R.id.tv_I)
	TextView tv_I;

	@Bind(R.id.chooser_p)
	TextView sp_chooser_p;// 项角
	@Bind(R.id.tv_jiaodu)
	TextView tv_jiaodu;
	
	@Bind(R.id.chooser_f)
	EditText et_chooser_f;// 频率

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

	@Bind(R.id.dianya_yuan)
	TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)
	TextView tv_dianliuyuan;
	@Bind(R.id.run_time)
	EditText et_runtime;
	@Bind(R.id.progressbar)
	ProgressBar bar;
	@Bind(R.id.tr_row2)
	TableRow tr_2;
	@Bind(R.id.tr_row3)
	TableRow tr_3;
	
	int time=0;//每一次校验的总时间
	int count=0;//上次次数
	private SharedPreferences sharedPreferences;
	private SharedPreferences dianbiaocanshu;
	double i_meter_level=0.5;
	private boolean send_state = false;
	private boolean recv_state = false;
	private boolean test_state = false;

	// 收发数据
	private BluetoothGattCharacteristic characteristic;
	private ALTEK altek = new ALTEK();
	private DataService ds = new DataService();
	int[] iTempBuf = new int[10];
	int iTxLen = 0;
	int iStart = 0, iEnd = 0, iLen = 0, i = 0;
	String strCurTx = null;
	String str = null;
	private Timer timer;

	// 基础数据
	private String dianya = "-1";
	private String dianliu = "0";
	private String jiaodu = "0";
	private String pinlv = "50";
	private String yougong = "0";
	private String wugong = "0";
	private String zonggong = "0";
	private String gonglvyinshu = "0";
	// 666
	private String quanshu = "3";
	private String cishu = "2";
	// 电表信息
	public static String meter1_no = "0";
	public static String meter2_no = "0";
	public static String meter3_no = "0";
	public static String maichongchangshu = "0";
    //Dialog
    private ChooseUDialog chooseUDialog; 
    private ChooseIDialog chooseIDialog;
    private ChooseQuanDialog chooseQuanDialog;
	int meter_numbers = 1;// 电表个数
    JiBenWuChaBean jibenwuchaBean=null;
	private JiBenWuChaDao dao;
	private TaitiCeLiangShuJuBean bean;
	int i_cishu=1;
	int iOFF = 0;// 用来判断源是否真得停止
	int maxtime=0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x001) {
				try {
					bean = (TaitiCeLiangShuJuBean) msg.obj;
					if (bean != null) {
						if (Double.valueOf(bean.getU()) > 0||Double.valueOf(bean.getI()) > 0) {
							tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
							tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
						}

						if ((Double.valueOf(bean.getU()) > 0) ||(Double.valueOf(bean.getI()) > 0)) {
							//btn_yuan_shuchu.setEnabled(false);
							btn_yuan_tingzhi.setEnabled(true);
							MissionSingleInstance.getSingleInstance().setYuan_state(true);
							if(dianya.equals("0")&&dianliu.equals("0"))
								btn_start_test.setEnabled(false);
							else if(test_state==false&&!btn_stop_test.isEnabled()&&Double.valueOf(tv_U.getText().toString())>Double.valueOf(et_chooser_u.getText().toString())-2&&Double.valueOf(tv_U.getText().toString())<Double.valueOf(et_chooser_u.getText().toString())+2)
							btn_start_test.setEnabled(true);
						}
						if ((Double.valueOf(bean.getU()) == 0) && (Double.valueOf(bean.getI()) == 0)) {
							iOFF++;
							if (iOFF == 3)// 断定源已停止
							{
								btn_start_test.setEnabled(false);
								btn_stop_test.setEnabled(false);
								btn_yuan_shuchu.setEnabled(true);
								//btn_yuan_tingzhi.setEnabled(false);源停止依旧可以点击
								tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
								tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));
								MissionSingleInstance.getSingleInstance().setTestState(false);
								MissionSingleInstance.getSingleInstance().setYuan_state(false);
								iOFF = 0;
							}
						}
						tv_U.setText(bean.getU());
						tv_I.setText(bean.getI());
						tv_f.setText(bean.getPinlv());
						tv_jiaodu.setText(bean.getJiaodu());
						tv_show_p.setText(bean.getYougong());//2
						tv_show_q.setText(bean.getWugong());//3
						tv_show_s.setText(bean.getZonggong());//00000
						tv_show_pf.setText(bean.getGonglvyinshu());
						
						// 上面三排
						try {
							if (test_state) {
													
								tv_meter1_circle.setText(et_meter_circle.getText().toString());
								tv_meter2_circle.setText(et_meter_circle.getText().toString());
								tv_meter3_circle.setText(et_meter_circle.getText().toString());

								tv_meter1_count.setText(bean.getCishu());
								tv_meter2_count.setText(bean.getCishu());
								tv_meter3_count.setText(bean.getCishu());
								
								i_cishu=Integer.valueOf(bean.getCishu());
								Log.i("result","i_cishu:"+i_cishu+" maxtime:"+maxtime+" jieguo:"+bean.getJieguo());
								int i_jieguo=Integer.valueOf(bean.getJieguo());
								if((i_cishu>0)&&(maxtime>0)&&(i_jieguo==1||i_jieguo==2||i_jieguo==3)){
									//bar.setProgress(0);
									Log.i("result","wucha1:"+bean.getWucha1()+" piancha1:"+bean.getPiancha1());
									if((Double.valueOf(bean.getPiancha1())<100)&&bean.getJieguo().equals("2"))
									{
										tv_meter1_st.setText(bean.getPiancha1());
										if(Double.valueOf(bean.getPiancha2())<100)
										tv_meter2_st.setText(bean.getPiancha2());
										if(Double.valueOf(bean.getPiancha3())<100)
										tv_meter3_st.setText(bean.getPiancha3());
									}
									if(i_cishu==1)
									{
										if(Double.valueOf(bean.getWucha1())<100)
											tv_meter1_ev.setText(bean.getWucha1());
										if(Double.valueOf(bean.getWucha2())<100)
										tv_meter2_ev.setText(bean.getWucha2());
										if(Double.valueOf(bean.getWucha3())<100)
										tv_meter3_ev.setText(bean.getWucha3());
									}
									if(i_cishu==2)
									{
										if(Double.valueOf(bean.getWucha1_2())<100)
											tv_meter1_ev.setText(bean.getWucha1_2());
										if(Double.valueOf(bean.getWucha2_2())<100)
										tv_meter2_ev.setText(bean.getWucha2_2());
										if(Double.valueOf(bean.getWucha3_2())<100)
										tv_meter3_ev.setText(bean.getWucha3_2());
									}
									if(i_cishu==3)
									{
										if(Double.valueOf(bean.getWucha1_3())<100)
											tv_meter1_ev.setText(bean.getWucha1_3());
										if(Double.valueOf(bean.getWucha2_3())<100)
										tv_meter2_ev.setText(bean.getWucha2_3());
										if(Double.valueOf(bean.getWucha3_3())<100)
										tv_meter3_ev.setText(bean.getWucha3_3());
									}
									if(i_cishu==4)
									{
										if(Double.valueOf(bean.getWucha1_4())<100)
											tv_meter1_ev.setText(bean.getWucha1_4());
										if(Double.valueOf(bean.getWucha2_4())<100)
										tv_meter2_ev.setText(bean.getWucha2_4());
										if(Double.valueOf(bean.getWucha3_4())<100)
										tv_meter3_ev.setText(bean.getWucha3_4());
									}
									if(i_cishu==5)
									{
										if(Double.valueOf(bean.getWucha1_5())<100)
											tv_meter1_ev.setText(bean.getWucha1_5());
										if(Double.valueOf(bean.getWucha2_5())<100)
										tv_meter2_ev.setText(bean.getWucha2_5());
										if(Double.valueOf(bean.getWucha3_5())<100)
										tv_meter3_ev.setText(bean.getWucha3_5());
									}
									if(i_cishu==6)
									{
										if(Double.valueOf(bean.getWucha1_6())<100)
											tv_meter1_ev.setText(bean.getWucha1());
										if(Double.valueOf(bean.getWucha2_6())<100)
										tv_meter2_ev.setText(bean.getWucha2_6());
										if(Double.valueOf(bean.getWucha3_6())<100)
										tv_meter3_ev.setText(bean.getWucha3_6());
									}
								}
					
								Log.e("MCNL_JiBenWuCha", "wucha1:"+bean.getWucha1());
								Log.e("UF","time:"+time);
								//if(time==0&&(Integer.valueOf(bean.getTime())>10))
								if(Integer.valueOf(bean.getJieguo())==5&&Integer.valueOf(bean.getTime())>0)
								{
									time=Integer.valueOf(bean.getTime());
									if(maxtime<time)
										maxtime=time;
									setBarProgress2(maxtime,Integer.valueOf(bean.getTime()));
								}

								if(count!=Integer.valueOf(bean.getCishu()))
								{
									//新一次
									secondState=0;
									count=Integer.valueOf(bean.getCishu());
									time=Integer.valueOf(bean.getTime());
								}
								if(meter_numbers==1)
								et_runtime.setText(bean.getMaichong1());
								else if(meter_numbers==2)
								et_runtime.setText(bean.getMaichong1()+","+bean.getMaichong2());
								else if(meter_numbers==3)
								et_runtime.setText(bean.getMaichong1()+","+bean.getMaichong2()+","+bean.getMaichong3());
								Log.e("UF","maxtime："+maxtime);
								
								if (Integer.valueOf(bean.getJieguo()) == 2 && (Integer.valueOf(bean.getTime()) == 0)
										&& (maxtime > 0)) {
									bar.setProgress(100);
									time=0;
									secondState=0;
									maxtime=0;
									test_state = false;
									MissionSingleInstance.getSingleInstance().setTestState(test_state);
									//recv_state = false;
									Toast.makeText(getActivity(), getText(R.string.job_done), Toast.LENGTH_SHORT).show();
									btn_start_test.setEnabled(true);
									btn_stop_test.setEnabled(false);
									jibenwuchaBean = new JiBenWuChaBean();
									jibenwuchaBean.setDate(ByteUtil.GetNowDate());
									jibenwuchaBean.setU(dianya);
									jibenwuchaBean.setI(dianliu);
									jibenwuchaBean.setJiaodu(jiaodu);
									jibenwuchaBean.setYougong(bean.getYougong());
									jibenwuchaBean.setWugong(bean.getWugong());
									jibenwuchaBean.setGonglvyinshu(bean.getGonglvyinshu());
									jibenwuchaBean.setMaichongchangshu(maichongchangshu);
									jibenwuchaBean.setQuanshu(quanshu);
									jibenwuchaBean.setCishu(cishu);
									if(bean.getWendu()!=null)
									SharepreferencesUtil.setTemperature(getActivity(), bean.getWendu());
									fnSetResult();
									/*jibenwuchaBean.setDiannengwucha1_2(bean.getWucha1_2());
									jibenwuchaBean.setDiannengwucha1_3(bean.getWucha1_3());
									jibenwuchaBean.setDiannengwucha1_4(bean.getWucha1_4());
									jibenwuchaBean.setDiannengwucha1_5(bean.getWucha1_5());
									jibenwuchaBean.setDiannengwucha1_6(bean.getWucha1_6());
									
									jibenwuchaBean.setDiannengwucha2_2(bean.getWucha2_2());
									jibenwuchaBean.setDiannengwucha2_3(bean.getWucha2_3());
									jibenwuchaBean.setDiannengwucha2_4(bean.getWucha2_4());
									jibenwuchaBean.setDiannengwucha2_5(bean.getWucha2_5());
									jibenwuchaBean.setDiannengwucha2_6(bean.getWucha2_6());
									
									jibenwuchaBean.setDiannengwucha3_2(bean.getWucha3_2());
									jibenwuchaBean.setDiannengwucha3_3(bean.getWucha3_3());
									jibenwuchaBean.setDiannengwucha3_4(bean.getWucha3_4());
									jibenwuchaBean.setDiannengwucha3_5(bean.getWucha3_5());
									jibenwuchaBean.setDiannengwucha3_6(bean.getWucha3_6());*/
									
									jibenwuchaBean.setType("0");
									//dao.add(jibenwuchaBean);保存校验结果

								}
							}

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (msg.what == 0x002) {
				Toast.makeText(getActivity(), getText(R.string.start_testing), Toast.LENGTH_SHORT).show();
				bar.setProgress(0);
			} else if (msg.what==0x003)
			{
				Toast.makeText(getActivity(),getText(R.string.virtual_mode_open), Toast.LENGTH_SHORT).show();
				recv_state=true;
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
		
		View view = inflater.inflate(R.layout.fragment_mcnl_jibenwucha, null);
		ButterKnife.bind(this, view);
		
		initView();
		initData();
		btn_dianbiao_canshu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getActivity(), ParameterSettingActivity.class);
				startActivity(intent);
			}
			
		});
                                       
		btn_start_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initTopThreeLine();
				jiaodu = sp_chooser_p.getText().toString();
				MissionSingleInstance.getSingleInstance().setJiaodu(jiaodu);
				dianya = et_chooser_u.getText().toString();
				dianliu = et_chooser_i.getText().toString();
				pinlv = et_chooser_f.getText().toString();
				quanshu = et_meter_circle.getText().toString();
				cishu = et_meter_count.getText().toString();
				int i_cishu=Integer.valueOf(cishu);
				
				if (quanshu.equals("") || cishu.equals("")||quanshu.equals("0") || cishu.equals("0"))
				{
					Toast.makeText(getActivity(), getText(R.string.format_illegal), Toast.LENGTH_SHORT).show();
					return;
				}
				if(i_cishu>6)
				{
					Toast.makeText(getActivity(), getText(R.string.count_limit), Toast.LENGTH_SHORT).show();
					return;
				}
				recv_state = false;
				send_state = true;
				test_state = true;
				MissionSingleInstance.getSingleInstance().setTestState(test_state);
				bar.setProgress(0);
				btn_start_test.setEnabled(false);
				btn_stop_test.setEnabled(true);
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
				et_runtime.setText("");
				MissionSingleInstance.getSingleInstance().setTestState(test_state);
				try {
					dianya = "0";
					dianliu = "0";
					jiaodu = "0";
					//pinlv = "0";
					quanshu = "0";
					cishu = "0";
					getTaitiData();
					fnSendState();
				} catch (Exception ex) {

				}
			}
		});

		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), JiBenWuChaResultActivity.class);
				intent.putExtra("jibenwuchaBean", jibenwuchaBean);  
				startActivity(intent);
			}
		});
		btn_yuan_shuchu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveTaitiData();
				quanshu = "0";
				cishu = "0";
				MissionSingleInstance.getSingleInstance().setYuan_state(true);
				MissionSingleInstance.getSingleInstance().setJiaodu(jiaodu);
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
					//pinlv = "0";
					quanshu = "0";
					cishu = "0";
					recv_state = false;
					send_state = true;
					test_state = false;
					MissionSingleInstance.getSingleInstance().setTestState(test_state);
					getTaitiData();
					fnSendState();
				} catch (Exception ex) {

				}
			}
		});
		et_chooser_u.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseUDialog = new ChooseUDialog(getActivity());  
				chooseUDialog.setTitle(getText(R.string.dialog_change_voltage));
				//chooseUDialog.setEditUStr(sharedPreferences.getString("tv_chooser_u", ""));
				chooseUDialog.setYesOnclickListener(getText(R.string.dialog_confirm).toString(), new ChooseUDialog.onYesOnclickListener() {  
                    @Override
                    public void onYesClick() {
                    	et_chooser_u.setText(chooseUDialog.getEditUStr());
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
		
		et_chooser_i.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseIDialog = new ChooseIDialog(getActivity());  
				chooseIDialog.setTitle(getText(R.string.dialog_change_current));   
				chooseIDialog.setYesOnclickListener(getText(R.string.dialog_confirm).toString(), new ChooseIDialog.onYesOnclickListener() {  
                    @Override  
                    public void onYesClick() {  
                    	et_chooser_i.setText(chooseIDialog.getEditIStr());
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
		sp_chooser_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseQuanDialog = new ChooseQuanDialog(getActivity());  
				chooseQuanDialog.setTitle(getText(R.string.dialog_change_angel));   
				chooseQuanDialog.setYesOnclickListener(getText(R.string.dialog_confirm).toString(), new ChooseQuanDialog.onYesOnclickListener() {  
                    @Override  
                    public void onYesClick() {  
                    	sp_chooser_p.setText(chooseQuanDialog.getEditQuanStr());
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
		title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.intrinsic_error).toString()));
		btn_yuan_shuchu.setEnabled(true);
		btn_yuan_tingzhi.setEnabled(true);
		sharedPreferences = getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE); // 私有数据
		dianbiaocanshu = getActivity().getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
	}
	private void initTopThreeLine()
	{
		meter_numbers=Utils.fnGetMeterInfo(getActivity()).getMeterNumbers();
		Log.e(TAG,"meter_numbers:"+meter_numbers);
		if(meter_numbers==2)
		{
			tr_3.setVisibility(View.INVISIBLE);
			tr_2.setVisibility(View.VISIBLE);
		}
		if(meter_numbers==1)
		{
			tr_3.setVisibility(View.INVISIBLE);
			tr_2.setVisibility(View.INVISIBLE);
		}
		if(meter_numbers==3)
		{
			tr_3.setVisibility(View.VISIBLE);
			tr_2.setVisibility(View.VISIBLE);
		}
		tv_meter1_circle.setText("");
		tv_meter2_circle.setText("");
		tv_meter3_circle.setText("");
		tv_meter1_ev.setText("");
		tv_meter2_ev.setText("");
		tv_meter3_ev.setText("");

		tv_meter1_count.setText("");
		tv_meter2_count.setText("");
		tv_meter3_count.setText("");

		tv_meter1_st.setText("");
		tv_meter2_st.setText("");
		tv_meter3_st.setText("");
		
	}
	private void initData() {
		try {
			ManualCheckLoadActivity.METHOD=0;
			dao = new JiBenWuChaDao(getActivity());
			// tv_chooser_u.setText(getDouble(sp_U.getSelectedItem().toString(),220));
			// int length=sp_chooser_i.getSelectedItem().toString().length();
			// tv_chooser_i.setText(getDouble(sp_I.getSelectedItem().toString(),Integer.valueOf(sp_chooser_i.getSelectedItem().toString().substring(0,length-1))));
			//BluetoothGattService service = MainActivity.mBluetoothLeService.getGattService(BLEActivity.SERVICE_UUID);
			//characteristic = service.getCharacteristic(UUID.fromString(BLEActivity.HEART_RATE_MEASUREMENT));
			//MainActivity.mBluetoothLeService.setCharacteristicNotification(characteristic);
			//getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			dianbiaocanshu.getString("meter1_constant", "0");
			getTaitiData();
			//MissionSingleInstance.getSingleInstance().setYuan_state(true);
			//recv_state=true;
			//timer = new Timer();
			//timer.schedule(new SendDataTask(), 1000, 1000);
			Comm.getInstance().init(handler);
			Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0, Integer.valueOf(maichongchangshu),
					Integer.valueOf(maichongchangshu), meter_numbers,meter1_no,
					meter2_no, meter3_no);
			Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
			Comm.getInstance().startLoop();
		} catch (Exception ex) {
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}



	private void getTaitiData() {
		//initTopThreeLine();
		String meter_level=dianbiaocanshu.getString("meter_level", getText(R.string.level_05).toString());
		if(meter_level.equals(getText(R.string.level_02)))
		i_meter_level=0.2;
		if(meter_level.equals(getText(R.string.level_05)))
			i_meter_level=0.5;
		if(meter_level.equals(getText(R.string.level_1)))
			i_meter_level=1;
		if(meter_level.equals(getText(R.string.level_2)))
			i_meter_level=2;
		
		String chooser_f = sharedPreferences.getString("et_chooser_f", "50");
		String meter_circle = sharedPreferences.getString("et_meter_circle", "3");
		String meter_count = sharedPreferences.getString("et_meter_count", "2");
		maichongchangshu = dianbiaocanshu.getString("meter1_constant", "1600");
		meter1_no = dianbiaocanshu.getString("meter1_no", "");
		meter2_no = dianbiaocanshu.getString("meter2_no", "");
		meter3_no = dianbiaocanshu.getString("meter3_no", "");
		if (meter1_no.equals(""))
			meter1_no = "0";
		if (!meter2_no.equals(""))
			meter_numbers++;
		else
			meter2_no = "0";
		if (!meter3_no.equals(""))
			meter_numbers++;
		else
			meter3_no = "0";
		if(maichongchangshu.equals(""))
			maichongchangshu="0";
		String str_U = sharedPreferences.getString("tv_chooser_u", "");

		String str_I = sharedPreferences.getString("tv_chooser_i", "");
		String str_chooser_p = sharedPreferences.getString("sp_chooser_p", "");

		if (str_U != "")
			et_chooser_u.setText(str_U);

		if (str_I != "")
			et_chooser_i.setText(str_I);
		if (str_chooser_p != "")
			sp_chooser_p.setText( str_chooser_p);

		if (chooser_f != "")
			et_chooser_f.setText(chooser_f);
		if (meter_circle != "")
			et_meter_circle.setText(meter_circle);
		if (meter_count != "")
			et_meter_count.setText(meter_count);
	}

	private void saveTaitiData() {
		try {
			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putString("sp_U", tv_U.getText().toString());

			editor.putString("sp_I", tv_I.getText().toString());
			jiaodu = sp_chooser_p.getText().toString();
			editor.putString("sp_chooser_p", jiaodu);
			MissionSingleInstance.getSingleInstance().setJiaodu(jiaodu);

			dianya = et_chooser_u.getText().toString();
			editor.putString("tv_chooser_u", dianya);
			dianliu = et_chooser_i.getText().toString();
			MissionSingleInstance.getSingleInstance().setU(dianya);
			MissionSingleInstance.getSingleInstance().setI(dianliu);
			editor.putString("tv_chooser_i", dianliu);
			pinlv = et_chooser_f.getText().toString();
			MissionSingleInstance.getSingleInstance().setPinlv(pinlv);
			Log.i(TAG, "频率:" + pinlv);
			editor.putString("et_chooser_f", pinlv);

			quanshu = et_meter_circle.getText().toString();
			editor.putString("et_meter_circle", quanshu);// 校验圈数
			cishu = et_meter_count.getText().toString();
			editor.putString("et_meter_count", cishu);// 校验次数

			editor.commit();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * 设置Spinner默认item
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

	
	

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			// 不在最前端显示 相当于调用了onPause();
			Log.i(TAG, "onHidden()");
			try {
				//getActivity().unregisterReceiver(mGattUpdateReceiver);
				//timer.cancel();
			} catch (Exception ex) {

			}

			return;
		} else { // 在最前端显示 相当于调用了onResume();
			Log.i(TAG, "onHiddenShow()");
			getTaitiData();
			//getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			Comm.getInstance().init(handler);
			Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0, Integer.valueOf(maichongchangshu),
					Integer.valueOf(maichongchangshu), meter_numbers,meter1_no,
					meter2_no, meter3_no);
			Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
			//timer = new Timer();
			//timer.schedule(new SendDataTask(), 1000, 1000);
        	if(MissionSingleInstance.getSingleInstance().isYuan_state())
        	{
        		tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
				tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
    			//btn_yuan_shuchu.setEnabled(false);
				btn_yuan_tingzhi.setEnabled(true);
    			
        	}
        	else if(!MissionSingleInstance.getSingleInstance().isYuan_state())
        	{
        		tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
				tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));

    			btn_yuan_shuchu.setEnabled(true);
				btn_yuan_tingzhi.setEnabled(true);
				btn_start_test.setEnabled(false);
				btn_stop_test.setEnabled(false);
				tv_U.setText("0.000");
				tv_I.setText("0.000");
				tv_f.setText("50.0");
				tv_jiaodu.setText("360.0");
				tv_show_p.setText("0.000000");
				tv_show_q.setText("0.000000");
				tv_show_s.setText("0.000000");
				tv_show_pf.setText("1.000");
    			
        	}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume()");
		getTaitiData();
		initTopThreeLine();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		try {
			//timer.cancel();
			//getActivity().unregisterReceiver(mGattUpdateReceiver);
		} catch (Exception ex) {

		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG, "onPause()");
		saveTaitiData();

	}

	public static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	/**
	 * 百分号字符串乘int，结果保留两位小数
	 */
	private String getDouble(String str, int value) {
		DecimalFormat df = new DecimalFormat("######0.00");
		String data = "";
		double num = 0;
		try {
			num = Double.parseDouble(str.replace("%", "")) * 0.01 * value;
			data = df.format(num);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	int x = 0;
	int secondState=0;
	private void setBarProgress(int second,int leftSecond)
	{
		if(second!=0)
		{
		int s=(second-leftSecond)-secondState;//增加的时间
		int quarter=100/(second*Integer.valueOf(cishu));
		bar.incrementProgressBy(s*quarter);
		secondState=second-leftSecond;
		}
		
	}
	private void setBarProgress2(int total,int now)
	{
		int quarter=100/(total*Integer.valueOf(cishu));
		bar.incrementProgressBy((total-now)*quarter);
		
	}
	private void fnSetResult()
	{
		if(Double.valueOf(bean.getPiancha1())<100)
		{
			jibenwuchaBean.setBiaozhunpiancha1(bean.getPiancha1());
		}
		if(Double.valueOf(bean.getWucha1())<100)
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1())<i_meter_level)
				str_result=bean.getWucha1()+" "+getText(R.string.qualified);
			else
				str_result=bean.getWucha1()+" "+getText(R.string.unqualified);
			jibenwuchaBean.setDiannengwucha1(str_result);
		}
		if((Double.valueOf(bean.getWucha1_2())<100)&&(i_cishu>=2))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_2())<i_meter_level)
				str_result=bean.getWucha1_2()+" "+getText(R.string.qualified);
			else
				str_result=bean.getWucha1_2()+" "+getText(R.string.unqualified);
			jibenwuchaBean.setDiannengwucha1_2(str_result);
		}
		if((Double.valueOf(bean.getWucha1_3())<100)&&(i_cishu>=3))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_3())<i_meter_level)
				str_result=bean.getWucha1_3()+" "+getText(R.string.qualified);
			else
				str_result=bean.getWucha1_3()+" "+getText(R.string.unqualified);
			jibenwuchaBean.setDiannengwucha1_3(str_result);
		}
		if((Double.valueOf(bean.getWucha1_4())<100)&&(i_cishu>=4))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_4())<i_meter_level)
				str_result=bean.getWucha1_4()+" "+getText(R.string.qualified);
			else
				str_result=bean.getWucha1_4()+" "+getText(R.string.unqualified);
			jibenwuchaBean.setDiannengwucha1_4(str_result);
		}
		if((Double.valueOf(bean.getWucha1_5())<100)&&(i_cishu>=5))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_5())<i_meter_level)
				str_result=bean.getWucha1_5()+" "+getText(R.string.qualified);
			else
				str_result=bean.getWucha1_5()+" "+getText(R.string.unqualified);
			jibenwuchaBean.setDiannengwucha1_5(str_result);
		}
		if((Double.valueOf(bean.getWucha1_6())<100)&&(i_cishu>=6))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_6())<i_meter_level)
				str_result=bean.getWucha1_6()+" "+getText(R.string.qualified);
			else
				str_result=bean.getWucha1_6()+" "+getText(R.string.unqualified);
			jibenwuchaBean.setDiannengwucha1_6(str_result);
		}
		if(Double.valueOf(bean.getPiancha2())<100)
		jibenwuchaBean.setBiaozhunpiancha2(bean.getPiancha2());
		if(Utils.fnGetMeterInfo(getActivity()).getMeterNumbers()==2)
		{
			if(Double.valueOf(bean.getWucha2())<100)
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2())<i_meter_level)
					str_result=bean.getWucha2()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2(str_result);
				//tv_meter2_ev.setText(str_result);
			}
			if((Double.valueOf(bean.getWucha2_2())<100)&&(i_cishu>=2))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_2())<i_meter_level)
					str_result=bean.getWucha2_2()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_2()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_2(str_result);
			}
			if((Double.valueOf(bean.getWucha2_3())<100)&&(i_cishu>=3))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_3())<i_meter_level)
					str_result=bean.getWucha2_3()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_3()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_3(str_result);
			}
			if((Double.valueOf(bean.getWucha2_4())<100)&&(i_cishu>=4))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_4())<i_meter_level)
					str_result=bean.getWucha2_4()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_4()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_4(str_result);
			}
			if((Double.valueOf(bean.getWucha2_5())<100)&&(i_cishu>=5))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_5())<i_meter_level)
					str_result=bean.getWucha2_5()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_5()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_5(str_result);
			}
			if((Double.valueOf(bean.getWucha2_6())<100)&&(i_cishu>=6))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_6())<i_meter_level)
					str_result=bean.getWucha2_6()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_6()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_6(str_result);
			}
		}
		if(Double.valueOf(bean.getPiancha3())<100)
		jibenwuchaBean.setBiaozhunpiancha3(bean.getPiancha3());
		if(Utils.fnGetMeterInfo(getActivity()).getMeterNumbers()==3)
		{
			if(Double.valueOf(bean.getWucha2())<100)
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2())<i_meter_level)
					str_result=bean.getWucha2()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2(str_result);
				//tv_meter2_ev.setText(str_result);
			}
			if((Double.valueOf(bean.getWucha2_2())<100)&&(i_cishu>=2))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_2())<i_meter_level)
					str_result=bean.getWucha2_2()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_2()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_2(str_result);
			}
			if((Double.valueOf(bean.getWucha2_3())<100)&&(i_cishu>=3))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_3())<i_meter_level)
					str_result=bean.getWucha2_3()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_3()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_3(str_result);
			}
			if((Double.valueOf(bean.getWucha2_4())<100)&&(i_cishu>=4))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_4())<i_meter_level)
					str_result=bean.getWucha2_4()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_4()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_4(str_result);
			}
			if((Double.valueOf(bean.getWucha2_5())<100)&&(i_cishu>=5))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_5())<i_meter_level)
					str_result=bean.getWucha2_5()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_5()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_5(str_result);
			}
			if((Double.valueOf(bean.getWucha2_6())<100)&&(i_cishu>=6))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_6())<i_meter_level)
					str_result=bean.getWucha2_6()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha2_6()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha2_6(str_result);
			}
			if(Double.valueOf(bean.getWucha3())<100)
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3())<i_meter_level)
					str_result=bean.getWucha3()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha3()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha3(str_result);
				//tv_meter3_ev.setText(str_result);
			}
			if((Double.valueOf(bean.getWucha3_2())<100)&&(i_cishu>=2))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_2())<i_meter_level)
					str_result=bean.getWucha3_2()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha3_2()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha3_2(str_result);
			}
			if((Double.valueOf(bean.getWucha3_3())<100)&&(i_cishu>=3))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_3())<i_meter_level)
					str_result=bean.getWucha3_3()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha3_3()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha3_3(str_result);
			}
			if((Double.valueOf(bean.getWucha3_4())<100)&&(i_cishu>=4))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_4())<i_meter_level)
					str_result=bean.getWucha3_4()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha3_4()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha3_4(str_result);
			}
			if((Double.valueOf(bean.getWucha3_5())<100)&&(i_cishu>=5))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_5())<i_meter_level)
					str_result=bean.getWucha3_5()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha3_5()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha3_5(str_result);
			}
			if((Double.valueOf(bean.getWucha3_6())<100)&&(i_cishu>=6))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_6())<i_meter_level)
					str_result=bean.getWucha3_6()+" "+getText(R.string.qualified);
				else
					str_result=bean.getWucha3_6()+" "+getText(R.string.unqualified);
				jibenwuchaBean.setDiannengwucha3_6(str_result);
			}
		}
	}
	private void fnSendState()
	{
		if(dianya.equals("")||dianliu.equals("")||pinlv.equals("")||quanshu.equals("")||cishu.equals(""))
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
		int i_quanshu=Integer.valueOf(quanshu);
		int i_cishu=Integer.valueOf(cishu);
		Log.e(TAG,"f_pinlv:"+f_pinlv);
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
		
		else if(f_pinlv<40||f_pinlv>60)
		{
			Toast.makeText(getActivity(), getText(R.string.frequency_overload),Toast.LENGTH_SHORT).show();
			return;
		}
		Comm.getInstance().status_one=altek.fnTaitiShuChu(f_dianya, f_dianliu, 0,
				f_jiaodu, f_pinlv, 0, 0, 0, i_quanshu,
				i_cishu, 0, 0, 0);
		if (test_state) {
			Message msg = new Message();
			msg.what = 0x002;
			handler.sendMessage(msg);
		}
	}

}
