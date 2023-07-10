package com.mooc.discover.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.lifecycle.LifecycleObserver
import com.bumptech.glide.Glide
import com.mooc.changeskin.SkinManager
import com.mooc.common.ktextends.dp2px
import com.mooc.discover.R

class SubscribeTipNewView @JvmOverloads constructor(
    var mContext: Context,
    attributeSet: AttributeSet? = null,
    defaultValue: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(mContext, attributeSet, defaultValue),
    LifecycleObserver {


    init {
        Glide.with(this).asGif().load(R.drawable.gif_order).into(this)
        _changeCurrentSkin()
    }

    private fun _changeCurrentSkin() {
        val getmSuffix = SkinManager.getInstance().getmSuffix()
        if (!TextUtils.isEmpty(getmSuffix)) {
            if (getmSuffix.equals("festival")) {//节日
                Glide.with(this).asGif().load(R.drawable.gif_order_festival).into(this)
            } else if (getmSuffix.equals("pag")) {//党政
                Glide.with(this).asGif().load(R.drawable.gif_order_pag).into(this)
            } else {
                Glide.with(this)
                    .load(R.mipmap.discover_ic_subscribe)
                    .override(20.dp2px(), 20.dp2px())
                    .into(this)
            }
        }
    }


}