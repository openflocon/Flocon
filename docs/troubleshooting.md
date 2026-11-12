---
title: Troubleshooting
description: Fix the common problems when connecting Flocon Desktop to an Android, iOS or Flutter app.
---

# 🚨 Troubleshooting

Start with the basics, they explain most cases:

- Flocon Desktop is running **before** the app starts (the SDK retries every 3 seconds, so starting it later also works).
- The SDK is started (`startFlocon` was called) and the feature you look for is installed.
- The SDK and the desktop are on the same major version (the connection is encrypted with `wss://` / `https://`, an old SDK cannot reach a new desktop).
- Turn on the SDK logs to see the connection attempts: `FloconLogger.enabled = true` (Kotlin), `Flocon.isLoggingEnabled = true` (Swift, Flutter), set before starting.

---

## Android emulator or USB device

The SDK connects to `localhost` and the desktop runs `adb reverse` so that the ports reach your computer. No cleartext or `networkSecurityConfig` entry is needed.

```bash
adb devices
```

- The device must be listed as `device`. If it says `unauthorized`, accept the USB debugging prompt on the phone.
- `adb` must be in your `PATH` (see the [Linux guide](linux-install.md) for the packages).
- Several devices or restarted emulator: unplug, run `adb kill-server`, then `adb devices` again.
- **Flutter** on an Android emulator: use the host `10.0.2.2` in `connect(host: ...)`, see [Connect your app](setup.md#4-connect).

## iOS: the local network permission

On a physical device, iOS asks the user to allow the app to find devices on the local network, and needs `NSLocalNetworkUsageDescription` in `Info.plist` (Swift and Flutter apps, and Kotlin Multiplatform iOS apps).

- No prompt and no connection: the key is missing. Add it and reinstall the app.
- You tapped "Don't Allow": enable it again in **Settings > Privacy & Security > Local Network** (or in the settings of your app).
- The iOS Simulator and macOS apps connect to `localhost` and need nothing special. The macOS Flutter app also needs the `com.apple.security.network.client` entitlement, and Android Flutter apps the `INTERNET` permission.

## Physical device over Wi-Fi

There is no `adb reverse` for Wi-Fi: the app must be given the IP address of your computer, and both must be on the same network.

- Give the address with `connect { address = "..." }` / `Flocon.connect(...)`, or scan the **QR code** of the desktop Connect dialog. See [Connect your app](setup.md#4-connect).
- Guest or corporate Wi-Fi often isolates devices from each other ("client isolation"): use a personal hotspot or USB for Android.
- A VPN or a firewall on your computer can block the ports `9023` and `9024`.
- The address has no scheme and no path: `192.168.1.20` or `192.168.1.20:9023:9024`.

## Connection fails after changing ports

If you changed the ports in **Settings > Server ports**, or launched the desktop with `--websocket-port` / `--http-port`, the SDK must use the same ones, except on an Android emulator or USB device where `adb reverse` maps the default `9023` / `9024` to the desktop ports. Everywhere else (device over Wi-Fi, iOS, JVM, Wasm, Flutter) set the ports in the SDK, otherwise the app **silently** never connects. See [Ports](setup.md#ports).

Desktop ports must be in `1024-65535`, different from each other, and not `9042`.

## The MCP port is taken

The MCP server of the desktop always listens on port `9042` and this cannot be changed. If another program uses it, Flocon Desktop logs `Cannot start MCP server on port 9042` and retries every 10 seconds: close the other program (find it with `lsof -i :9042` on macOS and Linux). Your SDK connection is not affected. See [MCP](mcp.md).

## The "SDK update" banner

A badge **SDK x -> y** in the top bar of Flocon Desktop means the connected app runs an older SDK than the latest release. Click it to see the versions and the release notes. Update the SDK in your app (and keep the desktop up to date too): an SDK older than the desktop may miss features or fail to connect. You can ignore the notice from the same popup.

## Web (Kotlin/Wasm)

The browser rejects the self-signed certificate of the desktop: open `https://localhost:9023` once (use your websocket port) in the browser and accept the certificate.

## Something is connected but a screen is empty

- The feature is not installed in the SDK (`install(...)`), or the Kotlin module is a `-no-op` in the build you run. The desktop only shows the menu entries of the installed plugins.
- The feature is not supported on your platform: check the table of [the home page](index.md#platform-support) and the support table of the feature page.
- Wrong build variant: in Kotlin, the real modules are `debugImplementation`; a release build runs the no-ops.
