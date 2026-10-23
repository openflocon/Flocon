---
title: Structured Data Tables
description: Stream and visualize structured data rows in interactive desktop tables.
---

# 📋 Structured Data Tables

Flocon supports structured **data tables** (Kotlin Multiplatform compatible). These allow your app to stream rows of multi-column data directly to the desktop interface for real-time inspection.

---

## Overview

<img width="1196" height="768" alt="Tables View" src="https://github.com/user-attachments/assets/ff3090fa-8f37-4138-a492-20b9159314af" style="border-radius: 8px;" />

Common use cases:
- Inspecting active in-memory user sessions or authentication states.
- Streaming custom business domain telemetry.
- Monitoring cache keys and eviction events.
- Tracking queued background jobs or workers.

---

## Setup

```kotlin
dependencies {
    debugImplementation("io.github.openflocon:flocon-tables:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-tables-no-op:LAST_VERSION")
}
```

```kotlin
startFlocon(context) {
    install(FloconTable)
}
```

---

## Logging Table Rows

To log a row into a named table, call `table(...)` on the table plugin and add the columns:

```kotlin
Flocon.tablePlugin.table("active_sessions") {
    column("user_id", "1024")
    column("name", "Raphael")
    column("status", "Active")
    column("last_seen", "2026-08-14 17:30")
}
```

---

## Real-Time Event Tracking

Every `table(...)` call appends a row with real-time updates:

```kotlin
fun onUserAction(user: User, action: String) {
    Flocon.tablePlugin.table("user_actions") {
        column("timestamp", System.currentTimeMillis().toString())
        column("user", user.name)
        column("action", action)
    }
}
```
