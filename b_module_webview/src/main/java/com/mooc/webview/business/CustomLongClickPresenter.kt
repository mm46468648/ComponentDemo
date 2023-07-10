package com.mooc.webview.business

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import androidx.core.view.size
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.common.utils.ScreenUtil
import com.mooc.webview.stratage.WebViewInitStrategy
import com.mooc.webview.R
import com.mooc.webview.x5kit.X5kitWebView

/**
 * 自定义的长按弹窗处理器
 */
class CustomLongClickPresenter constructor(var mContext: Context, var strategy: WebViewInitStrategy){

    var x5Webview : X5kitWebView? = null

    /**
     * 寻找长按复制弹窗，并替换自己的定义的View
     *
     * 当视频容器未加入视图时，Webview中只有一个承载复制弹窗的容器，
     * 如果点击播放了视频，这时候Webview中有两个Framlayout，而且这两个FragmLayout的顺序跟出现顺序有关系
     * 一步小心可能把视频播放控件remove掉，
     * 所以当第一次找到复制弹窗的时候要加一个Tag标记
     * 如果找到了标记位，直接替换标记位里的view
     * @param view
     */
    val CopyFramTag = "myCustomCopyPopTag"
    fun foreachView(view: View) {
        if (view is ViewGroup) {
            if (view.childCount <= 0) return
            for (i in 0 until view.childCount) {
                val childAt = view.getChildAt(i)
                if (CopyFramTag == childAt.tag) {
                    repleaceCopyPopView(childAt as FrameLayout)
                    return
                }
            }
            val childAt1 = view.getChildAt(view.childCount - 1)
            if (view is FrameLayout && view !is X5kitWebView) {
                view.setTag(CopyFramTag)
                val frameLayout = view
                loge("X5Webview", "childCount:" + frameLayout.childCount)
                repleaceCopyPopView(frameLayout)
                return
            }
            foreachView(childAt1)
        }
    }

