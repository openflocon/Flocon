package com.flocon.data.remote.mock.scenarios

import com.flocon.data.remote.mock.ago
import com.flocon.data.remote.mock.scenario
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

private const val STRESS_NETWORK_CALLS = 5_000

internal val stressNetworkScenario = scenario(
    "stress-network",
    description = "Network list scroll stress: 5 000 calls with ~20 KB JSON bodies and headers, every method, 2xx/4xx/5xx, exceptions, images and pending calls",
) {
    android("Pixel 8", app = SAMPLE_APP, packageName = SAMPLE_PACKAGE, appStartedAt = 30.minutes.ago) {
        val random = Random(7)
        val hosts = listOf("api.flocon-shop.com", "cdn.flocon-shop.com", "auth.flocon-shop.com", "ads.tracker.io")
        val methods = listOf("GET", "GET", "GET", "POST", "PUT", "PATCH", "DELETE")
        // the same big bodies for every call: the point is their weight in the database, not their content
        val requestBody = """{"items":[${(1..150).joinToString(",") { """{"id":$it,"name":"product $it","qty":${it % 9}}""" }}]}"""
        val responseBody = """{"products":[${(1..300).joinToString(",") { """{"id":$it,"name":"product $it","price":${it * 3}.99,"description":"a long enough description for product $it"}""" }}]}"""
        val sessionMs = 29.minutes.inWholeMilliseconds

        repeat(STRESS_NETWORK_CALLS) { n ->
            val at = (sessionMs - n * sessionMs / STRESS_NETWORK_CALLS).milliseconds.ago
            val roll = random.nextInt(100)
            val method = methods[random.nextInt(methods.size)]
            val host = hosts[random.nextInt(hosts.size)]
            when {
                roll < 2 -> network(at, method, "https://$host/v1/checkout/$n", error = "java.net.SocketTimeoutException: timeout", duration = 10_000.milliseconds)

                roll < 6 -> network(at, "GET", "https://images.unsplash.com/photo-$n?w=400&h=400&fm=jpg", duration = (40 + random.nextInt(300)).milliseconds) {
                    responseHeader("content-type", "image/jpeg")
                }

                // the last calls are still running
                n >= STRESS_NETWORK_CALLS - 5 -> network(at, method, "https://$host/v1/orders/$n/tracking", pending = true)

                else -> {
                    val status = when {
                        roll < 82 -> 200
                        roll < 88 -> 201
                        roll < 94 -> 404
                        else -> 500
                    }
                    network(at, method, "https://$host/v1/resource/$n?page=${n % 20}&size=50", status = status, duration = (20 + random.nextInt(900)).milliseconds) {
                        requestHeader("authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.$n")
                        requestHeader("content-type", "application/json")
                        if (method != "GET") requestBody(requestBody)
                        responseHeader("content-type", "application/json")
                        responseHeader("cache-control", "no-cache")
                        responseBody(responseBody)
                    }
                }
            }
        }
    }
}
