package com.wisdom.app.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.wisdom.app.activity.R;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.Blue;
import com.wisdom.app.utils.Comm;
import com.wisdom.app.utils.CustomerValueFormatter;
import com.wisdom.app.utils.DataService;
import com.wisdom.bean.XieBoDuquBean;
import com.wisdom.layout.TitleLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * @author JinJingYun �鸺���ֶ�У��-г������
 */
public class MCL_XieBoFenXiFragment extends Fragment {
	private String TAG="MCNL_XieBoFenXiFragment";
	@Bind(R.id.title_MCNL_xiebo_fenxi)
	TitleLayout title;
	@Bind(R.id.barChart)
	BarChart chart;
	@Bind(R.id.thdu)
	TextView tv_thdu;
	@Bind(R.id.thdi)
	TextView tv_thdi;
	@Bind(R.id.tr_btn)
	TableRow tr_btn;
	// �շ�����
	private BluetoothGattCharacteristic characteristic;
	private ALTEK altek = new ALTEK();
	private DataService ds = new DataService();
	int[] iTempBuf = new int[10];
	int iTxLen = 0;
	int iStart = 0, iEnd = 0, iLen = 0, i = 0;
	String strCurTx = null;
	String str = null;
	private XieBoDuquBean bean;
	private ArrayList<String> xvalues;
	int index=0;
	private Timer timer;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x001) {
				try {
					bean = (XieBoDuquBean) msg.obj;
					if (bean != null) {
						tv_thdu.setText(String.valueOf(bean.getTotalUhanliang()));
						tv_thdi.setText(String.valueOf(bean.getTotalIhanliang()));
						showBarChart(chart, xvalues, getYU(bean),getYI(bean));
						if(index==0)
						{
						Matrix m = new Matrix();
						m.postScale(3.5f, 20f);// ���������ֱ���x,y������ű��������磺��x������ݷŴ�Ϊ֮ǰ��1.5��
						chart.getViewPortHandler().refresh(m, chart, false);// ��ͼ��������ʾ֮ǰ��������

						//chart.animateX(0); // ����ִ�еĶ���,x��
						index++;
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (msg.what == 0x002) {
				Toast.makeText(getActivity(),getText(R.string.toast_cmd_send),Toast.LENGTH_SHORT).show();;
				
			} else if (msg.what == 0x003) {

			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mcnl_xiebo_fenxi, null);
		ButterKnife.bind(this, view);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_real_load_placeholder).toString(),getText(R.string.harmonic_analysis).toString()));
		
		initData();
		xvalues = getXvalues();
		initChart(chart);
		// mBarData = getBarData(4, 100);

		return view;
	}

	private void initView() {
		
	}

	private void initData() {
		try
		{
	/*	BluetoothGattService service = MainActivity.mBluetoothLeService.getGattService(BLEActivity.SERVICE_UUID);
		characteristic = service.getCharacteristic(UUID.fromString(BLEActivity.HEART_RATE_MEASUREMENT));
		MainActivity.mBluetoothLeService.setCharacteristicNotification(characteristic);
		getActivity().registerReceiver(mGattUpdateReceiver, MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
		*/
		tr_btn.setVisibility(View.GONE);
	
		Comm.getInstance().init(handler);
		Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte)0x7D);
		
		//timer=new Timer();
		//timer.schedule(new SendDataTask(), 1000, 1000);
		//fnSendBytes(altek.fnGetFrameByFunctionCode((byte)0x7D));
		}catch(Exception ex)
		{
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}

	private void showBarChart(BarChart barChart, ArrayList<String> xValues, ArrayList<BarEntry> yU,
			ArrayList<BarEntry> yI) {
		ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
		try {
			BarDataSet barDataSet1 = new BarDataSet(yU, getText(R.string.voltage_content_rate).toString());

			BarDataSet barDataSet2 = new BarDataSet(yI, getText(R.string.current_content_rate).toString());
			barDataSet1.setBarShadowColor(Color.parseColor("#00000000"));//��״ͼ����͸��
			
			barDataSet2.setBarShadowColor(Color.parseColor("#00000000"));

			barDataSet1.setColor(Color.rgb(114, 188, 223));
			barDataSet2.setColor(Color.parseColor("#ff6666"));
			barDataSets.add(barDataSet1);
			barDataSets.add(barDataSet2);

			BarData barData = new BarData(xValues, barDataSets);
			barData.setValueFormatter(new CustomerValueFormatter());
			barChart.clear();
			barChart.setData(barData); // ��������
			//barChart.moveViewToX(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
private void initChart(BarChart barChart)
{
	barChart.setDrawBorders(false); //// �Ƿ�������ͼ�����ӱ߿�

	barChart.setDescription("");// ��������

	// ���û�����ݵ�ʱ�򣬻���ʾ���������ListView��EmptyView
	barChart.setNoDataTextDescription("You need to provide data for the chart.");

	barChart.setDrawGridBackground(false); // �Ƿ���ʾ������ɫ
	barChart.setGridBackgroundColor(Color.WHITE); // ����ĵ���ɫ�����������Ǹ���ɫ����һ��͸����& 0x70FFFFFF
	barChart.setBorderColor(Color.WHITE);
	barChart.setTouchEnabled(true); // �����Ƿ���Դ���

	barChart.setDragEnabled(true);// �Ƿ������ק
	barChart.setScaleEnabled(true);// �Ƿ��������

	barChart.setPinchZoom(false);//

	barChart.setBackgroundColor(Color.WHITE);// ���ñ���

	barChart.setDrawBarShadow(true);


	Legend mLegend = barChart.getLegend(); // ���ñ���ͼ��ʾ

	mLegend.setForm(LegendForm.CIRCLE);// ��ʽ
	mLegend.setFormSize(6f);// ����
	mLegend.setTextColor(Color.BLACK);// ��ɫ

	// X���趨
	// XAxis xAxis = barChart.getXAxis();
	// xAxis.setPosition(XAxisPosition.BOTTOM);
	// ͼ����ߵ�y��������
	YAxis leftAxis = chart.getAxisLeft();
	leftAxis.setTextColor(Color.BLACK);

	// ���ֵ
	leftAxis.setAxisMaxValue(100.0f);

	// ��Сֵ
	leftAxis.setAxisMinValue(0.0f);

	// ��һ��Ҫ��0��ʼ
	leftAxis.setStartAtZero(false);

	leftAxis.setDrawGridLines(true);
	leftAxis.setLabelCount(5, true);
	YAxis rightAxis = chart.getAxisRight();
	//rightAxis.setAxisMinValue(10);
	//rightAxis.setSpaceBottom(30f);
	// ����ʾͼ�����ұ�y��������
	rightAxis.setEnabled(false);
	barChart.animateX(0); // ����ִ�еĶ���,x��
	// barChart.setData(bardata);
	}
	private ArrayList<BarEntry> getYU(XieBoDuquBean bean) {
		ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
		Vector vectorU=bean.getuHanliang();
		if(vectorU!=null)
		{
			for(int i=0;i<vectorU.size();i++)
			{
				
				Log.i(TAG, "��ѹг����"+String.valueOf(vectorU.get(i)));
				if(i==0)
				yValues.add(new BarEntry(Float.parseFloat(String.valueOf(vectorU.get(i))), i));
				if(i>0)
				yValues.add(new BarEntry(Float.parseFloat(String.valueOf(vectorU.get(i))), i));
				
			}
		}
		
		return yValues;
	}

	private ArrayList<BarEntry> getYI(XieBoDuquBean bean) {
		ArrayList<BarEntry> yi = new ArrayList<BarEntry>();
		Vector vectorI=bean.getiHanliang();
		if(vectorI!=null)
		{
			for(int i=0;i<vectorI.size();i++)
			{
				Log.i(TAG, "����г����"+String.valueOf(vectorI.get(i)));
				if(i==0)
				yi.add(new BarEntry(Float.parseFloat(String.valueOf(vectorI.get(i))), i));
				if(i>0)
				yi.add(new BarEntry(Float.parseFloat(String.valueOf(vectorI.get(i))), i));
					
			}
		}
		return yi;
	}

	private ArrayList<String> getXvalues() {
		ArrayList<String> xValues = new ArrayList<String>();
		for (int i = 0; i < 23; i++) {
			xValues.add((i + 1) + getText(R.string.ci_harmonic).toString());
		}
		/*
		 * ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
		 * 
		 * for (int i = 0; i < count; i++) { float value = (float)
		 * (Math.random() * range100���ڵ������) + 3; yValues.add(new BarEntry(value,
		 * i)); }
		 * 
		 * // y������ݼ��� BarDataSet barDataSet = new BarDataSet(yValues, "���Ա�״ͼ");
		 * 
		 * barDataSet.setColor(Color.rgb(114, 188, 223));
		 * 
		 * ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
		 * barDataSets.add(barDataSet); // add the datasets
		 */

		return xValues;
	}
/*	private void fnSendBytes(byte[] frame) {
		iTxLen = 0;
		str = ByteUtil.byte2HexStr(frame);
		str = str.toUpperCase();
		iLen = str.length();
		iStart = 0;
		iEnd = 0;
		// һ����෢��20�ֽڣ��ֿ�����
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
	}*/
	/**
	 * �������Ĺ㲥������
	 */
/*	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		String strRx = null, str2 = null;
		int iRxDataLen = 0;

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			// ���ӳɹ����½��涥������
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))// Gatt���ӳɹ�
			{

			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))// Gatt����ʧ��
			{

			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action))// ����GATT������
			{
			
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))// ��Ч����
			{
				// �������͹���������
				strRx = intent.getExtras().getString(BluetoothLeService.EXTRA_DATA);
				// displayData(strRx); //���ձ�������//���մ���
				iRxDataLen = strRx.length();
				byte[] data=ds.fnReceiveData(strRx, iRxDataLen);
				if(data!=null)
				{
					if((data[2]&0x7f)==0x7D)//г����ȡ
		    		{
		    			XieBoDuquBean bean=ds.fnGetXieBoData(data);
		    			if(bean!=null)
						{
							Message msg = new Message();
							msg.what = 0x001;
							msg.obj = bean;
							handler.sendMessage(msg);
						}
		    		}
				}
			}
		}
	};*/
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			
		//	getActivity().unregisterReceiver(mGattUpdateReceiver);
			timer.cancel();
			}catch(Exception ex)
			{
				
			}
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) { 
			// ������ǰ����ʾ �൱�ڵ�����onPause();
			Log.i(TAG,"onHidden()");
			try{
				//getActivity().unregisterReceiver(mGattUpdateReceiver);
				timer.cancel();
				}catch(Exception ex)
				{
					
				}
            return;
        }else{  // ����ǰ����ʾ �൱�ڵ�����onResume();
        	Log.i(TAG,"onHiddenShow()");
        	Comm.getInstance().init(handler);
        	Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte)0x7D);
        
        //	getActivity().registerReceiver(mGattUpdateReceiver, MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
        	//timer=new Timer();
    		//timer.schedule(new SendDataTask(), 1000, 1000);
        }
	}
	private class SendDataTask extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Blue.send(altek.fnGetFrameByFunctionCode((byte)0x7D),handler);
			
		}
 }
}