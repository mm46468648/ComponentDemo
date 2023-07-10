package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mooc.common.R;
import com.mooc.common.databinding.CommonViewSettingItemBinding
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.ktextention.setDrawLeft
import com.mooc.resource.ktextention.setDrawRight
//import kotlinx.android.synthetic.main.common_view_setting_item.view.*

/**
 *
 * @ProjectName:    用户页面小条目View
 * @Package:
 * @ClassName:
 * @Description:    用户左中右简单结构布局
 * @Author:         xym
 * @CreateDate:     2020/8/18 10:52 AM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/18 10:52 AM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class CommonSettingItem @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        FrameLayout(mContext, attributeSet, defaultInt) {

    var inflater: CommonViewSettingItemBinding =
        CommonViewSettingItemBinding.inflate(LayoutInflater.from(context),this)

    var leftText = ""
        set(value) {
            inflater.tvLeft.text = value
            field = value
        }
    var rightText = ""
        set(value) {
            inflater.tvRight.text = value
            field = value
        }
    var leftIcon = -1
        set(value) {
            if (value != -1)
                inflater.tvLeft.setDrawLeft(value)
        }
    var rightIcon = -1
        set(value) {
            if (value != -1) {
                inflater.tvRight.setDrawRight(value)
            }
        }
    var showLine = true
        set(value) {
            val visible = if (value) View.VISIBLE else View.GONE
            inflater.viewLine.visibility = visible
            field = value
        }



    init {
//        LayoutInflater.from(mContext).inflate(R.layout.common_view_setting_item, this)

        val a = context.obtainStyledAttributes(attributeSet, R.styleable.CommonSettingItem)
        leftText = a.getString(R.styleable.CommonSettingItem_csi_left_text) ?: ""
        rightText = a.getString(R.styleable.CommonSettingItem_csi_right_text) ?: ""
        leftIcon = a.getResourceId(R.styleable.CommonSettingItem_csi_left_icon, -1)
        rightIcon = a.getResourceId(R.styleable.CommonSettingItem_csi_right_icon, -1)
        showLine = a.getBoolean(R.styleable.CommonSettingItem_csi_showLine, true)

        a.recycle()

        setPadding(15.dp2px(), 0, 15.dp2px(), 0)


    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, 50.dp2px())
    }

    fun setRightTextClickFunction(rightClick: () -> Unit) {
        inflater.tvRight.setOnClickListener { rightClick.invoke() }
    }

    /**
     * 添加额外的布局
     * 展示一些额外的信息
     */
    fun addExtraLayout(view: View) {
        addView(view, LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 50.dp2px()))
    }
}