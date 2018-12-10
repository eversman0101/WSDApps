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
import android.widget.AdapterView.OnItemSelectedListener;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.wisdom.app.activity.ParameterSettingActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.activityResult.JiBenWuChaResultActivity;
import com.wisdom.app.utils.*;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.MeterInfoBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.dao.JiBenWuChaDao;
import com.wisdom.layout.TitleLayout;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author wisdom's JiangYuPeng
 * @version ����ʱ�䣺2017-11-9 ����5:16:27
 * @version �޸�ʱ�䣺2017-11-29����17:22 JinJingYun �鸺���ֶ�У��������
 */
public class MCNL_YaoCeFragment extends Fragment  {
	private String TAG = "MCNL_JiBenWuChaFragment";
	@Bind(R.id.title_MCNL_jibenwucha)
	TitleLayout title;
	@Bind(R.id.dianbiao_canshu)
	Button btn_dianbiao_canshu;
	@Bind(R.id.jiaoyan_jieguo)
	Button btn_jiaoyan_jieguo;
	@Bind(R.id.start_test)
	Button btn_start_test;// ����У��
	@Bind(R.id.stop_test)
	Button btn_stop_test;// ֹͣУ��

	@Bind(R.id.tv_f)
	TextView tv_f;

	@Bind(R.id.meter_u)
	TextView tv_meter_u;
	@Bind(R.id.meter_i)
	TextView tv_meter_i;
	@Bind(R.id.meter_p)
	TextView tv_meter_p;
	@Bind(R.id.meter_pf)
	TextView tv_meter_pf;
	
	@Bind(R.id.meter_u_result)
	TextView tv_meter_u_result;
	@Bind(R.id.meter_i_result)
	TextView tv_meter_i_result;
	@Bind(R.id.meter_p_result)
	TextView tv_meter_p_result;
	@Bind(R.id.meter_pf_result)
	TextView tv_meter_pf_result;

	//@Bind(R.id.meter_circle)
	//EditText et_meter_circle;// У��Ȧ��
	@Bind(R.id.sp_meter_index)
	Spinner sp_meter_index;// ��λ��

	@Bind(R.id.chooser_u)
	TextView et_chooser_u;// ��ѹ���
	@Bind(R.id.tv_U)
	TextView tv_U;// ��ѹ�Ҳ�

	@Bind(R.id.chooser_i)
	TextView et_chooser_i;// �������

	@Bind(R.id.tv_I)
	TextView tv_I;

	@Bind(R.id.chooser_p)
	TextView sp_chooser_p;// ���
	@Bind(R.id.tv_jiaodu)
	TextView tv_jiaodu;
	
	@Bind(R.id.chooser_f)
	EditText et_chooser_f;// Ƶ��

	@Bind(R.id.show_p)
	TextView tv_show_p;// A���й�
	
	@Bind(R.id.show_q)
	TextView tv_show_q;// A���޹�
	
	@Bind(R.id.show_s)
	TextView tv_show_s;// �ܹ�
	
	@Bind(R.id.show_pf)
	TextView tv_show_pf;// ��������

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
	
	
	
	int time=0;//ÿһ��У�����ʱ��
	int count=0;//�ϴδ���
	private SharedPreferences sharedPreferences;
	private SharedPreferences dianbiaocanshu;
	double i_meter_level=0.5;
	private boolean send_state = false;
	private boolean recv_state = false;
	private boolean test_state = false;

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

	// ��������
	private String dianya = "-1";
	private String dianliu = "0";
	private String jiaodu = "0";
	private String pinlv = "0";
	private String yougong = "0";
	private String wugong = "0";
	private String zonggong = "0";
	private String gonglvyinshu = "0";
	// 666
	private String quanshu = "0";
	private String cishu = "1";
	
	private int meter_index=1;
	// �����Ϣ
	public static String meter1_no = "0";
	public static String meter2_no = "0";
	public static String meter3_no = "0";
	public static String maichongchangshu = "0";
    //Dialog
    private ChooseUDialog chooseUDialog; 
    private ChooseIDialog chooseIDialog;
    private ChooseQuanDialog chooseQuanDialog;
    
