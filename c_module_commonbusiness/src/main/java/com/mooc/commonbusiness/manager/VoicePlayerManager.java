package com.mooc.commonbusiness.manager;


import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.commonbusiness.route.routeservice.AudioPlayService;
import com.mooc.commonbusiness.widget.VoicePlayerController;

import java.util.ArrayList;

/**
 * 只允许同时播放一个音频管理器
 */
public class VoicePlayerManager {

    public static ArrayList<VoicePlayerController> arrayList = new ArrayList<>();

    public static void addVoicePlayerVoiceView(VoicePlayerController voicePlayerController){
        releaseAll();
        arrayList.add(voicePlayerController);
    }

    public static void releaseAll(){

        //如果有音频正在播放则先停止音频播放
        AudioPlayService navigation = ARouter.getInstance().navigation(AudioPlayService.class);
        navigation.stopPlay();

        for(VoicePlayerController voicePlayerController1 : arrayList){
            voicePlayerController1.release();
            arrayList.remove(voicePlayerController1);
        }
    }
}
