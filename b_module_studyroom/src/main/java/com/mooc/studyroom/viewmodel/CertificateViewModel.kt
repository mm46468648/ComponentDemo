package com.mooc.studyroom.viewmodel

import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.model.studyroom.HonorDataBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import kotlinx.coroutines.flow.*

/**
 *
 * @ProjectName:证书ViewModel
 * @Package:
 * @ClassName:
 * @Description:    java类作用描述
 * @Author:         xym
 * @CreateDate:     2021/1/22 11:45 AM
 * @UpdateUser:     更新者
 * @UpdateDate:     2021/1/22 11:45 AM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class CertificateViewModel : BaseListViewModel2<HonorDataBean>() {

    val studyRoomApi = ApiService.getRetrofit().create(StudyRoomApi::class.java)
    override suspend fun getData(): Flow<List<HonorDataBean>?> {
        val arrayList = arrayListOf<HonorDataBean>()
        // 由于这个接口有肯能报错400，影响后面执行，try cache一下
        val flow = flow {
            val result = studyRoomApi.getCertificateListFromXt(0, 1000).results
            emit(result)
        }.catch { _ ->
//            loge(e.toString())
            emit(arrayListOf())
        }

        val flow1 = flow {
            val results = studyRoomApi.getCertificateListFromOwn(0, 1000).results
            emit(results)
        }.catch { _ ->
//            loge(e.toString())
            emit(arrayListOf())
        }

        return flow.zip(flow1) { list, list2 ->
            if(list!=null){
                arrayList.addAll(0, list)
            }
            if(list2!=null){
                arrayList.addAll(list2)
            }
            arrayList
        }
    }
}