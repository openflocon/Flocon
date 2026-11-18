package com.flocon.data.remote.mock.scenarios

import com.flocon.data.remote.mock.ago
import com.flocon.data.remote.mock.scenario
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// The bundled scenarios, registered in MockScenarios. One device per scenario unless stated.

internal val defaultScenario = scenario("default", description = "A Pixel 8 with a bit of every plugin: network, timeline, crashes, analytics, feature flags, deeplinks, database, files, preferences, images, dashboard, tables, accessibility, webview") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) {
        sampleNetworkCalls()
        sampleTimeline()
        sampleCrashes()
        sampleAnalytics()
        sampleFeatureFlags()
        sampleDeeplinks()
        sampleDatabases()
        sampleFiles()
        sampleSharedPreferences()
        sampleImages()
        sampleDashboard()
        sampleTables()
        sampleAccessibility()
        sampleWebView()
    }
}

internal val docNetworkScenario = scenario("doc-network", description = "Network calls only: JSON bodies, headers, 4xx/5xx, timeout, GraphQL, a pending call") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleNetworkCalls() }
}

internal val docGraphQlScenario = scenario("doc-graphql", description = "Network with GraphQL calls only: queries and mutations on one /graphql endpoint, two with an errors array and one HTTP error") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleGraphQlCalls() }
}

internal val docWebSocketScenario = scenario("doc-websocket", description = "Network with a few HTTP calls and many WebSocket events on two sockets: open, sent, received, error, closed") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleWebSocketSession() }
}

internal val docGrpcScenario = scenario("doc-grpc", description = "Network with mostly gRPC calls: unary calls on several services, an UNAVAILABLE, a DEADLINE_EXCEEDED, metadata and JSON messages") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleGrpcCalls() }
}

internal val docTimelineScenario = scenario("doc-timeline", description = "A 2.5 minutes session for the timeline list and graph: network calls with durations, database queries, analytics, logs and a crash") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE, appStartedAt = 170.seconds.ago) { sampleRichTimelineSession() }
}

internal val docCrashReporterScenario = scenario("doc-crashreporter", description = "Crashes only, with stack traces") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleCrashes() }
}

internal val docAnalyticsScenario = scenario("doc-analytics", description = "Analytics events only, in two tables (analytics, experiments)") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleAnalytics() }
}

internal val docFeatureFlagsScenario = scenario("doc-featureflags", description = "Feature flags only, from two providers (firebase, statsig)") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleFeatureFlags() }
}

internal val docDeeplinksScenario = scenario("doc-deeplinks", description = "Deeplinks only: a variable-based link, a link with fields, a plain link, and three shared variables") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleDeeplinks() }
}

internal val docDatabaseScenario = scenario("doc-database", description = "Databases only: a Room database (users, products, orders), a cache database and the queries the app ran") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleDatabases() }
}

internal val docFilesScenario = scenario("doc-files", description = "Files only: files, caches and shared_prefs roots with folders and files") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleFiles() }
}

internal val docSharedPrefsScenario = scenario("doc-sharedprefs", description = "Preferences only: SharedPreferences and DataStore files with every value type") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleSharedPreferences() }
}

internal val docImagesScenario = scenario("doc-images", description = "Images only: image downloads (jpeg, webp, png) with their headers") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleImages() }
}

internal val docDashboardScenario = scenario("doc-dashboard", description = "Dashboards only: user and debug dashboards with sections, forms, buttons, text fields, check boxes, json, markdown and html") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleDashboard() }
}

internal val docTablesScenario = scenario("doc-tables", description = "Tables only: two tables (orders, search_history) filled by the app") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleTables() }
}

internal val docAccessibilityScenario = scenario("doc-accessibility", description = "Accessibility only: TalkBack on, focus, clicks, announcements and an unlabeled button") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleAccessibility() }
}

internal val docWebViewScenario = scenario("doc-webview", description = "WebView only: console logs, page events and bridge calls of a checkout page") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleWebView() }
}

internal val emptyScenario = scenario("empty", description = "A connected Pixel 8 with no data, to see the empty states") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) {}
}

