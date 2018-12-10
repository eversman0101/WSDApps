package com.wisdom.dao;

import java.util.ArrayList;
import java.util.List;

import com.wisdom.bean.ACNL_JiBenWuChaBean;
import com.wisdom.bean.AutoCheckResultBean;
import com.wisdom.bean.AutoCheckSchemeResultBean;
import com.wisdom.bean.JiBenWuChaBean;
import com.wisdom.bean.Note;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ACNLDao {
	 private DatabaseHelper dbHelper;  
	  
	    public ACNLDao(Context context) {  
	        dbHelper = new DatabaseHelper(context);  
	    }  
	  
	    /** 
	     * 添加数据 
	     * 
	     * @param note 
	     */  
	    public void add(AutoCheckResultBean bean) {  
	        String sql = "insert into acnl(testType,powerType,ub,ib,ur,ir,pf,rate,count,circle,errorLimit,result1,result2,result3)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";  
	        Object[] args = {bean.getTest_type(),bean.getPower_type(),bean.getUb(),bean.getIb(),bean.getUr(),bean.getIr(),bean.getPower_factor(),bean.getPinlv(),bean.getCishu(),bean.getQuanshu(),bean.getResult1(),bean.getResult2(),bean.getResult3()};  
	        SQLiteDatabase db = dbHelper.getWritableDatabase();  
	  
	        db.execSQL(sql, args);  
	        db.close();  
	    }  
	    /** 
	     * 添加数据 方案
	     * 
	     * @param note 
	     */  
	    public void add(AutoCheckSchemeResultBean bean) {  
	        String sql = "insert into acnlScheme(schemeName)values(?)";  
	        Object[] args = {bean.getSchemeName()};  
	        SQLiteDatabase db = dbHelper.getWritableDatabase();  
	  
	        db.execSQL(sql, args);  
	        db.close();  
	    }  
	    /** 
	     * 添加数据 
	     * 
	     * @param note 
	     */  
	    public void add(List<AutoCheckResultBean> list) {  
	        String sql = "insert into acnl(testType,powerType,ub,ib,ur,ir,pf,rate,count,circle,errorLimit,result1,result2,result3,date,schemeId)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";  
	        SQLiteDatabase db = dbHelper.getWritableDatabase();  
	        if(list==null)
	        {
	        	Log.e("acnl","acnlDao list is null");
	        	return;
	        }
	        for(int i=0;i<list.size();i++)
	        {

	        	Log.i("acnl","test_type:"+list.get(i).getTest_type());
	        	Log.i("acnl","power_type:"+list.get(i).getPower_type());
	        	Log.i("acnl","ub:"+list.get(i).getUb());
	        	Log.i("acnl","ib:"+list.get(i).getIb());
	        	Log.i("acnl","ur:"+list.get(i).getUr());
	        	Log.i("acnl","ir:"+list.get(i).getIr());
	        	Log.i("acnl","power_factor:"+list.get(i).getPower_factor());
	        	Log.i("acnl","pinlv:"+list.get(i).getPinlv());
	        	Log.i("acnl","cishu:"+list.get(i).getCishu());
	        	Log.i("acnl","quanshu:"+list.get(i).getQuanshu());
	        	Log.i("acnl","wucha limit:"+list.get(i).getWucha_limit());
	        	Log.i("acnl","result1:"+list.get(i).getResult1());
	        	Log.i("acnl","result2:"+list.get(i).getResult2());
	        	Log.i("acnl","result3:"+list.get(i).getResult3());
	        	Log.i("acnl","date:"+list.get(i).getDate());
	        	Log.i("acnl","date:"+list.get(i).getSchemeId());
	        	Object[] args = {list.get(i).getTest_type(),list.get(i).getPower_type(),list.get(i).getUb(),list.get(i).getIb(),list.get(i).getUr(),list.get(i).getIr(),list.get(i).getPower_factor(),list.get(i).getPinlv(),list.get(i).getCishu(),list.get(i).getQuanshu(),list.get(i).getWucha_limit(),list.get(i).getResult1(),list.get(i).getResult2(),list.get(i).getResult3(),list.get(i).getDate(),list.get(i).getSchemeId()};
		        db.execSQL(sql, args); 
		        Log.e("acnl","已保存");
	        }
	        db.close();  
	    }  
	    /** 
	     * 按schemeName查询 
	     * 
	     * @param id 
	     * @return 
	     */  
	    public int findSchemeByName(String schemeName) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from acnlScheme where schemeName = ?";  
	        Cursor cursor = db.rawQuery(sql, new String[]{schemeName});  
	        if(cursor.moveToNext()){
                  return cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID));
	        }
	        return -1;
	    }
	    /** 
	     * 按id查询 
	     * 
	     * @param id 
	     * @return 
	     */  
	    public AutoCheckResultBean findById(String id) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from acnl where _id = ?";  
	        Cursor cursor = db.rawQuery(sql, new String[]{id});  
	  
	        AutoCheckResultBean note = null;  
	        if (cursor.moveToNext()) {
	        	  note = new AutoCheckResultBean();
		            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
		            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
		            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		            note.setUb(cursor.getString(cursor.getColumnIndexOrThrow("ub")));
		            note.setIb(cursor.getString(cursor.getColumnIndexOrThrow("ib")));
		            note.setUr(cursor.getString(cursor.getColumnIndexOrThrow("ur")));
		            note.setIr(cursor.getString(cursor.getColumnIndexOrThrow("ir")));
		            note.setTest_type(cursor.getString(cursor.getColumnIndexOrThrow("testType")));
		            note.setPower_type(cursor.getString(cursor.getColumnIndexOrThrow("powerType")));
		            note.setPower_factor(cursor.getString(cursor.getColumnIndexOrThrow("pf")));
		            note.setPinlv(cursor.getString(cursor.getColumnIndexOrThrow("rate")));
		            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("count")));
		            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("circle")));
		            note.setWucha_limit(cursor.getString(cursor.getColumnIndexOrThrow("errorLimit")));
		            note.setResult1(cursor.getString(cursor.getColumnIndexOrThrow("result1")));
		            note.setResult2(cursor.getString(cursor.getColumnIndexOrThrow("result2")));
		            note.setResult3(cursor.getString(cursor.getColumnIndexOrThrow("result3")));
		            
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
	    public AutoCheckResultBean find() {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from acnl order by _id desc";  
	        Cursor cursor = db.rawQuery(sql, new String[]{});  
	        AutoCheckResultBean note = null;  
	        if (cursor.moveToNext()) {  
	            note = new AutoCheckResultBean();
	            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
	            note.setUb(cursor.getString(cursor.getColumnIndexOrThrow("u")));
	            note.setIb(cursor.getString(cursor.getColumnIndexOrThrow("i")));
	            note.setUr(cursor.getString(cursor.getColumnIndexOrThrow("ur")));
	            note.setIr(cursor.getString(cursor.getColumnIndexOrThrow("ir")));
	            note.setTest_type(cursor.getString(cursor.getColumnIndexOrThrow("testType")));
	            note.setPower_type(cursor.getString(cursor.getColumnIndexOrThrow("powerType")));
	            note.setPower_factor(cursor.getString(cursor.getColumnIndexOrThrow("pf")));
	            note.setPinlv(cursor.getString(cursor.getColumnIndexOrThrow("rate")));
	            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("count")));
	            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("circle")));
	            note.setWucha_limit(cursor.getString(cursor.getColumnIndexOrThrow("errorLimit")));
	            note.setResult1(cursor.getString(cursor.getColumnIndexOrThrow("result1")));
	            note.setResult2(cursor.getString(cursor.getColumnIndexOrThrow("result2")));
	            note.setResult3(cursor.getString(cursor.getColumnIndexOrThrow("result3")));
	            
	        }  
	        cursor.close();  
	        db.close();  
	  
	        return note;  
	    }  
	    /** 
	     * 根据测试项查询最近20条
	     * 
	     * @return 
	     */  
	    public ArrayList<AutoCheckResultBean> findDataByTestType(String type) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from acnl where testType = ? order by _id desc limit 0,20";
	        Log.i("ACNLDao","test_type:"+type);
	        Cursor cursor = db.rawQuery(sql, new String[]{type});  
	  
	        ArrayList<AutoCheckResultBean> notes = new ArrayList<AutoCheckResultBean>();  
	        AutoCheckResultBean note = null;  
	        while (cursor.moveToNext()) {  
	        	 note = new AutoCheckResultBean();
		            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
		            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
		            
		            note.setUb(cursor.getString(cursor.getColumnIndexOrThrow("ub")));
		            note.setIb(cursor.getString(cursor.getColumnIndexOrThrow("ib")));
		            note.setUr(cursor.getString(cursor.getColumnIndexOrThrow("ur")));
		            note.setIr(cursor.getString(cursor.getColumnIndexOrThrow("ir")));
		            note.setTest_type(cursor.getString(cursor.getColumnIndexOrThrow("testType")));
		            note.setPower_type(cursor.getString(cursor.getColumnIndexOrThrow("powerType")));
		            note.setPower_factor(cursor.getString(cursor.getColumnIndexOrThrow("pf")));
		            note.setPinlv(cursor.getString(cursor.getColumnIndexOrThrow("rate")));
		            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("count")));
		            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("circle")));
		            note.setWucha_limit(cursor.getString(cursor.getColumnIndexOrThrow("errorLimit")));
		            note.setResult1(cursor.getString(cursor.getColumnIndexOrThrow("result1")));
		            note.setResult2(cursor.getString(cursor.getColumnIndexOrThrow("result2")));
		            note.setResult3(cursor.getString(cursor.getColumnIndexOrThrow("result3")));
		            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		            Log.i("ACNLDao","note.date:"+note.getDate());
		            
		            notes.add(note);
	        } 
	        cursor.close();  
	        db.close();  
	        return notes;  
	    }  
	    /** 
	     * 查询最近20条方案
	     * 
	     * @return 
	     */  
	    public ArrayList<AutoCheckSchemeResultBean> findScheme() {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from acnlscheme order by _id desc limit 0,20";
	        Cursor cursor = db.rawQuery(sql, new String[]{});  
	  
	        ArrayList<AutoCheckSchemeResultBean> notes = new ArrayList<AutoCheckSchemeResultBean>();  
	        while (cursor.moveToNext()) {  
	        	AutoCheckSchemeResultBean note = new AutoCheckSchemeResultBean();
		            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
		            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  		            
		            note.setSchemeName(cursor.getString(cursor.getColumnIndexOrThrow("schemeName")));
		            
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
	    /** 
	     * 根据方案编号查询方案所有内容
	     * 
	     * @return 
	     */  
	    public ArrayList<AutoCheckResultBean> findSchemeBySchemeId(String schemeId) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from acnl where (schemeId = ? and testType ='0') order by _id";
	        Log.i("ACNLDao","test_type:"+schemeId);
	        Cursor cursor = db.rawQuery(sql, new String[]{schemeId});  
	  
	        ArrayList<AutoCheckResultBean> notes = new ArrayList<AutoCheckResultBean>();  
	        AutoCheckResultBean note = null;  
	        while (cursor.moveToNext()) {  
	        	 note = new AutoCheckResultBean();
		            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
		            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));             
		            note.setUb(cursor.getString(cursor.getColumnIndexOrThrow("ub")));
		            note.setIb(cursor.getString(cursor.getColumnIndexOrThrow("ib")));
		            note.setUr(cursor.getString(cursor.getColumnIndexOrThrow("ur")));
		            note.setIr(cursor.getString(cursor.getColumnIndexOrThrow("ir")));
		            note.setTest_type(cursor.getString(cursor.getColumnIndexOrThrow("testType")));
		            note.setPower_type(cursor.getString(cursor.getColumnIndexOrThrow("powerType")));
		            note.setPower_factor(cursor.getString(cursor.getColumnIndexOrThrow("pf")));
		            note.setPinlv(cursor.getString(cursor.getColumnIndexOrThrow("rate")));
		            note.setCishu(cursor.getString(cursor.getColumnIndexOrThrow("count")));
		            note.setQuanshu(cursor.getString(cursor.getColumnIndexOrThrow("circle")));
		            note.setWucha_limit(cursor.getString(cursor.getColumnIndexOrThrow("errorLimit")));
		            note.setResult1(cursor.getString(cursor.getColumnIndexOrThrow("result1")));
		            note.setResult2(cursor.getString(cursor.getColumnIndexOrThrow("result2")));
		            note.setResult3(cursor.getString(cursor.getColumnIndexOrThrow("result3")));
		            note.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
		            note.setSchemeId(cursor.getInt(cursor.getColumnIndexOrThrow("schemeId")));
		            Log.i("ACNLDao","note.date:"+note.getDate());
		            
		            notes.add(note);
	        } 
	        cursor.close();  
	        db.close();  
	        return notes;  
	    }  
	    /** 
	     * 删除方案数据 
	     * 
	     * @param id 
	     */  
	    public void removeScheme(int id) {  
	        SQLiteDatabase db = dbHelper.getWritableDatabase();  
	        String sql = "delete from acnlscheme where _id = ?";  
	        Object[] args = {id};  
	        db.execSQL(sql, args); 
	        
	        String sql1 = "delete from acnl where schemeId = ?";  
	        db.execSQL(sql1, args);
	        
	        db.close();  
	    }  
}
