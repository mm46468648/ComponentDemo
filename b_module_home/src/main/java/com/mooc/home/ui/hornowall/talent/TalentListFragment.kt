package com.mooc.home.ui.hornowall.talent

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.MY_USER_ID
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.route.Paths
import com.mooc.common.ktextends.put
import com.mooc.home.R
import com.mooc.home.model.HonorRollResponse
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.widget.Space120LoadMoreView

/**
 * 学习项目达人Fragment
 *
 */
class TalentListFragment : BaseListFragment<Any, TalentViewModel>() {
//    lateinit var discoverMoocAdapter: TalentAdapter

    //学习项目达人项目页面头布局
    val projectPageHead by lazy {
        getHeadTextView()
    }


    override fun initAdapter(): BaseQuickAdapter<Any, BaseViewHolder>? {
        return (mViewModel as TalentViewModel).getPageData()?.value?.let {
            val discoverMoocAdapter = TalentAdapter(it)
            //添加头部布局
            discoverMoocAdapter.addHeaderView(projectPageHead)
            //设置点击事件
            discoverMoocAdapter.setOnItemClickListener { _, _, position ->
                onClickItem(discoverMoocAdapter, position)
            }
            discoverMoocAdapter
        }
    }

    /**
     * 布局管理器
     */
    override fun getLayoutManager(): LinearLayoutManager {
        //配置Manager，当TiTle类型需要独占一行
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                //由于有一个headview布局，position会比集合数据多1，所以position要减去一个
                val realItemPosition = position - 1
                val datas = (mViewModel as TalentViewModel).getPageData()?.value
                //避免索引越界
                if (datas?.indices?.contains(realItemPosition) == true) {
                    val any: Any = datas[realItemPosition]
                    //分割条类型，和学习项目Title，需要独占一行（3个位置）
                    return if (any is HonorRollResponse || any is String) 3 else 1
                }
                return 3
            }
        };
        return gridLayoutManager
    }

    /**
     * 点击Item
     */
    private fun onClickItem(discoverMoocAdapter: TalentAdapter, position: Int) {
        val itemData = discoverMoocAdapter.data[position]
        if (itemData is HonorRollResponse && itemData.success_nums>0) {
            //对跟布局新添加一个展示详情达人列表的Fragment
            if (itemData.id.isNotEmpty()) {
                val talentDetailListFragment = TalentDetailListFragment()
                talentDetailListFragment.arguments =
                        Bundle().put(TalentDetailListFragment.PARAMS_KEY_PLANID, itemData.id)
                                .put(TalentDetailListFragment.PARAMS_KEY_PLANNAME, itemData.plan_name)
                childFragmentManager.beginTransaction().add(R.id.flRoot, talentDetailListFragment).commit()
            }
        }

        if (itemData is UserInfo) {
            //跳转到用户主页页面
            ARouter.getInstance()
                    .build(Paths.PAGE_USER_INFO)
                    .withString(MY_USER_ID, itemData.user_id)
                    .navigation()
        }

    }

    /**
     * 生成头部布局
     * 排名随机显示文案提示
     */
    private fun getHeadTextView(): View {
        val textView = TextView(requireContext())
        textView.text = "排名随机显示"
        textView.gravity = Gravity.CENTER
        textView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 30.dp2px())
        return textView
    }

    override fun getLoadMoreView(): BaseLoadMoreView {
        return Space120LoadMoreView("查看更多")
    }

    //不需要自动加载更多，手动点击加载更多
    override fun getAutoLoadMore(): Boolean = false
}