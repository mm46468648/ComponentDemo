package com.mooc.webview.pop

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.core.BottomPopupView
import com.mooc.commonbusiness.model.VerifyCode
import com.mooc.webview.R
import com.mooc.webview.databinding.ArticlePopVerifyBottomBinding
import com.mooc.webview.interfaces.VerifyArticleCallBack
//import kotlinx.android.synthetic.main.article_pop_verify_bottom.view.*
import pl.droidsonroids.gif.GifDrawable
import java.io.File

class ArticlelVerfyPop(var mContext: Context, var data: VerifyCode) : BottomPopupView(mContext) {

    var callBack: VerifyArticleCallBack? = null;
    override fun getImplLayoutId(): Int {
        return R.layout.article_pop_verify_bottom
    }

    lateinit var inflater: ArticlePopVerifyBottomBinding
    override fun onCreate() {
        super.onCreate()

        inflater = ArticlePopVerifyBottomBinding.bind(popupImplView)
        inflater.btnUpload.isEnabled = true
        inflater.imgTouch.callBack = callBack
        inflater.refreshIcon.setOnClickListener {
//            callBack?.refreshVerifyImg()
        }

        downloadImage()
    }

    open fun downloadImage(){
        Glide.with(mContext).downloadOnly().load(data.url).into(object : CustomTarget<File>() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                refreshVerifyUI(resource.path, data.id, data.title)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {

            }

            override fun onLoadCleared(placeholder: Drawable?) {

            }
        })
    }






    fun refreshVerifyUI(url: String, id: Int, title: String) {
        if (inflater == null || inflater.imgTouch == null) {
            return
        }
        inflater.imgTouch.points.clear()
        inflater.imgTouch.drawablePoints.clear()
        inflater.imgTouch.invalidate()
//        inflater.imgTouch.setBackgroundDrawable(Drawable.createFromPath(url))
        inflater.imgTouch.background = Drawable.createFromPath(url)
        inflater.imgTouch.id = id
        inflater.btnUpload.text = "请依次点击" + convertString(title)
    }

    fun verifySuccess() {
        inflater.imgGif.visibility= View.VISIBLE
        inflater.btnUpload.setBackgroundResource(R.drawable.shape_verify_bg_success)
        inflater.btnUpload.setTextColor(resources.getColor(R.color.color_ff10955b))
        inflater.btnUpload.text = "验证成功"
        val drawable = inflater.imgGif.getDrawable() as GifDrawable;
        drawable.loopCount=1
        drawable.start()

        //按钮不可再点击,1s后消失
        inflater.btnUpload.isEnabled = false
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
        return true
    }


}