package com.mooc.commonbusiness.pop.studyroom

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.mooc.commonbusiness.R

class CertificationPop(private val mContext: Activity, val imgUrl: String) : PopupWindow(mContext), PopupWindow.OnDismissListener {
    private var mLayoutInflater: LayoutInflater? = null
    private var mContentView: View? = null
    private var tvCancle: ImageView? = null
    private fun initView() {
        mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mContentView = mLayoutInflater!!.inflate(R.layout.studyroom_pop_certification, null)
        contentView = mContentView
        // 设置SelectPicPopupWindow弹出窗体的宽
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
//        val screenHeigh = mContext.resources.displayMetrics.heightPixels
//        height = Math.round(screenHeigh * 0.25f)
        //        setAnimationStyle(R.style.PeriodicalWindow);
        setBackgroundDrawable(ColorDrawable()) //设置背景只有设置了这个才可以点击外边和BACK消失
        //        backgroundAlpha(mContext, 0.5f);//0.0-1.0
//        setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_pop_bg));
        isOutsideTouchable = true //点击外边消失
        setOnDismissListener(this)
        isFocusable = true //设置可以获取集点
        isTouchable = true
        initViews()
        initData()
        initListener()
    }

    private fun initListener() {
        tvCancle?.setOnClickListener {
            dismiss()
        }
    }

    private fun initViews() {
        val img_certification = mContentView?.findViewById<ImageView>(R.id.img_certification)
         tvCancle = mContentView?.findViewById<ImageView>(R.id.imgbt_certificate_close)
        if (img_certification != null) {
            Glide.with(mContext).load(imgUrl).placeholder(R.mipmap.studyroom_bg_certification_loading).
            error(R.mipmap.studyroom_bg_certification_loading).into(img_certification)
        }
    }

    private fun initData() {}
    override fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    fun backgroundAlpha(context: Activity, bgAlpha: Float) {
        val lp = context.window.attributes
        lp.alpha = bgAlpha
        context.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        context.window.attributes = lp
    }

    override fun onDismiss() {
//        mView.setVisibility(View.GONE);
        backgroundAlpha(mContext, 1f)
    }


    init {
        initView()
    }
}