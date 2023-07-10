package com.mooc.commonbusiness.module.studyroom.ebook

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.model.eventbus.EbookProgressRefreshEvent
import com.mooc.commonbusiness.module.studyroom.StudyListDetailViewModel
import com.mooc.commonbusiness.route.routeservice.StatisticsService
import com.mooc.resource.widget.Space120LoadMoreView
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 学习室课程
 */
class StudyRoomEbookFragment : BaseUserLogListenFragment<EBookBean, StudyRoomEbookViewModel>() {
    val logService by lazy {
        ARouter.getInstance().navigation(StatisticsService::class.java)
    }
    companion object {
        fun getInstance(bundle: Bundle? = null): StudyRoomEbookFragment {
            val fragment = StudyRoomEbookFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    var folderId:String = ""
    var parentViewModel: StudyListDetailViewModel? = null

    override fun genericViewModel() {
        //文件夹id，如果不传代表是根目录，""
        folderId = arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID) ?: ""
        if (folderId.isNotEmpty()) {
            parentViewModel =
                ViewModelProviders.of(requireActivity())[StudyListDetailViewModel::class.java]
        }
        mViewModel = ViewModelProviders.of(this, BaseViewModelFactory(folderId))
            .get(StudyRoomEbookViewModel::class.java)
    }

    override fun initAdapter(): BaseQuickAdapter<EBookBean, BaseViewHolder>? {
        val let = mViewModel?.getPageData()?.value?.let {
            val ebookAdapter = EbookAdapter(it)
            //点击跳转电子书详情
            ebookAdapter.setOnItemClickListener { adapter, view, position ->
                val eBookBean = it[position]
//                ARouter.getInstance().build(Paths.PAGE_EBOOK_DETAIL)
//                    .withString(IntentParamsConstants.EBOOK_PARAMS_ID, eBookBean.id).navigation()

                logService.addClickLog(LogEventConstants2.P_ROOM,eBookBean._resourceId,"${eBookBean._resourceType}",
                    eBookBean.title,"BOOK#${eBookBean._resourceId}")

                ResourceTurnManager.turnToResourcePage(eBookBean)
            }
            ebookAdapter
        }

        parentViewModel?.publicStatus?.observe(this,
            Observer<Boolean> {
                let?.needEdit = !it
                let?.toast = resources.getString(R.string.text_no_publicate_tip)
            })

        return let
    }

//    override fun getItemDecoration(): RecyclerView.ItemDecoration? = Padding15Divider1Decoration()

    override fun initEmptyView() {
        val tipStr = if (mViewModel?.folderId?.isEmpty() == true) "你还没有添加资源"
        else "抱歉，该学习清单里没有任何资源文件"
        //在学习室底部
        emptyView.setTitle(tipStr)
        emptyView.setEmptyIcon(R.drawable.common_gif_folder_empty)
        emptyView.setGravityTop(20.dp2px())
        emptyView.setIconOverride(150.dp2px(), 150.dp2px())
        emptyView.setButton("+添加学习资源", View.OnClickListener {
            //跳转专题页面可以通过拦截器判断登录状态
            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@OnClickListener
            }
            //已登录，跳转发现页
            ARouter.getInstance().build(Paths.PAGE_HOME)
                .withInt(IntentParamsConstants.HOME_SELECT_POSITION, 0)
                .withInt(IntentParamsConstants.HOME_SELECT_CHILD_POSITION, 0)
                .navigation()
        })
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView()
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