package com.mooc.course.ui.activity

//import com.mooc.commonbusiness.model.search.ChaptersBean
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.put
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseVmActivity
import com.mooc.commonbusiness.base.BaseVmViewModel
import com.mooc.commonbusiness.constants.*
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.pop.CommonMenuPopupW
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.route.routeservice.StatisticsService
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.share.ShareSchoolUtil
import com.mooc.course.R
import com.mooc.course.databinding.ActivityCourseDetailBinding
import com.mooc.course.model.*
import com.mooc.course.ui.adapter.CourseOutLineAdapter
import com.mooc.course.ui.adapter.CourseQuestionAdapter
import com.mooc.course.ui.adapter.CourseTeacherAdapter
import com.mooc.course.ui.adapter.RecommendCourseAdapter
import com.mooc.course.ui.deleget.MoocCourseDetailDeleget
import com.mooc.course.ui.deleget.NewXtCourseDetailDeleget
import com.mooc.course.ui.pop.CourseChoosePop
import com.mooc.course.ui.pop.TeacherDetailPop
import com.mooc.course.viewmodel.CourseDetailViewModel
import com.mooc.statistics.LogUtil
//import kotlinx.android.synthetic.main.activity_course_detail.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.regex.Pattern


@Route(path = Paths.PAGE_COURSE_DETAIL)
class CourseDetailActivity : BaseVmActivity<CourseDetail, ActivityCourseDetailBinding>() {


    companion object {
        const val TAB_STR_COURSE = "课程"
        const val TAB_STR_DES = "简介"
        const val TAB_STR_OURLINE = "大纲"
        const val TAB_STR_TEACHER = "教师"
        const val TAB_STR_QUESTION = "常见问题"

        //新学堂课程状态
        const val COURSE_STATUS_SIGNUP_READY = 1
        const val COURSE_STATUS_SIGNUP_NOT_START = 2
        const val COURSE_STATUS_SIGNUP_END = 3
        const val COURSE_STATUS_CLASS_END = 4
        const val COURSE_STATUS_NOT_READY = 5
        const val COURSE_STATUS_LOOK_BACK = 6
        const val COURSE_STATUS_CLASS_END_END = 7
        const val COURSE_STATUS_CLASS_NOT_START = 8
        const val COURSE_STATUS_READY = 9
        const val COURSE_STATUS_CLASS_END_NO_LOOK_BACK = 10

        const val REQUEST_CODE_TO_MARK = 0;//去课程评价请求码

    }

    var courseId = "" //课程id

//    var currentClassPosition = 0 //当前选中班级的位置
    var classRoomId = "" //班级id
    var coursePlatform = "" //课程平台标识
    var courseDetail: CourseDetail? = null


    var titleTabsPosition = linkedMapOf(TAB_STR_COURSE to 10)


    override fun genericViewModel(): BaseVmViewModel<CourseDetail> {
        courseId = intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_ID) ?: ""
        classRoomId = intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID) ?: ""
        coursePlatform = intent.getStringExtra(IntentParamsConstants.COURSE_PARAMS_PLATFORM) ?: ""
