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
- **State**: `On`, `Off` or `Custom` (an experiment arm, a value...): the variant is colored with it
- **Variant**: the evaluated variant, a free string (ex: `control`, `treatment`, `on`)
- **Payload**: optional, often JSON, displayed formatted in the right panel when clicking a flag

A selector at the top switches between the providers, and the list can be filtered by name, variant or payload content. The desktop is read only.

---

## Setup

```kotlin
dependencies {
    debugImplementation("io.github.openflocon:flocon-feature-flags:LAST_VERSION")
    releaseImplementation("io.github.openflocon:flocon-feature-flags-no-op:LAST_VERSION")
}
```

Install the plugin when you start Flocon:

```kotlin
startFlocon(FloconContext(applicationContext)) {
    install(FloconFeatureFlags)
}
```

---

## Usage

Register all the flags of a provider. Each call **replaces** the flags previously sent for this provider, and they are sent again automatically when the app reconnects to the desktop.

```kotlin
Flocon.featureFlagsPlugin.register(
    provider = "firebase",
    FloconFeatureFlag(name = "new_checkout", state = FloconFeatureFlagState.Custom, variant = "treatment", payload = """{"buttonColor":"#FF5722"}"""),
    FloconFeatureFlag(name = "dark_mode", state = FloconFeatureFlagState.On, variant = "on"),
    FloconFeatureFlag(name = "onboarding_v2", state = FloconFeatureFlagState.Off, variant = "control"),
)
```

---

## Generic Feature Flags Forwarder

Forward your flags to Flocon each time your provider refreshes them:

```kotlin
class FeatureFlagsRepository(private val remoteConfig: FirebaseRemoteConfig) {

    fun onFlagsActivated() {
        Flocon.featureFlagsPlugin.register(
            provider = "firebase",
            flags = remoteConfig.all.map { (key, value) ->
                FloconFeatureFlag(
                    name = key,
                    state = when (value.asString()) {
                        "true" -> FloconFeatureFlagState.On
                        "false" -> FloconFeatureFlagState.Off
                        else -> FloconFeatureFlagState.Custom
                    },
                    variant = value.asString(),
                )
            },
        )
    }
}
```

---

## AI Agents (MCP)

`list_feature_flags` and `get_feature_flag` let your AI agent read the flags of the app, see [MCP](mcp.md).
