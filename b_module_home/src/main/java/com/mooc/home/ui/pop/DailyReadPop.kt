package com.mooc.home.ui.pop

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.DateUtil
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.pop.CommonBottomSharePop
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.commonbusiness.utils.ServerTimeManager
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.home.R
import com.mooc.home.databinding.HomePopDaliyreadBinding
import com.mooc.home.model.DailyReadBean
import com.mooc.statistics.LogUtil
//import kotlinx.android.synthetic.main.home_pop_daliyread.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.Request

class DailyReadPop(var mContext: Context, var dailyReadBean: DailyReadBean) : CenterPopupView(mContext) {

    private lateinit var inflater: HomePopDaliyreadBinding
    override fun getImplLayoutId(): Int {
        return R.layout.home_pop_daliyread
    }

    override fun onCreate() {
        super.onCreate()
        inflater = HomePopDaliyreadBinding.bind(popupImplView)
        inflater.mivMiddle.setImageUrl(dailyReadBean.image_url, 6.dp2px())
        inflater.ibClose.setOnClickListener { dismiss() }
        inflater.tvShare.setOnClickListener {
            showShareDialog()
        }

        //保存显示时间
        SpDefaultUtils.getInstance().putString(SpConstants.TODAY_READPOP_LASTTIME, DateUtil.getDateStrNow(TimeFormatUtil.yyyyMMdd))
    }

    fun showShareDialog() {
        val commonBottomSharePop = CommonBottomSharePop(mContext)
        commonBottomSharePop.onItemClick = {
            //将图片url，分享
//            shareImage(it)
            shareImageNew(it)
        }
        XPopup.Builder(mContext)
                .asCustom(commonBottomSharePop)
                .show()
    }

    /**
     * 新的分享图片功能
     * 生成一个分享图，包含用户头像和当前日期的图
     * @param it 分享位置
     */
    private fun shareImageNew(shareSite: Int) {
        if (dailyReadBean.share_url.isEmpty()) return
        //正确的分享地址，防止图片被篡改
        if (!dailyReadBean.share_url.contains("moocnd.ykt.io") && !dailyReadBean.share_url.contains("learning.mil.cn")) return


        val toSite = when (shareSite) {
            CommonBottomSharePop.SHARE_TYPE_WX -> "WX"
            CommonBottomSharePop.SHARE_TYPE_WXCIRCLE -> "PYQ"
            CommonBottomSharePop.SHARE_TYPE_SCHOOL -> "XYQ"
            else -> ""
        }

        val formatDate = TimeFormatUtil.formatDate(
            ServerTimeManager.getInstance().serviceTime,
            TimeFormatUtil.yyyy_MM_dd
        )

        LogUtil.addClickLogNew(
            LogEventConstants2.P_DAILYREAD, formatDate ?: "", LogEventConstants2.ET_POP,
            LogEventConstants2.typeSharePointMap[shareSite] ?: "", toSite)

        lifecycleScope.launchWhenCreated {
            createShareBitmap(mContext, dailyReadBean.share_url)
                .flowOn(Dispatchers.IO)
                .catch {e->
                    loge(e.toString())
                    toast("图片状态异常")
                }
                .collect { view ->
                    loge(Thread.currentThread())
                    share(BitmapUtils.createUnShowBitmapFromLayout(view),shareSite)
                }
        }
    }

    /**
     * 调用分享
     */
    fun share(bitmap: Bitmap, platform: Int) {
        //分享服务
        val shareService: ShareSrevice =
            ARouter.getInstance().navigation(ShareSrevice::class.java)

        shareService.shareAddScore(
            ShareTypeConstants.TYPE_READ, mContext as Activity, IShare.Builder()
                .setSite(platform)
                .setTitle("")
                .setMessage("")
                .setImageBitmap(bitmap)
                .build()
        )
    }

    fun createShareBitmap(context: Context, imageUrl: String): Flow<View> {
        return flow {
            val shareImageBitmap = lifecycleScope.async(Dispatchers.IO) {
                getShareImageBitmap(imageUrl) ?: BitmapFactory.decodeResource(
                    context.getResources(),
                    R.mipmap.my_bg_dailyread_load,
                    null
                )
            }
            val shareHeadImageBitmap = lifecycleScope.async(Dispatchers.IO) {
                getShareHeadImageBitmap(context, GlobalsUserManager.userInfo?.avatar ?: "")
                    ?: BitmapFactory.decodeResource(
                        context.getResources(),
                        R.mipmap.common_ic_user_head_default,
                        null
                    )
            }

            val inflate = inflate(context, R.layout.my_layout_dailyread_share, null)
            val ivImage = inflate.findViewById<ImageView>(R.id.imageView)
            val ivHead = inflate.findViewById<ImageView>(R.id.ivHead)
            val tvTime = inflate.findViewById<TextView>(R.id.tvTime)
            val tvName = inflate.findViewById<TextView>(R.id.tvName)

            ivImage.setImageBitmap(shareImageBitmap.await())
            ivHead.setImageBitmap(shareHeadImageBitmap.await())
            tvName.setText(GlobalsUserManager.userInfo?.name)

            //data使用当天的日期
            val formatDateFromString = TimeFormatUtil.formatDate(
                ServerTimeManager.getInstance().serviceTime,
                TimeFormatUtil.yyyyNMMYddR
            )
            tvTime.setText(formatDateFromString)

            loge(Thread.currentThread())
            emit(inflate)
        }
    }

    fun getShareImageBitmap(imageUrl: String): Bitmap? {
        //封面图
        val request = Request.Builder().url(imageUrl).build()
        val okHttpClient = OkHttpClient()
        val response = okHttpClient.newCall(request).execute()
        val inputStream = response.body?.byteStream()
        val decodeStream = BitmapFactory.decodeStream(inputStream)
        return decodeStream
    }

    fun getShareHeadImageBitmap(context: Context, headUrl: String): Bitmap? {
        return try {
            val request = Request.Builder().url(
                GlobalsUserManager.userInfo?.avatar
                    ?: "").build()
            val okHttpClient = OkHttpClient()
            val response = okHttpClient.newCall(request).execute()
            val inputStream = response.body?.byteStream()
            BitmapUtils.makeRoundCorner(BitmapFactory.decodeStream(inputStream))
        } catch (e: Exception) {
            null
        }
    }
}