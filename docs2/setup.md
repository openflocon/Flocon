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

=== "BOM (align all modules)"

    The `flocon-bom` pins every Flocon module (and its `-no-op`) to the same version: you declare the version once and never mix versions between modules.

    ```toml
    [libraries]
    flocon-bom = { module = "io.github.openflocon:flocon-bom", version = "LAST_VERSION" }
    flocon = { module = "io.github.openflocon:flocon" }
    flocon-no-op = { module = "io.github.openflocon:flocon-no-op" }
    flocon-network = { module = "io.github.openflocon:flocon-network-core" }
    flocon-network-no-op = { module = "io.github.openflocon:flocon-network-core-no-op" }
    flocon-okhttp = { module = "io.github.openflocon:flocon-okhttp-interceptor" }
    flocon-okhttp-no-op = { module = "io.github.openflocon:flocon-okhttp-interceptor-no-op" }
    ```

    Import the BOM in both debug and release, then declare the modules without a version:

    ```kotlin
    dependencies {
        debugImplementation(platform(libs.flocon.bom))
        releaseImplementation(platform(libs.flocon.bom))

        debugImplementation(libs.flocon)
        releaseImplementation(libs.flocon.no.op)

        debugImplementation(libs.flocon.network)
        releaseImplementation(libs.flocon.network.no.op)
        debugImplementation(libs.flocon.okhttp)
        releaseImplementation(libs.flocon.okhttp.no.op)
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
| `FloconTimeline` | `flocon-timeline` | [Timeline](timeline.md#logs-from-your-app) |

!!! tip "Logs"
    Set `FloconLogger.enabled = true` to see what Flocon does (connection, plugins not installed, ...).

### Connect options

The `connect { }` block inside `startFlocon` configures how the SDK reaches the desktop:

```kotlin
startFlocon(this) {
    connect {
        address = "192.168.1.10"  // desktop host, optionally "host:ws" or "host:ws:http"
        websocketPort = 9023      // default 9023
        httpPort = 9024           // default 9024 (file upload)
    }
    install(FloconNetwork)
}
```

| Option | Default | Description |
| :--- | :--- | :--- |
| `address` | `localhost` | Desktop host. `host`, `host:ws` or `host:ws:http` are accepted (for example `"192.168.1.10:3030:3031"`). |
| `websocketPort` | `9023` | Port of the desktop websocket server. |
| `httpPort` | `9024` | Port of the desktop HTTP server (file upload). |

An address can carry both ports: `host`, `host:wsPort` or `host:wsPort:httpPort` (for example `192.168.1.10:10023:10024`). Each port is between `1` and `65535`; a missing port takes `websocketPort` / `httpPort`. The ports written in `address` win over `websocketPort` / `httpPort` for that address, and the file uploads use the HTTP port of the address they go through. The same format is accepted by `Flocon.connect(address)`.

The QR code of Flocon Desktop's Connect dialog contains `ip:ws:http` (always both ports) as soon as one of the desktop ports is not the default (and just the IP when both are the defaults), so scanning it is enough to connect with the right ports. The last address is saved as `host:ws:http` when one of its ports differs from `websocketPort` / `httpPort`, as the plain host otherwise; addresses saved by older versions still load.

### Custom ports

By default Flocon Desktop listens on `9023` (websocket) and `9024` (HTTP). If those ports are already used on your machine, change them in Flocon Desktop under **Settings > Server ports**:

- Ports must be between `1024` and `65535`.
- The websocket and HTTP ports must be different.
- `9042` is reserved for the [MCP](mcp.md) server and cannot be used.

The SDK must use the same ports as the desktop:

| Setup | Needs the SDK ports to be changed? |
| :--- | :--- |
| Android emulator or USB device | No. The desktop runs `adb reverse` so that the device defaults `9023` / `9024` still reach the desktop, whatever the desktop ports are. |
| Android real device over Wi-Fi | Yes. There is no `adb reverse`. |
| iOS (simulator or device), JVM, Wasm | Yes. They do not use `adb reverse`. |

!!! warning "Silent failure"
    If the SDK ports differ from the desktop settings where `adb reverse` is not used, the app simply never connects, with no error. Enable `FloconLogger.enabled = true` to see the connection attempts.

```kotlin
startFlocon(FloconContext()) {
    connect {
        address = "192.168.1.10"
        websocketPort = 3030
        httpPort = 3031
    }
}
```

On iOS with the Swift SDK (FloconSwift repository), use `flocon.config(host:webSocketPort:httpPort:)`; `host:ws:http` is also accepted in connect addresses.

#### Launch options

Flocon Desktop can also override the ports for **the current session only**. The values are never saved in the settings, and the next launch without options uses the saved ports again.

| Port | Argument | Environment variable |
| :--- | :--- | :--- |
| Websocket | `--websocket-port=<n>` (or `--websocket-port <n>`) | `FLOCON_WEBSOCKET_PORT` |
| HTTP | `--http-port=<n>` (or `--http-port <n>`) | `FLOCON_HTTP_PORT` |

For each port the priority is: argument, then environment variable, then the ports saved in **Settings > Server ports**, then the defaults `9023` / `9024`. Unknown arguments are ignored.

The ports that will be used (the overrides merged with the saved ports) follow the same rules as the settings. If they are invalid, or if a value is not a number, Flocon Desktop prints the reason on stderr and exits with code `1` without opening the window. When a port is overridden, its field is disabled in **Settings > Server ports**, and the servers, `adb reverse` and the Connect dialog use the overridden port.

```bash
# from the sources
./gradlew :composeApp:run --args="--websocket-port=9100 --http-port=9101"

# macOS, installed app
open -a Flocon --args --websocket-port=9100 --http-port=9101

# environment variables
FLOCON_WEBSOCKET_PORT=9100 FLOCON_HTTP_PORT=9101 ./gradlew :composeApp:run
```

With `open -a`, the environment variables of your shell are not forwarded to an already installed app: prefer the arguments, or run the app binary from the terminal.

---

## 3. Install the Desktop Companion

On macOS, install it with [Homebrew](https://brew.sh):

```bash
brew install --cask openflocon/tap/flocon
```

Update it later with `brew upgrade --cask flocon`.

On Windows and Linux (or on macOS without Homebrew), download the latest release for your operating system:

[:octicons-download-24: Download Flocon Desktop](https://github.com/openflocon/Flocon/releases){ .md-button .md-button--primary }

!!! tip "macOS First Launch"
    The Homebrew install takes care of this for you. With the DMG, Gatekeeper might require you to allow the app on first launch. See the [macOS Setup Guide](macos-install.md) for quick instructions or run:
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
