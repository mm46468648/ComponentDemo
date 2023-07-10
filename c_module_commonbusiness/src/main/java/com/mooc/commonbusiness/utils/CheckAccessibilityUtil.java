package com.mooc.commonbusiness.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import com.mooc.common.global.AppGlobals;
import com.mooc.commonbusiness.api.CommonApi;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.model.HttpResponse;
import com.mooc.commonbusiness.net.ApiService;
import com.mooc.commonbusiness.utils.format.RequestBodyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CheckAccessibilityUtil {

    private static volatile CheckAccessibilityUtil instance = null;

    private CheckAccessibilityUtil() {

    }

    public static CheckAccessibilityUtil getInstance() {
        if (instance == null) {
            synchronized (CheckAccessibilityUtil.class) {
                if (instance == null) {
                    instance = new CheckAccessibilityUtil();
                }
            }
        }

        return instance;
    }

    int lastNum = -1;
    int scheduleTime = 30 * 1000;
    Disposable subscribe;

    public void startCheckAccessibilityService() {
        subscribe = Observable.interval(scheduleTime, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .retry()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
//                        toast("开始获取无障碍" + UserUtils.getUid());
                        //不登陆不获取
                        if (!GlobalsUserManager.INSTANCE.isLogin()) return;

                        List<String> serviceNameList = getServiceNameList();
                        //如果有正在运行的无障碍服务，并且不等于上一次的数量，则上传一次
                        if (serviceNameList.size() != lastNum) {
                            lastNum = serviceNameList.size();
                            postToServer(serviceNameList);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        toast("startCheckAccessibilityService" + throwable.toString());
                    }
                });
    }

    /**
     * 获取无障碍服务报名列表
     */
    List<String> getServiceNameList() {
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = getInstalledAccessibilityServiceList();
        List<String> serviceNames = new ArrayList<>();
        if (installedAccessibilityServiceList.isEmpty()) return serviceNames;

        for (AccessibilityServiceInfo serviceInfo : installedAccessibilityServiceList) {
            ResolveInfo resolveInfo = serviceInfo.getResolveInfo();
            ServiceInfo serviceInfo3 = resolveInfo.serviceInfo;
            String s1 = serviceInfo3.toString();

            //去掉"}"
            if (!TextUtils.isEmpty(s1) && s1.contains("}")) {
                s1 = s1.replace("}", "");
            }
            if (!TextUtils.isEmpty(s1) && s1.contains(" ")) {
                String[] s = s1.split(" ");
                serviceNames.add(s[s.length - 1]);
            } else {
                serviceNames.add(s1);
            }


        }
        return serviceNames;
    }


    /**
     * 取得正在监控目标包名的AccessibilityService
     */
    private List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList() {
        List<AccessibilityServiceInfo> result = new ArrayList<>();
        AccessibilityManager accessibilityManager = (AccessibilityManager) AppGlobals.INSTANCE.getApplication().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager == null) {
            return result;
        }
        List<AccessibilityServiceInfo> infoList = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        if (infoList == null || infoList.size() == 0) {
            return result;
        }
        for (AccessibilityServiceInfo info : infoList) {
            if (info.packageNames == null) {
                result.add(info);
            } else {
                for (String packageName : info.packageNames) {
                    result.add(info);
                }
            }
        }
        return result;
    }

    @SuppressLint("CheckResult")
    void postToServer(List<String> serviceNameList) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < serviceNameList.size(); i++) {
                jsonArray.put(serviceNameList.get(i));
            }
            jsonObject.put("app_info", jsonArray);
            ApiService.getRetrofit().create(CommonApi.class).postRuningAcccessibility(RequestBodyUtil.Companion.fromJson(jsonObject))
                    .subscribe(new Consumer<HttpResponse>() {
                @Override
                public void accept(HttpResponse httpResponse) throws Exception {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
//
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
