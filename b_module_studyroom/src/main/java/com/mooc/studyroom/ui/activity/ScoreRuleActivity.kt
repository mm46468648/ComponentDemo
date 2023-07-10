package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.net.ApiService
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogPageConstants
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.common.webview.WebviewWrapper
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyroom.R
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.databinding.StudyroomActivityScoreruleBinding
import io.reactivex.disposables.Disposable

/**
 * 积分规则页面
 * 需要从接口中获取到html的内容，再渲染
 */
@Route(path = Paths.PAGE_SCORE_RULE)
class ScoreRuleActivity : BaseActivity() {

    val webviewWrapper by lazy { WebviewWrapper(this) }

    lateinit var subscribe: Disposable

    lateinit var inflater: StudyroomActivityScoreruleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyroomActivityScoreruleBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        //页面载入打点
//        LogUtil.addLoadLog(LogPageConstants.PID_INTEGRALRULE)
        inflater.flRoot.addView(webviewWrapper.getWebView(), 0)

        //设置返回键
        inflater.commonTitleLayout.setOnLeftClickListener { finish() }

        //监听加载进度
        webviewWrapper.onLoadProgressChange = { newProgress ->
            inflater.progressBar.progress = newProgress
            if (newProgress >= 99) {
                inflater.progressBar.visibility = View.GONE
            } else {
                if (inflater.progressBar.getVisibility() == View.GONE) {
                    inflater.progressBar.visibility = View.VISIBLE
                }
            }
        }

        //请求积分规则内容
        subscribe = ApiService.getRetrofit().create(StudyRoomApi::class.java)
                .getScoreRule()
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    loge(it)
                    val resolveContent = resolveContent(it.user_score_rule)
                    webviewWrapper.loadBaseUrl(resolveContent)
                }, {
                    loge(it.toString())
                })
    }

    private fun resolveContent(it: String) :String {

        return  """<html>
                         <head>
                         <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
                         </head>
                         <body style="word-break: break-all; font-size: 15px; line-height: 2; color: #222222;">
                         $it </body>
                         </html>
                        """



    }


    override fun onDestroy() {
        super.onDestroy()

        subscribe.dispose()
        webviewWrapper.recoveryWebview()
    }
}