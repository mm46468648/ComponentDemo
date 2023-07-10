package com.mooc.studyroom.window

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mooc.common.ktextends.dp2px
import com.mooc.studyroom.R
import com.mooc.studyroom.model.MoveArticleBean
import com.mooc.studyroom.ui.adapter.MoveResourceAdapter

class MoveResourcePopW(private val mContext: Context, private val parent: View) : PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    var container: LinearLayout? = null
    private var rcyMove: RecyclerView? = null
    private var moveResourceAdapter: MoveResourceAdapter? = null
    private fun initPopup() {
        mPopup = PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT, 450.dp2px(), false)
        //	mPopup.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mPopup?.isOutsideTouchable = true
        mPopup?.isFocusable = true
        mPopup?.setOnDismissListener(this)
    }

    fun setData(list: ArrayList<MoveArticleBean>?) {
        moveResourceAdapter = MoveResourceAdapter(list)
//        if (listener != null) {
//            moveResourceAdapter.setListener(listener)
//        }
        rcyMove?.layoutManager=LinearLayoutManager(mContext)
        rcyMove?.adapter = moveResourceAdapter
    }

//    fun setListener(listener: MoveResourceAdapter.MoveArticleClickListener) {
//        listener = listener
//    }

    private fun initListener() {
        container?.setOnClickListener { Dismiss() }
    }

    val isShowing: Boolean
        get() = mPopup?.isShowing == true

    fun show() {
        if (mPopup == null) {
            initPopup()
        }
        setBackgroundAlpha(0.5f)
        //在控件上方显示
//        mPopup.showAsDropDown(parent, -50, 0);
        if (!mPopup?.isShowing!!) {
            mPopup?.showAtLocation(parent, Gravity.BOTTOM, 0, 0)
        }
    }

    private fun initData() {}
    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (mContext as Activity).window
                .attributes
        lp.alpha = bgAlpha
        mContext.window.attributes = lp
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.studyroom_pop_move_resource, null) as LinearLayout?
        rcyMove = container?.findViewById<View>(R.id.rcy_move) as RecyclerView
    }

    override fun onDismiss() {
        setBackgroundAlpha(1.0f)
    }

    fun Dismiss() {
            mPopup?.dismiss()
    }

    init {
        initView()
        initData()
        initListener()
        initPopup()
    }
}