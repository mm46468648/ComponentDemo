package com.mooc.my.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.gone
import com.mooc.common.ktextends.toast
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.my.R
import com.mooc.my.adapter.QuestionMoreAdapter
import com.mooc.my.adapter.QuestionsListAdapter
import com.mooc.my.databinding.ActivityQuestionInfoBinding
import com.mooc.my.databinding.ActivityQuestionListBinding
import com.mooc.my.model.QuestionListBean
import com.mooc.my.model.QuestionMoreBean
import com.mooc.my.viewmodel.QuestionViewModel
//import kotlinx.android.synthetic.main.activity_question_list.*

/**
 *常见问题页面
 * @author limeng
 * @date 2020/10/12
 */
@Route(path = Paths.PAGE_QUESTION_LIST)
class QuestionListActivity : BaseActivity(), TextWatcher {
    var mQuestionsListAdapter: QuestionsListAdapter? = null
    var mQuestionMoreAdapter: QuestionMoreAdapter? = null
    var datas = ArrayList<QuestionListBean.HotListBean>()
    var hasNormal = false

    private lateinit var inflate : ActivityQuestionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = ActivityQuestionListBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()
        intData()
        initListener()
        getDataFromNet()
    }

    val mViewModel: QuestionViewModel by lazy {
        ViewModelProviders.of(this)[QuestionViewModel::class.java]
    }


    private fun intData() {
    }

    private fun initView() {
        mQuestionsListAdapter = QuestionsListAdapter(null)
        mQuestionMoreAdapter = QuestionMoreAdapter(null)
        inflate.rcyQuestions.layoutManager = LinearLayoutManager(this)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        val key: String = charSequence.toString()

        if (key.isEmpty()) {
            getDataFromNet()
            inflate.ivSearchRight.gone(true)
        } else {
            getSearchData(key, "")
            inflate.ivSearchRight.visiable(true)
        }
    }

    private fun getSearchData(keyWord: String, question_type: String) {
        inflate.rcyQuestions.adapter = mQuestionMoreAdapter
        val map = mapOf("search" to keyWord, "question_type" to question_type)
        mViewModel.loadQuestionMoreData(map)

    }

    private fun getDataFromNet() {
        inflate.rcyQuestions.adapter = mQuestionsListAdapter
        mViewModel.loadQuestionsListData()

    }

    override fun afterTextChanged(p0: Editable?) {

    }

    private fun initListener() {
        inflate.commonTitle.setOnRightTextClickListener(View.OnClickListener {
//            ARouter.getInstance().build(Paths.PAGE_FEED_LIST).navigation()
            //跳转到反馈详情页面 (目前是一个h5页面)
            ARouter.getInstance().build(Paths.PAGE_WEB)
                .withString(IntentParamsConstants.WEB_PARAMS_TITLE, "文章")
                .withString(IntentParamsConstants.WEB_PARAMS_URL, UrlConstants.FEEDBACK_URL)
                .navigation()
        })
        inflate.commonTitle.setOnLeftClickListener { finish() }
        inflate.ivSearchRight.setOnClickListener {
            inflate.editSearch.setText("")
        }
        inflate.editSearch.addTextChangedListener(this)
        mViewModel.questionListBean.observe(this, Observer {
            if (it == null) {
                toast("没有数据")
                return@Observer
            }
            handleData(it)
//            setData(it)
        })
        mViewModel.questionMoreBean.observe(this, Observer {
            val listBean=ArrayList<QuestionMoreBean.ResultsBean>()
            if (it == null) {
                toast("没有数据")
                return@Observer
            }

            if (it.results != null&&it.results.size>0) {
                for (bean in it.results) {
                    bean.itemType = 1
                }
                listBean.addAll(it.results)
            }


            val resultsBean: QuestionMoreBean.ResultsBean = QuestionMoreBean.ResultsBean(2)
            listBean.add(resultsBean)
            mQuestionMoreAdapter?.data?.clear()
            mQuestionMoreAdapter?.setNewInstance(listBean)
        })
        //设置更多点击事件
        mQuestionsListAdapter?.addChildClickViewIds(R.id.more)
        mQuestionsListAdapter?.addChildClickViewIds(R.id.to_qr)
        mQuestionsListAdapter?.addChildClickViewIds(R.id.container)
        mQuestionsListAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val bean = mQuestionsListAdapter?.data?.get(position)
            if (view.id == R.id.more) {//更多
                when (bean?.itemType) {
                    QuestionsListAdapter.TOP -> {
                        ARouter.getInstance().build(Paths.PAGE_QUESTION_MORE)
                                .withString("question_type", bean.question_type)
                                .withBoolean("is_hot", true).navigation()

                    }

                    QuestionsListAdapter.MID -> {
                        ARouter.getInstance().build(Paths.PAGE_QUESTION_MORE)
                                .withString("question_type", bean.question_type)
                                .withString("title", bean.question_content).navigation()

                    }

                }
            } else if (view.id == R.id.container) {//问题详情
                if (bean?.itemType == QuestionsListAdapter.BOTTOM) {
                    ARouter.getInstance().build(Paths.PAGE_QUESTION_INFO)
                            .withString("question_content", bean.question_content)
                            .withString("answer_content", bean.answer_content)
                            .navigation()

                }
            } else if (view.id == R.id.to_qr) {//没找到问题的点击事件
                if (bean?.itemType == QuestionsListAdapter.EMPTY) {
                    ARouter.getInstance().build(Paths.PAGE_CUSTOMER_SERVICE).navigation()
                }
            }
        }


        //添加更多和没找到的点击事件
        mQuestionMoreAdapter?.addChildClickViewIds(R.id.container,R.id.to_qr)
        mQuestionMoreAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val bean = mQuestionMoreAdapter?.data?.get(position)
            when (bean?.itemType) {
                QuestionMoreAdapter.NORMAL -> {
                    if (view.id == R.id.container) {
                        ARouter.getInstance().build(Paths.PAGE_QUESTION_INFO)
                                .withString("answer_content", bean.answer_content)
                                .navigation()
                    }
                }
                QuestionMoreAdapter.XIAO_MENG -> {
                    if (view.id == R.id.to_qr) {
                        ARouter.getInstance().build(Paths.PAGE_CUSTOMER_SERVICE).navigation()
                    }
                }

            }
        }
    }

    /**
     * 设置默认list数据
     */
    private fun handleData(beans: List<QuestionListBean>) {
        datas.clear()
        hasNormal = false
        for (i in beans.indices) {
            val bean: QuestionListBean = beans[i]
            //设置默认问题和title
            if (bean.hot_list != null && bean.hot_list.size != 0) {
                val hotListBean = QuestionListBean.HotListBean(QuestionsListAdapter.TOP)
                hotListBean.question_content = "热门问题"
                datas.add(hotListBean)
                datas.addAll(setDataType(bean.hot_list, QuestionsListAdapter.BOTTOM))
            }
        }
        //设置常见问题和title
        for (i in beans.indices) {
            val bean: QuestionListBean = beans[i]
            if (bean.not_hot_list != null && bean.not_hot_list.size != 0) {
                if (!hasNormal) {
                    val hotListBean = QuestionListBean.HotListBean(QuestionsListAdapter.TOP)
                    hotListBean.question_content = "常见问题"
                    datas.add(hotListBean)
                    hasNormal = true
                }
                val hotListBean1 = QuestionListBean.HotListBean(QuestionsListAdapter.MID)
                hotListBean1.question_content = bean.question_name
                hotListBean1.question_type = bean.hot_list?.get(0)?.question_type
                datas.add(hotListBean1)
                datas.addAll(setDataType(bean.not_hot_list, QuestionsListAdapter.BOTTOM))
            }
        }
        val empty = QuestionListBean.HotListBean(QuestionsListAdapter.EMPTY)
        datas.add(empty)
        mQuestionsListAdapter?.setNewInstance(datas)
    }

    /**
     * 修改设置列表数据type
     */
    private fun setDataType(list: List<QuestionListBean.HotListBean>, type: Int): List<QuestionListBean.HotListBean> {
        for (bean in list) {
            bean.itemType = type
        }
        return list
    }
}