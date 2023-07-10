package com.mooc.course.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.course.R
import com.mooc.course.model.SequentialBean
import com.mooc.download.DownloadModel

/**
 * 课程章节列表适配器
 */
class CourseChapterListAdapter(list:ArrayList<SequentialBean>)
    : BaseQuickAdapter<SequentialBean,BaseViewHolder>(R.layout.course_item_play_chapters,list),LoadMoreModule {

    //当前选中位置
    var currentSelectPosition = -1
        set(value) {
            //避免索引越界
            if(value in data.indices){
                field = value
                notifyItemChanged(value)
            }
        }

    override fun convert(holder: BaseViewHolder, item: SequentialBean) {
        holder.setText(R.id.tvChapterTitle,item.display_name)
        holder.setText(R.id.tvTime,TimeFormatUtil.formatAudioPlayTime(item.video_length.toLong()))

        val layoutPosition = holder.layoutPosition
        //如果是当前选中播放的条目
        if (layoutPosition == currentSelectPosition){
            //1。标题变成红色
            holder.setTextColor(R.id.tvChapterTitle,ContextCompat.getColor(context,R.color.color_D13B3B))
            //2.左边小icon，变为实心蓝色
            holder.setImageResource(R.id.ivPlay,R.mipmap.course_ic_ware_play_select)
            //3.播放的时间，显示视频的总时长
            holder.setText(R.id.tvTime,TimeFormatUtil.formatAudioPlayTime(item.video_length.toLong()))
        }else{
            holder.setTextColor(R.id.tvChapterTitle,ContextCompat.getColor(context,R.color.color_9B9B9B))
            holder.setImageResource(R.id.ivPlay,R.mipmap.course_ic_ware_play_unselect)

            val watchedPercentStr = (item.video_length * item.watched_percent).toLong()
            //显示上次播放进度。（默认00:00）/总时常
            holder.setText(R.id.tvTime,"${TimeFormatUtil.formatAudioPlayTime(watchedPercentStr)} / ${TimeFormatUtil.formatAudioPlayTime(item.video_length.toLong())}")
        }
        // todo 根据播放记录,展示是否观看过提示
        holder.setGone(R.id.ivPlayTip,item.watched)


        //下载信息模型
        val findDownloadMode = item.downloadModel
        //下载状态相关显示
        val ivDownloadState = holder.getView<ImageView>(R.id.ivDownloadState)
        val pbDownloadState = holder.getView<ProgressBar>(R.id.pbDownloadState)

        ivDownloadState.visibility = View.INVISIBLE
        pbDownloadState.visibility = View.INVISIBLE
        ivDownloadState.setImageResource(R.mipmap.course_ic_down_unstart)
        //下载状态相关
//        val findDownloadMode = DownloadManager.findDownloadMode(DownloadConfig.audioLocation, item.track_title)
        when(findDownloadMode?.status){
            DownloadModel.STATUS_DOWNLOADING->{
                pbDownloadState.visibility = View.VISIBLE
            }
            DownloadModel.STATUS_COMPLETED->{
                ivDownloadState.visibility = View.VISIBLE
                ivDownloadState.setImageResource(R.mipmap.course_ic_download_complete)
            }
            else->{
                ivDownloadState.visibility = View.VISIBLE
            }
        }

    }


}