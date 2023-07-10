package com.mooc.webview.business

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.webview.R

@Route(path = Paths.PAGE_WEB_MICRO_PROFESSIONAL)
class MicroProfessionalWebActivity : BaseResourceWebviewActivity() {

    var onTestBack = false        //是否是测试卷返回

    lateinit var callBack:Application.ActivityLifecycleCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        callBack = object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if(activity is BaseResourceWebviewActivity){
                    onTestBack = true
                }
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        }
        AppGlobals.getApplication()?.registerActivityLifecycleCallbacks(callBack)
    }

    override fun onDestroy() {
        super.onDestroy()

        AppGlobals.getApplication()?.unregisterActivityLifecycleCallbacks(callBack)
    }

    override fun onResume() {
        super.onResume()
        if (onTestBack) {
            webviewWrapper.strategy.evaluateJavascript("appRefresh()",null)
            onTestBack = false
        }
    }

    /**
     * 获取分享信息
     */
    override fun getShareInfo(resourceId: String) {
        mViewModel.getShareDetailData(resourceType.toString(), resourceId)
        mViewModel.articleWebShareDetaildata.observe(this, Observer {
            inflater.commonTitleLayout.ib_right_second?.visiable(false)
            //share_status  是否分享 0是 -1否
            if ("0" == it.share_status) {
//                val watchView = XPopup.Builder(this).watchView(inflater.commonTitleLayout.ib_right)
                inflater.commonTitleLayout.setRightFirstIconRes(R.mipmap.common_ic_title_right_menu)
                if (it.share_picture.isNullOrBlank()) {
                    it.share_picture = UrlConstants.SHARE_LOGO_URL
                }
                //去分享
                inflater.commonTitleLayout.setOnRightIconClickListener { _ ->
                    val shareLink =
                        if (TextUtils.isEmpty(it.weixin_url)) it.share_link else it.weixin_url
                    it?.share_picture?.let { it1 ->
                        showMenuPop(
                            it.share_title,
                            it.share_desc,
                            shareLink,
                            it1
                        )
                    }
                }
            }
        })
    }
}