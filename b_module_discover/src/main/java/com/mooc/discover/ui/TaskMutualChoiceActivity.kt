package com.mooc.discover.ui

import android.graphics.Typeface
import android.os.Bundle
import android.util.SparseArray
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.util.forEach
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.*
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.adapter.TaskMutualChoiceViewPagerAdatper
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.databinding.ActivityTaskMutalChoiceBinding
import com.mooc.discover.model.Choice
import com.mooc.discover.model.TaskChoiceBean
import com.mooc.discover.view.MutualTaskTabCustomView
import com.mooc.discover.viewmodel.TaskMutualChoiceViewModel

//import kotlinx.android.synthetic.main.activity_task_mutal_choice.*

/**
 * 互斥类型任务选择页面
 */
@Route(path = Paths.PAGE_TASK_MUTUAL_DETAIL)
class TaskMutualChoiceActivity : BaseActivity() {

    //    val choiceTaskDetail by extraDelegate<Choice>(TaskConstants.INTENT_MUTUAL_TAKS_DETAIL, Choice())
    lateinit var choiceTaskDetail: Choice
    val taskId by extraDelegate(IntentParamsConstants.PARAMS_TASK_ID, "")

    val mViewModle: TaskMutualChoiceViewModel by viewModels<TaskMutualChoiceViewModel>()

    val tvAward: TextView by lazy {
        findViewById(R.id.tvAward)
    }

    private lateinit var inflater: ActivityTaskMutalChoiceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(TaskConstants.INTENT_MUTUAL_TAKS_DETAIL)
        choiceTaskDetail = GsonManager.getInstance().convert(json, Choice::class.java)

        mViewModle.choic = choiceTaskDetail
        inflater = ActivityTaskMutalChoiceBinding.inflate(layoutInflater)
        setContentView(inflater.root)


        loge("taskID:${taskId},     choice:${choiceTaskDetail}")
        inflater.ibBack.setOnClickListener {
            finish()
        }
        val titles = arrayListOf<String>()
        if(choiceTaskDetail.necessary?.isNotEmpty() == true) {
            titles.add(TaskConstants.STR_MUST_TASK)
        }
        choiceTaskDetail.choice?.forEach {
            titles.add(it.title)
        }

        if(titles.isNotEmpty()){
            //adpater
            val discoverPagerAdapter = TaskMutualChoiceViewPagerAdatper(titles, choiceTaskDetail, this)
            inflater.viewPager2.adapter = discoverPagerAdapter
            setTabLayoutTab(titles)
        }

        mViewModle.choiceTaskList.observe(this, {
            updateTabView(it)
            updateAwardSocre(it)
        })

    }

    /**
     * 更新奖励积分
     */
    fun updateAwardSocre(choicList: ArrayList<TaskChoiceBean>) {
        //获取必做的奖励积分
        var success_score = choiceTaskDetail.necessary?.sumOf {
            it.success_score
        } ?: 0

        //获取必做的失败扣除积分
        var fail_score = choiceTaskDetail.necessary?.sumOf {
            it.fail_score
        } ?: 0


        //获取选做的积分
        choicList.forEach {
            if (it.choice_id.isNotEmpty()) {
                //找到选择的任务tab
                choiceTaskDetail.choice?.forEach { choiceTask ->
                    if (choiceTask.id == it.tab_id) {
                        //找到选择的组合任务
                        val find = choiceTask.choice_list?.find { taskDetailsBean ->
                            taskDetailsBean.id == it.choice_id
                        }

                        //该组合任务下的奖励积分
                        val ss = find?.children_list?.sumOf { it.success_score } ?: 0

                        success_score += ss

                        //该组合任务下的失败扣除积分
                        val fs = find?.children_list?.sumOf { it.fail_score } ?: 0

                        fail_score += fs
                    }
                }
            }
        }

        val tipStr = "预计获得积分奖励 ${success_score} (失败${-fail_score})"

        val spannableString = spannableString {
            str = tipStr
            colorSpan {
                color = getColorRes(R.color.colorPrimary)
                start = tipStr.indexOf(success_score.toString())
                end = tipStr.indexOf(success_score.toString()) + success_score.toString().length
            }

            absoluteSpan {
                size = 20.dp2px()
                start = tipStr.indexOf(success_score.toString())
                end = tipStr.indexOf(success_score.toString()) + success_score.toString().length
            }
            colorSpan {
                color = getColorRes(R.color.color_2)
                start = tipStr.indexOf(success_score.toString()) + success_score.toString().length
                end = tipStr.length
            }

            styleSpan {
                styleSpan = Typeface.BOLD
                start = tipStr.indexOf(success_score.toString())
                end = tipStr.indexOf(success_score.toString()) + success_score.toString().length
            }
        }

        this.tvAward.text = spannableString

        val any = choicList.any {
            it.choice_id.isEmpty()
        }

        inflater.tvConfirm.isEnabled = !any
        val tvConfirmAlpha = if (any) 0.4f else 1f
        inflater.tvConfirm.alpha = tvConfirmAlpha
        inflater.tvConfirm.setOnClickListener {
            val filterNot = choicList.filterNot {
                it.tab_id == TaskConstants.STR_MUST_TASK
            }
            loge(filterNot)
            mViewModle.postTaskToReceive(taskId, filterNot as ArrayList<TaskChoiceBean>)
                .observe(this, {
                    if (it != null && it.isSuccess) {
                        finish()
                    }
                })
        }
    }

    /**
     * 更新TabView的显示
     */
    fun updateTabView(choicList: ArrayList<TaskChoiceBean>) {
        loge("updateTabView ${choicList}")
        choicList.forEach {
            //获取选中的tabId
            if (it.choice_id.isNotEmpty()) {
                val find = choiceTaskDetail.choice?.find { choiceTask ->
                    choiceTask.id == it.tab_id
                }

                //找到该tabView
                tabViewArray.forEach { key, value ->

                    //必做任务直接勾选
                    if (value.str == find?.title ?: "" || value.str == TaskConstants.STR_MUST_TASK) {
                        value.updateSelect()
                    }
                }
            }

        }
    }

    val tabViewArray = SparseArray<MutualTaskTabCustomView>()

    fun setTabLayoutTab(str: ArrayList<String>) {
        inflater.tabLayout.removeAllTabs()
        tabViewArray.clear()
        for (i in 0 until str.size) {
            inflater.tabLayout.addTab(inflater.tabLayout.newTab())
        }

        for (i in 0 until str.size) {
            val makeTabView = makeTabView(str[i])
            tabViewArray.put(i, makeTabView)
            inflater.tabLayout.getTabAt(i)?.setCustomView(makeTabView)
        }

        inflater.tabLayout.getTabAt(0)?.select()

        //adapter建立关联
//        vpDiscover.addOnPageChangeListener(TabLayoutOnPageChangeListener(mTabLayout))
        inflater.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                inflater.tabLayout.selectTab(inflater.tabLayout.getTabAt(position), true)
            }
        })

        inflater.tabLayout.addOnTabSelectedListener(
            com.mooc.resource.listener.ViewPager2OnTabSelectedListener(
                inflater.viewPager2
            )
        )
    }


    /**
     * 设置tablayout tab 自定义样式
     * @param position
     * @return
     */
    private fun makeTabView(str: String): MutualTaskTabCustomView {
        val tabView = MutualTaskTabCustomView(this)
        tabView.setTitle(str)
        return tabView
    }
}