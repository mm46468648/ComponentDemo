package com.mooc.my.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.my.R
import com.mooc.my.databinding.MyItemComeOnBinding
import com.mooc.search.api.MyModelApi
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
//import kotlinx.android.synthetic.main.my_item_come_on.view.*
import org.json.JSONObject

/**
 * 为他人加油
 */
class ViewComeOnUser @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var inflater : MyItemComeOnBinding =
        MyItemComeOnBinding.inflate(LayoutInflater.from(context),this,true)

    init {
//        LayoutInflater.from(context).inflate(R.layout.my_item_come_on,this)
        _init()
    }

    private fun _init() {

    }

    /**
     * 根据位置对应不同的缩放比例
     */
    val scaleRatio = arrayOf(0.9f,1.0f,0.8f,0.7f,0.5f,0.5f,0.4f,0.7f)

    fun setUserInfo(userInfo : UserInfo, position:Int){
        userInfo.avatar?.let { inflater.mivHead.setImageUrl(it,true) }
        inflater.tvName.text = userInfo.name

        scaleX = scaleRatio.get(position)
        scaleY = scaleRatio.get(position)

        setOnClickListener {
            priseOther(userInfo.user_id)
        }
    }

    var subscribe:Disposable? = null

    /**
     * 点赞
     */
    fun priseOther(userId:String){
        val requestData = JSONObject()
        requestData.put("user_id", userId)
        subscribe = ApiService.getRetrofit().create(MyModelApi::class.java)
            .postcheckinCheer(RequestBodyUtil.fromJson(requestData))
            .compose(RxUtils.applySchedulers<HttpResponse<Any>>())
            .subscribe(Consumer {
                if (it.isSuccess) {
                    inflater.ivPrise.visibility = View.VISIBLE
                    this@ViewComeOnUser.isEnabled = false
                }
            },{

            });

    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        subscribe?.dispose()
    }
}