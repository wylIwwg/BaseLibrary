package com.sjjd.wyl.baseandroid.register;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.sjjd.wyl.baseandroid.bean.Register;
import com.sjjd.wyl.baseandroid.utils.Configs;
import com.sjjd.wyl.baseandroid.utils.DeviceUtil;
import com.sjjd.wyl.baseandroid.utils.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import es.dmoral.toasty.Toasty;

/**
 * Created by wyl on 2019/3/29.
 */
public class RegisterUtils {


    public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCA3aNm/ra4JgBYZzABSy5TAepEuSCE2x8OqBwtCAXITv/Ei0cn/l09ot4kIYXHZQ9hZ+B1W558AGAxfHZkrYZNj1Hn53AXVqw4/ojeP3RyfcXo8GJom/F9+1kd56NqEm/iv2ETB3vcrGwFJOldy7lMEXfHDtFQaj+i2U+eVfckUQIDAQAB";
    public static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIDdo2b+trgmAFhnMAFLLlMB6kS5IITbHw6oHC0IBchO/8SLRyf+XT2i3iQhhcdlD2Fn4HVbnnwAYDF8dmSthk2PUefncBdWrDj+iN4/dHJ9xejwYmib8X37WR3no2oSb+K/YRMHe9ysbAUk6V3LuUwRd8cO0VBqP6LZT55V9yRRAgMBAAECgYBq5vKp+33az/OTYq6pNBQO2lTcg/MdI6XlA8Kz/KbHX/m/s4bo/5OcESNVN9YB7q1Osdy7nrCfz7P8+XJB3M2/FFTm9iHsu24P6P1IxvjV05jir8g7ycs4RoZuxk2+2Ln8Kd6H+0M7qL528os5YRWu0PUhMOR1y103pqWT20UTkQJBAMwFoJ9xvcaH558Iqy/rPpre/25SyfnZbq/KrI2tv18ItfW5flXq3dfVrOYy9i6zWPu9gL9npuPjY5wK3mhdpyUCQQChsk0iT1+uyFhdg0VJeV4+fjd6Fukq7NjqRUIWGXxHoondtLM4qe7rcWMkFXN0U0gZH3XE/KRqrEM20jsXMma9AkEAwoiHHCDe2+MQJiKk378F5bPFiFMmRLZfBP1SRJErzRjILzGcVZ3pw3f5MVHcTLEzom2Rym+xwM87VjlC0e6ihQJAdoE1lMK1bmR4lrhhbFLd5lEcmYb3BjWlWDTAFXBCLEIMZodLnmi0qKtmLIjoH8X1niv3ZRJ/8YokjKYRFpQixQJAXP70G3X9n/OaPR2uCATh2LGcaaLBHBiqKcLGxmkfFTlbl811nG+sW5gyWIhiv+3/JmCmKCTTRhWTjs/YjKdAeQ==";
    private static final String TAG = " Register ";
    private static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/sjjd";

    //构建Cipher实例时所传入的的字符串，默认为"RSA/NONE/PKCS1Padding"  
    private static String transform = "RSA/NONE/PKCS1Padding";
    //private static String transform = "RSA";
    private static int base64Mode = Base64.DEFAULT;
    byte[] mPublicBytes = Base64.decode(publicKey, base64Mode);
    byte[] mPrivateBytes = Base64.decode(privateKey, base64Mode);

    private static RegisterUtils instance;
    private static Context mContext;
    private Register mRegister;

    public Register getRegister() {
        return mRegister;
    }

    public RegisterUtils(Context context, String privateKey, String publicKey) {
        if (mContext == null) mContext = context;
        if (privateKey != null) {
            mPrivateBytes = Base64.decode(privateKey, base64Mode);
        }
        if (publicKey != null) {
            mPublicBytes = Base64.decode(publicKey, base64Mode);
        }
    }

    public static RegisterUtils getInstance(Context context) {

        return getInstance(context, null, null);

    }

