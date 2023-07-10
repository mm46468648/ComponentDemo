package com.mooc.webview.business

//import kotlinx.android.synthetic.main.webview_activity_webview.*
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.constants.*
import com.mooc.commonbusiness.manager.studylog.StudyLogManager
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.pop.CommonMenuPopupW
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.route.routeservice.VideoActionService
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.incpoints.AddPointManager
import com.mooc.commonbusiness.utils.incpoints.FirstAddPointManager
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.webview.R
import com.mooc.webview.WebviewActivity
import com.mooc.webview.stratage.WebViewInitStrategy
import com.mooc.webview.viewmodel.ResourceWebViewModel
import org.json.JSONObject

/**
 * 资源h5页面基类，
 * 统一处理资源相关的操作
 * （增加积分，分享，加学习室等逻辑）
 */
@Route(path = Paths.PAGE_WEB_RESOURCE)
open class BaseResourceWebviewActivity : WebviewActivity() {
    //    val resId by extraDelegate(IntentParamsConstants.PARAMS_RESOURCE_ID, "")
//    val mViewModel: ResourceWebViewModel by lazy {
//        ViewModelProviders.of(this)[ResourceWebViewModel::class.java]
//    }
    val mViewModel: ResourceWebViewModel by viewModels()

    val shareService: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    val videoActionService: VideoActionService by lazy {
        ARouter.getInstance().build(Paths.SERVICE_COURSE_VIDEO_ACTION)
            .navigation() as VideoActionService
    }

    var coursePlatForm = -1 //课程平台

    override fun onCreate(savedInstanceState: Bundle?) {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        super.onCreate(savedInstanceState)

        onResourceShow()
    }


    /**
     * 学习资源单独逻辑
     */
    protected open fun onResourceShow() {

        //如果是课程资源，
        if (resourceType == ResourceTypeConstans.TYPE_COURSE) {
            //记录课程平台，后续使用
            coursePlatForm = intent.getIntExtra(IntentParamsConstants.COURSE_PARAMS_PLATFORM, -1)
            //要开启观看30分钟提示，并且记录行为打点
            val classRoomId =
                intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID) ?: ""
            videoActionService.setPageInfo(this, resourceID, classRoomId)
            videoActionService.startTime()
        }

        //如果是内置资源，显示资源对应的文字 (友情链接，测试卷除外)
        if (ResourceTypeConstans.typeStringMap.containsKey(resourceType)) {
            val showTitle = when (resourceType) {
                ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> "文章"  //专栏文章标题改为文章
                //测试卷等显示标题
                ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK,
                ResourceTypeConstans.TYPE_TEST_VOLUME -> title
                //微专业id不同显示不同的标题
                ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL -> {
                    when (resourceID) {
                        "1" -> {
                            "微专业"
                        }
                        "2" -> {
                            "鉴定理论培训"
                        }
                        else -> {
                            "微专业"
                        }
                    }
                }
                else -> ResourceTypeConstans.typeStringMap[resourceType]
            }
            inflater.commonTitleLayout.middle_text = showTitle
            setCommonTitle()
        }

        //如果是别名中的资源，需要调用首次增加积分
        if (ResourceTypeConstans.typeAliasName.containsKey(resourceType)) {
            val url: String =
                if (resourceType == ResourceTypeConstans.TYPE_MICRO_LESSON
                    || resourceType == ResourceTypeConstans.TYPE_COURSE
                ) resourceID else loadUrl
            //每日首次增加积分
            FirstAddPointManager.startAddFirstPointsTask(
                this, ResourceTypeConstans.typeAliasName[resourceType] ?: "", url, 500
            )

            //学习资源增加积分  因为上线绑定学习清单的任务，取消文章学习积分（不纳入每天30分的奖励范围）
            //现在任务在3.4.5版本隐藏，所以加积分的要放开,3.4.6版本改成接口继续请求，是否加积分后台控制
            AddPointManager.startAddPointsTask(
                this, title, resourceType.toString(), url
            )

            /**
             * 上传学习记录
             */
            val request = JSONObject()
            request.put("type", resourceType)
            request.put("url", url)
            request.put("title", title)
            StudyLogManager.postStudyLog(request)

            //获取分享及加入学习空间的状态
            if (resourceType == ResourceTypeConstans.TYPE_BAIKE) {
                //百科没有资源id，需要用url查询另一个接口
                getAddStudyRoomState()
            } else {
                getShareInfo(resourceID)
            }

        }

