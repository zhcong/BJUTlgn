package com.zhcong.lgnbjut;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.io.Serializable;

/**
 * Created by zhangcong on 17-4-12.
 */

public class Connect extends Thread{
    Handler mHandler;
    Context context;
    public Connect(Handler m,Context c){
        this.context=c;
        this.mHandler=m;
    }

//判断网络类型
//返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
    public static int GetNetype(Context context)
    {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo==null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_MOBILE)
        {
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
            {
                netType = 3;
            }
            else
            {
                netType = 2;
            }
        }
        else if(nType==ConnectivityManager.TYPE_WIFI)
        {
            netType = 1;
        }
        return netType;
    }
    //登陆
    public void run(){
        String msgf="登陆成功";
        //检查wifi状态
        try {
            if(GetNetype(context)!=1){
                msgf=":WIFI没有打开";
            }
        }catch (Exception e){
            e.printStackTrace();
            msgf=":没有权限访问网络状态";
        }

        Message msg = new Message();
        msg.what = 0;
        Bundle b = new Bundle();
        b.putSerializable("back", msgf);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }
}
