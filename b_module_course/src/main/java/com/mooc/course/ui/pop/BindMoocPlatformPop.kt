package com.mooc.course.ui.pop

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.View.OnClickListener
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.route.Paths
import com.mooc.course.R
import com.mooc.course.databinding.CoursePopBindMoocBinding
import com.mooc.course.model.HotListBean

class BindMoocPlatformPop(
    context: Context,
    var hotBean: HotListBean?,
    var clickOk: ((checkState: Boolean) -> Unit)
) : CenterPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.course_pop_bind_mooc
    }

    private lateinit var inflater: CoursePopBindMoocBinding

    var currentCheckState = false

    override fun onCreate() {
        super.onCreate()



        inflater = CoursePopBindMoocBinding.bind(popupImplView)

        //初始化状态
        currentCheckState = hotBean == null
        setClickSpan()
        setChecked(currentCheckState)

        //添加点击事件
        inflater.llCheck.setOnClickListener(OnClickListener {
            currentCheckState = !currentCheckState
            setChecked(currentCheckState)
        })

        inflater.btOk.setOnClickListener {
            clickOk.invoke(currentCheckState)
            dismiss()
        }
    }

    fun setClickSpan() {
        val text = "该课程将进入中国大学MOOC，本平台可能无法同步学习数据，如需学习数据同步，请点击\"如何绑定中国大学慕课平台账号\""
        val span = "\"如何绑定中国大学慕课平台账号\""

        val spannableString = SpannableString(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                ARouter.getInstance().build(Paths.PAGE_QUESTION_INFO)
                    .withString("question_content", hotBean?.question_content)
                    .withString("answer_content", hotBean?.answer_content)
                    .navigation()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(
            clickableSpan, text.indexOf(span),
            text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            UnderlineSpan(), text.indexOf(span),
            text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(context.getColorRes(R.color.colorPrimary)),
            text.indexOf(span),
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        inflater.tvTitle.text = spannableString
        inflater.tvTitle.setMovementMethod(LinkMovementMethod.getInstance())
    }

    fun setChecked(check: Boolean) {
        if (check) {
            inflater.ivCheck.setImageResource(R.mipmap.common_selector_iv_cb_green)
        } else {
            inflater.ivCheck.setImageResource(R.mipmap.common_selector_iv_cb_grey)
        }
    }
}