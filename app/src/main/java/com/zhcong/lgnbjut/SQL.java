package com.zhcong.lgnbjut;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import com.zhcong.Code.AES;

import java.io.File;

/**
 * Created by zhangcong on 17-4-12.
 * 执行SQLite的主要的怎删改差
 * user 用户名,password 密码,host 注册网关,share 是否分享（0不分享，1分享）,qrtime 二维码时间
 */

public class SQL {
    SQLiteDatabase db;
    Context ct;

    public SQL(Context context) {
        ct=context;
        try {
            File f = new File(Values.dbpath);
            if(f.exists()){
                db = SQLiteDatabase.openOrCreateDatabase(Values.dbpath, null);
            }else {
                db = SQLiteDatabase.openOrCreateDatabase(Values.dbpath,null);
                mktable();
            }
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();
        }
    }

    //执行sql语句，查询setting表
    public SettingStruct load() {
        //别忘了解密
        SettingStruct st = new SettingStruct();
        try {
            Cursor cursor = db.query("setting", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.move(i);
                    st.user = cursor.getString(0);
                    st.password = AES.Decrypt(cursor.getString(1),Connect.getID(ct));
                    st.host = cursor.getString(2);
                    st.flag = cursor.getInt(3) == 0 ? false : true;
                    st.flow_size = cursor.getInt(4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return st;
    }

    //执行sql语句
    public boolean exec(String sql) {
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //创建表，并初始化
    public void mktable() {
        String sql = "create table setting(user varchar,password varchar,host varchar,share INTEGER,flow INTEGER)";
        exec(sql);
        sql = "insert into setting values('','','" + Values.host + "',0,"+Values.flow_size+")";
        exec(sql);
    }

    //存取配置
    public void save(SettingStruct st) {
        //删除所有，这种逻辑只能工作在单用户下
        String sql = "delete from setting where 1==1";
        exec(sql);
        //别忘了加密,使用唯一ID作为KEY
        st.password= AES.Encrypt(st.password,Connect.getID(ct));

        int flag = st.flag ? 1 : 0;
        sql = "insert into setting values('" + st.user + "','" + st.password + "','" + st.host + "'," + flag + "," +st.flow_size+ ")";
        exec(sql);
    }
    //清除设置
    public void clear(){
        String sql = "delete from setting where 1==1";
        exec(sql);
        sql = "insert into setting values('','','" + Values.host + "',0,"+Values.flow_size+")";
        exec(sql);
    }
    //关闭
    public void close(){
        db.close();
    }
}
