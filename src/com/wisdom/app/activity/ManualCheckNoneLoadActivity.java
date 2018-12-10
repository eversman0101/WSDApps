package com.wisdom.app.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sunday.slidetabfragment.blue.BlueManager;
import com.wisdom.app.activity.MainActivity.TimeThread;
import com.wisdom.app.fragment.MCNL_BoXingXianShiFragment;
import com.wisdom.app.fragment.MCNL_ChangshujiaoheFragment;
import com.wisdom.app.fragment.MCNL_JiBenWuChaFragment;
import com.wisdom.app.fragment.MCNL_QiDongTestFragment;
import com.wisdom.app.fragment.MCNL_QianDongTestFragment;
import com.wisdom.app.fragment.MCNL_ShiZhongWuChaFragment;
import com.wisdom.app.fragment.MCNL_XieBoFenXiFragment;
import com.wisdom.app.fragment.MCNL_XieBoSheZhiFragment;
import com.wisdom.app.fragment.MCNL_YaoCeFragment;
import com.wisdom.app.fragment.MCNL_ZouZiTestFragment;
import com.wisdom.app.utils.Comm;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.app.utils.SharepreferencesUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author JinJingyun
 * 虚负荷手动校验
 * */
public class ManualCheckNoneLoadActivity extends FragmentActivity implements OnClickListener{
	public static int METHOD=0;//0虚负荷，2秒脉冲
	/**
	 * FrameLayout
	 */
	private FrameLayout frame_main;
	/**
	 * 分页
	 * */
	private MCNL_JiBenWuChaFragment jibenwuchaFragment;
	private MCNL_QianDongTestFragment qiandongFragment;
	private MCNL_QiDongTestFragment qidongFragment;
	private MCNL_ZouZiTestFragment zouziFragment;
	private MCNL_ShiZhongWuChaFragment shizhongFragment;
	private MCNL_XieBoSheZhiFragment xieboshezhiFragment;
	private MCNL_XieBoFenXiFragment xiebofenxiFragment;
	private MCNL_BoXingXianShiFragment boxingxianshiFragment;
	private MCNL_ChangshujiaoheFragment changshujiaoheFragment;
	private MCNL_YaoCeFragment yaoceFragment;
	/**
	 * 用于对Fragment进行管理
	 */
	private FragmentManager fragmentManager;
	/**
	 * 按钮
	 * */
	private Button jiBenWuChaBtn;
	private Button qianDongTestBtn;
	private Button qiDongTestBtn;
	private Button zouZiTestBtn;
	private Button shiZhongBtn;
	private Button xieBoSheZhiBtn;
	private Button xiebofenxiBtn;
	private Button boxingxianshiBtn;
	private Button changshujiaohe;
	private Button yaoceBtn;
	private TextView tv_systemtime;
	private TextView tv_temp;
	private String systime=null;
 
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0x003&&systime!=null)
            {
				systime = MissionSingleInstance.getSingleInstance().getSystemtime();
				tv_systemtime.setText(systime); //更新时间
            }
        }
	};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manual_check_none_load);
		
		frame_main=(FrameLayout)findViewById(R.id.frame_main);
		jiBenWuChaBtn = (Button)findViewById(R.id.jiben_wucha);
        qianDongTestBtn = (Button)findViewById(R.id.qiandong_test);
        qiDongTestBtn=(Button)findViewById(R.id.qidong_test);
        zouZiTestBtn=(Button)findViewById(R.id.zouzi_test);
        shiZhongBtn=(Button)findViewById(R.id.shizhong_wucha);
        xieBoSheZhiBtn=(Button)findViewById(R.id.xiebo_shezhi);
        xiebofenxiBtn=(Button)findViewById(R.id.xiebo_fenxi);
        boxingxianshiBtn=(Button)findViewById(R.id.boxing_xianshi);
        changshujiaohe =(Button) findViewById(R.id.changshujiaohe);
        yaoceBtn=(Button)findViewById(R.id.yaoce);
        
        tv_systemtime =(TextView) findViewById(R.id.status_time);
        tv_temp=(TextView)findViewById(R.id.status_temp_hum);
        fragmentManager = getSupportFragmentManager();
        jiBenWuChaBtn.setOnClickListener(this);
        qianDongTestBtn.setOnClickListener(this);
        qiDongTestBtn.setOnClickListener(this);
        zouZiTestBtn.setOnClickListener(this);
        shiZhongBtn.setOnClickListener(this);
        xieBoSheZhiBtn.setOnClickListener(this);
        xiebofenxiBtn.setOnClickListener(this);
        boxingxianshiBtn.setOnClickListener(this);
        changshujiaohe.setOnClickListener(this);
        yaoceBtn.setOnClickListener(this);
        setTabSelection(0);
        MissionSingleInstance.getSingleInstance().setFuhe_state(2);
        systime = MissionSingleInstance.getSingleInstance().getSystemtime();
        String str_temp=SharepreferencesUtil.getTemperature(ManualCheckNoneLoadActivity.this);
		if(str_temp!="")
		{
			tv_temp.setText(str_temp);
		}
        new TimeThread().start(); 
	}
