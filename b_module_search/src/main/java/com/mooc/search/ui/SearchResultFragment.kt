package com.mooc.search.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.search.R
import com.mooc.search.adapter.SearchResultAdapterNew
import com.mooc.search.model.SearchItem
import com.mooc.search.utils.SearchConstants
import com.mooc.search.viewmodel.SearchResultViewModel
import com.mooc.search.viewmodel.SearchViewModel

/**
 * 搜索结果列表
 */
class SearchResultFragment : BaseListFragment2<SearchItem, SearchResultViewModel>() {


    val parentViewModel by viewModels<SearchViewModel>(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val onCreateView = super.onCreateView(inflater, container, savedInstanceState)

        val searchWord = arguments?.getString(SearchConstants.RESCOURSE_WORD) ?: ""
        val isVague = arguments?.getInt(SearchConstants.RESCOURSE_ISVAGUE)

        mViewModel?.map = hashMapOf<String, String>(
            "page_size" to "2",
            "word" to searchWord,
            "search_match_type" to isVague.toString()
        )
        return onCreateView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentViewModel.fillterTypes.observe(viewLifecycleOwner) { typeList ->

            val list = mViewModel?.totalList?.let {
                it.filter { searItem ->
                    searItem.resource_type in typeList
                }
            }
            mAdapter?.setList(list as MutableList<SearchItem>?)


        }
    }

    override fun initAdapter(): BaseQuickAdapter<SearchItem, BaseViewHolder>? {
        return mViewModel?.getPageData()?.value?.let {
            val searchResultAdapterNew = SearchResultAdapterNew(it)

            //点击查看全部
            searchResultAdapterNew.addChildClickViewIds(R.id.llTop)
            searchResultAdapterNew.setOnItemChildClickListener { adapter, view, position ->
                if (view.id == R.id.llTop) {
                    val intent = Intent()
                    val searchItem = it.get(position)
                    intent.setClass(requireContext(), SearchListActivity().javaClass)
                    intent.putExtra(SearchConstants.RESCOURSE_TYPE, searchItem.resource_type)
                    intent.putExtra(
                        SearchConstants.RESCOURSE_SEARCH_NUM,
                        searchItem.ownTotalCount.toString()
                    )
                    intent.putExtra(SearchConstants.RESCOURSE_WORD, mViewModel?.map?.get("word"))
                    intent.putExtra(
                        SearchConstants.RESCOURSE_ISVAGUE,
                        mViewModel?.map?.get("search_match_type")
                    )
                    startActivity(intent)
                }
            }
            //点击资源跳转
            searchResultAdapterNew.setOnItemClickListener { adapter, view, position ->
                val searchItem = it.get(position)
                ResourceTurnManager.turnToResourcePage(searchItem)
            }

            searchResultAdapterNew
        }
    }

    /**
     * 不启用下拉刷新
     */
    override fun needPullToRefresh() = false
}