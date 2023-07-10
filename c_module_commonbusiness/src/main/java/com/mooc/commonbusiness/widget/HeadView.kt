package com.mooc.commonbusiness.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mooc.common.ktextends.visiable
import com.mooc.commonbusiness.R

/**

 * @Author limeng
 * @Date 1/20/22-2:28 PM
 */
class HeadView @JvmOverloads constructor(var mContext: Context, var attrs: AttributeSet? = null, var defaultInt: Int = 0) : ConstraintLayout(mContext, attrs, defaultInt) {
    var head: ImageView? = null
    var headTag: ImageView? = null
    var moocNoIdentity: ImageView? = null
    var RlHead: RelativeLayout? = null
//    var headUrl: String? = null
//    var headTagUrl: String? = null

    init {
        val view = LayoutInflater.from(mContext).inflate(R.layout.common_head, this)
        initView(view)
        initAttrs()


    }

    private fun initView(view: View) {

        head = view.findViewById<ImageView>(R.id.head);
        RlHead = view.findViewById<RelativeLayout>(R.id.rl_head);
        headTag = view.findViewById<ImageView>(R.id.headTag);

    }

    private fun initAttrs() {

    }

    fun setHeadImage(avatar: String?, avatar_identity: String?) {
        if (!avatar.isNullOrEmpty() && !avatar_identity.isNullOrEmpty()) {// 头像和头像框都有
            head?.visiable(true)
            headTag?.visiable(true)
            setTagView(avatar_identity, headTag)
            setHeadView(avatar, head)
        } else if (!avatar.isNullOrEmpty() && avatar_identity.isNullOrEmpty()) {// 只有头像 没有头像框
            head?.visiable(true)
            headTag?.visiable(false)
            setHeadView(avatar,head)
        }else if (avatar.isNullOrEmpty() && !avatar_identity.isNullOrEmpty()) {// 没有头像 有头像框
            head?.visiable(true)
            headTag?.visiable(true)
            setHeadView("",head)
            setTagView(avatar_identity, headTag)
        } else {//显示默认头像
            head?.visiable(true)
            headTag?.visiable(false)
            setHeadView("",head)

        }
    }

    fun setHeadView(url: String?, view: ImageView?) {
        if (view != null) {
            Glide.with(context)
                    .load(url)
                    .error(R.mipmap.common_ic_user_head_default)
                    .circleCrop()
                    .into(view)
        }

    }
    fun setTagView(url: String?, view: ImageView?) {
        if (view != null) {
            Glide.with(context)
                    .load(url)
                    .into(view)
        }

    }

}