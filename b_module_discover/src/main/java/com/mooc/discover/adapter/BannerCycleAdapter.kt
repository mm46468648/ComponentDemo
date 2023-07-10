package com.mooc.discover.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.mooc.common.ktextends.dp2px
import com.mooc.discover.R
import com.mooc.discover.model.RecommendBannerBean
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.widget.MoocImageView
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class BannerCycleAdapter : BaseBannerAdapter<RecommendBannerBean>() {

    var onItemClick : ((position : Int,recommendBean : RecommendBannerBean)->Unit)? = null

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_banner_image
    }

    override fun bindData(
        holder: BaseViewHolder<RecommendBannerBean>,
        data: RecommendBannerBean,
        position: Int,
        pageSize: Int
    ) {
        Glide.with(holder.itemView.context)
            .load(data.picture)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(2f.dp2px())))
            .into((holder.itemView as ImageView))

//        holder.itemView.setOnClickListener { onItemClick?.invoke(position,data) }
    }
}

