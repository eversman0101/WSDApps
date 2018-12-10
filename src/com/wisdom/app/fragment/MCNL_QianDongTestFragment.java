package com.wisdom.app.fragment;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.wisdom.app.activity.ParameterSettingActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.activityResult.QianDongResultActivity;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.Blue;
import com.wisdom.app.utils.ByteUtil;
import com.wisdom.app.utils.ChooseIDialog;
import com.wisdom.app.utils.ChooseQuanDialog;
import com.wisdom.app.utils.ChooseUDialog;
import com.wisdom.app.utils.Comm;
import com.wisdom.app.utils.DataService;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.app.utils.Utils;
import com.wisdom.bean.QianDongBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.IQiandongDao;
import com.wisdom.dao.QianDongDao;
import com.wisdom.layout.TitleLayout;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author JinJingYun 虚负荷手动校验-潜动试验
 */
public class MCNL_QianDongTestFragment extends Fragment {
	private String TAG = "MCNL_QianDongTestFragment";
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

	// 电压电流处
	@Bind(R.id.chooser_u)
	TextView tv_chooser_u;// 电压左侧

	@Bind(R.id.option_u)
	TextView option_u;// 电压设置
	@Bind(R.id.option_i)
	TextView option_i;// 电流设置

	@Bind(R.id.option_p)
	TextView option_p;//

	@Bind(R.id.option_f)
	EditText option_f;//

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

