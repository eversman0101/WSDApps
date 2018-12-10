package com.wisdom.update;

public class UpdateInformation {
	public static String appname = "";
    public static int localVersion = 1;// ���ذ汾
    public static String versionName = ""; // ���ذ汾��
    public static int serverVersion = 1;// �������汾
    public static int serverFlag = 0;// ��������־
    public static int lastForce = 0;// ֮ǰǿ�������汾
    public static String updateurl = "";// ��������ȡ��ַ
    public static String upgradeinfo = "";// ������Ϣ

    public static String downloadDir = "";// ����Ŀ¼
    
    
    
    /*OkhttpManager.getAsync(Config.UPDATE_URL, new OkhttpManager.DataCallBack() {
        @Override
        public void requestFailure(Request request, Exception e) {

        }
        @Override
        public void requestSuccess(String result) {
            try {
                Log.d("wuyiunlei",result);
                JSONObject object = new JSONObject(result);
                UpdateInfoModel model = new UpdateInfoModel();
                model.setAppname(object.getString("appname"));
                model.setLastForce(object.getString("lastForce"));
                model.setServerFlag(object.getString("serverFlag"));
                model.setServerVersion(object.getString("serverVersion"));
                model.setUpdateurl(object.getString("updateurl"));
                model.setUpgradeinfo(object.getString("upgradeinfo"));
                tmpMap.put(DeliverConsts.KEY_APP_UPDATE, model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //���͹㲥
            sendBroadcast(new Intent(UpdateReceiver.UPDATE_ACTION));
        }
    });*/
    
    
}
