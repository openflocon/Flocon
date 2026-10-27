<img width="100" height="100" alt="flocon_small" src="https://github.com/user-attachments/assets/27143843-fce2-4c74-96d8-a0b35a8fccde" />     

**Flocon** is an advanced debugging and inspection tool built with **Kotlin Multiplatform (KMP)**, designed to work seamlessly across Android and desktop environments.  

Inspired from [Flipper](https://github.com/facebook/flipper) by Meta, while leveraging **modern Kotlin multiplatform architecture** for networking, databases, analytics, and UI data visualization.

It allows developers to connect a Kotlin Multiplatform or Android app to their computer and launch a **desktop interface** that can **observe, inspect, and interact with the running app** in real time — across shared Kotlin code and platform-specific implementations.

---

<img width="1294" height="837" alt="Screenshot 2025-09-12 at 15 39 45" src="https://github.com/user-attachments/assets/3d585adb-6441-4cdb-ad25-69d771ad4ff6" />

## Getting Started

Since 2.0, the SDK is split into one module per feature: add the core and the plugins you need, each with its `-no-op` version for release builds.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon)
```kotlin
debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-no-op:LAST_VERSION")

// for example, the network inspector with OkHttp
debugImplementation("io.github.openflocon:flocon-network-core:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-network-core-no-op:LAST_VERSION")
debugImplementation("io.github.openflocon:flocon-okhttp-interceptor:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op:LAST_VERSION")
```

in your `Application.kt`
```kotlin
startFlocon(FloconContext(this)) {
    install(FloconNetwork)
    install(FloconDatabase) { room() }
    install(FloconAnalytics)
}

// then, anywhere in your app
Flocon.analyticsPlugin.log(AnalyticsEvent("button_clicked", "events"))
```

On desktop use `FloconContext(appName, packageName)`, on iOS `FloconContext()`.

The address of the desktop app is not known yet? Add `connectLater()` in the `startFlocon` block, then call `Flocon.connect("ws://192.168.1.20:9023")` when you have it: what was logged in the meantime is sent in order (see the [setup guide](https://openflocon.github.io/Flocon/setup#connecting-later)).

Coming from 1.x? See the 👉 **[migration guide](https://openflocon.github.io/Flocon/migration)**.

## 📖 Documentation

Visit the full documentation site for setup guides, feature details, and advanced usage:  
👉 **[openflocon.github.io/Flocon/](https://openflocon.github.io/Flocon/)**

### Table of Contents

- 🚀 **[Getting Started](https://openflocon.github.io/Flocon/setup)**
    - [Migrating to 2.0](https://openflocon.github.io/Flocon/migration)
    - [Installation Guide](https://openflocon.github.io/Flocon/setup#installation)
    - [Basic Usage](https://openflocon.github.io/Flocon/setup#usage)
    - [macOS Specifics](https://openflocon.github.io/Flocon/macos-install)
- 📡 **[Networking](https://openflocon.github.io/Flocon/network)**
    - [HTTP Inspector](https://openflocon.github.io/Flocon/network#📡-network-request-inspector)
    - [Request Mocking](https://openflocon.github.io/Flocon/network#🎭-http-request-mocking)
    - [GraphQL](https://openflocon.github.io/Flocon/graphql)
    - [gRPC](https://openflocon.github.io/Flocon/grpc)
    - [WebSockets](https://openflocon.github.io/Flocon/websocket)
- 💾 **[Local Storage](https://openflocon.github.io/Flocon/database)**
    - [Database Explorer (Room & SQLite)](https://openflocon.github.io/Flocon/database)
    - [Shared Preferences (Android)](https://openflocon.github.io/Flocon/sharedpref)
- 🛠️ **[Advanced Debugging](https://openflocon.github.io/Flocon/dashboard)**
    - [Custom Dashboards](https://openflocon.github.io/Flocon/dashboard)
    - [Dynamic Data Tables](https://openflocon.github.io/Flocon/table)
    - [Deeplink Launcher](https://openflocon.github.io/Flocon/deeplink)
    - [File Explorer](https://openflocon.github.io/Flocon/files)
    - [Analytics Viewer](https://openflocon.github.io/Flocon/analytics)
    - [Feature Flags Viewer](https://openflocon.github.io/Flocon/featureflags)
    - [Crash Reporter](https://openflocon.github.io/Flocon/crashreporter)
    - [Image Previewer](https://openflocon.github.io/Flocon/image)
- 🤖 **[AI Agents (MCP)](https://openflocon.github.io/Flocon/mcp)** — let Claude Code, Cursor, Codex, Gemini CLI or Antigravity inspect your network calls, mock them, simulate a bad network, read your app files, analytics events, feature flags and databases
- ❓ **[Troubleshooting](https://openflocon.github.io/Flocon/troubleshooting)**

---

## ✨ Features at a Glance

| Feature | Android | KMP (JVM/iOS) |
|---|:---:|:---:|
| **Network (HTTP/Mocking)** | ✅ | ✅ |
| **Database (Room/SQLite)** | ✅ | ✅ |
| **Preferences** | ✅ | ❌ |
| **Dashboards & Tables** | ✅ | ✅ |
| **Analytics & Images** | ✅ | ✅ |
| **Feature Flags** | ✅ | ✅ |
| **Deeplink & Files** | ✅ | ❌ |
| **AI Agents (MCP)** | ✅ | ✅ |

---

## 🧰 Requirements

- **Android**: Device/Emulator with USB debugging + ADB.
- **KMP**: a recent Kotlin version in your app (the SDK is built with Kotlin 2.4).
- **Desktop**: Flocon Desktop app (JVM-based).

For detailed platform-specific requirements, see the [Setup Guide](https://openflocon.github.io/Flocon/setup).

---

## 🤝 Contributors

Thanks to these amazing people for making Flocon better every day!

[See all contributors](https://openflocon.github.io/Flocon/contributors)

---

## License

Flocon is MIT licensed, as found in the [LICENSE](/LICENSE) file.
