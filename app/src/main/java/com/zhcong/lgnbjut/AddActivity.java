package com.zhcong.lgnbjut;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by zhangcong on 17-4-11.
 * 对应添加用户的界面
 */

public class AddActivity extends Activity {
    SQL sql = new SQL(AddActivity.this);
    boolean passwdflage=false;//密码更改了吗
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_main);
        //返回按钮
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button bt = (Button) findViewById(R.id.button3);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //添加文本改变事件
        EditText et = (EditText) findViewById(R.id.editText2);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(((EditText) findViewById(R.id.editText2)).getText().length()!=0){
                    passwdflage=true;
                }else{
                    passwdflage=false;
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
            }
        });

        et = (EditText) findViewById(R.id.editText);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                ((EditText) findViewById(R.id.editText2)).setText("");
                ((Switch) findViewById(R.id.switch1)).setChecked(false);
            }
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
            }
        });

        //初始化界面
        load();

    }

    //存储用户数据
    void save(){
        //根据不同的状态设置不同的sql语句

    }

    //读取用户数据
    void load() {
        SettingStruct st = sql.query();
        if (st == null) {
            Toast toast = Toast.makeText(AddActivity.this, "数据库打开失败", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        EditText et = (EditText) findViewById(R.id.editText);
        et.setText(st.user);

        if(st.flag) ((Switch) findViewById(R.id.switch1)).setChecked(true);
    }
}
