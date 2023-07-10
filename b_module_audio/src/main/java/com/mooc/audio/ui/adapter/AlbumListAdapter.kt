package com.mooc.audio.ui.adapter

import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.audio.R
import com.mooc.commonbusiness.model.search.TrackBean


/**
 * 音频课页面音频列表适配器
 */
class AlbumListAdapter(list: ArrayList<TrackBean>) : BaseQuickAdapter<TrackBean, BaseViewHolder>
(0, list), LoadMoreModule {

    var allDownloadMode = false      //下载模式
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var allSelect = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    var reverseList: MutableList<TrackBean>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var lastListenId = ""
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * 重写此方法，自己创建 View 用来构建 ViewHolder
     */
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layout =  AlbumItemLayout(context)
        return createBaseViewHolder(layout)
    }

    override fun convert(holder: BaseViewHolder, item: TrackBean) {

        val albumItemLayout = holder.itemView as AlbumItemLayout

        val checkDownload = reverseList?.contains(item)?:false
        val reverseSelectDrawable = if(checkDownload) R.mipmap.audio_ic_all_select else R.mipmap.audio_ic_all_unselected
        albumItemLayout.setHahDownLeft(reverseSelectDrawable)

        albumItemLayout.lastListenId = lastListenId
        albumItemLayout.allDownloadMode = allDownloadMode

        albumItemLayout.setTrackData(item)

    }
}