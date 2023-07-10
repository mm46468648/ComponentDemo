package com.mooc.common.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.mooc.common.global.AppGlobals;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;


/**
 * 获取手机系统信息类
 *
 * @author liyusheng
 */
public class SystemUtils {
    static Context context = AppGlobals.INSTANCE.getApplication();

    /**
     * 获取android系统版本号
     */
    public static String getOSVersion() {
        String release = Build.VERSION.RELEASE; // android系统版本号
        release = "android" + release;
        return release;
    }

    /**
     * 获得android系统sdk版本号
     */
    public static String getOSVersionSDK() {
        return Build.VERSION.SDK;
    }

    /**
     * 获得android系统sdk版本号
     */
    public static int getOSVersionSDKINT() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机型号
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 品牌
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }


    public boolean checkApplication(Context mContext, String packageName) {

        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packageName, PackageManager
                    .GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取 Channel
     */
    public static String getChannel(Context context) {

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);

            Bundle bundle = ai.metaData;
            if (bundle != null) {
                return bundle.getString("APP_CHANNEL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Moocnd";
    }

    /**
     * 获取设备的IMEI
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI() {

        if (null == context) {
            return null;
        }
        String imei = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        } catch (Exception e) {
        }
        return imei;
    }

    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    /**
     * 检测手机是否已插入SIM卡
     */
    public static boolean isCheckSimCardAvailable() {

        if (null == context) {
            return false;
        }
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /**
     * sim卡是否可读
     */
    public static boolean isCanUseSim() {

        if (null == context) {
            return false;
        }
        try {
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 取得当前sim手机卡的imsi
     */
    @SuppressLint("MissingPermission")
    public static String getIMSI() {

        if (null == context) {
            return null;
        }
        String imsi = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();
        } catch (Exception e) {
            return "";
        }
        return imsi;
    }

    /**
     * 取得当前sim手机卡的运营商
     */
    public static String getOperatorName() {

        if (null == context) {
            return null;
        }
        String operator = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            operator = tm.getSimOperatorName();
        } catch (Exception e) {
        }
        return operator;
    }

    /**
     * 返回本地手机号码，这个号码不一定能获取到
     */
    @SuppressLint("MissingPermission")
    public static String getNativePhoneNumber() {

        if (null == context) {
            return null;
        }
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String NativePhoneNumber = null;
        NativePhoneNumber = telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }

    /**
     * 返回手机服务商名字
     */
    public static String getProvidersName() {
        String ProvidersName = null;
        // 返回唯一的用户ID;就是这张卡的编号神马的
        String IMSI = getIMSI();
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = "中国移动";
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = "中国联通";
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = "中国电信";
        } else if (TextUtils.isEmpty(IMSI)) {
            ProvidersName = "";
        } else {
            ProvidersName = "其他服务商:" + IMSI;
        }
        return ProvidersName;
    }

    /**
     * 获取当前设备的SN
     */
    @SuppressLint("MissingPermission")
    public static String getSimSN() {

        if (null == context) {
            return null;
        }
        String simSN = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            simSN = tm.getSimSerialNumber();
        } catch (Exception e) {
        }
        return simSN;
    }


    /**
     * 获取当前设备的MAC地址
     */
    public static String getMacAddress() {

        if (null == context) {
            return null;
        }
        String mac = null;
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            mac = info.getMacAddress();
        } catch (Exception e) {
        }
        return mac;
    }

    /**
     * 获得设备ip地址
     */
    public static String getLocalAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
        }
        return null;
    }

    public static String getScreenOritatiemPortrait(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "portrait’";
        } else {
            return "landscape";
        }

    }

    // 获取CPU最大频率（单位KHZ）
    // "/system/bin/cat" 命令行
    // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径

    public static String getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    // 获取CPU最小频率（单位KHZ）
    public static String getMinCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        return result.trim();
    }

    // 实时获取CPU当前频率（单位KHZ）
    public static String getCurCpuFreq() {
        String result = "N/A";
        try {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取CPU名字
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备信息
     */
    public static String[] getDivceInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }

    /**
     * 判断手机CPU是否支持NEON指令集
     */
    public static boolean isNEON() {
        boolean isNEON = false;
        String cupinfo = getCPUInfos();
        if (cupinfo != null) {
            cupinfo = cupinfo.toLowerCase();
            isNEON = cupinfo != null && cupinfo.contains("neon");
        }
        return isNEON;
    }

    /**
     * 读取CPU信息文件，获取CPU信息
     */
    @SuppressWarnings("resource")
    private static String getCPUInfos() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        StringBuilder resusl = new StringBuilder();
        String resualStr = null;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                resusl.append(str2);
                // String cup = str2;
            }
            if (resusl != null) {
                resualStr = resusl.toString();
                return resualStr;
            }
        } catch (IOException e) {
        }
        return resualStr;
    }

    /**
     * 获取当前设备cpu的型号
     */
    public static int getCPUModel() {
        return matchABI(getSystemProperty("ro.product.cpu.abi")) | matchABI(getSystemProperty("ro.product.cpu.abi2"));
    }

    /**
     * 匹配当前设备的cpu型号
     */
    private static int matchABI(String abiString) {
        if (TextUtils.isEmpty(abiString)) {
            return 0;
        }
        if ("armeabi".equals(abiString)) {
            return 1;
        } else if ("armeabi-v7a".equals(abiString)) {
            return 2;
        } else if ("x86".equals(abiString)) {
            return 4;
        } else if ("mips".equals(abiString)) {
            return 8;
        }
        return 0;
    }

    /**
     * 获取CPU核心数
     */
    public static int getCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * 获取Rom版本
     */
    public static String getRomversion() {
        String rom = "";
        try {
            String modversion = getSystemProperty("ro.modversion");
            String displayId = getSystemProperty("ro.build.display.id");
            if (modversion != null && !modversion.equals("")) {
                rom = modversion;
            }
            if (displayId != null && !displayId.equals("")) {
                rom = displayId;
            }
        } catch (Exception e) {
        }
        return rom;
    }

    /**
     * 获取系统配置参数
     */
    public static String getSystemProperty(String key) {
        String pValue = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method m = c.getMethod("get", String.class);
            pValue = m.invoke(null, key).toString();
        } catch (Exception e) {
        }
        return pValue;
    }

    /**
     * 获取系统中的Library包
     */
    public static List<String> getSystemLibs() {

        if (null == context) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        String[] libNames = pm.getSystemSharedLibraryNames();
        List<String> listLibNames = Arrays.asList(libNames);
        return listLibNames;
    }


    public static String getKernelVersion() {
        String kernelVersion = "";
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/proc/version");
        } catch (Exception e) {
            e.printStackTrace();
            return kernelVersion;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8 * 1024);
        String info = "";
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                info += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (info != "") {
                final String keyword = "version ";
                int index = info.indexOf(keyword);
                line = info.substring(index + keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return kernelVersion;
    }


    /**
     * 获取单个应用最大分配内存，单位为byte
     */
    public static long getOneAppMaxMemory() {

        if (context == null) {
            return -1;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getMemoryClass() * 1024 * 1024;
    }

    /**
     * 获取指定本应用占用的内存，单位为byte
     */
    public static long getUsedMemory() {
        return getUsedMemory(null);
    }

    /**
     * 获取指定包名应用占用的内存，单位为byte
     */
    public static long getUsedMemory(String packageName) {

        if (context == null) {
            return -1;
        }
        if (TextUtils.isEmpty(packageName)) {
            packageName = context.getPackageName();
        }
        long size = 0;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runapps = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runapp : runapps) { // 遍历运行中的程序
            if (packageName.equals(runapp.processName)) {// 得到程序进程名，进程名一般就是包名，但有些程序的进程名并不对应一个包名
                // 返回指定PID程序的内存信息，可以传递多个PID，返回的也是数组型的信息
                Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runapp.pid});
                // 得到内存信息中已使用的内存，单位是K
                size = processMemoryInfo[0].getTotalPrivateDirty() * 1024;
            }
        }
        return size;
    }


    /**
     * 获取手机总内存，单位为byte
     */
    public static long getTotalMemory() {
        long size = 0;
        String path = "/proc/meminfo";// 系统内存信息文件
        try {
            String totalMemory = FileUtils.readProperties(path, "MemTotal", null);// 读出来是带单位kb的，并且单位前有空格，所以去掉最后三位
            if (!TextUtils.isEmpty(totalMemory) && totalMemory.length() > 3) {
                size = Long.valueOf(totalMemory.substring(0, totalMemory.length() - 3)) * 1024;
            }
        } catch (Exception e) {
        }
        return size;
    }

    /**
     * 手机低内存运行阀值，单位为byte
     */
    public static long getThresholdMemory() {

        if (context == null) {
            return -1;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        return info.threshold;
    }

    /**
     * 手机是否处于低内存运行
     */
    public static boolean isLowMemory() {

        if (context == null) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        return info.lowMemory;
    }


    public static String getVersionName() {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return "";
        }
        return info.versionName;

    }

}
