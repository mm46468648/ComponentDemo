package com.mooc.commonbusiness.pop.studyproject

import android.content.Context
import android.text.TextUtils
import android.view.View.OnClickListener
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.databinding.HomePopShowStudyPlanSuccessBinding
import com.mooc.commonbusiness.model.studyproject.StudyPlan
import com.mooc.commonbusiness.route.Paths

//import kotlinx.android.synthetic.main.home_pop_show_study_plan_success.view.*

/**
展示完成学习项目的弹框
 * @Author limeng
 * @Date 2021/4/19-3:10 PM
 */
class ShowStudyProjectScorePop(context: Context, var studyPlan: StudyPlan?) :
    CenterPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_show_study_plan_success
    }

    lateinit var inflater: HomePopShowStudyPlanSuccessBinding
    override fun onCreate() {
        super.onCreate()
        inflater = HomePopShowStudyPlanSuccessBinding.bind(popupImplView)
        setData()
        initListener()

    }

    private fun initListener() {
        inflater.ivShareScoreClose.setOnClickListener(OnClickListener {
            dismiss()
        })

        inflater.tvPlanPopLookPoint.setOnClickListener(OnClickListener {
            ARouter.getInstance().build(Paths.PAGE_SCORE_DETAIL).navigation()
        })
    }

    fun setData() {
        if (studyPlan != null) {
            val name = studyPlan!!.plan_name
            val planName: String?
            if (!TextUtils.isEmpty(name)) {
//                if (name?.length!! > 3) {
//                    planName = """
//                        ${name.substring(0, 3)}
//                        ${name.substring(3, name.length)}
//                        """.trimIndent()
//                    if (planName.length > 9) {
//                        planName = name.substring(0, 9) + "..."
//                    }
//                } else {
                planName = name
//                }
            } else {
                planName = context.resources.getString(R.string.pop_plan_title)
            }
            val title = String.format(
                context.resources.getString(R.string.text_plan_success_title),
                planName
            )
            inflater.tvPlanPopTitle.text = title
            if (studyPlan!!.extra_studyplan_score > 0) {
                inflater.tvPlanExtraScore.text = String.format(
                    context.resources.getString(R.string.pop_plan_extra_score),
                    studyPlan!!.extra_studyplan_score
                )
            } else {
                inflater.tvPlanExtraScore.text = ""
            }
            inflater.tvPlanBetScore.text = java.lang.String.valueOf(studyPlan!!.end_back_score)
        }
    }

    override fun getPopupWidth(): Int {
        return 350.dp2px()
    }

    override fun getMaxWidth(): Int {
        return 350.dp2px()
    }
}