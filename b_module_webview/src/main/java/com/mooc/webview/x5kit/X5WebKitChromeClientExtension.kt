package com.mooc.webview.x5kit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import com.mooc.webview.stratage.WebViewInitStrategy
import com.tencent.smtt.export.external.extension.interfaces.IX5WebChromeClientExtension
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension
import com.tencent.smtt.export.external.interfaces.IX5WebViewBase
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.MediaAccessPermissionsCallback
import java.util.*

class X5WebKitChromeClientExtension(var startage: WebViewInitStrategy? = null) : IX5WebChromeClientExtension {
    override fun getX5WebChromeClientInstance(): Any {
        TODO("Not yet implemented")
    }

    override fun getVideoLoadingProgressView(): View {
        TODO("Not yet implemented")
    }

    override fun onAllMetaDataFinished(p0: IX5WebViewExtension?, p1: HashMap<String, String>?) {
        TODO("Not yet implemented")
    }

    override fun onBackforwardFinished(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onHitTestResultForPluginFinished(p0: IX5WebViewExtension?, p1: IX5WebViewBase.HitTestResult?, p2: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onHitTestResultFinished(p0: IX5WebViewExtension?, p1: IX5WebViewBase.HitTestResult?) {
        TODO("Not yet implemented")
    }

    override fun onPromptScaleSaved(p0: IX5WebViewExtension?) {
        TODO("Not yet implemented")
    }

    override fun onPromptNotScalable(p0: IX5WebViewExtension?) {
        TODO("Not yet implemented")
    }

    override fun onAddFavorite(p0: IX5WebViewExtension?, p1: String?, p2: String?, p3: JsResult?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onPrepareX5ReadPageDataFinished(p0: IX5WebViewExtension?, p1: HashMap<String, String>?) {
        TODO("Not yet implemented")
    }

    override fun onSavePassword(p0: String?, p1: String?, p2: String?, p3: Boolean, p4: Message?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSavePassword(p0: android.webkit.ValueCallback<String>?, p1: String?, p2: String?, p3: String?, p4: String?, p5: String?, p6: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun onX5ReadModeAvailableChecked(p0: HashMap<String, String>?) {
        TODO("Not yet implemented")
    }

    override fun addFlashView(p0: View?, p1: ViewGroup.LayoutParams?) {
        TODO("Not yet implemented")
    }

    override fun h5videoRequestFullScreen(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun h5videoExitFullScreen(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun requestFullScreenFlash() {
        TODO("Not yet implemented")
    }

    override fun exitFullScreenFlash() {
        TODO("Not yet implemented")
    }

    override fun jsRequestFullScreen() {
        TODO("Not yet implemented")
    }

    override fun jsExitFullScreen() {
        TODO("Not yet implemented")
    }

    override fun acquireWakeLock() {
        TODO("Not yet implemented")
    }

    override fun releaseWakeLock() {
        TODO("Not yet implemented")
    }

    override fun getApplicationContex(): Context {
        TODO("Not yet implemented")
    }

    override fun onPageNotResponding(p0: Runnable?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onMiscCallBack(p0: String?, p1: Bundle?): Any {
        TODO("Not yet implemented")
    }

    override fun openFileChooser(p0: android.webkit.ValueCallback<Array<Uri>>?, p1: String?, p2: String?) {
        TODO("Not yet implemented")
    }

    override fun onPrintPage() {
        TODO("Not yet implemented")
    }

    override fun onColorModeChanged(p0: Long) {
        TODO("Not yet implemented")
    }

    override fun onPermissionRequest(p0: String?, p1: Long, p2: MediaAccessPermissionsCallback?): Boolean {
        p2?.invoke(p0, MediaAccessPermissionsCallback.ALLOW_AUDIO_CAPTURE, true)
        return true
    }


}