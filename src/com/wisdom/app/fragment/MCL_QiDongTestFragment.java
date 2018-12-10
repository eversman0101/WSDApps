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
import com.wisdom.app.activityResult.QiDongResultActivity;
import com.wisdom.app.utils.*;
import com.wisdom.bean.QiDongBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.IQidongDao;
import com.wisdom.dao.QiDongDao;
import com.wisdom.layout.ITypePopupWindow;
import com.wisdom.layout.TitleLayout;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author JinJingYun �鸺���ֶ�У��-������
 */
public class MCL_QiDongTestFragment extends Fragment {
	private String TAG = "MCNL_QiDongTestFragment";
	@Bind(R.id.title_MCNL_qidong)
	TitleLayout title;

	@Bind(R.id.calc_time)
	Button btn_calc_time;
	@Bind(R.id.dianbiao_canshu)
	Button btn_dianbiao_canshu;
	@Bind(R.id.jiaoyan_jieguo)
	Button btn_jiaoyan_jieguo;
	@Bind(R.id.start_test)
	Button btn_start_test;// ����У��
	@Bind(R.id.stop_test)
	Button btn_stop_test;// ֹͣУ��

	// ��ѹ������
	@Bind(R.id.chooser_u)
	TextView tv_chooser_u;// ��ѹ���

	@Bind(R.id.chooser_i)
	TextView tv_chooser_i;// �������

	@Bind(R.id.chooser_p)
	TextView tv_jiaodu;// ���
	@Bind(R.id.chooser_f)
	TextView tv_chooser_f;// Ƶ��

	@Bind(R.id.show_p)
	TextView tv_show_p;// A���й�
	@Bind(R.id.show_q)
	TextView tv_show_q;// A���޹�
	@Bind(R.id.show_s)
	TextView tv_show_s;// �ܹ�
	@Bind(R.id.show_pf)
	TextView tv_show_pf;// ��������
	@Bind(R.id.dianya_yuan)
	TextView tv_dianyayuan;
	@Bind(R.id.dianliu_yuan)
	TextView tv_dianliuyuan;
	// ������
	@Bind(R.id.meter_constant)
	EditText et_dianbiaochangshu;
	@Bind(R.id.base_current)
	EditText et_jibendianliu;
	@Bind(R.id.start_current)
	Spinner sp_qidongdianliu;

	@Bind(R.id.start_time)
	TextView tv_qidongshijian;
	@Bind(R.id.run_time)
	TextView tv_shengyushijian;
	// ������
	@Bind(R.id.result1)
	TextView tv_result1;
	@Bind(R.id.result2)
	TextView tv_result2;
	@Bind(R.id.result3)
	TextView tv_result3;
	@Bind(R.id.btn_select_i_type)
	Button btn_select_i_type;
	@Bind(R.id.progressbar)
	ProgressBar bar;
	int time = 0;// ÿһ��У�����ʱ��
	int iOFF = 0;// �����ж�Դ�Ƿ����ֹͣ
	private ITypePopupWindow popupwindow;
	private SharedPreferences sharedPreferences;
	private SharedPreferences jibenwucha;
	private SharedPreferences dianbiaocanshu;

	// �շ�����
	private BluetoothGattCharacteristic characteristic;
	private ALTEK altek = new ALTEK();
	private DataService ds = new DataService();
	int[] iTempBuf = new int[10];
	int iTxLen = 0;
	int iStart = 0, iEnd = 0, iLen = 0, i = 0;
	String strCurTx = null;
	String str = null;
	private Timer timer;

	// ��������-����
	private String Qdianya = "0";
	private String Qdianliu = "0";
	private String Qchangshu = "0";
	private String Qjibendianliu = "0";

	// ���ֵ�����,У����
	private double Mdianya = 220;
	private double Mdianliu = 10;
	private String Mjiaodu = "0";
	private String Mpinlv = "50";

	private int calc_time;
	private String result1;
	private String result2;
	private String result3;

	private IQidongDao dao;
	private TaitiCeLiangShuJuBean bean;

