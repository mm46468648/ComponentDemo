package com.mooc.commonbusiness.module.studyroom.ebook

import android.graphics.Color
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.interfaces.BaseEditableAdapter
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.resource.widget.MoocImageView
import com.mooc.resource.widget.tagtext.TagTextView

/**
 * 学习室电子书列表适配器
 *
 * @param needEdit isPublicPage是否是在公开的学习清单
 */
class EbookAdapter(list: ArrayList<EBookBean>, val isPublicPage: Boolean = false) :
    BaseEditableAdapter<EBookBean>(R.layout.home_item_studyroom_ebook, list,!isPublicPage), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: EBookBean) {
        holder.setText(R.id.tvBookName, item.title)
        holder.setText(R.id.tvBookAuthor, item.writer)

        //设置进度
        val readProcess = "${item.read_process}%"
        //换肤
        val skinColor = SkinManager.getInstance().resourceManager.getColor("colorPrimary")
        val spannableString = spannableString {
            colorSpan {
                str = "已读: $readProcess"
                color = skinColor
                start = str.indexOf(readProcess)
                end = str.length
            }
        }
        if (!isPublicPage || item.is_task) {  //任务或者自己的清单显示读书进度
            holder.setText(R.id.tvReadProgress, spannableString)
        } else {      //公开的学习清单中不显示已读
            holder.setText(R.id.tvReadProgress, "")
        }

        val tvBookNameDone = holder.getView<com.mooc.resource.widget.tagtext.TagTextView>(R.id.tvBookNameDone)
        //换肤
        val tagbg = SkinManager.getInstance().resourceManager.getDrawableByName("shape_radius2_solid_primary")
        tvBookNameDone.setTagsBgDrawable(tagbg)
        tvBookNameDone.setTagTextColor(Color.WHITE)
        tvBookNameDone.setTagTextSize(9)
        //设置阅读任务已完成
        if(item.task_finished){
            tvBookNameDone.visibility = View.VISIBLE
            holder.setVisible(R.id.tvBookName,false)
            tvBookNameDone.setTagStart(arrayListOf("已完成"),item.title)
        }else{
            tvBookNameDone.visibility = View.GONE
            holder.setVisible(R.id.tvBookName,true)
            tvBookNameDone.setTagStart(arrayListOf(""),"")
        }
        //设置来源
        var orgStr = "${item.platform_zh} | ${item.press}"
        if (item.platform_zh.isEmpty() || item.press.isEmpty()) {    //如果有一个为空则不拼接中间 ｜
            orgStr = orgStr.replace("|", "")
        }
        holder.setText(R.id.tvBookOrg, orgStr)

        //设置封面
        val ivBookCover = holder.getView<MoocImageView>(R.id.ivBookCover)

        Glide.with(context).load(item.picture)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(ivBookCover)
    }
}