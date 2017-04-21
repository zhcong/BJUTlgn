package com.zhcong.lgnbjut;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhcong.Code.Code;

/**
 * Created by zhangcong on 17-4-14.
 */

public class QRScannerActivity extends FragmentActivity implements AlertConfirm{
    SettingStruct Rbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrscaner_main);
        //检查并请求相机权限
        if(!Permission.check(new String[]{"android.permission.CAMERA","android.permission.VIBRATE"},QRScannerActivity.this)){
            Toast toast = Toast.makeText(QRScannerActivity.this, "无法获得相机权限", Toast.LENGTH_SHORT);
            toast.show();
        }

        //取消按钮
        Button bt = (Button) findViewById(R.id.button3);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //注册二维码扫描
        ZXingLibrary.initDisplayOpinion(this);
        /**
         * 执行扫面Fragment的初始化操作
         */
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.camera_main);

        captureFragment.setAnalyzeCallback(analyzeCallback);
        /**
         * 替换我们的扫描控件
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();
    }

    //扫描回调函数
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Rbox= Code.decode(result);
            //判断二维码的有效性
            if(Rbox.password.length()==0 || Rbox==null){
                Toast toast = Toast.makeText(QRScannerActivity.this, "二维码已过期", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }else {
                //弹出确认框
                AlertActivity radioButtonDialog = new AlertActivity(QRScannerActivity.this, "更改为用户" ,Rbox.user,QRScannerActivity.this);
                radioButtonDialog.create();
                radioButtonDialog.show();
            }
        }

        @Override
        public void onAnalyzeFailed() {
            Toast toast = Toast.makeText(QRScannerActivity.this, "扫描失败", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    //点击确认干的事情
    public void Confirm(){
        //读取本地数据库
        SQL sql = new SQL(QRScannerActivity.this);
        SettingStruct st = sql.load();

        st.user=Rbox.user;
        st.password=Rbox.user;
        st.flag=true;

        sql.save(st);

        finish();
    }

}
