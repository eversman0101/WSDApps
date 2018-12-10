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
 * @version ����ʱ�䣺2017-11-10 ����4:18:04 ��˵����
 */
public class SystemSettingActivity extends FragmentActivity implements OnClickListener {
	/**
	 * FrameLayout
	 */
	private FrameLayout frame_main;
	/**
	 * ��ҳ
	 */
	// private XTSZ_SheBeiPeiDuiFragment sheBeiPeiDuiFragment;
	private XTSZ_SheBeiPeiDuiFragment sheBeiPeiDuiFragment;

	/**
	 * ���ڶ�Fragment���й���
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
	 * @Description: TODO �ײ������ť�л�fragment
	 * @author JinJingYun
	 * @date 2017��11��11��
	 */
	public void setTabSelection(int index) {
		// ����һ��Fragment����
		FragmentTransaction homeaction = fragmentManager.beginTransaction();
		// �����ص����е�Fragment���Է�ֹ�ж��Fragment��ʾ�ڽ����ϵ����
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
