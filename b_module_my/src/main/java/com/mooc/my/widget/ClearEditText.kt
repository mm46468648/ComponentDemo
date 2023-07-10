package com.mooc.my.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import com.mooc.my.R
import com.mooc.common.ktextends.dp2px
import com.mooc.resource.utils.PixUtil

class ClearEditText(context: Context, attrs: AttributeSet?, defStyle: Int) : EditText(context, attrs, defStyle), OnFocusChangeListener, TextWatcher {
    /**
     * 删除按钮的引用
     */
    private var mClearDrawable: Drawable?
    private var passwordDrawable: Drawable?
    private var passwordDrawableInVisible: Drawable?

    /**
     * 控件是否有焦点
     */
    private var hasAddPadding = false //增加边距
    var firstPaddingRight = 0
    private var hasFoucs = false
    private var isPassword = false
    var passVisible = false
    private var isCenter = false
    private val paint: Paint? = null


    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, android.R.attr.editTextStyle) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
    }

    private fun init(context: Context) {
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        if (mClearDrawable == null) {
            mClearDrawable = resources.getDrawable(
                    R.mipmap.ic_mini_deletegray)
        }
        mClearDrawable?.intrinsicWidth?.let {
            mClearDrawable?.intrinsicHeight?.let { it1 ->
                mClearDrawable?.setBounds(0, 0, it,
                        it1)
            }
        }
        // 默认设置隐藏图标
        // setClearIconVisible(false);
        // 设置焦点改变的监听
        onFocusChangeListener = this
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this)
        if (isPassword && passwordDrawable == null) {
            passwordDrawable = resources.getDrawable(
                    R.mipmap.ic_password_visible)
        }
        if (isPassword && passwordDrawableInVisible == null) {
            passwordDrawableInVisible = resources.getDrawable(
                    R.mipmap.ic_password_invisible)
        }
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //		if (getText().length() == 0 || !hasFoucs) {
//			return;
//		}
        if (text.length == 0 || !hasFoucs) {
            super.onDraw(canvas)
            return
        }
        firstPaddingRight = 0
        if (!hasAddPadding) {
            hasAddPadding = true
            firstPaddingRight = paddingRight
            var paddingRight = firstPaddingRight
            if (isPassword) {
                paddingRight += passwordDrawable?.intrinsicWidth?:0
                paddingRight += 10.dp2px()
            }
            paddingRight += mClearDrawable?.intrinsicWidth?:0
            setPadding(paddingLeft, paddingTop, paddingRight + 10.dp2px(), paddingBottom)
        }
        //		super.onDraw(canvas);
        val width = this.measuredWidth - firstPaddingRight //getTotalPaddingRight
        val height = this.measuredHeight
        val rect = canvas.clipBounds
        if (isPassword&&passwordDrawable!=null) {
            var left: Float = (width - PixUtil.dp2px(4.toFloat())
                    - passwordDrawable!!.intrinsicWidth) + rect.left
            var top: Float = (height - PixUtil.dp2px(16.toFloat())
                    - passwordDrawable!!.intrinsicHeight)
            var bottom: Float = height - PixUtil.dp2px(16.toFloat())
            var right: Float = width - PixUtil.dp2px(4.toFloat()) + rect.left
            if (isCenter) {
                top = (height - passwordDrawable!!.intrinsicHeight) / 2.toFloat()
                bottom = (height + passwordDrawable!!.intrinsicHeight) / 2.toFloat()
            }
            if (!passVisible) {
                passwordDrawable!!.setBounds(left.toInt(), top.toInt(), right.toInt(),
                        bottom.toInt())
                passwordDrawable!!.draw(canvas)
            } else {
                passwordDrawableInVisible!!.setBounds(left.toInt(), top.toInt(),
                        right.toInt(), bottom.toInt())
                passwordDrawableInVisible!!.draw(canvas)
            }
            left = (left - PixUtil.dp2px(17.toFloat())
                    - mClearDrawable!!.intrinsicWidth)
            right = left + mClearDrawable!!.intrinsicWidth
            top = (height - PixUtil.dp2px(14.toFloat())
                    - mClearDrawable!!.intrinsicHeight)
            bottom = height -PixUtil.dp2px(14.toFloat())
            if (isCenter) {
                top = (height - mClearDrawable!!.intrinsicHeight) / 2.toFloat()
                bottom = (height + mClearDrawable!!.intrinsicHeight) / 2.toFloat()
            }
            mClearDrawable!!.setBounds(left.toInt(), top.toInt(), right.toInt(),
                    bottom.toInt())
            mClearDrawable!!.draw(canvas)
        } else {
            if (passwordDrawable != null) {

            val left: Float = (width - PixUtil.dp2px(8.toFloat())
                    - mClearDrawable!!.intrinsicWidth) + rect.left
            var top: Float = (height - PixUtil.dp2px(14.toFloat())
                    - mClearDrawable!!.intrinsicHeight)
            var bottom: Float = height - PixUtil.dp2px(14.toFloat())
            val right: Float = width - PixUtil.dp2px(8.toFloat()) + rect.left
            if (isCenter) {
                top = (height - mClearDrawable!!.intrinsicHeight) / 2.toFloat()
                bottom = (height + mClearDrawable!!.intrinsicHeight) / 2.toFloat()
            }
            mClearDrawable!!.setBounds(left.toInt(), top.toInt(), right.toInt(),
                    bottom.toInt())
            mClearDrawable!!.draw(canvas)

            }
        }
    }

    private fun setPasswordIconVisible(visible: Boolean) {
        passVisible = visible
        /*
		 * if (visible && passwordIcon != null) {
		 * passwordIcon.setVisibility(View.VISIBLE); } else { if (!visible &&
		 * passwordIcon != null) { passwordIcon.setVisibility(View.GONE);
		 * passwordIcon .setImageResource(R.drawable.ic_mini_passwordvisible);
		 * passVisible = false; setInputType(InputType.TYPE_CLASS_TEXT |
		 * InputType.TYPE_TEXT_VARIATION_PASSWORD); } }
		 */
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            var touchable = false
            var password = false
            val width = measuredWidth - firstPaddingRight
            if (!isPassword) {
                if (mClearDrawable != null) {

                val left: Float =(width - PixUtil.dp2px(6.toFloat())
                        - mClearDrawable!!.intrinsicWidth)

                }
                val right: Float = width - PixUtil.dp2px(6.toFloat())
                touchable = event.x > left && event.x < right
            } else {
                passwordDrawable?.let {
                var left: Float = (width - PixUtil.dp2px(6.toFloat())
                        - passwordDrawable!!.intrinsicWidth)
                var right: Float = width - PixUtil.dp2px(6.toFloat())
                password = event.x > left && event.x < right
                left = (left - PixUtil.dp2px(6.toFloat())
                        - mClearDrawable!!.intrinsicWidth)
                right = left + mClearDrawable!!.intrinsicWidth
                touchable = event.x > left && event.x < right
                }
            }
            if (password) {
                passVisible = !passVisible
                inputType = if (passVisible) {
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                } else {
                    (InputType.TYPE_CLASS_TEXT
                            or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                }
            }
            if (touchable) {
                if (hasFoucs) {
                    this.setText("")
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        hasFoucs = hasFocus
        invalidate()
        requestLayout()
        /*
		 * if (hasFocus) { setClearIconVisible(getText().length() > 0);
		 * setPasswordIconVisible(getText().length() > 0); } else {
		 * setClearIconVisible(false); setPasswordIconVisible(false); }
		 */
    }

    fun onInsideFocusChange(v: View?, hasFocus: Boolean) {
        hasFoucs = hasFocus
        invalidate()
        requestLayout()
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (hasFoucs) {
            invalidate()
            requestLayout()
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                   after: Int) {
    }

    override fun afterTextChanged(s: Editable) {}

    init {
        val a = context.obtainStyledAttributes(attrs,
                R.styleable.ClearEditText, defStyle, 0)
        isPassword = a.getBoolean(R.styleable.ClearEditText_isshowpassword,
                false)
        mClearDrawable = a
                .getDrawable(R.styleable.ClearEditText_deleteDrawable)
        passwordDrawable = a
                .getDrawable(R.styleable.ClearEditText_passwordDrawable)
        passwordDrawableInVisible = a
                .getDrawable(R.styleable.ClearEditText_passwordDrawableInvisible)
        isCenter = a.getBoolean(R.styleable.ClearEditText_isCenter, false)
        a.recycle()
        init(context)
    }
}