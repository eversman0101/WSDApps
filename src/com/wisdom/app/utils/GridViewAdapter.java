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
	Context context;//声明上下文
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
		//通过布局填充器LayoutInflater填充网格单元格内的布局
		 View v = LayoutInflater.from(context).inflate(R.layout.gridview_checktextview_item, null);
		//使用findViewById分别找到单元格内布局的图片以及文字
		 CheckedTextView checktv = (CheckedTextView) v.findViewById( R.id.item_grid_tvShow);
	    //引用数组内元素设置布局内图片以及文字的内容
		 if(listItems.get(position).getCheck())
		  checktv.setChecked(true);
		//返回值一定为单元格整体布局v
		  return v;

	}

}
