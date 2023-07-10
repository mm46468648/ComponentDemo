package com.mooc.commonbusiness.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.FollowMemberBean
import com.mooc.commonbusiness.widget.HeadView


/**

 * @Author limeng
 * @Date 2021/2/20-6:10 PM
 */
class UserFollowAdapter(data: ArrayList<FollowMemberBean>) : BaseQuickAdapter<FollowMemberBean, BaseViewHolder>(R.layout.common_item_follow_list, data), LoadMoreModule {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun convert(holder: BaseViewHolder, item: FollowMemberBean) {
        val headView = holder.getViewOrNull<HeadView>(R.id.iv_icon_follow)
        headView?.setHeadImage(item.avatar, item.avatar_identity)
        var nameStr = ""
        if (item.name.toString().isNotEmpty()) {
            nameStr = setNameEllipsis(item.name.toString())
        }
        holder.setText(R.id.tv_name_follow, nameStr)


        val userBean = GlobalsUserManager.userInfo
        if (userBean != null && !TextUtils.isEmpty(userBean.id)) {
            if (userBean.id.equals(item.user_id)) {
                holder.setGone(R.id.tv_follow, true)
            } else {
                holder.setGone(R.id.tv_follow, false)

            }
        } else {
            holder.setGone(R.id.tv_follow, false)
        }


        val drawable: Drawable

        if (item.state == 0) { //加关注
            drawable = context.resources.getDrawable(R.mipmap.common_iv_follow)
            holder.setText(R.id.tv_follow, context.getResources().getString(R.string.text_my_add_follow))
            holder.setTextColorRes(R.id.tv_follow, R.color.color_007E47)
            holder.setBackgroundResource(R.id.tv_follow, R.drawable.shape_corner3_width1_color3abe84)
        } else if (item.state == 1) { //互相关注
            drawable = context.resources.getDrawable(R.mipmap.common_iv_follow_each_other)
            holder.setText(R.id.tv_follow, context.getResources().getString(R.string.text_my_each_follow))
            holder.setTextColorRes(R.id.tv_follow, R.color.color_6F6F6F)
            holder.setBackgroundResource(R.id.tv_follow, R.drawable.shape_corners3_colore0_width1)
        } else { //已关注
            drawable = context.resources.getDrawable(R.mipmap.common_iv_followed)
            holder.setText(R.id.tv_follow, context.getResources().getString(R.string.text_my_followed))
            holder.setTextColorRes(R.id.tv_follow, R.color.color_6F6F6F)
            holder.setBackgroundResource(R.id.tv_follow, R.drawable.shape_corners3_colore0_width1)

        }

        //这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        holder.getView<TextView>(R.id.tv_follow).setCompoundDrawables(drawable, null, null, null)

    }

    fun setNameEllipsis(mobiles: String): String {
        return if (mobiles.length > 5) {
            mobiles.replace("(.{3}).*(.{2})".toRegex(), "$1****$2")
        } else mobiles
    }
}