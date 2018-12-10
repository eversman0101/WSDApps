package com.wisdom.app.activity;

import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.Blue;
import com.wisdom.app.utils.ByteUtil;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.layout.TitleLayout;
import com.zijunlin.Zxing.Demo.CaptureActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @author wisdom's JiangYuPeng
 * @version 创建时间：2017-11-10 下午3:29:46 类说明：
 */
public class ParameterSettingActivity extends Activity {
	private String TAG = "ParameterSettingActivity";
	private Spinner sp_meter_count;
	private Spinner sp_meter_level;
	private Spinner sp_error_type;

	private Spinner sp_ref_voltage;
	private Spinner sp_meter_type;
	private Spinner sp_ref_freq;// 参比频率
	private Spinner sp_meter_spec;// 电表规约

	private EditText et_base_current;// 基本电流
	private Spinner sp_baud_rate;
	private EditText et_max_current;// 最大电流

	private Spinner sp_check_type;// 校验方式
	private Spinner sp_impulse_input;// 脉冲输入

	private EditText et_meter1_no;
	private EditText et_meter1_constant;// 电表1常数
	private EditText et_meter2_no;
	private EditText et_meter2_constant;
	private EditText et_meter3_no;
	private EditText et_meter3_constant;
	private Button btn_update_info;
	private Button btn_scan1;
	private Button btn_scan2;
	private Button btn_scan3;
	private String meter_level="";
	private SharedPreferences sharedPreferences;
	private SharedPreferences MCNLQidong;
	
	private int meter_numbers=0;
	private String meter1_no ;
	private String meter2_no ;
	private String meter3_no ;
	

