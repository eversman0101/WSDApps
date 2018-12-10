package com.wisdom.app.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.sunday.slidetabfragment.blue.BlueConnActivity;
import com.sunday.slidetabfragment.blue.BlueManager;
import com.sunday.slidetabfragment.blue.Config;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.BluetoothLeService;
import com.wisdom.app.utils.ByteUtil;
import com.wisdom.app.utils.DataService;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.app.utils.SharepreferencesUtil;
import com.wisdom.app.utils.UnpackFrame;
import com.wisdom.app.utils.Utils;
import com.wisdom.bean.BoXingBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.update.Update;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private Button manualCheckNoLoadBtn;
	private Button autoCheckNoLoadBtn;
	private Button manualCheckLoadBtn;
	private Button parameterSettingBtn;
	private Button dataQueryBtn;
	private Button systemSettingBtn;
	private RelativeLayout rl_device;
	private TextView tv_deviceName;
	private TextView tv_deviceStatus;
	private TextView tv_systemtime;
	private TextView tv_temp;
	private TextView tv_version;
	/**
	 * 用于对Fragment进行管理
	 */
	private FragmentManager fragmentManager;
	// 蓝牙连接状态
	private boolean mConnected = false;
	private String status = "未连接";
	// 蓝牙名字
	private String mDeviceName;
	// 蓝牙地址
	private String mDeviceAddress;
	// 蓝牙信号值
	private String mRssi;
	private Bundle b;

	// 蓝牙service,负责后台的蓝牙服务
	public static BluetoothLeService mBluetoothLeService;
	private TextView tvblebtatus;
	private BluetoothGattCharacteristic characteristic;
	// private ArrayList<ArrayList<BluetoothGattCharacteristic>>
	// mGattCharacteristics = new
	// ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	// 蓝牙特征值
	// private static BluetoothGattCharacteristic target_chara = null;
	private Handler mhandler = new Handler();

	private String systime = null;
	private Intent intent;

	private Context context;
	private Dialog progressDialog;

	private SharedPreferences sharedPreferences;
	// 收发数据
	private UnpackFrame uf = new UnpackFrame();
	private ALTEK altek = new ALTEK();
	private DataService ds = new DataService();
	int[] iTempBuf = new int[10];
	int iTxLen = 0;
	int iStart = 0, iEnd = 0, iLen = 0, i = 0;
	String strCurTx = null;
	String str = null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x001) {
				try {
					progressDialog.dismiss();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (msg.what == 0x002) {
				Toast.makeText(getApplicationContext(), "蓝牙服务绑定失败",
						Toast.LENGTH_SHORT).show();
			}
			if (msg.what == 0x003) {
//				SimpleDateFormat sdf = new SimpleDateFormat(
//						"yyyy年MM月dd日  HH时mm分ss秒");
//				Calendar calendar = Calendar.getInstance();
//				Date date;
				try {
//					
//					date = sdf.parse(systime);
//					calendar.setTime(date);
//					calendar.add(Calendar.SECOND, 1);
//					String dateStr = sdf.format(calendar.getTime());
					systime = Utils.getSystemTime();
					tv_systemtime.setText(systime);// 更新时间
					MissionSingleInstance.getSingleInstance().setSystemtime(systime);
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("systime", e.toString());
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		manualCheckNoLoadBtn = (Button) findViewById(R.id.manual_check);
		manualCheckNoLoadBtn.setOnClickListener(this);
		autoCheckNoLoadBtn = (Button) findViewById(R.id.auto_check);
		autoCheckNoLoadBtn.setOnClickListener(this);
		manualCheckLoadBtn = (Button) findViewById(R.id.load_check);
		manualCheckLoadBtn.setOnClickListener(this);
		parameterSettingBtn = (Button) findViewById(R.id.dianbiao_canshu);
		parameterSettingBtn.setOnClickListener(this);
		dataQueryBtn = (Button) findViewById(R.id.data_view);
		dataQueryBtn.setOnClickListener(this);
		systemSettingBtn = (Button) findViewById(R.id.sys_setting);
		systemSettingBtn.setOnClickListener(this);
		tv_deviceName = (TextView) findViewById(R.id.bt_device_name);
		tv_deviceStatus = (TextView) findViewById(R.id.bt_device_status);
		tv_systemtime = (TextView) findViewById(R.id.status_time);
		tv_temp = (TextView) findViewById(R.id.status_temp_hum);
		tv_version = (TextView) findViewById(R.id.version_software);
		rl_device = (RelativeLayout) findViewById(R.id.bt_device);
		rl_device.setOnClickListener(this);

		context = this;
		initView();
		initData();
		new TimeThread().start();
	}

	public void initView() {

		progressDialog = new Dialog(context, R.style.progress_dialog);
		progressDialog.setContentView(R.layout.dialog);
		progressDialog.setCancelable(true);
		progressDialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		TextView msg = (TextView) progressDialog
				.findViewById(R.id.id_tv_loadingmsg);
		msg.setText("加载中....");
		String str_temp = SharepreferencesUtil
				.getTemperature(MainActivity.this);
		if (str_temp != "") {
			tv_temp.setText(str_temp);
		}
	}

	private void initData() {
		Log.e("Update", "初始化更新");
		Update.getInstance().checkUpdate(MainActivity.this);
		try {
			String str_version = Update.getInstance().getVersionName(MainActivity.this);
			if (str_version != null)
				//tv_version.setText("软件版本: " + str_version);
				tv_version.setText(String.format(getText(R.string.software_version_placeholder).toString(),str_version));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		Intent intent;
		/*
		if (view.getId() == R.id.manual_check
				|| view.getId() == R.id.auto_check
				|| view.getId() == R.id.load_check) {
			if (!BlueManager.getInstance().isConnect()) {
				Toast.makeText(getApplicationContext(), "请先连接设备",
						Toast.LENGTH_SHORT).show();
				return;

			}
		}
		*/
		switch (view.getId()) {
		case R.id.manual_check:
			intent = new Intent(MainActivity.this,
					ManualCheckNoneLoadActivity.class);
			startActivity(intent);
			break;
		case R.id.auto_check:
			intent = new Intent(MainActivity.this, ACNLActivity.class);
			startActivity(intent);
			break;
		case R.id.load_check:
			/*
			if (MissionSingleInstance.getSingleInstance().isYuan_state())
				Toast.makeText(this, "请先停止源输出！", Toast.LENGTH_SHORT).show();
			else {
				intent = new Intent(MainActivity.this,
						ManualCheckLoadActivity.class);
				startActivity(intent);
			}*/
			intent = new Intent(MainActivity.this,
					ManualCheckLoadActivity.class);
			startActivity(intent);
		
			break;
		case R.id.dianbiao_canshu:
			intent = new Intent(MainActivity.this,
					ParameterSettingActivity.class);
			startActivity(intent);
			break;
		case R.id.data_view:

			intent = new Intent(MainActivity.this, DataViewActivity.class);
			startActivity(intent);
			break;
		case R.id.sys_setting:
			intent = new Intent(MainActivity.this, BLEActivity.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.bt_device:
			intent = new Intent(MainActivity.this, BLEActivity.class);
			startActivityForResult(intent, 0);
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("MainActivity", "onPause()");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Log.e("BlueManager","on Resume");
		systime = MissionSingleInstance.getSingleInstance().getSystemtime();

		if (BlueManager.getInstance().isConnect())
			updateConnectionState(getText(R.string.bluetooth_is_connected).toString());
		else if (!BlueManager.getInstance().isConnect())
			updateConnectionState(getText(R.string.bluetooth_is_disconnected).toString());

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Toast.makeText(this,, Toast.LENGTH_SHORT).show();
		// 从意图获取显示的蓝牙信息

		try {
			if (requestCode == 0) {
				mDeviceName = data
						.getStringExtra(BLEActivity.EXTRAS_DEVICE_NAME);
				mDeviceAddress = data
						.getStringExtra(BLEActivity.EXTRAS_DEVICE_ADDRESS);
				mRssi = data.getStringExtra(BLEActivity.EXTRAS_DEVICE_RSSI);
				// Toast.makeText(this,mDeviceName+":"+mDeviceAddress,
				// Toast.LENGTH_SHORT).show();
				BlueManager.getInstance().setMode(Config.MODE_LE);
				BlueManager.getInstance().reset(mDeviceAddress);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 顶部连接状态
	private void updateConnectionState(String status) {
		tv_deviceName.setText(mDeviceName);
		tv_deviceStatus.setText(status);
	}

	class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(1000);
					Message msg = new Message();
					msg.what = 0x003; // 消息(一个整型值)
					handler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}
}
