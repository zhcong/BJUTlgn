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
    public int flow_size=Values.flow_size;//套餐流量
    public SettingStruct(String u,String p,String h,Boolean f,int fs){
        user=u;
        password=p;
        host=h;
        flag=f;
        flow_size=fs;
    }
    public SettingStruct(){}
}
