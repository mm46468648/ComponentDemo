package com.mooc.my.pop

import android.content.Context
import android.view.LayoutInflater
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.my.R
import com.mooc.my.databinding.MyPopCheckinScoreBinding
import com.mooc.my.model.CheckInDataBean
//import kotlinx.android.synthetic.main.my_pop_checkin_score.view.*

/**
 * 签到得分弹窗
 */
class CheckInScorePop(context: Context,var checkInDataBean: CheckInDataBean,var onConfirm:(()->Unit)? = null) : CenterPopupView(context) {

    lateinit var inflater: MyPopCheckinScoreBinding
    override fun getImplLayoutId(): Int {

        return R.layout.my_pop_checkin_score
    }

    override fun onCreate() {
        super.onCreate()
        inflater = MyPopCheckinScoreBinding.bind(popupImplView)
        val checkInScore = checkInDataBean.random_score + checkInDataBean.extra_score + checkInDataBean.special_score
        var message = "恭喜你获得${checkInScore}积分"
        if(checkInDataBean.extra_score>0){
            message += "，${checkInDataBean.extra_score}积分为额外奖励"
        }else if(checkInDataBean.special_score > 0){
            message += "，${checkInDataBean.special_score}积分为八一特别积分。"
        }
        //只显示一个，其中
        message = message.replaceFirst(",",",其中")
        inflater.tvMessage.text = message

        inflater.tvOk.setOnClickListener {
            dismiss()
            onConfirm?.invoke()
        }
    }
}