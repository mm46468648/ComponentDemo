package com.mooc.battle.model

/**

 * @Author limeng
 * @Date 2022/12/28-10:20 上午
 */
class RankDeatilsBean(
    var self_rank_num: String? = null,
    var tournament_info: RankInfoBean? = null,
    var tournament_rank_list: ArrayList<RankListBean>? = null
) {
    class RankListBean(
        var nickname: String? = null,
        var cover: String? = null,
        var total_score: String? = null,
        var rank_num: String? = null,
    )

    class RankInfoBean(
        var id: String? = null,
        var title: String? = null,
        )

}



