package com.mooc.resource.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.mooc.common.ktextends.getDrawableRes
import com.mooc.common.ktextends.setDrawLeft
import com.mooc.common.R;
import com.mooc.common.databinding.ViewCommonTitleLayoutBinding
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.utils.StatusBarUtil

//import kotlinx.android.synthetic.main.view_common_title_layout.view.*


/**
 *
 * @ProjectName:统一共同的标题栏控件，支持自适应沉浸式状态栏
 * @Package:
 * @ClassName:
 * @Description:    java类作用描述
 * @Author:         xym
 * @CreateDate:     2020/8/7 3:04 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/7 3:04 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class CommonTitleLayout @JvmOverloads constructor(
    var mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(mContext, attrs, defStyleAttr) {

    var inflater: ViewCommonTitleLayoutBinding? = null

    var middle_text: String? = ""
        set(value) {
            inflater?.commonTitle?.text = value
            inflater?.commonTitle?.visibility = View.VISIBLE
            field = value
        }
    private var middleTextColor: Int = -1
    private var rightTextColor: Int = -1
    var right_text: String? = ""
        set(value) {
            tv_right?.text = value
            tv_right?.visibility = View.VISIBLE
            field = value
        }
    var leftIcon: Int? = -1
        set(value) {
            if (value != null && value != -1) {
                inflater?.ibBack?.setImageResource(value)
                field = value
            }
        }
    private var rightIcon: Int = -1
    var rightSecondIcon: Int = -1 //右边从右数第二个icon
    var tv_right: TextView? = null
    var ib_right: ImageButton? = null
    var ib_right_second: ImageButton? = null
    var statusBarHeight = 0

    init {

        inflater = ViewCommonTitleLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext)

        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.CommonTitleLayout)
        middle_text = ta.getString(R.styleable.CommonTitleLayout_ctl_middle_text)
        middleTextColor = ta.getColor(
            R.styleable.CommonTitleLayout_ctl_middle_text_color,
            ContextCompat.getColor(mContext, R.color.color_2)
        )
        right_text = ta.getString(R.styleable.CommonTitleLayout_ctl_right_text)
        val right_text_drawableleft =
            ta.getResourceId(R.styleable.CommonTitleLayout_ctl_right_text_drawableleft, -1)


        rightTextColor = ta.getColor(
            R.styleable.CommonTitleLayout_ctl_right_text_color,
            ContextCompat.getColor(mContext, R.color.color_2)
        )
        leftIcon = ta.getResourceId(R.styleable.CommonTitleLayout_ctl_left_icon, -1)
        rightIcon = ta.getResourceId(R.styleable.CommonTitleLayout_ctl_right_icon, -1)
        rightSecondIcon = ta.getResourceId(R.styleable.CommonTitleLayout_ctl_right_second_icon, -1)
        //自动适配沉浸式状态栏(minsdk 19 true场景多一些，不需要需要写false）
        val adpterStatusBar = ta.getBoolean(R.styleable.CommonTitleLayout_ctl_adapteStatusBar, true)
        ta.recycle()

//        inflater = ViewCommonTitleLayoutBinding.inflate(LayoutInflater.from(context),this,true)
//        inflate(context, R.layout.view_common_title_layout, this)
        tv_right = findViewById(R.id.tv_right)
        ib_right = findViewById(R.id.ibRightIcon)
        ib_right_second = findViewById(R.id.ibRightSecondIcon)
        if (!TextUtils.isEmpty(right_text)) {
            tv_right?.text = right_text
            tv_right?.visibility = View.VISIBLE
        }
        if (right_text_drawableleft != -1) {
            val drawableLeft = mContext.getDrawableRes(right_text_drawableleft)

            tv_right?.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                null, null, null
            )
            tv_right?.setCompoundDrawablePadding(10)
        }
        if (!TextUtils.isEmpty(middle_text)) {
            inflater?.commonTitle?.text = middle_text
            inflater?.commonTitle?.visibility = View.VISIBLE
        }
        if (leftIcon != null && leftIcon != -1) {
            inflater?.ibBack?.setImageResource(leftIcon!!)
        }
        if (rightIcon != -1) {
            inflater?.ibRightIcon?.setImageResource(rightIcon)
            inflater?.ibRightIcon?.visibility = View.VISIBLE
        }

        if (rightSecondIcon != -1) {
            inflater?.ibRightSecondIcon?.setImageResource(rightSecondIcon)
            inflater?.ibRightSecondIcon?.visibility = View.VISIBLE

        }
        inflater?.commonTitle?.setTextColor(middleTextColor)
        tv_right?.setTextColor(rightTextColor)

        if(right_text_drawableleft!=-1){
            tv_right?.setDrawLeft(right_text_drawableleft,10)
        }

        // 自适应沉浸式状态栏,就是给TitleView加一个高度
        if (adpterStatusBar)
            inflater?.spaceStatusBar?.post {
                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    statusBarHeight
                )
                inflater?.spaceStatusBar?.layoutParams = params
            }

    }


    @Deprecated("请另外一个参数的方法")
    fun setOnLeftClickListener(listener: View.OnClickListener?) {
        if (listener != null)
            inflater?.ibBack?.setOnClickListener(listener)
    }


    /**
     * @method  直接传递方法形式，点击返回按钮
     * @description 描述一下方法的作用
     * @date: 2020/8/7 3:33 PM
     * @author: xym
     * @param
     * @return
     */
    fun setOnLeftClickListener(callback: () -> Unit) {
        inflater?.ibBack?.setOnClickListener(OnClickListener {
            callback.invoke()
        })
    }


    fun setOnRightIconClickListener(listener: View.OnClickListener?) {
        if (listener != null)
            inflater?.ibRightIcon?.setOnClickListener(listener)
    }

    /**
     * 设置从右数第二个icon点击事件
     */
    fun setOnSecondRightIconClickListener(clickCallBack: () -> Unit) {
        inflater?.ibRightSecondIcon?.setOnClickListener {
            clickCallBack.invoke()
        }
    }

    fun setOnRightTextClickListener(listener: View.OnClickListener?) {
        if (listener != null)
            tv_right?.setOnClickListener(listener)

    }

    fun setRightTextUnClickable(isListEmpty: Boolean) {
        tv_right?.isClickable = !isListEmpty
    }

    fun setRightSecondIconRes(rightIcon: Int) {
        inflater?.ibRightSecondIcon?.setImageResource(rightIcon)
        inflater?.ibRightSecondIcon?.visibility = View.VISIBLE
    }

    fun setRightFirstIconRes(rightIcon: Int) {
        inflater?.ibRightIcon?.setImageResource(rightIcon)
        inflater?.ibRightIcon?.visibility = View.VISIBLE
    }

    fun setIbBackVisibal(v: Int) {
        inflater?.ibBack?.visibility = v
    }

    /**
     * 添加额外的布局
     * 展示一些额外的信息
     */
    fun addExtraLayout(view: View) {
        val layoutParams = LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 44.dp2px())
        layoutParams.setMargins(0, statusBarHeight, 0, 0)
        addView(view, layoutParams)
    }
}
