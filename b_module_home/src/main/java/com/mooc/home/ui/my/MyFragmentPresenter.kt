package com.mooc.home.ui.my

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.XPopup
import com.mooc.common.excutor.DispatcherExecutor
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.dialog.PublicDialog
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.PublicDialogBean
import com.mooc.commonbusiness.model.eventbus.UserLoginStateEvent
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.home.R
import com.mooc.home.model.MyShareBean
import kotlinx.coroutines.*
import me.devilsen.czxing.code.BarcodeWriter
import me.devilsen.czxing.util.BarCodeUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

/**
 * MyFragment主持类
 */
class MyFragmentPresenter {

    val shareAddScore: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    var launch: Job? = null
    fun shareAppMessage(activity: FragmentActivity, shareSite: Int) {
        launch = GlobalScope.launch {

            try {
                val jsonObject = JSONObject()
                jsonObject.put(
                    "source_type",
                    NormalConstants.SHARE_SOURCE_TYPE_APP_SHARE.toString()
                )
                jsonObject.put("source_id", "0")

                val toRequestBody = jsonObject.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                //获取加入时间
                val a = async { HttpService.commonApi.getAppShareData().await() }
                //获取二维码地址
//            val b = async { HttpService.commonApi.getCMSShareData(toRequestBody).await() }
                val b = async {
                    HttpService.commonApi.getShareDetailDataNew(
                        ShareTypeConstants.SHARE_TYPE_APP,
                        "",
                        ""
                    )
                }
                val bean = MyShareBean(a.await()?.join_days, b.await().data.weixin_url)


                if (launch?.isCancelled == true) return@launch
                withContext(Dispatchers.Main) {
                    createBitmap(activity, bean).observe(activity, Observer {
                        val build = IShare.Builder()
                            .setSite(shareSite)
                            .setTitle("")
                            .setMessage("")
                            .setImageBitmap(it)
                            .build()
                        shareAddScore.shareAddScore(ShareTypeConstants.TYPE_APP, activity, build)
                    })
                }
            } catch (e: Exception) {

            }
        }
    }


    /**
     * 显示退出登录弹窗
     */
    @Suppress("DEPRECATION")
    fun showLogoutDialog(context: Context, nestedScrollView: NestedScrollView) {
        val publicDialogBean = PublicDialogBean()
        publicDialogBean.strMsg = context.resources.getString(R.string.text_str_exit)
        publicDialogBean.strLeft = context.resources?.getString(R.string.text_cancel)
        publicDialogBean.strRight = context.resources?.getString(R.string.text_ok)
        publicDialogBean.isLeftGreen = 0
        XPopup.Builder(context)
            .asCustom(PublicDialog(context, publicDialogBean) {
                if (it == 1) {
                    logoutUser(nestedScrollView)
                }
            })
            .show()
    }

    /**
     * 退出登录
     */
    private fun logoutUser(nestedScrollView: NestedScrollView) {
//        LogUtil.addClickLog(LogPageConstants.PID_MY, LogEventConstants.EID_LOGOUT)
        GlobalsUserManager.clearUserInfo()
        //同步发送用户登录信息改变事件
        EventBus.getDefault().post(UserLoginStateEvent(null))
        nestedScrollView.scrollTo(0, 0)
    }

    /**
     * 根据布局视图
     * 创建分享Bitmap
     */
    fun createBitmap(activity: FragmentActivity, myShareBean: MyShareBean): LiveData<Bitmap> {
        val liveData = MutableLiveData<Bitmap>()
        val inflate = View.inflate(activity, R.layout.home_view_app_share_layout, null)
        val dayTv: TextView = inflate.findViewById(R.id.days)
        val friend_name: TextView = inflate.findViewById(R.id.friend_name)
        val name: TextView = inflate.findViewById(R.id.name)
        val header1: ImageView = inflate.findViewById(R.id.iv_app_user_icon)
        val qrImg1 = inflate.findViewById(R.id.qr_img) as ImageView
        val userBean = GlobalsUserManager.userInfo
        name.setText(userBean?.name)
        friend_name.setText(userBean?.name)
        val writer = BarcodeWriter()
        val bitmap = writer.write(
            myShareBean.url,
            BarCodeUtil.dp2px(activity, 200f),
            Color.BLACK,
            BitmapFactory.decodeResource(
                activity.resources,
                R.mipmap.common_ic_share_logo_transparent
            )
        )


        val userHead = GlobalsUserManager.userInfo?.avatar ?: ""
        dayTv.setText(myShareBean.join_days)
        qrImg1.setImageBitmap(bitmap)
        //需要设置好头像再生成图片
        DispatcherExecutor.getIOExecutor().submit {
            Glide.with(activity).asBitmap().circleCrop().error(R.mipmap.common_ic_user_head_default)
                .load(userHead).into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        header1.setImageBitmap(resource)
                        val createUnShowBitmapFromLayout =
                            BitmapUtils.createUnShowBitmapFromLayout(inflate)
                        //转换完毕的bitmap
                        liveData.postValue(createUnShowBitmapFromLayout)

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                        header1.setImageDrawable(errorDrawable)
                        val createUnShowBitmapFromLayout =
                            BitmapUtils.createUnShowBitmapFromLayout(inflate)
                        //转换完毕的bitmap
                        liveData.postValue(createUnShowBitmapFromLayout)


                    }

                })
        }

        return liveData
    }

    fun release() {
        launch?.cancel()
    }
}