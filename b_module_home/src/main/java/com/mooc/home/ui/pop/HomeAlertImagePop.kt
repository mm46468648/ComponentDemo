package com.mooc.home.ui.pop

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mooc.commonbusiness.constants.UrlConstants
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.utils.GsonManager
import com.mooc.common.utils.ScreenUtil
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.LogPageConstants
import com.mooc.commonbusiness.utils.ServerTimeManager
import com.mooc.home.R
import com.mooc.home.databinding.HomePopAlertImageBinding
import com.mooc.home.model.AlertMsgBean
import com.mooc.statistics.LogUtil

//import kotlinx.android.synthetic.main.home_pop_alert_image.view.*

/**
 * 首页图片类型弹窗
 * 按比例缩放图片
 */
class HomeAlertImagePop(var mContext: Context, var alertMsgBean: AlertMsgBean) : CenterPopupView(mContext) {

    private lateinit var inflater : HomePopAlertImageBinding
    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_alert_image
    }

    override fun onCreate() {
        super.onCreate()

        inflater = HomePopAlertImageBinding.bind(popupImplView)
        Glide.with(inflater.ivImage).asBitmap()
                .load(alertMsgBean.alert_img)
                .placeholder(R.mipmap.common_bg_cover_default)
                .into(object : CustomTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                val width = resource.width

                if(width <= 0 || resource.height <= 0){ //图片异常
                    runOnMain {
                        inflater.ivImage.setImageResource(R.mipmap.common_bg_cover_default)
                    }
                    return
                }
                val radio: Float = ScreenUtil.getScreenWidth(mContext) * 0.8f / width
                val bitmap: Bitmap? = scaleBitmap(resource, radio)
                runOnMain {
                    inflater.ivImage.setImageBitmap(bitmap)
                    inflater.ivClose.setVisibility(View.VISIBLE)
                    inflater.ivClose.setOnClickListener {
                        dismiss()
                    }

                    inflater.ivImage.setOnClickListener {
                        if(UrlConstants.APK_DOWNLOAD_PATH == alertMsgBean.link){
                            //跳转到外部浏览器
                            val uri = Uri.parse(alertMsgBean.link) //url为你要链接的地址
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            mContext.startActivity(intent)
                        }else{
//                            LogUtil.addClickLogNew("${LogEventConstants2.P_POP}#${alertMsgBean.id}",alertMsgBean._resourceId,alertMsgBean._resourceType.toString()
//                                ,alertMsgBean.title,"${LogEventConstants2.typeLogPointMap[alertMsgBean._resourceType]}#${alertMsgBean._resourceId}",)

                            val hrid = if(TextUtils.isEmpty(alertMsgBean.getSystem_message_id())) "0" else alertMsgBean.getSystem_message_id()
                            val map = hashMapOf<String,Any>(
                                "ts" to ServerTimeManager.getInstance().serviceTime
                                ,"page" to LogPageConstants.PID_HOME
                                ,"heid" to LogPageConstants.EID_YES
                                ,"hrt" to LogPageConstants.EID_POP
                                ,"hrid" to hrid
                                ,"hprt" to alertMsgBean._resourceType
                                ,"hprid" to alertMsgBean._resourceId,
                               )
                            LogUtil.postServerLog(GsonManager.getInstance().toJson(map))
                            //打开相应的资源
                            ResourceTurnManager.turnToResourcePage(alertMsgBean)
                        }

                        dismiss()
                    }
                }

            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    private fun scaleBitmap(origin: Bitmap?, ratio: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.preScale(ratio, ratio)
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
//        origin.recycle()
        return newBM
    }

}