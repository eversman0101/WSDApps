package com.wisdom.dao;

import java.util.ArrayList;

import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.QianDongBean;
import com.wisdom.bean.ShiZhongWuChaBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ShiZhongDao implements IShiZhongDao{
	private DatabaseHelper dbHelper;  
	  
    public ShiZhongDao(Context context) {  
        dbHelper = new DatabaseHelper(context);  
    }  
	@Override
	public void add(ShiZhongWuChaBean bean) {
	    String sql = "insert into shizhong(no,meterNo,userName,stuffName,date,quanshu,cishu,shizhongwucha1,pingjunwucha1,shizhongwucha2,shizhongwucha3,pingjunwucha2,pingjunwucha3,type)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";  
        Object[] args = {bean.getNo(),bean.getMeterNo(),bean.getUserName(),bean.getStuffName(),bean.getDate(),bean.getQuanshu(),bean.getCishu(),bean.getShizhongwucha1(),bean.getPingjunwucha1(),bean.getShizhongwucha2(),bean.getShizhongwucha3(),bean.getPingjunwucha2(),bean.getPingjunwucha3(),bean.getType()};  
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
    public ShiZhongWuChaBean findById(String id) {  
        SQLiteDatabase db = dbHelper.getReadableDatabase();  
        String sql = "select * from shizhong where _id = ?";  
        Cursor cursor = db.rawQuery(sql, new String[]{id});  
  
        ShiZhongWuChaBean note = null;  
        if (cursor.moveToNext()) {  
        	  note = new ShiZhongWuChaBean();
        	  Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));  
	            note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));  
	            note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));  
	            note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
	            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
	            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("quanshu")));
	            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("cishu")));
	            note.setShizhongwucha1(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1")));
	            note.setPingjunwucha1(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha1")));
	            note.setShizhongwucha2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2")));
	            note.setShizhongwucha3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3")));
	            note.setPingjunwucha2(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha2")));
	            note.setPingjunwucha3(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha3")));
	            note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
        
	            note.setShizhongwucha1_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_2")));
	            note.setShizhongwucha1_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_3")));
	            note.setShizhongwucha1_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_4")));
	            note.setShizhongwucha1_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_5")));
	            note.setShizhongwucha1_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_6")));
	            
	            note.setShizhongwucha2_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_2")));
	            note.setShizhongwucha2_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_3")));
	            note.setShizhongwucha2_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_4")));
	            note.setShizhongwucha2_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_5")));
	            note.setShizhongwucha2_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_6")));
	            
	            note.setShizhongwucha3_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_2")));
	            note.setShizhongwucha3_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_3")));
	            note.setShizhongwucha3_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_4")));
	            note.setShizhongwucha3_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_5")));
	            note.setShizhongwucha3_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_6")));
        
        }  
        cursor.close();  
        db.close();  
  
        return note;  
    }  
	@Override
	public ShiZhongWuChaBean find() {
	     SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from shizhong order by _id desc";  
	        Cursor cursor = db.rawQuery(sql, new String[]{});  
	        ShiZhongWuChaBean note = null;  
	        if (cursor.moveToNext()) {  
	            note = new ShiZhongWuChaBean();
	            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));  
	            note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));  
	            note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));  
	            note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
	            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
	            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("quanshu")));
	            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("cishu")));
	            note.setShizhongwucha1(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1")));
	            note.setPingjunwucha1(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha1")));
	            note.setShizhongwucha2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2")));
	            note.setShizhongwucha3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3")));
	            note.setPingjunwucha2(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha2")));
	            note.setPingjunwucha3(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha3")));
	            note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
	        
	            note.setShizhongwucha1_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_2")));
	            note.setShizhongwucha1_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_3")));
	            note.setShizhongwucha1_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_4")));
	            note.setShizhongwucha1_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_5")));
	            note.setShizhongwucha1_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_6")));
	            
	            note.setShizhongwucha2_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_2")));
	            note.setShizhongwucha2_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_3")));
	            note.setShizhongwucha2_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_4")));
	            note.setShizhongwucha2_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_5")));
	            note.setShizhongwucha2_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_6")));
	            
	            note.setShizhongwucha3_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_2")));
	            note.setShizhongwucha3_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_3")));
	            note.setShizhongwucha3_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_4")));
	            note.setShizhongwucha3_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_5")));
	            note.setShizhongwucha3_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_6")));
        
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
    public ArrayList<ShiZhongWuChaBean> findDataByType(String type,String no,String username,String stuffName) {  
        SQLiteDatabase db = dbHelper.getReadableDatabase();  
        String sql = "select * from shizhong where  type = ? and no like '%"+no+"%' and username like '%"+username+"%' and stuffName like '%"+stuffName+"%' order by _id desc limit 0,100";
        Cursor cursor = db.rawQuery(sql, new String[]{type});  
  
        ArrayList<ShiZhongWuChaBean> notes = new ArrayList<ShiZhongWuChaBean>();  
        ShiZhongWuChaBean note = null;  
        while (cursor.moveToNext()) {
        	 note = new ShiZhongWuChaBean();
	            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setNo(cursor.getString(cursor.getColumnIndexOrThrow("no")));  
	            note.setMeterNo(cursor.getString(cursor.getColumnIndexOrThrow("meterNo")));  
	            note.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("userName")));  
	            note.setStuffName(cursor.getString(cursor.getColumnIndexOrThrow("stuffName")));
	            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
	            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("quanshu")));
	            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("cishu")));
	            note.setShizhongwucha1(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1")));
	            note.setPingjunwucha1(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha1")));
	            note.setShizhongwucha2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2")));
	            note.setShizhongwucha3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3")));
	            note.setPingjunwucha2(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha2")));
	            note.setPingjunwucha3(cursor.getString(cursor.getColumnIndexOrThrow("pingjunwucha3")));
	            note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
	            
	            note.setShizhongwucha1_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_2")));
	            note.setShizhongwucha1_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_3")));
	            note.setShizhongwucha1_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_4")));
	            note.setShizhongwucha1_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_5")));
	            note.setShizhongwucha1_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha1_6")));
	            
	            note.setShizhongwucha2_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_2")));
	            note.setShizhongwucha2_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_3")));
	            note.setShizhongwucha2_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_4")));
	            note.setShizhongwucha2_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_5")));
	            note.setShizhongwucha2_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha2_6")));
	            
	            note.setShizhongwucha3_2(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_2")));
	            note.setShizhongwucha3_3(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_3")));
	            note.setShizhongwucha3_4(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_4")));
	            note.setShizhongwucha3_5(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_5")));
	            note.setShizhongwucha3_6(cursor.getString(cursor.getColumnIndexOrThrow("shizhongwucha3_6")));
        
	            notes.add(note);
        }  
        cursor.close();  
        db.close();  
        return notes;  
    }  
}
