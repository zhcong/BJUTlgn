package com.zhcong.lgnbjut;


import android.graphics.Bitmap;
import android.widget.ImageView;

import com.uuzuche.lib_zxing.activity.CodeUtils;

/**
 * Created by zhangcong on 17-4-13.
 */

public class QRCode {
    public static void makeQR(String str, ImageView imageView){
        Bitmap mBitmap = CodeUtils.createImage(str, 280, 280, null);
        imageView.setImageBitmap(mBitmap);
    }
}
