package com.mooc.webview.business


import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants.Companion.MY_USER_ID
import com.mooc.commonbusiness.model.studyproject.DynamicUser
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.webview.R
import com.mooc.webview.WebViewWrapper
import com.mooc.webview.databinding.WebviewActivityInitiorBriefBinding

//import kotlinx.android.synthetic.main.webview_activity_initior_brief.*

/**
 *发起人简介
 * @author limeng
 * @date 2021/1/4
 */
@Route(path = Paths.PAGE_WEB_INITIORBRIEF)
class InitiorBriefActivity : BaseActivity() {
    val webviewWrapper by lazy { WebViewWrapper(this) }
    val INTENT_STUDY_PLAN_RULE = "intent_study_plan_rule"
    val INTENT_STUDY_PLAN_LIST = "intent_study_plan_list"
    private var list: ArrayList<DynamicUser>? = null
    private var mRuleDetail: String = ""
    private lateinit var inflater: WebviewActivityInitiorBriefBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = WebviewActivityInitiorBriefBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initView()
        initData()
        initListener()

    }



    private fun initView() {
        //将包装类中webview添加到布局
        val mWebView = webviewWrapper.getView()
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mWebView.layoutParams = layoutParams
        inflater.flRoot.addView(mWebView, 0)

    }

    private fun getIntentData() {
        val intent = intent
        if (intent != null) {
            mRuleDetail = intent.getStringExtra(INTENT_STUDY_PLAN_RULE)?:""
            list = intent.getParcelableArrayListExtra<DynamicUser>(INTENT_STUDY_PLAN_LIST)
        }
    }

    private fun initListener() {
        //设置返回健
        inflater.commonTitleLayout.setOnLeftClickListener {
            onPressBack()
        }
        adapter?.setOnItemClickListener { adapter, view, position ->
            val bean = adapter.data.get(position) as DynamicUser
            ARouter.getInstance()
                    .build(Paths.PAGE_USER_INFO)
                    .withString(MY_USER_ID, bean?.id?.toString())
                    .navigation()
        }
    }

    var adapter: StudyPlanUserAdapter? = null
    private fun initData() {
        getIntentData()
//        val layoutManager = LinearLayoutManager(this)
        inflater.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

//        recycler_view.setLayoutManager(layoutManager)
        adapter = StudyPlanUserAdapter(list)
        inflater.recyclerView.adapter = adapter

        if (!TextUtils.isEmpty(mRuleDetail)) {
            mRuleDetail = mRuleDetail.replace("<img", "<img width=\"100%\"")
            val html = HtmlUtils.getReplaceHtml(mRuleDetail)
            webviewWrapper.loadDataWithBaseURL(html)
        }

    }

    /**
     * 点击返回事件
     */
    fun onPressBack() {
        finish()
    }

    override fun onDestroy() {
        try {

            webviewWrapper.release()
        } catch (e: Exception) {
            loge("X5WebViewActivity", e.toString())
        }
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onPressBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    inner class StudyPlanUserAdapter(data:ArrayList<DynamicUser>?)
        : BaseQuickAdapter<DynamicUser, BaseViewHolder>(R.layout.webview_item_plan_user_study,data) {
        override fun convert(holder: BaseViewHolder, item: DynamicUser) {
            Glide.with(context)
                .load(item.avatar)
                .error(R.mipmap.common_ic_user_head_default)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(25.dp2px())))
                .into(holder.getView(R.id.iv_icon_user))
            holder.setText(R.id.tv_name_plan_user,item.name)
        }
    }
}