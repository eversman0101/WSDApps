package com.sunday.slidetabfragment.blue;

import com.wisdom.app.activity.BLEActivity;
import com.wisdom.app.activity.R;
import com.wisdom.app.utils.ALTEK;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.app.utils.UnpackFrame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;



/**
 * 蓝牙连接等待页面
 *  
 *
 * @author ShiPengHao
 * @date 2018/1/12
 */
public class BlueConnActivity extends BlueConnStateBaseActivity {
    /**
     * 提示用户取消连接的对话框
     */
    private AlertDialog mCancelDialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        sharedPreferences = BlueConnActivity.this.getSharedPreferences("shizhong", Context.MODE_PRIVATE);
        mCancelDialog = new AlertDialog.Builder(this)
                .setMessage("取消蓝牙连接？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BlueManager.getInstance().close();
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    @Override
    protected void onDestroy() {
        if (null != mCancelDialog) {
            mCancelDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onStateChanged(int stateCode, String msg) {
    	Log.e("bluemm","isConnect:"+BlueManager.getInstance().isConnect());
    	if(BlueManager.getInstance().isConnect())
    	{
    		fnGetClock();
    	}
    	else
        finish();
    }
    private void fnGetClock()
    {
    	ALTEK altek=new ALTEK();
		final byte[] data=altek.fnGetFrameByFunctionCode((byte)0x06);
    	new Thread(new Runnable(){

			@Override
			public void run() {
				fnSend(data);
			}}).start();
    }
    private void fnSend(byte[] data)
    {
    	BlueManager.getInstance().write(data);
		byte[] recv=BlueManager.getInstance().read(ProtocolSignALTEK.getInstance());
		if(recv.length==6&&recv[0]==(byte)0xff)
		{
			Log.i("BLEActivity","recv is null,try to resend");
			fnSend(data);
		}
		else if(recv.length!=6)
		{
			UnpackFrame uf=new UnpackFrame();
			final String str=uf.fnGetClock(recv);
			MissionSingleInstance.getSingleInstance().setSystemtime(str);
			finish();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					
					MissionSingleInstance.getSingleInstance().setSystemtime(str);
					finish();
					Toast.makeText(BlueConnActivity.this,"设备连接成功",Toast.LENGTH_SHORT).show();
				}
			});
		}
    }
    @Override
    public void onBackPressed() {
        if (mCancelDialog.isShowing()) {
            mCancelDialog.dismiss();
        } else{
            mCancelDialog.show();
        }
    }
}
