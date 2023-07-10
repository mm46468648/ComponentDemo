package com.mooc.course.service

import android.app.Activity
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.VideoActionService
import com.mooc.course.manager.VideoActionManager
import java.lang.ref.WeakReference

@Route(path = Paths.SERVICE_COURSE_VIDEO_ACTION)
class VideoPointServiceImp : VideoActionService {
    override fun setPageInfo(activity: Activity, classId: String, classRoomId: String) {
        VideoActionManager.context = WeakReference(activity)
        VideoActionManager.classId = classId
        VideoActionManager.classRoomId = classRoomId
    }

    override fun getTimerStatus() = VideoActionManager.timerStatus


    override fun startTime() {
       VideoActionManager.postAction(VideoActionManager.ACTION_PLAY)
    }

    override fun pauseTime(){
        VideoActionManager.postAction(VideoActionManager.ACTION_END)
    }
    override fun release() {
        VideoActionManager.postAction(VideoActionManager.ACTION_END)
        VideoActionManager.release()
    }
}