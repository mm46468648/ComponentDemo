package com.mooc.home.repository

import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.common.utils.DebugUtil
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.home.api.HomeApi
import com.mooc.home.model.AlertMsgBean
import com.mooc.home.model.DailyReadBean
import com.mooc.home.model.MedalBean
import com.mooc.home.model.StudyCompletion
import okhttp3.RequestBody
import org.json.JSONObject

class HomeRepository : BaseRepository() {

    suspend fun getAlertMsg(): AlertMsgBean {
        return request {
            val rootHost = if (DebugUtil.isTestServeUrl) {
                ApiService.TEST_BASE_IP_URL
            } else {
                if (ApiService.getUserNewUrl()) {
                    ApiService.NORMAL_BASE_URL
                } else {
                    ApiService.OLD_BASE_URL
                }
            }
            ApiService.noVerifyRetrofit.create(HomeApi::class.java).getAlertMsg("${rootHost}/api/web/message/alert_message/").await()
        }
    }

//    suspend fun getActivityUserJoin(): ActivityJoinBean {
//        return request {
//            ApiService.getRetrofit().create(HomeApi::class.java).getActivityUserJoin().await()
//        }
//    }

    suspend fun getDailyReading(): HttpResponse<DailyReadBean> {
        return request {
            ApiService.getRetrofit().create(HomeApi::class.java).getTodayReadAsync().await()
        }
    }

    suspend fun postCheckTime(body: RequestBody): HttpResponse<Any> {
        return request {
            ApiService.getRetrofit().create(HomeApi::class.java).postCheckTime(body).await()

        }
    }

    suspend fun getSpecialMedal(): MedalBean {
        return request {
            ApiService.getRetrofit().create(HomeApi::class.java).getSpecialMedal().await()

        }
    }

    suspend fun getStudyPlanCompletion(): StudyCompletion {
        return request {
            ApiService.getRetrofit().create(HomeApi::class.java).getStudyPlanCompletion().await()

        }
    }

    suspend fun postAlertMsgRead(id: Int) {
        request {
            val jsonObject = JSONObject()
            jsonObject.put("id", id)
            jsonObject.put("is_read", true.toString())
            val rootHost = if (DebugUtil.isTestServeUrl) {
                ApiService.TEST_BASE_IP_URL
            } else {
                if (ApiService.getUserNewUrl()) {
                    ApiService.NORMAL_BASE_URL
                } else {
                    ApiService.OLD_BASE_URL
                }
            }
            ApiService.noVerifyRetrofit.create(HomeApi::class.java).postAlertMsgRead("${rootHost}/api/web/message/alert_message/", RequestBodyUtil.fromJson(jsonObject)).await()
        }
    }
}