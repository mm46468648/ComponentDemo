package com.mooc.commonbusiness.module.studyroom.publication

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.search.PublicationBean
import com.mooc.home.ui.decoration.Padding15Divider1Decoration
import com.mooc.resource.widget.Space120LoadMoreView


/**
 * 公开清单中电子书
 */
class PublicListPublicationkFragment : BaseUserLogListenFragment<PublicationBean, PublicListPublicationViewModel>() {


    companion object {
        fun getInstance(bundle: Bundle? = null): PublicListPublicationkFragment {
            val fragment = PublicListPublicationkFragment()
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
            .get(PublicListPublicationViewModel::class.java)
    }

    override fun initAdapter(): BaseQuickAdapter<PublicationBean, BaseViewHolder>? {
        val userId = arguments?.getString(IntentParamsConstants.MY_USER_ID) ?: ""
        mViewModel?.userId = userId
        return mViewModel?.getPageData()?.value?.let {
            val ebookAdapter = PublicationAdapter(it, false)
            //点击跳转电子书详情
            ebookAdapter.setOnItemClickListener { adapter, view, position ->
                val eBookBean = it[position]
                ResourceTurnManager.turnToResourcePage(eBookBean)
            }
            ebookAdapter
        }
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
    }


    override fun getItemDecoration(): RecyclerView.ItemDecoration = Padding15Divider1Decoration()

    override fun initEmptyView() {
        val tipStr = "抱歉，该学习清单里没有任何资源文件"
        //在学习室底部
        emptyView.setTitle(tipStr)
        emptyView.setEmptyIcon(R.drawable.common_gif_folder_empty)
        emptyView.setGravityTop(20.dp2px())
        emptyView.setIconOverride(150.dp2px(), 150.dp2px())
    }

}