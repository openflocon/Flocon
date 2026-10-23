---
title: ADB Commander
description: Run ADB commands, save your favorites and chain them into automation flows from Flocon Desktop.
---

# 💻 ADB Commander

The **ADB Commander** is a desktop-only tool to run `adb` commands on your Android devices without leaving Flocon: no terminal, no `-s <serial>` to type.

---

## Overview

From the desktop UI, you can:

- **Run any command**: type it with or without the `adb` prefix (`shell pm list packages -3`), the output is displayed in the console.
- **Quick commands**: pick a ready-made command (device info, app management, input events…) and fill its `[placeholders]`.
- **Saved commands**: star a command to keep it in your library and run it again in one click.
- **History**: re-run any of your recent commands.
- **Automation flows**: chain several commands into a named flow, with an optional label and delay for each step, then run it and follow the result of every step.

---

## Setup

The ADB Commander only needs a working `adb`:

1. Install the [Android SDK Platform Tools](https://developer.android.com/tools/releases/platform-tools).
2. Open **Settings** in Flocon Desktop and check the ADB path (Flocon tries to find it automatically).
3. Connect your device: commands target the **device selected in the top bar**, or every connected device when none is selected.

!!! tip
    The ADB Commander does not need the Flocon SDK in your app: it works with any Android device visible by `adb devices`.

See [Troubleshooting › ADB Connection Issues](troubleshooting.md#2-adb-connection-issues) if your device is not detected.
