package com.mooc.commonbusiness.module.report;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.mooc.commonbusiness.R;
import com.mooc.commonbusiness.base.BaseActivity;
import com.mooc.commonbusiness.constants.IntentParamsConstants;
import com.mooc.commonbusiness.constants.UrlConstants;
import com.mooc.commonbusiness.route.Paths;

/**
 * 摇一摇反馈弹窗
 * 使用弹窗样式，方便显示
 *
 */
@Route(path = Paths.PAGE_SHAKE_DIALOG)
public class ShakeFeekbackDialogActivity extends BaseActivity {
    private TextView tvCancel;
    private TextView tvOk;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_shake_dialog);
        tvCancel = findViewById(R.id.layout_ignore_msg);
        tvOk = findViewById(R.id.layout_ok_msg);
        initListener();
    }


    public void initListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到反馈详情页面 (目前是一个h5页面)
                ARouter.getInstance().build(Paths.PAGE_WEB)
                        .withString(IntentParamsConstants.WEB_PARAMS_TITLE,"文章")
                        .withString(IntentParamsConstants.WEB_PARAMS_URL, UrlConstants.FEEDBACK_URL)
                        .navigation();
//                ARouter.getInstance().build(Paths.PAGE_FEED_BACK).navigation();
            }
        });
    }


}
