---
title: Timeline
description: Push your own navigation, user action, lifecycle and custom logs into the Flocon timeline.
---

# 🕒 Timeline

The timeline merges the events received by Flocon Desktop into **one chronological list** (and a graph), grouped by day and by app session, so you can see what happened right before a crash or an HTTP error. The logs sent by your app show up in it next to the other sources.

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
