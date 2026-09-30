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