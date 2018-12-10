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
 * @version ����ʱ�䣺2017-11-14 ����2:01:31 
 * ��˵����
 */
public class SheBeiPeiDuiActivity extends Activity {
	private Button switchBtn;//�������� 
	private Button searchBtn;//���������豸 
	private Button disConnectBtn;//�Ͽ�����
	private TextView tvDeviceAddress;
	private TextView tvDeviceConnectStatus;
	
	public ListView listView;//�����豸�嵥 
	private Set<BluetoothDevice> bondDevices;
    
    private BluetoothDeviceListAdapter mDeviceListAdapter;
    
    private BluetoothGatt mBluetoothGatt;
    
	/*
	 * ����������
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
		//��ʼ���������Լ����ø�listView��������
		listView.setAdapter(mDeviceListAdapter);
		
		//ע��㲥�����ź�
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������
		//ÿ��ɨ��ģʽ�仯��ʱ��Ӧ�ó������Ϊͨ��ACTION_SCAN_MODE_CHANGEDֵ������ȫ�ֵ���Ϣ֪ͨ�����磬���豸ֹͣ����Ѱ�Ժ󣬸���Ϣ���Ա�ϵͳ֪ͨ�oӦ�ó���
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); 
		//ÿ������ģ�鱻�򿪻��߹رգ�Ӧ�ó������Ϊͨ��ACTION_STATE_CHANGEDֵ������ȫ�ֵ���Ϣ֪ͨ��
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(searchDevices, intent); 
		
		//���������豸
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
		
		//��ʾ������豸�Լ�����δ����豸
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
			    
			    //����Զ���豸
			    mBluetoothGatt = remoteDevice.connectGatt(context, false, mGattCallback);
			}
		});
    }

	/* ����Զ���豸�Ļص����� */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
	{

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED)//���ӳɹ�
			{
//				tvDeviceConnectStatus.setText(getResources().getString(R.string.connected));
				tvDeviceConnectStatus.setText("������");
//				intentAction = ACTION_GATT_CONNECTED;
//				mConnectionState = STATE_CONNECTED;
//				/* ͨ���㲥��������״̬ */
//				broadcastUpdate(intentAction);
				String TAG = "BluetoothLeService";
				Log.i(TAG, "Connected to GATT server.");
				// Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:"+ mBluetoothGatt.discoverServices());
			}
			else if (newState == BluetoothProfile.STATE_DISCONNECTED)//����ʧ��
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

		//����˵�����õ����е�һ�����֣�Ȼ����ݲ�ͬ����ֵ�������Ҫ��listView��ÿһ��item
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//�õ�ListViewItem�Ĳ��֣�ת��ΪView���͵Ķ���
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
