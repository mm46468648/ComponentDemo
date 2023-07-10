package com.mooc.battle.ui.adapter

import android.widget.RatingBar
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.battle.R
import com.mooc.battle.model.GameLevelResponse

class GameLevelChooseAdapter(data: ArrayList<GameLevelResponse.LevelInfo>) :
    BaseQuickAdapter<GameLevelResponse.LevelInfo, BaseViewHolder>(
        R.layout.item_game_level_choose, data
    ) {
    override fun convert(baseViewHolder: BaseViewHolder, gameLevel: GameLevelResponse.LevelInfo) {

        baseViewHolder.setText(R.id.tvLevelTitle, gameLevel.level_title)

        val ratingBar = baseViewHolder.getView<RatingBar>(R.id.ratingBar)
        ratingBar.setIsIndicator(true)
        ratingBar.scaleX = 0.5f
        ratingBar.numStars = gameLevel.upgrade_stars
        ratingBar.rating = gameLevel.stars.toFloat()
    }
}