package com.mooc.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mooc.home.R

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    首页发现推荐轮播公告
 * @Author:         xym
 * @CreateDate:     2020/8/12 3:51 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/12 3:51 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class HomeDiscoverRotationView @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        FrameLayout(mContext, attributeSet, defaultInt) {


    init {
        LayoutInflater.from(mContext).inflate(R.layout.home_view_discover_rotation,this)
    }


    /**
     * 设置轮播数据
     */
    fun setRotationBean() {

    }
}