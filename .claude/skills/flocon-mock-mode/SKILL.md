---
name: flocon-mock-mode
description: Use when you need Flocon Desktop running with data and no phone connected - doc screenshots, checking how a screen looks in a given state, testing MCP tools on known data, edge cases (huge bodies, invalid JSON, many devices).
---

# Flocon Desktop mock mode

`--mock=<scenario>` starts Flocon Desktop with fake connected devices and pre-filled data. The data goes through the real handlers, Room and UI, exactly like a real SDK would send it. Timestamps are relative to the launch time, so the screens always look recent.

Covered plugins: **network, images (network calls with an `image/*` content type), timeline, crash reporter, analytics, feature flags, deeplinks, database, files, shared preferences, dashboards, tables, accessibility, webview** (+ devices/apps).

## Launch

From `FloconDesktop/`, with JDK 21:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
./gradlew :composeApp:run --args="--mock=default"
```

- `--mock=list` (or `--mock`) prints the scenarios and exits.
- An unknown name exits with the list of available names.
- Run it in the background: the app blocks until its window is closed. It is ready when the MCP port answers: `lsof -nP -iTCP:9042 -sTCP:LISTEN`.
- Each mock launch wipes and reuses `~/.flocon/mock/flocon_room.db`: the same scenario always gives the same screens. The real database (`~/.flocon/flocon_room.db`) is never touched.
- adb and the iOS simulator are disabled in mock mode (no command reaches a real phone).

## Limits

- The MCP server listens on the usual port **9042**: close the real Flocon Desktop first, or the mock one can't start its MCP server and your MCP calls reach the real app.
- Settings (theme, app scale, hidden menus, columns…) are the user's real settings, shared with the normal app.
- Commands sent by the desktop to the device (network mocks, bad network, toggling a flag, dashboard clicks, TalkBack, focus next/previous…) are accepted and ignored.
- Requests the desktop expects an answer to are answered from the scenario data by `MockDeviceResponder`: the database (list of databases, `sqlite_master`, `PRAGMA table_info`, `SELECT cols|*|COUNT(*) FROM t [WHERE c = v] [LIMIT n OFFSET m]`; any other SQL is an error), the files (roots, listing, delete, download of the `content`) and the preferences (read, set, remove; kept in memory). Edits last until the app closes.
- Images load from their url: use hosts that are reachable (`images.unsplash.com`), `picsum.photos` is flaky.

## Scenarios

| Name | Content |
|---|---|
| `default` | A Pixel 8 with a bit of every plugin (all of the above) |
| `doc-network` | Network only: JSON bodies, headers, 4xx/5xx, timeout, GraphQL, a pending call |
| `doc-timeline` | Timeline logs only: navigation, user actions, lifecycle, every level |
| `doc-crashreporter` | Crashes with stack traces |
| `doc-analytics` | Analytics events in two tables |
| `doc-featureflags` | Feature flags from two providers |
| `doc-deeplinks` | Deeplinks only: a link with a variable, a link with fields, plain links, three shared variables |
| `doc-database` | `app_database` (users, products, orders, order_items), `cache.db`, query logs with a transaction |
| `doc-files` | `files`, `caches`, `databases`, `shared_prefs` roots with folders and files |
| `doc-sharedprefs` | SharedPreferences and DataStore files, every value type |
| `doc-images` | 12 product photos (jpg, webp, png) with sizes and headers |
| `doc-dashboard` | `user` and `debug` dashboards: sections, form, button, text field, check box, json, markdown, html |
| `doc-tables` | Tables `orders` and `search_history` |
| `doc-accessibility` | TalkBack on, focus, clicks, announcements, an unlabeled button |
| `doc-webview` | Two WebViews: console logs, page events, bridge calls |
| `empty` | A connected device with no data (empty states) |
| `multi-device` | Android with two apps + an iPhone |
| `weird` | 5 MB body, invalid JSON, unicode, 50×500, 2 000-char URL, old SDK version, odd timestamps |

Code: `FloconDesktop/data/remote/src/commonMain/kotlin/com/flocon/data/remote/mock/` (`scenarios/Scenarios.kt`, the shared `scenarios/SampleData.kt` and `scenarios/DocSampleData.kt`, the request answers in `MockDeviceResponder.kt`).

## Take screenshots / check the data

With the mock app running, use its MCP server (`http://localhost:9042/mcp`, the `flocon` MCP tools if connected):

