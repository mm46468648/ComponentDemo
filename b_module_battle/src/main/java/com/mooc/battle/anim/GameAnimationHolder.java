package com.mooc.battle.anim;

import android.animation.ObjectAnimator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

/**
 * 对战游戏所用到的动画
 */
public class GameAnimationHolder {
    //匹配中的动画时长
    private static final int circleDuration = 5000;     //顺时针
    private static final int circleDuration2 = 2000;    //逆时针

    //匹配成功的动画时长
    public static final int matchSuccessDuration = 300;

    public static RotateAnimation createRotationAnimator() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(circleDuration);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        //让旋转动画一直转，不停顿的重点（实际上是添加动画插值器）
        rotateAnimation.setInterpolator(new LinearInterpolator());
        //永久循环
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        return rotateAnimation;
    }

    public static RotateAnimation createRotationAnimator2() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(circleDuration2);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        //让旋转动画一直转，不停顿的重点（实际上是添加动画插值器）
        rotateAnimation.setInterpolator(new LinearInterpolator());
        //永久循环
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        return rotateAnimation;
    }


    /**
     * 匹配成功
     */
    public static ScaleAnimation createMatchSuccessAnima() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.5f, 1f, 1.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(matchSuccessDuration);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;

    }

    /**
     * 匹配成功
     * 中间刀的动画
     */
    public static ScaleAnimation createMatchScccessAnima2() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(3f, 1f, 3f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(matchSuccessDuration);
        scaleAnimation.setInterpolator(new BounceInterpolator());
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }
}
