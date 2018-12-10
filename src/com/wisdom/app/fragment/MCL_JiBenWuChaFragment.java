package com.wisdom.app.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.wisdom.app.activity.ManualCheckLoadActivity;
import com.wisdom.app.activity.ParameterSettingActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.activityResult.JiBenWuChaResultActivity;
import com.wisdom.app.activityResult.MCL_JiBenWuChaResultActivity;
import com.wisdom.app.utils.*;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.JiBenWuChaDao;
import com.wisdom.layout.ITypePopupWindow;
import com.wisdom.layout.TitleLayout;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author wisdom's JiangYuPeng
 * @version 创建时间：2017-11-9 下午5:16:27
 * @version 修改时间：2018-8-18 上午9:50 JinJingYun 实负荷手动校验基本误差
 */
public class MCL_JiBenWuChaFragment extends Fragment {
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

	@Bind(R.id.tv_f)
	TextView tv_f;

	@Bind(R.id.meter2_circle)
	TextView tv_meter2_circle;
	@Bind(R.id.meter2_ev)
	TextView tv_meter2_ev;
	@Bind(R.id.meter2_count)
	TextView tv_meter2_count;


	@Bind(R.id.meter3_circle)
	TextView tv_meter3_circle;
	@Bind(R.id.meter3_ev)
	TextView tv_meter3_ev;
	@Bind(R.id.meter3_count)
	TextView tv_meter3_count;


	@Bind(R.id.meter_circle)
	EditText et_meter_circle;// 校验圈数

	@Bind(R.id.tv_U)
	TextView tv_U;// 电压

	@Bind(R.id.tv_I)
	TextView tv_I;

	@Bind(R.id.tv_jiaodu)
	TextView tv_jiaodu;

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
	@Bind(R.id.btn_select_i_type)
	Button btn_select_i_type;