//        return ViewModelProviders.of(this, BaseViewModelFactory(courseId))
//            .get(CourseDetailViewModel::class.java)

        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CourseDetailViewModel(courseId) as T
            }
        }).get(CourseDetailViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.activity_course_detail


    var mBindingView: ActivityCourseDetailBinding? = null
    lateinit var courseDetailVM: CourseDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置返回健
        mBindingView?.commonTitle?.setOnLeftClickListener {
            finish()
        }

        mBindingView?.ibScrollBack?.setOnClickListener {
            finish()
        }

        courseDetailVM.getRecommendCourse(courseId).observe(this, {

            if (it.isNullOrEmpty()) {
                mBindingView?.llRecommend?.visibility = View.GONE
                return@observe
            }
            val coureAdapter = RecommendCourseAdapter(it)
            coureAdapter.setOnItemClickListener { adapter, view, position ->
                val resource = coureAdapter.data.get(position)
                ResourceTurnManager.turnToResourcePage(resource)

                LogUtil.addClickLogNew("${LogEventConstants2.T_COURSE}#$courseId#${LogEventConstants2.P_SIM}",resource._resourceId,resource._resourceType.toString(),resource.title,)
            }
            mBindingView?.rcyRecommendCourse?.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            mBindingView?.rcyRecommendCourse?.adapter = coureAdapter
        })

    }

    override fun onCreateLayout(bindingView: ActivityCourseDetailBinding?) {
        courseDetailVM = mViewModel as CourseDetailViewModel
        mBindingView = bindingView


        initView()

    }

    private fun initView() {
        mBindingView?.commonTitle?.setOnLeftClickListener { finish() }
        //点击回调顶部
        mBindingView?.ivBackToTop?.setOnClickListener {
            mBindingView?.scrollView?.scrollTo(0, 0)
        }

        mBindingView?.scrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            onScrollChangeListener(scrollY)
        })

        mBindingView?.stlTitle?.setTabSelectListener {
            val childPositionList = titleTabsPosition.values.toTypedArray()
            if (it in childPositionList.indices)
                mBindingView?.scrollView?.scrollTo(0, childPositionList[it])
        }

        //点击菜单
