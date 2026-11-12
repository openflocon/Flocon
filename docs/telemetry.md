---
title: Telemetry & Privacy
description: Exactly what usage data Flocon Desktop can send, and only if you opt in.
---

# Telemetry & Privacy

Flocon Desktop can send a small amount of **usage statistics**, **only if you opt in**, so maintainers know which features are actually used and on which platforms. This page lists exactly what is collected.

!!! info "Nothing from your apps is ever sent"
    Flocon is a debugging tool: it sees network calls, databases, preferences and files of the apps you inspect. **None of it leaves your computer.** No URLs, payloads, headers, package names, device names or database content are ever part of an event.

The SDKs in your app (Kotlin, Swift, Flutter) collect and send nothing: telemetry is a Flocon Desktop setting only. The only SDK information that is reported is its kind and version, when an app connects (`device_connected`).

<!-- screenshot: telemetry-settings-privacy | scenario=default | screen=settings | Settings > Privacy with the telemetry level choice -->

## Levels

On first launch, a card asks you to pick a level. **Nothing is sent until you answer**: as long as you haven't chosen, telemetry stays disabled. You can change your choice at any time in **Settings → Privacy**.

| Level | What it means |
| --- | --- |
| **Anonymous** *(opt-in)* | A random identifier is generated in memory at each launch and **never written to disk**. Flocon can count sessions and feature usage, but cannot recognise you across launches. |
| **Full** *(opt-in)* | A random identifier is stored on your computer so returning users can be counted. Switching back to Anonymous or Disabled deletes it. |
| **Disabled** | Nothing is sent. This is also the behaviour until you answer the consent card. |

Telemetry is also fully disabled when:

- the `DO_NOT_TRACK` environment variable is set (any value other than empty, `0` or `false`), following [consoledonottrack.com](https://consoledonottrack.com);
- you build Flocon yourself or use a fork: only official releases contain the analytics key;
- Flocon runs in mock mode (`--mock=<scenario>`).

## Events

| Event | When | Extra properties |
| --- | --- | --- |
| `app_started` | Once per launch, if you opted in before | A snapshot of a few settings, see below |
| `screen_viewed` | A menu entry or a sub-screen is opened, **at most once per screen per launch** | `screen`: a menu entry (e.g. `Network`, `Database`) or `NetworkMocks`, `NetworkBadQuality`, `NetworkDetail` |
| `screen_empty_shown` | The empty state of a page is displayed (nothing to show yet), **at most once per page per launch** | `screen`: the documentation page (`Network`, `Database`, `Timeline`...) |
| `mcp_used` | An MCP tool is called, **at most once per tool per launch** | `tool`: the fixed tool name (e.g. `list_network_calls`), `client`: `claude`, `cursor`, `codex`, `windsurf`, `vscode`, `zed`, `other` or `unknown`. Never the arguments or the results of the call |
| `feature_used` | A bounded list of actions is used (see below). The core action of each screen is sent **at most once per launch** | `feature`, `screen`, and when relevant `value` (`true`/`false`), `source`, `enabled_mocks_count` |
| `setting_changed` | One of a few settings is changed | `setting`, `value` (`true`/`false`, or `graph`/`list`), `screen` |
| `device_connected` | An app with the Flocon SDK connects, **at most once per device and app per launch** (the app relaunches on every build) | `platform` (`android`, `ios`, `desktop`, `other`), `sdk` (`kotlin`, `swift`, `flutter`, `other`, `unknown`), `sdk_version` (`unknown` when it is not a version), `connection_type` (`default`, `wifi`, `unknown`). Only known values are sent. Never the device name, the app name or its package |

### `app_started` properties

Only booleans, counts and fixed values, never a name or a configuration value. A value that can't be read quickly is left out.

| Property | Values |
| --- | --- |
| `timeline_view` | `graph`, `list` |
| `timeline_auto_scroll`, `timeline_screenshots`, `timeline_merge_plugins` | `true`, `false` |
| `mascot_enabled` | `true`, `false` |
| `enabled_mocks_count` | number of enabled network mocks of the selected app (left out when no app is selected yet) |
| `bad_network_enabled` | `true` if a bad network configuration is enabled for the selected app (left out when no app is selected yet) |

### `feature_used` and `setting_changed`

`screen` takes the same values as `screen_viewed`: `NetworkMocks`, `NetworkBadQuality`, `NetworkDetail`, `Timeline`, `Settings`, `Database`, `Deeplinks`, `SharedPreferences`, `FeatureFlags`, `Files`, `AdbCommander`, `TopBar`.

| `feature` | Screen | Notes |
| --- | --- | --- |
| `mock_create_clicked`, `mock_imported`, `mock_exported` | `NetworkMocks` | |
| `mock_toggled` | `NetworkMocks` | `value` = enabled, `enabled_mocks_count` after the change |
| `bad_network_create_clicked` | `NetworkBadQuality` | |
| `bad_network_toggled` | `NetworkBadQuality` | `value` = a configuration is now enabled |
| `detail_open_externally_request`, `detail_open_externally_response`, `detail_share_markdown`, `detail_save_html`, `detail_copy_curl`, `detail_copy_text`, `detail_copy_image`, `detail_save_image`, `detail_diff_with_clipboard`, `detail_json_detail`, `detail_bearer_jwt` | `NetworkDetail` | Only which action, never the content |
| `database_query_executed` | `Database` | Once per launch. Never the query |
| `deeplink_launched` | `Deeplinks` | Once per launch. Never the deeplink |
| `shared_preference_edited` | `SharedPreferences` | Once per launch. Never the key or the value |
| `file_opened` | `Files` | Once per launch. Never the path |
| `adb_command_sent` | `AdbCommander` | Once per launch. Never the command |
| `screenshot_taken`, `video_recorded` | `TopBar` | Once per launch |
| `help_opened` | the documentation page (`Network`, `Database`, `Timeline`... or `Setup`) | `source`: `top_bar`, `empty_state`, `empty_state_no_device` |

| `setting` | Screen | `value` |
| --- | --- | --- |
| `timeline_view` | `Timeline` | `graph`, `list` |
| `timeline_auto_scroll`, `timeline_screenshots`, `timeline_merge_plugins` | `Timeline` | `true`, `false` |
| `mascot_enabled` | `Settings` | `true`, `false` |

Every event also carries: Flocon version, OS name and architecture, the telemetry level, and the per-launch session id.

## Where the data goes

Events are sent to [PostHog Cloud](https://posthog.com) in the **EU region**. No person profiles are created, IP geolocation is disabled, and the PostHog project is configured to discard client IP addresses.

The full implementation lives in [`FloconDesktop/composeApp/.../telemetry`](https://github.com/openflocon/Flocon/tree/main/FloconDesktop/composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/telemetry).

## For contributors

- Add events only through `TelemetryManager.track(...)` and list them in `TelemetryEvents` **and** on this page.
- Never pass data coming from the inspected apps.
- The key is injected at build time from the `POSTHOG_API_KEY` environment variable (a GitHub secret on the release workflow). Without it, the app compiles and telemetry is a no-op. To test locally against your own PostHog project: `POSTHOG_API_KEY=phc_xxx ./gradlew run`.
