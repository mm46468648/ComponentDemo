package com.mooc.commonbusiness.manager;

import android.content.Context;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.mooc.common.utils.aesencry.AesEncryptUtil;
import com.mooc.commonbusiness.base.BaseRepository;
import com.mooc.commonbusiness.dialog.CustomProgressDialog;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.net.EncryptParseData;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

import static com.mooc.common.ktextends.AnyExtentionKt.runOnMain;
import static com.mooc.common.ktextends.AnyExtentionKt.toast;

/**
 * Base Observer 的封装处理
 * <p>
 * 注意内存泄漏：https://github.com/trello/RxLifecycle/tree/2.x
 * <p>
 * Created by zenglb on 2017/4/14.
 */
public abstract class BaseObserver<T> implements Observer<T>, LifecycleObserver {
    private final String TAG = BaseObserver.class.getSimpleName();
    public final static String Thread_Main = "main";


    public static final int CODE_NO_TOAST = -267;
    public static final int CODE_HAS_TOAST = -277;

    private final int RESPONSE_CODE_OK = 0;       //自定义的业务逻辑，成功返回积极数据
    private final int RESPONSE_FATAL_EOR = -1;  //返回数据失败,严重的错误

    private final Context mContext;
    private final Gson gson = new Gson();

    private int errorCode = -1111;
    private String errorMsg = "未知的错误！";

    private String responseStr = "";

    private Disposable disposable;

    private CustomProgressDialog dialog;

    /**
     * 根据具体的Api 业务逻辑去重写 onSuccess 方法！Error 是选择重写，but 必须Super ！
     *
     * @param t
     */
    public abstract void onSuccess(T t);


    /**
     * @param mCtx
     */
    public BaseObserver(Context mCtx) {
//        this.mContext = mCtx;
        this(mCtx, false);
    }

