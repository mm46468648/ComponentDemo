package com.mooc.commonbusiness.scheme;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.ResourceTypeConstans;
import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.mooc.commonbusiness.interfaces.BaseResourceInterface;
import com.mooc.commonbusiness.manager.ResourceTurnManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class SchemasManager {
    private static HashMap<String, Boolean> needLoginMap;

    static {
        needLoginMap = new HashMap<>();
        needLoginMap.put("chapter", true);
    }

    public static void dispatchSchemas(SchemasData data, Context mContext, boolean isIgnoreHttp, boolean isPush) {
        if (TextUtils.isEmpty(data.getStrRawData()) || TextUtils.isEmpty(data.getStrSchemas())) {
            return; //数据不合法.
        }
        if (SchemasBlockList.isHttpSchemas(data.getStrSchemas())) {
            String url = data.getStrRawData();
            if (url.contains("&is_share=1")) {
                url = url.replace("&is_share=1", "");
            }

            if (url.contains("resource_type")) {
                String type = "";
                String id = "";
                Map<String, String> mapRequest = URLRequest(url);
                if (mapRequest.size() > 0) {
                    type = mapRequest.get("resource_type");
                    id = mapRequest.get("resource_id");
                }
                url = url.split("\\?")[0];

                turnToPage(id, Integer.parseInt(type), url, "");

            } else {
                SchemaBean schemaBean = new SchemaBean();
                schemaBean.setResource_id(url);
                schemaBean.setResource_type(ResourceTypeConstans.TYPE_WE_CHAT);
                ResourceTurnManager.Companion.turnToResourcePage(schemaBean);
            }

        } else if (SchemasBlockList.isBlockSchemas(data.getStrSchemas())) {
            //特殊协议.
            int code = data.getStrSchemas().hashCode();
            if (code == SchemasBlockList.TYPE_RESOURCE//进入首页--分类.
                    || code == SchemasBlockList.TYPE_RECOMMEND //进入首页--资源.
                    || code == SchemasBlockList.TYPE_DEFAULT //进入首页.
                    || code == SchemasBlockList.TYPE_STUDY_ROOM  //进入首页--学习空间.
                    || code == SchemasBlockList.TYPE_MY) { //进入首页--我的.
                Bundle bundle = new Bundle();
                bundle.putInt(MyPushKey.CODE_KEY, code);
                MenuItemEvent menuEvent = new MenuItemEvent(bundle);
                menuEvent.setPush(isPush);
                EventBus.getDefault().post(menuEvent);
            } else if (code == SchemasBlockList.TYPE_COURSE_ALL) {//全部课程
                Bundle bundle = new Bundle();
                bundle.putInt(MyPushKey.CODE_KEY, code);
                MenuItemEvent menuEvent = new MenuItemEvent(bundle);
                menuEvent.setPush(isPush);
                EventBus.getDefault().post(menuEvent);
            } else if (code == SchemasBlockList.TYPE_COURSE) {//课程
                Bundle bundle = new Bundle();
                bundle.putInt(MyPushKey.CODE_KEY, code);
                bundle.putSerializable(MyPushKey.DATA_KEY, data);
                MenuItemEvent menuEvent = new MenuItemEvent(bundle);
                menuEvent.setPush(isPush);
                EventBus.getDefault().post(menuEvent);
            } else if (code == SchemasBlockList.TYPE_DOWNLOAD
                    || code == SchemasBlockList.TYPE_MY_MESSAGE
                    || code == SchemasBlockList.TYPE_FEEDBACK
                    || code == SchemasBlockList.TYPE_USER_SIGN) {
                Bundle bundle = new Bundle();
                bundle.putInt(MyPushKey.CODE_KEY, code);
                MenuItemEvent menuEvent = new MenuItemEvent(bundle);
                menuEvent.setPush(isPush);
                EventBus.getDefault().post(menuEvent);
            } else if (code == SchemasBlockList.TYPE_COLUMN_ARTICLE
                    || code == SchemasBlockList.TYPE_HTML) {
                Bundle bundle = new Bundle();
                bundle.putInt(MyPushKey.CODE_KEY, code);
                bundle.putSerializable(MyPushKey.DATA_KEY, data);
                MenuItemEvent menuEvent = new MenuItemEvent(bundle);
                menuEvent.setPush(isPush);
                EventBus.getDefault().post(menuEvent);
            }

        } else {
            //非特殊协议.尝试使用隐式Intent打开.
            try {
                String originAction = data.getStrSchemas();
                String action = data.getStrSchemas();
                if (needLoginMap.get(action) != null && !GlobalsUserManager.INSTANCE.isLogin()) {
                    return; //没有登录，忽略通知栏.
                }
                if ("coursecerti".equals(originAction)) {
                    data.setStrSchemas("html");
                }
                if (!data.getStrSchemas().startsWith("com.xuetangx.mobile.")) {
                    action = "com.xuetangx.mobile." + data.getStrSchemas();
                }


                Intent intent = new Intent(action);

                for (Map.Entry<String, String> entry : data.getKeyValue().entrySet()) {
                    intent.putExtra(entry.getKey(), entry.getValue());
                }
                //外部链接跳转内部app,type 31用webView打开
                if (intent.hasExtra("resource_type")) {
                    turnToPage(intent.getStringExtra("resource_id"), Integer.valueOf(intent.getStringExtra("resource_type")), intent.getStringExtra("resource_link"),
                            intent.getStringExtra("resource_title"));

                    return;
                }

                if (isPush) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {

            }
        }

    }

    private static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<>();

        String[] arrSplit;

        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (!arrSplitEqual[0].equals("")) {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    private static void turnToPage(String resourceId, int type, String link, String title) {
        ResourceTurnManager.Companion.turnToResourcePage(new BaseResourceInterface() {


            @Override
            public int get_resourceStatus() {
                return 0;
            }

            @NonNull
            @Override
            public String get_resourceId() {
                return resourceId;
            }

            @Override
            public int get_resourceType() {
                return type;
            }

            @Nullable
            @Override
            public Map<String, String> get_other() {
                HashMap hashMapOf = new HashMap<String, String>();
                if (TextUtils.isEmpty(link)) {
                    hashMapOf.put(IntentParamsConstants.WEB_PARAMS_URL, link);
                }
                if (TextUtils.isEmpty(title)) {
                    hashMapOf.put(IntentParamsConstants.WEB_PARAMS_TITLE, title);
                }
                return hashMapOf;
            }
        });
    }


    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit;

        strURL = strURL.trim().toLowerCase();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[arrSplit.length - 1] != null) {
                    strAllParam = arrSplit[arrSplit.length - 1];
                }
            }
        }

        return strAllParam;
    }

}
