<p align="center">
  <img height="450" alt="flocon_small" src="https://github.com/openflocon/Flocon/blob/main/ressources/flocon_banner_small.jpeg" />
</p>

<h1 align="center">Flocon</h1>

<p align="center">
  <b>See what your mobile app does, live on your desktop: network, databases, preferences, analytics, crashes...<br/>
  And let your AI agent see it too.</b>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.openflocon/flocon"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.github.openflocon/flocon?label=Android%20%26%20KMP" /></a>
  <a href="https://github.com/openflocon/FloconSwift/releases"><img alt="FloconSwift" src="https://img.shields.io/github/v/release/openflocon/FloconSwift?label=iOS%20%2F%20macOS" /></a>
  <a href="https://pub.dev/packages/flocon"><img alt="pub.dev" src="https://img.shields.io/pub/v/flocon?label=Flutter" /></a>
  <a href="https://openflocon.github.io/Flocon/mcp"><img alt="MCP" src="https://img.shields.io/badge/AI%20agents-MCP-black" /></a>
  <a href="LICENSE"><img alt="License: MIT" src="https://img.shields.io/badge/license-MIT-blue" /></a>
</p>

<!-- screenshot: hero -->
| **Network**: every HTTP, GraphQL and gRPC call. Mock it, slow it down. | **Timeline**: what happened in the app. |
|:---:|:---:|
| ![Network inspector](docs/assets/screenshots/network-detail.jpg) | ![Timeline](docs/assets/screenshots/home-timeline-graph.jpg) |
| **Preferences**: read and edit SharedPreferences, DataStore, UserDefaults. | **Analytics**: every event your app sends, per provider. |
| ![Preferences](docs/assets/screenshots/preferences-list.jpg) | ![Analytics](docs/assets/screenshots/analytics-stream.jpg) |

Inspired by [Flipper](https://github.com/facebook/flipper) by Meta, Flocon is a desktop debugger plus lightweight SDKs for Android, Kotlin Multiplatform, iOS and Flutter. Your app connects to the desktop app, and you observe, inspect and drive it in real time.

Full documentation: **[openflocon.github.io/Flocon](https://openflocon.github.io/Flocon/)**

## Your AI agent can see your app

Flocon Desktop runs an MCP server (`http://localhost:9042/mcp`). Claude Code, Codex, Cursor, Gemini CLI, Antigravity, Copilot, Android Studio and more can read the network calls, timeline, databases, preferences and crashes of your running app, then mock calls, simulate a bad network, open deeplinks and take screenshots.

![An AI agent finds a failing checkout call through the Flocon MCP server and opens it in the Flocon timeline](docs/assets/screenshots/mcp-session.jpg)

```mermaid
flowchart LR
    App["Your app<br/>Android · iOS · KMP · Flutter"] <--> Desktop["Flocon Desktop"]
    Desktop <--> MCP["MCP server<br/>localhost:9042/mcp"]
    MCP <--> Agent["Your AI agent<br/>Claude Code, Codex,<br/>Cursor, Gemini..."]
```

Try asking:

- *"What happened right before the crash?"*
- *"Mock `/profile` with a 503 and take a screenshot."*
- *"Which analytics events are sent on checkout?"*
- *"Turn on a slow network config and tell me which calls time out."*

Install the plugin in Claude Code (it also ships for Codex, Cursor and Gemini):

```
/plugin marketplace add openflocon/Flocon
/plugin install flocon@flocon
```

See [MCP setup](https://openflocon.github.io/Flocon/mcp), the [tool reference](https://openflocon.github.io/Flocon/mcp-tools) and the [cookbook](https://openflocon.github.io/Flocon/mcp-cookbook).

## 1. Install the desktop app

**macOS** (Homebrew, no Gatekeeper prompt):

```bash
brew install --cask openflocon/tap/flocon
```

**Linux**: `brew install openflocon/tap/flocon` (Linuxbrew), or the `.deb` / `.rpm` (x64 and arm64) from the [releases](https://github.com/openflocon/Flocon/releases).

**Windows**: the `.msi` installer from the [releases](https://github.com/openflocon/Flocon/releases).

More in the [macOS](https://openflocon.github.io/Flocon/macos-install), [Linux](https://openflocon.github.io/Flocon/linux-install) and [Windows](https://openflocon.github.io/Flocon/windows-install) guides (manual install, `adb`).

## 2. Add the SDK to your app

Pick the SDK for your platform. They all connect to the same desktop app.

<table>
<tr><th align="left"><a href="https://github.com/openflocon/Flocon/tree/main/FloconAndroid">Android &amp; Kotlin Multiplatform</a> &nbsp; <a href="https://central.sonatype.com/artifact/io.github.openflocon/flocon"><img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.github.openflocon/flocon?label=maven%20central"></a></th></tr>
<tr><td>

**Install**

```kotlin
// build.gradle.kts
debugImplementation("io.github.openflocon:flocon:LAST_VERSION")
// + one module per feature you need
```

<a href="https://openflocon.github.io/Flocon/install-kotlin"><img alt="Android integration guide" src="https://img.shields.io/badge/Android_integration_guide_%E2%86%92-3DDC84?style=for-the-badge&logo=android&logoColor=black"></a>

</td></tr>
</table>

<table>
<tr><th align="left"><a href="https://github.com/openflocon/FloconSwift">iOS / macOS (Swift)</a> &nbsp; <a href="https://github.com/openflocon/FloconSwift/releases"><img alt="FloconSwift release" src="https://img.shields.io/github/v/release/openflocon/FloconSwift?label=SwiftPM"></a></th></tr>
<tr><td>

**Install**

```text
// Xcode › File › Add Package Dependencies…
https://github.com/openflocon/FloconSwift.git
```

<a href="https://openflocon.github.io/Flocon/install-swift"><img alt="iOS integration guide" src="https://img.shields.io/badge/iOS_integration_guide_%E2%86%92-F05138?style=for-the-badge&logo=swift&logoColor=white"></a>

</td></tr>
</table>

<table>
<tr><th align="left"><a href="https://github.com/openflocon/FloconFlutter">Flutter</a> &nbsp; <a href="https://pub.dev/packages/flocon"><img alt="pub.dev" src="https://img.shields.io/pub/v/flocon?label=pub"></a></th></tr>
<tr><td>

**Install**

```bash
flutter pub add flocon
```

<a href="https://openflocon.github.io/Flocon/install-flutter"><img alt="Flutter integration guide" src="https://img.shields.io/badge/Flutter_integration_guide_%E2%86%92-0175C2?style=for-the-badge&logo=flutter&logoColor=white"></a>

</td></tr>
</table>

---

<table>
<tr><th align="left">Start Flocon &nbsp; <sub>same API on every platform</sub></th></tr>
<tr><td>

```kotlin
startFlocon { ... }
```

Then run your app. **[Full documentation](https://openflocon.github.io/Flocon/setup)**, emulators and physical devices included.

</td></tr>
</table>

## Coming from Flocon 1.x?

See the [migration guide](https://openflocon.github.io/Flocon/migration).

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
