package com.wisdom.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.wisdom.app.activityResult.ACNLResultActivity;
import com.wisdom.app.activityResult.JiBenWuChaResultActivity;
import com.wisdom.app.activityResult.MCL_JiBenWuChaResultActivity;
import com.wisdom.app.activityResult.QiDongResultActivity;
import com.wisdom.app.activityResult.QianDongResultActivity;
import com.wisdom.app.activityResult.ShiZhongResultActivity;
import com.wisdom.app.activityResult.ZouZiResultActivity;
import com.wisdom.app.utils.MissionSingleInstance;
import com.wisdom.bean.AutoCheckResultBean;
import com.wisdom.bean.DataPreviewBean;
import com.wisdom.bean.DianbiaoZouZiBean;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.QiDongBean;
import com.wisdom.bean.QianDongBean;
import com.wisdom.bean.ShiZhongWuChaBean;
import com.wisdom.dao.ACNLDao;
import com.wisdom.dao.JiBenWuChaDao;
import com.wisdom.dao.QiDongDao;
import com.wisdom.dao.QianDongDao;
import com.wisdom.dao.ShiZhongDao;
import com.wisdom.dao.ZouZiDao;
import com.wisdom.layout.TitleLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import butterknife.Bind;
import butterknife.ButterKnife;

