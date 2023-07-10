package com.mooc.studyroom.ui.pop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.mooc.common.ktextends.gone
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.studyroom.R
import com.mooc.studyroom.model.MedalDataBean
import java.text.SimpleDateFormat

class ShowMedalBigPop(private val mContext: Context, private val parent: View) :
    PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    private var container: RelativeLayout? = null
    private lateinit var ivMedalContent: ImageView
    private var tvMedalContent: TextView? = null
    private var tvMedalTime: TextView? = null
    private var tvMedalShare: TextView? = null
    private var tvLookMicroKnowShareBig: TextView? = null
    var medalBean: MedalDataBean? = null

    //    private var callBack: MedalBigCallBack? = null
    var medalBigCallBack: ((medalBean: MedalDataBean?) -> Unit)? = null

//    fun setMedalBean(medalBean: MedalDataBean?) {
//        this.medalBean = medalBean
//    }
//
//    fun setCallBack(callBack: MedalBigCallBack?) {
//        this.callBack = callBack
//    }

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext)
            .inflate(R.layout.studyroom_my_pop_show_big_medal, null) as RelativeLayout?
        ivMedalContent = container?.findViewById<View>(R.id.iv_medal_big) as ImageView
        tvMedalContent = container?.findViewById<View>(R.id.tv_medal_big) as TextView
        tvMedalTime = container?.findViewById<View>(R.id.tv_medal_time_big) as TextView
        tvMedalShare = container?.findViewById<View>(R.id.tv_medal_share_big) as TextView
        tvLookMicroKnowShareBig =
            container?.findViewById<View>(R.id.tvLookMicroKnowShareBig) as TextView
    }

    private fun initData() {
        if (medalBean != null) {
            if (medalBean?.is_obtain.equals("1")) {
                Glide.with(mContext)
                    .load(medalBean?.after_img)
                    .placeholder(R.mipmap.studyroom_ic_medal_loading)
                    .error(R.mipmap.studyroom_ic_medal_loading)
                    .into(ivMedalContent)
                tvMedalShare?.visiable(true)
                if (medalBean?.medal_time?.equals(0) != null && !medalBean?.medal_time?.equals(0)!!) {
                    tvMedalTime?.visiable(true)
                    tvMedalTime?.text = String.format(
                        mContext.resources.getString(R.string.medal_time_get),
                        StringFormatUtil.timeToString(medalBean?.medal_time)
                    )
                } else {
                    tvMedalTime?.visiable(false)
                }
            } else {
                Glide.with(mContext)
                    .load(medalBean?.before_img)
                    .placeholder(R.mipmap.studyroom_ic_medal_loading)
                    .error(R.mipmap.studyroom_ic_medal_loading)
                    .into(ivMedalContent)
                tvMedalShare?.gone(true)
                tvMedalTime?.gone(true)
            }

            tvMedalContent?.text = medalBean?.title
            //微知识显示 查看微知识按钮
            if (medalBean?.resource_type == ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE) {
                tvLookMicroKnowShareBig?.visibility = View.VISIBLE
            } else {
                tvLookMicroKnowShareBig?.visibility = View.GONE
            }
        }
    }

    private fun timeToString(time: Long): String {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        return dateFormat.format(time * 1000)
    }

    private fun initListener() {
        container?.setOnClickListener {
            if (mPopup != null) {
                mPopup?.dismiss()
            }
        }
        tvMedalShare?.setOnClickListener {
            medalBigCallBack?.invoke(medalBean)
            tvMedalShare?.isEnabled = false
            if (mPopup != null) {
                mPopup?.dismiss()
            }

        }
        //跳转到微知识详情页面
        tvLookMicroKnowShareBig?.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_KNOWLEDGE_MAIN)
                .withString(IntentParamsConstants.PARAMS_RESOURCE_ID, medalBean?.resource_id)
                .navigation()
            if (mPopup != null) {
                mPopup?.dismiss()
            }

        }
    }

    private fun initPopup() {
        mPopup = PopupWindow(
            container,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )
        mPopup?.contentView = container
        mPopup?.setBackgroundDrawable(ColorDrawable(0))
        mPopup?.isOutsideTouchable = true
        mPopup?.setOnDismissListener(this)
    }

    fun show() {
        initView()
        initData()
        initListener()
        if (mPopup == null) {
            initPopup()
        }
        setBackgroundAlpha(0.5f)
        mPopup?.showAtLocation(parent, Gravity.CENTER, 0, 0)
    }

    private fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (mContext as Activity).window
            .attributes
        lp.alpha = bgAlpha
        mContext.window.attributes = lp
    }

    override fun onDismiss() {
        setBackgroundAlpha(1.0f)
    }

//    interface MedalBigCallBack {
//        fun shareContent(medalBean: MedalDataBean?)
//    }
}