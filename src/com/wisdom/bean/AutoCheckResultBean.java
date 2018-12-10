package com.wisdom.bean;

public class AutoCheckResultBean {
	private int id;
	private String no;
	private String test_type;
	private String power_type;
	private String Ub;
	private String Ib;
	private String Ur;
	private String Ir;
	private String power_factor;
	private String pinlv;
	private String quanshu;
	private String cishu;
	private String wucha_limit;
	private String result1;
	private String result2;
	private String result3;
	private String date;
	private String test_state="未完成";
	private Boolean check=false;//自动校验编辑方案表格是否选中

	public Boolean getCheck() {
		return check;
	}
	public void setCheck(Boolean check) {
		this.check = check;
	}
	private int schemeId;//方案外键
	
	public int getSchemeId() {
		return schemeId;
	}
	public void setSchemeId(int schemeId) {
		this.schemeId = schemeId;
	}
	public String getTest_state() {
		return test_state;
	}
	public void setTest_state(String test_state) {
		this.test_state = test_state;
	}
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public String getUb() {
		return Ub;
	}
	public void setUb(String ub) {
		Ub = ub;
	}
	public String getIb() {
		return Ib;
	}
	public void setIb(String ib) {
		Ib = ib;
	}
	public String getUr() {
		return Ur;
	}
	public void setUr(String ur) {
		Ur = ur;
	}
	public String getIr() {
		return Ir;
	}
	public void setIr(String ir) {
		Ir = ir;
	}
	public String getPower_factor() {
		return power_factor;
	}
	public void setPower_factor(String power_factor) {
		this.power_factor = power_factor;
	}
	public String getPinlv() {
		return pinlv;
	}
	public void setPinlv(String pinlv) {
		this.pinlv = pinlv;
	}
	public String getQuanshu() {
		return quanshu;
	}
	public void setQuanshu(String quanshu) {
		this.quanshu = quanshu;
	}
	public String getCishu() {
		return cishu;
	}
	public void setCishu(String cishu) {
		this.cishu = cishu;
	}
	public String getWucha_limit() {
		return wucha_limit;
	}
	public void setWucha_limit(String wucha_limit) {
		this.wucha_limit = wucha_limit;
	}
	public String getResult1() {
		return result1;
	}
	public void setResult1(String result1) {
		this.result1 = result1;
	}
	public String getResult2() {
		return result2;
	}
	public void setResult2(String result2) {
		this.result2 = result2;
	}
	public String getResult3() {
		return result3;
	}
	public void setResult3(String result3) {
		this.result3 = result3;
	}

	
}