        //问卷 , 微专业 需要分享
        if (resourceType == ResourceTypeConstans.TYPE_QUESTIONNAIRE || resourceType == ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL) {
            getShareInfo(resourceID)
        }
    }

    /**
     * 对于没有资源id的资源
     * 根据url获取加入学习空间状态
     */
    fun getAddStudyRoomState() {
        mViewModel.getStudyroomEnrollState(resourceType, loadUrl).observe(this, Observer {
            if (!it.is_enrolled) {
                inflater.commonTitleLayout.setRightSecondIconRes(R.mipmap.common_ic_title_right_add)
            } else {
                inflater.commonTitleLayout.setRightSecondIconRes(R.mipmap.common_ic_title_right_added)
            }
            inflater.commonTitleLayout.setOnSecondRightIconClickListener {
                if (!it.is_enrolled) {
                    //调用加入学习室弹窗
                    mViewModel.postBaikeAddToStudyRoom(
                        loadUrl,
                        resourceType.toString(),
                        title,
                        this
                    ).observe(this, Observer { success ->
                        if (success) {
                            inflater.commonTitleLayout.setRightSecondIconRes(R.mipmap.common_ic_title_right_added)
                        }
                    })
                }
            }
        })

        //设置百科的分享
//        val watchView = XPopup.Builder(this).watchView(inflater.commonTitleLayout.ib_right)
        inflater.commonTitleLayout.setRightFirstIconRes(R.mipmap.common_ic_title_right_menu)
        //去分享
        inflater.commonTitleLayout.setOnRightIconClickListener(View.OnClickListener { _ ->
            showMenuPop(
                title,
                intent.getStringExtra(IntentParamsConstants.BAIKE_PARAMS_SUMMARY) ?: "",
                loadUrl,
                ""
            )
        })


    }

    /**
     * 获取分享信息
     */
    open fun getShareInfo(resourceId: String) {
        if (resourceId == "0" || resourceId.isEmpty()) return

        //新学堂不需要查询分享信息
        if (resourceType == ResourceTypeConstans.TYPE_COURSE && coursePlatForm == CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT) {
            return
        }
        mViewModel.getShareDetailData(resourceType.toString(), resourceId)
        mViewModel.articleWebShareDetaildata.observe(this, Observer {
            if (resourceType == ResourceTypeConstans.TYPE_QUESTIONNAIRE) {
                inflater.commonTitleLayout.ib_right_second?.visiable(false)
            } else {
                //is_enroll  是否加入学习 0未加入 1加入
                //设置加入学习室逻辑
                if ("0" == it.is_enroll) {
                    inflater.commonTitleLayout.setRightSecondIconRes(R.mipmap.common_ic_title_right_add)
                } else {
                    inflater.commonTitleLayout.setRightSecondIconRes(R.mipmap.common_ic_title_right_added)
                }

            }

            inflater.commonTitleLayout.setOnSecondRightIconClickListener {
                if ("0" == it.is_enroll) {
                    //调用加入学习室弹窗
                    mViewModel.postAddToStudyRoom(
                        loadUrl,
                        resourceType.toString(),
                        resourceID,
                        this
                    )
                }
            }
            //share_status  是否分享 0是 -1否
            if ("0" == it.share_status) {
//                val watchView = XPopup.Builder(this).watchView(inflater.commonTitleLayout.ib_right)
                inflater.commonTitleLayout.setRightFirstIconRes(R.mipmap.common_ic_title_right_menu)
                if (resourceType == ResourceTypeConstans.TYPE_QUESTIONNAIRE && it.share_title.isNullOrEmpty()) {
                    //处理问卷为空，没有title
                    it.share_title = "问卷"
                }
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

    /**
     * 设置标题
     */
    fun setCommonTitle() {
        //期刊，百科，活动任务,微专业没有举报按钮
        if (resourceType == ResourceTypeConstans.TYPE_PERIODICAL
            || resourceType == ResourceTypeConstans.TYPE_BAIKE
            || resourceType == ResourceTypeConstans.TYPE_ACTIVITY_TASK
            || resourceType == ResourceTypeConstans.TYPE_TEST_VOLUME
            || resourceType == ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK
            || resourceType == ResourceTypeConstans.TYPE_KNOWLEDGE
            || resourceType == ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL
        ) return

        inflater.commonTitleLayout.tv_right?.text = "举报"
        if (resourceType == ResourceTypeConstans.TYPE_TEST_VOLUME) {
            inflater.commonTitleLayout.tv_right?.visiable(false)
        } else {
            inflater.commonTitleLayout.tv_right?.visiable(true)

        }
        inflater.commonTitleLayout.tv_right?.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )
        )
        inflater.commonTitleLayout.tv_right?.setOnClickListener {
            val put = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID, resourceID)
                .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, resourceType)
                .put(IntentParamsConstants.PARAMS_RESOURCE_TITLE, title)
            ARouter.getInstance().build(Paths.PAGE_REPORT_DIALOG).with(put).navigation()
        }

//        //如果问卷标题过长超过8个字就换成...
//        if (resourceType == ResourceTypeConstans.TYPE_QUESTIONNAIRE && title.length>8){
//            inflater.commonTitleLayout.middle_text = StringFormatUtil.elipsizeEnd(title,8)
//        }
    }

    /**
     * 展示菜单框
     */
    fun showMenuPop(
        shareTitle: String,
        shareDesc: String,
        loadUrl: String,
        sharePic: String
    ) {
//        val commonTitleMenuPop = CommonTitleMenuPop(this, arrayListOf("分享"))
        val commonTitleMenuPop =
            CommonMenuPopupW(this, arrayListOf("分享"), inflater.commonTitleLayout)
        commonTitleMenuPop.onTypeSelect = {
            if (it == "分享") {
                showChoseDialog(shareTitle, shareDesc, loadUrl, sharePic)
            }
        }
        commonTitleMenuPop.show()
    }

    /**
     * 弹出来选择弹框
     */
    private fun showChoseDialog(tit: String, msg: String, url: String, sharePic: String) {
        val commonBottomSharePop = CommonBottomSharePop(this, {
            if (it == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {
                //如果resouceId为空或者为0时，需要获取分享内容后再分享到学友圈，如百科
                if (TextUtils.isEmpty(resourceID) || "0" == resourceID) {
                    val requestData = JSONObject()
                    requestData.put("title", title)
                    requestData.put("url", loadUrl)
                    requestData.put("resource_type", resourceType.toString())
                    requestData.put("source", "0")
                    requestData.put("status", "true")
                    requestData.put("content", msg)
                    mViewModel.getShareDataAndPost(this, requestData)
                } else {
                    ShareSchoolUtil.postSchoolShare(
                        this,
                        resourceType.toString(),
                        resourceID,
                        loadUrl
                    )
                }
            } else {


                val build = IShare.Builder()
                    .setSite(it)
                    .setImageUrl(sharePic)
                    .setTitle(tit)
                    .setMessage(msg)
                    .setWebUrl(url)
                    .build()

                val shareType = when (resourceType) {
                    ResourceTypeConstans.TYPE_COLUMN_ARTICLE -> ShareTypeConstants.TYPE_ARTICAL
                    ResourceTypeConstans.TYPE_ARTICLE -> ShareTypeConstants.TYPE_ARTICAL
                    ResourceTypeConstans.TYPE_PERIODICAL -> ShareTypeConstants.TYPE_PERIODICAL
                    ResourceTypeConstans.TYPE_COURSE -> ShareTypeConstants.TYPE_COURSE
                    ResourceTypeConstans.TYPE_BAIKE -> ShareTypeConstants.TYPE_BAIKE
                    ResourceTypeConstans.TYPE_MICRO_LESSON -> ShareTypeConstants.TYPE_MICRO_COURSE
                    ResourceTypeConstans.TYPE_QUESTIONNAIRE -> ShareTypeConstants.TYPE_QUESTIONNAIRE
                    else -> ""
                }

                if (shareType.isEmpty()) {
                    shareService.share(this, build)
                } else {
                    shareService.shareAddScore(shareType, this, build)
                }
            }
        }, resourceType == ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL)
        XPopup.Builder(this)
            .asCustom(commonBottomSharePop)
            .show()

    }

    override fun onPressBack() {
        //如何是工信部的课程或者百科,或者友情链接直接返回
        if (resourceType == ResourceTypeConstans.TYPE_BAIKE || coursePlatForm == CoursePlatFormConstants.COURSE_PUBLIC_MESSAGE
            || resourceType == ResourceTypeConstans.TYPE_RECOMMEND_OUT_LINK
        ) {
            finish()
            return
        }
        super.onPressBack()
    }


    override fun onPause() {
        super.onPause()

        //息屏暂停计时
        if (resourceType == ResourceTypeConstans.TYPE_COURSE && videoActionService.getTimerStatus()) {
            videoActionService.pauseTime()
        }
    }

    override fun onResume() {
        super.onResume()

        //获取焦点重新计时
        if (resourceType == ResourceTypeConstans.TYPE_COURSE && !videoActionService.getTimerStatus()) {
            videoActionService.startTime()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (resourceType == ResourceTypeConstans.TYPE_COURSE) {
            videoActionService.release()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //选择图片
        if (requestCode == WebViewInitStrategy.CHOOSE_FILE_REQUEST_CODE) {
            webviewWrapper.strategy.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)


    }
}