package com.mooc.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.util.getItemView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupPosition
import com.lxj.xpopup.interfaces.SimpleCallback
import com.mooc.common.ktextends.loge
import com.mooc.home.R
import com.mooc.home.databinding.HomeViewDiscoverCommonHeadBinding
import com.mooc.home.databinding.HomeViewDiscoverMoocHeadBinding
import com.mooc.home.ui.pop.CourseFilterAttachPop
import com.mooc.home.ui.pop.CoursePlatformDrawerPop
import com.mooc.home.ui.pop.CourseStatePop
//import kotlinx.android.synthetic.main.home_view_discover_mooc_head.view.*


class DiscoverMoocHeadView @JvmOverloads constructor(
    var mContext: Context,
    var attributeSet: AttributeSet? = null,
    var defaultInt: Int = 0
) :
    FrameLayout(mContext, attributeSet, defaultInt) {

    private var inflater : HomeViewDiscoverMoocHeadBinding =
        HomeViewDiscoverMoocHeadBinding.inflate(LayoutInflater.from(context),this,true)
    init {
//        LayoutInflater.from(mContext).inflate(R.layout.home_view_discover_mooc_head, this)


        //点击平台
        inflater.tvPlatform.setOnClickListener {
            showPlatformCatelogPop()
        }

        //点击开课状态
        inflater.tvState.setOnClickListener {
            showCourseStateChoosePop()
        }

        //点击筛选
        inflater.tvFilter.setOnClickListener {
            showCourseTypeChoosePop()
        }
    }


    /**
     * 点击平台选择回调
     */
    var onPlatformCheckCallback: ((str: String) -> Unit)? = null

    /**
     * 点击课程状态回调
     */
    var onCourseStateCallback: ((state: String) -> Unit)? = null

    /**
     * 点击课程类型选择回调
     */
    var onCourseTypeCheckCallback: ((is_free: String, verified_active: String, is_have_exam: String) -> Unit)? =
        null

    //侧边栏弹窗
    var coursePlatformPop: CoursePlatformDrawerPop? = null

    /**
     * 左边间距（当出现有三级菜单的时候，应该距离左边78dp）
     */
    var leftMargin = 0

    /**
     * 展示课程平台侧边栏弹窗
     */
    private fun showPlatformCatelogPop() {
//        mContext.startActivity(Intent(mContext,CoursePlatFormActivity::class.java))
        if (coursePlatformPop == null) {
            //设置点击确认回调
            val platformDrawerLayout = CoursePlatformDrawerPop(mContext)
            platformDrawerLayout.onCheckConfirmCallback = {
                val stringBuilder = StringBuilder()
                it.forEach() {
                    stringBuilder.append(it.code)
                    stringBuilder.append(",")
                }

                if (stringBuilder.isEmpty()) {
                    //改变平台选中颜色
                    changePlatFormTextColor("")
                    onPlatformCheckCallback?.invoke("")
                }

                if (stringBuilder.isNotEmpty()) {
                    val platFormCodes =
                        stringBuilder.substring(0, stringBuilder.length - 1).toString()
                    loge(platFormCodes)

                    //改变平台选中颜色
                    changePlatFormTextColor(platFormCodes)
                    onPlatformCheckCallback?.invoke(platFormCodes)
                }


//                loge("allOtherPlatformsCode: $stringBuilder")

                coursePlatformPop?.dismiss()
            }
            //构建弹窗
            coursePlatformPop = XPopup.Builder(mContext)
                .popupPosition(PopupPosition.Left) //左边
                .hasStatusBarShadow(true) //启用状态栏阴影
                .asCustom(platformDrawerLayout) as CoursePlatformDrawerPop
        }
        coursePlatformPop?.show()

    }

    private fun changePlatFormTextColor(it: String) {
        val color = if (it.isEmpty()) R.color.color_2 else R.color.colorPrimary
        inflater.tvPlatform.setTextColor(ContextCompat.getColor(mContext, color))
    }

    //课程状态选择弹窗
    var courseStatePop: CourseStatePop? = null

    /**
     * 展示课程状态选择弹窗
     */
    private fun showCourseStateChoosePop() {
        //课程状态数组
//        val courseStateArray = arrayOf("全部", "已开课", "即将开课", "已结束")
        if (courseStatePop == null) {
            courseStatePop = XPopup.Builder(mContext)
                .atView(this) // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asCustom(CourseStatePop(mContext)) as CourseStatePop?
            courseStatePop?.onStateSelectCallback = {
                when (it) {//默认2全部，0即将开课，1已开课，-1已结束
                    "2" -> {
                        inflater.tvState.setText("全部")
                    }
                    "1" -> {
                        inflater.tvState.setText("已开课")
                    }
                    "0" -> {
                        inflater.tvState.setText("即将开课")
                    }
                    "-1" -> {
                        inflater.tvState.setText("已结课")
                    }
                    else -> {
                        inflater.tvState.setText("全部")
                    }
                }
                onCourseStateCallback?.invoke(it)
            }

        }
        courseStatePop?.showWithMargin(leftMargin)

    }

    //课程类型选择
    var courseTypePop: CourseFilterAttachPop? = null

    /**
     * 展示课程类型选择弹窗
     */
    private fun showCourseTypeChoosePop() {
        if (courseTypePop == null) {
            val courseFilterAttachPop = CourseFilterAttachPop(mContext)
            courseTypePop = XPopup.Builder(mContext)
                .setPopupCallback(object : SimpleCallback() {
                    override fun onDismiss(popupView: BasePopupView?) {
                        loge(
                            courseFilterAttachPop.is_free,
                            courseFilterAttachPop.is_have_exam,
                            courseFilterAttachPop.verified_active
                        )
                        onCourseTypeCheckCallback?.invoke(
                            courseFilterAttachPop.is_free,
                            courseFilterAttachPop.verified_active,
                            courseFilterAttachPop.is_have_exam
                        )
                    }
                })
                .atView(this) // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asCustom(courseFilterAttachPop) as CourseFilterAttachPop?
        }
        courseTypePop?.showWithMargin(leftMargin)
//        courseTypePop?.show()
    }


    /**
     * 重置所有的选中状态
     */
    fun resetState() {
//        platformPop?.destroy()
//        platformPop = null
//        courseStatePop?.destroy()
//        courseStatePop = null
//        courseTypePop?.destroy()
//        courseTypePop = null
    }
}