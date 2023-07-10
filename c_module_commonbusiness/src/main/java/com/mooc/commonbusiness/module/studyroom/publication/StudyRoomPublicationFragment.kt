package com.mooc.commonbusiness.module.studyroom.publication

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.search.PublicationBean
import com.mooc.home.ui.decoration.Padding15Divider1Decoration
import com.mooc.resource.widget.Space120LoadMoreView


/**
 * 学习室刊物
 *
 */
class StudyRoomPublicationFragment : BaseUserLogListenFragment<PublicationBean, PublicationViewModel>() {


    companion object {
        fun getInstance(bundle: Bundle? = null): StudyRoomPublicationFragment {
            val fragment = StudyRoomPublicationFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    override fun genericViewModel() {
        //文件夹id，如果不传代表是根目录，""
        val folderId = arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID) ?: ""
        mViewModel = ViewModelProviders.of(this, BaseViewModelFactory(folderId))
            .get(PublicationViewModel::class.java)
    }

    override fun initAdapter(): BaseQuickAdapter<PublicationBean, BaseViewHolder>? {
        return (mViewModel as PublicationViewModel).getPageData().value?.let {
            val discoverMoocAdapter = PublicationAdapter(it)
            discoverMoocAdapter.setOnItemClickListener { adapter, view, position ->
                val courseBean = it[position]
                ResourceTurnManager.turnToResourcePage(courseBean)
            }
            discoverMoocAdapter
        }
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration {
        return Padding15Divider1Decoration()
    }



    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }


    override fun initEmptyView() {

        val tipStr = if (mViewModel?.folderId?.isEmpty() == true) "你还没有添加资源"
        else "抱歉，该学习清单里没有任何资源文件"
        //在学习室底部
        emptyView.setTitle(tipStr)
        emptyView.setGravityTop(20.dp2px())
    }
}