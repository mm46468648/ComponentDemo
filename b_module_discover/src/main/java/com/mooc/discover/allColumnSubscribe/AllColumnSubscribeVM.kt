package com.mooc.discover.allColumnSubscribe

import androidx.lifecycle.MutableLiveData
import com.mooc.discover.model.SubscribeAllResponse
import com.mooc.discover.model.SubscribeBean
import com.mooc.commonbusiness.base.BaseViewModel

class AllColumnSubscribeVM : BaseViewModel() {

    val repository = ColumnRepository()


    /**
     * 组装后的数据
     * 格式：
     *      {
     *          0.我的订阅栏目（文字）
     *          1。订阅的专栏列表
     *          2。订阅的专题列表
     *
     *          3。可订阅的栏目（文字）
     *          4。可订阅的专栏列表
     *          5。可订阅的专题列表
     *      }
     */
    val assembleData: MutableLiveData<ArrayList<Any>> by lazy {
        MutableLiveData<ArrayList<Any>>().also {
            it.value = ArrayList()
            getData()
        }
    }

    var isEditState = false //是否是编辑模式

    /**
     * 网络请求加载数据
     */
    fun getData() {
        launchUI {
            val allColumnSubscribeData = repository.getAllColumnSubscribeData()
            recombinationData(allColumnSubscribeData)
        }
    }

    /**
     * 上报已订阅的数据
     */
    fun postChangeData() {
        val subscribeColumnList = assembleData.value?.get(1) as ArrayList<SubscribeBean>
        val subscribeSpecialList = assembleData.value?.get(2) as ArrayList<SubscribeBean>

        //提取id列表，转为数组
        val columnArray = subscribeColumnList.map {
            it.id
        }.toTypedArray()

        val specialArray = subscribeSpecialList.map {
            it.id
        }.toTypedArray()

        launchUI {
            repository.postSubscribeChangeData(columnArray + specialArray)
        }
    }

    /**
     * 对数据进行重组
     */
    fun recombinationData(allColumnSubscribeData: ArrayList<SubscribeAllResponse>) {
        //先添加订阅的栏目文字
        assembleData.value?.add(AllColumnSubscribeAdapter.STR_MY_SUBSCRIBE)
        //遍历第一次，添加已订阅的栏目
        allColumnSubscribeData.forEachIndexed { index, response ->
            val map = response.subscribe.map {//重新映射数据，空集合也保留，方便以后添加操作
                it.mAdapterType = index                 //等于0是专栏数据，等于1是专题数据
                it.subscribe = true
                it
            }
            assembleData.value?.add(map)

        }
        //遍历第二次，添加未订阅（可订阅）的栏目
        assembleData.value?.add(AllColumnSubscribeAdapter.STR_CAN_SUBSCRIBE)
        allColumnSubscribeData.forEachIndexed { index, response ->
            val map = response.not_subscribe.map {                //等于0是专栏数据，等于1是专题数据

                it.mAdapterType = index
                it
            }
            assembleData.value?.add(map)

        }

        assembleData.postValue(assembleData.value)
    }


    /**
     * 移动订阅专栏
     */
    fun moveSubscribeColumn(subscribeBean: SubscribeBean) {
        //将已订阅，移动到可订阅
        val subscribeColumnList = assembleData.value?.get(1) as ArrayList<SubscribeBean>
        val subscribeSpecialList = assembleData.value?.get(2) as ArrayList<SubscribeBean>
        val unsubscribeColumnList = assembleData.value?.get(4) as ArrayList<SubscribeBean>
        val unsubscribeSpecialList = assembleData.value?.get(5) as ArrayList<SubscribeBean>

        if (subscribeBean.subscribe && subscribeBean.editMode) { //如果是已订阅，必须是编辑模式才能移除
            if (subscribeColumnList.contains(subscribeBean)) {
                subscribeColumnList.remove(subscribeBean)
                subscribeBean.editMode = false    //移除就不用显示X了
                subscribeBean.subscribe = false
                unsubscribeColumnList.add(subscribeBean)

            }
            if (subscribeSpecialList.contains(subscribeBean)) {
                subscribeSpecialList.remove(subscribeBean)
                subscribeBean.editMode = false
                subscribeBean.subscribe = false
                unsubscribeSpecialList.add(subscribeBean)
            }

            postChangeData()
            assembleData.value = assembleData.value
            return
        }

        //此处不在方法末尾调用统一的postChange的方法是因为，前面会刚移除订阅集合，又添加回去，这是两个不同的逻辑
        if (!subscribeBean.subscribe) {  //如果是未订阅的可直接添加到已订阅中
            if (unsubscribeColumnList.contains(subscribeBean)) {
                unsubscribeColumnList.remove(subscribeBean)
                subscribeBean.subscribe = true
                subscribeBean.editMode = isEditState    //是否显示X是编辑状态而定
                subscribeColumnList.add(subscribeBean)

            }
            if (unsubscribeSpecialList.contains(subscribeBean)) {
                unsubscribeSpecialList.remove(subscribeBean)
                subscribeBean.subscribe = true
                subscribeBean.editMode = isEditState
                subscribeSpecialList.add(subscribeBean)
            }

            postChangeData()
            assembleData.value = assembleData.value
        }

    }

    /**
     * 设置已订阅为编辑模式,或者取消编辑模式
     */
    fun setDataEditMode() {
        isEditState = !isEditState
        assembleData.value?.forEach { a ->
            if (a is List<*>) {
                a.forEach {
                    if (it is SubscribeBean && it.subscribe) {
                        it.editMode = isEditState
                    }
                }
            }
        }
        assembleData.value = assembleData.value
    }
}