---
title: Installation & Setup
description: Step-by-step installation and initialization guide for Flocon SDK and Desktop client.
---

# 🚀 Getting Started with Flocon

Flocon consists of two parts:
1. **Flocon SDK**: Integrated into your Android or Kotlin Multiplatform (KMP) app. Since 2.0 it is split into one module per feature, so you only ship what you use.
2. **Flocon Desktop App**: The desktop companion used to observe, inspect, and interact with the running app.

!!! info "Coming from 1.x?"
    See the [migration guide](migration.md) for the list of changes.

---

## 1. Add Dependencies

Add the core, then one module per feature you want (the full list is in the [migration guide](migration.md#1-dependencies)):

=== "libs.versions.toml (Recommended)"

    ```toml
    [versions]
    flocon = "LAST_VERSION" # Replace with latest release

    [libraries]
    flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
    flocon-no-op = { module = "io.github.openflocon:flocon-no-op", version.ref = "flocon" }

    flocon-network = { module = "io.github.openflocon:flocon-network-core", version.ref = "flocon" }
    flocon-network-no-op = { module = "io.github.openflocon:flocon-network-core-no-op", version.ref = "flocon" }
    flocon-okhttp = { module = "io.github.openflocon:flocon-okhttp-interceptor", version.ref = "flocon" }
    flocon-okhttp-no-op = { module = "io.github.openflocon:flocon-okhttp-interceptor-no-op", version.ref = "flocon" }
    flocon-analytics = { module = "io.github.openflocon:flocon-analytics", version.ref = "flocon" }
    flocon-analytics-no-op = { module = "io.github.openflocon:flocon-analytics-no-op", version.ref = "flocon" }
    # ...
    ```

    Then in your app module `build.gradle.kts`:

    ```kotlin
    dependencies {
        debugImplementation(libs.flocon)
        releaseImplementation(libs.flocon.no.op)

        debugImplementation(libs.flocon.network)
        releaseImplementation(libs.flocon.network.no.op)
        debugImplementation(libs.flocon.okhttp)
        releaseImplementation(libs.flocon.okhttp.no.op)
        debugImplementation(libs.flocon.analytics)
        releaseImplementation(libs.flocon.analytics.no.op)
    }
    ```

=== "build.gradle.kts (Android Only)"

    ```kotlin
    dependencies {
        // Active in debug builds
        debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
        debugImplementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
        debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")

        // No-op placeholders in release builds
        releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-network-core-no-op:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:LAST_VERSION")
    }
    ```

=== "build.gradle.kts (Kotlin Multiplatform)"

    ```kotlin
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("io.github.openflocon:flocon:LAST_VERSION")
                implementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
                implementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
            }
        }
    }
    ```

    See [Release builds](#4-release-builds-no-op) to use the no-op versions in your release app.

---

## 2. Initialize the SDK

Call `startFlocon` as early as possible in your application lifecycle and `install` the plugins you added:

=== "Android (`Application.kt`)"

    ```kotlin
    class MyApp : Application() {
        override fun onCreate() {
            super.onCreate()

            startFlocon(this) {
                install(FloconNetwork)
                install(FloconAnalytics)
            }
        }
    }
    ```

=== "Desktop (JVM)"

    ```kotlin
    fun main() {
        startFlocon(FloconContext(appName = "My App", packageName = "com.example.myapp")) {
            install(FloconNetwork)
            install(FloconAnalytics)
        }

        // Launch your Compose Desktop application
    }
    ```

=== "iOS / Wasm (Kotlin Multiplatform)"

    ```kotlin
    fun initFlocon() {
        startFlocon(FloconContext()) {
            install(FloconNetwork)
            install(FloconAnalytics)
        }
    }
    ```

Each plugin is then reachable with `Flocon.xxxPlugin` (for example `Flocon.analyticsPlugin.log(...)`).
A plugin that is not installed does nothing instead of crashing.

The available plugins:

| Plugin | Module | Page |
| :--- | :--- | :--- |
| `FloconNetwork` | `flocon-network-core` (+ OkHttp / Ktor / gRPC interceptor) | [Network](network.md) |
| `FloconDatabase` | `flocon-database-core` + `flocon-database-room` or `-room3` | [Databases](database.md) |
| `FloconPreferences` | `flocon-sharedprefs` (+ `flocon-datastores`) | [Preferences](sharedpref.md) |
| `FloconFiles` | `flocon-files` | [Files](files.md) |
| `FloconDashboard` | `flocon` | [Dashboards](dashboard.md) |
| `FloconTable` | `flocon-tables` | [Tables](table.md) |
| `FloconDeeplinks` | `flocon-deeplinks` | [Deeplinks](deeplink.md) |
| `FloconAnalytics` | `flocon-analytics` | [Analytics](analytics.md) |
| `FloconFeatureFlags` | `flocon-feature-flags` | [Feature flags](featureflags.md) |
| `FloconCrashReporter` | `flocon-crashreporter` | [Crash reporter](crashreporter.md) |
| `FloconWebView` | `flocon-webview` | [WebViews](webview.md) |
| `FloconDevice` | `flocon-device` | Shows the app icon and lets the desktop restart the app (Android) |

!!! tip "Logs"
    Set `FloconLogger.enabled = true` to see what Flocon does (connection, plugins not installed, ...).

---

## 3. Install the Desktop Companion

Download the latest release for your operating system:

[:octicons-download-24: Download Flocon Desktop](https://github.com/openflocon/Flocon/releases){ .md-button .md-button--primary }

!!! tip "macOS First Launch"
    On macOS, Gatekeeper might require you to allow the app on first launch. See the [macOS Setup Guide](macos-install.md) for quick instructions or run:
    ```bash
    xattr -cr /Applications/Flocon.app
    ```

---

## 4. Release builds (no-op)

Every module has a `-no-op` twin with **the same API**: in release builds nothing is started and no
connection is made to the desktop app. Use `flocon-no-op` **and** the no-op of every plugin you use.

=== "Android app module"

    Use `debugImplementation` / `releaseImplementation` as shown in [step 1](#1-add-dependencies).

=== "Shared KMP module"

    A Kotlin Multiplatform module has a single variant, so it depends on the real modules. Swap them
    for the no-ops in the **release configurations of the app**:

    ```kotlin
    // app/build.gradle.kts
    val floconNoOps = mapOf(
        "io.github.openflocon:flocon" to "io.github.openflocon:flocon-no-op:LAST_VERSION",
        "io.github.openflocon:flocon-network-core" to "io.github.openflocon:flocon-network-core-no-op:LAST_VERSION",
        "io.github.openflocon:flocon-ktor-interceptor" to "io.github.openflocon:flocon-ktor-interceptor-no-op:LAST_VERSION",
        // ... one line per Flocon module you use
    )

    configurations.configureEach {
        if (name.startsWith("release")) {
            resolutionStrategy.dependencySubstitution {
                floconNoOps.forEach { (module, noOp) ->
                    substitute(module(module)).using(module(noOp))
                }
            }
        }
    }
    ```

    The no-ops are binary compatible: code compiled against the real modules runs on the no-ops.

---

## 5. Requirements & Prerequisites

=== "Android Requirements"
    - An Android device or emulator (API 23+) with **USB Debugging** enabled.
    - Android SDK Tools with `adb` (Android Debug Bridge) installed and accessible in your system `PATH`.
    - Nothing to configure for the connection: it is encrypted (TLS), so your app does not need to allow cleartext traffic.

=== "Multiplatform Requirements"
    - A recent Kotlin version (the SDK is built with Kotlin 2.4).
    - JVM 17+ for running the desktop application.
    - Aligned version between the mobile library and the desktop application.
