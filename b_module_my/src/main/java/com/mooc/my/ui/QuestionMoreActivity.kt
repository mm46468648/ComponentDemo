package com.mooc.my.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.my.R
import com.mooc.my.adapter.QuestionMoreAdapter
import com.mooc.my.viewmodel.QuestionViewModel
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.databinding.ActivityQuestionMoreBinding
//import kotlinx.android.synthetic.main.activity_question_list.*


/**
 *热门问题 更多列表页面
 * @author limeng
 * @date 2020/10/14
 */
@Route(path = Paths.PAGE_QUESTION_MORE)
class QuestionMoreActivity : BaseActivity() {
     var question_type: String?=null
    var mQuestionMoreAdapter: QuestionMoreAdapter? = null
    var title: String? = null
    var is_hot = false

    private lateinit var inflate : ActivityQuestionMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate = ActivityQuestionMoreBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()
        initData()
        initListener()

    }
    val mViewModel: QuestionViewModel by lazy {
        ViewModelProviders.of(this)[QuestionViewModel::class.java]
    }

    private fun initData() {
        title = intent.getStringExtra("title")
        inflate.commonTitle.middle_text=  title?:"热门问题"
        question_type = intent.getStringExtra("question_type")
        is_hot = intent.getBooleanExtra("is_hot", false)
        getSearchData()

    }



    private fun getSearchData() {
        var hot = ""
        if (is_hot) {
            hot = "1"
            question_type = ""
        } else {
            hot = "0"
        }
        if (question_type.isNullOrEmpty()) {
            var map = mapOf( "is_hot" to hot)
            mViewModel.loadQuestionMoreData(map)

        }else{
            var map = mapOf( "question_type" to question_type!!,"is_hot" to hot)
            mViewModel.loadQuestionMoreData(map)

        }


    }
    private fun initView() {
        mQuestionMoreAdapter = QuestionMoreAdapter(null)
        inflate.rcyQuestions.layoutManager = LinearLayoutManager(this)
        inflate.rcyQuestions.adapter = mQuestionMoreAdapter
    }

    private fun initListener() {
        inflate.commonTitle.setOnLeftClickListener { finish() }
        //添加更多和没找到的点击事件
        mQuestionMoreAdapter?.addChildClickViewIds(R.id.container,R.id.to_qr)
        mQuestionMoreAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val bean = mQuestionMoreAdapter?.data?.get(position)
            when (bean?.itemType) {
                QuestionMoreAdapter.NORMAL -> {
                    if (view.id == R.id.container) {//问题详情

                        ARouter.getInstance().build(Paths.PAGE_QUESTION_INFO)
                                .withString("question_content", bean.question_content)
                                .withString("answer_content", bean.answer_content)
                                .navigation()
                    }
                }
                QuestionMoreAdapter.XIAO_MENG -> {//没有想要的？
                    if (view.id == R.id.to_qr) {
                        ARouter.getInstance().build(Paths.PAGE_CUSTOMER_SERVICE)
                                .navigation()
                    }
                }

            }
        }
        mViewModel.questionMoreBean.observe(this, Observer {
            if (it == null) {
                toast("没有数据")
                return@Observer
            }
            for (bean in it.results!!) {
                bean.itemType = 1

            }

//            val resultsBean: QuestionMoreBean.ResultsBean = QuestionMoreBean.ResultsBean(2)
//            it.results.add(resultsBean)
            mQuestionMoreAdapter?.setNewInstance(it.results)
        })
    }
}