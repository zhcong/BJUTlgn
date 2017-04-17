package com.zhcong.lgnbjut;

/**
 * Created by zhangcong on 17-4-12.
 * 这个类用来存储一些固定的值
 * 比如注册用的域名地址、管理用的域名地址
 */

public class Values {
    //默认的无线登陆注册的域名
    public static String host="https://wlgn.bjut.edu.cn";
    //数据库位置
    public static String dbpath="/data/data/com.zhcong.lgnbjut/setting.db";
    //二维码有效时间，最好别太短
    public static int QRtime=30;
}