    public static RegisterUtils getInstance(Context context, String privateKey, String publicKey) {
        if (mContext == null) mContext = context;
        if (instance == null) {
            instance = new RegisterUtils(context, privateKey, publicKey);
        }
        return instance;
    }

    /**
     * 封装注册信息 转为base64字符串
     * 公钥加密
     *
     * @param just64 是否只是转为base64
     * @return
     */
    public String register2Base64(boolean just64, String mark) {

        String mac = DeviceUtil.getMacAddress(mContext);
        Register r = new Register();
        r.setIdentity(mac);
        r.setMark(mark);
        String sb = JSON.toJSONString(r);

        byte[] mDataBytes = sb.getBytes();
        String result;
        byte[] mEncode;
        if (just64) {
            mEncode = Base64.encode(mDataBytes, base64Mode);
            result = new String(mEncode);
        } else {
            mEncode = EncryptUtils.encryptRSA(mDataBytes, mPublicBytes, true, transform);
            result = Base64.encodeToString(mEncode, base64Mode);
        }

        LogUtils.e(TAG, "Register: 加密后的数据： " + result);
        return result;

    }


    /**
     * //设备注册成功，写入本地
     *
     * @param data 加密数据
     * @return
     */
    public boolean registerDevice(String data) {
        try {
            if (data == null || data.equals(""))
                return false;
            String result = str2Regsiter(data);//解密数据
            LogUtils.e(TAG, "registerDevice: 允许注册： " + result);
            if (result != null) {
                mRegister = JSON.parseObject(result, Register.class);
                if (mRegister != null) {
                    String mLimit = mRegister.getLimit();
                    if (mLimit != null && mLimit.length() > 0) {
                        int mParseInt = Integer.parseInt(mLimit);
                        if (mParseInt == 0) {//不允许注册
                            return false;
                        } else {
                            File mFile = new File(PATH);
                            try {
                                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mFile, false)));
                                bw.write(data);//写入密文数据
                                bw.flush();
                                bw.close();
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    }
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }


    public Register getRegisterText() {

        try {
            File mFile = new File(PATH);
            if (mFile.exists()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mFile)));
                String data = br.readLine();
                br.close();
                if (data != null && data.length() > 0) {

                    String result = str2Regsiter(data);//解密获取明文数据json
                    return JSON.parseObject(result, Register.class);//将数据转成对象
                }
            }//文件不存在 表示未注册

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * //判断设备是否注册
     * 返回设备注册时间
     *
     * @return
     */
    public int isDeviceRegistered() {
        try {
            mRegister = getRegisterText();//将数据转成对象
            if (mRegister != null) {
                //判断密钥里面的mac值和设备mac是否一致
                String mac = DeviceUtil.getMachineHardwareAddress();
                if (mac == null || mac.equals("02:00:00:00:00:00")) {
                    Toasty.error(mContext, "MAC获取不正确：" + mac, Toast.LENGTH_LONG, true).show();
                    return Configs.REGISTER_FORBIDDEN;
                }
                if (mRegister.getIdentity().equals(mac)) {
                    String mLimit = mRegister.getLimit();
                    mRegister.getDate();
                    if (mLimit != null && mLimit.length() > 0) {
                        return Integer.parseInt(mLimit);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Configs.REGISTER_FORBIDDEN;

    }

    /**
     * 将字符串解密为明文数据
     *
     * @param data
     * @return
     */
    public String str2Regsiter(String data) {
        //解密
        try {
            byte[] mDataBytes = Base64.decode(data, base64Mode);
            byte[] mDecryptBytes = EncryptUtils.decryptRSA(mDataBytes, mPrivateBytes, false, transform);

            String b64 = Base64.encodeToString(mDecryptBytes, base64Mode);
            byte[] mEncode = Base64.decode(b64, base64Mode);
            String result = new String(mEncode);
            LogUtils.e(TAG, "Register: 解密后的数据： " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


}
