package com.mooc.common.utils.permission;

public interface PermissionRequestCallback {

    /**
     * 申请权限成功
     */
    void permissionSuccess();

    /**
     * 申请权限失败，用户点击拒绝了
     */
    void permissionCanceled();

    /**
     * 申请权限失败，用户点击了不再询问
     */
    void permissionDenied();
}
