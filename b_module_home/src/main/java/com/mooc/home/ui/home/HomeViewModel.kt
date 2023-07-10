package com.mooc.home.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.common.utils.DateUtil
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.discover.model.ActivityJoinBean
import com.mooc.home.model.AlertMsgBean
import com.mooc.home.model.DailyReadBean
import com.mooc.home.model.MedalListBean
import com.mooc.home.repository.HomeRepository
import okhttp3.RequestBody


class HomeViewModel() : BaseViewModel() {

    private val mRepository = HomeRepository()

    /**
     * 获取弹窗消息
     */
    fun getAlertMsg(): LiveData<AlertMsgBean> {
        val liveData = MutableLiveData<AlertMsgBean>()
        launchUI {
            val alertMsg = mRepository.getAlertMsg()
            if (alertMsg.alert_img.isNotEmpty() || alertMsg.alert_title.isNotEmpty()) {
                liveData.postValue(alertMsg)
                //请求成功，上报给后端，已展示
                mRepository.postAlertMsgRead(alertMsg.id)
            }
        }
        //测试代码
//        val alertMsgBean = AlertMsgBean()
//        alertMsgBean.alert_title = "志愿者招募"
//        alertMsgBean.alert_desc = ""
//        alertMsgBean.system_message_id = "12"
//        alertMsgBean.alert_img = "https://static.learning.mil.cn/moocnd/img/9c39668b39cf43a62e293eebbacab99b.png"
//        alertMsgBean.link = "https://www.learning.mil.cn/publicishment/artical/13827/"
//        alertMsgBean.resource_id = 13827
//        alertMsgBean.resource_type = 14
//        liveData.postValue(alertMsgBean)
        return liveData
    }

//    /**
//     * 获取活动
//     */
//    fun getActivityUserJoin(): LiveData<ActivityJoinBean> {
//        val liveData = MutableLiveData<ActivityJoinBean>()
//        launchUI {
//            val joinBean = mRepository.getActivityUserJoin()
//            liveData.postValue(joinBean)
//
//        }
//        return liveData
//    }


    /**
     * 获取每日一读
     */
    fun getDailyReading(): LiveData<DailyReadBean>? {
        //是否登录
        if (!GlobalsUserManager.isLogin()) return null
        //判断上次请求时间是否
        val lastTimeData = SpDefaultUtils.getInstance().getString(SpConstants.TODAY_READPOP_LASTTIME, "")
        if (lastTimeData == DateUtil.getDateStrNow(TimeFormatUtil.yyyyMMdd)) return null

        val liveData = MutableLiveData<DailyReadBean>()
        launchUI {
            var dailyReadBean: DailyReadBean? = null
            val httpResponseBean: HttpResponse<DailyReadBean> = mRepository.getDailyReading()
            if (httpResponseBean.data != null) {
                dailyReadBean = httpResponseBean.data
            }
            liveData.postValue(dailyReadBean)
        }
        return liveData
    }

    /**
     * 检查并上传时间
     */
    fun postCheckTime(body: RequestBody): LiveData<HttpResponse<Any>> {
        val liveData = MutableLiveData<HttpResponse<Any>>()

        launchUI {
            val result = mRepository.postCheckTime(body)

            liveData.postValue(result)
        }
        return liveData
    }

    fun getSpecialMedal(): LiveData<ArrayList<MedalListBean>> {
        val liveData = MutableLiveData<ArrayList<MedalListBean>>()

        launchUI {
            val result = mRepository.getSpecialMedal()
            liveData.postValue(result.list)
        }
        return liveData

    }

    fun getStudyPlanCompletion(): LiveData<ArrayList<StudyPlan>> {
        val liveData = MutableLiveData<ArrayList<StudyPlan>>()

        launchUI {
            var result = mRepository.getStudyPlanCompletion()
            if (result.is_pop == 1) {//是否展示弹框
                liveData.postValue(result.message)
            }
//            liveData.postValue(result.list)
        }
        return liveData

    }

}