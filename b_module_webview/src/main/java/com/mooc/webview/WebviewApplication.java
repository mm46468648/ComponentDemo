package com.mooc.webview;

import com.mooc.common.utils.FileUtils;
import com.mooc.common.utils.sharepreference.SpDefaultUtils;
import com.mooc.commonbusiness.base.BaseApplication;
import com.mooc.common.global.AppGlobals;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.commonbusiness.constants.SpConstants;
import com.mooc.commonbusiness.model.GlobalConfig;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.WebView;

import java.util.HashMap;

import static com.mooc.common.ktextends.AnyExtentionKt.loge;

import android.app.Application;
import android.util.Log;
import android.widget.LinearLayout;

public class WebviewApplication extends BaseApplication {

    private static String TAG="WebviewApplication";
    //x5内核是否初始化完毕，（x5内核是动态下发，首次加载需要先异步下载到本地，立即使用不会立即生效）
    public static Boolean x5InitFinish = false;

    @Override
    public void init() {



//        loge("WebviewApplication","init");
        boolean hasAgree = SpDefaultUtils.getInstance().getBoolean(SpConstants.HAS_AGREE_MENT, false);
        if (!hasAgree) return;

        //设置非wifi条件下允许下载X5内核
//        QbSdk.setDownloadWithoutWifi(true);

        //设置开启优化方案
//        HashMap<String,Object> map = new HashMap();
//        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
//        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
//        map.put(TbsCoreSettings.TBS_SETTINGS_USE_PRIVATE_CLASSLOADER, true);
//        QbSdk.initTbsSettings(map);

        //再对webview进行初始化
//        QbSdk.initX5Environment(AppGlobals.INSTANCE.getApplication(), new QbSdk.PreInitCallback() {
//            @Override
//            public void onCoreInitFinished() {
//
//            }
//
//            @Override
//            public void onViewInitFinished(boolean b) {
//                x5InitFinish = b;
////                AnyExtentionKt.loge(this,"x5内核初始化" + x5InitFinish);
//            }
//        });

//        QbSdk.setTbsListener(new TbsListener() {
//            @Override
//            public void onDownloadFinish(int i) {
////                AnyExtentionKt.loge(this,"onDownloadFinish" + i);
//
//            }
//
//            @Override
//            public void onInstallFinish(int i) {
////                AnyExtentionKt.loge(this,"onInstallFinish" + i);
//            }
//
//            @Override
//            public void onDownloadProgress(int i) {
////                AnyExtentionKt.loge(this,"onDownloadProgress" + i);
//
//            }
//        });


    }

    private void initX5WithAssets(){
        //       Application application= Companion.getInstance();
//        boolean canLoadX5 = QbSdk.canLoadX5(application);
//        Log.e(TAG, "canLoadX5: " + canLoadX5+"|TbsVersion:"+QbSdk.getTbsVersion(application));
//        if (canLoadX5) {
//            x5InitFinish = true;
////            view.loadUrl("http://10.0.1.40:4201/");
//            return;
//        }
//        QbSdk.reset(application);
//        QbSdk.setTbsListener(new TbsListener() {
//            @Override
//            public void onDownloadFinish(int i) {
//
//            }
//
//            @Override
//            public void onInstallFinish(int i) {
//                Log.e(TAG, "onInstallFinish: " + i);
//                int tbsVersion = QbSdk.getTbsVersion(application);
//                Log.e(TAG, "tbsVersion: " + tbsVersion);
//                x5InitFinish = true;
//
//            }
//
//            @Override
//            public void onDownloadProgress(int i) {
//
//            }
//        });
////        QbSdk.installLocalTbsCore(application, 45947, this.getExternalFilesDir("x5").getPath() + "/x5.tbs.apk");
//        QbSdk.reset(application);
//        QbSdk.installLocalTbsCore(application, 46007, FileUtils.getTBSFileDir(application).getPath() + "/046007_x5.tbs.apk");
    }

}
