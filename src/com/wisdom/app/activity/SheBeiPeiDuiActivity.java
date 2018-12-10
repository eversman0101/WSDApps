package com.wisdom.app.activity;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/** 
 * @author wisdom's JiangYuPeng
 * @version 创建时间：2017-11-14 下午2:01:31 
 * 类说明：
 */
public class SheBeiPeiDuiActivity extends Activity {
	private Button switchBtn;//蓝牙开关 
	private Button searchBtn;//搜索蓝牙设备 
	private Button disConnectBtn;//断开连接
	private TextView tvDeviceAddress;
	private TextView tvDeviceConnectStatus;
	
	public ListView listView;//蓝牙设备清单 
	private Set<BluetoothDevice> bondDevices;
    
    private BluetoothDeviceListAdapter mDeviceListAdapter;
    
    private BluetoothGatt mBluetoothGatt;
    
	/*
	 * 蓝牙适配器
	 */
	private BluetoothAdapter mBluetoothAdapter;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_sys_setting_shebei_peidui);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		switchBtn = (Button) findViewById(R.id.bt_switch);
        searchBtn = (Button) findViewById(R.id.search_device);
        disConnectBtn = (Button) findViewById(R.id.disconnect_device);
        
        tvDeviceAddress = (TextView) findViewById(R.id.bt_device_name);
        tvDeviceConnectStatus = (TextView) findViewById(R.id.bt_device_status);
        
        listView = (ListView)findViewById(R.id.list);
		context = getApplicationContext();
		
		mDeviceListAdapter = new BluetoothDeviceListAdapter();
		//初始化适配器以及设置该listView的适配器
		listView.setAdapter(mDeviceListAdapter);
		
		//注册广播接收信号
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
		//每当扫描模式变化的时候，应用程序可以为通过ACTION_SCAN_MODE_CHANGED值来监听全局的消息通知。比如，当设备停止被搜寻以后，该消息可以被系统通知給应用程序。
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); 
		//每当蓝牙模块被打开或者关闭，应用程序可以为通过ACTION_STATE_CHANGED值来监听全局的消息通知。
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(searchDevices, intent); 
		
		//开关蓝牙设备
		switchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mBluetoothAdapter.isEnabled())
				{
					mBluetoothAdapter.disable();
					mDeviceListAdapter.clear();
					mDeviceListAdapter.notifyDataSetChanged();
				}
				else
				{
					mBluetoothAdapter.enable();
//					if(mBluetoothAdapter.isDiscovering()){
//						mBluetoothAdapter.cancelDiscovery();
//					}
//					mBluetoothAdapter.startDiscovery();
				}
			}
		});
		
		//显示已配对设备以及搜索未配对设备
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mBluetoothAdapter.isDiscovering()){
					mBluetoothAdapter.cancelDiscovery();
				}
				mDeviceListAdapter.clear();
				mDeviceListAdapter.notifyDataSetChanged();
				bondDevices = mBluetoothAdapter.getBondedDevices();
				for(BluetoothDevice device : bondDevices) {
					mDeviceListAdapter.addDevice(device,getResources().getString(R.string.paired));
				}
				mDeviceListAdapter.notifyDataSetChanged();
				mBluetoothAdapter.startDiscovery();
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mBluetoothAdapter.isDiscovering())
					mBluetoothAdapter.cancelDiscovery(); 
				
			    String address = mDeviceListAdapter.getDevice(position).getAddress();
			    tvDeviceAddress.setText(address);
			    tvDeviceConnectStatus.setText(getResources().getString(R.string.listening));
			    
			    BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(address);
			    
//			    try {
//					BluetoothUtils.createBond(BluetoothDevice.class, remoteDevice);
//					BluetoothUtils.pair(address, "1234");
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			    
			    //连接远端设备
			    mBluetoothGatt = remoteDevice.connectGatt(context, false, mGattCallback);
			}
		});
    }

	/* 连接远程设备的回调函数 */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
	{

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED)//连接成功
			{
//				tvDeviceConnectStatus.setText(getResources().getString(R.string.connected));
				tvDeviceConnectStatus.setText("已连接");
//				intentAction = ACTION_GATT_CONNECTED;
//				mConnectionState = STATE_CONNECTED;
//				/* 通过广播更新连接状态 */
//				broadcastUpdate(intentAction);
				String TAG = "BluetoothLeService";
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:"+ mBluetoothGatt.discoverServices());
			}
			else if (newState == BluetoothProfile.STATE_DISCONNECTED)//连接失败
			{
				tvDeviceConnectStatus.setText(getResources().getString(R.string.connect_error));
//				intentAction = ACTION_GATT_DISCONNECTED;
//				mConnectionState = STATE_DISCONNECTED;
				String TAG = "BluetoothLeService";
				Log.i(TAG, "Disconnected from GATT server.");
//				broadcastUpdate(intentAction);
			}
		}
		
	};
    
    private final BroadcastReceiver searchDevices = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			BluetoothDevice device = null;
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
				if (device.getBondState() == BluetoothDevice.BOND_NONE) {
					mDeviceListAdapter.addDevice(device,getResources().getString(R.string.unpaired));
				}
				mDeviceListAdapter.notifyDataSetChanged();
			}
		}
	};
	
	private class BluetoothDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private ArrayList<String> mIsPaired;
		private LayoutInflater mInflator;
		
		public BluetoothDeviceListAdapter()
		{
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mIsPaired = new ArrayList<String>();
			mInflator = getLayoutInflater();
		}
		public void addDevice(BluetoothDevice device,String isPaired)
		{
			if (!mLeDevices.contains(device))
			{
				mLeDevices.add(device);
				mIsPaired.add(isPaired);
			}
		}
		public BluetoothDevice getDevice(int position)
		{
			return mLeDevices.get(position);
		}
		public void clear()
		{
			mLeDevices.clear();
			mIsPaired.clear();
		}
		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return mLeDevices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		//简单来说就是拿到单行的一个布局，然后根据不同的数值，填充主要的listView的每一个item
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//拿到ListViewItem的布局，转换为View类型的对象
			View layout = View.inflate(SheBeiPeiDuiActivity.this, R.layout.abc_activity_list_view_item, null);
			
			TextView deviceName = (TextView) layout.findViewById(R.id.bt_device_name);
			TextView deviceStatus = (TextView) layout.findViewById(R.id.bt_device_status);
			
			BluetoothDevice device = mLeDevices.get(position);
			deviceName.setText(device.getName() + "\r\n" + device.getAddress());
			deviceStatus.setText(mIsPaired.get(position));
			
			return layout;
		}
	}
}
