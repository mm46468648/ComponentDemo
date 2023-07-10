package com.mooc.commonbusiness.model.sharedetail

class ShareDetailModel {
    var share_about: String = ""              //原来的share_desc, 但是改为了对象类型，所以更改为share_about
    var share_desc: String = ""              //3.4.4新版的share_desc, 又使用了这个字段
    var share_status: String = ""           //如果是-1代表没有分享
    var source_type: String = ""
    var share_title: String = ""
    var share_link: String = ""
    var weixin_url: String = ""          //3.4.4新版含有拉新功能的新分享地址
    var share_picture: String = ""
    var source_id: String = ""
    var is_enroll: String = ""         // 0 false 1 true

    var is_enrolled: Boolean = false         // 0 false 1 true这只老接口使用的一个字段

}