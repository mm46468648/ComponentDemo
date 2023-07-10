package com.mooc.home.ui.todaystudy.must

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.sharepreference.SpUtils
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.eventbus.ScoreChangeNeedRefreshEvent
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.route.Paths
import com.mooc.home.R
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.widget.Space80EmptyView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 最近在学页面
 */
class TodayStudyMustFragment : BaseListFragment2<Pair<Int, List<Any>>, MustViewModel>() {

    private lateinit var mustViewModel: MustViewModel

    val headView: View by lazy {
        View.inflate(requireContext(), R.layout.home_item_todaystudy_head, null)
    }

    //获取系统震动服务
    val vib by lazy { requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }

    override fun initAdapter(): BaseQuickAdapter<Pair<Int, List<Any>>, BaseViewHolder>? {
        mustViewModel = mViewModel as MustViewModel
        //监听签到状态改变
        return mustViewModel.getPageData().value?.let {
            val mustAdapter = MustAdapter2(it, mustViewModel)

            //拖拽排序提醒
            if (!SpUtils.get().getValue(SpConstants.TODAY_STUDY_SORT_TIP, false)) {
                headView.findViewById<ImageButton>(R.id.ibClose).setOnClickListener {
                    mustAdapter.removeHeaderView(headView)
                    SpUtils.get().putValue(SpConstants.TODAY_STUDY_SORT_TIP, true)
                }

                //移除parent
                if (headView.parent != null) {
                    (headView.parent as ViewGroup).removeView(headView)
                }
                mustAdapter.setHeaderView(headView)
            }

            mustAdapter.setFooterView(Space80EmptyView(requireContext()))

            //设置拖拽帮助
            mustAdapter.draggableModule.isDragEnabled = true
            mustAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
                override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {
                }

                override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    //改变背景颜色，并震动一下,震动70毫秒
                    viewHolder?.itemView?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_B2F))
                    @Suppress("DEPRECATION")
                    vib.vibrate(70)
                }

                override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    //还原背景颜色
                    viewHolder?.itemView?.setBackgroundColor(0)

                    val arrayList = mustAdapter.data as ArrayList<Pair<Int, List<Any>>>
                    val map = arrayList.map { it.first }
                    mViewModel?.postNewOrderList(map as ArrayList<Int>)
                    mViewModel?.getPageData()?.postValue(arrayList)
                }
            })
            mustAdapter
        }
    }

    //一次性请求，不需要加载更多
    override fun neadLoadMore(): Boolean = false

    override fun initEmptyView() {
        emptyView.setTitle("没有找到学习资源\r\n学习一门课或参加一个学习项目")
        emptyView.setEmptyIcon(R.mipmap.common_ic_empty)
        emptyView.setGravityTop(20.dp2px())
        emptyView.setButton("+添加学习资源") {
            //跳转发现页
            ARouter.getInstance().build(Paths.PAGE_HOME)
                    .withInt(IntentParamsConstants.HOME_SELECT_POSITION, 0)
                    .navigation()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserScoreEvent(userInfo: ScoreChangeNeedRefreshEvent) {
        if (GlobalsUserManager.isLogin()) {
            loadDataWithRrefresh()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserLoginStateEvent) {
        loge("${this::class.java.simpleName}收到了登录事件")
        if (userInfo.userInfo == null) {
            //退出登录，直接清空列表数据
            mViewModel?.getPageData()?.value?.clear()
            mViewModel?.getPageData()?.postValue(mViewModel?.getPageData()?.value)
            mAdapter?.notifyDataSetChanged()
        } else {
            loadDataWithRrefresh()
        }
    }

}