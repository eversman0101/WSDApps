package com.wisdom.dao;

import com.wisdom.bean.ShiZhongWuChaBean;

public interface IShiZhongDao {
	public void add(ShiZhongWuChaBean bean);
	public ShiZhongWuChaBean find();
	public ShiZhongWuChaBean findById(String id);
}
