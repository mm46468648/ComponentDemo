package com.mooc.home.ui.discover

/**
 * 发现页三级分类列表Fragment接口
 */
interface DiscoverChildListFragmentInterface {
    companion object{
        const val PARAMS_PARENT_ID = "params_parent_id"
        const val PARAMS_SORT_ID = "params_sort_id" //第三级分类的id，初始化的时候需要
        const val PARAMS_RESOURCE_TYPE = "params_resource_type"
    }
    /**
     * @param map 需要增加的参数
     * @param reset 是否需要重置参数,默认追加不重置
     */
    fun changeListData(map:Map<String,String>,reset:Boolean = false)
}