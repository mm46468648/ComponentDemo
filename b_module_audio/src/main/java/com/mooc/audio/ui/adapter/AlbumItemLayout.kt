package com.mooc.audio.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.mooc.audio.R
import com.mooc.audio.manager.AudioDbManger
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.download.DownloadModel
import com.mooc.newdowload.DownloadInfo
import com.mooc.newdowload.DownloadManager
import com.mooc.newdowload.DownloadTaskObserve
import com.mooc.resource.widget.tagtext.TagTextView

class AlbumItemLayout @JvmOverloads constructor(
    var mContext: Context,
    var attributeSet: AttributeSet? = null,
    var defaultInt: Int = 0
) : FrameLayout(mContext, attributeSet, defaultInt), DownloadTaskObserve {

    init {
        LayoutInflater.from(mContext).inflate(R.layout.audio_item_album_show, this)
        _init()
    }

    var tvTrackTitle : com.mooc.resource.widget.tagtext.TagTextView? = null
    var tvTrackPlayCount : TextView? = null
    var tvTrackPlayTime : TextView? = null
    var tvHasDown : TextView? = null
    var ivDownloadState : ImageView? = null
    var ivAdd : ImageView? = null
    var pbDownloadState : ProgressBar? = null

    private fun _init() {
         tvTrackTitle = findViewById<com.mooc.resource.widget.tagtext.TagTextView>(R.id.tvTrackTitle)
         tvTrackPlayCount = findViewById<TextView>(R.id.tvTrackPlayCount)
         tvTrackPlayTime = findViewById<TextView>(R.id.tvTrackPlayTime)
         tvHasDown = findViewById<TextView>(R.id.tvHasDown)
         ivAdd = findViewById<ImageView>(R.id.ivAdd)

        ivDownloadState = findViewById<ImageView>(R.id.ivDownloadState)
        pbDownloadState = findViewById<ProgressBar>(R.id.pbDownloadState)
    }


    var lastListenId = ""
    var allDownloadMode = false //下载模式


    @SuppressLint("SetTextI18n")
    fun setTrackData(item: TrackBean) {

        //音频标题
        if(item.id == lastListenId){
            tvTrackTitle?.setTagStart(arrayListOf("最近在听"),item.track_title)
            tvTrackTitle?.setTextColor(mContext.getColorRes(R.color.colorPrimary))
        }else{
            tvTrackTitle?.text = item.track_title
            tvTrackTitle?.setTextColor(mContext.getColorRes(R.color.color_3))
        }

        //播放次数
        tvTrackPlayCount?.setText(StringFormatUtil.formatPlayCount(item.play_count) + "次")
        tvTrackPlayTime?.setText(TimeUtils.timeParse(item.liveRoomId).toString() + "/" + TimeUtils.timeParse(item.duration))

        //加入学习室状态
        val addStudyRoomStateRes = if(item.enrolled) R.mipmap.audio_ic_right_added else R.mipmap.audio_ic_right_add
        ivAdd?.setImageResource(addStudyRoomStateRes)
        //显示下载状态
        val hastDownVisibal = if(allDownloadMode) View.VISIBLE else View.GONE
        tvHasDown?.visibility = hastDownVisibal

        ivDownloadState?.visibility = View.INVISIBLE
        pbDownloadState?.visibility = View.INVISIBLE
        ivDownloadState?.setImageResource(R.mipmap.audio_ic_download_start)

//        this.setTag(item.generateDownloadDBId())
        this.setTag(item)
        val downLoadInfo: DownloadInfo? = DownloadManager.DOWNLOAD_INFO_HASHMAP.get(getDownloadId())
        changeUi(downLoadInfo)

//        loge("title: ${item.track_title} downloadStae: ${if(downLoadInfo == null) "null" else downLoadInfo.state}")

    }



    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        DownloadManager.getInstance().addObserve(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        DownloadManager.getInstance().removeObserve(this)
    }

    override fun update(id: Long) {
        if(id == downloadId){
            val downLoadInfo: DownloadInfo? = DownloadManager.DOWNLOAD_INFO_HASHMAP[id]
            changeUi(downLoadInfo)
        }
    }

    private fun changeUi(downloadInfo: DownloadInfo?) {
        when(downloadInfo?.state){
            DownloadModel.STATUS_DOWNLOADING,DownloadModel.STATUS_WAIT->{
                pbDownloadState?.visibility = View.VISIBLE
                ivDownloadState?.visibility = View.INVISIBLE
                tvHasDown?.setDrawLeft(null)
                tvHasDown?.setText("下载中")
            }
            DownloadModel.STATUS_COMPLETED->{
                pbDownloadState?.visibility = View.INVISIBLE
                ivDownloadState?.visibility = View.VISIBLE
                ivDownloadState?.setImageResource(R.mipmap.audio_ic_download_complete)

                tvHasDown?.setDrawLeft(null)
                tvHasDown?.setText("已下载")

                val trackBean = this.getTag() as TrackBean
                //缓存音频表中插入一条记录
                AudioDbManger.insertDownloadAudio(trackBean.generateTrackDB())
                //同时向音频课表中更新此音频课有缓存内容
                AudioDbManger.updateHaveDownload(trackBean.albumId,true)
            }
            else->{
                ivDownloadState?.visibility = View.VISIBLE
                pbDownloadState?.visibility = View.INVISIBLE
                ivDownloadState?.setImageResource(R.mipmap.audio_ic_download_start)

            }
        }
    }


    /**
     * 由外部通知显示是否勾选
     */
    fun setHahDownLeft(drawableres:Int){
        //如果
        tvHasDown?.setText("")
        tvHasDown?.setDrawLeft(drawableres)
    }

    override fun getDownloadId(): Long {
        return (this.getTag() as TrackBean).generateDownloadDBId()
    }
}

