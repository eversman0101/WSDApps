package com.wisdom.dao;

import java.util.ArrayList;

import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.Note;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class JiBenWuChaDao {
	 private DatabaseHelper dbHelper;  
	  
	    public JiBenWuChaDao(Context context) {  
	        dbHelper = new DatabaseHelper(context);  
	    }  
	  
	    /** 
	     * 添加数据 
	     * 
	     * @param note 
	     */  
	    public void add(JiBenWuChaBean bean) {  
	        String sql = "insert into jibenwucha(no,meterNo,userName,stuffName,date,u,i,jiaodu,yougong,wugong,gonglvyinshu,wuchafangshi,fuhezhuangtai,maichongchangshu,quanshu,cishu,biaozhunpiancha1,diannengwucha1,biaozhunpiancha2,diannengwucha2,biaozhunpiancha3,diannengwucha3,type,diannengwucha1_2,diannengwucha1_3,diannengwucha1_4,diannengwucha1_5,diannengwucha1_6,diannengwucha2_2,diannengwucha2_3,diannengwucha2_4,diannengwucha2_5,diannengwucha2_6,diannengwucha3_2,diannengwucha3_3,diannengwucha3_4,diannengwucha3_5,diannengwucha3_6)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";  
	        Object[] args = {bean.getNo(),bean.getMeterNo(),bean.getUserName(),bean.getStuffName(),bean.getDate(),bean.getU(),bean.getI(),bean.getJiaodu(),bean.getYougong(),bean.getWugong(),bean.getGonglvyinshu(),bean.getWuchafangshi(),bean.getFuhezhuangtai(),bean.getMaichongchangshu(),bean.getQuanshu(),bean.getCishu(),bean.getBiaozhunpiancha1(),bean.getDiannengwucha1(),bean.getBiaozhunpiancha2(),bean.getDiannengwucha2(),bean.getBiaozhunpiancha3(),bean.getDiannengwucha3(),bean.getType(),bean.getDiannengwucha1_2(),bean.getDiannengwucha1_3(),bean.getDiannengwucha1_4(),bean.getDiannengwucha1_5(),bean.getDiannengwucha1_6(),bean.getDiannengwucha2_2(),bean.getDiannengwucha2_3(),bean.getDiannengwucha2_4(),bean.getDiannengwucha2_5(),bean.getDiannengwucha2_6(),bean.getDiannengwucha3_2(),bean.getDiannengwucha3_3(),bean.getDiannengwucha3_4(),bean.getDiannengwucha3_5(),bean.getDiannengwucha3_6()};  
	        SQLiteDatabase db = dbHelper.getWritableDatabase();  
	  
	        db.execSQL(sql, args);  
	        db.close();  
	    }  
	  
	    /** 
	     * 删除数据 
	     * 
	     * @param id 
	     */  
	    public void remove(int id) {  
	        SQLiteDatabase db = dbHelper.getWritableDatabase();  
	        String sql = "delete from note where _id = ?";  
	        Object[] args = {id};  
	        db.execSQL(sql, args);  
	        db.close();  
	    }  
	  
	    /** 
	     * 修改数据 
	     * 
	     * @param note 
	     */  
	    public void update(Note note) {  
	        SQLiteDatabase db = dbHelper.getWritableDatabase();  
	        String sql = "update note set title = ?, content = ?, updateDate = ? where _id = ?";  
	        Object[] args = {note.getTitle(), note.getContent(), note.getUpdateDate(), note.getId()};  
	        db.execSQL(sql, args);  
	        db.close();  
	    }  
	  
	    /** 
	     * 按id查询 
	     * 
	     * @param id 
	     * @return 
	     */  
	    public JiBenWuChaBean findById(String id) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from jibenwucha where _id = ?";  
	        Cursor cursor = db.rawQuery(sql, new String[]{id});  
	  
	        JiBenWuChaBean note = null;  
	        if (cursor.moveToNext()) {  
	        	  note = new JiBenWuChaBean();
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
		            note.setWuchafangshi(cursor.getString(cursor.getColumnIndexOrThrow("wuchafangshi")));
		            note.setFuhezhuangtai(cursor.getString(cursor.getColumnIndexOrThrow("fuhezhuangtai")));
		            note.setMaichongchangshu(cursor.getString(cursor.getColumnIndexOrThrow("maichongchangshu")));
		            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("quanshu")));
		            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("cishu")));
		            note.setBiaozhunpiancha1(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha1")));
		            note.setDiannengwucha1(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1")));
		            note.setBiaozhunpiancha2(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha2")));
		            note.setDiannengwucha2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2")));
		            note.setBiaozhunpiancha3(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha3")));
		            note.setDiannengwucha3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3")));
		            note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		            
		            note.setDiannengwucha1_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_2")));
		            note.setDiannengwucha1_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_3")));
		            note.setDiannengwucha1_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_4")));
		            note.setDiannengwucha1_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_5")));
		            note.setDiannengwucha1_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_6")));
		            
		            note.setDiannengwucha2_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_2")));
		            note.setDiannengwucha2_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_3")));
		            note.setDiannengwucha2_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_4")));
		            note.setDiannengwucha2_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_5")));
		            note.setDiannengwucha2_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_6")));
		            
		            note.setDiannengwucha3_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_2")));
		            note.setDiannengwucha3_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_3")));
		            note.setDiannengwucha3_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_4")));
		            note.setDiannengwucha3_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_5")));
		            note.setDiannengwucha3_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_6")));
	        }  
	        cursor.close();  
	        db.close();  
	  
	        return note;  
	    }  
	    /** 
	     * 查询最新一条记录 
	     * 
	     * @param  
	     * @return 
	     */  
	    public JiBenWuChaBean find() {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from jibenwucha order by _id desc";  
	        Cursor cursor = db.rawQuery(sql, new String[]{});  
	        JiBenWuChaBean note = null;  
	        if (cursor.moveToNext()) {  
	            note = new JiBenWuChaBean();
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
	            note.setWuchafangshi(cursor.getString(cursor.getColumnIndexOrThrow("wuchafangshi")));
	            note.setFuhezhuangtai(cursor.getString(cursor.getColumnIndexOrThrow("fuhezhuangtai")));
	            note.setMaichongchangshu(cursor.getString(cursor.getColumnIndexOrThrow("maichongchangshu")));
	            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("quanshu")));
	            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("cishu")));
	            note.setBiaozhunpiancha1(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha1")));
	            note.setDiannengwucha1(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1")));
	            note.setBiaozhunpiancha2(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha2")));
	            note.setDiannengwucha2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2")));
	            note.setBiaozhunpiancha3(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha3")));
	            note.setDiannengwucha3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3")));
	            note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
	        
	            note.setDiannengwucha1_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_2")));
	            note.setDiannengwucha1_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_3")));
	            note.setDiannengwucha1_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_4")));
	            note.setDiannengwucha1_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_5")));
	            note.setDiannengwucha1_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_6")));
	            
	            note.setDiannengwucha2_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_2")));
	            note.setDiannengwucha2_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_3")));
	            note.setDiannengwucha2_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_4")));
	            note.setDiannengwucha2_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_5")));
	            note.setDiannengwucha2_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_6")));
	            
	            note.setDiannengwucha3_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_2")));
	            note.setDiannengwucha3_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_3")));
	            note.setDiannengwucha3_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_4")));
	            note.setDiannengwucha3_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_5")));
	            note.setDiannengwucha3_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_6")));
        
	        }  
	        cursor.close();  
	        db.close();  
	  
	        return note;  
	    }  
	    /** 
	     * 根据类型查询最近100条
	     * 0为虚负荷，1为实负荷
	     * @return 
	     */  
	    public ArrayList<JiBenWuChaBean> findDataByType(String type,String no,String username,String stuffName) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from jibenwucha where type = ? and no like '%"+no+"%' and username like '%"+username+"%' and stuffName like '%"+stuffName+"%' order by _id desc limit 0,100";
	        Cursor cursor = db.rawQuery(sql, new String[]{type});  
	  
	        ArrayList<JiBenWuChaBean> notes = new ArrayList<JiBenWuChaBean>();  
	        JiBenWuChaBean note = null;  
	        while (cursor.moveToNext()) {  
	        	 note = new JiBenWuChaBean();
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
		            note.setWuchafangshi(cursor.getString(cursor.getColumnIndexOrThrow("wuchafangshi")));
		            note.setFuhezhuangtai(cursor.getString(cursor.getColumnIndexOrThrow("fuhezhuangtai")));
		            note.setMaichongchangshu(cursor.getString(cursor.getColumnIndexOrThrow("maichongchangshu")));
		            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("quanshu")));
		            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("cishu")));
		            note.setBiaozhunpiancha1(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha1")));
		            note.setDiannengwucha1(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1")));
		            note.setBiaozhunpiancha2(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha2")));
		            note.setDiannengwucha2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2")));
		            note.setBiaozhunpiancha3(cursor.getString(cursor.getColumnIndexOrThrow("biaozhunpiancha3")));
		            note.setDiannengwucha3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3")));
		            note.setType(cursor.getString(cursor.getColumnIndexOrThrow("type")));
		           
		            note.setDiannengwucha1_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_2")));
		            note.setDiannengwucha1_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_3")));
		            note.setDiannengwucha1_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_4")));
		            note.setDiannengwucha1_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_5")));
		            note.setDiannengwucha1_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha1_6")));
		            
		            note.setDiannengwucha2_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_2")));
		            note.setDiannengwucha2_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_3")));
		            note.setDiannengwucha2_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_4")));
		            note.setDiannengwucha2_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_5")));
		            note.setDiannengwucha2_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha2_6")));
		            
		            note.setDiannengwucha3_2(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_2")));
		            note.setDiannengwucha3_3(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_3")));
		            note.setDiannengwucha3_4(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_4")));
		            note.setDiannengwucha3_5(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_5")));
		            note.setDiannengwucha3_6(cursor.getString(cursor.getColumnIndexOrThrow("diannengwucha3_6")));
	        
		            notes.add(note);
	        }  
	        cursor.close();  
	        db.close();  
	        return notes;  
	    }  
	  
	    /** 
	     * 分页查询 
	     * 
	     * @param limit 默认查询的数量 
	     * @param skip 跳过的行数 
	     * @return 
	     */  
	    public ArrayList<Note> findLimit(int limit, int skip) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from note order by _id desc limit ? offset ?";  
	        String[] strs = new String[]{String.valueOf(limit), String.valueOf(skip)};  
	        Cursor cursor = db.rawQuery(sql,strs);  
	  
	  
	        ArrayList<Note> notes = new ArrayList<Note>();  
	        Note note = null;  
	        while (cursor.moveToNext()) {  
	            note = new Note();  
	  
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.TITLE)));  
	            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.CONTENT)));  
	            note.setCreateDate(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.CREATE_DATE)));  
	            note.setUpdateDate(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.UPDATE_DATE)));  
	            notes.add(note);  
	        }  
	        cursor.close();  
	        db.close();  
	        return notes;  
	    }
	  
}
