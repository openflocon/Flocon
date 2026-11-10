package io.github.openflocon.flocon.webview

import android.webkit.WebView

/** No-op: leaves the clients of [webView] untouched. */
@Suppress("UNUSED_PARAMETER")
fun FloconWebViewPlugin.attach(webView: WebView, webViewId: String) = Unit
