---
title: Sandbox File Explorer
description: Browse, inspect, and download files from your app's internal sandbox.
---

# 📁 Sandbox File Explorer

Flocon allows you to explore the **internal sandboxed storage** of your Android application without requiring complex ADB shell commands.

---

## Overview

<img width="1295" height="838" alt="File Explorer" src="https://github.com/user-attachments/assets/1c22c813-20c4-4e34-bcf8-6cc511deab21" style="border-radius: 8px;" />

From the desktop companion, you can:

- **Browse Directories**: Traverse internal files, cache folders, and databases.
- **Inspect Metadata**: View exact file sizes, MIME types, modification timestamps, and absolute file paths.
- **Download & View**: Inspect logs, exported JSON/database backups, and downloaded cached media directly on your desktop.
---

## Setup

No extra code is needed: the file explorer is available as soon as `Flocon.initialize(...)` is called (see [Initialize the SDK](setup.md#2-initialize-the-sdk)).

On Android, the root lists the two folders of your app sandbox:

- **Caches**: `context.cacheDir`
- **Files**: `context.filesDir`

!!! tip
    Select your device and app in the top bar of Flocon Desktop, then open a folder to browse its content. Use the refresh button to reload a folder that changed on the device.
