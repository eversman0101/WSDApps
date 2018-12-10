package com.wisdom.dao;

import java.util.List;

import com.wisdom.app.activity.R;
import com.wisdom.bean.AutoCheckSchemeResultBean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ACNLSchemeResultAdapter extends BaseAdapter{
	private List<AutoCheckSchemeResultBean> list;
	private LayoutInflater mInflater;
	
	public ACNLSchemeResultAdapter(List<AutoCheckSchemeResultBean> list,Context context)
	{
		this.mInflater=LayoutInflater.from(context);
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public void add(AutoCheckSchemeResultBean bean)
	{
		this.list.add(bean);
		notifyDataSetChanged();
	}
	public void delete(int position)
	{
		position=position-1;
		if(!((position<0)||(position>list.size())||(list.size()==0)))
		{
		this.list.remove(position);
		notifyDataSetChanged();
		}
	}
	public List<AutoCheckSchemeResultBean> getList()
	{
		return this.list;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = mInflater.inflate(R.layout.listview_acnl_scheme_item,null);
		
		TextView tv_ac_scheme_type1=(TextView)view.findViewById(R.id.tv_ac_scheme_type1);
		TextView tv_ac_scheme_type3=(TextView)view.findViewById(R.id.tv_ac_scheme_type3);
		
		//tv_no.setText(list.get(position).getNo());
		   int i= position+1;
		   tv_ac_scheme_type1.setText(i+"");
		//	tv_ac_scheme_type2.setText(list.get(position).getId()+"");
		tv_ac_scheme_type3.setText(list.get(position).getSchemeName());
		return view;
	}

}
