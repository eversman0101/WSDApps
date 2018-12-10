package com.wisdom.dao;

import java.util.List;

import com.wisdom.app.activity.R;
import com.wisdom.bean.AutoCheckResultBean;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAutoCheckResultAdapter extends BaseAdapter {
	private List<AutoCheckResultBean> list;
	private LayoutInflater mInflater;

	public ListAutoCheckResultAdapter(List<AutoCheckResultBean> list, Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
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

	public void add(AutoCheckResultBean bean) {
		this.list.add(bean);
		notifyDataSetChanged();
	}

	public void delete(int position) {
		position = position - 1;
		Log.e("option", "position:" + position);
		if (!((position < 0) || (position > list.size()) || (list.size() == 0))) {
			this.list.remove(position);
			notifyDataSetChanged();
		}
	}

	public List<AutoCheckResultBean> getList() {
		return this.list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = mInflater.inflate(R.layout.listview_autocheck_result_item, null);

		// TextView tv_no=(TextView)view.findViewById(R.id.tv_ac_result_no);
		TextView tv_test_type = (TextView) view.findViewById(R.id.tv_ac_result_type);
		TextView tv_power_type = (TextView) view.findViewById(R.id.tv_ac_result_power_type);
		TextView tv_Ub = (TextView) view.findViewById(R.id.tv_ac_result_Ub);
		TextView tv_Ib = (TextView) view.findViewById(R.id.tv_ac_result_Ib);
		TextView tv_Ur = (TextView) view.findViewById(R.id.tv_ac_result_Ur);
		TextView tv_Ir = (TextView) view.findViewById(R.id.tv_ac_result_Ir);

		TextView tv_pf = (TextView) view.findViewById(R.id.tv_ac_result_power_factor);
		TextView tv_pinlv = (TextView) view.findViewById(R.id.tv_ac_result_rate);
		TextView tv_quanshu = (TextView) view.findViewById(R.id.tv_ac_result_circle);
		TextView tv_cishu = (TextView) view.findViewById(R.id.tv_ac_result_count);
		TextView tv_wucha_limit = (TextView) view.findViewById(R.id.tv_ac_result_error_range);
		TextView tv_test_state = (TextView) view.findViewById(R.id.tv_ac_result_test_state);

		// tv_no.setText(list.get(position).getNo());
		tv_test_type.setText(list.get(position).getTest_type());
		tv_power_type.setText(list.get(position).getPower_type());
		tv_Ub.setText(list.get(position).getUb());
		tv_Ib.setText(list.get(position).getIb());
		tv_Ur.setText(list.get(position).getUr());
		tv_Ir.setText(list.get(position).getIr());

		tv_pf.setText(list.get(position).getPower_factor());
		tv_pinlv.setText(list.get(position).getPinlv());
		tv_quanshu.setText(list.get(position).getQuanshu());
		tv_cishu.setText(list.get(position).getCishu());
		tv_wucha_limit.setText(list.get(position).getWucha_limit());
		tv_test_state.setText(list.get(position).getTest_state());
		return view;
	}

}
