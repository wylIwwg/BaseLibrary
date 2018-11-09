package com.sjjd.wyl.baseandroid.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyl on 2017/11/17.
 */

public class FileUtils {
    //压缩包路径 根目录
    public static String SDCard_ZIP_Path = Environment.getExternalStorageDirectory().getPath();
    //压缩包解压目录
    public static String SDCard_OUT_Path = SDCard_ZIP_Path;


    public static final String TAG = "FileUtils";
    //public static final String APP_PACKAGE_NAME = "cn.com.cnpc.wmh.cnpc";
    //public static final String APP_PACKAGE_NAME = "com.cnpc.portal";
    public static final String APP_PACKAGE_NAME = "com.richfit.qixin.partybuild.product";
    // public static final String APP_PACKAGE_NAME = "com.sjjd.hu.table";


    /**
     * @param context
     */
    public static void launchapp(Context context) {
        // 判断是否安装过App，否则去市场下载

        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(APP_PACKAGE_NAME));

    }

    /**
     * @param context
     */
    public static void launchapp(Context context, String pakeName) {
        // 判断是否安装过App，否则去市场下载
        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(pakeName));

    }

    /**
     * 检测某个应用是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        //获取手机系统的所有APP包名，然后进行一一比较
        List<PackageInfo> pinfo = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            // LogUtils.e(TAG, "isAppInstalled: " + ((PackageInfo) pinfo.get(i)).packageName);
            if (((PackageInfo) pinfo.get(i)).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    public static void isAppInstalled2(Context context, String appLabel) {
        PackageManager packageManager = context.getPackageManager();
        //匹配程序的入口
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        //查询
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
        for (int i = 0; i < resolveInfos.size(); i++) {

            String appName = resolveInfos.get(i).loadLabel(packageManager).toString();
            LogUtils.e(TAG, "isAppInstalled2: " + appName);
            if (appLabel.equals(appName)) {
                String packageName = resolveInfos.get(i).activityInfo.packageName;
                LogUtils.e(TAG, "isAppInstalled2: " + packageName);
                LogUtils.e(TAG, "isAppInstalled2: " + resolveInfos.get(i).activityInfo.name);
            }
        }
    }


    public static Bitmap pathToBitmap(String path) {
        if (path == null || path.length() < 1)
            return null;
        if (!new File(path).exists())
            return null;
        if (new File(path).isDirectory())
            return null;
        return BitmapFactory.decodeFile(path);
    }


    public static void SortFileByName2(List<File> res) {
        Collections.sort(res, new Comparator<File>() {
            @Override
            public int compare(File p1, File p2) {
                String path1 = p1.getPath();
                String path2 = p2.getPath();
                String name1 = path1.substring(path1.lastIndexOf("/") + 1, path1.lastIndexOf("."));
                String name2 = path2.substring(path2.lastIndexOf("/") + 1, path2.lastIndexOf("."));
                // String name1 = "ABC-1234";
                // String name2 = "ABC-9090";
                //获取到文件名字 如果两文件名都是纯数字
                if (Pattern.matches("^[0-9]+", name1) && Pattern.matches("^[0-9]+", name1)) {
                    LogUtils.e(TAG, "compare: 纯数字");
                    return Integer.parseInt(name1) - Integer.parseInt(name2);
                }
                //名称包含数字和其他字符 比如 ABC-123
                if (name1.matches("-(?=[0-9])") && name2.matches("-(?=[0-9])")) {
                    //截取数字部分
                    LogUtils.e(TAG, "compare: 包含数字");
                    Pattern p = Pattern.compile("\\d+");
                    Matcher m = p.matcher(name1);
                    if (m.find()) {
                        LogUtils.e(TAG, "compare: " + m.group());
                    }
                }
                return 0;
            }
        });
    }

    /**
     * 判断文件是否是图片
     *
     * @param path
     * @return
     */
    public static boolean isImageResource(String path) {
        String dest = path.toLowerCase();
        if (dest.endsWith("jpg") || dest.endsWith("png") || dest.endsWith("jpeg") || dest.endsWith("bmp")) {
            return true;
        }
        return false;

    }

    public static boolean isImageResource(File file) {
        return isImageResource(file.getAbsolutePath());

    }


    public static String readJson(String path) {
        String json = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(new FileInputStream(new File(path)));
            br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            json = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return json;
    }

    /**
     * 删除指定文件或文件夹
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file.isFile()) {
            return file.delete();
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return true;
            }

            for (File childFile : childFiles) {
                deleteFile(childFile);
            }

            return file.delete();
        }
        return false;
    }

    public static boolean deleteFile(String filePath) {
        LogUtils.e(TAG, "deleteFile: " + filePath);
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        LogUtils.e(TAG, "deleteDirectory: " + filePath);
        //如果 filePath 不以文件夹分隔符结尾，自动添加文件分隔符

        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }

        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }

        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件（包括子目录）
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());

                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());

                if (!flag) break;
            }
        }
        if (!flag) return false;

        return dirFile.delete();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或者文件
     * @return
     */
    public static boolean DeleteFolder(String filePath) {

        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                //为文件时调用删除文件的方法
                return deleteFile(filePath);
            } else {
                //为目录是调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }


    public static String isUExist(Context context) {
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (manager == null)
            return "";

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();
            int deviceClass = usbDevice.getDeviceClass();
            if (deviceClass == 0) {
                UsbInterface anInterface = usbDevice.getInterface(0);
                int interfaceClass = anInterface.getInterfaceClass();
                //http://blog.csdn.net/u013686019/article/details/50409421
                //http://www.usb.org/developers/defined_class/#BaseClassFFh
                //通过下面的InterfaceClass 来判断到底是哪一种的，例如7就是打印机，8就是usb的U盘
                if (anInterface.getInterfaceClass() == 8) {
                    LogUtils.e(TAG, "isUExist: " + usbDevice.getDeviceName() + " " + anInterface.toString());
                    return getUPath();
                }
            }
        }
        return null;
    }

    public static String getUPath() {
        try {
            // 运行mount命令，获取命令的输出，得到系统中挂载的所有目录
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                // 将常见的linux分区过滤掉
                if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
                    continue;
                }
                // 下面这些分区是我们需要的
                if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))) {
                    // 将mount命令获取的列表分割，items[0]为设备名，items[1]为挂载路径
                    String items[] = line.split(" ");
                    if (items.length > 0) {
                        String path = items[1];//.toLowerCase(Locale.getDefault());
                        // 添加一些判断，确保是sd卡，如果是otg等挂载方式，可以具体分析并添加判断条件
                        //   LogUtils.e(TAG, "getUPath: " + items[0] + "   " + items[1]+ "   " + items[2]+ "   " + items[3]+ "   " + items[4]+ "   " + items[5]);
                        //  Rk3188   /storage/361A-28B5
                        //  Q-E10   /mnt/usb_storage
                        if (path.contains("usb_storage") || (path.contains("storage") && !path.contains("emulated"))) {
                            return path;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String bytes2mb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }


    public static boolean RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        Log.e("*** DEBUG ***", "Root SUC ");
        return true;
    }

    /**
     * 根据wifi信息获取本地mac
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        String macSerial = null;
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }

        return macSerial==null?"":macSerial.toUpperCase();
    }

    public static void restartApplication(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}



