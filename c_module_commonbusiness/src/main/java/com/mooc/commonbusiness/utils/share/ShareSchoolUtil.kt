package com.mooc.commonbusiness.utils.share

import android.content.Context
import android.text.TextUtils
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.model.ShareScoreResponse
import com.mooc.commonbusiness.pop.IncreaseScorePop
import org.json.JSONObject

/**
 * 分享到学友圈工具类
 */
class ShareSchoolUtil {

    companion object {
        const val KEY_RESOURCE_TYPE = "resource_type"
        const val KEY_RESOURCE_ID = "resource_id"
        const val KEY_RESOURCE_OTHER = "other_data"
        fun postSchoolShare(context: Context, resourceType: String, resourceId: String, otherData: String? = "") {
            val jsonObject = JSONObject()
            jsonObject.put(KEY_RESOURCE_TYPE, resourceType)
            jsonObject.put(KEY_RESOURCE_ID, resourceId)
            jsonObject.put(KEY_RESOURCE_OTHER, otherData)

            HttpService.commonApi.postSchoolShare(RequestBodyUtil.fromJson(jsonObject))
                    .compose(RxUtils.applySchedulers())
                    .subscribe(object : BaseObserver<ShareScoreResponse>(context) {
                        override fun onSuccess(t: ShareScoreResponse?) {
                            if (t == null) return
                            if (t.code != 1) {
                                toast(t.message)
                                return
                            }
                            if (t.score != 1) {
                                var message = t.message
                                if(!TextUtils.isEmpty(t.share_message)){
                                    message += t.share_message
                                }
                                toast("$message")
                                return
                            }
                            // 显示积分弹窗
//                            val scorePop = ShowScorePop(context,t.score,0 )
                            val scorePop = IncreaseScorePop(context,t.score,0 )
                            XPopup.Builder(context)
                                    .asCustom(scorePop)
                                    .show()
                        }
                    })
        }

        /**
         * 第二种直接传递jsonObject的方式
         */
        fun postSchoolShare(context: Context, jsonObject : JSONObject) {
            HttpService.commonApi.postSchoolShare(RequestBodyUtil.fromJson(jsonObject))
                .compose(RxUtils.applySchedulers())
                .subscribe(object : BaseObserver<ShareScoreResponse>(context) {
                    override fun onSuccess(t: ShareScoreResponse?) {
                        if (t == null) return
                        if (t.code != 1) {
                            toast(t.message)
                            return
                        }
                        if (t.score != 1) {
                            toast(t.message + t.share_message)
                            return
                        }
                        // 显示积分弹窗
//                        val scorePop = ShowScorePop(context,t.score,0 )
                        val scorePop = IncreaseScorePop(context,t.score,0 )
                        XPopup.Builder(context)
                            .asCustom(scorePop)
                            .show()
                    }
                })
        }

        /**
         *
         *  requestData.put("title", strTitle);
            requestData.put("url", strUrl);
            requestData.put("resource_type", String.valueOf(type));
            requestData.put("source", String.valueOf(postStudyBean.getSource()));
            requestData.put("status", "true");
         */
        fun getShareCircleData(requsetBodyJsonStr:String){

        }
    }
}