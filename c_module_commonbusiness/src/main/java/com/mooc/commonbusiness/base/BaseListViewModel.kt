package com.mooc.commonbusiness.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.constants.LoadStateConstants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    列表ViewModel基类
 * @Author:         xym
 * @CreateDate:     2020/8/19 10:11 AM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/19 10:11 AM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
abstract class BaseListViewModel<T> : BaseViewModel() {


    private var pageData = MutableLiveData<ArrayList<T>>()
    private val pageState = MutableLiveData<Int>()   //页面状态1，全部加载完毕，2.加载中，3。加载错误

    init {
        pageData.value = arrayListOf<T>()
    }

    fun getPageData(): MutableLiveData<ArrayList<T>> {
        return pageData
    }

    fun getPageState(): MutableLiveData<Int> {
        return pageState
    }

    protected var offset = 0    //不要轻易赋值，会影响加载不准确
    protected var limit = 10


    open fun initData() {
        limit = getLimitStart()
        offset = getOffSetStart()

        loadData()
    }

    var launch: Job? = null
    open fun loadData() {
//        if (needGetData2()) {
//            loadData2()
//            return
//        }

        if (launch?.isCompleted == false) {
            launch?.cancel()
        }

        launch = viewModelScope.launch() {
            try {
                val data = getData().await()
                if (offset == getOffSetStart()) {  //下拉刷新状态回调
                    pageState.value = LoadStateConstants.STATE_REFRESH_COMPLETE
                }

                if (offset == getOffSetStart()) {    //下拉刷新，清空数据
                    pageData.value?.clear()
                }
                //如果数据为空,并且是第一页，发送空状态
                if (offset == getOffSetStart() && (data == null || data.isEmpty())) {
                    pageState.postValue(LoadStateConstants.STATE_DATA_EMPTY);
                }

                //通知刷新
                data?.let {
                    (pageData.value as ArrayList).addAll(it)
                    pageData.postValue(pageData.value)
                    offset += data.size
                    //如果数据，小于需要的数量 ，加载更多完毕状态。
                    // 后台因某些特殊原因一页数据返回不到limit数量，但是还有下一页数据，所以加上只要数据不为空就继续加载更多
                    if (data.isEmpty()) {
                        pageState.value = LoadStateConstants.STATE_ALL_COMPLETE
                    } else {
                        pageState.postValue(LoadStateConstants.STATE_CURRENT_COMPLETE)
                    }
                }
            } catch (e: Exception) {
                coroutineExceptionHandler.handleException(this.coroutineContext, e)
                //如果第一页就错，由于没有error页，暂时显示empty页面样式
                if (offset == getOffSetStart()) {
                    pageState.value = LoadStateConstants.STATE_DATA_EMPTY
                }
                if (e !is CancellationException) {
                    pageState.postValue(LoadStateConstants.STATE_ERROR)
                }
                loge(e.toString())

            }
        }
    }

//    /**
//     * 加载列表数据的第二种方式
//     */
//    fun loadData2() {
//        viewModelScope.launch() {
//            try {
//                val data = getData2()?.await()
//                if (offset == getOffSetStart()) {  //下拉刷新状态回调
//                    pageState.value = LoadStateConstants.STATE_REFRESH_COMPLETE
//                }
//
//                if (offset == getOffSetStart()) {    //下拉刷新，清空数据
//                    pageData.value?.clear()
//                }
//                //如果数据为空,并且是第一页，发送空状态
//                if (offset == getOffSetStart() && (data == null || data.results == null && data.results.isEmpty())) {
//                    pageState.value = LoadStateConstants.STATE_DATA_EMPTY
//                }
//
//                //通知刷新
//                data?.results?.let {
//                    (pageData.value as ArrayList).addAll(it)
//                    pageData.postValue(pageData.value)
//                    offset += limit
//                    //如果数据，小于需要的数量 ，加载更多完毕状态
//                    if (offset > data.count) {
//                        pageState.value = LoadStateConstants.STATE_ALL_COMPLETE
//                    } else {
//                        pageState.postValue(LoadStateConstants.STATE_CURRENT_COMPLETE)
//                    }
//                }
//            } catch (e: Exception) {
//                CrashReport.postCatchedException(e)
//                coroutineExceptionHandler.handleException(this.coroutineContext, e)
//                pageState.postValue(LoadStateConstants.STATE_ERROR)
//                loge(e.toString())
//            }
//        }
//    }


    /**
     * 设置offset初始值，不同的接口会有不同需求
     */
    open fun getOffSetStart(): Int = 0

    /**
     * 设置limit初始值，不同的接口会有不同需求
     */
    open fun getLimitStart() = 10

    abstract suspend fun getData(): Deferred<List<T>?>

    /**
     * 是否需要用第二种方式判断分页
     * 根据接口返回的count字段
     */
//    open fun needGetData2() = false
//
//    open suspend fun getData2(): Deferred<HttpListResponse<List<T>>>? = null
}