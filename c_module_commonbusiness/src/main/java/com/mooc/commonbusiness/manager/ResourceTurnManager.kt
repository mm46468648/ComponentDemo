package com.mooc.commonbusiness.manager

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.StatisticsService
import com.mooc.commonbusiness.utils.ClickFilterUtil

/**
 * 资源跳转管理类
 */
class ResourceTurnManager {

    companion object {
        /**
         * 页面载入打点
         */
        fun onPageLoad(resourceType: Int, resourceId: String) {

            val logService = ARouter.getInstance().navigation(StatisticsService::class.java)
            val s = LogEventConstants2.typeLogPointMap[resourceType] ?: ""
            if (logService != null && !TextUtils.isEmpty(s))
                logService.addLoadLog(s, resourceId, resourceType.toString())

        }

        /**
         * 跳转资源页面
         */
        fun turnToResourcePage(resource: BaseResourceInterface) {
            if (!GlobalsUserManager.isLogin()) { //未登录，先登录
                turnToLogin()
                return
            }

            //写的有问题
//            if (!RepeatedClickUtils.isFastClick()) {//防止连续点击
//                toast("重复点击")
//                return
//            }

            if (resource._resourceStatus != 0) {
                toast("资源已下线")
                return
            }
            when (resource._resourceType) {
                ResourceTypeConstans.TYPE_WE_CHAT,
                ResourceTypeConstans.TYPE_PUSH_HTML -> {  //微信外链文章
                    val bundle = Bundle()
                    bundle.put(IntentParamsConstants.WEB_PARAMS_URL, resource._resourceId)
                    bundle.put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, resource._resourceType)
                    ARouter.getInstance().build(Paths.PAGE_WEB_EXTERNAL_WEB).with(bundle)
                        .navigation()
                }
                ResourceTypeConstans.TYPE_COURSE -> {
                    val put =
                        Bundle().put(IntentParamsConstants.COURSE_PARAMS_ID, resource._resourceId)
                            .put(
                                IntentParamsConstants.PARAMS_RESOURCE_TYPE,
                                ResourceTypeConstans.TYPE_COURSE
                            )
                            .put(IntentParamsConstants.PARAMS_RESOURCE_URL, resource._resourceId)
                    //添加额外信息
                    resource._other?.keys?.forEach {
                        put.put(it, resource._other?.get(it) ?: "")
                    }
                    ARouter.getInstance().build(Paths.PAGE_COURSE_DETAIL).with(put).navigation()
                }
                ResourceTypeConstans.TYPE_ARTICLE,
                ResourceTypeConstans.TYPE_BAIKE,
                ResourceTypeConstans.TYPE_ACTIVITY,
                ResourceTypeConstans.TYPE_ACTIVITY_TASK, //活动任务
                ResourceTypeConstans.TYPE_OTHER_COURSE,
                ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK,
                ResourceTypeConstans.TYPE_KNOWLEDGE,
                ResourceTypeConstans.TYPE_MICRO_LESSON,
                ResourceTypeConstans.TYPE_PERIODICAL,
                ResourceTypeConstans.TYPE_TEST_VOLUME, //测试卷
                ResourceTypeConstans.TYPE_QUESTIONNAIRE, //问答卷
                ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL, //微专业
                ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> {
                    val bundle = Bundle()
                    resource._other?.entries?.forEach {
                        bundle.put(it.key, it.value)
                    }
                    bundle.put(IntentParamsConstants.PARAMS_RESOURCE_ID, resource._resourceId)

                    //用来查询是否加入学习室的字段
                    bundle.put(IntentParamsConstants.PARAMS_RESOURCE_URL, resource._resourceId)
                    bundle.put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, resource._resourceType)

                    var routePath = when (resource._resourceType) {
                        ResourceTypeConstans.TYPE_MICRO_LESSON -> Paths.PAGE_WEB_MICROCOURSE
                        ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> Paths.PAGE_WEB_MICRO_PROFESSIONAL
                        ResourceTypeConstans.TYPE_PERIODICAL -> Paths.PAGE_WEB_PERIODICAL
                        ResourceTypeConstans.TYPE_ACTIVITY_TASK -> Paths.PAGE_WEB_ACTIVITY_TASK
                        ResourceTypeConstans.TYPE_KNOWLEDGE -> Paths.PAGE_WEB_KNOWLEDGE
                        ResourceTypeConstans.TYPE_ARTICLE -> Paths.PAGE_WEB_RECOMMEND_RESOURCE
                        ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> Paths.PAGE_WEB_RECOMMEND_RESOURCE
                        else -> Paths.PAGE_WEB_RESOURCE
                    }

                    if (resource._other?.keys?.contains(IntentParamsConstants.WEB_PARAMS_TASK_FINISH) == true) {
                        bundle.put(
                            IntentParamsConstants.STUDYROOM_FOLDER_ID,
                            resource._other?.get(IntentParamsConstants.STUDYROOM_FOLDER_ID)
                        )
                        routePath = Paths.PAGE_WEB_VERIFYCODE
                    }

                    //进入验证码页面的时候添加防连点,防止验证码重复弹出
                    if (routePath == Paths.PAGE_WEB_VERIFYCODE && !ClickFilterUtil.canClick()) {
                        return
                    }
                    ARouter.getInstance().build(routePath)
                        .with(bundle)
                        .navigation()

                }

                ResourceTypeConstans.TYPE_E_BOOK -> {
//                    toast("跳转到掌阅电子书页面")
                    ARouter.getInstance().build(Paths.PAGE_EBOOK_DETAIL)
                        .withString(IntentParamsConstants.EBOOK_PARAMS_ID, resource._resourceId)
                        .withInt(
                            IntentParamsConstants.PARAMS_RESOURCE_TYPE,
                            ResourceTypeConstans.TYPE_E_BOOK
                        )
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_URL, resource._resourceId)
                        .navigation()

                }
                ResourceTypeConstans.TYPE_STUDY_PLAN -> {
                    ARouter.getInstance().build(Paths.PAGE_STUDYPROJECT)
                        .withString(
                            IntentParamsConstants.STUDYPROJECT_PARAMS_ID,
                            resource._resourceId
                        )
                        .navigation()
                }
                ResourceTypeConstans.TYPE_SOURCE_FOLDER -> {


                    val fromAdmin =
                        resource._other?.get(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND)
                            ?: ""
                    //如果是运营推荐的
                    if ("true" == fromAdmin) {
                        ARouter.getInstance().build(Paths.PAGE_PUBLIC_STUDY_LIST)
                            .with(
                                Bundle().put(
                                    IntentParamsConstants.STUDYROOM_FOLDER_ID,
                                    resource._resourceId
                                )
                                    .put(
                                        IntentParamsConstants.STUDYROOM_FOLDER_NAME,
                                        resource._other?.get(IntentParamsConstants.STUDYROOM_FOLDER_NAME)
                                            ?: ""
                                    )
                                    .put(
                                        IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND,
                                        true
                                    )
                            )
                            .navigation()
                    } else {
                        ARouter.getInstance().build(Paths.PAGE_PUBLIC_STUDY_LIST)
                            .withString(
                                IntentParamsConstants.STUDYROOM_FOLDER_ID,
                                resource._resourceId
                            )
                            .withString(
                                IntentParamsConstants.STUDYROOM_FOLDER_NAME,
                                resource._other?.get(IntentParamsConstants.STUDYROOM_FOLDER_NAME)
                            )
                            .withString(
                                IntentParamsConstants.MY_USER_ID,
                                resource._other?.get(IntentParamsConstants.MY_USER_ID)
                            )
                            .withString(
                                IntentParamsConstants.STUDYROOM_FROM_FOLDER_ID,
                                resource._other?.get(IntentParamsConstants.STUDYROOM_FROM_FOLDER_ID)
                            )
                            .navigation()
                    }

                }

