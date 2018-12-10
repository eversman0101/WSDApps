package com.wisdom.app.activityResult;

import com.wisdom.app.activity.R;
import com.wisdom.app.activity.R.layout;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.QianDongBean;
import com.wisdom.dao.IQiandongDao;
import com.wisdom.dao.JiBenWuChaDao;
import com.wisdom.dao.QianDongDao;
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
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

public class QianDongResultActivity extends Activity {
	@Bind(R.id.title_result)TitleLayout title;
	@Bind(R.id.result_username)EditText et_username;
	@Bind(R.id.result_jiaoyanyuan)EditText et_jiaoyanyuan;
	@Bind(R.id.result_qiandong_date) TextView tv_date;
	@Bind(R.id.result_qiandong_maichongchangshu)TextView tv_changshu;
	@Bind(R.id.result_qiandong_u)TextView tv_u;
	@Bind(R.id.result_qiandong_i)TextView tv_i;
	@Bind(R.id.result_qiandong_shijian)TextView tv_shijian;
	@Bind(R.id.result_qiandong_result1)TextView tv_result1;
	@Bind(R.id.result_qiandong_result2)TextView tv_result2;
	@Bind(R.id.result_qiandong_result3)TextView tv_result3;
	@Bind(R.id.result_jibenwucha_id)EditText tv_id;
	@Bind(R.id.btn_savejiaoyanjieguo) Button btn_save;
	@Bind(R.id.btn_visible) LinearLayout btn_visible;
	IQiandongDao dao;
	String id=null;
    QianDongBean bean =null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qian_dong_result);
		ButterKnife.bind(this);
		if(MissionSingleInstance.getSingleInstance().getFuhe_state()==1)
		//title.setTitleText("虚负荷手动校验->潜动试验->校验结果");
			title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.false_actuation_test).toString())+" > "+getText(R.string.inspection_results));
		if(MissionSingleInstance.getSingleInstance().getFuhe_state()==2)
			title.setTitleText(String.format(getText(R.string.manual_validation_of_real_load_placeholder).toString(),getText(R.string.false_actuation_test).toString())+" > "+getText(R.string.inspection_results));
	    initData();
	    getData();
	    btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(bean!=null)
				{
					bean.setNo(tv_id.getText().toString());
					bean.setUserName(et_username.getText().toString());
					bean.setStuffName(et_jiaoyanyuan.getText().toString());
					dao.add(bean);//保存校验结果
					Toast.makeText(QianDongResultActivity.this, getText(R.string.save_ok), Toast.LENGTH_SHORT).show();
					finish();
				}
				else{
					Toast.makeText(QianDongResultActivity.this, getText(R.string.result_null), Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	private void initData()
	{
		 Intent intent = getIntent();
         id = intent.getStringExtra("id");
		 dao=new QianDongDao(this);
		 if(id!=null){
			 et_username.setEnabled(false);
			 tv_id.setEnabled(false);
			 et_jiaoyanyuan.setEnabled(false);
			 btn_visible.setVisibility(View.GONE);  
		 }
	}
	private void getData()
	{
		try
		{
		if(id==null){
		    	 bean = (QianDongBean) getIntent().getSerializableExtra("qiandongBean");
			    if(bean==null){
			    	return;
			    }
		    }
	        else{
			  bean=dao.findById(id);
	         }	
		//QianDongBean bean=dao.find();
	    et_username.setText(bean.getUserName());
	    et_jiaoyanyuan.setText(bean.getStuffName());
		//tv_id.setText(String.valueOf(bean.getId()));
	    tv_id.setText(bean.getNo());
		tv_u.setText(bean.getU());
		tv_i.setText(bean.getI());
		tv_date.setText(bean.getDate());
		tv_changshu.setText(bean.getChangshu());
		tv_shijian.setText(bean.getQiandongshijian());
	    tv_result1.setText(bean.getQiandongshiyan1());
		tv_result2.setText(bean.getQiandongshiyan2());
		tv_result3.setText(bean.getQiandongshiyan3());
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
