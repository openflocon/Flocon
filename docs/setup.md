---
title: Getting started
description: Install Flocon Desktop, add the Kotlin, Swift or Flutter SDK to your app, and connect it to the desktop.
---

# 🚀 Getting started

Flocon has two parts:

1. **Flocon Desktop**: the app on your computer where you inspect and control your running app.
2. **A Flocon SDK**, added to your app in debug builds: **Kotlin** (Android and Kotlin Multiplatform), **Swift** (iOS and macOS) or **Flutter** (Android, iOS and macOS).

Three steps, a few minutes.

!!! info "Coming from 1.x?"
    Only the Kotlin SDK existed before 2.0. See the [migration guide](migration.md).

## 1. Install the desktop app

<div class="grid cards" markdown>

-   :material-apple: __macOS__

    ---

    `brew install --cask openflocon/tap/flocon`, or the DMG.

    [:octicons-arrow-right-16: Install on macOS](macos-install.md)

-   :material-linux: __Linux__

    ---

    `.deb`, `.rpm` (x64 and arm64), or Homebrew.

    [:octicons-arrow-right-16: Install on Linux](linux-install.md)

-   :material-microsoft-windows: __Windows__

    ---

    The `.msi` installer.

    [:octicons-arrow-right-16: Install on Windows](windows-install.md)

</div>

## 2. Add the SDK to your app

<div class="grid cards" markdown>

-   :material-robot: __With your AI agent__

    ---

    Install the Flocon plugin in Claude Code, Codex or Cursor, and let the agent set Flocon up in your project.

    [:octicons-arrow-right-16: Install with the Flocon plugin](install-with-ai.md)

-   :material-android: __Kotlin__

    ---

    Android and Kotlin Multiplatform (Android, JVM, iOS), one Gradle module per feature.

    [:octicons-arrow-right-16: Kotlin SDK](install-kotlin.md)

-   :material-apple-ios: __Swift__

    ---

    iOS and macOS, with Swift Package Manager.

    [:octicons-arrow-right-16: Swift SDK](install-swift.md)

-   :simple-flutter: __Flutter__

    ---

    Android, iOS and macOS, one pub.dev package per feature.

    [:octicons-arrow-right-16: Flutter SDK](install-flutter.md)

</div>

## 3. Connect

Start Flocon Desktop, then run your app.

<div class="grid cards" markdown>

-   :material-lan-connect: __Automatic__

    ---

    Android emulator or USB device, iOS Simulator, macOS and JVM apps: nothing to do.

    [:octicons-arrow-right-16: Connect your app](connect.md)

-   :material-wifi: __Over Wi-Fi__

    ---

    A physical device on the same network: give the SDK the address of your computer.

    [:octicons-arrow-right-16: Connect over Wi-Fi](connect-wifi.md)

-   :material-qrcode-scan: __With a QR code__

    ---

    Scan the QR code shown by Flocon Desktop from your app.

    [:octicons-arrow-right-16: Connect with a QR code](connect-qr-code.md)

</div>

Something not showing up? See [Troubleshooting](troubleshooting.md).
