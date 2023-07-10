package com.mooc.commonbusiness.manager

import android.view.View
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import com.mooc.commonbusiness.model.studyroom.FolderItem
import com.mooc.commonbusiness.pop.studyroom.StudyRoomResourceEditPop
import com.mooc.commonbusiness.pop.studyroom.StudyRoomResourceEditPop2

/**
 * 学习室学习资源编辑弹窗工具类
 */
class StudyResourceEditPopUtil {

    companion object {
        /**
         * 显示编辑弹窗
         */
        fun showEditPop(
                view: View,
                editable: StudyResourceEditable,
                callBack: ((event: Int) -> Unit)? = null
        ) {
            //可编辑的是否是文件夹
            val isFolder = editable is FolderItem
            //由于非文件夹资源编辑的时候底部有遮挡，单独创建了一个新的弹窗，所以区分一下
            if (isFolder) {
//                //构造编辑资源弹框
                val studyRoomResourceEditPop = StudyRoomResourceEditPop(view.context, view, isFolder)
                studyRoomResourceEditPop.onClickEvent = {
                    callBack?.invoke(it)
                }
                XPopup.Builder(view.context)
                        .popupAnimation(PopupAnimation.NoAnimation)//设置动画
                        .atView(view)
                        .asCustom(studyRoomResourceEditPop) // 依附于所点击的View，内部会自动判断在上方或者下方显示
                        .show()
            } else {
                val studyRoomResourceEditPop =
                        StudyRoomResourceEditPop2(view.context, view, isFolder)
                studyRoomResourceEditPop.onClickEvent = {
                    callBack?.invoke(it)
                }
                studyRoomResourceEditPop.show()
            }
        }
    }
}