package com.mooc.home.ui.discover.micrlprofession

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.model.MicroProfession
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.resource.widget.MoocImageView
import com.mooc.home.R



class MicroProfessionAdapter(list:ArrayList<MicroProfession>)
    : BaseQuickAdapter<MicroProfession,BaseViewHolder>(R.layout.home_item_micro_profession,list),LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: MicroProfession) {
        holder.setText(R.id.tvTitle,item.title)

        Glide.with(context).load(item.img)
            .placeholder(R.mipmap.common_bg_cover_default)
            .error(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2).into(holder.getView<ImageView>(R.id.ivCover))

        holder.setText(R.id.tvOrg,item.org)
        holder.setText(R.id.tvNum,item.learn_num + "人学习")
        holder.setText(R.id.tvCert,item.user_cert_num + "人获得证书")

    }
}