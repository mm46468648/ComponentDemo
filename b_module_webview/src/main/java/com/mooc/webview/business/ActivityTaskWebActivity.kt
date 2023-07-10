package com.mooc.webview.business

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.ActivityTaskBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.UrlUtils
import com.mooc.webview.R
//import kotlinx.android.synthetic.main.webview_activity_webview.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = Paths.PAGE_WEB_ACTIVITY_TASK)
class ActivityTaskWebActivity : BaseResourceWebviewActivity() {

    val fromType by extraDelegate(IntentParamsConstants.ACT_FROM_TYPE, "")
    var mLoadUrl = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }
    override fun loadUrl() {
        //获取活动相关信息
        HttpService.otherApi.getActivityTaskDetail(resourceID)
            .compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<ActivityTaskBean>>(this) {
                override fun onSuccess(t: HttpResponse<ActivityTaskBean>) {
                    if(t.isSuccess){
                        onResponse(t.data)
                    }else{
                        showTipMessage(t.message)
                    }
                }

                override fun onFailure(code: Int, message: String?) {
                    super.onFailure(code, message)
//                    showTipMessage("${message}\n${code}")
                }
            })
    }

    fun onResponse(activityTaskBean: ActivityTaskBean) {

        //判断是否是在活动期间,或者活动报名时间
        if (!inActivityTime(activityTaskBean.start_time * 1000 , activityTaskBean.end_time * 1000)
            && !inActivityTime(activityTaskBean.apply_start_time * 1000, activityTaskBean.apply_end_time * 1000)
        ) {

            //不是，显示活动过期弹窗
            showTipMessage("活动已过期")
            return
        }

        //加载活动地址
        mLoadUrl = UrlUtils.appendParams(activityTaskBean.url, "type", fromType)
        webviewWrapper.loadUrl(mLoadUrl)

        //是否显示标题栏
        val titleVisiable = if (activityTaskBean.is_title) View.VISIBLE else View.GONE
        inflater.commonTitleLayout.visibility = titleVisiable
        //设置title
        inflater.commonTitleLayout.middle_text = title

        //设置分享内容
        if (activityTaskBean.share_status == 1) {
            inflater.commonTitleLayout.ib_right?.setImageResource(R.mipmap.common_ic_right_share_gray)
            inflater.commonTitleLayout.ib_right?.visibility = View.VISIBLE
            inflater.commonTitleLayout.ib_right?.setOnClickListener {
                showSharePop(activityTaskBean)
            }
        }

    }

    private fun showTipMessage(message:String) {
        XPopup.Builder(this).asConfirm("",message ) {
            finish()
        }.show()
    }

    /**
     * 展示分享弹窗
     */
    private fun showSharePop(activityTaskBean: ActivityTaskBean) {
        var shareUrl =
            if (activityTaskBean.share_link.isNotEmpty()) activityTaskBean.share_link else mLoadUrl
        var shareTitle =
            if (activityTaskBean.share_title.isNotEmpty()) activityTaskBean.share_title else title
        var shareDesc =
            if (activityTaskBean.share_desc.isNotEmpty()) activityTaskBean.share_desc else title
        val sharePicture =
            if (activityTaskBean.share_picture.isNotEmpty()) activityTaskBean.share_picture else UrlConstants.SHARE_LOGO_URL

        //拼接用户信息
        shareUrl =
            UrlUtils.appendParams(shareUrl, "user_id", GlobalsUserManager.uid, "is_app_share", "1")
        if (shareTitle.contains("\${user}")) {
            shareTitle = shareTitle.replace("\${user}", GlobalsUserManager.userInfo?.name ?: "")
        }
        if (shareDesc.contains("\${user}")) {
            shareDesc = shareDesc.replace("\${user}", GlobalsUserManager.userInfo?.name ?: "")
        }

        XPopup.Builder(this).asCustom(
            CommonBottomSharePop(this, {
                val shareSrevice =
                    ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
                val build = IShare.Builder()
                    .setSite(it)
                    .setTitle(shareTitle)
                    .setMessage(shareDesc)
                    .setImageUrl(sharePicture)
                    .setWebUrl(shareUrl)
                    .build()
                shareSrevice.share(this, build)
            })
           ).show()

    }

    /**
     * 判断是否在活动期间
     */
    fun inActivityTime(startTime: Long, endTime: Long): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        if (endTime == 0L && currentTimeMillis > startTime) {
            return true
        }
        if (startTime == 0L && currentTimeMillis < endTime) {
            return true
        }

        return currentTimeMillis in (startTime + 1) until endTime
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserLogEvent(logEvent:UserLoginStateEvent){
        if (logEvent.userInfo != null) {
            loadUrl()
        }
    }
    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }


}