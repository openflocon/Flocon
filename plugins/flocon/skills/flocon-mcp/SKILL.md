---
name: flocon-mcp
description: Use when debugging a mobile app (Android, iOS, KMP, Flutter) connected to Flocon Desktop - reading the timeline of what the app did, investigating crashes, inspecting network calls, mocking a response or error, simulating a slow or flaky network, reading app files, analytics events, preferences, feature flags, accessibility events, WebView logs or SQLite databases.
---

# Debugging a mobile app with Flocon

Flocon Desktop receives what happens in the mobile app connected to it and exposes it through the `flocon` MCP server (tools `mcp__flocon__*`).

## Prerequisites

- Flocon Desktop must be running (the MCP server listens on `http://localhost:9042/mcp`, loopback only). Port 9042 is only for MCP; the apps connect to Desktop on 9023 (WebSocket) and 9024 (HTTP).
- The app must be connected to Flocon. If a tool fails to connect or returns no device, ask the user to start Flocon Desktop and the app.

## Discover the tools, do not assume them

The table below lists the tools of the current Flocon Desktop, but the session may expose fewer: an older Desktop has fewer tools, and some clients only expose part of a server's tools (for example `list_images` present in the server `tools/list` but missing from the session). Work from the tools actually available in the session. If a tool you need is missing, say so and name it; do not call another tool as a substitute and present its result as the same thing. A tool available in the session can still return nothing for a platform whose SDK does not send that data (see below).

## Targeting

By default every tool targets the device and app selected in Flocon Desktop. Call `list_devices` to see the others (each has a `connectionType`: `default` for localhost, `wifi` when connected through the connect module), then pass `deviceId` and `packageName`. When nothing is selected in Desktop, or a call fails on the selection, retry with an explicit `deviceId` and `packageName` (the iOS bundle id) taken from `list_devices`.

## When a call fails

Tell the user which case it is, with the evidence, before concluding anything about the app:

| Symptom | Case | Next step |
|---|---|---|
| Connection refused on `localhost:9042` / the MCP server is not connected | Flocon Desktop not running (or MCP disabled) | Ask the user to start Flocon Desktop |
| "No device has been connected to Flocon yet" | No device ever connected | Check the app runs a build where Flocon is started, and the Desktop version is compatible with the SDK (the Swift SDK needs a Desktop with the encrypted `wss` connection) |
| "No device / app is currently selected" | Nothing selected in Desktop | `list_devices`, then pass `deviceId` and `packageName` |
| "App '…' not found", "Device '…' not found or has no app", "several apps" | Unknown or ambiguous target | Use the exact ids from `list_devices` |
| Unknown tool, missing argument, unexpected schema | Desktop version and skill out of sync | Say which tool and which Desktop version the user runs |
| A Kotlin class name in the message (for example `…RepositoryImpl$getDeviceSelectedApp$2`) or a stack-trace-like text | Internal Desktop error | Report it as a Flocon Desktop error with the tool, arguments and message; retry once with an explicit target. Do not present it as a bug of the mobile integration without other evidence |

Do not restart Desktop, clear data or change ports to work around an error unless the user asks.

## Platform coverage

The tools are shared by every SDK, but each SDK sends what it supports. With the Swift SDK (FloconSwift): network (only the instrumented `URLSession`s, plus gRPC with FloconSwiftGrpc), images (from network responses, no new entry on a cache hit), analytics, feature flags, tables, preferences (incl. keychain if registered), files, databases (inspection; query logs only when the app logs them), dashboards, deeplinks, timeline logs and crashes. Not sent by the Swift SDK at the time of writing: WebView events and accessibility events. Screenshots and screen recordings are taken by Desktop on the booted iOS Simulator only. With the Flutter SDK (FloconFlutter): network (only clients wrapped with `withFlocon()`, WebSockets only when logged by hand, mocks and bad network on Dio / `package:http`), images, analytics, feature flags, tables, preferences, files, databases (inspection; query logs only when the app logs them), dashboards, deeplinks, timeline logs and crashes (Dart and Flutter errors only). Not sent by the Flutter SDK: WebView events and accessibility events. An empty result for a feature the app did not wire is not an app bug: say which wiring is missing (see the `flocon-setup` skill).

## What to use for what