    JiBenWuChaBean jibenwuchaBean=null;
	private JiBenWuChaDao dao;
	private TaitiCeLiangShuJuBean bean;
	int i_cishu=1;
	int iOFF = 0;// �����ж�Դ�Ƿ����ֹͣ
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
							if (iOFF == 3)// �϶�Դ��ֹͣ
							{
								btn_start_test.setEnabled(false);
								btn_stop_test.setEnabled(false);
								btn_yuan_shuchu.setEnabled(true);
								//btn_yuan_tingzhi.setEnabled(false);Դֹͣ���ɿ��Ե��
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
						tv_show_p.setText(bean.getYougong());
						tv_show_q.setText(bean.getWugong());
						tv_show_s.setText(bean.getZonggong());
						tv_show_pf.setText(bean.getGonglvyinshu());
						
						// ��������
						try {
							if (test_state) {
								
								if(time==0&&(Integer.valueOf(bean.getTime())>5))
								{
									time=Integer.valueOf(bean.getTime());
									if(maxtime<time)
										maxtime=time;
								}

								if(count!=Integer.valueOf(bean.getCishu()))
								{
									//��һ��
									secondState=0;
									count=Integer.valueOf(bean.getCishu());
									time=Integer.valueOf(bean.getTime());
								}
								setBarProgress(time,Integer.valueOf(bean.getTime()));
								et_runtime.setText(bean.getTime());
								
								if(Integer.valueOf(cishu)==Integer.valueOf(bean.getCishu())&&(Integer.valueOf(bean.getJieguo())==2)&&(Integer.valueOf(bean.getTime())==0)&&(maxtime>0))// У�����
								{
									bar.setProgress(100);
									time=0;
									secondState=0;
									maxtime=0;
									test_state = false;
									MissionSingleInstance.getSingleInstance().setTestState(test_state);
									//recv_state = false;
									Log.e("UF","У�����");
									Toast.makeText(getActivity(), getText(R.string.job_done), Toast.LENGTH_SHORT).show();
									btn_start_test.setEnabled(true);
									btn_stop_test.setEnabled(false);
									tv_meter_u_result.setText(bean.getWucha1());
									tv_meter_i_result.setText(bean.getWucha1_2());
									tv_meter_p_result.setText(bean.getWucha1_3());
									tv_meter_pf_result.setText(bean.getWucha1_5());
									if(bean.getWendu()!=null)
									SharepreferencesUtil.setTemperature(getActivity(), bean.getWendu());
									//fnSetResult();
									
									jibenwuchaBean.setType("0");
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
				Toast.makeText(getActivity(), getText(R.string.virtual_mode_open), Toast.LENGTH_SHORT).show();
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
			else if(msg.what==0x13)
			{
				MeterInfoBean bean=(MeterInfoBean)msg.obj;
				if(bean!=null)
				{
					tv_meter_u.setText(bean.getU());
					tv_meter_i.setText(bean.getI());
					tv_meter_p.setText(bean.getP());
					tv_meter_pf.setText(bean.getPf());
				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_mcnl_yaoce, null);

		ButterKnife.bind(this, view);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.telemetry).toString()));
		
		initView();
		initData();
		sp_meter_index.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				 TextView tv = (TextView)view;
                 tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //���þ���
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
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
				String str_meter_index=sp_meter_index.getSelectedItem().toString();
				if(str_meter_index.equals(""))
				str_meter_index="1";
				meter_index=Integer.valueOf(str_meter_index);
				jiaodu = sp_chooser_p.getText().toString();
				MissionSingleInstance.getSingleInstance().setJiaodu(jiaodu);
				dianya = et_chooser_u.getText().toString();
				dianliu = et_chooser_i.getText().toString();
				pinlv = et_chooser_f.getText().toString();
				quanshu = "5";
				cishu = "1";
				int i_cishu=Integer.valueOf(cishu);
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
				//MissionSingleInstance.getSingleInstance().setTestState(test_state);
				try {
					dianya = "0";
					dianliu = "0";
					jiaodu = "0";
					//pinlv = "0";
					quanshu = "0";
					cishu = "0";
					getTaitiData();
					Comm.getInstance().status_loop2=null;
					fnSendState();
				} catch (Exception ex) {

				}
			}
		});
		btn_jiaoyan_jieguo.setVisibility(View.GONE);
		btn_jiaoyan_jieguo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
					//MissionSingleInstance.getSingleInstance().setTestState(test_state);
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
		btn_yuan_shuchu.setEnabled(true);
		btn_yuan_tingzhi.setEnabled(true);
		sharedPreferences = getActivity().getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE); // ˽������
		dianbiaocanshu = getActivity().getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
	}
	private void initTopThreeLine()
	{
		tv_meter_u.setText("");
		tv_meter_i.setText("");
		tv_meter_p.setText("");
		tv_meter_pf.setText("");
		tv_meter_u_result.setText("");
		tv_meter_i_result.setText("");
		tv_meter_p_result.setText("");
		tv_meter_pf_result.setText("");
	}
	private void initData() {
		try {
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
			Comm.getInstance().status_loop2=altek.fnChaoBiao(meter_index);
			
			
		} catch (Exception ex) {
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}

	int meter_numbers = 1;// �������

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
		String meter_circle = sharedPreferences.getString("et_meter_circle", "0");
		//String meter_count = sharedPreferences.getString("et_meter_count", "0");
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
		//if (meter_circle != "")
			//et_meter_circle.setText(meter_circle);
		//if (meter_count != "")
		//	et_meter_count.setText(meter_count);
	}

	private void saveTaitiData() {
		try {
			Editor editor = sharedPreferences.edit();// ��ȡ�༭��
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
			Log.i(TAG, "Ƶ��:" + pinlv);
			editor.putString("et_chooser_f", pinlv);

			quanshu = "5";
			//editor.putString("et_meter_circle", quanshu);// У��Ȧ��
			cishu = "1";
			editor.putString("et_meter_count", cishu);// У�����

			editor.commit();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * ����SpinnerĬ��item
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
			// ������ǰ����ʾ �൱�ڵ�����onPause();
			Log.i(TAG, "onHidden()");
			try {
				//getActivity().unregisterReceiver(mGattUpdateReceiver);
				//timer.cancel();
			} catch (Exception ex) {

			}

			return;
		} else { // ����ǰ����ʾ �൱�ڵ�����onResume();
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
	 * �ٷֺ��ַ�����int�����������λС��
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
		int s=(second-leftSecond)-secondState;//���ӵ�ʱ��
		int quarter=100/(second*Integer.valueOf(cishu));
		bar.incrementProgressBy(s*quarter);
		secondState=second-leftSecond;
		}
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
				str_result=bean.getWucha1()+" "+"�ϸ�";
			else
				str_result=bean.getWucha1()+" "+"���ϸ�";
			jibenwuchaBean.setDiannengwucha1(str_result);
		}
		if((Double.valueOf(bean.getWucha1_2())<100)&&(i_cishu>=2))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_2())<i_meter_level)
				str_result=bean.getWucha1_2()+" "+"�ϸ�";
			else
				str_result=bean.getWucha1_2()+" "+"���ϸ�";
			jibenwuchaBean.setDiannengwucha1_2(str_result);
		}
		if((Double.valueOf(bean.getWucha1_3())<100)&&(i_cishu>=3))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_3())<i_meter_level)
				str_result=bean.getWucha1_3()+" "+"�ϸ�";
			else
				str_result=bean.getWucha1_3()+" "+"���ϸ�";
			jibenwuchaBean.setDiannengwucha1_3(str_result);
		}
		if((Double.valueOf(bean.getWucha1_4())<100)&&(i_cishu>=4))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_4())<i_meter_level)
				str_result=bean.getWucha1_4()+" "+"�ϸ�";
			else
				str_result=bean.getWucha1_4()+" "+"���ϸ�";
			jibenwuchaBean.setDiannengwucha1_4(str_result);
		}
		if((Double.valueOf(bean.getWucha1_5())<100)&&(i_cishu>=5))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_5())<i_meter_level)
				str_result=bean.getWucha1_5()+" "+"�ϸ�";
			else
				str_result=bean.getWucha1_5()+" "+"���ϸ�";
			jibenwuchaBean.setDiannengwucha1_5(str_result);
		}
		if((Double.valueOf(bean.getWucha1_6())<100)&&(i_cishu>=6))
		{
			String str_result="";
			if(Double.valueOf(bean.getWucha1_6())<i_meter_level)
				str_result=bean.getWucha1_6()+" "+"�ϸ�";
			else
				str_result=bean.getWucha1_6()+" "+"���ϸ�";
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
					str_result=bean.getWucha2()+" "+"�ϸ�";
				else
					str_result=bean.getWucha2()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha2(str_result);
				//tv_meter2_ev.setText(str_result);
			}
			if((Double.valueOf(bean.getWucha2_2())<100)&&(i_cishu>=2))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_2())<i_meter_level)
					str_result=bean.getWucha2_2()+" "+"�ϸ�";
				else
					str_result=bean.getWucha2_2()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha2_2(str_result);
			}
			if((Double.valueOf(bean.getWucha2_3())<100)&&(i_cishu>=3))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_3())<i_meter_level)
					str_result=bean.getWucha2_3()+" "+"�ϸ�";
				else
					str_result=bean.getWucha2_3()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha2_3(str_result);
			}
			if((Double.valueOf(bean.getWucha2_4())<100)&&(i_cishu>=4))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_4())<i_meter_level)
					str_result=bean.getWucha2_4()+" "+"�ϸ�";
				else
					str_result=bean.getWucha2_4()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha2_4(str_result);
			}
			if((Double.valueOf(bean.getWucha2_5())<100)&&(i_cishu>=5))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_5())<i_meter_level)
					str_result=bean.getWucha2_5()+" "+"�ϸ�";
				else
					str_result=bean.getWucha2_5()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha2_5(str_result);
			}
			if((Double.valueOf(bean.getWucha2_6())<100)&&(i_cishu>=6))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha2_6())<i_meter_level)
					str_result=bean.getWucha2_6()+" "+"�ϸ�";
				else
					str_result=bean.getWucha2_6()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha2_6(str_result);
			}
		}
		if(Double.valueOf(bean.getPiancha3())<100)
		jibenwuchaBean.setBiaozhunpiancha3(bean.getPiancha3());
		if(Utils.fnGetMeterInfo(getActivity()).getMeterNumbers()==3)
		{
			if(Double.valueOf(bean.getWucha3())<100)
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3())<i_meter_level)
					str_result=bean.getWucha3()+" "+"�ϸ�";
				else
					str_result=bean.getWucha3()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha3(str_result);
				//tv_meter3_ev.setText(str_result);
			}
			if((Double.valueOf(bean.getWucha3_2())<100)&&(i_cishu>=2))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_2())<i_meter_level)
					str_result=bean.getWucha3_2()+" "+"�ϸ�";
				else
					str_result=bean.getWucha3_2()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha3_2(str_result);
			}
			if((Double.valueOf(bean.getWucha3_3())<100)&&(i_cishu>=3))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_3())<i_meter_level)
					str_result=bean.getWucha3_3()+" "+"�ϸ�";
				else
					str_result=bean.getWucha3_3()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha3_3(str_result);
			}
			if((Double.valueOf(bean.getWucha3_4())<100)&&(i_cishu>=4))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_4())<i_meter_level)
					str_result=bean.getWucha3_4()+" "+"�ϸ�";
				else
					str_result=bean.getWucha3_4()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha3_4(str_result);
			}
			if((Double.valueOf(bean.getWucha3_5())<100)&&(i_cishu>=5))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_5())<i_meter_level)
					str_result=bean.getWucha3_5()+" "+"�ϸ�";
				else
					str_result=bean.getWucha3_5()+" "+"���ϸ�";
				jibenwuchaBean.setDiannengwucha3_5(str_result);
			}
			if((Double.valueOf(bean.getWucha3_6())<100)&&(i_cishu>=6))
			{
				String str_result="";
				if(Double.valueOf(bean.getWucha3_6())<i_meter_level)
					str_result=bean.getWucha3_6()+" "+"�ϸ�";
				else
					str_result=bean.getWucha3_6()+" "+"���ϸ�";
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
			Toast.makeText(getActivity(), getText(R.string.frequency_range_40_60),Toast.LENGTH_SHORT).show();
			return;
		}
		Comm.getInstance().status_one=altek.fnTaitiShuChu(f_dianya, f_dianliu, 0,
				f_jiaodu, f_pinlv, 0, 0, 0, i_quanshu,
				1, 0, 12, 0);
		if (test_state) {
			Comm.getInstance().status_loop2=altek.fnChaoBiao(meter_index);
			Message msg = new Message();
			msg.what = 0x002;
			handler.sendMessage(msg);
		}
	}

}