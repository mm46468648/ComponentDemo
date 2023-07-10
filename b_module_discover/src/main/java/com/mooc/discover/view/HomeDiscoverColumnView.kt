package com.mooc.discover.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.DiscoverRecommendColumnAdapter
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.RecommendColumn
import io.reactivex.disposables.CompositeDisposable

/**
 * 发现页专栏View
 */
class HomeDiscoverColumnView @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        FrameLayout(mContext, attributeSet, defaultInt) {

    private var recyclerView: RecyclerView = RecyclerView(mContext)
    val columnlist = arrayListOf<RecommendColumn>()
    val columnAdapter = DiscoverRecommendColumnAdapter(columnlist)


    init {
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        addView(recyclerView, layoutParams)

        val linearLayoutManager = LinearLayoutManager(mContext)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = columnAdapter
    }


    /**
     * 设置专栏数据
     */
    fun setColumnData(list: List<RecommendColumn>?) {
        if (list?.isNotEmpty() == true) {
            columnlist.clear()
            columnlist.addAll(list)

            columnAdapter.notifyDataSetChanged()
        }
    }



}
