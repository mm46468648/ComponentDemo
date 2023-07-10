package com.mooc.my.repository

import com.mooc.common.ktextends.toJSON
import com.mooc.common.ktextends.toObject
import com.mooc.common.utils.TimeUtils
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.api.CommonApi
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseRepository
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.my.ParserStatusBean
import com.mooc.commonbusiness.model.my.UploadFileBean
import com.mooc.commonbusiness.model.studyroom.HonorDataBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.my.model.*
import com.mooc.search.api.MyModelApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody

/**

 * @Author limeng
 * @Date 2020/8/21-4:11 PM
 */
class MyModelRepository : BaseRepository() {

//    suspend fun getDailyRead(): EverydayReadBean {
//        //从sp读取
//        val dailyReadJson = SpDefaultUtils.getInstance().getString(SpConstants.DAILY_READING_JSON, "")
//        if(dailyReadJson.isNotEmpty()){
//            //json转换为数据bean
//            val everydayReadBean = dailyReadJson.toObject(EverydayReadBean::class.java)
//            val results = everydayReadBean.results
//            //如果不为空，并且没过时（是当天的数据直接返回）
//            if(results?.isNotEmpty() == true &&  TimeUtils.isTimeToday(results[0].date)){
//               return everydayReadBean
//            }
//        }
//        //如果为空，或者过时了（不是当天的数据）请求网络数据
//        return request {
//            val await = ApiService.getRetrofit().create(MyModelApi::class.java).getEverydayReadData(7, 0).await()
//            if(await.results?.isNotEmpty() ==true){  //保存
//                SpDefaultUtils.getInstance().putString(SpConstants.DAILY_READING_JSON,await.toJSON())
//            }
//            await
//        }
//
//    }

    suspend fun getDailyRead(): Flow<EverydayReadBean> {

        return flow {
            //从sp读取
            val dailyReadJson =
                    SpDefaultUtils.getInstance().getString(SpConstants.DAILY_READING_JSON, "")
            if (dailyReadJson.isNotEmpty()) {
                //json转换为数据bean
                val everydayReadBean = dailyReadJson.toObject(EverydayReadBean::class.java)
                val results = everydayReadBean.results
                //如果不为空，并且没过时（是当天的数据直接返回）
                if (results?.isNotEmpty() == true && TimeUtils.isTimeToday(results[0].date)) {
                    emit(everydayReadBean)
                    return@flow
                }
            } else {
                val await = ApiService.getRetrofit().create(MyModelApi::class.java)
                        .getEverydayReadData(7, 0)
                if (await.results?.isNotEmpty() == true) {  //保存
                    SpDefaultUtils.getInstance()
                            .putString(SpConstants.DAILY_READING_JSON, await.toJSON())
                }
                emit(await)
            }
            val await =
                    ApiService.getRetrofit().create(MyModelApi::class.java).getEverydayReadData(7, 0)
            if (await.results?.isNotEmpty() == true) {  //保存
                SpDefaultUtils.getInstance()
                        .putString(SpConstants.DAILY_READING_JSON, await.toJSON())
            }
            emit(await)

        }

    }

    suspend fun getSingleRead(date: String): Flow<ReadBean> {

        return flow {
            //从sp读取
            val await = ApiService.getRetrofit().create(MyModelApi::class.java)
                    .getEverydayReadDataByDate(date)
            emit(await.data)
        }
    }

    /**
     * 获取签到数据
     */
    suspend fun getCheckInData(): CheckInDataBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getCheckInData().await()
        }
    }

    suspend fun getCheckInMember(): ComeonOtherResponse {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getCheckInMembers().await()
        }
    }

    /**
     * 请求签到积分
     */
    suspend fun postCheckInData(): CheckInDataBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).postCheckInData()
        }
    }

