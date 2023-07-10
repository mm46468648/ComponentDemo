package com.mooc.home.ui.pop

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.model.privacy.PrivacyPolicyCheckBean
import com.mooc.home.R
import com.mooc.home.databinding.HomePopPrivacyPolicyBecomesBinding

//import kotlinx.android.synthetic.main.home_pop_privacy_policy_becomes.view.*

/**
 * 隐私政策变更
 */

@SuppressLint("ViewConstructor")
class PrivacyPolicyBecomesPop(context: Context, var privacyPolicyCheckBean: PrivacyPolicyCheckBean, var onConfirmCallBack: () -> Unit) : BottomPopupView(context) {

    private lateinit var inflater: HomePopPrivacyPolicyBecomesBinding
    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_privacy_policy_becomes
    }

    override fun onCreate() {
        super.onCreate()
        inflater = HomePopPrivacyPolicyBecomesBinding.bind(popupImplView)
        val spannableString = SpannableString(context.resources.getString(R.string.text_platform_policy))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {

                ARouter.getInstance().build(Paths.PAGE_WEB_PRIVACY)
                        .withString(
                                IntentParamsConstants.WEB_PARAMS_TITLE,
                                context.getString(R.string.text_privacy)
                        )
                        .withString(
                                IntentParamsConstants.WEB_PARAMS_URL,
                                UrlConstants.PRIVACY_POLICY_URL
                        )
                        .navigation()


            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, 31, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

//        spannableString.setSpan(UnderlineSpan(), 32, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
                ForegroundColorSpan(context.resources.getColor(R.color.color_0085D0)),
                31,
                37,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {

                ARouter.getInstance().build(Paths.PAGE_WEB)
                        .withString(
                                IntentParamsConstants.WEB_PARAMS_TITLE,
                                context.getString(R.string.text_policy)
                        )
                        .withString(
                                IntentParamsConstants.WEB_PARAMS_URL,
                                UrlConstants.USER_SERVICE_AGREDDMENT_URL
                        )
                        .navigation()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan1, 38, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        spannableString.setSpan(UnderlineSpan(), 39, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(
                ForegroundColorSpan(context.resources.getColor(R.color.color_0085D0)),
                38,
                46,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        inflater.tvMessage.setText(spannableString)
        inflater.tvMessage.setMovementMethod(LinkMovementMethod.getInstance())



        inflater.tvLeft.setOnClickListener {
            dismiss()
            System.exit(0)
        }
        inflater.tvRight.setOnClickListener {
            dismiss()
            onConfirmCallBack.invoke()
        }
    }
}