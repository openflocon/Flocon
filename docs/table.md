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

## Logging Table Rows

To log a row into a named table, call `floconTable(...)` and pass columns using the `toParam` DSL:

```kotlin
floconTable("active_sessions").log(
    "user_id" toParam "1024",
    "name" toParam "Raphael",
    "status" toParam "Active",
    "last_seen" toParam "2026-08-14 17:30"
)
```

---

## Real-Time Event Tracking

Every `.log()` invocation appends a row with real-time updates:

```kotlin
fun onUserAction(user: User, action: String) {
    floconTable("user_actions").log(
        "timestamp" toParam System.currentTimeMillis().toString(),
        "user" toParam user.name,
        "action" toParam action
    )
}
```