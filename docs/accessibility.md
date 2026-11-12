---
title: Accessibility Events
description: See what TalkBack and VoiceOver get from your app: focus order, content descriptions, announcements.
---

# ♿ Accessibility Events

Flocon streams the **accessibility events** of your application to the desktop, so you can check the focus order, the content descriptions, the announcements and the live-region updates without listening to a screen reader.

<!-- screenshot: accessibility-events | scenario=doc-accessibility | screen=accessibility | event list with CLICK and focus events, TalkBack remote visible -->

## Platform support

The Accessibility plugin is available in the **Kotlin / KMP SDK only**. The Swift and Flutter SDKs do not have it yet.

| | Android | KMP (iOS / JVM / Wasm) | Swift (iOS / macOS) | Flutter |
|---|:---:|:---:|:---:|:---:|
| Automatic capture | ✅ | ✅ (iOS partial, JVM and Wasm manual only) | ❌ | ❌ |
| Manual `log()` / `announce()` | ✅ | ✅ | ❌ | ❌ |
| Move the focus from the desktop | ✅ | ❌ | ❌ | ❌ |

| Platform | Capture | How | Limits |
|---|---|---|---|
| Android | Automatic | Clicks: a touch observer on the window of each activity, always on. Other events: an accessibility delegate on the decor view, plus the `AccessibilityManager` state listeners | Clicks are captured without any accessibility service. The other events are only emitted when TalkBack or another accessibility service is on. Taps only (no keyboard, long press or drag). Dialogs, popups and toasts (windows without an activity) are not covered |
| iOS (KMP) | Partial | Notifications for the focused element and the finished announcements, plus the VoiceOver and Switch Control status | Needs VoiceOver on. Notifications posted by the app itself (screen or layout changes) cannot be observed: use `log()` |
| JVM (desktop) | Manual only | `log()` / `announce()` | The desktop shows that automatic capture is not supported |
| Wasm (browser) | Manual only | `log()` / `announce()` | The desktop shows that automatic capture is not supported |

## Setup

=== "Kotlin"

    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon-accessibility:2.0.0")
        releaseImplementation("io.github.openflocon:flocon-accessibility-no-op:2.0.0")
    }
    ```

    ```kotlin
    startFlocon(FloconContext(context)) {
        install(FloconAccessibility)
    }
    ```

=== "Swift"

    !!! note "Not available in the Swift SDK yet"
        There is no accessibility plugin in FloconSwift.

=== "Flutter"

    !!! note "Not available in the Flutter SDK yet"
        There is no accessibility plugin in the Flutter packages.

## Usage

=== "Kotlin"

    On Android, nothing to call: taps appear on the desktop right away. Turn on TalkBack on the device (or an emulator) and move the focus in your app to also get the focus, announcement and window events.

    On the other platforms, or for the events the automatic capture cannot see, use the manual API:

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

=== "Swift"

    !!! note "Not available in the Swift SDK yet"

=== "Flutter"

    !!! note "Not available in the Flutter SDK yet"

## In Flocon Desktop

Each event shows:

- **Type**: what happened, e.g. `CLICK`, `VIEW_FOCUSED`, `VIEW_CLICKED`, `WINDOW_STATE_CHANGED`, `ANNOUNCEMENT`
- **Text & content description**: what the screen reader reads
- **Class & screen**: the element class and the activity it belongs to
- **Bounds**: the position of the element on screen
- **Extras**: platform details (hint, traits, assistive technology...)

Filter the list by type or with a text, and clear the events to isolate the next interaction.

The desktop also shows the **state** of the device: is an accessibility service on, is touch exploration on, which services. When no screen reader is on, the desktop tells you to turn on TalkBack or VoiceOver.

<!-- screenshot: accessibility-state-banner | scenario=doc-accessibility | screen=accessibility | banner telling that no accessibility service is running -->

### Move the focus from the desktop

The top of the Accessibility panel shows the element the screen reader is on (text or content description, class, bounds, and its position in the focus order of the screen). The **previous** / **next** buttons move the accessibility focus on the device, like a swipe left / right with TalkBack, and the refresh button asks where the focus is now.

- A **TalkBack** remote (collapsible, draggable) also shows whether TalkBack is on and lets you turn it on or off from the desktop when the permission is granted. Over Wi-Fi, the desktop gives you the `adb` command to grant it once.
- Android only, and only while TalkBack (or another accessibility service) is on. The buttons are disabled otherwise.
- The SDK builds the focus order of the resumed activity itself (Views, and the merged semantics tree of Compose), so it can differ slightly from TalkBack's. It does not wrap around: next on the last element does nothing.
- Dialogs, popups and other windows without an activity are not covered.

## With AI agents (MCP)

- `list_accessibility_events`: the events, newest first, filterable by type and text.
- `get_accessibility_state`: whether capture is supported, a screen reader is running, touch exploration and the running services. Use it when the list is empty.
- `get_accessibility_focused_node`: the element TalkBack is on, with its position in the focus order (Android, TalkBack on).
- `move_accessibility_focus`: moves the focus to the next or previous element and returns where it landed (Android, TalkBack on).
- `clear_accessibility_events`: deletes the received events.

> "Walk the focus order of the checkout screen and list the elements that have no label."

See the [MCP cookbook](mcp-cookbook.md) for full workflows.
