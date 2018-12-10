package com.wisdom.app.activity;


import java.util.Timer;

import com.wisdom.app.activity.ManualCheckNoneLoadActivity.TimeThread;
import com.wisdom.app.fragment.MCL_BoXingXianShiFragment;
import com.wisdom.app.fragment.MCL_JiBenWuChaFragment;
import com.wisdom.app.fragment.MCL_QiDongTestFragment;
import com.wisdom.app.fragment.MCL_QianDongTestFragment;
import com.wisdom.app.fragment.MCL_ShiZhongWuChaFragment;
import com.wisdom.app.fragment.MCL_XieBoFenXiFragment;
import com.wisdom.app.fragment.MCL_XieBoSheZhiFragment;
import com.wisdom.app.fragment.MCL_ZouZiTestFragment;
import com.wisdom.app.utils.Comm;
import com.wisdom.app.utils.MissionSingleInstance;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

/** 
 * @author wisdom's JiangYuPeng
 * @version 创建时间：2017-11-10 下午4:03:56 
 * 类说明：实负荷手动校验
 */
public class ManualCheckLoadActivity extends FragmentActivity implements OnClickListener {
	/**
	 * 电流采样方式
	 * 0: 虚负荷模式
	 * 1：直接接入式
	 * 3：100A电流钳
	 * 7：5A电流钳
	 * */
	public static int METHOD=1;
	/**
	 * FrameLayout
	 */
	private FrameLayout frame_main;
	/**
	 * 分页
	 * */
	private MCL_JiBenWuChaFragment jibenwuchaFragment;
	private MCL_QianDongTestFragment qiandongFragment;
	private MCL_QiDongTestFragment qidongFragment;
	private MCL_ZouZiTestFragment zouziFragment;
	private MCL_ShiZhongWuChaFragment shizhongFragment;
	private MCL_XieBoSheZhiFragment xieboshezhiFragment;
	private MCL_XieBoFenXiFragment xiebofenxiFragment;
	private MCL_BoXingXianShiFragment boxingxianshiFragment;
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
	private PopupWindow popuWindow;
	private View contentView1,view;
	private TextView tv_systemtime;
	private Context context;
	private String systime=null;
	private Handler popupHandler = new Handler(){  
	    @Override  
	    public void handleMessage(Message msg) {  
	        switch (msg.what) {  
	        case 0:  
	        	
	            break;  
	        case 0x003:
            if(systime!=null)
            {
				systime = MissionSingleInstance.getSingleInstance().getSystemtime();
				tv_systemtime.setText(systime); //更新时间

            }
            break;
	    }  
	      
	}
	    };  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manual_check_load);
		context=this;
		frame_main=(FrameLayout)findViewById(R.id.frame_main);
		jiBenWuChaBtn = (Button)findViewById(R.id.jiben_wucha);
        qianDongTestBtn = (Button)findViewById(R.id.qiandong_test);
        qiDongTestBtn=(Button)findViewById(R.id.qidong_test);
        zouZiTestBtn=(Button)findViewById(R.id.zouzi_test);
        shiZhongBtn=(Button)findViewById(R.id.shizhong_wucha);
        xieBoSheZhiBtn=(Button)findViewById(R.id.xiebo_shezhi);
        xiebofenxiBtn=(Button)findViewById(R.id.xiebo_fenxi);
        boxingxianshiBtn=(Button)findViewById(R.id.boxing_xianshi);
        tv_systemtime =(TextView) findViewById(R.id.status_time);
        fragmentManager = getSupportFragmentManager();
        
        jiBenWuChaBtn.setOnClickListener(this);
        
        qianDongTestBtn.setOnClickListener(this);
        qiDongTestBtn.setOnClickListener(this);
        zouZiTestBtn.setOnClickListener(this);
        shiZhongBtn.setOnClickListener(this);
        
        xieBoSheZhiBtn.setVisibility(View.GONE);
        qianDongTestBtn.setVisibility(View.GONE);
        qiDongTestBtn.setVisibility(View.GONE);
        zouZiTestBtn.setVisibility(View.GONE);
        shiZhongBtn.setVisibility(View.GONE);
        
        xieBoSheZhiBtn.setOnClickListener(this);
        xiebofenxiBtn.setOnClickListener(this);
        boxingxianshiBtn.setOnClickListener(this);
        setTabSelection(0);
        popupHandler.sendEmptyMessageDelayed(0, 1000);  
        MissionSingleInstance.getSingleInstance().setFuhe_state(2);
        MissionSingleInstance.getSingleInstance().setYuan_state(true);
        systime = MissionSingleInstance.getSingleInstance().getSystemtime();
        new TimeThread().start(); 
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jiben_wucha:
			setTabSelection(0);
			break;
		case R.id.qiandong_test:
			if(!MissionSingleInstance.getSingleInstance().isTestState())
				setTabSelection(1);
				else Toast.makeText(ManualCheckLoadActivity.this, "请先停止试验", Toast.LENGTH_SHORT).show();
			break;
		case R.id.qidong_test:
			if(!MissionSingleInstance.getSingleInstance().isTestState())
				setTabSelection(2);
				else Toast.makeText(ManualCheckLoadActivity.this, "请先停止试验", Toast.LENGTH_SHORT).show();
			break;
		case R.id.zouzi_test:
			if(!MissionSingleInstance.getSingleInstance().isTestState())
				setTabSelection(3);
				else Toast.makeText(ManualCheckLoadActivity.this, "请先停止试验", Toast.LENGTH_SHORT).show();
			break;
		case R.id.shizhong_wucha:
			if(!MissionSingleInstance.getSingleInstance().isTestState())
				setTabSelection(4);
				else Toast.makeText(ManualCheckLoadActivity.this, "请先停止试验", Toast.LENGTH_SHORT).show();
			break;
		case R.id.xiebo_shezhi:
			if(!MissionSingleInstance.getSingleInstance().isTestState())
				setTabSelection(5);
				else Toast.makeText(ManualCheckLoadActivity.this, "请先停止试验", Toast.LENGTH_SHORT).show();
			break;
		case R.id.xiebo_fenxi:
			if(!MissionSingleInstance.getSingleInstance().isTestState())
				setTabSelection(6);
				else Toast.makeText(ManualCheckLoadActivity.this, "请先停止试验", Toast.LENGTH_SHORT).show();
			break;
		case R.id.boxing_xianshi:
			if(!MissionSingleInstance.getSingleInstance().isTestState())
				setTabSelection(7);
				else Toast.makeText(ManualCheckLoadActivity.this, "请先停止试验", Toast.LENGTH_SHORT).show();
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
		// 开启一个Fragment事务
		FragmentTransaction homeaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(homeaction);
		switch (index) {
		case 0:
			if (jibenwuchaFragment == null) {
				jibenwuchaFragment = new MCL_JiBenWuChaFragment();
				homeaction.add(R.id.frame_main, jibenwuchaFragment);

			} else {
				homeaction.show(jibenwuchaFragment);
			}
			break;
			
		case 1:
			if (qiandongFragment == null) {
				qiandongFragment = new MCL_QianDongTestFragment();
				homeaction.add(R.id.frame_main, qiandongFragment);

			} else {
				homeaction.show(qiandongFragment);
			}
			break;
		case 2:
			if (qidongFragment == null) {
				qidongFragment = new MCL_QiDongTestFragment();
				homeaction.add(R.id.frame_main, qidongFragment);

			} else {
				homeaction.show(qidongFragment);
			}
			break;
		case 3:
			if (zouziFragment == null) {
				zouziFragment = new MCL_ZouZiTestFragment();
				homeaction.add(R.id.frame_main, zouziFragment);

			} else {
				homeaction.show(zouziFragment);
			}
			break;
		case 4:
			if (shizhongFragment == null) {
				shizhongFragment = new MCL_ShiZhongWuChaFragment();
				homeaction.add(R.id.frame_main, shizhongFragment);

			} else {
				homeaction.show(shizhongFragment);
			}
			break;
		case 5:
			if (xieboshezhiFragment == null) {
				xieboshezhiFragment = new MCL_XieBoSheZhiFragment();
				homeaction.add(R.id.frame_main, xieboshezhiFragment);

			} else {
				homeaction.show(xieboshezhiFragment);
			}
			break;	
		case 6:
			if (xiebofenxiFragment == null) {
				xiebofenxiFragment = new MCL_XieBoFenXiFragment();
				homeaction.add(R.id.frame_main, xiebofenxiFragment);

			} else {
				homeaction.show(xiebofenxiFragment);
			}
			break;	
		case 7:
			if (boxingxianshiFragment == null) {
				boxingxianshiFragment = new MCL_BoXingXianShiFragment();
				homeaction.add(R.id.frame_main, boxingxianshiFragment);

			} else {
				homeaction.show(boxingxianshiFragment);
			}
			break;	
		}
		homeaction.commit();
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
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MissionSingleInstance.getSingleInstance().setYuan_state(false);
		Comm.getInstance().cancelLoop();
	}
	class TimeThread extends Thread {
	    @Override
	    public void run() {
	        do {
	            try {
	                Thread.sleep(1000);
	                Message msg = new Message();
	                msg.what = 0x003;  //消息(一个整型值)
	                popupHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        } while (true);
	    }
	}
}
