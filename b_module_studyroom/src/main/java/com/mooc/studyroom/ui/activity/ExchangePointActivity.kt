package com.mooc.studyroom.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.dialog.MoreButtonDialog
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.studyroom.Constans
import com.mooc.studyroom.IntegralApi
import com.mooc.studyroom.R
import com.mooc.studyroom.databinding.ActivityExchangePointBinding
import com.mooc.studyroom.model.IntegralListBean
import com.mooc.studyroom.model.IntegralRecordListBean
import com.mooc.webview.WebViewWrapper
import com.mooc.webview.WebviewApplication
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


/**
 *积分兑换列表
 * @author limeng
 * @date 2020/12/24
 */
@Route(path = Paths.PAGE_EXCHANGE_POINT)
class ExchangePointActivity:BaseActivity() {


    val webviewWrapper by lazy { WebViewWrapper(this, userX5()) }

    open fun userX5(): Boolean = WebviewApplication.x5InitFinish

    val isRecord by extraDelegate(Constans.INTENT_EXCHANGE_PAGE, false)
    val totalScore by extraDelegate(Constans.INTENT_TOTAL_SCORE, 0)
    val data by extraDelegate(Constans.INTENT_EXCHANGE_POINT, IntegralListBean())
    val recordData by extraDelegate(Constans.INTENT_EXCHANGE_POINT, IntegralRecordListBean())

    private lateinit var inflater: ActivityExchangePointBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = ActivityExchangePointBinding.inflate(layoutInflater)
        setContentView(inflater.root)

        initView()

        initData()

        initWeb();

