package com.mooc.commonbusiness.model

class TestVolume {
    var cert_link : String = ""            //证书链接
    var exam_status : String = ""          //1无法考试 2可以参加考试 3考完试
    var score : String = ""                //考试分数
    var pdf_link : String = ""             //证书链接
    var certificate_id : String = ""       //证书id
    var test_paper_title : String = ""     //测试卷名称
    var certificate_status : String = ""    // 0不可申请证书 1可申请 2已申请不可查看 3可查看
    var test_paper_id : String = ""        //测试卷id
    var test_paper_url : String = ""        //测试卷地址
}