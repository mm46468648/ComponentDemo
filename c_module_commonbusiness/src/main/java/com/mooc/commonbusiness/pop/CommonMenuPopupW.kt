package com.mooc.commonbusiness.pop

import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import com.mooc.commonbusiness.R
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.ktextention.setDrawLeft
import java.util.ArrayList

/**
 * Created by peace on 2017/6/28.
 */
open class CommonMenuPopupW(private val mContext: Context, var setItems: ArrayList<String>, var parent: View) :
    PopupWindow.OnDismissListener {


    companion object {
        const val TYPE_SHARE_STR = "分享"
        const val TYPE_SHIELD_STR = "屏蔽"
        const val TYPE_CUTDOWN_TIME_STR = "定时"
        const val TYPE_DOWNLOAD_STR = "下载"
        const val TYPE_SPEED_STR = "倍速"
        const val TYPE_REPORT_STR = "举报"
    }

    private var mPopup: PopupWindow? = null
    private var container: View? = null
    private var recyclerView: RecyclerView? = null
    var onTypeSelect: ((type: String) -> Unit)? = null
    open val imgResMap = hashMapOf<String, Int>(
        TYPE_SHARE_STR to R.mipmap.common_ic_menu_share,
        TYPE_SHIELD_STR to R.mipmap.common_ic_menu_shield)

    init {
        initView()
        initData()
        initListener()
        initPopup()
    }
    
    private fun initListener() {
        container?.setOnClickListener { onDismiss() }
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext)
            .inflate(R.layout.common_pop_title_menu, null)
        recyclerView = container?.findViewById(R.id.recycler_view)
        recyclerView?.layoutManager = LinearLayoutManager(mContext)

    }


    private fun initPopup() {
        mPopup = PopupWindow(container, -1, -1, true)
        mPopup?.contentView = container
        //	mPopup.setBackgroundDrawable(new ColorDrawable(0x66000000));
        mPopup?.isOutsideTouchable = true
        mPopup?.setOnDismissListener(this)
    }

    private fun initData() {

        val mAdapter = MAdapter(setItems)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            onTypeSelect?.invoke(setItems[position])
            onDismiss()
        }
        recyclerView?.adapter = mAdapter
    }
    override fun onDismiss() {
        if (mPopup != null) {
            mPopup?.dismiss()
        }
    }

    fun show() {
        if (mPopup == null) {
            initPopup()
        }
        mPopup?.showAtLocation(parent, Gravity.TOP, 0, 0)
    }



    inner class MAdapter(list: ArrayList<String>) : BaseQuickAdapter<String, BaseViewHolder>(0, list) {

        override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

            val createTextView = createTextView(parent.context)
            return createBaseViewHolder(createTextView)
        }


        private fun createTextView(context: Context): TextView {
            val textView = TextView(context)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setSingleLine()
            textView.ellipsize = TextUtils.TruncateAt.END
            textView.setTextColor(ContextCompat.getColor(context, R.color.color_3))
            val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 40.dp2px())
            textView.compoundDrawablePadding = 15.dp2px()
//            layoutParams.leftMargin = 32.dp2px()
            textView.layoutParams = layoutParams
            return textView
        }

        override fun convert(holder: BaseViewHolder, item: String) {
            val textView = holder.itemView as TextView
            val res = imgResMap.get(item)
            if(res == null){
                textView.setDrawLeft(res)
            }else{
                textView.setDrawLeft(res)
            }
            textView.setText(item)
        }

    }
}