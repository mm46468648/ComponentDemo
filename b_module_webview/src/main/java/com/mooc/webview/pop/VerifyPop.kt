package com.mooc.webview.pop

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Toast
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.config.DownloadConfig
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.VerifyCode
import com.mooc.commonbusiness.model.eventbus.ArticleReadFinishEvent
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.utils.DownloadImageUtil
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.webview.R
import com.mooc.webview.api.RecommendApi
import com.mooc.webview.databinding.ArticlePopVerifyBottomBinding
import com.mooc.webview.interfaces.VerifyArticleCallBack
import com.mooc.webview.model.Point
import com.mooc.webview.model.PostVerifyBean
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.coroutines.*
import okhttp3.*
import org.greenrobot.eventbus.EventBus
//import kotlinx.android.synthetic.main.article_pop_verify_bottom.view.*
import pl.droidsonroids.gif.GifDrawable
import java.io.File
import java.io.IOException
import java.lang.Runnable

/**
 * 验证码Pop
 */
class VerifyPop(var lifeCyclerScope: CoroutineScope,var mContext: Activity, var resourceId:String,var forderId:String) : BottomPopupView(mContext),VerifyArticleCallBack {

//    var callBack: VerifyArticleCallBack? = null;
    override fun getImplLayoutId(): Int {
        return R.layout.article_pop_verify_bottom
    }

    var inflater: ArticlePopVerifyBottomBinding? = null
    override fun onCreate() {
        super.onCreate()

        inflater = ArticlePopVerifyBottomBinding.bind(popupImplView)
        inflater?.btnUpload?.isEnabled = true
        inflater?.imgTouch?.callBack = this
        inflater?.refreshIcon?.setOnClickListener {
            loadVerifyInfo()
        }
        loadVerifyInfo()
    }


    /**
     * 加载验证码信息
     */
    fun loadVerifyInfo(){
        lifeCyclerScope.launch(Dispatchers.IO){

            try {
                val loadInfo = async {loadInfo(resourceId).data}.await()
                val image = async {  loadBgImage(loadInfo.url) }.await()


                withContext(Dispatchers.Main) {
                    if(image != null){
                        refreshVerifyUI(image,loadInfo.id,loadInfo.title)
                    }else{
                        //展示加载错误
                        toast("图片加载失败")
                    }
                }
            }catch (e:Exception){

            }

        }
    }

    suspend fun loadInfo(id:String) : HttpResponse<VerifyCode> {
        return ApiService.getRetrofit().create(RecommendApi::class.java)
            .getArticleVerifyCode(id)
    }

    /**
     * 加载验证码图片
     */
    fun loadBgImage(imageUrl:String) : Drawable?{
        val request = Request.Builder().url(imageUrl).build()
        val okHttpClient = OkHttpClient()
        val response = okHttpClient.newCall(request).execute()
        val inputStream = response.body?.byteStream()
        val folder = File(DownloadConfig.imageLocation)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(DownloadConfig.imageLocation, "verifyCode.png")
        file.outputStream().let {
            inputStream?.copyTo(it, 2048)
            it.close()
            inputStream?.close()
        }
        return Drawable.createFromPath(file.path)
    }


    fun refreshVerifyUI(imageDrawable: Drawable, id: Int, title: String) {

        inflater?.imgTouch?.points?.clear()
        inflater?.imgTouch?.drawablePoints?.clear()
        inflater?.imgTouch?.invalidate()
        inflater?.imgTouch?.background = imageDrawable
        inflater?.imgTouch?.id = id
        inflater?.btnUpload?.text = "请依次点击" + convertString(title)
    }

    fun verifySuccess() {
        inflater?.imgGif?.visibility= View.VISIBLE
        inflater?.btnUpload?.setBackgroundResource(R.drawable.shape_verify_bg_success)
        inflater?.btnUpload?.setTextColor(resources.getColor(R.color.color_ff10955b))
        inflater?.btnUpload?.text = "验证成功"
        val drawable = inflater?.imgGif?.getDrawable() as GifDrawable;
        drawable.loopCount=1
        drawable.start()

        //按钮不可再点击,1s后消失
        inflater?.btnUpload?.isEnabled = false
        handler?.postDelayed(Runnable {
            dismiss()
        }, 1000)

    }

    fun convertString(myStrings: String): String {
        val l: Int = myStrings.length
        val sb = StringBuffer();
        for (i in 0 until l) {
            sb.append("\"").append(myStrings.get(i)).append("\" ")
        }
        return sb.toString();
    }

    override fun onBackPressed(): Boolean {
        //通知外面的Activity关闭
//        callBack?.closeActivity()
        mContext.finish()
        return true
    }

    /**
     * 发送验证码
     */
    override fun postVerifyCode(id : Int,points: MutableList<Point>) {

        if (points.size != 4) {
            Toast.makeText(mContext, "请先点选4个图案", Toast.LENGTH_SHORT).show()
            return
        }

        val postVerifyBean = PostVerifyBean()
        postVerifyBean.folder_id = forderId
        postVerifyBean.article_id = resourceId

        val verifyCode = PostVerifyBean.verifyCode();

        verifyCode.id = id
        verifyCode.corrd = points as ArrayList<Point>
        postVerifyBean.verification_code = verifyCode


        lifeCyclerScope.launch(Dispatchers.IO) {
            try {
                val postVerifyCode = ApiService.getRetrofit().create(RecommendApi::class.java)
                    .postVerifyCode(
                        RequestBodyUtil.fromJsonStr(
                            GsonManager.getInstance().toJson(postVerifyBean)
                        )
                    )

                withContext(Dispatchers.Main){
                    if (postVerifyCode.code == 200) {
                        Toast.makeText(mContext, postVerifyCode.msg, Toast.LENGTH_SHORT).show()
                        verifySuccess()
                        EventBus.getDefault().post(ArticleReadFinishEvent(resourceId))
                    } else {
                        Toast.makeText(mContext, postVerifyCode.msg, Toast.LENGTH_SHORT).show()
                        loadVerifyInfo()
                    }
                }
            }catch (e:Exception){
                Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show()
                loadVerifyInfo()
            }
        }
    }

}