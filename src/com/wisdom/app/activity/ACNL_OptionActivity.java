package com.wisdom.app.activity;

import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.bean.ACNL_JiBenWuChaBean;
import com.wisdom.layout.TitleLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ACNL_OptionActivity extends Activity{
	
	@Bind(R.id.title_acnl_option)
	TitleLayout title;
	@Bind(R.id.sp_option_power_type)
	Spinner sp_power_type;
	@Bind(R.id.sp_option_Ub)
	Spinner sp_Ub;
	@Bind(R.id.sp_option_Ib)
	Spinner sp_Ib;
	@Bind(R.id.sp_option_Ur)
	Spinner sp_Ur;
	@Bind(R.id.sp_option_Ir)
	Spinner sp_Ir;
	@Bind(R.id.sp_option_pf)
	Spinner sp_pf;
	@Bind(R.id.sp_option_rate)
	Spinner sp_rate;
	@Bind(R.id.sp_option_circle)
	Spinner sp_circle;
	@Bind(R.id.sp_option_count)
	Spinner sp_count;
	@Bind(R.id.sp_option_error_range)
	Spinner sp_error_range;
	@Bind(R.id.btn_option_save)
	Button btn_save;
	
	ACNL_JiBenWuChaBean bean;
	SharedPreferences preference_option;
	SharedPreferences preference_dianbiaocanshu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_acnl_option);
		ButterKnife.bind(this);
		//初始化数据
		initData();
		//初始化选项
		initOption();
		btn_save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveOption();
				finish();
			}
		});
	}
	private void initData()
	{
		title.setTitleText("虚负荷自动校验->添加测试项");
		bean=new ACNL_JiBenWuChaBean();
		preference_option = getSharedPreferences("ACNL_Option", Context.MODE_PRIVATE); // 私有数据
		preference_dianbiaocanshu = getSharedPreferences("ParameterSetting", Context.MODE_PRIVATE);
		
	}
	private void initOption()
	{
		String str_ub=preference_option.getString("ub", "");
		String str_ib=preference_option.getString("ib", "");
		String str_ur=preference_option.getString("ur", "");
		String str_ir=preference_option.getString("ir", "");
		String str_type=preference_option.getString("type", "");
		String str_pf=preference_option.getString("pf", "");
		/*if(str_pf.equals("0"))
			str_pf="1.0";
		else if(str_pf.equals("75.5"))
			str_pf="0.25L";
		else if(str_pf.equals("284.5"))
			str_pf="0.25C";
		else if(str_pf.equals("60"))
			str_pf="0.5L";
		else if(str_pf.equals("300"))
			str_pf="0.5C";
		else if(str_pf.equals("36.9"))
			str_pf="0.8L";
		else if(str_pf.equals("323.1"))
			str_pf="0.8C";*/
		String str_rate=preference_option.getString("rate", "");
		String str_count=preference_option.getString("count", "");
		String str_circle=preference_option.getString("circle", "");
		String str_error_limit=preference_option.getString("error_limit", "");
		
		setSpinnerSelection(sp_Ub,str_ub);
		setSpinnerSelection(sp_Ib,str_ib);
		setSpinnerSelection(sp_Ur,str_ur);
		setSpinnerSelection(sp_Ir,str_ir);
		setSpinnerSelection(sp_power_type,str_type);
		
		setSpinnerSelection(sp_pf,str_pf);
		setSpinnerSelection(sp_rate,str_rate);
		setSpinnerSelection(sp_count,str_count);
		setSpinnerSelection(sp_circle,str_circle);
		setSpinnerSelection(sp_error_range,str_error_limit);
	}
	private void saveOption() {
		try {
			Editor editor = preference_option.edit();// 获取编辑器
			String str_ub=sp_Ub.getSelectedItem().toString();
			String str_ib=sp_Ib.getSelectedItem().toString();
			String str_ur=sp_Ur.getSelectedItem().toString();
			String str_ir=sp_Ir.getSelectedItem().toString();
			String str_type=sp_power_type.getSelectedItem().toString();
			String str_pf=sp_pf.getSelectedItem().toString();
			String str_rate=sp_rate.getSelectedItem().toString();
			String str_count=sp_count.getSelectedItem().toString();
			String str_circle=sp_circle.getSelectedItem().toString();
			String str_error_limit=sp_error_range.getSelectedItem().toString();
			/*if(str_pf.equals("1.0"))
				str_pf="0";
			else if(str_pf.equals("0.25L"))
				str_pf="75.5";
			else if(str_pf.equals("0.25C"))
				str_pf="284.5";
			else if(str_pf.equals("0.5L"))
				str_pf="60";
			else if(str_pf.equals("0.5C"))
				str_pf="300";
			else if(str_pf.equals("0.8L"))
				str_pf="36.9";
			else if(str_pf.equals("0.8C"))
				str_pf="323.1";*/
			editor.putString("ub", str_ub);
			editor.putString("ib", str_ib);
			editor.putString("ur", str_ur);
			editor.putString("ir", str_ir);
			editor.putString("type",str_type);
			editor.putString("pf", str_pf);
			editor.putString("rate", str_rate);
			editor.putString("count", str_count);
			editor.putString("circle", str_circle);
			editor.putString("error_limit", str_error_limit);

			editor.commit();
		    bean.setU(str_ub);
		    Log.e("option","ub:"+str_ub);
		    bean.setI(str_ib);
		    bean.setUr(str_ur);
		    Log.e("option","ur:"+str_ur);
		    bean.setIr(str_ir);
		    Log.e("option","Ir:"+str_ir);
		    bean.setPower_type(str_type);
		    bean.setGonglvyinshu(str_pf);
		    bean.setPinlv(str_rate);
		    bean.setCishu(str_count);
		    bean.setQuanshu(str_circle);
		    bean.setError_limit(str_error_limit);
		    Log.e("option","limit:"+str_error_limit);
		    Intent intent=new Intent();
		    Bundle bundle=new Bundle();
		    bundle.putSerializable("option",bean);
		    intent.putExtras(bundle);
		    setResult(100,intent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/*
	 * 设置Spinner默认item
	 */
	private void setSpinnerSelection(Spinner spinner, String item) {
		SpinnerAdapter apsAdapter = spinner.getAdapter();
		int k = apsAdapter.getCount();
		for (int i = 0; i < k; i++) {
			if (item.equals(apsAdapter.getItem(i).toString())) {
				spinner.setSelection(i, true);
				break;
			}
		}
	}
}
