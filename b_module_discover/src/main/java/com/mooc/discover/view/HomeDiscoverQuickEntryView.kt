package com.mooc.discover.view

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.adapter.QuickEntryAdapter
import com.mooc.discover.column.ColumnQuickActivity
import com.mooc.discover.databinding.HomeViewDiscoverQuickEntryBinding
import com.mooc.discover.model.QuickEntry
import com.mooc.discover.ui.HotResourceActivity
import com.mooc.statistics.LogUtil

//import kotlinx.android.synthetic.main.home_view_discover_quick_entry.view.*


/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    首页发现推荐金刚区View
 * @Author:         xym
 * @CreateDate:     2020/8/12 3:51 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/12 3:51 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class HomeDiscoverQuickEntryView @JvmOverloads constructor(
    var mContext: Context,
    var attributeSet: AttributeSet? = null,
    var defaultInt: Int = 0
) :
    FrameLayout(mContext, attributeSet, defaultInt) {

    //    val titleArray = arrayOf("每日推荐","精选资源","排行榜","最新上线"
//            ,"军事","防疫","军地课程","科技","时间管理","养生运动","体能训练","哲学")
//    val iconArray = intArrayOf()
    val spanCount = 4
    val quickEntryList = arrayListOf<QuickEntry>()
    val quickEntryAdapter = QuickEntryAdapter(quickEntryList)

    companion object {
        const val QUICK_ENTRY_TYPE1 = "1"
        const val QUICK_ENTRY_TYPE2 = "2"
        const val QUICK_ENTRY_TYPE3 = "3"
        const val QUICK_ENTRY_TYPE4 = "4"
        const val QUICK_ENTRY_TYPE5 = "5"
    }

    //    val binding = HomeViewDiscoverQuickEntryBinding.inflate(LayoutInflater.from(getContext()), this);
    var binding: HomeViewDiscoverQuickEntryBinding =
        HomeViewDiscoverQuickEntryBinding.inflate(LayoutInflater.from(context), this)


    init {
//        LayoutInflater.from(mContext).inflate(R.layout.home_view_discover_quick_entry, this)
        binding.rcyQuickEntry.layoutManager = GridLayoutManager(mContext, spanCount)
        binding.rcyQuickEntry.adapter = quickEntryAdapter
        quickEntryAdapter.setOnItemClickListener { adapter, view, position ->

            val quickEntry = quickEntryList[position]
            if (quickEntry.relation_type == 1) {     //需要跳转特定资源
                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER,
                    "${position + 1}",
                    LogEventConstants2.ET_QS,
                    quickEntry.name,
                    "${LogEventConstants2.typeLogPointMap[quickEntry._resourceType]}#${quickEntry._resourceId}"
                )
            } else {
                LogUtil.addClickLogNew(
                    LogEventConstants2.P_DISCOVER, "${position + 1}",
                    LogEventConstants2.ET_QS, quickEntry.name
                )
            }
            onQuickEntryClick(quickEntry)
        }
    }


    /**
     * 设置快捷入口数据
     */
    fun setRotationBean(it: List<QuickEntry>) {
        if (it.isNotEmpty()) {
            quickEntryList.clear()
            quickEntryList.addAll(it)
            quickEntryAdapter.notifyDataSetChanged()
        }
    }


    /**
     * 点击快捷入口
     */
    private fun onQuickEntryClick(quickEntry: QuickEntry) {
        if (quickEntry.show_type == -1) {
            toast("该资源已下线")
            return
        }
        when (quickEntry.relation_type.toString()) {
            QUICK_ENTRY_TYPE1 -> {
                toSourceActivity(quickEntry)
            }
            QUICK_ENTRY_TYPE2 -> {
                if (quickEntry.source_select == "1" || quickEntry.source_select == "2") {
                    // 跳转到专题页面？,走一个接口，数据模型一样，但是样式不一样，可以通过Adapter，加载不同样式，没必要去跳转两个页面
                    ARouter.getInstance().build(Paths.PAGE_HOT_RESOURCE)
                        .withString(HotResourceActivity.HOME_HOT_TYPE, quickEntry.source_select)
                        .navigation()
                } else if (quickEntry.source_select == "3") {
                    // 跳转最新上线页面
                    ARouter.getInstance().build(Paths.PAGE_NEW_ONLINE)
                        .navigation()
                } else if (quickEntry.source_select == "4") {
                    ARouter.getInstance().build(Paths.PAGE_COLUMN_ALL).navigation()
                }
            }
            QUICK_ENTRY_TYPE3 -> { //跳转到h5页面
//                toast("跳转到h5标题${quickEntry.name} \r\n 链接:${quickEntry.source_select}")
                ARouter.getInstance().build(Paths.PAGE_WEB)
                    .with(
                        Bundle()
                            .put(IntentParamsConstants.WEB_PARAMS_TITLE, quickEntry.name)
                            .put(IntentParamsConstants.WEB_PARAMS_URL, quickEntry.source_select)
                    )
                    .navigation()
            }
            QUICK_ENTRY_TYPE4 -> { //跳转发现页面其他资源页面
                val sourcePosition = quickEntry.source_select.toInt() //想要跳转的资源的位置
                val childId = quickEntry.sort_id //想要跳转的资源的二级分类id

                val pair = Pair(sourcePosition, childId)
                LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_DISCOVER_TAB_CHANGE)
                    .postValue(pair)
            }
            QUICK_ENTRY_TYPE5 -> {//跳转组合栏目页面
                ARouter.getInstance().build(Paths.PAGE_COLUMN_QUICK)
                    .withString(ColumnQuickActivity.QUICK_COLUMN_TITLE, quickEntry.name)
                    .withString(ColumnQuickActivity.QUICK_COLUMN_ID, quickEntry.id).navigation()
            }
        }
    }


    /**
     * 跳转各类资源页面
     */
    private fun toSourceActivity(quickEntry: QuickEntry) {
        if (!GlobalsUserManager.isLogin()) {
            ResourceTurnManager.turnToLogin()
            return
        }

        ResourceTurnManager.turnToResourcePage(quickEntry)

    }
}