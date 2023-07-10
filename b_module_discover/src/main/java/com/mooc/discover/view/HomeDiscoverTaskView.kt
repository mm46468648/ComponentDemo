package com.mooc.discover.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
//import com.mooc.commonbusiness.modle.AnnouncementBean
import com.mooc.discover.model.TaskBean
//import kotlinx.android.synthetic.main.discover_view_task.view.*

class HomeDiscoverTaskView @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
FrameLayout(mContext, attributeSet, defaultInt) {

    var img_view : ImageView? = null
    init {
        LayoutInflater.from(mContext).inflate(R.layout.discover_view_task,this)
        img_view = findViewById(R.id.img_view)
        visibility = GONE
        img_view?.setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_DISCOVER_TASK).navigation()
        }
    }

    /**
     * 设置任务数据
     */
    fun setImg(it: TaskBean){

        if (it.task_entry_img.isNotEmpty()){
            visibility = View.VISIBLE

            img_view?.let { it1 ->
                Glide.with(context)
                    .load(it.task_entry_img)
                    .placeholder(R.mipmap.discover_bg_task)
                    .into(it1)
            };

        }else{
            visibility = View.GONE
        }



    }

}