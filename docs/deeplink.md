---
title: Deeplink Launcher
description: Interactive deeplink runner with parameter autocompletion and execution.
---

# 🔗 Deeplink Launcher

Flocon includes an **interactive deeplink runner** for Android that eliminates the need to manually construct and execute cumbersome `adb shell am start` commands in terminal.

---

## Overview

<img width="1293" height="836" alt="Deeplink Runner" src="https://github.com/user-attachments/assets/eeaa30fb-6567-437a-96a4-dff44c6c6a54" style="border-radius: 8px;" />

From the desktop UI, you can:
- Browse all registered application deeplinks.
- Fill parameters interactively with **autocomplete suggestions**.
- Trigger navigation instantly on the connected Android device.

---

## Defining Deeplinks in Kotlin

Declare deeplink routes and test variables directly from your Android codebase:

```kotlin
Flocon.deeplinks {
    variable("host") {
        description = "Host scheme/domain"
        autoComplete(listOf("flocon", "myapp"))
    }

    variable("sample_comment")

    deeplink("[host]://home") {
        "host" withVariable "host"
    }

    deeplink("[host]://user/[userId]") {
        label = "User Profile"
        "userId" withAutoComplete listOf("101", "202", "303")
        "host" withVariable "host"
    }

    deeplink("[host]://post/[postId]?comment=[commentText]") {
        label = "Post Detail"
        description = "Opens a post and pre-fills comment text"
        "postId" withAutoComplete listOf("1", "2", "3")
        "commentText" withVariable "sample_comment"
        "host" withVariable "host"
    }
}
```