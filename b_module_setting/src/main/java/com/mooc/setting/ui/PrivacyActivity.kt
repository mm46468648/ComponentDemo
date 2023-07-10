package com.mooc.setting.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.dialog.CustomProgressDialog
import com.mooc.commonbusiness.dialog.CustomProgressDialog.Companion.createLoadingDialog
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.eventbus.UserSettingChangeEvent
import com.mooc.commonbusiness.model.setting.UserSettingBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.resource.widget.CommonSettingItem
import com.mooc.setting.R
import com.mooc.setting.databinding.ActivityPrivacyBinding
import com.mooc.setting.viewmodel.UserSettingViewModel
//import kotlinx.android.synthetic.main.activity_privacy.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

@Route(path = Paths.SERVICE_SETTING_PRIVACY)
class PrivacyActivity : BaseActivity() {

    private var baseSettingBool: Boolean = true          //允许个性化推荐
    private var isBaseSetting: Boolean = false       //是否是允许个性化推荐修改

    private var likeRecommendBool: Boolean = true       //发现页感兴趣内容推荐
    private var isLikeRecommend: Boolean = false    //是否是发现页感兴趣内容推荐修改

    private var resourceRecommendBool: Boolean = true   //资源详情页相关资源推荐
    private var isResourceRecommend: Boolean = false  //是否是资源详情页相关资源推荐修改

    private var columnRecommendBool: Boolean = false     //首页栏目推荐
    private var isColumnRecommend: Boolean = false  //是否是首页栏目推荐修改

    private var dialog: CustomProgressDialog? = null

    private val mViewModel by viewModels<UserSettingViewModel>()

