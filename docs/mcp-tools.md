---
title: MCP Tools Reference
description: Every tool of the Flocon MCP server, grouped by area, with what it does, whether it changes something, and its parameters.
---

# 🧰 MCP Tools Reference

The Flocon MCP server exposes **57 tools**. This page lists all of them, by area. For the setup of your agent, see [Overview & Setup](mcp.md). For tools chained together on real problems, see the [Cookbook](mcp-cookbook.md).

Your agent discovers the tools and their parameters by itself (the server describes each one), so you rarely need this page to *use* them. It is useful to know what an agent can and cannot do, and to write precise prompts.

## Legend

| Badge | Meaning |
|---|---|
| :material-eye: **Read-only** | Reads what Flocon captured. Nothing changes. |
| :material-pencil: **Side effect** | Changes something: the behavior of your app, the device, Flocon Desktop or your computer. The kind of change is written next to the badge. |

In the parameter tables, **Req.** is `✅` when the parameter is required. Types are JSON types: `string`, `integer`, `number`, `boolean`, `array`, `object`. Limits and defaults come from the tool schema.

## Targeting: `deviceId` and `packageName`

Most tools act on **one app on one device**. By default, that is the device and app currently selected in Flocon Desktop. To act on another one, call [`list_devices`](#list_devices) then pass `deviceId` and `packageName`.

Tools that take these two optional parameters are marked **Targets: device / app** and the two parameters are not repeated in their tables.

| You pass | Flocon targets |
|---|---|
| Nothing | The device and app selected in Flocon Desktop. If nothing is selected, the call fails and lists the available targets. |
| `packageName` only | That app on the selected device, otherwise on the device where it was seen most recently. |
| `deviceId` only | The app selected on that device, otherwise its only app. If the device has several apps, the call fails and lists their package names. |
| Both | Exactly that app on that device. |

`packageName` is the Android package name or the iOS bundle id.

!!! note "Wi-Fi devices"
    A device connected over Wi-Fi has `connectionType: wifi` in `list_devices`. Tools that use `adb` (screenshot, recording, deeplink) are not available for it.

### Platform notes

- **Android**: tools that act on the device (screenshot, recording, deeplink, accessibility focus) use `adb`.
- **iOS**: screenshot, recording and deeplink use the **booted Simulator**. Physical iOS devices are not supported for these.
- **Data tools** return what the SDK of your app sends. If your SDK does not send a kind of data, the tool is still there but returns nothing. See the [platform support matrix](index.md#platform-support).

---

## Devices

### `list_devices` { #list_devices }

:material-eye: **Read-only**

Lists the devices and apps known by Flocon, the one currently selected in Flocon Desktop (used by default by the other tools), whether each app is connected, and each device's `connectionType` (`default` or `wifi`). **Parameters:** none.

---

## Network

### `list_network_calls`

:material-eye: **Read-only** · **Targets: device / app**

Lists the network calls (HTTP, GraphQL, gRPC, WebSocket events), newest first, as summaries (no headers, no bodies). By default only the current app session (since the last launch).

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `text` | string | | Full text filter: url, method, status, duration, bodies. |
| `urlContains` | string | | Only calls whose url contains this text (case insensitive). |
| `methods` | array of string | | Only these methods, e.g. `["GET", "POST"]`. |
| `statusCodes` | array of integer | | Only these HTTP status codes, e.g. `[401, 500]`. |
| `onlyErrors` | boolean | | Only failed calls: exceptions, HTTP code >= 400, GraphQL errors, gRPC status != OK. Default `false`. |
| `type` | string | | `http`, `graphql`, `grpc` or `websocket`. |
| `sinceTimestamp` | integer | | Only calls started after this epoch timestamp (ms). |
| `includeOldSessions` | boolean | | Include previous app launches. Default `false`. |
| `order` | string | | `newest_first` (default) or `oldest_first`. |
| `limit` | integer | | 1 to 500. Default 50. |

### `get_network_call`

:material-eye: **Read-only** · **Targets: device / app**

Full details of one call: request and response headers and bodies, GraphQL query, gRPC status, error, timing, and the equivalent **cURL command**.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `callId` | string | ✅ | Id from `list_network_calls` or `search_network_calls`. |
| `maxBodyChars` | integer | | Bodies longer than this are truncated. 100 to 200000. Default 20000. |
| `includeCurl` | boolean | | Include the cURL command. Default `true`. |

### `search_network_calls`

:material-eye: **Read-only** · **Targets: device / app**

Searches a text in the headers, bodies, url, method and status of the captured calls, including previous app sessions by default. Returns summaries, newest first.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `query` | string | ✅ | Text to search, case insensitive. |
| `scopes` | array of string | | Where to search. Default: everywhere. |
| `includeOldSessions` | boolean | | Default `true`. |
| `limit` | integer | | 1 to 500. Default 50. |

---

## Mocks

Mocks make the app receive a fake response (or throw an exception) instead of calling the server. They apply immediately when the app is connected to Flocon. See [Mocking](mocking.md).

### `list_network_mocks`

:material-eye: **Read-only** · **Targets: device / app**

Lists the mocks of the app, including the ones shared with all apps. Bodies are not returned.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `onlyEnabled` | boolean | | Only enabled mocks. Default `false`. |

### `get_network_mock`

:material-eye: **Read-only**

All the details of a mock, including the mocked body and headers.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `mockId` | string | ✅ | Id from `list_network_mocks`. |
| `maxBodyChars` | integer | | Truncation limit. 100 to 200000. Default 20000. |

### `create_network_mock`

:material-pencil: **Side effect: app behavior** · **Targets: device / app**

Creates a mock. Start from a captured call with `fromCallId` (url, method and response are copied), then override what you need, for example only `httpCode` to simulate a server error.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `fromCallId` | string | | Id of a captured call used as a template. |
| `urlPattern` | string | | Regex that must match the whole url, query included. E.g. `.*/users/[0-9]+`. |
| `method` | string | | HTTP method, or `*` for any. |
| `displayName` | string | | Name displayed in Flocon. |
| `httpCode` | integer | | Status of the mocked response, 100 to 599. Default 200. |
| `body` | string | | Body of the mocked response. |
| `mediaType` | string | | Media type of the body. |
| `headers` | object | | Headers of the mocked response, e.g. `{"Cache-Control": "no-cache"}`. |
| `delayMs` | integer | | Delay before the response, in ms. Default 0. |
| `exceptionClassPath` | string | | Instead of a response, the request throws this exception, e.g. `java.net.SocketTimeoutException`. |
| `enabled` | boolean | | Whether the mock is active. Default `true`. |
| `shared` | boolean | | Apply to all devices and apps. Default `false`. |

### `update_network_mock`

:material-pencil: **Side effect: app behavior** · `deviceId` / `packageName` *move* the mock

Changes only the given fields. Use `enabled` to enable or disable it. Giving `exceptionClassPath` turns it into an exception mock, giving `httpCode`, `body`, `mediaType` or `headers` turns it into a body mock.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `mockId` | string | ✅ | Id from `list_network_mocks`. |
| `urlPattern`, `method`, `displayName`, `httpCode`, `body`, `mediaType`, `headers`, `delayMs`, `exceptionClassPath` | | | Same as `create_network_mock`. |
| `enabled` | boolean | | Enable or disable the mock. |
| `shared` | boolean | | Share with all apps, or only one app. |
| `deviceId`, `packageName` | string | | Moves the mock to this device / app. By default it stays on its app. |

### `delete_network_mock`

:material-pencil: **Side effect: app behavior**

Deletes a mock. To stop it temporarily, prefer `update_network_mock` with `enabled: false`.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `mockId` | string | ✅ | Id from `list_network_mocks`. |

### `list_websocket_clients`

:material-eye: **Read-only** · **Targets: device / app**

Lists the ids of the app's WebSockets that accept injected messages. Only the Android SDK (OkHttp `FloconWebSocket`) registers them for now. **Parameters:** none besides targeting.

### `send_websocket_message`

:material-pencil: **Side effect: app behavior** · **Targets: device / app**

Injects a text message into a WebSocket of the app, as if it was received from the server. Android OkHttp only for now.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `websocketId` | string | ✅ | Id from `list_websocket_clients`. |
| `message` | string | ✅ | Text received by the app, e.g. a JSON payload. |

---

## Bad network

A bad network config simulates a poor connection: a part of the requests are delayed, a part fail with an error response or an exception. Only one config is active at a time. See [Bad network](bad-quality.md).

### `list_bad_network_configs`

:material-eye: **Read-only** · **Targets: device / app**

Lists the configs of the app and which one is active. **Parameters:** none besides targeting.

### `create_bad_network_config`

:material-pencil: **Side effect: app behavior** · **Targets: device / app**

Creates a config. Not active until `enabled: true` or `set_active_bad_network_config`. For example, a slow network is `latencyTriggerProbability: 1`, `minLatencyMs: 2000`, `maxLatencyMs: 5000`.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `name` | string | ✅ | Name displayed in Flocon. |
| `latencyTriggerProbability` | number | | Chance from 0 to 1 that a request is delayed. Default 0. |
| `minLatencyMs` | integer | | Minimum added latency, in ms. Default 0. |
| `maxLatencyMs` | integer | | Maximum added latency, in ms. Default 0. |
| `errorProbability` | number | | Chance from 0 to 1 that a request fails (needs at least one error). Default 0. |
| `errors` | array of object | | Possible errors, one is picked by weight. Each: `weight` (default 1), `httpCode` **or** `exceptionClassPath`, `body`, `contentType`. |
| `enabled` | boolean | | Also activates it now, deactivating the others. Default `false`. |

### `update_bad_network_config`

:material-pencil: **Side effect: app behavior** · **Targets: device / app**

Changes only the given fields. An update of the active config applies right away. To activate or deactivate, use `set_active_bad_network_config`.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `configId` | string | ✅ | Id from `list_bad_network_configs`. |
| `name`, `latencyTriggerProbability`, `minLatencyMs`, `maxLatencyMs`, `errorProbability` | | | Same as `create_bad_network_config`. |
| `errors` | array of object | | Replaces all the existing errors. |

### `set_active_bad_network_config`

:material-pencil: **Side effect: app behavior** · **Targets: device / app**

Activates a config (the others are deactivated), or **turns the simulation off** when `configId` is omitted.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `configId` | string | | Config to activate. Omit to turn the simulation off. |

### `delete_bad_network_config`

:material-pencil: **Side effect: app behavior** · **Targets: device / app**

Deletes a config. If it was active, the simulation is turned off.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `configId` | string | ✅ | Id from `list_bad_network_configs`. |

---

## Database

The app must be connected to Flocon. Only read-only SQL runs. See [Database](database.md).

### `list_databases`

:material-eye: **Read-only** · **Targets: device / app**

Lists the SQLite databases of the app (Room, SQLDelight...), with the `databaseId` used by the other database tools. **Parameters:** none besides targeting.

### `list_database_tables`

:material-eye: **Read-only** · **Targets: device / app**

Schema of a database: tables, columns, types, primary keys.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `databaseId` | string | ✅ | Id from `list_databases`. |

### `query_database`

:material-eye: **Read-only** · **Targets: device / app**

Runs a read-only SQL query (`SELECT`, `PRAGMA` without value, `EXPLAIN`) and returns the rows. Statements that modify the database are refused. Add a `LIMIT`: the app sends all the rows.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `databaseId` | string | ✅ | Id from `list_databases`. |
| `sql` | string | ✅ | SQLite query, e.g. `SELECT * FROM users WHERE id = 3`. |
| `maxRows` | integer | | 1 to 1000. Default 100. |
| `maxCellChars` | integer | | Longer values are truncated. 10 to 100000. Default 1000. |

### `list_database_query_logs`

:material-eye: **Read-only** · **Targets: device / app**

Lists the SQL queries executed by the app, newest first, with their arguments. Only available when the app logs its queries to Flocon (see [query logging](database.md#real-time-query-logging)). Each query has a `transactionId` when it ran in a transaction.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `databaseId` | string | ✅ | Id from `list_databases`. |
| `text` | string | | Only queries containing this text, in the SQL or the arguments. |
| `includeTransactions` | boolean | | Include BEGIN / COMMIT / ROLLBACK. Default `false`. |
| `includeOldSessions` | boolean | | Include previous app launches. Default `false`. |
| `transactionId` | string | | Only the queries of this transaction. |
| `limit` | integer | | 1 to 1000. Default 100. |

---

## Preferences

See [Preferences](sharedpref.md).

### `list_shared_preferences`

:material-eye: **Read-only** · **Targets: device / app**

Lists the SharedPreferences files (Android) and UserDefaults suites (iOS), with their `sharedPreferenceId` and storage `type` (SharedPreferences, DataStore, UserDefaults, Keychain...). **Parameters:** none besides targeting.

### `get_shared_preference_values`

:material-eye: **Read-only** · **Targets: device / app**

Keys and typed values of one file, as stored.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `sharedPreferenceId` | string | ✅ | Id from `list_shared_preferences`. |
| `text` | string | | Only entries containing this text in the key or value. |
| `limit` | integer | | 1 to 1000. Default 100. |

### `set_shared_preference_value`

:material-pencil: **Side effect: app data (destructive)** · **Targets: device / app**

Edits the value of **one existing key**. Keys cannot be created or removed. It is the only tool that edits captured data. It returns the previous value and whether the app confirmed the edit. The server asks agents to read the value first and to get your confirmation.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `sharedPreferenceId` | string | ✅ | Id from `list_shared_preferences`. |
| `key` | string | ✅ | An existing key. |
| `value` | string | ✅ | New value, as text, exactly of the right type: `"12"` for an int, `"true"` for a boolean, a JSON array of strings for a `string_set`. |
| `type` | string | | `string`, `int`, `long`, `float`, `boolean` or `string_set`. Default: the type of the current value. |

---

## Files

Browse the app's sandbox. The app must be connected to Flocon, otherwise the last known content is returned. See [Files](files.md).

### `list_file_roots`

:material-eye: **Read-only** · **Targets: device / app**

Lists the root folders announced by the app (id, name, absolute path): the platform defaults (files and caches on Android; documents, caches and tmp on iOS) and the folders registered by the app. **Parameters:** none besides targeting.

### `list_files`

:material-eye: **Read-only** · **Targets: device / app**

Lists a directory: name, absolute path, size, last modification.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `path` | string | | A root folder id from `list_file_roots`, or the absolute path of a directory. Default: the first root. |

### `read_file`

:material-eye: **Read-only** · **Targets: device / app**

Downloads a file: text files (json, xml, logs...) as text, images as images, only metadata for other binary files. A copy is saved on your computer (`localPath`); the file in the app is not touched.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `path` | string | ✅ | Absolute path, from `list_files`. |
| `maxChars` | integer | | Text longer than this is truncated. 100 to 200000. Default 20000. |

---

## Images

See [Images](image.md).

### `list_images`

:material-eye: **Read-only** · **Targets: device / app**

Images loaded by the app (url, load time), newest first. Only the url is known, not the image content.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `text` | string | | Only images whose url contains this text. |
| `sinceTimestamp` | integer | | Only images loaded after this epoch timestamp (ms). |
| `includeHeaders` | boolean | | Include the response headers. Default `false`. |
| `order` | string | | `newest_first` (default) or `oldest_first`. |
| `limit` | integer | | 1 to 500. Default 50. |

---

## Analytics

See [Analytics](analytics.md).

### `list_analytics_tables`

:material-eye: **Read-only** · **Targets: device / app**

Lists the analytics tables: one per analytics SDK or tracker sending events to Flocon. **Parameters:** none besides targeting.

### `list_analytics_events`

:material-eye: **Read-only** · **Targets: device / app**

Events with their properties, newest first. By default only the current app session, from all tables.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `analyticsTableId` | string | | Only this table (from `list_analytics_tables`). |
| `eventNames` | array of string | | Only events with one of these exact names. |
| `text` | string | | Only events containing this text in name, date or properties. |
| `sinceTimestamp` | integer | | Only events after this epoch timestamp (ms). |
| `includeOldSessions` | boolean | | Default `false`. |
| `order` | string | | `newest_first` (default) or `oldest_first`. |
| `limit` | integer | | 1 to 500. Default 50. |

### `count_analytics_events`

:material-eye: **Read-only** · **Targets: device / app**

Number of events per event name, the most frequent first. Same filters as `list_analytics_events`: `analyticsTableId`, `eventNames`, `text`, `sinceTimestamp`, `includeOldSessions` (no `order`, no `limit`).

---

## Feature flags

See [Feature flags](featureflags.md).

### `list_feature_flags`

:material-eye: **Read-only** · **Targets: device / app**

Flags with provider, name, state (on, off or custom), evaluated variant and a payload preview.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `provider` | string | | Only this provider, e.g. `firebase`. |
| `query` | string | | Only flags whose name, variant or payload contains this text. |
| `limit` | integer | | 1 to 500. Default 100. |

### `get_feature_flag`

:material-eye: **Read-only** · **Targets: device / app**

One flag with its state, variant and full payload.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `provider` | string | ✅ | Provider of the flag (from `list_feature_flags`). |
| `name` | string | ✅ | Name of the flag. |

!!! note
    Agents read flags, they do not change them. To flip a flag, use the Flocon Desktop screen or your provider.

---

## Tables

See [Tables](table.md).

### `list_tables`

:material-eye: **Read-only** · **Targets: device / app**

Lists the custom tables sent by the app (Table plugin), with their `tableId`. **Parameters:** none besides targeting.

### `list_table_items`

:material-eye: **Read-only** · **Targets: device / app**

Rows of a table, newest first, each row being a column to value map.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `tableId` | integer | ✅ | Id from `list_tables`. |
| `text` | string | | Only rows containing this text in a value. |
| `order` | string | | `newest_first` (default) or `oldest_first`. |
| `limit` | integer | | 1 to 500. Default 50. |

---

## Dashboards

See [Dashboard](dashboard.md).

### `list_dashboards`

:material-eye: **Read-only** · **Targets: device / app**

Ids of the dashboards sent by the app, and the one selected in Flocon Desktop. **Parameters:** none besides targeting.

### `get_dashboard`

:material-eye: **Read-only** · **Targets: device / app**

A dashboard with its containers and elements (texts, values, check boxes, text fields, buttons), as the app last sent it. Nothing is triggered: buttons and fields cannot be used by an agent.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `dashboardId` | string | | Id from `list_dashboards`. Default: the dashboard selected in Flocon Desktop. |

---

## Deeplinks

See [Deeplinks](deeplink.md).

### `list_deeplinks`

:material-eye: **Read-only** · **Targets: device / app**

Deeplinks declared by the app (link, label, description, parameters) and the variables their parameters can use. **Parameters:** none besides targeting.

### `list_deeplink_history`

:material-eye: **Read-only** · **Targets: device / app**

Deeplinks recently opened from Flocon, with the parameters used. **Parameters:** none besides targeting.

### `open_deeplink`

:material-pencil: **Side effect: device** · **Targets: device / app**

Opens a deeplink in the app: `adb` on Android, the booted Simulator on iOS. Not available for Android devices connected over Wi-Fi. Fill all the parameters of the link first (see `list_deeplinks`).

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `link` | string | ✅ | Full deeplink with its parameters filled, e.g. `myapp://profile/42?tab=posts`. |
| `deeplinkId` | integer | | Id of a declared deeplink, to keep its label in the history. |
| `saveIntoHistory` | boolean | | Add to the Flocon history. Default `true`. |

---

## Timeline

See [Timeline](timeline.md).

### `get_timeline`

:material-eye: **Read-only** · **Targets: device / app**

What the app did in a window of time, **above the network**: every source (network, database, analytics, crashes, deeplinks, logs, accessibility, WebView...) in one narrative. One line per event with an id (`source:id`), repetitions folded (`12× GET /feed in 3 s`), anomalies first (crash, HTTP errors, ERROR logs, calls in a loop, large responses). Give nothing and it anchors on the latest crash, otherwise on the last 2 minutes of the latest session, and the answer says which window and why. The times are those of the phone.

Each line id works with the matching tool: `network:<callId>` with `get_network_call`, `crash:<crashId>` with `get_crash_report`.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `mode` | string | | `recent` (last `minutes`), `session`, `before_crash`, `at`, `range`. Omit to let the tool choose. |
| `at` | string | | Time for mode `at`: epoch ms, ISO-8601 with an offset (`2026-11-03T14:32:00+01:00`), or `HH:mm[:ss]`. |
| `from` | string | | Start of mode `range`, same formats. |
| `to` | string | | End of mode `range`, same formats. Defaults to the last event. |
| `eventId` | string | | An event id of a previous answer (`network:abc`): reads around it. Not with `mode`, `at`, `from`, `to`. |
| `aroundSeconds` | integer | | Half width of the window for `at` and `eventId`. Default 30. |
| `minutes` | integer | | Length for mode `recent`. Default 5. |
| `search` | string | | Text to look for in the window (name, url, query...). Matches are never cut, and `found` tells if the event was sent. |
| `searchSource` | string | | Only search in this source. |
| `sources` | array of string | | Only read these sources: `network`, `database`, `analytics`, `accessibility`, `webview`, `crash`, `table`, `deeplink`, `adb`. |
| `excludeSources` | array of string | | Leave these sources out, e.g. `["database"]`. |
| `maxLines` | integer | | 1 to 400. Default 150. Database lines are the first dropped when capped. |

### `show_in_timeline`

:material-pencil: **Side effect: Flocon Desktop** · **Targets: device / app**

Shows a zone of the timeline in the Flocon Desktop window for you: opens the timeline, selects the device and app, selects an event and centers the graph on its zone. Flocon is not brought to the front, so the agent asks you to go to the Flocon window. Your filters are never changed: if one hides the event, the answer says which. The graph draws network, database and analytics events only: a crash is shown with its zone, and its row is in the list view.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `eventId` | string | | An event id of `get_timeline`. Not with `mode`, `at`, `from`, `to`. |
| `mode`, `at`, `from`, `to` | | | Same as `get_timeline`. |
| `aroundSeconds` | integer | | Half width of the zone shown. Default 30. |
| `view` | string | | `graph` (default) or `list`. Changes the timeline view saved in your preferences. |

With nothing given, it shows the latest crash, otherwise the main anomaly of the last 2 minutes.

---

## Crashes

See [Crash reporter](crashreporter.md).

### `list_crash_reports`

:material-eye: **Read-only** · **Targets: device / app**

Crashes reported by the app (exception type and message), newest first.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `text` | string | | Only crashes containing this text in the exception type, message or stack trace. |
| `sinceTimestamp` | integer | | Only crashes after this epoch timestamp (ms). |
| `order` | string | | `newest_first` (default) or `oldest_first`. |
| `limit` | integer | | 1 to 200. Default 20. |

### `get_crash_report`

:material-eye: **Read-only** · **Targets: device / app**

Full details of a crash, including its stack trace.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `crashId` | string | ✅ | Id from `list_crash_reports`. |

---

## Accessibility

What TalkBack and VoiceOver receive. See [Accessibility](accessibility.md). The Swift and Flutter SDKs do not send accessibility events yet.

### `list_accessibility_events`

:material-eye: **Read-only** · **Targets: device / app**

Accessibility events (focus, announcements, content changes) with text, content description, class and screen, newest first.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `type` | string | | Only this type (case insensitive), e.g. `VIEW_FOCUSED`, `ANNOUNCEMENT`. |
| `text` | string | | Only events containing this text in text, description, class or screen. |
| `limit` | integer | | 1 to 500. Default 50. |

### `get_accessibility_state`

:material-eye: **Read-only** · **Targets: device / app**

Whether automatic capture is supported and enabled, whether a screen reader is running, touch exploration, and the running services. Use it when `list_accessibility_events` is empty. **Parameters:** none besides targeting.

### `get_accessibility_focused_node`

:material-eye: **Read-only** · **Targets: device / app**

Asks the device which element TalkBack is on: text, content description, class, bounds, position in the focus order. **Android only**, needs TalkBack on. **Parameters:** none besides targeting.

### `move_accessibility_focus`

:material-pencil: **Side effect: device** · **Targets: device / app**

Moves the accessibility focus to the next or previous element, like a TalkBack swipe, and returns the element it landed on. **Android only**, needs TalkBack on.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `direction` | string | ✅ | `next` or `previous`. It does not wrap around. |

### `clear_accessibility_events`

:material-pencil: **Side effect: Flocon data (destructive)** · **Targets: device / app**

Deletes the accessibility events received from the app, to isolate the events of the next interaction. **Parameters:** none besides targeting.

---

## WebView

### `list_webview_events`

:material-eye: **Read-only** · **Targets: device / app**

What happened in the app's WebViews, newest first: JS console messages, page events (loads, navigation, errors) and calls from the page to a native object (bridge). Requests made by the page are in the network tools. The Swift and Flutter SDKs do not send WebView events yet. See [WebView](webview.md).

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `kind` | string | | `console`, `page` or `bridge`. All by default. |
| `text` | string | | Only events containing this text in level / type, message, url or WebView id. |
| `sinceTimestamp` | integer | | Only events after this epoch timestamp (ms). |
| `order` | string | | `newest_first` (default) or `oldest_first`. |
| `limit` | integer | | 1 to 500. Default 50. |

---

## Device control: screenshots and recordings

These tools use `adb` on Android and the booted Simulator on iOS, and are not available for Wi-Fi devices. Files are saved in the Flocon documents folder.

### `take_screenshot`

:material-pencil: **Side effect: device** · **Targets: device / app**

Takes a screenshot of the device screen and saves it as a PNG in `Screenshot/`. Returns the path **and the image**, so the agent sees the screen. **Parameters:** none besides targeting.

### `start_screen_recording`

:material-pencil: **Side effect: device** · **Targets: device / app**

Starts recording the device screen. One recording per device. On Android, `screenrecord` stops by itself after 3 minutes. Reproduce the scenario, then call `stop_screen_recording`. **Parameters:** none besides targeting.

### `stop_screen_recording`

:material-pencil: **Side effect: device** · **Targets: device**

Stops the recording and saves the video as an mp4 in `ScreenRecord/`. Returns its path. Without target, stops the only running recording.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `deviceId` | string | | Device of the recording to stop. Default: the only running recording, or the selected device. |
| `packageName` | string | | Optional, as for the other tools. |

---

## Desktop: navigate, screenshot, ports, folder

These tools act on Flocon Desktop or your computer, not on the app.

### `flocon_navigate_into_desktop_app`

:material-pencil: **Side effect: Flocon Desktop**

Moves the Flocon Desktop window for you: displays a screen, selects a device and app, sets the timeline view. This changes what **you** selected in Flocon. Flocon is not brought to the front, so the agent asks you to go to the Flocon window. At least one parameter is required, and nothing changes if one is invalid.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `screen` | string | | `dashboard`, `network`, `images`, `database`, `files`, `shared_preferences`, `analytics`, `feature_flags`, `accessibility`, `tables`, `deeplinks`, `adb_commander`, `crash_reporter`, `timeline`, `web_view`. |
| `deviceId` | string | | Selects this device in Flocon. |
| `packageName` | string | | Selects this app (and its device if different). |
| `timelineView` | string | | `graph` or `list`. Saved in your preferences. |

### `take_flocon_desktop_screenshot`

:material-eye: **Read-only**

Returns an image of what the Flocon Desktop window currently displays (not the device screen: use `take_screenshot` for that). Only the visible content is captured, scaled down if too big, and nothing is saved on your computer. **Parameters:** none.

### `get_server_ports`

:material-eye: **Read-only**

Ports of the Flocon Desktop servers the SDKs connect to: the websocket (default 9023) and HTTP (default 9024) ports. Returns the effective ports, the saved ones, the defaults, whether each is forced by a launch option (`--websocket-port`, `--http-port` or the `FLOCON_WEBSOCKET_PORT`, `FLOCON_HTTP_PORT` environment variables), and the MCP port. **Parameters:** none.

### `set_server_ports`

:material-pencil: **Side effect: Flocon Desktop**

Changes the websocket and / or HTTP port (saved in the Flocon settings). The omitted one keeps its value, and `9023` / `9024` put the defaults back. Ports must be in 1024..65535 and different, not 9042 (reserved for MCP), the websocket port cannot be 9024 and the HTTP port cannot be 9023. A port forced by a launch option cannot be changed.

!!! warning
    Applying the ports restarts the websocket and HTTP servers and **disconnects every device** until the SDKs reconnect with the new ports. The MCP server on 9042 is not affected, and it cannot be moved.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `websocketPort` | integer | | New websocket port. |
| `httpPort` | integer | | New HTTP port. |

### `open_flocon_folder`

:material-pencil: **Side effect: your computer**

Opens the Flocon documents folder (screenshots, screen recordings, exports) in the file manager and returns its path.

| Parameter | Type | Req. | Description |
|---|---|:---:|---|
| `subfolder` | string | | Sub folder to open, e.g. `Screenshot` or `ScreenRecord`. Default: the documents folder. |

---

## Coverage check

Tools registered in the server code (`FloconDesktop/mcp`, one `addTypedTool`, `addTypedToolWithResult` or `addLoggedTool` call each) against tools documented on this page: **57 in code, 57 documented, none missing**.

| Area | Tools |
|---|---|
| Devices (1) | `list_devices` |
| Network (3) | `list_network_calls`, `get_network_call`, `search_network_calls` |
| Mocks (7) | `list_network_mocks`, `get_network_mock`, `create_network_mock`, `update_network_mock`, `delete_network_mock`, `list_websocket_clients`, `send_websocket_message` |
| Bad network (5) | `list_bad_network_configs`, `create_bad_network_config`, `update_bad_network_config`, `set_active_bad_network_config`, `delete_bad_network_config` |
| Database (4) | `list_databases`, `list_database_tables`, `query_database`, `list_database_query_logs` |
| Preferences (3) | `list_shared_preferences`, `get_shared_preference_values`, `set_shared_preference_value` |
| Files (3) | `list_file_roots`, `list_files`, `read_file` |
| Images (1) | `list_images` |
| Analytics (3) | `list_analytics_tables`, `list_analytics_events`, `count_analytics_events` |
| Feature flags (2) | `list_feature_flags`, `get_feature_flag` |
| Tables (2) | `list_tables`, `list_table_items` |
| Dashboards (2) | `list_dashboards`, `get_dashboard` |
| Deeplinks (3) | `list_deeplinks`, `list_deeplink_history`, `open_deeplink` |
| Timeline (2) | `get_timeline`, `show_in_timeline` |
| Crashes (2) | `list_crash_reports`, `get_crash_report` |
| Accessibility (5) | `list_accessibility_events`, `get_accessibility_state`, `get_accessibility_focused_node`, `move_accessibility_focus`, `clear_accessibility_events` |
| WebView (1) | `list_webview_events` |
| Device control (3) | `take_screenshot`, `start_screen_recording`, `stop_screen_recording` |
| Desktop (5) | `flocon_navigate_into_desktop_app`, `take_flocon_desktop_screenshot`, `get_server_ports`, `set_server_ports`, `open_flocon_folder` |

Total: 1 + 3 + 7 + 5 + 4 + 3 + 3 + 1 + 3 + 2 + 2 + 2 + 3 + 2 + 2 + 5 + 1 + 3 + 5 = **57**.
