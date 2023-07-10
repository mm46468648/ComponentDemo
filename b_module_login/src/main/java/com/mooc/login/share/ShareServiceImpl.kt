package com.mooc.login.share

import android.app.Activity
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.share.ShareScoreUtil
import com.mooc.login.R
import com.mooc.common.ktextends.toast

@Route(path = Paths.SERVICE_SHARE)
class ShareServiceImpl : ShareSrevice {
    override fun share(activity: Activity, builder: IShare.Builder, shareCallBack: ((status: Int) -> Unit)?) {
        val shareManager = ShareManager()
        shareManager.share(activity,builder,shareCallBack)
    }

    /**
     * @param shareType 分享的类型
     */
    override fun shareAddScore(
        shareType: String,
        activity: Activity,
        builder: IShare.Builder,
        shareAddScoreCallBack: ((status: Int) -> Unit)?
    ) {

        val shareManager = ShareManager()

        shareManager.share(activity, builder) {
            if (it == 1) {
                // 获取分享积分 并弹框
                ShareScoreUtil.getShareScore(activity,shareType)
            } else {
                toast(activity.resources.getString(R.string.toast_get_app_share_faile))
            }
            shareAddScoreCallBack?.invoke(it)
        }

    }
}