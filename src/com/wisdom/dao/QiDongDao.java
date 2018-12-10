package com.wisdom.dao;

import java.util.ArrayList;

import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.QiDongBean;
import com.wisdom.bean.QianDongBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QiDongDao implements IQidongDao {
	private DatabaseHelper dbHelper;

	public QiDongDao(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	@Override
	public void add(QiDongBean bean) {
		// TODO Auto-generated method stub
		String sql = "insert into qidong(no, meterNo, userName,stuffName,date,changshu,u,i,qidongshijian,qidongshiyan1,qidongshiyan2,qidongshiyan3,type)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = { bean.getNo(), bean.getMeterNo(), bean.getUserName(), bean.getStuffName(), bean.getDate(),
				bean.getChangshu(), bean.getU(), bean.getI(), bean.getQidongshijian(), bean.getQidongshiyan1(),
				bean.getQidongshiyan2(), bean.getQidongshiyan3(), bean.getType() };
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.execSQL(sql, args);
		db.close();
	}

	/**
	 * 按id查询
	 * 
	 * @param id
	 * @return
	 */
	public QiDongBean findById(String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from qidong where _id = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { id });

		QiDongBean note = null;
		if (cursor.moveToNext()) {
			note = new QiDongBean();
			Log.i("DataBase", "ID:" + cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
			note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
			note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));
			note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));
			note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));
			note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
			note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
			note.setChangshu(cursor.getString(cursor.getColumnIndexOrThrow("changshu")));
			note.setU(cursor.getString(cursor.getColumnIndexOrThrow("u")));
			note.setI(cursor.getString(cursor.getColumnIndexOrThrow("i")));
			note.setQidongshijian(cursor.getString(cursor.getColumnIndexOrThrow("qidongshijian")));
			note.setQidongshiyan1(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan1")));
			note.setQidongshiyan2(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan2")));
			note.setQidongshiyan3(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan3")));
			note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		}
		cursor.close();
		db.close();

		return note;
	}

	@Override
	public QiDongBean find() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from qidong order by _id desc";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		QiDongBean note = null;
		if (cursor.moveToNext()) {
			note = new QiDongBean();
			Log.i("DataBase", "ID:" + cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
			note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
			note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));
			note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));
			note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));
			note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
			note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
			note.setChangshu(cursor.getString(cursor.getColumnIndexOrThrow("changshu")));
			note.setU(cursor.getString(cursor.getColumnIndexOrThrow("u")));
			note.setI(cursor.getString(cursor.getColumnIndexOrThrow("i")));
			note.setQidongshijian(cursor.getString(cursor.getColumnIndexOrThrow("qidongshijian")));
			note.setQidongshiyan1(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan1")));
			note.setQidongshiyan2(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan2")));
			note.setQidongshiyan3(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan3")));
			note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		}
		cursor.close();
		db.close();

		return note;
	}

	/**
	 * 根据类型查询最近100条
	 * 
	 * @return
	 */
	public ArrayList<QiDongBean> findDataByType(String type, String no, String username, String stuffName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from qidong where  type = ? and no like '%" + no + "%' and username like '%" + username
				+ "%' and stuffName like '%" + stuffName + "%' order by _id desc limit 0,100";
		Cursor cursor = db.rawQuery(sql, new String[] { type });

		ArrayList<QiDongBean> notes = new ArrayList<QiDongBean>();
		QiDongBean note = null;
		while (cursor.moveToNext()) {
			note = new QiDongBean();
			Log.i("DataBase", "ID:" + cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
			note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
			note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));
			note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));
			note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));
			note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
			note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
			note.setChangshu(cursor.getString(cursor.getColumnIndexOrThrow("changshu")));
			note.setU(cursor.getString(cursor.getColumnIndexOrThrow("u")));
			note.setI(cursor.getString(cursor.getColumnIndexOrThrow("i")));
			note.setQidongshijian(cursor.getString(cursor.getColumnIndexOrThrow("qidongshijian")));
			note.setQidongshiyan1(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan1")));
			note.setQidongshiyan2(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan2")));
			note.setQidongshiyan3(cursor.getString(cursor.getColumnIndexOrThrow("qidongshiyan3")));
			note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
			notes.add(note);
		}
		cursor.close();
		db.close();
		return notes;
	}
}
