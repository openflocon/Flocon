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

---

## Setup & Integration

### With OkHttp (Android)

=== "libs.versions.toml"

    ```toml
    [libraries]
    flocon-okhttp = { module = "io.github.openflocon:flocon-okhttp-interceptor", version.ref = "flocon" }
    flocon-okhttp-no-op = { module = "io.github.openflocon:flocon-okhttp-interceptor-no-op", version.ref = "flocon" }
    ```

=== "build.gradle.kts"

    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:LAST_VERSION")
    }
    ```

Add the interceptor to your `OkHttpClient` builder:

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(FloconOkhttpInterceptor())
    .build()
```

---

### With Ktor (Kotlin Multiplatform)

=== "libs.versions.toml"

    ```toml
    [libraries]
    flocon-ktor = { module = "io.github.openflocon:flocon-ktor-interceptor", version.ref = "flocon" }
    flocon-ktor-no-op = { module = "io.github.openflocon:flocon-ktor-interceptor-no-op", version.ref = "flocon" }
    ```

=== "build.gradle.kts"

    ```kotlin
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
            }
        }
    }
    ```

Install the plugin into your Ktor `HttpClient`:

```kotlin
val httpClient = HttpClient {
    install(FloconKtorPlugin)
}
```

!!! tip "Ktor Compatibility"
    Tested and verified with Ktor `3.x` and `2.x`.
