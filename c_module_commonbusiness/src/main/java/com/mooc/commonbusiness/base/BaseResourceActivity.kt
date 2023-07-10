package com.mooc.commonbusiness.base

import android.os.Bundle
import android.view.View
import androidx.lifecycle.*
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.manager.studylog.StudyLogManager
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.incpoints.AddPointManager
import com.mooc.commonbusiness.utils.incpoints.FirstAddPointManager
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.resource.widget.CommonTitleLayout
import org.json.JSONObject


/**
 * 学习资源基类页面
 * 统一实现举报，加入学习室，分享
 * 加积分，加学习记录等功能
 */
abstract class BaseResourceActivity : BaseActivity() {

    lateinit var baseResourceViewModel: BaseResourceViewModel
    lateinit var resourceId: String

    val titleLayoutData = MutableLiveData<CommonTitleLayout>()
    var bindShareData = false      //是否已经绑定了分享数据

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //
        resourceId = intent.getStringExtra(IntentParamsConstants.PARAMS_RESOURCE_ID) ?: ""
        baseResourceViewModel = ViewModelProviders.of(
            this,
        object : ViewModelProvider.AndroidViewModelFactory(AppGlobals.getApplication()!!){
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(BaseResourceViewModel::class.java)) {
                    return BaseResourceViewModel(resourceId,getResourceType()) as T
                }
                throw RuntimeException("unknown class :" + modelClass.name)
            }
        }
        )[BaseResourceViewModel::class.java]

        titleLayoutData.observe(this, Observer {
            initCommonTitle(it)
            if(!bindShareData && baseResourceViewModel.resourceShareDetaildata.value!=null){
                initShareAndAddStatus(baseResourceViewModel.resourceShareDetaildata.value!!)
            }
        })

        //分享数据
        baseResourceViewModel.resourceShareDetaildata.observe(this, Observer {
            if(titleLayoutData.value!=null){
                initShareAndAddStatus(it)
            }
        })

    }

    private fun initCommonTitle(titleLayout: CommonTitleLayout) {
        titleLayout.setOnLeftClickListener { finish() }
        if (titleLayout.tv_right?.text.toString() == "举报") {
            titleLayout.tv_right?.setOnClickListener {
                showReportDialog(resourceId, getResourceType(), "")
            }
        }
    }

    /**
     * 初始化分享和加入学习室状态
     */
    private fun initShareAndAddStatus(shareModel: ShareDetailModel){
        bindShareData = true
        val commonTitle = titleLayoutData.value
        if ("-1" != shareModel.share_status) {
            commonTitle?.setRightFirstIconRes(R.mipmap.common_ic_right_share_gray)
            commonTitle?.setOnRightIconClickListener { view ->
                showShareDialog(shareModel)
            }
        }

        //如果设置了加入学习室按钮
        if (commonTitle?.ib_right_second?.visibility == View.VISIBLE
            && commonTitle.rightSecondIcon == R.mipmap.common_ic_title_right_add
        ) {

            val rightIcon =
                if (shareModel.is_enroll == "1") R.mipmap.common_ic_title_right_added else R.mipmap.common_ic_title_right_add
            commonTitle.setRightSecondIconRes(rightIcon)

            //未加入设置点击事件
            if (shareModel.is_enroll != "1") {
                commonTitle.setOnSecondRightIconClickListener {
                    addToStudyRoom()
                }
            }
        }
    }


    fun showShareDialog(shareDetailModel: ShareDetailModel) {
        val commonBottomSharePop = CommonBottomSharePop(this, { platform ->

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


                val shareType = when(getResourceType()){
                    ResourceTypeConstans.TYPE_PERIODICAL->ShareTypeConstants.TYPE_PUBLICATION
                    else -> ""
                }
                shareAddScore.shareAddScore(shareType,
                    this, IShare.Builder()
                        .setWebUrl(shareDetailModel.weixin_url)
                        .setTitle(shareDetailModel.share_title ?: "")
                        .setMessage(shareDetailModel.share_desc ?: "")
                        .setImageUrl(shareDetailModel.share_picture ?: "")
                        .build()
                )
            }
        }, false)
        XPopup.Builder(this)
            .asCustom(commonBottomSharePop)
            .show()
    }

    /**
     * 展示举报弹窗
     */
    fun showReportDialog(resourceId: String, resourceType: Int, resourceTitle: String) {
        val put = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID, resourceId)
            .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, resourceType)
            .put(IntentParamsConstants.PARAMS_RESOURCE_TITLE, resourceTitle)
        ARouter.getInstance().build(Paths.PAGE_REPORT_DIALOG).with(put).navigation()
    }

    /**
     * 资源类型
     */
    abstract fun getResourceType(): Int


//    /**
//     * 获取加入学习室与分享状态
//     */
//    abstract fun getCommonTitle(): CommonTitleLayout

    /**
     * 加入学习室
     */
    private fun addToStudyRoom() {

        val jsonObject = JSONObject()
        jsonObject.put("url", resourceId)
        jsonObject.put("resource_type", getResourceType())
        jsonObject.put("other_resource_id", resourceId)

        //显示加入学习室弹窗
        val studyRoomService: StudyRoomService? =
            ARouter.getInstance().navigation(StudyRoomService::class.java)
        studyRoomService?.showAddToStudyRoomPop(this, jsonObject) {
            if (it) {
                val commonTitle = titleLayoutData.value
                //更新加入学习室状态
                val rightIcon = R.mipmap.common_ic_title_right_added
                commonTitle?.setRightSecondIconRes(rightIcon)
                commonTitle?.setOnSecondRightIconClickListener { }
            }
        }
    }

    /**
     * 学习资源增加积分
     */
    fun studyResourceAddScore() {
        AddPointManager.startAddPointsTask(
            this, "",
            getResourceType().toString(), resourceId
        )
    }

    /**
     * 每日首次加积分
     */
    fun everyDayfirstAddScore() {
        FirstAddPointManager.startAddFirstPointsTask(
            this, ResourceTypeConstans.typeAliasName[getResourceType()] ?: "", resourceId
        )
    }

    /**
     * 上传学习记录
     */
    fun postStudyLog() {
        val request = JSONObject()
        request.put("type", getResourceType())
        request.put("url", resourceId)
        request.put("title", "")
        StudyLogManager.postStudyLog(request)

    }

}