| Goal | Tools |
|---|---|
| Understand what the app did around a moment ("before the crash", "at 14:32", "recently"), or know if an event was sent | `get_timeline` first: one narrative of every source with the anomalies first (crash, HTTP errors, ERROR logs, loops, responses over 1 MB). Give nothing to let it choose (the latest crash, otherwise the last 2 minutes) and read `window.reason`; `mode` / `at` / `from` / `to` / `eventId` to choose; `search` to know if an event was sent; `excludeSources: ["database"]` if the database lines are noise. Then use the ids of its lines (`network:<callId>` for `get_network_call`, `crash:<crashId>` for `get_crash_report`) |
| Show the user a zone of the timeline (a crash, an error, a loop, a large call) in Flocon | `show_in_timeline` (`eventId` of `get_timeline`, or a time, or nothing: the latest crash then the main anomaly; `view` graph or list). It selects the device and app, opens the timeline and centers the graph. Flocon is not brought to the front: tell the user to go to the Flocon app. Read `hiddenBy` and the message: if a filter of the user hides the event, say it, the filters are never changed |
| Find a failing or slow call | `list_network_calls`, `search_network_calls` (summaries), then `get_network_call` with a `callId` for headers, bodies and the cURL command |
| Make the app receive a fake response or exception | `create_network_mock` (`fromCallId` starts from a captured call), `update_network_mock`, `list_network_mocks`, `get_network_mock`, `delete_network_mock` |
| Simulate a slow or flaky network (latency, random errors or exceptions on a part of the requests) | `list_bad_network_configs` (the configs of the app and the active one), `create_bad_network_config` (`latencyTriggerProbability` with `minLatencyMs` / `maxLatencyMs`; `errorProbability` with `errors`, each an `httpCode` or an `exceptionClassPath` such as `java.net.SocketTimeoutException`, picked by `weight`; `enabled: true` activates it right away, otherwise it is created inactive), `update_bad_network_config` (only the given fields, `errors` replaces all of them), `set_active_bad_network_config` (one active config per app; without `configId` it turns the simulation off), `delete_bad_network_config` |
| Check a file written by the app | `list_file_roots` first (the root folders announced by the app: the platform defaults, such as files and caches on Android or documents, caches and tmp on iOS, plus the folders registered by the app), then `list_files` (a root `id`, then the absolute path of a sub directory), `read_file` |
| Read a custom dashboard of the app | `list_dashboards`, `get_dashboard` for its elements and values |
| Investigate a crash | `list_crash_reports` for the exception type and message, `get_crash_report` for the full stack trace |
| Debug a WebView (JS console errors, failed page loads, calls from the page to native code) | `list_webview_events` (filters: `kind` console / page / bridge, text, start timestamp); the requests made by the page are not captured yet |
| Check analytics events | `count_analytics_events` for an overview per event name, `list_analytics_events` for the properties, `list_analytics_tables` |
| Check the deeplinks of the app | `list_deeplinks` (declared links and parameters), `list_deeplink_history`, `open_deeplink` (opens a link on the device, fill its parameters first) |
| See the current screen, or keep a visual trace | `take_screenshot` (saves a PNG and returns the image) |
| See what the Flocon desktop app itself displays (the screen the user is looking at in Flocon) | `take_flocon_desktop_screenshot` (returns the image of the visible window, nothing is saved; not the device screen) |
| Read or change the ports of the Flocon servers (websocket 9023, HTTP 9024 by default) | `get_server_ports` (effective, saved, defaults, forced by a launch option), `set_server_ports` (`websocketPort`, `httpPort`, at least one) |
| Show the user something in the Flocon desktop app (a screen, another device / app, the graph or list view of the timeline) | `flocon_navigate_into_desktop_app` (`screen`, `deviceId`, `packageName`, `timelineView`; Flocon is not brought to the front, so tell the user to go to the Flocon app to see it) |
| Reproduce a bug on video | `start_screen_recording`, do the scenario, then `stop_screen_recording` (returns the mp4 path; Android stops by itself after 3 minutes) |
| Show the user the saved screenshots, videos or exports | `open_flocon_folder` (opens the Flocon documents folder, optional `Screenshot` / `ScreenRecord` sub folder) |
| Check SharedPreferences | `list_shared_preferences` (gives the `sharedPreferenceId` and the storage `type`), `get_shared_preference_values` (typed values, optional text filter) |
| Check feature flags | `list_feature_flags` (provider, name, state, variant, filters), `get_feature_flag` for the full payload |
| Check what a screen reader gets (focus order, announcements, content descriptions) | `get_accessibility_state` first (is TalkBack / VoiceOver on, is capture supported), then `list_accessibility_events` (type and text filters), `clear_accessibility_events`; walk the focus order with `move_accessibility_focus` (`next` / `previous`, needs TalkBack on) and read where it is with `get_accessibility_focused_node` |
| Check which images the app loaded | `list_images` (urls and load times). If one is missing, check in order: did a real download happen (cache hits are not captured), is it in `list_network_calls`, is its `Content-Type` an `image/*`, then whether only the Desktop thumbnail fails (Desktop downloads the image from its URL) |
| Check persisted data | `list_databases` (gives the `databaseId`), `list_database_tables` (schema), `query_database`, `list_database_query_logs` (queries run by the app, each with a `transactionId` when it ran in a transaction, plus `nestedTransactionId` / `parentTransactionId` for a savepoint inside it; pass `transactionId` to get only that transaction) |
| Check custom tables | `list_tables` (gives the `tableId`), `list_table_items` (rows, optional text filter) |

