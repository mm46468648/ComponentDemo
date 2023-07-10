package com.mooc.course.ui.adapter

import android.view.ViewGroup
import androidx.annotation.NonNull
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.course.model.ChaptersBean
import com.mooc.course.ui.widget.XtCourseDownloadNewView
//import com.mooc.commonbusiness.model.search.ChaptersBean
import com.mooc.course.ui.widget.XtCourseDownloadView

class XtCourseDownloadListAdapter(data: ArrayList<ChaptersBean>?) : BaseQuickAdapter<ChaptersBean, BaseViewHolder>(0,data) {
//    var downloadManager: DownloadManager = DownloadService.getDownloadManager(ApplicationModule.mContext)

    /**
     * 重写此方法，自己创建 View 用来构建 ViewHolder
     */
    override fun onCreateDefViewHolder(@NonNull parent: ViewGroup, viewType: Int): BaseViewHolder {
        // 创建自己的布局
//        val xtCourseDownloadView = XtCourseDownloadView(context)
        val xtCourseDownloadView = XtCourseDownloadNewView(context)
        return createBaseViewHolder(xtCourseDownloadView)
    }

    override fun convert(holder: BaseViewHolder, item: ChaptersBean) {
//        val xtCourseDownloadView: XtCourseDownloadView = holder.itemView as XtCourseDownloadView
        val xtCourseDownloadView: XtCourseDownloadNewView = holder.itemView as XtCourseDownloadNewView
        xtCourseDownloadView.setCourseData(item)

        //布局的显示隐藏
        val adapterPosition = holder.adapterPosition
        val currentData = data.get(adapterPosition)

        val nextPosition = holder.adapterPosition + 1

        xtCourseDownloadView.setSpaceShow(false)

        if(nextPosition<data.size){
            val nextData = data.get(nextPosition)
            xtCourseDownloadView.setSpaceShow(currentData.chapterName != nextData.chapterName)
        }
    }
}