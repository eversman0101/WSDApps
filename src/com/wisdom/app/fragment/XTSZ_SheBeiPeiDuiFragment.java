package com.wisdom.app.fragment;

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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.wisdom.app.activity.R;
import com.wisdom.app.utils.BluetoothChatUtil;
import com.wisdom.layout.TitleLayout;
/**
 * @author JinJingYun
 * ϵͳ����-�豸���
 * */
public class XTSZ_SheBeiPeiDuiFragment extends Fragment{
	private TitleLayout title;
	private String TAG="XTSZ_SheBeiPeiDuiFragment";
	private Button switchBtn;// ��������
	private Button searchBtn;// ���������豸
	private Button disConnectBtn;// �Ͽ�����
	private TextView tvDeviceAddress;
	private TextView tvDeviceConnectStatus;

	public ListView listView;// �����豸�嵥
	public ArrayAdapter<String> adapter;
	public ArrayList<String> list = new ArrayList<String>();
	private Set<BluetoothDevice> bondDevices;

	private BluetoothDeviceListAdapter mDeviceListAdapter;

	private BluetoothGatt mBluetoothGatt;

	/*
	 * ����������
	 */
	private BluetoothAdapter mBluetoothAdapter;
	private Context mContext;
	private BluetoothChatUtil mBlthChatUtil;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothChatUtil.STATE_CONNECTED:
				String deviceName = msg.getData().getString(BluetoothChatUtil.DEVICE_NAME);
				tvDeviceConnectStatus.setText("�ѳɹ����ӵ��豸:" + deviceName);

				break;
			case BluetoothChatUtil.STATAE_CONNECT_FAILURE:

				Toast.makeText(getActivity(), "����ʧ��", Toast.LENGTH_SHORT).show();
				break;
			case BluetoothChatUtil.MESSAGE_DISCONNECTED:

				tvDeviceConnectStatus.setText("���豸�Ͽ�����");
				break;
			case BluetoothChatUtil.MESSAGE_READ: {
				byte[] buf = msg.getData().getByteArray(BluetoothChatUtil.READ_MSG);
				String str = new String(buf, 0, buf.length);
				Toast.makeText(getActivity(), "���ɹ�" + str, Toast.LENGTH_SHORT).show();

				// mTvChat.setText(mTvChat.getText().toString()+"\n"+str);
				break;
			}
			case BluetoothChatUtil.MESSAGE_WRITE: {
				byte[] buf = (byte[]) msg.obj;
				String str = new String(buf, 0, buf.length);
				Toast.makeText(getActivity(), "���ͳɹ�" + str, Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		}

	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");
		initBluetooth();
		mBlthChatUtil = BluetoothChatUtil.getInstance(mContext);
		mBlthChatUtil.registerHandler(mHandler);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_sys_setting_shebei_peidui, null);
		title=(TitleLayout)view.findViewById(R.id.title_xtsz_shebeipeidui);
        title.setTitleText("ϵͳ����->�豸���");
        Log.d(TAG, "onCreateView()");
		initView(view);
		
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
		disConnectBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mBlthChatUtil != null) {
					if (mBlthChatUtil.getState() == BluetoothChatUtil.STATE_CONNECTED)
					{
						mBlthChatUtil.disconnect();
					}
				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(mBluetoothAdapter.isDiscovering())
					mBluetoothAdapter.cancelDiscovery(); 
				/*if (mBlthChatUtil != null) {
					if (mBlthChatUtil.getState() == BluetoothChatUtil.STATE_CONNECTED)
					{
						mBlthChatUtil.disconnect();
					}
				}*/
				String address = mDeviceListAdapter.getDevice(position).getAddress();
				String name=mDeviceListAdapter.getDevice(position).getName();
				tvDeviceAddress.setText(name);
				tvDeviceConnectStatus.setText(getResources().getString(R.string.listening));

				BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(address);

				try {
					mBlthChatUtil.connect(remoteDevice);
					//BluetoothUtils.cancelPairingUserInput(BluetoothDevice.class, remoteDevice);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		return view;
	}
	
	private void initView(View view) {
		switchBtn = (Button) view.findViewById(R.id.bt_switch);
		searchBtn = (Button) view.findViewById(R.id.search_device);
		disConnectBtn = (Button) view.findViewById(R.id.disconnect_device);

		tvDeviceAddress = (TextView) view.findViewById(R.id.bt_device_name);
		tvDeviceConnectStatus = (TextView) view.findViewById(R.id.bt_device_status);

		listView = (ListView) view.findViewById(R.id.list);
		// adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1,list);
		// listView.setAdapter(adapter);

		mDeviceListAdapter = new BluetoothDeviceListAdapter();
		// ��ʼ���������Լ����ø�listView��������
		listView.setAdapter(mDeviceListAdapter);
	}

	private void initBluetooth() {
		mContext = getActivity();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// ע��㲥�����ź�
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������
		// ÿ��ɨ��ģʽ�仯��ʱ��Ӧ�ó������Ϊͨ��ACTION_SCAN_MODE_CHANGEDֵ������ȫ�ֵ���Ϣ֪ͨ�����磬���豸ֹͣ����Ѱ�Ժ󣬸���Ϣ���Ա�ϵͳ֪ͨ�oӦ�ó���
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		// ÿ������ģ�鱻�򿪻��߹رգ�Ӧ�ó������Ϊͨ��ACTION_STATE_CHANGEDֵ������ȫ�ֵ���Ϣ֪ͨ��
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		mContext.registerReceiver(searchDevices, intent);
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
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		mBlthChatUtil = null;
		mContext.unregisterReceiver(searchDevices);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "onPause()");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume()");
		if (mBlthChatUtil != null) {
			if (mBlthChatUtil.getState() == BluetoothChatUtil.STATE_CONNECTED)
			{
				BluetoothDevice device = mBlthChatUtil.getConnectedDevice();
				if(null != device && null != device.getName()){
					tvDeviceAddress.setText(device.getName());
					tvDeviceConnectStatus.setText("�ѳɹ����ӵ��豸" + device.getName());
				}else {
					tvDeviceConnectStatus.setText("�ѳɹ����ӵ��豸");
				}
			}
			else
			{
				tvDeviceConnectStatus.setText("δ����");
			}
		}
	}

	private class BluetoothDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private ArrayList<String> mIsPaired;
		private LayoutInflater mInflator;
		
		public BluetoothDeviceListAdapter()
		{
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mIsPaired = new ArrayList<String>();
			mInflator = getActivity().getLayoutInflater();
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
			View layout = View.inflate(getActivity(), R.layout.abc_activity_list_view_item, null);
			
			TextView deviceName = (TextView) layout.findViewById(R.id.bt_device_name);
			TextView deviceStatus = (TextView) layout.findViewById(R.id.bt_device_status);
			
			BluetoothDevice device = mLeDevices.get(position);
			deviceName.setText(device.getName() + "\r\n" + device.getAddress());
			deviceStatus.setText(mIsPaired.get(position));
			
			return layout;
		}
	}
}
