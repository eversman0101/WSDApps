package com.wisdom.app.utils;

import java.util.Vector;

import android.util.Log;

/**
 * @author JinJingYun 2017年11月27日 组包方法
 */
public class ALTEK {
	private byte HEADER1 = (byte) 0xEB;
	private byte HEADER2 = (byte) 0x90;
	private byte VERSION = (byte) 0x02;
	private byte CS;

	/**
	 * 无数据区的下行报文
	 * 
	 * @param fc
	 *            功能码
	 */
	public byte[] fnGetFrameByFunctionCode(byte fc) {
		byte[] data = new byte[6];
		data[0] = HEADER1;
		data[1] = HEADER2;
		data[2] = fc;
		data[3] = VERSION;

		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		byte check = ByteUtil.SumCheck(data);
		byte[] frame = new byte[7];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[6] = check;
		return frame;
	}

	/**
	 * 3抄表数据读取
	 * 
	 * @param i
	 *            表位号(1,2,3)
	 */
	public byte[] fnChaoBiao(int i) {
		Log.e("UF", "表位号："+i);
		// 无校验的长度
		byte[] data = new byte[6 + 8];
		data[0] = HEADER1;
		data[1] = HEADER2;
		// 功能码
		data[2] = 0x0E;
		data[3] = VERSION;
		// 报文长度
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// 数据区
		data[6] = 0x00;
		data[7] = 0x00;
		data[8] = 0x00;
		data[9] = (byte) i;

		data[10] = 0x00;
		data[11] = 0x00;

		data[12] = 0x00;
		data[13] = 0x28;
		byte check = ByteUtil.SumCheck(data);
		// 总长度
		byte[] frame = new byte[7 + 8];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/**
	 * 6台体输出配置参数
	 * 
	 * @param UA
	 *            A项电压有效值
	 * @param IA
	 *            A项电流有效值
	 * @param jiaoduUA
	 *            A项电压角度
	 * @param jiaoduIA
	 *            A项电流角度
	 * @param pinlv
	 *            频率
	 * @param xiebocishu
	 *            谐波次数
	 * @param xiebohanliang
	 *            谐波含量
	 * @param pinlvG
	 *            高频输出频率
	 * @param quanshu
	 *            测试圈数
	 * @param cishu
	 *            测试次数
	 * @param shichang
	 *            测试时长
	 * @param leixing
	 *            测试类型
	 * @param key
	 *            测试KEY
	 */
	public byte[] fnTaitiShuChu(float UA, float IA, float jiaoduUA, float jiaoduIA, float pinlv, int xiebocishu,
			int xiebohanliang, int pinlvG, int quanshu, int cishu, int shichang, int leixing, int key) {
		// 无校验的长度
		byte[] data = new byte[90];
		byte[] value;
		byte[] ndata = new byte[4];
		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// 功能码
		data[2] = 0x60;
		data[3] = VERSION;
		// 报文长度
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// 数据区
		iflag = 6;
		// IA
		Log.e("ALTEK","电流："+IA);
		value = ByteUtil.float001to4Byte(IA);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// IB
		// value = ByteUtil.float001to4Byte(IA);
		// System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// IC
		// value = ByteUtil.float001to4Byte(IA);
		// System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		// UA
		value = ByteUtil.float01to4Byte(UA);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// value = ByteUtil.float01to4Byte(UA);
		// System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// value = ByteUtil.float01to4Byte(UA);
		// System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		// 角度UA
		value = ByteUtil.float0001to4Byte(jiaoduUA);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		iflag = iflag + 4;
		iflag = iflag + 4;

		value = ByteUtil.float0001to4Byte(jiaoduIA);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		iflag = iflag + 4;
		iflag = iflag + 4;
		// 频率
		value = ByteUtil.float01to4Byte(pinlv);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		value = ByteUtil.intTo4ByteArray(xiebocishu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		value = ByteUtil.intTo4ByteArray(xiebohanliang);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		value = ByteUtil.intTo4ByteArray(pinlvG);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// 圈数
		value = ByteUtil.intTo4ByteArray(quanshu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// 次数
		value = ByteUtil.intTo4ByteArray(cishu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		// 测试时长
		value = ByteUtil.intTo4ByteArray(shichang);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		value = ByteUtil.intTo4ByteArray(leixing);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		value = ByteUtil.intTo4ByteArray(key);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		byte check = ByteUtil.SumCheck(data);
		// 总长度
		byte[] frame = new byte[91];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/**
	 * 4台体测试类型配置参数
	 */
	public byte[] fnTaitiCeShiLeiXingPeiZhiCanshu(int ceshileixing, int jiexianleixing, int yougongmaichongchangshu,
			int wugongmaichongchangshu, int biaoshuliang, String add1, String add2, String add3) {
		Log.i("UF","ALTEK:台体测试类型配置参数命令");
		// 无校验的长度
		byte[] data = new byte[41];
		byte[] value;
		byte[] ndata = new byte[4];

		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// 功能码
		data[2] = 0x5E;
		data[3] = VERSION;
		// 报文长度
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// 数据区
		iflag = 6;
		// 测试类型
		value = ByteUtil.intTo4ByteArray(ceshileixing);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// 接线类型
		iflag = iflag + 4;
		// 有功脉冲常数
		value = ByteUtil.intTo4ByteArray(yougongmaichongchangshu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		// 无功脉冲常数
		value = ByteUtil.intTo4ByteArray(wugongmaichongchangshu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// 表数量
		data[iflag] = (byte) (biaoshuliang & 0xff);
		iflag++;
		// 表位1地址
		if (add1 != "0") {
			value = ByteUtil.str2Bcd(add1);
			System.arraycopy(value, 0, data, iflag + 6 - value.length, value.length);
		}
		iflag++;
		iflag++;
		iflag = iflag + 4;

		if (add2 != "0") {
			value = ByteUtil.str2Bcd(add2);
			System.arraycopy(value, 0, data, iflag + 6 - value.length, value.length);
		}
		iflag++;
		iflag++;
		iflag = iflag + 4;

		if (add3 != "0") {
			value = ByteUtil.str2Bcd(add3);
			System.arraycopy(value, 0, data, iflag + 6 - value.length, value.length);
		}
		iflag++;
		iflag++;

		byte check = ByteUtil.SumCheck(data);
		// 总长度
		byte[] frame = new byte[42];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/*
	 * 设置时钟
	 */
	public byte[] fnSetClock(int year, int month, int day, int hour, int min, int sec) {
		// 无校验的长度
		byte[] data = new byte[15];
		byte[] value;
		// byte[] ndata = new byte[9];
		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// 功能码
		data[2] = 0x07;
		data[3] = VERSION;
		// 报文长度
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// 数据区
		iflag = 6;
		// year
		value = ByteUtil.int2byte(year);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 2;
		data[iflag++] = (byte) month;
		data[iflag++] = (byte) day;
		data[iflag++] = (byte) hour;
		data[iflag++] = (byte) min;
		data[iflag++] = (byte) sec;

		byte check = ByteUtil.SumCheck(data);
		// 总长度
		byte[] frame = new byte[16];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/*
	 * 谐波补偿
	 */
	public byte[] fnXieBoBuChang() {
		String str_frame = "EB907301000A010101FC";
		byte[] frame = ByteUtil.hexStr2Bytes(str_frame);
		return frame;
	}

	/*
	 * 取消谐波补偿
	 */
	public byte[] fnQuXiaoXieBoBuChang() {
		String str_frame = "EB907301000A010100FB";
		byte[] frame = ByteUtil.hexStr2Bytes(str_frame);
		return frame;
	}

	/**
	 * 电表走字测试
	 * 
	 * @param type
	 *            报文类型 0参数设置模式，1数据读取模式
	 * @param method
	 *            走字方式
	 * @param yuzhidianneng
	 *            预置电能
	 * @param beilv
	 *            输入倍率
	 */
	public byte[] fnDianbiaoZouZi(int type, int method, int yuzhidianneng, int beilv) {
		// 无校验的长度
		byte[] data = new byte[76];
		byte[] value;
		byte[] ndata = new byte[4];

		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// 功能码
		data[2] = 0x6B;
		data[3] = VERSION;
		// 报文长度
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// 数据区
		iflag = 6;
		// 报文类型
		data[iflag] = (byte) type;
		iflag++;
		// 走字方式
		data[iflag] = (byte) method;
		Log.e("UF","走字方式:"+method);
		iflag++;
		// 预置电能
		value = ByteUtil.intTo4ByteArray(yuzhidianneng);
		Log.e("UF","预置电能:"+yuzhidianneng);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// 倍率
		value = ByteUtil.intTo4ByteArray(beilv);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// 累计电能
		iflag = iflag + 4;

		iflag = iflag + 4;
		iflag = iflag + 4;
		iflag = iflag + 4;
		iflag = iflag + 4;

		iflag = iflag + 4;
		iflag = iflag + 4;
		iflag = iflag + 4;
		iflag = iflag + 4;

		iflag = iflag + 4;
		iflag = iflag + 4;
		iflag = iflag + 4;
		iflag = iflag + 4;

		iflag = iflag + 4;
		iflag = iflag + 4;
		byte check = ByteUtil.SumCheck(data);
		// 总长度
		byte[] frame = new byte[77];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/**
	 * 谐波设置
	 * 
	 * @param count
	 *            谐波次数
	 * @param args
	 *            1次A项电压含有率，1次A项电流含有率。2次A项电压含有率，2次A项电流含有率...
	 */
	public byte[] fnXieBoSheZhi(int count, Vector args) {
		if ((count * 2) != args.size())
			return null;
		int dataLength = (count + 1) * 12 * 2;
		byte[] len = new byte[2];
		len = ByteUtil.int2Twobyte(dataLength);

		// 无校验的长度
		byte[] data = new byte[6 + 4 + dataLength];
		byte[] frameLength = new byte[2];
		frameLength = ByteUtil.int2Twobyte(data.length + 1);
		byte[] value;
		byte[] ndata = new byte[4];
		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// 功能码
		data[2] = 0x71;
		data[3] = (byte)0x01;
		// 报文长度 2018-04-23
		data[4] = frameLength[1];
		data[5] = frameLength[0];
		// 数据区
		iflag = 5;

		data[++iflag]=(byte)0x02;
		data[++iflag] = len[0];
		data[++iflag] = (byte)0x00;
		//++iflag;
		data[++iflag] = (byte) count;
		++iflag;
		int index = 0;
		// string转float*100，再转int再转两字节byte
		for (int i = 0; i <= count; i++) {

			if (i == 0) {
				// 电压含有率
				double d1 = 100;
				Log.i("ALTEK", i + 1 + "电压含有率：" + d1);
				int x1 = (int) (d1 * 100);
				byte[] bFrame1 = new byte[2];
				bFrame1 = ByteUtil.int2TwobyteRevert(x1);
				System.arraycopy(bFrame1, 0, data, iflag, bFrame1.length);
				Log.e("ALTEK","u han:"+ByteUtil.byte2HexStr(bFrame1));
				Log.e("ALTEK","u han index:"+iflag);
				iflag = iflag + 4;
				// 电流含有率
				double d2 = 100;
				Log.i("ALTEK", i + 1 + "电流含有率：" + d2);
				int x2 = (int) (d2 * 100);
				byte[] bFrame2 = new byte[2];
				bFrame2 = ByteUtil.int2TwobyteRevert(x2);
				System.arraycopy(bFrame2, 0, data, iflag, bFrame2.length);
				Log.e("ALTEK","i han:"+ByteUtil.byte2HexStr(bFrame1));
				Log.e("ALTEK","i han index:"+iflag);
				iflag = iflag + 20;
			} else {
				// 电压含有率
				double d1 = Double.parseDouble(args.get(index).toString());
				Log.i("ALTEK", i + 1 + "电压含有率：" + d1);
				int x1 = (int) (d1 * 100);
				byte[] bFrame1 = new byte[2];
				bFrame1 = ByteUtil.int2TwobyteRevert(x1);
				Log.e("ALTEK","u han:"+ByteUtil.byte2HexStr(bFrame1));
				
				System.arraycopy(bFrame1, 0, data, iflag, bFrame1.length);
				Log.e("ALTEK","u han index:"+iflag);
				
				iflag = iflag + 4;
				// 电流含有率
				double d2 = Double.parseDouble(args.get(index + 1).toString());
				Log.i("ALTEK", i + 1 + "电流含有率：" + d2);
				int x2 = (int) (d2 * 100);
				byte[] bFrame2 = new byte[2];
				bFrame2 = ByteUtil.int2TwobyteRevert(x2);
				Log.e("ALTEK","i han:"+ByteUtil.byte2HexStr(bFrame2));
				
				System.arraycopy(bFrame2, 0, data, iflag, bFrame2.length);
				Log.e("ALTEK","i han index:"+iflag);
				
				iflag = iflag + 20;
				index = i * 2;
			}
		}
		byte check = ByteUtil.SumCheck(data);
		// 总长度
		byte[] frame = new byte[6 + 4 + dataLength + 1];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/**
	 * 谐波设置
	 * 
	 * @param count
	 *            谐波次数
	 * @param args
	 *            1次A项电压含有率，“1次A相电压相位”，1次A项电流含有率,“1次A相电流相位”。2次A项电压含有率，“2次A相电压相位”，2次A项电流含有率，“2次A相电流相位”...
	 */
	public byte[] fnXieBoSheZhiAndXiangWei(int count, Vector args) {
//		if ((count * 4) != args.size())
//		{
//			Log.i("ALTEK","args.size:"+args.size()+" count:"+count +" return ");
//			return null;
//		}
		Log.i("ALTEK","args.size:"+args.size()+" count:"+count);
		
		int dataLength = (count + 1) * 12 * 2;
		byte[] len = new byte[2];
		len = ByteUtil.int2Twobyte(dataLength);

		// 无校验的长度
		byte[] data = new byte[6 + 4 + dataLength];
		byte[] frameLength = new byte[2];
		frameLength = ByteUtil.int2Twobyte(data.length + 1);
		byte[] value;
		byte[] ndata = new byte[4];
		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// 功能码
		data[2] = 0x71;
		data[3] = (byte)0x01;
		// 报文长度 2018-04-23
		data[4] = frameLength[1];
		data[5] = frameLength[0];
		// 数据区
		iflag = 5;

		data[++iflag]=(byte)0x02;
		data[++iflag] = len[0];
		data[++iflag] = (byte)0x00;
		//++iflag;
		data[++iflag] = (byte) (count+1);
		++iflag;
		int index = 0;
		// string转float*100，再转int再转两字节byte
		for (int i = 0; i <= count; i++) {
			Log.i("ALTEK","xiebo count:"+count);
			
			if (i == 0) {
				// 电压含有率
				double d1 = 100;
				Log.i("ALTEK", i + 1 + "电压含有率：" + d1);
				int x1 = (int) (d1 * 100);
				byte[] bFrame1 = new byte[2];
				bFrame1 = ByteUtil.int2TwobyteRevert(x1);
				System.arraycopy(bFrame1, 0, data, iflag, bFrame1.length);
				Log.e("ALTEK","u han:"+ByteUtil.byte2HexStr(bFrame1));
				Log.e("ALTEK","u han index:"+iflag);
				iflag = iflag + 4;
				// 电流含有率
				double d2 = 100;
				Log.i("ALTEK", i + 1 + "电流含有率：" + d2);
				int x2 = (int) (d2 * 100);
				byte[] bFrame2 = new byte[2];
				bFrame2 = ByteUtil.int2TwobyteRevert(x2);
				System.arraycopy(bFrame2, 0, data, iflag, bFrame2.length);
				Log.e("ALTEK","i han:"+ByteUtil.byte2HexStr(bFrame1));
				Log.e("ALTEK","i han index:"+iflag);
				iflag = iflag + 20;
			} else {
				// 电压含有率
				double d1 = Double.parseDouble(args.get(index).toString());
				Log.i("ALTEK", i + 1 + "电压含有率：" + d1);
				int x1 = (int) (d1 * 100);
				byte[] bFrame1 = new byte[2];
				bFrame1 = ByteUtil.int2TwobyteRevert(x1);
				Log.e("ALTEK","u han:"+ByteUtil.byte2HexStr(bFrame1));
				System.arraycopy(bFrame1, 0, data, iflag, bFrame1.length);
				Log.e("ALTEK","u han index:"+iflag);
				iflag=iflag+2;
				
				//电压相位
				double d3 = Double.parseDouble(args.get(index+1).toString());
				Log.i("ALTEK", i + 1 + "电压相位：" + d3);
				int x3 = (int) (d3 * 100);
				byte[] bFrame3 = new byte[2];
				bFrame3 = ByteUtil.int2TwobyteRevert(x3);
				Log.e("ALTEK","u 相位:"+ByteUtil.byte2HexStr(bFrame3));
				System.arraycopy(bFrame3, 0, data, iflag, bFrame3.length);
				Log.e("ALTEK","u 相位 index:"+iflag);
				iflag=iflag+2;
				
				// 电流含有率
				double d2 = Double.parseDouble(args.get(index + 2).toString());
				Log.i("ALTEK", i + 1 + "电流含有率：" + d2);
				int x2 = (int) (d2 * 100);
				byte[] bFrame2 = new byte[2];
				bFrame2 = ByteUtil.int2TwobyteRevert(x2);
				Log.e("ALTEK","i han:"+ByteUtil.byte2HexStr(bFrame2));
				
				System.arraycopy(bFrame2, 0, data, iflag, bFrame2.length);
				Log.e("ALTEK","i han index:"+iflag);
				iflag=iflag+2;
				
				//电流相位
				double d4 = Double.parseDouble(args.get(index + 3).toString());
				Log.i("ALTEK", i + 1 + "电流相位：" + d4);
				int x4 = (int) (d4 * 100);
				byte[] bFrame4 = new byte[2];
				bFrame4 = ByteUtil.int2TwobyteRevert(x4);
				Log.e("ALTEK","i 相位:"+ByteUtil.byte2HexStr(bFrame4));
				
				System.arraycopy(bFrame4, 0, data, iflag, bFrame4.length);
				Log.e("ALTEK","i 相位 index:"+iflag);
				
				iflag = iflag + 18;
				index = i * 4;
			}
		}
		byte check = ByteUtil.SumCheck(data);
		// 总长度
		byte[] frame = new byte[6 + 4 + dataLength + 1];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}
}