//        val watchView = XPopup.Builder(this).watchView(commonTitle.ib_right)
        mBindingView?.commonTitle?.setOnRightIconClickListener(View.OnClickListener {
//            showMenuPop()
            val commonTitleMenuPop = CommonMenuPopupW(this, arrayListOf("分享"), it)
            commonTitleMenuPop.onTypeSelect = {
                if (it == "分享") {
                    showSharePop()
                }
            }
            commonTitleMenuPop.show()
        })
    }

    /**
     * 点击分享点击事件
     */
    val onShareItemClick: (platForm: Int, shareDetail: ShareDetailModel) -> Unit =
            { platForm, shareDetail ->
                if (platForm == CommonBottomSharePop.SHARE_TYPE_SCHOOL) {
                    //分享到学友圈
                    ShareSchoolUtil.postSchoolShare(
                            this,
                            ResourceTypeConstans.TYPE_COURSE.toString(),
                            courseId,
                            shareDetail.share_picture
                    )
                } else {
//                val targetUrl: String = shareDetail.share_link
//                val shareAddScore = ARouter.getInstance().navigation(ShareSrevice::class.java)
//                shareAddScore.shareAddScore(ShareTypeConstants.TYPE_COURSE,
//                    this, IShare.Builder()
//                        .setSite(platForm)
//                        .setWebUrl(targetUrl)
//                        .setTitle(shareDetail.share_title)
//                        .setMessage(shareDetail.share_about)
//                        .setImageUrl(shareDetail.share_picture)
//                        .build()
//                )

                    //由于老版本默认分享字段为不可分享，暂时写死分享内容
                    var targetUrl =
                            "${UrlConstants.COURSE_SHARE_URL_HEADER}${courseDetail?.id ?: ""}${UrlConstants.SHARE_FOOT}${UrlConstants.SHARE_FOOT_MASTER}"
                    val shareTitle = courseDetail?.title ?: ""
                    val shareContent = courseDetail?.about ?: ""
                    val sharePicture = courseDetail?.picture ?: UrlConstants.SHARE_LOGO_URL

                    if (courseDetail?.platform == CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT) {
                        targetUrl = "${
                            String.format(
                                    UrlConstants.NEWXT_COURSE_SHARE_URL_HEADER,
                                    courseDetail?.id ?: ""
                            )
                        }${courseDetail?.id ?: ""}${UrlConstants.SHARE_FOOT}${UrlConstants.SHARE_FOOT_MASTER}"
                    }

                    //新版本统一使用weixin_url字段
                    targetUrl = shareDetail.weixin_url
                    val shareAddScore = ARouter.getInstance().navigation(ShareSrevice::class.java)
                    shareAddScore.shareAddScore(
                            ShareTypeConstants.TYPE_COURSE,
                            this, IShare.Builder()
                            .setSite(platForm)
                            .setWebUrl(targetUrl)
                            .setTitle(shareTitle)
                            .setMessage(shareContent)
                            .setImageUrl(sharePicture)
                            .build()
                    )
                }
            }

    private fun showSharePop() {
//        toast("展示分享弹窗")
        var chooseBack: ((platform: Int) -> Unit)? = null
        if (mViewModel?.resourceShareDetaildata?.value != null) {
            chooseBack = { platform ->
                onShareItemClick.invoke(platform, mViewModel?.resourceShareDetaildata?.value!!)
            }
        } else {
            mViewModel?.getShareInfo(ResourceTypeConstans.TYPE_COURSE.toString(), courseId)
                    ?.observe(this, Observer {
                        chooseBack = { platform ->
                            onShareItemClick.invoke(
                                    platform,
                                    mViewModel?.resourceShareDetaildata?.value!!
                            )
                        }
                    })
        }
        val commonBottomSharePop = CommonBottomSharePop(this, chooseBack, false)
        XPopup.Builder(this)
                .asCustom(commonBottomSharePop)
                .show()


    }

    /**
     * scrollView滚动监听
     * @param y 滑动距离
     */
    private fun onScrollChangeListener(y: Int) {
        val llScrollTitleVisiable = if (y >= 10) View.VISIBLE else View.GONE
        mBindingView?.llScrollTitle?.visibility = llScrollTitleVisiable
        mBindingView?.ivBackToTop?.visibility = llScrollTitleVisiable

        //判断是在哪个位置
        var tabSelectPosition = 0

        val childPositionList = titleTabsPosition.values.toTypedArray()
        for (i in 0 until childPositionList.size) {
            if (i == childPositionList.size - 1) break    //最后一个前一个，没有就没有了，以免索引越界
            if (y in childPositionList[i] until childPositionList[i + 1]) {
                tabSelectPosition = i
                break
            }
        }
        mBindingView?.stlTitle?.selectPosition(tabSelectPosition)
//        loge(childPositionList, "selectPosition: $tabSelectPosition")
    }

    /**
     * 数据改变
     */
    override fun onDataChange(it: CourseDetail) {
        courseDetail = it
        //绑定xml数据
        mBindingView?.courseDetail = it

        //设置评分布局
        setMarkData(it)
        //设置一起学习的人
        setSubUsers(it)

        mBindingView?.tvToMark?.setOnClickListener { v->
            ARouter.getInstance().build(Paths.PAGE_COURSE_MARK).withString(IntentParamsConstants.COURSE_PARAMS_ID, it.id).navigation(this, REQUEST_CODE_TO_MARK)
        }

        //如果是中国大学mooc，简介，章节，教师通过富文本展示
        if (it.platform == CoursePlatFormConstants.COURSE_PLATFORM_MOOC) {
            val moocCourseDetailDeleget = MoocCourseDetailDeleget(this, courseId)
            mBindingView?.let { it1 -> moocCourseDetailDeleget.bind(it1, it, classRoomId) }

            titleTabsPosition = moocCourseDetailDeleget.titleTabsPosition
            mBindingView?.stlTitle?.setTabStrs(titleTabsPosition.keys.toTypedArray())
            return
        }


        //如果是新学堂课程，并且班级信息不为空，代表是新学堂课程，绑定班级信息
        if (it.platform == CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT && it.classroom_info != null && it.classroom_info.isNotEmpty()) {
            val moocCourseDetailDeleget = NewXtCourseDetailDeleget(this, courseId)
            mBindingView?.let { it1 -> moocCourseDetailDeleget.bind(it1, it, classRoomId) }
//            bindClassRoomInfo(it.classroom_info)
            titleTabsPosition = moocCourseDetailDeleget.titleTabsPosition
            mBindingView?.stlTitle?.setTabStrs(titleTabsPosition.keys.toTypedArray())
            return
        }


        //设置标题栏加入学习室状态
        setTitleAddStatus(it.is_enrolled)
        //设置基本信息
        setBaseInfo(it)
        //设置课程简介
        setCourseIntro(it.about)
        //设置课程大纲
        bindChapters(it.chapters)

        //设置教师
        bindStaffInfo(it.staffs)

        //设置常见问题
        bindQuestionInfo(it.qas)

        //设置TabIndicator
        mBindingView?.stlTitle?.setTabStrs(titleTabsPosition.keys.toTypedArray())

        //设置去学习按钮
        setAddStudyRoom(it)


    }

    fun setMarkData(course: CourseDetail) {
        if (course.is_enrolled && !course.is_appraise) { //已报名且未打分显示去评分
            mBindingView?.tvToMark?.visibility = View.VISIBLE
        } else {
            mBindingView?.tvToMark?.visibility = View.INVISIBLE
        }

        //设置星星数量
        val starNum = when (course.appraise_score) {
            in 0.0f..0.4f -> {
                0.0f
            }
            in 0.5f..1.4f -> {
                0.5f
            }
            in 1.5f..2.4f -> {
                1f
            }
            in 2.5f..3.4f -> {
                1.5f
            }
            in 3.5f..4.4f -> {
                2f
            }
            in 4.5f..5.4f -> {
                2.5f
            }
            in 5.5f..6.4f -> {
                3f
            }
            in 6.5f..7.4f -> {
                3.5f
            }
            in 7.5f..8.4f -> {
                4f
            }
            in 8.5f..9.4f -> {
                4.5f
            }
            in 9.5f..10.0f -> {
                5f
            }
            else -> {
                if (course.appraise_score < 0) {
                    0f
                } else {
                    5f
                }
            }
        }

        mBindingView?.starBar?.starMark = starNum
        mBindingView?.tvMark?.setText("${course.appraise_score}分")
//        loge("课程评分: ${starNum}")
    }

    fun setSubUsers(course: CourseDetail) {
        if (course.sub_users == null || course.sub_users?.isEmpty() == true) {
            mBindingView?.llSubUsersContainer?.visibility = View.GONE
            return
        }
        mBindingView?.llSubUsersContainer?.visibility = View.VISIBLE

        mBindingView?.apply {
            when (course.sub_users?.size) {
                1 -> {
                    Glide.with(this.root)
                        .load(course.sub_users?.get(0)?.avatar ?: "")
                        .transform(CircleCrop())
                        .error(R.mipmap.common_ic_user_head_default)
                        .into(this.ivUser1)
                    mBindingView?.ivUser2?.visibility = View.GONE
                    mBindingView?.ivUser3?.visibility = View.GONE
                }
                2 -> {
                    Glide.with(this.root)
                        .load(course.sub_users?.get(0)?.avatar ?: "")
                        .transform(CircleCrop())
                        .error(R.mipmap.common_ic_user_head_default)
                        .into(this.ivUser1)
                    Glide.with(this.root)
                        .load(course.sub_users?.get(1)?.avatar ?: "")
                        .transform(CircleCrop())
                        .error(R.mipmap.common_ic_user_head_default)
                        .into(this.ivUser2)
                    mBindingView?.ivUser3?.visibility = View.GONE
                }
                3 -> {
                    Glide.with(this.root)
                        .load(course.sub_users?.get(0)?.avatar ?: "")
                        .transform(CircleCrop())
                        .error(R.mipmap.common_ic_user_head_default)
                        .into(this.ivUser1)
                    Glide.with(this.root)
                        .load(course.sub_users?.get(1)?.avatar ?: "")
                        .transform(CircleCrop()).error(R.mipmap.common_ic_user_head_default)
                        .into(this.ivUser2)
                    Glide.with(this.root)
                        .load(course.sub_users?.get(2)?.avatar ?: "")
                        .transform(CircleCrop())
                        .error(R.mipmap.common_ic_user_head_default)
                        .into(this.ivUser3)
                }
            }
        }


        mBindingView?.llSubUsersContainer?.setOnClickListener {
            val sb = StringBuffer()
            course.sub_users?.forEach {
                sb.append(it.user_id)
                sb.append(",")
            }

            if (sb.endsWith(",")) {
                sb.replace(sb.length - 1, sb.length, "")
            }


            ARouter.getInstance().build(Paths.PAGE_COURSE_SUB_USER)
                    .withString(IntentParamsConstants.COURSE_PARAMS_ID, course.id)
                    .withString(CourseChoseUserActivity.PARAMS_SUB_USERS, sb.toString()).navigation()
            loge("点击跳转到查看所有学习的用户${sb.javaClass} : ${sb}")
        }

    }


    /**
     * 设置基本信息
     */
    private fun setBaseInfo(it: CourseDetail) {
        try {
            //开课时间和报名时间
            if (TextUtils.isEmpty(it.end)) {
                mBindingView?.tvStartTime?.text = "永久开课"
            } else {
                mBindingView?.tvStartTime?.text =
                        "开课时间: " + it.start.substring(0, 10) + "－" + it.end.substring(0, 10)
            }

            if (!TextUtils.isEmpty(it.enrollment_start)) {
                mBindingView?.tvEnrollTime?.visibility = View.VISIBLE
                var enrollStr = "选课时间: " + it.enrollment_start.substring(0, 10)
                if (!TextUtils.isEmpty(it.enrollment_end)) {
                    enrollStr += "－" + it.enrollment_end.substring(0, 10)
                }
                mBindingView?.tvEnrollTime?.setText(enrollStr)
            } else {
                mBindingView?.tvEnrollTime?.visibility = View.GONE
            }

            //是否有考试
            if (it.is_have_exam_info.isNotEmpty()) {
                mBindingView?.tvHasExam?.text = it.is_have_exam_info
            }

            //证书
            if (it.verified_active_info.isNotEmpty()) {
                mBindingView?.tvHasVerification?.text = it.verified_active_info
            }


            mBindingView?.tvCourseTitle?.text = courseDetail?.title

            mBindingView?.ivCover?.let { ivCover ->
                Glide.with(this)
                        .applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                        .load(courseDetail?.picture)
                        .override(330.dp2px(), 210.dp2px()).into(
                                ivCover
                        )
            }

            if (courseDetail?.video_duration ?: 0.0 > 0.0) {
                mBindingView?.tvStudentTime?.text = "${courseDetail?.video_duration}学时"
            } else {
                mBindingView?.tvStudentTime?.text = "暂无"
            }
            mBindingView?.tvStudentCount?.text = "${courseDetail?.student_num}人学过"
            mBindingView?.tvPlatform?.text = courseDetail?.org
            mBindingView?.tvOrg?.text = courseDetail?.platform_zh
            mBindingView?.tvIsFree?.text = if (courseDetail?.is_free != false) "免费" else "付费"


        } catch (e: Exception) {

        }

    }

    /**
     * 设置课程简介内容，如果包含html标签，需要先解析html的内容
     * 如果不包含html，直接设置文本内容
     */
    fun setCourseIntro(content: String) {
        if (TextUtils.isEmpty(content)) {
            mBindingView?.tvCourseDes?.visibility = View.GONE
            mBindingView?.etvCourseDesc?.visibility = View.GONE
            mBindingView?.line?.visibility = View.GONE
            return
        } else {
            mBindingView?.tvCourseDes?.visibility = View.VISIBLE
            mBindingView?.etvCourseDesc?.visibility = View.VISIBLE
            mBindingView?.line?.visibility = View.VISIBLE
        }

        titleTabsPosition.put(TAB_STR_DES, 0)
        //获取课程简介在布局中的位置
        mBindingView?.tvCourseDes?.post {
            val courseOutlinTop = mBindingView?.tvCourseDes?.top ?: 0
            loge("tvCourseDesTop: ${courseOutlinTop}")
//            childPositionList.add(courseOutlinTop)
            titleTabsPosition.put(TAB_STR_DES, courseOutlinTop)
        }

        val regex = "\\<.*?>"
        val pat = Pattern.compile(regex)
        val mat = pat.matcher(content)

        if (mat.find()) {      //包含
            mBindingView?.etvCourseDesc?.setContent(Html.fromHtml(content).toString())
            return
        }
        mBindingView?.etvCourseDesc?.setContent(content)
    }

    /**
     * 绑定章节信息
     */
    private fun bindChapters(chapters: List<ChaptersBean>?) {
        if (chapters?.isNotEmpty() == true) {
            val combinationChapters = (mViewModel as CourseDetailViewModel).getChapters(chapters)
            //常见问题适配器
            mBindingView?.rvChapers?.isNestedScrollingEnabled = false  //解决嵌套问题
            mBindingView?.rvChapers?.layoutManager = LinearLayoutManager(this)
            mBindingView?.rvChapers?.adapter =
                    CourseOutLineAdapter(combinationChapters as ArrayList<ChaptersBean>)
            mBindingView?.tvCourseOutline?.visibility = View.VISIBLE
            titleTabsPosition.put(TAB_STR_OURLINE, 0)
            mBindingView?.tvCourseOutline?.post {
                val courseOutlinTop = mBindingView?.tvCourseOutline?.top ?: 0
                loge("courseOutlinTop: ${courseOutlinTop}")
                titleTabsPosition.put(TAB_STR_OURLINE, courseOutlinTop)
            }
        } else {
            mBindingView?.tvCourseOutline?.visibility = View.GONE
            mBindingView?.line?.visibility = View.GONE
        }
    }

    /**
     * 绑定教师信息
     */
    private fun bindStaffInfo(staffs: List<StaffInfo>?) {
        if (staffs?.isNotEmpty() == true) {
            mBindingView?.tvTeacher?.visibility = View.VISIBLE
            mBindingView?.rvTeacher?.isNestedScrollingEnabled = false  //解决嵌套问题
            mBindingView?.rvTeacher?.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val courseTeacherAdapter = CourseTeacherAdapter(staffs as ArrayList<StaffInfo>)
            courseTeacherAdapter.setOnItemClickListener { adapter, view, position ->
                staffs.get(position).let { it1 -> showTeacherPop(it1) }
            }
            mBindingView?.rvTeacher?.adapter = courseTeacherAdapter
            titleTabsPosition.put(TAB_STR_TEACHER, 0)
            mBindingView?.tvTeacher?.post {
                val tvTeacherTop = mBindingView?.tvTeacher?.top ?: 0
                loge("tvTeacherTop: ${tvTeacherTop}")
                titleTabsPosition.put(TAB_STR_TEACHER, tvTeacherTop)
            }
        } else {
            mBindingView?.tvTeacher?.visibility = View.GONE
        }
    }

    /**
     * 绑定常见问题列表
     */
    private fun bindQuestionInfo(qas: List<Question>?) {
        if (qas?.isNotEmpty() == true) {//it?.qas.isNotEmpty()会崩溃 空指针异常两个
            mBindingView?.tvQuestion?.visibility = View.VISIBLE
            mBindingView?.rvQuestion?.isNestedScrollingEnabled = false  //解决嵌套问题
            mBindingView?.rvQuestion?.layoutManager = LinearLayoutManager(this)
            mBindingView?.rvQuestion?.adapter = CourseQuestionAdapter(qas as ArrayList<Question>)
            titleTabsPosition.put(TAB_STR_QUESTION, 0)
            mBindingView?.tvQuestion?.post {
                val tvQuestionTop = mBindingView?.tvQuestion?.top ?: 0
                loge("tvQuestionTop: ${tvQuestionTop}")
                titleTabsPosition.put(TAB_STR_QUESTION, tvQuestionTop)
            }
        } else {
            mBindingView?.tvQuestion?.visibility = View.GONE
            mBindingView?.tvDivide5?.visibility = View.GONE
        }
    }

    /**
     * 老学堂
     * 设置加入学习室按钮
     * 新学堂和老学堂逻辑不一样，所以用代码控制
     * 不在binding里设置
     */
    @SuppressLint("SetTextI18n")
    private fun setAddStudyRoom(courseDetailBean: CourseDetail) {
        mBindingView?.tvClassStatus?.setOnClickListener(View.OnClickListener {


            if (!GlobalsUserManager.isLogin()) {
                ResourceTurnManager.turnToLogin()
                return@OnClickListener
            }
            //如果没加入学习室，显示确认弹窗
            if (!courseDetailBean.is_enrolled) {
                showConfirmPop()
                return@OnClickListener
            }
            toCoursePage()
        })


        if (courseDetailBean.status != 0) {
            mBindingView?.tvClassStatus?.text = "已下线"
            mBindingView?.tvClassStatus?.isClickable = false
            return
        }

        //不是来自学堂X，但是没有放链接的
        if (!courseDetailBean.from_xuetangx && TextUtils.isEmpty(courseDetailBean.link)) {
            mBindingView?.tvClassStatus?.text = "该课程仅支持在PC端学习"
            mBindingView?.tvClassStatus?.isClickable = false
            return
        }

        //设置去上课按钮可点击
        mBindingView?.tvClassStatus?.isClickable = true
        mBindingView?.tvClassStatus?.text = courseDetailBean.course_time_message
        if (courseDetailBean.course_time_status == 0) {
            mBindingView?.tvClassStatus?.isClickable = !courseDetailBean.from_xuetangx
            return
        }

        //点击选课按钮
        if (courseDetailBean.course_time_status == 1 && courseDetailBean.from_xuetangx) {
            if (courseDetailBean.check_enrollment == -1) {
                mBindingView?.tvClassStatus?.text = "选课尚未开始"
                mBindingView?.tvClassStatus?.isClickable = false
            } else if (courseDetailBean.check_enrollment == -2 && !courseDetailBean.is_enrolled) {
                //已加入学习室，可以去上课
                mBindingView?.tvClassStatus?.text = "选课已关闭"
                mBindingView?.tvClassStatus?.isClickable = false
            }
            return
        }
        //已结课不可点击课程状态提示按钮
        if (courseDetailBean.course_time_status == -1 && courseDetailBean.course_time_message.contains(
                        "已结课"
                )
        ) {
            mBindingView?.tvClassStatus?.isClickable = courseDetailBean.is_enrolled
        }
    }

    /**
     * 进入课堂页面
     */
    private fun toCoursePage() {
        LogUtil.addClickLogNew(
                "${LogEventConstants2.T_COURSE}#${courseDetail?.id}",
                "${courseDetail?.id}",
                ResourceTypeConstans.TYPE_COURSE.toString(),
                "${courseDetail?.title}"
        )
        courseDetail?.apply {
            //进入之前同步课程
            courseDetailVM.redisCourse(this.id)

            //添加成功，通知学习室刷新
            EventBus.getDefault().post(RefreshStudyRoomEvent(ResourceTypeConstans.TYPE_COURSE))

            //选中了订阅课程消息，上传订阅设置（同时也代表了选课）
            if (subsCribeMessage)
                (mViewModel as CourseDetailViewModel).postEnrollUserSetting(courseDetail?.id ?: "")

            //如果不是xuetangx ,并且不是33，跳转到web
            if (!this.from_xuetangx && this.platform != CoursePlatFormConstants.COURSE_PLATFORM_ZHS) {
                if (this.link.isEmpty()) {   //但是link为空
                    toast("该课程仅支持在PC端学习")
                    return
                }
                ARouter.getInstance().build(Paths.PAGE_WEB_RESOURCE)
                        .with(
                                Bundle()
                                        .put(IntentParamsConstants.WEB_PARAMS_TITLE, this.title)
                                        .put(IntentParamsConstants.WEB_PARAMS_URL, this.link)
                                        .put(IntentParamsConstants.PARAMS_RESOURCE_ID, id)
                                        .put(
                                                IntentParamsConstants.PARAMS_RESOURCE_TYPE,
                                                ResourceTypeConstans.TYPE_COURSE
                                        )
                                        .put(IntentParamsConstants.COURSE_PARAMS_PLATFORM, this.platform)
                        )
                        .navigation()
                return

            }
            //如果33。去智慧树播放页面
            if (this.platform == CoursePlatFormConstants.COURSE_PLATFORM_ZHS) {
                toZhsCoursePlay()
                return@apply
            }
        }


    }

    /**
     * 弹窗确认选课
     */
    private fun confirmSelectCourse(courseDetailBean: CourseDetail) {
        selectCourse(courseDetailBean)
    }

    /**
     * 真正调用选课报名
     */
    private fun selectCourse(courseDetailBean: CourseDetail) {
        courseDetailVM.selectionCourse(courseDetailBean.id).observe(this, Observer {
            setTitleAddStatus(it)
            //改变选课状态
            mViewModel?.initData()
            toCoursePage()

        })
    }

    /**
     * 去智慧树课程播放
     */
    private fun toZhsCoursePlay() {

        val courseBean = courseDetail?.convertCourseBean()
        ARouter.getInstance().build(Paths.PAGE_COURSE_ZHS_PLAY)
                .withString(IntentParamsConstants.COURSE_PARAMS_ID, courseDetail?.course_id)
                .withParcelable(IntentParamsConstants.COURSE_PARAMS_DATA, courseBean)
                .withString(IntentParamsConstants.COURSE_PARAMS_TITLE, courseDetail?.title)
                .navigation()
    }


    /**
     * 点击头部添加学习室按钮
     */
    private fun clickTitleAdd() {
        //添加到学习室文件夹
        addToStudyResouseFolder()
    }

    /**
     * 直接通过接口加入学习室资源文件夹
     */
    private fun addToStudyResouseFolder() {
        //加入成功后，进入
//        toCoursePage()
        val json = JSONObject()
        json.put("id", courseDetail?.id)
        json.put("type", ResourceTypeConstans.TYPE_COURSE.toString())
        (mViewModel as CourseDetailViewModel).addToFolder("0", json).observe(this, Observer {
            if (it.isSuccess) {
                setTitleAddStatus(true)
                //改变选课状态
                mViewModel?.initData()
                toCoursePage()
            } else {
                toast(it.msg)
            }
        })
    }

    /**
     * 设置选课状态
     * @param isEnrolled 是否已加入
     */
    private fun setTitleAddStatus(isEnrolled: Boolean) {
        val rightIcon =
                if (isEnrolled) R.mipmap.common_ic_title_right_added else R.mipmap.common_ic_title_right_add
        mBindingView?.commonTitle?.setRightSecondIconRes(rightIcon)
        mBindingView?.commonTitle?.ib_right_second?.isEnabled = !isEnrolled

        if (!isEnrolled) { //未加入再设置点击事件
            mBindingView?.commonTitle?.setOnSecondRightIconClickListener {
                clickTitleAdd()
            }
        }
    }

    /**
     * 展示教师弹窗
     */
    private fun showTeacherPop(staffInfo: StaffInfo) {
        XPopup.Builder(this)
                .asCustom(TeacherDetailPop(this, staffInfo))
                .show()
    }

    var subsCribeMessage = false //是否订阅课程消息

    /**
     * 展示确认选课弹窗
     */
    private fun showConfirmPop() {
        val courseChoosePop = CourseChoosePop(this)
        courseChoosePop.onConfirm = {
            subsCribeMessage = it
            courseDetail?.let { confirmSelectCourse(it) }
        }
        XPopup.Builder(this)
                .asCustom(courseChoosePop)
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_TO_MARK) {
            //刷新课程评分
            mViewModel?.initData()
        }
    }


}
