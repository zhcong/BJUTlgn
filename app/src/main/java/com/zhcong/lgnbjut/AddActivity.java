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
import android.widget.CompoundButton;
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
    SettingStruct st;//保存设置的数据结构
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

        //保存按钮
        bt = (Button) findViewById(R.id.button2);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                Toast toast = Toast.makeText(AddActivity.this, "已保存", Toast.LENGTH_SHORT);
                toast.show();
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
                //密码框改变
                    passwdflage=true;
            }
            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,int arg3) {
            }
        });

        //用户名
        et = (EditText) findViewById(R.id.editText);
        //文本改变事件
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(st.user)){
                    //用户名更改，则所有的问题重置
                    ((EditText) findViewById(R.id.editText2)).setHint("输入密码");
                    st.password = "";
                    ((Switch) findViewById(R.id.switch1)).setChecked(false);
                }
            }
        });
        //share按钮
        ((Switch) findViewById(R.id.switch1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    st.flag=true;
                } else {
                    st.flag=false;
                }

            }
        });
        //初始化界面
        load();

    }

    //存储用户数据
    void save(){
        //判断非法情况
        if(((EditText) findViewById(R.id.editText)).getText().length()==0){
            Toast toast = Toast.makeText(AddActivity.this, "用户名不能为空", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if(((EditText) findViewById(R.id.editText2)).getText().length()==0 && st.password.length()==0){
            Toast toast = Toast.makeText(AddActivity.this, "密码不能为空", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if(passwdflage) st.password=((EditText) findViewById(R.id.editText2)).getText().toString();
        st.user=((EditText) findViewById(R.id.editText)).getText().toString();

        sql.save(st);
    }

    //读取用户数据
    void load() {
        st = sql.load();
        if (st == null) {
            Toast toast = Toast.makeText(AddActivity.this, "数据库打开失败", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if(st.user.length()==0){
            EditText et = (EditText) findViewById(R.id.editText);
            et.setHint("添加用户名");
            et = (EditText) findViewById(R.id.editText2);
            et.setHint("输入密码");
        }else {
            EditText et = (EditText) findViewById(R.id.editText);
            et.setText(st.user);
        }
        if(st.flag) ((Switch) findViewById(R.id.switch1)).setChecked(true);
    }
}
