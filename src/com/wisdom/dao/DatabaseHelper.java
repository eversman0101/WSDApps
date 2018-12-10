package com.wisdom.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	//数据库名字  
    private static final String DB_NAME = "note.db";  
  
    //本版号  
    private static final int VERSION = 1;  
  
    //创建表  
    private static final  String CREATE_TABLE_NOTE = "CREATE TABLE IF NOT EXISTS note(_id integer primary key autoincrement,"+  
            "title text, content text, createDate text, updateDate text)";  
    private static final  String CREATE_TABLE_JIBENWUCHA = "CREATE TABLE IF NOT EXISTS jibenwucha(_id integer primary key autoincrement,"+  
            "no text, meterNo text, userName text, stuffName text,date text,u text,i text,jiaodu text,yougong text,wugong text,gonglvyinshu text,wuchafangshi text,fuhezhuangtai text,maichongchangshu text,quanshu text,cishu text,biaozhunpiancha1 text,diannengwucha1 text,biaozhunpiancha2 text,diannengwucha2 text,biaozhunpiancha3 text,diannengwucha3 text,type text,diannengwucha1_2 text,diannengwucha1_3 text,diannengwucha1_4 text,diannengwucha1_5 text,diannengwucha1_6 text,diannengwucha2_2 text,diannengwucha2_3 text,diannengwucha2_4 text,diannengwucha2_5 text,diannengwucha2_6 text,diannengwucha3_2 text,diannengwucha3_3 text,diannengwucha3_4 text,diannengwucha3_5 text,diannengwucha3_6 text)";
    private static final  String CREATE_TABLE_QIANDONG = "CREATE TABLE IF NOT EXISTS qiandong(_id integer primary key autoincrement,"+  
            "no text, meterNo text, userName text, stuffName text,date text,changshu text,u text,i text,qiandongshijian text,qiandongshiyan1 text,qiandongshiyan2 text,qiandongshiyan3 text,type text)";
    private static final  String CREATE_TABLE_QIDONG = "CREATE TABLE IF NOT EXISTS qidong(_id integer primary key autoincrement,"+  
            "no text, meterNo text, userName text, stuffName text,date text,changshu text,u text,i text,qidongshijian text,qidongshiyan1 text,qidongshiyan2 text,qidongshiyan3 text,type text)";
    private static final  String CREATE_TABLE_ZOUZI = "CREATE TABLE IF NOT EXISTS zouzi(_id integer primary key autoincrement,"+  
            "no text, meterNo text, userName text, stuffName text,date text,zouzifangshi text,yuzhidianneng text,beilv text,biaozhundianneng text,qishi1 text,jieshu1 text,shiji1 text,wucha1 text,qishi2 text,jieshu2 text,shiji2 text,wucha2 text,qishi3 text,jieshu3 text,shiji3 text,wucha3 text,yougong text,wugong text,gonglvyinshu text,u text,i text,jiaodu text,type text)";
    private static final  String CREATE_TABLE_SHIZHONG = "CREATE TABLE IF NOT EXISTS shizhong(_id integer primary key autoincrement,"+  
            "no text, meterNo text, userName text, stuffName text,date text,quanshu text,cishu text,shizhongwucha1 text,pingjunwucha1 text,shizhongwucha2 text,shizhongwucha3 text,pingjunwucha2 text,pingjunwucha3 text,type text,shizhongwucha1_2 text,shizhongwucha1_3 text,shizhongwucha1_4 text,shizhongwucha1_5 text,shizhongwucha1_6 text,shizhongwucha2_2 text,shizhongwucha2_3 text,shizhongwucha2_4 text,shizhongwucha2_5 text,shizhongwucha2_6 text,shizhongwucha3_2 text,shizhongwucha3_3 text,shizhongwucha3_4 text,shizhongwucha3_5 text,shizhongwucha3_6 text)";
    private static final String CREATE_TABLE_ACNL="CREATE TABLE IF NOT EXISTS acnl(_id integer primary key autoincrement,"+  
            "testType text,powerType text,ub text,ib text,ur text,ir text,pf text,rate text,count text,circle text,errorLimit text,result1 text,result2 text,result3 text,date text,schemeId text)";
    private static final String CREATE_TABLE_ACNLSCHEME="CREATE TABLE IF NOT EXISTS acnlscheme(_id integer primary key autoincrement, schemeName text)";
 
    //删除表  
    private static final String DROP_TABLE_NOTE = "drop table if exists note";  
    private static final String DROP_TABLE_JIBENWUCHA = "drop table if exists jibenwucha";  
    private static final String DROP_TABLE_QIANDONG = "drop table if exists qiandong"; 
    private static final String DROP_TABLE_QIDONG = "drop table if exists qidong"; 
    private static final String DROP_TABLE_ZOUZI = "drop table if exists zouzi"; 
    private static final String DROP_TABLE_SHIZHONG = "drop table if exists shizhong"; 
    private static final String DROP_TABLE_ACNL = "drop table if exists acnl"; 
    private static final String DROP_TABLE_ACNLSCHEME = "drop table if exists acnlscheme"; 
    
    public DatabaseHelper(Context context) {  
        super(context, DB_NAME, null, VERSION);  
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		//SQLiteDatabase 用于操作数据库的工具类  
        
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
		db.execSQL(CREATE_TABLE_NOTE);  
        db.execSQL(CREATE_TABLE_JIBENWUCHA);
        db.execSQL(CREATE_TABLE_QIANDONG);
        db.execSQL(CREATE_TABLE_QIDONG);
        db.execSQL(CREATE_TABLE_ZOUZI);
        db.execSQL(CREATE_TABLE_SHIZHONG);
        db.execSQL(CREATE_TABLE_ACNL);
        db.execSQL(CREATE_TABLE_ACNLSCHEME);
        
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE_NOTE);  
	    db.execSQL(CREATE_TABLE_NOTE);  
	    
	    db.execSQL(DROP_TABLE_JIBENWUCHA);  
	    db.execSQL(CREATE_TABLE_JIBENWUCHA);  
	    
	    db.execSQL(DROP_TABLE_QIANDONG);  
	    db.execSQL(CREATE_TABLE_QIANDONG);  
	    
	    db.execSQL(DROP_TABLE_QIDONG);  
	    db.execSQL(CREATE_TABLE_QIDONG);  
	    
	    db.execSQL(DROP_TABLE_ZOUZI);  
	    db.execSQL(CREATE_TABLE_ZOUZI);  
	    
	    db.execSQL(DROP_TABLE_SHIZHONG);  
	    db.execSQL(CREATE_TABLE_SHIZHONG);  
	    
	    db.execSQL(DROP_TABLE_ACNL);
	    db.execSQL(CREATE_TABLE_ACNL);
	    
	    db.execSQL(DROP_TABLE_ACNLSCHEME);
	    db.execSQL(CREATE_TABLE_ACNLSCHEME);
	}  
    
}