                ResourceTypeConstans.TYPE_COLUMN -> {
                    ARouter.getInstance().build(Paths.PAGE_COLUMN_LIST)
                        .withString(IntentParamsConstants.COLUMN_PARAMS_ID, resource._resourceId)
                        .navigation()

                }
                ResourceTypeConstans.TYPE_ALBUM -> {
                    ARouter.getInstance().build(Paths.PAGE_ALBUM)
                        .withString(IntentParamsConstants.ALBUM_PARAMS_ID, resource._resourceId)
                        .navigation()
                }
                ResourceTypeConstans.TYPE_TRACK -> {

                    ARouter.getInstance().build(Paths.PAGE_AUDIO_PLAY)
                        .withString(IntentParamsConstants.AUDIO_PARAMS_ID, resource._resourceId)
                        .withString(
                            IntentParamsConstants.ALBUM_PARAMS_ID,
                            resource._other?.get(IntentParamsConstants.ALBUM_PARAMS_ID)
                                ?: ""
                        )
//                        .withBoolean(IntentParamsConstants.AUDIO_PARAMS_SIGNLE_MODE, true)
                        .navigation()
                }
                //以前的栏目页面统一跳转到专题（合集）
//                ResourceTypeConstans.TYPE_BLOCK, //慎用，会有默认id，0的情况
                ResourceTypeConstans.TYPE_SPECIAL -> {
                    ARouter.getInstance().build(Paths.PAGE_RECOMMEND_SPECIAL)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, resource._resourceId)
                        .navigation()
                }
                ResourceTypeConstans.TYPE_ONESELF_TRACK -> { //自建音频
                    ARouter.getInstance().build(Paths.PAGE_AUDIO_OWN_BUILD_PLAY)
                        .withString(IntentParamsConstants.AUDIO_PARAMS_ID, resource._resourceId)
                        .navigation()
                }
                ResourceTypeConstans.TYPE_WX_PROGRAM -> { //跳转到微信小程序
                    ARouter.getInstance().build(Paths.PAGE_TO_WX_PROGRAM_DIALOG).navigation()
                }
                ResourceTypeConstans.TYPE_BATTLE -> { //跳转到对战
                    ARouter.getInstance().build(Paths.PAGE_BATTLE_MAIN).navigation()
                }
                ResourceTypeConstans.TYPE_PUBLICATION -> {
                    ARouter.getInstance().build(Paths.PAGE_PUBLICATION_DETAIL)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, resource._resourceId)
                        .navigation()
                }
                ResourceTypeConstans.TYPE_NOTE -> {  //笔记
                    ARouter.getInstance().build(Paths.PAGE_NODE)
                        .withString(IntentParamsConstants.INTENT_NODE_ID, resource._resourceId)
                        .navigation()
                }
                ResourceTypeConstans.TYPE_TASK -> {
                    ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS)
                        .withString(IntentParamsConstants.PARAMS_TASK_ID, resource._resourceId)
                        .navigation()
                }

                ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {
                    ARouter.getInstance().build(Paths.PAGE_KNOWLEDGE_MAIN)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, resource._resourceId)
                        .navigation()
                }

                ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE -> {
                    ARouter.getInstance().build(Paths.PAGE_FOLLOWUP_NEW)
                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, resource._resourceId)
                        .withString(
                            IntentParamsConstants.PARAMS_PARENT_ID,
                            resource._other?.get(IntentParamsConstants.PARAMS_PARENT_ID)
                        )
                        .withString(
                            IntentParamsConstants.PARAMS_PARENT_TYPE,
                            resource._other?.get(IntentParamsConstants.PARAMS_PARENT_TYPE)
                        )
                        .navigation()
                }

                ResourceTypeConstans.TYPE_CUSTOMER_SERVICE -> {//客服
                    //跳转到反馈详情页面 (目前是一个h5页面)
                    ARouter.getInstance().build(Paths.PAGE_WEB_FEED_BACK)
                        .withString(IntentParamsConstants.WEB_PARAMS_TITLE, "意见反馈")
                        .navigation()
                }

                //ResourceTypeConstans.TYPE_BLOCK->{ //推荐查看更多，现在跳到专题页面
//                    ARouter.getInstance().build(Paths.PAGE_RECOMMEND_LOOKMORE)
//                        .withString(IntentParamsConstants.PARAMS_RESOURCE_ID,resource._resourceId)
//                        .navigation()
//                }
            }
        }


        var lastTime = 0L

        /**
         * 跳转登录页，必须要添加SignleTOP Flag
         */
        @JvmStatic
        fun turnToLogin() {
            //防止部分机型跳转的时候context为空
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTime > 1000) {
                lastTime = currentTime
                try {
                    ARouter.getInstance().build(Paths.PAGE_LOGIN)
                        .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                } catch (e: Exception) {
//                    ARouter.getInstance().build(Paths.PAGE_LOGIN)
//                        .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK).navigation()
                }
            }
        }
    }
}