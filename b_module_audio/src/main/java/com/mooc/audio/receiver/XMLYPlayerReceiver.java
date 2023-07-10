package com.mooc.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mooc.audio.manager.PrePlayListManager;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

/**
 * 喜马拉雅播放器广播回调
 */

public class XMLYPlayerReceiver extends BroadcastReceiver {

    public static final String ACTION_CLOSE = "com.moocxuetang.Action_Close";
    public static final String ACTION_NEXT = "com.moocxuetang.ACTION_NEXT";
    public static final String ACTION_PRE = "com.moocxuetang.ACTION_PRE";
    public static final String ACTION_PLAY_PLAUSE = "com.moocxuetang.ACTION_PAUSE_START";
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("XMLYPlayerReceiver.onReceive " + intent);
        String action = intent.getAction();

        if(ACTION_CLOSE.equals(action)){
            XmPlayerManager.getInstance(context).pause();
            XmPlayerManager.getInstance(context).closeNotification();
        } else if(ACTION_NEXT.equals(action)){
//            XmPlayerManager.getInstance(context).playNext();
            PrePlayListManager.getInstance().playNext();
        }
        else if(ACTION_PRE.equals(action)){
//            XmPlayerManager.getInstance(context).playPre();
            PrePlayListManager.getInstance().playPre();
        }
        else if(ACTION_PLAY_PLAUSE.equals(action)) {
            if(XmPlayerManager.getInstance(context).isPlaying()) {
                XmPlayerManager.getInstance(context).pause();
            } else {
                XmPlayerManager.getInstance(context).play();
            }
         }
    }
}
