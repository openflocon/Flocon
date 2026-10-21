---
title: Accessibility Events
description: See what TalkBack and VoiceOver get from your app: focus order, content descriptions, announcements.
---

# ♿ Accessibility Events

Flocon streams the **accessibility events** of your application to the desktop, so you can check the focus order, the content descriptions, the announcements and the live-region updates without listening to a screen reader.

---

## Overview

Each event shows:
- **Type**: what happened, e.g. `VIEW_FOCUSED`, `VIEW_CLICKED`, `WINDOW_STATE_CHANGED`, `ANNOUNCEMENT`
- **Text & content description**: what the screen reader reads
- **Class & screen**: the element class and the activity it belongs to
- **Bounds**: the position of the element on screen
- **Extras**: platform details (hint, traits, assistive technology...)

The desktop also shows the **state** of the device: is an accessibility service on, is touch exploration on, which services. When no screen reader is on, the desktop tells you to turn on TalkBack or VoiceOver.

---

## Setup

```kotlin
dependencies {
    debugImplementation("io.github.openflocon:flocon-accessibility:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-accessibility-no-op:LAST_VERSION")
}
```

```kotlin
startFlocon(context) {
    install(FloconAccessibility)
}
```

---

## Platform support

| Platform | Capture | How | Limits |
|---|---|---|---|
| Android | Automatic | An accessibility delegate on the decor view of each activity, plus the `AccessibilityManager` state listeners | Events are only emitted when TalkBack or another accessibility service is on. Dialogs, popups and toasts (windows without an activity) are not covered |
| iOS | Partial | Notifications for the focused element and the finished announcements, plus the VoiceOver and Switch Control status | Needs VoiceOver on. Notifications posted by the app itself (screen or layout changes) cannot be observed: use `log()` |
| JVM (desktop) | Manual only | `log()` / `announce()` | The desktop shows that automatic capture is not supported |
| Wasm (browser) | Manual only | `log()` / `announce()` | The desktop shows that automatic capture is not supported |

On Android, turn on TalkBack on the device (or an emulator) and move the focus in your app: the events appear on the desktop.

---

## Manual API

Use it on every platform, for example for the events the automatic capture cannot see:

```kotlin
Flocon.accessibilityPlugin.log(
    AccessibilityEvent(
        type = "VIEW_FOCUSED",
        text = "Add to cart",
        contentDescription = "Add to cart",
        className = "Button",
        screen = "CartScreen",
        bounds = "[48,1530][1032,1674]",
        extras = mapOf("hint" to "Double tap to add"),
    )
)

// the text your app announces to a screen reader, logged as an ANNOUNCEMENT event
Flocon.accessibilityPlugin.announce("Item added to the cart")
```

`log` also accepts a `List<AccessibilityEvent>`.

---

## AI Agents (MCP)

Your agent can read the accessibility events with `get_accessibility_state`, `list_accessibility_events` and `clear_accessibility_events`. See [AI Agents (MCP)](mcp.md).
