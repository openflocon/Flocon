---
title: WebView Viewer
description: See the JS console, the page events and the bridge calls of the WebViews of your app.
---

# 🌐 WebView Viewer

Flocon displays what happens inside the **WebViews** of your application: the JS console, the page loads and errors, and the calls from the page to your native code.
No more `chrome://inspect` and USB debugging to read a `console.error`.

---

## Overview

The WebViews screen lists three kinds of events, newest first:

- **Console**: `console.log`, `info`, `warn` and `error` of the page, with the source file and line when the browser gives them
- **Pages**: page started / finished, navigation, and the errors (network error, HTTP error, SSL error)
- **Bridge**: the calls from the page to a native object exposed with `addJavascriptInterface`, with their arguments and result

Filter by kind with the chips at the top, or search in the level, message, url and WebView id. Each WebView is identified by the id you give it, so several WebViews of the same app can be told apart.

---

## Setup

```kotlin
dependencies {
    debugImplementation("io.github.openflocon:flocon-webview:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-webview-no-op:LAST_VERSION")
}
```

Install the plugin when you start Flocon:

```kotlin
startFlocon(FloconContext(applicationContext)) {
    install(FloconWebView)
}
```

---

## Usage (Android)

Attach each WebView once, **after** you set your own `WebViewClient` / `WebChromeClient`:

```kotlin
webView.settings.javaScriptEnabled = true
webView.webViewClient = MyWebViewClient()
webView.webChromeClient = MyWebChromeClient()

Flocon.webViewPlugin.attach(webView, webViewId = "checkout")
```

`attach` wraps your clients: every callback is still forwarded to them, so the behavior of your WebView does not change. The wrappers are read back from the WebView on Android 8.0 (API 26) and above. Below, wrap your clients yourself:

```kotlin
webView.webViewClient = FloconWebViewClient(Flocon.webViewPlugin, "checkout", delegate = MyWebViewClient())
webView.webChromeClient = FloconWebChromeClient(Flocon.webViewPlugin, "checkout", webView, delegate = MyWebChromeClient())
```

---

## Bridge calls

Flocon cannot see the calls made to an object you expose with `addJavascriptInterface`: log them from the method itself.

```kotlin
class AppBridge {
    @JavascriptInterface
    fun share(text: String): String {
        val result = shareUseCase(text)
        Flocon.webViewPlugin.logBridgeCall(
            webViewId = "checkout",
            interfaceName = "Android",
            method = "share",
            arguments = text,
            result = result,
        )
        return result
    }
}
```

---

## Manual API

The same functions are available on every platform, for a WebView Flocon does not attach to:

```kotlin
Flocon.webViewPlugin.logConsole(webViewId = "checkout", level = WebViewConsoleLevel.Warn, message = "slow render")
Flocon.webViewPlugin.logPageEvent(webViewId = "checkout", type = WebViewPageEventType.PageFinished, url = "https://example.com")
```

---

## Platform support

| Platform | Capture |
| :--- | :--- |
| Android | Console, page events and errors with `attach`. Bridge calls with `logBridgeCall` |
| iOS, JVM, Wasm | Manual API only, no `attach` yet |

The requests made by the page (`fetch`, `XMLHttpRequest`) are not captured yet.

---

## AI Agents (MCP)

Your agent can read these events with `list_webview_events`. See [AI Agents (MCP)](mcp.md).
