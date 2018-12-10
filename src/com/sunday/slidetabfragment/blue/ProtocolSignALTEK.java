package com.sunday.slidetabfragment.blue;

import com.wisdom.app.utils.ByteUtil;

import android.util.Log;

public class ProtocolSignALTEK implements ProtocolSign {
	private static final ProtocolSignALTEK instance = new ProtocolSignALTEK();

	public static ProtocolSignALTEK getInstance() {
		return instance;
	}

	@Override
	public int getSignLen() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public int getBodyLen(byte[] head) {
		int len = ByteUtil.byteArray2ToInt(head[5], head[4]);
		//Log.e("BlueManager", "BodyLen:" + len);
		return len - 6;
	}

	@Override
	public boolean checked(byte[] head) {
		return (head[0] == (byte) 0xEB) && (head[1] == (byte) 0x90);
	}

	@Override
	public boolean filterHeartFrame(byte[] frame) {
		//EB908001000703
		return frame[2] == (byte)0x80;
	}

	@Override
	public boolean filterModelFrame(byte[] frame) {
		//41542B54454D503F
		if((frame[0]==(byte)0x41)&&(frame[1]==(byte)0x54)&&(frame[2]==(byte)0x2B)&&(frame[3]==(byte)0x54)&&(frame[4]==(byte)0x45)&&(frame[5]==(byte)0x4D))
			return true;
		else
			return false;
	}

}
