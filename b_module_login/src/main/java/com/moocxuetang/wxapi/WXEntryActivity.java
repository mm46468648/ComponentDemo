
package com.moocxuetang.wxapi;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mooc.login.manager.WechatManager;

/**
 * @ProjectName: WXEntryActivity
 * @Package: com.moocxuetang.wxapi
 * @ClassName: WXEntryActivity
 * @Description: 微信回调接受页面可以处理微信消息的回调
 * @Author: xym
 * @CreateDate: 2020/8/7 4:44 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/7 4:44 PM
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */
public class WXEntryActivity extends AppCompatActivity{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        handleIntent(intent);

    }

    /**
     * 交给IWXAPIEventHandler的实现类处理
     * @param intent
     */
    private void handleIntent(Intent intent) {
        WechatManager.INSTANCE.handleIntent(intent);
        finish();
    }




}