package com.sjjd.wyl.baseandroid;

/**
 * Created by wyl on 2018/11/13.
 */
public class Skills {
    //通过WiFiManager获取mac地址
  /*
    private static String tryGetWifiMac(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wi = wm.getConnectionInfo();
        if (wi == null || wi.getMacAddress() == null) {
            return "";
        }
        if ("02:00:00:00:00:00".equals(wi.getMacAddress().trim())) {
            return "";
        } else {
            return wi.getMacAddress().trim();
        }
    }
  */

    //根据资源名称获取资源id
   /*
    public int getResource(String imageName) {
        Context ctx = getBaseContext();
        int resId = getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }
    */


    //去除中文和空格换行
  /*
     url.replaceAll("[\u4e00-\u9fa5]","")
        .replaceAll(" ","")
        .replaceAll("\n","");

  */

    //指定编译版本 放在build文件顶部即可
    /*
    configurations.all{
        resolutionStrategy.eachDependency {
        DependencyResolveDetails details ->
        def requested = details.requested
        if (requested.group == 'com.android.support') {
            if (!requested.name.startsWith("multidex")) {
                details.useVersion '27.1.1'
            }
        }
      }
    }
    */

    //查看项目依赖
    //  ./gradlew -q app:dependences


}
