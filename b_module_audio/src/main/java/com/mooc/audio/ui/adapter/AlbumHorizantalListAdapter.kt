package com.mooc.audio.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.module.UpFetchModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.audio.R
import com.mooc.commonbusiness.model.search.TrackBean

/**
 * 音频播放页面横向滚动音频课列表适配器
 */
class AlbumHorizantalListAdapter(list:ArrayList<TrackBean>) : BaseQuickAdapter<TrackBean,BaseViewHolder>
    (R.layout.audio_item_album_horizantal,list), LoadMoreModule,UpFetchModule {

    var selectPosition = 0 //默认选中第一个
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun convert(holder: BaseViewHolder, item: TrackBean) {
        holder.setText(R.id.tvTrackTitle,item.track_title)
        val bgRes = if(holder.layoutPosition == selectPosition) R.drawable.shape_radius2_stroke1_primary
                        else R.drawable.shape_radius2_stroke1_f2f2f2

        holder.setBackgroundResource(R.id.tvTrackTitle,bgRes)
    }


}