package com.mooc.discover.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.util.forEach
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.mooc.common.dsl.spannableString
import com.mooc.common.ktextends.*
import com.mooc.common.utils.NetUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.discover.R
import com.mooc.discover.adapter.*
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.databinding.ActivityTaskDetailsNewBinding
import com.mooc.discover.model.Choice
import com.mooc.discover.model.TaskChoiceBean
import com.mooc.discover.model.TaskDetailsBean
import com.mooc.discover.model.TaskFinishResource
import com.mooc.discover.view.MutualTaskTabCustomView
import com.mooc.discover.viewmodel.TaskMutualChoiceViewModel
import com.mooc.discover.viewmodel.TaskViewModel
//import kotlinx.android.synthetic.main.activity_task_details_new.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


/**
 * 新版本任务详情页面
 */
@Route(path = Paths.PAGE_TASK_DETAILS)
class TaskDetailsActivity : BaseActivity() {
    companion object {
        const val REQUEST_CODE_SELECT_STUDY_LIST = 100
        const val STUDYLIST = "study_list"
        const val STUDYLISTNAME = "study_list_name"
        const val STUDYLISTID = "study_list_id"
        const val FROM_COMBINE_TASK = "from_combine_task"    //是否从组合任务进入
        const val FROM_MUTUAL_CHILD_TASK = "from_mutual_child_task"    //是否从互斥任务子任务进入
    }

    val mViewModel: TaskViewModel by viewModels()

    //选择任务ViewModel
    val choiceViewModel: TaskMutualChoiceViewModel by viewModels<TaskMutualChoiceViewModel>()

    //可选任务详情
    var choiceTaskDetail: Choice? = null