    /**
     * @param mCtx
     * @param showProgress 默认需要显示进程，不要的话请传 false
     */
    public BaseObserver(Context mCtx, boolean showProgress) {
        this.mContext = mCtx;
        if (showProgress) {
            dialog = CustomProgressDialog.Companion.createLoadingDialog(mContext, true);
            dialog.show();
        }

        //注册观察Actiivty生命周期，如果页面销毁，取消事件流
        if (mContext instanceof LifecycleOwner) {
            runOnMain(this, new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    LifecycleOwner activity = (LifecycleOwner) mContext;
                    activity.getLifecycle().addObserver(BaseObserver.this);
                    return null;
                }
            });
//            LifecycleOwner activity = (LifecycleOwner) mContext;
//            activity.getLifecycle().addObserver(this);
        }


    }

    SwipeRefreshLayout swipeRefreshLayout;

    public BaseObserver(Context mCtx, SwipeRefreshLayout swipeRefreshLayout) {
        this.mContext = mCtx;
        if (swipeRefreshLayout != null) {
            this.swipeRefreshLayout = swipeRefreshLayout;
            this.swipeRefreshLayout.setRefreshing(true);
        }

        //注册观察Actiivty生命周期，如果页面销毁，取消事件流
        if (mContext instanceof LifecycleOwner) {
            LifecycleOwner activity = (LifecycleOwner) mContext;
            activity.getLifecycle().addObserver(this);
        }
    }


    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public final void onNext(T response) {
        //此处并没有真正的断开事件流，只是判断context状态不分发了，还是会造成内存泄露
        if (checkContext()) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
            return;
        }

        cancleDialog();
        onSuccess(response);

    }

    private boolean checkContext() {
        if (mContext == null) {
            return true;
        }

        if (mContext instanceof LifecycleOwner) {
            Lifecycle.State currentState = ((LifecycleOwner) mContext).getLifecycle().getCurrentState();
            return currentState == Lifecycle.State.DESTROYED;
        }

        return false;
    }


    /**
     * 通用异常错误的处理，不能弹出一样的东西出来
     */
    @Override
    public final void onError(Throwable t) {
        Log.e(TAG, t.toString());
        if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            errorCode = httpException.code();
            errorMsg = httpException.getMessage();
            getErrorMsg(httpException);
        } else if (t instanceof SocketTimeoutException) {  //VPN open
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "服务器响应超时";
        } else if (t instanceof ConnectException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "网络连接异常，请检查网络";
        } else if (t instanceof UnknownHostException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "无法解析主机，请检查网络连接";
            switchRootUrl();
        } else if (t instanceof UnknownServiceException) {
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "未知的服务器错误";
        } else if (t instanceof IOException) {  //飞行模式等
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "没有网络，请检查网络连接";
        } else if (t instanceof NetworkOnMainThreadException) {//主线程不能网络请求，这个很容易发现
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "主线程不能网络请求";
        } else if (t instanceof RuntimeException) { //很多的错误都是extends RuntimeException
            errorCode = RESPONSE_FATAL_EOR;
            errorMsg = "运行时错误";
        }
        if (checkContext()) {
            return;
        }
        cancleDialog();
        onFailure(errorCode, errorMsg);
    }

    private void switchRootUrl() {
//        if ("https://moocnd.ykt.io".equals(Urls.ROOT_ON_LINE)) {
//            Urls.ROOT_ON_LINE = "https://www.learning.mil.cn";
//            Urls.ROOT_URL = Urls.ROOT_ON_LINE;
//            HttpRetrofit.setBaseUrl(Urls.ROOT_URL + "/");
//            HttpRetrofit.gloadMap.remove("https://moocnd.ykt.io");
//        } else {
//            Urls.ROOT_ON_LINE = "https://moocnd.ykt.io";
//            Urls.ROOT_URL = Urls.ROOT_ON_LINE;
//            HttpRetrofit.setBaseUrl(Urls.ROOT_URL + "/");
//            HttpRetrofit.gloadMap.remove("https://www.learning.mil.cn");
//        }
//
//        RequestUtil.getInstance().resetUsrApi();
//        LogUtil.i(BaseObserver.class.getSimpleName(), "切换域名成功");
    }

    private void cancleDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(false);
        }
    }


    /**
     * 简单的把Dialog 处理掉
     */
    @Override
    public final void onComplete() {

    }


    /**
     * Default error dispose!
     * 一般的就是 AlertDialog 或 SnackBar
     */
    @CallSuper  //if overwrite,you should let it run.
    public void onFailure(int code, String message) {
        disposeEorCode(message, code);
    }


    /**
     * 对通用问题的统一拦截处理,Demo 项目的特定的做法
     */
    private void disposeEorCode(String message, int code) {
        if (code == RESPONSE_FATAL_EOR || code == CODE_NO_TOAST) return;
        if (code != 401) {
            toast(mContext, message);
            return;
        }
        BaseRepository.onErrorHandle(code, responseStr);
    }


    /**
     * 获取详细的错误的信息 errorCode,errorMsg
     * <p>
     * 以登录的时候的Grant_type 故意写错为例子,这个时候的http 应该是直接的返回401=httpException.code()
     * 但是是怎么导致401的？我们的服务器会在respose.errorBody 中的content 中说明
     */
    private void getErrorMsg(HttpException httpException) {
        String errorBodyStr = "";
        //我们的项目需要的UniCode转码 ,!!!!!!!!!!!!!!
        try (ResponseBody body = Objects.requireNonNull(httpException.response()).errorBody()) {
            errorBodyStr = Objects.requireNonNull(body).string();
            Log.i(TAG, errorBodyStr);
            EncryptParseData parseData = gson.fromJson(errorBodyStr, EncryptParseData.class);
            String data = parseData.getData();
            responseStr = AesEncryptUtil.decrypt(data);
        } catch (Exception e) {
            e.printStackTrace();
            responseStr = errorBodyStr;
        }
//            errorBodyStr = StringEscapeUtils.unescapeJava(errorBodyStr);


        if (TextUtils.isEmpty(responseStr)) {
            responseStr = errorBodyStr;
        }
        try {
            HttpResponse errorResponse = gson.fromJson(responseStr, HttpResponse.class);
            if (null != errorResponse) {
                if (!TextUtils.isEmpty(errorResponse.getMsg())) {
                    errorMsg = errorResponse.getMsg();
                } else {
                    if (!TextUtils.isEmpty(errorResponse.getMessage())) {
                        errorMsg = errorResponse.getMessage();
                    }
                }
            }
        } catch (Exception jsonException) {
            jsonException.printStackTrace();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onActivityDestory() {
        if (disposable != null) {
            disposable.dispose();
        }

        cancleDialog();
    }
}
