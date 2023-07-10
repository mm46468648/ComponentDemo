package com.mooc.home.ui.todaystudy.complete

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.changeskin.SkinManager
import com.mooc.commonbusiness.base.BaseUserLogListenFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.decoration.PaddingLeftRight15NoDividerDecoration
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.eventbus.RefreshTodayCompleteEvent
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.ShowDialogUtils
import com.mooc.home.R
import com.mooc.home.model.todaystudy.TodayTask
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.ktextention.setDrawLeft
import com.mooc.statistics.LogUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class TodayStudyCompleteFragment : BaseUserLogListenFragment<TodayTask, CompleteViewModel>() {


    fun getHeadView(): View {
        val textView = TextView(requireContext())
        textView.text = resources.getText(R.string.today_finish)
        textView.gravity = Gravity.CENTER_VERTICAL
        val layoutParams =
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(0, 20.dp2px(), 0, 0)
        textView.layoutParams = layoutParams

        //换肤
        val drawableLeft =
            SkinManager.getInstance().resourceManager.getDrawableByName("home_ic_todaystudy_complete_left")

        textView.setDrawLeft(drawableLeft, 10.dp2px())
        return textView
    }

    override fun initAdapter(): BaseQuickAdapter<TodayTask, BaseViewHolder>? {
        return (mViewModel as CompleteViewModel).getPageData().value?.let {
            val discoverMoocAdapter = CompleteAdapter(it)
            discoverMoocAdapter.addHeaderView(getHeadView())
            discoverMoocAdapter.setOnItemClickListener { _, _, position ->
                val any = it[position]

                LogUtil.addClickLogNew(
                    LogEventConstants2.P_TODAY, any._resourceId,
                    "${any._resourceType}",
                    any.title,
                    "${LogEventConstants2.typeLogPointMap[any._resourceType]}#${any._resourceId}"
                )


                if (CompleteViewModel.TASK_TITLE == any.name) {
                    //任务
//                    if ("0" == any.display_status) {
//                        ResourceTurnManager.turnToResourcePage(any)
//                    } else if ("1" == any.display_status) {
//
//                    }
                    ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS).withString(
                        IntentParamsConstants.PARAMS_TASK_ID, any.url
                    ).navigation();
                } else {
                    if (any._resourceType == ResourceTypeConstans.TYPE_COURSE
                        && ResourceTypeConstans.allCourseDialogId.contains(any._resourceId)
                    ) {//工信部点击不进入课程详情页面，需要弹框的课程id
                        activity?.let { it1 -> ShowDialogUtils.showDialog(it1) }
                    } else {
                        ResourceTurnManager.turnToResourcePage(any as BaseResourceInterface)
                    }
                }

            }
            discoverMoocAdapter
        }
    }

    override fun neadLoadMore(): Boolean {
        return false
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration =
        PaddingLeftRight15NoDividerDecoration()

    override fun initEmptyView() {
        emptyView.setGravityTop(20.dp2px())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUCompleteRefreshEvent(userInfo: RefreshTodayCompleteEvent) {
        loadDataWithRrefresh()
    }


}