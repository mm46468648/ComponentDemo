package com.mooc.webview.interfaces

import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.common.ktextends.loge
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.route.routeservice.ShareSelectTextService
import com.mooc.commonbusiness.route.routeservice.StudyRoomService
import com.mooc.webview.R
import org.json.JSONObject
import org.w3c.dom.Text

/**
 * 自定义长按事件回调接口
 * @param selectFinish 选中操作完毕
 */
class ActionSelectInterface(var context: Context, var jsonObject: JSONObject?,var selectFinish : (()->Unit)? = null) {

//    val mViewModel: StudyListMoveViewModel by lazy {
//        ViewModelProviders.of(this)[StudyListMoveViewModel::class.java]
//    }

    @JavascriptInterface
    open fun callback(value: String, title: String) {
        if (TextUtils.isEmpty(value)) {
            Toast.makeText(
                context,
                context.getString(R.string.text_str_content_null),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        //这个时候有可能选中弹框没有消失，再调用一下消失方法
        when (title) {
            "笔记" -> {
                if (jsonObject == null) {
                    jsonObject = JSONObject()
                }
                jsonObject?.put("content", value)
                jsonObject?.put("resource_type", ResourceTypeConstans.TYPE_NOTE)
                selectFinish?.invoke()
                addNote()
            }
            "复制" -> {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.text = if (TextUtils.isEmpty(value)) "" else value
                Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
            }
            "分享" -> {
                if (jsonObject == null) {
                    jsonObject = JSONObject()
                }
                jsonObject?.put("content", value)
                jsonObject?.put("resource_type", ResourceTypeConstans.TYPE_NOTE)
                share(jsonObject)
            }
            else -> {
            }
        }
        loge(value + title)
    }

    private fun share(jsonObject: JSONObject?) {
        val shareSelectTextService: ShareSelectTextService? =
            ARouter.getInstance().navigation(ShareSelectTextService::class.java)
        if (jsonObject != null) {
            shareSelectTextService?.shareSelectText(context, jsonObject) {
                selectFinish?.invoke()
            }
        }
    }


    fun addNote() {

        val studyRoomService: StudyRoomService? =
            ARouter.getInstance().navigation(StudyRoomService::class.java)
        if (jsonObject != null) {
            studyRoomService?.showAddToStudyRoomPop(context, jsonObject) {

            }
        }

    }


}