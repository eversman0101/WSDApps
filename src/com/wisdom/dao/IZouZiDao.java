package com.wisdom.dao;

import com.wisdom.bean.DianbiaoZouZiBean;

public interface IZouZiDao {
	public void add(DianbiaoZouZiBean bean);

	public DianbiaoZouZiBean find();

	public DianbiaoZouZiBean findById(String id);
}
