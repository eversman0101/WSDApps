package com.wisdom.app.activityResult;

import com.wisdom.app.activity.R;
import com.wisdom.app.activity.R.layout;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.QianDongBean;
import com.wisdom.bean.ShiZhongWuChaBean;
import com.wisdom.dao.IShiZhongDao;
import com.wisdom.dao.ShiZhongDao;
import com.wisdom.layout.TitleLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiZhongResultActivity extends Activity {
	@Bind(R.id.title_result)
	TitleLayout title;
	@Bind(R.id.result_username)
	EditText et_username;
	@Bind(R.id.result_jiaoyanyuan)
	EditText et_jiaoyanyuan;
	@Bind(R.id.result_qiandong_date)
	TextView tv_date;
	@Bind(R.id.result_shizhong_quanshu)
	TextView tv_quanshu;
	@Bind(R.id.result_shizhong_cishu)
	TextView tv_cishu;
	@Bind(R.id.result_shizhong_wucha1)
	TextView tv_wucha1;
	@Bind(R.id.result_shizhong_wucha2)
	TextView tv_wucha2;
	@Bind(R.id.result_shizhong_wucha3)
	TextView tv_wucha3;
	/*
	@Bind(R.id.result_shizhong_wucha_avg1)
	TextView tv_wucha_avg1;
	
	@Bind(R.id.result_shizhong_wucha_avg2)
	TextView tv_wucha_avg2;
	
	@Bind(R.id.result_shizhong_wucha_avg3)
	TextView tv_wucha_avg3;
	*/
	@Bind(R.id.result_jibenwucha_id)
	EditText tv_id;
	@Bind(R.id.btn_savejiaoyanjieguo)
	Button btn_save;
	@Bind(R.id.btn_visible)
	LinearLayout btn_visible;

	@Bind(R.id.tr_wucha1_2)
	TableRow tr_wucha1_2;
	@Bind(R.id.tr_wucha1_3)
	TableRow tr_wucha1_3;
	@Bind(R.id.tr_wucha1_4)
	TableRow tr_wucha1_4;
	@Bind(R.id.tr_wucha1_5)
	TableRow tr_wucha1_5;
	@Bind(R.id.tr_wucha1_6)
	TableRow tr_wucha1_6;

	@Bind(R.id.tr_wucha2_2)
	TableRow tr_wucha2_2;
	@Bind(R.id.tr_wucha2_3)
	TableRow tr_wucha2_3;
	@Bind(R.id.tr_wucha2_4)
	TableRow tr_wucha2_4;
	@Bind(R.id.tr_wucha2_5)
	TableRow tr_wucha2_5;
	@Bind(R.id.tr_wucha2_6)
	TableRow tr_wucha2_6;

	@Bind(R.id.tr_wucha3_2)
	TableRow tr_wucha3_2;
	@Bind(R.id.tr_wucha3_3)
	TableRow tr_wucha3_3;
	@Bind(R.id.tr_wucha3_4)
	TableRow tr_wucha3_4;
	@Bind(R.id.tr_wucha3_5)
	TableRow tr_wucha3_5;
	@Bind(R.id.tr_wucha3_6)
	TableRow tr_wucha3_6;

	@Bind(R.id.result_shizhong_wucha1_2)
	TextView tv_wucha1_2;
	@Bind(R.id.result_shizhong_wucha1_3)
	TextView tv_wucha1_3;
	@Bind(R.id.result_shizhong_wucha1_4)
	TextView tv_wucha1_4;
	@Bind(R.id.result_shizhong_wucha1_5)
	TextView tv_wucha1_5;
	@Bind(R.id.result_shizhong_wucha1_6)
	TextView tv_wucha1_6;

	@Bind(R.id.result_shizhong_wucha2_2)
	TextView tv_wucha2_2;
	@Bind(R.id.result_shizhong_wucha2_3)
	TextView tv_wucha2_3;
	@Bind(R.id.result_shizhong_wucha2_4)
	TextView tv_wucha2_4;
	@Bind(R.id.result_shizhong_wucha2_5)
	TextView tv_wucha2_5;
	@Bind(R.id.result_shizhong_wucha2_6)
	TextView tv_wucha2_6;

	@Bind(R.id.result_shizhong_wucha3_2)
	TextView tv_wucha3_2;
	@Bind(R.id.result_shizhong_wucha3_3)
	TextView tv_wucha3_3;
	@Bind(R.id.result_shizhong_wucha3_4)
	TextView tv_wucha3_4;
	@Bind(R.id.result_shizhong_wucha3_5)
	TextView tv_wucha3_5;
	@Bind(R.id.result_shizhong_wucha3_6)
	TextView tv_wucha3_6;

	IShiZhongDao dao;
	String id = null;
	ShiZhongWuChaBean bean = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shi_zhong_result);
		ButterKnife.bind(this);
		if (MissionSingleInstance.getSingleInstance().getFuhe_state() == 1)
			title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.clock_error_test).toString())+" > "+getText(R.string.inspection_results));
		if (MissionSingleInstance.getSingleInstance().getFuhe_state() == 2)
			title.setTitleText(String.format(getText(R.string.manual_validation_of_real_load_placeholder).toString(),getText(R.string.clock_error_test).toString())+" > "+getText(R.string.inspection_results));

		initData();
		getData();
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bean != null) {
					bean.setNo(tv_id.getText().toString());
					bean.setUserName(et_username.getText().toString());
					bean.setStuffName(et_jiaoyanyuan.getText().toString());
					dao.add(bean);// 保存校验结果
					Toast.makeText(ShiZhongResultActivity.this, getText(R.string.save_ok), Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(ShiZhongResultActivity.this, getText(R.string.result_null), Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private void initData() {
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		dao = new ShiZhongDao(this);
		if (id != null) {
			et_username.setEnabled(false);
			tv_id.setEnabled(false);
			et_jiaoyanyuan.setEnabled(false);
			btn_visible.setVisibility(View.GONE);
		}
	}

	private void getData() {
		try {
			if (id == null) {
				bean = (ShiZhongWuChaBean) getIntent().getSerializableExtra("shizhongwuchaBean");
				if (bean == null) {
					return;
				}
			} else {
				bean = dao.findById(id);
			}
			// ShiZhongWuChaBean bean=dao.find();
			et_username.setText(bean.getUserName());
			et_jiaoyanyuan.setText(bean.getStuffName());
			// tv_id.setText(String.valueOf(bean.getId()));
			tv_id.setText(bean.getNo());
			tv_date.setText(bean.getDate());
			tv_quanshu.setText(bean.getQuanshu());
			tv_cishu.setText(bean.getCishu());

			tv_wucha1.setText(bean.getShizhongwucha1());
			tv_wucha2.setText(bean.getShizhongwucha2());
			tv_wucha3.setText(bean.getShizhongwucha3());
			/*
			tv_wucha_avg1.setText(bean.getPingjunwucha1());
			tv_wucha_avg2.setText(bean.getPingjunwucha2());
			tv_wucha_avg3.setText(bean.getPingjunwucha3());
			*/
			// 表1 表2 表3 剩余误差
			int i_cishu = Integer.valueOf(bean.getCishu());
			if (i_cishu > 1) {
				for (int i = 2; i <= i_cishu; i++) {
					initWuCha(i);
				}
			}
		} catch (Exception ex) {

		}
	}

	/**
	 * 初始化误差
	 * 
	 * @param position
	 *            第n个误差
	 */
	private void initWuCha(int position) {
		if (position > 6)
			return;
		if (position == 2) {
			// 表1
			tr_wucha1_2.setVisibility(View.VISIBLE);
			tv_wucha1_2.setText(bean.getShizhongwucha1_2());
			// 表2
			tr_wucha2_2.setVisibility(View.VISIBLE);
			tv_wucha2_2.setText(bean.getShizhongwucha2_2());
			// 表3
			tr_wucha3_2.setVisibility(View.VISIBLE);
			tv_wucha3_2.setText(bean.getShizhongwucha2_3());
		}
		if (position == 3) {
			// 表1
			tr_wucha1_3.setVisibility(View.VISIBLE);
			tv_wucha1_3.setText(bean.getShizhongwucha1_3());
			// 表2
			tr_wucha2_3.setVisibility(View.VISIBLE);
			tv_wucha2_3.setText(bean.getShizhongwucha2_3());
			// 表3
			tr_wucha3_3.setVisibility(View.VISIBLE);
			tv_wucha3_3.setText(bean.getShizhongwucha3_3());
		}
		if (position == 4) {
			// 表1
			tr_wucha1_4.setVisibility(View.VISIBLE);
			tv_wucha1_4.setText(bean.getShizhongwucha1_4());
			// 表2
			tr_wucha2_4.setVisibility(View.VISIBLE);
			tv_wucha2_4.setText(bean.getShizhongwucha2_4());
			// 表3
			tr_wucha3_4.setVisibility(View.VISIBLE);
			tv_wucha3_4.setText(bean.getShizhongwucha3_4());
		}
		if (position == 5) {
			// 表1
			tr_wucha1_5.setVisibility(View.VISIBLE);
			tv_wucha1_5.setText(bean.getShizhongwucha1_5());
			// 表2
			tr_wucha2_5.setVisibility(View.VISIBLE);
			tv_wucha2_5.setText(bean.getShizhongwucha2_5());
			// 表3
			tr_wucha3_5.setVisibility(View.VISIBLE);
			tv_wucha3_5.setText(bean.getShizhongwucha3_5());
		}
		if (position == 6) {
			// 表1
			tr_wucha1_6.setVisibility(View.VISIBLE);
			tv_wucha1_6.setText(bean.getShizhongwucha1_6());
			// 表2
			tr_wucha2_6.setVisibility(View.VISIBLE);
			tv_wucha2_6.setText(bean.getShizhongwucha2_6());
			// 表3
			tr_wucha3_6.setVisibility(View.VISIBLE);
			tv_wucha3_6.setText(bean.getShizhongwucha3_6());
		}
	}
}
