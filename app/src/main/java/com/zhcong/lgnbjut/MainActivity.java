package com.zhcong.lgnbjut;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.zhcong.Code.Code;
import com.zhcong.Code.TOTP;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view add
        setTheme(R.style.Color1SwitchStyle);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //fab按钮的操作
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, AddActivity.class);
                startActivity(it);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab3);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开分享界面
                ShareActivity radioButtonDialog=new ShareActivity(MainActivity.this);
                radioButtonDialog.create();
                radioButtonDialog.show();
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏控件
                hide(0,false);
                connect();
                //显示控件
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab4);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开扫描界面
                try {
                    Intent it = new Intent(MainActivity.this, QRScannerActivity.class);
                    startActivityForResult(it, 1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    //主界面前台时电泳
    @Override
    protected void onResume(){
        super.onResume();
        //查看是否分享，添加分享按钮
        SQL sql=new SQL(MainActivity.this);
        SettingStruct st=sql.load();
        if(!st.flag) ((FloatingActionButton) findViewById(R.id.fab3)).setVisibility(View.GONE);
        else ((FloatingActionButton) findViewById(R.id.fab3)).setVisibility(View.VISIBLE);
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
        //用于接受线程的消息
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    String mg = (String) msg.getData().getSerializable("back");
                    ShowMsg(mg);
                    hide(0,true);
                }
            }
        };
        //用于新的线程
        Connect m1 = new Connect(mHandler,MainActivity.this);
        m1.start();
        try {
            //等待线程工作完成
            mHandler.wait();
        }catch (Exception e){
            e.printStackTrace();
            Toast toast = Toast.makeText(MainActivity.this, "网络访问失败，请检查是否赋予权限", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //每个动作下那些空间会隐藏，flag=false隐藏，i代表不同的动作
    void hide(int i,boolean flag){
        switch (i){
            case 0:{//按下登陆fab的界面
                if(!flag){
                    //隐藏
                    findViewById(R.id.fab1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progressBar4).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar4_text).setVisibility(View.VISIBLE);
                    findViewById(R.id.textView).setVisibility(View.INVISIBLE);
                }else{
                    //显示
                    findViewById(R.id.fab1).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar4).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progressBar4_text).setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView).setVisibility(View.VISIBLE);
                }
            }break;
        }
    }
//显示错误信息
    void ShowMsg(String str){
        if(str.charAt(0)==':'){
            //通过':'号判断是否出错
            TextView textview = (TextView) findViewById(R.id.textView);
            textview.setText("登陆失败");

            //提示错误类型
            Toast toast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT);
            toast.show();
        }else {
            //显示登陆成功
            TextView textview = (TextView) findViewById(R.id.textView);
            textview.setText(str);
        }
    }
}
