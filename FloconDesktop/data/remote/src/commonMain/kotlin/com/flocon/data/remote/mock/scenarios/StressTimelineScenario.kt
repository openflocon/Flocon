package com.flocon.data.remote.mock.scenarios

import com.flocon.data.remote.mock.MockDeviceBuilder
import com.flocon.data.remote.mock.ago
import com.flocon.data.remote.mock.scenario
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal val stressTimelineScenario = scenario(
    "stress-timeline",
    description = "Timeline / graph stress: ~1 000 network calls in bursts (simultaneous), chains (a few ms apart) and long overlapping calls, plus logs, crashes and analytics",
) {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE, appStartedAt = 12.minutes.ago) {
        val random = Random(42)
        val hosts = listOf("api.flocon-shop.com", "cdn.flocon-shop.com", "auth.flocon-shop.com", "ads.tracker.io")
        val methods = listOf("GET", "GET", "GET", "POST", "PUT", "DELETE")
        var counter = 0

        fun MockDeviceBuilder.call(at: Duration, duration: Duration = (30 + random.nextInt(600)).milliseconds) {
            val n = counter++
            val roll = random.nextInt(100)
            val status = when {
                roll < 80 -> 200
                roll < 88 -> 304
                roll < 94 -> 404
                roll < 98 -> 500
                else -> 429
            }
            val host = hosts[random.nextInt(hosts.size)]
            network(
                at,
                methods[random.nextInt(methods.size)],
                "https://$host/v1/resource/$n?page=${n % 7}",
                status = status,
                duration = duration,
            ) {
                responseHeader("content-type", "application/json")
                responseBody("""{"id":$n,"status":$status}""")
            }
        }

        // Quiet start: a call every ~10 s.
        repeat(8) { call((720 - it * 10).seconds.ago) }

        // 12 bursts, spread over the session: 20 to 60 calls starting at the exact same instant.
        repeat(12) { burst ->
            val start = (680 - burst * 52).seconds.ago
            repeat(20 + random.nextInt(41)) { call(start, (50 + random.nextInt(1500)).milliseconds) }
        }

        // 15 chains: 15 to 40 calls, 5 to 60 ms apart (almost overlapping markers).
        repeat(15) { chain ->
            val start = 700.seconds - (chain * 45).seconds
            var offset = Duration.ZERO
            repeat(15 + random.nextInt(26)) {
                call((start - offset).coerceAtLeast(Duration.ZERO), (20 + random.nextInt(150)).milliseconds)
                offset += (5 + random.nextInt(56)).milliseconds
            }
        }

        // Steady drizzle: a call every 700 ms for the last 5 minutes.
        repeat(430) { call((300 - it * 0.7).coerceAtLeast(0.0).seconds.ago, (100 + random.nextInt(300)).milliseconds) }

        // Long overlapping calls: stay open while everything else happens.
        repeat(6) {
            network((600 - it * 80).seconds.ago, "GET", "https://api.flocon-shop.com/v1/stream/$it", duration = (20 + it * 10).seconds) {
                responseHeader("content-type", "text/event-stream")
            }
        }
        network(90.seconds.ago, "GET", "https://api.flocon-shop.com/v1/never-ends", pending = true)
        network(45.seconds.ago, "POST", "https://api.flocon-shop.com/v1/upload", error = "java.net.SocketTimeoutException: timeout", duration = 30.seconds)
        repeat(10) { grpc((400 - it * 3).seconds.ago, "grpc.flocon-shop.com:443", "shop.Catalog/List", duration = (40 + it * 15).milliseconds) }

        // Other plugins, in bursts and chains too.
        val screens = listOf("HomeScreen", "SearchScreen", "ProductScreen", "CartScreen", "CheckoutScreen", "ProfileScreen")
        repeat(40) { timeline((700 - it * 17).seconds.ago, screens[it % screens.size], type = "navigation", level = null) }
        repeat(60) { timeline((690 - it * 11).seconds.ago, "tap #$it", type = "userAction", level = null) }
        repeat(8) { timeline((650 - it * 80).seconds.ago, if (it % 2 == 0) "onPause" else "onResume", type = "lifecycle", level = null) }
        val levels = listOf("debug", "info", "info", "warn", "error", "verbose")
        // 120 logs at the same instant as a network burst, then 200 spread over the session.
        repeat(120) { timeline(628.seconds.ago, "burst log $it", level = levels[it % levels.size]) }
        repeat(200) { timeline((710 - it * 3.5).seconds.ago, "log $it", type = if (it % 3 == 0) "billing" else "log", level = levels[it % levels.size]) }

        repeat(6) { crash((600 - it * 90).seconds.ago, "java.lang.IllegalStateException", "boom #$it", "at io.github.openflocon.sample.Foo.bar(Foo.kt:$it)") }
        repeat(80) { analytics((690 - it * 8).seconds.ago, "screen_view", "screen" to screens[it % screens.size], tableId = "analytics") }
        repeat(30) { analytics(628.seconds.ago, "burst_event_$it", "index" to "$it", tableId = "experiments") }
    }
}
