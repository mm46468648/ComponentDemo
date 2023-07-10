package com.mooc.login.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mooc.login.constants.WxConstants;
//import com.moocxuetang.wxapi.SimpleLogManager;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI api = WXAPIFactory.createWXAPI(context, WxConstants.APP_ID,true);

//		SimpleLogManager.INSTANCE.saveToFile("AppRegister onReceive");

		// 将该app注册到微信
		api.registerApp(WxConstants.APP_ID);
	}
}
