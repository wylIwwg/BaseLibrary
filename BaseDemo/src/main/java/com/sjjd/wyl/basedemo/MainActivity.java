package com.sjjd.wyl.basedemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sjjd.wyl.baseandroid.base.BaseActivity2;
import com.sjjd.wyl.baseandroid.utils.DeviceUtil;
import com.sjjd.wyl.baseandroid.utils.LogUtils;
import com.sjjd.wyl.baseandroid.utils.WifiUtil;
import com.yanzhenjie.permission.runtime.Permission;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends BaseActivity2 {
    TextView mTextView;
    TextView mTvMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mTextView = findViewById(R.id.tvMac);


        PERMISSIONS = new String[]{Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE, Permission.READ_PHONE_STATE};
       /* setLeftTitleName("左边标题");
        setMiddleTitleName("中间标题");

        mTvTitleMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingPop(view);
            }
        });
*/
        LogUtils.e(TAG, "onCreate: ");

        hasPermission();

    }

    private String INFO() {
        //BOARD 主板
        String phoneInfo = "BOARD: " + android.os.Build.BOARD + "\n";
        //android.os.Build.VERSION.RELEASE：获取系统版本字符串。如4.1.2 或2.2 或2.3等
        phoneInfo += ", BOOTLOADER: " + android.os.Build.BOOTLOADER + "\n";
        //BRAND 运营商
        phoneInfo += ", BRAND: " + android.os.Build.BRAND + "\n";
        phoneInfo += ", CPU_ABI: " + android.os.Build.CPU_ABI + "\n";
        phoneInfo += ", CPU_ABI2: " + android.os.Build.CPU_ABI2 + "\n";

        //DEVICE 驱动
        phoneInfo += ", DEVICE: " + android.os.Build.DEVICE + "\n";
        //DISPLAY Rom的名字 例如 Flyme 1.1.2（魅族rom） &nbsp;JWR66V（Android nexus系列原生4.3rom）
        phoneInfo += ", DISPLAY: " + android.os.Build.DISPLAY + "\n";
        //指纹
        phoneInfo += ", FINGERPRINT: " + android.os.Build.FINGERPRINT + "\n";
        //HARDWARE 硬件
        phoneInfo += ", HARDWARE: " + android.os.Build.HARDWARE + "\n";
        phoneInfo += ", HOST: " + android.os.Build.HOST + "\n";
        phoneInfo += ", ID: " + android.os.Build.ID + "\n";
        //MANUFACTURER 生产厂家
        phoneInfo += ", MANUFACTURER: " + android.os.Build.MANUFACTURER + "\n";
        //MODEL 机型
        phoneInfo += ", MODEL: " + android.os.Build.MODEL + "\n";
        phoneInfo += ", PRODUCT: " + android.os.Build.PRODUCT + "\n";
        phoneInfo += ", RADIO: " + android.os.Build.RADIO + "\n";
        phoneInfo += ", RADITAGSO: " + android.os.Build.TAGS + "\n";
        phoneInfo += ", TIME: " + android.os.Build.TIME + "\n";
        phoneInfo += ", TYPE: " + android.os.Build.TYPE + "\n";
        phoneInfo += ", USER: " + android.os.Build.USER + "\n";
        //VERSION.RELEASE 固件版本
        phoneInfo += ", VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE + "\n";
        phoneInfo += ", VERSION.CODENAME: " + android.os.Build.VERSION.CODENAME + "\n";
        //VERSION.INCREMENTAL 基带版本
        phoneInfo += ", VERSION.INCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL + "\n";
        //VERSION.SDK SDK版本
        phoneInfo += ", VERSION.SDK: " + android.os.Build.VERSION.SDK + "\n";
        phoneInfo += ", VERSION.SDK_INT: " + android.os.Build.VERSION.SDK_INT + "\n";

        return phoneInfo;
    }

    /**
     * 获取设备HardwareAddress地址
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = "+";
        NetworkInterface iF = null;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress += bytesToString(iF.getHardwareAddress()) + "+";
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hardWareAddress;
    }

    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }


    public void showInfo(View view) {
        mTextView.setText(DeviceUtil.getMacAddress(mContext) + "\n"
                + DeviceUtil.getMacFromWlan0() + "\n"
                + DeviceUtil.getMacFromCatOrder() + "\n"
                + DeviceUtil.getMachineHardwareAddress() + "\n"
                + DeviceUtil.getMacFromWifiManager(mContext) + "\n"
                + getMachineHardwareAddress() + "\n"
                + WifiUtil.getBroadcastAddress() + "\n\n"
                + INFO()


        );

        navigationView.removeAllViews();
        TextView mTextView = new TextView(mContext);
        mTextView.setText("asdasdasd");
        navigationView.addView(mTextView);


    }
}
