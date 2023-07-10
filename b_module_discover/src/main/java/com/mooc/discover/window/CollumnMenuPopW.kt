package com.mooc.discover.window

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mooc.discover.R
import com.mooc.discover.adapter.CollumMenuAdapter
import com.mooc.discover.model.MenuBean


/**
 * * 新鲜上线列表的弹框
 * @author limeng
 * @date 2020/11/23
 */
class CollumnMenuPopW(private var mContext: Context?, private val parent: View,var onDismiss:(()->Unit)? = null) : PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    var container: LinearLayout? = null
    private var rcyMove: RecyclerView? = null
    var menuAdapter: CollumMenuAdapter? = null
    var onClickListener: (( menuBean:MenuBean? ,isTypeMenu:Boolean) -> Unit)? = null
    var isTypeMenu:Boolean=false
    private fun initPopup() {
        mPopup = PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        //	mPopup.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mPopup?.isOutsideTouchable = true
        mPopup?.setOnDismissListener(this)
    }



    fun setData(menuBeans: ArrayList<MenuBean>) {
        if (menuBeans == null) {
            return
        }
        menuAdapter?.setNewInstance(menuBeans)
    }


    val isShowing: Boolean
        get() = if (mPopup != null && mPopup?.isShowing!!) {
            true
        } else false

    fun show() {
        if (mPopup == null) {
            initPopup()
        }
        menuAdapter?.notifyDataSetChanged()
        mPopup?.showAsDropDown(parent, 0, 0)
    }

    private fun initData() {
        menuAdapter = CollumMenuAdapter(null)
        rcyMove?.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rcyMove?.adapter = menuAdapter
        menuAdapter?.setOnItemClickListener { adapter, view, position ->
           var menu= menuAdapter?.data?.get(position)
            onClickListener?.invoke(menu,isTypeMenu)

        }


    }

    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (mContext as Activity?)?.window?.attributes
        lp?.alpha = bgAlpha
        (mContext as Activity?)?.window?.attributes = lp
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.pop_move_resource, null) as LinearLayout?
        rcyMove = container?.findViewById<View>(R.id.rcy_move) as RecyclerView
    }

    override fun onDismiss() {
        onDismiss?.invoke()
    }
    fun Dismiss() {
        if (mPopup != null) {
            mPopup?.dismiss()
        }
    }

    fun desTroy() {
        menuAdapter = null
        mContext = null
    }

    init {
        initView()
        initData()
        initPopup()
    }
}