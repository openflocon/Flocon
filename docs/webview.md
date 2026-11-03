---
title: WebView Inspector
description: Follow page loads, console messages and JavaScript bridge calls of the WebViews of your app.
---

# 🌐 WebView Inspector

Flocon shows what happens inside the **WebViews** of your Android app: page loads and errors, `console.*` messages and calls from the page to native objects exposed to JavaScript.

---

## Overview

Every event carries the id of the WebView it comes from, and you can filter by kind: **Console**, **Pages** or **Bridge**.

Requests made by the page (`fetch` / `XMLHttpRequest`) are not listed here: they are sent to the [Network](network.md) page.

---

## Setup

Add the module, and its no-op version for release builds:

```kotlin
debugImplementation("io.github.openflocon:flocon-webview:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-webview-no-op:LAST_VERSION")
```

Install the plugin, then attach each WebView you want to inspect, with an id of your choice:

```kotlin
Flocon.initialize(context) {
    install(FloconWebView)
}

Flocon.webViewPlugin.attach(webView, "checkout")
```

### Bridge calls

Calls from the page to a native object are logged by your own code, since Flocon cannot see inside it:

```kotlin
Flocon.webViewPlugin.logBridgeCall(
    webViewId = "checkout",
    interfaceName = "Android",
    method = "getUser",
    arguments = "42",
    result = "{\"name\":\"Flo\"}",
)
```

See the [protocol](https://github.com/openflocon/Flocon/blob/main/protocol/webview.md) for the details of each event.
