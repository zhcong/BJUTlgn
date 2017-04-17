package com.zhcong.lgnbjut;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhcong.Code.Code;

/**
 * Created by zhangcong on 17-4-12.
 */
//百度的代码
public class AlertActivity extends Dialog {
    AlertConfirm ob;//确认键
    private Context context;
    private AdapterView.OnItemClickListener   onItemClickListener;
    String title;
    String about;
    public AlertActivity(Context context,String t,String a,AlertConfirm ob) {
        super(context);
        this.context = context;
        title=t;
        about=a;
        this.ob=ob;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //用于创建弹出窗口Dig
        View view = LayoutInflater.from(context).inflate(R.layout.alert_main, null);
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager manager = ((Activity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay();
        params.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(params);

        //显示title与内容
        TextView textview = (TextView) findViewById(R.id.texttitle);
        textview.setText(title);

        textview = (TextView) findViewById(R.id.textabout);
        textview.setText(about);

        //确认和取消键
        Button bt = (Button) findViewById(R.id.buttoncancel);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        bt = (Button) findViewById(R.id.buttonsure);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ob.Confirm();
                dismiss();
            }
        });
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null)
            this.onItemClickListener = onItemClickListener;
    }
}
