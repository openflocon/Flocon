---
title: Flocon - Debugger for Android, iOS and Flutter
description: A desktop debugger for Android, Kotlin Multiplatform, iOS (Swift) and Flutter apps, with an MCP server for AI agents.
---

<div class="hero" markdown>

<img width="90" height="90" alt="Flocon Logo" src="assets/app_icon_small.png" />

# Flocon

<p class="lead">
Inspect and control your running <strong>Android</strong>, <strong>Kotlin Multiplatform</strong>, <strong>iOS (Swift)</strong> and <strong>Flutter</strong> apps from a desktop app: network, databases, preferences, files, dashboards, crashes and more. AI agents can drive it too, through <strong>MCP</strong>.
</p>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg?style=flat-square&color=4f46e5)](https://search.maven.org/artifact/io.github.openflocon/flocon)
[![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)](https://github.com/openflocon/Flocon/blob/main/LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20KMP%20%7C%20iOS%20%7C%20Flutter-brightgreen.svg?style=flat-square)](setup.md)

<div class="buttons" markdown>
[Get Started :octicons-arrow-right-24:](setup.md){ .md-button .md-button--primary }
[Download Desktop App :octicons-download-24:](https://github.com/openflocon/Flocon/releases){ .md-button }
[View on GitHub :octicons-mark-github-16:](https://github.com/openflocon/Flocon){ .md-button }
</div>

</div>

---

![One app session in the Timeline graph: network calls, database queries, analytics events and logs](assets/screenshots/home-timeline-graph.jpg)

---

## ⚡ Highlights

<div class="grid cards" markdown>

-   :material-chart-timeline-variant: __Timeline: start here__

    ---

    Everything your app did, in order, on one screen: network calls, database queries, analytics, logs, crashes. Switch to the graph to see a whole session at a glance.

    [:octicons-arrow-right-16: Timeline](timeline.md)

-   :material-swap-horizontal: __Network__

    ---

    HTTP (OkHttp, Ktor, URLSession, Dio, `http`), GraphQL, WebSockets and gRPC, with live mocks, bad network simulation and an image preview.

    [:octicons-arrow-right-16: HTTP & REST](network.md) · [Mocking](mocking.md) · [gRPC](grpc.md)

-   :material-database: __Storage__

    ---

    Browse and query your SQLite databases, edit preferences and key-value stores, explore and download the files of your app.

    [:octicons-arrow-right-16: Databases](database.md) · [Preferences](sharedpref.md) · [Files](files.md)

-   :material-view-dashboard: __Custom debugging__

    ---

    Dashboards with buttons and forms, tables, parameterized deeplinks, analytics events and feature flags, all rendered on your desktop.

    [:octicons-arrow-right-16: Dashboards](dashboard.md) · [Deeplinks](deeplink.md) · [Analytics](analytics.md)

-   :material-bug: __Observability__

    ---

    Crash reports with stack traces, accessibility events and WebView traffic.

    [:octicons-arrow-right-16: Crash reporter](crashreporter.md) · [Accessibility](accessibility.md) · [WebView](webview.md)

-   :material-robot: __AI agents (MCP)__

    ---

    Flocon Desktop is an MCP server: let Claude Code, Cursor or Gemini read your network calls, query a database or set a mock.

    [:octicons-arrow-right-16: MCP overview](mcp.md) · [Cookbook](mcp-cookbook.md)

-   :material-cellphone-link: __Four SDKs, one desktop__

    ---

    Kotlin (Android, KMP), Swift (iOS, macOS) and Flutter SDKs share the same protocol and the same desktop app. Pick yours and connect in minutes.

    [:octicons-arrow-right-16: Getting started](setup.md)

</div>

---

## 🏗️ Architecture Overview

Your app, with the Flocon SDK in its debug build, connects to Flocon Desktop over a secure WebSocket (through `adb reverse` on Android emulators and USB devices, directly on iOS simulators and over Wi-Fi):

```mermaid
sequenceDiagram
    autonumber
    actor Dev as Developer
    participant App as App + Flocon SDK
    participant Desktop as Flocon Desktop
    participant AI as AI agent (MCP)

    Dev->>App: Uses the app
    App->>Desktop: Network call, DB query, event, crash...
    Desktop-->>Dev: Live view
    Dev->>Desktop: Edits a preference, sets a mock, runs a deeplink
    Desktop->>App: Command
    App-->>Dev: State changed, mocked response
    AI->>Desktop: MCP tool call
    Desktop-->>AI: Data from the app
```

---

## 📱 Platform support

Pick your platform to see which plugins are available. Click a feature for its details.

=== "Android"

    | Plugin | Support |
    | :--- | :--- |
    | [HTTP & REST](network.md) | ✅ OkHttp, Ktor |
    | [Mocking](mocking.md) | ✅ |
    | [Bad network simulation](bad-quality.md) | ✅ |
    | [GraphQL](graphql.md) | ✅ |
    | [WebSockets](websocket.md) | ✅ automatic (OkHttp) |
    | [gRPC](grpc.md) | ✅ |
    | [Images](image.md) | ✅ |
    | [Databases](database.md) | ✅ Room, SQLDelight |
    | [Preferences](sharedpref.md) | ✅ SharedPreferences, DataStore |
    | [Files](files.md) | ✅ |
    | [Dashboards](dashboard.md) | ✅ |
    | [Tables](table.md) | ✅ |
    | [Deeplinks](deeplink.md) | ✅ |
    | [Analytics](analytics.md) | ✅ |
    | [Feature flags](featureflags.md) | ✅ |
    | [Timeline](timeline.md) | ✅ |
    | [Crash reporter](crashreporter.md) | ✅ |
    | [Accessibility](accessibility.md) | ✅ automatic |
    | [WebView](webview.md) | ✅ automatic `attach` |

=== "iOS"

    Two ways to use Flocon on iOS: the **Swift** SDK (iOS and macOS), or the **Kotlin Multiplatform** SDK in a shared KMP module.

    | Plugin | Swift | Kotlin Multiplatform |
    | :--- | :--- | :--- |
    | [HTTP & REST](network.md) | ✅ `URLSession` | ✅ Ktor |
    | [Mocking](mocking.md) | ✅ | ✅ |
    | [Bad network simulation](bad-quality.md) | ✅ | ✅ |
    | [GraphQL](graphql.md) | ✅ | ✅ |
    | [WebSockets](websocket.md) | ✅ manual logging | ✅ manual logging |
    | [gRPC](grpc.md) | ✅ add-on `FloconSwiftGrpc` | ❌ |
    | [Images](image.md) | ✅ | ✅ |
    | [Databases](database.md) | ✅ SQLite (Core Data, GRDB...) | ✅ Room, SQLDelight |
    | [Preferences](sharedpref.md) | ✅ UserDefaults, keychain | ✅ DataStore |
    | [Files](files.md) | ✅ | ✅ |
    | [Dashboards](dashboard.md) | ✅ | ✅ |
    | [Tables](table.md) | ✅ | ✅ |
    | [Deeplinks](deeplink.md) | ✅ | ✅ |
    | [Analytics](analytics.md) | ✅ | ✅ |
    | [Feature flags](featureflags.md) | ✅ | ✅ |
    | [Timeline](timeline.md) | ✅ | ✅ |
    | [Crash reporter](crashreporter.md) | ✅ | ✅ |
    | [Accessibility](accessibility.md) | ❌ | ⚠️ partial |
    | [WebView](webview.md) | ❌ | ⚠️ manual API only |

=== "JVM / Wasm"

    Kotlin Multiplatform desktop (JVM) and web (Wasm) targets.

    | Plugin | JVM | Wasm |
    | :--- | :--- | :--- |
    | [HTTP & REST](network.md) | ✅ OkHttp, Ktor | ✅ Ktor |
    | [Mocking](mocking.md) | ✅ | ✅ |
    | [Bad network simulation](bad-quality.md) | ✅ | ✅ |
    | [GraphQL](graphql.md) | ✅ | ✅ |
    | [WebSockets](websocket.md) | ✅ manual logging | ✅ manual logging |
    | [gRPC](grpc.md) | ❌ | ❌ |
    | [Images](image.md) | ✅ | ✅ |
    | [Databases](database.md) | ✅ Room, SQLDelight | ✅ Room 3 only |
    | [Preferences](sharedpref.md) | ✅ DataStore | ✅ (no DataStore) |
    | [Files](files.md) | ✅ | ✅ (no browsing) |
    | [Dashboards](dashboard.md) | ✅ | ✅ |
    | [Tables](table.md) | ✅ | ✅ |
    | [Deeplinks](deeplink.md) | ✅ | ✅ |
    | [Analytics](analytics.md) | ✅ | ✅ |
    | [Feature flags](featureflags.md) | ✅ | ✅ |
    | [Timeline](timeline.md) | ✅ | ✅ |
    | [Crash reporter](crashreporter.md) | ✅ | ✅ |
    | [Accessibility](accessibility.md) | ⚠️ manual | ⚠️ manual |
    | [WebView](webview.md) | ⚠️ manual API only | ⚠️ manual API only |

=== "Flutter"

    Android, iOS and macOS.

    | Plugin | Support |
    | :--- | :--- |
    | [HTTP & REST](network.md) | ✅ Dio, `http` |
    | [Mocking](mocking.md) | ✅ |
    | [Bad network simulation](bad-quality.md) | ✅ |
    | [GraphQL](graphql.md) | ✅ |
    | [WebSockets](websocket.md) | ✅ manual logging |
    | [gRPC](grpc.md) | ❌ |
    | [Images](image.md) | ✅ |
    | [Databases](database.md) | ✅ SQLite (sqflite, Drift...) |
    | [Preferences](sharedpref.md) | ✅ SharedPreferences |
    | [Files](files.md) | ✅ |
    | [Dashboards](dashboard.md) | ✅ |
    | [Tables](table.md) | ✅ |
    | [Deeplinks](deeplink.md) | ✅ |
    | [Analytics](analytics.md) | ✅ |
    | [Feature flags](featureflags.md) | ✅ |
    | [Timeline](timeline.md) | ✅ |
    | [Crash reporter](crashreporter.md) | ✅ Dart errors only |
    | [Accessibility](accessibility.md) | ❌ |
    | [WebView](webview.md) | ❌ |

See each page for the exact limits per platform, and [Getting started](setup.md) for the modules and packages to add.

---

<div style="text-align: center; margin: 2.5rem 0;" markdown>

Ready to inspect your app in real-time?

[Get started :octicons-arrow-right-24:](setup.md){ .md-button .md-button--primary .md-button--large }

</div>
