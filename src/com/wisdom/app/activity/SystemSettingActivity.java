package com.wisdom.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.wisdom.app.fragment.XTSZ_SheBeiPeiDuiFragment;
import com.wisdom.app.utils.ByteUtil;

/**
 * @author wisdom's JiangYuPeng
 * @version 创建时间：2017-11-10 下午4:18:04 类说明：
 */
public class SystemSettingActivity extends FragmentActivity implements OnClickListener {
	/**
	 * FrameLayout
	 */
	private FrameLayout frame_main;
	/**
	 * 分页
	 */
	// private XTSZ_SheBeiPeiDuiFragment sheBeiPeiDuiFragment;
	private XTSZ_SheBeiPeiDuiFragment sheBeiPeiDuiFragment;

	/**
	 * 用于对Fragment进行管理
	 */
	private FragmentManager fragmentManager;

	private Button sheBeiPeiDuiBtn;
	private Button yingXiaoXiTongBtn;
	private Button guJianShengJiBtn;
	private Button canShuJiaoZhunBtn;
	private Button moShiQieHuanBtn;
	// @Override
	// protected void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sys_setting);
		fragmentManager = getSupportFragmentManager();
		frame_main = (FrameLayout) findViewById(R.id.frame_system_main);
		sheBeiPeiDuiBtn = (Button) findViewById(R.id.shebei_peidui);
		sheBeiPeiDuiBtn.setOnClickListener(this);
		yingXiaoXiTongBtn = (Button) findViewById(R.id.yinxiao_xitong);
		yingXiaoXiTongBtn.setOnClickListener(this);
		guJianShengJiBtn = (Button) findViewById(R.id.gujian_shengji);
		guJianShengJiBtn.setOnClickListener(this);
		canShuJiaoZhunBtn = (Button) findViewById(R.id.canshu_jiaozhun);
		canShuJiaoZhunBtn.setOnClickListener(this);
		moShiQieHuanBtn = (Button) findViewById(R.id.moshi_qiehuan);
		moShiQieHuanBtn.setOnClickListener(this);
		setTabSelection(0);
	}

	@Override
	public void onClick(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.shebei_peidui:
			setTabSelection(0);
			break;
		case R.id.yinxiao_xitong:
			setTabSelection(1);
			break;
		case R.id.gujian_shengji:
			setTabSelection(2);
			break;
		case R.id.canshu_jiaozhun:
			setTabSelection(3);
			break;
		case R.id.moshi_qiehuan:
			setTabSelection(4);
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
			if (sheBeiPeiDuiFragment == null) {
				sheBeiPeiDuiFragment = new XTSZ_SheBeiPeiDuiFragment();
				homeaction.add(R.id.frame_system_main, sheBeiPeiDuiFragment);
				// homeaction.add(R.id.frame_main, sheBeiPeiDuiFragment);

			} else {
				homeaction.show(sheBeiPeiDuiFragment);
			}
			break;
		}
		homeaction.commit();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (sheBeiPeiDuiFragment != null) {
			transaction.hide(sheBeiPeiDuiFragment);
		}
	}
}
