---
title: Timeline
description: Follow everything your app did, in order, on a single screen: network calls, analytics, database queries, crashes...
---

# 🕒 Timeline

The timeline merges the events received by Flocon Desktop into **one chronological list**, grouped by day and by app session, so you can see what happened right before a crash or an HTTP error.

---

## Overview

From the desktop UI, you can:

- **Follow the events in order**: network calls (a request line with a → arrow, then a response line with a ← arrow when it arrives; selecting or hovering one highlights both, and hovering shows a bar linking them), analytics events, WebView events (console messages, page loads and errors, bridge calls), database queries, table rows, logs sent by your app, crashes, deeplinks, ADB commands and app starts.
- **Switch between the list and the graph** (the **List** and **Graph** chips next to the filter bar, the choice is remembered), see [Graph](#graph).
- **Read the counters**: number of events, sessions, HTTP errors and crashes of what is displayed.
- **Options** (⋮ menu): auto scroll to the newest event, invert the direction of the list, pin the detail panel (it then stays next to the list instead of covering it).
- **Filter**: toggle the sources (the selection is remembered) and search a text (`Ctrl+K` / `Cmd+K` focuses the search field).
- **Open an event**: click a row to open its detail in a side panel (network call, analytics event, log, database query, table row, crash, deeplink, ADB command). App starts and WebView events have nothing to open: the WebViews screen shows them.

---

## Graph

The **Graph** view shows one session at a time on a time axis, with the app start at 0. The filters and the selected event are shared with the list.

- **One lane per type**: App, Network, Database, Analytics, Logs (a lane without event is hidden). The other sources only appear in the list.
- **A network call is one bar**, from its request to its response. The method and an image icon are shown when the bar is wide enough, and a failed call (HTTP 4xx or 5xx) is red. A call still waiting for its response goes up to the last event received.
- **Database queries, analytics events and logs are diamonds**: they have no duration.
- **Events that do not overlap share the same row**, to keep the graph short.

| Action | Result |
|---|---|
| Click a bar or a diamond | Opens the event in the detail panel and selects it in the list too |
| Mouse wheel | Scrolls the lanes |
| `Ctrl` / `Cmd` + wheel, `+` / `-` buttons | Zooms around the pointer |
| `Shift` + wheel, drag | Moves along the time axis |
| **Fit** | Shows the whole session again and follows the new events |
| `‹` / `›` | Previous / next session |

---

## Logs from your app

Besides the events of the other plugins, your app can push its own events into the timeline, to read what happened around a bug: which screen was open, what the user did, what the app lifecycle was, and any milestone of your own. Flocon does not detect anything by itself: your app calls the SDK, usually from a navigation listener.

### Setup

```kotlin
dependencies {
    debugImplementation("io.github.openflocon:flocon-timeline:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-timeline-no-op:LAST_VERSION")
}
```

```kotlin
startFlocon(FloconContext(applicationContext)) {
    install(FloconTimeline)
}
```

A plugin that is not installed does nothing instead of crashing.

### Usage

There are two kinds of calls, never mixed:

- **Typed, without level**: `log(type, value, params)` and the shortcuts `logNavigation`, `logUserAction` and `logLifecycle`.
- **Tagged, with a level**: `log(tag, value, params, level = Info)`. The tag is the type of the event, and `level` is `Debug`, `Info`, `Warning` or `Error`.

`value` is the main text of the row (a screen name for example), `params` is a `Map<String, String>` shown in the detail panel (text only, optional).

```kotlin
Flocon.timelinePlugin.logNavigation("CheckoutScreen", params = mapOf("from" to "CartScreen"))
Flocon.timelinePlugin.logUserAction("Tap on Pay")
Flocon.timelinePlugin.logLifecycle("App moved to background")

// a custom tag, with a level
Flocon.timelinePlugin.log("payment", "Card declined", mapOf("code" to "402"), FloconTimelineLevel.Warning)
```

!!! note
    The reserved types are `navigation`, `userAction` and `lifecycle` (matched without case sensitivity). A tag equal to one of them is shown as that type.

### Navigation listener (Compose)

```kotlin
@Composable
fun NavigationLogger(navController: NavController) {
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, arguments ->
            Flocon.timelinePlugin.logNavigation(
                value = destination.route ?: "unknown",
                params = arguments?.keySet().orEmpty().associateWith { arguments?.get(it).toString() },
            )
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
}
```

### Define your own type

Declare a type once and reuse it, optionally with an extension:

```kotlin
val Billing = FloconTimelineType("billing")

fun FloconTimelinePlugin.logBilling(value: String, params: Map<String, String> = emptyMap()) =
    log(Billing, value, params)

Flocon.timelinePlugin.logBilling("Subscription started", mapOf("sku" to "premium_yearly"))
```

The new type shows up by itself as a value of the Type filter: the desktop does not need an update.

### iOS

The native Swift SDK lives in a separate repository, [FloconSwift](https://github.com/openflocon/FloconSwift). It will get the same API (a `.timeline` plugin and a `TimelineType`) in a separate release, it is not available yet. The wire format is the same, see the [protocol](https://github.com/openflocon/Flocon/blob/main/protocol/timeline.md). A reusable type will look the same in Swift: a constant `TimelineType("billing")` and an extension of the plugin calling `log`.

### On the desktop

- Logs are a new **Log** source, in the list and in the graph (one marker per log, in its own lane).
- Filter them by **Type** (including your custom tags) and by **Level**.
- Click a row to open its detail: value, type, level, params and time.
- The desktop keeps at most 20 000 logs per device and app, the oldest ones are dropped first.

### Limits

- No custom icon or color per tag: custom tags use a default style.
- `params` are text only, not nested.
- No clear button for the logs: they are removed with the device or app data.
- It is not a replacement for [analytics events](analytics.md), which keep their own plugin.

---

## Setup

There is nothing to add for the timeline itself: it is built on the desktop from the events of the other plugins. Each source only shows up once its plugin is installed in your app, see:

- [Network](network.md#setup-integration)
- [Analytics](analytics.md#setup)
- [Databases](database.md#setup)
- [Data Tables](table.md#setup)
- [Crash Reporter](crashreporter.md#setup)
- [Deeplinks](deeplink.md)
- [WebViews](webview.md#setup)
- [Logs from your app](#logs-from-your-app)

!!! tip
    Start with [Getting Started](setup.md) to connect your app to Flocon Desktop.
