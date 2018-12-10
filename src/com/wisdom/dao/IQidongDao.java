package com.wisdom.dao;

import com.wisdom.bean.QiDongBean;

public interface IQidongDao {
	public void add(QiDongBean bean);
	public QiDongBean find();
	public QiDongBean findById(String id);
}
