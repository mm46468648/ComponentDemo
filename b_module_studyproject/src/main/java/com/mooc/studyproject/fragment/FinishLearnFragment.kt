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
import com.mooc.studyproject.eventbus.ClickFinishEvent
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.studyproject.model.StudyPlanSource
import com.mooc.studyproject.presenter.ItemClickPresenter
import com.mooc.studyproject.viewmodel.FinishLearnViewModel
//import kotlinx.android.synthetic.main.studyproject_fragment_study_source.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class FinishLearnFragment(
    var isDaShiMode: Boolean = false,
    var mStudyPlanDetailBean: StudyPlanDetailBean? = null
) : BaseListFragment<StudyPlanSource, FinishLearnViewModel>() {


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
        val finishLearnViewModel = mViewModel as FinishLearnViewModel
        finishLearnViewModel.id = mStudyPlanDetailBean?.study_plan?.id

        return finishLearnViewModel.getPageData().value?.let {
            emptyView.isShowEmptyIcon(false)
            emptyView.setTitle(getString(R.string.text_str_no_finish_study_check))
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

    private fun getFootView(): View {
        return LayoutInflater.from(requireContext())
            .inflate(R.layout.studyproject_item_plan_foot, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(value: ClickFinishEvent) {
        if (value.event == 0) {
            loadDataWithRrefresh()
        }

    }
}