package com.mooc.webview.business

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.extraDelegate
import com.mooc.common.utils.GsonManager
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.VerifyCode
import com.mooc.commonbusiness.model.eventbus.ArticleReadFinishEvent
import com.mooc.commonbusiness.route.Paths
import com.mooc.webview.htmlparser.HtmlParser
import com.mooc.webview.interfaces.VerifyArticleCallBack
import com.mooc.webview.model.Point
import com.mooc.webview.model.PostVerifyBean
import com.mooc.webview.pop.VerifyPop
import com.mooc.webview.widget.TaskCutDownTipView
import com.mooc.webview.widget.TaskTimeTipView
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedScrollLayout
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopDelegateLayout
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopWebView
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
//import kotlinx.android.synthetic.main.webview_activity_webview.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 *
 * 再加载信息中的链接
 */
@Route(path = Paths.PAGE_WEB_VERIFYCODE)
open class VerifyCodeWebActivity : BaseResourceWebviewActivity() {


    val is_task by extraDelegate(IntentParamsConstants.WEB_PARAMS_IS_TASK, "false")
    val task_finish by extraDelegate(IntentParamsConstants.WEB_PARAMS_TASK_FINISH, "false");
    val verifyTime by extraDelegate(IntentParamsConstants.WEB_PARAMS_TASK_COUNT_DOWN, "40");

    var defaultVerifyTime = verifyTime.toInt() * 1000

    val folder_id by extraDelegate(IntentParamsConstants.STUDYROOM_FOLDER_ID, "")
    var id: Int = 0

    /**
     * 定时器
     */
    private val timer: Timer = Timer("taskTimer")

    /**
     * 定时任务
     */
    private var timerTask: MyTimeTask? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val taskFinish = task_finish.toBoolean();
        val isTask = is_task.toBoolean();
        //如果是任务文章并且没有完成
//        userVerifyCode(taskFinish, isTask)
//        userVerifyCode(false, true)

    }

    override fun getContentView() {
        if (!task_finish.toBoolean() && is_task.toBoolean()) {
            addWebviewWrapper()
        } else {
            super.getContentView()
        }
    }

