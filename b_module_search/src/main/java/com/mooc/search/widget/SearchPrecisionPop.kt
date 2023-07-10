package com.mooc.search.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.mooc.search.R
import com.mooc.common.ktextends.dp2px

class SearchPrecisionPop(private val mContext: Context) : PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    private var container: View? = null
    private var tvExact: TextView? = null
    private var tvFuzzy: TextView? = null
     var feedPopClickListener: (( type:Int)->Unit)?= null


    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.search_pop_pricision, null)
        tvExact = container?.findViewById<View>(R.id.tv_screen_search) as TextView
        tvFuzzy = container?.findViewById<View>(R.id.tv_screen_fuzzy) as TextView
    }

    /**
     *
     */
    private fun initListener() {

        container?.setOnClickListener {
                mPopup?.dismiss()
            feedPopClickListener?.invoke(2)
        }

        tvExact?.setOnClickListener {
            tvFuzzy?.setTextColor(mContext.resources.getColor(R.color.color_2))
            tvExact?.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            feedPopClickListener?.invoke(1)
                mPopup?.dismiss()
        }
        tvFuzzy?.setOnClickListener {
            tvFuzzy?.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            tvExact?.setTextColor(mContext.resources.getColor(R.color.color_2))

            feedPopClickListener?.invoke(0)
                mPopup?.dismiss()

        }
    }

    /**
     * 初始化pop
     */
    private fun initPopup() {
        initView()
        initListener()
        mPopup = PopupWindow(container, 80.dp2px(),85.dp2px(), true)
        mPopup?.contentView = container
        mPopup?.setBackgroundDrawable(ColorDrawable(0))
        mPopup?.isOutsideTouchable = true
        mPopup?.setOnDismissListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun show(parent:View) {
        if (mPopup == null) {
            initPopup()
        }
        mPopup?.showAsDropDown(parent, 0, 0, Gravity.NO_GRAVITY)
    }



    override fun onDismiss() {
            feedPopClickListener?.invoke(2)
    }

    init {
        initPopup()
    }
}