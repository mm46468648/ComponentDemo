package com.mooc.microknowledge.ui

import android.content.Context
import com.bumptech.glide.Glide
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.microknowledge.R
import com.mooc.microknowledge.databinding.KnowledgeActivityMainBinding
import com.mooc.microknowledge.databinding.KnowledgePopShowMedalBinding


class ShowMedalPop(private val mContext: Context, private val imageUrl: String) :
    CenterPopupView(mContext){

    override fun getImplLayoutId(): Int {
        return R.layout.knowledge_pop_show_medal
    }

    lateinit var inflater : KnowledgePopShowMedalBinding
    override fun onCreate() {
        super.onCreate()

        inflater = KnowledgePopShowMedalBinding.bind(popupImplView)
        Glide.with(mContext)
            .load(imageUrl)
            .placeholder(R.mipmap.knowledge_ic_medal_holder)
            .error(R.mipmap.common_bg_cover_default)
            .into(inflater.ivMedalBig)
    }

}