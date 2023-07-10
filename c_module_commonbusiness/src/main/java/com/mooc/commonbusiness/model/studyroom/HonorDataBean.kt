package com.mooc.commonbusiness.model.studyroom

/**
 * 证书，和勋章都可以复用
 */
class HonorDataBean {
    /**
     * apply_credential_link : /commercial/course-v1:TsinghuaX+00612643X+sp/certificate/apply
     * credential_flow : no_applied
     * display_name : 大唐兴衰（自主模式）
     * honor_type : 普通证书
     * linked_in_url : http://www.linkedin.com/profile/add?_ed=0_W5EWw0XLLCSxjR9BrKmNSzvXZj5oe845OnqPSmUhWEVR_C4OuQTleAHHnRBjRMFOaSgvthvZk7wTBMS3S-m0L6A6mLjErM6PJiwMkk6nYZylU7__75hCVwJdOTZCAkdv&pfCertificationName=%E3%80%8A%E5%A4%A7%E5%94%90%E5%85%B4%E8%A1%B0%EF%BC%88%E8%87%AA%E4%B8%BB%E6%A8%A1%E5%BC%8F%EF%BC%89%E3%80%8B%E7%BB%93%E8%AF%BE%E8%AF%81%E6%98%8E&pfCertificationUrl=http%3A%2F%2Fwww.xuetangx.com%2Fverify%2FpIZcA62W_MG&pfLicenseNo=pIZcA62W_MG&pfCertStartDate=201912&source=o
     * credential_type : paper
     * study_end :
     * honor_org : ["学堂在线"]
     * credential_status :
     * download_url : /download_credential/pIZcA62W_MG.pdf
     * credential_process_link : /commercial/course-v1:TsinghuaX+00612643X+sp/certificate/grant/process
     * credential_id : pIZcA62W_MG
     * about_link : /courses/course-v1:TsinghuaX+00612643X+sp/about
     * src_url : http://storage.xuetangx.com/public_assets/xuetangx/credential/thumbnail/pIZcA62W_MG_1080.jpg
     * honor_date : 2019-12-11
     * course_id : course-v1:TsinghuaX+00612643X+sp
     * study_start : 2017-06-01T00:00:00Z
     * paper_src_url : /commercial/course-v1:TsinghuaX+00612643X+sp/certificate/preview_paper
     * thumbnail : /asset-v1:TsinghuaX+00612643X+sp+type@asset+block@1848992459.jpg
     */
    var apply_credential_link: String = ""
    var credential_flow: String = ""
    var display_name: String = ""
    var honor_type: String = ""
    var linked_in_url: String = ""
    var credential_type: String = ""
    var study_end: String = ""
    var credential_status: String = ""
    var download_url: String = ""
    var credential_process_link: String = ""
    var credential_id: String = ""
    var about_link: String = ""
    var src_url: String = ""
    var honor_date: String = ""
    var course_id: String = ""
    var study_start: String = ""
    var paper_src_url: String = ""
    var thumbnail: String = ""
    var honor_org: List<String>? = null
    var id = 0
    var title: String = ""
    var link: String = ""
    var study_evaluate: String = ""
    var pdf_link: String = ""
    var apply_status: String = ""
    var type: String = ""
}