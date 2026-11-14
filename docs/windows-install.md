---
title: Install on Windows
description: Install Flocon Desktop on Windows with the MSI installer.
---

# 🪟 Install on Windows

Download the `.msi` installer (`Flocon-<version>.msi`, x64) from the [releases page](https://github.com/openflocon/Flocon/releases) and run it. Flocon then starts from the Start menu.

[:octicons-download-24: Download Flocon Desktop](https://github.com/openflocon/Flocon/releases){ .md-button .md-button--primary }

A portable jar is also attached (`Flocon-windows-x64.jar`); it needs a JDK 21 to run.

## adb

To reach Android emulators and USB devices, Flocon Desktop needs `adb` in your `PATH`. It comes with the Android SDK platform-tools (installed with Android Studio, in `%LOCALAPPDATA%\Android\Sdk\platform-tools`): add that folder to your `PATH`, then check:

```bash
adb devices
```

Next: [add the SDK to your app](setup.md#2-add-the-sdk-to-your-app).
