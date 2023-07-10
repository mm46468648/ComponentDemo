package com.mooc.course.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.course.R
import com.mooc.course.model.SequentialBean
import com.mooc.course.ui.adapter.CourseExamAdapter
import com.mooc.course.viewmodel.CourseExamViewModel
import com.mooc.course.viewmodel.CoursePlayViewModel
import com.gavin.com.library.StickyDecoration
import com.gavin.com.library.listener.GroupListener
import com.mooc.resource.ktextention.dp2px
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.route.Paths
import com.mooc.course.databinding.CourseItemExamHeadBinding

//import kotlinx.android.synthetic.main.course_item_exam_head.*

/**
 * 课程考核Fragment
 * 不要分页
 */
class CourseCheckFragment : BaseListFragment<SequentialBean,CourseExamViewModel>(){

    val xtCourseId  by extraDelegate(IntentParamsConstants.COURSE_XT_PARAMS_ID,"")

    val parentModel : CoursePlayViewModel by lazy {
        activity?.let {
            ViewModelProviders.of(it, BaseViewModelFactory(xtCourseId))}!![CoursePlayViewModel::class.java]
    }

//    val mHeadView by lazy {
//        View.inflate(requireContext(), R.layout.course_item_exam_head,null)
//    }

    private lateinit var headViewInflater: CourseItemExamHeadBinding
    companion object{
        fun getInstence(courseId : String) : CourseCheckFragment{
            val courseWareFragment = CourseCheckFragment()
            courseWareFragment.arguments = Bundle().put(IntentParamsConstants.COURSE_XT_PARAMS_ID,courseId)
            return courseWareFragment
        }

        const val REQUEST_CODE_APPLY_VERIFY = 0 //申请证书请求码
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        headViewInflater = CourseItemExamHeadBinding.inflate(layoutInflater)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initHeadData()
    }

    /**
     * 初始化头部数据
     */
    private fun initHeadData() {
//        val headTvScore = mHeadView.findViewById<TextView>(R.id.tvScore)
        val headTvScore = headViewInflater.tvScore
        val headtvScoreDetail = headViewInflater.tvScoreDetail
        //头部总得分数据
        mViewModel?.coursePoint?.observe(viewLifecycleOwner, Observer {
            headTvScore.text = it.toString()
        })
        //头部得分规则数据
        parentModel.courseGradePolicy.observe(viewLifecycleOwner, Observer {
            //将数据拼接成字符串并展示
            loge(it)
            var tvScoreDetailStr = "总分100分，${it.GRADE_CUTOFFS?.min_count}过关，其中"
            it.RAW_GRADER?.forEach {
                tvScoreDetailStr += "${it.type}占<font color='#10955B'>${it.weight * 100}%</>"
            }
            tvScoreDetailStr += "。"

            val fromHtml = HtmlUtils.fromHtml(tvScoreDetailStr)
            headtvScoreDetail.text = fromHtml
            //            val tvScore = mHeadView.findViewById<TextView>(R.id.tvScore)
        })
        //头部证书数据
        mViewModel?.verifyStatus?.observe(viewLifecycleOwner, Observer {
            setVerfyStatus(it.cert_status)
        })
    }

    /**
     * 设置申请证书状态
     */
    fun setVerfyStatus(str : String){
        when(str){
            "ready"->{
                headViewInflater.tvApplyVerify.setText("申请证书")
                headViewInflater.tvApplyVerify.setBackgroundResource(R.drawable.shape_radius30_stroke1_blue4a90e2)
                headViewInflater.tvApplyVerify.setTextColor(ContextCompat.getColor(requireContext(),R.color.color_4A90E2))
                headViewInflater.tvApplyVerify.setOnClickListener {
                    // "跳转到课程证书申请的页面"
                    ARouter.getInstance().build(Paths.PAGE_COURSE_APPLY_VERIFY).navigation(requireActivity(),REQUEST_CODE_APPLY_VERIFY)
                }
            }
            "finish","paper","verified"->{
                headViewInflater.tvApplyVerify.setText("已申请")
                headViewInflater.tvApplyVerify.setOnClickListener { }
            }
        }

        headViewInflater.tvApplyVerify.setOnClickListener {
            // "跳转到课程证书申请的页面"
            ARouter.getInstance().build(Paths.PAGE_COURSE_APPLY_VERIFY).navigation(requireActivity(),REQUEST_CODE_APPLY_VERIFY)
        }
    }

    override fun initAdapter(): BaseQuickAdapter<SequentialBean, BaseViewHolder>? {
        mViewModel?.xtCourseId =xtCourseId
        return (mViewModel as CourseExamViewModel).getPageData()?.value?.let {
            val courseExamAdapter = CourseExamAdapter(it)
            courseExamAdapter.setHeaderView(headViewInflater.root)
            courseExamAdapter.setOnItemClickListener { adapter, view, position ->
                //点击考试章节，进入考试页面
                toast("跳转到考核页面")
            }
            courseExamAdapter
        }
    }

    override fun neadLoadMore(): Boolean {
        return false
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return mViewModel?.getPageData()?.value?.let {
            val groupListener = GroupListener { position -> //获取分组名
                if (position in it.indices) {
                    val get = it.get(position)
                    get.parentDisplayName
                } else ""
            }
            val decoration = StickyDecoration.Builder
                    .init(groupListener)
                    .setHeaderCount(1)
                    .setGroupBackground(ContextCompat.getColor(requireContext(), R.color.white))
                    .setTextSideMargin(14.dp2px())
                    .setGroupTextColor(ContextCompat.getColor(requireContext(), R.color.color_4A4A4A))
                    .setOnClickListener { position, id ->

                    }//重置span（使用GridLayoutManager时必须调用）
                    //.resetSpan(mRecyclerView, (GridLayoutManager) manager)
                    .build()
            decoration
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_APPLY_VERIFY && resultCode == Activity.RESULT_OK){
            //
            setVerfyStatus("verified")
        }

    }
}