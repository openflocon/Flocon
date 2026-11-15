# Changelog

Notable changes of each release. Full release notes and binaries are on the [releases page](https://github.com/openflocon/Flocon/releases).

## [2.0.0](https://github.com/openflocon/Flocon/releases/tag/2.0.0) - 2026-11-13

The highlights between **1.10.0** and **2.0.0**. Upgrading a Kotlin app from 1.x? Follow the [migration guide](https://openflocon.github.io/Flocon/migration/).

**Breaking changes in the Kotlin SDK.** The SDK is split into one module per feature (each with a `-no-op` twin), it is initialized with an `install { }` DSL, and every plugin is reached as `Flocon.xxxPlugin`. `flocon-base` is gone.
See [Migrating from 1.x](https://openflocon.github.io/Flocon/migration/) for the steps, the API mapping and the pitfalls.

### SDKs

- **One module per feature** + `-no-op` for release builds, aligned by the **`flocon-bom`**.
- **Kotlin Multiplatform**: Android, JVM, iOS and Wasm (see the [platform support](https://openflocon.github.io/Flocon/migration/#platform-support-details)).
- **Swift** ([FloconSwift](https://github.com/openflocon/FloconSwift)) and **Flutter** ([FloconFlutter](https://github.com/openflocon/FloconFlutter)) SDKs, on 2.0.0 like the desktop.
- **Encrypted connection** (wss/https): no more cleartext traffic config.
- **Connect later**: start without a server address and keep what was logged until the desktop is found.
- **Configurable ports**, and the device sends its connection type (USB or Wi-Fi).
- **SQLDelight** and **Room 3** support for [databases](https://openflocon.github.io/Flocon/database/).

### New features

- [Timeline](https://openflocon.github.io/Flocon/timeline/): every event of a session on one screen, with filters, a graph view, app logs and optional periodic screenshots.
- [Feature flags](https://openflocon.github.io/Flocon/featureflags/), [Accessibility](https://openflocon.github.io/Flocon/accessibility/) (with a floating TalkBack remote) and [WebView](https://openflocon.github.io/Flocon/webview/) (console, page events, bridge calls).
- [Connect over Wi-Fi](https://openflocon.github.io/Flocon/connect-wifi/) and [with a QR code](https://openflocon.github.io/Flocon/connect-qr-code/).
- [ADB Commander](https://openflocon.github.io/Flocon/adb-commander/) to run ADB commands from the desktop.
- iOS Simulator support: open deeplinks, take screenshots and record the screen.

### Desktop app

- New design: light and dark themes, a common filter bar and panel layout on every screen, a new logo and a dog mascot.
- Choose which plugins are shown and the start screen.
- Reworked screens: [Network](https://openflocon.github.io/Flocon/network/) (resizable columns, mock editor with a URL tester), [Images](https://openflocon.github.io/Flocon/image/), [Files](https://openflocon.github.io/Flocon/files/), [Preferences](https://openflocon.github.io/Flocon/sharedpref/) (typed, editable inline), [Databases](https://openflocon.github.io/Flocon/database/) (transactions grouped), [Crash reporter](https://openflocon.github.io/Flocon/crashreporter/), [Dashboards](https://openflocon.github.io/Flocon/dashboard/), [Deeplinks](https://openflocon.github.io/Flocon/deeplink/).
- [Analytics](https://openflocon.github.io/Flocon/analytics/): exclude events by name, saved per app.
- Settings: data size and purge, cleanup of screenshots and recordings, logs tab.
- Install with **Homebrew** (macOS, Linux), Linux arm64 and `.rpm` packages.
- Opt-in, anonymous [usage telemetry](https://openflocon.github.io/Flocon/telemetry/).

### AI agents (MCP)

New in 2.0: an [MCP server](https://openflocon.github.io/Flocon/mcp/) that lets AI agents (Claude Code, Codex, Gemini...) read what Flocon captured: network calls and mocks, bad network, databases, files, preferences, analytics, tables, dashboards, crashes, images, deeplinks and the timeline. They can also take a desktop screenshot and navigate the app. The `flocon-setup` skill [installs or migrates](https://openflocon.github.io/Flocon/install-with-ai/) Flocon in your app.

See the [tools reference](https://openflocon.github.io/Flocon/mcp-tools/) for the full list.

### 👒 Dependencies

- OkHttp 4.12 minimum, Room 3, SQLDelight.
