---
title: Feature Flags Viewer
description: List and search the feature flags evaluated by your app, with their variant and payload.
---

# 🚩 Feature Flags Viewer

Flocon displays the **feature flags** evaluated by your application: for each flag, its provider, its variant and its optional payload.
No dependency on a feature flag SDK: your app forwards the flags it already evaluated, from Firebase Remote Config, Statsig, LaunchDarkly, an in-house system...

---

## Overview

- **Provider**: where the flag comes from (ex: `firebase`, `statsig`)
- **Name**: the flag key
- **Variant**: the evaluated variant, a free string (ex: `control`, `treatment`, `on`)
- **Payload**: optional, often JSON, displayed formatted in the right panel when clicking a flag

The list can be filtered by provider, name, variant or payload content. The desktop is read only.

---

## Usage

Register all the flags of a provider. Each call **replaces** the flags previously sent for this provider, and they are sent again automatically when the app reconnects to the desktop.

```kotlin
floconFeatureFlags("firebase").register(
    FloconFeatureFlag(name = "new_checkout", variant = "treatment", payload = """{"buttonColor":"#FF5722"}"""),
    FloconFeatureFlag(name = "dark_mode", variant = "on"),
)
```

---

## Generic Feature Flags Forwarder

Forward your flags to Flocon each time your provider refreshes them:

```kotlin
class FeatureFlagsRepository(private val remoteConfig: FirebaseRemoteConfig) {

    fun onFlagsActivated() {
        floconFeatureFlags("firebase").register(
            remoteConfig.all.map { (key, value) ->
                FloconFeatureFlag(
                    name = key,
                    variant = value.asString(),
                )
            }
        )
    }
}
```

---

## AI Agents (MCP)

`list_feature_flags` and `get_feature_flag` let your AI agent read the flags of the app, see [MCP](mcp.md).
