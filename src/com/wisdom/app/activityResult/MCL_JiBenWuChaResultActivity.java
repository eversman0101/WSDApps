package com.wisdom.app.activityResult;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.wisdom.app.activity.R;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.dao.JiBenWuChaDao;
import com.wisdom.layout.TitleLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MCL_JiBenWuChaResultActivity extends Activity {
	@Bind(R.id.title_result)
	TitleLayout title;
	// @Bind(R.id.result_jibenwucha_dianbiaohao)EditText tv_dianbiaohao;
	@Bind(R.id.result_username)
	EditText et_username;
	@Bind(R.id.result_jiaoyanyuan)
	EditText et_jiaoyanyuan;
	@Bind(R.id.result_jibenwucha_u)
	TextView tv_u;
	@Bind(R.id.result_jibenwucha_i)
	TextView tv_i;
	@Bind(R.id.result_jibenwucha_yougong)
	TextView tv_yougong;
	@Bind(R.id.result_jibenwucha_wugong)
	TextView tv_wugong;
	@Bind(R.id.result_jibenwucha_gonglvyinshu)
	TextView tv_gonglvyinshu;
	@Bind(R.id.result_jibenwucha_maichongchangshu)
	TextView tv_maichongchangshu;
	@Bind(R.id.result_jibenwucha_quanshu)
	TextView tv_quanshu;
	@Bind(R.id.result_jibenwucha_cishu)
	TextView tv_cishu;
	// @Bind(R.id.result_jibenwucha_biaozhunpiancha1)
	// TextView tv_piancha1;

	@Bind(R.id.result_jibenwucha_shijian)
	TextView tv_shijian;
	@Bind(R.id.result_jibenwucha_xiangjiao)
	TextView tv_xiangjiao;
	@Bind(R.id.result_jibenwucha_id)
	EditText tv_id;
	@Bind(R.id.btn_savejiaoyanjieguo)
	Button btn_save;
	@Bind(R.id.btn_visible)
	LinearLayout btn_visible;
	// 2018-07-10 用于动态添加TableRow
	@Bind(R.id.tl_main)
	TableLayout tl_main;
	JiBenWuChaDao dao;
	String id = null;
	JiBenWuChaBean bean = null;
	@Bind(R.id.layout_main)
	LinearLayout layout_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mcl_ji_ben_wu_cha_result);
		ButterKnife.bind(this);
		title.setTitleText(String.format(getText(R.string.manual_validation_of_real_load_placeholder).toString(),getText(R.string.intrinsic_error).toString())+" > "+getText(R.string.inspection_results));
		initData();
		getData();
		btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (bean != null) {
					if (tv_id.getText().toString().equals("")) {
						Toast.makeText(MCL_JiBenWuChaResultActivity.this,
								getText(R.string.toast_work_num), Toast.LENGTH_SHORT).show();
						return;
					} else if (et_username.getText().toString().equals("")) {
						Toast.makeText(MCL_JiBenWuChaResultActivity.this,
								getText(R.string.toast_username), Toast.LENGTH_SHORT).show();
						return;
					} else if (et_jiaoyanyuan.getText().toString().equals(""))
					{
						Toast.makeText(MCL_JiBenWuChaResultActivity.this,
								getText(R.string.toast_worker), Toast.LENGTH_SHORT).show();
						return;
					}

					bean.setNo(tv_id.getText().toString());
					bean.setUserName(et_username.getText().toString());
					bean.setStuffName(et_jiaoyanyuan.getText().toString());
					dao.add(bean);// 保存校验结果
					Toast.makeText(MCL_JiBenWuChaResultActivity.this, getText(R.string.save_ok),
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(MCL_JiBenWuChaResultActivity.this, getText(R.string.result_null),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	private void initData() {
		try {
			Intent intent = getIntent();
			id = intent.getStringExtra("id");

			dao = new JiBenWuChaDao(this);
			if (id != null) {
				et_username.setEnabled(false);
				tv_id.setEnabled(false);
				et_jiaoyanyuan.setEnabled(false);
				btn_visible.setVisibility(View.GONE);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void getData() {
		try {
			if (id == null) {
				bean = (JiBenWuChaBean) getIntent().getSerializableExtra(
						"jibenwuchaBean");
				if (bean == null) {
					Log.i("UF", "bean is null");
					return;
				}
			} else {
				bean = dao.findById(id);
			}
			et_username.setText(bean.getUserName());
			et_jiaoyanyuan.setText(bean.getStuffName());
			// tv_id.setText(String.valueOf(bean.getId()));
			tv_id.setText(bean.getNo());
			tv_u.setText(bean.getU());
			tv_i.setText(bean.getI());
			tv_yougong.setText(bean.getYougong());
			tv_wugong.setText(bean.getWugong());
			tv_gonglvyinshu.setText(bean.getGonglvyinshu());
			tv_maichongchangshu.setText(bean.getMaichongchangshu());
			tv_quanshu.setText(bean.getQuanshu());
			tv_cishu.setText(bean.getCishu());
			// tv_piancha1.setText(bean.getBiaozhunpiancha1());

			tv_shijian.setText(bean.getDate());
			tv_xiangjiao.setText(bean.getJiaodu());

			// 误差
			int i_cishu = Integer.valueOf(bean.getCishu());
			String str_result = bean.getDiannengwucha1();
			String[] arr = str_result.split(",");
			if (arr.length > 0)
				addRow(arr);
			/*
			 * <TableRow android:layout_height="fill_parent"
			 * android:layout_weight="1.0">
			 * 
			 * <TextView android:paddingTop="6dip" android:paddingBottom="6dip"
			 * android:layout_width="0.0dip" android:layout_height="fill_parent"
			 * android:layout_weight="1.5" android:layout_marginLeft="-2.0dip"
			 * android:layout_marginTop="-2.0dip"
			 * android:background="@drawable/cell_header_bg"
			 * android:gravity="center" android:text="标准偏差1"
			 * android:textColor="@color/text_color_main"
			 * android:textSize="@dimen/text_size_en" />
			 * 
			 * <TextView android:paddingTop="6dip" android:paddingBottom="6dip"
			 * android:layout_width="0.0dip" android:layout_height="fill_parent"
			 * android:id="@+id/result_jibenwucha_biaozhunpiancha1"
			 * android:layout_weight="4.0" android:layout_marginLeft="-2.0dip"
			 * android:layout_marginTop="-2.0dip"
			 * android:background="@drawable/cell_noteditable_bg"
			 * android:enabled="false" android:gravity="center"
			 * android:text="0.001%" android:textColor="@color/text_color_main"
			 * android:textSize="@dimen/text_size_num" />
			 * 
			 * </TableRow>
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void addRow(String[] arr_result) {
		Log.i("addRow", " init");

		for (int i = 1; i <= arr_result.length; i++) {

			LinearLayout linearLayout = new LinearLayout(this);
			// 设置LinearLayout属性(宽和高)
			linearLayout.setOrientation(0);

			linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			LinearLayout.LayoutParams lp_left = new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.FILL_PARENT, 1.5f);

			lp_left.leftMargin = -2;
			lp_left.topMargin = -2;

			LinearLayout.LayoutParams lp_right = new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.FILL_PARENT, 4.0f);
			// lp_right.weight = 4.0f;
			lp_right.leftMargin = -2;
			lp_right.topMargin = -2;

			// TableRow tr = new TableRow(this);
			TextView tv_left = new TextView(this);
			TextView tv_right = new TextView(this);
			// 左侧
			tv_left.setGravity(Gravity.CENTER);
			tv_left.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			tv_left.setTextColor(Color.parseColor("#ff000000"));
			tv_left.setPadding(0, 6, 0, 6);
			tv_left.setBackgroundResource(R.drawable.cell_header_bg);
			tv_left.setText(getText(R.string.error).toString() + i);
			tv_left.setLayoutParams(lp_left);
			// 右侧
			tv_right.setGravity(Gravity.CENTER);
			tv_right.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			tv_right.setTextColor(Color.parseColor("#ff000000"));
			tv_right.setPadding(0, 6, 0, 6);
			tv_right.setBackgroundResource(R.drawable.cell_noteditable_bg);
			tv_right.setText(arr_result[i - 1]);
			tv_right.setLayoutParams(lp_right);

			linearLayout.addView(tv_left);
			linearLayout.addView(tv_right);
			// tr.addView(linearLayout);
			// tr.addView(tv_left);
			// tr.addView(tv_right);
			layout_main.addView(linearLayout);

		}

	}
}
