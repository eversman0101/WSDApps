package com.wisdom.app.activityResult;

import com.wisdom.app.activity.R;
import com.wisdom.bean.AutoCheckResultBean;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.dao.ACNLDao;
import com.wisdom.dao.JiBenWuChaDao;
import com.wisdom.layout.TitleLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ACNLResultActivity extends Activity{
	@Bind(R.id.title_result_acnl)
	TitleLayout title;
	
	@Bind(R.id.result_acnl_id)
	TextView tv_id;
	@Bind(R.id.result_acnl_test_type)
	TextView tv_test_type;
	@Bind(R.id.result_acnl_power_type)
	TextView tv_power_type;
	@Bind(R.id.result_acnl_ub)
	TextView tv_ub;
	@Bind(R.id.result_acnl_ib)
	TextView tv_ib;
	@Bind(R.id.result_acnl_ur)
	TextView tv_ur;
	@Bind(R.id.result_acnl_ir)
	TextView tv_ir;
	@Bind(R.id.result_acnl_pf)
	TextView tv_pf;
	@Bind(R.id.result_acnl_pinlv)
	TextView tv_pinlv;
	@Bind(R.id.result_acnl_circle)
	TextView tv_circle;
	@Bind(R.id.result_acnl_cishu)
	TextView tv_cishu;
	
	@Bind(R.id.result_acnl_wucha_limit)
	TextView tv_wucha_limit;
	@Bind(R.id.result_acnl_result1)
	TextView tv_result1;
	@Bind(R.id.result_acnl_result2)
	TextView tv_result2;
	@Bind(R.id.result_acnl_result3)
	TextView tv_result3;
	
	@Bind(R.id.result_acnl_date)
	TextView tv_date;
	ACNLDao dao;
	String id=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_acnl_result);
		ButterKnife.bind(this);
		title.setTitleText(String.format(getText(R.string.automatic_validation_of_virtual_load_title).toString(),getText(R.string.verification_result)));
		
		initData();
	    getData();
	}
	private void initData()
	{
		 Intent intent = getIntent();
         id = intent.getStringExtra("id");
		 dao=new ACNLDao(this);
		 
	}
	private void getData()
	{
		try
		{
		AutoCheckResultBean bean;
		if(id!=null)
		  bean=dao.findById(id);
		else
		  bean=dao.find();
		tv_id.setText(String.valueOf(bean.getId()));
		tv_test_type.setText(bean.getTest_type());
		tv_power_type.setText(bean.getPower_type());
		tv_ub.setText(bean.getUb()+"V");
		tv_ib.setText(bean.getIb()+"A");
		tv_ur.setText(bean.getUr()+"%");
		tv_ir.setText(bean.getIr());
		tv_pf.setText(bean.getPower_factor());
		tv_pinlv.setText(bean.getPinlv()+"Hz");
		tv_circle.setText(bean.getQuanshu());
		tv_cishu.setText(bean.getCishu());
		tv_wucha_limit.setText(bean.getWucha_limit());
		tv_result1.setText(bean.getResult1());
		tv_result2.setText(bean.getResult2());
		tv_result3.setText(bean.getResult3());
		tv_date.setText(bean.getDate());
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
