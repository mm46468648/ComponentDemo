package com.mooc.my.adapter

import androidx.annotation.Nullable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.widget.VoicePlayerController
import com.mooc.my.R


/**
 * 跟读语音动态适配器
 */
class FollowUpVoiceDynamicAdapter(@Nullable list:ArrayList<StudyDynamic.RepeatRecordBean>?)
    : BaseQuickAdapter<StudyDynamic.RepeatRecordBean, BaseViewHolder>(R.layout.my_item_followup_voice_dynamic,list) {

    override fun convert(holder: BaseViewHolder, item: StudyDynamic.RepeatRecordBean) {
        holder.setText(R.id.tvNum,(holder.layoutPosition + 1).toString())
        val voicePlayerController = holder.getView<VoicePlayerController>(R.id.voicePlayerController)
        voicePlayerController.setTotleTimeLength(item?.audio_time)
        voicePlayerController.playPath = item?.audio_url
    }

}