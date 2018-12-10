package com.wisdom.app.activity;

import java.util.List;

import com.wisdom.app.utils.StopException;
import com.wisdom.bean.AutoCheckResultBean;
import com.wisdom.dao.ACNLDao;
import com.wisdom.layout.TitleLayout;
import com.wisdom.service.MeasureProcess;
import com.wisdom.service.MeasureProcess.IProgressListener;
import com.wisdom.service.MeasureProcess.IStateListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ACNLActivity extends Activity {
	@Bind(R.id.title_acnl)
	TitleLayout title;
	@Bind(R.id.tv_scheme)
	TextView tv_scheme;
	@Bind(R.id.bt_acnl_test)
	Button btn_acnl_test;
	@Bind(R.id.bt_acnl_stop)
	Button btn_acnl_stop;
	@Bind(R.id.tv_jindu)
	TextView tv_jindu;
	List<AutoCheckResultBean> autoCheckResultBeans;
	ACNLDao acnlDao;
	private SharedPreferences scheme;
	
	IProgressListener progress_listener;
	IStateListener state_listener;
	Dialog progressDialog;
	TextView progressText;
	MeasureProcess mp;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //获得刚才发送的Message对象，然后在这里进行UI操作
            if(msg.what==1){
            	
            }
        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_acnl);
		ButterKnife.bind(this);
		title.setTitleText(getText(R.string.automatic_validation_of_virtual_load).toString());
		initView();
		initData();
		tv_scheme.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ACNLActivity.this,ACNLSchemeActivity.class);
				startActivity(intent);
			}
		});
		btn_acnl_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mp.bl_test_state==false)
					return;
				mp.stop_flag=true;
			}
		});
		btn_acnl_test.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				for(int i =0;i<autoCheckResultBeans.size();i++){
					autoCheckResultBeans.get(i).setTest_type("基本误差");
				}
				if(autoCheckResultBeans.size()<=0){
					Toast.makeText(getApplicationContext(),getText(R.string.toast_choose_plan), Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(ACNLActivity.this,getText(R.string.start_testing),Toast.LENGTH_SHORT).show();
				//progressDialog.show();
				new Thread(){
					@Override
					public void run() {

						try {
						List<AutoCheckResultBean> acrb;
						
							acrb = mp.fnStart(autoCheckResultBeans, state_listener);
						/*
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								progressDialog.dismiss();
								
							}});
						*/
						if(acrb!=null)
						{
							acnlDao.add(acrb);
						}
						
						else
							Log.e("acnl","acrb is null");
						} catch (StopException e) {
							e.printStackTrace();
							runOnUiThread(new Runnable(){
								@Override
								public void run() {
									//progressDialog.dismiss();
									Toast.makeText(ACNLActivity.this,getText(R.string.stoppted),Toast.LENGTH_SHORT).show();
								}});
						}
					}
					
				}.start();
				
			}
		});
	}
	private void initView()
	{

		scheme = ACNLActivity.this.getSharedPreferences("scheme", Context.MODE_PRIVATE);//存储方案
		
		progressDialog = new Dialog(ACNLActivity.this,R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressText = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        progressText.setText(getText(R.string.testing));
	}
	private void initData()
	{
		mp=new MeasureProcess(getApplicationContext());
		tv_jindu.setText("");
	      String schemeName =scheme.getString("schemeName", getText(R.string.acnl_scheme_default).toString());
	      tv_scheme.setText(schemeName);
	      acnlDao = new ACNLDao(getApplicationContext());
		  autoCheckResultBeans= acnlDao.findSchemeBySchemeId(scheme.getString("schemeId", ""));
//			 Toast.makeText(getApplicationContext(), autoCheckResultBeans.size()+"", Toast.LENGTH_SHORT).show();
				
			state_listener=new IStateListener(){

				@Override
				public void onState(int index, int state,int size) {
					// TODO Auto-generated method stub
					String str_state=getText(R.string.waiting).toString();
					switch(state)
					{
					case 0:
						str_state=getText(R.string.testing).toString();
						break;
					case 1:
						str_state=getText(R.string.job_done).toString();
						break;
					case -1:
						str_state=getText(R.string.stopping).toString();
						break;
					case -2:
						str_state=getText(R.string.stoppted).toString();
						break;
					default:
						break;
					}
					
					autoCheckResultBeans.get(index).setTest_state(str_state);
					int now=size-index;
					int line=index+1;
					if(str_state==getText(R.string.job_done)){
						now--;
					}
					String str="";
					str =getText(R.string.state_total).toString()+size+getText(R.string.state_testing)+line+getText(R.string.state_teststate)+str_state+getText(R.string.state_left)+now+getText(R.string.state_undone);
					if(state==10){
						 str =getText(R.string.job_done).toString();
					}
					final String str1 =str;
					
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							tv_jindu.setText(str1);
						}
						
					});
				}};
				
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
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
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