//    /**
//     * 获取证书列表
//     */
//    suspend fun getHonorData(limit: Int, offset: Int): HttpResponse<List<HonorDataBean>> {
//        return request {
//            ApiService.xtRetrofit.create(MyModelApi::class.java).getHonorList(limit, offset).await()
//        }
//    }

    /**
     * 获取证书列表2
     */
    suspend fun getMyCertificateData(limit: Int, offset: Int): HttpResponse<List<HonorDataBean>> {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java)
                    .getMyCertificateList(limit, offset).await()
        }
    }

    /**
     * 申请证书列表2=
     */
    suspend fun getCertificateData(limit: Int, offset: Int): CertificateBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java)
                    .getCertificateList(limit, offset).await()
        }
    }

    /**
     * 申请证书内容
     */
    suspend fun applyMoocCertificate(body: RequestBody): HttpResponse<Any> {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).applyMoocCertificate(body)
                    .await()
        }
    }

    /**
     * 获取常见问题列表
     */
    suspend fun getQuestionList(): List<QuestionListBean> {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getQuestionList().await()
        }
    }

    /**
     * 获取更多问题列表
     */
    suspend fun getQuestionListMore(map: Map<String, String>): QuestionMoreBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getQuestionListMore(map).await()
        }
    }

    /**
     * 获取反馈问题列表
     */
    suspend fun getFeedList(map: Map<String, String>): FeedListBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getFeedList(map).await()
        }
    }

    /**
     * 上传图片
     */
    suspend fun postImageFile(body: RequestBody): HttpResponse<UploadFileBean> {
        return request {
            HttpService.commonApi.postImageFile(body).await()
        }
    }


    /**
     * 获取用户关注列表
     */
    suspend fun getFollowFansList(
            type: Int,
            userId: String?,
            limit: Int,
            offset: Int
    ): UserFollowBean {
        return request {
            when (type) {// 0 粉丝  1 关注的人
                1 -> {
                    ApiService.getRetrofit().create(MyModelApi::class.java)
                            .getFollowList(userId, limit, offset).await()
                }
                0 -> {
                    ApiService.getRetrofit().create(MyModelApi::class.java)
                            .getFansList(userId, limit, offset).await()
                }
                else -> ApiService.getRetrofit().create(MyModelApi::class.java)
                        .getFollowList(userId, limit, offset).await()
            }
        }
    }

    suspend fun getLearningList(
            userId: String?,
            limit: Int,
            offset: Int
    ): HttpResponse<ArrayList<LearingListBean>>? {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java)
                    .getShowFolderList(userId, limit, offset).await().data
        }
    }

    suspend fun getUserShareSchoolCircle(userId: String): ArrayList<SchoolSourceBean> {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getUserSchoolCircle(userId)
                    .await().share_list!!
        }
    }


    /**
     * 获取意见反馈列表
     */
    suspend fun getFeedBackList(feedId: String?): FeedBackListBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getFeedBackList(feedId).await()
        }
    }

    /**
     * 发送意见反馈
     */
    suspend fun sendFeedMsg(
            id: String?,
            content: String,
            reply_id: String?,
            img_attachment: String
    ): SendFeedMsgBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java)
                    .sendFeedMsg(id, content, reply_id, img_attachment).await()
        }
    }

    /**
     * 获取反馈类型
     */
    suspend fun getFeedType(): FeedBackBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getFeedType().await()
        }
    }

    /**
     * 提交反馈
     */
    suspend fun postFeedback(body: RequestBody): FeedBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).postFeedback(body).await()
        }
    }

    /**
     * 提交反馈
     */
    suspend fun getUserSchoolCircle(userId: String): SchoolCircleBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).getUserSchoolCircle(userId)
                    .await()
        }
    }

    /**
     *  //其他用户主页分享点赞（我的信息）
     */
    suspend fun postUserLikeAndDis(body: RequestBody): ParserStatusBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).postUserLikeAndDis(body).await()
        }
    }

    /**
     *  //我的分享点赞（我的信息）
     */
    suspend fun postLikeSchoolResource(body: RequestBody): ParserStatusBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).postLikeSchoolResource(body)
                    .await()
        }
    }

    /**
     * 我的分享关注
     */
    suspend fun postFollowUser(body: RequestBody): ParserStatusBean {
        return request {
            ApiService.getRetrofit().create(CommonApi::class.java).postFollowUser(body).await()
        }
    }

    /**
     * 我的分享删除学友圈
     */
    suspend fun delSchoolResource(body: RequestBody): ParserStatusBean {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).delSchoolResource(body).await()
        }
    }


    /**
     * @param body json格式
     * key name
     */
    suspend fun postUserInfo(body: RequestBody): HttpResponse<Any> {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).updateUserNickname(body).await()
        }
    }

    /**
     * @param body json格式
     * file 流
     */
    suspend fun postUserHead(body: RequestBody): HttpResponse<UploadFileBean> {
        return request {
            ApiService.getRetrofit().create(MyModelApi::class.java).postUserHeader(body).await()
        }
    }
}