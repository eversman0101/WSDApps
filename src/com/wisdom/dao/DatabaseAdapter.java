package com.wisdom.dao;

import java.util.ArrayList;

import com.wisdom.bean.Note;
import com.wisdom.bean.TaitiCeLiangShuJuBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseAdapter {
	 private DatabaseHelper dbHelper;  
	  
	    public DatabaseAdapter(Context context) {  
	        dbHelper = new DatabaseHelper(context);  
	    }  
	  
	    /** 
	     * 添加数据 
	     * 
	     * @param note 
	     */  
	    public void add(Note note) {  
	        String sql = "insert into note(title, content, createDate, updateDate)values(?,?,?,?)";  
	        Object[] args = {note.getTitle(), note.getContent(), note.getCreateDate(), note.getUpdateDate()};  
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
	    public Note findById(int id) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from note where _id = ?";  
	        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});  
	  
	        Note note = null;  
	        if (cursor.moveToNext()) {  
	            note = new Note();
	            
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.TITLE)));  
	            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.CONTENT)));  
	            note.setCreateDate(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.CREATE_DATE)));  
	            note.setUpdateDate(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.UPDATE_DATE)));  
	        }  
	        cursor.close();  
	        db.close();  
	  
	        return note;  
	    }  
	    /** 
	     * 按title查询 
	     * 
	     * @param  
	     * @return 
	     */  
	    public Note findByTitle(String title) {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from note where title = ? order by _id desc";  
	        Cursor cursor = db.rawQuery(sql, new String[]{title});  
	  
	        Note note = null;  
	        if (cursor.moveToNext()) {  
	            note = new Note();
	            Log.i("DataBase","ID:"+cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));
	            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MetaData._ID)));  
	            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.TITLE)));  
	            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.CONTENT)));  
	            note.setCreateDate(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.CREATE_DATE)));  
	            note.setUpdateDate(cursor.getString(cursor.getColumnIndexOrThrow(MetaData.UPDATE_DATE)));  
	        }  
	        cursor.close();  
	        db.close();  
	  
	        return note;  
	    }  
	    /** 
	     * 查询所有 
	     * 
	     * @return 
	     */  
	    public ArrayList<Note> findAll() {  
	        SQLiteDatabase db = dbHelper.getReadableDatabase();  
	        String sql = "select * from note";  
	        Cursor cursor = db.rawQuery(sql,null);  
	  
	  
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