    val taskId by ExtraDelegate(IntentParamsConstants.PARAMS_TASK_ID, "")
    val fromCombineTask by ExtraDelegate(FROM_COMBINE_TASK, false)
    val fromMutualChildTask by ExtraDelegate(FROM_MUTUAL_CHILD_TASK, false)
    private lateinit var inflater: ActivityTaskDetailsNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = ActivityTaskDetailsNewBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        //处理接口请求数据
        setData()
        initListener()
    }

    /**
     * 接口请求
     * @param needshowLoading 是否需要显示loading
     */
    private fun getData(needshowLoading: Boolean = true) {
        if (NetUtils.isNetworkConnected()) {
            if (needshowLoading) {
//                emptyView.visibility = View.GONE
//                llLoading.visibility = View.VISIBLE
                setLoadingUi()
            }
            mViewModel.getTaskDetail(taskId)
        } else {
            toast(getString(R.string.net_error))
            setNoNetUi()
        }

    }

    fun initListener() {
        inflater.commonTitle.setOnLeftClickListener { finish() }
        inflater.tvInputStudyListName.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList(
                STUDYLIST,
                mViewModel.taskDetailData.value?.data?.other_data?.folder_list
            )
            ARouter.getInstance()
                .build(Paths.PAGE_SELECT_STUDY_LIST_ACTIVITY)
                .with(bundle)
                .navigation(this, REQUEST_CODE_SELECT_STUDY_LIST)
        }

        inflater.swipeLayout.setOnRefreshListener {
            getData(false)
        }

    }

    /**
     * 处理接口请求的数据展示
     */
    private fun setData() {
        mViewModel.taskDetailData.observe(this, {
//            llLoading.visibility = View.GONE
            if (it.isSuccess) {//成功请求数据
                inflater.emptyView.visibility = View.GONE
                inflater.viewEmpty.visibility = View.GONE
                inflater.nestedScrollView.visibility = View.VISIBLE
                inflater.flBottom.visibility = View.VISIBLE
                bindData(it.data)
            } else {//无数据
                Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                setNoDataUi()
            }
        })
        mViewModel.getError().observe(this, {
            //数据请求失败
//            llLoading.visibility = View.GONE
            inflater.swipeLayout.isRefreshing = false
            setFailUi()
        })
    }

    /**
     * 无数据
     */
    private fun setNoDataUi() {
        //接口请求失败
        inflater.emptyView.visibility = View.VISIBLE
        inflater.viewEmpty.visibility = View.VISIBLE
        inflater.emptyView.setEmptyIcon(R.mipmap.common_ic_empty)
        inflater.emptyView.setTitle("暂无数据")//不显示暂无数据
        inflater.emptyView.setButton("点击刷新") {
            //接口请求失败重新请求接口
            getData(true)
        }
        inflater.nestedScrollView.visibility = View.GONE
        inflater.flBottom.visibility = View.GONE
        inflater.swipeLayout.isRefreshing = false
    }

    /**
     * 失败UI
     */
    private fun setFailUi() {
        //接口请求失败
        inflater.emptyView.visibility = View.VISIBLE
        inflater.viewEmpty.visibility = View.VISIBLE
        inflater.emptyView.setEmptyIcon(R.mipmap.common_ic_empty)
        inflater.emptyView.setTitle("")//不显示暂无数据
        inflater.emptyView.setButton("点击刷新") {
            //接口请求失败重新请求接口
            getData(true)
        }
        inflater.nestedScrollView.visibility = View.GONE
        inflater.flBottom.visibility = View.GONE
        inflater.swipeLayout.isRefreshing = false
    }

    /**
     * 无网UI
     */
    private fun setNoNetUi() {
        //接口请求失败
        inflater.emptyView.visibility = View.VISIBLE
        inflater.viewEmpty.visibility = View.VISIBLE
        inflater.emptyView.setEmptyIcon(R.mipmap.common_ic_no_net)
        inflater.emptyView.setTitle("网络连接失败")
        inflater.emptyView.setButton("") {
        }
        inflater.nestedScrollView.visibility = View.GONE
        inflater.flBottom.visibility = View.GONE
        inflater.swipeLayout.isRefreshing = false
    }

    /**
     * loadingUI
     */
    private fun setLoadingUi() {
        //接口请求失败
        inflater.emptyView.visibility = View.VISIBLE
        inflater.viewEmpty.visibility = View.VISIBLE
        inflater.emptyView.setEmptyIcon(R.drawable.common_gif_loading)
        inflater.emptyView.setTitle(getString(R.string.loading))
        inflater.emptyView.setButton("") {
        }
        inflater.nestedScrollView.visibility = View.GONE
        inflater.flBottom.visibility = View.GONE
        inflater.swipeLayout.isRefreshing = false
    }

    override fun onPause() {
        //3.5.1注释这个方法,是因为这样会影响音频课的播放
        //防止页面返回到后台后音频继续播放
//        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        listener = OnAudioFocusChangeListener { }
//        audioManager?.requestAudioFocus(
//                listener,
//                AudioManager.STREAM_MUSIC,
//                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
//        )
        super.onPause()


        if (inflater.webDesc != null) {
            inflater.webDesc.onPause()
        }
    }


    override fun onResume() {
//        if (audioManager != null) {
//            audioManager?.abandonAudioFocus(listener);
//            audioManager = null;
//        }

        super.onResume()


        getData()
        if (inflater.webDesc != null) {
            inflater.webDesc.onResume()
        }
    }

    var mTaskBean: TaskDetailsBean? = null

    fun bindData(taskBean: TaskDetailsBean) {
        mTaskBean = taskBean
        inflater.swipeLayout.isRefreshing = false
        //展示基本信息
        showNomalData(taskBean)
        //展示任务进度
        showTaskProgress(taskBean)
        //显示任务关联资源
        showFinishResource(taskBean)
        //展示子任务列表
        showChildTaskList(taskBean)
        //展示互斥任务列表
        showMutualTaskList(taskBean)
        //任务规则
        showTaskRule(taskBean)
        //未领取显示任务详情
        showTaskDetail(taskBean)
        //是否显示绑定学习清单
        showBindStudyList(taskBean)
        //底部按钮
        showBtBottom(taskBean)
    }

    var lastIndex = -1
    var currentMutualAdapterIndex = -1 //当前互斥任务列表选中位置,防止返回后的刷新

    /**
     * 展示互斥任务列表
     */
    private fun showMutualTaskList(taskBean: TaskDetailsBean) {
        if (taskBean.choice_task == null) {
            inflater.llmutualTaskContainer.visibility = View.GONE
            inflater.llMutualBottom.visibility = View.GONE
            return
        }

        //已领取不展示
        if (taskBean.status != TaskConstants.TASK_STATUS_CANNOT_GET_UNACCPET
            && taskBean.status != TaskConstants.TASK_STATUS_CANNOT_GET && taskBean.status != TaskConstants.TASK_STATUS_EXPIRED
        ) {
            inflater.llmutualTaskContainer.visibility = View.GONE
            inflater.llMutualBottom.visibility = View.GONE
            return
        }

        choiceTaskDetail = taskBean.choice_task
        //更新tab勾选
        choiceViewModel.choiceTaskList.observe(this, {
            updateTabView(it)
            updateAwardSocre(taskBean, it)
        })

        inflater.llmutualTaskContainer.visibility = View.VISIBLE
        val titles = arrayListOf<String>()
        //必做任务不为空的时候才添加
        if (taskBean.choice_task?.necessary?.isNotEmpty() == true) {
            titles.add(TaskConstants.STR_MUST_TASK)
        }
        taskBean.choice_task?.choice?.forEach {
            titles.add(it.title)
        }

        //如果都没有则隐藏
        if (titles.isEmpty()) {
            inflater.llmutualTaskContainer.visibility = View.GONE
            return
        }
        if (currentMutualAdapterIndex != -1) {
            inflater.vpMutualTask.currentItem = currentMutualAdapterIndex
        } else {
            //adpater
            val discoverPagerAdapter =
                TaskMutualViewPagerAdatper(titles, taskBean.choice_task!!, this)
            inflater.vpMutualTask.adapter = discoverPagerAdapter

            //adapter建立关联
//        vpDiscover.addOnPageChangeListener(TabLayoutOnPageChangeListener(mTabLayout))
            inflater.vpMutualTask.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    loge("onPageSelected lastIndex${lastIndex} adpaterIndex${currentMutualAdapterIndex}")
                    lastIndex = currentMutualAdapterIndex
                    currentMutualAdapterIndex = position
                    inflater.tabLayout.selectTab(inflater.tabLayout.getTabAt(position), true)
                }
            })

            inflater.vpMutualTask.setPageTransformer { page, position ->
                loge("setPageTransformer lastIndex${lastIndex} adpaterIndex${currentMutualAdapterIndex}")
                updatePagerHeightForChild(page, inflater.vpMutualTask)
            }

            setTabLayoutTab(titles)
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
        inflater.tabLayout.addOnTabSelectedListener(
            com.mooc.resource.listener.ViewPager2OnTabSelectedListener(
                inflater.vpMutualTask
            )
        )
    }

    //计算fragment的高度并设置给viewPager
    private fun updatePagerHeightForChild(view: View?, pager: ViewPager2) {
        view?.post {
            val wMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(wMeasureSpec, hMeasureSpec)
            loge("pageHeight:${pager.layoutParams.height}  viewHeight:${view.measuredHeight}")

            if (pager.layoutParams.height != view.measuredHeight) {
                pager.layoutParams = pager.layoutParams.also { lp ->
                    loge("measureHeight:${view.measuredHeight}")
                    //第一次进入使用WrapContent -2
                    if (lastIndex == -1) {
                        lp.height = -2
                    } else {
                        lp.height = view.measuredHeight
                    }
                }
            }


        }
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


    /**
     * 更新TabView的显示
     */
    fun updateTabView(choicList: ArrayList<TaskChoiceBean>) {
        loge("updateTabView ${choicList}")
        choicList.forEach {
            //获取选中的tabId
            if (it.choice_id.isNotEmpty()) {
                val find = choiceTaskDetail?.choice?.find { choiceTask ->
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

    /**
     * 更新奖励积分
     */
    fun updateAwardSocre(taskBean: TaskDetailsBean, choicList: ArrayList<TaskChoiceBean>) {
        //获取必做的奖励积分
        var success_score = choiceTaskDetail?.necessary?.sumOf {
            it.success_score
        } ?: 0

        //获取必做的失败扣除积分
        var fail_score = choiceTaskDetail?.necessary?.sumOf {
            it.fail_score
        } ?: 0


        //获取选做的积分
        choicList.forEach {
            if (it.choice_id.isNotEmpty()) {
                //找到选择的任务tab
                choiceTaskDetail?.choice?.forEach { choiceTask ->
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

        inflater.tvCombinAward.text = spannableString

        val any = choicList.any {
            it.choice_id.isEmpty()
        }

//        inflater.tvConfirm.isEnabled = !any
        val tvConfirmAlpha = if (any) 0.4f else 1f
        inflater.tvConfirm.alpha = tvConfirmAlpha
        inflater.tvConfirm.setOnClickListener {
            //如果有未选择的tab,提示toast
            if (any) {
                toast("领取任务前需要在每个标签页选择一组任务")
                return@setOnClickListener
            }
            val filterNot = choicList.filterNot {
                it.tab_id == TaskConstants.STR_MUST_TASK
            }
            loge(filterNot)
            choiceViewModel.postTaskToReceive(taskId, filterNot as ArrayList<TaskChoiceBean>)
                .observe(this, {
                    if (it != null && it.isSuccess) {

                        //领取成功,如果详情不为空,收起详情
                        if (!TextUtils.isEmpty(taskBean.content) && inflater.llWebContainer.visibility == View.VISIBLE) {
                            inflater.tvLookDetail.visibility = View.VISIBLE
                            inflater.llWebContainer.visibility = View.GONE
                            inflater.tvLookDetail.setDrawRight(
                                R.mipmap.task_ic_arrow_down,
                                7.dp2px()
                            )
                            expand = false
                        }
                        //刷新当前页面状态
                        getData()
                    }
                })
        }
    }

    private fun showTaskProgress(taskBean: TaskDetailsBean) {
        if (taskBean.status == TaskConstants.TASK_STATUS_EXPIRED || taskBean.status == TaskConstants.TASK_STATUS_CANNOT_GET || taskBean.status == TaskConstants.TASK_STATUS_CANNOT_GET_UNACCPET) {
            //未领取,隐藏进度
            inflater.llProgressContainer.visibility = View.GONE
            inflater.viewUnAcceptSpace.visibility = View.GONE
        } else {
            inflater.llProgressContainer.visibility = View.VISIBLE
            if (inflater.clTaskDes.visibility == View.VISIBLE) {
                inflater.viewUnAcceptSpace.visibility = View.VISIBLE
            } else {
                inflater.viewUnAcceptSpace.visibility = View.GONE
            }

            if (taskBean.status == TaskConstants.TASK_STATUS_UNSTART) {
                //已领取,未开始,展示距离任务开始
                inflater.taskCutdownView.visibility = View.VISIBLE
                inflater.tvTaskTime.visibility = View.VISIBLE

                showStartAwayTime(taskBean)

                inflater.llTotalProgressContainer.visibility = View.GONE
                inflater.tvTaskEndAway2.visibility = View.GONE
                inflater.progressBar.visibility = View.GONE
            } else {
                //进行中,成功或失败
                inflater.taskCutdownView.visibility = View.GONE
                inflater.tvTaskTime.visibility = View.GONE

                inflater.llTotalProgressContainer.visibility = View.VISIBLE
                inflater.tvTaskEndAway2.visibility = View.VISIBLE

                //展示任务进度
                //如果任务类型是普通任务,隐藏总进度tvTotalProgressTip
                if (taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_NOMAL) {
                    inflater.tvTotalProgressTip.visibility = View.GONE
                } else if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN) {
                    //累计任务中的打卡任务也隐藏
                    inflater.tvTotalProgressTip.visibility = View.GONE
                } else if (taskBean.type == TaskConstants.TASK_TYPE_REED_BOOK) {
                    //青年读书会定制任务也隐藏
                    inflater.tvTotalProgressTip.visibility = View.GONE
                } else {
                    inflater.tvTotalProgressTip.visibility = View.VISIBLE
                }


                //普通的打卡任务,需要显示进度条容器
                if (taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_NOMAL && taskBean.type == TaskConstants.TASK_TYPE_CHECKIN) {
                    inflater.llTotalProgressContainer.visibility = View.VISIBLE
                    inflater.progressBar.visibility = View.VISIBLE
                    inflater.tvTaskEndAway2.visibility = View.GONE

                } else if (taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_CONTINUE || taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_SUM) {
                    //或者累计型任务,或者连续性任务,需要显示进度条容器
                    inflater.llTotalProgressContainer.visibility = View.VISIBLE
                    inflater.progressBar.visibility = View.VISIBLE
                    inflater.tvTaskEndAway2.visibility = View.GONE

                } else if (taskBean.type == TaskConstants.TASK_TYPE_REED_BOOK) {
                    //青年读书会定制任务也显示进度条
                    inflater.llTotalProgressContainer.visibility = View.VISIBLE
                    inflater.progressBar.visibility = View.VISIBLE
                    inflater.tvTaskEndAway2.visibility = View.GONE
                } else {
                    inflater.llTotalProgressContainer.visibility = View.GONE
                    inflater.progressBar.visibility = View.GONE
                    inflater.tvTaskEndAway2.visibility = View.VISIBLE

                }

                showTaskTotalProgress(taskBean)
            }
        }


        //成功标识
        if (taskBean.status == TaskConstants.TASK_STATUS_SUCCESS) {
            //组合任务不需要显示,在item的第一个显示
            if (taskBean.calculate_type != TaskConstants.TASK_CACULATE_TYPE_COMBINE) {
                inflater.ivSuccessPrise.visibility = View.VISIBLE
            } else {
                inflater.ivSuccessPrise.visibility = View.GONE
            }
        } else {
            inflater.ivSuccessPrise.visibility = View.GONE
        }

        //成功标识位置
        var margin = 4.dp2px()
        if (taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_NOMAL || taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE) {
            margin = 35.dp2px()
        }
        val marginLayoutParams =
            inflater.ivSuccessPrise.layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParams.topMargin = margin
        inflater.ivSuccessPrise.layoutParams = marginLayoutParams

    }

    /**
     * 展示子任务列表
     */
    fun showChildTaskList(taskBean: TaskDetailsBean) {
        //不是组合任务隐藏 (互斥任务也属于组合任务的一种)
        if (taskBean.calculate_type != TaskConstants.TASK_CACULATE_TYPE_COMBINE) {
            inflater.rvChildResources.visibility = View.GONE
            return
        }

        if (taskBean.children_task == null || taskBean.children_task!!.isEmpty()) {
            inflater.rvChildResources.visibility = View.GONE
            return
        }

        if (taskBean.status == TaskConstants.TASK_STATUS_UNACCPET || taskBean.status == TaskConstants.TASK_STATUS_CANNOT_GET
            || taskBean.status == TaskConstants.TASK_STATUS_EXPIRED || taskBean.status == TaskConstants.TASK_STATUS_CANNOT_GET_UNACCPET
        ) {
            inflater.rvChildResources.visibility = View.GONE
            return
        }


        inflater.rvChildResources.visibility = View.VISIBLE
        inflater.rvChildResources.layoutManager = LinearLayoutManager(this)
        val taskChildAdapter =

            //如果是互斥任务,使用互斥任务子任务适配器
            if (taskBean.type == TaskConstants.TASK_TYPE_COMBINE_DO_MUTUAL) {

                //间距稍微往下调整
                val layoutParams =
                    inflater.rvChildResources.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.topMargin = -10.dp2px()
                inflater.rvChildResources.layoutParams = layoutParams

                val combineMutualTaskChildList = combineMutualTaskChildList(taskBean)
                TaskMutualChildAdapter(
                    combineMutualTaskChildList,
                    taskBean.status == TaskConstants.TASK_STATUS_SUCCESS
                )


            } else { //组合任务子任务适配器
                TaskChildAdapter(
                    taskBean.children_task,
                    taskBean.status == TaskConstants.TASK_STATUS_SUCCESS
                )
            }
        taskChildAdapter.setOnItemClickListener { adapter, view, position ->
//            val id = taskBean.children_task?.get(position)?.id
            val id = (adapter.data.get(position) as TaskDetailsBean).id
            ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS)
                .withString(IntentParamsConstants.PARAMS_TASK_ID, id)
                .withBoolean(FROM_COMBINE_TASK, true)
                .navigation()
        }
        inflater.rvChildResources.adapter = taskChildAdapter
    }

    /**
     * 组装互斥任务子任务
     */
    fun combineMutualTaskChildList(taskBean: TaskDetailsBean): ArrayList<TaskDetailsBean> {
        val arrayList = ArrayList<TaskDetailsBean>();

        taskBean.choice_task?.necessary?.forEach {
            it.groupName = TaskConstants.STR_MUST_TASK
        }
        taskBean.choice_task?.necessary?.let { necessarys ->
            arrayList.addAll(necessarys)
        }

        taskBean.choice_task?.choice?.forEach { choiceTask ->
            choiceTask.choice_list?.forEach { taskDetailsBean ->
                taskDetailsBean.children_list?.forEach { child ->
                    child.groupName = choiceTask.title
                }
                taskDetailsBean.children_list?.let { childrenList ->
                    arrayList.addAll(childrenList)
                }
            }
        }
        return arrayList;
    }

    fun showNomalData(taskBean: TaskDetailsBean) {
        if (fromCombineTask) {
            //如果是从组合任务进入的,不显示任务描述区域
            inflater.clTaskDes.visibility = View.GONE
            inflater.viewUnAcceptSpace.visibility = View.GONE
            return
        }
        inflater.viewUnAcceptSpace.visibility = View.VISIBLE
        inflater.clTaskDes.visibility = View.VISIBLE
        //任务标题
        inflater.tvTaskTitle.setText(taskBean.title)
        //任务领取时间
        var taskGetTime = "${taskBean.start_time} - ${taskBean.end_time}"
        if (TextUtils.isEmpty(taskBean.start_time) && TextUtils.isEmpty(taskBean.end_time)) {
            taskGetTime = "永久开放"
        }

        //任务时间
        var taskTime = "${taskBean.task_start_date} - ${taskBean.task_end_date}"
        if (TextUtils.isEmpty(taskBean.task_start_date) && TextUtils.isEmpty(taskBean.task_end_date)) {
            taskTime = "永久开放"
        }
        inflater.tvGetTime.text = "领取时间: $taskGetTime"
        inflater.tvOpenTime.text = "任务时间: $taskTime"

        if (taskBean.time_mode == 1) {
            inflater.tvGetTime.text = "领取时间: 永久开放"
            inflater.tvOpenTime.text = "任务时间: 永久开放"
        }
        //参与人数
        var s = "${taskBean.join_num}人参与"
        if (taskBean.is_limit_num) { //限制人数
            s = "${taskBean.join_num}/${taskBean.limit_num}人参与"
        }
        inflater.tvPeopleNum.setText(s)


        //奖励和失败都为空
        if (TextUtils.isEmpty(taskBean.score?.success_score) && TextUtils.isEmpty(taskBean.score?.fail_score)) {
            inflater.llAwardContainer.visibility = View.GONE
        } else {
            inflater.llAwardContainer.visibility = View.VISIBLE

            //奖励积分
            if (!TextUtils.isEmpty(taskBean.score?.success_score)) {
                inflater.tvAward.visibility = View.VISIBLE
                inflater.tvAwardScore.visibility = View.VISIBLE
                inflater.tvAwardScore.setText(taskBean.score?.success_score.toString())
            } else {
                inflater.tvAward.visibility = View.GONE
                inflater.tvAwardScore.visibility = View.GONE
            }

            //失败扣除积分
            if (!TextUtils.isEmpty(taskBean.score?.fail_score)) {
                inflater.tvFailScore.visibility = View.VISIBLE
                inflater.tvFailScore.setText("(失败-${taskBean.score?.fail_score})")
            } else {
                inflater.tvFailScore.visibility = View.GONE
            }
        }

        //任务描述图片
        Glide.with(this).load(taskBean.image_url)
            .placeholder(R.mipmap.common_bg_cover_big_default)
            .error(R.mipmap.common_bg_cover_big_default)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(2f.dp2px())))
            .into(inflater.ivTaskDes)
    }

    /**
     * 展示任务详情
     */
    fun showTaskDetail(taskBean: TaskDetailsBean) {
        if (taskBean.status == TaskConstants.TASK_STATUS_EXPIRED || taskBean.status == TaskConstants.TASK_STATUS_CANNOT_GET
            || taskBean.status == TaskConstants.TASK_STATUS_CANNOT_GET_UNACCPET || taskBean.status == TaskConstants.TASK_STATUS_UNSTART
        ) {
            //如果有任务详情富文本直接显示
            if (!TextUtils.isEmpty(taskBean.content) || taskBean.task_rule != null) {
                inflater.tvLookDetail.visibility = View.GONE
                showTaskWebDetail(true)
            }
        } else {
            //如果有任务详情富文本,需点击查看详情后,展开显示
            if (!TextUtils.isEmpty(taskBean.content) || taskBean.task_rule != null) {
                inflater.tvLookDetail.visibility = View.VISIBLE
                inflater.tvLookDetail.setText("查看详情")
                inflater.tvLookDetail.setDrawRight(R.mipmap.task_ic_arrow_down, 7.dp2px())
                inflater.tvLookDetail.setOnClickListener {
                    showTaskWebDetail(!expand)
                    //点击展开详情,向下滚动一点位置
                    if (expand) {
                        inflater.llWebContainer.post {
                            inflater.nestedScrollView.scrollBy(0, 150.dp2px())
                        }
                    }
                }
            }

        }
    }

    private fun showBindStudyList(taskBean: TaskDetailsBean) {

        if (taskBean.status == TaskConstants.TASK_STATUS_UNACCPET || taskBean.status == TaskConstants.TASK_STATUS_CANNOT_GET
            || taskBean.status == TaskConstants.TASK_STATUS_EXPIRED || taskBean.status == TaskConstants.TASK_STATUS_CANNOT_GET_UNACCPET
        ) {
            inflater.llBindStudyList.visibility = View.GONE
            return
        }
        if (!taskBean.is_bind_folder) {
            inflater.llBindStudyList.visibility = View.GONE
            return
        }
        if (taskBean.is_bind_folder) {
            inflater.llBindStudyList.visibility = View.VISIBLE

            //如果已绑定,显示绑定的学习清单名字,透明北京,向右箭头
            if (taskBean.bind_data != null && taskBean.bind_data?.source_name?.isNotEmpty() == true) {
                inflater.tvBindTip.text = "绑定的学习清单"
                inflater.tvInputStudyListName.setBackgroundColor(Color.TRANSPARENT)
                inflater.tvInputStudyListName.setDrawRight(R.mipmap.common_ic_right_arrow_gray)
                inflater.tvInputStudyListName.setTextColor(resources.getColor(R.color.color_2))
                inflater.tvInputStudyListName.text = taskBean.bind_data?.source_name ?: ""
                //点击跳转到该学习清单
                inflater.tvInputStudyListName.setOnClickListener {
                    turnToStudyList(taskBean)
                }
            }
        }
    }

    /**
     * 跳转到学习清单
     */
    fun turnToStudyList(taskBean: TaskDetailsBean) {
        if (taskBean.bind_data == null) return

        //运营推荐的跳转到运营推荐的学习清单中
        if (taskBean.bind_data?.source_data?.is_admin == true) {
            ARouter.getInstance().build(Paths.PAGE_PUBLIC_STUDY_LIST)
                .with(
                    Bundle().put(
                        IntentParamsConstants.STUDYROOM_FOLDER_ID,
                        taskBean.bind_data?.source_data?.show_folder_id
                    )
                        .put(
                            IntentParamsConstants.STUDYROOM_FOLDER_NAME,
                            taskBean.bind_data?.source_name
                        )
                        .put(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK, true)
                        .put(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_TASK_ID, taskBean.id)
                        .put(IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND, true)
                )
                .navigation()
        } else {
            //跳转到自己的学习清单
            ARouter.getInstance().build(Paths.PAGE_STUDYLIST_DETAIL)
                .with(
                    Bundle().put(
                        IntentParamsConstants.STUDYROOM_FOLDER_ID,
                        taskBean.bind_data!!.source_id
                    )
                        .put(
                            IntentParamsConstants.STUDYROOM_FOLDER_NAME,
                            taskBean.bind_data?.source_name
                        )
                )
                .navigation()
        }

    }

    var expand = false //当前是否展开


    /**
     * @param expand 是否展开详情
     */
    fun showTaskWebDetail(expand: Boolean) {
        if (expand) {    //要展开

            //任务规则
            if (!TextUtils.isEmpty(mTaskBean?.task_rule?.base_rule)) {
                val html = HtmlUtils.getHtml(mTaskBean?.task_rule?.base_rule ?: "")
                HtmlUtils.setWebView(inflater.webRule, this)
                inflater.webRule.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }

            //任务描述
            if (!TextUtils.isEmpty(mViewModel.taskDetailData.value?.data?.content)) {
                val html =
                    HtmlUtils.getHtml(mViewModel.taskDetailData.value?.data?.content ?: "")
                HtmlUtils.setWebView(inflater.webDesc, this)
                inflater.webDesc.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                inflater.llTaskDetail.visibility = View.VISIBLE
            } else {
                inflater.llTaskDetail.visibility = View.GONE
            }
            inflater.llWebContainer.visibility = View.VISIBLE
            inflater.tvLookDetail.setText("收起详情")
            inflater.tvLookDetail.setDrawRight(R.mipmap.task_ic_arrow_up, 7.dp2px())


        } else {
            inflater.tvLookDetail.setText("查看详情")
            inflater.llWebContainer.visibility = View.GONE
            inflater.tvLookDetail.setDrawRight(R.mipmap.task_ic_arrow_down, 7.dp2px())
        }
        this.expand = expand
    }

    /**
     * 展示底部按钮样式
     */
    fun showBtBottom(taskBean: TaskDetailsBean) {
        if (fromMutualChildTask) {       //如果是互斥任务的子任务点击进入,不显示底部按钮
            inflater.flBottom.visibility = View.GONE
            return
        }

        //重置状态
        inflater.btBottom.setOnClickListener {}
        inflater.btBottom.setBackgroundResource(R.drawable.shape_radius22_color_primary)
        when (taskBean.status) {
            //任务领取还未开始
            TaskConstants.TASK_STATUS_CANNOT_GET -> {
                inflater.btBottom.setText("领取未开始")
                inflater.btBottom.setBackgroundResource(R.drawable.shape_radius22_color_87caab)
            }
            //任务未领取但是已过期
            TaskConstants.TASK_STATUS_EXPIRED -> {
                inflater.btBottom.setText("任务已过期")
                inflater.btBottom.setBackgroundResource(R.drawable.shape_radius22_color_87caab)
            }
            TaskConstants.TASK_STATUS_CANNOT_GET_UNACCPET -> {
                //未领取
                inflater.btBottom.setText("领取任务")
                inflater.btBottom.setOnClickListener {

                    //互斥任务,新版本在当前页面展示领取详情
                    if (taskBean.type == TaskConstants.TASK_TYPE_COMBINE_DO_MUTUAL) {
                        currentMutualAdapterIndex = -1 //重置索引
                        showChoiceTaskLayout()
                        return@setOnClickListener
                    }
                    inflater.swipeLayout.isRefreshing = true
                    mViewModel.postTaskToReceive(taskBean.id).observe(this, Observer {
                        inflater.swipeLayout.isRefreshing = false
                        if (!it.isSuccess) {
                            toast(it.msg ?: "")
                            return@Observer
                        }else{
                            getData()
                        }
                        //领取成功,如果详情不为空,收起详情
                        if (!TextUtils.isEmpty(taskBean.content) && inflater.llWebContainer.visibility == View.VISIBLE) {
                            inflater.tvLookDetail.visibility = View.VISIBLE
                            inflater.llWebContainer.visibility = View.GONE
                            inflater.tvLookDetail.setDrawRight(
                                R.mipmap.task_ic_arrow_down,
                                7.dp2px()
                            )
                            expand = false
                        }
                    })
                }
            }
            //领取未开始
            TaskConstants.TASK_STATUS_UNSTART -> {
                inflater.btBottom.setText("任务未开始")
                inflater.btBottom.setBackgroundResource(R.drawable.shape_radius22_color_87caab)


                //但是如果需要绑定清单,如果没有绑定学习清单,要让让他绑定学习清单
                if (taskBean.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && taskBean.is_bind_folder) {
                    if (taskBean.bind_data == null || taskBean.bind_data?.source_name?.isEmpty() == true) {
                        inflater.btBottom.setOnClickListener { bindStudyList() }
                        if (bindStudyListName.isNotEmpty()) {
                            inflater.btBottom.setText("确定")
                        } else {
                            inflater.btBottom.setText("绑定学习清单")
                        }
                    }
                }
            }
            //成功,失败,未完成
            TaskConstants.TASK_STATUS_SUCCESS, TaskConstants.TASK_STATUS_FAIL, TaskConstants.TASK_STATUS_CHILD_NOT_COMPLETE -> {
                inflater.flBottom.visibility = View.GONE
            }
            //进行中
            TaskConstants.TASK_STATUS_DOING -> {
                if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN_MORNING) {//早起任务，底部按钮隐藏
                    inflater.flBottom.visibility = View.GONE
                } else if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN) {
                    if (taskBean.task_config_data?.click == true) {
                        //打卡任务
                        inflater.btBottom.text = "去打卡"
                        inflater.btBottom.setOnClickListener {
                            ARouter.getInstance().build(Paths.PAGE_CHECKIN).navigation()
                        }
                    } else {
                        inflater.btBottom.text = "当前不在打卡时间"
                        inflater.btBottom.setBackgroundResource(R.drawable.shape_radius22_color_87caab)
                    }

                } else if (taskBean.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && taskBean.task_resource_type == ResourceTypeConstans.TYPE_ARTICLE) {
                    if (taskBean.bind_data != null && taskBean.bind_data?.source_name?.isNotEmpty() == true) {
                        //已绑定学习清单
                        inflater.btBottom.text = "去阅读"
                        //点击去阅读,和学习清单调整一样
                        inflater.btBottom.setOnClickListener {
                            turnToStudyList(taskBean)
                        }
                    } else {
                        inflater.btBottom.setOnClickListener { bindStudyList() }
                        if (bindStudyListName.isNotEmpty()) {
                            inflater.btBottom.text = "确定"
                        } else {
                            inflater.btBottom.text = "绑定学习清单"
                        }
                    }
                } else {
                    inflater.flBottom.visibility = View.GONE
                }
            }
            else -> {
                inflater.flBottom.visibility = View.GONE
            }
        }
    }

    var bindStudyListId = "" //绑定学习清单id
    var bindStudyListName = "" //绑定学习清单name

    /**
     * 展示选择任务布局
     */
    fun showChoiceTaskLayout() {
        choiceViewModel.choic = choiceTaskDetail
        inflater.llMutualBottom.visibility = View.VISIBLE


        val titles = arrayListOf<String>()
        if (choiceTaskDetail?.necessary?.isNotEmpty() == true) {
            titles.add(TaskConstants.STR_MUST_TASK)
        }
        choiceTaskDetail?.choice?.forEach {
            titles.add(it.title)
        }

        if (titles.isNotEmpty() && choiceTaskDetail != null) {
            //adpater
            val discoverPagerAdapter =
                TaskMutualChoiceViewPagerAdatper(titles, choiceTaskDetail!!, this)
            inflater.vpMutualTask.adapter = discoverPagerAdapter
            setTabLayoutTab(titles)
        }
    }

    /**
     * 点击确定,绑定学习清单
     */
    fun bindStudyList() {
        if (bindStudyListId.isEmpty() && bindStudyListName.isEmpty()) {
//            toast("当前任务未设置指定学习清单")
            val bundle = Bundle()
            bundle.putParcelableArrayList(
                STUDYLIST,
                mViewModel.taskDetailData.value?.data?.other_data?.folder_list
            )
            ARouter.getInstance()
                .build(Paths.PAGE_SELECT_STUDY_LIST_ACTIVITY)
                .with(bundle)
                .navigation(this, REQUEST_CODE_SELECT_STUDY_LIST)

            return
        }
        val jsonObject = JSONObject()
        jsonObject.put("task_id", taskId)
        val jsonObjectBean = JSONObject()
        if (bindStudyListId.isNotEmpty()) {
            jsonObjectBean.put("id", bindStudyListId)
        }
        if (bindStudyListName.isNotEmpty()) {
            jsonObjectBean.put("name", bindStudyListName)
        }
        jsonObject.put("relation", jsonObjectBean)
        val toRequestBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        mViewModel.bindStudyList(toRequestBody).observe(this, {
            inflater.tvNoStudyListTip.visibility = View.GONE
            getData()
        })
    }


    /**
     * 展示任务进度总进度
     */
    private fun showTaskTotalProgress(taskBean: TaskDetailsBean) {
        //任务进度文案提示
        inflater.tvTaskEndAway.text = getTaskProcessTipStr(taskBean)
        inflater.tvTaskEndAway2.text = getTaskProcessTipStr(taskBean)

        if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN_MORNING) {
            inflater.tvMorningTimeDuration.visibility = View.VISIBLE
            inflater.tvMorningTimeDuration.text = getMorningTipString(taskBean)
            if (taskBean.status == TaskConstants.TASK_STATUS_FAIL || taskBean.status == TaskConstants.TASK_STATUS_SUCCESS) {
                inflater.tvMorningTimeDuration.visibility = View.GONE
            }
        } else {
            inflater.tvMorningTimeDuration.visibility = View.GONE
        }


        var max = taskBean.finish_data?.total_day_count ?: 0
        var progress = taskBean.finish_data?.finish_day_count ?: 0

        //普通任务,或组合任务显示完成个数
        if (taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_NOMAL || taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE) {
            max = taskBean.finish_data?.total_count ?: 0
            progress = taskBean.finish_data?.completed_count ?: 0
        }

        //青年读书会定制任务显示完成个数和总个数
        if (taskBean.type == TaskConstants.TASK_TYPE_REED_BOOK) {
            max = taskBean.finish_data?.total_count ?: 0
            progress = taskBean.finish_data?.completed_count ?: 0
        }

        inflater.progressBar.max = max
        inflater.progressBar.progress = progress

        if (taskBean.status == TaskConstants.TASK_STATUS_FAIL || taskBean.status == TaskConstants.TASK_STATUS_CHILD_NOT_COMPLETE) {
            inflater.progressBar.progressDrawable =
                getDrawableRes(R.drawable.bg_task_detail_progressbar_fail)
        } else {
            inflater.progressBar.progressDrawable =
                getDrawableRes(R.drawable.bg_task_detail_progressbar)
        }


    }


    fun getMorningTipString(taskBean: TaskDetailsBean): CharSequence {
        val morningStr = spannableString {
            val startStr = "指定时间:  ";
            str = startStr + taskBean.finish_data?.early_task_limit_time;
            colorSpan {
                color = getColorRes(R.color.colorPrimary)
                start = startStr.length
                end = str.length
            }
        };
        return morningStr;
    }

    /**
     * 组合任务进度的文案提示
     */
    fun getTaskProcessTipStr(taskBean: TaskDetailsBean): CharSequence {


        val finishDay = taskBean.finish_data?.remain_days
        var tempStr =
            "${taskBean.finish_data?.finish_day_count ?: 0}/${taskBean.finish_data?.total_day_count ?: 0}"


        val todayCount = taskBean.finish_data?.today_count
        val todayFinishCount = taskBean.finish_data?.today_finish_count

        val totalCount = taskBean.finish_data?.total_count
        val completedCount = taskBean.finish_data?.completed_count

        //普通任务,或组合任务显示完成个数
        if (taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_NOMAL || taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE) {
            tempStr = "${completedCount}/${totalCount}"
        }

        //青年读书会定制任务,显示已完成/总个数
        if (taskBean.type == TaskConstants.TASK_TYPE_REED_BOOK) {
            tempStr = "${completedCount}/${totalCount}"
        }
        var temp = ""
        //累积型任务
        if (taskBean.status == TaskConstants.TASK_STATUS_FAIL) {
            temp =
                "任务失败。您${getTaskUnitBefore(taskBean)} ${tempStr} ${getTaskUnitAfter(taskBean)}。"
        } else if (taskBean.status == TaskConstants.TASK_STATUS_CHILD_NOT_COMPLETE) {
            temp =
                "任务未完成。您${getTaskUnitBefore(taskBean)} ${tempStr} ${getTaskUnitAfter(taskBean)}。"
        } else if (taskBean.status == TaskConstants.TASK_STATUS_SUCCESS) {
            temp =
                "任务成功。您${getTaskUnitBefore(taskBean)} ${tempStr} ${getTaskUnitAfter(taskBean)}。"
        } else {
            temp = "距任务截止还有 ${finishDay} 天，您${getTaskUnitBefore(taskBean)} ${tempStr} ${
                getTaskUnitAfter(taskBean)
            }。"

            if (taskBean.time_mode == 1) {
                temp = "您${getTaskUnitBefore(taskBean)} ${tempStr} ${getTaskUnitAfter(taskBean)}。"
            }
        }

        //进行中的打卡r有点特殊
        if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN && taskBean.status == TaskConstants.TASK_STATUS_DOING) {
            var tvTaskEndAwayStr =
                "距任务截止还有 ${finishDay} 天，您已完成 ${tempStr} 天任务。今日已完成 ${todayFinishCount}/${todayCount} 打卡"

            if (taskBean.time_mode == 1) {
                tvTaskEndAwayStr =
                    "您已完成 ${tempStr} 天任务。今日已完成 ${todayFinishCount}/${todayCount} 打卡"
            }
            val spannableString = spannableString {
                str = tvTaskEndAwayStr
                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = tvTaskEndAwayStr.indexOf("距任务截止还有 ${finishDay}") + 8
                    end =
                        tvTaskEndAwayStr.indexOf("距任务截止还有 ${finishDay}") + 8 + "$finishDay".length
                }
                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = tvTaskEndAwayStr.indexOf("您已完成") + 4
                    end = tvTaskEndAwayStr.indexOf("天任务")
                }

                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = tvTaskEndAwayStr.indexOf("今日已完成") + 5
                    end = tvTaskEndAwayStr.indexOf("打卡")
                }
            }
            return spannableString
        }

        //早起打卡文案
        if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN_MORNING && taskBean.status == TaskConstants.TASK_STATUS_DOING) {
            var tvTaskEndAwayStr =
                "距任务截止还有 ${finishDay} 天，您已完成 ${tempStr} 天指定时间打卡"

            if (taskBean.time_mode == 1) {
                tvTaskEndAwayStr =
                    "您已完成 ${tempStr} 天指定时间打卡"
            }
            val spannableString = spannableString {
                str = tvTaskEndAwayStr
                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = tvTaskEndAwayStr.indexOf("距任务截止还有 ${finishDay}") + 8
                    end =
                        tvTaskEndAwayStr.indexOf("距任务截止还有 ${finishDay}") + 8 + "$finishDay".length
                }
                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = tvTaskEndAwayStr.indexOf("您已完成") + 4
                    end = tvTaskEndAwayStr.indexOf("天指定时间打卡")
                }


            }
            return spannableString
        }


        var spannableString = spannableString {
            str = temp
            colorSpan {
                color = getColorRes(R.color.colorPrimary)
                start = str.indexOf("${tempStr}")
                end = str.indexOf("${tempStr}") + "${tempStr}".length
            }
        }

        if (spannableString.startsWith("任务成功")) {
            spannableString = spannableString {
                str = spannableString
                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = str.indexOf("任务成功")
                    end = str.indexOf("任务成功") + "任务成功".length
                }
                styleSpan {
                    styleSpan = Typeface.BOLD
                    start = str.indexOf("任务成功")
                    end = str.indexOf("任务成功") + "任务成功".length
                }
            }
        }

        if (spannableString.startsWith("任务失败")) {
            spannableString = spannableString {
                str = spannableString
                styleSpan {
                    styleSpan = Typeface.BOLD
                    start = str.indexOf("任务失败")
                    end = str.indexOf("任务失败") + "任务失败".length
                }
            }
        }

        if (spannableString.startsWith("任务未完成")) {
            spannableString = spannableString {
                str = spannableString
                styleSpan {
                    styleSpan = Typeface.BOLD
                    start = str.indexOf("任务未完成")
                    end = str.indexOf("任务未完成") + "任务未完成".length
                }
            }
        }

        if (spannableString.contains("距任务截止")) {
            spannableString = spannableString {
                str = spannableString
                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = str.indexOf("${finishDay}")
                    end = str.indexOf("${finishDay}") + "${finishDay}".length
                }
            }
        }

        return spannableString


    }

    /**
     * 获取任务进度提示的单位,(已完成,已阅读)
     */
    fun getTaskUnitBefore(taskBean: TaskDetailsBean): String {
        return when (taskBean.type) {
            TaskConstants.TASK_TYPE_STUDY_RESOURCE -> {
                if (taskBean.task_resource_type == ResourceTypeConstans.TYPE_E_BOOK) {
                    "已阅读"
                } else {
                    "已完成"
                }
            }
            TaskConstants.TASK_TYPE_REED_BOOK -> {
                "已阅读"
            }
            else -> "已完成"
        }
    }

    /**
     * 获取任务进度提示的单位,(天任务,已阅读)
     */
    fun getTaskUnitAfter(taskBean: TaskDetailsBean): String {
        return when {
            taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_CONTINUE || taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_SUM -> {
                "天任务"
            }
            taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_COMBINE -> {
                "个子任务"
            }
            taskBean.type == TaskConstants.TASK_TYPE_STUDY_PROJECT -> {
                "个学习项目"
            }
            taskBean.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && (taskBean.task_resource_type == ResourceTypeConstans.TYPE_ARTICLE) && taskBean.is_bind_folder -> {
                "篇文章"
            }
            taskBean.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE && taskBean.task_resource_type == ResourceTypeConstans.TYPE_E_BOOK -> {
                "本电子书"
            }
            taskBean.type == TaskConstants.TASK_TYPE_REED_BOOK -> {
                "本电子书"
            }
            taskBean.type == TaskConstants.TASK_TYPE_CHECKIN_MORNING -> {
                "天指定时间打卡"
            }
            else -> ""
        }
    }


    /**
     * 已领取,未开始
     * 展示距任务开始时间
     */
    fun showStartAwayTime(taskBean: TaskDetailsBean) {
        var taskTime = "${taskBean.task_start_date} - ${taskBean.task_end_date}"
        if (TextUtils.isEmpty(taskBean.task_start_date) && TextUtils.isEmpty(taskBean.task_end_date)) {
            taskTime = "永久开放"
        }
        inflater.tvTaskTime.setText("任务时间: ${taskTime}")

        if (taskBean.time_mode == 1) {
            inflater.tvTaskTime.setText("任务时间: 永久开放")
        }
        inflater.taskCutdownView.bindCutDownTime(taskBean.finish_data?.timedelta_remain ?: "")
        inflater.taskCutdownView.heighLightBgRes = R.drawable.shape_gray_ededed_radius2
    }

    /**
     * 展示任务规则
     */
    fun showTaskRule(taskBean: TaskDetailsBean) {
        if (taskBean.task_rule == null) {
            inflater.llRuleContainer.visibility = View.GONE
            return
        }
        //如果两个字段都为空
        if (TextUtils.isEmpty(taskBean.task_rule!!.base_rule) && TextUtils.isEmpty(taskBean.task_rule!!.special_rule)) {
            inflater.llRuleContainer.visibility = View.GONE
            return
        }

        inflater.llRuleContainer.visibility = View.VISIBLE
//        tvRuleTitle.visibility = View.GONE
//        webRule.visibility = View.GONE
        inflater.tvRuleDetail.visibility = View.GONE

        if (!TextUtils.isEmpty(taskBean.task_rule!!.special_rule)) {
            inflater.tvRuleDetail.setText(taskBean.task_rule!!.special_rule)
            inflater.tvRuleDetail.visibility = View.VISIBLE
        }
    }


    /**
     * 显示完成或待完成的任务资源
     */
    fun showFinishResource(taskBean: TaskDetailsBean) {
        //累计,或连续型任务,需要进行中才显示
        if (taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_SUM || taskBean.calculate_type == TaskConstants.TASK_CACULATE_TYPE_CONTINUE) {
            if (taskBean.status == TaskConstants.TASK_STATUS_DOING) {
                //累计型的打卡也不显示今日进度
                if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN) {
                    inflater.llTodayProgressContainer.visibility = View.GONE
                } else {
                    inflater.llTodayProgressContainer.visibility = View.VISIBLE
                }
                inflater.tvTodayTip.visibility = View.VISIBLE
                inflater.tvTodayRemain.visibility = View.VISIBLE
                setTodayProgressTip(taskBean)


                if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN_MORNING) {
                    inflater.tvTodayRemain.visibility = View.GONE
                }

                val taskCompleteResourceAdapter =
                    TaskCompleteResourceAdapter(taskBean.finish_source_data!!)
                taskCompleteResourceAdapter.addChildClickViewIds(R.id.tvToDo)
                taskCompleteResourceAdapter.setOnItemChildClickListener(OnItemChildClickListener { adapter, view, position ->
                    //早起任务打卡跳转到内容提交页
                    if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN_MORNING) {
                        if (!GlobalsUserManager.isLogin()) { //未登录，先登录
                            ResourceTurnManager.turnToLogin()
                            return@OnItemChildClickListener
                        }
                        val put =
                            Bundle().put(IntentParamsConstants.PARAMS_TASK_ID, taskId)

                        ARouter.getInstance().build(Paths.PAGE_TASK_SIGN_DETAIL).with(put)
                            .navigation()
                        return@OnItemChildClickListener
                    }

                    ResourceTurnManager.turnToResourcePage(adapter.data[position] as BaseResourceInterface)
                })

                taskCompleteResourceAdapter.setOnItemClickListener { adapter, view, position ->
                    val item = adapter.data[position] as TaskFinishResource
                    if (item.resource_id != 0) {  //只要绑定了资源就跳转资源详情
                        ResourceTurnManager.turnToResourcePage(adapter.data[position] as BaseResourceInterface)
                    }
                }

                inflater.rvResources.layoutManager = LinearLayoutManager(this)
                taskCompleteResourceAdapter.setTaskType(taskBean.type)
                inflater.rvResources.adapter = taskCompleteResourceAdapter

            } else {
                inflater.llTodayProgressContainer.visibility = View.GONE
                inflater.tvTodayRemain.visibility = View.GONE
                inflater.tvTodayTip.visibility = View.GONE
            }
        } else {
            //普通任务,隐藏距任务截止
            inflater.tvTodayRemain.visibility = View.GONE
            inflater.tvTodayTip.visibility = View.GONE

            if (taskBean.finish_source_data != null && taskBean.finish_source_data!!.isNotEmpty()) {
                inflater.llTodayProgressContainer.visibility = View.VISIBLE

                val taskCompleteResourceAdapter =
                    TaskCompleteResourceAdapter(taskBean.finish_source_data!!)
                taskCompleteResourceAdapter.setOnItemClickListener { adapter, view, position ->
                    val item = adapter.data[position] as TaskFinishResource
                    if (item.resource_id != 0) {  //只要绑定了资源就跳转资源详情
                        ResourceTurnManager.turnToResourcePage(adapter.data[position] as BaseResourceInterface)
                    }
                }
                taskCompleteResourceAdapter.addChildClickViewIds(R.id.tvToDo)
                taskCompleteResourceAdapter.setOnItemChildClickListener(OnItemChildClickListener { adapter, view, position ->
                    ResourceTurnManager.turnToResourcePage(adapter.data[position] as BaseResourceInterface)
                })

                inflater.rvResources.layoutManager = LinearLayoutManager(this)
                inflater.rvResources.adapter = taskCompleteResourceAdapter
            } else {
                inflater.llTodayProgressContainer.visibility = View.GONE
            }
        }

        //占位背景显示,同真正容器
        inflater.llProgressSpace.visibility = inflater.llTodayProgressContainer.visibility


    }

    /**
     * 累计型任务设置今日进度提醒
     */
    fun setTodayProgressTip(taskBean: TaskDetailsBean) {
        val todayTotalCount = taskBean.finish_data?.today_count ?: 0
        val todayCompleteCount = taskBean.finish_data?.today_finish_count ?: 0
        val remainCount = todayTotalCount - todayCompleteCount;

        if (taskBean.type == TaskConstants.TASK_TYPE_STUDY_RESOURCE) {
            val temp = "今日结束前只需要再阅读 ${remainCount} 篇文章可提升任务总进度。"
            val spannableString = spannableString {
                str = temp
                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = temp.indexOf(remainCount.toString())
                    end = temp.indexOf(remainCount.toString()) + "${remainCount}".length
                }
            }
            inflater.tvTodayRemain.text = spannableString
        } else if (taskBean.type == TaskConstants.TASK_TYPE_CHECKIN) {
            val temp = "今日结束前只需要再打 ${remainCount} 次卡可提升任务总进度。"
            val spannableString = spannableString {
                str = temp
                colorSpan {
                    color = getColorRes(R.color.colorPrimary)
                    start = temp.indexOf(remainCount.toString())
                    end = temp.indexOf(remainCount.toString()) + "${remainCount}".length
                }
            }
            inflater.tvTodayRemain.text = spannableString
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_STUDY_LIST) {
            if (resultCode == RESULT_OK) {
                bindStudyListName = data?.getStringExtra(STUDYLISTNAME) ?: ""
                bindStudyListId = data?.getStringExtra(STUDYLISTID) ?: ""
                val bindTipVisible =
                    if (TextUtils.isEmpty(bindStudyListId)) View.VISIBLE else View.GONE
                inflater.tvNoStudyListTip.visibility = bindTipVisible
                inflater.tvInputStudyListName.text = bindStudyListName
                inflater.tvInputStudyListName.setTextColor(resources.getColor(R.color.color_2))

            }
        }
    }

}