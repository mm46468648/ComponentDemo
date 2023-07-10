package com.mooc.audio.presenter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.audio.AlbumRepository
import com.mooc.audio.AudioApi
import com.mooc.audio.manager.TrackCloseTimerManager
import com.mooc.audio.manager.TrackPlayManger
import com.mooc.audio.manager.XiMaUtile
import com.mooc.audio.ui.OwnBuildUseXimaAudioActivity
import com.mooc.audio.ui.pop.AudioSettingNewPop
import com.mooc.audio.ui.pop.AudioSpeedPop
import com.mooc.audio.ui.pop.AudioTimingPop
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.model.eventbus.AlbumRefreshEvent
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.pop.CommonMenuPopupW
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * 喜马拉雅
 * 音频业务逻辑处理类
 */
class XimaAudioBusinessPresenter constructor(activity: Activity) {

    var weakActivity: WeakReference<Activity>? = WeakReference(activity)

    /**
     * 倒计时时间监听
     */
    var onTimeCountCallback: ((time: Long) -> Unit)? = null

    /***
     * 点击下载
     */
    var downloadClick: (() -> Unit)? = null

    /**
     * 获取分享内容
     */
    var getShareContent: (() -> ShareDetailModel?)? = null


    var resourceId = ""
    var resourceName = ""

    val mXimaUtil: XiMaUtile by lazy {
        XiMaUtile.getInstance()
    }

    /**
     * 加入学习室
     */
    fun addToStudyRoom(
        resourceType: Int,
        resourseId: String,
        success: ((b: Boolean) -> Unit)? = null
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("url", resourseId)
        jsonObject.put("resource_type", resourceType)
        jsonObject.put("other_resource_id", resourseId)

        //显示加入学习室弹窗
        val studyRoomService: StudyRoomService? =
            ARouter.getInstance().navigation(StudyRoomService::class.java)
        weakActivity?.get()?.let {
            studyRoomService?.showAddToStudyRoomPop(it, jsonObject) { suc ->
                success?.invoke(suc)
                //更新加入学习室状态
                //                commonTitle.setRightSecondIconRes(R.mipmap.common_ic_title_right_added_white)
                //                commonTitle.ib_right_second?.isEnabled = false
            }
        }
    }

    /**
     * 显示设置弹窗
     */
    fun showSettingPop(parentView: View, isShowShield: Boolean = false) {
        weakActivity?.get()?.let { context ->

            val mutableListOf = mutableListOf(
                CommonMenuPopupW.TYPE_CUTDOWN_TIME_STR,
                CommonMenuPopupW.TYPE_SHARE_STR,
                CommonMenuPopupW.TYPE_SPEED_STR,
            )



            //如果不是自建音频添加下载选项
            if (weakActivity?.get() !is OwnBuildUseXimaAudioActivity) {
                mutableListOf.add(CommonMenuPopupW.TYPE_DOWNLOAD_STR)
            }
            //如果是自建音频添加举报选项
//            if (weakActivity?.get() is OwnBuildUseXimaAudioActivity) {
//                mutableListOf.add(CommonMenuPopupW.TYPE_REPORT_STR)
//            }

            //屏蔽在最后一项
            if (isShowShield) {
                mutableListOf.add(CommonMenuPopupW.TYPE_SHIELD_STR)
            }
            val commonTitleMenuPop =
                AudioSettingNewPop(context, mutableListOf as ArrayList<String>, parentView)
            commonTitleMenuPop.onTypeSelect = {
                when (it) {
                    CommonMenuPopupW.TYPE_CUTDOWN_TIME_STR -> {
                        showTimeSetPop()
                    }
                    CommonMenuPopupW.TYPE_SHARE_STR -> {
                        showSharePop()
                    }

                    CommonMenuPopupW.TYPE_DOWNLOAD_STR -> {
                        downloadClick?.invoke()
                    }
                    CommonMenuPopupW.TYPE_SPEED_STR -> {
                        showSpeedSetPop()
                    }
                    CommonMenuPopupW.TYPE_SHIELD_STR -> {
                        shiledResource()
                    }
                    CommonMenuPopupW.TYPE_REPORT_STR ->{
                        reportResource()
                    }
                }


            }
            commonTitleMenuPop.show()
        }

    }

