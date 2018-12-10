package com.wisdom.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.wisdom.bean.AutoCheckResultBean;
import com.wisdom.bean.AutoCheckSchemeResultBean;
import com.wisdom.dao.ACNLDao;
import com.wisdom.dao.ACNLSchemeResultAdapter;
import com.wisdom.dao.ListAutoCheckResultAdapter;
import com.wisdom.layout.TitleLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ACNLSchemeActivity extends Activity {

	List<AutoCheckSchemeResultBean> list_scheme_result_bean;
	ACNLSchemeResultAdapter scheme_result_adapter;
	@Bind(R.id.title_scheme)
	TitleLayout title;
	@Bind(R.id.lv_autocheck_result)
	ListView lv_autocheck_result;
	@Bind(R.id.btn_autocheck_add_plan)
	Button btn_autocheck_add_plan;
	@Bind(R.id.btn_autocheck_ok_plan)
	Button btn_autocheck_ok_plan;
	@Bind(R.id.btn_autocheck_update)
	Button btn_autocheck_update;
	@Bind(R.id.btn_autocheck_delete)
	Button btn_autocheck_delete;

	ACNLDao acnlDao;
	private SharedPreferences scheme;
	int item_result_position = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scheme);
		ButterKnife.bind(this);
		title.setTitleText(String.format(getText(R.string.automatic_validation_of_virtual_load_title).toString(),getText(R.string.choose_plan)));
		initView();
		initData();
		btn_autocheck_add_plan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ACNLSchemeActivity.this, ACNLOptionSchemeActivity.class);
				startActivity(intent);
			}
		});
		lv_autocheck_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				item_result_position = position;
				Log.e("option", "lv_result position:" + position);
			}
		});
		btn_autocheck_ok_plan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (item_result_position > 0&&item_result_position<=scheme_result_adapter.getCount()) {
					Editor editor = scheme.edit();
					editor.putString("schemeId", list_scheme_result_bean.get(item_result_position - 1).getId() + "");
					editor.putString("schemeName",
							list_scheme_result_bean.get(item_result_position - 1).getSchemeName());
					editor.commit();
					finish();
					// Toast.makeText(getApplicationContext(), position+"",
					// Toast.LENGTH_SHORT).show();
				}
			}
		});
		btn_autocheck_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (item_result_position > 0&&item_result_position<=scheme_result_adapter.getCount()) {
				   Intent intent = new Intent(ACNLSchemeActivity.this, ACNLOptionSchemeActivity.class);
				   intent.putExtra("SchemeId", list_scheme_result_bean.get(item_result_position - 1).getId()+"");
				   intent.putExtra("SchemeName", list_scheme_result_bean.get(item_result_position - 1).getSchemeName());
				   startActivity(intent);
				}
			}
		});
		btn_autocheck_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (item_result_position > 0&&item_result_position<=scheme_result_adapter.getCount()) {
					acnlDao.removeScheme(list_scheme_result_bean.get(item_result_position - 1).getId());
					scheme_result_adapter.delete(item_result_position);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							scheme_result_adapter.notifyDataSetChanged();
						}
					});
				}

			}
		});
	}

	private void initView() {

		View lv_header = LayoutInflater.from(ACNLSchemeActivity.this).inflate(R.layout.listview_autocheck_scheme_head,
				null);
		lv_autocheck_result.addHeaderView(lv_header);

		scheme = ACNLSchemeActivity.this.getSharedPreferences("scheme", Context.MODE_PRIVATE);// ´æ´¢·½°¸
	}

	private void initData() {
		acnlDao = new ACNLDao(getApplicationContext());
		list_scheme_result_bean = acnlDao.findScheme();
		scheme_result_adapter = new ACNLSchemeResultAdapter(list_scheme_result_bean, this);
		lv_autocheck_result.setAdapter(scheme_result_adapter);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
	}
}
