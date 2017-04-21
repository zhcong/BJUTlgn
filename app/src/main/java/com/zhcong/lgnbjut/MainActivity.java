package com.zhcong.lgnbjut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class MainActivity extends AppCompatActivity {

    //登陆状态
    boolean logStatus=false;
    //状态检查器时间
    int thtime=5*1000;
    //两次退出标志
    long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view add
        setTheme(R.style.Color1SwitchStyle);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //fab按钮的操作
        //添加用户操作
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setIcon(R.drawable.people);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, AddActivity.class);
                startActivity(it);
            }
        });

        //分享按钮
        fab = (FloatingActionButton) findViewById(R.id.fab3);
        fab.setIcon(R.drawable.share);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开分享界面
                ShareActivity radioButtonDialog = new ShareActivity(MainActivity.this);
                radioButtonDialog.create();
                radioButtonDialog.show();
            }
        });


        //链接按钮
        fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setIcon(R.drawable.link);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏控件
                hide(0, false);
                if (!logStatus) connect();
                else disconnect();
                //显示控件
            }
        });

        //扫描
        fab = (FloatingActionButton) findViewById(R.id.fab4);
        fab.setIcon(R.drawable.photo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开扫描界面
                try {
                    Intent it = new Intent(MainActivity.this, QRScannerActivity.class);
                    startActivityForResult(it, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //更新套餐
        CircularProgressBar cpb = (CircularProgressBar) findViewById(R.id.progressBarFlow);
        cpb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //刷新
                showAmount(null,false);
                getAccountInfo();
            }
        });
        chkStatusThread();
    }

    //主界面前台时电泳
    @Override
    protected void onResume(){
        super.onResume();
        //查看是否分享，添加分享按钮
        SQL sql=new SQL(MainActivity.this);
        SettingStruct st=sql.load();
        sql.close();
        if(!st.flag) ((FloatingActionButton) findViewById(R.id.fab3)).setVisibility(View.GONE);
        else ((FloatingActionButton) findViewById(R.id.fab3)).setVisibility(View.VISIBLE);

        //id数据库读取
        ((TextView)findViewById(R.id.textViewId)).setText(st.user);
        //检查状态
        chkStatus();
    }

    //两下返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if((System.currentTimeMillis() - exitTime) < 2000) {
                finish();
                System.exit(0);
            }else{
                Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime=System.currentTimeMillis();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar   clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {//打开帮助界面
            Intent it = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(it);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //执行post请求，用于网络登陆
    void connect() {
        try {
        //检查权限
        if(!Permission.check(new String[]{"android.permission.INTERNET","android.permission.ACCESS_NETWORK_STATE","android.permission.ACCESS_WIFI_STATE"},MainActivity.this)) {
            throw  new Exception("没有网络权限");
        }
        //用于接受线程的消息
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    LoginStruct mg = (LoginStruct) msg.getData().getSerializable("back");
                    ShowMsg(mg);
                    chkStatus();
                    hide(0,true);
                    //成功更改按钮背景
                    logStatus=mg.flag;
                    hide(1,logStatus);
                }
            }
        };
            //用于新的登陆线程
            Connect m1 = new Connect(mHandler,MainActivity.this,0);
            m1.start();
        }catch (Exception e){
            e.printStackTrace();
            Toast toast = Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //用于登出
    void disconnect() {
        try {
            //检查权限
            if(!Permission.check(new String[]{"android.permission.INTERNET","android.permission.ACCESS_NETWORK_STATE","android.permission.ACCESS_WIFI_STATE"},MainActivity.this)) {
                throw  new Exception("没有网络权限");
            }
            //用于接受线程的消息
            Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        LoginStruct mg = (LoginStruct) msg.getData().getSerializable("back");
                        ShowMsg(mg);
                        chkStatus();
                        hide(0,true);
                        //成功更改按钮背景
                        logStatus=!mg.flag;
                        hide(1,logStatus);
                    }
                }
            };
            //用于新的登陆线程
            Connect m1 = new Connect(mHandler,MainActivity.this,1);
            m1.start();
        }catch (Exception e){
            e.printStackTrace();
            Toast toast = Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //执行post请求，检查登陆状态
    void chkStatus() {
        if(Connect.GetNetype(MainActivity.this)!=1){
            ShowMsg(new LoginStruct("请打开WIFI","",false));
            findViewById(R.id.fab1).setVisibility(View.GONE);//wifi没打开不能登陆
            showAmount(null,false);
            return;
        }else{
            findViewById(R.id.fab1).setVisibility(View.VISIBLE);
        }
        try {
            //检查权限
            if(!Permission.check(new String[]{"android.permission.INTERNET","android.permission.ACCESS_NETWORK_STATE","android.permission.ACCESS_WIFI_STATE"},MainActivity.this)) {
                throw  new Exception("没有网络权限");
            }
            if(Connect.GetNetype(MainActivity.this)!=1) return;
            //用于接受线程的消息
            Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        LoginStruct mg = (LoginStruct) msg.getData().getSerializable("back");
                        logStatus=mg.flag;
                        hide(1,logStatus);
                        ShowMsg(mg);
                        //套餐界面设置
                        if(logStatus) getAccountInfo();
                        else showAmount(null,false);
                    }
                }
            };
            //用于新的登陆线程
            Connect m1 = new Connect(mHandler,MainActivity.this,3);
            m1.start();
        }catch (Exception e){
            e.printStackTrace();
            Toast toast = Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //用于查询信息
    void getAccountInfo() {
        try {
            //检查权限
            if(!Permission.check(new String[]{"android.permission.INTERNET","android.permission.ACCESS_NETWORK_STATE","android.permission.ACCESS_WIFI_STATE"},MainActivity.this)) {
                throw  new Exception("没有网络权限");
            }
            if(Connect.GetNetype(MainActivity.this)!=1) return;
            //用于接受线程的消息
            Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        LoginStruct mg = (LoginStruct) msg.getData().getSerializable("back");
                        showAmount(mg,logStatus);
                    }
                }
            };
            //用于新的登陆线程
            Connect m1 = new Connect(mHandler,MainActivity.this,2);
            m1.start();
        }catch (Exception e){
            e.printStackTrace();
            Toast toast = Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //每个动作下那些空间会隐藏，flag=false隐藏，i代表不同的动作
    void hide(int i,boolean flag){
        switch (i){
            case 0:{//按下登陆fab的界面
                if(!flag){
                    //隐藏
                    findViewById(R.id.fab1).setVisibility(View.GONE);
                    findViewById(R.id.progressBar4).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar4_text).setVisibility(View.VISIBLE);
                    findViewById(R.id.textView).setVisibility(View.GONE);
                    findViewById(R.id.textViewId).setVisibility(View.GONE);
                }else{
                    //显示
                    findViewById(R.id.fab1).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar4).setVisibility(View.GONE);
                    findViewById(R.id.progressBar4_text).setVisibility(View.GONE);
                    findViewById(R.id.textView).setVisibility(View.VISIBLE);
                    findViewById(R.id.textViewId).setVisibility(View.VISIBLE);
                }
            }break;
            case 1:{//登陆成功的界面
                if(!flag){
                    //没登陆
                    findViewById(R.id.textViewId).setVisibility(View.VISIBLE);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);;
                    fab.setIcon(R.drawable.link);
                }else{
                    //登陆了
                    findViewById(R.id.textViewId).setVisibility(View.GONE);
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);;
                    fab.setIcon(R.drawable.nolink);
                }
            }break;
        }
    }
