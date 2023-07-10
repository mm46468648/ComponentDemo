package com.mooc.studyproject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.model.eventbus.StudyProjectRefresh
import com.mooc.studyproject.R
import com.mooc.studyproject.adapter.StudySourceAdapter
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.studyproject.model.StudyPlanSource
import com.mooc.studyproject.presenter.ItemClickPresenter
import com.mooc.studyproject.viewmodel.ChoseLearnViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 选学页面
 *
 */
class ChoseLearnFragment(
    var isDaShiMode: Boolean = false,
    var mStudyPlanDetailBean: StudyPlanDetailBean? = null
) : BaseListFragment<StudyPlanSource, ChoseLearnViewModel>() {
    val itemClickPresenter: ItemClickPresenter by lazy {
        ItemClickPresenter(
            requireActivity(),
            mStudyPlanDetailBean
        )
    }

//    fun upDateData() {
//        if (mAdapter != null) {
//            val adapter = mAdapter as StudySourceAdapter
//            adapter.mStudyPlanData = mStudyPlanDetailBean?.study_plan
//            adapter.mJoin = mStudyPlanDetailBean?.is_join ?: 0
//            loadDataWithRrefresh()
//        }
//
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(refresh: StudyProjectRefresh) {
        this.mStudyPlanDetailBean = refresh.detail
        val adapter = mAdapter as StudySourceAdapter
        adapter.mStudyPlanData = mStudyPlanDetailBean?.study_plan
        adapter.mJoin = mStudyPlanDetailBean?.is_join ?: 0
        loadDataWithRrefresh()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = mAdapter as StudySourceAdapter
        adapter.addFooterView(getFootView())
    }

    override fun initAdapter(): BaseQuickAdapter<StudyPlanSource, BaseViewHolder>? {
        val choseLearnViewModel = mViewModel as ChoseLearnViewModel
        choseLearnViewModel.id = mStudyPlanDetailBean?.study_plan?.id
        return choseLearnViewModel.getPageData().value?.let {
            emptyView.isShowEmptyIcon(false)
            emptyView.setTitle(getString(R.string.text_str_chose_learn_study_check))
            emptyView.setTitleViewBottom(100)
            val studySourceAdapter = StudySourceAdapter(it)
            studySourceAdapter.isDaShiMode = isDaShiMode
            studySourceAdapter.mStudyPlanData = mStudyPlanDetailBean?.study_plan
            studySourceAdapter.mJoin = mStudyPlanDetailBean?.is_join ?: 0


            studySourceAdapter.setOnItemClickListener { _, _, position ->
                itemClickPresenter.ItemClick(
                    studySourceAdapter,
                    position
                )
            }



            studySourceAdapter
        }
    }

    /**
     * 增加脚布局，适配底部的view
     */
    private fun getFootView(): View {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.studyproject_item_plan_foot, null);
        return view;
    }
}