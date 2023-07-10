package com.mooc.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BitmapTools {
    private static String TAG = "BitmapTools";

    public BitmapTools() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static String image_path;
    public static String filepath;
    public static String filename;


    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        int degree = readPictureDegree(srcPath);


        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        bitmap = rotateBitmap(bitmap, degree);

        if (bitmap != null) {
            return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
        } else {
            return null;
        }
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 500) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    //判断照片角度
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }


    /**
     * 对图片进行二次采样
     * <p/>
     * param pathName  图片地址
     * param reqWidth  要压缩的宽度
     * param reqHeight 要压缩的高度
     * return
     */
    public static Bitmap decodeSampledBitmapFromResource(String pathName,
                                                         int reqWidth, int reqHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
        return bitmap;

    }

    /**
     * 获取图片压缩比例
     * <p/>
     * param options
     * param reqWidth
     * param reqHeight
     * return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        final int height = options.outHeight;
        final int width = options.outWidth;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 判断sdcard是否存在
    public static boolean isExistSDcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获得指定的存储路径：例如Picture
     * <p/>
     * return
     */
    public static String getExternalPath() {
        if (isExistSDcard()) {
//            String externalPath = Environment
//                    .getExternalStorageDirectory() + File.separator
//                    + "photos" + File.separator + "images" + File.separator + "bitmaptools";

            String externalPath = Environment.getExternalStorageDirectory().getPath() + "/junzhizaixian/images/";
            File tempFile = new File(externalPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            return externalPath;
        }
        return "";
    }

    /**
     * 创建图片的存储路径，包含文件名
     * <p/>
     * return
     */
    @SuppressLint("SimpleDateFormat")
    public static String createImageFile(Context context) {
        // 创建图片的名称，以年月日时分秒来命名
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        // String timeStampStr = (timeStamp.replaceAll("-", "")).replaceAll(":",
        // "");
        String imageFileName = null;
        if (isExistSDcard()) {
            imageFileName = getExternalPath() + File.separator + timeStamp
                    + ".jpg";

            filepath = getExternalPath();
            filename = timeStamp + ".jpg";
        } else {
            String FilePath = context.getCacheDir() + "/junzhizaixian";

            imageFileName = FilePath + File.separator + timeStamp + ".jpg";
            filepath = FilePath;
            filename = timeStamp + ".jpg";
        }
        return imageFileName;
    }

    /**
     * 保存图片到指定的目录中
     * <p/>
     * param bitmap
     */
    public static String saveBitmapToSDCard(Context context, Bitmap bitmap) {

        FileOutputStream outputStream = null;
        image_path = createImageFile(context).replaceAll(" ", "");
        try {
            File file = getFilePath(filepath, filename);
            outputStream = new FileOutputStream(file);
            boolean flag = bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    outputStream);
            if (flag) {
                return image_path;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static File getFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + File.separator + fileName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirFile(dir);
    }

    public static void deleteDirFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public static String getDirectoryPath() {
        if (isExistSDcard()) {
//            String externalPath = Environment
//                    .getExternalStorageDirectory() + File.separator
//                    + "photos" + File.separator + "images" + File.separator + "bitmaptools";

            String externalPath = Environment.getExternalStorageDirectory().getPath() + "/junzhizaixian/images/";
            File tempFile = new File(externalPath);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            return externalPath;
        }
        return "";
    }

    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public static Bitmap stringSplitToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = android.util.Base64.decode(string.split(",")[1], android.util.Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将base64编码格式图片转化bitmap
     * @param string
     * @return
     */
    public static Bitmap base64ToBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = android.util.Base64.decode(string, android.util.Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void decoderBase64File(String base64Code, String savePath) throws Exception {
        //byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        byte[] buffer = android.util.Base64.decode(base64Code, android.util.Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }

//    public static void sendBroadcast(Context context, File file) {
//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), file.getName(), null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
//    }


}
