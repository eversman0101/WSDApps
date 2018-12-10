package com.sunday.slidetabfragment.blue;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 蓝牙连接状态监控Activity基类
 *
 * @author ShiPengHao
 * @date 2018/1/12
 */
public abstract class BlueConnStateBaseFragment extends Fragment {

	/**
	 * 蓝牙连接状态监控本地广播
	 */
	private final BroadcastReceiver BLUE_STATE_RECEIVER = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BlueManager.BLUE_STATE_ACTION.equalsIgnoreCase(intent.getAction())) {
				onStateChanged(intent.getIntExtra("code", -1), intent.getStringExtra("msg"));
			}
		}
	};

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(BLUE_STATE_RECEIVER,
				BlueManager.BLUE_STATE_INTENT_FILTER);
	}

	@Override
	public void onDestroy() {
		LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(BLUE_STATE_RECEIVER);
		super.onDestroy();
	}

	/**
	 * 蓝牙状态发生变化
	 *
	 * @param stateCode
	 *            状态码
	 */
	protected abstract void onStateChanged(int stateCode, String msg);

}
