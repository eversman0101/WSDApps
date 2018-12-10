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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.wisdom.app.activity.ManualCheckLoadActivity;
import com.wisdom.app.activity.ParameterSettingActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.activityResult.ShiZhongResultActivity;
import com.wisdom.app.utils.*;
import com.wisdom.bean.ShiZhongWuChaBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.IShiZhongDao;
import com.wisdom.dao.ShiZhongDao;
import com.wisdom.layout.ITypePopupWindow;
import com.wisdom.layout.TitleLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author JinJingYun �鸺���ֶ�У��-ʱ�����
 */
public class MCL_ShiZhongWuChaFragment extends Fragment {
	private String TAG = "MCNL_ShiZhongWuChaFragment";
	@Bind(R.id.title_MCNL_shizhong)
	TitleLayout title;

	@Bind(R.id.dianbiao_canshu)
	Button btn_dianbiao_canshu;
	@Bind(R.id.jiaoyan_jieguo)
	Button btn_jiaoyan_jieguo;
	@Bind(R.id.start_test)
	Button btn_start_test;// ����У��
	@Bind(R.id.stop_test)
	Button btn_stop_test;// ֹͣУ��

	@Bind(R.id.meter1_circle)
	TextView tv_meter1_circle;// ���1Ȧ��
	@Bind(R.id.meter1_result_clock)
	TextView tv_meter1_result_clock;// ʱ�����
	@Bind(R.id.meter1_result_clock_avg)
	TextView tv_meter1_result_clock_avg;// ƽ�����
	@Bind(R.id.meter1_count)
	TextView tv_meter1_count;// ����

	@Bind(R.id.meter2_circle)
	TextView tv_meter2_circle;// ���2Ȧ��
	@Bind(R.id.meter2_result_clock)
	TextView tv_meter2_result_clock;// ʱ�����
	@Bind(R.id.meter2_result_clock_avg)
	TextView tv_meter2_result_clock_avg;// ƽ�����
	@Bind(R.id.meter2_count)
	TextView tv_meter2_count;// ����

	@Bind(R.id.meter3_circle)
	TextView tv_meter3_circle;// ���3Ȧ��
	@Bind(R.id.meter3_result_clock)
	TextView tv_meter3_result_clock;// ʱ�����
	@Bind(R.id.meter3_result_clock_avg)
	TextView tv_meter3_result_clock_avg;// ƽ�����
	@Bind(R.id.meter3_count)
	TextView tv_meter3_count;// ����

	@Bind(R.id.meter_time)
	EditText et_celiangshijian;// ����ʱ��
	@Bind(R.id.meter_count)
	EditText et_meter_count;// У�����

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
	@Bind(R.id.yuan_shuchu)
	Button btn_yuan_shuchu;
	@Bind(R.id.yuan_tingzhi)
	Button btn_yuan_tingzhi;
	@Bind(R.id.btn_select_i_type)
	Button btn_select_i_type;
	@Bind(R.id.progressbar)
	ProgressBar bar;
	@Bind(R.id.meter_circle)
	EditText et_meter_circle;
	@Bind(R.id.run_time)
	TextView tv_run_time;
	private ITypePopupWindow popupwindow;
	private SharedPreferences sharedPreferences_jibenwucha;
	private SharedPreferences sharedPreferences_shizhongwucha;
	private boolean send_state = false;
	private boolean recv_state = false;
	private boolean test_state = false;
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
	int iType = 0;// ����������ʽ,-1Ϊ����
	int shizhong_state = 0;
	// ��������
	private String dianya = "220";
	private String dianliu = "10";
	private String jiaodu = "0";
	private String pinlv = "0";
	private String quanshu = "3";

