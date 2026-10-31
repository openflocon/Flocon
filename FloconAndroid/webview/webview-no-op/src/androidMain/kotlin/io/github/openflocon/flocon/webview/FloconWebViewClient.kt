package io.github.openflocon.flocon.webview

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.webkit.ClientCertRequest
import android.webkit.HttpAuthHandler
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

/** No-op: logs nothing, and only forwards every callback to [delegate]. */
@Suppress("UNUSED_PARAMETER")
class FloconWebViewClient(
    plugin: FloconWebViewPlugin,
    webViewId: String,
    private val delegate: WebViewClient? = null,
) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        delegate?.onPageStarted(view, url, favicon) ?: super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        delegate?.onPageFinished(view, url) ?: super.onPageFinished(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean =
        delegate?.shouldOverrideUrlLoading(view, request) ?: super.shouldOverrideUrlLoading(view, request)

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean =
        delegate?.shouldOverrideUrlLoading(view, url) ?: super.shouldOverrideUrlLoading(view, url)

    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        delegate?.onReceivedError(view, request, error) ?: super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        delegate?.onReceivedHttpError(view, request, errorResponse) ?: super.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        delegate?.onReceivedSslError(view, handler, error) ?: super.onReceivedSslError(view, handler, error)
    }

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

    override fun onReceivedHttpAuthRequest(view: WebView?, handler: HttpAuthHandler?, host: String?, realm: String?) {
        delegate?.onReceivedHttpAuthRequest(view, handler, host, realm)
            ?: super.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun onReceivedClientCertRequest(view: WebView?, request: ClientCertRequest?) {
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
