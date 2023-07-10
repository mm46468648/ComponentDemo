package com.mooc.my.ui

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.my.databinding.ActivityQuestionInfoBinding

//import kotlinx.android.synthetic.main.activity_question_info.*

/**
 *问题详情页面
 * @author limeng
 * @date 2020/10/12
 */
@Route(path = Paths.PAGE_QUESTION_INFO)
class QuestionInfoActivity : BaseActivity() {

    private lateinit var inflate: ActivityQuestionInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityQuestionInfoBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()
        initListener()
    }


    private var answerContent: String? = null
    private var questionContent: String? = null
    private fun initView() {
        answerContent = intent.getStringExtra("answer_content") ?: ""
        questionContent = intent.getStringExtra("question_content") ?: ""
        inflate.tvQuestionContent.text = HtmlUtils.fromHtml(questionContent!!)
        HtmlUtils.setWebView(inflate.wvAnswerContent, this@QuestionInfoActivity)
        inflate.wvAnswerContent.loadDataWithBaseURL(
            null,
            HtmlUtils.getFormatHtml(answerContent!!),
            "text/html",
            "utf-8",
            null
        )
    }

    private fun initListener() {
        inflate.commonTitle.setOnLeftClickListener { finish() }
        inflate.commonTitle.tv_right?.setOnClickListener {
            //跳转到反馈详情页面 (目前是一个h5页面)
            ARouter.getInstance().build(Paths.PAGE_WEB)
                .withString(IntentParamsConstants.WEB_PARAMS_TITLE, "文章")
                .withString(IntentParamsConstants.WEB_PARAMS_URL, UrlConstants.FEEDBACK_URL)
                .navigation()
        }
    }

}