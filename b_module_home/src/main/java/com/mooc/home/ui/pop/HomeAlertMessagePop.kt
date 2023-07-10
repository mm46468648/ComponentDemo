package com.mooc.home.ui.pop

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.utils.HtmlUtils
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.LogPageConstants
import com.mooc.commonbusiness.utils.ServerTimeManager
import com.mooc.home.R
import com.mooc.home.databinding.HomePopAlertMessageBinding
import com.mooc.home.model.AlertMsgBean
import com.mooc.statistics.LogUtil

//import kotlinx.android.synthetic.main.home_pop_alert_message.view.*

/**
 * 首页消息类型弹窗
 */
class HomeAlertMessagePop(var mContext: Context, var alertMsgBean: AlertMsgBean,var sigleButton:Boolean = false) : CenterPopupView(mContext) {

    private lateinit var inflater: HomePopAlertMessageBinding
    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_alert_message
    }

    override fun onCreate() {
        super.onCreate()
        inflater = HomePopAlertMessageBinding.bind(popupImplView)
        inflater.tvTitle.text = alertMsgBean.alert_title

        if(alertMsgBean.alert_desc.isNotEmpty()){
            val replace = alertMsgBean.alert_desc.replace("<img", "<img width=\"100%\"")
            val fromHtml = HtmlUtils.getReplaceHtml(replace)
            inflater.webDes.loadDataWithBaseURL(null, fromHtml, "text/html", "UTF-8", null)
        }
        //设置描述信息
        inflater.tvCancel.setOnClickListener {
            clickPoint()
            dismiss()
        }
        inflater.tvOk.setOnClickListener {
            onConfirmClick()
        }

        if(sigleButton){
            inflater.tvCancel.visibility = View.GONE
            inflater.tvOk.visibility = View.GONE
            inflater.tvSingleConfirm.visibility = View.VISIBLE
            inflater.tvSingleConfirm.setOnClickListener {
                onConfirmClick()
            }
        }
    }

    fun onConfirmClick(){
//        LogUtil.addClickLogNew("${LogEventConstants2.P_POP}#${alertMsgBean.id}",alertMsgBean._resourceId,alertMsgBean._resourceType.toString()
//            ,alertMsgBean.title,"${LogEventConstants2.typeLogPointMap[alertMsgBean._resourceType]}#${alertMsgBean._resourceId}",)
        clickPoint(false)
        ResourceTurnManager.turnToResourcePage(alertMsgBean)


        dismiss()
    }

    /**
     * 是否是cancle事件
     */
    fun clickPoint(isCancel : Boolean = true){
        val hrid = if(TextUtils.isEmpty(alertMsgBean.getSystem_message_id())) "0" else alertMsgBean.getSystem_message_id()
        val heid = if(isCancel) LogPageConstants.EID_NO else LogPageConstants.EID_YES
        val map = hashMapOf<String,Any>(
            "ts" to ServerTimeManager.getInstance().serviceTime
            ,"page" to LogPageConstants.PID_HOME
            ,"heid" to heid
            ,"hrt" to LogPageConstants.EID_POP
            ,"hrid" to hrid
            ,"hprt" to alertMsgBean._resourceType
            ,"hprid" to alertMsgBean._resourceId,
        )
        LogUtil.postServerLog(GsonManager.getInstance().toJson(map))
    }
}