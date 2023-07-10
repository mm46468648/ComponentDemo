package com.mooc.course.model

/**
 * {
 * "status": 4,//课程状态
 * "jwt":"",
 * "link": null,//跳转的h5页面（请求时需要添加参数）
 * "class_end": "2020-01-12T07:59:59Z",//结课时间
 * "signup_end": "2020-01-12T07:59:59Z",//选课结束时间
 * "signup_start": "2019-08-31T08:00:00Z",//选课开始时间
 * "class_start": "2019-09-08T16:00:00Z",//开课时间
 * "days":"4324"//回顾学习剩余时间     秒数
 * }
 * SIGNUP_READY = 1  # 1立即报名--报名--学习页
 * SIGNUP_NOT_START = 2  # 2报名未开始
 * SIGNUP_END = 3  # 3报名已结束
 * CLASS_END = 4  # 4已结课 XXXX结课
 * NOT_READY = 5  # 5不可报名
 * LOOK_BACK = 6  # 回顾学习
 * CLASS_END_END = 7  # 已结课（回顾期已过）
 * CLASS_NOT_START = 8  # 已报名，xxxx开课
 * READY = 9  # 已报名，去学习
 * CLASS_END_NO_LOOK_BACK = 10  # 已报名，xxxx已结课（没有回顾期）
 */
/**
 * status : 1
 * link : https://next.xuetangx.com/api/v1/lms/third_party_platform/login/?next=https://xt.yuketang.cn/moocnd/learn/product_20200227/course_20200227/14486
 * class_end :
 * signup_end :
 * jwt : {"jwt_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjc0MiwiYXZhdGFyIjoiaHR0cDovL3N0b3JhZ2UueHVldGFuZ3guY29tL21vb2NuZC9pbWcvZGVlNGFkM2U3NDRjZmQ2ZGZmMTZlMjU0NDc2MDk5MjAuanBnIiwibmFtZSI6Ilx1NGUwYlx1NGUwMFx1N2FkOVx1NWU3OFx1Nzk4Zlx1OGZkOFx1NTk3ZCJ9.R4chYvVGnWh1MnX6KaODdwyBbxNBaMzwgdZ2jtzznXY","provider":"moocnd"}
 * signup_start : 2019-06-19T08:00:00Z
 * class_start : 2019-06-19T08:00:00Z
 * days :
 */
class XtCourseStatus {

    var status = 0
    var link: String= ""
    var class_end //时间戳格式,如果为空，则为永久开课
            : Long?= 0
    var signup_end //时间戳(秒s)格式
            :  Long= 0
    var jwt: JwtBean?= null
    var signup_start //时间戳(秒s)格式
            : Long= 0
    var class_start //时间戳(秒s)格式
            : Long= 0
    var next: String= ""
    var days //回顾学习剩余天数秒值，如果为空，或者为0，则为永久回顾
            : Long?= 0

    /**
     * 拼接新学堂课程的时候需要
     */
    class JwtBean {
        /**
         * jwt_token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjc0MiwiYXZhdGFyIjoiaHR0cDovL3N0b3JhZ2UueHVldGFuZ3guY29tL21vb2NuZC9pbWcvZGVlNGFkM2U3NDRjZmQ2ZGZmMTZlMjU0NDc2MDk5MjAuanBnIiwibmFtZSI6Ilx1NGUwYlx1NGUwMFx1N2FkOVx1NWU3OFx1Nzk4Zlx1OGZkOFx1NTk3ZCJ9.R4chYvVGnWh1MnX6KaODdwyBbxNBaMzwgdZ2jtzznXY
         * provider : moocnd
         */
        var jwt_token: String= ""
        var provider: String= ""

    }
}