<img width="100" height="100" alt="flocon_small" src="https://github.com/user-attachments/assets/27143843-fce2-4c74-96d8-a0b35a8fccde" />

# Flocon

[![Maven Central](https://img.shields.io/maven-central/v/io.github.openflocon/flocon.svg)](https://search.maven.org/artifact/io.github.openflocon/flocon)

**Flocon lets you see what your Android, Kotlin Multiplatform, iOS and Flutter apps do: network, databases, preferences, analytics, crashes... from your desktop, and lets AI agents see it too.**

Inspired by [Flipper](https://github.com/facebook/flipper) by Meta, Flocon is a desktop debugger plus lightweight SDKs. Your app connects to the desktop app, and you observe, inspect and drive it in real time.

<!-- screenshot: hero -->
<img width="1294" height="837" alt="Screenshot 2025-09-12 at 15 39 45" src="https://github.com/user-attachments/assets/3d585adb-6441-4cdb-ad25-69d771ad4ff6" />

Full documentation: **[openflocon.github.io/Flocon](https://openflocon.github.io/Flocon/)**

## 1. Install the desktop app

**macOS** (Homebrew, no Gatekeeper prompt):

```bash
brew install --cask openflocon/tap/flocon
```

**Linux**: `brew install openflocon/tap/flocon` (Linuxbrew), or the `.deb` / `.rpm` (x64 and arm64) from the [releases](https://github.com/openflocon/Flocon/releases).

More in the [macOS](https://openflocon.github.io/Flocon/macos-install) and [Linux](https://openflocon.github.io/Flocon/linux-install) guides (manual DMG install, `adb`).

## 2. Add the SDK

| Platform | Install | Guide |
|---|---|---|
| **Kotlin** (Android & KMP) | `debugImplementation("io.github.openflocon:flocon:LAST_VERSION")` | [Setup](https://openflocon.github.io/Flocon/setup) |
| **Swift** (iOS) | Swift Package Manager: `https://github.com/openflocon/FloconSwift.git` | [FloconSwift](https://github.com/openflocon/FloconSwift), gRPC add-on: [FloconSwiftGrpc](https://github.com/openflocon/FloconSwiftGrpc) |
| **Flutter** | `flutter pub add flocon` | [FloconFlutter](https://github.com/openflocon/FloconFlutter), [pub.dev](https://pub.dev/packages/flocon) |

### Kotlin quick start

One module per feature, each with a `-no-op` twin for release builds. The BOM keeps every version aligned:

```kotlin
dependencies {
    debugImplementation(platform("io.github.openflocon:flocon-bom:LAST_VERSION"))
    releaseImplementation(platform("io.github.openflocon:flocon-bom:LAST_VERSION"))

    debugImplementation("io.github.openflocon:flocon")
    releaseImplementation("io.github.openflocon:flocon-no-op")

    debugImplementation("io.github.openflocon:flocon-network-core")
    releaseImplementation("io.github.openflocon:flocon-network-core-no-op")
    debugImplementation("io.github.openflocon:flocon-okhttp-interceptor")
    releaseImplementation("io.github.openflocon:flocon-okhttp-interceptor-no-op")
}
```

Start Flocon in `Application.onCreate()` and install the plugins you use:

```kotlin
startFlocon(FloconContext(this)) {
    install(FloconNetwork)
    install(FloconAnalytics)
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(FloconOkhttpInterceptor())
    .build()
```

Run the app, open Flocon, and your requests show up. Coming from 1.x? See the [migration guide](https://openflocon.github.io/Flocon/migration).

## Features at a glance

Support per SDK, built from the modules of each SDK. Click a feature for its page.

| Feature | Android | KMP | Swift | Flutter |
|---|:---:|:---:|:---:|:---:|
| [HTTP inspector](https://openflocon.github.io/Flocon/network) (OkHttp, Ktor, URLSession, Dio, `http`) | ✅ | ✅ | ✅ | ✅ |
| [Request mocking](https://openflocon.github.io/Flocon/mocking) and [bad network](https://openflocon.github.io/Flocon/bad-quality) | ✅ | ✅ | ✅ | ✅ |
| [GraphQL](https://openflocon.github.io/Flocon/graphql) | ✅ | ✅ | ✅ | ✅ |
| [WebSocket](https://openflocon.github.io/Flocon/websocket) | ✅ automatic (OkHttp) | ✅ manual | ✅ manual | ✅ manual |
| [gRPC](https://openflocon.github.io/Flocon/grpc) | ✅ | ❌ | ✅ (add-on) | ❌ |
| [Images](https://openflocon.github.io/Flocon/image) | ✅ | ✅ | ✅ | ✅ |
| [Databases](https://openflocon.github.io/Flocon/database) (Room, SQLDelight, SQLite) | ✅ | ✅ | ✅ | ✅ |
| [Preferences](https://openflocon.github.io/Flocon/sharedpref) (SharedPreferences, DataStore, UserDefaults) | ✅ | ✅ | ✅ | ✅ |
| [Files](https://openflocon.github.io/Flocon/files) | ✅ | ✅ | ✅ | ✅ |
| [Dashboards](https://openflocon.github.io/Flocon/dashboard) and [tables](https://openflocon.github.io/Flocon/table) | ✅ | ✅ | ✅ | ✅ |
| [Deeplinks](https://openflocon.github.io/Flocon/deeplink) | ✅ | ✅ | ✅ | ✅ |
| [Analytics](https://openflocon.github.io/Flocon/analytics) | ✅ | ✅ | ✅ | ✅ |
| [Feature flags](https://openflocon.github.io/Flocon/featureflags) | ✅ | ✅ | ✅ | ✅ |
| [Timeline](https://openflocon.github.io/Flocon/timeline) | ✅ | ✅ | ✅ | ✅ |
| [Crash reporter](https://openflocon.github.io/Flocon/crashreporter) | ✅ | ✅ | ✅ | ✅ |
| [Accessibility](https://openflocon.github.io/Flocon/accessibility) | ✅ | ⚠️ partial | ❌ | ❌ |
| [WebView](https://openflocon.github.io/Flocon/webview) | ✅ | ⚠️ manual API | ❌ | ❌ |
| [QR code connection](https://openflocon.github.io/Flocon/setup/#4-connect) | ✅ | ❌ | ✅ | ✅ |
| [AI agents (MCP)](https://openflocon.github.io/Flocon/mcp) | ✅ | ✅ | ✅ | ✅ |

## AI agents (MCP)

Flocon Desktop runs an MCP server (`http://localhost:9042/mcp`), so AI agents can work with your running app:

- inspect network calls, mock them, simulate a bad network
- read your app files, databases, preferences and analytics events
- works with Claude Code, Codex, Cursor, Gemini CLI, Antigravity, Copilot, Android Studio and more

Install the plugin in Claude Code (it also ships for Codex, Cursor and Gemini):

```
/plugin marketplace add openflocon/Flocon
/plugin install flocon@flocon
```

See [MCP setup](https://openflocon.github.io/Flocon/mcp), the [tool reference](https://openflocon.github.io/Flocon/mcp-tools) and the [cookbook](https://openflocon.github.io/Flocon/mcp-cookbook).

## Requirements

- **Desktop**: macOS, Linux or Windows. The portable jar needs JDK 21.
- **Android**: emulator or device, with `adb` for screenshots, recordings and deeplinks.
- **Kotlin**: KMP targets Android, JVM, iOS and Wasm.
- **Swift**: iOS 15 / macOS 12, Xcode 16+, Swift Package Manager.
- **Flutter**: Flutter 3.38.1+ (Dart 3.10+).

Stuck? See [troubleshooting](https://openflocon.github.io/Flocon/troubleshooting).

## Contributing

Issues and pull requests are welcome on [GitHub](https://github.com/openflocon/Flocon/issues). Thanks to everyone who makes Flocon better: [see all contributors](https://openflocon.github.io/Flocon/contributors).

## License

Flocon is MIT licensed, as found in the [LICENSE](/LICENSE) file.
