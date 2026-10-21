---
title: Network Inspection & Mocking
description: Inspect and mock HTTP/REST requests using OkHttp and Ktor in Flocon.
---

# 📡 Network Request Inspector & Mocking

Flocon captures **all outgoing network requests** made by your Android or Kotlin Multiplatform app — whether they are simple REST calls or multipart uploads — and displays them in a clean, real-time desktop UI.

---

## Overview

<img width="1291" height="834" alt="Network Inspector UI" src="https://github.com/user-attachments/assets/48f86fdf-f552-4f68-abe2-8d61229ccb27" style="border-radius: 8px;" />

<img width="1292" height="833" alt="Network Request Detail View" src="https://github.com/user-attachments/assets/c0f74bb4-85f3-4ced-b156-78dfae0189f3" style="border-radius: 8px; margin-top: 1rem;" />

For every captured request, Flocon provides:

- **Method & URL**: HTTP method (`GET`, `POST`, `PUT`, `DELETE`, etc.) and full endpoint URL
- **Headers & Body**: Formatted JSON/text request and response payloads with search & copy
- **Metrics**: HTTP status code, response time duration, payload size, and timestamps
- **Error Details**: Clear diagnostics on connection drops, timeouts, and server errors

---

## 🎭 HTTP Request Mocking

Flocon allows you to **mock HTTP requests on the fly** without editing your codebase or redeploying your application:

<img width="1293" height="836" alt="Network Mocking in Flocon" src="https://github.com/user-attachments/assets/3a529e3f-488e-4dba-aee1-fc6f70efcb08" style="border-radius: 8px;" />

- **Simulate Network Errors**: Test 401 Unauthorized, 404 Not Found, 429 Rate Limit, or 500 Internal Server Error handling.
- **Custom Response Payloads**: Inject mock JSON data to test unreleased backend features or rare edge cases.
- **Clone from Existing Request**: Convert any captured real request into an active mock with a single click in the desktop UI.

[:octicons-play-16: Step-by-Step Mocking Tutorial](mocking.md){ .md-button .md-button--primary style="margin-top: 1rem;" }

---

## 📉 Bad Network Simulation (Network Conditioning)

Flocon also includes a built-in network conditioner to simulate real-world conditions like subway network drops, slow 2G/3G speeds, high latency jitter, and random packet loss without modifying device OS settings.

[:octicons-pulse-16: Bad Network Simulation Guide](bad-quality.md){ .md-button style="margin-top: 0.5rem;" }

---

## Setup & Integration

The interceptors report to the **network plugin**: add `flocon-network-core` and install it.

```kotlin
startFlocon(context) {
    install(FloconNetwork)
}
```

Without `FloconNetwork`, the interceptors let the calls go through untouched.

### With OkHttp (Android & JVM)

=== "libs.versions.toml"

    ```toml
    [libraries]
    flocon-network = { module = "io.github.openflocon:flocon-network-core", version.ref = "flocon" }
    flocon-network-no-op = { module = "io.github.openflocon:flocon-network-core-no-op", version.ref = "flocon" }
    flocon-okhttp = { module = "io.github.openflocon:flocon-okhttp-interceptor", version.ref = "flocon" }
    flocon-okhttp-no-op = { module = "io.github.openflocon:flocon-okhttp-interceptor-no-op", version.ref = "flocon" }
    ```

=== "build.gradle.kts"

    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-network-core-no-op:LAST_VERSION")
        debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:LAST_VERSION")
    }
    ```

Add the interceptor to your `OkHttpClient` builder:

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(
        FloconOkhttpInterceptor(
            // optional: display these responses as images
            isImage = { it.request.url.toString().contains("cdn") },
            // optional: calls to skip
            shouldLog = { chain -> !chain.request().url.encodedPath.startsWith("/health") },
        )
    )
    .build()
```

---

### With Ktor (Kotlin Multiplatform)

=== "libs.versions.toml"

    ```toml
    [libraries]
    flocon-network = { module = "io.github.openflocon:flocon-network-core", version.ref = "flocon" }
    flocon-network-no-op = { module = "io.github.openflocon:flocon-network-core-no-op", version.ref = "flocon" }
    flocon-ktor = { module = "io.github.openflocon:flocon-ktor-interceptor", version.ref = "flocon" }
    flocon-ktor-no-op = { module = "io.github.openflocon:flocon-ktor-interceptor-no-op", version.ref = "flocon" }
    ```

=== "build.gradle.kts"

    ```kotlin
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
                implementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
            }
        }
    }
    ```

    For release builds, see [Release builds](setup.md#4-release-builds-no-op).

Install the plugin into your Ktor `HttpClient`:

```kotlin
val httpClient = HttpClient {
    install(FloconKtorPlugin) {
        // optional
        isImage = { it.responseContentType?.startsWith("image/") == true }
        shouldLog = { request -> request.url.host != "localhost" }
    }
}
```

!!! tip "Ktor Compatibility"
    Built and tested with Ktor `3.x`.
