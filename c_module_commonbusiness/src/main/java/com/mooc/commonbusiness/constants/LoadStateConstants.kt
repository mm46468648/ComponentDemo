package com.mooc.commonbusiness.constants

/**
 * 数据加载状态常量信息
 */
class LoadStateConstants {

    companion object{
        const val STATE_CURRENT_COMPLETE = 0  //当此加载更多完毕
        const val STATE_ALL_COMPLETE = 1    //所有加载更多完毕
        const val STATE_LOADING = 2
        const val STATE_ERROR = 3   //加载错误
        const val STATE_REFRESH_COMPLETE = 4   //下拉刷新完毕
        const val STATE_DATA_EMPTY = 5   //加载数据为空
    }
}