@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	Comm.getInstance().cancelLoop();
	
}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.jiben_wucha:
			setTabSelection(0);
			break;
		case R.id.qiandong_test:
			if(!MissionSingleInstance.getSingleInstance().isYuan_state())
			setTabSelection(1);
			else Toast.makeText(ManualCheckNoneLoadActivity.this,getText(R.string.toast_stop_power).toString(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.qidong_test:
			if(!MissionSingleInstance.getSingleInstance().isYuan_state())
			setTabSelection(2);
			else Toast.makeText(ManualCheckNoneLoadActivity.this, getText(R.string.toast_stop_power).toString(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.zouzi_test:
			setTabSelection(3);
			break;
		case R.id.shizhong_wucha:
			setTabSelection(4);
			break;
		case R.id.xiebo_shezhi:
			setTabSelection(5);
			break;
		case R.id.xiebo_fenxi:
			setTabSelection(6);
			break;
		case R.id.boxing_xianshi:
			setTabSelection(7);
			break;
		case R.id.changshujiaohe:
			setTabSelection(8);
			break;
		case R.id.yaoce:
			setTabSelection(9);
			break;
		default:
			break;
		}
	}
	/**
	 * 
	 * @Description: TODO 底部点击按钮切换fragment
	 * @author JinJingYun
	 * @date 2017年11月11日
	 */
	public void setTabSelection(int index) {
		//是否正在试验
		boolean testState=MissionSingleInstance.getSingleInstance().isTestState();
		if(!testState){
		// 开启一个Fragment事务
		FragmentTransaction homeaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(homeaction);
		switch (index) {
		case 0:
			if (jibenwuchaFragment == null) {
				jibenwuchaFragment = new MCNL_JiBenWuChaFragment();
				homeaction.add(R.id.frame_main, jibenwuchaFragment);
			} else {
				homeaction.show(jibenwuchaFragment);
			}
			break;
			
		case 1:
			if (qiandongFragment == null) {
				qiandongFragment = new MCNL_QianDongTestFragment();
				
				homeaction.add(R.id.frame_main, qiandongFragment);
			} else {
				homeaction.show(qiandongFragment);
			}
			break;
		case 2:
			if (qidongFragment == null) {
				qidongFragment = new MCNL_QiDongTestFragment();
				homeaction.add(R.id.frame_main, qidongFragment);

			} else {
				homeaction.show(qidongFragment);
			}
			break;
		case 3:
			if (zouziFragment == null) {
				zouziFragment = new MCNL_ZouZiTestFragment();
				homeaction.add(R.id.frame_main, zouziFragment);

			} else {
				homeaction.show(zouziFragment);
			}
			break;
		case 4:
			if (shizhongFragment == null) {
				shizhongFragment = new MCNL_ShiZhongWuChaFragment();
				homeaction.add(R.id.frame_main, shizhongFragment);

			} else {
				homeaction.show(shizhongFragment);
			}
			break;
		case 5:
			if (xieboshezhiFragment == null) {
				xieboshezhiFragment = new MCNL_XieBoSheZhiFragment();
				homeaction.add(R.id.frame_main, xieboshezhiFragment);

			} else {
				homeaction.show(xieboshezhiFragment);
			}
			break;	
		case 6:
			if (xiebofenxiFragment == null) {
				xiebofenxiFragment = new MCNL_XieBoFenXiFragment();
				homeaction.add(R.id.frame_main, xiebofenxiFragment);

			} else {
				homeaction.show(xiebofenxiFragment);
			}
			break;	
		case 7:
			if (boxingxianshiFragment == null) {
				boxingxianshiFragment = new MCNL_BoXingXianShiFragment();
				homeaction.add(R.id.frame_main, boxingxianshiFragment);

			} else {
				homeaction.show(boxingxianshiFragment);
			}
			break;	
		case 8:
			if (changshujiaoheFragment == null) {
				changshujiaoheFragment = new MCNL_ChangshujiaoheFragment();
				homeaction.add(R.id.frame_main, changshujiaoheFragment);

			} else {
				homeaction.show(changshujiaoheFragment);
			}
			break;	
		case 9:
			if (yaoceFragment == null) {
				yaoceFragment = new MCNL_YaoCeFragment();
				homeaction.add(R.id.frame_main, yaoceFragment);

			} else {
				homeaction.show(yaoceFragment);
			}
			break;	
		}
		homeaction.commit();
		}else{
			Toast.makeText(ManualCheckNoneLoadActivity.this, getText(R.string.toast_stop_test).toString(), Toast.LENGTH_SHORT).show();
		}
	}
private void hideFragments(FragmentTransaction transaction) {
		
		if (jibenwuchaFragment != null) {
			transaction.hide(jibenwuchaFragment);
		}
		if (qiandongFragment != null) {
			transaction.hide(qiandongFragment);
		}
		if (qidongFragment != null) {
			transaction.hide(qidongFragment);
		}
		if (zouziFragment != null) {
			transaction.hide(zouziFragment);
		}
		if (shizhongFragment != null) {
			transaction.hide(shizhongFragment);
		}
		if(xieboshezhiFragment!=null){
			transaction.hide(xieboshezhiFragment);
		}
		if(xiebofenxiFragment!=null){
			transaction.hide(xiebofenxiFragment);
		}
		if(boxingxianshiFragment!=null){
			transaction.hide(boxingxianshiFragment);
		}
		if(changshujiaoheFragment!=null){
			transaction.hide(changshujiaoheFragment);
		}
		if(yaoceFragment!=null){
			transaction.hide(yaoceFragment);
		}
	}
@Override
public boolean onKeyDown(int keyCode, KeyEvent event)
{
	if (keyCode == KeyEvent.KEYCODE_BACK )
	{
		if(BlueManager.getInstance().isConnect()&&MissionSingleInstance.getSingleInstance().isYuan_state())
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ManualCheckNoneLoadActivity.this);
	        builder.setTitle(getText(R.string.dialog_warn));
	        builder.setMessage(getText(R.string.dialog_stop_source));
	        
	        builder.setPositiveButton(getText(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                
	            }
	        });
	        AlertDialog dialog = builder.create();
	        dialog.show();
		}
		else
		{
			//蓝牙断开了
			MissionSingleInstance.getSingleInstance().setYuan_state(false);
			finish();
		}
	}
	
	return false;
	
}
class TimeThread extends Thread {
    @Override
    public void run() {
        do {
            try {
                Thread.sleep(1000);
                Message msg = new Message();
                msg.what = 0x003;  //消息(一个整型值)
                handler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
    }
}
}