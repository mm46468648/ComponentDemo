package com.mooc.commonbusiness.pop

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.commonbusiness.R

/**
 * 公共底部分享弹窗
 * @param hideSchoolItem 是否隐藏分享到学友圈选项，默认隐藏
 */
class CommonBottomSharePop @JvmOverloads constructor(var mContext: Context, var onItemClick: ((type : Int)->Unit)? = null,var hideSchoolItem : Boolean = true,var onlySchoolShow:Boolean=false) : BottomPopupView(mContext), View.OnClickListener {


    var llShareWx: View?=null
    var llShareWxCircle: View?=null
    var llShareSchool: View?=null
    var ivMiddle: ImageView?=null
    var middleImage : Bitmap? = null
    companion object {
        const val SHARE_TYPE_WX = 0
        const val SHARE_TYPE_WXCIRCLE = 1
        const val SHARE_TYPE_SCHOOL = 2
    }

    override fun getImplLayoutId(): Int {
        return R.layout.common_dialog_share_bottom
    }

    override fun onCreate() {
        super.onCreate()

        llShareWx = findViewById<View>(R.id.tvShareWX)
        llShareWxCircle = findViewById<View>(R.id.tvShareWxCircle)
        llShareSchool = findViewById<View>(R.id.tvShareSchoolCircle)
        ivMiddle = findViewById<ImageView>(R.id.ivMiddle)

        if(middleImage!=null){
            ivMiddle?.setImageBitmap(middleImage)
        }

        if(!hideSchoolItem){
            llShareSchool?.visibility = View.VISIBLE
        }
        if (onlySchoolShow) {
            llShareWx?.visibility = View.GONE
            llShareWxCircle?.visibility = View.GONE
            llShareSchool?.visibility = View.VISIBLE
        }


        llShareWx?.setOnClickListener(this)
        llShareWxCircle?.setOnClickListener(this)
        llShareSchool?.setOnClickListener(this)
    }

    override fun onShow() {
        super.onShow()

    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvShareWX -> onItemClick?.invoke(SHARE_TYPE_WX)
            R.id.tvShareWxCircle -> onItemClick?.invoke(SHARE_TYPE_WXCIRCLE)
            R.id.tvShareSchoolCircle -> onItemClick?.invoke(SHARE_TYPE_SCHOOL)
        }
        dismiss()
    }
}