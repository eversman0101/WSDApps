package com.wisdom.update;

public class UpdateInfoModel {
	//app����
    private String appname;
    //��������app�汾
    private String serverVersion;
    //��������־
    private String serverFlag;
    //ǿ������
    private String lastForce;
    //app���°汾��ַ
    private String updateurl;
    //������Ϣ
    private String upgradeinfo;
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getServerVersion() {
		return serverVersion;
	}
	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}
	public String getServerFlag() {
		return serverFlag;
	}
	public void setServerFlag(String serverFlag) {
		this.serverFlag = serverFlag;
	}
	public String getLastForce() {
		return lastForce;
	}
	public void setLastForce(String lastForce) {
		this.lastForce = lastForce;
	}
	public String getUpdateurl() {
		return updateurl;
	}
	public void setUpdateurl(String updateurl) {
		this.updateurl = updateurl;
	}
	public String getUpgradeinfo() {
		return upgradeinfo;
	}
	public void setUpgradeinfo(String upgradeinfo) {
		this.upgradeinfo = upgradeinfo;
	}
    
}
