package com.wisdom.app.activity;

import java.util.List;
import java.util.UUID;

import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.BluetoothLeService;
import com.wisdom.app.utils.ByteUtil;
import com.wisdom.app.utils.DataService;
import com.wisdom.app.utils.UnpackFrame;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DataCheckActivity extends Activity {
	private String TAG = "DataCheckActivity";
	private Button btn_clock;
	private Button btn_chaobiao1;
	private Button btn_taitishuchu;
	private Button btn_taiticeliang;
	private Button btn_taititingzhi;
	
	//收发数据
	private UnpackFrame uf=new UnpackFrame();
	private BluetoothGattCharacteristic characteristic;
	private ALTEK altek = new ALTEK();
	private DataService ds = new DataService();
	int[] iTempBuf = new int[10];
	int iTxLen = 0;
	int iStart = 0, iEnd = 0, iLen = 0, i = 0;
	String strCurTx = null;
	String str = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_check);
		btn_clock = (Button) findViewById(R.id.btn_clock);
		btn_chaobiao1=(Button) findViewById(R.id.btn_chaobiao1);
		btn_taitishuchu=(Button) findViewById(R.id.btn_taitishuchu);
		btn_taiticeliang=(Button) findViewById(R.id.btn_taiticeliang);
		btn_taititingzhi=(Button)findViewById(R.id.btn_taititingzhi);
		try{
			BluetoothGattService service = MainActivity.mBluetoothLeService.getGattService(BLEActivity.SERVICE_UUID);
			characteristic = service.getCharacteristic(UUID.fromString(BLEActivity.HEART_RATE_MEASUREMENT));
			MainActivity.mBluetoothLeService.setCharacteristicNotification(characteristic);
		}catch(Exception ex)
		{
			Toast.makeText(this,"请连接蓝牙设备", Toast.LENGTH_SHORT).show();
		}
		btn_clock.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// characteristic.setValue(altek.fnGetFrameByFunctionCode((byte)0x61));
				// MainActivity.mBluetoothLeService.writeCharacteristic(characteristic);
				fnSendBytes(altek.fnGetFrameByFunctionCode((byte)0x06));
				//Log.i(TAG, "数据装填：" + altek.fnGetFrameByFunctionCode((byte) 0x61));
			}
		});
		btn_chaobiao1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fnSendBytes(altek.fnChaoBiao(1));
			}
		});
		btn_taitishuchu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fnSendBytes(altek.fnTaitiShuChu(110, 10, 0,0, 0, 0,0, 0, 0, 0, 0, 0, 0));
			}
		});
		btn_taiticeliang.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fnSendBytes(altek.fnGetFrameByFunctionCode((byte)0x61));
			}
		});
		btn_taititingzhi.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fnSendBytes(altek.fnTaitiShuChu(0, 0, 0, 0, 0,0, 0, 0, 0, 0, 0, 0, 0));
			}
		});
	}

	private void fnSendBytes(byte[] frame) {
		try
		{
			iTxLen = 0;
			str = ByteUtil.byte2HexStr(frame);
			str = str.toUpperCase();
			iLen = str.length();
			iStart = 0;
			iEnd = 0;
			// 一次最多发送20字节，分开发送
			for (i = iStart; i < iLen;) {
				if (iLen < (i + 40)) {
					strCurTx = str.substring(i, iLen);
				} else {
					strCurTx = str.substring(i, i + 40);
				}
				if (characteristic != null) {
					characteristic.setValue(ByteUtil.hexStringToByte(strCurTx));
					MainActivity.mBluetoothLeService.writeCharacteristic(characteristic);
				}
				i += 40;
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (i > iLen)
					break;
				else
					continue;
			}
		}catch(Exception ex)
		{
			Toast.makeText(this,"发送失败，连接断开",Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 这个是真的广播接收器
	 */
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		String strRx = null, str2 = null;
		int iRxDataLen = 0;

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// 连接成功更新界面顶部字体
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))// Gatt连接成功
			{

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))// Gatt连接失败
			{

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))// 发现GATT服务器
			{
			
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))// 有效数据
			{
				// 处理发送过来的数据
				strRx = intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
				// displayData(strRx); //接收报文区域//接收处理
				iRxDataLen = strRx.length();
				List<String> list = ds.fnGetData(strRx, iRxDataLen);
				if(list!=null)
					Toast.makeText(DataCheckActivity.this,list.get(0),Toast.LENGTH_SHORT).show();
				/*if (frame != null)
				{
					if((frame[2]&0x7f)==0x06)
					{
						 Toast.makeText(DataCheckActivity.this,uf.fnGetClock(frame),Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(DataCheckActivity.this, ByteUtil.BinaryToHexString(frame), Toast.LENGTH_LONG).show();
					}
				}*/
				//Toast.makeText(DataCheckActivity.this, ByteUtil.BinaryToHexString(frame), Toast.LENGTH_LONG).show();
				
				
				// 
				// Toast.makeText(DataCheckActivity.this,strRx,Toast.LENGTH_SHORT).show();
				// Toast.makeText(DataCheckActivity.this,uf.fnGetClock(ByteUtil.hexStr2Bytes(strRx)),Toast.LENGTH_SHORT).show();
				// int[] idata=ByteUtil.hexStringToInt(strRx);

			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mGattUpdateReceiver);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
}
