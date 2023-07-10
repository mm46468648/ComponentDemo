package com.mooc.commonbusiness.pop.studyroom

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import com.mooc.commonbusiness.R
import com.lxj.xpopup.core.AttachPopupView
import com.mooc.common.ktextends.dp2px

/**
 * 学习室资源编辑pop
 * 和学习清单资源文件夹编辑复用
 * 通过构造方法 isFolder区分
 * @param isFolder 是否是学习清单文件夹编辑
 *
 * 原来的pop会出现底部显示不全，由于封装的使用的decoverview的原因
 */
class StudyRoomResourceEditPop2(var mContext:Context, var attachView:View,var isFolder:Boolean = false) :
    PopupWindow.OnDismissListener {

//    override fun getImplLayoutId(): Int = R.layout.studyroom_pop_resource_edit

    companion object{
        const val EVENT_MOVE = 0     //移动
        const val EVENT_DELETE = 1   //删除
        const val EVENT_RENAME = 2   //重命名
    }

    var onClickEvent:((event:Int)->Unit)? = null
    lateinit var container : View
    lateinit var mPopup : PopupWindow
    init {
        onCreate()
    }
     fun onCreate() {

         container = LayoutInflater.from(mContext).inflate(R.layout.studyroom_pop_resource_edit, null)

         mPopup =
             PopupWindow(container, 180.dp2px(),  50.dp2px(), true)
         mPopup.setContentView(container)
//        mPopup.setBackgroundDrawable(new ColorDrawable(0x66000000));
         //        mPopup.setBackgroundDrawable(new ColorDrawable(0x66000000));
         mPopup.setOutsideTouchable(true)
         mPopup.setOnDismissListener(this)

        if(isFolder){ //如果是文件夹要显示，重命名
            val tvRename = container.findViewById<TextView>(R.id.tvRename)
            tvRename.setOnClickListener {
                onClickEvent?.invoke(EVENT_RENAME)
                mPopup.dismiss()
            }
            container.findViewById<View>(R.id.divider3).visibility = View.VISIBLE
            tvRename.visibility = View.VISIBLE
        }

         container.findViewById<TextView>(R.id.tvMove).setOnClickListener {
            onClickEvent?.invoke(EVENT_MOVE)
            mPopup.dismiss()
        }

         container.findViewById<TextView>(R.id.tvDelete).setOnClickListener {
            onClickEvent?.invoke(EVENT_DELETE)
            mPopup.dismiss()
        }

    }

    fun show(){
        //获取自身的长宽高
        //		mPopup.showAsDropDown(parent);
//		mPopup.showAsDropDown(parent, 0,0);
        //获取需要在其上方显示的控件的位置信息
        //获取自身的长宽高
        container.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupHeight = container.measuredHeight
        val popupWidth = container.measuredWidth
        val wm = mContext
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)


        val location = IntArray(2)
//        val a = location[0]
//        val b = location[1]
        attachView.getLocationOnScreen(location)
        setBackgroundAlpha(0.5f)

        //在控件下方显示
        mPopup.showAtLocation(
            attachView,
            Gravity.NO_GRAVITY,
            location[0] + attachView.getWidth() - popupWidth / 4 - 10.dp2px(),
            location[1] + attachView.height / 2
        )

//        if (metrics.widthPixels - location[0] > popupWidth) {
//            //在控件下方显示
//            mPopup.showAtLocation(
//                attachView,
//                Gravity.NO_GRAVITY,
//                location[0] + attachView.getWidth() - popupWidth / 4 - 10.dp2px(),
//                location[1] + 2 * popupHeight
//            )
//        } else {
//            container.setBackgroundResource(R.mipmap.home_bg_studyroom_resource_edit_pop)
//            mPopup.showAtLocation(
//                attachView,
//                Gravity.NO_GRAVITY,
//                location[0] - attachView.getWidth() - popupWidth / 4 - 20.dp2px(),
//                location[1] + 2 * popupHeight
//            )
//        }
    }

    override fun onDismiss() {
        setBackgroundAlpha(1.0f)
    }

    fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (mContext as Activity).window.attributes
        lp.alpha = bgAlpha
        (mContext as Activity).window.attributes = lp
    }
}