package com.mooc.course.ui.deleget

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.put
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.constants.CoursePlatFormConstants
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.eventbus.RefreshStudyRoomEvent
//import com.mooc.commonbusiness.model.search.ChaptersBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.course.CourseApi
import com.mooc.course.binding.XtCourseDetialBindings
import com.mooc.course.databinding.ActivityCourseDetailBinding
import com.mooc.course.model.*
import com.mooc.course.ui.activity.CourseDetailActivity
import com.mooc.course.ui.adapter.CourseOutLineAdapter
import com.mooc.course.ui.adapter.CourseQuestionAdapter
import com.mooc.course.ui.adapter.CourseTeacherAdapter
import com.mooc.course.ui.pop.XTCourseClassChooseDialog
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.regex.Pattern

/**
 * 新学堂详情页代理类
 */
class NewXtCourseDetailDeleget(activity: CourseDetailActivity, var courseId: String) : BaseCourseDetailDeleget(){
    var currentClassPosition = 0 //当前选中班级的位置
    var mWeakReference: WeakReference<CourseDetailActivity>? = null
    var classroom_info: List<ClassroomInfo>? = null
    var currentStatus : XtCourseStatus? = null

    init {
        mWeakReference = WeakReference<CourseDetailActivity>(activity)
    }


    @SuppressLint("SetTextI18n")
    fun bind(
        databinding: ActivityCourseDetailBinding,
        courseDetail: CourseDetail,
        classRoomId: String
    ) {
        databinding.tvCourseTitle.text = courseDetail.title

        databinding.ivCover.let { ivCover ->
            Glide.with(ivCover)
                .applyDefaultRequestOptions(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .load(courseDetail.picture)
                .override(330.dp2px(),210.dp2px()).into(
                    ivCover
                )
        }
        databinding.tvStudentCount.text = "${courseDetail.student_num}人学过"
        databinding.tvHasExam.text = if(courseDetail.is_have_exam == "1") "有考试" else "无考试"

        databinding.tvHasVerification.text = if(courseDetail.verified_active)"有证书" else "无证书"
        databinding.tvPlatform.text = courseDetail.org
        databinding.tvIsFree.text = if(courseDetail.is_free) "免费" else "付费"
        databinding.tvEnrollTime.text = "学堂在线"
        databinding.tvStartTime.visibility = View.GONE
        databinding.tvOrg.visibility = View.GONE


        if (courseDetail.classroom_info?.isNotEmpty() == true) {
            classroom_info = courseDetail.classroom_info
            databinding.clCalss.visibility = View.VISIBLE
            databinding.clCalss.setOnClickListener {
                showClassChooseDialog(databinding)
            }
            bindClassRoomInfo(databinding, classRoomId)
        }
    }

    /**
     * 绑定班级信息
     */
    private fun bindClassRoomInfo(mBindingView: ActivityCourseDetailBinding,classRoomId: String) {
        if (classroom_info == null || classroom_info?.isEmpty() == true) return

//        var classRoomIndex = 0 //默认选中零
        classroom_info?.forEachIndexed { index, classroomInfo ->
            if (classroomInfo.id == classRoomId) {
                currentClassPosition = index
            }
        }

        val classRoomInfo = classroom_info?.get(currentClassPosition)

        //学时在班级详情里
        mBindingView.tvStudentTime.text = "${classRoomInfo?.duration}学时"
        mBindingView.tvHasExam.text = if(classRoomInfo?.is_have_exam == "1") "有考试" else "无考试"
        //设置班级名称和开课时间
        mBindingView.classTitle.text = classRoomInfo?.name
        mBindingView.classTime.let {
            XtCourseDetialBindings.courseDuration(
                it,
                classRoomInfo!!.class_start,
                classRoomInfo.class_end
            )
        }

        setCourseIntro(mBindingView,classRoomInfo?.about?:"")
        bindChapters(mBindingView,classRoomInfo?.chapters)
        bindStaffInfo(mBindingView,classRoomInfo?.staff)
        bindQuestionInfo(mBindingView,classRoomInfo?.qas)

        //获取课程状态
        requestCourseStatus(mBindingView,courseId,classRoomInfo?.id?:"")

        //点击报名
        mBindingView.tvClassStatus.setOnClickListener(View.OnClickListener {
            newXtClickAddRoom()
        })
    }

    /**
     * 设置课程简介内容，如果包含html标签，需要先解析html的内容
     * 如果不包含html，直接设置文本内容
     */
    fun setCourseIntro(mBindingView: ActivityCourseDetailBinding, content: String) {
        if(content.isEmpty()){
            mBindingView.tvCourseDes.visibility = View.GONE
            titleTabsPosition.remove(CourseDetailActivity.TAB_STR_DES)
            return
        }

        //先占位
        titleTabsPosition.put(CourseDetailActivity.TAB_STR_DES,0)
        //获取课程简介在布局中的位置
        mBindingView.tvCourseDes.post {
            val courseOutlinTop = mBindingView.tvCourseDes.top
            loge("tvCourseDesTop: ${courseOutlinTop}")
            titleTabsPosition.put(CourseDetailActivity.TAB_STR_DES,courseOutlinTop)
        }

        val regex = "\\<.*?>"
        val pat = Pattern.compile(regex)
        val mat = pat.matcher(content)

        if (mat.find()) {      //包含
            mBindingView.etvCourseDesc.setContent(Html.fromHtml(content).toString())
            return
        }
        mBindingView.etvCourseDesc.setContent(content)
    }

    /**
     * 绑定教师信息
     */
    private fun bindStaffInfo(mBindingView: ActivityCourseDetailBinding,staffs: List<StaffInfo>?) {
        val mActivity = mWeakReference?.get()

        if (staffs?.isNotEmpty() == true) {
            mBindingView.tvTeacher.visibility = View.VISIBLE
            mBindingView.rvTeacher.isNestedScrollingEnabled = false  //解决嵌套问题
            mBindingView.rvTeacher.layoutManager =
                LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
            val courseTeacherAdapter = CourseTeacherAdapter(staffs as ArrayList<StaffInfo>)
            courseTeacherAdapter.setOnItemClickListener { adapter, view, position ->
                staffs.get(position).let { it1 ->
                    mActivity?.let { showTeacherPop(it,it1) }
                }
            }
            mBindingView.rvTeacher.adapter = courseTeacherAdapter
            //先占位
            titleTabsPosition.put(CourseDetailActivity.TAB_STR_TEACHER,0)
            mBindingView.tvTeacher.post {
                val tvTeacherTop = mBindingView.tvTeacher.top
                loge("tvTeacherTop: ${tvTeacherTop}")
                titleTabsPosition.put(CourseDetailActivity.TAB_STR_TEACHER,tvTeacherTop)
            }
        } else {
            titleTabsPosition.remove(CourseDetailActivity.TAB_STR_TEACHER)
            mBindingView.tvTeacher.visibility = View.GONE
        }
    }

    /**
     * 绑定常见问题列表
     */
    private fun bindQuestionInfo(mBindingView: ActivityCourseDetailBinding,qas: List<Question>?) {
        val mActivity = mWeakReference?.get()
        if (qas?.isNotEmpty() == true) {
            mBindingView.tvQuestion.visibility = View.VISIBLE
            mBindingView.rvQuestion.isNestedScrollingEnabled = false  //解决嵌套问题
            mBindingView.rvQuestion.layoutManager = LinearLayoutManager(mActivity)
            mBindingView.rvQuestion.adapter = CourseQuestionAdapter(qas as ArrayList<Question>)
            //先占位
            titleTabsPosition.put(CourseDetailActivity.TAB_STR_QUESTION,0)
            mBindingView.tvQuestion.post {
                val tvQuestionTop = mBindingView.tvQuestion.top ?: 0
                loge("tvQuestionTop: ${tvQuestionTop}")
                titleTabsPosition.put(CourseDetailActivity.TAB_STR_QUESTION, tvQuestionTop)
            }
        } else {
            titleTabsPosition.remove(CourseDetailActivity.TAB_STR_QUESTION)
            mBindingView.tvQuestion.visibility = View.GONE
        }
    }

    /**
     * 绑定章节信息
     */
    private fun bindChapters(mBindingView: ActivityCourseDetailBinding,chapters: List<ChaptersBean>?) {
        val mActivity = mWeakReference?.get()
        if (chapters?.isNotEmpty() == true) {
            val combinationChapters = getChapters(chapters)
            //常见问题适配器
            mBindingView.rvChapers.isNestedScrollingEnabled = false  //解决嵌套问题
            mBindingView.rvChapers.layoutManager = LinearLayoutManager(mActivity)
            mBindingView.rvChapers.adapter =
                CourseOutLineAdapter(combinationChapters as ArrayList<ChaptersBean>)
            mBindingView.tvCourseOutline.visibility = View.VISIBLE
            //先占位
            titleTabsPosition.put(CourseDetailActivity.TAB_STR_OURLINE,0)
            mBindingView.tvCourseOutline.post {
                val courseOutlinTop = mBindingView.tvCourseOutline.top ?: 0
                loge("courseOutlinTop: ${courseOutlinTop}")
                titleTabsPosition.put(CourseDetailActivity.TAB_STR_OURLINE,courseOutlinTop)
            }
        } else {
            titleTabsPosition.remove(CourseDetailActivity.TAB_STR_OURLINE)
            mBindingView.tvCourseOutline.visibility = View.GONE
        }
    }

    private fun enterToCourseDetail() {
        val classInfo = classroom_info?.get(currentClassPosition)

        //拼接加载链接
        val link: String = generateLoadUrl()


        //赋值班级id
        val courseBean = mWeakReference?.get()?.courseDetail?.convertCourseBean()
        courseBean?.classroom_id = classInfo?.id?:""


        val put = Bundle().put(IntentParamsConstants.WEB_PARAMS_URL, link)
            .put(IntentParamsConstants.WEB_PARAMS_TITLE, classInfo?.name)
            .put(IntentParamsConstants.COURSE_PARAMS_ID, courseId)
            .put(IntentParamsConstants.PARAMS_RESOURCE_ID, courseId)
            .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, ResourceTypeConstans.TYPE_COURSE)
            .put(IntentParamsConstants.COURSE_PARAMS_DATA, courseBean)
            .put(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID, classInfo?.id ?: "")
            .put(IntentParamsConstants.COURSE_PARAMS_PLATFORM, CoursePlatFormConstants.COURSE_PLATFORM_NEW_XT)
        ARouter.getInstance().build(Paths.PAGE_COURSE_NEWXT_PLAY).with(put).navigation()
    }

