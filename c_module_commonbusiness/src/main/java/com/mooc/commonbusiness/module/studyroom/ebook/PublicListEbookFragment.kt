package com.mooc.commonbusiness.module.studyroom.ebook

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.eventbus.EbookProgressRefreshEvent
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.resource.widget.Space120FootView
import com.mooc.resource.widget.Space120LoadMoreView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 公开清单中电子书
 */
class PublicListEbookFragment : BaseUserLogListenFragment<EBookBean, PublicListEbookViewModel>() {


    companion object {
        fun getInstance(bundle: Bundle? = null): PublicListEbookFragment {
            val fragment = PublicListEbookFragment()
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
            .get(PublicListEbookViewModel::class.java)
        mViewModel?.fromRecommend = arguments?.getBoolean(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND) ?: false
        mViewModel?.fromTask = arguments?.getBoolean(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK) ?: false
        mViewModel?.fromTaskId = arguments?.getString(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK_ID) ?: ""

    }

    override fun initAdapter(): BaseQuickAdapter<EBookBean, BaseViewHolder>? {
        val userId = arguments?.getString(IntentParamsConstants.MY_USER_ID) ?: ""
        mViewModel?.userId = userId
        return mViewModel?.getPageData()?.value?.let {
            val ebookAdapter = EbookAdapter(it, true)
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

//    override fun getItemDecoration(): RecyclerView.ItemDecoration? = Padding15Divider1Decoration()

    override fun initEmptyView() {
        val tipStr = "抱歉，该学习清单里没有任何资源文件"
        //在学习室底部
        emptyView.setTitle(tipStr)
        emptyView.setEmptyIcon(R.drawable.common_gif_folder_empty)
        emptyView.setGravityTop(20.dp2px())
        emptyView.setIconOverride(150.dp2px(), 150.dp2px())
    }
    /***
     *更新电子书进度
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateEbookProgress(event: EbookProgressRefreshEvent) {
        mViewModel?.updateProgress(event.ebookId) {
            mAdapter?.notifyItemChanged(it)
        }
    }


}