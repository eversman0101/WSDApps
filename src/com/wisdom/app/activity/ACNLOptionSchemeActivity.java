package com.wisdom.app.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.wisdom.app.utils.GridViewAdapter;
import com.wisdom.bean.AutoCheckResultBean;
import com.wisdom.bean.AutoCheckSchemeResultBean;
import com.wisdom.dao.ACNLDao;
import com.wisdom.layout.TitleLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ACNLOptionSchemeActivity extends Activity  {
	@Bind(R.id.title_option_scheme)
	TitleLayout title;
	@Bind(R.id.grid)
	GridView grid;
	@Bind(R.id.grid_head)
	GridView gridHead;
	@Bind(R.id.grid_body)
	GridView gridBody;
	@Bind(R.id.schemeName)
	EditText et_schemeName;
	
	@Bind(R.id.sp_option_power_type)
	Spinner sp_power_type;
	@Bind(R.id.sp_option_Ub)
	Spinner sp_Ub;
	@Bind(R.id.sp_option_Ib)
	Spinner sp_Ib;
	@Bind(R.id.sp_option_Ur)
	Spinner sp_Ur;
	@Bind(R.id.sp_option_rate)
	EditText sp_rate;
	@Bind(R.id.sp_option_circle)
	Spinner sp_circle;
	@Bind(R.id.sp_option_count)
	Spinner sp_count;
	@Bind(R.id.sp_option_error_range)
	Spinner sp_error_range;
	
	@Bind(R.id.btn_autocheck_add_plan)
	Button btn_add;
	@Bind(R.id.btn_autocheck_cancle)
	Button btn_cancle;
	
	List<AutoCheckResultBean> item_area;
	List item_head;
	List item_body;
	int indexArea =-1;
	ACNLDao acnlDao;
	String id=null;
	String schemeId =null;//编辑方案
	
	String[] rowList={"0.05Ib","0.1Ib","0.2Ib","0.5Ib","0.8Ib","Ib","Imax"};//保存行头数据
	String[] colList={"1.0","0.25L","0.25C","0.5L","0.5C","0.8L","0.8C"};//保存列头数据
	int row;
	int col;
	List<AutoCheckResultBean> acResultBean ;
	AutoCheckResultBean asBean;
	AutoCheckSchemeResultBean acsResultBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_acnloption_scheme);
		ButterKnife.bind(this);
		title.setTitleText(String.format(getText(R.string.automatic_validation_of_virtual_load_title).toString(),getText(R.string.txt_edit_plan)));
		initView();
		initData();

		grid.setOnItemClickListener(new OnItemClickListener() {  
			  
            @Override  
            public void onItemClick(android.widget.AdapterView<?> parent,  
                    View view, int position, long id) {  
                // TODO Auto-generated method stub  
  
/*                if (indexArea != -1) {  
                    CheckedTextView tvShowClose = (CheckedTextView) parent  
                            .getChildAt(indexArea).findViewById(  
                                    R.id.item_grid_tvShow);  
                    tvShowClose.setChecked(false);  
                }  */
//                indexArea = position;  
                CheckedTextView tvShow = (CheckedTextView) view  
                        .findViewById(R.id.item_grid_tvShow);  
                if (tvShow.isChecked()) {  
                    tvShow.setChecked(false);
                    row=position/7;
                    col=position%7;
                    for(int i = acResultBean.size() - 1; i >= 0; i--){
                        AutoCheckResultBean item = acResultBean.get(i);
                        if(item.getIr().equalsIgnoreCase(rowList[row])&&item.getPower_factor().equalsIgnoreCase(colList[col])){
                        	acResultBean.remove(item);
                        }
                    }        
                } else {  
                    tvShow.setChecked(true); 
                    row=position/7;
                    col=position%7;
                    asBean= new AutoCheckResultBean();
                    asBean.setIr(rowList[row]);
                    asBean.setPower_factor(colList[col]);
                    acResultBean.add(asBean);
                }  
  
            }  
        });
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(et_schemeName.getText().toString().trim().isEmpty()||acResultBean.size()==0){
					Toast.makeText(getApplicationContext(), getText(R.string.toast_plan_null), Toast.LENGTH_SHORT).show();
					return ;
				}
				if(sp_rate.getText().toString().isEmpty()){
					Toast.makeText(getApplicationContext(), getText(R.string.toast_frequency_null), Toast.LENGTH_SHORT).show();
					return ;
				}
				if(Integer.valueOf(sp_rate.getText().toString())<=40||Integer.valueOf(sp_rate.getText().toString())>=60){
					Toast.makeText(getApplicationContext(), getText(R.string.frequency_range_40_60), Toast.LENGTH_SHORT).show();
					return;
				}
				acsResultBean.setSchemeName(et_schemeName.getText().toString());
				//保存方案前，判断是增加方案还是编辑方案
				if(schemeId!=null){
					//编辑方案，删除原有的方案
					acnlDao.removeScheme(Integer.valueOf(schemeId));
				}
				//确定方案名不重复
				if(acnlDao.findSchemeByName(et_schemeName.getText().toString())==-1){

					
					//保存方案对象，返回主键
					acnlDao.add(acsResultBean);
				}
				else{
					Toast.makeText(getApplicationContext(), getText(R.string.toast_plan_duplicate), Toast.LENGTH_SHORT).show();
					return ;
				}
				
				
				
				for(AutoCheckResultBean a:acResultBean){
					a.setIb(sp_Ib.getSelectedItem().toString());
					a.setPower_type(sp_power_type.getSelectedItem().toString());
					a.setQuanshu(sp_circle.getSelectedItem().toString());
					a.setUb(sp_Ub.getSelectedItem().toString());
					a.setUr(sp_Ur.getSelectedItem().toString());
					a.setCishu(sp_count.getSelectedItem().toString());
					a.setPinlv(sp_rate.getText().toString());
					a.setWucha_limit(sp_error_range.getSelectedItem().toString());
					a.setTest_type("0");
					//插入方案主键
					a.setSchemeId(acnlDao.findSchemeByName(et_schemeName.getText().toString()));
					Log.i("resultacccccc", a.getIr());
				}
				acnlDao.add(acResultBean);
				finish();
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		sp_rate.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
                if(!s.toString().isEmpty()){
                   int value = Integer.valueOf(s.toString());
                    if(value>60){
                	   sp_rate.setText(String.valueOf(60));
                   }
                }
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void initView()
	{

	  	 
	  	item_head = new ArrayList<>();  
	  	item_body = new ArrayList<>();
	  	HashMap<String, String> map;
	  	HashMap<String, String> map1;
	  	map = new HashMap<>();  
		map.put("itemName", "Ir(%)\\Pv");
		item_head.add(map);
	  	for(int i=0;i<colList.length;i++){
	  		 map = new HashMap<>();
	  		map.put("itemName", colList[i]);
	  		item_head.add(map);
	  	}
	  	for(int i=0;i<rowList.length;i++){
	  		 map1 = new HashMap<>();
	  		map1.put("itemName", rowList[i]);
	  		item_body.add(map1);
	  	}
		
        SimpleAdapter  adapterArea = new SimpleAdapter(  
                this, item_head,  
                R.layout.grid_item, new String[] { "itemName" },  
                new int[] { R.id.item_grid_tvShow }); 
        SimpleAdapter  adapterArea1 = new SimpleAdapter(  
                this, item_body,  
                R.layout.grid_item, new String[] { "itemName" },  
                new int[] { R.id.item_grid_tvShow }); 
        
        gridHead.setAdapter(adapterArea);   
        gridBody.setAdapter(adapterArea1); 
        	
		
	}
	private void initData()
	{
        acResultBean=new ArrayList<AutoCheckResultBean>();
        acsResultBean = new AutoCheckSchemeResultBean();
		acnlDao =new ACNLDao(getApplicationContext());
		
	  	item_area = new ArrayList<AutoCheckResultBean>();
	  	 for (int i = 0; i < 49; i++) {  
	  		 item_area.add(new AutoCheckResultBean());
	  	 }

	  	 schemeId =getIntent().getStringExtra("SchemeId"); 
	  	 
	  	if(schemeId!=null){
	  		et_schemeName.setText(getIntent().getStringExtra("SchemeName"));
	  		acResultBean = acnlDao.findSchemeBySchemeId(schemeId);
	  	}
			for(int i=0;i<acResultBean.size();i++){
				for(int j=0;j<7;j++){
					if(acResultBean.get(i).getPower_factor().equalsIgnoreCase(colList[j])){
						Log.i("itemsCheckedjjjj", "j:"+j);
						for(int m =0;m<7;m++){
							if(acResultBean.get(i).getIr().equalsIgnoreCase(rowList[m])){
								//acResultBean.get(i).setPosition(j*7+m);
								item_area.get(m*7+j).setCheck(true);
								 
							}
						}
					}
				}
			
		}
			grid.setAdapter(new GridViewAdapter(this,item_area));
	  	/*    for (int i = 0; i < 63; i++) {  
        	  HashMap<String, String> map = new HashMap<>();  
        	  HashMap<String, String> map2 = new HashMap<>();  
            map.put("itemName", "");  
            item_area.add(map);  
        }  
        SimpleAdapter  adapterArea = new SimpleAdapter(  
                this, item_area,  
                R.layout.gridview_checktextview_item, new String[] { "itemName" },  
                new int[] { R.id.item_grid_tvShow });  

        grid.setAdapter(adapterArea);  */
		


	}

}
