package com.flocon.data.remote.mock.scenarios

import com.flocon.data.remote.mock.ago
import com.flocon.data.remote.mock.scenario
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// The bundled scenarios, registered in MockScenarios. One device per scenario unless stated.

internal val defaultScenario = scenario("default", description = "A Pixel 8 with a bit of every plugin: network, timeline, crashes, analytics, feature flags") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) {
        sampleNetworkCalls()
        sampleTimeline()
        sampleCrashes()
        sampleAnalytics()
        sampleFeatureFlags()
    }
}

internal val docNetworkScenario = scenario("doc-network", description = "Network calls only: JSON bodies, headers, 4xx/5xx, timeout, GraphQL, a pending call") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleNetworkCalls() }
}

internal val docTimelineScenario = scenario("doc-timeline", description = "Timeline logs only: navigation, user actions, lifecycle and tagged logs of every level") {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE) { sampleTimeline() }
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
