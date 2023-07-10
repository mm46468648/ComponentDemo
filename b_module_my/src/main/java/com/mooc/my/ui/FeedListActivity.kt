package com.mooc.my.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.mooc.my.R
import com.mooc.my.adapter.FeedListAdapter
import com.mooc.my.model.FeedUserBean
import com.mooc.my.viewmodel.FeedBackViewModel
import com.mooc.my.pop.FeedScreenPop
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.SystemUtils
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.databinding.ActivityFeedBackListBinding
import com.mooc.my.databinding.ActivityFeedListBinding
//import kotlinx.android.synthetic.main.activity_feed_list.*

/**
 *我的意见反馈
 * @author limeng
 * @date 2020/10/19
 *
 * 暂时用不上
 */
@Route(path = Paths.PAGE_FEED_LIST)
class FeedListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener {
    private var search: Int = 1// 1全部  0 未读
    var offset: Int = 0
    var mFeedListAdapter: FeedListAdapter? = null
    var emptyView: View? = null
    var headView: View? = null
    private var feedScreenPop: FeedScreenPop? = null
    private lateinit var inflate : ActivityFeedListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityFeedListBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()
        initData()
        initListener()
//        getData()
    }

//    val mViewModel: FeedBackViewModel by lazy {
//        ViewModelProviders.of(this)[FeedBackViewModel::class.java]
//    }
    val mViewModel: FeedBackViewModel by viewModels()


    private fun initView() {
        inflate.commonTitleLayout.setOnLeftClickListener { finish() }
    }

    private fun initData() {
        mFeedListAdapter = FeedListAdapter(null)
        inflate.rcvFeedList.layoutManager = LinearLayoutManager(this)
        inflate.rcvFeedList.adapter = mFeedListAdapter
        emptyView = layoutInflater.inflate(R.layout.my_layout_feed_empty, inflate.content, false)
        headView = layoutInflater.inflate(R.layout.my_layout_feed_top, inflate.content, false)
        emptyView?.let {
        mFeedListAdapter?.setEmptyView(emptyView!!)}
        headView?.let {
        mFeedListAdapter?.addHeaderView(headView!!)}

    }

    private fun initListener() {
        inflate.swipeFeedList.setOnRefreshListener(this)
        inflate.commonTitleLayout.setOnLeftClickListener { finish() }
        mViewModel.feedUserBean.observe(this, Observer {
            if (offset == 0) {
                inflate.swipeFeedList.isRefreshing = false
            }
            setData(it)
        })
        mFeedListAdapter?.loadMoreModule?.setOnLoadMoreListener(this)
        emptyView?.findViewById<TextView>(R.id.tv_feed_empty_submit)?.setOnClickListener {
            //进入提交新反馈页面页面
            ARouter.getInstance().build(Paths.PAGE_FEED_BACK).navigation()
        }
        headView?.findViewById<TextView>(R.id.tv_feed_top_sub)?.setOnClickListener {
            //同上
            ARouter.getInstance().build(Paths.PAGE_FEED_BACK).navigation()
        }
        mFeedListAdapter?.setOnItemClickListener { adapter, view, position ->
            //单条反馈进入
            mFeedListAdapter?.data?.get(position)?.is_read_message = "0"
            mFeedListAdapter?.notifyDataSetChanged()
            ARouter.getInstance().build(Paths.PAGE_FEED_BACK_LIST)
                    .withString(FeedBackListActivity.FEED_ID, mFeedListAdapter?.data?.get(position)?.id)
                    .navigation()
        }
        inflate.commonTitleLayout.tv_right?.setOnClickListener {
            //筛选全部还是未读
            feedScreenPop = FeedScreenPop(this, inflate.rcvFeedList)
            feedScreenPop?.show()
        }
        feedScreenPop?.listener = {
            search = it
            offset = 0
            getData()
        }
    }

    fun getData() {
        var map: Map<String, String>? = null;
        if (search == 1) {
            map = mapOf("limit" to 10.toString(), "offset" to offset.toString())
        } else {//等于0的筛选条件未读
            map = mapOf("search" to search.toString(), "limit" to 10.toString(), "offset" to offset.toString())

        }
        mViewModel.loadData(map)

    }

    override fun onRefresh() {
        if (NetUtils.isNetworkConnected()) {
            offset = 0
            getData()
        } else {
            inflate.swipeFeedList.setRefreshing(false)
            toast(getString(R.string.net_error))

        }

    }

    override fun onLoadMore() {//加载更多
        inflate.rcvFeedList.postDelayed(Runnable {
            offset += 10
            getData()
        }, 1000)
    }

    /**
     *课程数据
     */
    fun setData(beans: ArrayList<FeedUserBean>) {
        if (offset == 0) {
            if (beans.size < 10) {
                mFeedListAdapter?.setNewInstance(beans)
                mFeedListAdapter?.loadMoreModule?.isEnableLoadMore = false
                mFeedListAdapter?.loadMoreModule?.loadMoreComplete()
            } else {
                mFeedListAdapter?.setNewInstance(beans)
                mFeedListAdapter?.loadMoreModule?.isEnableLoadMore = true
                mFeedListAdapter?.loadMoreModule?.loadMoreComplete()

            }
        } else {
            if (beans.size < 10) {
                mFeedListAdapter?.addData(beans)
                mFeedListAdapter?.loadMoreModule?.isEnableLoadMore = false
                mFeedListAdapter?.loadMoreModule?.loadMoreEnd()

            } else {
                mFeedListAdapter?.addData(beans)
                mFeedListAdapter?.loadMoreModule?.isEnableLoadMore = true
                mFeedListAdapter?.loadMoreModule?.loadMoreComplete()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //ToDO设置回来怎么刷新数据？？？？？
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CODE_EMPTY_SUB) {
//                offset = 0
//                getData()
//            } else if (requestCode == REQUEST_CODE_SUB) {
//                offset = 0
//                getData()
//            } else if (requestCode == REQUEST_CODE_ITEM) {/
            //TODO需要吗，整体刷新不行吗
//                if (data != null) {
//                    val time = data.getLongExtra(ConstantUtils.INTENT_FEED_REPLY_TIME, 0)
//                    if (time > 0) {
//                        feedUserBean.setFeedback_time(time)
//                        adapter.notifyItemChanged(position, feedUserBean)
//                    }
//                }
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        offset=0
        getData()
    }
//    private val REQUEST_CODE_EMPTY_SUB = 100
//    private val REQUEST_CODE_SUB = 101
//    private val REQUEST_CODE_ITEM = 102
}