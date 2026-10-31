package io.github.openflocon.flocon.webview

import android.os.Build
import android.webkit.WebView

/**
 * Logs the console and the page lifecycle of [webView] to Flocon.
 *
 * Call it **after** the app has set its own `WebViewClient` / `WebChromeClient`: they are kept and called
 * (read back on API 26+). Below API 26 the existing clients cannot be read, so use
 * [FloconWebViewClient] / [FloconWebChromeClient] with your own client as `delegate` instead.
 */
fun FloconWebViewPlugin.attach(webView: WebView, webViewId: String) {
    val canReadClients = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    webView.webViewClient = FloconWebViewClient(
        plugin = this,
        webViewId = webViewId,
        delegate = if (canReadClients) webView.webViewClient else null,
    )
    webView.webChromeClient = FloconWebChromeClient(
        plugin = this,
        webViewId = webViewId,
        webView = webView,
        delegate = if (canReadClients) webView.webChromeClient else null,
    )
}
