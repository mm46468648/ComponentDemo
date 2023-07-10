package com.mooc.commonbusiness.pop

import android.content.Context
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.databinding.CommonPopAlertBinding

//import kotlinx.android.synthetic.main.common_pop_alert.view.*

/**
 * 公共的提示弹窗
 */
class CommonAlertPop(mConText:Context,var content:CharSequence
                     ,var confirm:Confirm? = null
                     ,var cancle:Cancle? = null
                     ): CenterPopupView(mConText){

    private lateinit var inflater: CommonPopAlertBinding
    override fun getImplLayoutId(): Int {
        return R.layout.common_pop_alert
    }

    override fun onCreate() {
        super.onCreate()
        inflater = CommonPopAlertBinding.bind(popupImplView)
        inflater.tvContent.setText(content)
        //不需要按钮
        if(confirm == null && cancle==null){
            inflater.llBottom.visibility = View.GONE
        }
        //只有一个按钮
        if(confirm!=null && cancle == null){
            inflater.btCancle.visibility = View.GONE
            inflater.btConfirm.visibility = View.GONE
            inflater.btSingle.visibility = View.VISIBLE

            confirm?.let { confirm->
                inflater.btSingle.setText(confirm.text)
            }

            inflater.btSingle.setOnClickListener {
                dismiss()
            }
        }

        //两者都不为空
        if(confirm!=null && cancle != null){
           confirm?.let { confirm->
               if(confirm.position == Position.LEFT){   //如果在左边
                   inflater.btCancle.setBackgroundResource(R.mipmap.common_ic_dialog_left)
                   inflater.btCancle.setText(confirm.text)
                   inflater.btCancle.setOnClickListener {
                       dismiss()
                       confirm.confirmBack?. invoke()}
               }

               if(confirm.position == Position.RIGHT){   //如果在右边
                   inflater.btConfirm.setBackgroundResource(R.mipmap.common_bg_pop_ok_right)
                   inflater.btConfirm.setText(confirm.text)
                   inflater.btConfirm.setOnClickListener {
                       dismiss()
                       confirm.confirmBack?. invoke()}
               }
           }

            cancle?.let { cancle->
                if(cancle.position == Position.LEFT){   //如果在左边
                    inflater.btCancle.setBackgroundResource(R.mipmap.common_bg_pop_cancel_left)
                    inflater.btCancle.setText(cancle.text)
                    inflater.btCancle.setOnClickListener {
                        dismiss()
                        cancle.cancleBack?. invoke()}
                }

                if(cancle.position == Position.RIGHT){   //如果在右边
                    inflater.btConfirm.setBackgroundResource(R.mipmap.common_ic_dialog_right)
                    inflater.btConfirm.setText(cancle.text)
                    inflater.btConfirm.setOnClickListener {
                        dismiss()
                        cancle.cancleBack?. invoke()}
                }
            }
        }


    }

    /**
     * 取消接口
     */
    interface Cancle{
        val position:Int    //0,在左边，1在右边
        val text:String
        val cancleBack:(()->Unit)?
    }

    /**
     * 确定接口
     */
    interface Confirm{
        val position:Int //0,在左边，1在右边
        val text:String
        val confirmBack:(()->Unit)?
    }

    /**
     * 位置
     */
    @Target(AnnotationTarget.TYPE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Position {
        companion object{
            const val LEFT = 0
            const val RIGHT = 1
            const val MIDDLE = 2   //中间只有一个按钮
        }
    }
}