package com.zhcong.lgnbjut;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by zhangcong on 17-4-12.
 * 执行SQLite的主要的怎删改差
 */

public class SQL extends SQLiteOpenHelper{
    SQLiteDatabase db;
    public SQL(Context context) {
        super(context, "setting.db", null, 1);
        try {
            db=SQLiteDatabase.openOrCreateDatabase(Values.dbpath,null);
        }catch (SQLiteCantOpenDatabaseException e){
            e.printStackTrace();
        }
    }
    public void onCreate(SQLiteDatabase db) {
        mktable();
    }
    public void onUpgrade(SQLiteDatabase db,int i,int j) {
        //软件升级，什么也不做
    }
    //执行sql语句，查询setting表
    public SettingStruct query(){
        SettingStruct st = new SettingStruct();
        try{
            Cursor cursor=db.query("setting",null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                for(int i=0;i<cursor.getCount();i++){
                    cursor.move(i);
                    st.user=cursor.getString(1);
                    st.password=cursor.getString(2);
                    st.host=cursor.getString(3);
                    st.flag=cursor.getInt(1)==0?false:true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            //查询失败，重新建表，因为可能是第一次运行
            mktable();
        }
        return st;
    }
    //执行sql语句
    public boolean exec(String sql){
        try{
            db.execSQL(sql);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //创建表，并初始化
    public void mktable(){
        String sql="create table setting(user varchar,password varchar,host varchar,share INTEGER)";
        exec(sql);
        sql="insert into setting values('','','"+Values.host+"',0)";
        exec(sql);
    }
}
