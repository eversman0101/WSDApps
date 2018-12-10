package com.wisdom.app.fragment;

import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.wisdom.app.activity.ManualCheckLoadActivity;
import com.wisdom.app.activity.ManualCheckNoneLoadActivity;
import com.wisdom.app.activity.ParameterSettingActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.activityResult.ShiZhongResultActivity;
import com.wisdom.app.utils.*;
import com.wisdom.bean.ShiZhongWuChaBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.IShiZhongDao;
import com.wisdom.dao.ShiZhongDao;
import com.wisdom.layout.IClockPopupWindow;
import com.wisdom.layout.ITypePopupWindow;
import com.wisdom.layout.TitleLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author JinJingYun 虚负荷手动校验-时钟误差
 */
public class MCNL_ShiZhongWuChaFragment extends Fragment {
	private String TAG = "MCNL_ShiZhongWuChaFragment";
	@Bind(R.id.title_MCNL_shizhong)
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
	@Bind(R.id.meter1_result_clock)
	TextView tv_meter1_result_clock;// 时钟误差
	//@Bind(R.id.meter1_result_clock_avg)
	//TextView tv_meter1_result_clock_avg;// 平均误差
	@Bind(R.id.meter1_count)
	TextView tv_meter1_count;// 次数

	@Bind(R.id.meter2_circle)
	TextView tv_meter2_circle;// 电表2圈数
	@Bind(R.id.meter2_result_clock)
	TextView tv_meter2_result_clock;// 时钟误差
	//@Bind(R.id.meter2_result_clock_avg)
	//TextView tv_meter2_result_clock_avg;// 平均误差
	@Bind(R.id.meter2_count)
	TextView tv_meter2_count;// 次数

	@Bind(R.id.meter3_circle)
	TextView tv_meter3_circle;// 电表3圈数
	@Bind(R.id.meter3_result_clock)
	TextView tv_meter3_result_clock;// 时钟误差
	//@Bind(R.id.meter3_result_clock_avg)
	//TextView tv_meter3_result_clock_avg;// 平均误差
	@Bind(R.id.meter3_count)
	TextView tv_meter3_count;// 次数

	@Bind(R.id.meter_time)
	EditText et_celiangshijian;// 测量时间
	@Bind(R.id.meter_count)
	EditText et_meter_count;// 校验次数

	@Bind(R.id.option_u)
	TextView option_u;// 电压设置
	@Bind(R.id.option_i)
	TextView option_i;// 电流设置

	@Bind(R.id.option_p)
	TextView option_p;//

	@Bind(R.id.option_f)
	EditText option_f;//

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

	@Bind(R.id.yuan_shuchu)
	Button btn_yuan_shuchu;
	@Bind(R.id.yuan_tingzhi)
	Button btn_yuan_tingzhi;

