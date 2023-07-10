package com.mooc.studyproject.window

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mooc.commonbusiness.utils.format.StringFormatUtil.Companion.getStrUserName
import com.mooc.resource.utils.PixUtil.Companion.dp2px
import com.mooc.studyproject.R
import com.mooc.studyproject.model.MemberListBean
import java.util.*

class LearnPlanMembersPop(private val mContext: Context, private val parent: View, private val membersBeans: ArrayList<MemberListBean>) : PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    private var container: View? = null
    private var userName1: TextView? = null
    private var userName2: TextView? = null
    private var userName3: TextView? = null
    private var userName4: TextView? = null
    private var userName5: TextView? = null
    private var userName6: TextView? = null
    private var userName7: TextView? = null
    private var userName8: TextView? = null
    private var rl1: RelativeLayout? = null
    private var rl2: RelativeLayout? = null
    private var rl3: RelativeLayout? = null
    private var rl4: RelativeLayout? = null
    private var rl5: RelativeLayout? = null
    private var rl6: RelativeLayout? = null
    private var rl7: RelativeLayout? = null
    private var rl8: RelativeLayout? = null
    private var like1: ImageView? = null
    private var like2: ImageView? = null
    private var like3: ImageView? = null
    private var like4: ImageView? = null
    private var like5: ImageView? = null
    private var like6: ImageView? = null
    private var like7: ImageView? = null
    private var like8: ImageView? = null
    private var ivClose: ImageView? = null
    private var head1: ImageView? = null
    private var head2: ImageView? = null
    private var head3: ImageView? = null
    private var head4: ImageView? = null
    private var head5: ImageView? = null
    private var head6: ImageView? = null
    private var head7: ImageView? = null
    private var head8: ImageView? = null

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.studyproject_pop_learn_members, null)
        userName1 = container?.findViewById(R.id.user_name1)
        userName2 = container?.findViewById(R.id.user_name2)
        userName3 = container?.findViewById(R.id.user_name3)
        userName4 = container?.findViewById(R.id.user_name4)
        userName5 = container?.findViewById(R.id.user_name5)
        userName6 = container?.findViewById(R.id.user_name6)
        userName7 = container?.findViewById(R.id.user_name7)
        userName8 = container?.findViewById(R.id.user_name8)
        head1 = container?.findViewById(R.id.user_avatar1)
        head2 = container?.findViewById(R.id.user_avatar2)
        head3 = container?.findViewById(R.id.user_avatar3)
        head4 = container?.findViewById(R.id.user_avatar4)
        head5 = container?.findViewById(R.id.user_avatar5)
        head6 = container?.findViewById(R.id.user_avatar6)
        head7 = container?.findViewById(R.id.user_avatar7)
        head8 = container?.findViewById(R.id.user_avatar8)
        rl1 = container?.findViewById(R.id.rl_user_avatar1)
        rl2 = container?.findViewById(R.id.rl_user_avatar2)
        rl3 = container?.findViewById(R.id.rl_user_avatar3)
        rl4 = container?.findViewById(R.id.rl_user_avatar4)
        rl5 = container?.findViewById(R.id.rl_user_avatar5)
        rl6 = container?.findViewById(R.id.rl_user_avatar6)
        rl7 = container?.findViewById(R.id.rl_user_avatar7)
        rl8 = container?.findViewById(R.id.rl_user_avatar8)
        like1 = container?.findViewById(R.id.like1)
        like2 = container?.findViewById(R.id.like2)
        like3 = container?.findViewById(R.id.like3)
        like4 = container?.findViewById(R.id.like4)
        like5 = container?.findViewById(R.id.like5)
        like6 = container?.findViewById(R.id.like6)
        like7 = container?.findViewById(R.id.like7)
        like8 = container?.findViewById(R.id.like8)
        ivClose = container?.findViewById(R.id.iv_member_close)
    }

    private fun initData(members: ArrayList<MemberListBean>) {
        val size = members.size
        setUser1(if (size > 0) members[0] else null)
        setUser2(if (size > 1) members[1] else null)
        setUser3(if (size > 2) members[2] else null)
        setUser4(if (size > 3) members[3] else null)
        setUser5(if (size > 4) members[4] else null)
        setUser6(if (size > 5) members[5] else null)
        setUser7(if (size > 6) members[6] else null)
        setUser8(if (size > 7) members[7] else null)
    }

    private fun setMember(rl: RelativeLayout?, name: TextView?, head: ImageView?, like: ImageView?, bean: MemberListBean?) {
         if (bean != null) {
            rl?.visibility = View.VISIBLE
            name?.visibility = View.VISIBLE
            like?.visibility = View.GONE
            head?.let { Glide.with(mContext).load(bean.avatar).circleCrop().into(it) }
            name?.setText(bean.name?.let { getStrUserName(it) })
            head?.setOnClickListener {
                if (!bean.isLike) {
                    likeMemberListener?.invoke(name, like, bean)
                }
            }
        } else {
            name?.visibility = View.GONE
            rl?.visibility = View.GONE
        }
    }

    var likeMemberListener: ((name: TextView?, like: ImageView?, dataBean: MemberListBean) -> Unit)? = null
    private fun setUser1(dataBean: MemberListBean?) {
        setMember(rl2, userName2, head2, like2, dataBean)
    }

    private fun setUser2(dataBean: MemberListBean?) {
        setMember(rl4, userName4, head4, like4, dataBean)
    }

    private fun setUser3(dataBean: MemberListBean?) {
        setMember(rl3, userName3, head3, like3, dataBean)
    }

    private fun setUser4(dataBean: MemberListBean?) {
        setMember(rl1, userName1, head1, like1, dataBean)
    }

    private fun setUser5(dataBean: MemberListBean?) {
        setMember(rl6, userName6, head6, like6, dataBean)
    }

    private fun setUser6(dataBean: MemberListBean?) {
        setMember(rl5, userName5, head5, like5, dataBean)
    }

    private fun setUser7(dataBean: MemberListBean?) {
        setMember(rl8, userName8, head8, like8, dataBean)
    }

    private fun setUser8(dataBean: MemberListBean?) {
        setMember(rl7, userName7, head7, like7, dataBean)
    }

    private fun initListener() {
        ivClose?.setOnClickListener {
            if (mPopup != null) {
                mPopup?.dismiss()
            }
        }
    }

    private fun initPopup() {
        mPopup = PopupWindow(container, dp2px(300.toFloat()).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT, true)
        mPopup?.contentView = container
        mPopup?.setBackgroundDrawable(ColorDrawable(0))
        mPopup?.isOutsideTouchable = true
        mPopup?.isTouchable = true
        mPopup?.setOnDismissListener(this)
    }

    fun show() {
        if (mPopup == null) {
            initPopup()
        }
        setBackgroundAlpha(0.5f)
        mPopup?.showAtLocation(parent, Gravity.CENTER, 0, 0)
    }

    private fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (mContext as Activity).window
                .attributes
        lp.alpha = bgAlpha
        mContext.window.attributes = lp
    }

    override fun onDismiss() {
        setBackgroundAlpha(1.0f)
    }

    init {
        initView()
        initData(membersBeans)
        initListener()
        initPopup()
    }
}