---
title: Troubleshooting
description: Common issues and solutions when connecting Flocon Desktop to your mobile app.
---

# 🚨 Troubleshooting & FAQ

## Flocon Desktop Cannot See Device Calls

The SDK talks to the desktop over TLS (`wss://localhost:9023`, `https://localhost:9024` by default, see [Custom ports](setup.md#custom-ports)) through `adb reverse`, so no cleartext configuration is needed in your app (no `networkSecurityConfig` entry for `localhost`).

### 1. Check the connection
- Flocon Desktop must be running: it serves a self-signed certificate that the SDK accepts without verifying it.
- The SDK and Flocon Desktop must be on the same version: an older SDK still tries the removed `ws://` connection and cannot connect (see [Migration](migration.md)).
- Web (wasmJs) only: the browser rejects self-signed certificates, open `https://localhost:<websocket port>` (`9023` by default) once in the browser and accept the certificate.

---

## Connection fails after changing ports

If you changed the ports in Flocon Desktop (**Settings > Server ports**), the SDK must use the same ones, except on Android emulator / USB where `adb reverse` maps the default `9023` / `9024` to the desktop ports.

- Real device over Wi-Fi, iOS (simulator or device), JVM and Wasm: set `connect { websocketPort = ...; httpPort = ... }` (or `address = "host:port"`) to the desktop values. On iOS with the Swift SDK use `flocon.config(host:webSocketPort:httpPort:)`. Otherwise the app silently fails to connect.
- Desktop ports must be in `1024-65535`, different from each other, and not `9042` (MCP).
- Wasm: open `https://localhost:<websocket port>` once in the browser to accept the certificate.

See [Custom ports](setup.md#custom-ports).

---

## 2. ADB Connection Issues

!!! info "Verifying ADB Connectivity"
    Make sure your device is detected by ADB:
    ```bash
    adb devices
    ```
    If your device is in `unauthorized` state, check your phone screen for the USB debugging authorization prompt.

---

## 3. Version Mismatch

!!! warning "Keep Versions Aligned"
    Make sure the version of the Flocon SDK in your project matches the major/minor version of the **Flocon Desktop** client you have downloaded.
