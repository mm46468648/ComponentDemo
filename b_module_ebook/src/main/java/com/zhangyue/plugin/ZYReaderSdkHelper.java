package com.zhangyue.plugin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ireader.plug.api.ZYReaderPluginApi;
import com.mooc.common.global.AppGlobals;
import com.mooc.common.utils.DebugUtil;


/**
 * 掌阅SDK操作帮助类
 *
 * @author jdxu
 * @date 2019-09-12 16:39
 */
public class ZYReaderSdkHelper {
    /**
     * 协议头是 zyreaderplugin，host是宿主的包名
     */
    private static final String SCHEME_BASE_PRE = "zyreaderplugin://" + AppGlobals.INSTANCE.getApplication().getPackageName();
    /**
     * 跳转"书籍目录页"协议前缀
     */
    private static final String SCHEME_BOOK_CATALOG_PRE = SCHEME_BASE_PRE + "/bookcatalog?bookid=";
    /**
     * 跳转"阅读页"协议前缀
     */
    private static final String SCHEME_BOOK_READING_PRE = SCHEME_BASE_PRE + "/reading?bookid=";
    /**
     * 删除书籍离线数据协议前缀
     */
    private static final String SCHEME_BOOK_DEL_PRE = SCHEME_BASE_PRE + "/bookdel?bookids=";

    /**
     * SDK可配置信息，由宿主根据需要调用相应方法
     * 建议在 {@link Application#onCreate() 方法的最后调用}
     */
    public static void initSdk(Context context) {
        ZYReaderPluginApi.setDebugMode(DebugUtil.debugMode);
        // 若调用该方法，则宿主冷启动后第一次进入sdk也不会出现启动页，且能更快打开sdk页面
        ZYReaderPluginApi.acceleratePlugin(context);
        // 若调用该方法，会后台提前安装sdk，在第一次进入掌阅sdk时不会出现"安装中"弹窗
        ZYReaderPluginApi.installPluginPre(context);

    }

    /**
     * 进入SDK：指定的书籍目录页
     *
     * @param context
     * @param bookId
     */
    public static void enterBookCatalog(Context context, int bookId) {
        openURLByBrowser(context, SCHEME_BOOK_CATALOG_PRE + bookId);
    }

    /**
     * 进入SDK：指定的书籍阅读页
     *
     * @param context
     * @param bookId
     */
    public static void enterBookReading(Context context, String bookId) {
        openURLByBrowser(context, SCHEME_BOOK_READING_PRE + bookId);
    }

    /**
     * 进入SDK：删除指定书籍的离线数据
     *
     * @param activity
     * @param bookIds  多个bookId以,分割
     */
    public static void deleteBooks(Activity activity, String bookIds, int requestCode) {
        openURLByBrowser(activity, SCHEME_BOOK_DEL_PRE + bookIds, requestCode);
    }

    /**
     * 使用系统浏览器打开一个URL
     *
     * @param url
     */
    private static void openURLByBrowser(Context context, String url) {
//        try {
//            Uri uri = Uri.parse(url);
//            Intent it = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(it);
//        } catch (Exception e) { // Android手机种类繁多，Rom可定制，导致一些机型上面就是没有这些功能,需要捕获异常
//            e.printStackTrace();
//        }
        ZYReaderPluginApi.openUrl(context,url);
    }

    /**
     * 使用系统浏览器打开一个URL
     *
     * @param url
     */
    private static void openURLByBrowser(Activity activity, String url, int requestCode) {
//        try {
//            Uri uri = Uri.parse(url);
//            Intent it = new Intent(Intent.ACTION_VIEW, uri);
//            activity.startActivityForResult(it, requestCode);
//        } catch (Exception e) { // Android手机种类繁多，Rom可定制，导致一些机型上面就是没有这些功能,需要捕获异常
//            e.printStackTrace();
//        }
        ZYReaderPluginApi.openUrl(activity,url,requestCode);
    }

}
