package com.mooc.studyroom.ui.adapter

import android.graphics.Bitmap
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.studyroom.R

/**
 * 邀请朋友读书分享
 */
class ShareBookInviteAdapter(list: ArrayList<EBookBean>, var coverMap: HashMap<String, Bitmap>)
    : BaseQuickAdapter<EBookBean, BaseViewHolder>(R.layout.studyroom_item_share_book_invite,list ) {

    override fun convert(holder: BaseViewHolder, item: EBookBean) {

        holder.setText(R.id.tvBookName,item.title)
        holder.setText(R.id.tvBookAuthor,item.writer)
        //设置封面图
//        helper.getView<MoocImageView>(R.id.mivBookCover).setImageUrl(item.picture,2)
        val get = coverMap.get(item.title)
        val mivBookCover = holder.getView<ImageView>(R.id.mivBookCover)
        mivBookCover.setImageBitmap(get)
    }
}