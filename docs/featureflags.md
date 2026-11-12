---
title: Feature Flags Viewer
description: List and search the feature flags evaluated by your app, with their variant and payload.
---

# 🚩 Feature Flags Viewer

Flocon displays the **feature flags** evaluated by your app: for each flag, its provider, its variant and its optional payload. There is no dependency on a feature flag SDK: your app forwards the flags it already evaluated, from Firebase Remote Config, Statsig, LaunchDarkly, an in-house system...

<!-- screenshot: featureflags-list | scenario=doc-featureflags | screen=featureflags | the provider selector, the colored variant toggles with their counts, and the list of flags with state and variant -->

For each flag the desktop shows:

- **Provider**: where the flag comes from (for example `firebase`, `statsig`).
- **Name**: the flag key.
- **State**: `On`, `Off` or `Custom` (an experiment arm, a value...). The variant is colored with it, and each custom variant gets its own color.
- **Variant**: the evaluated variant, a free string (for example `control`, `treatment`, `on`).
- **Payload**: optional, often JSON, displayed formatted in the right panel when you click a flag.

## Platform support

| | Android | KMP (iOS / JVM / Wasm) | Swift (iOS / macOS) | Flutter |
|---|:---:|:---:|:---:|:---:|
| Register flags per provider | ✅ | ✅ | ✅ | ✅ |
| Change a flag from the desktop | ❌ | ❌ | ❌ | ❌ |

The viewer is **read only** on every platform: the desktop never sends a value back to the app. To try another variant, change it in your provider (or in your own code) and register the flags again.

## Setup

=== "Kotlin"

    Add the module (the `-no-op` twin keeps the same API in release builds, see [Release builds](setup.md#release-builds)):

    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon-feature-flags:2.0.0")
        releaseImplementation("io.github.openflocon:flocon-feature-flags-no-op:2.0.0")
    }
    ```

    Install the plugin when you start Flocon:

    ```kotlin
    startFlocon(FloconContext(applicationContext)) {
        install(FloconFeatureFlags)
    }
    ```

=== "Swift"

    Feature flags are part of the `Flocon` product. Install the plugin:

    ```swift
    import Flocon

    startFlocon { flocon in
        flocon.install(.featureFlags)
    }
    ```

    `Flocon.initialize()` installs every plugin.

=== "Flutter"

    ```bash
    flutter pub add flocon
    ```

    The feature flags plugin is part of the `flocon` package and is always available: no `install(...)` is needed, you only have to start Flocon (see [Setup](setup.md)). Registering does nothing before Flocon is started, so you can leave the calls in your code.

    ```dart
    import 'package:flocon/flocon.dart';

    await startFlocon((flocon) => flocon..connect(host: 'localhost'));
    ```

## Usage

Register **all the flags of a provider** at once, one provider per feature flag system. Each call **replaces** the flags previously sent for this provider, and they are sent again automatically when the app reconnects to the desktop.

`state` tells the desktop how to color the variant: on, off, or custom for an experiment arm or a value.

=== "Kotlin"

    ```kotlin
    import io.github.openflocon.flocon.Flocon
    import io.github.openflocon.flocon.featureflags.featureFlagsPlugin
    import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlag
    import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlagState

    Flocon.featureFlagsPlugin.register(
        provider = "firebase",
        FloconFeatureFlag(name = "new_checkout", state = FloconFeatureFlagState.Custom, variant = "treatment", payload = """{"buttonColor":"#FF5722"}"""),
        FloconFeatureFlag(name = "dark_mode", state = FloconFeatureFlagState.On, variant = "on"),
        FloconFeatureFlag(name = "onboarding_v2", state = FloconFeatureFlagState.Off, variant = "control"),
    )
    ```

    `register` also takes a `List<FloconFeatureFlag>`.

=== "Swift"

    ```swift
    Flocon.featureFlagsPlugin.sender("firebase").register(
        FloconFeatureFlag(name: "new_checkout", state: .custom, variant: "treatment", payload: #"{"steps": 3}"#),
        FloconFeatureFlag(name: "dark_mode", state: .on, variant: "on"),
        FloconFeatureFlag(name: "onboarding_v2", state: .off, variant: "control")
    )
    ```

    `register` also takes an array. Keep the sender (`let firebase = Flocon.featureFlagsPlugin.sender("firebase")`) to reuse it.

=== "Flutter"

    ```dart
    Flocon.featureFlags.register('firebase', [
      FloconFeatureFlag(name: 'new_checkout', state: FloconFeatureFlagState.custom, variant: 'treatment', payload: '{"steps": 3}'),
      FloconFeatureFlag(name: 'dark_mode', state: FloconFeatureFlagState.on, variant: 'on'),
      FloconFeatureFlag(name: 'onboarding_v2', state: FloconFeatureFlagState.off, variant: 'control'),
    ]);
    ```

### Generic feature flags forwarder

Forward your flags to Flocon each time your provider refreshes them:

=== "Kotlin"

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

=== "Swift"

    ```swift
    func onFlagsRefreshed(_ values: [String: String]) {
        Flocon.featureFlagsPlugin.sender("remote_config").register(
            values.map { key, value in
                FloconFeatureFlag(
                    name: key,
                    state: value == "true" ? .on : value == "false" ? .off : .custom,
                    variant: value
                )
            }
        )
    }
    ```

=== "Flutter"

    ```dart
    void onFlagsRefreshed(Map<String, String> values) {
      Flocon.featureFlags.register('remote_config', [
        for (final entry in values.entries)
          FloconFeatureFlag(
            name: entry.key,
            state: switch (entry.value) {
              'true' => FloconFeatureFlagState.on,
              'false' => FloconFeatureFlagState.off,
              _ => FloconFeatureFlagState.custom,
            },
            variant: entry.value,
          ),
      ]);
    }
    ```

## In Flocon Desktop

Open **Feature flags** in the sidebar.

- A **provider selector** at the top switches between the providers your app registered.
- The **filter** field searches the flags by name, variant or payload content.
- The variants present in the list are shown at the top as **colored toggles with their number of flags**: click one to show or hide that variant quickly (`on`, `off`, `control`, `treatment`...).
- Click a flag to see its state, variant and **payload** (formatted when it is JSON) in the right panel, where the payload can be copied.

<!-- screenshot: featureflags-detail | scenario=doc-featureflags | screen=featureflags | a flag selected with its payload shown formatted in the right panel, one variant toggle turned off -->

## With AI agents (MCP)

- `list_feature_flags`: the flags registered by the app, with their provider, state and variant. Filter by provider or text.
- `get_feature_flag`: one flag with its state, variant and full payload.

> "Which feature flags are in a custom variant right now, and what is the payload of `new_checkout`?"

See the [MCP cookbook](mcp-cookbook.md) for full workflows.

## Limitations

- Read only: the desktop cannot override a flag in the app. See [Platform support](#platform-support).
