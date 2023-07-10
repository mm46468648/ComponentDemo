package com.mooc.studyproject.presenter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mooc.common.excutor.DispatcherExecutor
import com.mooc.common.utils.bitmap.BitmapUtils
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.model.my.CMSShareBean
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ShareSrevice
import com.mooc.commonbusiness.utils.IShare
import com.mooc.studyproject.R
import com.mooc.commonbusiness.model.studyproject.StudyPlanDetailBean
import com.mooc.studyproject.viewmodel.StudyProjectViewModel
import me.devilsen.czxing.code.BarcodeWriter
import me.devilsen.czxing.util.BarCodeUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

/**
 * MyFragment主持类
 */
class StudyProjectSharePresenter {

    val shareService: ShareSrevice by lazy {
        ARouter.getInstance().build(Paths.SERVICE_SHARE).navigation() as ShareSrevice
    }

    fun shareAppMessage(activity: FragmentActivity, mStudyPlanDetailBean: StudyPlanDetailBean?, shareSite: Int, model: StudyProjectViewModel, planId: String?) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("source_type", ShareTypeConstants.SHARE_TYPE_STUDYPROJECT)
            jsonObject.put("source_id", planId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val toRequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        model.getCMSShareData(toRequestBody).observe(activity, Observer {
            createBitmap(activity, it?.data, mStudyPlanDetailBean).observe(activity, Observer {
                val build = IShare.Builder()
                        .setSite(shareSite)
                        .setTitle("")
                        .setMessage("")
                        .setImageBitmap(it)
                        .build()
                shareService.shareAddScore(ShareTypeConstants.TYPE_STUDYPLAN, activity, build)
            })
        })


    }

    /**
     * 获取后台返回的有关个人信息的二维码地址
     */
    private fun getMediaShareView() {
//        mViewModel.loadData()

    }

    /**
     * 根据布局视图
     * 创建分享Bitmap
     */
    fun createBitmap(activity: FragmentActivity, cmsShareBean: CMSShareBean?, mStudyPlanDetailBean: StudyPlanDetailBean?): LiveData<Bitmap> {
//        val writer = BarcodeWriter()
//        val bitmap = writer.write(cmsShareBean?.url, BarCodeUtil.dp2px(activity, 90f),
//                BarCodeUtil.dp2px(activity, 90f), BitmapFactory.decodeResource(activity.resources, R.mipmap.common_ic_share_logo_transparent))
        val userBean = GlobalsUserManager.userInfo


        val liveData = MutableLiveData<Bitmap>()
        val inflate = View.inflate(activity, R.layout.studyproject_share_view_study_plan, null)
        val introTv: TextView = inflate.findViewById(R.id.intro)
        val planIntro: TextView = inflate.findViewById(R.id.plan_intro)
        val nameTv: TextView = inflate.findViewById(R.id.name)
        val planDes: TextView = inflate.findViewById(R.id.plan_des)
        val qr_img: ImageView = inflate.findViewById(R.id.qr_img)
        val head_img: ImageView = inflate.findViewById(R.id.img_plan)

        val header: ImageView = inflate.findViewById(R.id.iv_app_user_icon)

        val writer = BarcodeWriter()
        val bitmap = writer.write(
                cmsShareBean?.url,
                BarCodeUtil.dp2px(activity, 200f),
                Color.BLACK,
        )
        qr_img.setImageBitmap(bitmap)
        introTv.setText(cmsShareBean?.invitation_words)
        planIntro.setText(mStudyPlanDetailBean?.study_plan?.plan_name)
        nameTv.text = "——${userBean?.name}"
        planDes.setText(mStudyPlanDetailBean?.study_plan?.plan_subtitle)
        DispatcherExecutor.getIOExecutor().submit {
            Glide.with(activity).asBitmap().load(mStudyPlanDetailBean?.study_plan?.head_img).into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    head_img.setImageBitmap(resource)
                    setHeadImage(activity,userBean?.avatar,header,liveData,inflate)

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    setHeadImage(activity,userBean?.avatar,header,liveData,inflate)
                }

            })
        }

        return liveData
    }

    public fun setHeadImage(activity: FragmentActivity,avatar:String?,header:ImageView,liveData: MutableLiveData<Bitmap>,inflate:View) {
        DispatcherExecutor.getIOExecutor().submit {
            Glide.with(activity).asBitmap().circleCrop().error(R.mipmap.common_ic_user_head_default).load(avatar).into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    header.setImageBitmap(resource)
                    val createUnShowBitmapFromLayout = BitmapUtils.createUnShowBitmapFromLayout(inflate)
                    //转换完毕的bitmap
                    liveData.postValue(createUnShowBitmapFromLayout)

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)

                    header.setImageDrawable(errorDrawable)
                    val createUnShowBitmapFromLayout = BitmapUtils.createUnShowBitmapFromLayout(inflate)
                    //转换完毕的bitmap
                    liveData.postValue(createUnShowBitmapFromLayout)


                }

            })
        }

    }
}