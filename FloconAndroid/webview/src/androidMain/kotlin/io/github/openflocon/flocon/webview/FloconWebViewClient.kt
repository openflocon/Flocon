package io.github.openflocon.flocon.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Logs the page lifecycle of a WebView to Flocon, and forwards every callback to [delegate]
 * so the behavior of the app's own [WebViewClient] is unchanged.
 */
class FloconWebViewClient(
    private val plugin: FloconWebViewPlugin,
    private val webViewId: String,
    private val delegate: WebViewClient? = null,
) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        plugin.logPageEvent(webViewId, WebViewPageEventType.PageStarted, url = url)
        delegate?.onPageStarted(view, url, favicon) ?: super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        plugin.logPageEvent(webViewId, WebViewPageEventType.PageFinished, url = url)
        delegate?.onPageFinished(view, url) ?: super.onPageFinished(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request?.isForMainFrame == true) {
            plugin.logPageEvent(webViewId, WebViewPageEventType.Navigation, url = request.url?.toString())
        }
        return delegate?.shouldOverrideUrlLoading(view, request) ?: super.shouldOverrideUrlLoading(view, request)
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        plugin.logPageEvent(webViewId, WebViewPageEventType.Navigation, url = url)
        return delegate?.shouldOverrideUrlLoading(view, url) ?: super.shouldOverrideUrlLoading(view, url)
    }

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        plugin.logPageEvent(
            webViewId = webViewId,
            type = WebViewPageEventType.Error,
            url = request?.url?.toString(),
            message = error?.description?.toString(),
        )
        delegate?.onReceivedError(view, request, error) ?: super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        plugin.logPageEvent(
            webViewId = webViewId,
            type = WebViewPageEventType.HttpError,
            url = request?.url?.toString(),
            message = errorResponse?.reasonPhrase,
            statusCode = errorResponse?.statusCode,
        )
        delegate?.onReceivedHttpError(view, request, errorResponse) ?: super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        plugin.logPageEvent(
            webViewId = webViewId,
            type = WebViewPageEventType.SslError,
            url = error?.url,
            message = error?.toString(),
        )
        delegate?.onReceivedSslError(view, handler, error) ?: super.onReceivedSslError(view, handler, error)
    }

    // The callbacks below are only forwarded, so wrapping the client never changes the app's behavior.

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? =
        delegate?.shouldInterceptRequest(view, request) ?: super.shouldInterceptRequest(view, request)

    override fun onLoadResource(view: WebView?, url: String?) {
        delegate?.onLoadResource(view, url) ?: super.onLoadResource(view, url)
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        delegate?.onPageCommitVisible(view, url) ?: super.onPageCommitVisible(view, url)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        delegate?.doUpdateVisitedHistory(view, url, isReload) ?: super.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onFormResubmission(view: WebView?, dontResend: Message?, resend: Message?) {
        delegate?.onFormResubmission(view, dontResend, resend) ?: super.onFormResubmission(view, dontResend, resend)
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView?,
        handler: android.webkit.HttpAuthHandler?,
        host: String?,
        realm: String?,
    ) {
        delegate?.onReceivedHttpAuthRequest(view, handler, host, realm)
            ?: super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun onReceivedClientCertRequest(view: WebView?, request: android.webkit.ClientCertRequest?) {
        delegate?.onReceivedClientCertRequest(view, request) ?: super.onReceivedClientCertRequest(view, request)
    }

    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean =
        delegate?.onRenderProcessGone(view, detail) ?: super.onRenderProcessGone(view, detail)

    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean =
        delegate?.shouldOverrideKeyEvent(view, event) ?: super.shouldOverrideKeyEvent(view, event)

    override fun onUnhandledKeyEvent(view: WebView?, event: KeyEvent?) {
        delegate?.onUnhandledKeyEvent(view, event) ?: super.onUnhandledKeyEvent(view, event)
    }

    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
        delegate?.onScaleChanged(view, oldScale, newScale) ?: super.onScaleChanged(view, oldScale, newScale)
    }
}
