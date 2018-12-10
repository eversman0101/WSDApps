package com.wisdom.app.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import com.wisdom.app.activityResult.ZouZiResultActivity;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.Blue;
import com.wisdom.app.utils.ChooseIDialog;
import com.wisdom.app.utils.ChooseQuanDialog;
import com.wisdom.app.utils.ChooseUDialog;
import com.wisdom.app.utils.Comm;
import com.wisdom.app.utils.DataService;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.app.utils.Utils;
import com.wisdom.bean.DianbiaoZouZiBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.IZouZiDao;
import com.wisdom.dao.ZouZiDao;
import com.wisdom.layout.HzEditText;
import com.wisdom.layout.TitleLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author JinJingYun 虚负荷手动校验-常数校核
 */
public class MCNL_ChangshujiaoheFragment extends Fragment {
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

	@Bind(R.id.option_u)
	TextView option_u;// 电压设置
	@Bind(R.id.option_i)
	TextView option_i;// 电流设置

	@Bind(R.id.option_p)
	TextView option_p;//

	@Bind(R.id.option_f)
	HzEditText option_f;//

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

	// 上四行
	@Bind(R.id.zouzi_fangshi)
	Spinner sp_zouzifangshi;
	@Bind(R.id.zouzi_related_column_value)
	EditText et_yuzhidianneng;
	//@Bind(R.id.input_beilv)
	//EditText et_beilv;
	@Bind(R.id.standard_energy)
	TextView tv_biaozhundianneng;
	@Bind(R.id.tv_left_standard_energy)
	TextView tv_left_standard_energy;
	@Bind(R.id.sp_shichang)
	Spinner sp_shichang;
	
	//2018-06-19新增三个表位脉冲计数
	@Bind(R.id.pulse_count1)
	TextView tv_pulse1;
	@Bind(R.id.pulse_count2)
	TextView tv_pulse2;
	@Bind(R.id.pulse_count3)
	TextView tv_pulse3;
	
	// 上右侧
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

	@Bind(R.id.yuan_shuchu)
	Button btn_yuan_shuchu;
	@Bind(R.id.yuan_tingzhi)
	Button btn_yuan_tingzhi;

