package com.wisdom.app.utils;


import com.wisdom.app.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeGridAdapter extends BaseAdapter{

	Context context;
	private int[] images = {R.drawable.ic_category_0, R.drawable.ic_category_1,
			R.drawable.ic_category_2, R.drawable.ic_category_3, R.drawable.ic_category_4,
			R.drawable.ic_category_5};
	private String[] names = { "虚负荷手动校验", "虚负荷自动校验", "实负荷手动校验", "电表参数", "数据查询", "系统设置"};
	public HomeGridAdapter(Context context)
	{
		this.context=context;
	}
	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return names[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_grid_item, null);
        ImageView iv = (ImageView) v.findViewById(R.id.home_grid_image);
        TextView tv = (TextView) v.findViewById(R.id.home_grid_tv);
        iv.setImageResource(images[position]);
        tv.setText(names[position]);
        return v;
	}

}
