package com.wisdom.bean;

import java.io.Serializable;

/**
 * @author Jinjingyun
 * 2018/03/19
 * */
public class ACNL_JiBenWuChaBean extends JiBenWuChaBean implements Serializable{
	private String test_type;
	/**
	 * 电能类型
	 * */
	private String power_type;
	/**
	 * 电压比例
	 * */
	private String ur;
	/**
	 * 电流比例
	 * */
	private String ir;
	/**
	 * 校验结果：合格/不合格
	 * */
	private String result;
	/**
	 * 频率
	 * */
	private String pinlv;
	/**
	 * 误差限值
	 * */
	private String error_limit;
	
	public String getTest_type() {
		return test_type;
	}
	public void setTest_type(String test_type) {
		this.test_type = test_type;
	}
	public String getPower_type() {
		return power_type;
	}
	public void setPower_type(String power_type) {
		this.power_type = power_type;
	}
	public String getUr() {
		return ur;
	}
	public void setUr(String ur) {
		this.ur = ur;
	}
	public String getIr() {
		return ir;
	}
	public void setIr(String ir) {
		this.ir = ir;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getPinlv() {
		return pinlv;
	}
	public void setPinlv(String pinlv) {
		this.pinlv = pinlv;
	}
	public String getError_limit() {
		return error_limit;
	}
	public void setError_limit(String error_limit) {
		this.error_limit = error_limit;
	}
	
}