    /**
     * 展示班级选择弹窗
     */
    private fun showClassChooseDialog(mBindingView: ActivityCourseDetailBinding) {
        val xtCourseClassChooseDialog = XTCourseClassChooseDialog()
        xtCourseClassChooseDialog.setData(classroom_info, currentClassPosition)
        xtCourseClassChooseDialog.setClassChooseListener(object :
            XTCourseClassChooseDialog.onClassChooseListener {
            override fun onClassChoose(classroomInfoBean: BaseClassInfo, position: Int) {
                if (currentClassPosition == position) {
                    return
                }
//                currentClassPosition = position
                if(classroomInfoBean is ClassroomInfo){
                    bindClassRoomInfo(mBindingView,classroomInfoBean.id)
                }
            }
        })
        mWeakReference?.get()?.supportFragmentManager?.let {
            xtCourseClassChooseDialog.show(it, "courseChoolseDialog")
        }
    }

    /**
     * 获取加入课程链接
     * 和时间，同新学堂查询接口
     */
    private fun requestCourseStatus(mBindingView: ActivityCourseDetailBinding,courseId:String,classRoomId: String) {
        ApiService.getRetrofit().create(CourseApi::class.java).getMoocCourseState(courseId, classRoomId)
            .compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<XtCourseStatus>>(mWeakReference?.get(), true) {
                override fun onSuccess(t: HttpResponse<XtCourseStatus>?) {
                    t?.data?.let {
                        currentStatus = it
                        setNewXTCourseClassStatus(mBindingView.tvClassStatus,it)
//                        mWeakReference?.get()?.setNewXTCourseClassStatus(it)
                        //显示课程评价
                        mWeakReference?.get()?.courseDetail?.let { courseDetail->
                            if(!courseDetail.is_appraise){          //未评价,已报名
                                if(it.status == 6 || it.status == 7 || it.status == 9){
                                    mBindingView.tvToMark.visibility = View.VISIBLE
                                }else{
                                    mBindingView.tvToMark.visibility = View.INVISIBLE
                                }
                            }else{
                                mBindingView.tvToMark.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            })
    }


    /**
     * 新学堂点击去报名按钮
     */
    open fun newXtClickAddRoom() {
        if(currentStatus == null) return
        mWeakReference?.get()?.apply {

            val classRoom = classroom_info?.get(currentClassPosition)?.id ?: ""
            LogUtil.addClickLogNew("${LogEventConstants2.T_COURSE}#${courseId}#${classRoom}", "${courseId}#${classRoom}",ResourceTypeConstans.TYPE_COURSE.toString(), "${courseDetail?.title}")

            when(currentStatus!!.status){
                1->{
                    //点击去报名
                    this.courseDetailVM.selectionNewXtCourse(
                        courseId, classRoom
                    ).observe(this, Observer {
                        this.courseDetailVM.redisCourse(courseId)
                        //刷新课程状态
                        mWeakReference?.get()?.mBindingView?.let {
                                it1 -> requestCourseStatus(it1,courseId,classroom_info?.get(currentClassPosition)?.id ?: "") }

                        //添加成功，通知学习室刷新
                        EventBus.getDefault().post(RefreshStudyRoomEvent(ResourceTypeConstans.TYPE_COURSE))

                        //加入成功 ，进入播放页面去学习
                        enterToCourseDetail()
                    })
                }
                6,7,9->{
                    this.courseDetailVM.redisCourse(courseId)
                    //已报名或回顾期，直接去学习
                    enterToCourseDetail()
                }
            }
        }
    }

    /**
     * http://xt.yuketang.cn/api/v1/lms/third_party_platform/login/
     * * ?jwt_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjEwMSwiYXZhdGFyIjoiaHR0cHM6Ly9zdGF0aWMubW9vY25kLnlrdC5pby91Y2xvdWQvbW9vY25kL2ltZy9iNDQwNTEzZWZhNmE4Y2U0NmZiNGZiYzcxYTZmZGE4My5qcGciLCJuYW1lIjoiQSh2dmdoaGpqXHUwNTA1KFx1MDBhZlx1MzE0Mlx1MDBhZlx1MDUwNSlcdTUzZWZcdTRlNTBcdTgyYjFcdTUzNDkifQ.ccp6VPR92ojw3tTAj8bK5dm6fvyeqGG0ZEWZybiM38E
     * * &provider=junzhi
     *
     * @return
     */
    fun generateLoadUrl(): String {
        val apply = currentStatus?.let {
            val link: String = it.next
            if (TextUtils.isEmpty(link)) {
                return ""
            }
            val stringBuilder = StringBuilder(link)
            stringBuilder.append("?")
            stringBuilder.append("jwt_token=")
            stringBuilder.append(it.jwt?.jwt_token)
            stringBuilder.append("&")
            stringBuilder.append("provider=")
            stringBuilder.append(it.jwt?.provider)


            //需要传递课程的封面，名称，和id
//            val xtCourseDetail = CourseDetail()
//            xtCourseDetail.setId(courseDetailBean.getId())
//            xtCourseDetail.setTitle(courseDetailBean.getTitle())
//            xtCourseDetail.setPicture(courseDetailBean.getPicture())
            stringBuilder
        }

        return apply.toString()
    }
}