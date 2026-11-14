---
title: Connect your app
description: How an app with the Flocon SDK reaches Flocon Desktop: automatically on emulators, simulators and USB devices, by address or QR code over Wi-Fi.
---

# Connect your app

Start Flocon Desktop, then run your app. The SDK connects in the background and retries every 3 seconds. Until a desktop is reachable, what the app logs is kept in memory (5000 messages by default, the oldest are dropped) and sent in order once connected.

## Automatic connection

In most setups there is nothing to do:

| Where the app runs | What to do |
| :--- | :--- |
| Android emulator (Kotlin) | Nothing. The SDK uses `localhost` and the desktop runs `adb reverse` for you. |
| Android device over USB (Kotlin) | Nothing (`adb reverse` too). USB debugging must be on and `adb devices` must list it. |
| iOS Simulator, macOS app, JVM desktop app | Nothing. `localhost` is the default. |
| Flutter on an Android emulator | Use the host `10.0.2.2`, see below. |
| Physical device over Wi-Fi (Android or iOS) | Give the SDK the address of your computer: [over Wi-Fi](connect-wifi.md) or by [scanning a QR code](connect-qr-code.md). |

For Android, the desktop needs `adb` in your `PATH` (it comes with the Android SDK platform-tools) to reach emulators and USB devices.

**Flutter on an Android emulator**: the emulator reaches your computer at `10.0.2.2`, and the Flutter SDK does not run `adb reverse`:

```dart
await startFlocon((flocon) => flocon
  ..connect(host: Platform.isAndroid ? '10.0.2.2' : 'localhost')
  ..install(FloconNetwork()));
```

## Follow the connection

Turn on the SDK logs to see the attempts: `FloconLogger.enabled = true` (Kotlin), `Flocon.isLoggingEnabled = true` (Swift, Flutter), set before starting. In your app, the connection state is exposed as:

| SDK | API | States |
| :--- | :--- | :--- |
| Kotlin | `Flocon.connectionState`, a `StateFlow<FloconConnectionState>` (import `io.github.openflocon.flocon.connectionState`) | `Idle`, `Buffering`, `Connecting`, `Connected`, `Failed(reason)` |
| Swift | `Flocon.connectionState` or the `Flocon.connectionStates` stream | `.idle`, `.buffering`, `.connecting`, `.connected`, `.failed(reason)` |
| Flutter | `Flocon.connectionState` or the `Flocon.connectionStates` stream | idle, buffering, connecting, connected, failed |

!!! warning "Keep the SDK and the desktop up to date together"
    The SDK connects with `wss://` / `https://` and the desktop no longer serves the plain ports, so an old SDK cannot connect to a new desktop, and the other way around. Flocon Desktop shows an **SDK update** badge in the top bar when the connected app runs an older SDK than the latest release.

<a id="custom-ports"></a>

## Ports

Flocon Desktop listens on three ports:

| Port | Default | Used for |
| :--- | :--- | :--- |
| WebSocket | `9023` | the SDK connection |
| HTTP | `9024` | file uploads of the [Files](files.md) feature |
| MCP | `9042` | the [MCP](mcp.md) server for AI agents (fixed) |

If `9023` or `9024` are taken on your machine, change them in the desktop under **Settings > Server ports** (between `1024` and `65535`, different from each other, and not `9042`). The SDK must then use the same ports, except on an Android emulator or USB device with the Kotlin SDK, where the desktop's `adb reverse` maps the default `9023` / `9024` to the desktop ports:

=== "Kotlin"

    ```kotlin
    startFlocon(FloconContext()) {
        connect {
            address = "192.168.1.10"
            websocketPort = 3030
            httpPort = 3031
        }
    }
    ```

=== "Swift"

    ```swift
    #if DEBUG
    startFlocon { flocon in
        flocon.config(host: "192.168.1.20", webSocketPort: 3030, httpPort: 3031)
        flocon.install(.network)
    }
    #endif
    ```

=== "Flutter"

    ```dart
    await startFlocon((flocon) => flocon
      ..connect(host: '192.168.1.20', webSocketPort: 3030, httpPort: 3031));
    ```

!!! warning "Silent failure"
    If the SDK ports differ from the desktop ones where `adb reverse` is not used (Wi-Fi device, iOS, JVM, Wasm, Flutter), the app simply never connects, with no error. Turn on the SDK logs to see the attempts.

**Launch options.** The desktop can also override the ports for the current session only (never saved):

| Port | Argument | Environment variable |
| :--- | :--- | :--- |
| WebSocket | `--websocket-port=<n>` (or `--websocket-port <n>`) | `FLOCON_WEBSOCKET_PORT` |
| HTTP | `--http-port=<n>` (or `--http-port <n>`) | `FLOCON_HTTP_PORT` |

The priority for each port is: argument, environment variable, port saved in **Settings > Server ports**, default. If the resulting ports are invalid, or a value is not a number, the desktop prints the reason on stderr and exits with code `1` without opening a window. An overridden port is disabled in **Settings > Server ports**.

```bash
# macOS, installed app
open -a Flocon --args --websocket-port=9100 --http-port=9101

# from the sources
./gradlew :composeApp:run --args="--websocket-port=9100 --http-port=9101"
```

With `open -a`, the environment variables of your shell are not forwarded to an already installed app: prefer the arguments.

Something not connecting? See [Troubleshooting](troubleshooting.md).
