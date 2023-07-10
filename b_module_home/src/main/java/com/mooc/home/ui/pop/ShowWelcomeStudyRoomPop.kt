package com.mooc.home.ui.pop

import android.content.Context
import android.view.LayoutInflater
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.home.R
import com.mooc.home.databinding.HomeWelcomePopStudyRoomBinding

//import kotlinx.android.synthetic.main.home_welcome_pop_study_room.view.*

/**
显示欢迎会回到学习室的dialog
 * @Author limeng
 * @Date 2021/4/27-5:04 PM
 */
class ShowWelcomeStudyRoomPop(context: Context,var score: String?,var num: String) : CenterPopupView(context) {

    private lateinit var inflater: HomeWelcomePopStudyRoomBinding
    override fun getImplLayoutId(): Int {
        return R.layout.home_welcome_pop_study_room
    }

    override fun onCreate() {
        super.onCreate()

        inflater = HomeWelcomePopStudyRoomBinding.bind(popupImplView)
        inflater.pointTv.setText("+$score")
        inflater.resTv.setText("您昨天在军职在线APP学习了" + num + "个资源")
        inflater.totalTv.setText("共获得" + score + "积分")
    }
}