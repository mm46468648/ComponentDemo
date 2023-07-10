package com.mooc.studyproject.adapter

import androidx.annotation.Nullable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import com.mooc.commonbusiness.widget.VoicePlayerController
import com.mooc.studyproject.R


/**
 * 跟读语音动态适配器
 */
class FollowUpVoiceDynamicAdapter(@Nullable list:ArrayList<StudyDynamic.RepeatRecordBean>?)
    : BaseQuickAdapter<StudyDynamic.RepeatRecordBean, BaseViewHolder>(R.layout.studyproject_item_followup_voice_dynamic,list) {


    override fun convert(helper: BaseViewHolder, item: StudyDynamic.RepeatRecordBean) {
        helper.setText(R.id.tvNum,(helper.layoutPosition + 1).toString())
        val voicePlayerController = helper.getView<VoicePlayerController>(R.id.voicePlayerController)
        voicePlayerController.setTotleTimeLength(item?.audio_time)
        voicePlayerController.playPath = item?.audio_url
    }


}