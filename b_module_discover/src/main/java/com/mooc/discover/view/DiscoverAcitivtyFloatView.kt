package com.mooc.discover.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.mooc.common.ktextends.dp2px
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.interfaces.AudioBottomPlayable
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.AudioFloatService
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.ActivityJoinBean
import com.mooc.discover.window.ShowActivityPop
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 发现页浮窗控件
 * 1。根据列表的滑动显示隐藏
 *      500ms， 向右平移%80，透明度0。5
 * 2。支持gif加载
 * 3。登录状态下请求活动数据
 * 4。点击进入对应的资源页面
 * 5。退出登录的时候要隐藏
 */
class DiscoverAcitivtyFloatView @JvmOverloads constructor(
    var mContext: Context,
    var attributeSet: AttributeSet? = null,
    var defaultInt: Int = 0
) :
    AppCompatImageView(mContext, attributeSet, defaultInt) {

    companion object{
        val showControllLiveData : MutableLiveData<Boolean> by lazy {
            MutableLiveData<Boolean>().also{
                it.value = true
            }
        }
    }
    init {
        _init()
//        setImageResource(R.mipmap.common_ic_user_head_default)
        scaleType = ScaleType.CENTER_CROP
    }

    private fun _init() {
        if (GlobalsUserManager.isLogin()) {
            loadData()
        }
    }

    var subscribe: Disposable? = null

    /**
     * 加载活动数据
     */
    fun loadData() {
        subscribe = HttpService.discoverApi.getActivityJoin()
            .compose(RxUtils.applySchedulers())
            .subscribe(
                {
                    if (subscribe?.isDisposed == true) return@subscribe
                    onResponse(it)
                }, {

                }
            )
    }

    private fun onResponse(joinBean: ActivityJoinBean?) {
        if (joinBean == null) return
        if (joinBean.countdown < 0 || joinBean.picture_float?.isEmpty() == true) {
            visibility = View.GONE
            return
        }

        //显示活动弹窗
        if (joinBean.is_popup == true && !joinBean.picture_frame.isNullOrEmpty()) {
            val showActPop = ShowActivityPop(mContext,parent as View)
            showActPop.joinBean = joinBean
            showActPop.show()
        }

        //显示浮窗
        visibility = View.VISIBLE

        joinBean.picture_float?.apply {
            if (this.endsWith(".gif"))
                Glide.with(mContext).asGif()
                    .load(this).into(this@DiscoverAcitivtyFloatView)
            else
                Glide.with(context).load(joinBean.picture_float)
                    .into(this@DiscoverAcitivtyFloatView)
        }

        //设置点击事件
        setOnClickListener {
            if("-1" == joinBean.resource_type){
                ARouter.getInstance().build(Paths.PAGE_WEB)
                    .withString(IntentParamsConstants.WEB_PARAMS_URL,joinBean.url)
                    .navigation()
            }else{
                ResourceTurnManager.turnToResourcePage(joinBean)

            }
        }

    }

    var isHide =false
    /**
     * 显示动画
     */
    fun show() {
        if(!isHide) return
        isHide = false
        animate().translationX(0f).alpha(1f).setStartDelay(1500).setDuration(500).start();
    }

    /**
     * 隐藏动画
     */
    fun hide() {
        if(isHide) return
        isHide = true
        animate().translationX(140f).alpha(0.5f).setStartDelay(0).setDuration(500).start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(64.dp2px(),76.dp2px())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserEvent(userInfo: UserLoginStateEvent){
        if(userInfo.userInfo!=null){
            loadData()
        }else{
            visibility = View.GONE
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        EventBus.getDefault().unregister(this)
        subscribe?.isDisposed
    }


    /**
     * 浮窗显示隐藏接口
     */
    interface FloatViewVisibale{
//        val needRecyclerView:RecyclerView?


        // 用来设置view滑动时,toolbar消失和显示
        fun attachToScroll(needRecyclerView:RecyclerView) {
            needRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        //show view
                        showControllLiveData.value = true
                        return
                    }

                    //hide view
                    showControllLiveData.value = false
                }
            })
        }

        fun attachToScroll(needRecyclerView:RecyclerView,activity: Activity) {
            needRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var audioFloatService: AudioFloatService? = ARouter.getInstance().navigation(AudioFloatService::class.java)
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    //首页浮窗的显示隐藏
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        //show view
                        showControllLiveData.value = true
                        return
                    }

                    //hide view
                    showControllLiveData.value = false

                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    //音频播放浮窗的显示隐藏
                    if(activity is AudioBottomPlayable){
                        audioFloatService?.hide(activity , dy > 0)
                    }
                }
            })
        }
    }
}