	@Bind(R.id.dianya_yuan)
	TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)
	TextView tv_dianliuyuan;
	@Bind(R.id.progressbar)
	ProgressBar bar;
	@Bind(R.id.meter_circle)
	EditText et_meter_circle;
	@Bind(R.id.run_time)
	TextView tv_run_time;

	@Bind(R.id.btn_set_clock)
	Button btn_set_clock;
	@Bind(R.id.tr_row2)
	TableRow tr_2;
	@Bind(R.id.tr_row3)
	TableRow tr_3;
	private SharedPreferences sharedPreferences_jibenwucha;
	private SharedPreferences sharedPreferences_shizhongwucha;
	private boolean send_state = false;
	private boolean recv_state = false;
	private boolean test_state = false;
	private SharedPreferences dianbiaocanshu;

	// Dialog
	private ChooseUDialog chooseUDialog;
	private ChooseIDialog chooseIDialog;
	private ChooseQuanDialog chooseQuanDialog;
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
	private String Mdianya = "220";
	private String Mdianliu = "0";
	private String Mjiaodu = "0";
	private String Mpinlv = "50";
	private String Mquanshu = "3";

	private String dianya;
	private String dianliu;
	private String jiaodu;
	private String pinlv;

	private String cishu = "2";
	private String ceshishijian;
	private String shengyushijian;
	private IShiZhongDao dao;
	private TaitiCeLiangShuJuBean bean;

	int index = 0;

	ShiZhongWuChaBean shizhongwuchaBean;
	int count = 0;
	int xufuhe = 0;
	int iOFF = 0;
	// 电表信息
	private String meter1_no = "0";
	private String meter2_no = "0";
	private String meter3_no = "0";
	private String maichongchangshu = "0";
	int meter_numbers = 1;// 电表个数
	int shizhong_state = 0;
	int maxtime = 0;
	int time = 0;
	private IClockPopupWindow popupwindow;
	private Dialog progressDialog;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x001) {
				try {
					bean = (TaitiCeLiangShuJuBean) msg.obj;
					if (bean != null) {
						if (Double.valueOf(bean.getU()) > 0 || Double.valueOf(bean.getI()) > 0) {
							tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
							tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
						}
						if ((Double.valueOf(bean.getU()) > 0) || (Double.valueOf(bean.getI()) > 0)) {
							//btn_yuan_shuchu.setEnabled(false);
							btn_yuan_tingzhi.setEnabled(true);
							MissionSingleInstance.getSingleInstance().setYuan_state(true);
							if (test_state == false && !btn_stop_test.isEnabled()
									&& Double.valueOf(tv_chooser_u.getText().toString()) > Double
											.valueOf(option_u.getText().toString()) - 2
									&& Double.valueOf(tv_chooser_u.getText().toString()) < Double
											.valueOf(option_u.getText().toString()) + 2)
								btn_start_test.setEnabled(true);
						}
						if ((Double.valueOf(bean.getU()) == 0) && (Double.valueOf(bean.getI()) == 0)) {
							iOFF++;
							if (iOFF == 3)// 断定源已停止
							{
								btn_start_test.setEnabled(false);
								btn_stop_test.setEnabled(false);
								btn_yuan_shuchu.setEnabled(true);
								btn_yuan_tingzhi.setEnabled(true);
								tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
								tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));
								MissionSingleInstance.getSingleInstance().setTestState(false);
								MissionSingleInstance.getSingleInstance().setYuan_state(false);
								iOFF = 0;
							}
						}
						// 上面三排
						tv_meter1_circle.setText(Mquanshu);
						tv_meter2_circle.setText(Mquanshu);
						tv_meter3_circle.setText(Mquanshu);
						tv_meter1_count.setText(bean.getCishu());
						tv_meter2_count.setText(bean.getCishu());
						tv_meter3_count.setText(bean.getCishu());
						//下面3拍
						tv_chooser_u.setText(bean.getU());
						tv_chooser_i.setText(bean.getI());
						tv_chooser_f.setText(bean.getPinlv());
						tv_show_p.setText(bean.getYougong());
						tv_show_q.setText(bean.getWugong());
						tv_show_s.setText(bean.getZonggong());
						tv_show_pf.setText(bean.getGonglvyinshu());
						tv_jiaodu.setText(bean.getJiaodu());
						Log.e("MCNL_ShiZhong", "wucha1:" + bean.getWucha1());
						Log.e("MCNL_ShiZhong", "piancha1:" + bean.getPiancha1());
						Log.i("TAG", "次数：" + bean.getCishu());
						if (test_state) {
							int i_cishu=Integer.valueOf(bean.getCishu());
						    if((i_cishu>0)&&(maxtime>0))
						    {
								//tv_meter1_result_clock_avg.setText(bean.getPiancha1());
								//tv_meter2_result_clock_avg.setText(bean.getPiancha2());
								//tv_meter3_result_clock_avg.setText(bean.getPiancha3());
								if(i_cishu==1)
								{
									if (Double.valueOf(bean.getWucha1()) < 100)
										tv_meter1_result_clock.setText(bean.getWucha1());
									if (Double.valueOf(bean.getWucha2()) < 100)
										tv_meter2_result_clock.setText(bean.getWucha2());
									if (Double.valueOf(bean.getWucha3()) < 100)
										tv_meter3_result_clock.setText(bean.getWucha3());

								}
								if(i_cishu==2)
								{
									if(Double.valueOf(bean.getWucha1_2())<100)
										tv_meter1_result_clock.setText(bean.getWucha1());
									if(Double.valueOf(bean.getWucha2_2())<100)
										tv_meter2_result_clock.setText(bean.getWucha2_2());
									if(Double.valueOf(bean.getWucha3_2())<100)
										tv_meter3_result_clock.setText(bean.getWucha3_2());
								}
								if(i_cishu==3)
								{
									if(Double.valueOf(bean.getWucha1_3())<100)
										tv_meter1_result_clock.setText(bean.getWucha1_3());
									if(Double.valueOf(bean.getWucha2_3())<100)
										tv_meter2_result_clock.setText(bean.getWucha2_3());
									if(Double.valueOf(bean.getWucha3_3())<100)
										tv_meter3_result_clock.setText(bean.getWucha3_3());
								}
								if(i_cishu==4)
								{
									if(Double.valueOf(bean.getWucha1_4())<100)
										tv_meter1_result_clock.setText(bean.getWucha1_4());
									if(Double.valueOf(bean.getWucha2_4())<100)
										tv_meter2_result_clock.setText(bean.getWucha2_4());
									if(Double.valueOf(bean.getWucha3_4())<100)
										tv_meter3_result_clock.setText(bean.getWucha3_4());
								}
								if(i_cishu==5)
								{
									if(Double.valueOf(bean.getWucha1_5())<100)
										tv_meter1_result_clock.setText(bean.getWucha1_5());
									if(Double.valueOf(bean.getWucha2_5())<100)
										tv_meter2_result_clock.setText(bean.getWucha2_5());
									if(Double.valueOf(bean.getWucha3_5())<100)
										tv_meter3_result_clock.setText(bean.getWucha3_5());
								}
								if(i_cishu==6)
								{
									if(Double.valueOf(bean.getWucha1_6())<100)
										tv_meter1_result_clock.setText(bean.getWucha1());
									if(Double.valueOf(bean.getWucha2_6())<100)
										tv_meter2_result_clock.setText(bean.getWucha2_6());
									if(Double.valueOf(bean.getWucha3_6())<100)
										tv_meter3_result_clock.setText(bean.getWucha3_6());
								}
						    }
							if (time == 0 && (Integer.valueOf(bean.getTime()) > 5)) {
								time = Integer.valueOf(bean.getTime());
								if (maxtime < time)
									maxtime = time;
							}
							if (count != Integer.valueOf(bean.getCishu())) {
								// 新一次
								secondState = 0;
								count = Integer.valueOf(bean.getCishu());
								time = Integer.valueOf(bean.getTime());
							}
							setBarProgress(time, Integer.valueOf(bean.getTime()));
							tv_run_time.setText(bean.getTime());
							Log.e("MCNL_ShiZhong", "maxtime:" + maxtime);
							if (Integer.valueOf(bean.getJieguo()) == 2 && (Integer.valueOf(bean.getTime()) == 0)
									&& (maxtime > 0)) {
								try {
									
									// Log.e("MCNL_ShiZhong",
									// "wucha1:"+bean.getWucha1());
									test_state = false;
									MissionSingleInstance.getSingleInstance().setTestState(test_state);
									Toast.makeText(getActivity(), getText(R.string.job_done), Toast.LENGTH_SHORT).show();
									btn_start_test.setEnabled(false);
									btn_stop_test.setEnabled(false);
									bar.setProgress(100);
									time = 0;
									maxtime = 0;
									secondState = 0;
									xufuhe = -1;
									count = 0;

									// recv_state=false;
									shizhongwuchaBean = new ShiZhongWuChaBean();
									shizhongwuchaBean.setDate(ByteUtil.GetNowDate());
									shizhongwuchaBean.setQuanshu(Mquanshu);
									shizhongwuchaBean.setCishu(cishu);
									shizhongwuchaBean.setShizhongwucha1(bean.getWucha1());
									shizhongwuchaBean.setShizhongwucha2(bean.getWucha2());
									shizhongwuchaBean.setShizhongwucha3(bean.getWucha3());
									shizhongwuchaBean.setPingjunwucha1(bean.getPiancha1());
									shizhongwuchaBean.setPingjunwucha2(bean.getPiancha2());
									shizhongwuchaBean.setPingjunwucha3(bean.getPiancha3());
									
									shizhongwuchaBean.setShizhongwucha1_2(bean.getWucha1_2());
									shizhongwuchaBean.setShizhongwucha1_3(bean.getWucha1_2());
									shizhongwuchaBean.setShizhongwucha1_4(bean.getWucha1_2());
									shizhongwuchaBean.setShizhongwucha1_5(bean.getWucha1_2());
									shizhongwuchaBean.setShizhongwucha1_6(bean.getWucha1_2());
									
									shizhongwuchaBean.setShizhongwucha2_2(bean.getWucha2_2());
									shizhongwuchaBean.setShizhongwucha2_3(bean.getWucha2_3());
									shizhongwuchaBean.setShizhongwucha2_4(bean.getWucha2_4());
									shizhongwuchaBean.setShizhongwucha2_5(bean.getWucha2_5());
									shizhongwuchaBean.setShizhongwucha2_6(bean.getWucha2_6());
									
									shizhongwuchaBean.setShizhongwucha3_2(bean.getWucha3_2());
									shizhongwuchaBean.setShizhongwucha3_3(bean.getWucha3_3());
									shizhongwuchaBean.setShizhongwucha3_4(bean.getWucha3_4());
									shizhongwuchaBean.setShizhongwucha3_5(bean.getWucha3_5());
									shizhongwuchaBean.setShizhongwucha3_6(bean.getWucha3_6());
									shizhongwuchaBean.setType("0");

									// dao.add(shizhongwuchaBean);

								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}

					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (msg.what == 0x002) {

			} else if (msg.what == 0x003) {
				if (ManualCheckNoneLoadActivity.METHOD == 2)
					Toast.makeText(getActivity(), getText(R.string.second_pulse_mode_open), Toast.LENGTH_SHORT).show();
				else if (ManualCheckNoneLoadActivity.METHOD == 0)
					Toast.makeText(getActivity(), getText(R.string.virtual_mode_open), Toast.LENGTH_SHORT).show();
			}
			else if(msg.what==0x008)
			{
				try {
					int[] arr_clock = (int[]) msg.obj;
					if (arr_clock != null) {
						popupwindow.setGPSClock(arr_clock);
						progressDialog.dismiss();
						// 显示窗口
						popupwindow.showAtLocation(getActivity().findViewById(R.id.mcnl_shizhong),
								Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
					}
				} catch (Exception ex) {
					progressDialog.dismiss();
					Toast.makeText(getActivity(), getText(R.string.setting_failed) + ex.toString(), Toast.LENGTH_SHORT).show();
					ex.printStackTrace();
				}
			}
			else if (msg.what == 0x009) {
				try {
					int[] arr_clock = (int[]) msg.obj;
					if (arr_clock != null) {
						popupwindow.setClock(arr_clock);
						//byte[] data = altek.fnSetClock(arr_clock[0], arr_clock[1], arr_clock[2], arr_clock[3],
						//arr_clock[4], arr_clock[5]);
						//Comm.getInstance().status_one = data;
						final byte[] data = altek.fnGetFrameByFunctionCode((byte) 0x7E);
						Comm.getInstance().status_one = data;
					}
				} catch (Exception ex) {
					progressDialog.dismiss();
					Toast.makeText(getActivity(), getText(R.string.setting_failed) + ex.toString(), Toast.LENGTH_SHORT).show();
					ex.printStackTrace();
				}
			} else if (msg.what == 0x10) {
				Toast.makeText(getActivity(),getText(R.string.voltage_overload), Toast.LENGTH_SHORT).show();
			} else if (msg.what == 0x11) {
				Toast.makeText(getActivity(), getText(R.string.current_overload), Toast.LENGTH_SHORT).show();
			} else if (msg.what == 0x12) {
				Toast.makeText(getActivity(), getText(R.string.format_error), Toast.LENGTH_SHORT).show();
			} else if (msg.what == 0x13) {
				Toast.makeText(getActivity(), getText(R.string.clock_set_success), Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mcnl_shizhong_wucha, null);
		ButterKnife.bind(this, view);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.clock_error_test).toString()));
		
		initData();
		getTaitiData();
		btn_dianbiao_canshu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ParameterSettingActivity.class);
				startActivity(intent);
			}
		});
		btn_start_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initTopThreeLine();
				String s_shijian = et_celiangshijian.getText().toString();
				String s_cishu = et_meter_count.getText().toString();
				String s_quanshu = et_meter_circle.getText().toString();
				int i_cishu=Integer.valueOf(s_cishu);
				if(i_cishu>6)
				{
					Toast.makeText(getActivity(), getText(R.string.count_limit), Toast.LENGTH_SHORT).show();
					return;
				}
				if (s_shijian.equals("") || s_cishu.equals("")||s_quanshu.equals("")||s_shijian.equals("0") || s_cishu.equals("0")||s_quanshu.equals("0"))
				{
					Toast.makeText(getActivity(), getText(R.string.format_illegal), Toast.LENGTH_SHORT).show();
					return;
				}
				else {
					saveData();
					recv_state = false;
					test_state = true;
					MissionSingleInstance.getSingleInstance().setTestState(test_state);
					send_state = true;
					bar.setProgress(0);
					Toast.makeText(getActivity(), getText(R.string.start_testing), Toast.LENGTH_SHORT).show();
					btn_start_test.setEnabled(false);
					btn_stop_test.setEnabled(true);
					fnSendState();
				}
			}
		});
		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ShiZhongResultActivity.class);
				intent.putExtra("shizhongwuchaBean", shizhongwuchaBean);
				startActivity(intent);
			}
		});
		
		btn_stop_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recv_state = false;
				send_state = true;
				test_state = false;
				// MissionSingleInstance.getSingleInstance().setTestState(test_state);

				try {
					dianya = "0";
					dianliu = "0";
					jiaodu = "0";
					//pinlv = "0";
					getTaitiData();
					getData();
					fnSendState();
				} catch (Exception ex) {

				}
			}
		});
		btn_yuan_shuchu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveData();
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
					//pinlv = "0";
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
		btn_set_clock.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				progressDialog.show();
				
				final byte[] data = altek.fnGetFrameByFunctionCode((byte) 0x06);
				Comm.getInstance().status_one = data;
				popupwindow = new IClockPopupWindow(getActivity(), itemsOnClick);
			}
		});
		option_u.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseUDialog = new ChooseUDialog(getActivity());
				chooseUDialog.setTitle(getText(R.string.dialog_change_voltage));
				// chooseUDialog.setEditUStr(sharedPreferences.getString("tv_chooser_u",
				// ""));
				chooseUDialog.setYesOnclickListener(getText(R.string.dialog_confirm).toString(), new ChooseUDialog.onYesOnclickListener() {
					@Override
					public void onYesClick() {
						option_u.setText(chooseUDialog.getEditUStr());
						saveData();
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
						saveData();
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
		option_p.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				chooseQuanDialog = new ChooseQuanDialog(getActivity());
				chooseQuanDialog.setTitle(getText(R.string.dialog_change_quan));
				chooseQuanDialog.setYesOnclickListener(getText(R.string.dialog_confirm).toString(), new ChooseQuanDialog.onYesOnclickListener() {
					@Override
					public void onYesClick() {
						option_p.setText(chooseQuanDialog.getEditQuanStr());
						saveData();
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

	private void getTaitiData() {
		dianbiaocanshu = getActivity().getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
		maichongchangshu = dianbiaocanshu.getString("meter1_constant", "1200");
		meter1_no = dianbiaocanshu.getString("meter1_no", "");
		meter2_no = dianbiaocanshu.getString("meter2_no", "");
		meter3_no = dianbiaocanshu.getString("meter3_no", "");
		if(maichongchangshu.equals(""))
			maichongchangshu="0";
		if (meter1_no == "")
			meter1_no = "0";
		if (meter2_no != "")
			meter_numbers++;
		else
			meter2_no = "0";
		if (meter3_no != "")
			meter_numbers++;
		else
			meter3_no = "0";
		// fnSendBytes(altek.fnTaitiCeShiLeiXingPeiZhiCanshu(2, 0,
		// Integer.valueOf(maichongchangshu), Integer.valueOf(maichongchangshu),
		// meter_numbers, Integer.valueOf(meter1_no),
		// Integer.valueOf(meter2_no), Integer.valueOf(meter3_no)));
	}

	private void initData() {
		try {
			progressDialog = new Dialog(getActivity(), R.style.progress_dialog);
			progressDialog.setContentView(R.layout.dialog);
			progressDialog.setCancelable(true);
			progressDialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			TextView msg = (TextView) progressDialog
					.findViewById(R.id.id_tv_loadingmsg);
			msg.setText("加载中....");
			
			if (MissionSingleInstance.getSingleInstance().isYuan_state()) {
				tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
				tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
				btn_start_test.setEnabled(true);
				//btn_yuan_shuchu.setEnabled(false);
				btn_yuan_tingzhi.setEnabled(true);
			} else if (!MissionSingleInstance.getSingleInstance().isYuan_state()) {
        		tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
				tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));
				btn_start_test.setEnabled(false);
				btn_yuan_shuchu.setEnabled(true);
				btn_yuan_tingzhi.setEnabled(true);
			}
			dao = new ShiZhongDao(getActivity());
			sharedPreferences_jibenwucha = getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE);
			sharedPreferences_shizhongwucha = getActivity().getSharedPreferences("MCNL_ShiZhongWuCha",
					Context.MODE_PRIVATE);
			Mdianya = sharedPreferences_jibenwucha.getString("tv_chooser_u", "220");
			//Mdianliu = sharedPreferences_jibenwucha.getString("tv_chooser_i", "10");
			Mjiaodu = MissionSingleInstance.getSingleInstance().getJiaodu();
			Mpinlv = sharedPreferences_jibenwucha.getString("et_chooser_f", "0");
			Mquanshu = sharedPreferences_jibenwucha.getString("et_meter_circle", "3");
			tv_jiaodu.setText(Mjiaodu);
			option_u.setText(sharedPreferences_jibenwucha.getString("tv_chooser_u", "220.0"));
			//option_i.setText(sharedPreferences_jibenwucha.getString("tv_chooser_i", "5.0"));
			option_i.setText("0.0");
			
			Comm.getInstance().init(handler);
			ManualCheckNoneLoadActivity.METHOD = 2;
			Comm.getInstance().status_one = altek.fnTaitiCeShiLeiXingPeiZhiCanshu(2, 0,
					Integer.valueOf(maichongchangshu), Integer.valueOf(maichongchangshu), meter_numbers, meter1_no,
					meter2_no, meter3_no);
			Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
			recv_state = true;
			getData();
		} catch (Exception ex) {
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}

	private void getData() {
		
		String quanshu = sharedPreferences_shizhongwucha.getString("quanshu", "3");
		String shijian = sharedPreferences_shizhongwucha.getString("shijian", "1");
		String cishu = sharedPreferences_shizhongwucha.getString("cishu", "2");
		String str_OptionU = sharedPreferences_jibenwucha.getString("tv_chooser_u", "220.0");
		String str_OptionI = sharedPreferences_jibenwucha.getString("tv_chooser_i", "5.0");
		String str_OptionP = sharedPreferences_jibenwucha.getString("sp_chooser_p", "0.0");
		if (str_OptionU != "")
			option_u.setText(str_OptionU);
		//if (str_OptionI != "")
		//	option_i.setText(str_OptionI);
		if (str_OptionP != "") {
			option_p.setText(str_OptionP);
		}
		if (shijian != null)
			et_celiangshijian.setText(shijian);
		if (cishu != null)
			et_meter_count.setText(cishu);
		if (quanshu != null)
			et_meter_circle.setText(quanshu);
	}

	private void initTopThreeLine() {
		int meter_numbers=Utils.fnGetMeterInfo(getActivity()).getMeterNumbers();
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
		tv_meter1_result_clock.setText("");
		tv_meter2_result_clock.setText("");
		tv_meter3_result_clock.setText("");

		tv_meter1_count.setText("");
		tv_meter2_count.setText("");
		tv_meter3_count.setText("");

		//tv_meter1_result_clock_avg.setText("");
		//tv_meter2_result_clock_avg.setText("");
		//tv_meter3_result_clock_avg.setText("");
	}

	private void saveData() {
		try {
			dianya = option_u.getText().toString();
			dianliu = option_i.getText().toString();
			jiaodu = option_p.getText().toString();
			pinlv = option_f.getText().toString();

			MissionSingleInstance.getSingleInstance().setU(dianya);
			MissionSingleInstance.getSingleInstance().setI(dianliu);
			MissionSingleInstance.getSingleInstance().setJiaodu(jiaodu);
			MissionSingleInstance.getSingleInstance().setPinlv(pinlv);
			Editor editor1 = sharedPreferences_jibenwucha.edit();
			Editor editor = sharedPreferences_shizhongwucha.edit();// 获取编辑器
			ceshishijian = et_celiangshijian.getText().toString();
			cishu = et_meter_count.getText().toString();
			Mquanshu = et_meter_circle.getText().toString();
			editor.putString("quanshu", Mquanshu);
			editor.putString("shijian", ceshishijian);
			editor.putString("cishu", cishu);
			editor.commit();
			editor1.putString("tv_chooser_u", option_u.getText().toString());
			//editor1.putString("tv_chooser_i", option_i.getText().toString());
			editor1.putString("sp_chooser_p", option_p.getText().toString());
			editor1.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			saveData();
			// 不在最前端显示 相当于调用了onPause();
			Log.i(TAG, "onHidden()");
			try {
				ManualCheckNoneLoadActivity.METHOD = 0;
				Comm.getInstance().status_one = altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0,
						Integer.valueOf(maichongchangshu), Integer.valueOf(maichongchangshu), meter_numbers, meter1_no,
						meter2_no, meter3_no);
				// getActivity().unregisterReceiver(mGattUpdateReceiver);
				// timer.cancel();
			} catch (Exception ex) {

			}

			return;
		} else { // 在最前端显示 相当于调用了onResume();
			Log.i(TAG, "onHiddenShow()");
			getData();
			// getActivity().registerReceiver(mGattUpdateReceiver,
			// MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
			Comm.getInstance().init(handler);
			ManualCheckNoneLoadActivity.METHOD = 2;
			Comm.getInstance().status_one = altek.fnTaitiCeShiLeiXingPeiZhiCanshu(2, 0,
					Integer.valueOf(maichongchangshu), Integer.valueOf(maichongchangshu), meter_numbers, meter1_no,
					meter2_no, meter3_no);

			Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
			// timer=new Timer();
			// timer.schedule(new SendDataTask(), 1000, 1000);
			if (MissionSingleInstance.getSingleInstance().isYuan_state()) {
				tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
				tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
				//btn_yuan_shuchu.setEnabled(false);
				btn_yuan_tingzhi.setEnabled(true);
			} else if (!MissionSingleInstance.getSingleInstance().isYuan_state()) {
        		tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_stopped));
				tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_stopped));
				btn_yuan_shuchu.setEnabled(true);
				btn_yuan_tingzhi.setEnabled(true);
				btn_start_test.setEnabled(false);
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
			// getActivity().unregisterReceiver(mGattUpdateReceiver);
			// timer.cancel();
		} catch (Exception ex) {

		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG, "onPause()");
		saveData();
	}

	int secondState = 0;

	private void setBarProgress(int second, int leftSecond) {
		if (second != 0) {
			int s = (second - leftSecond) - secondState;// 增加的时间
			int quarter = 100 / (second * Integer.valueOf(cishu));
			bar.incrementProgressBy(s * quarter);
			secondState = second - leftSecond;
		}
	}

	int x = 0;

	private void fnSendState() {
		if (send_state)// 执行一次后，执行查数据命令
		{
			if (test_state)
				Comm.getInstance().status_one = altek.fnTaitiShuChu(Float.parseFloat(Mdianya),
						Float.parseFloat(Mdianliu), 0, Float.parseFloat(Mjiaodu), Float.parseFloat(Mpinlv), 0, 0, 0,
						Integer.valueOf(Mquanshu), Integer.valueOf(cishu), Integer.valueOf(ceshishijian), 11, 0);
			else {
				if (dianya.equals("") || dianliu.equals("") || pinlv.equals("")) {
					Message msg = new Message();
					msg.what = 0x12;
					handler.sendMessage(msg);
					send_state = false;
					return;
				}
				float f_dianya = Float.parseFloat(dianya);
				float f_dianliu = Float.parseFloat(dianliu);
				float f_jiaodu = Float.parseFloat(jiaodu);
				float f_pinlv = Float.parseFloat(pinlv);
				if (f_dianya > 264) {
					Message msg = new Message();
					msg.what = 0x10;
					handler.sendMessage(msg);
					send_state = false;
					return;
				} else if (f_dianliu > 100) {
					Message msg = new Message();
					msg.what = 0x11;
					handler.sendMessage(msg);
					send_state = false;
					return;
				}
				else if(f_pinlv<40||f_pinlv>60)
				{
					Toast.makeText(getActivity(), getText(R.string.frequency_range_40_60),Toast.LENGTH_SHORT).show();
					return;
				}
				Comm.getInstance().status_one = altek.fnTaitiShuChu(f_dianya, f_dianliu, 0, f_jiaodu, f_pinlv, 0, 0, 0,
						0, 0, 0, 0, 0);
			}
			send_state = false;
			recv_state = true;
		}
	}
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			popupwindow.dismiss();
			switch (v.getId()) {
			case R.id.tv_zhijiejieru:
				int[] arr_clock=popupwindow.getClock();
				byte[] data = altek.fnSetClock(arr_clock[0], arr_clock[1], arr_clock[2], arr_clock[3],
				arr_clock[4], arr_clock[5]);
				Comm.getInstance().status_one = data;
				break;
			case R.id.tv_100A:
				int[] arr_gpsclock=popupwindow.getGPSClock();
				byte[] gps_data = altek.fnSetClock(arr_gpsclock[0], arr_gpsclock[1], arr_gpsclock[2], arr_gpsclock[3],
				arr_gpsclock[4], arr_gpsclock[5]);
				Comm.getInstance().status_one = gps_data;
				break;
			default:
				break;
			}

		}

	};
}
