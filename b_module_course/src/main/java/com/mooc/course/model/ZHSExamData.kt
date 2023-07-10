package com.mooc.course.model

data class ZHSExam(
        var exam_page_link :String? = "",
        var exam_list: List<ZHSExamData>,
        var user_course_score: UserCourseScoreBean?,
        var course_score_rule: CourseScoreRuleBean?
)

data class ZHSExamData(
        var examName: String = "",    //名字
        var totalScore: String = "",   //总分
        var score: String = "",     //得分
        val type: String = "",     //类型//"2"考试  其他 作业
        var isSub: String = "",     //是否已提交//0 未提交 1 已提交",

        val studentExamId: String = "",
        val examId: String = ""

)

data class UserCourseScoreBean(
        var testAccessScore: Int = 0,
        val courseId: Int = 0,
        val userId: Int = 0,
        val totalAccessScore: Int = 0,
        val examAccessScore: Int = 0,
        val videoAccessScore: Int = 0,
        val id: Int = 0
)

data class CourseScoreRuleBean(
        var passScore: Int = 0,
        val videoViewProPercent: String = "",
        val courseId: Int = 0,
        val classRoomTestPercent: String = "",
        val finalExamPercent: String = "",
        val totalScore: Int = 0,
        val id: Int = 0

)