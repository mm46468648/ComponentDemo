package com.mooc.studyproject.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.mooc.common.ktextends.getColorRes
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.model.eventbus.StudyProjectRefresh
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.StudyListVpAdapter
import com.mooc.studyproject.adapter.StudyNotIntelligentVpAdapter
import com.mooc.studyproject.databinding.FragmentIntelligentLearnListBinding
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import org.greenrobot.eventbus.EventBus
//import kotlinx.android.synthetic.main.fragment_intelligent_learn_list.*
import java.util.*


private const val ARG_PARAM1 = "param1"

/**
 *智能导学的学习项目学习清单
 * @author limeng
 * @date 3/9/22
 */
class IntelligentLearnListFragment : Fragment() {

    private val tabTextViewList = ArrayList<TextView>()
    var currentIndex = 0 //当前显示的位置，默认0
    var mStudyPlanDetailBean: StudyPlanDetailBean? = null
    var isDaShiMode: Boolean = false
    var studyListVpAdapter: StudyListVpAdapter? = null
    var studyNotIntelligentVpAdapter: StudyNotIntelligentVpAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mStudyPlanDetailBean = it.getParcelable(ARG_PARAM1) as StudyPlanDetailBean?
        }
    }

    companion object {
        const val INTENT_STUDY_PLAN_DYNAMIC = "intent_study_plan_dynamic"
        const val REFRESH_ALL = 169

        @JvmStatic
        fun newInstance(param1: StudyPlanDetailBean?) =
                IntelligentLearnListFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, param1)
                    }
                }
    }

    fun updateData(mStudyPlanDetailBean: StudyPlanDetailBean?) {
        this.mStudyPlanDetailBean = mStudyPlanDetailBean
        // 更新各个列表数据
        onRefresh()

    }

    private fun onRefresh() {


        if(mStudyPlanDetailBean == null) return

        mStudyPlanDetailBean?.apply {
            if ("1" != this.study_plan?.is_bind_testpaper) {
                studyNotIntelligentVpAdapter?.mStudyPlanDetailBean = this
//                val mustLearnFragment = studyNotIntelligentVpAdapter?.fragments?.get(StudyNotIntelligentVpAdapter.PAGE_MUST_LEARN) as MustLearnFragment
//                mustLearnFragment.mStudyPlanDetailBean = mStudyPlanDetailBean
//                mustLearnFragment.upDateData()
//                val finishLearnFragment = studyNotIntelligentVpAdapter?.fragments?.get(StudyNotIntelligentVpAdapter.PAGE_FINISH_LEARN) as FinishLearnFragment
//                finishLearnFragment.mStudyPlanDetailBean = mStudyPlanDetailBean
//                finishLearnFragment.upDateData()

                //通知刷新
                EventBus.getDefault().post(StudyProjectRefresh(this))
            } else {//智能导学
                studyListVpAdapter?.mStudyPlanDetailBean = this
//                val mustLearnFragment = studyListVpAdapter?.fragments?.get(StudyListVpAdapter.PAGE_MUST_LEARN) as MustLearnFragment
//                mustLearnFragment.mStudyPlanDetailBean = this
//                mustLearnFragment.upDateData()
//                val choseLearnFragment = studyListVpAdapter?.fragments?.get(StudyListVpAdapter.PAGE_CHOSE_LEARN) as ChoseLearnFragment
//                choseLearnFragment.mStudyPlanDetailBean = this
//                choseLearnFragment.upDateData()
//                val finishLearnFragment = studyListVpAdapter?.fragments?.get(StudyListVpAdapter.PAGE_FINISH_LEARN) as FinishLearnFragment
//                finishLearnFragment.mStudyPlanDetailBean = this
//                finishLearnFragment.upDateData()

                //通知刷新
                EventBus.getDefault().post(StudyProjectRefresh(this))

            }
        }

    }

    private var _binding : FragmentIntelligentLearnListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentIntelligentLearnListBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager(binding.viewPager)
        initTab()
        initListener()


    }


    private fun initViewPager(viewPager: ViewPager2) {
        if ("1" != mStudyPlanDetailBean?.study_plan?.is_bind_testpaper) {//普通学习清单
            studyNotIntelligentVpAdapter = StudyNotIntelligentVpAdapter(requireActivity(), isDaShiMode, mStudyPlanDetailBean)
            viewPager.adapter = studyNotIntelligentVpAdapter
        } else {//智能导学学习清单
            binding.tabMustLearn.setText(getString(R.string.title_must_learn))
            binding.tabChoseLearn.visiable(true)
            binding.lineTwo.visiable(true)
            studyListVpAdapter = StudyListVpAdapter(requireActivity(), isDaShiMode, mStudyPlanDetailBean)
            viewPager.adapter = studyListVpAdapter
        }

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentIndex = position
                seleteTab(position)
            }
        })
    }

    private fun initTab() {
        tabTextViewList.clear()

        if ("1" != mStudyPlanDetailBean?.study_plan?.is_bind_testpaper) {
            tabTextViewList.add(binding.tabMustLearn)
            tabTextViewList.add(binding.tabFinish)
        } else {//智能导学
            tabTextViewList.add(binding.tabMustLearn)
            tabTextViewList.add(binding.tabChoseLearn)
            tabTextViewList.add(binding.tabFinish)
        }
        //默认选中0
        seleteTab(0)
    }

    private fun initListener() {
        binding.tabMustLearn.setOnClickListener { // tab改变 viewpager 改变
            if (currentIndex != 0) {    //如果不是0，则切换到0
                changePosition(0)
            }
        }
        binding.tabChoseLearn.setOnClickListener {
            changePosition(1)
        }
        binding.tabFinish.setOnClickListener {
            if ("1" != mStudyPlanDetailBean?.study_plan?.is_bind_testpaper) {
                changePosition(1)
            } else {//智能导学
                changePosition(2)
            }
        }
    }

    /**
     * 设置选中的按钮状态
     */
    private fun seleteTab(position: Int) {
        for (i in tabTextViewList.indices) {
            val textView = tabTextViewList[i]
            if (position == i) {
//                //设置选中状态
                textView.setTextColor(requireContext().getColorRes(R.color.colorPrimary))
                textView.setBackgroundResource(R.color.color_D2F0E3)
            } else { //设置未选中状态
                textView.setTextColor(requireContext().getColorRes(R.color.color_94BFAC))
                textView.setBackgroundResource(R.color.color_white)

            }
        }
    }

    /**
     * 切换位置
     *
     * @param i
     */
    private fun changePosition(i: Int) {
        if (binding.viewPager.adapter != null && i >= binding.viewPager.adapter!!.itemCount) return
        binding.viewPager.currentItem = i
    }

    var mMustLearnFragment: MustLearnFragment? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ("1" != mStudyPlanDetailBean?.study_plan?.is_bind_testpaper) {
//            if (studyNotIntelligentVpAdapter?.fragments?.get(0) != null) {
//                mMustLearnFragment = studyNotIntelligentVpAdapter?.fragments?.get(0) as MustLearnFragment
//            }

            //todo 刷新mMustLearnFragment

        } else {//智能导学
//            if (studyListVpAdapter?.fragments?.get(0) != null) {
//                mMustLearnFragment = studyListVpAdapter?.fragments?.get(0) as MustLearnFragment
//            }

            //todo 刷新mMustLearnFragment

        }
        mMustLearnFragment?.onActivityResult(requestCode, resultCode, null)


    }


}