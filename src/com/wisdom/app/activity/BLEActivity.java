package com.wisdom.app.activity;

import java.util.ArrayList;

import com.sunday.slidetabfragment.blue.BlueManager;
import com.sunday.slidetabfragment.blue.ProtocolSignALTEK;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.UnpackFrame;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BLEActivity extends Activity implements OnClickListener {
	// 扫描蓝牙按钮
	private Button scan_btn;
	private Button btn_disconnect;
	private Button btn_disc;
	// 蓝牙适配器
	BluetoothAdapter mBluetoothAdapter;
	// 蓝牙信号强度
	private ArrayList<Integer> rssis;
	// 自定义Adapter
	LeDeviceListAdapter mleDeviceListAdapter;
	// listview显示扫描到的蓝牙信息
	ListView lv;
	// 描述扫描蓝牙的状态
	private boolean mScanning;
	private boolean scan_flag;
	private Handler mHandler;
	int REQUEST_ENABLE_BT = 1;
	// 蓝牙扫描时间
	private static final long SCAN_PERIOD = 10000;

	private Intent intent;
	// sevice uuid
	public static String SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
	// characteristic uuid
	public static String HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public static String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	public static String EXTRAS_DEVICE_RSSI = "RSSI";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ble);

		// 初始化控件
		init();
		// 初始化蓝牙
		init_ble();
		scan_flag = true;
		// 自定义适配器
		mleDeviceListAdapter = new LeDeviceListAdapter();
		// 为listview指定适配器
		lv.setAdapter(mleDeviceListAdapter);

		// listview点击函数
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				// TODO Auto-generated method stub
				final BluetoothDevice device = mleDeviceListAdapter.getDevice(position);
				if (device == null)
					return;
				Intent intent = new Intent();
				intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
				intent.putExtra(EXTRAS_DEVICE_ADDRESS, device.getAddress());
				intent.putExtra(EXTRAS_DEVICE_RSSI, rssis.get(position).toString());
				if (mScanning) {
					// 停止扫描设备
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mScanning = false;
				}
				try {
					setResult(1, intent);
					BLEActivity.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btn_disconnect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ALTEK altek = new ALTEK();
				final byte[] data = altek.fnGetFrameByFunctionCode((byte) 0x06);
				new Thread() {
					@Override
					public void run() {
						BlueManager.getInstance().write(data);
						byte[] recv = BlueManager.getInstance().read(ProtocolSignALTEK.getInstance());
						if (recv == null)
							Log.i("BLEActivity", "recv is null");
						else {
							UnpackFrame uf = new UnpackFrame();
							final String str = uf.fnGetClock(recv);
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(BLEActivity.this, str, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				}.start();
				// BlueManager.getInstance().close(true,true);
			}
		});
		btn_disc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			if(BlueManager.getInstance().isConnect())
				BlueManager.getInstance().close();
			Toast.makeText(BLEActivity.this,getText(R.string.ble_msg_disconnected).toString(),Toast.LENGTH_SHORT).show();
			}
		});

	}

	/**
	 * @Title: init @Description: TODO(初始化UI控件) @param 无 @return void @throws
	 */
	private void init() {
		btn_disconnect = (Button) this.findViewById(R.id.btn_disconnect);
		btn_disconnect.setVisibility(View.GONE);
		scan_btn = (Button) this.findViewById(R.id.scan_dev_btn);
		scan_btn.setOnClickListener(this);
		lv = (ListView) this.findViewById(R.id.lv);
		btn_disc=(Button)this.findViewById(R.id.btn_disc);
		mHandler = new Handler();
	}

	/**
	 * @Title: init_ble @Description: TODO(初始化蓝牙) @param 无 @return void @throws
	 */
	private void init_ble() {
		// 手机硬件支持蓝牙// 使用此检查确定BLE是否支持在设备上，然后你可以有选择性禁用BLE相关的功能
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, getText(R.string.ble_msg_not_support_ble).toString(), Toast.LENGTH_SHORT).show();
			finish();
		}
		// Initializes Bluetooth adapter.
		// 获取手机本地的蓝牙适配器
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 打开蓝牙权限
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}

	/*
	 * 按钮响应事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (scan_flag) {
			mleDeviceListAdapter = new LeDeviceListAdapter();
			lv.setAdapter(mleDeviceListAdapter);
			scanLeDevice(true);
		} else {
			scanLeDevice(false);
			scan_btn.setText(getText(R.string.search_equipment).toString());
		}
	}

	/**
	 * @Title: scanLeDevice @Description: TODO(扫描蓝牙设备 ) @param enable
	 * (扫描使能，true:扫描开始,false:扫描停止) @return void @throws
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					scan_flag = true;
					scan_btn.setText(getText(R.string.search_equipment).toString());
					Log.i("SCAN", "stop.....................");
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			/* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
			Log.i("SCAN", "begin.....................");
			mScanning = true;
			scan_flag = false;
			scan_btn.setText(getText(R.string.stop_searching).toString());
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			Log.i("Stop", "stoping................");
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			scan_flag = true;
		}
	}

	/**
	 * 蓝牙扫描回调函数 实现扫描蓝牙设备，回调蓝牙BluetoothDevice，可以获取name MAC等信息
	 * 
	 **/
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					/* 讲扫描到设备的信息输出到listview的适配器 */
					mleDeviceListAdapter.addDevice(device, rssi);
					mleDeviceListAdapter.notifyDataSetChanged();
				}
			});
			// System.out.println("Address:" + device.getAddress());
			// System.out.println("Name:" + device.getName());
			// System.out.println("rssi:" + rssi);
		}
	};

	/**
	 * @Description: TODO<自定义适配器Adapter,作为listview的适配器>
	 * @author 广州汇承信息科技有限公司
	 * @data: 2014-10-12 上午10:46:30
	 * @version: V1.0
	 */
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			rssis = new ArrayList<Integer>();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device, int rssi) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
				rssis.add(rssi);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
			rssis.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		/**
		 * 重写getview
		 * 
		 **/
		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			// General ListView optimization code.
			// 加载listview每一项的视图
			view = mInflator.inflate(R.layout.listitem, null);
			// 初始化三个textview显示蓝牙信息
			TextView deviceAddress = (TextView) view.findViewById(R.id.tv_deviceAddr);
			TextView deviceName = (TextView) view.findViewById(R.id.tv_deviceName);
			TextView rssi = (TextView) view.findViewById(R.id.tv_rssi);

			BluetoothDevice device = mLeDevices.get(i);
			deviceAddress.setText(device.getAddress());
			deviceName.setText(device.getName());
			rssi.setText("" + rssis.get(i));
			return view;
		}
	}

}
