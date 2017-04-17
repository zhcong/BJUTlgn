package com.zhcong.lgnbjut;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by zhangcong on 17-4-16.
 */

public class Permission {
    //权限获取，最重要的那个是[0]
    static boolean check(String []pre, Activity at){
        int permission = ContextCompat.checkSelfPermission(at,pre[0]);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(at, pre, 1);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