## Rules

- Mocks change the app behavior: prefer disabling a mock (`update_network_mock`) over deleting it, and tell the user which mocks you left enabled.
- `query_database` is read only (`SELECT`, `PRAGMA` without value, `EXPLAIN`): always add a `LIMIT`.
- The captured data stays read only: network calls, files, databases, preferences, analytics events, feature flags, crashes, the timeline, tables, dashboards, images and WebView events cannot be modified, deleted or replayed through the tools (`get_dashboard` cannot trigger a button or a field). `read_file` only saves a copy of the file on the computer.
- These tools change the state of the app, the device or Flocon Desktop: use them when the user asks or when the task needs it, and always tell the user what you changed.
  - App behavior: `create_network_mock`, `update_network_mock`, `delete_network_mock`, `create_bad_network_config`, `update_bad_network_config`, `set_active_bad_network_config`, `delete_bad_network_config`.
  - Device: `open_deeplink`, `move_accessibility_focus`, `take_screenshot`, `start_screen_recording`, `stop_screen_recording` (the screenshots and videos are saved in the Flocon documents folder).
  - Flocon Desktop and the computer: `set_server_ports`, `show_in_timeline`, `flocon_navigate_into_desktop_app`, `clear_accessibility_events` (deletes the accessibility events received from the app), `open_flocon_folder`.
- Bad network configs change the app behavior as soon as one is active, and an update of the active config is applied right away: tell the user which config you left active, if any. To stop the simulation, prefer `set_active_bad_network_config` without `configId` (the configs are kept) over deleting a config.
- `get_timeline` only reads. The times are those of the phone, not the computer. Read `window.reason`, `truncated` and `notes` before concluding: an empty answer can mean the window was wrong, not that nothing happened.
- `show_in_timeline` changes what the user sees and selected in Flocon (device, app, the timeline view saved in their preferences) but never their filters. The graph draws network, database and analytics events only: for a crash or another source, say that its row is in the list (`view: list`). The request is only registered: it does not tell that the user saw it.
- `flocon_navigate_into_desktop_app` changes what the user selected in Flocon (device, app) and saves the timeline view in their preferences: use it when asked to show something, say what you changed, and ask them to go to the Flocon app.
- `set_server_ports` restarts the websocket and HTTP servers and disconnects every device until the SDKs reconnect (they must use the new ports): only use it when asked, call `get_server_ports` first, always tell the user the ports you changed (old and new) and that the devices are disconnected. A port forced by a launch option cannot be changed (error). The MCP port (9042) is not affected.
- `take_screenshot`, `start_screen_recording` and `open_flocon_folder` act on the device / computer (iOS: the booted Simulator only): use them when asked or when seeing the screen helps. `take_screenshot`, `start_screen_recording` and `open_deeplink` use adb on Android, so they fail for a device whose `connectionType` (in `list_devices`) is `wifi`.
- `take_flocon_desktop_screenshot` only reads: it captures the Flocon main window as displayed, with its open dialogs, menus and popups (separate windows such as About are not included).
- Start from summaries and fetch details only for the calls you need, bodies can be large.
