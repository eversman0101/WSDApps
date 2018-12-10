package com.wisdom.app.utils;

import java.util.List;

import com.wisdom.app.activity.R;
import com.wisdom.bean.AutoCheckResultBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class GridViewAdapter extends BaseAdapter{

	private List<AutoCheckResultBean> listItems; 
	Context context;//����������
	public GridViewAdapter(Context context,List<AutoCheckResultBean> listItems) {  
		this.context=context;
		this.listItems =listItems;
		}  
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//ͨ�����������LayoutInflater�������Ԫ���ڵĲ���
		 View v = LayoutInflater.from(context).inflate(R.layout.gridview_checktextview_item, null);
		//ʹ��findViewById�ֱ��ҵ���Ԫ���ڲ��ֵ�ͼƬ�Լ�����
		 CheckedTextView checktv = (CheckedTextView) v.findViewById( R.id.item_grid_tvShow);
	    //����������Ԫ�����ò�����ͼƬ�Լ����ֵ�����
		 if(listItems.get(position).getCheck())
		  checktv.setChecked(true);
		//����ֵһ��Ϊ��Ԫ�����岼��v
		  return v;

	}

}
