package com.mooc.commonbusiness.model

class ActivityTaskBean {
    public val start_time: Long = 0//任务开始时间
    public val end_time: Long = 0//任务结束时间
    public val apply_start_time: Long = 0//报名开始时间
    public val apply_end_time: Long = 0//任务结束时间
    public val is_title = true
    public val font_color: String = ""
    public val background_color: String = ""
    public val url: String = ""
    public val title: String = ""
    public val share_desc: String = ""
    public val share_button_picture: String = ""
    public val share_status = 0  //1代表需要分享
    public val share_picture: String = ""
    public val share_link: String = ""
    public val share_title: String = ""
}