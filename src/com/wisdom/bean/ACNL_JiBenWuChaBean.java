package com.wisdom.bean;

import java.io.Serializable;

/**
 * @author Jinjingyun
 * 2018/03/19
 * */
public class ACNL_JiBenWuChaBean extends JiBenWuChaBean implements Serializable{
	private String test_type;
	/**
	 * ��������
	 * */
	private String power_type;
	/**
	 * ��ѹ����
	 * */
	private String ur;
	/**
	 * ��������
	 * */
	private String ir;
	/**
	 * У�������ϸ�/���ϸ�
	 * */
	private String result;
	/**
	 * Ƶ��
	 * */
	private String pinlv;
	/**
	 * �����ֵ
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
