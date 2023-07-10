package com.mooc.webview.business

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.db.WebDB
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.StatisticsService
import com.mooc.commonbusiness.utils.InjectJsManager
import com.mooc.webview.R
import com.mooc.webview.db.WebDatabase
import com.mooc.webview.reccommend.RecommendArticleAdapter
import com.qmuiteam.qmui.nestedScroll.*
//import kotlinx.android.synthetic.main.webview_activity_webview.*


@Route(path = Paths.PAGE_WEB_RECOMMEND_RESOURCE)
open class RecommendResourceWebViewActivity : BaseResourceWebviewActivity() {


    var hasScroll = false;
    var handler: Handler? = null;

    var progressBar: ProgressBar? = null;

    override fun userX5(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webviewWrapper.onLoadProgressChange = null;

        inflater.flRoot.removeAllViews()

        addWithQMUI()

        addProgress()

    }



    var mCoordinatorLayout: QMUIContinuousNestedScrollLayout? = null;
    var mNestedWebView: View? = null;
    var mRecyclerView: QMUIContinuousNestedBottomRecyclerView? = null;

    var articleAdapter: RecommendArticleAdapter? = null;

    fun addWithQMUI() {
        handler = Handler(Looper.getMainLooper())
        mCoordinatorLayout = QMUIContinuousNestedScrollLayout(this);
        mNestedWebView = webviewWrapper.getView()
        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        val webViewLp = CoordinatorLayout.LayoutParams(
            matchParent, matchParent
        )
        webViewLp.behavior = QMUIContinuousNestedTopAreaBehavior(this)
        mCoordinatorLayout?.setTopAreaView(mNestedWebView, webViewLp)


        mRecyclerView = QMUIContinuousNestedBottomRecyclerView(this)

        articleAdapter = RecommendArticleAdapter(null)
        articleAdapter?.setOnItemClickListener { adapter, view, position ->
            articleAdapter?.data?.get(position)?.let { resource->
                ResourceTurnManager.turnToResourcePage(resource)
                val logService = ARouter.getInstance().navigation(StatisticsService::class.java)
                logService.addClickLog("${LogEventConstants2.T_ARTICLE}#$resourceID#${LogEventConstants2.P_SIM}",resource._resourceId,resource._resourceType.toString(),resource.title,)
            }

        }
        mRecyclerView?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView?.adapter = articleAdapter

        val recyclerViewLp = CoordinatorLayout.LayoutParams(
            matchParent, matchParent
        )
        recyclerViewLp.behavior = QMUIContinuousNestedBottomAreaBehavior()

        mCoordinatorLayout?.setBottomAreaView(mRecyclerView, recyclerViewLp)
        webviewWrapper.loadUrl(loadUrl)

        inflater.flRoot.addView(mCoordinatorLayout)

        webviewWrapper.onLoadProgressChange = {

            if (it == 100 && !hasScroll) {
                Log.i("TAG", "pageFinish")
                InjectJsManager.closeInject = false
                hasScroll = true
                runOnMainDelayed(500) {
                    val webDB = WebDatabase.DATABASE?.getWebDao()?.findDownloadTackById(loadUrl);
                    if (webDB != null) {
                        Log.i("TAG", "scroll Y:" + webDB.position)
                        mNestedWebView?.scrollTo(0, webDB.position)

                    } else {
                        Log.i("TAG", "webDB is null")
                    }
                    requestRecommend()
                }
            }

            progressBar?.progress = it
            if (it >= 99) {
                progressBar?.visibility = View.GONE
                loadPreviewImageAndFilterWxLinkJs()
            } else {
                if (progressBar?.visibility == View.GONE) {
                    progressBar?.visibility = View.VISIBLE
                }
            }


        }


    }

    //请求推荐文章
    private fun requestRecommend() {
        mViewModel.getRecommendArticle(resourceID).observe(this, Observer {
            articleAdapter?.setNewData(it)
        })
    }

    private fun addProgress() {
        progressBar =
            LayoutInflater.from(this).inflate(R.layout.progress_web, null, false) as ProgressBar;
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3.dp2px());
        inflater.flRoot.addView(progressBar, lp)
    }



    override fun finish() {
        super.finish()


        val webDB = WebDB();
        webDB.key = loadUrl
        webDB.position = mNestedWebView?.scrollY ?: 0

        Log.i("TAG", "save Y:" + mNestedWebView?.scrollY)

        WebDatabase.DATABASE?.getWebDao()?.insert(webDB)
    }

    override fun onDestroy() {
        super.onDestroy()

        handler?.removeCallbacksAndMessages(null)
    }

}