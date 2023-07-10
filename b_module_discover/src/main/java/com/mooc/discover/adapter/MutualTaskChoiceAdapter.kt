package com.mooc.discover.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lihang.ShadowLayout
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.TaskDetailsBean
import com.mooc.discover.ui.TaskDetailsActivity

/**
 * 互斥任务选择适配器
 * 用于互斥任务详情页面(与MutualTaskChooseAdapter.kt不同!!!)
 */
class MutualTaskChoiceAdapter(var list : ArrayList<TaskDetailsBean>,var title:String)
    : BaseQuickAdapter<TaskDetailsBean,BaseViewHolder>(R.layout.item_mutual_task_choice,list){

    var selectPosition = -1
        set(value) {
            if(value != field){
                field = value
                notifyDataSetChanged()
            }
        }

    init {
        if(title == TaskConstants.STR_MUST_TASK){
            selectPosition = 0
        }
    }

    var onItemClick : ((position:Int)->Unit)? = null
    override fun convert(holder: BaseViewHolder, item: TaskDetailsBean) {
        val layoutPosition = holder.layoutPosition - headerLayoutCount
        val rvChild = holder.getView<RecyclerView>(R.id.rvChild)
        rvChild.layoutManager = LinearLayoutManager(context)
        val mutualTaskChooseChildAdapter =
            MutualTaskChooseChildAdapter(item.children_list ?: arrayListOf(),false)

        mutualTaskChooseChildAdapter.setOnItemClickListener { adapter, view, position ->
//            onItemClick?.invoke(layoutPosition)
            val bean = item.children_list?.get(position)
            //跳转任务详情
            ARouter.getInstance().build(Paths.PAGE_TASK_DETAILS)
                .withString(IntentParamsConstants.PARAMS_TASK_ID, bean!!.id)
                .withBoolean(TaskDetailsActivity.FROM_MUTUAL_CHILD_TASK,true)
                .navigation()
        }

//        val ivSelect = holder.getView<ImageView>(R.id.ivSelect)
        //        ivSelect.setOnClickListener {
//            onItemClick?.invoke(layoutPosition)
//        }
        rvChild.adapter = mutualTaskChooseChildAdapter
        val iconRes = if(layoutPosition == selectPosition) R.mipmap.ic_task_mutual_select else R.mipmap.ic_task_mutual_unselect
        holder.setImageResource(R.id.ivSelect,iconRes)
        val flSelect = holder.getView<View>(R.id.flSelect)
        flSelect.setOnClickListener {
            onItemClick?.invoke(layoutPosition)
        }



        val mShadowLayout = holder.getView<ShadowLayout>(R.id.mShadowLayout)
        mShadowLayout.setShadowHidden(layoutPosition != selectPosition)
        mShadowLayout.isSelected = layoutPosition == selectPosition

        if(layoutPosition != selectPosition){
            mShadowLayout.setShadowHidden(true)
            mShadowLayout.setStrokeColor(context.getColorRes(R.color.transparent))
            mShadowLayout.setCornerRadius(0)
        }else{
            mShadowLayout.setShadowHidden(false)
            mShadowLayout.setStrokeColor(context.getColorRes(R.color.colorPrimary))
            mShadowLayout.setCornerRadius(5)
        }
//        val llRoot = holder.getView<View>(R.id.llRoot)
//        if(layoutPosition == selectPosition){
////            llRoot.setBackgroundResource(R.drawable.shape_radius2_stoke1primary_solidwhite )
//            mShadowLayout.setBackgroundColor(context.getColorRes(R.color.transparent))
//        }else{
//            mShadowLayout.setBackgroundColor(context.getColorRes(R.color.color_E))
//        }
    }


}