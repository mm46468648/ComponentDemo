package com.mooc.search.adapter

import android.text.Html
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.search.R
import com.mooc.commonbusiness.model.search.TulingBean

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class TulingAdapter(data: MutableList<TulingBean>?, layoutResId: Int = R.layout.search_item_tuling) : BaseQuickAdapter<TulingBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: TulingBean) {

        holder.setText(R.id.tv_title, item.question)
        holder.setText(R.id.tv_content, Html.fromHtml(item.answer))


    }

}