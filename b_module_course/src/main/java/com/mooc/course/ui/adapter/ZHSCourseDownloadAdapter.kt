package com.mooc.course.ui.adapter

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseDelegateMultiAdapter
import com.chad.library.adapter.base.delegate.BaseMultiTypeDelegate
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.loge
import com.mooc.course.R
import com.mooc.course.model.LessonInfo
import com.mooc.download.DownloadModel
import com.mooc.newdowload.State
import com.mooc.resource.widget.DownloadCircleProgressView
import org.jetbrains.annotations.NotNull

class ZHSCourseDownloadAdapter(var list: ArrayList<LessonInfo>) :
    BaseDelegateMultiAdapter<LessonInfo, BaseViewHolder>(list) {

    companion object {
        const val ITEM_TYPE_CHAPTER = 0   //chapterName
        const val ITEM_TYPE_LESSON = 1    //lessonName
        const val ITEM_TYPE_LESSONVIDEO = 2    //lessonVideoName

    }

    init {
        // 第一步，设置代理
        setMultiTypeDelegate(object : BaseMultiTypeDelegate<LessonInfo>() {
            override fun getItemType(@NotNull data: List<LessonInfo>, position: Int): Int {
                // 根据数据，自己判断应该返回的类型
                val obj = data[position].level
//                if (obj == 1 && data[position].videoUrl.isEmpty()) {
//                    return ITEM_TYPE_LESSON
//                }
//                if (obj == 1 && data[position].videoUrl.isNotEmpty()) {
//                    return ITEM_TYPE_LESSONVIDEO
//                }
                return obj
            }
        })
        // 第二部，绑定 item 类型(course,ablum,ebook可复用相同布局) （学习计划二级列表布局也可复用）
        getMultiTypeDelegate()
            ?.addItemType(ITEM_TYPE_CHAPTER, R.layout.course_item_zhs_download_chaptername)
            ?.addItemType(ITEM_TYPE_LESSON, R.layout.course_item_zhs_download_lessonname)
            ?.addItemType(ITEM_TYPE_LESSONVIDEO, R.layout.course_item_zhs_download_lessonvideo)

    }

    var isEditMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    //选中的list
    var isAllSelect = false
        set(value) {
            field = value
            list.forEach {
                it.deleteDownloadSelect = value
            }
            notifyDataSetChanged()
        }

    var onDownloadComplete: (() -> Unit)? = null

//    override fun getItemId(position: Int): Long {
//        if(position >= list.size) return 0
//        return list.get(position).hashCode().toLong()
//    }

    override fun convert(holder: BaseViewHolder, item: LessonInfo) {

        when (holder.itemViewType) {
            ITEM_TYPE_CHAPTER -> {
                holder.setText(R.id.tvChapterName, item.name)
            }
            ITEM_TYPE_LESSON -> {
                holder.setText(R.id.tvLessonName, item.name)
            }
            ITEM_TYPE_LESSONVIDEO ->{
                holder.setText(R.id.tvLessonVideoName, item.name)

                //如果是编辑模式，则显示选择框
                holder.setGone(R.id.cbDownload, !isEditMode)
                //如果selectList中包含，当前item,则显示选中状态
                val cbDownload = holder.getView<CheckBox>(R.id.cbDownload)
                //loge(item.name + item.deleteDownloadSelect)
                //是否选中删除下载
                cbDownload.isChecked = item.deleteDownloadSelect
                //下载信息模型
                val findDownloadMode = item.downloadInfo
                //下载状态相关显示
                val dcpView = holder.getView<com.mooc.resource.widget.DownloadCircleProgressView>(R.id.dcpView)
                if (findDownloadMode == null) {
                    dcpView.state = DownloadModel.STATUS_NONE
//                    loge("downloadMode == null")
                } else {
                    dcpView.state = findDownloadMode.state ?: DownloadModel.STATUS_NONE
//                    loge(findDownloadMode.toString())
                    if (findDownloadMode.size != 0L) {
                        val i = (100 * findDownloadMode.downloadSize / findDownloadMode.size).toInt()
                        dcpView.setmCurrent(i)
                    } else {
                        dcpView.setmCurrent(0)
                    }

                    if (findDownloadMode.state == State.DOWNLOAD_COMPLETED) {
                        onDownloadComplete?.invoke()
                    }
                }
            }
        }
    }

}