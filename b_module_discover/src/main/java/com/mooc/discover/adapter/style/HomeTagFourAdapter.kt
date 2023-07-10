package com.mooc.discover.adapter.style

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.column.ui.columnall.delegate.ColumnTagFourDelegate
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.ResourceTypeConstans.Companion.typeStringMap
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.discover.R
import com.mooc.discover.model.RecommendColumn

class HomeTagFourAdapter(data: ArrayList<RecommendColumn>?) :
    BaseDelegateMultiAdapter<RecommendColumn, BaseViewHolder>(data) {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    init {
        setMultiTypeDelegate(ColumnTagFourDelegate())
    }

    override fun convert(holder: BaseViewHolder, item: RecommendColumn) {
        val itemViewType: Int = holder.getItemViewType()
        if (itemViewType == ResourceTypeConstans.TYPE_UNDEFINE) return

        holder.setGone(R.id.icon_play, true)
        val value = typeStringMap[item.type]
        holder.setText(R.id.tv_identify, value)
        holder.setGone(R.id.tv_identify, TextUtils.isEmpty(value))
        holder.setText(R.id.tvTypeHomeFour, value)
        holder.setGone(R.id.tvTypeHomeFour, true)

        holder.setText(R.id.title, item.title)
        Glide.with(context)
            .load(item.small_image)
            .placeholder(R.mipmap.common_bg_cover_big_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into((holder.getView<View>(R.id.cover) as ImageView))


        when (item.type) {
            ResourceTypeConstans.TYPE_SPECIAL -> if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                holder.setText(R.id.tv_identify, item.identity_name)
            }
            ResourceTypeConstans.TYPE_COURSE -> if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                if (!TextUtils.isEmpty(item.identity_name)) { //后台返回的右上角角标不为空，显示后台配置
                    holder.setGone(R.id.tvTypeHomeFour, false)
                    holder.setGone(R.id.tv_identify, true)
                    holder.setText(R.id.tvTypeHomeFour, item.identity_name)
                } else {
                    holder.setGone(R.id.tvTypeHomeFour, true)
                    holder.setGone(R.id.tv_identify, false)
                }
            }
            ResourceTypeConstans.TYPE_TRACK, ResourceTypeConstans.TYPE_ALBUM, ResourceTypeConstans.TYPE_ONESELF_TRACK -> holder.setGone(
                R.id.icon_play,
                false
            )
            ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE -> {}
        }
    }
}