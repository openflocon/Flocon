package io.github.openflocon.flocon.myapplication.multi.ui

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.ui.viewinterop.AndroidView
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.myapplication.multi.ui.screens.SampleWebViewHost
import io.github.openflocon.flocon.webview.attach
import io.github.openflocon.flocon.webview.webViewPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val WebViewId = "sample"
private const val BaseUrl = "https://sample.flocon.local/"

private val SamplePage = """
    <html>
    <body style="font-family: sans-serif; padding: 16px;">
        <h3>Flocon WebView sample</h3>
        <p>Use the rows under this WebView, then look at the WebViews screen of the desktop app.</p>
        <script>
            function logAll() {
                console.log('log from the page');
                console.info('info from the page');
                console.warn('warn from the page');
                console.error('error from the page');
            }
            function throwError() {
                undefinedFunction();
            }
            function callBridge() {
                return FloconSample.share('hello from the page');
            }
        </script>
    </body>
    </html>
""".trimIndent()

/** The bridge exposed to the page: the calls are logged by hand, Flocon cannot see them otherwise. */
private class SampleBridge(private val webView: () -> WebView?) {
    @JavascriptInterface
    fun share(text: String): String {
        val result = "shared: $text"
        Flocon.webViewPlugin.logBridgeCall(
            webViewId = WebViewId,
            interfaceName = "FloconSample",
            method = "share",
            arguments = text,
            result = result,
            url = webView()?.url,
        )
        return result
    }
}

private var sampleWebView: WebView? = null

/** Shows a WebView on the WebView screen of the sample, and runs its rows on it. */
@SuppressLint("SetJavaScriptEnabled")
fun registerAndroidSampleWebView() {
    SampleWebViewHost.content = { modifier ->
        AndroidView(
            modifier = modifier,
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    addJavascriptInterface(SampleBridge { sampleWebView }, "FloconSample")
                    Flocon.webViewPlugin.attach(this, WebViewId)
                    loadSamplePage()
                    sampleWebView = this
                }
            },
            onRelease = { if (sampleWebView === it) sampleWebView = null },
        )
    }

    SampleFeatures.on(SampleFeature.WebViewConsole) { runOnWebView { evaluateJavascript("logAll()", null) } }
    SampleFeatures.on(SampleFeature.WebViewJsError) { runOnWebView { evaluateJavascript("throwError()", null) } }
    SampleFeatures.on(SampleFeature.WebViewBridge) { runOnWebView { evaluateJavascript("callBridge()", null) } }
    SampleFeatures.on(SampleFeature.WebViewNotFound) {
        runOnWebView { loadUrl("https://example.com/flocon-not-found") }
    }
    SampleFeatures.on(SampleFeature.WebViewReload) { runOnWebView { loadSamplePage() } }
}

private fun WebView.loadSamplePage() {
    loadDataWithBaseURL(BaseUrl, SamplePage, "text/html", "UTF-8", null)
}

private suspend fun runOnWebView(block: WebView.() -> Unit) {
    val webView = sampleWebView ?: error("Open the WebView screen first, the WebView is created there")
    withContext(Dispatchers.Main) { webView.block() }
}
