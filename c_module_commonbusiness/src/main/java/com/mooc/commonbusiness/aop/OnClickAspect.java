//package com.mooc.commonbusiness.aop;
//
//import android.util.Log;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//
////@Aspect
//public class OnClickAspect {
//
//    //@Pointcut声明切入点
//    // 第一个*所在的位置表示的是返回值，*表示的是任意的返回值，
//    // onClick()中的 .. 所在位置是方法参数的位置，.. 表示的是任意类型、任意个数的参数
//    // * 表示的是通配
//    //    @Pointcut("execution(* getTime(..))")
////    @Pointcut("execution(* com.maniu.mn_vip_aspectj.MainActivity.getTime(..))")
////    @Pointcut("execution(* android.view.View.OnClickListener.onClick(..))")
//    @Pointcut("execution(* android.view.View.OnClickListener.onClick(..))")
//    public void clickMethod() {
//    }
//
//    @After("clickMethod()")
//    public void afterClickMethodCall(JoinPoint joinPoint) {
//        Log.e("mooc----------->",  "after "+joinPoint.toString()+" call");
//    }
//}