public class DataViewActivity extends Activity{
	@Bind(R.id.sp_type)
	Spinner sp_type;
	@Bind(R.id.sp_category)
	Spinner sp_category;
	@Bind(R.id.btn_search)
	Button btn_search;
	@Bind(R.id.lv_data)
	ListView lv_data;
	@Bind(R.id.et_no)
	EditText et_no;
	@Bind(R.id.et_username)
	EditText et_username;
	@Bind(R.id.et_stuffName)
	EditText et_stuffName;
	@Bind(R.id.title_result)TitleLayout title;
	int type=0;
	int category=0;
	ArrayList<DataPreviewBean> data;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0) {
				List<DataPreviewBean> missionBean = (List<DataPreviewBean>) msg.obj;
				DataAdapter adapter = new DataAdapter(missionBean);
				lv_data.setAdapter(adapter);
			}
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_data_view);
		ButterKnife.bind(this);
		 title.setTitleText(getText(R.string.data_inquiry).toString());
		//测试项
		sp_type.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				type=position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
		//虚负荷手动、实负荷手动、虚负荷自动
		sp_category.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				category=position;
				if(position==0)
					MissionSingleInstance.getSingleInstance().setFuhe_state(1);
				if(position==1)
					MissionSingleInstance.getSingleInstance().setFuhe_state(2);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}});
		btn_search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDataList();
			}
		});
		lv_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent;
				if(category!=2)
				{
					if(type==0&&data!=null)
					{
						if(category==0)
						{
							 intent= new Intent(DataViewActivity.this,JiBenWuChaResultActivity.class);
							 intent.putExtra("id",String.valueOf(data.get(position).getId()));
							 startActivity(intent);
						}
						else if(category==1)
						{
							 intent= new Intent(DataViewActivity.this,MCL_JiBenWuChaResultActivity.class);
							 intent.putExtra("id",String.valueOf(data.get(position).getId()));
							 startActivity(intent);
						}
					}
					if(type==1&&data!=null)
					{
						 intent= new Intent(DataViewActivity.this,QianDongResultActivity.class);
						 intent.putExtra("id",String.valueOf(data.get(position).getId()));
						 startActivity(intent);
					}
					if(type==2&&data!=null)
					{
						 intent= new Intent(DataViewActivity.this,QiDongResultActivity.class);
						 intent.putExtra("id",String.valueOf(data.get(position).getId()));
						 startActivity(intent);
					}
					if(type==3&&data!=null)
					{
						 intent= new Intent(DataViewActivity.this,ZouZiResultActivity.class);
						 intent.putExtra("id",String.valueOf(data.get(position).getId()));
						 startActivity(intent);
					}
					if(type==4&&data!=null)
					{
						 intent= new Intent(DataViewActivity.this,ShiZhongResultActivity.class);
						 intent.putExtra("id",String.valueOf(data.get(position).getId()));
						 startActivity(intent);
					}
					if(type==5&&data!=null)
					{
						MissionSingleInstance.getSingleInstance().setFuhe_state(3);
						
						 intent= new Intent(DataViewActivity.this,ZouZiResultActivity.class);
						 intent.putExtra("id",String.valueOf(data.get(position).getId()));
						 startActivity(intent);
					}
				}else if(category==2)
				{
					 intent= new Intent(DataViewActivity.this,ACNLResultActivity.class);
					 intent.putExtra("id",String.valueOf(data.get(position).getId()));
					 startActivity(intent);
				}
			}});
	}
	private void getDataList()
	{
		data = new ArrayList<DataPreviewBean>();
		DataPreviewBean mb;
		//虚负荷手动、实负荷手动
		Log.e("DataViewActivity","category:"+category+" type:"+type);
		if(category!=2)
		{
			if(type==0)
			{
				JiBenWuChaDao dao=new JiBenWuChaDao(this);
				ArrayList<JiBenWuChaBean> bean=dao.findDataByType(String.valueOf(category),et_no.getText().toString(),et_username.getText().toString(),et_stuffName.getText().toString());
				for(JiBenWuChaBean item : bean)
				{
					mb= new DataPreviewBean();
					mb.setId(item.getId());
					mb.setDate(item.getDate());
					data.add(mb);
				}
				Message msg = new Message();
				msg.what = 0;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			else if(type==1)
			{
				QianDongDao dao=new QianDongDao(this);
				ArrayList<QianDongBean> bean=dao.findDataByType(String.valueOf(category),et_no.getText().toString(),et_username.getText().toString(),et_stuffName.getText().toString());
				for(QianDongBean item : bean)
				{
					mb= new DataPreviewBean();
					mb.setId(item.getId());
					mb.setDate(item.getDate());
					data.add(mb);
				}
				Message msg = new Message();
				msg.what = 0;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			else if(type==2)
			{
				QiDongDao dao=new QiDongDao(this);
				ArrayList<QiDongBean> bean=dao.findDataByType(String.valueOf(category),et_no.getText().toString(),et_username.getText().toString(),et_stuffName.getText().toString());
				for(QiDongBean item : bean)
				{
					mb= new DataPreviewBean();
					mb.setId(item.getId());
					mb.setDate(item.getDate());
					data.add(mb);
				}
				Message msg = new Message();
				msg.what = 0;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			else if(type==3)
			{
				ZouZiDao dao=new ZouZiDao(this);
				ArrayList<DianbiaoZouZiBean> bean=dao.findDataByType(String.valueOf(category),et_no.getText().toString(),et_username.getText().toString(),et_stuffName.getText().toString());
				Log.e("DataViewActivity","走字，list size:"+bean.size());
				for(DianbiaoZouZiBean item : bean)
				{
					mb= new DataPreviewBean();
					mb.setId(item.getId());
					mb.setDate(item.getDate());
					data.add(mb);
				}
				Message msg = new Message();
				msg.what = 0;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			else if(type==4)
			{
				ShiZhongDao dao=new ShiZhongDao(this);
				ArrayList<ShiZhongWuChaBean> bean=dao.findDataByType(String.valueOf(category),et_no.getText().toString(),et_username.getText().toString(),et_stuffName.getText().toString());
				for(ShiZhongWuChaBean item : bean)
				{
					mb= new DataPreviewBean();
					mb.setId(item.getId());
					mb.setDate(item.getDate());
					data.add(mb);
				}
				Message msg = new Message();
				msg.what = 0;
				msg.obj = data;
				handler.sendMessage(msg);
			}
			else if(type==5&&(category==0))
			{
				Log.e("DataView","常数校核！");
				ZouZiDao dao=new ZouZiDao(this);
				ArrayList<DianbiaoZouZiBean> bean=dao.findDataByType("3",et_no.getText().toString(),et_username.getText().toString(),et_stuffName.getText().toString());
				for(DianbiaoZouZiBean item : bean)
				{
					mb= new DataPreviewBean();
					mb.setId(item.getId());
					mb.setDate(item.getDate());
					data.add(mb);
				}
				Message msg = new Message();
				msg.what = 0;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		}
		//虚负荷自动
		else if(category==2)
		{
			String test_type=getText(R.string.intrinsic_error).toString();
			if(type==0)
				test_type=getText(R.string.intrinsic_error).toString();
			if(type==1)
				test_type=getText(R.string.false_actuation_test).toString();
			if(type==2)
				test_type=getText(R.string.qidong_test).toString();
			if(type==3)
				test_type=getText(R.string.walk_test).toString();
			if(type==4)
				test_type=getText(R.string.clock_error_test).toString();
			ACNLDao dao=new ACNLDao(this);
			ArrayList<AutoCheckResultBean> bean=dao.findDataByTestType(test_type);
			for(AutoCheckResultBean item : bean)
			{
				mb= new DataPreviewBean();
				mb.setId(item.getId());
				mb.setDate(item.getDate());
				data.add(mb);
			}
			Message msg = new Message();
			msg.what = 0;
			msg.obj = data;
			handler.sendMessage(msg);
		}
	}
	public class DataAdapter extends BaseAdapter {
		private List<DataPreviewBean> missionBean;

		public DataAdapter(List<DataPreviewBean> missionBean) {
			super();
			this.missionBean = missionBean;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.missionBean.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return missionBean.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = getLayoutInflater().inflate(R.layout.mission_list_item,
					null);
			TextView mission_mName = (TextView) view
					.findViewById(R.id.mission_mName);
			TextView mission_number = (TextView) view
					.findViewById(R.id.mission_number);
			mission_mName.setText(String.valueOf(missionBean.get(position).getId()));
			mission_number.setText(missionBean.get(position).getDate());
			return view;
		}

	}
}
