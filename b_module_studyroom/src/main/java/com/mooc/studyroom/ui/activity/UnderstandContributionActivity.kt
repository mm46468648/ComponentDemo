package com.mooc.studyroom.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.webview.WebviewWrapper
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.StudyroomActivityUnderstandContributionBinding
import com.mooc.studyroom.viewmodel.UnderstandContributionViewModel
//import kotlinx.android.synthetic.main.studyroom_activity_understand_contribution.*

@Route(path = Paths.PAGE_UNDERSTAND_CONTRIBUTION)
class UnderstandContributionActivity : BaseActivity() {

    companion object{
        const val PARAMS_TYPE_RULE = "params_type_rule"
        const val TYPE_RULE_EXPLAIN = "1"    //举报说明
        const val TYPE_RULE_UNDERSTAND = "3"  //贡献规则
    }
    /*
    1 举报说明 3 贡献规则
    * */
    val type by extraDelegate(IntentParamsConstants.HOME_UNDERSTAND_TYPE,"3")
//    val mViewModel: UnderstandContributionViewModel by lazy {
//        ViewModelProviders.of(this)[UnderstandContributionViewModel::class.java]
//    }
    val mViewModel: UnderstandContributionViewModel by viewModels()

    val webviewWrapper by lazy { WebviewWrapper(this) }

    private lateinit var inflater: StudyroomActivityUnderstandContributionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = StudyroomActivityUnderstandContributionBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        //标题
        val titleStr = if(type == TYPE_RULE_UNDERSTAND) "了解贡献榜" else "举报说明"
        inflater.commonTitle.middle_text = titleStr

        //设置返回健
        inflater.commonTitle.setOnLeftClickListener {
            finish()
        }

        //将包装类中webview添加到布局
        val mWebView = webviewWrapper.getWebView()
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mWebView.layoutParams = layoutParams
        inflater.flRoot.addView(mWebView, 0)

        //内容
        mViewModel.commondata.observe(this, Observer {
            val replaceHtml = HtmlUtils.getFormatHtml(it.words)
            webviewWrapper.loadBaseUrl(replaceHtml)
        })
        mViewModel.getData(type)
    }


}