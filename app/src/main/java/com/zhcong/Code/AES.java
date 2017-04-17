package com.zhcong.Code;

import android.util.Base64;

import com.scottyab.aescrypt.AESCrypt;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * Created by zhangcong on 17-4-11.
 */
public class AES {
    //thanks for "https://github.com/scottyab/AESCrypt-Android"
    public static String Encrypt(String content, String password) {
        AESCrypt.DEBUG_LOG_ENABLED = false;
        try {
            String bt = AESCrypt.encrypt(password,content);
            return bt;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }


    public static String Decrypt(String content, String password) {
        AESCrypt.DEBUG_LOG_ENABLED = false;
        try {
            String bt = AESCrypt.decrypt(password,content);
            return bt;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static String Base64Encode(byte[] bstr){
        return Base64.encodeToString(bstr,Base64.DEFAULT);
    }

    public static byte[] Base64Decode(String str){
            return Base64.decode(str,Base64.DEFAULT);
    }
    public static String MD5(String data){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        }catch (Exception e){
            return "";
        }
    }
}
