---
title: Accessibility Events
description: See what TalkBack and VoiceOver get from your app: focus order, content descriptions, announcements.
---

# ♿ Accessibility Events

Flocon streams the **accessibility events** of your application to the desktop, so you can check the focus order, the content descriptions, the announcements and the live-region updates without listening to a screen reader.

---

## Overview

Each event shows:
- **Type**: what happened, e.g. `CLICK`, `VIEW_FOCUSED`, `VIEW_CLICKED`, `WINDOW_STATE_CHANGED`, `ANNOUNCEMENT`
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
| Android | Automatic | Clicks: a touch observer on the window of each activity, always on. Other events: an accessibility delegate on the decor view, plus the `AccessibilityManager` state listeners | Clicks are captured without any accessibility service. The other events are only emitted when TalkBack or another accessibility service is on. Taps only (no keyboard, long press or drag). Dialogs, popups and toasts (windows without an activity) are not covered |
| iOS | Partial | Notifications for the focused element and the finished announcements, plus the VoiceOver and Switch Control status | Needs VoiceOver on. Notifications posted by the app itself (screen or layout changes) cannot be observed: use `log()` |
| JVM (desktop) | Manual only | `log()` / `announce()` | The desktop shows that automatic capture is not supported |
| Wasm (browser) | Manual only | `log()` / `announce()` | The desktop shows that automatic capture is not supported |

On Android, taps appear on the desktop right away. Turn on TalkBack on the device (or an emulator) and move the focus in your app to also get the focus, announcement and window events.

### Clicks on Android (`CLICK`)

Flocon observes the touches of each activity window (it never consumes or alters them). When a tap ends on a clickable View or Compose element, it sends a `CLICK` event describing what a screen reader would read for that element: text, content description and role. A tap on nothing clickable, a scroll or a long press sends nothing.

Extras of a `CLICK` event:

| Extra | Meaning |
|---|---|
| `source` | `view` or `compose` |
| `viewId` | Resource entry name of the View (Views only) |
| `testTag` | Test tag of the node (Compose only) |
| `stateDescription` | State description of the node (Compose only) |
| `clickLabel` | Label of the click action (Compose only) |
| `enabled` | `true` or `false` |
| `missingLabel` | `true` when the element is clickable but has no text and no content description: a screen reader cannot name it. The desktop shows a warning on these rows |

Compose is detected at runtime, the SDK does not add it to your app. `VIEW_CLICKED` (sent by the platform) still appears when a service is on, next to `CLICK`.

---

## Move the focus from the desktop

The top of the Accessibility panel shows the element the screen reader is on (text or content description, class, bounds, and its position in the focus order of the screen). The **previous** / **next** buttons move the accessibility focus on the device, like a swipe left / right with TalkBack, and the refresh button asks where the focus is now.

- Android only, and only while TalkBack (or another accessibility service) is on. The buttons are disabled otherwise.
- The SDK builds the focus order of the resumed activity itself (Views, and the merged semantics tree of Compose), so it can differ slightly from TalkBack's. It does not wrap around: next on the last element does nothing.
- Dialogs, popups and other windows without an activity are not covered.

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
