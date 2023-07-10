package com.mooc.commonbusiness.utils.share

import android.content.Context
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.pop.studyproject.MedalPop
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.model.ShareScoreResponse
import com.mooc.commonbusiness.pop.IncreaseScorePop
import org.json.JSONObject

/**
 * 分享积分工具类
 */
class ShareScoreUtil {

    companion object {

        fun getShareScore(context: Context,shareType:String = "") {
            //显示勋章的范围
            val range = arrayOf(2, 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000)

            val jsonObject = JSONObject()
            if(shareType.isNotEmpty()){
                jsonObject.put("share_type", shareType)
            }

            HttpService.commonApi.getShareScore(RequestBodyUtil.fromJson(jsonObject))
                    .compose(RxUtils.applySchedulers())
                    .subscribe(object : BaseObserver<ShareScoreResponse>(context) {
                        override fun onSuccess(t: ShareScoreResponse?) {
                            if (t == null) return

                            val code = t.share_code
                            val msg = t.share_message
                            if (code==0&&msg != null) {
                                toast(msg)
                            }
                            val img = t.share_medal_img
                            var inCount: Boolean = false
                            range.forEach {
                                if (it == t.share_count) {
                                    inCount = true
                                    return@forEach
                                }
                            }
                            if (inCount) {
                                if ((code==0||code==1)&&!img.isNullOrEmpty()) { //显示勋章
                                    postShowDelely {
                                        val medalpop = MedalPop(context, img)
                                        XPopup.Builder(context)
                                            .asCustom(medalpop)
                                            .show()
                                    }

                                }
                            }else {
                                if (code==1&&t.share_score>0) {// 显示分享加积分  禁成长
                                    postShowDelely {
//                                        val scorePop = ShowScorePop(context,t.share_score,0 )
                                        val scorePop = IncreaseScorePop(context,t.share_score,0 )
                                        XPopup.Builder(context)
                                            .asCustom(scorePop)
                                            .show()
                                    }
                                }
                            }

                        }
                    })
        }

        /**
         * 点击返回需要延迟500毫秒再显示
         */
        fun postShowDelely(function:(()->Unit)){
            runOnMainDelayed(1000) {
                function.invoke()
            }
        }
    }
}