//    /**
//     * 判断是否需要验证码
//     */
//    private fun userVerifyCode(taskFinish: Boolean, isTask: Boolean) {
//        if (!taskFinish && isTask) {
//            mViewModel.upVerifyLiverData.observe(this, {
//                if (it.code == 200) {
//                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
//                    articlelVerfyPop?.verifySuccess()
//                    EventBus.getDefault().post(ArticleReadFinishEvent(resourceID))
//                } else {
//                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
//                    refreshVerifyImg()
//                }
//            })
//        }
//    }


    val taskTipView by lazy {
        TaskTimeTipView(this)
    }

    override fun userX5(): Boolean {
        return false
    }

    var dispose: Disposable? = null;

    override fun loadUrl() {
        super.loadUrl()
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    val flayoutParams = FrameLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )


    var mTopDelegateLayout: QMUIContinuousNestedTopDelegateLayout? = null;
    var mCoordinatorLayout: QMUIContinuousNestedScrollLayout? = null;

    private fun addWebviewWrapper() {

        mCoordinatorLayout = QMUIContinuousNestedScrollLayout(this);

        mTopDelegateLayout = QMUIContinuousNestedTopDelegateLayout(this)
        mTopDelegateLayout?.setHeaderView(taskTipView)


        val mWebView = webviewWrapper.getView()

        mTopDelegateLayout?.setDelegateView(mWebView as QMUIContinuousNestedTopWebView)

        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        val webViewLp = CoordinatorLayout.LayoutParams(
            matchParent, matchParent
        )
        mCoordinatorLayout?.setTopAreaView(mTopDelegateLayout, webViewLp)


        inflater.flRoot.addView(mCoordinatorLayout, 0, flayoutParams)


        webviewWrapper.onLoadProgressChange = { p ->
            inflater.progressBar.progress = p
            if (p >= 99) {
                webviewWrapper.onLoadProgressChange = {}
                inflater.progressBar.visibility = View.GONE
                loadPreviewImageAndFilterWxLinkJs()


                //计算文章字数
                dispose = Observable.create(ObservableOnSubscribe<Int> { emitter ->
                    val htmlStr = HtmlParser.getText(loadUrl);
                    emitter.onNext(htmlStr.length);
                }).compose(RxUtils.applySchedulers()).subscribe({ t ->
                    //根据字数计算阅读时间
                    defaultVerifyTime = t * 1000 / 700 * 60;
                    val addEbookGifView = addEbookGifView(defaultVerifyTime)
                    startCutdown(addEbookGifView)

                }, {
                    //字数统计失败，按默认时间
                    val addEbookGifView = addEbookGifView(defaultVerifyTime)
                    startCutdown(addEbookGifView)
                })
            } else {
                if (inflater.progressBar.visibility == View.GONE) {
                    inflater.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

//    var articlelVerfyPop: ArticlelVerfyPop? = null;
//    var basePop: BasePopupView? = null;

    /**
     * 添加一个翻书gif控件
     */
    fun addEbookGifView(initTime: Int): TaskCutDownTipView {

        val taskCutDownTipView = TaskCutDownTipView(this)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        taskCutDownTipView.elevation = 4.dp2px().toFloat()
        layoutParams.gravity = Gravity.BOTTOM
        inflater.flRoot.addView(taskCutDownTipView, layoutParams)
        taskCutDownTipView.setCutdownTime(initTime)
        return taskCutDownTipView
    }

//    var cutDownObserver : Disposable? = null
    /**
     * 开始倒计时
     */
    fun startCutdown(tView: TaskCutDownTipView) {
//        val max = defaultVerifyTime
        if (timerTask != null) {
            timerTask?.cancel() //将原任务从队列中移除
        }
        timer.purge()
        timerTask = MyTimeTask(tView)
        timer.schedule(timerTask, 1000, 1000)
    }

//    fun showCollectedPop(it: HttpResponse<VerifyCode>) {
//
//        id = it.data.id
//
//        if (basePop == null) {
//            articlelVerfyPop = ArticlelVerfyPop(this, it.data)
////            articlelVerfyPop?.callBack = this
//            basePop = XPopup.Builder(this)
//                .enableDrag(false)
//                .dismissOnTouchOutside(false)
//                .dismissOnBackPressed(false)
//                .hasShadowBg(false)
//                .asCustom(articlelVerfyPop)
////                .show()
//        }
//        articlelVerfyPop?.data = it.data
//        articlelVerfyPop?.downloadImage()
//        articlelVerfyPop?.show()
//
//
////        showVerifyImageDelay(it)
//    }


    fun showCollectedPop() {

        val articlelVerfyPop = VerifyPop(lifecycleScope, this, resourceID, folder_id)
        XPopup.Builder(this)
            .enableDrag(false)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .hasShadowBg(false)
            .asCustom(articlelVerfyPop)
            .show()
    }

//    override fun getVerifyCodeInfo(id: String) {
//        mViewModel.getVerifyCode(id)
//            .observe(this, {
//                handleVerifyData(it)
//            })
//    }

    //处理获取验证图
//    private fun handleVerifyData(it: HttpResponse<VerifyCode>?) {
//        if (it?.isSuccess == true) {
//            id = it.data.id ?: 0
//            showCollectedPop(it)
//        } else {
//            Toast.makeText(this, it?.msg ?: "", Toast.LENGTH_LONG).show()
//        }
//    }


//    override fun postVerifyCode(points: MutableList<Point>) {
//
//        if (points.size != 4) {
//            Toast.makeText(this, "请先点选4个图案", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val postVerifyBean = PostVerifyBean()
//        postVerifyBean.folder_id = folder_id
//        postVerifyBean.article_id = resourceID
//
//        val verifyCode = PostVerifyBean.verifyCode();
//
//        verifyCode.id = id
//        verifyCode.corrd = points as ArrayList<Point>
//        postVerifyBean.verification_code = verifyCode
//
//        mViewModel.postVerifyCode(GsonManager.getInstance().toJson(postVerifyBean))
//
//
//    }


    //刷新验证图
//    override fun refreshVerifyImg() {
//        mViewModel.getVerifyCode(resourceID).observe(this, {
//            handleVerifyData(it)
//        })
//    }

//    /**
//     * 验证码显示的时候关闭Acitivty
//     */
//    override fun closeActivity() {
//        finish()
//    }


    override fun finish() {
//        mHandler.removeCallbacksAndMessages(null)
        dispose?.dispose()
        stopTimer()

        super.finish()
    }

    fun stopTimer() {
        if (timerTask != null) {
            timerTask?.cancel()
            timerTask = null
        }
    }


    inner class MyTimeTask(var tView: TaskCutDownTipView) : TimerTask() {
        override fun run() {

            defaultVerifyTime -= 1000
            if (defaultVerifyTime <= 0) {
                stopTimer()
                runOnUiThread {
//                    tView.setPercent(100)
                    tView.setCutdownTime(0)
                    tView.visibility = View.GONE
//                    getVerifyCodeInfo(resourceID)

                    showCollectedPop()
                }
                return
            }

            runOnUiThread {
                tView.setCutdownTime(defaultVerifyTime)
            }
        }
    }
}