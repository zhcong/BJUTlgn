package com.zhcong.lgnbjut;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by zhangcong on 17-4-12.
 */

public class SQL {
    SQLiteDatabase db;
    public SQL(){
        db = openOrCreateDatabase("setting.db", null);
        String sql = "SELECT COUNT(*) FROM sqlite_master where type='table' and name='setting'";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                mktable();
            }
        }
    }
    //执行sql语句，查询
    public boolean query(String table){
        try{
            Cursor cursor=db.query("setting",null,null,null,null,null,null);
            if(cursor.moveToFirst()){
                for(int i=0;i<cursor.getCount();i++){
                    cursor.move(i);
                    String username=cursor.getString(1);
                    String password=cursor.getString(2);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean exec(String sql){
        try{
            db.execSQL(sql);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //创建表
    public void mktable(){
        String sql="create table setting(user varchar,password varchar,host varchar,share INTEGER)";
        exec(sql);
        sql="insert into setting values('','','"+Values.host+"',0)";
    }
}
