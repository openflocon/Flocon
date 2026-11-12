---
title: Connect your app
description: Install Flocon Desktop, add the Kotlin, Swift or Flutter SDK to your app, start it and connect it to the desktop.
---

# 🚀 Connect your app

Flocon has two parts:

1. **Flocon Desktop**: the app on your computer where you inspect and control your running app.
2. **A Flocon SDK**, added to your app in debug builds: **Kotlin** (Android and Kotlin Multiplatform), **Swift** (iOS and macOS) or **Flutter** (Android, iOS and macOS).

The four steps below take a few minutes. The tabs **Kotlin / Swift / Flutter** are synchronized across the whole documentation: pick yours once.

!!! info "Coming from 1.x?"
    Only the Kotlin SDK existed before 2.0. See the [migration guide](migration.md).

---

## 1. Install the desktop app

=== "macOS"

    ```bash
    brew install --cask openflocon/tap/flocon
    ```

    Update it later with `brew upgrade --cask flocon`. Without Homebrew, download the DMG from the [releases page](https://github.com/openflocon/Flocon/releases) and see the [macOS guide](macos-install.md) for the Gatekeeper prompt.

=== "Linux"

    Install the `.deb` or `.rpm` of the [releases page](https://github.com/openflocon/Flocon/releases), or use Homebrew (`brew install openflocon/tap/flocon`). See the [Linux guide](linux-install.md), including `adb`.

=== "Windows"

    Download the `.msi` installer from the [releases page](https://github.com/openflocon/Flocon/releases).

[:octicons-download-24: Download Flocon Desktop](https://github.com/openflocon/Flocon/releases){ .md-button .md-button--primary }

For Android you also need `adb` in your `PATH` (it comes with the Android SDK platform-tools): the desktop uses it to reach emulators and USB devices.

!!! warning "Keep the SDK and the desktop up to date together"
    The SDK connects with `wss://` / `https://` and the desktop no longer serves the plain ports, so an old SDK cannot connect to a new desktop, and the other way around. Flocon Desktop shows an **SDK update** badge in the top bar when the connected app runs an older SDK than the latest release.

---

## 2. Add the SDK

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.openflocon/flocon) [![FloconSwift release](https://img.shields.io/github/v/release/openflocon/FloconSwift?label=FloconSwift)](https://github.com/openflocon/FloconSwift/releases) [![pub package](https://img.shields.io/pub/v/flocon.svg)](https://pub.dev/packages/flocon)

In the snippets of this site, replace `LAST_VERSION` with the version shown on the badge of your SDK.

=== "Kotlin"

    Since 2.0 the SDK is split into **one module per feature**: add the core, then only the modules you use. Every module has a `-no-op` twin with the same API, so release builds ship nothing.

    Requirements: Android API 23+, or a Kotlin Multiplatform project (Android, JVM, iOS, Wasm).

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
    | [Databases](database.md) | `flocon-database-core` + `flocon-database-room` (Room 2) or `flocon-database-room3` (Room 3) or `flocon-database-sqldelight` | Android, JVM, iOS (+ Wasm for Room 3 and SQLDelight) |
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

=== "Swift"

    Requirements: iOS 15+ or macOS 12+, Xcode 16+ (Swift 6).

    In Xcode, choose **File > Add Package Dependencies...**, paste `https://github.com/openflocon/FloconSwift.git`, keep **Up to Next Major Version** from the latest release, and add the `Flocon` library to your app target.

    With a `Package.swift`:

    ```swift
    dependencies: [
        .package(url: "https://github.com/openflocon/FloconSwift.git", from: "LAST_VERSION"),
    ],
    targets: [
        .target(name: "MyApp", dependencies: [
            .product(name: "Flocon", package: "FloconSwift"),
        ]),
    ]
    ```

    **gRPC (optional)**: the [`FloconSwiftGrpc`](https://github.com/openflocon/FloconSwiftGrpc) package adds the interceptor for grpc-swift 1.26+ (1.x). Add `https://github.com/openflocon/FloconSwiftGrpc.git` (from the latest release) and the `FloconGrpc` library; it already includes `Flocon`. See [gRPC](grpc.md).

    **Info.plist**: add `NSLocalNetworkUsageDescription` so iOS lets the app reach your computer on the local network (a physical device shows a permission prompt the first time).

    ```xml
    <key>NSLocalNetworkUsageDescription</key>
    <string>Flocon connects to the desktop debugger on your computer.</string>
    ```

    No App Transport Security exception is needed: the SDK uses `wss://` and `https://` with the self-signed certificate of the desktop (encrypted, not authenticated).

=== "Flutter"

    Requirements: Dart 3.10 / Flutter 3.38.1 or later. Android, iOS and macOS are supported.

    ```bash
    flutter pub add flocon
    ```

    Then add only the packages you need:

    | Package | Adds | Depends on |
    | :--- | :--- | :--- |
    | `flocon` | connection, [Network](network.md) (WebSocket events, mocks, bad network), [Analytics](analytics.md), [Feature flags](featureflags.md), [Tables](table.md), [Dashboards](dashboard.md), [Deeplinks](deeplink.md), [Timeline](timeline.md) | `path_provider` |
    | `flocon_dio` | HTTP calls of [Dio](https://pub.dev/packages/dio): `dio.withFlocon()` | `dio` |
    | `flocon_http` | HTTP calls of [`package:http`](https://pub.dev/packages/http): `client.withFlocon()` | `http` |
    | `flocon_database` | [Databases](database.md) (SQLite) | `sqflite` |
    | `flocon_shared_preferences` | [Preferences](sharedpref.md) | `shared_preferences` |
    | `flocon_files` | [Files](files.md) | `path_provider` |
    | `flocon_crash_reporter` | [Crash reporter](crashreporter.md) | `path_provider` |
    | `flocon_qr_scanner` | `FloconQrScannerView` to connect by scanning a QR code | `mobile_scanner` |

    ```bash
    flutter pub add flocon_dio flocon_database flocon_shared_preferences
    ```

    **Platform setup** (see `sample/` in the [repository](https://github.com/openflocon/FloconFlutter) for a working configuration):

    - **Android**: the `INTERNET` permission in `AndroidManifest.xml`.
    - **iOS**: `NSLocalNetworkUsageDescription` in `Info.plist`, so the app can reach your computer on the local network.
    - **macOS**: the `com.apple.security.network.client` entitlement.

    No cleartext or App Transport Security exception is needed: the SDK uses `wss://` and `https://` with the self-signed certificate of the desktop (encrypted, not authenticated).

---

## 3. Start Flocon

Start the SDK as early as possible, then `install` the features you use. A feature that is not installed does nothing instead of crashing, so calls to it can stay in your code.

=== "Kotlin"

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

=== "Swift"

    ```swift
    import Flocon

    @main
    struct MyApp: App {
        init() {
            #if DEBUG
            startFlocon { flocon in
                flocon.install(.network)
                flocon.install(.analytics)
                flocon.install(.database) { $0.register(displayName: "App", url: databaseURL) }
            }
            #endif
        }

        var body: some Scene {
            WindowGroup { ContentView() }
        }
    }
    ```

    The plugins are `.network` (also intercepts `URLSession.shared`), `.analytics`, `.featureFlags`, `.tables`, `.preferences`, `.files`, `.database`, `.dashboard`, `.deeplinks`, `.timeline` and `.crashReporter`. Those with options take a closure. `Flocon.initialize()` is the short form that installs all of them. `Flocon.isLoggingEnabled = true`, set before starting, prints what the SDK does with the `[Flocon]` prefix.

=== "Flutter"

    ```dart
    import 'package:flocon/flocon.dart';
    import 'package:flocon_database/flocon_database.dart';
    import 'package:flocon_shared_preferences/flocon_shared_preferences.dart';
    import 'package:flutter/foundation.dart';
    import 'package:shared_preferences/shared_preferences.dart';

    Future<void> main() async {
      WidgetsFlutterBinding.ensureInitialized();
      if (kDebugMode) await initializeFlocon(); // debug builds only
      runApp(const MyApp());
    }

    Future<void> initializeFlocon() async {
      final prefs = await SharedPreferences.getInstance();
      await startFlocon((flocon) => flocon
        ..install(FloconNetwork())
        ..install(FloconDatabase())
        ..install(FloconSharedPreferences(prefs)));
    }
    ```

    The plugins of the `flocon` package (network, analytics, tables, timeline, feature flags, deeplinks, dashboard) are always available through `Flocon.analytics`, `Flocon.timeline`, ...; install `FloconNetwork` only to give it options. The plugins of the other packages must be installed: `FloconDatabase`, `FloconSharedPreferences`, `FloconFiles`, `FloconCrashReporter`. Calling `startFlocon` more than once has no effect. `Flocon.isLoggingEnabled = true`, set before starting, prints what the SDK does.

---

## 4. Connect

Start Flocon Desktop, then run your app. The SDK connects in the background and retries every 3 seconds. Until a desktop is reachable, what the app logs is kept in memory (5000 messages by default, the oldest are dropped) and sent in order once connected.

| Where the app runs | What to do |
| :--- | :--- |
| Android emulator (Kotlin) | Nothing. The SDK uses `localhost` and the desktop runs `adb reverse` for you. |
| Android device over USB (Kotlin) | Nothing (`adb reverse` too). USB debugging must be on and `adb devices` must list it. |
| iOS Simulator, macOS, JVM desktop app | Nothing. `localhost` is the default. |
| Flutter on an Android emulator | Use the host `10.0.2.2` (see the Flutter tab below). |
| Physical device over Wi-Fi (Android or iOS) | Give the SDK the IP address of your computer, or scan the QR code of the desktop. Both must be on the same network. |

=== "Kotlin"

    On a real device over Wi-Fi, give the address at start, or later (from a debug menu, for example) with `Flocon.connect`:

    ```kotlin
    startFlocon(FloconContext(this)) {
        connect {
            address = "192.168.1.10"  // host, host:ws or host:ws:http
        }
        install(FloconNetwork)
    }

    // or, once the address is known
    val result = Flocon.connect("192.168.1.10")   // suspend, returns a FloconConnectResult
    Flocon.disconnect()                            // suspend, forgets the address
    ```

    `connect { }` also has `bufferCapacity` (messages kept while not connected) and `rememberLastAddress` (the last connected address is tried again at the next launch and wins over `address`, `true` by default).

    **Scan the QR code** (Android): add `flocon-qr-scanner` (and its `-no-op` in release) and call:

    ```kotlin
    import io.github.openflocon.flocon.qrscanner.scanQrCodeAndConnect

    val result = Flocon.scanQrCodeAndConnect(context) // suspend, null when the user closes the scanner
    ```

    It opens the Google code scanner, so it needs no camera permission but requires Google Play Services; without it, the result is `FloconConnectResult.Failed` and you connect with the address instead.

    Follow the connection with `Flocon.connectionState`, a `StateFlow<FloconConnectionState>` (`Idle`, `Buffering`, `Connecting`, `Connected`, `Failed(reason)`), imported from `io.github.openflocon.flocon.connectionState`.

=== "Swift"

    On a physical device, use the IP address of your Mac:

    ```swift
    Flocon.initialize(config: FloconConfig(host: "192.168.1.20"))

    // or with the builder
    startFlocon { flocon in
        flocon.config(host: "192.168.1.20")
        flocon.install(.network)
    }

    // or later, once the address is known
    let result = await Flocon.connect(address: "192.168.1.20") // FloconConnectResult
    await Flocon.disconnect()
    ```

    **Scan the QR code**: `FloconQRScannerView` (AVFoundation, no extra dependency) reads the QR code of the desktop and calls `Flocon.connect(address:)` for you. Your app needs `NSCameraUsageDescription`; the simulator has no camera, use `Flocon.connect(address:)` there.

    ```swift
    .sheet(isPresented: $isScanning) {
        FloconQRScannerView { result in
            if result == .connected { isScanning = false }
        }
    }
    ```

    Follow the connection with `Flocon.connectionState` or the `Flocon.connectionStates` stream (`.idle`, `.buffering`, `.connecting`, `.connected`, `.failed(reason)`).

=== "Flutter"

    The default host is `localhost`. On an Android emulator use `10.0.2.2`, and on a physical device the IP address of your computer:

    ```dart
    await startFlocon((flocon) => flocon
      ..connect(host: Platform.isAndroid ? '10.0.2.2' : 'localhost')
      ..install(FloconNetwork()));

    // or later, once the address is known
    final result = await Flocon.connect('192.168.1.20'); // FloconConnectResult
    await Flocon.disconnect();
    ```

    **Scan the QR code** with the `flocon_qr_scanner` package (Android, iOS, macOS):

    ```dart
    FloconQrScannerView(onResult: (result) {
      if (result is FloconConnectResultConnected) Navigator.pop(context);
    })
    ```

    It needs the camera permission: `NSCameraUsageDescription` (iOS, macOS), `CAMERA` (Android) and `com.apple.security.device.camera` (macOS entitlements). On Android, `mobile_scanner` needs `minSdk` 23, `compileSdk` 36 and the Android Gradle plugin 8.9.1 or higher.

    Follow the connection with `Flocon.connectionState` or the `Flocon.connectionStates` stream (idle, buffering, connecting, connected, failed).

### The address format

An address is the IP address or host of your computer, with optional ports: `host`, `host:ws` or `host:ws:http`, for example `192.168.1.20:10023:10024`. No scheme, no path. Ports go from `1` to `65535`; an omitted port is the configured one (`9023` / `9024` by default), and the ports in the address win for that address. Anything else (a scheme, more than two ports...) is an invalid address: the connect call returns an `InvalidAddress` result instead of throwing.

The **QR code** of the desktop Connect dialog holds the IP address of your computer, followed by both ports (`ip:ws:http`) as soon as one of them is not the default. The desktop shows a wifi icon next to a device connected by address.

<a id="custom-ports"></a>

### Ports

Flocon Desktop listens on three ports:

| Port | Default | Used for |
| :--- | :--- | :--- |
| WebSocket | `9023` | the SDK connection |
| HTTP | `9024` | file uploads of the [Files](files.md) feature |
| MCP | `9042` | the [MCP](mcp.md) server for AI agents (fixed) |

If `9023` or `9024` are taken on your machine, change them in the desktop under **Settings > Server ports** (between `1024` and `65535`, different from each other, and not `9042`). The SDK must then use the same ports, except on an Android emulator or USB device with the Kotlin SDK, where the desktop's `adb reverse` maps the default `9023` / `9024` to the desktop ports:

=== "Kotlin"

    ```kotlin
    startFlocon(FloconContext()) {
        connect {
            address = "192.168.1.10"
            websocketPort = 3030
            httpPort = 3031
        }
    }
    ```

=== "Swift"

    ```swift
    startFlocon { flocon in
        flocon.config(host: "192.168.1.20", webSocketPort: 3030, httpPort: 3031)
        flocon.install(.network)
    }
    ```

=== "Flutter"

    ```dart
    await startFlocon((flocon) => flocon
      ..connect(host: '192.168.1.20', webSocketPort: 3030, httpPort: 3031));
    ```

!!! warning "Silent failure"
    If the SDK ports differ from the desktop ones where `adb reverse` is not used (Wi-Fi device, iOS, JVM, Wasm, Flutter), the app simply never connects, with no error. Turn on the SDK logs to see the attempts.

**Launch options.** The desktop can also override the ports for the current session only (never saved):

| Port | Argument | Environment variable |
| :--- | :--- | :--- |
| WebSocket | `--websocket-port=<n>` (or `--websocket-port <n>`) | `FLOCON_WEBSOCKET_PORT` |
| HTTP | `--http-port=<n>` (or `--http-port <n>`) | `FLOCON_HTTP_PORT` |

The priority for each port is: argument, environment variable, port saved in **Settings > Server ports**, default. If the resulting ports are invalid, or a value is not a number, the desktop prints the reason on stderr and exits with code `1` without opening a window. An overridden port is disabled in **Settings > Server ports**.

```bash
# macOS, installed app
open -a Flocon --args --websocket-port=9100 --http-port=9101

# from the sources
./gradlew :composeApp:run --args="--websocket-port=9100 --http-port=9101"
```

With `open -a`, the environment variables of your shell are not forwarded to an already installed app: prefer the arguments.

Something not connecting? See [Troubleshooting](troubleshooting.md).

---

## Release builds

Flocon is a debugging tool: once connected, the desktop can read your network calls, edit your preferences, delete files and run SQL on your databases. Keep it out of release builds.

=== "Kotlin"

    Use `debugImplementation` with the `-no-op` twin in `releaseImplementation`, as in [step 2](#2-add-the-sdk). The no-op has the same API: nothing is started and no connection is made. Use `flocon-no-op` **and** the no-op of every module you use. In a shared Kotlin Multiplatform module, which has a single variant, swap the modules in the release configurations of the app:

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

=== "Swift"

    The Swift SDK has no no-op package. Wrap the start in `#if DEBUG`: the other calls (`Flocon.analyticsPlugin`, ...) do nothing until the SDK is started, so they can stay in your code. The package is still linked in release builds, but inert.

    ```swift
    #if DEBUG
    startFlocon { flocon in flocon.install(.network) }
    #endif
    ```

=== "Flutter"

    Start Flocon only in debug builds with `kDebugMode` (a constant, removed from release builds by the compiler). Every other Flocon call (`Flocon.analytics`, `withFlocon()`...) does nothing when Flocon was not started. Use `!kReleaseMode` to also keep profile builds.

    The packages stay in your `dependencies`, so their native code is still in the release binary. To keep the Flocon Dart code out of it too, start Flocon from a debug-only entry point and keep every Flocon import there:

    ```dart
    // lib/main_debug.dart
    import 'package:flocon/flocon.dart';
    import 'package:flutter/widgets.dart';
    import 'main.dart' as app;

    Future<void> main() async {
      WidgetsFlutterBinding.ensureInitialized();
      await startFlocon((flocon) => flocon..install(FloconNetwork()));
      app.main();
    }
    ```

    ```bash
    flutter run -t lib/main_debug.dart   # debug, with Flocon
    flutter build apk                    # release, from lib/main.dart, without Flocon
    ```

---

## Next steps

- Pick a feature: [Network](network.md), [Databases](database.md), [Preferences](sharedpref.md), [Timeline](timeline.md)...
- Let an AI agent drive Flocon: [MCP](mcp.md).
- Something not showing up: [Troubleshooting](troubleshooting.md).
