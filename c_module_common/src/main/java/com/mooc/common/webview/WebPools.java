package com.mooc.common.webview;

import android.app.Activity;
import android.content.MutableContextWrapper;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.mooc.common.ktextends.AnyExtentionKt.logi;

public class WebPools {


    private final Queue<WebView> mWebViews;

    private Object lock = new Object();
    private static WebPools mWebPools = null;

    private static final AtomicReference<WebPools> mAtomicReference = new AtomicReference<>();
    private static final String TAG=WebPools.class.getSimpleName();

    private WebPools() {
        mWebViews = new LinkedBlockingQueue<>();
    }
    public static WebPools getInstance() {

        for (; ; ) {
            if (mWebPools != null)
                return mWebPools;
            if (mAtomicReference.compareAndSet(null, new WebPools()))
                return mWebPools=mAtomicReference.get();

        }
    }
    public void recycle(WebView webView) {
        recycleInternal(webView);
    }
    public WebView acquireWebView(Activity activity) {
        return acquireWebViewInternal(activity);
    }
    private WebView acquireWebViewInternal(Activity activity) {
        WebView mWebView = mWebViews.poll();
        logi(TAG,"acquireWebViewInternal  webview:"+mWebView);
        if (mWebView == null) {
            synchronized (lock) {
                return new WebView(new MutableContextWrapper(activity));
            }
        } else {
            MutableContextWrapper mMutableContextWrapper = (MutableContextWrapper) mWebView.getContext();
            mMutableContextWrapper.setBaseContext(activity);
            return mWebView;
        }
    }



    private void recycleInternal(WebView webView) {
        try {
            if (webView.getContext() instanceof MutableContextWrapper) {
                MutableContextWrapper mContext = (MutableContextWrapper) webView.getContext();
                mContext.setBaseContext(mContext.getApplicationContext());
                logi(TAG,"enqueue  webview:"+webView);

                //清除缓存
//                webView.clearCache(true);
//                webView.clearHistory();
                //避免缓存上一页渲染的视图，load一个空白页
                webView.loadUrl("about:blank");
                if(webView.getParent()!=null){ //从父布局中移除
                    ((ViewGroup)webView.getParent()).removeView(webView);
                }
                mWebViews.offer(webView);
            }
            if(webView.getContext() instanceof  Activity){
//            throw new RuntimeException("leaked");
                logi(TAG,"Abandon this webview  ， It will cause leak if enqueue !");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}