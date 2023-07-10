package com.mooc.commonbusiness.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.mooc.common.utils.permission.PermissionRequestCallback;
import com.mooc.common.utils.permission.PermissionUtil;

/**
 * 权限申请页面
 * 透明
 */
public class PermissionApplyActivity extends AppCompatActivity {
    /**
     * 请求的权限
     */
    public static final String REQUEST_PERMISSIONS = "request_permissions";

    //请求码
    public static final String REQUEST_CODE = "request_code";
    //默认请求码
    public static final int REQUEST_CODE_DEFAULT = 0;
    //回调接口
    private static PermissionRequestCallback requestCallback;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent!=null){
            //获取到意图传递过来的权限的数组
            String[] permissionArr = intent.getStringArrayExtra(REQUEST_PERMISSIONS);
            int request_code = intent.getIntExtra(REQUEST_CODE, REQUEST_CODE_DEFAULT);
            //三者一项不符合，结束当前页面
            if (permissionArr == null || request_code == -1 || requestCallback == null) {
                this.finish();
                return;
            }

            //判断是否已经授权了
            if(PermissionUtil.hasPermissionRequest(this,permissionArr)){
                requestCallback.permissionSuccess();
                this.finish();
                return;
            }

            //开始申请权限
            ActivityCompat.requestPermissions(this,permissionArr,request_code);
        }
    }


    /**
     * 启动当前activity
     *
     * @param context     上下文
     * @param permissions 权限
     * @param requestCode 请求码
     * @param callback    回调
     */
    public static void launchActivity(Context context, String[] permissions, int requestCode, PermissionRequestCallback callback) {
        requestCallback = callback;

        Bundle bundle = new Bundle();
        bundle.putStringArray(REQUEST_PERMISSIONS, permissions);
        bundle.putInt(REQUEST_CODE, requestCode);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(context, PermissionApplyActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //再次判断是否权限真正的申请成功
        if(PermissionUtil.requestPermissionSuccess(grantResults)){
            requestCallback.permissionSuccess();
            this.finish();
            return;
        }

        //权限拒绝，并且用户勾选了不在提示
        if(PermissionUtil.shouldShowRequestPermissionRationale(this,permissions)){
            requestCallback.permissionDenied();
            this.finish();
            return;
        }

        //权限拒绝
        requestCallback.permissionCanceled();
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,0);
    }
}
