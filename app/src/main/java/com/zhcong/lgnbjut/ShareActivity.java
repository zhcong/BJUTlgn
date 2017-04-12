package com.zhcong.lgnbjut;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

/**
 * Created by zhangcong on 17-4-12.
 */
//百度的代码
public class ShareActivity extends Dialog {
    private Context context;
    private AdapterView.OnItemClickListener   onItemClickListener;
    public ShareActivity(Context context) {
        super(context);
        this.context = context;
    }
    public ShareActivity(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        View view = LayoutInflater.from(context).inflate(R.layout.share_main, null);
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager manager = ((Activity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay();
        params.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(params);
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null)
            this.onItemClickListener = onItemClickListener;
    }
}
