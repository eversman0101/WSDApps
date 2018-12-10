package com.wisdom.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.wisdom.app.utils.StopException;
import com.wisdom.bean.ACNL_JiBenWuChaBean;
import com.wisdom.bean.AutoCheckResultBean;
import com.wisdom.dao.ACNLDao;
import com.wisdom.dao.ListAutoCheckResultAdapter;
import com.wisdom.layout.TitleLayout;
import com.wisdom.service.MeasureProcess;
import com.wisdom.service.MeasureProcess.IProgressListener;
import com.wisdom.service.MeasureProcess.IStateListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

public class AutoCheckNoneLoadActivity extends Activity{
	String[] option ={"基本误差","潜动试验"};
	List<AutoCheckResultBean> list_result_bean=null;
	ListAutoCheckResultAdapter result_adapter=null;
	@Bind(R.id.title_auto_check_none_load)
	TitleLayout title;
	@Bind(R.id.lv_autocheck_result)
	ListView lv_result;
	@Bind(R.id.lv_autocheck_option)
	ListView lv_option;
	@Bind(R.id.btn_autocheck_add_plan)
	Button btn_add;
	@Bind(R.id.btn_autocheck_start_test)
	Button btn_start;
	@Bind(R.id.btn_autocheck_delete)
	Button btn_delete;
	@Bind(R.id.btn_autocheck_stop_test)
	Button btn_stop;
	Dialog progressDialog;
	TextView progressText;
	int item_option_position=0;
	int item_result_position=0;
	String str_test_type="基本误差";
	IProgressListener progress_listener;
	IStateListener state_listener;
	ACNLDao dao;
	MeasureProcess mp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_auto_check_none_load);
		initView();
		initData();
	}
	private void initView()
	{
		ButterKnife.bind(this);
		title.setTitleText(getText(R.string.automatic_validation_of_virtual_load).toString());
		
		progressDialog = new Dialog(AutoCheckNoneLoadActivity.this,R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressText = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        progressText.setText(getText(R.string.testing));
        
		View lv_header=LayoutInflater.from(AutoCheckNoneLoadActivity.this).inflate(R.layout.listview_auto_check_result_header, null);
		lv_result.addHeaderView(lv_header);

	    ArrayAdapter<String> array=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,option);
	    
	    lv_option.setAdapter(array);
	    lv_option.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				item_option_position=position;
			}});
	    lv_result.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				item_result_position=position;
				Log.e("option","lv_result position:"+position);
			}
		});
	    btn_add.setOnClickListener(new View.OnClickListener() {

			@Override 
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(AutoCheckNoneLoadActivity.this,ACNL_OptionActivity.class);
				int test_type=item_option_position;
				if(test_type==0)
					str_test_type="基本误差";
				if(test_type==1)
					str_test_type="潜动试验";
				startActivityForResult(intent,test_type);
			}
		});
	    btn_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mp.stop_flag=false;
			}
		});
	    btn_delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				result_adapter.delete(item_result_position);
			}
		});
	    btn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(result_adapter.getList().size()<=0)
					return;
				progressDialog.show();
				new Thread(){
					@Override
					public void run() {

						try {
						List<AutoCheckResultBean> acrb;
						
							acrb = mp.fnStart(result_adapter.getList(), state_listener);
						
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								progressDialog.dismiss();
								
							}});
						
						if(acrb!=null)
						{
							dao.add(acrb);
						}
						
						else
							Log.e("acnl","acrb is null");
						} catch (StopException e) {
							e.printStackTrace();
							runOnUiThread(new Runnable(){
								@Override
								public void run() {
									progressDialog.dismiss();
									Toast.makeText(AutoCheckNoneLoadActivity.this, "已停止",Toast.LENGTH_SHORT).show();
								}});
						}
					}
					
				}.start();
				
					
			}
		});
	    
	}
	
	private void initData()
	{
		mp=new MeasureProcess(getApplicationContext());
		dao=new ACNLDao(getApplicationContext());
		list_result_bean=new ArrayList<AutoCheckResultBean>();
		result_adapter=new ListAutoCheckResultAdapter(list_result_bean,this);
		lv_result.setAdapter(result_adapter);
		
		progress_listener=new IProgressListener(){
			long BEG=0;
			@Override
			public void onProgress(long len, long total) {
				Log.i("SocketUtil","onProgress,len:"+len+" total:"+total);
				if (System.currentTimeMillis() - BEG > 200 || len == total) {
					BEG = System.currentTimeMillis();
					String result = String.format("%.2f", 100 * (double) len / (double) total);
					showTV(result + "%");
				}
				
			}};
			
		state_listener=new IStateListener(){

			@Override
			public void onState(int index, int state,int size) {
				// TODO Auto-generated method stub
				String str_state="待测试";
				switch(state)
				{
				case 0:
					str_state="测试中";
					break;
				case 1:
					str_state="测试完成";
					break;
				case -1:
					str_state="停止中";
					break;
				case -2:
					str_state="已停止";
					break;
				default:
					break;
				}
				result_adapter.getList().get(index).setTest_state(str_state);
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						result_adapter.notifyDataSetChanged();
					}});
			}};
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*if(requestCode==0)
			Toast.makeText(AutoCheckNoneLoadActivity.this,"0",Toast.LENGTH_SHORT).show();
		if(requestCode==1)
			Toast.makeText(AutoCheckNoneLoadActivity.this,"1",Toast.LENGTH_SHORT).show();*/
		if(resultCode==100)
		{
			ACNL_JiBenWuChaBean acnl_bean=(ACNL_JiBenWuChaBean)data.getSerializableExtra("option");
			AutoCheckResultBean result_bean=new AutoCheckResultBean();
			result_bean.setNo(String.valueOf(lv_result.getAdapter().getCount()));
			result_bean.setTest_type(str_test_type);
			result_bean.setPower_type(acnl_bean.getPower_type());
			result_bean.setUb(acnl_bean.getU());
			result_bean.setIb(acnl_bean.getI());
			result_bean.setUr(acnl_bean.getUr());
			Log.e("option_end","ur:"+acnl_bean.getUr());
			result_bean.setIr(acnl_bean.getIr());
			Log.e("option_end","ir:"+acnl_bean.getIr());
			result_bean.setPower_factor(acnl_bean.getGonglvyinshu());
			result_bean.setPinlv(acnl_bean.getPinlv());
			result_bean.setQuanshu(acnl_bean.getQuanshu());
			result_bean.setCishu(acnl_bean.getCishu());
			Log.e("option_end","wucha_limit:"+acnl_bean.getError_limit());
			result_bean.setWucha_limit(acnl_bean.getError_limit());
			
			result_adapter.add(result_bean);
		}
	}
	private final static int CMD_TOAST_INFO = 0;
	private final static int CMD_TEXTVIEW_INFO = 1;
	private final static int CMD_LOG_INFO = -1;
	Handler commonhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			
			case CMD_TEXTVIEW_INFO:
				progressText.setText(msg.obj.toString());
				//tv_progress.setText(msg.obj.toString());
				break;
		}
	}
	};
	private void showTV(String info) {
		Message msg = commonhandler.obtainMessage();
		msg.what = CMD_TEXTVIEW_INFO;
		msg.obj = info;
		commonhandler.sendMessage(msg);
	}
}
