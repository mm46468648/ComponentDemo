package com.mooc.discover.adapter

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.discover.R
import com.mooc.discover.model.QuickEntry
import com.mooc.resource.ktextention.dp2px
//import kotlinx.android.synthetic.main.item_resource_sort.view.*
import org.jetbrains.annotations.NotNull

/**
 * 快速入口适配器
 * 根据数据，自己判断应该返回的类型
 */
class QuickEntryAdapter(list: ArrayList<QuickEntry>) : BaseDelegateMultiAdapter<QuickEntry, BaseViewHolder>(list) {

    companion object {
        const val TYPE_BIG = 0;     //大ICon类型
        const val TYPE_SMALL = 1; //小ICon类型
    }

    init {
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<QuickEntry>() {
            override fun getItemType(@NotNull data: List<QuickEntry>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                return if (position < 4) TYPE_BIG else TYPE_SMALL
            }
        })
        getMultiTypeDelegate()
                ?.addItemType(TYPE_BIG, R.layout.item_resource_sort)
                ?.addItemType(TYPE_SMALL, R.layout.item_resource_sort)
    }

    override fun convert(holder: BaseViewHolder, item: QuickEntry) {

        val imageView = holder.getView<ImageView>(R.id.img)
        val titleView = holder.getView<TextView>(R.id.name)
        Glide.with(context).load(item.link).override(54.dp2px(), 54.dp2px()).into(imageView)
        titleView.text = item.name

    }
}