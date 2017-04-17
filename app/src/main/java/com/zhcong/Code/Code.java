package com.zhcong.Code;
/**
 * Created by zhangcong on 17-4-15.
 */
//使用AES与TOTP对用户名与密码进行加密
//多线程要完蛋啊，TOTP类幸亏不会多线程访问
public class Code {
    //用于检验是否解密成功的字符串
    public static String encode(String user, String password){
        //使用产生TOTP的key产生一次性密钥，用这个密钥对密码进行加密
        //返回格式：用户名;加密后的密码;产生TOTP的key;矫正步长
        try{
            //产生TOTP的key
            String TOTPKey = TOTP.generateBase32Secret();
            //获得TOTP生成的一次性密钥
            TOTP.value TOTPOKey = TOTP.generateCurrentMAKENumberString(TOTPKey);
            //AES对密码进行加密
            String passwordEncode=AES.Encrypt(password,TOTPOKey.vs);
            //拼接
            return user+";"+passwordEncode+";"+TOTPKey+";"+TOTPOKey.step;

        }catch (Exception e){
            e.printStackTrace();
            return "Encryption Error.";
        }
    }

    //解密失败返回空字符串，否则返回；隔开的用户名与密码
    public static String decode(String EncodeStr){
        try{
            String[] strList=EncodeStr.split(";");
            //生成解密用的AES的key
            String AESKey = TOTP.generateCurrentCHKNumberString(strList[2],Integer.parseInt(strList[3]));//传入矫正步长
            //解密AES加密的密码
            String password=AES.Decrypt(strList[1],AESKey);
            //拼接
            if(password!=null) return strList[0]+";"+password;
            else throw new Exception();
        }catch (Exception e){
            return "";
        }
    }
}
