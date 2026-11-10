package io.github.openflocon.flocon.webview

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

/**
 * Logs the JS console of a WebView to Flocon, and forwards every callback to [delegate]
 * so the behavior of the app's own [WebChromeClient] is unchanged.
 */
class FloconWebChromeClient(
    private val plugin: FloconWebViewPlugin,
    private val webViewId: String,
    private val webView: WebView? = null,
    private val delegate: WebChromeClient? = null,
) : WebChromeClient() {

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        if (consoleMessage != null) {
            plugin.logConsole(
                webViewId = webViewId,
                level = consoleMessage.messageLevel().toLevel(),
                message = consoleMessage.message().orEmpty(),
                url = webView?.url,
                sourceId = consoleMessage.sourceId()?.takeIf { it.isNotEmpty() },
                lineNumber = consoleMessage.lineNumber().takeIf { it > 0 },
            )
        }
        return delegate?.onConsoleMessage(consoleMessage) ?: super.onConsoleMessage(consoleMessage)
    }

    private fun ConsoleMessage.MessageLevel?.toLevel() = when (this) {
        ConsoleMessage.MessageLevel.DEBUG -> WebViewConsoleLevel.Debug
        ConsoleMessage.MessageLevel.WARNING -> WebViewConsoleLevel.Warn
        ConsoleMessage.MessageLevel.ERROR -> WebViewConsoleLevel.Error
        ConsoleMessage.MessageLevel.TIP -> WebViewConsoleLevel.Info
        else -> WebViewConsoleLevel.Log
    }

    // The callbacks below are only forwarded, so wrapping the client never changes the app's behavior.

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        delegate?.onProgressChanged(view, newProgress) ?: super.onProgressChanged(view, newProgress)
    }

    override fun onReceivedTitle(view: WebView?, title: String?) {
        delegate?.onReceivedTitle(view, title) ?: super.onReceivedTitle(view, title)
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        delegate?.onReceivedIcon(view, icon) ?: super.onReceivedIcon(view, icon)
    }

    override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean =
        delegate?.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
            ?: super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)

    override fun onCloseWindow(window: WebView?) {
        delegate?.onCloseWindow(window) ?: super.onCloseWindow(window)
    }

    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean =
        delegate?.onJsAlert(view, url, message, result) ?: super.onJsAlert(view, url, message, result)

    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean =
        delegate?.onJsConfirm(view, url, message, result) ?: super.onJsConfirm(view, url, message, result)

    override fun onJsPrompt(
        view: WebView?,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?,
    ): Boolean =
        delegate?.onJsPrompt(view, url, message, defaultValue, result)
            ?: super.onJsPrompt(view, url, message, defaultValue, result)

    override fun onPermissionRequest(request: PermissionRequest?) {
        delegate?.onPermissionRequest(request) ?: super.onPermissionRequest(request)
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        delegate?.onPermissionRequestCanceled(request) ?: super.onPermissionRequestCanceled(request)
    }

    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
        delegate?.onGeolocationPermissionsShowPrompt(origin, callback)
            ?: super.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    override fun onGeolocationPermissionsHidePrompt() {
        delegate?.onGeolocationPermissionsHidePrompt() ?: super.onGeolocationPermissionsHidePrompt()
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        delegate?.onShowCustomView(view, callback) ?: super.onShowCustomView(view, callback)
    }

    override fun onHideCustomView() {
        delegate?.onHideCustomView() ?: super.onHideCustomView()
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?,
    ): Boolean =
        delegate?.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            ?: super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
}
