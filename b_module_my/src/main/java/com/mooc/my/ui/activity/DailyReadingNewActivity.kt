package com.mooc.my.ui.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarLayout
import com.haibin.calendarview.CalendarUtil
import com.haibin.calendarview.CalendarView
import com.lxj.xpopup.XPopup
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.my.R
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.runOnMain
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.ScreenUtil
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.my.databinding.ActivityUserInfoNameBinding
import com.mooc.my.databinding.MyActivityDailyreadNewBinding
import com.mooc.my.viewmodel.EverydayReadViewModel
import com.mooc.resource.widget.ScrollFramLayout
import com.mooc.statistics.LogUtil
//import kotlinx.android.synthetic.main.my_activity_dailyread_new.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * 3.4.8版本新的每日一读页面
 */
@Route(path = Paths.PAGE_EVERYDAY_READ)
class DailyReadingNewActivity : BaseActivity(), CalendarView.OnYearChangeListener,
    CalendarView.OnCalendarSelectListener, CalendarView.OnMonthChangeListener{

    private val everydayReadViewModel: EverydayReadViewModel by viewModels()

    var mTextMonthDay: TextView? = null

    var mCalendarView: CalendarView? = null


    private var mYear = 0
    var mCalendarLayout: CalendarLayout? = null

    private lateinit var inflate : MyActivityDailyreadNewBinding


    //分享服务
    val shareService: ShareSrevice by lazy {
        ARouter.getInstance().navigation(ShareSrevice::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflate = MyActivityDailyreadNewBinding.inflate(layoutInflater)
        setContentView(inflate.root)
        initView()

        initCalendarRange()
        initData()
    }

    override fun darkMode() = true

    @SuppressLint("SetTextI18n")
    protected fun initView() {
        mTextMonthDay = findViewById(R.id.tv_month_day)
        mCalendarView = findViewById(R.id.calendarView)
        mCalendarLayout = findViewById(R.id.calendarLayout)

        //点击切换年月日视图
        mTextMonthDay?.setOnClickListener(View.OnClickListener {
            if (mCalendarLayout?.isExpand == false) {
                mCalendarLayout?.expand()
                return@OnClickListener
            }
            mCalendarView?.showYearSelectLayout(mYear)
            mTextMonthDay?.setText(mYear.toString())
        })
//        findViewById<FrameLayout>(R.id.fl_current).setOnClickListener(View.OnClickListener { mCalendarView?.scrollToCurrent() })
        mCalendarView?.setOnYearChangeListener(this)
        mCalendarView?.setOnMonthChangeListener(this)
        mCalendarView?.setOnCalendarSelectListener(this)
        mYear = mCalendarView?.getCurYear() ?: 0
        mTextMonthDay?.setText(
            mCalendarView?.curYear.toString() + "年" + mCalendarView?.curMonth + "月"
        )
        mCalendarView?.scrollToCurrent()


        //share
        inflate.ibShare.setOnClickListener {
            shareCurrentReadData()
        }

        //download
        inflate.ibDownload.setOnClickListener {
            downloadCurrentReadData()
        }

        //back
        inflate.ibBack.setOnClickListener { finish() }

        //empty
//        mEmptyView.setTitle("网络不畅，轻触刷新")
//        mEmptyView.setEmptyIcon(R.mipmap.my_ic_dailyread_fail)
//        mEmptyView.setOnClickListener {
//
//            val formatDate = TimeFormatUtil.formatDate(mCalendarView?.selectedCalendar?.timeInMillis?:System.currentTimeMillis(), TimeFormatUtil.yyyy_MM_dd)
//            changeImage(formatDate)
//        }

        inflate.mScrollFramLayout.mScrollChangeListener = object : ScrollFramLayout.OnScrollChange {
            override fun scrollOritation(oratation: ScrollFramLayout.Oritation) {
                onScrollChange(oratation)
            }
        }
    }

    fun onScrollChange(oratation: ScrollFramLayout.Oritation){
        val preCalendar =if(oratation == ScrollFramLayout.Oritation.NEXT){
            CalendarUtil.getNextCalendar(mCalendarView?.selectedCalendar)
        }else{
            CalendarUtil.getPreCalendar(mCalendarView?.selectedCalendar)
        }
        loge("day: ${preCalendar.year} - ${preCalendar.month} - ${preCalendar.day}" )
        inflate.calendarView.scrollToCalendar(preCalendar.year,preCalendar.month,preCalendar.day,false,true)

        if(inflate.calendarView.isInRange(preCalendar)){
            onCalendarSelect(preCalendar,true)
        }
    }
    /**
     * 通过当前图片生成一张分享图
     * 再分享
     */
    private fun shareCurrentReadData() {
        everydayReadViewModel.getSingleReadBeanData().value?.let {
            val imageUrl = it.share_url ?: ""

            if (!everydayReadViewModel.checkUrl(imageUrl)) {
                toast("图片地址不正确")
                return
            }

            lifecycleScope.launchWhenCreated {


//                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                everydayReadViewModel.createShareBitmap(this@DailyReadingNewActivity, imageUrl)
                    .flowOn(Dispatchers.IO)
                    .catch {e->
                        loge(e.toString())
                        toast("图片状态异常")
                    }
                    .collect { view ->
                        loge(Thread.currentThread())
                        share(BitmapUtils.createUnShowBitmapFromLayout(view),it.date)
//                        (findViewById(android.R.id.content) as ViewGroup).addView(view)
                    }
            }
        }
    }

    /**
     * 调用分享
     */
    fun share(bitmap: Bitmap,date:String) {
        XPopup.Builder(this@DailyReadingNewActivity)
            .asCustom(
                CommonBottomSharePop(this@DailyReadingNewActivity,{ platform ->
                shareService.shareAddScore(
                    ShareTypeConstants.TYPE_READ, this@DailyReadingNewActivity, IShare.Builder()
                        .setSite(platform)
                        .setTitle("")
                        .setMessage("")
                        .setImageBitmap(bitmap)
                        .build()
                )
                actionLog(date, platform)
            }))
            .show()
    }

    /**
     * 行为日志
     */
    private fun actionLog(date: String, platform: Int) {
        //打点
        val toSite = when (platform) {
            CommonBottomSharePop.SHARE_TYPE_WX -> "WX"
            CommonBottomSharePop.SHARE_TYPE_WXCIRCLE -> "PYQ"
            CommonBottomSharePop.SHARE_TYPE_SCHOOL -> "XYQ"
            else -> ""
        }
        LogUtil.addClickLogNew(
            LogEventConstants2.P_DAILYREAD,
            date ?: "",
            LogEventConstants2.ET_ICON,
            resources.getString(R.string.daliy_read),
            toSite
        )
    }


    /**
     * 下载当前数据
     */
    private fun downloadCurrentReadData() {
        everydayReadViewModel.downloadImage(this)
    }

    /**
     * 初始化日历，设置可选范围
     */
    fun initCalendarRange() {
        //获取当前日期的年月日
        val c = java.util.Calendar.getInstance()
        c.time = Date(System.currentTimeMillis())
        val year = c[java.util.Calendar.YEAR]
        val month = c[java.util.Calendar.MONTH] + 1
        val day = c[java.util.Calendar.DAY_OF_MONTH]

        mCalendarView?.setRange(
            2017, 5, 9,
            year, month, day
        )
    }

    protected fun initData() {

        everydayReadViewModel.getSingleReadBeanData().observe(this, Observer {
            if(it == null){ //网络一场的情况
                showErrorState(true)
            }else{
                loadAndScale(it.image_url?:"")
            }
        })
    }

    /**
     * 加载图片，并对图片进行缩放
     *
     */
    fun loadAndScale(imageUrl:String){
        showErrorState(false)
        if(imageUrl.isEmpty()){
            //如果url为null，设置空状态
            inflate.imageView.setImageResource(R.mipmap.my_bg_dailyread_empty)
            return
        }

        Glide.with(inflate.imageView).asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

//                    val width = resource.width
                    val height = resource.height
//                    val radio: Float = ScreenUtil.getScreenWidth(this@DailyReadingNewActivity) * 1.0f / width
                    val radio: Float = (ScreenUtil.getScreenHeight(this@DailyReadingNewActivity) - inflate.imageView.top) * 1.0f / height
                    loge("getScreenHeight: ${ScreenUtil.getScreenHeight(this@DailyReadingNewActivity)} currentY: ${inflate.imageView.top} resourceHeight: ${resource.height} resourceWidth: ${resource.width} radio: ${radio}")

                    val bitmap: Bitmap? = scaleBitmap(resource, radio)
                    runOnMain {
                        inflate.imageView.setImageBitmap(bitmap)
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)

                    runOnMain {
                        showErrorState(true)
                    }
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }


    fun showErrorState(errorStatus:Boolean){
        if(errorStatus){
            inflate.imageView.setImageResource(R.mipmap.my_bg_dailyread_error)
            inflate.imageView.setOnClickListener {
                val formatDate = TimeFormatUtil.formatDate(mCalendarView?.selectedCalendar?.timeInMillis?:System.currentTimeMillis(), TimeFormatUtil.yyyy_MM_dd)
                changeImage(formatDate)
            }
        }else{
            inflate.imageView.setOnClickListener {

            }
        }
    }


    override fun onCalendarOutOfRange(calendar: Calendar?) {

    }


    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
        mTextMonthDay?.text = calendar.year.toString() + "年" + calendar.month + "月"
        mYear = calendar.year

        //如果不是点击选中的不切换
        if (!isClick) return
        val formatDate = TimeFormatUtil.formatDate(calendar.timeInMillis, TimeFormatUtil.yyyy_MM_dd)
//        loge("加载${calendar.year}-${calendar.month}-${calendar.day}")
        changeImage(formatDate)
    }

    /**
     * 切换日期
     * @param date 时间类型字符串
     */
    fun changeImage(date: String) {
        inflate.imageView.setImageResource(R.mipmap.my_bg_dailyread_load)
        everydayReadViewModel.getSingleReadData(date)

    }

    override fun onYearChange(year: Int) {
        //展开的情况下才重新设置年
        if (mCalendarLayout?.isExpand == true) {
            mTextMonthDay?.text = year.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onMonthChange(year: Int, month: Int) {
        mTextMonthDay?.text = "${year}年${month}月"
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    private fun scaleBitmap(origin: Bitmap?, ratio: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.preScale(ratio, ratio)
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
//        origin.recycle()
        return newBM
    }




}