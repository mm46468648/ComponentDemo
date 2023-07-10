package com.mooc.periodical.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.search.PeriodicalBean
import com.mooc.periodical.viewmodel.PeriodicalDetailViewModel
import com.mooc.periodical.viewmodel.PeriodicalListViewModel

class PeriodicalListFragment : BaseListFragment<PeriodicalBean, PeriodicalListViewModel>() {

//    val parentViewModel by lazy {
//        ViewModelProviders.of(requireActivity())[PeriodicalDetailViewModel::class.java]
//    }

    val parentViewModel by viewModels<PeriodicalDetailViewModel>(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val onCreateView = super.onCreateView(inflater, container, savedInstanceState)

        //传递id
        val string = arguments?.getString(IntentParamsConstants.PARAMS_RESOURCE_ID, "")?:""
        mViewModel?.pId = string

        parentViewModel.currentSelectTerm.observe(this, Observer {
            mViewModel?.year = it.first
            mViewModel?.term = it.second
            loadDataWithRrefresh()
        })
        return onCreateView


    }

    override fun initAdapter(): BaseQuickAdapter<PeriodicalBean, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value.let {
            val periodicalListAdapter = PeriodicalListAdapter(it)
            periodicalListAdapter.setOnItemClickListener { adapter, view, position ->
                val any = adapter.data[position] as PeriodicalBean
                ResourceTurnManager.turnToResourcePage(any)
            }
            periodicalListAdapter
        }
    }
}