    /**
     * 替换复制弹窗的方法
     * @param frameLayout
     */
    private fun repleaceCopyPopView(frameLayout: FrameLayout) {
        val childAt = frameLayout.getChildAt(0)
        frameLayout.removeView(childAt)
        val customCopyLayout = createCustomCopyLayout()
        frameLayout.postDelayed({
            //                frameLayout.setLayoutParams(layoutParams);
            frameLayout.addView(customCopyLayout)

            //监听布局变化
            frameLayout.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                val x = frameLayout.x

                //容器的宽度
                val containerWidth =  x5Webview?.width?:ScreenUtil.getScreenWidth(v.context)
                val v1: Int = containerWidth - 200.dp2px() //最小距离
                //如果弹窗位置，超出了屏幕,将弹窗平移进来,
                if (x > containerWidth - v1) {
                    loge("X5Webview", "x: " + x + " v1: " + v1 + " tranns: " + (x - v1))
                    frameLayout.animate().translationX(v1 - x - 10.dp2px()).start()
                }

                //如果在屏幕左边
                if(x < 0f){
                    frameLayout.animate().translationX(-x + 10.dp2px()).start()
                }
            }
        }, 500)
    }

    private fun createCustomCopyLayout(): View {
        val linearLayout = LinearLayout(mContext)
        val layoutParams = LinearLayout.LayoutParams(200.dp2px() as Int, 40.dp2px() as Int)
        linearLayout.layoutParams = layoutParams
        linearLayout.setBackgroundResource(R.drawable.shap_corner2_white_stroke1_ededed)
        val childLayoutParams = LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT, 1f)
        //		View actionSelectView = LayoutInflater.from(mContext).inflate(R.layout.layout_longpress_copywindow,null);
        val textView = TextView(mContext)
        textView.text = "复制"
        textView.setTextColor(Color.BLACK)
        textView.gravity = Gravity.CENTER
        textView.layoutParams = childLayoutParams
        val textView1 = TextView(mContext)
        textView1.text = "笔记"
        textView1.setTextColor(Color.BLACK)
        textView1.gravity = Gravity.CENTER
        textView1.layoutParams = childLayoutParams
        val textView2 = TextView(mContext)
        textView2.text = "分享"
        textView2.setTextColor(Color.BLACK)
        textView2.gravity = Gravity.CENTER
        textView2.layoutParams = childLayoutParams
        linearLayout.addView(textView)
        linearLayout.addView(textView1)
        linearLayout.addView(textView2)
        textView.setOnClickListener { v: View? ->
            getSelectedData("复制")
            performClickEvent()
        }
        textView1.setOnClickListener { v: View? ->
            getSelectedData("笔记")
//            performClickEvent()
        }
        textView2.setOnClickListener { v: View? ->
            getSelectedData("分享")
//            performClickEvent()
        }
        return linearLayout
    }


    /**
     * 注入js，获取选中文字
     *
     * @param title
     */
    private fun getSelectedData(title: String) {


        val js = "(function getSelectedText() {" +
                "var txt;" +
                "var title = \"" + title + "\";" +
                "if (window.getSelection) {" +
                "txt = window.getSelection().toString();" +
                "} else if (window.document.getSelection) {" +
                "txt = window.document.getSelection().toString();" +
                "} else if (window.document.selection) {" +
                "txt = window.document.selection.createRange().text;" +
                "}" +
                "ActionSelectInterface.callback(txt,title);" +
                "})()"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            strategy?.evaluateJavascript("javascript:" + js, null)
        } else {
            strategy?.loadUrl("javascript:$js")
        }
    }

    /**
     * 模拟点击事件，达到复制弹窗消失目的
     */
    fun performClickEvent() {
        val me = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0f, 0f, 0)
        //        onTouch(mPageWidget, me);
        strategy.dispatchTouchEvent(me)
    }


    //--------Android 系统内核实现自定义长按事件的方法------------------

    var mActionMode: ActionMode? = null
    var mActionArray = arrayOf("复制", "笔记", "分享")
    fun resolveActionMode(actionMode: ActionMode?): ActionMode? {
        if (actionMode != null) {
            val menu = actionMode.menu
            mActionMode = actionMode
            menu.clear()
            for (s in mActionArray) {
                menu.add(s)
            }
            for (i in 0 until menu.size()) {
                val menuItem = menu.getItem(i)
                menuItem.setOnMenuItemClickListener { item: MenuItem ->
                    getSelectedData((item.title as String))
                    releaseAction()
                    true
                }
            }
        }
        mActionMode = actionMode
        return actionMode
    }


    /**
     * 自定义callback，用于菜单过滤
     *
     * @param callback
     * @return
     */
    public fun buildCustomCallback(callback: ActionMode.Callback): ActionMode.Callback {
        val customCallback: ActionMode.Callback =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                object : ActionMode.Callback2() {

                    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        if(mode == null || menu == null) return false
                        return callback.onCreateActionMode(mode, menu);
                    }

                    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        callback.onPrepareActionMode(mode, menu);

                        addCustomMenu(mode);

                        val size = menu?.size ?: 0
                        for (i in 0 until size) {
                            val menuItem = menu?.getItem(i);

                            if (menuItem?.getItemId() == 0) {
                                //自定义或是通过PROCESS_TEXT方案加入到菜单中的选项，item都为0
                                val intent = menuItem.getIntent();
                                val componentName =
                                    if (intent == null) null else intent.getComponent();
                                //根据包名比较菜单中的选项是否是本app加入的
                                if (componentName != null && mContext.getPackageName()
                                        .equals(componentName.getPackageName())
                                ) {
                                    menuItem.setVisible(true);
                                } else {
                                    menuItem.setVisible(false);
                                }
                            } else {
                                menuItem?.setVisible(false);
                            }

                        }
                        return true;
                    }

                    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                        if (item == null || TextUtils.isEmpty(item.getTitle())) {
                            return callback.onActionItemClicked(mode, item);
                        }
                        val title = item.getTitle().toString();
                        if (mActionArray.contains(title)) {
                            try {
                                getSelectedData(title);
                                //延迟release，防止js获取不到选中的文字
                                runOnMainDelayed(200) {
                                    releaseAction();
                                };
                            } catch (e: Exception) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                        return callback.onActionItemClicked(mode, item);
                    }

                    override fun onDestroyActionMode(mode: ActionMode?) {
                        callback.onDestroyActionMode(mode);
                    }

                    override fun onGetContentRect(mode: ActionMode?, view: View?, outRect: Rect?) {
                        if (callback is ActionMode.Callback2) {
                            val tempCallback2 = callback as ActionMode.Callback2;
                            tempCallback2.onGetContentRect(mode, view, outRect);
                        } else {
                            super.onGetContentRect(mode, view, outRect);
                        }
                    }
                };
            } else {
                callback
            }
        return customCallback;
    }

    /**
     * 添加自定义菜单
     *
     * @param actionMode
     */
    fun addCustomMenu(actionMode: ActionMode?): ActionMode? {
        if (actionMode != null) {
            val menu = actionMode.getMenu();
            var groupId = 0
            //查找"复制"选项的信息
            menu.forEach { menuItem ->
                if ("复制".equals(menuItem.getTitle())) {
                    groupId = menuItem.getGroupId();
                }
            }
            //添加自定义选项

            val size = mActionArray.size;

            for (i in 0 until size) {
                //intent主要用于过滤菜单时使用
                val intent = Intent()
                intent.setComponent(ComponentName(mContext.getPackageName(), ""));
                val title = mActionArray.get(i);
                //非系统选项，itemId只能为0，否则会崩溃（ Unable to find resource ID）
                //order可以自己选择控制，但是有些rom不行
                menu.add(groupId, 0, 0, title).setIntent(intent);
            }
            mActionMode = actionMode;
        }
        return actionMode;
    }

    private fun releaseAction() {
        if (mActionMode != null) {
            mActionMode?.finish()
            mActionMode = null
        }
    }
}