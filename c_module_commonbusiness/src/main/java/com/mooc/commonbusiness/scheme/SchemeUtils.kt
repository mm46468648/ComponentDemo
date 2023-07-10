package com.mooc.commonbusiness.scheme

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.utils.sharepreference.SpDefaultUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.constants.SpConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.route.Paths
import java.util.*
import java.util.regex.Pattern

//外部跳转公共方法类
class SchemeUtils {

    companion object {
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

        //解析scheme 进行跳转
        fun appLinkActivity(context: Context) {
            val strLinkUrl: String =
                SpDefaultUtils.getInstance().getString(SpConstants.KEY_LOCATION, "")
            if (!TextUtils.isEmpty(strLinkUrl)) {
                setSchemasData(context, strLinkUrl)
                SpDefaultUtils.getInstance().putString(SpConstants.KEY_LOCATION, "")
            }
        }

        private fun setSchemasData(context: Context, location: String) {
            try {
                val data = SchemasData(location)
                SchemasManager.dispatchSchemas(data, context, false, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun toOtherPage(bundle: Bundle) {
            val data = bundle.getSerializable(MyPushKey.DATA_KEY) as SchemasData?
            val pageCode = bundle.getInt(MyPushKey.CODE_KEY, -1)
            if (pageCode == SchemasBlockList.TYPE_DOWNLOAD) {
                ARouter.getInstance().build(Paths.PAGE_MY_DOWNLOAD).navigation();

            } else if (pageCode == SchemasBlockList.TYPE_MY_MESSAGE) {
                ARouter.getInstance().build(Paths.PAGE_MY_MSG).navigation();
            } else if (pageCode == SchemasBlockList.TYPE_FEEDBACK) {
                ARouter.getInstance().build(Paths.PAGE_FEED_BACK).navigation();
            } else if (pageCode == SchemasBlockList.TYPE_USER_SIGN) {
                ARouter.getInstance().build(Paths.PAGE_TASK_SIGN_DETAIL).navigation();

            } else if (pageCode == SchemasBlockList.TYPE_HTML) {
                if (data != null) {
                    turnToPage(
                        "",
                        ResourceTypeConstans.TYPE_PUSH_HTML,
                        data.getStrValue(MyPushKey.START_PAGE_KEY),
                        data.getStrValue(MyPushKey.TITLE_KEY)
                    )
                }
            } else if (pageCode == SchemasBlockList.TYPE_COLUMN_ARTICLE) {
                if (data != null && !TextUtils.isEmpty(data.getStrValue(MyPushKey.ID_KEY))) {
                    turnToPage(
                        data.getStrValue(MyPushKey.ID_KEY),
                        ResourceTypeConstans.TYPE_COLUMN,
                        null,
                        null
                    )
                }
            }

        }

        fun turnToPage(resourceId: String, type: Int, link: String?, title: String?) {
            ResourceTurnManager.turnToResourcePage(object : BaseResourceInterface {
                override val _resourceId: String
                    get() = resourceId
                override val _resourceType: Int
                    get() = type
                override val _other: Map<String, String>
                    get() {
                        val hashMapOf: HashMap<String, String> = HashMap<String, String>()
                        if (TextUtils.isEmpty(link)) {
                            hashMapOf[IntentParamsConstants.WEB_PARAMS_URL] = link!!
                        }
                        if (TextUtils.isEmpty(title)) {
                            hashMapOf[IntentParamsConstants.WEB_PARAMS_TITLE] = title!!
                        }
                        return hashMapOf
                    }
            })
        }
    }


}