package com.mooc.commonbusiness.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.resource.utils.PixUtil

/**
 * 搜索分享电子书适配器
 * 与邀请朋友读通用
 * 区别，add还是delete
 * @param type 0,delete,1,add
 * @param allreadyList 已经选中的书籍
 */
class SearchShareBookAdapter(
    list: ArrayList<EBookBean>,
    var type: Int = 0,
    var allreadyList: ArrayList<EBookBean>? = null
) : BaseQuickAdapter<EBookBean, BaseViewHolder>(R.layout.home_item_search_share_book, list) {


    var coverMap = hashMapOf<String, Bitmap>()
    override fun convert(helper: BaseViewHolder, item: EBookBean) {

        helper.setText(R.id.tvBookName, item.title)
        helper.setText(R.id.tvBookAuthor, item.writer)
        helper.setText(R.id.tvBookCount, StringFormatUtil.formatPlayCount(item.word_count) + "字")

        //拼接来源字符串
        val sourseStr = item.platform_zh + " | " + item.press;
        //如果有一个为空，则不拼接 "｜"
        if (TextUtils.isEmpty(item.platform_zh) || TextUtils.isEmpty(item.press)) {
            sourseStr.replace(" | ", "")
        }
        helper.setText(R.id.tvBookSource, sourseStr)
        //设置封面图
//        helper.getView<MoocImageView>(R.id.mivBookCover).setImageUrl(item.picture,2)

        val mivBookCover = helper.getView<ImageView>(R.id.mivBookCover)
        //获取加载图片的bitmap，后续分享会用到
        Glide.with(context)
            .asBitmap()
            .transform(RoundedCorners(PixUtil.dp2px(2.toFloat()).toInt()))
            .load(item.picture)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if (!coverMap.containsKey(item.title)) {
                        coverMap.put(item.title, resource)
                    }
                    mivBookCover.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })


        //如果已经选中了书籍也显示减号
        val contains = allreadyList?.map { it.id }?.contains(item.id) ?: false
        val ivAddRes =
            if (type == 0 || contains) R.mipmap.home_ic_search_share_book_delete else R.mipmap.home_ic_search_share_book_add

        helper.setImageResource(R.id.ivAdd, ivAddRes)
    }
}