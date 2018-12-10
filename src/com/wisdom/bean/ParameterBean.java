package com.wisdom.bean;

public class ParameterBean {
	private int meterNumbers;
	private String meter1_no;
	private String meter2_no;
	private String meter3_no;
	private String maichongchangshu;
	private double meter_level=0.5;
	
	public double getMeter_level() {
		return meter_level;
	}
	public void setMeter_level(double meter_level) {
		this.meter_level = meter_level;
	}
	public int getMeterNumbers() {
		return meterNumbers;
	}
	public void setMeterNumbers(int meterNumbers) {
		this.meterNumbers = meterNumbers;
	}
	public String getMeter1_no() {
		return meter1_no;
	}
	public void setMeter1_no(String meter1_no) {
		this.meter1_no = meter1_no;
	}
	public String getMeter2_no() {
		return meter2_no;
	}
	public void setMeter2_no(String meter2_no) {
		this.meter2_no = meter2_no;
	}
	public String getMeter3_no() {
		return meter3_no;
	}
	public void setMeter3_no(String meter3_no) {
		this.meter3_no = meter3_no;
	}
	public String getMaichongchangshu() {
		return maichongchangshu;
	}
	public void setMaichongchangshu(String maichongchangshu) {
		this.maichongchangshu = maichongchangshu;
	}
	
}
