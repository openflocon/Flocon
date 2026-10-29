---
title: Troubleshooting
description: Common issues and solutions when connecting Flocon Desktop to your mobile app.
---

# 🚨 Troubleshooting & FAQ

## Flocon Desktop Cannot See Device Calls

The SDK talks to the desktop over TLS (`wss://localhost:9023`, `https://localhost:9024`) through `adb reverse`, so no cleartext configuration is needed in your app (no `networkSecurityConfig` entry for `localhost`).

### 1. Check the connection
- Flocon Desktop must be running: it serves a self-signed certificate that the SDK accepts without verifying it.
- The SDK and Flocon Desktop must be on the same version: an older SDK still tries the removed `ws://` connection and cannot connect, update both together.
- Web (wasmJs) only: the browser rejects self-signed certificates, open `https://localhost:9023` once in the browser and accept the certificate.

!!! tip "Connecting to the IP of your computer (real device)"
    `Flocon.connect("192.168.1.20")` (optional `flocon-connect` module) opens a TLS connection like the default one, nothing to allow in your `networkSecurityConfig`. See [Setup](setup.md).

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
