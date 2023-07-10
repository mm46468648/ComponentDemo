package com.mooc.home.ui.scheme

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.base.BaseActivity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.scheme.SchemasData
import com.mooc.commonbusiness.scheme.SchemasManager
import com.mooc.home.R
import com.mooc.home.databinding.SetActivitySchemeBinding
import java.util.*
import java.util.regex.Pattern

/**
 * 跳转外部
 */
@Route(path = Paths.PAGE_SCHEME)
class SchemeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val contentView = DataBindingUtil.setContentView<SetActivitySchemeBinding>(
            this,
            R.layout.set_activity_scheme
        )
        contentView.commonTitleLayout.setOnLeftClickListener {
            finish()
        }

        getIntentData()

    }

    val START_APP_LINK = "moocndrly://"

    fun getIntentData() {
        val action = intent.action
        val uri = intent.data
        if (Intent.ACTION_VIEW == action) {
            if (uri != null && uri.toString().contains(START_APP_LINK)) {  //可测试uri
                var strLinkUrl: String =
                    substringAfter(uri.toString(), START_APP_LINK)!!
                strLinkUrl = if (strLinkUrl.contains(IntentParamsConstants.INTENT_KEY_COURSE_ID)) {
                    "course://$strLinkUrl"
                } else {
                    uri.toString()
                }
                if (!TextUtils.isEmpty(strLinkUrl)) {
                    setSchemasData(strLinkUrl)
                    finish()
                } else {
                    finish()
                }
                return
            }
            if (uri != null && isHttpUrl(uri.toString())) {  //  测试uri  http://192.168.9.104:11112/api/mobile?resource_type=31&resource_id=23&is_share=1
                setSchemasData(uri.toString())
                finish()
            }
        }
    }

    private fun setSchemasData(location: String) {
        try {
            val data = SchemasData(location)
            SchemasManager.dispatchSchemas(data, this, false, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun substringAfter(str: String, separator: String?): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        if (separator == null) {
            return "";
        }
        val pos = str.indexOf(separator)
        return if (pos == -1) {
            ""
        } else str.substring(pos + separator.length)
    }

    fun isHttpUrl(url: String?): Boolean {
        try {
            if (url == null) {
                return false
            }
            val p = Pattern
                .compile(
                    "^((https?|ftp|news)://)?([a-z]([a-z0-9\\-]*[\\.。])+([a-z]{2}"
                            + "|aero|arpa|biz|com|coop|edu|gov|info|int|jobs|mil|museum|name|nato|net|org|pro|travel)"
                            + "|(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
                            + ")(/[a-z0-9_\\-\\.~]+)*(/([a-z0-9_\\-\\.]*)(\\?[a-z0-9+_\\-\\.%=&]*)?)?(#[a-z][a-z0-9_]*)?$"
                )
            val m = p.matcher(url.lowercase(Locale.getDefault()))
            if (m.matches() || url.contains("http")) {
                return true
            }
        } catch (e: java.lang.Exception) {
            return false
        }
        return false
    }

}