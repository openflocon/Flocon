---
title: Crash Reporter
description: Capture the crashes of your app and inspect them in Flocon Desktop.
---

# 💥 Crash Reporter

Flocon records the **uncaught exceptions** of your app (type, message, stack trace and time) and
displays them in the desktop app, even when the crash happened while the desktop was not connected.

---

## Setup

```kotlin
dependencies {
    debugImplementation("io.github.openflocon:flocon-crashreporter:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-crashreporter-no-op:LAST_VERSION")
}
```

```kotlin
startFlocon(context) {
    install(FloconCrashReporter)
}
```

---

## How It Works

1. When the plugin is installed, Flocon registers an uncaught exception handler. The previous handler
   is still called, so the app crashes as usual (and your other crash tools keep working).
2. The crash is saved on the device.
3. On the next connection to Flocon Desktop, the saved crashes are sent and deleted from the device.

To install the handler yourself, disable the automatic setup and call `setupCrashHandler()`:

```kotlin
startFlocon(context) {
    install(FloconCrashReporter) {
        catchFatalErrors = false
    }
}

// later
Flocon.crashReporterPlugin.setupCrashHandler()
```
