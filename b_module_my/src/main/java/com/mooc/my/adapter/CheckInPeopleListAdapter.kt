package com.mooc.my.adapter

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.CheckPeopleBean
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.resource.ktextention.dp2px

class CheckInPeopleListAdapter(var list:ArrayList<CheckPeopleBean>) : RecyclerView.Adapter<CheckInPeopleListAdapter.MViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {
        return MViewHolder(createTextView(parent.context))
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        convert(holder,list[position % list.size])
    }

    override fun getItemCount(): Int {
        if(list == null) return 0
        return if (list.size > 2) { Int.MAX_VALUE } else { list.size }
    }

//    : BaseQuickAdapter<CheckPeopleBean,BaseViewHolder>(0,list) {

//    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
//        convert(holder,list[position % list.size])
//    }
//    /**
//     * 重写此方法，自己创建 View 用来构建 ViewHolder
//     */
//    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//
//        val textView = createTextView(parent.context)
//        return createBaseViewHolder(textView);
//    }


    private fun createTextView(context: Context): TextView {
        val textView = TextView(context)
        textView.gravity = Gravity.LEFT
        textView.setSingleLine()
        textView.ellipsize = TextUtils.TruncateAt.END
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 26.dp2px())
        layoutParams.setMargins(0.dp2px(),5.dp2px(),0,0)
        textView.layoutParams = layoutParams
        return textView
    }

    fun convert(holder: MViewHolder, item: CheckPeopleBean) {


        val userName: String = StringFormatUtil.getStrUserName(item.user_name)
        val score: Int = item.random_score + item.extra_score
        val userDesc: String = kotlin.String.format(holder.context.getResources().getString(R.string.sign_today_user), userName, score)
        val spannableString = SpannableString(userDesc)
        spannableString.setSpan(ForegroundColorSpan(holder.context.getResources().getColor(R.color.color_6)), 0, userName.length + 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(holder.context.getResources().getColor(R.color.color_1982FF)), userName.length + 9, userDesc.length - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(holder.context.getResources().getColor(R.color.color_6)), userDesc.length - 3, userDesc.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        (holder.itemView as TextView).text = spannableString
    }

    class MViewHolder(item : TextView) : RecyclerView.ViewHolder(item){
        val context by lazy { item.context }

    }
}