internal val multiDeviceScenario = scenario("multi-device", description = "An Android phone with two apps and an iPhone") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) {
        sampleNetworkCalls()
        sampleTimeline()
    }
    android("Pixel 8", app = "Flocon Admin", packageName = "io.github.openflocon.admin") {
        network(4.minutes.ago, "GET", "https://admin.flocon-shop.com/v1/orders?status=pending", duration = 210.milliseconds) {
            responseHeader("content-type", "application/json")
            responseBody("""{"orders":[{"id":"o-5521","status":"pending"}]}""")
        }
        timeline(4.minutes.ago, "OrdersScreen", type = "navigation", level = null)
    }
    ios("iPhone 16", app = SAMPLE_APP, packageName = "io.github.openflocon.sample.ios") {
        network(3.minutes.ago, "GET", "https://api.flocon-shop.com/v1/config", duration = 120.milliseconds) {
            responseHeader("content-type", "application/json")
            responseBody("""{"minVersion":"2.1.0","maintenance":false}""")
        }
        timeline(3.minutes.ago, "HomeView", type = "navigation", level = null)
        sampleAnalytics()
        sampleFeatureFlags()
    }
}

internal val weirdScenario = scenario("weird", description = "Edge cases: huge and invalid bodies, unicode, 500 storm, very long URL, old SDK, odd timestamps") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE, sdkVersion = "1.0.0", appStartedAt = 15.minutes.ago) {
        // the 30-day-old call below happened "before" the app started, on purpose
        network(10.minutes.ago, "GET", "https://api.flocon-shop.com/v1/export", duration = 4_800.milliseconds) {
            responseHeader("content-type", "application/json")
            responseBody(hugeJson())
        }
        network(9.minutes.ago, "GET", "https://api.flocon-shop.com/v1/broken", duration = 40.milliseconds) {
            responseHeader("content-type", "application/json")
            responseBody("""{"items":[{"id":1,"name":"unterminated""")
        }
        network(8.minutes.ago, "GET", "https://api.flocon-shop.com/v1/search?q=caf%C3%A9%20%F0%9F%8D%B0&lang=ar") {
            responseHeader("content-type", "application/json")
            responseBody("""{"query":"café 🍰","results":["مرحبا بالعالم","日本語のテキスト","Ünïcödé ✓","👩‍👩‍👧‍👦"]}""")
        }
        repeat(50) { index ->
            network((7.minutes - index.seconds).ago, "GET", "https://api.flocon-shop.com/v1/status", status = 500, duration = (900 + index * 10).milliseconds) {
                responseBody("""{"error":"internal_error","attempt":$index}""")
            }
        }
        network(5.minutes.ago, "GET", "https://api.flocon-shop.com/v1/track?" + (1..120).joinToString("&") { "param$it=value_$it" })
        network(4.minutes.ago, "GET", "https://api.flocon-shop.com/v1/empty", status = 200, duration = 0.milliseconds)
        network(30.days.ago, "GET", "https://api.flocon-shop.com/v1/very-old-call")
        network(0.seconds.ago, "GET", "https://api.flocon-shop.com/v1/right-now", pending = true)
        raw(3.minutes.ago, plugin = "network", method = "log_network_call_request", body = "{not json")
        raw(3.minutes.ago, plugin = "unknown_plugin", method = "whatever", body = "{}")
        timeline(2.minutes.ago, "A log with an unknown level", type = "custom", level = "catastrophic")
        timeline(2.minutes.ago, "x".repeat(2_000), type = "custom")
        timeline(1.minutes.ago, "Ünïcödé 🍰 مرحبا", type = "🔥 emoji type", params = mapOf("ключ" to "значение"))
        crash(1.minutes.ago, exceptionType = "java.lang.OutOfMemoryError", message = "", stackTrace = "")
        analytics(1.minutes.ago, "event_with_many_properties", *(1..40).map { "prop_$it" to "value_$it" }.toTypedArray())
        databases {
            // long names test the truncation
            listOf(
                "app_database",
                "Application Support/BeReal.sqlite",
                "Application Support/Google/FirebaseCore/Measurement/google-app-measurement.sqlite",
                "Documents/very/long/path/to/a/database/shake.sqlite3",
            ).forEach { database(it) }
        }
        featureFlags("firebase") {
            flag("weird_payload", state = "custom", variant = "v", payload = "not json at all")
            flag("unknown_state", state = "maybe", variant = "?")
        }
    }
}

private fun hugeJson(): String {
    // ~5 MB
    val item = """{"id":0,"name":"Item with a long enough name to take some room","tags":["a","b","c"],"description":"${"lorem ipsum ".repeat(20)}"}"""
    return (0 until 15_000).joinToString(separator = ",", prefix = """{"items":[""", postfix = "]}") { item.replace("\"id\":0", "\"id\":$it") }
}
