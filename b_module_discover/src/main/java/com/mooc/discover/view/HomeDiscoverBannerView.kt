package com.mooc.discover.view

import android.content.Context
import android.gesture.GestureOverlayView.ORIENTATION_HORIZONTAL
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mooc.changeskin.SkinManager
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.discover.R
import com.mooc.discover.adapter.BannerCycleAdapter
import com.mooc.discover.model.BannerBean
import com.mooc.discover.model.RecommendBannerBean
import com.mooc.resource.ktextention.dp2px
import com.mooc.resource.widget.MoocImageView
import com.mooc.statistics.LogUtil
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.indicator.enums.IndicatorStyle
import kotlin.math.absoluteValue


/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    首页发现推荐全部头
 * @Author:         xym
 * @CreateDate:     2020/8/12 3:51 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/12 3:51 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class HomeDiscoverBannerView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    private val bannerList = arrayListOf<RecommendBannerBean>()


    private var touchSlop = 0
    private var initialX = 0f
    private var initialY = 0f

    private val parentViewPager: ViewPager2?
        get() {
            var v: View? = parent as? View
            while (v != null && v !is ViewPager2) {
                v = v.parent as? View
            }
            return v as? ViewPager2
        }

    @Suppress("unused")
    private val child: View?
        get() = if (childCount > 0) getChildAt(0) else null

    init {
        LayoutInflater.from(context).inflate(R.layout.item_banner, this)

        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    @Suppress("UNUSED_PARAMETER")
    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
//        val direction = -delta.sign.toInt()
//        return when (orientation) {
//            0 -> child?.canScrollHorizontally(direction) ?: false
//            1 -> child?.canScrollVertically(direction) ?: false
//            else -> throw IllegalArgumentException()
//        }
        return true
    }

    private lateinit var mViewPager: BannerViewPager<RecommendBannerBean>
    fun setBannerBean(it: BannerBean) {
        if (it.results?.isNotEmpty() == true) {
            bannerList.clear()
            bannerList.addAll(it.results!!)
//
            val bannerCycleAdapter = BannerCycleAdapter()
            val onItemClick = { position: Int, bean: RecommendBannerBean ->
                if (bean.relation_type == 2) {//分类
                    val childId = bean.sort_id //想要跳转的资源的二级分类id
                    val pair = Pair(bean.resource_type, childId.toString())
                    LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_DISCOVER_TAB_CHANGE)
                        .postValue(pair)

                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_DISCOVER,
                        "${position + 1}",
                        LogEventConstants2.ET_BAN,
                        bean.name
                    )
                } else if (bean.relation_type == 1) {//资源库
                    ResourceTurnManager.turnToResourcePage(bean)

                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_DISCOVER,
                        "${position + 1}",
                        LogEventConstants2.ET_BAN,
                        bean.name,
                        "${LogEventConstants2.typeLogPointMap[bean._resourceType]}#${bean._resourceId}"
                    )
                } else if (bean.relation_type == 3) {//无配置

                } else {//其他情况，默认跳转资源库
                    ResourceTurnManager.turnToResourcePage(bean)

                    LogUtil.addClickLogNew(
                        LogEventConstants2.P_DISCOVER,
                        "${position + 1}",
                        LogEventConstants2.ET_BAN,
                        bean.name,
                        "${LogEventConstants2.typeLogPointMap[bean._resourceType]}#${bean._resourceId}"
                    )
                }
            }
            //换肤
            val skinColor =
                SkinManager.getInstance().resourceManager.getColor("colorPrimary")

            mViewPager = findViewById(R.id.banner)
            mViewPager.adapter = bannerCycleAdapter
            mViewPager
                .setIndicatorStyle(IndicatorStyle.ROUND_RECT)
                .setIndicatorSliderColor(Color.WHITE,skinColor)
                .setIndicatorHeight(2.dp2px())
                .setIndicatorSliderGap(4.dp2px())
                .setIndicatorSliderWidth(6.dp2px())
                .setIndicatorMargin(0, 0, 0, 8.dp2px())
                .setInterval(5000)
                .setOnPageClickListener { _, position ->
                    onItemClick.invoke(
                        position,
                        bannerList[position]
                    )
                }
                .create(bannerList)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension(measuredWidth, 150.dp2px())

    }

    inner class MAdapter(private var bannerList: ArrayList<RecommendBannerBean>) : PagerAdapter() {
        override fun getCount(): Int {
            return bannerList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = MoocImageView(container.context)
            imageView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            container.addView(imageView)
            imageView.setImageUrl(bannerList[position].picture, 3.dp2px())
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

    }


    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        val orientation = parentViewPager?.orientation ?: return
        // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return
        }

        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = e.x - initialX
            val dy = e.y - initialY
            val isVpHorizontal = orientation == ORIENTATION_HORIZONTAL
            // assuming ViewPager2 touch-slop is 2x touch-slop of child
            val scaledDx = dx.absoluteValue * if (isVpHorizontal) .5f else 1f
            val scaledDy = dy.absoluteValue * if (isVpHorizontal) 1f else .5f

            if (scaledDx > touchSlop || scaledDy > touchSlop) {

                if (isVpHorizontal == (scaledDy > scaledDx)) {
                    // Gesture is perpendicular, allow all parents to intercept
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    // Gesture is parallel, query child if movement in that direction is possible
                    if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
                        // Child can scroll, disallow all parents to intercept
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        // Child cannot scroll, allow all parents to intercept
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
        }
    }
}