	private boolean send_state = false;
	private boolean recv_state = false;
	private boolean test_state = false;
	private int meter_count = 1;
	int iType = 0;// ����������ʽ,-1Ϊ����
	int maxtime = 0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x001) {
				try {
					bean = (TaitiCeLiangShuJuBean) msg.obj;
					if (bean != null) {
						if (Double.valueOf(bean.getU()) > 0) {
							tv_dianyayuan.setText("��ѹԴ:�����");

						}
						if (Double.valueOf(bean.getI()) > 0) {
							tv_dianliuyuan.setText("����Դ:�����");
						}
						if ((Double.valueOf(bean.getU()) == 0) && (Double.valueOf(bean.getI()) == 0)) {
							iOFF++;
							if (iOFF == 3)// �϶�Դ��ֹͣ
							{

								tv_dianyayuan.setText("��ѹԴ:��ֹͣ");
								tv_dianliuyuan.setText("����Դ:��ֹͣ");
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
						// ��������
						tv_shengyushijian.setText(bean.getTime());
						// �жϺϸ�ı�׼�����޸�
						if (test_state) {
							if (time == 0 && (Integer.valueOf(bean.getTime()) > 20)) {
								time = Integer.valueOf(bean.getTime());
								if (maxtime < time)
									maxtime = time;
							}
							setBarProgress(Integer.valueOf(bean.getTime()));
							tv_shengyushijian.setText(bean.getTime());

							if (Integer.valueOf(bean.getJieguo()) == 2) {
								Toast.makeText(getActivity(), "�������", Toast.LENGTH_SHORT).show();
								btn_start_test.setEnabled(true);
								test_state = false;
								// recv_state=false;
								bar.setProgress(100);
								time = 0;
								try {
									if (Integer.valueOf(bean.getJieguo()) == 2
											&& Double.valueOf(bean.getWucha1()).intValue() == 0) {
										result1 = "�ϸ�";
										tv_result1.setText(result1);
									} else if (Integer.valueOf(bean.getJieguo()) == 2
											&& Double.valueOf(bean.getWucha1()).intValue() == 255) {
										result1 = "���ϸ�";
										tv_result1.setText(result1);
									}
									if (Integer.valueOf(bean.getJieguo()) == 2
											&& (Double.valueOf(bean.getWucha2()).intValue() == 0) && meter_count == 2) {
										result2 = "�ϸ�";
										tv_result2.setText(result2);
									} else if (Integer.valueOf(bean.getJieguo()) == 2
											&& (Double.valueOf(bean.getWucha2()).intValue() == 255)
											&& meter_count == 2) {
										result2 = "���ϸ�";
										tv_result2.setText(result2);
									}
									if (Integer.valueOf(bean.getJieguo()) == 2
											&& (Double.valueOf(bean.getWucha3()).intValue() == 0) && meter_count == 3) {
										result3 = "�ϸ�";
										tv_result3.setText(result3);
									}

									else if (Integer.valueOf(bean.getJieguo()) == 2
											&& (Double.valueOf(bean.getWucha3()).intValue() == 255)
											&& meter_count == 3) {
										result3 = "���ϸ�";
										tv_result3.setText(result3);
									}
									QiDongBean qidongBean = new QiDongBean();
									qidongBean.setDate(ByteUtil.GetNowDate());
									qidongBean.setU(String.valueOf(Mdianya));
									qidongBean.setI(String.valueOf(Mdianliu));
									qidongBean.setChangshu(Qchangshu);
									qidongBean.setQidongshijian(String.valueOf(calc_time));
									qidongBean.setQidongshiyan1(result1);
									qidongBean.setQidongshiyan2(result2);
									qidongBean.setQidongshiyan3(result3);
									dao.add(qidongBean);
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
				if (ManualCheckLoadActivity.METHOD == 1)
					Toast.makeText(getActivity(), "ֱ�ӽ���ģʽ�Ѵ�", Toast.LENGTH_SHORT).show();
				else if (ManualCheckLoadActivity.METHOD == 3)
					Toast.makeText(getActivity(), "100A����ǯģʽ�Ѵ�", Toast.LENGTH_SHORT).show();
				else if (ManualCheckLoadActivity.METHOD == 7)
					Toast.makeText(getActivity(), "5A����ǯģʽ�Ѵ�", Toast.LENGTH_SHORT).show();

			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mcl_qidong_test, null);
		ButterKnife.bind(this, view);
		title = (TitleLayout) view.findViewById(R.id.title_MCNL_qidong);
		title.setTitleText("ʵ�����ֶ�У��->������");
		Log.i(TAG, "��������" + MissionSingleInstance.getSingleInstance().getMeterCount());
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
		sp_qidongdianliu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Mdianliu = Integer.valueOf(et_jibendianliu.getText().toString());
				Qdianliu = getDoubleUn(sp_qidongdianliu.getSelectedItem().toString(), Mdianliu);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), QiDongResultActivity.class);
				startActivity(intent);

			}
		});
		btn_calc_time.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				double changshu = Double.valueOf(et_dianbiaochangshu.getText().toString());
				double time = ((600 * 1000) / (changshu * Mdianya * Mdianliu * 0.001));
				calc_time = (int) (time * 1.5);
				tv_qidongshijian.setText(String.format("%.1f", time));
			}
		});
		btn_start_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveTaitiData();
				recv_state = false;
				test_state = true;
				send_state = true;
				bar.setProgress(0);
				Toast.makeText(getActivity(), "���Կ�ʼ", Toast.LENGTH_SHORT).show();
				btn_start_test.setEnabled(false);
			}
		});
		btn_stop_test.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				test_state = false;
			}
		});
		btn_select_i_type.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupwindow = new ITypePopupWindow(getActivity(), itemsOnClick);
				// ��ʾ����
				popupwindow.showAtLocation(getActivity().findViewById(R.id.mcl_layout),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		return view;
	}

	private void initView() {
		jibenwucha = getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE);
		sharedPreferences = getActivity().getSharedPreferences("MCNL_QiDong", Context.MODE_PRIVATE); // ˽������
		dianbiaocanshu = getActivity().getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
		if (MissionSingleInstance.getSingleInstance().isYuan_state()) {
			tv_dianyayuan.setText("��ѹԴ:�����");
			tv_dianliuyuan.setText("����Դ:�����");

		} else if (!MissionSingleInstance.getSingleInstance().isYuan_state()) {
			tv_dianyayuan.setText("��ѹԴ:��ֹͣ");
			tv_dianliuyuan.setText("����Դ:��ֹͣ");

		}
	}

	private void initData() {
		try {
			dao = new QiDongDao(getActivity());
			meter_count = ByteUtil.getMeterCount(getActivity());
			// tv_chooser_u.setText(getDouble(sp_U.getSelectedItem().toString(),220));
			// int length=sp_chooser_i.getSelectedItem().toString().length();
			// tv_chooser_i.setText(getDouble(sp_I.getSelectedItem().toString(),Integer.valueOf(sp_chooser_i.getSelectedItem().toString().substring(0,length-1))));
			/*
			 * BluetoothGattService service =
			 * MainActivity.mBluetoothLeService.getGattService(BLEActivity.
			 * SERVICE_UUID); characteristic =
			 * service.getCharacteristic(UUID.fromString(BLEActivity.
			 * HEART_RATE_MEASUREMENT));
			 * MainActivity.mBluetoothLeService.setCharacteristicNotification(
			 * characteristic);
			 * getActivity().registerReceiver(mGattUpdateReceiver,
			 * MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
			 */
			Mjiaodu = MissionSingleInstance.getSingleInstance().getJiaodu();
			Mpinlv = MissionSingleInstance.getSingleInstance().getPinlv();
			timer = new Timer();
			timer.schedule(new SendDataTask(), 1000, 1000);
			recv_state = true;
			getTaitiData();
		} catch (Exception ex) {
			Toast.makeText(getActivity(), "�������������豸", Toast.LENGTH_SHORT).show();
		}
	}

	private void getTaitiData() {
		String str_TU = sharedPreferences.getString("sp_qidongdianliu", "");

		String str_Tchangshu = sharedPreferences.getString("et_dianbiaochangshu", "");
		String str_Tzuidadianliu = sharedPreferences.getString("et_jibendianliu", "");

		Mjiaodu = MissionSingleInstance.getSingleInstance().getJiaodu();
		Mpinlv = MissionSingleInstance.getSingleInstance().getPinlv();
		if (str_TU != "")
			setSpinnerSelection(sp_qidongdianliu, str_TU);

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

		if (base_current != "")
			et_jibendianliu.setText(base_current);

		if (meter1_constant != "")
			et_dianbiaochangshu.setText(meter1_constant);

	}

	private void saveTaitiData() {
		try {
			Editor editor = sharedPreferences.edit();// ��ȡ�༭��
			Mdianliu = Integer.valueOf(et_jibendianliu.getText().toString());
			Qdianliu = getDoubleUn(sp_qidongdianliu.getSelectedItem().toString(), Mdianliu);

			Qchangshu = et_dianbiaochangshu.getText().toString();
			Qjibendianliu = et_jibendianliu.getText().toString();
			String s_shijian = tv_qidongshijian.getText().toString();
			if (s_shijian.equals(""))
				calc_time = 0;
			else
				calc_time = Double.valueOf(s_shijian).intValue();
			// editor.putString("sp_qiandongdianya", Qdianya);

			editor.putString("sp_qidongdianliu", sp_qidongdianliu.getSelectedItem().toString());
			editor.putString("et_dianbiaochangshu", Qchangshu);

			editor.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			// getActivity().unregisterReceiver(mGattUpdateReceiver);
			timer.cancel();
		} catch (Exception ex) {

		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			// ������ǰ����ʾ �൱�ڵ�����onPause();
			Log.i(TAG, "onHidden()");
			try {
				// getActivity().unregisterReceiver(mGattUpdateReceiver);
				timer.cancel();
			} catch (Exception ex) {

			}
			return;
		} else { // ����ǰ����ʾ �൱�ڵ�����onResume();
			Log.i(TAG, "onHiddenShow()");
			// getActivity().registerReceiver(mGattUpdateReceiver,
			// MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
			timer = new Timer();
			timer.schedule(new SendDataTask(), 1000, 1000);
			if (MissionSingleInstance.getSingleInstance().isYuan_state()) {
				tv_dianyayuan.setText("��ѹԴ:�����");
				tv_dianliuyuan.setText("����Դ:�����");

			} else if (!MissionSingleInstance.getSingleInstance().isYuan_state()) {
				tv_dianyayuan.setText("��ѹԴ:��ֹͣ");
				tv_dianliuyuan.setText("����Դ:��ֹͣ");

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
	 * �ٷֺ�Ib�ַ�����int�����������λС��
	 */
	private String getDoubleUn(String str, double mdianya2) {
		DecimalFormat df = new DecimalFormat("######0.000");
		String data = "";
		double num = 0;

		try {
			num = Double.parseDouble(str.replace("%Ib", "")) * 0.01 * mdianya2;
			Log.i(TAG, "num:" + num);
			data = df.format(num);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Log.i(TAG, "data:" + data);
		return data;
	}

	/*
	 * ����SpinnerĬ��item item �ַ�������Ҫ��spinner��item��ͬ
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
	 * str.length(); iStart = 0; iEnd = 0; // һ����෢��20�ֽڣ��ֿ����� for (i = iStart; i
	 * < iLen;) { if (iLen < (i + 40)) { strCurTx = str.substring(i, iLen); }
	 * else { strCurTx = str.substring(i, i + 40); } if (characteristic != null)
	 * { characteristic.setValue(ByteUtil.hexStringToByte(strCurTx));
	 * MainActivity.mBluetoothLeService.writeCharacteristic(characteristic); } i
	 * += 40; try { Thread.sleep(20); } catch (InterruptedException e) {
	 * e.printStackTrace(); } if (i > iLen) break; else continue; } }
	 */
	/**
	 * �������Ĺ㲥������
	 */
	/*
	 * private final BroadcastReceiver mGattUpdateReceiver = new
	 * BroadcastReceiver() { String strRx = null, str2 = null; int iRxDataLen =
	 * 0;
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { final
	 * String action = intent.getAction(); // ���ӳɹ����½��涥������ if
	 * (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))// Gatt���ӳɹ� {
	 * 
	 * } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))//
	 * Gatt����ʧ�� {
	 * 
	 * } else if
	 * (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))//
	 * ����GATT������ {
	 * 
	 * } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))//
	 * ��Ч���� { // �����͹��������� strRx =
	 * intent.getExtras().getString(BluetoothLeService.EXTRA_DATA); //
	 * displayData(strRx); //���ձ�������//���մ��� iRxDataLen = strRx.length(); byte[]
	 * data = ds.fnReceiveData(strRx, iRxDataLen); if (data != null) { if
	 * ((data[2] & 0x7f) == 0x61)// ʵʱ̨��������� { TaitiCeLiangShuJuBean bean =
	 * ds.fnGetTaitiData(data); if (bean != null) { Message msg = new Message();
	 * msg.what = 0x001; msg.obj = bean; handler.sendMessage(msg); } } if
	 * ((data[2] & 0x7f) == 0x5E)// ̨������������ò��� { if
	 * (ManualCheckLoadActivity.METHOD == 1) Toast.makeText(getActivity(),
	 * "ֱ�ӽ���ģʽ�Ѵ�", Toast.LENGTH_SHORT).show(); else if
	 * (ManualCheckLoadActivity.METHOD == 3) Toast.makeText(getActivity(),
	 * "100A����ǯģʽ�Ѵ�", Toast.LENGTH_SHORT).show(); else if
	 * (ManualCheckLoadActivity.METHOD == 7) Toast.makeText(getActivity(),
	 * "5A����ǯģʽ�Ѵ�", Toast.LENGTH_SHORT).show(); } } } } };
	 */
	private void setBarProgress(int leftSecond) {
		if (time != 0) {
			int quarter = 10000 / time;
			bar.setProgress((time - leftSecond) * quarter);
		}
	}

	private class SendDataTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (iType == -1) {
				Blue.send(altek.fnTaitiCeShiLeiXingPeiZhiCanshu(ManualCheckLoadActivity.METHOD, 0,
						Integer.valueOf(MCL_JiBenWuChaFragment.maichongchangshu),
						Integer.valueOf(MCL_JiBenWuChaFragment.maichongchangshu), ByteUtil.getMeterCount(getActivity()),
						MCL_JiBenWuChaFragment.meter1_no, MCL_JiBenWuChaFragment.meter2_no,
						MCL_JiBenWuChaFragment.meter3_no), handler);
				iType++;
			}
			if (recv_state) {

				if (MissionSingleInstance.getSingleInstance().isYuan_state())
					Blue.send(altek.fnGetFrameByFunctionCode((byte) 0x61), handler);
			} else if (send_state)// ִ��һ�κ�ִ�в���������
			{
				Blue.send(altek.fnTaitiShuChu(220, Float.parseFloat(Qdianliu), 0, Float.parseFloat(Mjiaodu),
						Float.parseFloat(Mpinlv), 0, 0, 0, 1, 1, calc_time, 9, 0), handler);
				send_state = false;
				Log.i(TAG, "�Ƕȣ�" + Mjiaodu);
				Log.i(TAG, "Ƶ��" + Mpinlv);
				recv_state = true;
			}
		}
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			popupwindow.dismiss();
			switch (v.getId()) {
			case R.id.tv_zhijiejieru:
				btn_select_i_type.setText("ֱ�ӽ���ʽ");
				ManualCheckLoadActivity.METHOD = 1;
				iType = -1;
				break;
			case R.id.tv_100A:
				btn_select_i_type.setText("100A����ǯ");
				ManualCheckLoadActivity.METHOD = 3;
				iType = -1;
				break;
			case R.id.tv_5A:
				btn_select_i_type.setText("5A����ǯ");
				ManualCheckLoadActivity.METHOD = 7;
				iType = -1;
				break;
			default:
				break;
			}

		}

	};
}
