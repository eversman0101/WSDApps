package com.wisdom.app.utils;

public class MissionSingleInstance {
	private static MissionSingleInstance single=null;
	private String jiaodu="0";
	private String u="220";
	private String i="5";
	private String pinlv="50";
	private int meterCount=0;
	private boolean yuan_state=false;
	private int meterState=1;
	private boolean testState=false;
	private String systemtime=null;
	public String getSystemtime() {
		return systemtime;
	}
	public void setSystemtime(String systemtime) {
		this.systemtime = systemtime;
	}
	public boolean isTestState() {
		return testState;
	}
	public void setTestState(boolean testState) {
		this.testState = testState;
	}
	/**
	 * 负荷状态，1位虚负荷，2为实负荷,3虚负荷 常数校核
	 * */
	private int fuhe_state=0;
	private MissionSingleInstance(){}
	public static MissionSingleInstance getSingleInstance()
	{
		if(single==null)
			single= new MissionSingleInstance();
		return single;
	}
	public int getMeterState() {
		return meterState;
	}
	public void setMeterState(int meterState) {
		this.meterState = meterState;
	}
	public int getFuhe_state() {
		return fuhe_state;
	}
	public void setFuhe_state(int fuhe_state) {
		this.fuhe_state = fuhe_state;
	}
	public String getPinlv() {
		return pinlv;
	}
	public void setPinlv(String pinlv) {
		this.pinlv = pinlv;
	}
	public String getU() {
		return u;
	}
	public void setU(String u) {
		this.u = u;
	}
	public String getI() {
		return i;
	}
	public void setI(String i) {
		this.i = i;
	}
	public int getMeterCount() {
		return meterCount;
	}
	public void setMeterCount(int meterCount) {
		this.meterCount = meterCount;
	}
	public String getJiaodu() {
		return jiaodu;
	}
	public void setJiaodu(String jiaodu) {
		this.jiaodu = jiaodu;
	}
	public boolean isYuan_state() {
		return yuan_state;
	}
	public void setYuan_state(boolean yuan_state) {
		this.yuan_state = yuan_state;
	}
	
}
