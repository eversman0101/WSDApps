package com.wisdom.service;

import java.util.Date;
import java.util.List;

import com.sunday.slidetabfragment.blue.BlueManager;
import com.sunday.slidetabfragment.blue.ProtocolSignALTEK;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.Blue;
import com.wisdom.app.utils.ByteUtil;
import com.wisdom.app.utils.DataService;
import com.wisdom.app.utils.StopException;
import com.wisdom.bean.AutoCheckResultBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

/**
 * @author Jinjingyun �Զ�����ȫ���� 1.�����ȼ���������� 2.��ʼ���ԣ������Խ�����ж��Ƿ�ϸ� 3.���浽������
 */
public class MeasureProcess {
	Context mContext;
	SharedPreferences dianbiaocanshu;
	// �����Ϣ
	double d_meter_level = 0.5;
	int i_meter_count = 1;
	String meter1_no = "0";
	String meter2_no = "0";
	String meter3_no = "0";
	String maichongchangshu = "0";
	ALTEK altek;
	// ��ѹ��������
	float f_dianya;
	float f_dianliu;
	float f_jiaodu;
	float f_pinlv;
	int i_quanshu;
	int i_cishu;
	boolean bl_yuan = true;
	public boolean bl_test_state = false;
	DataService ds = new DataService();
	/**
	 * ֹͣ���Եı�־λ
	 * */
	public boolean stop_flag=false;
	/**
	 * ������
	 */
	public interface IProgressListener {
		void onProgress(long bytescount, long bytestotal);
	}
	/**
	 * ����״̬
	 * */
	public interface IStateListener {
		/**
		 * @param index ������
		 * @param state ���Խ׶Σ�0�����У�1������ɣ�10ȫ���������,-1ֹͣ�У�-2��ֹͣ
		 * */
		void onState(int index,int state,int size);
	}
	public MeasureProcess(Context context) {
		this.mContext = context;
		dianbiaocanshu = context.getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
		altek = new ALTEK();
		initMeterInfo();
	}

