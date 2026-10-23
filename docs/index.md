---
title: Flocon - Modern Debugging for Kotlin & Android
description: Advanced, lightweight debugging and inspection tool built for Android and Kotlin Multiplatform.
---

<div class="hero" markdown>

<img width="90" height="90" alt="Flocon Logo" src="assets/app_icon_small.png" />

# Flocon

<p class="lead">
An advanced, lightweight debugging & inspection tool built with <strong>Kotlin Multiplatform (KMP)</strong> and <strong>Compose Multiplatform</strong>, designed to seamlessly inspect and debug Android, JVM, and iOS applications in real time.
</p>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg?style=flat-square&color=4f46e5)](https://search.maven.org/artifact/io.github.openflocon/flocon)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)](https://github.com/openflocon/Flocon/blob/main/LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7f52ff.svg?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Desktop%20%7C%20iOS-brightgreen.svg?style=flat-square)](setup.md)

<div class="buttons" markdown>
[Get Started :octicons-arrow-right-24:](setup.md){ .md-button .md-button--primary }
[Download Desktop App :octicons-download-24:](https://github.com/openflocon/Flocon/releases){ .md-button }
[View on GitHub :octicons-mark-github-16:](https://github.com/openflocon/Flocon){ .md-button }
</div>

</div>

---

<img width="1294" height="837" alt="Flocon Desktop Interface" src="https://github.com/user-attachments/assets/3d585adb-6441-4cdb-ad25-69d771ad4ff6" style="border-radius: 8px; box-shadow: 0 4px 20px rgba(0,0,0,0.15);" />

---

## ⚡ Highlights & Key Features

<div class="grid cards" markdown>

-   :material-swap-horizontal: __Network & API Inspector__

    ---

    Capture and inspect all HTTP/REST (OkHttp, Ktor), GraphQL (Apollo), WebSockets, and gRPC requests with payload formatting and status codes.
    
    [:octicons-arrow-right-16: Learn more](network.md)

-   :material-theater: __Live Request Mocking__

    ---

    Intercept network calls on the fly and provide custom responses or simulate error status codes without writing boilerplate test mocks.

    [:octicons-arrow-right-16: Learn more](network.md#http-request-mocking)

-   :material-database: __Database Explorer & SQL Editor__

    ---

    Explore schemas, browse tables, run custom SQL queries with syntax highlighting, and log live database queries for Room & SQLite.

    [:octicons-arrow-right-16: Learn more](database.md)

-   :material-view-dashboard: __Reactive Custom Dashboards__

    ---

    Build custom mobile debug dashboards with buttons, forms, inputs, and Kotlin `Flow` bindings that render interactively on your desktop.

    [:octicons-arrow-right-16: Learn more](dashboard.md)

-   :material-key: __Preferences & DataStore__

    ---

    Inspect and edit `SharedPreferences`, `Jetpack DataStore`, and custom key-value stores in real-time from the desktop companion.

    [:octicons-arrow-right-16: Learn more](sharedpref.md)

-   :material-link-variant: __Deeplink Runner & Tools__

    ---

    Auto-discover and trigger parameterized deeplinks with autocomplete, inspect app sandboxed files, and preview downloaded images.

    [:octicons-arrow-right-16: Learn more](deeplink.md)

</div>

---

## 🏗️ Architecture Overview

Flocon connects your running mobile/desktop app to the Flocon Desktop companion over a lightweight local socket connection (via ADB on Android or direct sockets on Desktop/iOS):

```mermaid
sequenceDiagram
    autonumber
    actor Dev as Developer
    participant App as Mobile / KMP App
    participant SDK as Flocon SDK (~140KB)
    participant Bridge as ADB / Localhost Bridge
    participant Desktop as Flocon Desktop UI

    Dev->>App: Interacts with App
    App->>SDK: Network Request / DB Query / Event
    SDK->>Bridge: Stream telemetry over Socket
    Bridge->>Desktop: Live visual update
    Dev->>Desktop: Edits Prefs / Mocks Network / Triggers Deeplink
    Desktop->>Bridge: Send Command
    Bridge->>SDK: Execute in App runtime
    SDK-->>App: Mutates state / Injects response
```

---

## 📱 Platform Support Matrix

| Feature | Android | Desktop (JVM) | iOS (Simulator) | iOS (Device) |
| :--- | :---: | :---: | :---: | :---: |
| **Network (HTTP / Ktor / OkHttp)** | :white_check_mark: | :white_check_mark: | :white_check_mark: | Coming soon |
| **Network Mocking** | :white_check_mark: | :white_check_mark: | :white_check_mark: | Coming soon |
| **GraphQL & gRPC** | :white_check_mark: | :white_check_mark: | Coming soon | Coming soon |
| **WebSockets** | :white_check_mark: | :white_check_mark: | Coming soon | Coming soon |
| **Database (Room / SQLite)** | :white_check_mark: | :white_check_mark: | :white_check_mark: | Coming soon |
| **Preferences & DataStore** | :white_check_mark: | Coming soon | Coming soon | Coming soon |
| **Reactive Dashboards** | :white_check_mark: | Coming soon | Coming soon | Coming soon |
| **Data Tables** | :white_check_mark: | :white_check_mark: | :white_check_mark: | Coming soon |
| **Analytics Viewer** | :white_check_mark: | :white_check_mark: | :white_check_mark: | Coming soon |
| **Deeplink Launcher** | :white_check_mark: | Coming soon | Coming soon | Coming soon |
| **Sandbox File Explorer** | :white_check_mark: | Coming soon | Coming soon | Coming soon |
| **Image Previewer** | :white_check_mark: | :white_check_mark: | :white_check_mark: | Coming soon |

---

<div style="text-align: center; margin: 2.5rem 0;" markdown>

Ready to inspect your app in real-time?

[Start Setup Guide :octicons-arrow-right-24:](setup.md){ .md-button .md-button--primary .md-button--large }

</div>