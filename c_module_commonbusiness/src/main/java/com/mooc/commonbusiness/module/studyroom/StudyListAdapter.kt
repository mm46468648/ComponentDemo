package com.mooc.commonbusiness.module.studyroom

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.resource.widget.MoocImageView

class StudyListAdapter(list:ArrayList<FolderItem>?)
    : BaseQuickAdapter<FolderItem,BaseViewHolder>(R.layout.studyroom_item_horizantal_studylist,list) {


    companion object{
        /**
         * 创建创建清单头部View
         */
        fun createAddHeadView(context: Context, callBack : (()->Unit)? = null) : View {
//            val headView = View.inflate(context,R.layout.home_item_studyroom_studylist,null)
//            val findViewById = headView.findViewById<ImageView>(R.id.ivCover)
            val imageView = ImageView(context)
            imageView.id = R.id.rv_studyfolder_head_add
            imageView.layoutParams = FrameLayout.LayoutParams(68.dp2px(),68.dp2px())
            imageView.setImageResource(R.mipmap.home_ic_folder_cover_add)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setOnClickListener { callBack?.invoke() }
            return imageView
        }

        fun createCollectHeadView(context: Context,callBack : (()->Unit)? = null) : View {
            val headView = View.inflate(context,R.layout.studyroom_item_horizantal_studylist,null)
            val ivCover = headView.findViewById<ImageView>(R.id.ivCover)
            val tvName = headView.findViewById<TextView>(R.id.tvName)
            ivCover.setImageResource(R.mipmap.home_ic_collect_studylist)
            tvName.setText("收藏的清单")
            headView.setOnClickListener { callBack?.invoke() }
            return headView
        }
    }
    private val drawables = intArrayOf(R.mipmap.home_ic_folder_cover_1, R.mipmap.home_ic_folder_cover_2, R.mipmap.home_ic_folder_cover_3, R.mipmap.home_ic_folder_cover_4, R.mipmap.home_ic_folder_cover_5
            , R.mipmap.home_ic_folder_cover_6, R.mipmap.home_ic_folder_cover_7, R.mipmap.home_ic_folder_cover_8, R.mipmap.home_ic_folder_cover_9, R.mipmap.home_ic_folder_cover_10, R.mipmap.home_ic_folder_cover_11,
            R.mipmap.home_ic_folder_cover_12)

    override fun convert(holder: BaseViewHolder, item: FolderItem) {
        holder.setText(R.id.tvName,item.name)
        val drawablesIndex = holder.adapterPosition % drawables.size
//        holder.setImageResource(R.id.ivCover,drawables[drawablesIndex])
        val ivCover = holder.getView<MoocImageView>(R.id.ivCover)
        ivCover.setImageUrl(drawables[drawablesIndex],3)

        holder.setGone(R.id.tvPublic,!item.is_show)
    }
}