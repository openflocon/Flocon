---
title: Crash Reporter
description: Collect the uncaught crashes of your app and read their full stack traces from the desktop.
---

# 🐞 Crash Reporter

Flocon records the **uncaught exceptions** of your application and lists them in the desktop companion, with their full stack trace, so a crash that happened while the device was unplugged is never lost.

---

## Overview

From the desktop UI, you can:

- **Browse crashes**: every crash is listed with its exception type, message and date.
- **Read the stack trace**: select a crash to display its complete stack trace.
- **Copy & clean**: copy a report to share it, delete a single crash or clear the whole list.

Crash reports are also exposed to AI agents through the [MCP server](mcp.md) (`list_crash_reports`, `get_crash_report`).

---

## Setup

There is nothing to add: the crash handler is installed by `Flocon.initialize(...)`, as early as possible so that crashes happening during the initialization of your app are caught too. See [Initialize the SDK](setup.md#2-initialize-the-sdk).

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Flocon.initialize(this) // also installs the crash handler
    }
}
```

!!! info "When do crashes show up?"
    When the app crashes, the report is saved on the device and the original handler is called, so your app still crashes normally (and other crash tools keep working).
    The report is sent to Flocon Desktop **the next time the app connects**: relaunch the app after a crash to see it.

---

## Supported Platforms

| Platform | Support |
| --- | --- |
| Android | ✅ |
| Desktop (JVM) | ✅ (reports stored in `~/.flocon/crashes`) |
| iOS | ❌ Not yet supported |
