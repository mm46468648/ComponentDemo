package com.mooc.commonbusiness.manager;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.mooc.commonbusiness.config.DownloadConfig;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashManager implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    // CrashHandler实例
    private static CrashManager instance;
    // 程序的Context对象
    private Application application;
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashManager(Context context) {
        application = (Application) context.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashManager getInstance(Context context) {
        CrashManager inst = instance;
        if (inst == null) {
            synchronized (CrashManager.class) {
                inst = instance;
                if (inst == null) {
                    inst = new CrashManager(context.getApplicationContext());
                    instance = inst;
                }
            }
        }
        return inst;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        TaskManager.getInstance(application).saveErrorLog(ex);
//        CrashReport.setUserId(GlobalsUserManager.INSTANCE.getUid());
//        CrashReport.postCatchedException(ex);

        if(ex!=null){
            handleException(ex);
        }

        mDefaultHandler.uncaughtException(thread, ex);
        // 退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public boolean handleException(Throwable ex) {
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(application, "很抱歉,程序出现异常,即将退出", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        // 收集设备参数信息
        Map<String, String> stringStringMap = collectDeviceInfo(application);
        // 保存日志文件
        saveCrashInfo2File(ex,stringStringMap);
//        CrashReport.setUserId(GlobalsUserManager.INSTANCE.getUid());
//        CrashReport.postCatchedException(ex);
        return true;
    }


    public Map<String,String> collectDeviceInfo(Context context) {
        Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息

        try {
            PackageManager pm = context.getPackageManager();// 获得包管理器
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();// 反射机制
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
                Log.d(TAG, field.getName() + ":" + field.get(""));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return info;
    }

    private String saveCrashInfo2File(Throwable ex,Map<String, String> info) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String result = writer.toString();
        sb.append(result);
        // 保存文件
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String fileName = "crash-" + time + "-" + timetamp + ".txt";
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(DownloadConfig.INSTANCE.getErrorLogLoccation());
                Log.i("CrashHandler", dir.toString());
                if (!dir.exists())
                    dir.mkdir();
                FileOutputStream fos = new FileOutputStream(new File(dir,
                        fileName));
                fos.write(sb.toString().getBytes());
                fos.close();
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
