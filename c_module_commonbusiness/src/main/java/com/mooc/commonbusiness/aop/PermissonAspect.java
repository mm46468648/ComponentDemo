//package com.mooc.commonbusiness.aop;
//
//
//import android.content.Context;
//import android.util.Log;
//
//import androidx.fragment.app.Fragment;
//
//import com.mooc.common.utils.permission.PermissionRequestCallback;
//import com.mooc.common.utils.permission.PermissionUtil;
//import com.mooc.commonbusiness.annotation.Permission;
//import com.mooc.commonbusiness.base.PermissionApplyActivity;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//
//@Aspect
//public class PermissonAspect {
//
//    @Pointcut("execution(@com.mooc.commonbusiness.annotation.Permission * *(..)) && @annotation(permissions)")
//    public void getPermission(Permission permissions){}
//
//    @Around("getPermission(permissions)")
//    public void getPermissionPoint(final ProceedingJoinPoint proceedingJoinPoint, Permission permissions){
//
//        //获取到上下文
//        Context context = null;
//        //获取到注解所在的类对象
//        final Object aThis = proceedingJoinPoint.getThis();
//        //判断aThis是否是Context的子类  如果是 就进行赋值
//        if(aThis instanceof Context){
//            context = (Context) aThis;
//        }else if(aThis instanceof Fragment){
//            context = ((Fragment) aThis).getActivity();
//        }
//        //获取到要申请的权限
//        if(context==null || permissions == null || permissions.value()==null || permissions.value().length==0){
//            return;
//        }
//        //1.获取需要申请的权限
//        String[] value = permissions.value();
//        final Context finalContext = context;
//        PermissionApplyActivity.launchActivity(context, value, permissions.requestCode(), new PermissionRequestCallback() {
//            @Override
//            public void permissionSuccess() {
//                Log.e("MN-------->","权限申请结果：成功");
//                //权限获取成功 继续执行
//                try {
//                    proceedingJoinPoint.proceed();
//                } catch (Throwable throwable) {
//                    throwable.printStackTrace();
//                }
//            }
//
//            @Override
//            public void permissionCanceled() {
//                Log.e("MN-------->","权限申请结果：失败");
//            }
//
//            @Override
//            public void permissionDenied() {
//                //开发者可以根据自己的需求看是否需要跳转到设置页面去
//                PermissionUtil.startAndroidSettings(finalContext);
//            }
//        });
//    }
//
//}
