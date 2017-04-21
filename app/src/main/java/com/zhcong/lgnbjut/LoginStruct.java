package com.zhcong.lgnbjut;

import java.io.Serializable;

/**
 * Created by zhangcong on 17-4-18.
 */

public class LoginStruct implements Serializable {
    public String title;//在主界面上显示的内容
    public String about;//Toast提示的内容
    public boolean flag;//可以表示各种状态
    public LoginStruct(String t, String a, boolean f){
        this.about=a;
        this.title=t;
        this.flag=f;
    }
    public LoginStruct(){
        this.about="";
        this.title="";
        this.flag=false;
    }
}
