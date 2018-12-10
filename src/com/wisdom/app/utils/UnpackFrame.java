package com.wisdom.app.utils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;

import com.wisdom.bean.BoXingBean;
import com.wisdom.bean.DianbiaoZouZiBean;
import com.wisdom.bean.MeterInfoBean;
import com.wisdom.bean.TaitiCeLiangShuJuBean;
import com.wisdom.bean.XieBoDuquBean;

import android.util.Log;

public class UnpackFrame {
	public boolean fnCheckFormat(byte[] frame) {
		byte[] data=new byte[frame.length-1];
		System.arraycopy(frame, 0, data, 0,frame.length-1);
		if(frame[frame.length-1]!=ByteUtil.SumCheck(data))
			return false;
		if (frame[0] == (byte) 0xEB && frame[1] == (byte) 0x90) {
			int length=(int)(frame[4]&0xff)*2*2*2*2*2*2*2*2+(int)(frame[5]&0xff);
			if(length!=frame.length)
				return false;
			else
				return true;
		}
		return false;
	}
	/**
	 * 终端时钟
	 * */
	public String fnGetClock(byte[] frame)
	{
		int year=(int)(frame[6]&0xff)*2*2*2*2*2*2*2*2+(int)(frame[7]&0xff);
		int month=(int)(frame[8]&0xff);
		int day=(int)(frame[9]&0xff);
		int hour=(int)(frame[10]&0xff);
		int min=(int)(frame[11]&0xff);
		int sec=(int)(frame[12]&0xff);
		return year+"年"+month+"月"+day+"日"+"  "+hour+"时"+min+"分"+sec+"秒";
	}
	/*
	 * 获取终端时钟
	 * @return 年月日时分秒,纯int，从0开始往后排
	 * */
	public int[] fnGetClockAsIntArray(byte[] frame)
	{
		int year=(int)(frame[6]&0xff)*2*2*2*2*2*2*2*2+(int)(frame[7]&0xff);
		int month=(int)(frame[8]&0xff);
		int day=(int)(frame[9]&0xff);
		int hour=(int)(frame[10]&0xff);
		int min=(int)(frame[11]&0xff);
		int sec=(int)(frame[12]&0xff);
		int[] arr_time=new int[6];
		arr_time[0]=year;
		arr_time[1]=month;
		arr_time[2]=day;
		arr_time[3]=hour;
		arr_time[4]=min;
		arr_time[5]=sec;
		return arr_time;
	}
	/*
	 * 获取GPS时钟
	 * @return 年月日时分秒,纯int，从0开始往后排
	 * */
	public int[] fnGetGPSClockAsIntArray(byte[] frame)
	{
		int year=(int)(frame[7]&0xff)*2*2*2*2*2*2*2*2+(int)(frame[6]&0xff);
		int month=(int)(frame[8]&0xff);
		int day=(int)(frame[9]&0xff);
		int hour=(int)(frame[10]&0xff)+8;
		int min=(int)(frame[11]&0xff);
		int sec=(int)(frame[12]&0xff);
		int[] arr_time=new int[6];
		arr_time[0]=year;
		arr_time[1]=month;
		arr_time[2]=day;
		arr_time[3]=hour;
		arr_time[4]=min;
		arr_time[5]=sec;
		return arr_time;
	}
	/**
	 * 实时台体测量数据
	 * @return 电压，电流，频率，有功，无功，总功，功率因数。误差1-1,1-2...2-2,2-3...3-1,3-2,3-3...3-6，次数，标准偏差1 2 3,温度
	 * */
	public TaitiCeLiangShuJuBean fnGetTaiTiCeLiangShuJu(byte[] frame)
	{
		String u="";
		String i="";
		String pinlv="";
		String yougong="";
		String wugong="";
		String zonggong="";
		String gonglvyinshu="";
		String wucha1="",wucha2="",wucha3="";
		String wucha1_2="",wucha1_3="",wucha1_4="",wucha1_5="",wucha1_6="";
		String wucha2_2="",wucha2_3="",wucha2_4="",wucha2_5="",wucha2_6="";
		String wucha3_2="",wucha3_3="",wucha3_4="",wucha3_5="",wucha3_6="";
		String cishu="";
		String jiaodu="";
		String piancha1="",piancha2="",piancha3="";
		String time="";
		String jieguo="";
		String wendu="";
		//2018-11-14新增
		String maichong1="";
		String maichong2="";
		String maichong3="";
		//版本号
		String hard_version="";
		TaitiCeLiangShuJuBean bean=new TaitiCeLiangShuJuBean();
		int iflag=5;
		try
		{
			u=ByteUtil.int1000ToString(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setU(u);
			Log.i("UF","U[V]:"+u);
			iflag=iflag+8;
			i=ByteUtil.int100000ToString(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setI(i);
			iflag=iflag+8;
			Log.i("UF","I[A]:"+i);
			
			//总功=根下 有功方+无功方
			/*
			zonggong=ByteUtil.long1000ToString(ByteUtil.bytes2Long(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]));
			bean.setZonggong(zonggong);
			*/
			iflag=iflag+8;
			//有功
			byte[] data_yougong=new byte[8];
			data_yougong[0]=frame[++iflag];
			data_yougong[1]=frame[++iflag];
			data_yougong[2]=frame[++iflag];
			data_yougong[3]=frame[++iflag];
			data_yougong[4]=frame[++iflag];
			data_yougong[5]=frame[++iflag];
			data_yougong[6]=frame[++iflag];
			data_yougong[7]=frame[++iflag];
			Log.i("UF","P[W]:"+ByteUtil.BinaryToHexString(data_yougong));
			
			yougong=ByteUtil.long1000ToString(ByteUtil.bytes2Long2(data_yougong[0], data_yougong[1], data_yougong[2], data_yougong[3], data_yougong[4], data_yougong[5], data_yougong[6], data_yougong[7]));
			bean.setYougong(yougong);
			iflag=iflag+24;
			//无功
			byte[] data_wugong=new byte[8];
			data_wugong[0]=frame[++iflag];
			data_wugong[1]=frame[++iflag];
			data_wugong[2]=frame[++iflag];
			data_wugong[3]=frame[++iflag];
			data_wugong[4]=frame[++iflag];
			data_wugong[5]=frame[++iflag];
			data_wugong[6]=frame[++iflag];
			data_wugong[7]=frame[++iflag];
			Log.i("UF","Q[kvar]:"+ByteUtil.BinaryToHexString(data_wugong));
			
			wugong=ByteUtil.long1000ToString(ByteUtil.bytes2Long2(data_wugong[0], data_wugong[1], data_wugong[2], data_wugong[3], data_wugong[4], data_wugong[5], data_wugong[6], data_wugong[7]));
			bean.setWugong(wugong);
			double d_yougong=Double.valueOf(yougong);
			double d_wugong=Double.valueOf(wugong);
			double d_zonggong=Math.sqrt(d_yougong*d_yougong+d_wugong*d_wugong);
			DecimalFormat df=new DecimalFormat("0.000");
			zonggong=df.format(d_zonggong);
			bean.setZonggong(zonggong);
			iflag=iflag+18;
			//功率因数
			gonglvyinshu=ByteUtil.int1000ToString(ByteUtil.byteArray2ToInt(frame[++iflag], frame[++iflag]));
			bean.setGonglvyinshu(gonglvyinshu);
			iflag=iflag+4;
			//频率
			pinlv=ByteUtil.int100ToString(ByteUtil.byteArray2ToInt(frame[++iflag], frame[++iflag]));
			bean.setPinlv(pinlv);
			iflag=iflag+4;
			//次数
			cishu=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setCishu(cishu);
			Log.i("UF","次数:"+cishu);
			//表1误差
			wucha1=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha1(wucha1);
			Log.i("UF","误差1_1："+wucha1);
			//iflag=iflag+20;
			//iflag=iflag+4;
			wucha1_2=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha1_2(wucha1_2);
			//Log.i("UF","误差1_2:"+wucha1_2);
			//iflag=iflag+4;
			wucha1_3=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha1_3(wucha1_3);
			//Log.i("UF","误差1_3:"+wucha1_3);
			//iflag=iflag+4;
			wucha1_4=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha1_4(wucha1_4);
			//iflag=iflag+4;
			//Log.i("UF","误差1_4:"+wucha1_4);
			wucha1_5=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha1_5(wucha1_5);
			//iflag=iflag+4;
			//Log.i("UF","误差1_5:"+wucha1_5);
			wucha1_6=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha1_6(wucha1_6);
			//Log.i("UF","误差1_6:"+wucha1_6);
			//iflag=iflag+4;
			//表2误差
			wucha2=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha2(wucha2);
			Log.i("UF","误差2_1:"+wucha2);
			
			wucha2_2=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha2_2(wucha2_2);
			//Log.i("UF","误差2_2:"+wucha2_2);
			
			wucha2_3=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha2_3(wucha2_3);
			//Log.i("UF","误差2_3:"+wucha2_3);
			
			wucha2_4=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha2_4(wucha2_4);
			
			//Log.i("UF","误差2_4:"+wucha2_4);
			wucha2_5=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha2_5(wucha2_5);
			
			//Log.i("UF","误差2_5:"+wucha2_5);
			wucha2_6=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha2_6(wucha2_6);
			//Log.i("UF","误差2_6:"+wucha2_6);
			
			//表3误差
			wucha3=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha3(wucha3);
			Log.i("UF","误差3_1:"+wucha3);
			
			wucha3_2=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha3_2(wucha3_2);
			//Log.i("UF","误差3_2:"+wucha3_2);
			
			wucha3_3=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha3_3(wucha3_3);
			//Log.i("UF","误差3_3:"+wucha3_3);
			
			wucha3_4=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha3_4(wucha3_4);
			//Log.i("UF","误差3_4:"+wucha3_4);
			
			
			wucha3_5=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha3_5(wucha3_5);
			//Log.i("UF","误差3_5:"+wucha3_5);
			
			
			wucha3_6=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha3_6(wucha3_6);
			//Log.i("UF","误差3_6:"+wucha3_6);
			
			iflag=iflag+18*4;
			iflag=iflag+4;
			
			jiaodu=String.valueOf(Double.valueOf(String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag])))/1000);
			bean.setJiaodu(jiaodu);
			iflag=iflag+8;
			piancha1=String.valueOf(ByteUtil.getFloat(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setPiancha1(piancha1);
			Log.i("UF","偏差1："+piancha1);
			piancha2=String.valueOf(ByteUtil.getFloat(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setPiancha2(piancha2);
			Log.i("UF","偏差2："+piancha2);
			piancha3=String.valueOf(ByteUtil.getFloat(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setPiancha3(piancha3);
			Log.i("UF","偏差3："+piancha3);
			time=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			Log.e("UF","剩余时间："+time+" index:"+iflag);
			bean.setTime(time);
			jieguo=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setJieguo(jieguo);
			Log.e("UF","测试结果："+jieguo);
			wendu=String.valueOf(String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag])));
			bean.setWendu(wendu+"℃");
			Log.i("UF","温度："+wendu);
			
			try {
				//2018-11-14新增
				maichong1=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
				bean.setMaichong1(maichong1);
				Log.i("UF","脉冲1："+maichong1);
				maichong2=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
				bean.setMaichong2(maichong2);
				Log.i("UF","脉冲2："+maichong2);
				maichong3=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
				bean.setMaichong3(maichong3);
				Log.i("UF","脉冲3："+maichong3);
				hard_version=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
				bean.setHard_version(hard_version);
				Log.i("UF","版本号："+hard_version);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				return bean;
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bean;
	}
	/**
	 * 实时电表走字数据
	 * 
	 * */
	public DianbiaoZouZiBean fnGetZouZiShuJu(byte[] frame)
	{
		String biaozhundianneng;
		
		String qishi1;
		String jieshu1;
		String shiji1;
		String wucha1;
		
		String qishi2;
		String jieshu2;
		String shiji2;
		String wucha2;
		
		String qishi3;
		String jieshu3;
		String shiji3;
		String wucha3;
		String time;
		String jieguo;
		int maichong;
		
		int maichong1;
		int maichong2;
		int maichong3;
		DianbiaoZouZiBean bean=new DianbiaoZouZiBean();
		int iflag=15;
		try
		{
			biaozhundianneng=ByteUtil.int1000000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setBiaozhundianneng(biaozhundianneng);
			
			qishi1=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setQishi1(qishi1);
			jieshu1=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setJieshu1(jieshu1);
			shiji1=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setShiji1(shiji1);
			wucha1=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha1(wucha1);
			
			qishi2=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setQishi2(qishi2);
			jieshu2=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setJieshu2(jieshu2);
			shiji2=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
		    bean.setShiji2(shiji2);
		    wucha2=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
		    bean.setWucha2(wucha2);
		    
		    qishi3=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setQishi3(qishi3);
			jieshu3=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setJieshu3(jieshu3);
			shiji3=ByteUtil.int1000ToStringP2(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setShiji3(shiji3);
			wucha3=String.valueOf(ByteUtil.byte2Float(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setWucha3(wucha3);
			
			time=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setTime(time);
			jieguo=String.valueOf(ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setJieguo(jieguo);
			maichong=ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]);
			Log.e("UF","脉冲："+maichong);
			bean.setMaichong(maichong);
			
			maichong1=ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]);
			bean.setMaichong1(maichong1);
			Log.e("UF","脉冲1："+maichong1);
			maichong2=ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]);
			bean.setMaichong2(maichong2);
			Log.e("UF","脉冲2："+maichong2);
			maichong3=ByteUtil.byteArrayToInt(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]);
			bean.setMaichong3(maichong3);
			Log.e("UF","脉冲3："+maichong3);
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bean;
	}
	public MeterInfoBean fnGetYaoCe(byte[] frame)
	{
		String u;
		String i;
		String p;//kw
		String pf;
		
		String u_result;
		String i_result;
		String p_result;
		String pf_result;
		MeterInfoBean bean=new MeterInfoBean();
		int iflag=5;
		iflag=iflag+21*4;
		try
		{
			u=ByteUtil.int10000ToString(ByteUtil.byteArrayToIntRevert(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setU(u);
			//d("UF","U:"+bean.getU()+" flag:"+iflag);
			iflag=iflag+8;
			i=ByteUtil.int10000ToString(ByteUtil.byteArrayToIntRevert(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setI(i);
			//Log.d("UF","I:"+bean.getI());
			iflag=iflag+12;
			p=ByteUtil.int10000ToString(ByteUtil.byteArrayToIntRevert(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setP(p);
			//Log.d("UF","P:"+bean.getP());
			iflag=iflag+7*4;
			pf=ByteUtil.int10000ToString(ByteUtil.byteArrayToIntRevert(frame[++iflag],frame[++iflag],frame[++iflag], frame[++iflag]));
			bean.setPf(pf);
			//Log.d("UF","PF:"+bean.getPf()+" flag:"+iflag);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bean;
	}
	/**
	 * 读取谐波数据
	 * */
	public XieBoDuquBean fnGetXieBoShuJu(byte[] frame)
	{
		XieBoDuquBean bean = null;
		try
		{
		int iflag=8;
		//int length=ByteUtil.byteArray2ToInt(frame[++iflag],frame[++iflag]);
		//int count=(length-16)/16;
		int length=frame.length-9;
		int count=(length-16)/16;
		Log.i("UF", "xiebo count:"+count);
		if(count<=0)
			return null;
		float totalUhanliang=ByteUtil.byte2Float(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]);
		float totalUxiangwei=ByteUtil.byte2Float(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]);
		float totalIhanliang=ByteUtil.byte2Float(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]);
		float totalIxiangwei=ByteUtil.byte2Float(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]);
		Vector uHanliang=new Vector();
		Vector iHanliang=new Vector();
		bean=new XieBoDuquBean();
		for(int i=0;i<count-1;i++)
		{
			//Log.i("解包", "起始位置："+iflag);
			float x=ByteUtil.byte2Float(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]);
			Log.i("UF", "电压含量"+i+"："+x);
			uHanliang.addElement(x);
			x=ByteUtil.byte2Float(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]);
			Log.i("UF", "电压相位"+i+"："+x);
			
			//iflag=iflag+4;
			float y=ByteUtil.byte2Float(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]);
			//Log.i("解包", "电流含量："+y);
			iHanliang.addElement(y);
			y=ByteUtil.byte2Float(frame[++iflag], frame[++iflag], frame[++iflag], frame[++iflag]);
			//Log.i("解包", "电流相位："+y);
			
			//iflag=iflag+4;
		}
		bean.setTotalUhanliang(totalUhanliang);
		bean.setTotalIhanliang(totalIhanliang);
		bean.setTotalUxiangwei(totalUxiangwei);
		bean.setTotalIxiangwei(totalIxiangwei);
		bean.setuHanliang(uHanliang);
		bean.setiHanliang(iHanliang);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bean;
	}
	/**
	 * 读取波形
	 * */
	public BoXingBean fnGetBoXingShuJu(byte[] frame)
	{
		BoXingBean bean = null;
		short[] dianya=new short[512];
		/*
		 * 电流前移1个字节，整体共少2个字节
		 * */
		short[] dianliu=new short[510];
		try
		{
		int iflag=5;
		//++iflag;
		//++iflag;
		if(frame.length<256*2*2)
			return null;
		
		bean=new BoXingBean();
		for(int i=0;i<256;i++)
		{
			dianya[i]=ByteUtil.byte2Short(frame[++iflag],frame[++iflag]);
		}
		for(int i=256;i<512;i++)
		{
			dianya[i]=dianya[i-256];
		}
		iflag=iflag+2;
		for(int i=0;i<255;i++)
		{
			dianliu[i]=ByteUtil.byte2Short(frame[++iflag],frame[++iflag]);
		}
		for(int i=255;i<510;i++)
		{
			dianliu[i]=dianliu[i-255];
		}

		/*for(int i=0;i<256;i++)
		{
			//iflag=iflag+2;
			dianliu[i]=ByteUtil.byte2Short(frame[++iflag],frame[++iflag]);
		}
		for(int i=256;i<512;i++)
		{
			dianliu[i]=dianliu[i-256];
		}*/
		bean.setU(dianya);
		bean.setI(dianliu);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return bean;
	}
}
