package com.mooc.commonbusiness.utils.incpoints

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.pop.IncreaseScorePop
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

/**
首次阅读增加资源积分
 * @Author limeng
 * @Date 2021/2/25-5:26 PM
 */
class FirstAddPointManager {

    companion object : LifecycleObserver {

        lateinit var launchJob: Job

        /**
         * 增加资源积分实际方法
         * @param type 资源类型
         * @param url 一般是资源id
         * @param deley 有的页面需要延迟
         */
        fun startAddFirstPointsTask(
                lifecycleOwner: Activity?,
                type: String,
                url: String, deley: Long = 0
        ) {
            val requestData = JSONObject()
            try {
                requestData.put("source", type)
                requestData.put("original_url", url)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val toRequestBody = requestData.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            launchJob = GlobalScope.launch {
                try {
                    delay(deley)
                    val bean =
                            HttpService.commonApi.postFirstStudyScoreRecord(toRequestBody).await()
                    val num = bean.score + bean.extra_score
                    if (isActive) {
                        if (num <= 0) return@launch //禁成长
                        withContext(Dispatchers.Main) {
                            lifecycleOwner?.let {
                                if (!it.isFinishing) {
                                    XPopup.Builder(it)
                                            .asCustom(IncreaseScorePop(it, num, bean.continue_days))
                                            .show()
                                }

                            }
                        }

                    }
                } catch (e: Exception) {

                }

            }
        }

    }


}