	@Bind(R.id.dianya_yuan)
	TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)
	TextView tv_dianliuyuan;

	@Bind(R.id.tv_progress)
	TextView tv_progress;

	@Bind(R.id.tr_row2)
	TableRow tr_2;
	@Bind(R.id.tr_row3)
	TableRow tr_3;

	int time = 0;// 每一次校验的总时间
	int count = 0;// 上次次数
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences dianbiaocanshu;
	double i_meter_level = 0.5;
	private boolean send_state = false;
	private boolean recv_state = true;
	private boolean test_state = false;
	private JiBenWuChaBean jibenwuchaBean = null;
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
	private String dianya = "0";
	private String dianliu = "0";
	private String jiaodu = "0";
	private String pinlv = "0";
	private String yougong = "0";
	private String wugong = "0";
	private String zonggong = "0";
	private String gonglvyinshu = "0";
	// 666
	private String quanshu = "0";
	private String cishu = "0";
	// 电表信息
	public static String meter1_no = "0";
	public static String meter2_no = "0";
	public static String meter3_no = "0";
	public static String maichongchangshu = "0";

	private JiBenWuChaDao dao;
	private TaitiCeLiangShuJuBean bean;
	int i_cishu = 1;
	int iOFF = 0;// 用来判断源是否真得停止
	int iType = 0;// 电流采样方式,-1为设置
	int maxtime = 0;
	int send_count=0;
	
	int second_time=0;
	int error_times=0;
	private ITypePopupWindow popupwindow;
	String str_wucha="";
	long startMillSeconds=0;
	int time_count=0;//计时
	int first_time_count=0;//第一次误差计时
	boolean first_time_state=false;
	//误差集合
	private ArrayList<TaitiCeLiangShuJuBean> list_error;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x001) {
				try {
					bean = (TaitiCeLiangShuJuBean) msg.obj;
					if (bean != null) {
						if (Double.valueOf(bean.getU()) > 0) {
							tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
						}
						if (Double.valueOf(bean.getI()) > 0) {
							tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
						}
						if ((Double.valueOf(bean.getU()) > 0)
								|| (Double.valueOf(bean.getI()) > 0)) {
							MissionSingleInstance.getSingleInstance()
									.setYuan_state(true);
							if (test_state == false)
							{
								btn_start_test.setEnabled(true);
								btn_dianbiao_canshu.setEnabled(true);
								btn_jiaoyan_jieguo.setEnabled(true);
							}
						}
						if ((Double.valueOf(bean.getU()) == 0)
								&& (Double.valueOf(bean.getI()) == 0)) {
							iOFF++;
							if (iOFF == 3)// 断定源已停止
							{
								btn_start_test.setEnabled(true);
								btn_stop_test.setEnabled(false);
								tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
								tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));
								MissionSingleInstance.getSingleInstance()
										.setYuan_state(false);
								iOFF = 0;
							}
						}
						tv_U.setText(bean.getU());
						tv_I.setText(bean.getI());
						tv_f.setText(bean.getPinlv());
						tv_show_p.setText(bean.getYougong());
						tv_jiaodu.setText(bean.getJiaodu());
						tv_show_q.setText(bean.getWugong());
						tv_show_s.setText(bean.getZonggong());
						tv_show_pf.setText(bean.getGonglvyinshu());
						
						jiaodu=bean.getJiaodu();
						dianya=bean.getU();
						dianliu=bean.getI();
						
						// 上面三排
						try {
							if (test_state) {
								
								if(send_count==1)
								{
									send_count++;
									return;
								}
								if(meter_numbers==1)
										tv_progress.setText(bean.getMaichong1());
									else if(meter_numbers==2)
										tv_progress.setText(bean.getMaichong1()+","+bean.getMaichong2());
									else if(meter_numbers==3)
										tv_progress.setText(bean.getMaichong1()+","+bean.getMaichong2()+","+bean.getMaichong3());
								if(send_count==2&&(bean.getJieguo().equals("1")||bean.getJieguo().equals("2")))
								{
									send_count=0;
									//赋值
									tv_meter1_circle.setText(et_meter_circle
											.getText().toString());
									tv_meter2_circle.setText(et_meter_circle
											.getText().toString());
									tv_meter3_circle.setText(et_meter_circle
											.getText().toString());
									// tv_meter1_count.setText(bean.getCishu());
									// tv_meter2_count.setText(bean.getCishu());
									// tv_meter3_count.setText(bean.getCishu());
									// =Integer.valueOf(bean.getCishu());
									
									if (Double.valueOf(bean.getWucha1()) < 100) {
										tv_meter1_ev.setText(bean.getWucha1());
										i_cishu++;
										tv_meter1_count.setText(i_cishu + "");
										str_wucha=str_wucha+bean.getWucha1()+",";
										
										list_error.add(bean);	
										
										//JiBenWuChaBean jibenwuchaBean = new JiBenWuChaBean();
										jibenwuchaBean.setDate(ByteUtil.GetNowDate());
										jibenwuchaBean.setU(dianya);
										jibenwuchaBean.setI(dianliu);
										jibenwuchaBean.setJiaodu(jiaodu);
										jibenwuchaBean.setYougong(bean.getYougong());
										jibenwuchaBean.setWugong(bean.getWugong());
										jibenwuchaBean.setGonglvyinshu(bean.getGonglvyinshu());
										jibenwuchaBean.setMaichongchangshu(maichongchangshu);
										jibenwuchaBean.setQuanshu(quanshu);
										jibenwuchaBean.setCishu(i_cishu+"");
										jibenwuchaBean.setType("1");
										fnSetResult(str_wucha,i_cishu);
									}
									/*
									 * 队列方式
									 * 
									new Thread(){

										@Override
										public void run() {
										while(test_state)	
										{
											try {
												Thread.sleep(1000);
												time_count++;
												//long endMillSeconds=System.currentTimeMillis();
												//long totalMillSeconds=endMillSeconds-startMillSeconds;
												//double d_totalMillSeconds=totalMillSeconds/1000;
												Log.e("UF","time_count:"+time_count);
												if((time_count%time)==0)
												{
													
													final TaitiCeLiangShuJuBean errorBean=list_error.get(list_error.size()-1);
													if (Double.valueOf(errorBean.getWucha1()) < 100) {
														i_cishu++;
														getActivity().runOnUiThread(new Runnable(){

															@Override
															public void run() {
																// TODO Auto-generated method stub
																tv_meter1_ev.setText(errorBean.getWucha1());
																tv_meter1_count.setText(i_cishu + "");
																
															}
															
														});
														Log.e("UF","==================i_cishu:"+i_cishu);
														
														str_wucha=str_wucha+errorBean.getWucha1()+",";
														
														//JiBenWuChaBean jibenwuchaBean = new JiBenWuChaBean();
														jibenwuchaBean.setDate(ByteUtil.GetNowDate());
														jibenwuchaBean.setU(dianya);
														jibenwuchaBean.setI(dianliu);
														jibenwuchaBean.setJiaodu(jiaodu);
														jibenwuchaBean.setYougong(errorBean.getYougong());
														jibenwuchaBean.setWugong(errorBean.getWugong());
														jibenwuchaBean.setGonglvyinshu(errorBean.getGonglvyinshu());
														jibenwuchaBean.setMaichongchangshu(maichongchangshu);
														jibenwuchaBean.setQuanshu(quanshu);
														jibenwuchaBean.setCishu(i_cishu+"");
														jibenwuchaBean.setType("1");
														fnSetResult(str_wucha,i_cishu);
													}
											
												}
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											
										}
									}
									}.start();
									*/
								}
								else
								{
									if (Double.valueOf(bean.getWucha1()) < 100) {
										TaitiCeLiangShuJuBean errorBean=list_error.get(list_error.size()-1);
										if(errorBean.getWucha1().equals(bean.getWucha1()))
											return;
										i_cishu++;
										list_error.add(bean);	
										
										getActivity().runOnUiThread(new Runnable(){

											@Override
											public void run() {
												// TODO Auto-generated method stub
												tv_meter1_ev.setText(bean.getWucha1());
												tv_meter1_count.setText(i_cishu + "");
												
											}
											
										});
										Log.e("UF","==================i_cishu:"+i_cishu);
										
										str_wucha=str_wucha+bean.getWucha1()+",";
										
										//JiBenWuChaBean jibenwuchaBean = new JiBenWuChaBean();
										jibenwuchaBean.setDate(ByteUtil.GetNowDate());
										jibenwuchaBean.setU(dianya);
										jibenwuchaBean.setI(dianliu);
										jibenwuchaBean.setJiaodu(jiaodu);
										jibenwuchaBean.setYougong(bean.getYougong());
										jibenwuchaBean.setWugong(bean.getWugong());
										jibenwuchaBean.setGonglvyinshu(bean.getGonglvyinshu());
										jibenwuchaBean.setMaichongchangshu(maichongchangshu);
										jibenwuchaBean.setQuanshu(quanshu);
										jibenwuchaBean.setCishu(i_cishu+"");
										jibenwuchaBean.setType("1");
										fnSetResult(str_wucha,i_cishu);
									}

									/*
									 * 队列
									 * 
								if (Double.valueOf(bean.getWucha1()) < 100)
								{
								//TaitiCeLiangShuJuBean wuchaBean=new TaitiCeLiangShuJuBean();
								//jibenwuchaBean.setCishu(i_cishu+"");
								Log.e("UF","list_erro bean added");
								list_error.add(bean);	
								}
								*/
								//double remain_time=new BigDecimal(d_totalMillSeconds%time).floatValue();
								//double ne_remain_time=time-remain_time;
								/*
								double beishu=d_totalMillSeconds/time;
								int i_beishu=(int)beishu;
								double beishu_point=beishu-i_beishu;
								Log.i("UF","beishu:"+beishu+" point:"+beishu_point);
								
								if((beishu_point>0.3||beishu_point<-0.3))
									return;
								*/
					//			Log.i("UF","remain_time:"+remain_time+"ne_remain_time "+ne_remain_time+" d_totalMillSeconds:"+d_totalMillSeconds);
					//			
					//			if(!((remain_time<1.8)||(ne_remain_time<1.8)))
					//				return;
					//			tv_meter1_circle.setText(et_meter_circle
					//					.getText().toString());
					//			tv_meter2_circle.setText(et_meter_circle
					//					.getText().toString());
					//			tv_meter3_circle.setText(et_meter_circle
					//					.getText().toString());
								// tv_meter1_count.setText(bean.getCishu());
								// tv_meter2_count.setText(bean.getCishu());
								// tv_meter3_count.setText(bean.getCishu());
								// =Integer.valueOf(bean.getCishu());
								
								/*
								else
									error_times++;
								//无效结果，停止试验
								if(error_times==5)
								{
									error_times=0;
									tv_meter1_ev.setText("无效");
									i_cishu=0;
									recv_state = false;
									send_state = true;
									test_state = false;
									tv_progress.setText("-----");
									tv_progress.setVisibility(View.VISIBLE);
									bar.setVisibility(View.GONE);
									MissionSingleInstance.getSingleInstance().setTestState(
											test_state);
								}
				*/
								}//过滤第一次
								
								//second_time++;
								
								//initTextViewTime(time);
											}
						}  catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (msg.what == 0x002) {
				Toast.makeText(getActivity(), getText(R.string.start_testing), Toast.LENGTH_SHORT).show();
			} else if (msg.what == 0x003) {
				if (ManualCheckLoadActivity.METHOD == 1)
				{
					Log.i("UF","直接接入模式打开了");
					Toast.makeText(getActivity(), getText(R.string.toast_direct_access),
							Toast.LENGTH_SHORT).show();
				}
				else if (ManualCheckLoadActivity.METHOD == 3)
					Toast.makeText(getActivity(), getText(R.string.toast_100_current_clamp),
							Toast.LENGTH_SHORT).show();
				else if (ManualCheckLoadActivity.METHOD == 7)
					Toast.makeText(getActivity(), getText(R.string.toast_5_current_clamp),
							Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_mcl_jibenwucha, null);

		ButterKnife.bind(this, view);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_real_load_placeholder).toString(),getText(R.string.intrinsic_error).toString()));
		//title.setTitleText("实负荷手动校验->基本误差");

		btn_dianbiao_canshu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(getActivity(),
						ParameterSettingActivity.class);
				startActivity(intent);
			}
		});

		btn_start_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				list_error=new ArrayList<TaitiCeLiangShuJuBean>();
				
				
				initTopThreeLine();
				jibenwuchaBean=new JiBenWuChaBean();
				//jiaodu = MissionSingleInstance.getSingleInstance().getJiaodu();
				//dianya = MissionSingleInstance.getSingleInstance().getU();
				//dianliu = MissionSingleInstance.getSingleInstance().getI();
				//pinlv = MissionSingleInstance.getSingleInstance().getPinlv();
				count=0;
				send_count=1;
				quanshu = et_meter_circle.getText().toString();
				cishu = "1";
				
				str_wucha="";
				i_cishu=0;
				
				recv_state = false;
				send_state = true;
				test_state = true;
				
				//Comm.getInstance().cancelLoop();
				//计算发送时间
				int i_maichongchangshu=Integer.valueOf(maichongchangshu);
				double d_u=Double.valueOf(dianya);
				double d_i=Double.valueOf(dianliu);
				int i_quanshu=Integer.valueOf(quanshu);
				double d_jiaodu=Double.valueOf(jiaodu);
				d_jiaodu=d_jiaodu*3.14/180.0;
				d_jiaodu=Utils.round(d_jiaodu, 1);
				
				double cos=Math.cos(d_jiaodu);
				//10800 261311
				double d_time=1000*(i_quanshu*3600)/(d_u*d_i*cos*i_maichongchangshu);
				Log.e("UF","脉冲常数："+i_maichongchangshu+"\n电压："+d_u+"\n电流："+d_i+"\n圈数："+i_quanshu+"\n角度："+d_jiaodu+"\ncos："+cos+"\n发送间隔："+d_time);
				int i_time=(new Double(d_time)).intValue();
				if(i_time<0)
					i_time=-i_time;
				time=i_time;
				MissionSingleInstance.getSingleInstance().setTestState(
						test_state);
				//Comm.getInstance().init(handler);
				fnSendState();
				//Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
				
				//Comm.getInstance().millseconds=i_time*1000;
				btn_start_test.setEnabled(false);
				btn_stop_test.setEnabled(true);
				
				btn_dianbiao_canshu.setEnabled(false);
				btn_jiaoyan_jieguo.setEnabled(false);
				startMillSeconds=System.currentTimeMillis();
				//initTextViewTime(time);
				first_time_state=true;
				Toast.makeText(getActivity(), getText(R.string.test_round).toString()+new BigDecimal(time).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() +getText(R.string.second),Toast.LENGTH_SHORT).show();
				/*
				new Thread(){

					@Override
					public void run() {
						while(first_time_state)	
						{
							try {
								Thread.sleep(1000);
								first_time_count++;
								Log.e("UF","first_time_count:"+first_time_count);
								if((first_time_count/time)==5)
								{
									if(i_cishu<=0)
									{
									first_time_state=false;
									time_count=0;
									first_time_count=0;
									error_times=0;
									i_cishu=0;
									recv_state = false;
									send_state = true;
									test_state = false;
									MissionSingleInstance.getSingleInstance().setTestState(
											test_state);
									getActivity().runOnUiThread(new Runnable(){
										@Override
										public void run() {
											// TODO Auto-generated method stub
											tv_progress.setText("-----");
											tv_progress.setVisibility(View.VISIBLE);
											bar.setVisibility(View.GONE);
											
											Toast.makeText(getActivity(), "规定时间未出误差，停止试验",Toast.LENGTH_SHORT).show();
										}});
									}else
										first_time_state=false;
								}
							}catch(Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
					
				}.start();
				*/
			}
		});
		btn_stop_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//Comm.getInstance().cancelLoop();
				//Comm.getInstance().init(handler);
				//Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
			
				//Comm.getInstance().startLoop();
				//Comm.getInstance().millseconds=200;
				first_time_state=false;
				time_count=0;
				first_time_count=0;
				error_times=0;
				i_cishu=0;
				recv_state = false;
				send_state = true;
				test_state = false;
				tv_progress.setText("");
				
			
				MissionSingleInstance.getSingleInstance().setTestState(
						test_state);
				try {
					dianya = "0";
					dianliu = "0";
					jiaodu = "0";
					pinlv = "0";
					quanshu = "0";
					cishu = "0";
					getTaitiData();
					initTopThreeLine();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		initView();
		initData();

		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MCL_JiBenWuChaResultActivity.class);
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
					quanshu = "0";
					cishu = "0";
					recv_state = false;
					send_state = true;
					test_state = false;
					getTaitiData();
				} catch (Exception ex) {

				}
			}
		});
		btn_select_i_type.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupwindow = new ITypePopupWindow(getActivity(), itemsOnClick);
				// 显示窗口
				popupwindow.showAtLocation(
						getActivity().findViewById(R.id.mcl_layout),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		return view;
	}

	private void initView() {
		tr_2.setVisibility(View.INVISIBLE);
		tr_3.setVisibility(View.INVISIBLE);
		btn_yuan_shuchu.setEnabled(false);
		btn_yuan_tingzhi.setEnabled(false);
		sharedPreferences = getActivity().getSharedPreferences(
				"MCNL_JiBenWuCha", Context.MODE_PRIVATE); // 私有数据
		dianbiaocanshu = getActivity().getSharedPreferences("ParameterSetting",
				Context.MODE_PRIVATE);

		Resources resources = getActivity().getResources();
		Drawable not_editable = resources
				.getDrawable(R.drawable.cell_noteditable_bg);

	}
	private void fnSendState()
	{
			Comm.getInstance().status_one=altek.fnTaitiShuChu(Float.parseFloat("220"),
					Float.parseFloat("5"), 0,
					Float.parseFloat(jiaodu),
					Float.parseFloat(pinlv), 0, 0, 0,
					Integer.valueOf(quanshu),
					Integer.valueOf(cishu), 0, 0, 0);
			//send_state = false;
			//recv_state = true;
		/*
		 if (test_state) {
			Message msg = new Message();
			msg.what = 0x002;
			handler.sendMessage(msg);
		}
		*/
		
	}
	private void initTopThreeLine() {
		/*
		 * int
		 * meter_numbers=Utils.fnGetMeterInfo(getActivity()).getMeterNumbers();
		 * Log.e(TAG,"meter_numbers:"+meter_numbers); if(meter_numbers==2) {
		 * tr_3.setVisibility(View.INVISIBLE); tr_2.setVisibility(View.VISIBLE);
		 * } if(meter_numbers==1) { tr_3.setVisibility(View.INVISIBLE);
		 * tr_2.setVisibility(View.INVISIBLE); } if(meter_numbers==3) {
		 * tr_3.setVisibility(View.VISIBLE); tr_2.setVisibility(View.VISIBLE); }
		 */
		tv_meter1_circle.setText("");
		tv_meter2_circle.setText("");
		tv_meter3_circle.setText("");
		tv_meter1_ev.setText("");
		tv_meter2_ev.setText("");
		tv_meter3_ev.setText("");

		tv_meter1_count.setText("");
		tv_meter2_count.setText("");
		tv_meter3_count.setText("");

	}
	private void initTextViewTime(final int seconds)
	{
		//int count=seconds;
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=seconds;i>0;i--){
					
					try{
						final int x=i;
						Thread.sleep(1000);
						getActivity().runOnUiThread(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								//int i_count=count;
								tv_progress.setText(x);
							}
							
						});
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
					//int i_count=count--;

				}
			}}).start();		
	}
	private void initData() {
		try {
			dao = new JiBenWuChaDao(getActivity());
			// tv_chooser_u.setText(getDouble(sp_U.getSelectedItem().toString(),220));
			// int length=sp_chooser_i.getSelectedItem().toString().length();
			// tv_chooser_i.setText(getDouble(sp_I.getSelectedItem().toString(),Integer.valueOf(sp_chooser_i.getSelectedItem().toString().substring(0,length-1))));
			/*
			 * BluetoothGattService service =
			 * MainActivity.mBluetoothLeService.getGattService
			 * (BLEActivity.SERVICE_UUID); characteristic =
			 * service.getCharacteristic
			 * (UUID.fromString(BLEActivity.HEART_RATE_MEASUREMENT));
			 * MainActivity
			 * .mBluetoothLeService.setCharacteristicNotification(characteristic
			 * ); getActivity().registerReceiver(mGattUpdateReceiver,
			 * makeGattUpdateIntentFilter());
			 */
			dianbiaocanshu.getString("meter1_constant", "0");
			getTaitiData();
			recv_state = true;
			//timer = new Timer();
			//timer.schedule(new SendDataTask(), 1000, 1000);
			ManualCheckLoadActivity.METHOD = dianbiaocanshu.getInt("current_clamp",1);
			switch(ManualCheckLoadActivity.METHOD)
			{
			case 1:
				btn_select_i_type.setText(getText(R.string.direct_access));
				break;
			case 3:
				btn_select_i_type.setText(getText(R.string.txt_100_current_clamp));
				break;
			case 7:
				btn_select_i_type.setText(getText(R.string.txt_5_current_clamp));
				break;
				default:
					break;
			}
			iType = -1;
			//btn_select_i_type.setText("直接接入式");

			Comm.getInstance().init(handler);
			Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(
					ManualCheckLoadActivity.METHOD, 0,
					Integer.valueOf(maichongchangshu),
					Integer.valueOf(maichongchangshu), meter_numbers,
					meter1_no, meter2_no, meter3_no);
			
			Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
			Comm.getInstance().startLoop();
		} catch (Exception ex) {
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT)
					.show();
		}
	}

	public static int meter_numbers = 1;// 电表个数

	private void getTaitiData() {
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
		String meter_circle = sharedPreferences.getString("et_meter_circle",
				"0");
		String meter_count = sharedPreferences.getString("et_meter_count", "0");
		maichongchangshu = dianbiaocanshu.getString("meter1_constant", "1200");
		meter1_no = dianbiaocanshu.getString("meter1_no", "");
		meter2_no = dianbiaocanshu.getString("meter2_no", "");
		meter3_no = dianbiaocanshu.getString("meter3_no", "");
		meter1_no = Utils.fnGetMeterInfo(getActivity()).getMeter1_no();
		meter2_no = Utils.fnGetMeterInfo(getActivity()).getMeter2_no();
		meter3_no = Utils.fnGetMeterInfo(getActivity()).getMeter3_no();

		meter_numbers = Utils.fnGetMeterInfo(getActivity()).getMeterNumbers();
		String str_U = sharedPreferences.getString("sp_U", "");

		String str_I = sharedPreferences.getString("sp_I", "");
		String str_chooser_p = sharedPreferences.getString("sp_chooser_p", "");

		if (str_U != "")
			tv_U.setText(str_U);

		if (str_I != "")
			tv_I.setText(str_I);

		if (meter_circle != "")
			et_meter_circle.setText(meter_circle);

	}

	private void saveTaitiData() {
		try {
			Editor editor = sharedPreferences.edit();// 获取编辑器

			quanshu = et_meter_circle.getText().toString();
			editor.putString("et_meter_circle", quanshu);// 校验圈数
			cishu = "1";
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

	/*
	 * private void fnSendBytes(byte[] frame) { iTxLen = 0; str =
	 * ByteUtil.byte2HexStr(frame); str = str.toUpperCase(); iLen =
	 * str.length(); iStart = 0; iEnd = 0; // 一次最多发送20字节，分开发送 for (i = iStart; i
	 * < iLen;) { if (iLen < (i + 40)) { strCurTx = str.substring(i, iLen); }
	 * else { strCurTx = str.substring(i, i + 40); } if (characteristic != null)
	 * { characteristic.setValue(ByteUtil.hexStringToByte(strCurTx));
	 * MainActivity.mBluetoothLeService.writeCharacteristic(characteristic); } i
	 * += 40; try { Thread.sleep(20); } catch (InterruptedException e) {
	 * e.printStackTrace(); } if (i > iLen) break; else continue; } }
	 */

	/**
	 * 这个是真的广播接收器
	 */
	/*
	 * private final BroadcastReceiver mGattUpdateReceiver = new
	 * BroadcastReceiver() { String strRx = null, str2 = null; int iRxDataLen =
	 * 0;
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { final
	 * String action = intent.getAction(); // 连接成功更新界面顶部字体 if
	 * (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))// Gatt连接成功 {
	 * 
	 * } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))//
	 * Gatt连接失败 {
	 * 
	 * } else if
	 * (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))//
	 * 发现GATT服务器 {
	 * 
	 * } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//
	 * 有效数据 { // 处理发送过来的数据 strRx =
	 * intent.getExtras().getString(BluetoothLeService.EXTRA_DATA); //
	 * displayData(strRx); //接收报文区域//接收处理 iRxDataLen = strRx.length(); byte[]
	 * data = ds.fnReceiveData(strRx, iRxDataLen); if (data != null) { if
	 * ((data[2] & 0x7f) == 0x61)// 实时台体测量数据 { TaitiCeLiangShuJuBean bean =
	 * ds.fnGetTaitiData(data); if (bean != null) { Message msg = new Message();
	 * msg.what = 0x001; msg.obj = bean; handler.sendMessage(msg); } } if
	 * ((data[2] & 0x7f) == 0x5E)// 台体测试类型配置参数 { if
	 * (ManualCheckLoadActivity.METHOD == 1) Toast.makeText(getActivity(),
	 * "直接接入模式已打开", Toast.LENGTH_SHORT).show(); else if
	 * (ManualCheckLoadActivity.METHOD == 3) Toast.makeText(getActivity(),
	 * "100A电流钳模式已打开", Toast.LENGTH_SHORT).show(); else if
	 * (ManualCheckLoadActivity.METHOD == 7) Toast.makeText(getActivity(),
	 * "5A电流钳模式已打开", Toast.LENGTH_SHORT).show(); } } } } };
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			// 不在最前端显示 相当于调用了onPause();
			Log.i(TAG, "onHidden()");
			try {
				
				// getActivity().unregisterReceiver(mGattUpdateReceiver);
				//timer.cancel();
			} catch (Exception ex) {

			}

			return;
		} else { // 在最前端显示 相当于调用了onResume();
			Log.i(TAG, "onHiddenShow()");
			Comm.getInstance().init(handler);
			Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
			Comm.getInstance().startLoop();
			// getActivity().registerReceiver(mGattUpdateReceiver,
			// makeGattUpdateIntentFilter());
		//	timer = new Timer();
		//	timer.schedule(new SendDataTask(), 1000, 1000);

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
			// getActivity().unregisterReceiver(mGattUpdateReceiver);
		} catch (Exception ex) {

		}
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public Timer getTimer() {
		return this.timer;
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
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
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

	public class SendDataTask extends TimerTask {
		public SendDataTask() {
		}

		@Override
		public void run() {

			if (iType == -1) {
				if (ManualCheckLoadActivity.METHOD == 1) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							btn_select_i_type.setText(getText(R.string.direct_access));

						}
					});
				}
				Blue.send(altek.fnTaitiCeShiLeiXingPeiZhiCanshu(
						ManualCheckLoadActivity.METHOD, 0,
						Integer.valueOf(maichongchangshu),
						Integer.valueOf(maichongchangshu), meter_numbers,
						meter1_no, meter2_no, meter3_no), handler);
				Log.i("UF", "脉冲常数：" + maichongchangshu + " 表数量："
						+ meter_numbers + " 表号1:" + meter1_no + " 表号2:"
						+ meter2_no + " 表号3:" + meter3_no);
				iType++;
			}
			if (recv_state) {
				Blue.send(altek.fnGetFrameByFunctionCode((byte) 0x61), handler);
			} else if (send_state)// 执行一次后，执行查数据命令
			{
				Log.i("MCL", "taitishuchu!");
				Blue.send(
						altek.fnTaitiShuChu(Float.parseFloat("220"),
								Float.parseFloat("5"), 0,
								Float.parseFloat(jiaodu),
								Float.parseFloat(pinlv), 0, 0, 0,
								Integer.valueOf(quanshu),
								Integer.valueOf(cishu), 0, 0, 0), handler);
				send_state = false;
				recv_state = true;
				if (test_state) {
					Message msg = new Message();
					msg.what = 0x002;
					handler.sendMessage(msg);
				}
			}
		}
	}
	/*
	 * 根据测试结果得出测试结论，并将测试结论以','为间隔，存入电能误差1
	 * @param result 测试结果,以','为间隔
	 * @param count 测试结果的个数
	 * */
	private void fnSetResult(String result, int count) {
		if(result.endsWith(","))
			result=result.substring(0,result.length()-1);
		String[] arr_result = result.split(",");
		Log.i("MCL", "arr_result count:" + arr_result.length + " test count:"
				+ count);
		StringBuilder sb = new StringBuilder();

		if (arr_result.length != count)
			return;
		else {
			for (int i = 0; i < count; i++) {
				if ((Double.valueOf(arr_result[i]) < i_meter_level)&&(Double.valueOf(arr_result[i]) > -i_meter_level))
					sb.append(arr_result[i] + " " + getText(R.string.qualified_comma));
				else
					sb.append(arr_result[i] + " " + getText(R.string.unqualified_comma));
			}
			String str_result=sb.substring(0,sb.length()-1);
			jibenwuchaBean.setDiannengwucha1(str_result);
		}
	}

	private void fnSetResult() {
		if (Double.valueOf(bean.getPiancha1()) < 100) {
			jibenwuchaBean.setBiaozhunpiancha1(bean.getPiancha1());
		}
		if (Double.valueOf(bean.getWucha1()) < 100) {
			String str_result = "";
			if (Double.valueOf(bean.getWucha1()) < i_meter_level)
				str_result = bean.getWucha1() + " " + getText(R.string.qualified);
			else
				str_result = bean.getWucha1() + " " + "不合格";
			jibenwuchaBean.setDiannengwucha1(str_result);
		}
		if ((Double.valueOf(bean.getWucha1_2()) < 100) && (i_cishu >= 2)) {
			String str_result = "";
			if (Double.valueOf(bean.getWucha1_2()) < i_meter_level)
				str_result = bean.getWucha1_2() + " " + "合格";
			else
				str_result = bean.getWucha1_2() + " " + "不合格";
			jibenwuchaBean.setDiannengwucha1_2(str_result);
		}
		if ((Double.valueOf(bean.getWucha1_3()) < 100) && (i_cishu >= 3)) {
			String str_result = "";
			if (Double.valueOf(bean.getWucha1_3()) < i_meter_level)
				str_result = bean.getWucha1_3() + " " + "合格";
			else
				str_result = bean.getWucha1_3() + " " + "不合格";
			jibenwuchaBean.setDiannengwucha1_3(str_result);
		}
		if ((Double.valueOf(bean.getWucha1_4()) < 100) && (i_cishu >= 4)) {
			String str_result = "";
			if (Double.valueOf(bean.getWucha1_4()) < i_meter_level)
				str_result = bean.getWucha1_4() + " " + "合格";
			else
				str_result = bean.getWucha1_4() + " " + "不合格";
			jibenwuchaBean.setDiannengwucha1_4(str_result);
		}
		if ((Double.valueOf(bean.getWucha1_5()) < 100) && (i_cishu >= 5)) {
			String str_result = "";
			if (Double.valueOf(bean.getWucha1_5()) < i_meter_level)
				str_result = bean.getWucha1_5() + " " + "合格";
			else
				str_result = bean.getWucha1_5() + " " + "不合格";
			jibenwuchaBean.setDiannengwucha1_5(str_result);
		}
		if ((Double.valueOf(bean.getWucha1_6()) < 100) && (i_cishu >= 6)) {
			String str_result = "";
			if (Double.valueOf(bean.getWucha1_6()) < i_meter_level)
				str_result = bean.getWucha1_6() + " " + "合格";
			else
				str_result = bean.getWucha1_6() + " " + "不合格";
			jibenwuchaBean.setDiannengwucha1_6(str_result);
		}
		if (Double.valueOf(bean.getPiancha2()) < 100)
			jibenwuchaBean.setBiaozhunpiancha2(bean.getPiancha2());
		if (Utils.fnGetMeterInfo(getActivity()).getMeterNumbers() == 2) {
			if (Double.valueOf(bean.getWucha2()) < 100) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2()) < i_meter_level)
					str_result = bean.getWucha2() + " " + "合格";
				else
					str_result = bean.getWucha2() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2(str_result);
				// tv_meter2_ev.setText(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_2()) < 100) && (i_cishu >= 2)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_2()) < i_meter_level)
					str_result = bean.getWucha2_2() + " " + "合格";
				else
					str_result = bean.getWucha2_2() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_2(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_3()) < 100) && (i_cishu >= 3)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_3()) < i_meter_level)
					str_result = bean.getWucha2_3() + " " + "合格";
				else
					str_result = bean.getWucha2_3() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_3(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_4()) < 100) && (i_cishu >= 4)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_4()) < i_meter_level)
					str_result = bean.getWucha2_4() + " " + "合格";
				else
					str_result = bean.getWucha2_4() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_4(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_5()) < 100) && (i_cishu >= 5)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_5()) < i_meter_level)
					str_result = bean.getWucha2_5() + " " + "合格";
				else
					str_result = bean.getWucha2_5() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_5(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_6()) < 100) && (i_cishu >= 6)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_6()) < i_meter_level)
					str_result = bean.getWucha2_6() + " " + "合格";
				else
					str_result = bean.getWucha2_6() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_6(str_result);
			}
		}
		if (Double.valueOf(bean.getPiancha3()) < 100)
			jibenwuchaBean.setBiaozhunpiancha3(bean.getPiancha3());
		if (Utils.fnGetMeterInfo(getActivity()).getMeterNumbers() == 3) {
			if (Double.valueOf(bean.getWucha2()) < 100) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2()) < i_meter_level)
					str_result = bean.getWucha2() + " " + "合格";
				else
					str_result = bean.getWucha2() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2(str_result);
				// tv_meter2_ev.setText(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_2()) < 100) && (i_cishu >= 2)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_2()) < i_meter_level)
					str_result = bean.getWucha2_2() + " " + "合格";
				else
					str_result = bean.getWucha2_2() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_2(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_3()) < 100) && (i_cishu >= 3)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_3()) < i_meter_level)
					str_result = bean.getWucha2_3() + " " + "合格";
				else
					str_result = bean.getWucha2_3() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_3(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_4()) < 100) && (i_cishu >= 4)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_4()) < i_meter_level)
					str_result = bean.getWucha2_4() + " " + "合格";
				else
					str_result = bean.getWucha2_4() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_4(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_5()) < 100) && (i_cishu >= 5)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_5()) < i_meter_level)
					str_result = bean.getWucha2_5() + " " + "合格";
				else
					str_result = bean.getWucha2_5() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_5(str_result);
			}
			if ((Double.valueOf(bean.getWucha2_6()) < 100) && (i_cishu >= 6)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha2_6()) < i_meter_level)
					str_result = bean.getWucha2_6() + " " + "合格";
				else
					str_result = bean.getWucha2_6() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha2_6(str_result);
			}
			if (Double.valueOf(bean.getWucha3()) < 100) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha3()) < i_meter_level)
					str_result = bean.getWucha3() + " " + "合格";
				else
					str_result = bean.getWucha3() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha3(str_result);
				// tv_meter3_ev.setText(str_result);
			}
			if ((Double.valueOf(bean.getWucha3_2()) < 100) && (i_cishu >= 2)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha3_2()) < i_meter_level)
					str_result = bean.getWucha3_2() + " " + "合格";
				else
					str_result = bean.getWucha3_2() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha3_2(str_result);
			}
			if ((Double.valueOf(bean.getWucha3_3()) < 100) && (i_cishu >= 3)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha3_3()) < i_meter_level)
					str_result = bean.getWucha3_3() + " " + "合格";
				else
					str_result = bean.getWucha3_3() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha3_3(str_result);
			}
			if ((Double.valueOf(bean.getWucha3_4()) < 100) && (i_cishu >= 4)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha3_4()) < i_meter_level)
					str_result = bean.getWucha3_4() + " " + "合格";
				else
					str_result = bean.getWucha3_4() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha3_4(str_result);
			}
			if ((Double.valueOf(bean.getWucha3_5()) < 100) && (i_cishu >= 5)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha3_5()) < i_meter_level)
					str_result = bean.getWucha3_5() + " " + "合格";
				else
					str_result = bean.getWucha3_5() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha3_5(str_result);
			}
			if ((Double.valueOf(bean.getWucha3_6()) < 100) && (i_cishu >= 6)) {
				String str_result = "";
				if (Double.valueOf(bean.getWucha3_6()) < i_meter_level)
					str_result = bean.getWucha3_6() + " " + "合格";
				else
					str_result = bean.getWucha3_6() + " " + "不合格";
				jibenwuchaBean.setDiannengwucha3_6(str_result);
			}
		}
	}
	private void setSPCurrentClamp(int value)
	{
		Editor editor=dianbiaocanshu.edit();
		editor.putInt("current_clamp", value);
		editor.commit();
	}
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			popupwindow.dismiss();
			switch (v.getId()) {
			case R.id.tv_zhijiejieru:
				btn_select_i_type.setText(getText(R.string.direct_access));
				ManualCheckLoadActivity.METHOD = 1;
				
				Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(
						ManualCheckLoadActivity.METHOD, 0,
						Integer.valueOf(maichongchangshu),
						Integer.valueOf(maichongchangshu), meter_numbers,
						meter1_no, meter2_no, meter3_no);
			
				iType = -1;
				setSPCurrentClamp(ManualCheckLoadActivity.METHOD);
				break;
			case R.id.tv_100A:
				btn_select_i_type.setText(getText(R.string.txt_100_current_clamp));
				ManualCheckLoadActivity.METHOD = 3;
				Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(
						ManualCheckLoadActivity.METHOD, 0,
						Integer.valueOf(maichongchangshu),
						Integer.valueOf(maichongchangshu), meter_numbers,
						meter1_no, meter2_no, meter3_no);
			
				iType = -1;
				setSPCurrentClamp(ManualCheckLoadActivity.METHOD);
				
				break;
			case R.id.tv_5A:
				btn_select_i_type.setText(getText(R.string.txt_5_current_clamp));
				ManualCheckLoadActivity.METHOD = 7;
				Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(
						ManualCheckLoadActivity.METHOD, 0,
						Integer.valueOf(maichongchangshu),
						Integer.valueOf(maichongchangshu), meter_numbers,
						meter1_no, meter2_no, meter3_no);
			
				iType = -1;
				setSPCurrentClamp(ManualCheckLoadActivity.METHOD);
				
				break;
			default:
				break;
			}

		}

	};
class ClockThread implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
}
