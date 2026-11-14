---
title: Kotlin SDK (Android & KMP)
description: Add the Flocon Kotlin SDK to an Android or Kotlin Multiplatform app, start it, and keep it out of release builds.
---

# Kotlin SDK (Android & KMP)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.openflocon/flocon)

Replace `LAST_VERSION` with the version shown on the badge.

Since 2.0 the SDK is split into **one module per feature**: add the core, then only the modules you use. Add them in every variant, plus `flocon-no-op` in release: it turns Flocon off, see [Release builds](#release-builds).

Requirements: Android API 23+, or a Kotlin Multiplatform project (Android, JVM, iOS, Wasm).

!!! tip "Let your AI agent do it"
    The [Flocon plugin](install-with-ai.md) adds the dependencies, starts Flocon and keeps it out of release builds for you.

## 1. Add the dependencies

=== "Version catalog"

    ```toml
    [versions]
    flocon = "LAST_VERSION"

    [libraries]
    flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
    flocon-no-op = { module = "io.github.openflocon:flocon-no-op", version.ref = "flocon" }

    flocon-okhttp = { module = "io.github.openflocon:flocon-okhttp-interceptor", version.ref = "flocon" }
    # ... one entry per module you use, see the table below
    ```

    ```kotlin
    // app/build.gradle.kts
    dependencies {
        implementation(libs.flocon)
        implementation(libs.flocon.okhttp)

        // turns Flocon off in release builds
        releaseImplementation(libs.flocon.no.op)
    }
    ```

=== "BOM"

    The `flocon-bom` pins every Flocon module (and `flocon-no-op`) to the same version: declare the version once, then leave the modules without a version.

    ```toml
    [libraries]
    flocon-bom = { module = "io.github.openflocon:flocon-bom", version = "LAST_VERSION" }
    flocon = { module = "io.github.openflocon:flocon" }
    flocon-no-op = { module = "io.github.openflocon:flocon-no-op" }
    ```

    ```kotlin
    dependencies {
        implementation(platform(libs.flocon.bom))
        releaseImplementation(platform(libs.flocon.bom))

        implementation(libs.flocon)

        releaseImplementation(libs.flocon.no.op)
    }
    ```

=== "Plain Gradle (Android)"

    ```kotlin
    dependencies {
        implementation("io.github.openflocon:flocon:LAST_VERSION")
        implementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")

        // turns Flocon off in release builds
        releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
    }
    ```

=== "Kotlin Multiplatform"

    ```kotlin
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("io.github.openflocon:flocon:LAST_VERSION")
                implementation("io.github.openflocon:flocon-ktor-interceptor:LAST_VERSION")
            }
        }
    }
    ```

    A multiplatform module has a single variant: add `flocon-no-op` to the release build of the app, see [Release builds](#release-builds).

**All modules** (Maven group `io.github.openflocon`; only `flocon-accessibility` and `flocon-qr-scanner` keep a `-no-op` artifact, see [Release builds](#release-builds)):

| Feature | Artifact | Platforms |
| :--- | :--- | :--- |
| Core: [Dashboards](dashboard.md), [Network](network.md) (mocks, bad network, WebSocket logging), [Preferences](sharedpref.md), [Files](files.md) (no file browsing on Wasm), [Deeplinks](deeplink.md), [Analytics](analytics.md), [Feature flags](featureflags.md), [Timeline](timeline.md), [Crash reporter](crashreporter.md), [Tables](table.md) | `flocon` | Android, JVM, iOS, Wasm |
| OkHttp interceptor (+ WebSockets) | `flocon-okhttp-interceptor` | Android, JVM |
| Ktor plugin | `flocon-ktor-interceptor` | Android, JVM, iOS, Wasm |
| [gRPC](grpc.md) | `flocon-grpc-interceptor`, `flocon-grpc-interceptor-lite`, `flocon-grpc-interceptor-base` | Android |
| [Databases](database.md) | `flocon-database-core` + `flocon-database-room` (Room 2) or `flocon-database-room3` (Room 3) or `flocon-database-sqldelight` | Android, JVM, iOS (+ Wasm for Room 3) |
| [DataStore](sharedpref.md) | `flocon-datastores` | Android, JVM, iOS |
| [Accessibility](accessibility.md) | `flocon-accessibility` | Android, JVM, iOS, Wasm (automatic capture on Android and iOS) |
| [WebView](webview.md) | `flocon-webview` | Android, JVM, iOS, Wasm (automatic `attach` on Android) |
| QR code scanner | `flocon-qr-scanner` | Android |
| BOM | `flocon-bom` | all |

## 2. Start Flocon

Start the SDK as early as possible, then `install` the features you use. A feature that is not installed does nothing instead of crashing, so calls to it can stay in your code.

=== "Android"

    ```kotlin
    class MyApp : Application() {
        override fun onCreate() {
            super.onCreate()

            startFlocon(FloconContext(this)) {
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
        // launch your application
    }
    ```

=== "iOS / Wasm (KMP)"

    ```kotlin
    fun initFlocon() {
        startFlocon(FloconContext()) {
            install(FloconNetwork)
            install(FloconAnalytics)
        }
    }
    ```

Each plugin is then reachable with `Flocon.xxxPlugin` (for example `Flocon.analyticsPlugin.log(...)`). Set `FloconLogger.enabled = true` to see what Flocon does (connection, plugins not installed...).

| Plugin to `install` | Module | Page |
| :--- | :--- | :--- |
| `FloconNetwork` | `flocon` (+ OkHttp / Ktor / gRPC interceptor) | [Network](network.md) |
| `FloconDatabase` | `flocon-database-core` + a Room / SQLDelight provider | [Databases](database.md) |
| `FloconPreferences` | `flocon` (+ `flocon-datastores`) | [Preferences](sharedpref.md) |
| `FloconFiles` | `flocon` | [Files](files.md) |
| `FloconDashboard` | `flocon` | [Dashboards](dashboard.md) |
| `FloconTable` | `flocon` | [Tables](table.md) |
| `FloconDeeplinks` | `flocon` | [Deeplinks](deeplink.md) |
| `FloconAnalytics` | `flocon` | [Analytics](analytics.md) |
| `FloconFeatureFlags` | `flocon` | [Feature flags](featureflags.md) |
| `FloconCrashReporter` | `flocon` | [Crash reporter](crashreporter.md) |
| `FloconWebView` | `flocon-webview` | [WebView](webview.md) |
| `FloconTimeline` | `flocon` | [Timeline](timeline.md) |
| `FloconAccessibility` | `flocon-accessibility` | [Accessibility](accessibility.md) |

## Release builds

Flocon is a debugging tool: once connected, the desktop can read your network calls, edit your preferences, delete files and run SQL on your databases. Keep it out of release builds.

Keep the Flocon modules in `implementation` and add `flocon-no-op` to the release build only, as in [step 1](#1-add-the-dependencies):

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("io.github.openflocon:flocon:LAST_VERSION")

    releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
}
```

`flocon-no-op` turns Flocon off in the build it is added to: `startFlocon` starts nothing, no connection is made, every `Flocon.xxxPlugin` is a no-op and the interceptors pass the calls through. Your code stays the same in every variant. Add it to every build type that ships (e.g. `internalImplementation(...)` for an internal distribution build type).

- **Minified release (R8 on)**: `flocon-no-op` brings an R8 rule that removes the Flocon code (connection, plugins, interceptors) from the app. Only small API classes stay, so your calls still compile and run.
- **Not minified**: Flocon is still off at runtime, but its code and its dependencies stay in the binary.

**Accessibility and QR code scanner**: these two modules add Android manifest entries (the `WRITE_SECURE_SETTINGS` permission, the ML Kit code scanner) that `flocon-no-op` can't remove, so they keep their `-no-op` artifact:

```kotlin
dependencies {
    debugImplementation("io.github.openflocon:flocon-accessibility:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-accessibility-no-op:LAST_VERSION")

    debugImplementation("io.github.openflocon:flocon-qr-scanner:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-qr-scanner-no-op:LAST_VERSION")
}
```

**Kotlin Multiplatform**: a shared module has a single variant, so it depends on the real modules with `implementation`. In the Android app, add `releaseImplementation` of `flocon-no-op` as above. If the shared module uses accessibility or the QR code scanner, swap only these two in the release configurations of the app:

```kotlin
// app/build.gradle.kts
configurations.configureEach {
    if (name.startsWith("release")) {
        resolutionStrategy.dependencySubstitution {
            substitute(module("io.github.openflocon:flocon-accessibility"))
                .using(module("io.github.openflocon:flocon-accessibility-no-op:LAST_VERSION"))
            substitute(module("io.github.openflocon:flocon-qr-scanner"))
                .using(module("io.github.openflocon:flocon-qr-scanner-no-op:LAST_VERSION"))
        }
    }
}
```

The iOS framework, desktop (JVM) and Wasm targets have no release configuration: add `flocon-no-op` to the build that ships (e.g. a release-only Gradle module or flavor), or call `startFlocon` only behind your app's own debug check.
## Next: connect

Run the app with Flocon Desktop open: on an Android emulator or a USB device it connects by itself. See [Connect](connect.md).
