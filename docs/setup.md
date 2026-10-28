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

By default Flocon tries the address it can guess (`localhost` on an emulator or a simulator) and keeps retrying every 3 seconds. On a real device, that address does not reach your computer: let the user type its address (in a debug menu, for example) and add it with `Flocon.connect`. Flocon keeps trying the default address and also connects to the one you give:

```kotlin
startFlocon(FloconContext(this)) {
    connectLater() // optional, or connectLater(bufferCapacity = 10_000)
    install(FloconNetwork)
    install(FloconAnalytics)
}

// later, once the address is known
Flocon.connect("ws://192.168.1.20:9023")
```

Until a desktop is reachable, everything the plugins log is kept in memory. `Flocon.connect` is a `suspend` function: it opens the connection, introduces the app to the desktop, sends what was kept in the order it was logged, then sends the next messages right away. It returns a `FloconConnectResult` (`Connected`, `NotStarted`, `InvalidAddress` or `Failed`) instead of throwing; the messages are kept in every case, so you can call it again. Calling it with a new address replaces the previous one. The address can be `ws://host:port`, `host:port` or `host` (port `9023`).

Up to 5000 messages are kept: above that, the oldest are dropped. `connectLater(bufferCapacity = ...)` changes this size. The same buffer is used when a connection to the desktop is lost: the messages are kept, Flocon reconnects every 3 seconds, and sends them once it is back.

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