	private String cishu = "0";
	private String ceshishijian;
	private String shengyushijian;
	private IShiZhongDao dao;
	private TaitiCeLiangShuJuBean bean;
	int index = 0;
	int iOFF = 0;// �����ж�Դ�Ƿ����ֹͣ
	// �����Ϣ
	private String meter1_no = "0";
	private String meter2_no = "0";
	private String meter3_no = "0";
	private String maichongchangshu = "0";
	int meter_numbers = 1;// ������
	int maxtime = 0;
	int count = 0;
	int time = 0;
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
						if (test_state) {
							// ��������
							tv_meter1_circle.setText(quanshu);
							tv_meter2_circle.setText(quanshu);
							tv_meter3_circle.setText(quanshu);

							tv_meter1_count.setText(bean.getCishu());
							tv_meter2_count.setText(bean.getCishu());
							tv_meter3_count.setText(bean.getCishu());

							if (time == 0 && (Integer.valueOf(bean.getTime()) > 5)) {
								time = Integer.valueOf(bean.getTime());
								if (maxtime < time)
									maxtime = time;
							}
							if (count != Integer.valueOf(bean.getCishu())) {
								// ��һ��
								secondState = 0;
								count = Integer.valueOf(bean.getCishu());
								time = Integer.valueOf(bean.getTime());
							}
							setBarProgress(time, Integer.valueOf(bean.getTime()));
							tv_run_time.setText(bean.getTime());
							Log.i("TAG", "������" + bean.getCishu());
							// �������
							if (Integer.valueOf(bean.getJieguo()) == 2) {
								try {
									bar.setProgress(100);
									time = 0;
									maxtime = 0;
									secondState = 0;
									xufuhe = -1;
									count = 0;
									test_state = false;
									ShiZhongWuChaBean shizhongwuchaBean = new ShiZhongWuChaBean();
									shizhongwuchaBean.setDate(ByteUtil.GetNowDate());
									shizhongwuchaBean.setQuanshu(quanshu);
									shizhongwuchaBean.setCishu(cishu);
									shizhongwuchaBean.setShizhongwucha1(bean.getWucha1());
									shizhongwuchaBean.setShizhongwucha2(bean.getWucha2());
									shizhongwuchaBean.setShizhongwucha3(bean.getWucha3());
									shizhongwuchaBean.setPingjunwucha1(bean.getPiancha1());
									shizhongwuchaBean.setPingjunwucha2(bean.getPiancha2());
									shizhongwuchaBean.setPingjunwucha3(bean.getPiancha3());
									dao.add(shizhongwuchaBean);
									if (Double.valueOf(bean.getWucha1()) < 100)
										tv_meter1_result_clock.setText(bean.getWucha1());
									if (Double.valueOf(bean.getWucha2()) < 100)
										tv_meter2_result_clock.setText(bean.getWucha2());
									if (Double.valueOf(bean.getWucha3()) < 100)
										tv_meter3_result_clock.setText(bean.getWucha3());

									tv_meter1_result_clock_avg.setText(bean.getPiancha1());
									tv_meter2_result_clock_avg.setText(bean.getPiancha2());
									tv_meter3_result_clock_avg.setText(bean.getPiancha3());
									Toast.makeText(getActivity(), "�������", Toast.LENGTH_SHORT).show();

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
				if (ManualCheckLoadActivity.METHOD == 2)
					Toast.makeText(getActivity(), "������ģʽ��", Toast.LENGTH_SHORT).show();
				else if (ManualCheckLoadActivity.METHOD == 0)
					Toast.makeText(getActivity(), "�鸺��ģʽ��", Toast.LENGTH_SHORT).show();
				else if (ManualCheckLoadActivity.METHOD == 1)
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
		View view = inflater.inflate(R.layout.fragment_mcl_shizhong_wucha, null);
		ButterKnife.bind(this, view);
		title.setTitleText("ʵ�����ֶ�У��->ʱ�����");
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
				if (s_shijian.equals("") || s_cishu.equals(""))
					Toast.makeText(getActivity(), "��������Ϊ��", Toast.LENGTH_SHORT).show();
				else {
					saveData();
					// fnSendBytes(altek.fnTaitiShuChu(Float.parseFloat(dianya),
					// Float.parseFloat(dianliu), 0, Float.parseFloat(jiaodu),
					// Float.parseFloat(pinlv), 0, 0, 0,
					// Integer.valueOf(quanshu),
					// Integer.valueOf(cishu),Integer.valueOf(ceshishijian), 11,
					// 0));
					recv_state = true;
					send_state = false;
					test_state = true;
					shizhong_state = -1;
					bar.setProgress(0);
					Toast.makeText(getActivity(), "���Կ�ʼ", Toast.LENGTH_SHORT).show();
					// btn_start_test.setEnabled(false);
				}
			}
		});
		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ShiZhongResultActivity.class);
				startActivity(intent);
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

	private void getTaitiData() {
		dianbiaocanshu = getActivity().getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
		maichongchangshu = dianbiaocanshu.getString("meter1_constant", "1200");
		meter1_no = dianbiaocanshu.getString("meter1_no", "");
		meter2_no = dianbiaocanshu.getString("meter2_no", "");
		meter3_no = dianbiaocanshu.getString("meter3_no", "");
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
		// meter_numbers, meter1_no, meter2_no, meter3_no));
	}

	private void initTopThreeLine() {
		tv_meter1_circle.setText("");
		tv_meter2_circle.setText("");
		tv_meter3_circle.setText("");
		tv_meter1_result_clock.setText("");
		tv_meter2_result_clock.setText("");
		tv_meter3_result_clock.setText("");

		tv_meter1_count.setText("");
		tv_meter2_count.setText("");
		tv_meter3_count.setText("");

		tv_meter1_result_clock_avg.setText("");
		tv_meter2_result_clock_avg.setText("");
		tv_meter3_result_clock_avg.setText("");
	}

	private void initData() {
		try {
			if (MissionSingleInstance.getSingleInstance().isYuan_state()) {
				tv_dianyayuan.setText("��ѹԴ:�����");
				tv_dianliuyuan.setText("����Դ:�����");

			} else if (!MissionSingleInstance.getSingleInstance().isYuan_state()) {
				tv_dianyayuan.setText("��ѹԴ:��ֹͣ");
				tv_dianliuyuan.setText("����Դ:��ֹͣ");

			}
			dao = new ShiZhongDao(getActivity());
			sharedPreferences_jibenwucha = getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE);
			sharedPreferences_shizhongwucha = getActivity().getSharedPreferences("MCNL_ShiZhongWuCha",
					Context.MODE_PRIVATE);
			dianya = sharedPreferences_jibenwucha.getString("tv_chooser_u", "220");
			dianliu = sharedPreferences_jibenwucha.getString("tv_chooser_i", "10");
			jiaodu = MissionSingleInstance.getSingleInstance().getJiaodu();
			pinlv = sharedPreferences_jibenwucha.getString("et_chooser_f", "0");
			quanshu = sharedPreferences_jibenwucha.getString("et_meter_circle", "3");
			tv_jiaodu.setText(jiaodu);
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
			timer = new Timer();
			timer.schedule(new SendDataTask(), 1000, 1000);
			recv_state = true;
			getData();
		} catch (Exception ex) {
			Toast.makeText(getActivity(), "�������������豸", Toast.LENGTH_SHORT).show();
		}
	}

	private void getData() {
		String quanshu = sharedPreferences_shizhongwucha.getString("quanshu", "5");
		String shijian = sharedPreferences_shizhongwucha.getString("shijian", "1");
		String cishu = sharedPreferences_shizhongwucha.getString("cishu", "1");
		if (shijian != null)
			et_celiangshijian.setText(shijian);
		if (cishu != null)
			et_meter_count.setText(cishu);
		if (quanshu != null)
			et_meter_circle.setText(quanshu);
	}

	private void saveData() {
		try {
			Editor editor = sharedPreferences_shizhongwucha.edit();// ��ȡ�༭��
			ceshishijian = et_celiangshijian.getText().toString();
			cishu = et_meter_count.getText().toString();
			quanshu = et_meter_circle.getText().toString();
			editor.putString("quanshu", quanshu);
			editor.putString("shijian", ceshishijian);
			editor.putString("cishu", cishu);

		} catch (Exception ex) {
			ex.printStackTrace();
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
	 * data=ds.fnReceiveData(strRx, iRxDataLen); if(data!=null) {
	 * if((data[2]&0x7f)==0x61)//ʵʱ̨��������� { TaitiCeLiangShuJuBean
	 * bean=ds.fnGetTaitiData(data); if(bean!=null) { Message msg = new
	 * Message(); msg.what = 0x001; msg.obj = bean; handler.sendMessage(msg); }
	 * } if((data[2]&0x7f)==0x5E)//̨������������ò��� {
	 * if(ManualCheckLoadActivity.METHOD==2) Toast.makeText(getActivity(),
	 * "������ģʽ��", Toast.LENGTH_SHORT).show(); else
	 * if(ManualCheckLoadActivity.METHOD==0) Toast.makeText(getActivity(),
	 * "�鸺��ģʽ��", Toast.LENGTH_SHORT).show(); else if
	 * (ManualCheckLoadActivity.METHOD == 1) Toast.makeText(getActivity(),
	 * "ֱ�ӽ���ģʽ�Ѵ�", Toast.LENGTH_SHORT).show(); else if
	 * (ManualCheckLoadActivity.METHOD == 3) Toast.makeText(getActivity(),
	 * "100A����ǯģʽ�Ѵ�", Toast.LENGTH_SHORT).show(); else if
	 * (ManualCheckLoadActivity.METHOD == 7) Toast.makeText(getActivity(),
	 * "5A����ǯģʽ�Ѵ�", Toast.LENGTH_SHORT).show(); } }
	 * 
	 * } } };
	 */

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

	int secondState = 0;

	private void setBarProgress(int second, int leftSecond) {
		if (second != 0) {
			int s = (second - leftSecond) - secondState;// ���ӵ�ʱ��
			int quarter = 100 / (second * Integer.valueOf(cishu));
			bar.incrementProgressBy(s * quarter);
			secondState = second - leftSecond;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume()");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		try {
			// getActivity().unregisterReceiver(mGattUpdateReceiver);
			timer.cancel();
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

	int xufuhe = 0;
	int x = 0;

	private class SendDataTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (xufuhe == -1) {
				ManualCheckLoadActivity.METHOD = 0;
				Blue.send(
						altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0, Integer.valueOf(maichongchangshu),
								Integer.valueOf(maichongchangshu), meter_numbers, meter1_no, meter2_no, meter3_no),
						handler);
				recv_state = true;
				xufuhe++;
				test_state = false;
			}

			if (recv_state) {
				if (MissionSingleInstance.getSingleInstance().isYuan_state()) {
					if (x % 2 == 0) {
						if (shizhong_state == -1) {
							ManualCheckLoadActivity.METHOD = 2;
							Blue.send(altek.fnTaitiCeShiLeiXingPeiZhiCanshu(2, 0, Integer.valueOf(maichongchangshu),
									Integer.valueOf(maichongchangshu), meter_numbers, meter1_no, meter2_no, meter3_no),
									handler);
							shizhong_state++;
							recv_state = false;
							send_state = true;

						}
					} else
						Blue.send(altek.fnGetFrameByFunctionCode((byte) 0x61), handler);
					x++;
				}
			} else if (send_state)// ִ��һ�κ�ִ�в���������
			{

				Blue.send(altek.fnTaitiShuChu(Float.parseFloat(dianya), Float.parseFloat(dianliu), 0,
						Float.parseFloat(jiaodu), Float.parseFloat(pinlv), 0, 0, 0, Integer.valueOf(quanshu),
						Integer.valueOf(cishu), Integer.valueOf(ceshishijian), 11, 0), handler);
				send_state = false;
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
