package com.wisdom.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdom.app.activity.R;

import net.sf.json.JSONObject;


public class Update {
    UpdateInfoModel mModel = null;
    Context mContext = null;
    private static Update instance = null;

    public static Update getInstance() {
        if (instance == null)
            instance = new Update();
        return instance;
    }

    public boolean checkUpdate(Context context) {
        this.mContext = context;
        fnGetUpdateInfo();
        return false;
    }

    private void fnGetUpdateInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//鍒涘缓OkHttpClient瀵硅薄
                    Request request = new Request.Builder()
                            .url(Web.root)//璇锋眰鎺ュ彛銆傚鏋滈渶瑕佷紶鍙傛嫾鎺ュ埌鎺ュ彛鍚庨潰銆�
                            .build();
                    Response response = null;
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.d("kwwl","response.code()=="+response.code());
                        Log.d("kwwl","response.message()=="+response.message());

                        String result=response.body().string();
                        Log.d("kwwl","res=="+result);
                        String sub_result=result.substring(1,result.length()-1);
                        sub_result=sub_result.replace("\\\"","\"");
                        sub_result=sub_result.replace("\\\\","\\");
                        Log.d("kwwl","sub_result:"+sub_result);
                        JSONObject object;
                        try {
                            /*
                            result = result.substring(1, result.length() - 1);
                            result = result.replaceAll("\\\\", "");
                            result = result.replace("rn", "");
                            object = JSONObject.fromObject(result);
                            final UpdateInfoModel model = new UpdateInfoModel();
                            model.setAppname(object.getString("appname"));
                            model.setLastForce(object.getString("lastForce"));
                            model.setServerFlag(object.getString("serverFlag"));
                            model.setServerVersion(Integer.valueOf(object.getString("serverVersion")));
                            model.setUpdateurl(object.getString("updateurl"));
                            model.setUpgradeinfo(object.getString("upgradeinfo"));
                            mModel = model;
                            */
                            final UpdateInfoModel model=parseJSONWithGSON(sub_result);
                            mModel=model;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (fnCheckVersionNo(model) == 1) {
                                        String server_apk_url = model.getUpdateurl();
                                        showDialogUpdate(server_apk_url);
                                    } else
                                        Toast.makeText(mContext,mContext.getText(R.string.toast_latest_version), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,mContext.getText(R.string.toast_time_out), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
    //浣跨敤GSON瑙ｆ瀽json鏁版嵁
    private UpdateInfoModel parseJSONWithGSON(String jsonData) {
        //鐢变簬鎴戜粠php鏈嶅姟鍣ㄨ繑鍥炵殑鏁版嵁涔﹀瓧绗︿覆锛屼笉鏄暟缁勶細[{"name":"hehe" , "age":"10"}]锛屾墍浠ヤ娇鐢ㄤ互涓嬭В鏋恓son鏁版嵁鐨勬柟娉曘��
        Gson gson = new Gson();
        try
        {
            Type type=new TypeToken<UpdateInfoModel>(){}.getType();

            UpdateInfoModel model = gson.fromJson(jsonData, type);
            return model;
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
    private Activity getActivity()
    {
        Context context = mContext;
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }
    /**
     * ???姹�???????????路???1,??????路???0
     */
    private int fnCheckVersionNo(UpdateInfoModel model) {
        int server_version = Integer.valueOf(model.getServerVersion());
        int app_version = getVersionCode();
        if (app_version < server_version)
            return 1;
        else
            return 0;
    }

    /**
     * ????姹�?????????
     */
    public void showDialogUpdate(final String str_url) {
        if (mContext == null)
            return;
        // ????????????????????????????煤????????builder????
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // ?????????????
        builder.setTitle(mContext.getText(R.string.toast_version_update)).
                // ?????????????
                // setIcon(R.mipmap.wisdom_bg).
                // ?????????????
                        setMessage(mModel.getUpgradeinfo()).
                // ??????????
                        setPositiveButton(mContext.getText(R.string.ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadNewVersionProgress(str_url);
                    }
                }).

                // ??????????,null?????????????????????
                        setNegativeButton(mContext.getText(R.string.cancel), null);

        // ?????????
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        // ????????
        alertDialog.show();
    }

    /**
     * ?????掳姹�????????????
     */
    private void loadNewVersionProgress(String apk_url) {
        final String uri = apk_url;
        final ProgressDialog pd;    //???????????
        pd = new ProgressDialog(mContext);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage(mContext.getText(R.string.toast_on_downloading));
        pd.show();
        //?????????????????
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(uri, pd);
                    sleep(1000);
                    installApk(file);
                    pd.dismiss(); //?????????????????
                } catch (Exception e) {
                    //????apk???
                    Toast.makeText(mContext.getApplicationContext(),mContext.getText(R.string.toast_download_failed), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * ??????????apk????????
     * ???????uri??????????????????File???
     * ??????????????????
     */
    public static File getFileFromServer(String uri, ProgressDialog pd) throws Exception {
        //????????????????sdcard????????????????????
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //???????????小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            long time = System.currentTimeMillis();//????????????
            File file = new File(Environment.getExternalStorageDirectory(), time + "updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //????????????
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /**
     * ???apk
     */
    protected void installApk(File file) {
        Intent intent = new Intent();
        //??卸???
        intent.setAction(Intent.ACTION_VIEW);
        //??械?????????
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /*
     * ???????????姹�??
     */
    public String getVersionName(Context context) {
        try
        {
            //packagemanager
            PackageManager packageManager = context.getPackageManager();
            //getPackageName()
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            Log.e("Update", "鐗堟湰鍙凤細" + packInfo.versionCode);
            Log.e("Update", "鐗堟湰鍚嶏細" + packInfo.versionName);
            return packInfo.versionName;
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "1.1.1";
    }


    /*
     * ???????????姹�??
     */
    private int getVersionCode() {
        try {
            if (mContext == null)
                return 0;
            //???packagemanager?????
            PackageManager packageManager = mContext.getPackageManager();
            //getPackageName()?????????????0?????????姹�???
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            Log.e("Update", "versionCode:" + packInfo.versionCode);
            Log.e("Update", "versionName:" + packInfo.versionName);
            return packInfo.versionCode;

        } catch (Exception e) {
            e.printStackTrace();

        }

        return 1;
    }
}
