---
title: Accessibility Inspector
description: See what TalkBack and VoiceOver read in your app, and drive TalkBack from the desktop.
---

# ♿ Accessibility Inspector

Flocon lists the **accessibility events** of your app: the elements focused by the screen reader, what it announces, and the clicks it triggers. Check what users of TalkBack (Android) or VoiceOver (iOS) actually hear, without wearing headphones.

---

## Overview

For each event you get the text read, the content description, the class and the bounds of the element, and, on Android, what a screen reader would likely say (label, role, state and hint).

From the desktop you can also:
- Move the focus to the **previous** or **next** element of the screen.
- Read the element which has the focus.
- Turn **TalkBack** on or off on the device.

---

## Setup

Add the module, and its no-op version for release builds:

```kotlin
debugImplementation("io.github.openflocon:flocon-accessibility:LAST_VERSION")
releaseImplementation("io.github.openflocon:flocon-accessibility-no-op:LAST_VERSION")
```

Install the plugin:

```kotlin
Flocon.initialize(context) {
    install(FloconAccessibility)
}
```

Events are captured once a screen reader is running on the device. No event is sent when it is off: turn TalkBack on from the desktop, or in the settings of the device, then move through your app.

You can also log an announcement yourself, so it is listed even without a screen reader:

```kotlin
Flocon.accessibilityPlugin.announce("Item added to the cart")
```

See the [protocol](https://github.com/openflocon/Flocon/blob/main/protocol/accessibility.md) for the details of each event.