	// 上三行
	@Bind(R.id.creeping_voltage)
	Spinner sp_qiandongdianya;
	@Bind(R.id.creeping_current)
	Spinner sp_qiandongdianliu;
	@Bind(R.id.meter_constant)
	EditText et_dianbiaochangshu;
	@Bind(R.id.max_current)
	EditText et_zuidadianliu;
	@Bind(R.id.creeping_time)
	EditText et_qiandongshijian;
	@Bind(R.id.run_time)
	TextView tv_yunxingshijian;
	// 中三行
	@Bind(R.id.result1)
	TextView tv_result1;
	@Bind(R.id.result2)
	TextView tv_result2;
	@Bind(R.id.result3)
	TextView tv_result3;
	@Bind(R.id.dianya_yuan)
	TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)
	TextView tv_dianliuyuan;
	@Bind(R.id.progressbar)
	ProgressBar bar;

	private SharedPreferences sharedPreferences;
	private SharedPreferences jibenwucha;
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

	public static String maichongchangshu = "0";

	// 基础数据-顶部
	private String Qdianya = "0";
	private String Qdianliu = "0";
	private String Qchangshu = "0";
	private String Qzuidadianliu = "0";

	// 部分电表参数,校验结果
	private double Mdianya = 220;
	private double Mdianliu = 10;
	private String Mjiaodu = "0";
	private String Mpinlv = "50";

	private String dianya;
	private String dianliu;
	private String jiaodu;
	private String pinlv;

	private double calc_time;
	private String result1; //= getText(R.string.result_undone).toString();
	private String result2; //= getText(R.string.result_undone).toString();
	private String result3;//= getText(R.string.result_undone).toString();

	private IQiandongDao dao;
	private TaitiCeLiangShuJuBean bean;
	double i_meter_level = 0.5;

	QianDongBean qiandongBean = null;
	private boolean send_state = false;
	private boolean recv_state = false;
	private boolean test_state = false;
	private int yuan_state = 0;// 源状态，0关闭 1输出
	int time = 0;
	int maxtime = 0;
	int iOFF = 0;
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
							btn_yuan_shuchu.setEnabled(false);
							btn_yuan_tingzhi.setEnabled(true);
							
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
						// 上面三排
						Log.e("MCNL_QianDong", "test_state:" + test_state);
						if (test_state) {
							if (time == 0 && (Integer.valueOf(bean.getTime()) > 20)) {
								time = Integer.valueOf(bean.getTime());
								// 记录最大时间，当最大时间==设置的时间，说明进入测试
								if (maxtime < time) {
									maxtime = time;
									// Log.e("MCNL_QianDong",
									// "maxtime:"+maxtime);
								}
							}
							setBarProgress(Integer.valueOf(bean.getTime()));
							tv_yunxingshijian.setText(bean.getTime());

							if (Integer.valueOf(bean.getJieguo()) == 2 && (Integer.valueOf(bean.getTime()) == 0)
									&& (maxtime > 0)) {
								// 停源
								dianya = "0";
								dianliu = "0";							
								recv_state = false;
								send_state = true;
								test_state = false;
								fnSendState();
								MissionSingleInstance.getSingleInstance().setTestState(test_state);
								getTaitiData();

								Toast.makeText(getActivity(),getText(R.string.job_done), Toast.LENGTH_SHORT).show();
								btn_start_test.setEnabled(true);
								btn_stop_test.setEnabled(false);

								// recv_state=false;
								bar.setProgress(10000);
								time = 0;
								maxtime = 0;
								try {
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
									qiandongBean = new QianDongBean();
									qiandongBean.setDate(ByteUtil.GetNowDate());
									qiandongBean.setU(Qdianya);
									qiandongBean.setI(Qdianliu);
									qiandongBean.setChangshu(Qchangshu);
									Log.i("TAG", "潜动时间：" + calc_time);
									qiandongBean.setQiandongshijian(String.valueOf(calc_time));
									qiandongBean.setQiandongshiyan1(result1);
									qiandongBean.setQiandongshiyan2(result2);
									qiandongBean.setQiandongshiyan3(result3);
									qiandongBean.setType("0");

									// dao.add(qiandongBean);
								} catch (Exception ex) {

								}
							}

						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (msg.what == 0x002) {

			} else if (msg.what == 0x003) {
				
			} else if (msg.what == 0x10) {
				Toast.makeText(getActivity(),  getText(R.string.voltage_overload), Toast.LENGTH_SHORT).show();
			} else if (msg.what == 0x11) {
				Toast.makeText(getActivity(),  getText(R.string.current_overload), Toast.LENGTH_SHORT).show();
			} else if (msg.what == 0x12) {
				Toast.makeText(getActivity(),  getText(R.string.format_error), Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_mcnl_qiandong_test, null);
		ButterKnife.bind(this, view);
		// title=(TitleLayout)view.findViewById(R.id.title_MCNL_qiandong);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.false_actuation_test).toString()));
		
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
		sp_qiandongdianya.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Qdianya = getDoubleUn(sp_qiandongdianya.getSelectedItem().toString(), Mdianya);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});
		sp_qiandongdianliu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Qdianliu = getDoubleIQ(sp_qiandongdianliu.getSelectedItem().toString(), Mdianliu);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), QianDongResultActivity.class);
				intent.putExtra("qiandongBean", qiandongBean);
				startActivity(intent);

			}
		});
		btn_calc_time.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// double
				// changshu=Double.valueOf(et_dianbiaochangshu.getText().toString());
				// double time=((600*1000)/(changshu*Mdianya*Mdianliu*0.001));
				// calc_time=(int)time;
				// et_qiandongshijian.setText(String.format("%.1f", calc_time));
				// calc_time=20*60;
				if (i_meter_level == 0.2) {
					calc_time = (900 * 10 * 10 * 10 * 10 * 10 * 10) / (220 * Double.parseDouble(maichongchangshu)
							* Double.parseDouble(et_zuidadianliu.getText().toString()));
				} else if (i_meter_level == 2) {
					calc_time = (480 * 10 * 10 * 10 * 10 * 10 * 10) / (220 * Double.parseDouble(maichongchangshu)
							* Double.parseDouble(et_zuidadianliu.getText().toString()));

				} else {
					calc_time = (600 * 10 * 10 * 10 * 10 * 10 * 10) / (220 * Double.parseDouble(maichongchangshu)
							* Double.parseDouble(et_zuidadianliu.getText().toString()));
				}
				et_qiandongshijian.setText(String.format("%.3f", calc_time));
			}
		});
		btn_start_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveTaitiData();
				initResult();
				option_u.setText(Qdianya.toString());
				option_i.setText(Qdianliu.toString());
				recv_state = false;
				test_state = true;
				MissionSingleInstance.getSingleInstance().setTestState(test_state);
				send_state = true;
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
				// MissionSingleInstance.getSingleInstance().setTestState(test_state);

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
		btn_yuan_shuchu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveTaitiData();
				MissionSingleInstance.getSingleInstance().setYuan_state(true);
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
					// MissionSingleInstance.getSingleInstance().setTestState(test_state);
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
				// chooseUDialog.setEditUStr(sharedPreferences.getString("tv_chooser_u",
				// ""));
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
		jibenwucha = getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE);
		sharedPreferences = getActivity().getSharedPreferences("MCNL_QianDong", Context.MODE_PRIVATE); // 私有数据
		dianbiaocanshu = getActivity().getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
		Log.i(TAG, "yuan state:" + MissionSingleInstance.getSingleInstance().isYuan_state());

		option_u.setText(jibenwucha.getString("tv_chooser_u", "220.0"));
		option_i.setText(jibenwucha.getString("tv_chooser_i", "5.0"));

		if (MissionSingleInstance.getSingleInstance().isYuan_state()) {
			tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
			tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
			btn_start_test.setEnabled(false);
			btn_yuan_shuchu.setEnabled(false);
			btn_yuan_tingzhi.setEnabled(true);
		} else if (!MissionSingleInstance.getSingleInstance().isYuan_state()) {
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
		try {
			dao = new QianDongDao(getActivity());

			// tv_chooser_u.setText(getDouble(sp_U.getSelectedItem().toString(),220));
			// int length=sp_chooser_i.getSelectedItem().toString().length();
			// tv_chooser_i.setText(getDouble(sp_I.getSelectedItem().toString(),Integer.valueOf(sp_chooser_i.getSelectedItem().toString().substring(0,length-1))));

			Mdianliu = Double.valueOf(dianbiaocanshu.getString("base_current", "10"));
			Mjiaodu = MissionSingleInstance.getSingleInstance().getJiaodu();
			Mpinlv = MissionSingleInstance.getSingleInstance().getPinlv();
			// timer=new Timer();
			// timer.schedule(new SendDataTask(), 1000, 1000);
			Comm.getInstance().init(handler);
			Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0, Integer.valueOf(maichongchangshu),
					Integer.valueOf(maichongchangshu), Utils.fnGetMeterInfo(getActivity()).getMeterNumbers(),Utils.fnGetMeterInfo(getActivity()).getMeter1_no(),
					Utils.fnGetMeterInfo(getActivity()).getMeter2_no(), Utils.fnGetMeterInfo(getActivity()).getMeter3_no());
			Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
			recv_state = true;
			getTaitiData();
		} catch (Exception ex) {
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}

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
		maichongchangshu = dianbiaocanshu.getString("meter1_constant", "1600");
		String str_TU = sharedPreferences.getString("sp_qiandongdianya", "");
		String str_TI = sharedPreferences.getString("sp_qiandongdianliu", "");
		String str_Tchangshu = sharedPreferences.getString("et_dianbiaochangshu", "");
		String str_Tzuidadianliu = sharedPreferences.getString("et_zuidadianliu", "");
		String str_qiandongshijian = sharedPreferences.getString("et_qiandongshijian", "300");

		String str_OptionU = jibenwucha.getString("tv_chooser_u", "220.0");
		String str_OptionI = jibenwucha.getString("tv_chooser_i", "5.0");
		String str_OptionP = jibenwucha.getString("sp_chooser_p", "0.0");

		Mjiaodu = MissionSingleInstance.getSingleInstance().getJiaodu();
		Mpinlv = MissionSingleInstance.getSingleInstance().getPinlv();
		if (str_TU != "")
			setSpinnerSelection(sp_qiandongdianya, str_TU);
		if (str_TI != "")
			setSpinnerSelection(sp_qiandongdianliu, str_TI);

		if (str_OptionU != "")
			option_u.setText(str_OptionU);
		if (str_OptionI != "")
			option_i.setText(str_OptionI);
		if (str_OptionP != "") {
			option_p.setText(str_OptionP);
		}

		if (str_Tchangshu != "")
			et_dianbiaochangshu.setText(str_Tchangshu);
		if (str_Tzuidadianliu != "")
			et_zuidadianliu.setText(str_Tzuidadianliu);
		if (str_qiandongshijian != "")
			et_qiandongshijian.setText(str_qiandongshijian);

	}

	private void getParamettingSettings() {
		String meter_count = dianbiaocanshu.getString("meter_count", "");
		String ref_voltage = dianbiaocanshu.getString("ref_voltage", "");
		String base_current = dianbiaocanshu.getString("base_current", "");
		String max_current = dianbiaocanshu.getString("max_current", "");
		String meter1_no = dianbiaocanshu.getString("meter1_no", "");
		String meter1_constant = dianbiaocanshu.getString("meter1_constant", "");
		String meter2_no = dianbiaocanshu.getString("meter2_no", "");
		String meter2_constant = dianbiaocanshu.getString("meter2_constant", "");
		String meter3_no = dianbiaocanshu.getString("meter3_no", "");
		String meter3_constant = dianbiaocanshu.getString("meter3_constant", "");

		if (max_current != "")
			et_zuidadianliu.setText(max_current);

		if (meter1_constant != "")
			et_dianbiaochangshu.setText(meter1_constant);

	}

	private void saveTaitiData() {
		try {
			dianya = option_u.getText().toString();
			dianliu = option_i.getText().toString();
			jiaodu = option_p.getText().toString();
			pinlv = option_f.getText().toString();

			MissionSingleInstance.getSingleInstance().setU(dianya);
			MissionSingleInstance.getSingleInstance().setI(dianliu);
			MissionSingleInstance.getSingleInstance().setJiaodu(jiaodu);
			MissionSingleInstance.getSingleInstance().setPinlv(pinlv);

			Editor editor = sharedPreferences.edit();// 获取编辑器
			Editor editor1 = jibenwucha.edit();

			Qdianya = getDoubleUn(sp_qiandongdianya.getSelectedItem().toString(), Mdianya);

			Qdianliu = getDoubleIQ(sp_qiandongdianliu.getSelectedItem().toString(), Mdianliu);
			Qchangshu = et_dianbiaochangshu.getText().toString();
			Qzuidadianliu = et_zuidadianliu.getText().toString();
			String s_shijian = et_qiandongshijian.getText().toString();
			if (s_shijian.equals(""))
				calc_time = 0;
			else
				calc_time = Double.valueOf(s_shijian);
			editor.putString("sp_qiandongdianya", sp_qiandongdianya.getSelectedItem().toString());
			editor.putString("et_qiandongshijian", et_qiandongshijian.getText().toString());
			editor.putString("sp_qiandongdianliu", sp_qiandongdianliu.getSelectedItem().toString());
			editor.putString("et_dianbiaochangshu", Qchangshu);
			editor.putString("et_zuidadianliu", Qzuidadianliu);
			editor.commit();

			editor1.putString("tv_chooser_u", option_u.getText().toString());
			editor1.putString("tv_chooser_i", option_i.getText().toString());
			editor1.putString("sp_chooser_p", option_p.getText().toString());
			editor1.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * 设置Spinner默认item item 字符串，需要与spinner的item相同
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
		try {
			if (hidden) {
				saveTaitiData();
				// 不在最前端显示 相当于调用了onPause();
				Log.i(TAG, "onHidden()");
				try {

					// timer.cancel();
				} catch (Exception ex) {

				}
				return;
			} else { // 在最前端显示 相当于调用了onResume();
				Log.i(TAG, "onHiddenShow()");
				getTaitiData();
				Comm.getInstance().init(handler);
				Comm.getInstance().status_one=altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0, Integer.valueOf(maichongchangshu),
						Integer.valueOf(maichongchangshu), Utils.fnGetMeterInfo(getActivity()).getMeterNumbers(),Utils.fnGetMeterInfo(getActivity()).getMeter1_no(),
						Utils.fnGetMeterInfo(getActivity()).getMeter2_no(), Utils.fnGetMeterInfo(getActivity()).getMeter3_no());
				
				Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
				// timer=new Timer();
				// timer.schedule(new SendDataTask(), 1000, 1000);
				if (MissionSingleInstance.getSingleInstance().isYuan_state()) {
					tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
					tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
					btn_yuan_shuchu.setEnabled(false);
					btn_yuan_tingzhi.setEnabled(true);
				} else if (!MissionSingleInstance.getSingleInstance().isYuan_state()) {
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
		} catch (Exception ex) {

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		try {
			timer.cancel();
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
			Log.i(TAG, "num:" + num);
			data = df.format(num);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Log.i(TAG, "data:" + data);
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
			Log.i(TAG, "num:" + num);
			data = df.format(num);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Log.i(TAG, "data:" + data);
		return data;
	}

	private void setBarProgress(int leftSecond) {
		if (time != 0) {
			int quarter = 10000 / time;
			bar.setProgress((time - leftSecond) * quarter);
		}
	}

	private void fnSendState() {
		if (test_state)
			Comm.getInstance().status_one = altek.fnTaitiShuChu(Float.parseFloat(Qdianya), Float.parseFloat(Qdianliu),
					0, Float.parseFloat(Mjiaodu), Float.parseFloat(Mpinlv), 0, 0, 0, 1, 1, (int) (calc_time * 60), 10,
					0);
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
			Comm.getInstance().status_one = altek.fnTaitiShuChu(f_dianya, f_dianliu, 0, f_jiaodu, f_pinlv, 0, 0, 0, 0,
					0, 0, 0, 0);
		}
	}
}