	private ALTEK altek = new ALTEK();
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
         if (msg.what==0x003)
			{
				Toast.makeText(ParameterSettingActivity.this, "电表参数已更新", Toast.LENGTH_SHORT).show();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_parameter_setting);
		initView();
	 	initData();
		//sp_meter_count.getSelectedItem().toString();
		sp_meter_count.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				MissionSingleInstance.getSingleInstance().setMeterCount(position + 1);
				initMeterInfo();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		btn_scan1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openCameraIntent = new Intent(ParameterSettingActivity.this, CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
				MissionSingleInstance.getSingleInstance().setMeterState(1);
			}
		});
		btn_scan2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openCameraIntent = new Intent(ParameterSettingActivity.this, CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
				MissionSingleInstance.getSingleInstance().setMeterState(2);
			}
		});
		btn_scan3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent openCameraIntent = new Intent(ParameterSettingActivity.this, CaptureActivity.class);
				startActivityForResult(openCameraIntent, 0);
				MissionSingleInstance.getSingleInstance().setMeterState(3);
			}
		});
		btn_update_info.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				meter1_no=et_meter1_no.getText().toString();
				meter2_no=et_meter2_no.getText().toString();
				meter3_no=et_meter3_no.getText().toString();
				if (et_meter1_no.getText().toString().equals(""))
					meter1_no = "0";
				if (!et_meter2_no.getText().toString().equals(""))
					meter_numbers++;
				else
					meter2_no = "0";
				if (!et_meter3_no.getText().toString().equals(""))
					meter_numbers++;
				else
					meter3_no = "0";
				Blue.send(altek.fnTaitiCeShiLeiXingPeiZhiCanshu(ManualCheckLoadActivity.METHOD, 0, Integer.valueOf(et_meter1_constant.getText().toString()),
						Integer.valueOf(et_meter1_constant.getText().toString()), meter_numbers,meter1_no,
						meter2_no, meter3_no),handler);
				finish();
			}
		});
		et_meter1_constant.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				et_meter2_constant.setText(et_meter1_constant.getText().toString());
				et_meter3_constant.setText(et_meter1_constant.getText().toString());
			}
		});
	}
	private void initMeterInfo()
	{
		if (MissionSingleInstance.getSingleInstance().getMeterCount() == 1) {
			et_meter1_no.setEnabled(true);
			et_meter1_constant.setEnabled(true);
			btn_scan1.setEnabled(true);
			
			et_meter2_no.setEnabled(false);
			et_meter2_constant.setEnabled(false);
			btn_scan2.setEnabled(false);

			et_meter3_no.setEnabled(false);
			et_meter3_constant.setEnabled(false);
			btn_scan3.setEnabled(false);
		}
		if (MissionSingleInstance.getSingleInstance().getMeterCount() == 2) {
			et_meter2_no.setEnabled(true);
			et_meter2_constant.setEnabled(true);
			btn_scan2.setEnabled(true);

			et_meter3_no.setEnabled(false);
			et_meter3_constant.setEnabled(false);
			btn_scan3.setEnabled(false);
		}
		if (MissionSingleInstance.getSingleInstance().getMeterCount() == 3) {
			et_meter2_no.setEnabled(true);
			et_meter2_constant.setEnabled(true);
			btn_scan2.setEnabled(true);

			et_meter3_no.setEnabled(true);
			et_meter3_constant.setEnabled(true);
			btn_scan3.setEnabled(true);
		}
	}
	private void initView() {
		sp_meter_count = (Spinner) findViewById(R.id.meter_count);
		sp_meter_level = (Spinner) findViewById(R.id.meter_level);
		// sp_error_type=(Spinner)findViewById(R.id.error_type);
		sp_ref_voltage = (Spinner) findViewById(R.id.ref_voltage);
		// sp_meter_type=(Spinner)findViewById(R.id.meter_type);
		// sp_ref_freq=(Spinner)findViewById(R.id.ref_freq);
		sp_meter_spec = (Spinner) findViewById(R.id.meter_spec);
		sp_baud_rate = (Spinner) findViewById(R.id.baud_rate);
		sp_check_type = (Spinner) findViewById(R.id.check_type);
		// sp_impulse_input=(Spinner)findViewById(R.id.impulse_input);

		et_base_current = (EditText) findViewById(R.id.base_current);
		et_max_current = (EditText) findViewById(R.id.max_current);
		et_meter1_no = (EditText) findViewById(R.id.meter1_no);
		et_meter1_constant = (EditText) findViewById(R.id.meter1_constant);
		et_meter2_no = (EditText) findViewById(R.id.meter2_no);
		et_meter2_constant = (EditText) findViewById(R.id.meter2_constant);
		et_meter3_no = (EditText) findViewById(R.id.meter3_no);
		et_meter3_constant = (EditText) findViewById(R.id.meter3_constant);
		btn_scan1 = (Button) findViewById(R.id.btn_scan1);
		btn_scan2 = (Button) findViewById(R.id.btn_scan2);
		btn_scan3 = (Button) findViewById(R.id.btn_scan3);
		btn_update_info=(Button) findViewById(R.id.update_info);
		sp_baud_rate.setSelection(0);
		sp_meter_level.setSelection(1);
		sharedPreferences = getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE); // 私有数据
		MCNLQidong =getSharedPreferences("MCNL_QiDong", Context.MODE_PRIVATE);
	}

	private void initData() {
		meter_level = sharedPreferences.getString("meter_level", "");
		// String error_type=sharedPreferences.getString("error_type","");

		// String meter_type=sharedPreferences.getString("meter_type","");
		// String ref_freq=sharedPreferences.getString("ref_freq","");
		String meter_spec = sharedPreferences.getString("meter_spec", "");
		String baud_rate = sharedPreferences.getString("baud_rate", "");
		String check_type = sharedPreferences.getString("check_type", "");
		// String impulse_input=sharedPreferences.getString("impulse_input","");
		String meter_count = sharedPreferences.getString("meter_count", "");
		String ref_voltage = sharedPreferences.getString("ref_voltage", "");
		String base_current = sharedPreferences.getString("base_current", "");
		String max_current = sharedPreferences.getString("max_current", "");
		String meter1_no = sharedPreferences.getString("meter1_no", "");
		String meter1_constant = sharedPreferences.getString("meter1_constant", "");
		String meter2_no = sharedPreferences.getString("meter2_no", "");
		String meter2_constant = sharedPreferences.getString("meter2_constant", "");
		String meter3_no = sharedPreferences.getString("meter3_no", "");
		String meter3_constant = sharedPreferences.getString("meter3_constant", "");
		if (meter_level != "")
		{
			setSpinnerSelection(sp_meter_level, meter_level);
		}
		if (meter_spec != "")
			setSpinnerSelection(sp_meter_spec, meter_spec);
		if (baud_rate != "")
			setSpinnerSelection(sp_baud_rate, baud_rate);
		if (check_type != "")
			setSpinnerSelection(sp_check_type, check_type);

		if (meter_count != "")
		{
			setSpinnerSelection(sp_meter_count, meter_count);
			MissionSingleInstance.getSingleInstance().setMeterCount(Integer.valueOf(meter_count));
		}
		if (ref_voltage != "")
			setSpinnerSelection(sp_ref_voltage, ref_voltage);

		if (base_current != "")
			et_base_current.setText(base_current);
		if (max_current != "")
			et_max_current.setText(max_current);
		if (meter1_no != "")
			et_meter1_no.setText(meter1_no);
		if (meter1_constant != "")
			et_meter1_constant.setText(meter1_constant);
		if (meter2_no != "")
			et_meter2_no.setText(meter2_no);
		if (meter2_constant != "")
			et_meter2_constant.setText(meter2_constant);
		if (meter3_no != "")
			et_meter3_no.setText(meter3_no);
		if (meter3_constant != "")
			et_meter3_constant.setText(meter3_constant);
		initMeterInfo();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			int meter_state=MissionSingleInstance.getSingleInstance().getMeterState();
			if(meter_state==1)
				et_meter1_no.setText(scanResult);
			if(meter_state==2)
				et_meter2_no.setText(scanResult);
			if(meter_state==3)
				et_meter3_no.setText(scanResult);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause()");
		try {
			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putString("meter_count", sp_meter_count.getSelectedItem().toString());
			editor.putString("meter_level", sp_meter_level.getSelectedItem().toString());
			// editor.putString("error_type",sp_error_type.getSelectedItem().toString());
			editor.putString("ref_voltage", sp_ref_voltage.getSelectedItem().toString());
			// editor.putString("meter_type",sp_meter_type.getSelectedItem().toString());
			// editor.putString("ref_freq",sp_ref_freq.getSelectedItem().toString());
			editor.putString("meter_spec", sp_meter_spec.getSelectedItem().toString());
			editor.putString("baud_rate", sp_baud_rate.getSelectedItem().toString());
			editor.putString("check_type", sp_check_type.getSelectedItem().toString());
			// editor.putString("impulse_input",sp_impulse_input.getSelectedItem().toString());

			editor.putString("base_current", et_base_current.getText().toString());
			editor.putString("max_current", et_max_current.getText().toString());
			editor.putString("meter1_no", et_meter1_no.getText().toString());
			editor.putString("meter1_constant", et_meter1_constant.getText().toString());
			editor.putString("meter2_no", et_meter2_no.getText().toString());
			editor.putString("meter2_constant", et_meter2_constant.getText().toString());
			editor.putString("meter3_no", et_meter3_no.getText().toString());
			editor.putString("meter3_constant", et_meter3_constant.getText().toString());
			editor.commit();
			
			Editor editor1 =MCNLQidong.edit();
			
			String levels = sp_meter_level.getSelectedItem().toString();
			if(levels.equals("0.5级")){
				editor1.putString("sp_qidongdianliu", "0.1%Ib");
			}else if(levels.equals("1.0级")){
				editor1.putString("sp_qidongdianliu", "0.4%Ib");;
			}else if(levels.equals("2.0级")){
				editor1.putString("sp_qidongdianliu", "0.5%Ib");
			}else if(levels.equals("0.2级")){
				editor1.putString("sp_qidongdianliu", "0.1%Ib");
			}
			editor1.commit();
			// Toast.makeText(this, "数据已保存", Toast.LENGTH_SHORT).show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		try {
			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putString("meter_count", sp_meter_count.getSelectedItem().toString());
			editor.putString("meter_level", sp_meter_level.getSelectedItem().toString());
			// editor.putString("error_type",sp_error_type.getSelectedItem().toString());
			editor.putString("ref_voltage", sp_ref_voltage.getSelectedItem().toString());
			// editor.putString("meter_type",sp_meter_type.getSelectedItem().toString());
			// editor.putString("ref_freq",sp_ref_freq.getSelectedItem().toString());
			editor.putString("meter_spec", sp_meter_spec.getSelectedItem().toString());
			editor.putString("baud_rate", sp_baud_rate.getSelectedItem().toString());
			editor.putString("check_type", sp_check_type.getSelectedItem().toString());
			// editor.putString("impulse_input",sp_impulse_input.getSelectedItem().toString());

			editor.putString("base_current", et_base_current.getText().toString());
			editor.putString("max_current", et_max_current.getText().toString());
			String no1 = et_meter1_no.getText().toString();

			editor.putString("meter1_no", no1);

			editor.putString("meter1_constant", et_meter1_constant.getText().toString());
			String no2 = et_meter2_no.getText().toString();

			editor.putString("meter2_no", no2);

			editor.putString("meter2_constant", et_meter2_constant.getText().toString());
			String no3 = et_meter3_no.getText().toString();

			editor.putString("meter3_no", no3);

			editor.putString("meter3_constant", et_meter3_constant.getText().toString());
			editor.commit();
			Editor editor1 =MCNLQidong.edit();
			
			String levels = sp_meter_level.getSelectedItem().toString();
			Log.e("rrrrrrrrrrrr", levels);
			if(levels.equals("0.5级")){
				editor1.putString("sp_qidongdianliu", "0.1%Ib");
			}else if(levels.equals("1.0级")){
				editor1.putString("sp_qidongdianliu", "0.4%Ib");;
			}else if(levels.equals("2.0级")){
				editor1.putString("sp_qidongdianliu", "0.5%Ib");
			}else if(levels.equals("0.2级")){
				editor1.putString("sp_qidongdianliu", "0.1%Ib");
			}
			editor1.commit();
			// Toast.makeText(this, "数据已保存", Toast.LENGTH_SHORT).show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
