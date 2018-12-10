package com.wisdom.app.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ByteUtil {
	/** 
     * @功能: 10进制串转为BCD码 
     * @参数: 10进制串 
     * @结果: BCD码 
     */  
    public static byte[] str2Bcd(String asc) {  
        int len = asc.length();  
        int mod = len % 2;  
        if (mod != 0) {  
            asc = "0" + asc;  
            len = asc.length();  
        }  
        byte abt[] = new byte[len];  
        if (len >= 2) {  
            len = len / 2;  
        }  
        byte bbt[] = new byte[len];  
        abt = asc.getBytes();  
        int j, k;  
        for (int p = 0; p < asc.length() / 2; p++) {  
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {  
                j = abt[2 * p] - '0';  
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {  
                j = abt[2 * p] - 'a' + 0x0a;  
            } else {  
                j = abt[2 * p] - 'A' + 0x0a;  
            }  
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {  
                k = abt[2 * p + 1] - '0';  
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {  
                k = abt[2 * p + 1] - 'a' + 0x0a;  
            } else {  
                k = abt[2 * p + 1] - 'A' + 0x0a;  
            }  
            int a = (j << 4) + k;  
            byte b = (byte) a;  
            bbt[p] = b;  
        }  
        return bbt;  
    }  
	/**
	 * 从持久化数据里计算出电表个数
	 * */
	public static int getMeterCount(Context context)
	{
		int count=1;
		SharedPreferences sharedPreferences = context.getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE); //私有数据
		String meter_count=sharedPreferences.getString("meter_count", "");
		if(meter_count!="")
			count=Integer.valueOf(meter_count);
		return count;
	}
	/**
	 * 获取当前系统时间
	 * 
	 * @return
	 */
	public static String GetNowDate() {
		String temp_str = "";
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		temp_str = sdf.format(dt);
		return temp_str;
	}
	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param src
	 *            String Byte字符串，每个Byte之间没有分隔符(字符范围:0-9 A-F)
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		/* 对输入值进行规范化整理 */
		src = src.trim().replace(" ", "").toUpperCase(Locale.US);
		// 处理值初始化
		int m = 0, n = 0;
		int iLen = src.length() / 2; // 计算长度
		byte[] ret = new byte[iLen]; // 分配存储空间

		for (int i = 0; i < iLen; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = (byte) (Integer.decode("0x" + src.substring(i * 2, m) + src.substring(m, n)) & 0xFF);
		}
		return ret;
	}

	// 16进制字符串 –>int [ ]
	public static int[] hexStringToInt(String hex) {
		int len = (hex.length() / 2);
		int[] result = new int[len];
		char[] achar = hex.toCharArray();
		int pos = 0;
		int iH = 0, iL = 0;
		for (int i = 0; i < len; i++) {
			pos = i * 2;
			iH = Character.getNumericValue(achar[pos]);
			iL = Character.getNumericValue(achar[pos + 1]);
			result[i] = (int) (iH * 0x10 + iL);
		}
		return result;
	}

	// int[ ] –>16进制字符串
	public static String int2hex(int[] buffer, int iInLen) {
		String h = "";
		for (int i = 0; i < iInLen; i++) {
			String temp = Integer.toHexString(buffer[i] & 0xFF);
			if (temp.length() == 1) {
				temp = "0" + temp;
			}
			h = h + temp;
		}
		return h;
	}

	// 16进制字符串 –>byte[ ]
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static byte[] float01to4Byte(float data)
	{
		int x=(int) (data*100);
		return intTo4ByteArray(x);
	}
	public static byte[] float001to4Byte(float data)
	{
		int x=(int) (data*1000);
		return intTo4ByteArray(x);
	}
	public static byte[] float0001to4Byte(float data)
	{
		int x=(int) (data*10000);
		return intTo4ByteArray(x);
	}
	/**
	 * 4个字节转int
	 * */
	public static int byteArrayToInt(byte a,byte b,byte c,byte d) {   
		int x=0;
		int a1=0,a2=0,a3=0,a4=0;
		a1=a&0xff;
		a2=(b&0xff)*0x100;
		a3=(c&0xff)*0x10000;
		a4=(d&0xff)*0x1000000;
		x= a1+a2+a3+a4;
		byte[] test=new byte[4];
		test[0]=a;
		test[1]=b;
		test[2]=c;
		test[3]=d;
		//Log.e("UF","btint:"+ByteUtil.byte2HexStr(test));
		return x;
	}
	//前高后低
	public static int byteArrayToIntRevert(byte d,byte c,byte b,byte a) {   
		int x=0;
		int a1=0,a2=0,a3=0,a4=0;
		a1=a&0xff;
		a2=(b&0xff)*0x100;
		a3=(c&0xff)*0x10000;
		a4=(d&0xff)*0x1000000;
		x= a1+a2+a3+a4;
		byte[] test=new byte[4];
		test[0]=a;
		test[1]=b;
		test[2]=c;
		test[3]=d;
		Log.e("UF","btint:"+ByteUtil.byte2HexStr(test));
		return x;
	}
	/**
	 * 2个字节转int
	 * */
	public static int byteArray2ToInt(byte c,byte d) {   
		int x=0;
		int b1=c&0xff;
		int b2=(d&0xff)*0x100;
		x= b1+b2;
		return x;
	}
	/**
	 * 两个byte转short
	 * */
	public static short byte2Short(byte[] b) {  
        return (short) (((b[1] << 8) | b[0] & 0xff));  
    }
	/**
	 * 两个byte转short,左低右高
	 * */
	public static short byte2Short(byte a,byte b) {  
        return (short) (((b << 8) | a & 0xff));  
    } 
	/**
	 * 浮点型byte数组转float
	 * */
	public static float byte2Float(byte a,byte b,byte c,byte d) { 
	        int accum = 0; 
	        accum = accum|(a & 0xff) << 0;
	        accum = accum|(b & 0xff) << 8; 
	        accum = accum|(c & 0xff) << 16; 
	        accum = accum|(d & 0xff) << 24; 
	        //System.out.println(accum);
	        float f=Float.intBitsToFloat(accum);
	        BigDecimal decimal= new BigDecimal(f);  
	        float f_result =   decimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();  
	        return f_result; 
	}
	/**
	 * 8个字节转long
	 * */
	public static long bytes2Long(byte a1,byte a2,byte a3,byte a4,byte a5,byte a6,byte a7,byte a8) {  
		byte[] bb=new byte[8];
		bb[0]=a1;
		bb[1]=a2;
		bb[2]=a3;
		bb[3]=a4;
		bb[4]=a5;
		bb[5]=a6;
		bb[6]=a7;
		bb[7]=a8;
		
		Log.i("bytes2long","bb:"+ByteUtil.BinaryToHexString(bb));
		long x=0;
		x=(a1 & 0xFF) |   
          (a2 & 0xFF) <<  8 |   
          (a3 & 0xFF) << 16 |   
          (a4 & 0xFF) << 24 |
          (a5 & 0xFF) << 32 |
		  (a6 & 0xFF) << 40 |
		  (a7 & 0xFF) << 48 |
		  (a8 & 0xFF) << 56 ;
		return x;
	}  
	public static long bytes2Long2(byte a1,byte a2,byte a3,byte a4,byte a5,byte a6,byte a7,byte a8) { 
	       return ((((long) a8 & 0xff) << 56) 
	               | (((long) a7 & 0xff) << 48) 
	               | (((long) a6 & 0xff) << 40) 
	               | (((long) a5 & 0xff) << 32) 
	               | (((long) a4 & 0xff) << 24) 
	               | (((long) a3 & 0xff) << 16) 
	               | (((long) a2 & 0xff) << 8) | (((long) a1 & 0xff) << 0)); 
	  }
	/**
	 * int除1000保留3位小数
	 * */
	public static String int1000ToString(int x)
	{
		DecimalFormat df=new DecimalFormat("0.000");
		return df.format((float)x/1000);
	}
	/**
	 * int除10000保留4位小数
	 * */
	public static String int10000ToString(int x)
	{
		DecimalFormat df=new DecimalFormat("0.0000");
		return df.format((float)x/10000);
	}
	
	/**
	 * int除1000保留2位小数
	 * */
	public static String int1000ToStringP2(int x)
	{
		DecimalFormat df=new DecimalFormat("0.00");
		return df.format((float)x/1000);
	}
	/**
	 * int除10000保留2位小数
	 * */
	public static String int10000ToStringP2(int x)
	{
		DecimalFormat df=new DecimalFormat("0.00");
		return df.format((float)x/10000);
	}
	/**
	 * int除1000 000保留3位小数
	 * */
	public static String int1000000ToString(int x)
	{
		DecimalFormat df=new DecimalFormat("0.000");
		return df.format((float)x/1000000);
	}
	/**
	 * int除1000 000保留2位小数
	 * */
	public static String int1000000ToStringP2(int x)
	{
		DecimalFormat df=new DecimalFormat("0.00");
		return df.format((float)x/1000000);
	}
	/**
	 * int除100000保留3位小数
	 * */
	public static String int100000ToString(int x)
	{
		DecimalFormat df=new DecimalFormat("0.000");
		return df.format((float)x/100000);
	}
	/**
	 * int除100保留两位小数
	 * */
	public static String int100ToString(int x)
	{
		DecimalFormat df=new DecimalFormat("0.00");
		return df.format((float)x/100);
	}
	/**
	 * long除1000保留3位小数
	 * */
	public static String long1000ToString(long x)
	{
		DecimalFormat df=new DecimalFormat("0.000");
		return df.format((float)x/1000);
	}
	public static String BinaryToHexString(byte[] bytes) {
		String hexStr = "0123456789ABCDEF";
		String result = "";
		String hex = "";
		for (byte b : bytes) {
			hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
			hex += String.valueOf(hexStr.charAt(b & 0x0F));
			result += hex + " ";
		}
		return result;
	}

	public static String bytes2hex02(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes) {
			// 灏嗘瘡涓瓧鑺備笌0xFF杩涜涓庤繍绠楋紝鐒跺悗杞寲涓?10杩涘埗锛岀劧鍚庡?熷姪浜嶪nteger鍐嶈浆鍖栦负16杩涘埗
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)// 姣忎釜瀛楄妭8涓猴紝杞负16杩涘埗鏍囧織锛?2涓?16杩涘埗浣?
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}

	/**
	 * bytes转锟斤拷锟斤拷十锟斤拷锟斤拷锟斤拷锟街凤拷锟斤拷
	 */
	public static String byte2HexStr(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			// if (n<b.length-1) hs=hs+":";
		}
		return hs.toUpperCase();
	}

	///
	public static byte string2byte(String bString) {
		byte result = 0;
		for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
			result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
		}
		return result;
	}

	/**
	 * byte转2锟斤拷锟斤拷string
	 */
	public static String byte2string(byte b) {

		int z = b;
		z |= 256;
		String str = Integer.toBinaryString(z);
		int len = str.length();
		return str.substring(len - 8, len);
	}

	/**
	 * 锟斤拷byte转锟斤拷为一锟斤拷锟斤拷锟斤拷为8锟斤拷byte锟斤拷锟介，锟斤拷锟斤拷每锟斤拷值锟斤拷锟斤拷bit
	 */
	public static byte[] getBooleanArray(byte b) {
		byte[] array = new byte[8];
		for (int i = 7; i >= 0; i--) {
			array[i] = (byte) (b & 1);
			b = (byte) (b >> 1);
		}
		return array;
	}

	/**
	 * int转2锟街斤拷byte[],锟斤拷位锟斤拷前锟斤拷锟斤拷位锟节后，碉拷位锟斤拷尾锟斤拷锟斤拷10
	 * 
	 * @param res
	 * @return
	 */
	public static byte[] int2byte(int res) {
		byte[] targets = new byte[2];

		targets[0] = (byte) ((res << 2) + 2 & 0xff);// 锟斤拷锟轿?
		targets[1] = (byte) ((res >> 6) & 0xff);// 锟轿碉拷位

		return targets;
	}
	/**
	 * int转2字节数组，低字节在前，高字节在后
	 * */
	public static byte[] int2Twobyte(int integer) {
		int byteNum = 2;
		byte[] byteArray = new byte[2];

		for (int n = 0; n < byteNum; n++)
			byteArray[n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}
	/**
	 * int转2字节数组，高字节在前，低字节在后
	 * */
	public static byte[] int2TwobyteRevert(int integer) {
		int byteNum = 2;
		byte[] byteArray = new byte[2];
		byte[] byteArray1 = new byte[2];

		for (int n = 0; n < byteNum; n++)
			byteArray[n] = (byte) (integer >>> (n * 8));
		byteArray1[0]=byteArray[1];
		byteArray1[1]=byteArray[0];
		return (byteArray1);
	}
	/**
	 * 锟斤拷锟斤拷转锟斤拷为锟斤拷锟斤拷锟斤拷byte锟斤拷锟斤拷位转为锟斤拷锟斤拷位锟斤拷十位转为锟斤拷锟斤拷位
	 * 
	 * @param res
	 * @return
	 */
	public static byte intToDoublebyte(int res) {
		int i = Integer.valueOf(res);
		String devIdString = Integer.toHexString(i);
		byte devBin = (byte) Integer.parseInt(devIdString, 16);
		return devBin;
	}

	/**
	 * int转4字节数组
	 * 
	 * @param integer
	 * @return
	 */
	public static byte[] intTo4ByteArray(final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[4];

		for (int n = 0; n < byteNum; n++)
			byteArray[n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}
	/**  
     * int到byte[] 17/12/01
     * @param i  
     * @return
     */  
    public static byte[] intToByteArray(int i) {     
          byte[] result = new byte[4];    
          //由高位到低位  
          result[0] = (byte)((i >> 24) & 0xFF);  
          result[1] = (byte)((i >> 16) & 0xFF);  
          result[2] = (byte)((i >> 8) & 0xFF);  
          result[3] = (byte)(i & 0xFF);  
          return result;  
    } 
    /**
     * 通过byte数组取得float
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    public static float getFloat(byte a,byte b,byte c,byte d) {
        int l;
        l =a;
        l &= 0xff;
        l |= ((long) b << 8);
        l &= 0xffff;
        l |= ((long) c << 16);
        l &= 0xffffff;
        l |= ((long) d << 24);
        float f=Float.intBitsToFloat(l);
        BigDecimal decimal= new BigDecimal(f);  
        float f_result =   decimal.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue();  
        
        return f_result;
    }
	/**
	 * 4字节数组转int
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	/**
	 * 锟斤拷锟斤拷byte转锟斤拷为int,锟斤拷锟斤拷锟斤拷锟斤拷频锟杰讹拷锟斤拷
	 * 
	 * @param a
	 *            锟斤拷位
	 * @param b
	 *            锟斤拷位
	 * @return
	 */
	public static int TwobyteToInt(byte a, byte b) {
		int x = a;
		int y = b << 8;
		return x + y;
	}

	/**
	 * 求校验和
	 */
	public static byte SumCheck(byte[] data) {
		byte sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}

		return sum;
	}

	/**
	 * int
	 * 
	 * @param integer
	 * @return
	 */
	public static byte[] intTo2ByteArray(final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[2];

		for (int n = 0; n < byteNum; n++)
			byteArray[1 - n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}

	/**
	 * 3瀛楄妭鏁扮粍杞琭loat锛孉.25
	 * 
	 * @param data
	 */
	public static float A25Byte3toFloat(byte[] data) {
		float pHundred = 0;
		float pThousand = 0;
		float pTen = 0;
		float Count = 0;
		float Ten = 0;
		float Hundred = 0;
		float fTotal = 0;
		try {
			if (data.length == 3) {
				pThousand = data[0] & 0x0f;
				pHundred = ((data[0] & 0xff) >> 4) & 0x0f;
				pTen = data[1] & 0x0f;
				Count = ((data[1] & 0xff) >> 4) & 0x0f;
				Ten = data[2] & 0x0f;
				Hundred = (data[2] >> 4) & 0x0f;

				fTotal = (float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1 + pHundred * 0.01 + pThousand * 0.001);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fTotal;
	}

	/**
	 * 鏃ユ湡杞?5瀛楄妭byte A.15
	 * 
	 * @param time
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static byte[] A15Stringto5byte(String time) {
		byte[] data = new byte[5];// 鍒? 鏃? 鏃? 鏈? 骞?
		// 2018-02-09 12:23:23
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// time=df.format(new Date());
		String[] arr = time.split(" ");
		String[] arrBigT = arr[0].split("-");
		String[] arrSmallT = arr[1].split(":");
		int min = Integer.valueOf(arrSmallT[1]);
		int hour = Integer.valueOf(arrSmallT[0]);

		int day = Integer.valueOf(arrBigT[2]);
		int month = Integer.valueOf(arrBigT[1]);
		int year = Integer.valueOf(arrBigT[0]);
		// data[0]=(byte) ((minL<<4)&0xF0+minR&0x0F);
		byte a = (byte) (((byte) (min / 10) << 4) & 0xF0);
		byte b = (byte) ((byte) (min % 10) & 0x0F);
		data[0] = (byte) (a + b);
		a = (byte) (((byte) (hour / 10) << 4) & 0xF0);
		b = (byte) ((byte) (hour % 10) & 0x0F);
		data[1] = (byte) (a + b);
		a = (byte) (((byte) (day / 10) << 4) & 0xF0);
		b = (byte) ((byte) (day % 10) & 0x0F);
		data[2] = (byte) (a + b);
		a = (byte) (((byte) (month / 10) << 4) & 0xF0);
		b = (byte) ((byte) (month % 10) & 0x0F);
		data[3] = (byte) (a + b);
		a = (byte) (((byte) ((year % 2000) / 10) << 4) & 0xF0);
		b = (byte) ((byte) ((year % 2000) % 10) & 0x0F);
		data[4] = (byte) (a + b);
		return data;
	}

	/**
	 * 2瀛楄妭byte杞琭loat,A.5
	 * 
	 * @param data
	 * @return
	 */
	public static float A5Byte2toFloat(byte[] data) {
		float pTen = 0;
		float Count = 0;
		float Ten = 0;
		float Hundred = 0;
		float fTotal = 0;
		int INDEX = 0;
		try {
			pTen = data[0] & 0x0f;
			Count = ((data[0] & 0xff) >> 4) & 0x0f;
			Ten = data[1] & 0x0f;
			Hundred = ((data[1] & 0xff) >> 4) & 0x07;
			INDEX = (data[1] & 0xff) >> 7;
			if (INDEX == 0)
				fTotal = (float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1);
			else if (INDEX == 1)
				fTotal = -(float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fTotal / 10;
	}

	/**
	 * 2 ge byte杞琭loat,A.5
	 * 
	 * @param data
	 * @return
	 */
	public static float A5ByteByte2toFloat(byte data1, byte data2) {
		float pTen = 0;
		float Count = 0;
		float Ten = 0;
		float Hundred = 0;
		float fTotal = 0;
		int INDEX = 0;
		try {
			pTen = data1 & 0x0f;
			Count = ((data1 & 0xff) >> 4) & 0x0f;
			Ten = data2 & 0x0f;
			Hundred = ((data2 & 0xff) >> 4) & 0x07;
			INDEX = (data2 & 0xff) >> 7;
			if (INDEX == 0)
				fTotal = (float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1);
			else if (INDEX == 1)
				fTotal = -(float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fTotal;
	}

	/**
	 * 2瀛楄妭byte杞琭loat,A.7
	 * 
	 * @param data
	 * @return
	 */
	public static float A7Byte2toFloat(byte[] data) {
		float pTen = 0;
		float Count = 0;
		float Ten = 0;
		float Hundred = 0;
		float fTotal = 0;
		try {
			if (data.length == 2) {
				pTen = data[0] & 0x0f;
				Count = ((data[0] & 0xff) >> 4) & 0x0f;
				Ten = data[1] & 0x0f;
				Hundred = ((data[1] & 0xff) >> 4) & 0x0f;

				fTotal = (float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1);
				Log.i("婀垮害:", "鐧? " + Hundred + "鍗? " + Count + "鍗佸垎浣? " + pTen * 0.1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fTotal;
	}

	public static float A7Humanity(byte[] data) {
		float pTen = 0;
		float Count = 0;
		float Ten = 0;
		float Hundred = 0;
		float fTotal = 0;
		try {
			if (data.length == 2) {
				pTen = data[0] & 0x0f;
				Count = ((data[0] & 0xff) >> 4) & 0x0f;
				Ten = data[1] & 0x0f;
				Hundred = ((data[1] & 0xff) >> 4) & 0x0f;

				fTotal = (float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1);
				Log.i("婀垮害:", "鐧? " + Hundred + "鍗? " + Ten + "涓綅 " + Count + "鍗佸垎浣? " + pTen * 0.1);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fTotal;
	}

	public static float A7HumadityByteByte2toFloat(byte data1, byte data2) {
		float pTen = 0;
		float Count = 0;
		float Ten = 0;
		float Hundred = 0;
		float fTotal = 0;
		try {
			pTen = data1 & 0x0f;
			Count = ((data1 & 0xff) >> 4) & 0x0f;
			Ten = data2 & 0x0f;
			Hundred = ((data2 & 0xff) >> 4) & 0x0f;

			fTotal = (float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1);
			Log.i("婀垮害:", "鐧? " + Hundred + "鍗? " + Ten + "涓綅 " + Count + "鍗佸垎浣? " + pTen * 0.1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fTotal;
	}

	/**
	 * 2ge byte杞琭loat,A.7
	 * 
	 * @param data
	 * @return
	 */
	public static float A7ByteByte2toFloat(byte data1, byte data2) {
		float pTen = 0;
		float Count = 0;
		float Ten = 0;
		float Hundred = 0;
		float fTotal = 0;
		try {
			pTen = data1 & 0x0f;
			Count = ((data1 & 0xff) >> 4) & 0x0f;
			Ten = data2 & 0x0f;
			Hundred = ((data2 & 0xff) >> 4) & 0x0f;

			fTotal = (float) (Hundred * 100 + Ten * 10 + Count + pTen * 0.1);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fTotal;
	}

	/**
	 * 3瀛楄妭byte杞琭loat,A.9
	 * 
	 * @param data
	 * @return
	 */
	public static float A9Byte3toFloat(byte[] data) {
		float pTen = 0;
		float pHundred = 0;
		float pThousand = 0;
		float p100Hundred = 0;

		float Count = 0;
		float Ten = 0;
		float fTotal = 0;
		try {
			if (data.length == 3) {
				p100Hundred = data[0] & 0x0f;
				pThousand = ((data[0] & 0xff) >> 4) & 0x0f;
				pHundred = data[1] & 0x0f;
				pTen = ((data[1] & 0xff) >> 4) & 0x0f;
				Count = data[2] & 0x0f;
				Ten = ((data[2] & 0xff) >> 4) & 0x0f;

				fTotal = (float) (Ten * 10 + Count + pTen * 0.1 + pHundred * 0.01 + pThousand * 0.001
						+ p100Hundred * 0.0001);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return fTotal;
	}

	/**
	 * 5瀛楄妭byte杞椂闂? A.15
	 * 
	 * @param data
	 * @return
	 */
	public static String A15BytetoString(byte[] data) {
		String sRet = "";
		if (data != null)
			sRet = oneByte2TenInt(data[4]) + "-" + oneByte2TenInt(data[3]) + "-" + oneByte2TenInt(data[2]) + " "
					+ oneByte2TenInt(data[1]) + ":" + oneByte2TenInt(data[0]);
		return sRet;
	}

	/**
	 * 1瀛楄妭楂樺洓浣嶈浆10浣嶏紝浣庡洓浣嶈浆涓綅
	 * 
	 * @param data
	 * @return 鏁村舰
	 */
	private static int oneByte2TenInt(byte data) {
		int y = data & 0x0f;
		int x = ((data & 0xff) >> 4) & 0x0f;
		return x * 10 + y;
	}

	public static String A1BytetoString(byte[] data) {
		int sec = 0, min = 0, hour = 0, day = 0, weekday = 0, month = 0, year = 0;
		int ten = ((data[0] & 0xff) >> 4) * 10;
		int count = data[0] & 0x0f;
		sec = ten + count;
		ten = ((data[1] & 0xff) >> 4) * 10;
		count = data[1] & 0x0f;
		min = ten + count;
		ten = ((data[2] & 0xff) >> 4) * 10;
		count = data[2] & 0x0f;
		hour = ten + count;
		ten = ((data[3] & 0xff) >> 4) * 10;
		count = data[3] & 0x0f;
		day = ten + count;
		weekday = (data[4] & 0xff) >> 5;
		ten = (((data[4] & 0xff) >> 4) & 0x01) * 10;
		count = data[4] & 0x0f;
		month = ten + count;
		ten = ((data[5] & 0xff) >> 4) * 10;
		count = data[5] & 0x0f;
		year = ten + count;
		return String.valueOf(year + "骞?" + month + "鏈?" + day + "鏃?")
				+ String.valueOf(" " + hour + "鏃?" + min + "鍒?" + sec + "绉?");
	}
}
