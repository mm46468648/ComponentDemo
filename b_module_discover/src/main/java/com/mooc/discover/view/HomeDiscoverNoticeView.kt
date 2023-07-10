package com.mooc.discover.view

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.AnnouncementBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.databinding.DiscoverViewNoticeBinding

class HomeDiscoverNoticeView @JvmOverloads constructor(
    var mContext: Context,
    var attributeSet: AttributeSet? = null,
    var defaultInt: Int = 0
) :
    FrameLayout(mContext, attributeSet, defaultInt) {


    val binding = DiscoverViewNoticeBinding.inflate(LayoutInflater.from(getContext()), this, true);

    init {
//        LayoutInflater.from(mContext).inflate(R.layout.discover_view_notice,this)
        visibility = GONE
        SkinManager.getInstance().injectSkin(this)
    }

    /**
     * 设置公告列表
     */
    fun setNoticeList(list: List<AnnouncementBean>?) {
        binding.viewFlipper.stopFlipping()
        binding.viewFlipper.removeAllViews()
        if (list == null || list.isEmpty()) {
            visibility = View.GONE
            return
        }
        list.forEach {
            val createTextView = createTextView(it)
            binding.viewFlipper.addView(createTextView)
        }

        //开启或停止轮播
        if (list.size > 1) {
            binding.viewFlipper.setFlipInterval(5 * 1000)
            binding.viewFlipper.startFlipping()
        } else {
            binding.viewFlipper.stopFlipping()
        }

        visibility = View.VISIBLE
    }

    private fun createTextView(announcementBean: AnnouncementBean): TextView {
        val textView = TextView(mContext)
        textView.gravity = Gravity.CENTER_VERTICAL
        textView.setSingleLine()
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.textSize = 4.dp2px().toFloat()
//        textView.typeface = Typeface.DEFAULT_BOLD


        //换肤
        val skinColor =
            SkinManager.getInstance().resourceManager.getColor("colorPrimary")
        textView.setTextColor(skinColor)
        textView.text = announcementBean.title
        textView.setOnClickListener {
            //携带id跳转到公告详情页面
            ARouter.getInstance()
                .build(Paths.PAGE_ANNOUNCEMENT_DETAIL)
                .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, announcementBean.id)
                .withString(IntentParamsConstants.WEB_PARAMS_TITLE, announcementBean.title)
                .navigation()
        }
        return textView
    }
}