package com.mooc.studyroom.ui.adapter.mydownload

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.studyroom.R

class DownloadAudioListAdapter(list : ArrayList<TrackBean>) : BaseQuickAdapter<TrackBean,BaseViewHolder>
        (R.layout.download_item_audio_detail,list) {

    var editMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectTracks = arrayListOf<TrackBean>()

    override fun convert(holder: BaseViewHolder, item: TrackBean) {
        holder.setText(R.id.tvAudioTitle,item.track_title)
        holder.setText(R.id.tvAudioTime,TimeFormatUtil.formatAudioPlayTime(item.duration * 1000))
        holder.setGone(R.id.itemDownloadChxSelect,!editMode)

        val checkBox = holder.getView<CheckBox>(R.id.itemDownloadChxSelect)
        checkBox.isChecked = item in selectTracks

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                if(!selectTracks.contains(item)) {
                    selectTracks.add(item)
                }
            }else{
                if(selectTracks.contains(item)){
                    selectTracks.remove(item)
                }
            }
        }
    }
}