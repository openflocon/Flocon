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

```kotlin
dependencies {
    debugImplementation("io.github.openflocon:flocon-files:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-files-no-op:LAST_VERSION")
}
```

```kotlin
startFlocon(FloconContext(context)) {
    install(FloconFiles)
}
```

!!! info "Platforms"
    File browsing is available on Android, JVM and iOS. On Wasm there is no file system, so the plugin lists no files.

---

## Folders

The desktop shows the default folders of the platform:

| Platform | Folders |
|----------|---------|
| Android  | Files (`filesDir`), Caches (`cacheDir`) |
| iOS      | Documents, Application Support, Caches, tmp, App container |
| JVM      | Working directory, Temp, Home |

Only these folders and the ones you register can be browsed: nothing outside of them is listed, downloaded or deleted,
and a root folder itself is never deleted.

Show another folder at install time:

```kotlin
startFlocon(FloconContext(context)) {
    install(FloconFiles) {
        folder(
            displayName = "Databases",
            absolutePath = context.getDatabasePath("dummy").parentFile!!.absolutePath,
        )
    }
}
```

or later, once the plugin is installed:

```kotlin
Flocon.filesPlugin.register(displayName = "Exports", absolutePath = exportsDir.absolutePath)
```

Registering the same folder again replaces its name; registering a default folder renames it. A folder that does not exist is not shown.

On iOS, show the container of an App Group shared with your widgets and extensions (the app needs the App Groups capability):

```kotlin
Flocon.filesPlugin.registerAppGroup("group.com.example.app")
```

