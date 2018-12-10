package com.wisdom.app.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sunday.slidetabfragment.blue.BlueManager;
import com.sunday.slidetabfragment.blue.ProtocolSignALTEK;
import com.wisdom.bean.BoXingBean;
import com.wisdom.bean.DianbiaoZouZiBean;
import com.wisdom.bean.MeterInfoBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.bean.XieBoDuquBean;

/**
 * 蓝牙发送、接收，统一handler处理
 * 
 * @author Jinjingyun
 */
public class Blue {
	private static DataService ds = new DataService();

	public static synchronized void send(byte[] frame, Handler handler) {
		try{
			if(!BlueManager.getInstance().isConnect())
				return;
			BlueManager.getInstance().write(frame);
			byte[] data = fnRead(frame);
			if (data != null) {
				if ((data[2] & 0x7f) == 0x61)// 实时台体测量数据
				{
					TaitiCeLiangShuJuBean bean = ds.fnGetTaitiData(data);
					if (bean != null) {
						Message msg = new Message();
						msg.what = 0x001;
						msg.obj = bean;
						handler.sendMessage(msg);
					}
				} else if ((data[2] & 0x7f) == 0x5E)// 台体输出配置参数
				{
					Message msg = new Message();
					msg.what = 0x003;
					handler.sendMessage(msg);
				} else if ((data[2] & 0x7f) == 0x6B)// 电表走字测试
				{
					DianbiaoZouZiBean bean = ds.fnGetZouZiData(data);
					if (bean != null) {
						Message msg = new Message();
						msg.what = 0xAA;
						msg.obj = bean;
						handler.sendMessage(msg);
					}
				} else if ((data[2] & 0x7f) == 0x71)// 谐波读取 谐波设置
				{
					Message msg = new Message();
					msg.what = 0x002;
					handler.sendMessage(msg);
				} else if ((data[2] & 0x7f) == 0x7D)// 谐波读取 谐波分析
				{
					XieBoDuquBean bean = ds.fnGetXieBoData(data);
					if (bean != null) {
						Message msg = new Message();
						msg.what = 0x001;
						msg.obj = bean;
						handler.sendMessage(msg);
					}
				} else if ((data[2] & 0x7f) == 0x68)// 波形
				{ 
					BoXingBean bean = ds.fnGetBoXingData(data);
					if (bean != null) {
						Message msg = new Message();
						msg.what = 0x002;
						msg.obj = bean;
						handler.sendMessage(msg);
					}
				} else if ((data[2] & 0x7f) == 0x06)// 时钟
				{
					UnpackFrame uf = new UnpackFrame();
					final int[] arr_clock = uf.fnGetClockAsIntArray(data);
					if (arr_clock != null) {
						Message msg = new Message();
						msg.what = 0x009;
						msg.obj = arr_clock;
						handler.sendMessage(msg);
					}
				}
				else if((data[2] & 0x7f) == 0x7E)//GPS时钟
				{
					UnpackFrame uf = new UnpackFrame();
					int[] arr_clock = uf.fnGetGPSClockAsIntArray(data);
					if (arr_clock != null) {
						Message msg = new Message();
						msg.what = 0x008;
						msg.obj = arr_clock;
						handler.sendMessage(msg);
					}
				}
				else if ((data[2] & 0x7f) == 0x07)// 时钟
				{
					Message msg = new Message();
					msg.what = 0x13;
					handler.sendMessage(msg);
				}
				else if ((data[2] & 0x7f) == 0x73)//谐波补偿
				{
					Message msg = new Message();
					msg.what = 0x002;
					handler.sendMessage(msg);
				}
				else if ((data[2] & 0x7f) == 0x0E)//抄表
				{
					UnpackFrame uf=new UnpackFrame();
					MeterInfoBean bean = uf.fnGetYaoCe(data);
					if (bean != null) {
						Message msg = new Message();
						msg.what = 0x13;
						msg.obj = bean;
						handler.sendMessage(msg);
					}
				}
			}
			else
			{
				Log.i("BlueManager","Blue.Class read is null");
				//send(frame,handler);
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private static byte[] fnRead(byte[] frame)
	{
		if(!BlueManager.getInstance().isConnect())
			return null;
		//BlueManager.getInstance().write(frame);
		byte[] data = BlueManager.getInstance().read(ProtocolSignALTEK.getInstance());
		if(data==null)
		{
			Log.e("BlueManager","Blue.fnRead is null, try to reRead");
			fnRead(frame);
		}
		else if(data.length==6&&data[0]==(byte)0xff&&data[1]==(byte)0xff&&data[2]==(byte)0xff)
		{
			BlueManager.getInstance().write(frame);
			fnRead(frame);
		}
		else
		return data;
		return null;
	}
}
