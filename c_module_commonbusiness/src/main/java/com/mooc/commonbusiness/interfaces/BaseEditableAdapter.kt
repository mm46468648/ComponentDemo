package com.mooc.commonbusiness.interfaces

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.model.eventbus.StudyRoomResourceChange
import com.mooc.resource.widget.Space120FootView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 默认实现可长按编辑的适配器
 * @param needEdit 公开的学习清单列表复用，传递false不能长按编辑
 * @param toast 如果在我的清单中需要提示，别人的不需要提示
 */
abstract class BaseEditableAdapter<T : StudyResourceEditable>(
    layoutId: Int,
    list: ArrayList<T>,
    var needEdit: Boolean = true,
    var toast : String = ""
) : BaseQuickAdapter<T, BaseViewHolder>(layoutId, list), EditableAdapterInterface {

    init {

        //统一实现长按编辑
        setOnItemLongClickListener { adapter, view, position ->
            if (needEdit) {
                showEditPop(view, list[position])
            }else{
                if(toast.isNotEmpty()){
                    toast(toast)
                }
            }
            return@setOnItemLongClickListener true
        }

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

//        setFooterView(View.inflate(context,R.layout.common_view_load_more_120,null))
//        setFooterView(Space120FootView(context))

        EventBus.getDefault().register(this)

    }

    override fun onEditAbleChange(editable: StudyResourceEditable) {
        remove(editable as T)
    }


    /***
     * 文件夹进行移动删除等操作事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFolderChangeEvent(resourceChange: StudyRoomResourceChange) {
        if (resourceChange.moveType == StudyRoomResourceChange.TYPE_RESOURCE) {
            val resourceId = resourceChange.resourceId
            val find = data.find {
                it.resourceId == resourceId
            }

            if (find is StudyResourceEditable) {
                onEditAbleChange(find)
            }
        }
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        EventBus.getDefault().unregister(this)
    }
}