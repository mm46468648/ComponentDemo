/*******************************************************************************
 *
 * Copyright (c)北京慕华信息科技有限公司
 *
 * FileMgr
 *
 * xtcore.utils.FileMgr.java
 * @author: yusheng.li
 * @since: 2014年12月28日
 * @version: 1.0.0
 *
 * @changeLogs:
 *     1.0.0: First created this class.
 *
 ******************************************************************************/
package com.mooc.common.utils;

import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

import com.mooc.common.global.AppGlobals;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class FileMgr {
    private static Application getApplication() {
        return (Application) AppGlobals.INSTANCE.getApplication();
    }

    /**
     * 保存数据到文件,如果文件夹不存在，会自动创建好
     *
     * @param inSDCard 是否保存到SDCard
     * @param dirName  保存到的文件夹名称
     * @param fileName 文件名
     * @param is       输入流
     * @return 写文件出错时返回false，成功时返回true
     */
    public static boolean saveFile(boolean inSDCard, String dirName, String fileName, InputStream is) {
        File outFile = newFile(inSDCard, dirName, fileName);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = is.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取指定目录下的所有log文件名
     *
     * @param dirPath
     * @return
     */
    public static ArrayList<String> logFileNameList(String dirPath) {
        ArrayList<String> logFileName = new ArrayList<String>();
        File mfile = new File(dirPath);
        File[] files = mfile.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsLogFile(file.getPath())) {
                logFileName.add(file.getPath());
            }
        }
        return logFileName;

    }

    /**
     * 获取指定目录下的所有log文件大小
     *
     * @param dirPath
     * @return byte
     */
    public static long getListLogFileSize(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return 0;
        }
        ArrayList<String> logFileName = new ArrayList<String>();
        long size = 0;
        File mfile = new File(dirPath);
        File[] files = mfile.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsLogFile(file.getPath())) {
                size += file.length();
            }
        }
        return size;

    }


    private static boolean checkIsLogFile(String fName) {
        boolean isLogFile = false;

        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("log")) {
            isLogFile = true;
        } else {
            isLogFile = false;
        }
        return isLogFile;

    }

    /**
     * 创建一个文件夹文件对象，创建对象后是否自动在存储设备上创建文件夹由参数createDirIfNeeded指定
     *
     * @param inSDCard          是否在SDCard上创建
     * @param dirName           文件夹名称
     * @param createDirIfNeeded 是否自动在存储设备上创建对应的文件夹
     * @return 文件夹文件对象
     */
    public static File newDir(boolean inSDCard, String dirName, boolean createDirIfNeeded) {
        File dir = null;
        if (inSDCard) {
            dir = new File(Environment.getExternalStorageDirectory() + File.separator
                    + "Android" + File.separator + "data" + File.separator
                    + getApplication().getPackageName() + File.separator + dirName);


        } else {
            dir = new File(getApplication().getFilesDir(), dirName);
        }

        if (createDirIfNeeded && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    public static File newDownLoadImageDir(boolean inSDCard, String dirName, boolean createDirIfNeeded) {
        File dir = null;
        if (inSDCard) {
            dir = new File(Environment.getExternalStorageDirectory() + File.separator + dirName);
        } else {
            dir = new File(getApplication().getFilesDir(), dirName);
        }

        if (createDirIfNeeded && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }


    /**
     * 创建一个文件夹文件对象，创建对象后会自动在存储设备上创建对应的文件夹，如不需要自动创建文件夹请使用
     * newDir(boolean inSDCard, String dirName, boolean createDirIfNeeded)
     * 并将最后一个参数置为false
     *
     * @param inSDCard 是否在SDCard上创建
     * @param dirName  文件夹名称
     * @return 文件夹文件对象
     */
    public static File newDir(boolean inSDCard, String dirName) {
        return newDir(inSDCard, dirName, true);
    }

    /**
     * 创建一个普通文件对象，创建对象后是否自动在存储设备上创建相关的文件夹由参数createDirIfNeeded指定
     *
     * @param inSDCard          是否在SDCard上创建
     * @param dirName           保存到的文件夹名称
     * @param fileName          文件名
     * @param createDirIfNeeded 是否在存储设备上自动创建相关的文件夹
     * @return 文件对象
     */
    public static File newFile(boolean inSDCard, String dirName, String fileName, boolean createDirIfNeeded) {
        return new File(newDir(inSDCard, dirName, createDirIfNeeded), fileName);
    }

    public static File newDownLoadImageFile(boolean inSDCard, String dirName, String fileName, boolean createDirIfNeeded) {
        return new File(newDownLoadImageDir(inSDCard, dirName, createDirIfNeeded), fileName);
    }


    /**
     * 创建一个普通文件对象，创建对象后会自动在存储设备上创建相关的文件夹，如不需要自动创建文件夹请使用
     * newFile(boolean inSDCard, String dirName, String fileName, boolean createDirIfNeeded)
     * 并将最后一个参数置为false
     *
     * @param inSDCard 是否在SDCard上创建
     * @param dirName  保存到的文件夹名称
     * @param fileName 文件名
     * @return 文件对象
     */
    private static File newFile(boolean inSDCard, String dirName, String fileName) {
        return newFile(inSDCard, dirName, fileName, true);
    }

    /**
     * 创建一个普通文件对象，创建对象后会自动在存储设备上创建相关的文件夹，如不需要自动创建文件夹请使用
     * newFile(boolean inSDCard, String dirName, String fileName, boolean createDirIfNeeded)
     * 并将最后一个参数置为false
     *
     * @param dirName  保存到的文件夹名称
     * @param fileName 文件名
     * @return 文件对象
     */
    public static File newFile(String dirName, String fileName) {
        if (sdCardAvailable()) {
            return newFile(true, dirName, fileName, true);
        } else {
            return newFile(false, dirName, fileName, true);
        }
    }

    public static File newDownLoadImgFile(String dirName, String fileName) {
        if (sdCardAvailable()) {
            return newDownLoadImageFile(true, dirName, fileName, true);
        } else {
            return newDownLoadImageFile(false, dirName, fileName, true);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param inSDCard 是否在SDCard的文件
     * @param dirName  保存文件的文件夹名称
     * @param fileName 文件名
     * @return true代表文件存在，false为不存在
     */
    public static boolean exists(boolean inSDCard, String dirName, String fileName) {
        File file = newFile(inSDCard, dirName, fileName, false);
        return file.exists();
    }

    /**
     * 判断文件夹是否存在
     *
     * @param inSDCard 是否在SDCard的文件夹
     * @param dirName  文件夹名称
     * @return true代表文件夹存在，false为不存在
     */
    public static boolean exists(boolean inSDCard, String dirName) {
        File dir = newDir(inSDCard, dirName, false);
        return dir.exists();
    }

    /**
     * 删除存储设备上的文件
     *
     * @param inSDCard 文件是否在SDCard
     * @param dirName  文件存储的文件夹
     * @param fileName 文件名
     * @return true代表文件删除成功，false为删除失败
     */
    public static boolean deleteFile(boolean inSDCard, String dirName, String fileName) {
        File file = newFile(inSDCard, dirName, fileName, false);
        if (file != null && file.exists()) {
            return file.delete();
        } else {
            return true;
        }
    }

    /**
     * 删除存储设备上的文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        } else {
            return true;
        }
    }

    /**
     * 删除指定的文件夹中的文件，满足filter条件的所有文件将被删除
     *
     * @param inSDCard 文件是否在SDCard
     * @param dirName  文件夹名称
     * @param filter   文件过滤条件，filter.accept(File file)接口返回true的文件将被删除
     */
    public static void deleteFiles(boolean inSDCard, String dirName, FileFilter filter) {
        File dir = newDir(inSDCard, dirName, false);
        if (dir.exists()) {
            if (filter != null) {
                for (File file : dir.listFiles()) {
                    if (filter.accept(file)) {
                        file.delete();
                    }
                }
            } else {
                for (File file : dir.listFiles()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 删除指定的文件夹中的文件，满足filter条件的所有文件将被删除
     *
     * @param dirName 文件夹名称
     */
    public static void deleteFiles(String dirName) {
        File dir = new File(dirName);
        if (dir.exists()) {

            for (File file : dir.listFiles()) {
                file.delete();
            }

        }
    }


    /**
     * 删除指定的文件夹中的所有文件
     *
     * @param inSDCard 文件是否在SDCard
     * @param dirName  文件夹名称
     */
    public static void deleteFiles(boolean inSDCard, String dirName) {
        deleteFiles(inSDCard, dirName, null);
    }

    /**
     * 删除文件夹中的所有文件，并将此文件夹也删除，如只需要删除文件夹中的文件，请使用deleteFiles
     *
     * @param inSDCard 文件是否在SDCard
     * @param dirName  文件夹名称
     */
    public static void deleteDir(boolean inSDCard, String dirName) {
        File dir = newDir(inSDCard, dirName, false);
        if (dir.exists()) {
            deleteFiles(inSDCard, dirName);
            dir.delete();
        }
    }

    // 递归
    public static long getFileSize(File f)// 取得文件夹大小
    {
        if(f == null) return 0;
        long size = 0;
        File files[] = f.listFiles();

        if(files == null) return 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                size = size + getFileSize(files[i]);
            } else {
                size = size + files[i].length();
            }
        }
        return size;
    }

    public static String FormetFileSize(long fileS) {// 转换文件大小

        DecimalFormat df = new DecimalFormat("#0.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * 删除指定目录下文件及目录
     *
     * @param filePath
     * @param deleteThisPath
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {

        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFolderFile(files[i].getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {// 如果是文件，删除
                    file.delete();
                } else {// 目录
                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * @Description: 删除除strFileName外的所有文件
     */
    public static boolean delAllFile(String dirPath, String strFileName) {
        boolean flag = false;
        File file = new File(dirPath);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        File[] tempList = file.listFiles();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            temp = tempList[i];
            if (!TextUtils.isEmpty(strFileName)) {
                if (!temp.getName().equals(strFileName)) {
                    temp.delete();
                }
            }
        }
        return flag;
    }

    /**
     * 判断sdcard是否可用
     *
     * @return
     */
    public static boolean sdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
