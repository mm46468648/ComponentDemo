package com.intelligent.reader.module.share.api.constans

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX

/**
 * 分享常量
 * Created by Administrator on 2019/1/11.
 */

object ShareConstant {
    val ACTION_SHARE_RESULT_WEIXIN = -0X01001A//微信分享结果
    val ERROR_WITHOUT_INIT = -10010//没有初始化
    val ERROR_WITHOUT_CLIENT = -10086//没有安装客户端
    val ERROR_WITHOUT_VERSION_SUPPORT = -10011//版本不支持
    val THUMB_SIZE = 100//微信小logo大小
    val THUMB_SIZE_LIMIT = 32//微信缩略图大小限制32KB以内
    val SHARE_WAY_TEXT = 1   //文字
    val SHARE_WAY_PICTURE = 2 //图片
    val SHARE_WAY_WEBPAGE = 3  //链接
    val SHARE_WAY_MEDIA = 4 //媒体
    val SHARE_WAY_MINI_PROGRAM = 5 //小程序 TODO（注意：如果选中该类型，则不会有文字图片链接媒体的区分！）
    val SHARE_TYPE_WECHAT_SESSION = SendMessageToWX.Req.WXSceneSession  //微信会话
    val SHARE_TYPE_WECHAT_FRENDS_GROUP = SendMessageToWX.Req.WXSceneTimeline //微信朋友圈
    val SHARE_TYPE_WECHAT_FAVORITE = SendMessageToWX.Req.WXSceneFavorite  //微信收藏
    val SHARE_TYPE_QQ_SESSION = 6//QQ会话
    val SHARE_TYPE_QQ_QZONE = 7//QQ空间
}
