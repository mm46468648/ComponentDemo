package com.mooc.commonbusiness.utils.incpoints

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.pop.IncreaseScorePop
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
增加资源积分
 * @Author limeng
 * @Date 2021/2/25-5:26 PM
 */
class AddPointManager {

    companion object : LifecycleObserver {

        private val random = Random()
        var launchJob: Job? = null

        /**
         * 增加资源积分实际方法
         */
        fun startAddPointsTask(
            lifecycleOwner: Activity?,
            title: String,
            type: String,
            url: String
        ) {
//            lifecycleOwner?.lifecycle?.addObserver(this)
            var delayTime = 20000

            val t = type.toInt()
            if (t == ResourceTypeConstans.TYPE_TRACK || t == ResourceTypeConstans.TYPE_COURSE || t == ResourceTypeConstans.TYPE_MICRO_LESSON || t == ResourceTypeConstans.TYPE_ONESELF_TRACK) {
                delayTime = 0
            }

            if (t == ResourceTypeConstans.TYPE_PERIODICAL || t == ResourceTypeConstans.TYPE_E_BOOK || t == ResourceTypeConstans.TYPE_KNOWLEDGE || t == ResourceTypeConstans.TYPE_ALBUM || t == ResourceTypeConstans.TYPE_BAIKE) {
                delayTime += random.nextInt(10000)
            }
            if (t == ResourceTypeConstans.TYPE_ARTICLE) {
                delayTime += 20000
            }

            val requestData = JSONObject()
            try {
                requestData.put("type", type)
                requestData.put("url", url)
                requestData.put("title", title)
            } catch (e: JSONException) {
                e.printStackTrace()
            }


            val toRequestBody = requestData.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            launchJob?.cancel()
            launchJob = GlobalScope.launch {
                try {
                    delay(delayTime.toLong())

                    if (lifecycleOwner?.isFinishing == true) {
                        return@launch
                    }

                    val bean = HttpService.commonApi.postUserPoint(toRequestBody).await()
                    if (bean.isSuccess) {
                        withContext(Dispatchers.Main) {
                            if (lifecycleOwner != null && !lifecycleOwner.isFinishing) {
                                XPopup.Builder(lifecycleOwner)
                                    .asCustom(IncreaseScorePop(lifecycleOwner, 1, 0))
                                    .show()
                            }
                        }
                    }

                } catch (e: Exception) {
                }
            }
        }

    }


}