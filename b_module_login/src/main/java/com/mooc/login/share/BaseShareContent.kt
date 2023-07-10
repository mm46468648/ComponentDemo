package com.intelligent.reader.module.share.api.bean


/**
 * 分享内容基类
 * Created by Administrator on 2019/1/11.
 */

abstract class BaseShareContent {
    /**
     * 分享方式
     * @return
     */
    abstract val shareWay: Int

    /**
     * 分享内容
     * @return
     */
    abstract val content: String

    /**
     * 分享标题
     * @return
     */
    abstract val title: String

    /**
     * 分享链接
     * @return
     */
    abstract val url: String

    /**
     * 分享图片
     * @return
     */
    abstract var pictureResource: String

//    /**
//     * 默认的分享图片（当getPictureResource调用失败后调用的本地资源，微信专用，QQ不存在这个东西）
//     * @return
//     */
//    abstract val defaultPictureResource: Int

    /**
     * 小程序页面路径
     * @return
     */
    abstract fun miniProgramPagePath(): String
}