	@Bind(R.id.zouzi_related_column)
	TextView tv_zouzi_related_column;// 预置电能文本框
	@Bind(R.id.dianya_yuan)
	TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)
	TextView tv_dianliuyuan;
	@Bind(R.id.run_time)
	TextView tv_shengyushijian;
	@Bind(R.id.progressbar)
	ProgressBar bar;
	@Bind(R.id.meter_position)
	TextView tv_meter_position;
	@Bind(R.id.tv_zouziwucha)
	TextView tv_zouziwucha;
	private SharedPreferences sharedPreferences;
	private SharedPreferences jibenwucha;
	private SharedPreferences dianbiaocanshu;

	// Dialog
	private ChooseUDialog chooseUDialog;
	private ChooseIDialog chooseIDialog;
	private ChooseQuanDialog chooseQuanDialog;

	// 表位色
	private GradientDrawable mGroupDrawable;

	private Timer timer;
	private boolean send_state = false;
	private boolean recv_state = false;
	private boolean test_state = false;
	private boolean save_zouzi = false;
	// 收发数据
	private BluetoothGattCharacteristic characteristic;
	private ALTEK altek = new ALTEK();
	private DataService ds = new DataService();
	int[] iTempBuf = new int[10];
	int iTxLen = 0;
	int iStart = 0, iEnd = 0, iLen = 0, i = 0;
	String strCurTx = null;
	String str = null;

	// 基础数据
	private int iZouzifangshi;
	private int iYuzhidianneng;
	private int iBeilv;

	// 部分电表参数,校验结果
	private double Mdianya = 220;
	private double Mdianliu = 10;
	private String Mjiaodu = "0";
	private String Mpinlv = "0";

	private String dianya;
	private String dianliu;
	private String jiaodu;
	private String pinlv;

	private int calc_time;
	private String result1;
	private String result2;
	private String result3;

	private IZouZiDao dao;
	private TaitiCeLiangShuJuBean bean;
	private DianbiaoZouZiBean qiandongBean;
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
						Log.e("BlueManager", "Handler!" + bean.getU());
						if (Double.valueOf(bean.getU()) > 0 || Double.valueOf(bean.getI()) > 0) {
					 		tv_dianyayuan.setText(getActivity().getText(R.string.voltage_source_output));
							tv_dianliuyuan.setText(getActivity().getText(R.string.current_source_output));
							Log.e("BlueManager", "if1" + bean.getU());
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
							Log.e("BlueManager", "if2" + bean.getU());
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
							Log.e("BlueManager", "if3" + bean.getU());
						}
						tv_chooser_u.setText(bean.getU());
						tv_chooser_i.setText(bean.getI());
						tv_chooser_f.setText(bean.getPinlv());
						tv_show_p.setText(bean.getYougong());
						tv_show_q.setText(bean.getWugong());
						tv_show_s.setText(bean.getZonggong());
						tv_show_pf.setText(bean.getGonglvyinshu());
						tv_jiaodu.setText(bean.getJiaodu());
						Log.e("BlueManager", "tv set! U:" + bean.getU());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (msg.what == 0xAA)// 走字
			{
				try {
					DianbiaoZouZiBean zouziBean = (DianbiaoZouZiBean) msg.obj;
					if (zouziBean != null) {
						if (test_state) {
							if (time == 0 && (Integer.valueOf(zouziBean.getTime()) > 20)) {
								time = Integer.valueOf(zouziBean.getTime());
								if (maxtime < time)
									maxtime = time;
							}
							setBarProgress(Integer.valueOf(zouziBean.getTime()));
							tv_shengyushijian.setText(zouziBean.getTime());
							// 上右侧四行
							if(iZouzifangshi!=5)
								tv_biaozhundianneng.setText(zouziBean.getBiaozhundianneng());
							else
								tv_biaozhundianneng.setText(String.valueOf(zouziBean.getMaichong()));
							tv_pulse1.setText(zouziBean.getMaichong1()+"");
							tv_pulse2.setText(zouziBean.getMaichong2()+"");
							tv_pulse3.setText(zouziBean.getMaichong3()+"");
							
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
							if (Integer.valueOf(zouziBean.getJieguo()) == 1
									&& (Integer.valueOf(zouziBean.getTime()) == 0) && (maxtime > 0)) {
								// 停源
								dianya = "0";
								dianliu = "0";
								
								recv_state = false;
								send_state = true;
								test_state = false;
								getTaitiData();
								fnSendState();
								MissionSingleInstance.getSingleInstance().setTestState(test_state);
								
								btn_start_test.setEnabled(false);
								btn_stop_test.setEnabled(false);
								Toast.makeText(getActivity(), getText(R.string.job_done), Toast.LENGTH_SHORT).show();

								Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
								Comm.getInstance().status_loop2=null;
								time = 0;
								maxtime = 0;
								bar.setProgress(10000);
								qiandongBean = new DianbiaoZouZiBean();
								qiandongBean.setU(MissionSingleInstance.getSingleInstance().getU());
								qiandongBean.setI(MissionSingleInstance.getSingleInstance().getI());
								qiandongBean.setJiaodu(MissionSingleInstance.getSingleInstance().getJiaodu());
								qiandongBean.setYougong(bean.getYougong());
								qiandongBean.setWugong(bean.getWugong());
								qiandongBean.setGonglvyinshu(bean.getGonglvyinshu());
								qiandongBean.setZouzifangshi(sp_zouzifangshi.getSelectedItem().toString());
								qiandongBean.setYuzhidianneng(et_yuzhidianneng.getText().toString());
								qiandongBean.setBeilv("1");

								qiandongBean.setBiaozhundianneng(tv_biaozhundianneng.getText().toString());
								qiandongBean.setQishi1(tv_qishi1.getText().toString());
								qiandongBean.setJieshu1(tv_jieshu1.getText().toString());
								qiandongBean.setShiji1(tv_shiji1.getText().toString());
								qiandongBean.setWucha1(tv_zouzi1.getText().toString());
								int meter_numbers=Utils.fnGetMeterInfo(getActivity()).getMeterNumbers();
								if(meter_numbers>=2)
								{
								qiandongBean.setQishi2(tv_qishi2.getText().toString());
								qiandongBean.setJieshu2(tv_jieshu2.getText().toString());
								qiandongBean.setShiji2(tv_shiji2.getText().toString());
								qiandongBean.setWucha2(tv_zouzi2.getText().toString());
									if(meter_numbers==3)
									{
										qiandongBean.setQishi3(tv_qishi3.getText().toString());
										qiandongBean.setJieshu3(tv_jieshu3.getText().toString());
										qiandongBean.setShiji3(tv_shiji3.getText().toString());
										qiandongBean.setWucha3(tv_zouzi3.getText().toString());
										
									}
								}
								qiandongBean.setType("3");
								// dao.add(qiandongBean);

							}
						}

					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (msg.what == 0x003) {

			} else if (msg.what == 0x10) {
				Toast.makeText(getActivity(), getText(R.string.voltage_overload), Toast.LENGTH_SHORT).show();
			} else if (msg.what == 0x11) {
				Toast.makeText(getActivity(), getText(R.string.current_overload), Toast.LENGTH_SHORT).show();
			} else if (msg.what == 0x12) {
				Toast.makeText(getActivity(), getText(R.string.format_error), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mcnl_changshujiaohe, null);
		ButterKnife.bind(this, view);
		title = (TitleLayout) view.findViewById(R.id.title_MCNL_zouzi);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.constant_check).toString()));
		
		initView();
		initData();
		btn_meter1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv_pulse1.setVisibility(View.VISIBLE);
				tv_qishi1.setVisibility(View.VISIBLE);
				tv_jieshu1.setVisibility(View.VISIBLE);
				tv_shiji1.setVisibility(View.VISIBLE);
				tv_zouzi1.setVisibility(View.VISIBLE);

				tv_pulse2.setVisibility(View.GONE);
				tv_qishi2.setVisibility(View.GONE);
				tv_jieshu2.setVisibility(View.GONE);
				tv_shiji2.setVisibility(View.GONE);
				tv_zouzi2.setVisibility(View.GONE);

				tv_pulse3.setVisibility(View.GONE);
				tv_qishi3.setVisibility(View.GONE);
				tv_jieshu3.setVisibility(View.GONE);
				tv_shiji3.setVisibility(View.GONE);
				tv_zouzi3.setVisibility(View.GONE);
				tv_meter_position.setText(getText(R.string.station_1));
				// tv_meter_position.setBackgroundColor(Color.parseColor("#ffebeadc"));
				mGroupDrawable = (GradientDrawable) tv_meter_position.getBackground();
				/* 设置边框颜色和宽度 */
				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));
				/* 设置整体背景颜色 */
				mGroupDrawable.setColor(getResources().getColor(R.color.moren1));
				
				mGroupDrawable = (GradientDrawable) tv_pulse1.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren1));

				mGroupDrawable = (GradientDrawable) tv_qishi1.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren1));
				mGroupDrawable = (GradientDrawable) tv_jieshu1.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren1));
				mGroupDrawable = (GradientDrawable) tv_shiji1.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren1));
				mGroupDrawable = (GradientDrawable) tv_zouzi1.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren1));

				mGroupDrawable = (GradientDrawable) tv_shengyushijian.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren1));

				mGroupDrawable = (GradientDrawable) tv_biaozhundianneng.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren1));
			}
		});
		btn_meter2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv_pulse1.setVisibility(View.GONE);
				tv_qishi1.setVisibility(View.GONE);
				tv_jieshu1.setVisibility(View.GONE);
				tv_shiji1.setVisibility(View.GONE);
				tv_zouzi1.setVisibility(View.GONE);

				tv_pulse2.setVisibility(View.VISIBLE);
				tv_qishi2.setVisibility(View.VISIBLE);
				tv_jieshu2.setVisibility(View.VISIBLE);
				tv_shiji2.setVisibility(View.VISIBLE);
				tv_zouzi2.setVisibility(View.VISIBLE);

				tv_pulse3.setVisibility(View.GONE);
				tv_qishi3.setVisibility(View.GONE);
				tv_jieshu3.setVisibility(View.GONE);
				tv_shiji3.setVisibility(View.GONE);
				tv_zouzi3.setVisibility(View.GONE);
				tv_meter_position.setText(getText(R.string.station_2));
				mGroupDrawable = (GradientDrawable) tv_meter_position.getBackground();
				/* 设置边框颜色和宽度 */
				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));
				/* 设置整体背景颜色 */
				mGroupDrawable.setColor(getResources().getColor(R.color.moren2));

				mGroupDrawable = (GradientDrawable) tv_pulse2.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren2));
				
				mGroupDrawable = (GradientDrawable) tv_qishi2.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren2));

				mGroupDrawable = (GradientDrawable) tv_jieshu2.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren2));

				mGroupDrawable = (GradientDrawable) tv_shiji2.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren2));

				mGroupDrawable = (GradientDrawable) tv_zouzi2.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren2));

				mGroupDrawable = (GradientDrawable) tv_shengyushijian.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren2));

				mGroupDrawable = (GradientDrawable) tv_biaozhundianneng.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren2));
			}
		});
		btn_meter3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv_pulse1.setVisibility(View.GONE);
				tv_qishi1.setVisibility(View.GONE);
				tv_jieshu1.setVisibility(View.GONE);
				tv_shiji1.setVisibility(View.GONE);
				tv_zouzi1.setVisibility(View.GONE);

				tv_pulse2.setVisibility(View.GONE);
				tv_qishi2.setVisibility(View.GONE);
				tv_jieshu2.setVisibility(View.GONE);
				tv_shiji2.setVisibility(View.GONE);
				tv_zouzi2.setVisibility(View.GONE);

				tv_pulse3.setVisibility(View.VISIBLE);
				tv_qishi3.setVisibility(View.VISIBLE);
				tv_qishi3.setVisibility(View.VISIBLE);
				tv_jieshu3.setVisibility(View.VISIBLE);
				tv_shiji3.setVisibility(View.VISIBLE);
				tv_zouzi3.setVisibility(View.VISIBLE);
				tv_meter_position.setText(getText(R.string.station_3));
				mGroupDrawable = (GradientDrawable) tv_meter_position.getBackground();
				/* 设置边框颜色和宽度 */
				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));
				/* 设置整体背景颜色 */
				mGroupDrawable.setColor(getResources().getColor(R.color.moren3));

				mGroupDrawable = (GradientDrawable) tv_pulse3.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren3));
				
				mGroupDrawable = (GradientDrawable) tv_qishi3.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren3));

				mGroupDrawable = (GradientDrawable) tv_jieshu3.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren3));

				mGroupDrawable = (GradientDrawable) tv_shiji3.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren3));

				mGroupDrawable = (GradientDrawable) tv_zouzi3.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren3));

				mGroupDrawable = (GradientDrawable) tv_shengyushijian.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren3));

				mGroupDrawable = (GradientDrawable) tv_biaozhundianneng.getBackground();

				mGroupDrawable.setStroke(1, getResources().getColor(R.color.contents_text));

				mGroupDrawable.setColor(getResources().getColor(R.color.moren3));
			}
		});
		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ZouZiResultActivity.class);
				intent.putExtra("zouzibean", qiandongBean);
				startActivity(intent);

			}
		});

		btn_start_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveTaitiData();
				recv_state = false;
				send_state = true;
				test_state = true;
				MissionSingleInstance.getSingleInstance().setTestState(test_state);
				bar.setProgress(0);
				Toast.makeText(getActivity(), getText(R.string.start_testing), Toast.LENGTH_SHORT).show();
				btn_start_test.setEnabled(false);
				btn_stop_test.setEnabled(true);
				fnSendState();
			}
		});
		btn_stop_test.setOnClickListener(new View.OnClickListener() {

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
					Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
					Comm.getInstance().status_loop2=null;
					fnSendState();
				} catch (Exception ex) {

				}
			}
		});
		sp_zouzifangshi.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				iZouzifangshi = sp_zouzifangshi.getSelectedItemPosition();
				if (iZouzifangshi == 0) {
					tv_zouzi_related_column.setVisibility(View.VISIBLE);
					sp_shichang.setVisibility(View.GONE);
					tv_zouzi_related_column.setText(getText(R.string.preset_electric_energy));
					tv_left_standard_energy.setText(getText(R.string.standard_electric_energy));
				} else if (iZouzifangshi == 1) {
					tv_zouzi_related_column.setVisibility(View.GONE);
					sp_shichang.setVisibility(View.VISIBLE);
					tv_left_standard_energy.setText(getText(R.string.standard_electric_energy));
				} else if (iZouzifangshi == 2) {
					tv_zouzi_related_column.setVisibility(View.VISIBLE);
					sp_shichang.setVisibility(View.GONE);

					tv_zouzi_related_column.setText(getText(R.string.pulse_shu));
					tv_left_standard_energy.setText(getText(R.string.pulse_geshu));
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

	private void initTopView() {
		tv_shengyushijian.setText("");
		// 上右侧四行
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
		MissionSingleInstance.getSingleInstance().setFuhe_state(3);
		jibenwucha = getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE);
		sharedPreferences = getActivity().getSharedPreferences("MCNL_ZouZi", Context.MODE_PRIVATE); // 私有数据
		dianbiaocanshu = getActivity().getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);

		option_u.setText(jibenwucha.getString("tv_chooser_u", "220.0"));
		option_i.setText(jibenwucha.getString("tv_chooser_i", "5.0"));
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
		tv_zouziwucha.setText(getText(R.string.pulse_constants));
		
	}

	private void initData() {
		try {
			dao = new ZouZiDao(getActivity());

			
			Comm.getInstance().init(handler);
			Comm.getInstance().status_one = altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0,
					Integer.valueOf(Utils.fnGetMeterInfo(getActivity()).getMaichongchangshu()),
					Integer.valueOf(Utils.fnGetMeterInfo(getActivity()).getMaichongchangshu()),
					Utils.fnGetMeterInfo(getActivity()).getMeterNumbers(),
					Utils.fnGetMeterInfo(getActivity()).getMeter1_no(),
					Utils.fnGetMeterInfo(getActivity()).getMeter2_no(),
					Utils.fnGetMeterInfo(getActivity()).getMeter3_no());

			Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
			recv_state = true;
			getTaitiData();
		} catch (Exception ex) {
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}

	private void getTaitiData() {
		String str_zouzifangshi = sharedPreferences.getString("sp_zouzifangshi", getText(R.string.start_stop_power).toString());
		String str_yuzhidianneng = sharedPreferences.getString("et_yuzhidianneng", "1200");
		String str_tv_zouzi_related_column = sharedPreferences.getString("tv_zouzi_related_column", "");
		String str_beilv = sharedPreferences.getString("et_beilv", "1");
		String str_OptionU = jibenwucha.getString("tv_chooser_u", "220.0");
		String str_OptionI = jibenwucha.getString("tv_chooser_i", "5.0");
		String str_OptionP = jibenwucha.getString("sp_chooser_p", "0.0");
		
		String str_shichang=sharedPreferences.getString("sp_shichang",getText(R.string.duration_1).toString());
		if(str_shichang!="")
			setSpinnerSelection(sp_shichang,str_shichang);
		
		if (str_OptionU != "")
			option_u.setText(str_OptionU);
		if (str_OptionI != "")
			option_i.setText(str_OptionI);
		if (str_OptionP != "") {
			option_p.setText(str_OptionP);
		}
		if (str_zouzifangshi != "")
			setSpinnerSelection(sp_zouzifangshi, str_zouzifangshi);

		if (str_yuzhidianneng != "")
			et_yuzhidianneng.setText(str_yuzhidianneng);
		if (str_tv_zouzi_related_column != "")
			tv_zouzi_related_column.setText(str_tv_zouzi_related_column);
		//if (str_beilv != "")
			//et_beilv.setText(str_beilv);
		
		iZouzifangshi = sp_zouzifangshi.getSelectedItemPosition();
		if(iZouzifangshi==1)
		{
			tv_zouzi_related_column.setVisibility(View.GONE);
			sp_shichang.setVisibility(View.VISIBLE);
		}
		if(iZouzifangshi==2)
			tv_left_standard_energy.setText(getText(R.string.pulse_geshu));
		
		int meter_numbers=Utils.fnGetMeterInfo(getActivity()).getMeterNumbers();
		Log.e(TAG,"meter_numbers:"+meter_numbers);
		if(meter_numbers==2)
		{
			btn_meter2.setEnabled(true);
			btn_meter3.setEnabled(false);
		}
		if(meter_numbers==1)
		{
			btn_meter2.setEnabled(false);
			btn_meter3.setEnabled(false);
		}
		if(meter_numbers==3)
		{
			btn_meter2.setEnabled(true);
			btn_meter3.setEnabled(true);
		}
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
			Editor editor1 = jibenwucha.edit();
			Editor editor = sharedPreferences.edit();// 获取编辑器
			String zouzifangshi = sp_zouzifangshi.getSelectedItem().toString();

			// iZouzifangshi=Integer.valueOf(zouzifangshi);
			String yuzhidianneng = et_yuzhidianneng.getText().toString();
			
			iYuzhidianneng = (int) (Double.valueOf(yuzhidianneng) * 100);
			if(iZouzifangshi==1)
			{
				int time_type=sp_shichang.getSelectedItemPosition();
				if(time_type==0)
					iYuzhidianneng=iYuzhidianneng*60;
				if(time_type==2)
					iYuzhidianneng=iYuzhidianneng*60*60;
			}	
			String beilv = "1";
			iBeilv = Integer.valueOf(beilv);

			String tv_yuzhidianneng = tv_zouzi_related_column.getText().toString();
			editor.putString("sp_shichang",sp_shichang.getSelectedItem().toString());
			editor.putString("sp_zouzifangshi", zouzifangshi);
			editor.putString("et_yuzhidianneng", yuzhidianneng);
			editor.putString("et_beilv", beilv);
			editor.putString("tv_zouzi_related_column", tv_yuzhidianneng);
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

	/**
	 * 获取走字方式的int格式
	 * 
	 * @return 0起止电量 1走字定时 2脉冲计数
	 */
	private int getZouZiInt(String str) {
		return 1;
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
	 * displayData(strRx); //接收报文区域//接收处理 iRxDataLen = strRx.length();
	 * 
	 * byte[] data=ds.fnReceiveData(strRx, iRxDataLen); if(data!=null) {
	 * if((data[2]&0x7f)==0x61)//实时台体测量数据 { TaitiCeLiangShuJuBean
	 * bean=ds.fnGetTaitiData(data); if(bean!=null) { Message msg = new
	 * Message(); msg.what = 0x001; msg.obj = bean; handler.sendMessage(msg); }
	 * } if((data[2]&0x7f)==0x6B)//电表走字测试 { DianbiaoZouZiBean
	 * bean=ds.fnGetZouZiData(data); if(bean!=null) { Message msg = new
	 * Message(); msg.what = 0x002; msg.obj = bean; handler.sendMessage(msg); }
	 * } } } } };
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			// 不在最前端显示 相当于调用了onPause();
			Log.i(TAG, "onHidden()");
			try {
				saveTaitiData();
				MissionSingleInstance.getSingleInstance().setFuhe_state(1);
			} catch (Exception ex) {

			}
			return;
		} else { // 在最前端显示 相当于调用了onResume();
			Log.i(TAG, "onHiddenShow()");
			MissionSingleInstance.getSingleInstance().setFuhe_state(3);
			getTaitiData();
			Comm.getInstance().init(handler);
			Comm.getInstance().status_one = altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0,
					Integer.valueOf(Utils.fnGetMeterInfo(getActivity()).getMaichongchangshu()),
					Integer.valueOf(Utils.fnGetMeterInfo(getActivity()).getMaichongchangshu()),
					Utils.fnGetMeterInfo(getActivity()).getMeterNumbers(),
					Utils.fnGetMeterInfo(getActivity()).getMeter1_no(),
					Utils.fnGetMeterInfo(getActivity()).getMeter2_no(),
					Utils.fnGetMeterInfo(getActivity()).getMeter3_no());

			Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
			// timer = new Timer();
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
		saveTaitiData();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume()");
		getTaitiData();
	}

	int x = 0;

	private void setBarProgress(int leftSecond) {
		if (time != 0) {
			int quarter = 10000 / time;
			bar.setProgress((time - leftSecond) * quarter);
		}
	}

	private void fnSendState() {
		if (test_state) {
			if(iZouzifangshi==0)
				iZouzifangshi=3;
			else if(iZouzifangshi==1)
				iZouzifangshi=4;
			else if(iZouzifangshi==2)
				iZouzifangshi=5;
			Comm.getInstance().status_one = altek.fnDianbiaoZouZi(0, iZouzifangshi, iYuzhidianneng, iBeilv);
			Log.e("UF","yuzhidiannneg:"+iYuzhidianneng);
			Comm.getInstance().status_loop1 = altek.fnGetFrameByFunctionCode((byte) 0x61);
			Comm.getInstance().status_loop2 = altek.fnDianbiaoZouZi(1, iZouzifangshi, iYuzhidianneng, iBeilv);
			Log.e("UF","zouzi set!");
		} else {

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
				Toast.makeText(getActivity(), getText(R.string.frequency_range_40_60).toString(),Toast.LENGTH_SHORT).show();
				return;
			}
			Comm.getInstance().status_one = altek.fnTaitiShuChu(f_dianya, f_dianliu, 0, f_jiaodu, f_pinlv, 0, 0, 0, 0,
					0, 0, 0, 0);
			
		}
		send_state = false;
		recv_state = true;
	}
}