- `flocon_navigate_into_desktop_app` with `screen` (`network`, `timeline`, `analytics`, `feature_flags`, `crash_reporter`…) to show a screen.
- `take_flocon_desktop_screenshot` returns a PNG of the window.
- `list_devices`, `list_network_calls`, `get_timeline`, `list_crash_reports`, `list_analytics_events`, `list_feature_flags` to check the data.

Without the MCP tools connected, call them with curl (stateless JSON-RPC):

```bash
curl -s -H 'Content-Type: application/json' -H 'Accept: application/json, text/event-stream' \
  http://localhost:9042/mcp \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"take_flocon_desktop_screenshot","arguments":{}}}'
```

The image is base64 in `result.content[].data`.

## Write a new scenario

Scenarios are Kotlin, compiled with the app, and **committed** with the change they test.

1. Add a file in `mock/scenarios/` (or extend `Scenarios.kt`):

```kotlin
internal val checkoutErrorsScenario = scenario("checkout-errors", description = "Checkout failing in every way") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) {
        network(5.minutes.ago, "POST", "https://api.example.com/checkout", status = 500, duration = 1.seconds) {
            requestHeader("content-type", "application/json")
            requestBody("""{"cartId":"c-1"}""")
            responseBody("""{"error":"internal_error"}""")
        }
        network(4.minutes.ago, "POST", "https://api.example.com/checkout", error = "java.net.SocketTimeoutException: timeout")
        network(3.minutes.ago, "GET", "https://api.example.com/slow", pending = true)
        repeat(100) { network((200 - it).seconds.ago, "GET", "https://api.example.com/items/$it") }
        timeline(6.minutes.ago, "CheckoutScreen", type = "navigation", level = null)
        timeline(3.minutes.ago, "Checkout failed", type = "billing", level = "error", params = mapOf("code" to "500"))
        crash(2.minutes.ago, exceptionType = "java.lang.IllegalStateException", message = "boom", stackTrace = "at a.b(C.kt:1)")
        analytics(1.minutes.ago, "checkout_error", "code" to "500", tableId = "analytics")
        featureFlags("firebase") { flag("checkout_v2", state = "custom", variant = "treatment", payload = "{}") }
        raw(30.seconds.ago, plugin = "network", method = "log_network_call_request", body = "{not json")
    }
    ios("iPhone 16", app = "Shop", packageName = "com.example.shop", appStartedAt = 10.minutes.ago, sdkVersion = "1.0.0") { }
}
```

2. Add it to `MockScenarios.all` (`mock/MockScenarios.kt`).
3. `./gradlew :data:remote:desktopTest --tests "*MockScenariosTest"` (update the expected name list there), then launch it.

DSL notes:
- `at` is the time **before launch** (`5.minutes.ago`).
- `appStartedAt` is when the app started (default: 1 s before its first event); the timeline session starts there.
- `sdkVersion` is the Flocon SDK version on the device (default: the desktop version, so no "SDK out of date" banner).
- `databases { database("name") { table("t") { column(..); row(..) } } }`, `databaseQuery(..)` (query log), `files { root(id, name, path) { file(..); folder(..) { } } }`, `sharedPreferences { preferences("name", type) { string/int/long/float/boolean/stringSet } }`, `dashboard(id) { section/form { text, label, button, textField, checkBox, json, plainText, markdown, html } }`, `tableRow(at, table, "col" to "v")`, `accessibilityState(..)`, `accessibility(..)`, `webViewConsole/Page/Bridge(..)`.
- `responseSize(bytes)` in a `network` block sets the size of a body that is not kept (images).
- `raw(plugin, method, body)` sends any message as is, to test what a real SDK would not send.
- Timeline types `navigation`, `userAction`, `lifecycle` have their own icon; use `level = null` for them.
