package com.zhcong.lgnbjut;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view add
        setTheme(R.style.Color1SwitchStyle);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                ShareActivity radioButtonDialog=new ShareActivity(MainActivity.this);
                radioButtonDialog.create();
                radioButtonDialog.show();
            }
        });


        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏控件
                hide(0,false);
                connect();
                //显示控件
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
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
            mHandler.wait();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //每个动作下那些空间会隐藏，flag=false隐藏，i代表不同的动作
    void hide(int i,boolean flag){
        switch (i){
            case 0:{
                if(!flag){
                    findViewById(R.id.fab1).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progressBar4).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar4_text).setVisibility(View.VISIBLE);
                    findViewById(R.id.textView).setVisibility(View.INVISIBLE);
                }else{
                    findViewById(R.id.fab1).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar4).setVisibility(View.INVISIBLE);
                    findViewById(R.id.progressBar4_text).setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView).setVisibility(View.VISIBLE);
                }
            }break;//链接时隐藏
        }
    }
//显示错误信息
    void ShowMsg(String str){
        if(str.charAt(0)==':'){
            TextView textview = (TextView) findViewById(R.id.textView);
            textview.setText("登陆失败");

            Toast toast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT);
            toast.show();
        }else {
            TextView textview = (TextView) findViewById(R.id.textView);
            textview.setText(str);
        }
    }
}
