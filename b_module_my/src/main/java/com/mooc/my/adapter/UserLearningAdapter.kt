package com.mooc.my.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.my.R
import com.mooc.my.model.LearingListBean

/**

 * @Author jiboxue
 * @Date 2021/11/05
 */
class UserLearningAdapter(data: ArrayList<LearingListBean>) : BaseQuickAdapter<LearingListBean, BaseViewHolder>(R.layout.my_item_user_learning, data) ,LoadMoreModule{
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun convert(holder: BaseViewHolder, item: LearingListBean) {

        holder.setText(R.id.tv_learning_title, if (item.name.isNullOrEmpty()) "" else item.name)
        holder.setText(R.id.tv_resources, "共" + item.resource_count.toString() + "个学习资源")//共28个学习资源
        holder.setText(R.id.tv_zan, item.like_count.toString()  + "人点赞")//2435人点赞

    }


}