package com.mooc.course.ui.deleget

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.WebView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.lxj.xpopup.XPopup
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
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.utils.HtmlUtils
import com.mooc.course.CourseApi
import com.mooc.course.R
import com.mooc.course.binding.XtCourseDetialBindings
import com.mooc.course.databinding.ActivityCourseDetailBinding
import com.mooc.course.model.*
import com.mooc.course.ui.activity.CourseDetailActivity
import com.mooc.course.ui.adapter.CourseTeacherAdapter
import com.mooc.course.ui.pop.XTCourseClassChooseDialog
import com.mooc.statistics.LogUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.course.ui.pop.BindMoocPlatformPop
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

/**
 * 中国大学Mooc详情页代理类
 */
class MoocCourseDetailDeleget(activity: CourseDetailActivity, var courseId: String) : BaseCourseDetailDeleget(){
    var currentClassPosition = 0 //当前选中班级的位置
    var mWeakReference: WeakReference<CourseDetailActivity>? = null
    var classroom_info: List<MoocClassInfo>? = null
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
        //请求绑定提示数据
        getBindTipData()
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
        databinding.tvHasExam.text = courseDetail.is_have_exam_info
        databinding.tvStudentTime.text = courseDetail.org
        databinding.tvPlatform.text = courseDetail.is_free_info
        databinding.tvHasVerification.text = courseDetail.verified_active_info
        databinding.tvEnrollTime.text = courseDetail.platform_zh
        databinding.tvStartTime.visibility = View.GONE
        databinding.tvOrg.visibility = View.GONE


        if (courseDetail.term_info?.isNotEmpty() == true) {
            classroom_info = courseDetail.term_info
            databinding.clCalss.visibility = View.VISIBLE
            databinding.clCalss.setOnClickListener {
                showClassChooseDialog(databinding)
            }
            bindMoocClassRoomInfo(databinding, classRoomId)
        }


        //先占位
        titleTabsPosition.put(CourseDetailActivity.TAB_STR_DES,0)
        //获取课程简介在布局中的位置
        databinding.tvCourseDes.post {
            val courseOutlinTop = databinding?.tvCourseDes?.top ?: 0
            loge("tvCourseDesTop: ${courseOutlinTop}")
            titleTabsPosition.put(CourseDetailActivity.TAB_STR_DES,courseOutlinTop)
        }

//        val vsDescInflate = databinding.vsCourseDesc.viewStub?.inflate()
//        val wvDes = vsDescInflate?.findViewById<WebView>(R.id.webview)
        databinding.wvDes.loadDataWithBaseURL(
            null,
            HtmlUtils.getReplaceHtml(courseDetail.new_about),
            "text/html",
            "utf-8",
            null
        )

        if (!TextUtils.isEmpty(courseDetail.new_chapters)) {
            //先占位
            titleTabsPosition.put(CourseDetailActivity.TAB_STR_OURLINE,0)
            //获取课程简介在布局中的位置
            databinding.tvCourseDes.post {
                val courseOutlinTop = databinding.tvCourseDes.top
                loge("tvCourseDesTop: ${courseOutlinTop}")
                titleTabsPosition.put(CourseDetailActivity.TAB_STR_OURLINE,courseOutlinTop)
            }

//            val vsChapterInflate = databinding.vsChapters.viewStub?.inflate()
//            val wvChapter = vsChapterInflate?.findViewById<WebView>(R.id.webview)
            databinding.wvChapter.loadDataWithBaseURL(
                null,
//                HtmlUtils.getReplaceHtml(courseDetail.new_chapters),
                courseDetail.new_chapters,
                "text/html",
                "utf-8",
                null
            )

        }

        //绑定教师信息
        bindStaffInfo(databinding,courseDetail.staff_info)