	/**
	 * ��ʼ����
	 * 
	 * @param bean
	 *            δ���Ե����ݼ�
	 * @return �в��Խ�������ݼ�
	 */
	public List<AutoCheckResultBean> fnStart(final List<AutoCheckResultBean> list_bean,
			final IStateListener listener) throws StopException{
		try {
			
					BlueManager.getInstance()
							.write(altek.fnTaitiCeShiLeiXingPeiZhiCanshu(0, 0, Integer.valueOf(maichongchangshu),
									Integer.valueOf(maichongchangshu), i_meter_count, meter1_no, meter2_no, meter3_no));
					byte[] recv = BlueManager.getInstance().read(ProtocolSignALTEK.getInstance());
					if (recv != null) {
						if (!((recv[2] & 0x7f) == 0x5E))
							return null;
						if (list_bean.size() <= 0)
						{
							Log.e("acnl","list_bean is null");
							return null;
						}
						for (int i = 0; i < list_bean.size(); i++) {
							if(stop_flag==true)
							{
								stop_flag=false;
								listener.onState(i,-2,list_bean.size());
								throw new StopException("����ֹͣ:line 98");
							}
							initData(list_bean.get(i));
							if (bl_yuan) {
								BlueManager.getInstance().write(altek.fnTaitiShuChu(f_dianya, f_dianliu, 0, f_jiaodu,
										f_pinlv, 0, 0, 0, i_quanshu, i_cishu, 0, 0, 0));
								byte[] rec = BlueManager.getInstance().read(ProtocolSignALTEK.getInstance());
								if (!((rec[2] & 0x7f) == 0x60))
								{
									Log.e("acnl","recv[2] is not 0x60:"+ByteUtil.byte2HexStr(rec));
									return null;
								}
								bl_yuan = false;
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							bl_test_state = true;
							// �������״̬
							while (bl_test_state) {
								if(stop_flag==true)
								{
									//stop_flag=false;
									listener.onState(i,-1,list_bean.size());
									Log.e("UF","����ֹͣ��line 122");
									
									break;
									//throw new StopException("����ֹͣ:line 122");
								}
								BlueManager.getInstance().write(altek.fnGetFrameByFunctionCode((byte) 0x61));
								byte[] data = BlueManager.getInstance().read(ProtocolSignALTEK.getInstance());
								if (data != null) {
									if ((data[2] & 0x7f) == 0x61)// ʵʱ̨���������
									{
										TaitiCeLiangShuJuBean taiti_bean = ds.fnGetTaitiData(data);
										if (taiti_bean != null) {
											//listener.onProgress(i, list_bean.size());
											listener.onState(i,0,list_bean.size());
											// �������
											if (Integer.valueOf(i_cishu) == Integer.valueOf(taiti_bean.getCishu())
													&& (Integer.valueOf(taiti_bean.getJieguo()) == 2)
													&& (Integer.valueOf(taiti_bean.getTime()) == 0)) {
												listener.onState(i,1,list_bean.size());
												bl_test_state = false;

												String str_result = "";
												if (Double.valueOf(taiti_bean.getWucha1()) < Double.valueOf(list_bean.get(i).getWucha_limit()))
													str_result = taiti_bean.getWucha1() + " " + "�ϸ�";
												else
													str_result = taiti_bean.getWucha1() + " " + "���ϸ�";
												list_bean.get(i).setResult1(str_result);
												if (Double.valueOf(taiti_bean.getWucha2()) < Double.valueOf(list_bean.get(i).getWucha_limit()))
													str_result = taiti_bean.getWucha2() + " " + "�ϸ�";
												else
													str_result = taiti_bean.getWucha2() + " " + "���ϸ�";
												list_bean.get(i).setResult2(str_result);

												if (Double.valueOf(taiti_bean.getWucha3()) < Double.valueOf(list_bean.get(i).getWucha_limit()))
													str_result = taiti_bean.getWucha3() + " " + "�ϸ�";
												else
													str_result = taiti_bean.getWucha3() + " " + "���ϸ�";
												list_bean.get(i).setResult3(str_result);
												list_bean.get(i).setDate(ByteUtil.GetNowDate());
											}
										}
									}
								}

							}
							//ͣԴ
							BlueManager.getInstance().write(altek.fnTaitiShuChu(0, 0, 0, f_jiaodu,
									f_pinlv, 0, 0, 0, i_quanshu, i_cishu, 0, 0, 0));
							byte[] rec = BlueManager.getInstance().read(ProtocolSignALTEK.getInstance());
							if (!((rec[2] & 0x7f) == 0x60))
							{
								Log.e("acnl","recv[2] is not 0x60:"+ByteUtil.byte2HexStr(rec));
								return null;
							}
							bl_yuan=true;
							if(stop_flag==true)
							{
								stop_flag=false;
								listener.onState(i,-2,list_bean.size());
								Log.e("UF","����ֹͣ��line 185");
								return null;
								//throw new StopException("����ֹͣ:line 122");
							}
							}
						listener.onState(list_bean.size()-1, 10, list_bean.size());
					}
					return list_bean;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ȡ���ȼ����������
	 */
	private void initMeterInfo() {
		String meter_level = dianbiaocanshu.getString("meter_level", "0.5");
		if(meter_level.equals("0.2��"))
			d_meter_level=0.2;
			if(meter_level.equals("0.5��"))
				d_meter_level=0.5;
			if(meter_level.equals("1.0��"))
				d_meter_level=1;
			if(meter_level.equals("2.0��"))
				d_meter_level=2;
		maichongchangshu = dianbiaocanshu.getString("meter1_constant", "1600");
		meter1_no = dianbiaocanshu.getString("meter1_no", "");
		meter2_no = dianbiaocanshu.getString("meter2_no", "");
		meter3_no = dianbiaocanshu.getString("meter3_no", "");
		if (meter1_no.equals(""))
			meter1_no = "0";
		if (!meter2_no.equals(""))
			i_meter_count++;
		else
			meter2_no = "0";
		if (!meter3_no.equals(""))
			i_meter_count++;
		else
			meter3_no = "0";
	}

	private void initData(AutoCheckResultBean bean) {
		try {
			String str_ub = bean.getUb();
			String str_ib = bean.getIb();
			double d_ur = Double.valueOf(bean.getUr()) / 100;
			//double d_ir = Double.valueOf(bean.getIr()) / 100;
			f_dianya = (float) (Double.valueOf(str_ub) * d_ur);
			f_dianliu = floatIr(bean,str_ib);
			String str_pf = bean.getPower_factor();
			if (str_pf.equals("1.0"))
				str_pf = "0";
			else if (str_pf.equals("0.25L"))
				str_pf = "75.5";
			else if (str_pf.equals("0.25C"))
				str_pf = "284.5";
			else if (str_pf.equals("0.5L"))
				str_pf = "60";
			else if (str_pf.equals("0.5C"))
				str_pf = "300";
			else if (str_pf.equals("0.8L"))
				str_pf = "36.9";
			else if (str_pf.equals("0.8C"))
				str_pf = "323.1";

			f_jiaodu = Float.parseFloat(str_pf);
			f_pinlv = Float.parseFloat(bean.getPinlv());
			i_quanshu = Integer.valueOf(bean.getQuanshu());
			i_cishu = Integer.valueOf(bean.getCishu());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	//����
	public float floatIr(AutoCheckResultBean bean,String str_ib){
		float ir1=0;
		String ir =bean.getIr();
		if(ir.equals("0.05Ib")){
			ir1=(float) (0.05*Double.valueOf(str_ib));
		}
		else if(ir.equals("0.1Ib")){
			ir1=(float) (0.1*Double.valueOf(str_ib));
		}else if(ir.equals("0.2Ib")){
			ir1=(float) (0.2*Double.valueOf(str_ib));
		}else if(ir.equals("0.5Ib")){
			ir1=(float) (0.5*Double.valueOf(str_ib));
		}else if(ir.equals("0.8Ib")){
			ir1=(float) (0.8*Double.valueOf(str_ib));
		}else if(ir.equals("Imax")){
			 bean.setIb(dianbiaocanshu.getString("max_current", "5"));
			 return  Float.valueOf(bean.getIb());
		}else 
		{
			ir1=(float) (1*Double.valueOf(str_ib));
		}
		
		return ir1;
	}

}

