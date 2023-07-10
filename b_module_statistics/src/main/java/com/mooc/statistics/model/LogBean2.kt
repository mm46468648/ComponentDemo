package com.mooc.statistics.model

import com.mooc.common.utils.NetUtils
import com.mooc.common.utils.SystemUtils
import com.mooc.commonbusiness.constants.ChannelConstants
import com.mooc.commonbusiness.global.GlobalsUserManager

/**
 * 新版本打点模型
 * 需求文档：
 * https://www.tapd.cn/66836626/prong/stories/view/1166836626001047694
 */
class LogBean2 : Cloneable {
    var page: String = ""
    var to: String = ""
    var event: String = ""         //日志类型，onClick

    //etype
    //2（新增字段）字符串类型，这里有两种情况：
    // 1. 针对具体资源的点击，跟资源类型对应（比如2是课程 5是电子书之类的）
    // 2. 当点击的是页面的一些筛选项时，用约定的字符串填写，入快捷入口qs、最顶上的导航tab、下面的分类cate，banner页ban，icon独立图标，qd学习清单；
    // 目前先打这些，后续逐步完善；
    var etype: String = ""

    //element:
    //1/ 课程id/课程id#班级id
    //调整了字段内容）这里有两种情况：
    //1. 针对具体资源的点击，对于带班级的课程特殊情况 使用 课程id#班级id，其他为资源id；
    //2. 当点击的是页面的一些筛选项时，表示的是对应的序号，比如12个快接入口的顺序号1-12
    var element: String = ""

    // 目前该版本不需要该字段，这里的想法是想记录当前资源点击时的筛选状态，比如 音频课#有声书#最新#侦察连 这样的记录
//    var block: String = ""
    //name（新增字段）对应的中文名称 防止乱换顺序导致无法映射
    var name: String = ""

    //公共字段
    var uid: String = GlobalsUserManager.uid            //用户id
    var channel: String = ChannelConstants.channelName
    var net: String = NetUtils.getNetworkType()
    var ip: String = NetUtils.getLocalIpAddress()  //ip
    var host: String = "android"
    var model: String = SystemUtils.getDeviceModel()        //机型
    var timestamp: Long = System.currentTimeMillis()
    var version: String = SystemUtils.getVersionName()


    public override fun clone(): LogBean2 {
        return super.clone() as LogBean2
    }
}