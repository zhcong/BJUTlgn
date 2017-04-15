package com.zhcong.lgnbjut;

/**
 * Created by zhangcong on 17-4-13.
 */

//对应数据库的一行
public class SettingStruct {
    public String user="";
    public String password="";
    public String host="";//登陆页面
    public Boolean flag=false;//是否分享
    public SettingStruct(String u,String p,String h,Boolean f){
        user=u;
        password=p;
        host=h;
        flag=f;
    }
    public SettingStruct(){}
}
