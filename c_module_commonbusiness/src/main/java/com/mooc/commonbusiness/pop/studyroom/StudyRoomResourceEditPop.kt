package com.mooc.commonbusiness.pop.studyroom

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.lxj.xpopup.core.AttachPopupView
import com.mooc.commonbusiness.R

/**
 * 学习室资源编辑pop
 * 和学习清单资源文件夹编辑复用
 * 通过构造方法 isFolder区分
 * @param isFolder 是否是学习清单文件夹编辑
 */
@SuppressLint("ViewConstructor")
class StudyRoomResourceEditPop(
        var mContext: Context,
        var attatchView: View,
        var isFolder: Boolean = false
) : AttachPopupView(mContext) {

    override fun getImplLayoutId(): Int = R.layout.studyroom_pop_resource_folder_edit

    companion object {
        const val EVENT_MOVE = 0     //移动
        const val EVENT_DELETE = 1   //删除
        const val EVENT_RENAME = 2   //重命名
    }

    var onClickEvent: ((event: Int) -> Unit)? = null
    override fun onCreate() {
        super.onCreate()

        if (isFolder) { //如果是文件夹要显示，重命名
            val tvRename = findViewById<TextView>(R.id.tvRename)
            tvRename.setOnClickListener {
                onClickEvent?.invoke(EVENT_RENAME)
                dismiss()
            }
            findViewById<View>(R.id.divider3).visibility = View.VISIBLE
            tvRename.visibility = View.VISIBLE
        }

        findViewById<TextView>(R.id.tvMove).setOnClickListener {
            onClickEvent?.invoke(EVENT_MOVE)
            dismiss()
        }

        findViewById<TextView>(R.id.tvDelete).setOnClickListener {
            onClickEvent?.invoke(EVENT_DELETE)
            dismiss()
        }

    }

    override fun onShow() {
        super.onShow()

        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)

        val location = IntArray(2)
        attatchView.getLocationOnScreen(location)
        if (location[0] + attatchView.width / 2 > metrics.widthPixels / 2) {
            findViewById<View>(R.id.llRoot).setBackgroundResource(R.mipmap.home_bg_study_resource_edit_pop_right)
        } else {
            findViewById<View>(R.id.llRoot).setBackgroundResource(R.mipmap.home_bg_studyroom_resource_edit_pop)
        }

    }
}