//显示错误或者成功的信息
    void ShowMsg(LoginStruct str){
        TextView textview = (TextView) findViewById(R.id.textView);
        textview.setText(str.title);
        if(!str.flag){
            //提示错误类型
            Toast toast = Toast.makeText(MainActivity.this, str.about, Toast.LENGTH_SHORT);
            if(str.about.length()!=0) toast.show();
        }
    }

    //显示套餐信息，可以用来隐藏相关控件
    void showAmount(LoginStruct mg,boolean f){
        if(f) {
            findViewById(R.id.progressBarFlow).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBarFlowText).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.progressBarFlowText)).setText(mg.title+" MB");
            Double count = 0.0;
            try {
                count = Double.parseDouble(mg.title);
            } catch (Exception e) {
                count = 0.0;
            }
            SQL sql = new SQL(MainActivity.this);
            SettingStruct st = sql.load();
            sql.close();
//            animal((int) (count / (st.flow_size * 1024)*100));
            ((CircularProgressBar) findViewById(R.id.progressBarFlow)).setProgress((int) (count / (st.flow_size * 1024)*100));

        }else{
            findViewById(R.id.progressBarFlow).setVisibility(View.GONE);
            findViewById(R.id.progressBarFlowText).setVisibility(View.GONE);
        }
    }

//    //动画
//    void animal(int m){
//        final int max=m;
//        CircularProgressBar pb = (CircularProgressBar) findViewById(R.id.progressBarFlow);
//        pb.setProgress(0);
//        //定时器handler
//        final Handler handler = new Handler();
//        //进度条定时器
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                // handler自带方法实现定时器
//                CircularProgressBar pb = (CircularProgressBar) findViewById(R.id.progressBarFlow);
//                if (pb.getProgress() < max) handler.postDelayed(this, 5);
//                try {
//                    pb.setProgress(pb.getProgress() + 1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        handler.postDelayed(runnable, 5);
//    }

    //检查状态
    void chkStatusThread(){
        //定时器handler
        final Handler handler = new Handler();
        //进度条定时器
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // handler自带方法实现定时器
                handler.postDelayed(this, thtime);
                try {
                    chkStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, thtime);
    }
}
