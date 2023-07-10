package com.mooc.studyroom.ui.activity.download

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.common.ktextends.extraDelegate
import com.mooc.commonbusiness.route.Paths
import com.mooc.video.VideoControListener
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.DownloadActivityCourseOfflinePlayBinding

//import kotlinx.android.synthetic.main.download_activity_course_offline_play.*

@Route(path = Paths.PAGE_DOWNLOAD_COURSE_PLAY)
class DownloadCoursePlayActivity : BaseActivity() {

    companion object{
        const val PARAMS_COURSE_TITLE = "params_course_title"
        const val PARAMS_COURSE_PLAY_PATH = "params_course_play_path"

    }

    val playPath by extraDelegate(PARAMS_COURSE_PLAY_PATH,"")
    private lateinit var inflater: DownloadActivityCourseOfflinePlayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = DownloadActivityCourseOfflinePlayBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        inflater.commonTitle.setOnLeftClickListener { onBackPressed() }
//        playerWrapper.bindControllerView(controllerView)
        inflater.playerWrapper.playLocalFile(playPath)
        inflater.playerWrapper.controllerListener = object : VideoControListener {
            override fun onShowController(b: Boolean) {
                val visibal = if(b)View.VISIBLE else View.GONE
                inflater.commonTitle.visibility = visibal
            }

            override fun onClickReply() {

            }
        }

    }

    override fun onPause() {
        super.onPause()

        inflater.playerWrapper.pause()
    }

    override fun onBackPressed() {
        if(inflater.playerWrapper.onBackPressed()){
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        inflater.playerWrapper.release()
    }
}