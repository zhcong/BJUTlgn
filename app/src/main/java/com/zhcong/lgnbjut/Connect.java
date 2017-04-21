package com.zhcong.lgnbjut;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.zhcong.Code.AES;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangcong on 17-4-12.
 */

public class Connect extends Thread{
    int runflag=0;//run函数执行的模式，0：登陆模式，1：登出模式，2，找信息
    //用于线程通信
    Handler mHandler;
    Context context;
    //初始化
    public Connect(Handler m,Context c,int type){
        this.context=c;
        this.mHandler=m;
        this.runflag=type;
    }

//判断网络类型
//返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
    public static int GetNetype(Context context)
    {
        try {
            int netType = -1;
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo == null) {
                return netType;
            }
            int nType = networkInfo.getType();
            if (nType == ConnectivityManager.TYPE_MOBILE) {
                netType = 2;
            } else if (nType == ConnectivityManager.TYPE_WIFI) {
                netType = 1;
            }
            return netType;
        }catch (Exception e){
            e.printStackTrace();
            return 2;
        }
    }
    //获得IP，一定要先检查wifi有没有打开??
    public static String getIp(Context ct){
        WifiManager wifiManager = (WifiManager) ct.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        StringBuilder sb = new StringBuilder();
        sb.append(ipAddress & 0xFF).append(".");
        sb.append((ipAddress >> 8) & 0xFF).append(".");
        sb.append((ipAddress >> 16) & 0xFF).append(".");
        sb.append((ipAddress >> 24) & 0xFF);
        return sb.toString();
    }
    //登陆主体的线程
    public void run(){
        LoginStruct msgf = new LoginStruct();
        //检查wifi状态
        try {
            if(GetNetype(context)!=1){
                msgf = new LoginStruct("登录失败","WIFI没有打开",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            msgf = new LoginStruct("登录失败","没有权限访问网络状态",false);
        }

        //登陆
        if(runflag==0) {
            //执行登陆的操作
            //检查所在网络类型
            int type = gettype();
            //读取数据库
            SQL sql = new SQL(context);
            SettingStruct st = sql.load();
            String msg="用户名密码错误";
            if (st.user.length() != 0 && st.password.length() != 0) {
                //post请求登陆
                msg = login(st.user, st.password, type);
            }
            if(chkStatus(gettype())) msgf = new LoginStruct("您已登录","",false);
            else msgf = new LoginStruct("请登录",msg,false);
            sql.close();
        }

        //登出
        if(runflag==1){
            int type = gettype();
            logout(type);
            if(!chkStatus(gettype())){
                msgf = new LoginStruct("您已登出","",true);
            }else{
                msgf = new LoginStruct("登出失败","请稍后再试",false);
            }
        }

        //获取信息
        if(runflag==2){
            String html = get("https://lgn.bjut.edu.cn");
            String used = getAmount(html);//LoginStruct.title
            String money = getMoney(html);//LoginStruct.about
            msgf = new LoginStruct(used, money, true);
        }

        //检查状态
        if(runflag==3){
            if (chkStatus(gettype())) msgf = new LoginStruct("您已登录", "", true);
            else msgf = new LoginStruct("请登录", "", false);
        }

        //向主界面发送登陆的结果
        Message msg = new Message();
        msg.what = 0;
        Bundle b = new Bundle();
        b.putSerializable("back", (Serializable)msgf);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    //获得设备唯一ID
    public static String getID(Context ct){
        //感谢 http://blog.csdn.net/ljz2009y/article/details/22895297
        WifiManager wm = (WifiManager)ct.getSystemService(Context.WIFI_SERVICE);
        //获取androidID
        String m_szAndroidID = Settings.Secure.getString(ct.getContentResolver(), Settings.Secure.ANDROID_ID);
        //获取MAC地址
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        //获取BuildID
        String m_szBuildID = Build.ID.toString();

        //拼接取MD5
        String passwordKey= AES.MD5(m_szAndroidID+m_szWLANMAC+m_szBuildID);

        return passwordKey;
    }
    //get请求
    public static String get(String url){
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    //post请求
    public static String post(String url,String param){
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    //检查是否登陆的错误种类
    public static String chk(String str){
        //检查是否登陆成功
        if(str.indexOf("ldap auth error")>=0) return "用户名密码错误";//顺序千万不能反
        if(str.indexOf("In use")>=0) return "已在其他设备上登陆";
        return "";
    }

    //检查在线状态
    public static boolean chkStatus(int type){
        if(type==0){
            String html=get("https://wlgn.bjut.edu.cn");
            return html.indexOf("successfully logged")>=0;
        }else{
            String html=get("https://lgn.bjut.edu.cn");
            return html.indexOf("Used flux")>=0;
        }
    }

    //登陆，type=0从wlan登陆，否则从正常网络登陆，返回登陆是否成功字符串
    public String login(String user, String passwd,int type){
        if(type==0){
            String re = post("https://wlgn.bjut.edu.cn", "DDDDD="+user+"&upass="+passwd+"&6MKKey=123");
            return chk(re);
        }else{
            String re = post("https://lgn.bjut.edu.cn", "DDDDD="+user+"&upass="+passwd+"&v46s=1&v6ip=&f4serip=172.30.201.2&0MKKey=");
            return chk(re);
        }
    }
    //登出
    public void logout(int type){
        if(type==0){
            get("https://wlgn.bjut.edu.cn/F.htm");
        }else{
            get("https://lgn.bjut.edu.cn/F.htm");
        }
    }
    //判断是在那种网络下，通过ip判断
    public int gettype(){
        String ip = getIp(context);
        //开头为10.则在学校的无线网中
        if(ip.charAt(0)=='1' && ip.charAt(1)=='0' && ip.charAt(2)=='.') return 0;
        else return 1;
    }

    //检查套餐使用量
    public static String getAmount(String str){
        //
        Pattern pattern = Pattern.compile("(?<=flow=\')[^\']*");
        Matcher matcher = pattern.matcher(str);
        DecimalFormat df = new DecimalFormat("#.000");
        if(matcher.find()) return df.format(Long.parseLong(matcher.group(0).trim())/1024.0);
        else return "";
    }

    //检查余额
    public  String getMoney(String html) {
        Pattern pattern = Pattern.compile("(?<=fee=\')[^\']*");
        Matcher matcher = pattern.matcher(html);
        DecimalFormat df = new DecimalFormat("#.000");
        if(matcher.find()) return df.format(Long.parseLong(matcher.group(0).trim())/1000.0);
        else return "";
    }
}
