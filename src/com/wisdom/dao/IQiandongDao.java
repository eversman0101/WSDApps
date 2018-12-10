package com.wisdom.dao;

import com.wisdom.bean.QianDongBean;

public interface IQiandongDao {
	public void add(QianDongBean bean);
	public QianDongBean findById(String id);
	public QianDongBean find();
}