        initListener();

    }

    private fun initListener() {
        inflater.tvPointExchange.setOnClickListener(){
            exchangePointDialog()
        };
    }

    private fun initWeb() {

        val mWebView = webviewWrapper.getView()
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mWebView.layoutParams = layoutParams
        inflater.webExchange.addView(mWebView, 0)


    }

    private fun initView() {
        inflater.commonTitle.setOnLeftClickListener { finish() }
    }


    private fun exchangePointDialog() {
        val dialog = MoreButtonDialog(
            this@ExchangePointActivity,
            R.style.DefaultDialogStyle,
            object : MoreButtonDialog.InfoCallback {
                override fun onLeft() {}
                override fun onRight() {
                    if (data.prize_score <= totalScore) {
                        exchangePoint()
                    }
                }

                override fun show() {}
            })
        val message: String
        message = if (data.prize_score > totalScore) {
            resources.getString(R.string.integral_no_score)
        } else {
            java.lang.String.format(
                resources.getString(R.string.integral_score),
                inflater.commonTitle.middle_text.toString(),
                data.prize_score
            )
        }
        dialog.setDialogTitle(message)
        dialog.setStrRight(getString(R.string.text_ok))
        dialog.setStrLeft(getString(R.string.text_cancel))
        dialog.show()
    }

    @SuppressLint("CheckResult")
    private fun exchangePoint() {
        val requestData = JSONObject()
        requestData.put("score_prize", java.lang.String.valueOf(data.id))
        ApiService.getRetrofit().create(IntegralApi::class.java)
            .exchangePoint(RequestBodyUtil.fromJson(requestData))
            .compose(RxUtils.applySchedulers())
            .subscribe({
                when (it.getCode()) {
                    0 -> Toast.makeText(this@ExchangePointActivity,it.message,Toast.LENGTH_SHORT).show();
                    1 -> Toast.makeText(this@ExchangePointActivity,it.message,Toast.LENGTH_SHORT).show();
                    2 ->Toast.makeText(this@ExchangePointActivity,it.message,Toast.LENGTH_SHORT).show();
                    3 -> {
                        Toast.makeText(this@ExchangePointActivity,it.message,Toast.LENGTH_SHORT).show();
                        ARouter.getInstance().build(Paths.PAGE_LOGIN).navigation();
                    }
                    4 -> Toast.makeText(this@ExchangePointActivity,it.message,Toast.LENGTH_SHORT).show();
                    5 -> {
                        val msg = java.lang.String.format(
                            resources.getString(R.string.integral_exchange_success),
                            inflater.commonTitle.middle_text,
                            data.prize_score
                        )
                        Toast.makeText(this@ExchangePointActivity,it.message,Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            });
    }

    private fun initData(){
        var detail: String
        if (isRecord) {
            val scorePrize: IntegralRecordListBean.ScorePrize = recordData.getScore_prize()
            detail = scorePrize.getPrize_content()
            inflater.tvPoint.setText(java.lang.String.valueOf(scorePrize.getPrize_score()))
            inflater.tvPointTitle.setText(scorePrize.getPrize_title())
            inflater.tvPointTitle.setText(scorePrize.getPrize_title())
            inflater.tvExchangeTime.setText(timeToSecond(recordData.getCreated_time()))
            if (recordData.getPrize_status() === 0) {
                inflater.tvPointExchange.setText(resources.getString(R.string.integral_record_audit_tip))
                inflater.tvPointExchange.setGravity(Gravity.TOP or Gravity.RIGHT)
                inflater.tvPointExchange.setTextColor(resources.getColor(R.color.color_E5472D))
                inflater.tvPoint.setTextColor(resources.getColor(R.color.color_1982FF))
                inflater.tvStScore.setTextColor(resources.getColor(R.color.color_1982FF))
            } else {
                inflater.tvPointExchange.setText(resources.getString(R.string.integral_record_exchanged_tip))
                inflater.tvPointExchange.setGravity(Gravity.TOP or Gravity.RIGHT)
                inflater.tvPointExchange.setTextColor(resources.getColor(R.color.color_9))
                inflater.tvPoint.setTextColor(resources.getColor(R.color.color_9))
                inflater.tvStScore.setTextColor(resources.getColor(R.color.color_9))
            }
            inflater.tvPointExchange.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_integral_white))
            inflater.tvPointExchange.setEnabled(false)
        } else {

            detail = data.prize_content
            inflater.tvPoint.setText(java.lang.String.valueOf(data.prize_score))
            inflater.tvPointTitle.setText(data.prize_title)
            inflater.commonTitle.middle_text=data.prize_title
            inflater.tvExchangeTime.setText(
                java.lang.String.format(
                    resources.getString(R.string.integral_exchange_time),
                    timeToString(data.prize_start_time),
                    timeToString(data.prize_end_time)
                )
            )
            if (data.is_expire == 1) {
                inflater.tvPointExchange.setEnabled(true)
                inflater.tvPointExchange.setText(resources.getString(R.string.point_exchange))
                inflater.tvPointExchange.setTextColor(resources.getColor(R.color.color_white))
                inflater.tvPoint.setTextColor(resources.getColor(R.color.color_1982FF))
                inflater.tvStScore.setTextColor(resources.getColor(R.color.color_1982FF))
                inflater.tvPointExchange.setGravity(Gravity.CENTER)
                inflater.tvPointExchange.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_integral_red))
            } else {
                inflater.tvPointExchange.setEnabled(false)
                inflater.tvPointExchange.setText(resources.getString(R.string.integral_no_exchange))
                inflater.tvPointExchange.setTextColor(resources.getColor(R.color.color_9))
                inflater.tvPoint.setTextColor(resources.getColor(R.color.color_9))
                inflater.tvStScore.setTextColor(resources.getColor(R.color.color_9))
                inflater.tvPointExchange.setGravity(Gravity.TOP or Gravity.RIGHT)
                inflater.tvPointExchange.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_integral_white))
            }
        }
        if (!TextUtils.isEmpty(detail)) {
//            mTvRule.setText(Html.fromHtml(mRuleDetail));
            detail = detail.replace("<img", "<img width=\"100%\"")
        }
        val html: String = getReplaceHtml(detail)
        webviewWrapper.loadDataWithBaseURL(html);
    }


    private fun timeToString(time: Long): String? {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return dateFormat.format(Date(time))
    }

    private fun timeToSecond(time: Long): String? {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        return dateFormat.format(Date(time))
    }


    private fun getReplaceHtml(detail: String): String {
        return """<html>
 <head>
 <meta name="viewport" content="width=device-width,initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
 </head>
 <body style="word-break: break-all; margin: 25px 25px; font-size: 14px; line-height: 2; color: #555555;">
$detail </body>
</html>"""
    }

}