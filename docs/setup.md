---
title: Installation & Setup
description: Step-by-step installation and initialization guide for Flocon SDK and Desktop client.
---

# 🚀 Getting Started with Flocon

Flocon consists of two parts:
1. **Flocon SDK** (~140KB): Integrated into your Android or Kotlin Multiplatform (KMP) app.
2. **Flocon Desktop App**: The desktop companion used to observe, inspect, and interact with the running app.

---

## 1. Add Dependencies

=== "libs.versions.toml (Recommended)"

    ```toml
    [versions]
    flocon = "LAST_VERSION" # Replace with latest release

    [libraries]
    flocon = { module = "io.github.openflocon:flocon", version.ref = "flocon" }
    flocon-no-op = { module = "io.github.openflocon:flocon-no-op", version.ref = "flocon" }
    ```

    Then in your module `build.gradle.kts`:

    ```kotlin
    dependencies {
        debugImplementation(libs.flocon)
        releaseImplementation(libs.flocon.no.op)
    }
    ```

=== "build.gradle.kts (Android Only)"

    ```kotlin
    dependencies {
        // Active in debug builds
        debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
        
        // No-op placeholder in release builds
        releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")
    }
    ```

=== "build.gradle.kts (Kotlin Multiplatform)"

    ```kotlin
    kotlin {
        sourceSets {
            commonMain.dependencies {
                implementation("io.github.openflocon:flocon:LAST_VERSION")
            }
        }
    }
    ```

---

## 2. Initialize the SDK

Initialize Flocon as early as possible in your application lifecycle:

=== "Android (`Application.kt`)"

    ```kotlin
    class MyApp : Application() {
        override fun onCreate() {
            super.onCreate()
            
            // Initializes Flocon with Android Context
            Flocon.initialize(this)
        }
    }
    ```

=== "Desktop (JVM)"

    ```kotlin
    fun main() {
        // Initialize Flocon for Desktop
        Flocon.initialize()
        
        // Launch your Compose Desktop application
    }
    ```

=== "iOS (Kotlin Multiplatform)"

    ```kotlin
    fun initFlocon() {
        // Initialize Flocon for iOS Simulator
        Flocon.initialize()
    }
    ```

---

### Connecting to another address

By default Flocon tries the address it can guess (`localhost` on an emulator or a simulator) and keeps retrying every 3 seconds. On a real device, that address does not reach your computer: let the user type its address (in a debug menu, for example) and add it with `Flocon.connect`. Flocon keeps trying the default address and also connects to the one you give. This lives in the optional `flocon-connect` module:

```kotlin
debugImplementation("io.github.openflocon:flocon-connect:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-connect-no-op:LAST_VERSION")
```

It also allows the cleartext `ws://` connection to any IP address (Android cannot allow an IP range), for debug builds only. If your app declares its own `networkSecurityConfig`, it takes precedence: see [Troubleshooting](troubleshooting.md).

```kotlin
import io.github.openflocon.flocon.connect
import io.github.openflocon.flocon.connectionState

startFlocon(FloconContext(this)) {
    connect { } // optional, see below
    install(FloconNetwork)
    install(FloconAnalytics)
}

// later, once the address is known
Flocon.connect("192.168.1.20")
```

Until a desktop is reachable, everything the plugins log is kept in memory. `Flocon.connect` is a `suspend` function: it opens the connection, introduces the app to the desktop, sends what was kept in the order it was logged, then sends the next messages right away. It returns a `FloconConnectResult` (`Connected`, `NotStarted`, `InvalidAddress` or `Failed`) instead of throwing; the messages are kept in every case, so you can call it again. Calling it with a new address replaces the previous one. The desktop shows a wifi icon next to a device connected this way. These devices have no adb link, so the features that rely on adb (screenshot, screen recording, deeplinks on Android, ADB Commander and their MCP tools) are disabled for them. Give only the IP address (or host) of the computer, without scheme nor port: the port is always `9023`, anything else returns `InvalidAddress`.

### Connecting with a QR code

The desktop app has a **Connect** button in the top bar (QR code icon). It opens a dialog listing the IP addresses of the computer on the local network (one per network interface) and, for each one, a button that shows the address as a QR code. The QR code contains only the IP address, the same value as `Flocon.connect("192.168.1.20")`.

On Android, the optional `flocon-qr-scanner` module scans it for you, so nobody has to type the IP. It opens the Google code scanner, a screen of Google Play Services, so the app needs no camera permission but the device needs Google Play Services:

```kotlin
debugImplementation("io.github.openflocon:flocon-qr-scanner:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-qr-scanner-no-op:LAST_VERSION")
```

```kotlin
import io.github.openflocon.flocon.qrscanner.scanQrCodeAndConnect

// from a button of a debug menu
val result = Flocon.scanQrCodeAndConnect(activity)
```

`scanQrCodeAndConnect` is a `suspend` function. It returns the `FloconConnectResult` of the connection (a failed scan, for example without Google Play Services, is a `Failed`), or `null` if the user closed the scanner without scanning. If you scan with your own screen, give the text you read to `Flocon.connectWithQrCode(content)`: the content is the IP address, or a URL whose host is that address (the scheme, the port and the path are ignored).

Up to 5000 messages are kept: above that, the oldest are dropped. `connect { bufferCapacity = ... }` changes this size.

`connect { ... }` has two more options, all set here with a computer that has a fixed IP:

```kotlin
startFlocon(FloconContext(this)) {
    connect {
        bufferCapacity = 10_000       // messages kept while no desktop is reachable
        address = "192.168.1.20"      // also tried at start, next to the default address
        rememberLastAddress = true    // the last connected address is tried again at the next launch
    }
    install(FloconNetwork)
}
```

- `address`: the IP of a computer to also try when Flocon starts, for example one with a fixed IP (`connect { address = "192.168.1.20" }`). Same rules as `Flocon.connect`: no port nor scheme.
- `rememberLastAddress` (`true` by default): the last address Flocon connected to (given here or typed with `Flocon.connect`) is kept in the app preferences (SharedPreferences on Android, UserDefaults on iOS, `java.util.prefs` on desktop, `localStorage` on web) and tried again at the next launch, next to the default address. It wins over `address`. Pass `false` to disable it.

The saving and the retry at launch work when `connect { }` is called in the `startFlocon` block.

The same buffer is used when a connection to the desktop is lost: the messages are kept, Flocon reconnects every 3 seconds, and sends them once it is back.

Follow the connection with `Flocon.connectionState`, a `StateFlow<FloconConnectionState>` (`Connected` as soon as one address is connected):

```kotlin
Flocon.connectionState.collect { state ->
    when (state) {
        FloconConnectionState.Idle -> Unit       // Flocon is not started
        FloconConnectionState.Buffering -> Unit  // not connected, the messages are kept
        FloconConnectionState.Connecting -> Unit
        FloconConnectionState.Connected -> Unit  // the messages are sent right away
        is FloconConnectionState.Failed -> Unit  // state.reason, the messages are still kept
    }
}
```

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

## 4. Requirements & Prerequisites

=== "Android Requirements"
    - An Android device or emulator with **USB Debugging** enabled.
    - Android SDK Tools with `adb` (Android Debug Bridge) installed and accessible in your system `PATH`.
    - Allowed cleartext connection to `localhost` / `127.0.0.1` (see [Troubleshooting](troubleshooting.md)).

=== "Multiplatform Requirements"
    - Kotlin **2.0.0** or newer.
    - JVM 17+ for running the desktop application.
    - Aligned version between the mobile library and the desktop application.