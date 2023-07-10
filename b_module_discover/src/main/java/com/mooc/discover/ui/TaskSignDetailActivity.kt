package com.mooc.discover.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.ExtraDelegate
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.NetUtils
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.dialog.PublicOneDialog
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.discover.R
import com.mooc.discover.databinding.ActivityTaskSignDetailBinding
import com.mooc.discover.viewmodel.TaskViewModel
import com.qmuiteam.qmui.util.QMUIKeyboardHelper
import com.qmuiteam.qmui.util.QMUINotchHelper
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
//import kotlinx.android.synthetic.main.activity_task_sign_detail.*
import java.lang.reflect.Field


/**
 *早起打卡任务，打卡页面
 * @date 2022/07/25
 */
@Route(path = Paths.PAGE_TASK_SIGN_DETAIL)
class TaskSignDetailActivity : BaseActivity() {


    val mViewModel: TaskViewModel by viewModels()


    val taskId by ExtraDelegate(IntentParamsConstants.PARAMS_TASK_ID, "")

    private var resourceId: Int = -1
    private var dataContent: String = ""
    private lateinit var inflater: ActivityTaskSignDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityTaskSignDetailBinding.inflate(layoutInflater)
        setContentView(inflater.root)
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        initData()
        initListener()
    }

    private fun initData() {
//        disableCopyAndPaste(etTaskSign)
        //请求打卡文案
        if (NetUtils.isNetworkConnected()) {
            mViewModel.getEarlyTaskContent()
        } else {
            toast(getString(R.string.net_error))
        }
        mViewModel.taskEarlyCheckin.observe(this, {
            if (it.code == 200) {
                toast(it.message)
                finish()
            } else if (it.code == 4201) {
                if (it.msg.isNotEmpty()) {
                    showTipDialog(it.msg)
                }
            } else {
                toast(it.message)
            }
        })

        mViewModel.taskEarlyContent.observe(this, {
            if (it.code == 200) {
                inflater.tvTaskSignContent.text = it.data.content
                dataContent = it.data.content
                resourceId = it.data.resource_id
            } else {
                inflater.tvTaskSignContent.text = ""
            }
        })
    }


    fun setViewTreeListener(context: Context) {
        val decorView = window.decorView
        inflater.rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                inflater.rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                if (QMUIKeyboardHelper.isKeyboardVisible(context as Activity?)) {
//                    pading.layoutParams =
//                        LinearLayout.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            dp(context, 300f)
//                        )
//                } else {
//                    //隐藏软键盘
//                    pading.layoutParams =
//                        LinearLayout.LayoutParams(
//                            ViewGroup.LayoutParams.MATCH_PARENT,
//                            dp(context, 15f)
//                        )
//                }
                val rect = Rect()
                decorView.getWindowVisibleDisplayFrame(rect)

                val height: Int =
                    inflater.rootView.getContext().getResources().getDisplayMetrics().heightPixels
                // 获取键盘抬高的高度
                // 获取键盘抬高的高度
                val diff: Int =
                    height - rect.bottom
                var pading: Int = 0;
                if (QMUINotchHelper.hasNotch(context as Activity)) {
                    pading = diff + QMUIStatusBarHelper.getStatusbarHeight(context);
                } else {
                    pading = diff;
                }

                if (pading != 0) {
                    if (inflater.rootView.getPaddingBottom() !== diff) {
                        inflater.scrollView.post({
                            inflater.scrollView.fullScroll(View.FOCUS_DOWN)
                        });
                        // 将聊天记录定位到最后一行
                        inflater.rootView.setPadding(0, 0, 0, pading)
                    }
                } else {
                    if (inflater.rootView.getPaddingBottom() !== 0) {
                        inflater.rootView.setPadding(0, 0, 0, 0)
                    }
                }

                inflater.rootView.postDelayed({
                    setViewTreeListener(context)
                }, 100)

            }

        });
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setViewTreeListener(this)
        }
    }

    var isScroll = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListener() {

        inflater.scrollView.setOnTouchListener(object : OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isScroll = false;
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isScroll) {
                            QMUIKeyboardHelper.hideKeyboard(inflater.etTaskSign);
                        }
                        return true;
                    }
                }
                return false;
            };

        })

        /**
         * 滑动监听
         */
        inflater.scrollView.setOnCustomScrollListener { scrollX, scrollY, oldScrollX, oldScrollY ->
            isScroll = true
        }

        inflater.commonTitleTaskSign.setOnLeftClickListener { finish() }
        //输入框文字监听
        inflater.etTaskSign.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    inflater.tvSubmit.visibility = View.VISIBLE
                } else {
                    inflater.tvSubmit.visibility = View.GONE
                }
            }

        })
        //输入框是否获取焦点
        @Suppress("DEPRECATION")
        inflater.etTaskSign.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {//获取焦点
                inflater.etTaskSign.background =
                    resources.getDrawable(R.drawable.shape_radius20_stroke_color_green)
            } else {
                inflater.etTaskSign.background =
                    resources.getDrawable(R.drawable.shape_radius20_solid_color_f)
            }
        }
        //提交点击事件
        inflater.tvSubmit.setOnClickListener {
            if (NetUtils.isNetworkConnected()) {
                //输入框文字
                val etStr = inflater.etTaskSign.text.toString().trim()
                submitContent(etStr)
            } else {
                toast(getString(R.string.net_error))
            }

        }
    }


    fun submitContent(content: String) {
        if (TextUtils.isEmpty(content)) {
            toast("输入内容为空")
            return
        }
        if (TextUtils.isEmpty(dataContent)) {
            toast("返回文本为空")
            return
        }
        if (resourceId == -1) {
            toast("resource_id为空")
            return
        }

        mViewModel.postTaskEarlyCheckin(resourceId.toString(), taskId, content)
    }

    fun showTipDialog(msg: String) {
        val publicDialogBean = PublicDialogBean()
        if (msg.isNotEmpty()) {
            publicDialogBean.strMsg = msg
        } else {
            publicDialogBean.strMsg = getString(R.string.str_early_content_tip)
        }
        publicDialogBean.strTv = resources.getString(R.string.text_ok)

        val dialog = PublicOneDialog(this, publicDialogBean)
        XPopup.Builder(this)
            .asCustom(dialog)
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun disableCopyAndPaste(editText: EditText?) {
        try {
            if (editText == null) {
                return
            }
            editText.setOnLongClickListener(OnLongClickListener { true })
            editText.isLongClickable = false
            editText.setOnTouchListener(OnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    // setInsertionDisabled when user touches the view
                    setInsertionDisabled(editText)
                }
                false
            })
            editText.setCustomSelectionActionModeCallback(object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {}
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setInsertionDisabled(editText: EditText) {
        try {
            val editorField: Field = TextView::class.java.getDeclaredField("mEditor")
            editorField.setAccessible(true)
            val editorObject: Any = editorField.get(editText)

            // if this view supports insertion handles
            val editorClass = Class.forName("android.widget.Editor")
            val mInsertionControllerEnabledField: Field =
                editorClass.getDeclaredField("mInsertionControllerEnabled")
            mInsertionControllerEnabledField.setAccessible(true)
            mInsertionControllerEnabledField.set(editorObject, false)

            // if this view supports selection handles
            val mSelectionControllerEnabledField: Field =
                editorClass.getDeclaredField("mSelectionControllerEnabled")
            mSelectionControllerEnabledField.setAccessible(true)
            mSelectionControllerEnabledField.set(editorObject, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}