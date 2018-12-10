package com.wisdom.dao;

import java.util.ArrayList;

import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.QianDongBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QianDongDao implements IQiandongDao {
	private DatabaseHelper dbHelper;

	public QianDongDao(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	@Override
	public void add(QianDongBean bean) {
		// TODO Auto-generated method stub
		String sql = "insert into qiandong(no, meterNo, userName,stuffName,date,changshu,u,i,qiandongshijian,qiandongshiyan1,qiandongshiyan2,qiandongshiyan3,type)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] args = { bean.getNo(), bean.getMeterNo(), bean.getUserName(), bean.getStuffName(), bean.getDate(),
				bean.getChangshu(), bean.getU(), bean.getI(), bean.getQiandongshijian(), bean.getQiandongshiyan1(),
				bean.getQiandongshiyan2(), bean.getQiandongshiyan3(), bean.getType() };
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.execSQL(sql, args);
		Log.i("qiandongDao", "已添加");
		db.close();
	}

	/**
	 * 按id查询
	 * 
	 * @param id
	 * @return
	 */
	public QianDongBean findById(String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from qiandong where _id = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { id });

		QianDongBean note = null;
		if (cursor.moveToNext()) {
			note = new QianDongBean();
			Log.i("DataBase", "ID:" + cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
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
			note.setQiandongshijian(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshijian")));
			note.setQiandongshiyan1(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan1")));
			note.setQiandongshiyan2(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan2")));
			note.setQiandongshiyan3(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan3")));
			note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		}
		cursor.close();
		db.close();

		return note;
	}

	@Override
	public QianDongBean find() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from qiandong order by _id desc";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		QianDongBean note = null;
		if (cursor.moveToNext()) {
			note = new QianDongBean();
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
			note.setQiandongshijian(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshijian")));
			note.setQiandongshiyan1(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan1")));
			note.setQiandongshiyan2(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan2")));
			note.setQiandongshiyan3(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan3")));
			note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		}
		cursor.close();
		db.close();
		Log.i("qiandongDao", "已查询：u:" + note.getU());
		return note;
	}

	/**
	 * 根据类型查询最近100条
	 * 
	 * @return
	 */
	public ArrayList<QianDongBean> findDataByType(String type, String no, String username, String stuffName) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from qiandong where type = ? and no like '%" + no + "%' and username like '%" + username
				+ "%' and stuffName like '%" + stuffName + "%' order by _id desc limit 0,100";
		Cursor cursor = db.rawQuery(sql, new String[] { type });

		ArrayList<QianDongBean> notes = new ArrayList<QianDongBean>();
		QianDongBean note = null;
		while (cursor.moveToNext()) {
			note = new QianDongBean();
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
			note.setQiandongshijian(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshijian")));
			note.setQiandongshiyan1(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan1")));
			note.setQiandongshiyan2(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan2")));
			note.setQiandongshiyan3(cursor.getString(cursor.getColumnIndexOrThrow("qiandongshiyan3")));
			note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
			notes.add(note);
		}
		cursor.close();
		db.close();
		return notes;
	}
}
