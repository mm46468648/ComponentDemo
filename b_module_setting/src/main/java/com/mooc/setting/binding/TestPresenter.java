package com.mooc.setting.binding;

import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.setting.R;
import com.mooc.common.global.AppGlobals;
import com.mooc.common.ktextends.AnyExtentionKt;
import com.mooc.commonbusiness.constants.UrlConstants;
import com.mooc.commonbusiness.route.Paths;

public class TestPresenter {

    public static void turnToUserServiceAgreement(){
        Bundle bundle = new Bundle();
        AnyExtentionKt.put(bundle,"params_title", AppGlobals.INSTANCE.getApplication().getResources().getString(R.string.text_str_user_service_intro));
        AnyExtentionKt.put(bundle,"params_url", UrlConstants.USER_SERVICE_AGREDDMENT_URL);
        ARouter.getInstance().build(Paths.PAGE_WEB).with(bundle).navigation();
    }
}