    private lateinit var inflater : ActivityPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initData()
        initListener()

    }

    private fun initData() {
        inflater.baseSetting.showLine = false
        dialog = createLoadingDialog(this, true)
        dialog?.show()
        mViewModel.getData()

        //初始化数据
        mViewModel.getSettingData.observe(this, {
            if (dialog != null) {
                dialog?.dismiss()
            }
            upData(it)
        })

        //修改设置之后
        mViewModel.updateSettingData.observe(this, {
            if (dialog != null) {
                dialog?.dismiss()
            }
            if (isBaseSetting) {//修改了允许个性化推荐
                EventBus.getDefault().post(UserSettingChangeEvent(it, 1))//通知推荐页面，更新猜你喜欢接口 推荐接口
            }
            if (isLikeRecommend) {//修改了发现页感兴趣内容推荐
                EventBus.getDefault().post(UserSettingChangeEvent(it, 2))//通知推荐页面，更新猜你喜欢接口
            }
            if (isColumnRecommend) {//修改了首页栏目推荐
                EventBus.getDefault().post(UserSettingChangeEvent(it, 3))//通知推荐页面，更新推荐接口
            }

            upData(it)
        })

        mViewModel.errorException.observe(this, {
            if (dialog != null) {
                dialog?.dismiss()
            }
        })
    }

    //更新数据
    private fun upData(it: UserSettingBean) {
        baseSettingBool = it.privacy_setting?.base_setting ?: true
        likeRecommendBool = it.privacy_setting?.like_recommend ?: true
        resourceRecommendBool = it.privacy_setting?.resource_recommend ?: true
        columnRecommendBool = it.privacy_setting?.column_recommend ?: false

        //隐藏资源详情页相关资源推荐及首页栏目推荐
        if (baseSettingBool) {
            inflater.likeSetting.visibility = View.VISIBLE
            inflater.resourceSetting.visibility = View.VISIBLE
            inflater.columnSetting.visibility = View.VISIBLE
        } else {
            inflater.likeSetting.visibility = View.GONE
            inflater.resourceSetting.visibility = View.GONE
            inflater.columnSetting.visibility = View.GONE
        }
        setViewSetting(inflater.baseSetting, baseSettingBool)
        setViewSetting(inflater.likeSetting, likeRecommendBool)
        setViewSetting(inflater.resourceSetting, resourceRecommendBool)
        setViewSetting(inflater.columnSetting, columnRecommendBool)
    }

    private fun initListener() {

        //个性化推荐修改
        inflater.baseSetting.setRightTextClickFunction {
            isBaseSetting = true
            isLikeRecommend = false
            isResourceRecommend = false
            isColumnRecommend = false
            baseSettingBool = !baseSettingBool
            upSettingStatus(baseSettingBool)
        }

        //发现页感兴趣内容推荐修改
        inflater.likeSetting.setRightTextClickFunction {
            isBaseSetting = false
            isLikeRecommend = true
            isResourceRecommend = false
            isColumnRecommend = false
            likeRecommendBool = !likeRecommendBool
            upSettingStatus(likeRecommendBool)

        }

        //资源详情页相关资源推荐
        inflater.resourceSetting.setRightTextClickFunction {
            isBaseSetting = false
            isLikeRecommend = false
            isResourceRecommend = true
            isColumnRecommend = false
            resourceRecommendBool = !resourceRecommendBool
            upSettingStatus(resourceRecommendBool)
        }

        //发现页栏目按个人阅读次数排序
        inflater.columnSetting.setRightTextClickFunction {
            isBaseSetting = false
            isLikeRecommend = false
            isResourceRecommend = false
            isColumnRecommend = true
            columnRecommendBool = !columnRecommendBool
            upSettingStatus(columnRecommendBool)
        }

        inflater.commonTitle.setOnLeftClickListener { finish() }

    }

    //打开推荐不需要弹窗
    private fun upSettingStatus(settingBoolean: Boolean) {
        if (settingBoolean) {
            upPostData()
        } else {
            showLogoutDialog()
        }
    }

    /**
     * 显示隐私设置弹窗
     */
    private fun showLogoutDialog() {
        val publicDialogBean = PublicDialogBean()
        val message: String = getString(R.string.text_str_close_setting)
        publicDialogBean.strMsg = message
        publicDialogBean.strLeft = getString(R.string.text_ok)
        publicDialogBean.strRight = resources.getString(R.string.text_cancel)
        publicDialogBean.isLeftGreen = 0
        XPopup.Builder(this)
                .dismissOnBackPressed(false)
                .dismissOnTouchOutside(false)
                .asCustom(PublicDialog(this, publicDialogBean) {
                    if (it == 0) {
                        upPostData()
                    } else {//点击取消还原修改的值
                        if (isBaseSetting) {
                            baseSettingBool = !baseSettingBool
                            isBaseSetting = !isBaseSetting
                        }
                        if (isLikeRecommend) {
                            likeRecommendBool = !likeRecommendBool
                            isLikeRecommend = !isLikeRecommend
                        }
                        if (isResourceRecommend) {
                            resourceRecommendBool = !resourceRecommendBool
                            isResourceRecommend = !isResourceRecommend
                        }
                        if (isColumnRecommend) {
                            columnRecommendBool = !columnRecommendBool
                            isBaseSetting = !isBaseSetting
                        }
                    }
                })
                .show()
    }


    private fun upPostData() {
        if (dialog == null) {
            dialog = createLoadingDialog(this, true)
        }
        dialog?.show()
        val requestDataData = JSONObject()
        requestDataData.put("base_setting", baseSettingBool)
        requestDataData.put("like_recommend", likeRecommendBool)
        requestDataData.put("resource_recommend", resourceRecommendBool)
        requestDataData.put("column_recommend", columnRecommendBool)
        val requestData = JSONObject()
        requestData.put("privacy_setting", requestDataData)
        val stringBody = requestData.toString().toRequestBody("application/json;charset=utf-8".toMediaType())
        mViewModel.postData(stringBody)
    }


    /**
     * 设置开关状态
     */
    private fun setViewSetting(view: CommonSettingItem, open: Boolean) {
        val openRes = if (open) R.mipmap.set_ic_switch_open else R.mipmap.set_ic_switch_close
        view.rightIcon = openRes
    }


}