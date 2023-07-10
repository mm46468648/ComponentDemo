package com.mooc.course.ui.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.course.R
import com.mooc.course.databinding.CourseItemZhsExamHeadBinding
import com.mooc.course.model.CourseScoreRuleBean
import com.mooc.course.model.UserCourseScoreBean
import com.mooc.course.model.ZHSExam
import com.mooc.course.model.ZHSExamData
import com.mooc.course.ui.adapter.ZHSCourseExamAdapter
import com.mooc.course.ui.pop.ZHSExamTipDialog
import com.mooc.course.viewmodel.ZHSExamViewModel
import org.json.JSONObject

class ZHSCheckFragment : BaseListFragment<ZHSExamData, ZHSExamViewModel>() {

    val headerZhsExamBinding: CourseItemZhsExamHeadBinding by lazy {
        DataBindingUtil.inflate<CourseItemZhsExamHeadBinding>(
                LayoutInflater.from(requireContext()),
                R.layout.course_item_zhs_exam_head,
                null,
                false
        )
    }

    var exam_page_link = ""

    companion object {
        fun getInstance(courseId: String, courseBean: CourseBean): ZHSCheckFragment {
            val courseWareFragment = ZHSCheckFragment()
            courseWareFragment.arguments =
                    Bundle().put(IntentParamsConstants.COURSE_PARAMS_ID, courseId)
                            .put(IntentParamsConstants.COURSE_PARAMS_DATA, courseBean)
            return courseWareFragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel?.headData?.observe(viewLifecycleOwner, Observer {
            exam_page_link = it.exam_page_link?:""
            initHeaderView(it)

            val courseBean = (arguments?.getSerializable(IntentParamsConstants.COURSE_PARAMS_DATA)) as CourseBean
            val requestData = JSONObject()
            requestData.put("course_id", courseBean.id)
            requestData.put("course_point", it.user_course_score?.totalAccessScore?:0)
            mViewModel?.postCourseScore(RequestBodyUtil.fromJson(requestData))
        })
    }

    override fun initAdapter(): BaseQuickAdapter<ZHSExamData, BaseViewHolder>? {
        mViewModel?.courseId =
                (arguments?.get(IntentParamsConstants.COURSE_PARAMS_ID) ?: "") as String

        return mViewModel?.getPageData()?.value?.let {
            val zhsCourseExamAdapter = ZHSCourseExamAdapter(it)
            zhsCourseExamAdapter.addHeaderView(headerZhsExamBinding.root)
            zhsCourseExamAdapter.addChildClickViewIds(R.id.btEnter)
            zhsCourseExamAdapter.setOnItemChildClickListener { adapter, view, position ->
                if (view.id == R.id.btEnter) {
                    //点击进入考试，或者查看
                    clickEnter(it[position])
                }
            }
            zhsCourseExamAdapter
        }
    }


    private fun initHeaderView(data: ZHSExam) {
        val userCourseScoreBean: UserCourseScoreBean? = data.user_course_score
        val courseScoreRuleBean: CourseScoreRuleBean? = data.course_score_rule

        userCourseScoreBean?.apply {
            headerZhsExamBinding.videoScore.setText(java.lang.String.valueOf(userCourseScoreBean.videoAccessScore))
            headerZhsExamBinding.videoScore2.setText(java.lang.String.valueOf(userCourseScoreBean.testAccessScore))
            headerZhsExamBinding.videoScore3.setText(java.lang.String.valueOf(userCourseScoreBean.examAccessScore))
            headerZhsExamBinding.videoScore4.setText(java.lang.String.valueOf(userCourseScoreBean.totalAccessScore))

            val passScore: Int = courseScoreRuleBean?.passScore?:0
            if (userCourseScoreBean.videoAccessScore >= passScore) {
                headerZhsExamBinding.videoScore.setTextColor(resources.getColor(R.color.colorPrimary))
            } else {
                headerZhsExamBinding.videoScore.setTextColor(resources.getColor(R.color.color_D77762))
            }
            if (userCourseScoreBean.testAccessScore >= passScore) {
                headerZhsExamBinding.videoScore2.setTextColor(resources.getColor(R.color.colorPrimary))
            } else {
                headerZhsExamBinding.videoScore2.setTextColor(resources.getColor(R.color.color_D77762))
            }
            if (userCourseScoreBean.examAccessScore >= passScore) {
                headerZhsExamBinding.videoScore3.setTextColor(resources.getColor(R.color.colorPrimary))
            } else {
                headerZhsExamBinding.videoScore3.setTextColor(resources.getColor(R.color.color_D77762))
            }
        }

        val tip1 = "总分"
        val tip2 = "分，"
        val tip3 = "分过关，其中视频观看进度占"
        val tip4 = "(每章节视频需至少持续看完80%才显示进度)、随堂测试占"
        val tip5 = "，期末考试成绩占"
        val tip6 = "。"

        courseScoreRuleBean?.apply {
            val builder = SpannableStringBuilder(tip1)
            builder.append(courseScoreRuleBean.totalScore.toString() + "")
            builder.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                tip1.length,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(tip2)
            val length2 = builder.length
            builder.append(passScore.toString() + "")
            builder.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                length2,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(tip3)
            val length3 = builder.length
            builder.append(courseScoreRuleBean.videoViewProPercent.toString() + "%")
            builder.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                length3,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(tip4)
            val length4 = builder.length
            builder.append(courseScoreRuleBean.classRoomTestPercent.toString() + "%")
            builder.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                length4,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(tip5)
            val length5 = builder.length
            builder.append(courseScoreRuleBean.finalExamPercent.toString() + "%")
            builder.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorPrimary)),
                length5,
                builder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.append(tip6)
            headerZhsExamBinding.desTv.setText(builder)
        }

    }

    /**
     * 点击进入或者查看
     */
    private fun clickEnter(zhsExamData: ZHSExamData) {
        if ("2" != zhsExamData.type) { //如果不是考试类型，是作业类型，点击进入，直接进入web页
            //拼接链接
            val put = Bundle().put(IntentParamsConstants.WEB_PARAMS_TITLE, "军职在线")
                    .put(IntentParamsConstants.WEB_PARAMS_URL, getSbLinkUrl(zhsExamData))
            ARouter.getInstance().build(Paths.PAGE_WEB).with(put).navigation()
            isEnterExam = true
        } else {  //如何是考试类型  type为"2" 的时候弹提示
            showExamTipDialog(zhsExamData, zhsExamData.isSub)
        }
    }

    /**
     * @param commitType 0未提交 1已提交
     * 如果未提交，提示只有一次考试机会
     * 已提交提示已提交
     */
    private fun showExamTipDialog(zhsExamData: ZHSExamData, commitType: String) {
        val courseChoosePop = ZHSExamTipDialog(requireContext())
        courseChoosePop.type = commitType
        courseChoosePop.onConfirm = {
            //点击确认，进入考试
            //拼接链接
            val put = Bundle().put(IntentParamsConstants.WEB_PARAMS_TITLE, "军职在线")
                    .put(IntentParamsConstants.WEB_PARAMS_URL, getSbLinkUrl(zhsExamData))
            ARouter.getInstance().build(Paths.PAGE_WEB).with(put).navigation()
            isEnterExam = true
        }
        XPopup.Builder(requireContext())
                .asCustom(courseChoosePop)
                .show()
    }

    var isEnterExam = false //是否跳转了考试页面
    override fun onResume() {
        super.onResume()

        if (isEnterExam) {
            //进入考试页面后返回刷新下状态
            loadDataWithRrefresh()
            isEnterExam = false
        }
    }


    private fun getSbLinkUrl(bean: ZHSExamData): String {
        val stringBuffer = StringBuffer(exam_page_link)
        stringBuffer.append("&stuExamId=" + bean.studentExamId + "&")
        stringBuffer.append("examId=" + bean.examId + "&")
        stringBuffer.append("isSub=" + bean.isSub + "&")
        stringBuffer.append("name=" + bean.examName)
        return stringBuffer.toString()
    }

    override fun neadLoadMore(): Boolean = false

}