    fun reportResource(){
        val put = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID, resourceId)
            .put(
                IntentParamsConstants.PARAMS_RESOURCE_TYPE,
                ResourceTypeConstans.TYPE_ONESELF_TRACK
            )
            .put(
                IntentParamsConstants.PARAMS_RESOURCE_TITLE,
                resourceName
            )
        ARouter.getInstance().build(Paths.PAGE_REPORT_DIALOG).with(put).navigation()
    }

    /**
     * 屏蔽音频资源
     * 音频资源固定传"1"
     */
    @SuppressLint("CheckResult")
    private fun shiledResource() {
        val requestData = JSONObject()
        requestData.put("type", "1")
        requestData.put("pk", resourceId)
//        requestData.put("duration", audioPointManager.currentMusicData?.progress?.div(1000) ?: 0)
        ApiService.getRetrofit().create(AudioApi::class.java)
            .postResourceShield(RequestBodyUtil.fromJson(requestData))
            .compose(RxUtils.applySchedulers())
            .subscribe ({
                if(it!=null){
                    if(it.isSuccess){
                        EventBus.getDefault().post(AlbumRefreshEvent(resourceId))
                    }
                    toast(it.msg)
                }
            },{})
    }

    private fun showSharePop() {
        weakActivity?.get()?.apply {
            val shareDetailModel = getShareContent?.invoke() ?: return
//            val targetUrl: String = UrlConstants.TRACK_SHARE_URL + shareDetailModel.source_id + UrlConstants.SHARE_FOOT + UrlConstants.SHARE_FOOT_MASTER + "&resource_type=" + shareDetailModel?.source_type + "&resource_id=" + shareDetailModel?.source_id
            val targetUrl: String = shareDetailModel.weixin_url

            val chooseBack: ((platform: Int) -> Unit) = { platform ->
                if (platform == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {
                    //分享到学友圈
                    ShareSchoolUtil.postSchoolShare(
                        this,
                        shareDetailModel.source_type,
                        shareDetailModel.source_id,
                        shareDetailModel.share_picture
                    )
                } else {
                    val shareAddScore = ARouter.getInstance().navigation(ShareSrevice::class.java)
                    shareAddScore.shareAddScore(
                        ShareTypeConstants.TYPE_TRACK, this, IShare.Builder()
                            .setSite(platform)
                            .setWebUrl(targetUrl)
                            .setTitle(shareDetailModel.share_title)
                            .setMessage(shareDetailModel.share_desc)
                            .setImageUrl(shareDetailModel.share_picture)
                            .build()
                    )
                }
            }

            //自建音频隐藏分享到学友圈
            val hide = this is OwnBuildUseXimaAudioActivity
            val commonBottomSharePop = CommonBottomSharePop(this, chooseBack, hide)
            XPopup.Builder(this)
                .asCustom(commonBottomSharePop)
                .show()
        }

    }

    private fun showTimeSetPop() {
        weakActivity?.get()?.let {
            val audioTimingPop = AudioTimingPop(it)

            audioTimingPop.onTimeSelect = { time ->
//                toast("设置定时${it}分钟")
                if (time == 0L) { //如果是0就代表关闭计时
                    stopCloseTimeCount()
                } else {
                    startCloseTimeCount(time)
                }
            }
            XPopup.Builder(weakActivity?.get())
                .asCustom(audioTimingPop)
                .show()
        }

    }

    /**
     * 开始定时关闭倒计时
     */
    private fun startCloseTimeCount(time: Long) {

        mXimaUtil.setTimer(time, object : TrackCloseTimerManager.TimeCloseListener {
            override fun selectTime(t: Long) {
                onTimeCountCallback?.invoke(t)
            }
        })
    }

    /**
     * 停止定时关闭倒计时
     */
    private fun stopCloseTimeCount() {
        onTimeCountCallback?.invoke(0)
        mXimaUtil.stopTimer()
    }


    /**
     * 显示倍速弹窗
     */
    private fun showSpeedSetPop() {
        weakActivity?.get()?.let {
            val audioSpeedPop =
                AudioSpeedPop(it)

            audioSpeedPop.currentSelectSpeed = mXimaUtil.currentSpeed
            audioSpeedPop.onItemtSelect = {
                mXimaUtil.setSpeed(it)
            }
            XPopup.Builder(weakActivity?.get())
                .asCustom(audioSpeedPop)
                .show()
        }

    }

    fun release() {
        resourceId = ""
        resourceName = ""
        onTimeCountCallback = null
        weakActivity?.clear()
        weakActivity = null
    }
}