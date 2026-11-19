---
title: Kotlin SDK (Android & KMP)
description: Add the Flocon Kotlin SDK to an Android or Kotlin Multiplatform app, start it, and keep it out of release builds.
---

# Kotlin SDK (Android & KMP)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.openflocon/flocon)

Replace `LAST_VERSION` with the version shown on the badge.

Since 2.0 the SDK is split into **one module per feature**. From **2.0.1**, most of them are part of the core (`flocon`): add the core, then only the modules you still need. Add them in every variant, plus `flocon-no-op` in release: it turns Flocon off, see [Release builds](#release-builds).

!!! info "Version 2.0.0?"
    Before 2.0.1, each feature was its own module with its own `-no-op` twin, added with `debugImplementation` / `releaseImplementation`. Every section below has a **2.0.0** tab for it, and the [migration guide](migration.md#after-200-one-flocon-no-op-for-release-builds) lists what changed.

Requirements: Android API 23+, or a Kotlin Multiplatform project (Android, JVM, iOS, Wasm).

!!! tip "Let your AI agent do it"
    The [Flocon plugin](install-with-ai.md) adds the dependencies, starts Flocon and keeps it out of release builds for you.

## 1. Add the dependencies

=== "2.0.1 and later"

    === "Version catalog"

        ```toml
        [versions]
        flocon = "LAST_VERSION"

        [libraries]
        flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
        flocon-no-op = { module = "io.github.openflocon:flocon-no-op", version.ref = "flocon" }

        flocon-ktor = { module = "io.github.openflocon:flocon-ktor-interceptor", version.ref = "flocon" }
        # ... one entry per module you use, see the table below
        ```

        ```kotlin
        // app/build.gradle.kts
        dependencies {
            implementation(libs.flocon)

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
    | Core: [Dashboards](dashboard.md), [Network](network.md) (mocks, bad network, WebSocket logging, OkHttp interceptor and WebSockets on Android and JVM), [Databases](database.md) plugin, [Preferences](sharedpref.md), [Files](files.md) (no file browsing on Wasm), [Deeplinks](deeplink.md), [Analytics](analytics.md), [Feature flags](featureflags.md), [Timeline](timeline.md), [Crash reporter](crashreporter.md), [Tables](table.md), [WebView](webview.md) (automatic `attach` on Android) | `flocon` | Android, JVM, iOS, Wasm |
    | Ktor plugin | `flocon-ktor-interceptor` | Android, JVM, iOS, Wasm |
    | [gRPC](grpc.md) | `flocon-grpc-interceptor`, `flocon-grpc-interceptor-lite`, `flocon-grpc-interceptor-base` | Android |
    | [Databases](database.md) providers | `flocon-database-room` (Room 2), `flocon-database-room3` (Room 3) or `flocon-database-sqldelight` | Android, JVM, iOS (+ Wasm for Room 3) |
    | [DataStore](sharedpref.md) | `flocon-datastores` | Android, JVM, iOS |
    | [Accessibility](accessibility.md) | `flocon-accessibility` | Android, JVM, iOS, Wasm (automatic capture on Android and iOS) |
    | QR code scanner | `flocon-qr-scanner` | Android |
    | BOM | `flocon-bom` | all |

=== "2.0.0"

    === "Version catalog"

        ```toml
        [versions]
        flocon = "LAST_VERSION"

        [libraries]
        flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
        flocon-no-op = { module = "io.github.openflocon:flocon-no-op", version.ref = "flocon" }

        flocon-network = { module = "io.github.openflocon:flocon-network-core", version.ref = "flocon" }
        flocon-network-no-op = { module = "io.github.openflocon:flocon-network-core-no-op", version.ref = "flocon" }
        flocon-okhttp = { module = "io.github.openflocon:flocon-okhttp-interceptor", version.ref = "flocon" }
        flocon-okhttp-no-op = { module = "io.github.openflocon:flocon-okhttp-interceptor-no-op", version.ref = "flocon" }
        flocon-analytics = { module = "io.github.openflocon:flocon-analytics", version.ref = "flocon" }
        flocon-analytics-no-op = { module = "io.github.openflocon:flocon-analytics-no-op", version.ref = "flocon" }
        # ... one pair per module you use, see the table below
        ```

        ```kotlin
        // app/build.gradle.kts
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

    === "BOM"

        The `flocon-bom` pins every Flocon module (and its `-no-op`) to the same version: declare the version once, then leave the modules without a version.

        ```toml
        [libraries]
        flocon-bom = { module = "io.github.openflocon:flocon-bom", version = "LAST_VERSION" }
        flocon = { module = "io.github.openflocon:flocon" }
        flocon-no-op = { module = "io.github.openflocon:flocon-no-op" }
        flocon-network = { module = "io.github.openflocon:flocon-network-core" }
        flocon-network-no-op = { module = "io.github.openflocon:flocon-network-core-no-op" }
        ```

        ```kotlin
        dependencies {
            debugImplementation(platform(libs.flocon.bom))
            releaseImplementation(platform(libs.flocon.bom))

            debugImplementation(libs.flocon)
            releaseImplementation(libs.flocon.no.op)

            debugImplementation(libs.flocon.network)
            releaseImplementation(libs.flocon.network.no.op)
        }
        ```

    === "Plain Gradle (Android)"

        ```kotlin
        dependencies {
            debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
            debugImplementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
            debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")

            releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
            releaseImplementation("io.github.openflocon:flocon-network-core-no-op:LAST_VERSION")
            releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:LAST_VERSION")
        }
        ```

    === "Kotlin Multiplatform"

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

        A multiplatform module has a single variant: swap the real modules for the no-ops in the release configurations of the app, see [Release builds](#release-builds).

    **All modules** (Maven group `io.github.openflocon`; add `-no-op` to the artifact name for the release twin):

    | Feature | Artifact | Platforms |
    | :--- | :--- | :--- |
    | Core, [Dashboards](dashboard.md) | `flocon` | Android, JVM, iOS, Wasm |
    | [Network](network.md) (mocks, bad network, WebSocket logging) | `flocon-network-core` | Android, JVM, iOS, Wasm |
    | OkHttp interceptor (+ WebSockets) | `flocon-okhttp-interceptor` | Android, JVM |
    | Ktor plugin | `flocon-ktor-interceptor` | Android, JVM, iOS, Wasm |
    | [gRPC](grpc.md) | `flocon-grpc-interceptor`, `flocon-grpc-interceptor-lite`, `flocon-grpc-interceptor-base` | Android |
    | [Databases](database.md) | `flocon-database-core` + `flocon-database-room` (Room 2) or `flocon-database-room3` (Room 3) or `flocon-database-sqldelight` | Android, JVM, iOS (+ Wasm for Room 3) |
    | [Preferences](sharedpref.md) | `flocon-sharedprefs` | Android, JVM, iOS, Wasm |
    | DataStore | `flocon-datastores` | Android, JVM, iOS |
    | [Files](files.md) | `flocon-files` | Android, JVM, iOS, Wasm (no file browsing on Wasm) |
    | [Tables](table.md) | `flocon-tables` | Android, JVM, iOS, Wasm |
    | [Deeplinks](deeplink.md) | `flocon-deeplinks` | Android, JVM, iOS, Wasm |
    | [Analytics](analytics.md) | `flocon-analytics` | Android, JVM, iOS, Wasm |
    | [Feature flags](featureflags.md) | `flocon-feature-flags` | Android, JVM, iOS, Wasm |
    | [Timeline](timeline.md) | `flocon-timeline` | Android, JVM, iOS, Wasm |
    | [Crash reporter](crashreporter.md) | `flocon-crashreporter` | Android, JVM, iOS, Wasm |
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

=== "2.0.1 and later"

    | Plugin to `install` | Module | Page |
    | :--- | :--- | :--- |
    | `FloconNetwork` | `flocon` (OkHttp interceptor included; + `flocon-ktor-interceptor` / gRPC interceptor) | [Network](network.md) |
    | `FloconDatabase` | `flocon` + a Room / SQLDelight provider | [Databases](database.md) |
    | `FloconPreferences` | `flocon` (+ `flocon-datastores`) | [Preferences](sharedpref.md) |
    | `FloconFiles` | `flocon` | [Files](files.md) |
    | `FloconDashboard` | `flocon` | [Dashboards](dashboard.md) |
    | `FloconTable` | `flocon` | [Tables](table.md) |
    | `FloconDeeplinks` | `flocon` | [Deeplinks](deeplink.md) |
    | `FloconAnalytics` | `flocon` | [Analytics](analytics.md) |
    | `FloconFeatureFlags` | `flocon` | [Feature flags](featureflags.md) |
    | `FloconCrashReporter` | `flocon` | [Crash reporter](crashreporter.md) |
    | `FloconWebView` | `flocon` | [WebView](webview.md) |
    | `FloconTimeline` | `flocon` | [Timeline](timeline.md) |
    | `FloconAccessibility` | `flocon-accessibility` | [Accessibility](accessibility.md) |

=== "2.0.0"

    | Plugin to `install` | Module | Page |
    | :--- | :--- | :--- |
    | `FloconNetwork` | `flocon-network-core` (+ OkHttp / Ktor / gRPC interceptor) | [Network](network.md) |
    | `FloconDatabase` | `flocon-database-core` + a Room / SQLDelight provider | [Databases](database.md) |
    | `FloconPreferences` | `flocon-sharedprefs` (+ `flocon-datastores`) | [Preferences](sharedpref.md) |
    | `FloconFiles` | `flocon-files` | [Files](files.md) |
    | `FloconDashboard` | `flocon` | [Dashboards](dashboard.md) |
    | `FloconTable` | `flocon-tables` | [Tables](table.md) |
    | `FloconDeeplinks` | `flocon-deeplinks` | [Deeplinks](deeplink.md) |
    | `FloconAnalytics` | `flocon-analytics` | [Analytics](analytics.md) |
    | `FloconFeatureFlags` | `flocon-feature-flags` | [Feature flags](featureflags.md) |
    | `FloconCrashReporter` | `flocon-crashreporter` | [Crash reporter](crashreporter.md) |
    | `FloconWebView` | `flocon-webview` | [WebView](webview.md) |
    | `FloconTimeline` | `flocon-timeline` | [Timeline](timeline.md) |
    | `FloconAccessibility` | `flocon-accessibility` | [Accessibility](accessibility.md) |

## Release builds

Flocon is a debugging tool: once connected, the desktop can read your network calls, edit your preferences, delete files and run SQL on your databases. Keep it out of release builds.

=== "2.0.1 and later"

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

=== "2.0.0"

    Use `debugImplementation` with the `-no-op` twin in `releaseImplementation`, as in [step 1](#1-add-the-dependencies). The no-op has the same API: nothing is started and no connection is made. Use `flocon-no-op` **and** the no-op of every module you use. In a shared Kotlin Multiplatform module, which has a single variant, swap the modules in the release configurations of the app:

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

    The no-ops are binary compatible: code compiled against the real modules runs on them.

## Next: connect

Run the app with Flocon Desktop open: on an Android emulator or a USB device it connects by itself. See [Connect](connect.md).
