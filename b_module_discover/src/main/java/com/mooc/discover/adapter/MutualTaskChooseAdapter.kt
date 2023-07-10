package com.mooc.discover.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.model.TaskDetailsBean
import com.mooc.discover.ui.TaskDetailsActivity

/**
 * 任务详情中互斥任务可选择的任务适配器
 */
class MutualTaskChooseAdapter(list : ArrayList<TaskDetailsBean>,var mustTask:Boolean = false)
    : BaseQuickAdapter<TaskDetailsBean,BaseViewHolder>(R.layout.item_mutual_task_choose,list){


    var onItemClick : ((position:Int)->Unit)? = null


    override fun convert(holder: BaseViewHolder, item: TaskDetailsBean) {

        val layoutPosition = holder.layoutPosition - headerLayoutCount


        val rvChild = holder.getView<RecyclerView>(R.id.rvChild)
        rvChild.layoutManager = LinearLayoutManager(context)
        rvChild.isNestedScrollingEnabled = false
        val mutualTaskChooseChildAdapter =
            MutualTaskChooseChildAdapter(item.children_list ?: arrayListOf())
        mutualTaskChooseChildAdapter.setOnItemClickListener { adapter, view, position ->
//            onItemClick?.invoke(layoutPosition)

            val bean = item.children_list?.get(position)
            //跳转任务详情
            ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS)
                .withString(IntentParamsConstants.PARAMS_TASK_ID, bean!!.id)
                .withBoolean(TaskDetailsActivity.FROM_MUTUAL_CHILD_TASK,true)
                .navigation()
        }

//        holder.itemView.setOnClickListener {
//            onItemClick?.invoke(layoutPosition)
//        }

        rvChild.adapter = mutualTaskChooseChildAdapter

        //由于有一个header,所以layoutposition就是+1的数据
        holder.setText(R.id.tvPosition,(holder.layoutPosition).toString())
        //必做任务隐藏
        holder.setGone(R.id.flPosition,mustTask)
    }


}