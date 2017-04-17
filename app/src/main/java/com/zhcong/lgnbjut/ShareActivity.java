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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.zhcong.Code.Code;

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
        //读取设置
        SQL sql=new SQL(context);
        final SettingStruct st = sql.load();

        //用于创建弹出窗口Dig
        View view = LayoutInflater.from(context).inflate(R.layout.share_main, null);
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager manager = ((Activity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay();
        params.width = (int) (d.getWidth() * 0.8);
        dialogWindow.setAttributes(params);
        //测试
        //绑定刷新按钮
        findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(findViewById(R.id.progressBar2).getVisibility()==View.INVISIBLE){
                    findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                    findViewById(R.id.imageView3).setAlpha(1f);
                    findViewById(R.id.textView5).setVisibility(View.GONE);

                    QRCode.makeQR(Code.encode(st.user,st.password),(ImageView) findViewById(R.id.imageView3));

                    ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar2);
                    pb.setProgress(pb.getMax());
                    //定时器执行
                    handler.postDelayed(runnable, 50);
                }
            }
        });
        //设置进度条
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar2);
        pb.setMax(Values.QRtime*20);
        pb.setProgress(pb.getMax());
        //创建二维码
        QRCode.makeQR(Code.encode(st.user,st.password),(ImageView) findViewById(R.id.imageView3));
        //定时器执行
        handler.postDelayed(runnable, 50);
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null)
            this.onItemClickListener = onItemClickListener;
    }

    //定时器handler
    Handler handler = new Handler();
    //进度条定时器
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // handler自带方法实现定时器
            ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar2);
            try {
                if(pb.getProgress()>0) handler.postDelayed(this, 50);
                else{
                    findViewById(R.id.progressBar2).setVisibility(View.INVISIBLE);
                    findViewById(R.id.imageView3).setAlpha(0.1f);
                    findViewById(R.id.textView5).setVisibility(View.VISIBLE);
                    }
                pb.setProgress(pb.getProgress()-1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
