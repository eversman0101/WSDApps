package com.wisdom.dao;

import java.util.ArrayList;

import com.wisdom.bean.DianbiaoZouZiBean;
import com.wisdom.bean.JiBenWuChaBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ZouZiDao implements IZouZiDao{
	private DatabaseHelper dbHelper;  
	  
    public ZouZiDao(Context context) {  
        dbHelper = new DatabaseHelper(context);  
    }  
	@Override
	public void add(DianbiaoZouZiBean bean) {
		  String sql = "insert into zouzi(no,meterNo,userName,stuffName,date,zouzifangshi,yuzhidianneng,beilv,biaozhundianneng,qishi1,jieshu1,shiji1,wucha1,qishi2,jieshu2,shiji2,wucha2,qishi3,jieshu3,shiji3,wucha3,yougong,wugong,gonglvyinshu,u,i,jiaodu,type)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";  
	        Log.e("DataView","type:"+bean.getType());
		  Object[] args = {bean.getNo(),bean.getMeterNo(),bean.getUserName(),bean.getStuffName(),bean.getDate(),bean.getZouzifangshi(),bean.getYuzhidianneng(),bean.getBeilv(),bean.getBiaozhundianneng(),bean.getQishi1(),bean.getJieshu1(),bean.getShiji1(),bean.getWucha1(),bean.getQishi2(),bean.getJieshu2(),bean.getShiji2(),bean.getWucha2(),bean.getQishi3(),bean.getJieshu3(),bean.getShiji3(),bean.getWucha3(),bean.getYougong(),bean.getWugong(),bean.getGonglvyinshu(),bean.getU(),bean.getI(),bean.getJiaodu(),bean.getType()};  
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
    public DianbiaoZouZiBean findById(String id) {  
        SQLiteDatabase db = dbHelper.getReadableDatabase();  
        String sql = "select * from zouzi where _id = ?";  
        Cursor cursor = db.rawQuery(sql, new String[]{id});  
  
        DianbiaoZouZiBean note = null;  
        if (cursor.moveToNext()) {  
        	  note = new DianbiaoZouZiBean();
        	  Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));  
	            note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));  
	            note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));  
	            note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
	            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
	            
	            note.setU(cursor.getString(cursor.getColumnIndexOrThrow("u")));
	            note.setI(cursor.getString(cursor.getColumnIndexOrThrow("i")));
	            note.setJiaodu(cursor.getString(cursor.getColumnIndexOrThrow("jiaodu")));
	            note.setYougong(cursor.getString(cursor.getColumnIndexOrThrow("yougong")));
	            note.setWugong(cursor.getString(cursor.getColumnIndexOrThrow("wugong")));
	            note.setGonglvyinshu(cursor.getString(cursor.getColumnIndexOrThrow("gonglvyinshu")));
	            
	            note.setZouzifangshi(cursor.getString(cursor.getColumnIndexOrThrow("zouzifangshi")));
	            note.setYuzhidianneng(cursor.getString(cursor.getColumnIndexOrThrow("yuzhidianneng")));
	            note.setBeilv(cursor.getString(cursor.getColumnIndexOrThrow("beilv")));
	            note.setBiaozhundianneng(cursor.getString(cursor.getColumnIndexOrThrow("biaozhundianneng")));
	            
	            note.setQishi1(cursor.getString(cursor.getColumnIndexOrThrow("qishi1")));
	            note.setJieshu1(cursor.getString(cursor.getColumnIndexOrThrow("jieshu1")));
	            note.setShiji1(cursor.getString(cursor.getColumnIndexOrThrow("shiji1")));
	            note.setWucha1(cursor.getString(cursor.getColumnIndexOrThrow("wucha1")));
	        
	            note.setQishi2(cursor.getString(cursor.getColumnIndexOrThrow("qishi2")));
	            note.setJieshu2(cursor.getString(cursor.getColumnIndexOrThrow("jieshu2")));
	            note.setShiji2(cursor.getString(cursor.getColumnIndexOrThrow("shiji2")));
	            note.setWucha2(cursor.getString(cursor.getColumnIndexOrThrow("wucha2")));
	            
	            note.setQishi3(cursor.getString(cursor.getColumnIndexOrThrow("qishi3")));
	            note.setJieshu3(cursor.getString(cursor.getColumnIndexOrThrow("jieshu3")));
	            note.setShiji3(cursor.getString(cursor.getColumnIndexOrThrow("shiji3")));
	            note.setWucha3(cursor.getString(cursor.getColumnIndexOrThrow("wucha3")));
	            note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
        }  
        cursor.close();  
        db.close();  
  
        return note;  
    }  
	@Override
	public DianbiaoZouZiBean find() {
		   SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from zouzi order by _id desc";  
	        Cursor cursor = db.rawQuery(sql, new String[]{});  
	        DianbiaoZouZiBean note = null;  
	        if (cursor.moveToNext()) {  
	            note = new DianbiaoZouZiBean();
	            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));  
	            note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));  
	            note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));  
	            note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
	            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
	            
	            note.setU(cursor.getString(cursor.getColumnIndexOrThrow("u")));
	            note.setI(cursor.getString(cursor.getColumnIndexOrThrow("i")));
	            note.setJiaodu(cursor.getString(cursor.getColumnIndexOrThrow("jiaodu")));
	            note.setYougong(cursor.getString(cursor.getColumnIndexOrThrow("yougong")));
	            note.setWugong(cursor.getString(cursor.getColumnIndexOrThrow("wugong")));
	            note.setGonglvyinshu(cursor.getString(cursor.getColumnIndexOrThrow("gonglvyinshu")));
	            
	            note.setZouzifangshi(cursor.getString(cursor.getColumnIndexOrThrow("zouzifangshi")));
	            note.setYuzhidianneng(cursor.getString(cursor.getColumnIndexOrThrow("yuzhidianneng")));
	            note.setBeilv(cursor.getString(cursor.getColumnIndexOrThrow("beilv")));
	            note.setBiaozhundianneng(cursor.getString(cursor.getColumnIndexOrThrow("biaozhundianneng")));
	            
	            note.setQishi1(cursor.getString(cursor.getColumnIndexOrThrow("qishi1")));
	            note.setJieshu1(cursor.getString(cursor.getColumnIndexOrThrow("jieshu1")));
	            note.setShiji1(cursor.getString(cursor.getColumnIndexOrThrow("shiji1")));
	            note.setWucha1(cursor.getString(cursor.getColumnIndexOrThrow("wucha1")));
	        
	            note.setQishi2(cursor.getString(cursor.getColumnIndexOrThrow("qishi2")));
	            note.setJieshu2(cursor.getString(cursor.getColumnIndexOrThrow("jieshu2")));
	            note.setShiji2(cursor.getString(cursor.getColumnIndexOrThrow("shiji2")));
	            note.setWucha2(cursor.getString(cursor.getColumnIndexOrThrow("wucha2")));
	            
	            note.setQishi3(cursor.getString(cursor.getColumnIndexOrThrow("qishi3")));
	            note.setJieshu3(cursor.getString(cursor.getColumnIndexOrThrow("jieshu3")));
	            note.setShiji3(cursor.getString(cursor.getColumnIndexOrThrow("shiji3")));
	            note.setWucha3(cursor.getString(cursor.getColumnIndexOrThrow("wucha3")));
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
    public ArrayList<DianbiaoZouZiBean> findDataByType(String type,String no,String username,String stuffName) {  
        SQLiteDatabase db = dbHelper.getReadableDatabase();  
        String sql = "select * from zouzi where type = ? and no like '%"+no+"%' and username like '%"+username+"%' and stuffName like '%"+stuffName+"%' order by _id desc limit 0,100";
        Cursor cursor = db.rawQuery(sql, new String[]{type});  
  
        ArrayList<DianbiaoZouZiBean> notes = new ArrayList<DianbiaoZouZiBean>();  
        DianbiaoZouZiBean note = null;  
        while (cursor.moveToNext()) {
        	 note = new DianbiaoZouZiBean();
	            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));  
	            note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));  
	            note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));  
	            note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
	            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
	            
	            note.setU(cursor.getString(cursor.getColumnIndexOrThrow("u")));
	            note.setI(cursor.getString(cursor.getColumnIndexOrThrow("i")));
	            note.setJiaodu(cursor.getString(cursor.getColumnIndexOrThrow("jiaodu")));
	            note.setYougong(cursor.getString(cursor.getColumnIndexOrThrow("yougong")));
	            note.setWugong(cursor.getString(cursor.getColumnIndexOrThrow("wugong")));
	            note.setGonglvyinshu(cursor.getString(cursor.getColumnIndexOrThrow("gonglvyinshu")));
	            
	            note.setZouzifangshi(cursor.getString(cursor.getColumnIndexOrThrow("zouzifangshi")));
	            note.setYuzhidianneng(cursor.getString(cursor.getColumnIndexOrThrow("yuzhidianneng")));
	            note.setBeilv(cursor.getString(cursor.getColumnIndexOrThrow("beilv")));
	            note.setBiaozhundianneng(cursor.getString(cursor.getColumnIndexOrThrow("biaozhundianneng")));
	            
	            note.setQishi1(cursor.getString(cursor.getColumnIndexOrThrow("qishi1")));
	            note.setJieshu1(cursor.getString(cursor.getColumnIndexOrThrow("jieshu1")));
	            note.setShiji1(cursor.getString(cursor.getColumnIndexOrThrow("shiji1")));
	            note.setWucha1(cursor.getString(cursor.getColumnIndexOrThrow("wucha1")));
	        
	            note.setQishi2(cursor.getString(cursor.getColumnIndexOrThrow("qishi2")));
	            note.setJieshu2(cursor.getString(cursor.getColumnIndexOrThrow("jieshu2")));
	            note.setShiji2(cursor.getString(cursor.getColumnIndexOrThrow("shiji2")));
	            note.setWucha2(cursor.getString(cursor.getColumnIndexOrThrow("wucha2")));
	            
	            note.setQishi3(cursor.getString(cursor.getColumnIndexOrThrow("qishi3")));
	            note.setJieshu3(cursor.getString(cursor.getColumnIndexOrThrow("jieshu3")));
	            note.setShiji3(cursor.getString(cursor.getColumnIndexOrThrow("shiji3")));
	            note.setWucha3(cursor.getString(cursor.getColumnIndexOrThrow("wucha3")));
	            note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
	            notes.add(note);
        }  
        cursor.close();  
        db.close();  
        return notes;  
    }  
}
