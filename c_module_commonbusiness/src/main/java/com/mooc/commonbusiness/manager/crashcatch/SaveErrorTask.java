package com.mooc.commonbusiness.manager.crashcatch;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.mooc.common.utils.FileUtils;
import com.mooc.common.utils.SystemUtils;
import com.mooc.commonbusiness.config.DownloadConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveErrorTask implements Runnable {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    private Context context;
    private Throwable ex;

    public SaveErrorTask(Context context, Throwable ex) {
//        setPriority(TaskPriority.UI_LOW);
        this.context = context;
        this.ex = ex;
    }


    @Override
    public void run() {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        String time = formatter.format(new Date());
        String fileName = time + ".txt";
        StringBuilder stringBuffer = new StringBuilder();
//        DeviceInfo deviceInfo = Utils.getDeviceInfo(context);
        stringBuffer.append("\nsdkVersion：" + SystemUtils.getVersionName());
        stringBuffer.append("\nmodel：" + SystemUtils.getDeviceModel());
        stringBuffer.append("\nversion" + SystemUtils.getVersionName());
        stringBuffer.append("\nerrorStr：" + result);
        stringBuffer.append("\ntime：" + time);
        String filePath = DownloadConfig.INSTANCE.getErrorLogLoccation();
//        CacheFileUtils.saveErrorStr(filePath, stringBuffer.toString());
        FileOutputStream fos = null;
        try {
            File dir = new File(filePath);
            Log.i("CrashHandler", dir.toString());
            if (!dir.exists())
                dir.mkdirs();
            fos= new FileOutputStream(new File(dir, fileName));
            fos.write(stringBuffer.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