        if (!TextUtils.isEmpty(courseDetail.new_qas)) {
            databinding.tvQuestion.visibility = View.VISIBLE
            titleTabsPosition.put(CourseDetailActivity.TAB_STR_QUESTION,0)
            databinding.tvQuestion.post {
                val tvTeacherTop = databinding.tvTeacher.top
                loge("tvTeacherTop: ${tvTeacherTop}")
                titleTabsPosition.put(CourseDetailActivity.TAB_STR_QUESTION,tvTeacherTop)
            }

//            val vsQuestionInflate = databinding.vsQuestion.viewStub?.inflate()
//            val wvQuestion = vsQuestionInflate?.findViewById<WebView>(R.id.webview)
//            wvQuestion?.loadDataWithBaseURL(
            databinding.wvQuestion.loadDataWithBaseURL(
                null,
//                HtmlUtils.getReplaceHtml(courseDetail.new_qas),
                courseDetail.new_qas,
                "text/html",
                "utf-8",
                null
            )
        }


    }

    //根据这个模型是否为空,判断是否绑定过 (空:绑定过,不为空:未绑定)
    var hotListBean : HotListBean? = null
    /**
     * 获取绑定提示数据
     */
    fun getBindTipData(){
        ApiService.getRetrofit().create(CourseApi::class.java).getMoocBindMsg()
            .compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<HotListBean?>>(mWeakReference?.get(), true) {
                override fun onSuccess(t: HttpResponse<HotListBean?>?) {
                    if(!TextUtils.isEmpty(t?.data?.answer_content)){    //有内容再赋值
                        hotListBean = t?.data
                    }
                }
            })
    }

    fun postBindMsg(){
        ApiService.getRetrofit().create(CourseApi::class.java).postMoocBindMsg()
            .compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<Any>>(mWeakReference?.get(), true) {
                override fun onSuccess(t: HttpResponse<Any>?) {
                    if(t?.isSuccess == true){
                        hotListBean = null
                    }
                }

            })
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
     * 绑定中国大学慕课的班级信息
     */
    fun bindMoocClassRoomInfo(mBindingView: ActivityCourseDetailBinding, classRoomId: String) {
        if (classroom_info == null || classroom_info?.isEmpty() == true) return

        classroom_info?.forEachIndexed { index, classroomInfo ->
            if (classroomInfo.id == classRoomId) {
                currentClassPosition = index
            }
        }
        //绑定布局
        val classRoomInfo = classroom_info?.get(currentClassPosition)
        //设置班级名称和开课时间
        mBindingView.classTitle.text = classRoomInfo?.name
        mBindingView.classTime.let {
            XtCourseDetialBindings.courseDuration(
                it,
                classRoomInfo!!.class_start,
                classRoomInfo.class_end
            )
        }

        //获取课程状态
        requestCourseStatus(mBindingView,courseId,classRoomInfo!!.id)
        mBindingView.clCalss.visibility = View.VISIBLE
        //点击报名
        mBindingView.tvClassStatus.setOnClickListener(View.OnClickListener {
            clickAddRoom()
        })
    }

    /**
     * 点击去上课
     */
    private fun clickAddRoom() {
        if(currentStatus == null) return //
        mWeakReference?.get()?.apply {

            val classRoomID = classroom_info?.get(currentClassPosition)?.id ?: ""
            LogUtil.addClickLogNew("${LogEventConstants2.T_COURSE}#${courseId}#${classRoomID}", "${courseId}#${classRoomID}",ResourceTypeConstans.TYPE_COURSE.toString(), "${courseDetail?.title}")

            when(currentStatus!!.status){
                1->{
                    //点击去报名
                    this.courseDetailVM.selectionNewXtCourse(
                        courseId, classRoomID
                    ).observe(this, Observer {
                        this.courseDetailVM.redisCourse(courseId)
                        //刷新课程状态
                        mWeakReference?.get()?.mBindingView?.let {
                                it1 -> requestCourseStatus(it1,courseId,classroom_info?.get(currentClassPosition)?.id ?: "") }

                        //添加成功，通知学习室刷新
                        EventBus.getDefault().post(RefreshStudyRoomEvent(ResourceTypeConstans.TYPE_COURSE))

                        //检测是否绑定过中国大学MOOC平台账号
                        if(!checkBindMoocPlatform()) return@Observer
                        //加入成功 ，进入播放页面去学习
                        enterToCourseDetail()
                    })
                }
                6,7,9->{
                    //检测是否绑定过中国大学MOOC平台账号
                    if(!checkBindMoocPlatform()) return

                    this.courseDetailVM.redisCourse(courseId)
                    //已报名或回顾期，直接去学习
                    enterToCourseDetail()
                }
            }
        }

    }

    private fun enterToCourseDetail() {


        val link = currentStatus?.link
        val classInfo = classroom_info?.get(currentClassPosition)
        // 需要传递课程的封面，名称，和id
        val put = Bundle().put(IntentParamsConstants.WEB_PARAMS_URL, link)
            .put(IntentParamsConstants.WEB_PARAMS_TITLE, classInfo?.name ?: "")
            .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, ResourceTypeConstans.TYPE_COURSE)
            .put(IntentParamsConstants.PARAMS_RESOURCE_ID, "$courseId")
            .put(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID, "${classInfo?.id}")
            .put(IntentParamsConstants.COURSE_PARAMS_PLATFORM, CoursePlatFormConstants.COURSE_PLATFORM_MOOC)
        ARouter.getInstance().build(Paths.PAGE_WEB_RESOURCE).with(put).navigation()
    }

    /**
     * 检测是否绑定过中国大学MOOC平台账号
     */
    fun checkBindMoocPlatform():Boolean{
        if(hotListBean != null){
            showBindDialog()
        }
        return hotListBean == null
    }

    /**
     * 展示绑定平台账号的弹窗
     */
    fun showBindDialog(){
        mWeakReference?.get()?.let { actvity ->
            val bindMoocPlatformPop = BindMoocPlatformPop(actvity, hotListBean){ state->
                enterToCourseDetail()
                if(state){
                    postBindMsg()
                }
            }
            XPopup.Builder(actvity)
                .maxWidth(300.dp2px())
                .asCustom(bindMoocPlatformPop)
                .show()
        }
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
                currentClassPosition = position

                //根据班级信息，重新设置一下相关信息（班级名称，和开课时间）
                mBindingView.classTitle.text = classroomInfoBean.name
                mBindingView.classTime.let {
                    XtCourseDetialBindings.courseDuration(
                        it,
                        classroomInfoBean.class_start,
                        classroomInfoBean.class_end
                    )
                }
                //重新获取课程状态
                requestCourseStatus(mBindingView, courseId, classroomInfoBean.id)


//                getXtCourseStatus()
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
    private fun requestCourseStatus(
        mBindingView: ActivityCourseDetailBinding,
        courseId: String,
        classRoomId: String
    ) {
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


}