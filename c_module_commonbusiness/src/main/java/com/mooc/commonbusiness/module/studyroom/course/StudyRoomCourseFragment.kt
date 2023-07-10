package com.mooc.commonbusiness.module.studyroom.course

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.base.BaseViewModelFactory
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.module.studyroom.StudyListDetailViewModel
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.StatisticsService
import com.mooc.commonbusiness.utils.ShowDialogUtils
import com.mooc.home.ui.decoration.Padding15Divider1Decoration
import com.mooc.resource.widget.Space120LoadMoreView


/**
 * 学习室课程
 *
 */
class StudyRoomCourseFragment : BaseUserLogListenFragment<CourseBean, CourseViewModel>() {
    val logService by lazy {
        ARouter.getInstance().navigation(StatisticsService::class.java)
    }

    companion object {
        fun getInstance(bundle: Bundle? = null): StudyRoomCourseFragment {
            val fragment = StudyRoomCourseFragment()
            bundle?.apply {
                fragment.arguments = this
            }
            return fragment
        }
    }

    var folderId: String = ""
    var parentViewModel: StudyListDetailViewModel? = null
    override fun genericViewModel() {
        //文件夹id，如果不传代表是根目录，""
        folderId = arguments?.getString(IntentParamsConstants.STUDYROOM_FOLDER_ID) ?: ""

        if (folderId.isNotEmpty()) {
            parentViewModel =
                ViewModelProviders.of(requireActivity())[StudyListDetailViewModel::class.java]
        }
        mViewModel = ViewModelProviders.of(this, BaseViewModelFactory(folderId))
            .get(CourseViewModel::class.java)
    }

    override fun initAdapter(): BaseQuickAdapter<CourseBean, BaseViewHolder>? {
        val let = (mViewModel as CourseViewModel).getPageData().value?.let {
            val discoverMoocAdapter = CourseAdapter(it)
            discoverMoocAdapter.setOnItemClickListener { adapter, view, position ->
                val courseBean = it[position]
                if (ResourceTypeConstans.allCourseDialogId.contains(courseBean.id)) {//工信部点击不进入课程详情页面，需要弹框的课程id
                    activity?.let { it1 -> ShowDialogUtils.showDialog(it1) }
                } else {
                    val logId =
                        if (courseBean.classroom_id.isNotEmpty()) "${courseBean.id}#${courseBean.classroom_id}" else courseBean.id
                    logService.addClickLog(
                        LogEventConstants2.P_ROOM,
                        logId,
                        "${courseBean._resourceType}",
                        courseBean.title,
                        "COURSE#${courseBean._resourceId}"
                    )
                    ResourceTurnManager.turnToResourcePage(courseBean)
                }
            }
            discoverMoocAdapter
        }

//        //获取公开状态，确定是否可以编辑
        parentViewModel?.publicStatus?.observe(this,
            Observer<Boolean> {
                let?.needEdit = !it
                let?.toast = resources.getString(R.string.text_no_publicate_tip)
            })
        return let
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration {
        return Padding15Divider1Decoration()
    }


    override fun initEmptyView() {
        //title文案，学习清单详情，和学习室底部不同
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
}