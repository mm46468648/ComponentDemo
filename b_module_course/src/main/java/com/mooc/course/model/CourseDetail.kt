package com.mooc.course.model

//import com.mooc.commonbusiness.model.search.ChaptersBean
import android.text.TextUtils
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.model.search.CourseBean
import java.io.Serializable

/**
 * 学堂课程详情数据模型
 * (新学堂可以复用)
 */
data class CourseDetail(
    var id: String = "",
    var course_id: String = "",      //xt的courseid，用于调用学堂接口，包括智慧树课程章节列表也是用这个查询
    val title: String = "",
    val picture: String = "",
    var org: String = "",
    val about: String = "",
    val student_num: Int = 0,
    val is_free: Boolean = false,
    var is_free_info: String = "",//是否收费文案（付费证书，免费证书）
    val platform: Int = -1,
    var platform_zh: String = "",
    val classroom_info: List<ClassroomInfo>? = null,
    var end: String = "", //结束时间
    var start: String = "",//课程开始时间
    var enrollment_start: String = "",
    val enrollment_end: String = "",

    var video_duration: Double = 0.0, //老学堂学时字段
    var chapters: List<ChaptersBean>? = null, //老学堂课程的章节列表在这个数据结构中
    var staffs: List<StaffInfo>? = null,
    var qas: List<Question>? = null,
    var status: Int = 0,   //老学堂开课状态
    var course_time_message: String = "",
    var course_time_status: Int = -1, //不知道干啥的
    var from_xuetangx: Boolean = false,  //学堂的课，包括新老学堂
    val check_enrollment: Int = 0, //不知道干啥的
    val is_enrolled: Boolean = false, //是否已加入学习室
    var link: String = "",
    var course_type: Int = 0, //1、自主模式  0、随堂模式
    var is_have_exam_info :String = "", // 是否有考试文案
    var verified_active_info :String = "", // 是否有证书文案

    var grade_policy: GradePolicy? = null,  //考核模块，头部，考核相关信息使用


        //新学堂课程详情里的字段
    var duration:String = "",  //新学堂，学时字段
    var is_have_exam:String = "",  //新学堂，是否有考试 1，有 0没有
    var verified_active:Boolean = false,  //新学堂，是否有证书

        //中国大学mooc详情的字段
    var new_about: String = "",  //简介使用富文本格式
    var new_qas: String = "",  //问题使用富文本格式
    var new_chapters: String = "",  //章节使用富文本格式
    var term_info: List<MoocClassInfo>? = null,
    var staff_info: List<StaffInfo>? = null,

    var appraise_score : Float, //课程评分
    var is_appraise: Boolean, //是否已经评分
    var sub_users:ArrayList<UserInfo>? = null

) {
    fun convertCourseBean(): CourseBean {
        val courseBean = CourseBean()
        if(!TextUtils.isEmpty(this.course_id)){
            courseBean.course_id = this.course_id
        }
        courseBean.id = this.id
        courseBean.title = this.title
        courseBean.picture = this.picture
        courseBean.platform = this.platform
        return courseBean
    }
}

/**
 *
 */
class ClassroomInfo : MoocClassInfo(){

    val chapters: List<ChaptersBean>? = null
    val staff: List<StaffInfo>? = null
    val qas: List<Question>? = null
}

/**
 * 中国大学mooc班级信息
 */
open class MoocClassInfo : BaseClassInfo() {
//    var id: String = ""
//    val name: String = ""
//    val class_start: String = ""
//    val class_end: String = ""

    val picture: String = ""
    val sku_id: Int = 0
    val signup_start: String = ""
    val signup_end: String = ""
    val about: String = ""
    val classroom_id: Int = 0
    val verified_active: Boolean = false
    val is_have_exam: String = ""
    val max_study_days: Int = 0
    val duration: String = "0"
    val learning_duration: String = ""
    val course_duration: String = ""
    val created_time: String = ""
    val updated_time: String = ""
}

open class BaseClassInfo{
    var id: String = ""
    val name: String = ""
    val class_start: String = ""      //有可能返回null，所有用String
    val class_end: String = ""
}

/**
 * 考核信息
 * 屎一样的数据结构，就为了这么几个字段
 */
data class GradePolicy(var GRADE_CUTOFFS: GRADECUTOFFSBean? = null,
                       var RAW_GRADER: List<RAWGRADERBean>? = null) : Serializable

data class GRADECUTOFFSBean(var min_count: String = "0") : Serializable

data class RAWGRADERBean(
        var min_count: Int = 0,
        val weight: Double = 0.0,
        val type: String = "",
        val drop_count: Int = 0,
        val short_label: String? = null
) : Serializable