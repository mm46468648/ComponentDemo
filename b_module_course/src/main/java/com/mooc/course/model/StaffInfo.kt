package com.mooc.course.model

/**
 * 职业信息数据模型
 *
 * /**
 * id : 57
 * name : 管晓宏
 * avatar : http://storage.xuetangx.com/moocnd/course-v1:TsinghuaX+60250101X+sp/staff/20727/tmp.jpg
 * staff_org : TsinghuaX
 * about : 管晓宏，博士，教授，博士导师；国家杰出青年基金获得者，长江学者特聘教授，IEEE Fellow；清华大学学士、硕士，美国康涅狄格大学博士，哈佛大学访问科学家；曾获得国际学术成就奖、国际最佳论文奖、国家自然科学二等奖等学术奖励；现任国务院学位委员会学科评议组成员，国际期刊IEEE Transactions on Smart Grid编辑。
*/
 */
data class StaffInfo(
        var id: Int = 0,
        val name: String = "",
        val avatar: String = "",
        val staff_org: String = "",
        val about: String? = ""
)