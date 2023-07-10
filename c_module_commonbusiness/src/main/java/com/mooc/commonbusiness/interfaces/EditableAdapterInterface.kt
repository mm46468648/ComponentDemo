package com.mooc.commonbusiness.interfaces

import android.content.Context
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.manager.StudyResourceEditPopUtil
import com.mooc.commonbusiness.pop.studyroom.StudyRoomResourceEditPop
import com.mooc.commonbusiness.pop.studyroom.StudyRoomResourceDeletePop
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.put
import com.mooc.commonbusiness.route.Paths

/**
 * 可长按编辑的适配器接口
 */
interface EditableAdapterInterface {
    /**
     * 显示编辑弹窗
     */
    fun showEditPop(view: View, editable: StudyResourceEditable) {
        StudyResourceEditPopUtil.showEditPop(view,editable){
            when(it){
                StudyRoomResourceEditPop.EVENT_DELETE->{
                    //其他显示资源删除弹窗,与课程删除，只差一个文案，在弹窗里区分，具体删除逻辑通过回调设置
                    showStudyRoomResourceDeletePop(view.context,editable)
                }
                StudyRoomResourceEditPop.EVENT_MOVE->{
                    // 进入移动文件夹页面
                    val put = Bundle().put(IntentParamsConstants.PARAMS_RESOURCE_ID, editable.resourceId)
                            .put(IntentParamsConstants.PARAMS_RESOURCE_TYPE, editable.sourceType)
                    ARouter.getInstance().build(Paths.PAGE_STUDYLIST_MOVE).with(put).navigation()
                }
            }
        }
    }


    /**
     * 显示资源删除弹窗
     */
    private fun showStudyRoomResourceDeletePop(context: Context, editable: StudyResourceEditable) {
        val studyRoomCourseDeletePop = StudyRoomResourceDeletePop(context,editable)
        studyRoomCourseDeletePop.onConfirmCallback = {
            if(it){  //如果删除成功adapter进行更新
                onEditAbleChange(editable)
            }
        }
        XPopup.Builder(context)
                .asCustom(studyRoomCourseDeletePop) // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .show()
    }

    /**
     * 资源编辑弹窗回调
     * 当编辑的资源改变
     */
    fun onEditAbleChange(editable: StudyResourceEditable)


}