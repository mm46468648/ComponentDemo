package com.mooc.home.ui.hornowall.talent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.base.BaseListFragment
import com.mooc.commonbusiness.base.BaseListFragment2
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.route.Paths
import com.mooc.home.R
import java.util.ArrayList

class TalentDetailListFragment : BaseListFragment2<Any, TalentDetailViewModel>() {

    companion object {
        const val PARAMS_KEY_PLANID = "params_key_planid"
        const val PARAMS_KEY_PLANNAME = "params_key_planname"
    }

//    var planName: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //在发起请求前做一些ViewModel初始化的逻辑
        val planId = arguments?.getString(PARAMS_KEY_PLANID) ?: ""
        val planName = arguments?.getString(PARAMS_KEY_PLANNAME) ?: ""
        (mViewModel as TalentDetailViewModel).planId = planId


        view.findViewById<FrameLayout>(com.mooc.commonbusiness.R.id.flRoot).addView(getTalentDetailHeadView(planName))
        (refresh_layout?.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0, 78.dp2px(), 0, 0)
        super.onViewCreated(view, savedInstanceState)


    }

    override fun initAdapter(): BaseQuickAdapter<Any, BaseViewHolder>? {
        return (mViewModel as TalentDetailViewModel).getPageData().value?.let {
            val discoverMoocAdapter = TalentAdapter(it)
//            //添加头部布局
//            discoverMoocAdapter.addHeaderView(getTalentDetailHeadView())
            //设置点击事件
            discoverMoocAdapter.setOnItemClickListener { _, _, position ->
                onClickItem(position, it)
            }
            discoverMoocAdapter
        }
    }

    private fun onClickItem(position: Int, it: ArrayList<Any>) {
        val any = it[position]
        if (any is UserInfo) {
            //查看个人主页
            ARouter.getInstance().build(Paths.PAGE_USER_INFO)
                .withString(IntentParamsConstants.MY_USER_ID, any.user_id)
                .navigation()
        }


    }

    private fun getTalentDetailHeadView(planName: String): View {
        val inflate = LayoutInflater.from(requireContext())
            .inflate(R.layout.home_view_honor_talentdetail_head, null)
        inflate.findViewById<ImageButton>(R.id.ibBack).setOnClickListener {
            parentFragment?.childFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
        inflate.findViewById<TextView>(R.id.tvTitle).setText(planName)
        return inflate
    }

    /**
     * 重写LayoutManager，展示网络布局
     */
    override fun getLayoutManager(): LinearLayoutManager {
        //配置Manager，当TiTle类型需要独占一行
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
//        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                //由于有一个headview布局，position会比集合数据多1，所以position要减去一个
//                val realItemPosition = position - 1
//                val datas = (mViewModel as TalentDetailViewModel).getPageData()?.value
//                //避免索引越界
//                if (datas?.indices?.contains(realItemPosition) == true) {
//                    val any: Any = datas[realItemPosition]
//                    //分割条类型，和学习项目Title，需要独占一行（3个位置）
//                    return if (any is HonorRollResponse || any is String) 3 else 1
//                }
//                return 3
//            }
//        }
        return gridLayoutManager
    }
}