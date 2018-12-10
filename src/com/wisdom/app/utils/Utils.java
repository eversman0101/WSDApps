package com.wisdom.app.utils;


import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import com.wisdom.bean.ParameterBean;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Utils {
	/**
     * 四舍五入
     * @param v
     * @param scale 精确位数
     * @return
     */
    public static double round(double v, int scale) {

        if (scale < 0) {

            throw new IllegalArgumentException("The scale must be a positive integer or zero");

        }

        BigDecimal b = new BigDecimal(Double.toString(v));

        BigDecimal one = new BigDecimal("1");

        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

    }
    /*
     * 获取小数点后的位数
     * */
    public static int getNumberDecimalDigits(Double balance) { 
    	int dcimalDigits = 0; 
    	String balanceStr = new BigDecimal(balance).toString(); 
    	Log.e("Utils","str_numer:"+balanceStr);
    	int indexOf = balanceStr.indexOf("."); 
    	if(indexOf > 0){ 
    	dcimalDigits = balanceStr.length() - 1 - indexOf; 
    	} 
    	return dcimalDigits; 
    }
    public static int getNumberDecimalDigits(BigDecimal balance) { 
    	int dcimalDigits = 0; 
    	String balanceStr = balance.toString(); 
    	int indexOf = balanceStr.indexOf("."); 
    	if(indexOf > 0){ 
    	dcimalDigits = balanceStr.length() - 1 - indexOf; 
    	} 
    	return dcimalDigits; 
    }
    public static int getNumberDecimalDigits(String balance) { 
    	int dcimalDigits = 0; 
    	
    	int indexOf = balance.indexOf("."); 
    	if(indexOf > 0){ 
    	dcimalDigits = balance.length() - 1 - indexOf; 
    	} 
    	return dcimalDigits; 
    }
	public static void reset(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("HomeFragment", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();// 閼惧嘲褰囩紓鏍帆閸ｏ拷
		editor.putBoolean("lock_state",false);
		editor.commit();
	}
	public static boolean isOpen(Context context)
	{
		SharedPreferences  sp = context.getSharedPreferences("HomeFragment", Context.MODE_PRIVATE);
		return sp.getBoolean("lock_state",false);
	}
	/**
	 * 鐏忓棝鏀ｉ悩鑸碉拷浣瑰瘮娑斿懎瀵�
	 * */
	public static void SaveLockState(Context context,String door_state,String lock_state)
	{
		SharedPreferences sp = context.getSharedPreferences("HomeFragment", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();// 閼惧嘲褰囩紓鏍帆閸ｏ拷
		if(door_state.equals("瀹告彃鍙ч梻锟�")&&lock_state.equals("瀹告彃鍙ч梻锟�"))
			editor.putBoolean("lock_state",false);
		else
			editor.putBoolean("lock_state",true);
		editor.commit();
	}
	public static byte SumCheck(byte[] bytes) {
		byte sum = 0;
		for (int i = 0; i < bytes.length; i++) {
			sum += bytes[i];
		}
		return sum;
	}

	public static byte SumCheck(byte C, byte[] A, byte[] user) {
		byte sum = 0;
		sum += C;
		for (int i = 0; i < A.length; i++) {
			sum += A[i];
		}
		for (int j = 0; j < user.length; j++) {
			sum += user[j];
		}
		return sum;
	}

	/**
	 * 閼惧嘲褰囪ぐ鎾冲缁崵绮洪弮鍫曟？
	 * 
	 * @return
	 */
	public static String GetNowDate() {
		String temp_str = "";
		Date dt = new Date();
		// HH閿熸枻鎷风ず24灏忔椂閿熸枻鎷� 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓绲焗閿熸枻鎷风ず12灏忔椂閿熸枻鎷�
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		temp_str = sdf.format(dt);
		return temp_str;
	}

	private static int getWeekOfDate(Date dt) {
		// String[] weekDays = {"閺勭喐婀￠弮锟�", "閺勭喐婀℃稉锟�", "閺勭喐婀℃禍锟�", "閺勭喐婀℃稉锟�", "閺勭喐婀￠崶锟�", "閺勭喐婀℃禍锟�",
		// "閺勭喐婀￠崗锟�"};
		int[] weekDays = { 7, 1, 2, 3, 4, 5, 6 };

		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		System.out.println("w:" + w);

		return weekDays[w];
	}

	public static byte[] GetNowA1DateTo6byte() {
		byte[] data = new byte[6];
		try {
			String temp_str = "";
			String week_day = "";

			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			temp_str = sdf.format(dt);
			dt = new Date();
			int iWeekDay = getWeekOfDate(dt);
			String[] bigT = temp_str.split(" ");
			String[] lT = bigT[0].split("-");
			String[] rT = bigT[1].split(":");

			int sec = Integer.valueOf(rT[2]);
			int min = Integer.valueOf(rT[1]);
			int hour = Integer.valueOf(rT[0]);

			int day = Integer.valueOf(lT[2]);
			int month = Integer.valueOf(lT[1]);
			int year = Integer.valueOf(lT[0]) % 2000;
			data[0] = (byte) ((byte) ((byte) ((byte) (sec / 10) << 4) & 0xf0) + (byte) ((byte) (sec % 10) & 0x0f));
			data[1] = (byte) ((byte) ((byte) ((byte) (min / 10) << 4) & 0xf0) + (byte) ((byte) (min % 10) & 0x0f));
			data[2] = (byte) ((byte) ((byte) ((byte) (hour / 10) << 4) & 0xf0) + (byte) ((byte) (hour % 10) & 0x0f));
			data[3] = (byte) ((byte) ((byte) ((byte) (day / 10) << 4) & 0xf0) + (byte) ((byte) (day % 10) & 0x0f));

			data[4] = (byte) ((byte) ((byte) ((byte) (iWeekDay) << 5) & 0xE0)
					+ (byte) ((byte) ((byte) (month / 10) << 4) & 0x10) + (byte) ((byte) (month % 10) & 0x0f));

			data[5] = (byte) ((byte) ((byte) ((byte) (year / 10) << 4) & 0xf0) + (byte) ((byte) (year % 10) & 0x0f));
			System.out.println(iWeekDay);
			System.out.println(temp_str);
			System.out.println(ByteUtil.BinaryToHexString(data));

		} catch (Exception ex) {
			data = null;
			Log.d("Utils", "閺嶇厧绱″鍌氱埗:" + ex.getMessage());
		}
		return data;
	}
	/**
	 * 获取电表数量、电表号1,2,3
	 * */
	public static ParameterBean fnGetMeterInfo(Context context)
	{
		ParameterBean bean=new ParameterBean();
		SharedPreferences dianbiaocanshu = context.getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
		String meter1_no = "0";
		String meter2_no = "0";
		String meter3_no = "0";
		String maichongchangshu="1200";
		double i_meter_level=0.5;
		/*String meter_level=dianbiaocanshu.getString("meter_level", "0.5级");
		if(meter_level.equals("0.2级"))
		i_meter_level=0.2;
		if(meter_level.equals("0.5级"))
			i_meter_level=0.5;
		if(meter_level.equals("1.0级"))
			i_meter_level=1;
		if(meter_level.equals("2.0级"))
			i_meter_level=2;*/
		int meter_numbers = 1;// 电表个数
		String meter_count = dianbiaocanshu.getString("meter_count", "1");
		if(!meter_count.equals(""))
		meter_numbers=Integer.valueOf(meter_count);
		meter1_no = dianbiaocanshu.getString("meter1_no", "");
		meter2_no = dianbiaocanshu.getString("meter2_no", "");
		meter3_no = dianbiaocanshu.getString("meter3_no", "");
		maichongchangshu = dianbiaocanshu.getString("meter1_constant", "1600");
		if (meter1_no.equals(""))
			meter1_no = "0";
		if (meter2_no.equals(""))
			meter2_no="0";
		if (meter3_no.equals(""))
			meter3_no="0";
		
		bean.setMeterNumbers(meter_numbers);
		bean.setMeter1_no(meter1_no);
		bean.setMeter2_no(meter2_no);
		bean.setMeter3_no(meter3_no);
		bean.setMaichongchangshu(maichongchangshu);
		bean.setMeter_level(i_meter_level);
		return bean;                                                                                                                                                                                                                                                     
	}
	/**
	 * 閼惧嘲褰噄p閸︽澘娼�
	 * 
	 * @param context
	 * @return
	 */
	public static String getIPAddress(Context context) {
		NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {// 瑜版挸澧犳担璺ㄦ暏2G/3G/4G缂冩垹绮�
				try {
					// Enumeration<NetworkInterface>
					// en=NetworkInterface.getNetworkInterfaces();
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
							.hasMoreElements();) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
								.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress();
							}
						}
					}
				} catch (SocketException e) {
					e.printStackTrace();
				}

			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {// 瑜版挸澧犳担璺ㄦ暏閺冪姷鍤庣純鎴犵捕
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());// 瀵版鍩孖PV4閸︽澘娼�
				return ipAddress;
			}
		} else {
			// 瑜版挸澧犻弮鐘电秹缂佹粏绻涢幒锟�,鐠囧嘲婀拋鍓х枂娑擃厽澧﹀锟界純鎴犵捕
		}
		return "IPNULL";
	}

	/**
	 * 鐏忓棗绶遍崚鎵畱int缁鐎烽惃鍑鏉烆剚宕叉稉绡爐ring缁鐎�
	 *
	 * @param ip
	 * @return
	 */
	public static String intIP2StringIP(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
	}

	public static String getSystemTime() {
		SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sm.format(new Date());
	}

	/**
	 * 闁俺绻冮柌鍥肠閺冨爼妫块妴渚�妫块梾鏂伙拷浣哄仯閺佹澘绶遍崙鐑樻闂傚瓨鏆熺紒锟�
	 * 
	 * @param time
	 *            闁插洭娉﹂弮鍫曟？
	 * @param interval
	 *            闁插洭娉﹂梻鎾
	 * @param count
	 *            闁插洭娉﹂悙瑙勬殶
	 * @return
	 */
	public static String[] getTimeArray(String time, int interval, int count) {
		String[] arrtime = time.split(":");
		String[] arrTotal = new String[count];
		int hour = Integer.valueOf(arrtime[0]);
		int min = Integer.valueOf(arrtime[1]);

		for (int i = 0; i < count; i++) {
			if (min < 10)
				arrTotal[i] = hour + "閺冿拷0" + min + "閸掞拷";
			else
				arrTotal[i] = hour + "閺冿拷" + min + "閸掞拷";

			min = min + interval;
			if (min == 60) {
				min = 0;
				hour++;
				if (hour == 24)
					hour = 0;
			}
		}
		return arrTotal;
	}

	/**
	 * 闁俺绻冮柌鍥肠閺冨爼妫块妴渚�妫块梾鏂伙拷浣哄仯閺佹澘绶遍崙鍝勫嬀閺堝牊妫╅弮璺哄瀻閺冨爼妫块弫鎵矋
	 * 
	 * @param time
	 *            闁插洭娉﹂弮鍫曟？
	 * @param interval
	 *            闁插洭娉﹂梻鎾
	 * @param count
	 *            闁插洭娉﹂悙瑙勬殶
	 * @return
	 */
	public static String[] getTotalTimeArray(String time, int interval, int count) {
		// "yyyy-MM-dd HH:mm:ss"
		if(interval==1)
			interval=15;
		else if(interval==255)
			interval=1;
		else if(interval==254)
			interval=5;
		String[] arrBig = time.split(" ");
		String[] lt = arrBig[0].split("-");
		String[] rt = arrBig[1].split(":");

		String[] arrTotal = new String[count];
		int hour = Integer.valueOf(rt[0]);
		int min = Integer.valueOf(rt[1]);
		int month = Integer.valueOf(lt[1]);
		int day = Integer.valueOf(lt[2]);
		int year = Integer.valueOf(lt[0]);

		for (int i = 0; i < count; i++) {
			arrTotal[i] = month + "閺堬拷" + day + "閺冿拷" + hour + "閺冿拷" + min + "閸掞拷";

			min = min + interval;
			if (min == 60) {
				min = 0;
				hour++;
				if (hour == 24) {
					hour = 0;
					day++;
					if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
							|| month == 12) {
						if (day == 32) {
							day = 1;
							month++;
							if (month == 13)
								month = 1;
						}
					} else if (month == 4 || month == 6 || month == 9 || month == 11) {

						if (day == 31) {
							day = 1;
							month++;
							if (month == 13)
								month = 1;
						}
					} else if (month == 2) {
						if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
							// 鐠囥儱鍕鹃弰顖炴８楠烇拷
							if (day == 30) {
								day = 1;
								month++;
								if (month == 13)
									month = 1;
							}
						}
						else
						{
							if (day == 29) {
								day = 1;
								month++;
								if (month == 13)
									month = 1;
							}
						}
					}
				}
			}

		}
		return arrTotal;
	}
}
