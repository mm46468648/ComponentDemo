package com.mooc.commonbusiness.pop.studyroom

import android.content.Context
import android.view.LayoutInflater
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.databinding.StudyroomPopStudylistFolderDeleteBinding
//import kotlinx.android.synthetic.main.studyroom_pop_studylist_folder_delete.view.*

/**
 * 学习室文件夹删除弹窗
 */
class FolderDeletePop(mContext : Context,var folderName:String,var okCallback:()->Unit) : CenterPopupView(mContext) {

    private lateinit var inflater: StudyroomPopStudylistFolderDeleteBinding
    override fun getImplLayoutId(): Int {
        return R.layout.studyroom_pop_studylist_folder_delete
    }

    override fun onCreate() {
        super.onCreate()
        inflater = StudyroomPopStudylistFolderDeleteBinding.bind(popupImplView)
        inflater.tvCreateName.text = folderName
        inflater.tvCancel.setOnClickListener { dismiss() }
        inflater.tvOk.setOnClickListener {
            okCallback.invoke()
            dismiss()
        }
    }
}