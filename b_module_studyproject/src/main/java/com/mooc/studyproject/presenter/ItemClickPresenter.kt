package com.mooc.studyproject.presenter

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.DateUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.routeservice.AudioPlayService
import com.mooc.statistics.LogUtil
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.StudySourceAdapter
import com.mooc.studyproject.fragment.IntelligentLearnListFragment
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.studyproject.model.StudyPlanSource
import com.mooc.studyproject.ui.FollowUpNewActivity

/**

 * @Author limeng
 * @Date 3/10/22-2:23 PM
 */
class ItemClickPresenter(var context: Activity? = null, var mStudyPlanDetailBean: StudyPlanDetailBean? = null
) {
    fun ItemClick(adapter: StudySourceAdapter?, position: Int) {
        val dataBean = adapter?.data?.get(position)

        if (dataBean?.is_lock_up != 0) {//锁定资源  是否试听
            if (dataBean?.set_resource_show_time != null && dataBean.set_resource_show_time > 0) {
                return
            }
        }
        if ("-1".equals(dataBean?.show_type)) {
            toast(context?.getString(R.string.res_underline))
            return
        }
        if (dataBean != null) {

            if (TextUtils.isEmpty(dataBean.before_resource_check_status)) {
                dataBean.before_resource_check_status = "0"
            }
            val beforeResource = dataBean.before_resource_check_status
            if (mStudyPlanDetailBean?.study_plan?.let { isUnStartOrStop(DateUtil.getCurrentTime(), it.plan_starttime, it.plan_endtime) } == 2) {
                toResourceItem(dataBean)
            } else {
                if (beforeResource == "0") {
                    toResourceItem(dataBean)
                } else {
                    if (isBeforeSuccess(dataBean)) {
                        toResourceItem(dataBean)
                    }
                }
            }
        }
    }

    private fun toResourceItem(dataBean: StudyPlanSource) {
        //打点
        LogUtil.addClickLogNew(LogEventConstants2.P_STUDYPLAN,
                "${mStudyPlanDetailBean?.study_plan?.id}",
                "${dataBean._resourceType}",
                "${mStudyPlanDetailBean?.study_plan?.plan_name}",
                "${LogEventConstants2.typeLogPointMap[dataBean._resourceType]}#${dataBean._resourceId}")


        if (ResourceTypeConstans.TYPE_FOLLOWUP_RESOURCE == dataBean.source_type) { //如果是跟读类型的资源
            //需先加入学习项目
            if (mStudyPlanDetailBean?.is_join == 0) {
                toast("跟读资源需先加入学习项目")
                return
            }
            //跳转到跟读页面
            enterFollowUpActivity(dataBean)
            return
        }
//        AudioHeat.studyPlanIds.put(dataBean.audio_data?.id, studyPlanSource?.id);
        //如果是自建音频。记录一下学习项目id，音频打点统计进度使用
        if (dataBean._resourceType == ResourceTypeConstans.TYPE_ONESELF_TRACK) {
            ARouter.getInstance().navigation(AudioPlayService::class.java).setStudyPlanId(dataBean._resourceId, mStudyPlanDetailBean?.study_plan?.id
                    ?: dataBean.study_plan.toString())
        }
        ResourceTurnManager.turnToResourcePage(dataBean)
    }

    /**
     * 跳转到跟读资源页面
     */
    private fun enterFollowUpActivity(dataBean: StudyPlanSource?) {

        val intent = Intent(context, FollowUpNewActivity::class.java)
        intent.putExtra(FollowUpNewActivity.PARAMS_RESOURSE_ID, java.lang.String.valueOf(dataBean?.source_other_id))
        intent.putExtra(FollowUpNewActivity.PARAMS_CHECKIN_SOURCE_ID, java.lang.String.valueOf(dataBean?.id))
        intent.putExtra(FollowUpNewActivity.PARAMS_STUDYPLAN_ID, mStudyPlanDetailBean?.study_plan?.id)
        context?.startActivityForResult(intent, IntelligentLearnListFragment.REFRESH_ALL)
    }

    private fun isBeforeSuccess(dataBean: StudyPlanSource): Boolean {
        return if (!TextUtils.isEmpty(dataBean.before_resource_check_status)) {
            val beforeResourceCheck = dataBean.before_resource_check_status
            val beforeResourceInfo = dataBean.before_resource_info
            if (beforeResourceCheck == "0") {
                true
            } else {
                if (beforeResourceInfo != null) {
                    if (!TextUtils.isEmpty(beforeResourceInfo.code)) {
                        val code = beforeResourceInfo.code
                        code == "3"
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
        } else {
            true
        }
    }

    fun isUnStartOrStop(time: Long, startTime: Long?, stopTime: Long?): Int {
        if (startTime != null && stopTime != null) {
            return if (time < startTime * 1000) { //尚未开始
                0
            } else if (time >= startTime * 1000 && time <= stopTime * 1000) { //可以加入计划
                1
            } else { //计划结束
                2
            }
        }
        return 2

    }
}