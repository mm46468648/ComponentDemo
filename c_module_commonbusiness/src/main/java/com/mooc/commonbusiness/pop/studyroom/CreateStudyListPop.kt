package com.mooc.commonbusiness.pop.studyroom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import com.mooc.commonbusiness.R
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.databinding.StudyroomPopCreateStudylistFolderBinding
import com.mooc.commonbusiness.model.studyroom.FolderItem
//import kotlinx.android.synthetic.main.studyroom_pop_create_studylist_folder.view.*

/**
 * 创建学习清单弹窗
 */
class CreateStudyListPop(context: Context,var folderItem: FolderItem? = null) : CenterPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.studyroom_pop_create_studylist_folder
    }

    var onConfirmCallBack : ((name:String)->Unit)? = null
    private val maxCount = 14
    lateinit var inflater: StudyroomPopCreateStudylistFolderBinding
    override fun onCreate() {
        super.onCreate()

        inflater = StudyroomPopCreateStudylistFolderBinding.bind(popupImplView)
        inflater.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                //监听输入框改变，显示隐藏，删除按钮
                setDeleteButtion(inflater.editText.text.isNotEmpty())
                //设置字数提示
                inflater.tvCount.text = p0.toString().trim { it <= ' ' }.length.toString() + "/" + maxCount

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        //如果不为空，则是重命名
        if(folderItem!= null){
            inflater.tvTitle.text = "重命名学习清单"
            inflater.editText.setText(folderItem?.name?:"")
            inflater.editText.setSelection(folderItem?.name?.length?:0)
        }

        inflater.ibDelete.setOnClickListener {
            //重置输入框
            inflater.editText.setText("")
        }

        //回调确认
        inflater.tvConfirm.setOnClickListener {
            val text = inflater.editText.text.toString().trim()
            if(text.isEmpty()){
                toast("文件夹名字不能为空")
                return@setOnClickListener
            }
            onConfirmCallBack?.invoke(text)
//            dismiss()
        }

        //
        inflater.tvCancel.setOnClickListener {
            //消失并隐藏输入框
            dismiss()
        }

    }

    fun setDeleteButtion(show:Boolean){
        val visiable = if(show) View.VISIBLE else View.GONE
        inflater.ibDelete.visibility = visiable
    }
}