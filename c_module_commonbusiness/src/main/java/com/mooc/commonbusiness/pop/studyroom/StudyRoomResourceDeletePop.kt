package com.mooc.commonbusiness.pop.studyroom

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.databinding.HomePopDeleteStudyroomCourseBinding
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
//import kotlinx.android.synthetic.main.home_pop_delete_studyroom_course.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 学习室资源删除弹窗
 */
@SuppressLint("ViewConstructor")
class StudyRoomResourceDeletePop(
        mContext: Context,
        var editable: StudyResourceEditable,
        var onConfirm: (() -> Unit)? = null
) : CenterPopupView(mContext) {

    override fun getImplLayoutId(): Int = R.layout.home_pop_delete_studyroom_course

    override fun getMaxWidth(): Int {
        return 300.dp2px()
    }

    var onConfirmCallback: ((success: Boolean) -> Unit)? = null
    lateinit var inflater: HomePopDeleteStudyroomCourseBinding
    override fun onCreate() {
        super.onCreate()
        inflater = HomePopDeleteStudyroomCourseBinding.bind(popupImplView)
        //如果不是课程，改变中间提示文案
        if (editable !is CourseBean) {
            inflater.tvMiddle.text = "是否要删除所选资源?"
        }

        //如果是文件夹，改变中间提示文案
        if (editable is FolderItem) {
            inflater.tvMiddle.text = "是否要删除所选学习清单?"
        }

        findViewById<TextView>(R.id.tvOk).setOnClickListener {
            if (onConfirm != null) {
                onConfirm?.invoke()
                dismiss()
            } else {
                deleteResourse(editable)
            }
        }

        findViewById<TextView>(R.id.tvCancel).setOnClickListener { dismiss() }
    }

    var job: Job? = null
    private fun deleteResourse(editable: StudyResourceEditable) {
        job = GlobalScope.launch {
            try {
                val studyRoomService =
                        ARouter.getInstance().navigation(StudyRoomService::class.java)
                val deleteResFromFolder =
                        studyRoomService.deleteResFromFolder(editable.resourceId, editable.sourceType)

                if (!isActive) return@launch

                //回调到主线程
                runOnMain {
                    if (!deleteResFromFolder.isSuccess) {
                        toast(deleteResFromFolder.message)
                    }
                    //回调
                    onConfirmCallback?.invoke(deleteResFromFolder.isSuccess)
                    //弹窗消失
                    dismiss()
                }
            } catch (e: Exception) {

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        job?.cancel()
    }
}