package com.mooc.home.ui.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.studyroom.DiscoverTab
import com.mooc.commonbusiness.model.studyroom.SortChild
import com.mooc.commonbusiness.net.network.StateLiveData
import com.mooc.commonbusiness.utils.UrlUtils
import com.mooc.home.HttpService
import com.mooc.commonbusiness.route.Paths
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    发现页数据持有类
 * @Author:         xym
 * @CreateDate:     2020/8/11 4:49 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/11 4:49 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class DiscoverViewModel : BaseViewModel() {

    val mRepository = DiscoverRepository()

    companion object {
        const val TAG = "DiscoverViewModel"
    }

    //    val defaultTabDatas = arrayOf("推荐","学习项目","慕课","音频","文章","电子书")
    val tabDatas = StateLiveData<List<DiscoverTab>>()     //一级分类

    /**
     * tab切换位置
     * pair = <类型，二级id>
     */
    val tabSelectPositionPair = MutableLiveData<Pair<Int, String>>()

    init {
        //初始化默认的一级分类Tab
//        val map = defaultTabDatas.mapIndexed{index,str->  //接口中默认id是1开始，relation_type是-1开始
//            DiscoverTab(name = str,id = index+1,relation_type = index-1) }.toList()
//        tabDatas.value = map
    }

    /**
     * 获取发现一级分类Tab数据
     */
    fun getDisCoverTabData() {

        viewModelScope.launch {
            mRepository.getDiscoverTabSort(tabDatas)
        }

    }


//    /**
//     * 获取二级，tab标题
//     * @param tabId 一级分类模型中的id
//     */
//    fun getChildColumeTabData(tabId: Int): LiveData<List<DiscoverTab>> {
//        val liveData = MutableLiveData<List<DiscoverTab>>()
//        launchUI({
//            loge("发起tab分类请求tabid: ${tabId}")
//            val discoverChildTabSort = HttpService.homeApi.getDiscoverChildTabSort(tabId)
//            val data = discoverChildTabSort.data
//            loge("tab分类请求成功tabid: ${tabId} tabdata: ${data}")
//            liveData.postValue(data)
//        },{
//            loge("tab分类请求失败tabid: ${tabId}，返回空")
//            liveData.postValue(arrayListOf())
//        })
//        return liveData
//    }


    /**
     * 获取二级，tab标题
     * @param tabId 一级分类模型中的id
     */
    fun getChildColumeTabFlow(tabId: Int): Flow<List<DiscoverTab>> {
        return flow<List<DiscoverTab>> {
            val discoverChildTabSort = HttpService.homeApi.getDiscoverChildTabSort(tabId)
            val data = discoverChildTabSort.data
            emit(data)
        }.catch {
            emit(arrayListOf())
        }
    }


    /**
     * 获取第三级分类数据
     */
    fun getChildSortCallBack(parentId: Int) : LiveData<List<SortChild>> {
        val child2Sort = MutableLiveData<List<SortChild>>()
        launchUI {
            val discoverChild2Sort = HttpService.homeApi.getDiscoverChild2Sort(parentId)
            child2Sort.postValue(discoverChild2Sort.await().data)
        }
        return child2Sort
    }


    /**
     *解析二维码数据
     */
    fun resolveQrResult(result: String) {
        //如果不包含，直接跳转扫描出来的二维码页面
        if(!result.contains("resource_type")){
            ARouter.getInstance().build(Paths.PAGE_WEB)
                    .withString(IntentParamsConstants.WEB_PARAMS_URL,result).navigation()
        }

        //解析url参数
        val parseParamsMap = UrlUtils.parseParams(result)
        //相应跳转
        ResourceTurnManager.turnToResourcePage(object : BaseResourceInterface{
            override val _resourceId: String
                get() = parseParamsMap?.get(IntentParamsConstants.PARAMS_RESOURCE_ID)?:""
            override val _resourceType: Int
                get() = parseParamsMap?.get(IntentParamsConstants.PARAMS_RESOURCE_TYPE)?.toInt()?:0
            override val _other: Map<String, String>?
                get() = hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to result)

        })
    }


}