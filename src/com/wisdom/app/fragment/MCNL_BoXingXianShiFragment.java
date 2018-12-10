package com.wisdom.app.fragment;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.wisdom.app.activity.BLEActivity;
import com.wisdom.app.activity.MainActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.Blue;
import com.wisdom.app.utils.Comm;
import com.wisdom.app.utils.DataService;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.bean.BoXingBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.layout.TitleLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author JinJingYun �鸺���ֶ�У��-������ʾ
 */
public class MCNL_BoXingXianShiFragment extends Fragment {
	private String TAG="MCNL_BoXingXianShiFragment";
	@Bind(R.id.title_MCNL_boxingxianshi)
	TitleLayout title;
	@Bind(R.id.lineChart1)
	LineChart chartU;
	@Bind(R.id.lineChart2)
	LineChart chartI;

	// ��ѹ������
	@Bind(R.id.chooser_u)
	TextView tv_chooser_u;// ��ѹ���

	@Bind(R.id.chooser_i)
	TextView tv_chooser_i;// �������

	@Bind(R.id.chooser_p)
	TextView tv_chooser_p;// ���
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
	private TaitiCeLiangShuJuBean bean;
	private ArrayList<String> xvalues;
	private boolean boxing_state=false;
	private boolean dianya_state=false;
	//������λ
	//0-0.25A��
	//1-1A��
	//2-5A��
	//3-20A��
	//4-100A��
	private int Iflag=0;
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x001) {
            	try
            	{
            	bean=(TaitiCeLiangShuJuBean) msg.obj;
            	if(bean!=null)
            	{
            		tv_chooser_u.setText(bean.getU());
            		tv_chooser_i.setText(bean.getI());
            		float f=Float.parseFloat(bean.getI());
            		modifyYIValues((short)f);
            		tv_chooser_f.setText(bean.getPinlv());
            		tv_show_p.setText(bean.getYougong());
            		tv_show_q.setText(bean.getWugong());
            		tv_show_s.setText(bean.getZonggong());
            		tv_show_pf.setText(bean.getGonglvyinshu());
            		tv_chooser_p.setText(MissionSingleInstance.getSingleInstance().getJiaodu());
            		boxing_state=true;
            		dianya_state=false;
            	}
            	}catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }
            else if(msg.what==0x002)
            {
            	try
            	{
            		BoXingBean boxingBean=(BoXingBean) msg.obj;
            		if(boxingBean!=null)
            		{
            			Log.i(TAG,"��ѹ����������������"+boxingBean.getU()+","+boxingBean.getI().length);
            			setLineDataSet(chartU,getYValueU(boxingBean),Color.BLUE,getText(R.string.unit)+":V");
            			setLineDataSet(chartI,getYValueI(boxingBean),Color.RED,getText(R.string.unit)+":A");
            			boxing_state=false;
                		dianya_state=true;
            		}
            	}catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }
            else if(msg.what==0x003)
            {
            	
            }
          
        }
    };
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mcnl_boxing_xianshi, null);
		ButterKnife.bind(this, view);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.waveform_display).toString()));
		
		initView();
		initData();
		return view;
	}
	private void initView() {
		initChartU();
		initChartI();
		xvalues = getXvalues();
	}
	private ArrayList<String> getXvalues()
	{
		DecimalFormat df = new DecimalFormat("0.000");  
		ArrayList<String> xValues = new ArrayList<String>();
		for (int i = 0; i < 512; i++) {
			double d=((double)40/512)*(i + 1);
			//xValues.add(df.format(d));
			xValues.add("");
		}
		return xValues;
	}
	private ArrayList<Entry> getYValueU(BoXingBean boxingBean)
	{
		ArrayList<Entry> entry=new ArrayList<Entry>();
		short[] data=boxingBean.getU();
		if(data==null)
			return null;
		for(int i=0;i<data.length;i++)
		{
			float value=data[i]*0.012f;
			entry.add(new Entry(value,i));
		}
       /* String[] bb = {"100","-50","-20","0","20","40","60"};  
        ArrayList<Entry> entry = new ArrayList<Entry>();  
  
        for (int i = 0; i < bb.length; i++) {  
        	entry.add(new Entry(Float.parseFloat(bb[i]), i));  
        }  */
		return entry;
	}
	private ArrayList<Entry> getYValueI(BoXingBean boxingBean)
	{
		ArrayList<Entry> entry=new ArrayList<Entry>();
		short[] data=boxingBean.getI();
		if(data==null)
			return null;
		for(int i=0;i<data.length;i++)
		{
			//��yʵ��ֵת��Ϊ����ֵ
			//ԭʼֵ��18022.125����Ӧ��λֵ��0.25A��1A��5A��20A��100A��
			float iValue=0;
			switch(Iflag)
			{
			case 0:
				iValue=data[i]/18022.125f*0.25f;
				break;
			case 1:
				iValue=data[i]/18022.125f*1.0f;
				break;
			case 2:
				iValue=data[i]/18022.125f*5f;
				break;
			case 3:
				iValue=data[i]/18022.125f*20f;
				break;
			case 4:
				iValue=data[i]/18022.125f*100f;
				break;
			default:
				break;
			}
			entry.add(new Entry(iValue,i));
		}
       /* String[] bb = {"100","-50","-20","0","20","40","60"};  
        ArrayList<Entry> entry = new ArrayList<Entry>();  
  
        for (int i = 0; i < bb.length; i++) {  
        	entry.add(new Entry(Float.parseFloat(bb[i]), i));  
        }  */
		return entry;
	}
	 /**
     * ��������
     * @param lineChart
     * @param lineData
     * @param color
     */
    private void setLineDataSet(LineChart lineChart,ArrayList<Entry> yValue,int color,String name) {
    	ArrayList<LineDataSet> barDataSets = new ArrayList<LineDataSet>();
    	
        LineDataSet lineDataSet = new LineDataSet(yValue, name);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setDrawCircles(false);
        //lineDataSet.setCircleSize(0.5f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setHighLightColor(color);
        barDataSets.add(lineDataSet);
        LineData lineData=new LineData(xvalues,barDataSets);
        //�����������
        lineDataSet.setDrawFilled(true);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(10f);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
	private void initData() {
		try
		{
	    dianya_state=true;
		Comm.getInstance().init(handler);
		
		Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
		Comm.getInstance().status_loop2=altek.fnGetFrameByFunctionCode((byte)0x68);
		//timer=new Timer();
		//timer.schedule(new SendDataTask(), 1000, 3000);
		}catch(Exception ex)
		{
			Toast.makeText(getActivity(), getText(R.string.toast_connect_device), Toast.LENGTH_SHORT).show();
		}
	}

	private void initChartU() {
			chartU.setDescription(getText(R.string.voltage_waveform).toString());
			chartU.setNoDataTextDescription(getText(R.string.no_data).toString());
			chartU.setTouchEnabled(true);

			// ����ҷ
			chartU.setDragEnabled(true);

			// ������
			chartU.setScaleEnabled(true);
			chartU.setDrawGridBackground(false);

			chartU.setPinchZoom(true);

			// ����ͼ��ı�����ɫ
			chartU.setBackgroundColor(Color.WHITE);

			LineData data = new LineData();
			
			// ������ʾ����ɫ
			data.setValueTextColor(Color.BLACK);

			// ������һ���յ����ݣ���������涯̬���
			chartU.setData(data);

			// ͼ���ע��(ֻ�е����ݼ�����ʱ�����Ч)
			Legend l = chartU.getLegend();

			// �����޸�ͼ��ע�ⲿ�ֵ�λ��
			// l.setPosition(LegendPosition.LEFT_OF_CHART);

			// ���ԣ�Ҳ����Բ
			l.setForm(LegendForm.LINE);

			// ��ɫ
			l.setTextColor(Color.BLACK);

			// x������
			XAxis xl = chartU.getXAxis();
			xl.setTextColor(Color.BLACK);
			xl.setDrawGridLines(false);
			xl.setAvoidFirstLastClipping(true);

			// ����x������֮��Ż��ƣ�
			// xl.setSpaceBetweenLabels(2);

			// ���false����ôx�����Ὣ���ɼ�
			xl.setEnabled(true);
			// ��X����������ڵײ���Ĭ�����ڶ�����
			xl.setPosition(XAxisPosition.BOTTOM);
			// ͼ����ߵ�y��������
			YAxis leftAxis = chartU.getAxisLeft();
			leftAxis.setTextColor(Color.BLACK);

			// ���ֵ
			leftAxis.setAxisMaxValue(320f);
			
			// ��Сֵ
			leftAxis.setAxisMinValue(-320f);

			// ��һ��Ҫ��0��ʼ
			leftAxis.setStartAtZero(false);

			leftAxis.setDrawGridLines(true);

			YAxis rightAxis = chartU.getAxisRight();
			// ����ʾͼ����ұ�y��������
			rightAxis.setEnabled(false);
	}
	private void modifyYIValues(short iValue)
	{
		//����������ת����ϵ��ԭʼֵ��18022.125����Ӧ��λֵ��0.25A��1A��5A��20A��100A��
		//�������������жϵ�λ���������ֵ��Сֵ������ʵ��yֵ
		// ͼ����ߵ�y��������
		YAxis leftAxis = chartI.getAxisLeft();
		//0.25A��λ
		if(iValue<=0.25*1.414)
		{
			Iflag=0;
			leftAxis.setAxisMaxValue(0.25f*1.5f);
			leftAxis.setAxisMinValue(-0.25f*1.5f);
		}
		//1A��λ
		else if(iValue>0.25*1.414&&iValue<=1.414)
		{
			Iflag=1;
			leftAxis.setAxisMaxValue(1.5f);
			leftAxis.setAxisMinValue(-1.5f);
		}
		//5A
		else if(iValue>1.414&&iValue<=5*1.414)
		{
			Iflag=2;
			leftAxis.setAxisMaxValue(5f*1.5f);
			leftAxis.setAxisMinValue(-5f*1.5f);
		}
		//20A
		else if(iValue>5*1.414&&iValue<=20*1.414)
		{
			Iflag=3;
			leftAxis.setAxisMaxValue(20f*1.5f);
			leftAxis.setAxisMinValue(-20f*1.5f);
		}
		//100A
		else if(iValue>20*1.414)
		{
			Iflag=4;
			leftAxis.setAxisMaxValue(100f*1.5f);
			leftAxis.setAxisMinValue(-100f*1.5f);
		}
	}
	private void initChartI() {
			chartI.setDescription(getText(R.string.current_waveform).toString());
			chartI.setNoDataTextDescription(getText(R.string.no_data).toString());

			chartI.setTouchEnabled(true);

			// ����ҷ
			chartI.setDragEnabled(true);

			// ������
			chartI.setScaleEnabled(true);
			chartI.setDrawGridBackground(false);

			chartI.setPinchZoom(true);

			// ����ͼ��ı�����ɫ
			chartI.setBackgroundColor(Color.WHITE);

			LineData data = new LineData();

			// ������ʾ����ɫ
			data.setValueTextColor(Color.BLACK);

			// ������һ���յ����ݣ���������涯̬���
			chartI.setData(data);

			// ͼ���ע��(ֻ�е����ݼ�����ʱ�����Ч)
			Legend l = chartI.getLegend();

			// �����޸�ͼ��ע�ⲿ�ֵ�λ��
			// l.setPosition(LegendPosition.LEFT_OF_CHART);

			// ���ԣ�Ҳ����Բ
			l.setForm(LegendForm.LINE);

			// ��ɫ
			l.setTextColor(Color.BLACK);

			// x������
			XAxis xl = chartI.getXAxis();
			xl.setTextColor(Color.BLACK);
			xl.setDrawGridLines(false);
			xl.setAvoidFirstLastClipping(true);

			// ����x������֮��Ż��ƣ�
			// xl.setSpaceBetweenLabels(2);

			// ���false����ôx�����Ὣ���ɼ�
			xl.setEnabled(true);

			// ��X����������ڵײ���Ĭ�����ڶ�����
			xl.setPosition(XAxisPosition.BOTTOM);

			// ͼ����ߵ�y��������
			YAxis leftAxis = chartI.getAxisLeft();
			leftAxis.setTextColor(Color.BLACK);

			// ���ֵ
			leftAxis.setAxisMaxValue(29000f);

			// ��Сֵ
			leftAxis.setAxisMinValue(-29000f);

			// ��һ��Ҫ��0��ʼ
			leftAxis.setStartAtZero(false);

			leftAxis.setDrawGridLines(true);

			YAxis rightAxis = chartI.getAxisRight();
			// ����ʾͼ����ұ�y��������
			rightAxis.setEnabled(false);
	}
	/**
	 * �������Ĺ㲥������
	 */

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) { 
			// ������ǰ����ʾ �൱�ڵ�����onPause();
			Log.i(TAG,"onHidden()");
			try{
				//getActivity().unregisterReceiver(mGattUpdateReceiver);
				//timer.cancel();
			}catch(Exception ex)
				{
					
				}
            return;
        }else{  // ����ǰ����ʾ �൱�ڵ�����onResume();
        	Log.i(TAG,"onHiddenShow()");
        	//getActivity().registerReceiver(mGattUpdateReceiver, MCNL_JiBenWuChaFragment.makeGattUpdateIntentFilter());
        	//timer=new Timer();
    		//timer.schedule(new SendDataTask(), 1000, 3000);
			Comm.getInstance().init(handler);
    		Comm.getInstance().status_loop1=altek.fnGetFrameByFunctionCode((byte) 0x61);
    		Comm.getInstance().status_loop2=altek.fnGetFrameByFunctionCode((byte)0x68);
        }
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			//getActivity().unregisterReceiver(mGattUpdateReceiver);
			//timer.cancel();
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
	}
	private class SendDataTask extends TimerTask{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(dianya_state)
				Blue.send(altek.fnGetFrameByFunctionCode((byte)0x61),handler);
			if(boxing_state)
				Blue.send(altek.fnGetFrameByFunctionCode((byte)0x68),handler);
			
		}
 }
}
