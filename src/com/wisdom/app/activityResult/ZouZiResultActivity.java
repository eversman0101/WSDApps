package com.wisdom.app.activityResult;

import com.wisdom.app.activity.R;
import com.wisdom.app.activity.R.layout;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.bean.DianbiaoZouZiBean;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.dao.IZouZiDao;
import com.wisdom.dao.JiBenWuChaDao;
import com.wisdom.dao.ZouZiDao;
import com.wisdom.layout.TitleLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ZouZiResultActivity extends Activity {
	@Bind(R.id.title_result)TitleLayout title;
	@Bind(R.id.result_username)EditText et_username;
	@Bind(R.id.result_jiaoyanyuan)EditText et_jiaoyanyuan;
	@Bind(R.id.result_jibenwucha_u)TextView tv_u;
	@Bind(R.id.result_jibenwucha_i)TextView tv_i;
	@Bind(R.id.result_jibenwucha_yougong)TextView tv_yougong;
	@Bind(R.id.result_jibenwucha_wugong)TextView tv_wugong;
	@Bind(R.id.result_jibenwucha_gonglvyinshu)TextView tv_gonglvyinshu;
	@Bind(R.id.result_jibenwucha_xiangjiao)TextView tv_jiaodu;
	
	@Bind(R.id.result_zouzi_zouzifangshi)TextView tv_zouzifangshi;
	@Bind(R.id.result_zouzi_yuzhidianneng)TextView tv_yuzhidianneng;
	@Bind(R.id.result_zouzi_beilv)TextView tv_beilv;
	@Bind(R.id.result_zouzi_biaozhundianneng)TextView tv_biaozhundianneng;
	@Bind(R.id.result_zouzi_qishi1)TextView tv_qishi1;
	@Bind(R.id.result_zouzi_qishi2)TextView tv_qishi2;
	@Bind(R.id.result_zouzi_qishi3)TextView tv_qishi3;
	
	@Bind(R.id.result_zouzi_jieshu1)TextView tv_jieshu1;
	@Bind(R.id.result_zouzi_jieshu2)TextView tv_jieshu2;
	@Bind(R.id.result_zouzi_jieshu3)TextView tv_jieshu3;
	
	@Bind(R.id.result_zouzi_shiji1)TextView tv_shiji1;
	@Bind(R.id.result_zouzi_shiji2)TextView tv_shiji2;
	@Bind(R.id.result_zouzi_shiji3)TextView tv_shiji3;
	
	@Bind(R.id.result_zouzi_wucha1)TextView tv_wucha1;
	@Bind(R.id.result_zouzi_wucha2)TextView tv_wucha2;
	@Bind(R.id.result_zouzi_wucha3)TextView tv_wucha3;
	@Bind(R.id.result_zouzi_id)EditText tv_id;
	@Bind(R.id.btn_savejiaoyanjieguo) Button btn_save;
	@Bind(R.id.btn_visible) LinearLayout btn_visible;
	
	@Bind(R.id.tv_zouzi1)
	TextView tv_zouzi1;
	@Bind(R.id.tv_zouzi2)
	TextView tv_zouzi2;
	@Bind(R.id.tv_zouzi3)
	TextView tv_zouzi3;
	@Bind(R.id.tv_yuzhidianneng)
	TextView tv_show_yuzhidianneng;
	@Bind(R.id.tv_left_standard_energy)
	TextView tv_left_standard_energy;
	IZouZiDao dao;
	String id =null;
	DianbiaoZouZiBean bean=null;
	private SharedPreferences sharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_zou_zi_result);
		ButterKnife.bind(this);
		if(MissionSingleInstance.getSingleInstance().getFuhe_state()==1)
			title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.walk_test).toString())+" > "+getText(R.string.inspection_results));
		if(MissionSingleInstance.getSingleInstance().getFuhe_state()==2)
			title.setTitleText(String.format(getText(R.string.manual_validation_of_real_load_placeholder).toString(),getText(R.string.walk_test).toString())+" > "+getText(R.string.inspection_results));
		
		if(MissionSingleInstance.getSingleInstance().getFuhe_state()==3)
		{
			title.setTitleText(String.format(getText(R.string.manual_validation_of_virtual_load_placeholder).toString(),getText(R.string.constant_check).toString())+" > "+getText(R.string.inspection_results));
			tv_zouzi1.setText(getText(R.string.pulse_constant_1));
			tv_zouzi2.setText(getText(R.string.pulse_constant_2));
			tv_zouzi3.setText(getText(R.string.pulse_constant_3));
		}
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
					Toast.makeText(ZouZiResultActivity.this, getText(R.string.save_ok), Toast.LENGTH_SHORT).show();
					finish();
				}
				else{
					Toast.makeText(ZouZiResultActivity.this, getText(R.string.result_null), Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	private void initData()
	{
		 Intent intent = getIntent();
         id = intent.getStringExtra("id");
         //int iZouzifangshi=intent.getIntExtra("zouzifangshi",0);
		 dao=new ZouZiDao(this);
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
		/*sharedPreferences = getSharedPreferences("MCNL_JiBenWuCha", Context.MODE_PRIVATE); // 私有数据
		
		String chooser_u = sharedPreferences.getString("tv_chooser_u", "220.00");
		String chooser_i = sharedPreferences.getString("tv_chooser_i", "10.00");
		String jiaodu=sharedPreferences.getString("sp_chooser_p", "50.0");
*/
			 if(id==null){
		    	 bean = (DianbiaoZouZiBean) getIntent().getSerializableExtra("zouzibean");
			    if(bean==null){
			    	return;
			    }
		    }
	        else{
			  bean=dao.findById(id);
	         }
		//DianbiaoZouZiBean bean=dao.find();
			    et_username.setText(bean.getUserName());
			    et_jiaoyanyuan.setText(bean.getStuffName());
				//tv_id.setText(String.valueOf(bean.getId()));
			    tv_id.setText(bean.getNo());
		tv_u.setText(bean.getU());
		tv_i.setText(bean.getI());
		tv_jiaodu.setText(bean.getJiaodu());
		tv_yougong.setText(bean.getYougong());
		tv_wugong.setText(bean.getWugong());
		tv_gonglvyinshu.setText(bean.getGonglvyinshu());
		
		String str_zouzifangshi=bean.getZouzifangshi();
		if(str_zouzifangshi.equals(getText(R.string.walk_time).toString()))
		{
			tv_show_yuzhidianneng.setText(getText(R.string.duration_0));
		}else if(str_zouzifangshi.equals(getText(R.string.pulse_counts).toString()))
		{
			tv_show_yuzhidianneng.setText(getText(R.string.pulse_shu));
			tv_left_standard_energy.setText(getText(R.string.pulse_geshu));
		}
		
		tv_zouzifangshi.setText(bean.getZouzifangshi());
		tv_yuzhidianneng.setText(bean.getYuzhidianneng());
		tv_beilv.setText(bean.getBeilv());
		tv_biaozhundianneng.setText(bean.getBiaozhundianneng());
		tv_qishi1.setText(bean.getQishi1());
		tv_qishi2.setText(bean.getQishi2());
		tv_qishi3.setText(bean.getQishi3());
		tv_jieshu1.setText(bean.getJieshu1());
		tv_jieshu2.setText(bean.getJieshu2());
		tv_jieshu3.setText(bean.getJieshu3());
		tv_shiji1.setText(bean.getShiji1());
		tv_shiji2.setText(bean.getShiji2());
		tv_shiji3.setText(bean.getShiji3());
		tv_wucha1.setText(bean.getWucha1());
		tv_wucha2.setText(bean.getWucha2());
		tv_wucha3.setText(bean.getWucha3());
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
