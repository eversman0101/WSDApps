package com.wisdom.app.utils;

import java.util.Vector;

import android.util.Log;

/**
 * @author JinJingYun 2017��11��27�� �������
 */
public class ALTEK {
	private byte HEADER1 = (byte) 0xEB;
	private byte HEADER2 = (byte) 0x90;
	private byte VERSION = (byte) 0x02;
	private byte CS;

	/**
	 * �������������б���
	 * 
	 * @param fc
	 *            ������
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
	 * 3�������ݶ�ȡ
	 * 
	 * @param i
	 *            ��λ��(1,2,3)
	 */
	public byte[] fnChaoBiao(int i) {
		Log.e("UF", "��λ�ţ�"+i);
		// ��У��ĳ���
		byte[] data = new byte[6 + 8];
		data[0] = HEADER1;
		data[1] = HEADER2;
		// ������
		data[2] = 0x0E;
		data[3] = VERSION;
		// ���ĳ���
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// ������
		data[6] = 0x00;
		data[7] = 0x00;
		data[8] = 0x00;
		data[9] = (byte) i;

		data[10] = 0x00;
		data[11] = 0x00;

		data[12] = 0x00;
		data[13] = 0x28;
		byte check = ByteUtil.SumCheck(data);
		// �ܳ���
		byte[] frame = new byte[7 + 8];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/**
	 * 6̨��������ò���
	 * 
	 * @param UA
	 *            A���ѹ��Чֵ
	 * @param IA
	 *            A�������Чֵ
	 * @param jiaoduUA
	 *            A���ѹ�Ƕ�
	 * @param jiaoduIA
	 *            A������Ƕ�
	 * @param pinlv
	 *            Ƶ��
	 * @param xiebocishu
	 *            г������
	 * @param xiebohanliang
	 *            г������
	 * @param pinlvG
	 *            ��Ƶ���Ƶ��
	 * @param quanshu
	 *            ����Ȧ��
	 * @param cishu
	 *            ���Դ���
	 * @param shichang
	 *            ����ʱ��
	 * @param leixing
	 *            ��������
	 * @param key
	 *            ����KEY
	 */
	public byte[] fnTaitiShuChu(float UA, float IA, float jiaoduUA, float jiaoduIA, float pinlv, int xiebocishu,
			int xiebohanliang, int pinlvG, int quanshu, int cishu, int shichang, int leixing, int key) {
		// ��У��ĳ���
		byte[] data = new byte[90];
		byte[] value;
		byte[] ndata = new byte[4];
		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// ������
		data[2] = 0x60;
		data[3] = VERSION;
		// ���ĳ���
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// ������
		iflag = 6;
		// IA
		Log.e("ALTEK","������"+IA);
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

		// �Ƕ�UA
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
		// Ƶ��
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
		// Ȧ��
		value = ByteUtil.intTo4ByteArray(quanshu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// ����
		value = ByteUtil.intTo4ByteArray(cishu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		// ����ʱ��
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
		// �ܳ���
		byte[] frame = new byte[91];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/**
	 * 4̨������������ò���
	 */
	public byte[] fnTaitiCeShiLeiXingPeiZhiCanshu(int ceshileixing, int jiexianleixing, int yougongmaichongchangshu,
			int wugongmaichongchangshu, int biaoshuliang, String add1, String add2, String add3) {
		Log.i("UF","ALTEK:̨������������ò�������");
		// ��У��ĳ���
		byte[] data = new byte[41];
		byte[] value;
		byte[] ndata = new byte[4];

		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// ������
		data[2] = 0x5E;
		data[3] = VERSION;
		// ���ĳ���
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// ������
		iflag = 6;
		// ��������
		value = ByteUtil.intTo4ByteArray(ceshileixing);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// ��������
		iflag = iflag + 4;
		// �й����峣��
		value = ByteUtil.intTo4ByteArray(yougongmaichongchangshu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;

		// �޹����峣��
		value = ByteUtil.intTo4ByteArray(wugongmaichongchangshu);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// ������
		data[iflag] = (byte) (biaoshuliang & 0xff);
		iflag++;
		// ��λ1��ַ
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
		// �ܳ���
		byte[] frame = new byte[42];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/*
	 * ����ʱ��
	 */
	public byte[] fnSetClock(int year, int month, int day, int hour, int min, int sec) {
		// ��У��ĳ���
		byte[] data = new byte[15];
		byte[] value;
		// byte[] ndata = new byte[9];
		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// ������
		data[2] = 0x07;
		data[3] = VERSION;
		// ���ĳ���
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// ������
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
		// �ܳ���
		byte[] frame = new byte[16];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/*
	 * г������
	 */
	public byte[] fnXieBoBuChang() {
		String str_frame = "EB907301000A010101FC";
		byte[] frame = ByteUtil.hexStr2Bytes(str_frame);
		return frame;
	}

	/*
	 * ȡ��г������
	 */
	public byte[] fnQuXiaoXieBoBuChang() {
		String str_frame = "EB907301000A010100FB";
		byte[] frame = ByteUtil.hexStr2Bytes(str_frame);
		return frame;
	}

	/**
	 * ������ֲ���
	 * 
	 * @param type
	 *            �������� 0��������ģʽ��1���ݶ�ȡģʽ
	 * @param method
	 *            ���ַ�ʽ
	 * @param yuzhidianneng
	 *            Ԥ�õ���
	 * @param beilv
	 *            ���뱶��
	 */
	public byte[] fnDianbiaoZouZi(int type, int method, int yuzhidianneng, int beilv) {
		// ��У��ĳ���
		byte[] data = new byte[76];
		byte[] value;
		byte[] ndata = new byte[4];

		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// ������
		data[2] = 0x6B;
		data[3] = VERSION;
		// ���ĳ���
		data[4] = 0x00;
		data[5] = (byte) (data.length + 1);
		// ������
		iflag = 6;
		// ��������
		data[iflag] = (byte) type;
		iflag++;
		// ���ַ�ʽ
		data[iflag] = (byte) method;
		Log.e("UF","���ַ�ʽ:"+method);
		iflag++;
		// Ԥ�õ���
		value = ByteUtil.intTo4ByteArray(yuzhidianneng);
		Log.e("UF","Ԥ�õ���:"+yuzhidianneng);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// ����
		value = ByteUtil.intTo4ByteArray(beilv);
		System.arraycopy(value, 0, data, iflag, value.length);
		iflag = iflag + 4;
		// �ۼƵ���
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
		// �ܳ���
		byte[] frame = new byte[77];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/**
	 * г������
	 * 
	 * @param count
	 *            г������
	 * @param args
	 *            1��A���ѹ�����ʣ�1��A����������ʡ�2��A���ѹ�����ʣ�2��A�����������...
	 */
	public byte[] fnXieBoSheZhi(int count, Vector args) {
		if ((count * 2) != args.size())
			return null;
		int dataLength = (count + 1) * 12 * 2;
		byte[] len = new byte[2];
		len = ByteUtil.int2Twobyte(dataLength);

		// ��У��ĳ���
		byte[] data = new byte[6 + 4 + dataLength];
		byte[] frameLength = new byte[2];
		frameLength = ByteUtil.int2Twobyte(data.length + 1);
		byte[] value;
		byte[] ndata = new byte[4];
		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// ������
		data[2] = 0x71;
		data[3] = (byte)0x01;
		// ���ĳ��� 2018-04-23
		data[4] = frameLength[1];
		data[5] = frameLength[0];
		// ������
		iflag = 5;

		data[++iflag]=(byte)0x02;
		data[++iflag] = len[0];
		data[++iflag] = (byte)0x00;
		//++iflag;
		data[++iflag] = (byte) count;
		++iflag;
		int index = 0;
		// stringתfloat*100����תint��ת���ֽ�byte
		for (int i = 0; i <= count; i++) {

			if (i == 0) {
				// ��ѹ������
				double d1 = 100;
				Log.i("ALTEK", i + 1 + "��ѹ�����ʣ�" + d1);
				int x1 = (int) (d1 * 100);
				byte[] bFrame1 = new byte[2];
				bFrame1 = ByteUtil.int2TwobyteRevert(x1);
				System.arraycopy(bFrame1, 0, data, iflag, bFrame1.length);
				Log.e("ALTEK","u han:"+ByteUtil.byte2HexStr(bFrame1));
				Log.e("ALTEK","u han index:"+iflag);
				iflag = iflag + 4;
				// ����������
				double d2 = 100;
				Log.i("ALTEK", i + 1 + "���������ʣ�" + d2);
				int x2 = (int) (d2 * 100);
				byte[] bFrame2 = new byte[2];
				bFrame2 = ByteUtil.int2TwobyteRevert(x2);
				System.arraycopy(bFrame2, 0, data, iflag, bFrame2.length);
				Log.e("ALTEK","i han:"+ByteUtil.byte2HexStr(bFrame1));
				Log.e("ALTEK","i han index:"+iflag);
				iflag = iflag + 20;
			} else {
				// ��ѹ������
				double d1 = Double.parseDouble(args.get(index).toString());
				Log.i("ALTEK", i + 1 + "��ѹ�����ʣ�" + d1);
				int x1 = (int) (d1 * 100);
				byte[] bFrame1 = new byte[2];
				bFrame1 = ByteUtil.int2TwobyteRevert(x1);
				Log.e("ALTEK","u han:"+ByteUtil.byte2HexStr(bFrame1));
				
				System.arraycopy(bFrame1, 0, data, iflag, bFrame1.length);
				Log.e("ALTEK","u han index:"+iflag);
				
				iflag = iflag + 4;
				// ����������
				double d2 = Double.parseDouble(args.get(index + 1).toString());
				Log.i("ALTEK", i + 1 + "���������ʣ�" + d2);
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
		// �ܳ���
		byte[] frame = new byte[6 + 4 + dataLength + 1];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}

	/**
	 * г������
	 * 
	 * @param count
	 *            г������
	 * @param args
	 *            1��A���ѹ�����ʣ���1��A���ѹ��λ����1��A�����������,��1��A�������λ����2��A���ѹ�����ʣ���2��A���ѹ��λ����2��A����������ʣ���2��A�������λ��...
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

		// ��У��ĳ���
		byte[] data = new byte[6 + 4 + dataLength];
		byte[] frameLength = new byte[2];
		frameLength = ByteUtil.int2Twobyte(data.length + 1);
		byte[] value;
		byte[] ndata = new byte[4];
		int iflag = 0;
		data[0] = HEADER1;
		data[1] = HEADER2;
		// ������
		data[2] = 0x71;
		data[3] = (byte)0x01;
		// ���ĳ��� 2018-04-23
		data[4] = frameLength[1];
		data[5] = frameLength[0];
		// ������
		iflag = 5;

		data[++iflag]=(byte)0x02;
		data[++iflag] = len[0];
		data[++iflag] = (byte)0x00;
		//++iflag;
		data[++iflag] = (byte) (count+1);
		++iflag;
		int index = 0;
		// stringתfloat*100����תint��ת���ֽ�byte
		for (int i = 0; i <= count; i++) {
			Log.i("ALTEK","xiebo count:"+count);
			
			if (i == 0) {
				// ��ѹ������
				double d1 = 100;
				Log.i("ALTEK", i + 1 + "��ѹ�����ʣ�" + d1);
				int x1 = (int) (d1 * 100);
				byte[] bFrame1 = new byte[2];
				bFrame1 = ByteUtil.int2TwobyteRevert(x1);
				System.arraycopy(bFrame1, 0, data, iflag, bFrame1.length);
				Log.e("ALTEK","u han:"+ByteUtil.byte2HexStr(bFrame1));
				Log.e("ALTEK","u han index:"+iflag);
				iflag = iflag + 4;
				// ����������
				double d2 = 100;
				Log.i("ALTEK", i + 1 + "���������ʣ�" + d2);
				int x2 = (int) (d2 * 100);
				byte[] bFrame2 = new byte[2];
				bFrame2 = ByteUtil.int2TwobyteRevert(x2);
				System.arraycopy(bFrame2, 0, data, iflag, bFrame2.length);
				Log.e("ALTEK","i han:"+ByteUtil.byte2HexStr(bFrame1));
				Log.e("ALTEK","i han index:"+iflag);
				iflag = iflag + 20;
			} else {
				// ��ѹ������
				double d1 = Double.parseDouble(args.get(index).toString());
				Log.i("ALTEK", i + 1 + "��ѹ�����ʣ�" + d1);
				int x1 = (int) (d1 * 100);
				byte[] bFrame1 = new byte[2];
				bFrame1 = ByteUtil.int2TwobyteRevert(x1);
				Log.e("ALTEK","u han:"+ByteUtil.byte2HexStr(bFrame1));
				System.arraycopy(bFrame1, 0, data, iflag, bFrame1.length);
				Log.e("ALTEK","u han index:"+iflag);
				iflag=iflag+2;
				
				//��ѹ��λ
				double d3 = Double.parseDouble(args.get(index+1).toString());
				Log.i("ALTEK", i + 1 + "��ѹ��λ��" + d3);
				int x3 = (int) (d3 * 100);
				byte[] bFrame3 = new byte[2];
				bFrame3 = ByteUtil.int2TwobyteRevert(x3);
				Log.e("ALTEK","u ��λ:"+ByteUtil.byte2HexStr(bFrame3));
				System.arraycopy(bFrame3, 0, data, iflag, bFrame3.length);
				Log.e("ALTEK","u ��λ index:"+iflag);
				iflag=iflag+2;
				
				// ����������
				double d2 = Double.parseDouble(args.get(index + 2).toString());
				Log.i("ALTEK", i + 1 + "���������ʣ�" + d2);
				int x2 = (int) (d2 * 100);
				byte[] bFrame2 = new byte[2];
				bFrame2 = ByteUtil.int2TwobyteRevert(x2);
				Log.e("ALTEK","i han:"+ByteUtil.byte2HexStr(bFrame2));
				
				System.arraycopy(bFrame2, 0, data, iflag, bFrame2.length);
				Log.e("ALTEK","i han index:"+iflag);
				iflag=iflag+2;
				
				//������λ
				double d4 = Double.parseDouble(args.get(index + 3).toString());
				Log.i("ALTEK", i + 1 + "������λ��" + d4);
				int x4 = (int) (d4 * 100);
				byte[] bFrame4 = new byte[2];
				bFrame4 = ByteUtil.int2TwobyteRevert(x4);
				Log.e("ALTEK","i ��λ:"+ByteUtil.byte2HexStr(bFrame4));
				
				System.arraycopy(bFrame4, 0, data, iflag, bFrame4.length);
				Log.e("ALTEK","i ��λ index:"+iflag);
				
				iflag = iflag + 18;
				index = i * 4;
			}
		}
		byte check = ByteUtil.SumCheck(data);
		// �ܳ���
		byte[] frame = new byte[6 + 4 + dataLength + 1];
		System.arraycopy(data, 0, frame, 0, data.length);
		frame[frame.length - 1] = check;
		return frame;
	}
}
