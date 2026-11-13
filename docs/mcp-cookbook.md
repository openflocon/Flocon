---
title: MCP Cookbook
description: Ten realistic workflows for debugging a mobile app with an AI agent connected to Flocon, with the prompt to type, the tools the agent calls, and the tips.
---

# 🍳 MCP Cookbook

Ten workflows you can copy. Each one gives the **goal**, the **prompt** you type, the **tool calls** the agent makes (in order, with the key arguments), a short **result** and some **tips**.

!!! info "About the results"
    The results shown are **illustrative**: urls, ids, values and wording depend on your app and on your agent. The tool names and arguments are the real ones, see the [Tools Reference](mcp-tools.md).

Before you start: Flocon Desktop is running, your agent is [connected](mcp.md#setup), and your app is connected to Flocon. By default the tools use the device and app selected in Flocon Desktop (see [targeting](mcp-tools.md#targeting-deviceid-and-packagename)).

---

## 1. Why does this request fail { #why-request-fails }

**Goal:** understand a failing call, and get a command to replay it yourself.

**Prompt**

> My article page doesn't display. Is it a network call? If so, give me the cURL.

**Tool calls**

1. `list_network_calls` with `onlyErrors: true`, `limit: 20`: the failed calls of the current session.
2. `get_network_call` with `callId: "<id of the failing call>"`: headers, bodies, error and cURL (`includeCurl` is `true` by default).

**Result** (illustrative)

```text
GET /articles/42 failed with 500 Internal Server Error in 312 ms.
Response body: {"error":"article_service_unavailable"}
The request has the header Authorization: Bearer eyJ... (valid format).

curl -X GET 'https://api.example.com/articles/42' -H 'Authorization: Bearer eyJ...'
```

**Tips**

- The agent starts from summaries and only opens the calls it needs: bodies can be large.
- To search for a value anywhere (a header, a field of a body), ask for it: the agent uses `search_network_calls` with `query`.
- Replaying the cURL in your terminal tells you if the error comes from the server or from what your app sends.

---

## 2. Mock an endpoint to test an error state { #mock-error-state }

**Goal:** see how a screen behaves on a server error, without touching the backend.

**Prompt**

> Make the articles endpoint fail with a 500 so I can check my error screen. Tell me when it's on, and turn it off at the end.

**Tool calls**

1. `list_network_calls` with `urlContains: "/articles"`, `limit: 5`: finds a real captured call.
2. `create_network_mock` with `fromCallId: "<id>"`, `httpCode: 500`, `body: "{\"error\":\"internal_error\"}"`, `displayName: "Articles 500"`: copies url, method and response from the call, and overrides the status and body. Active right away.
3. You open an article in the app and check the error screen.
4. `update_network_mock` with `mockId: "<id>"`, `enabled: false`: stops the mock without deleting it.

**Result** (illustrative)

```text
Mock "Articles 500" created for GET .*/articles/.* : status 500, enabled.
...
Mock "Articles 500" is now disabled (kept in Flocon, you can enable it again).
```

**Tips**

- Mock a network failure instead of an HTTP error with `exceptionClassPath: "java.net.SocketTimeoutException"` (no `httpCode`).
- Add `delayMs` to also test the loading state.
- `shared: true` applies the mock to all your apps and devices: leave it off unless you want that.
- Agents are told to prefer disabling over deleting, and to tell you which mocks are still enabled. See [Mocking](mocking.md).

---

## 3. Reproduce a slow or flaky network { #slow-network }

**Goal:** check loading states, timeouts and retries on a bad connection.

**Prompt**

> Make my network slow (2 to 5 seconds) and flaky (30% of calls fail with a timeout or a 503). I'll navigate the app, then tell you when to stop.

**Tool calls**

1. `list_bad_network_configs`: reuses an existing config if there is one.
2. `create_bad_network_config` with `name: "Slow and flaky"`, `latencyTriggerProbability: 1`, `minLatencyMs: 2000`, `maxLatencyMs: 5000`, `errorProbability: 0.3`, `errors: [{"exceptionClassPath": "java.net.SocketTimeoutException"}, {"httpCode": 503, "body": "{\"error\":\"unavailable\"}"}]`, `enabled: true`.
3. You use the app. Then the agent can read the effect: `list_network_calls` with `sinceTimestamp: <time you started>`, to see durations and failures.
4. `set_active_bad_network_config` with **no** `configId`: turns the simulation off, the config is kept.

**Result** (illustrative)

```text
Active config: "Slow and flaky".
Since then: 41 calls, 12 failed (7 SocketTimeoutException, 5 with a 503), median duration 3.4 s.
The feed screen shows no error state after a 503: the list stays empty.
Simulation is now off.
```

**Tips**

- Asking the agent to look at the calls afterwards gives you evidence of what the user experienced, not only what you saw.
- Combine with [workflow 8](#bug-ticket-capture) to record the screen while the network is bad.
- Always finish with the simulation off: agents are told to do it and to say which config they left active. See [Bad network](bad-quality.md).

---

## 4. Inspect the database after an action { #db-after-action }

**Goal:** check that what the user did was really saved, and with the right values.

**Prompt**

> I just created an account in the app. Is the user saved in the database with the right email?

**Tool calls**

1. `list_databases`: gives the `databaseId`.
2. `list_database_tables` with `databaseId: "<id>"`: the schema, to find the right table and columns.
3. `query_database` with `databaseId: "<id>"`, `sql: "SELECT id, email, created_at FROM users ORDER BY created_at DESC LIMIT 3"`.
4. Optional: `list_database_query_logs` with `databaseId: "<id>"`, `text: "users"`, to see the queries your app ran, if [query logging](database.md#real-time-query-logging) is set up.

**Result** (illustrative)

```text
users, 1 row matches: id=18, email="jean@example.com", created_at="2026-11-03 14:31:55".
The email is saved as typed, but without trimming: the input had a trailing space.
```

**Tips**

- Only read-only SQL runs (`SELECT`, `PRAGMA`, `EXPLAIN`). Always keep a `LIMIT`: the app sends all the rows.
- Preferences work the same way: `list_shared_preferences`, then `get_shared_preference_values` with `sharedPreferenceId` and an optional `text` filter.
- Query logs show transactions: pass a `transactionId` from a previous result to see only one.

---

## 5. Crash triage { #crash-triage }

**Goal:** find the cause of a crash from what the app did just before.

**Prompt**

> The app just crashed. What happened in the two minutes before?

**Tool calls**

1. `get_timeline` with no argument: it anchors on the latest crash and says why. To be explicit: `mode: "before_crash"`.
2. `get_crash_report` with `crashId: "<id of the crash line, after crash:>"`: the full stack trace.
3. `get_network_call` with `callId: "<id of a suspicious line, after network:>"` if a call looks wrong.
4. Optional: `show_in_timeline` with no argument, to open the zone around the crash in Flocon Desktop for you.

**Result** (illustrative)

```text
Window: 14:31:05 to 14:32:10 (the minute before the latest crash, plus 5 s). 38 events, 3 anomalies:
- crash:c91  IllegalStateException: "token is null"  (14:32:05)
- network:a7 POST /v1/checkout -> 401  (14:32:03)
- 12x GET /v1/feed in 3.1 s  (14:31:48, loop)
The 401 happened 2 s before the crash, and the stack trace points to CheckoutViewModel.pay().
```

**Tips**

- The times are those of the **phone**, not your computer. Give times as `HH:mm` and the tool uses the phone clock; the answer shows the window it chose.
- Read the window and the `notes` in the answer before concluding: an empty timeline can mean the window was wrong.
- To check whether something was sent at all, use `search`: `get_timeline` with `search: "checkout_failed"` returns `found` and never cuts the matches.
- `excludeSources: ["database"]` removes noisy transactions.
- See [Timeline](timeline.md) and [Crash reporter](crashreporter.md).

---

## 6. Toggle a feature flag and verify the calls change { #feature-flag }

**Goal:** check that flipping a flag really changes what the app does.

Flocon shows feature flags but does not change them: you flip the flag in your provider (Firebase, Statsig, your own debug menu...), the agent checks the effect.

**Prompt**

> I'm going to turn `new_checkout` on in our provider. Read the flag now, tell me when to flip it, then compare the network calls of the checkout before and after.

**Tool calls**

1. `get_feature_flag` with `provider: "firebase"`, `name: "new_checkout"`: state, variant and payload before.
2. `list_network_calls` with `urlContains: "/checkout"`: the calls before (note the time of the latest one).
3. You flip the flag, restart or refresh the app, and run the checkout again.
4. `get_feature_flag` again: confirms the new variant.
5. `list_network_calls` with `urlContains: "/checkout"`, `sinceTimestamp: <time of the flip>`: the calls after.

**Result** (illustrative)

```text
new_checkout (firebase): off -> treatment, payload {"steps":3}.
Before: POST /v1/checkout (1 call). After: POST /v2/checkout/init, POST /v2/checkout/confirm (2 calls).
The new flow is used, and /v1/checkout is no longer called.
```

**Tips**

- Use `list_feature_flags` with `query: "checkout"` if you do not know the exact name or provider.
- To compare two flows, ask the agent to put the two lists in a table.
- See [Feature flags](featureflags.md).

---

## 7. Accessibility audit by moving the focus { #accessibility-audit }

**Goal:** check what TalkBack reads, screen by screen, in the focus order. **Android only**: the focus tools need TalkBack and `adb`.

**Prompt**

> TalkBack is on. Walk through the focus order of the profile screen and tell me every element without a content description.

Turn TalkBack on yourself on the device first: the agent cannot do that for you.

**Tool calls**

1. `get_accessibility_state`: is capture supported, is a screen reader running.
2. `clear_accessibility_events`: starts from an empty list.
3. `get_accessibility_focused_node`: where the focus is now.
4. `move_accessibility_focus` with `direction: "next"`, repeated: each call returns the element it landed on.
5. `list_accessibility_events` with `type: "VIEW_FOCUSED"`, `limit: 50`: everything TalkBack received.

**Result** (illustrative)

```text
Focus order of the profile screen (9 stops):
1. "Back" button  2. "Jean Dupont" heading  3. ImageButton (no content description)
4. "Follow" button ...
Problem: stop 3 is an ImageButton with no text and no content description, TalkBack would read "button".
```

**Tips**

- `move_accessibility_focus` does not wrap around: the agent stops when the focus does not move.
- If `list_accessibility_events` is empty, `get_accessibility_state` tells why (no screen reader, capture not supported).
- On iOS, the Swift and Flutter SDKs do not send accessibility events yet. See [Accessibility](accessibility.md).

---

## 8. Capture a screenshot and a recording of a bug for a ticket { #bug-ticket-capture }

**Goal:** attach proof to a bug report.

**Prompt**

> Record me opening the cart and paying, stop when I tell you, then take a screenshot and list the failed calls for the ticket.

**Tool calls**

1. `start_screen_recording`.
2. You reproduce the bug.
3. `take_screenshot`: saved as a PNG in `Screenshot/`, and the image is returned so the agent can see the screen.
4. `stop_screen_recording`: saved as an mp4 in `ScreenRecord/`.
5. `list_network_calls` with `onlyErrors: true` and `sinceTimestamp: <start time>`: the failed calls during the recording.
6. Optional: `open_flocon_folder` with `subfolder: "ScreenRecord"`: opens the folder on your computer.

**Result** (illustrative)

```text
Recording saved: ~/Documents/Flocon/ScreenRecord/record_com.example.shop_1736000000000.mp4
Screenshot saved in ~/Documents/Flocon/Screenshot/ (PNG).
The screenshot shows an empty payment form with no message. POST /payments returned 402 during the recording.
```

**Tips**

- Uses `adb` on Android and the booted Simulator on iOS. Not available for a device connected over Wi-Fi.
- One recording per device, and on Android `screenrecord` stops by itself after 3 minutes.
- Ask the agent to write the ticket text from what it saw, and attach the two files yourself.

---

## 9. Open a deeplink and check the network calls and the screen { #deeplink-check }

**Goal:** check that a deeplink lands on the right screen and loads the right data.

**Prompt**

> Open the deeplink for the profile of user 42 on the posts tab. Then tell me what screen it shows and which calls it made.

**Tool calls**

1. `list_deeplinks`: the deeplinks declared by the app, with their parameters.
2. `open_deeplink` with `link: "myapp://profile/42?tab=posts"`, `deeplinkId: <id from list_deeplinks>` (keeps the label in the history).
3. `take_screenshot`: the agent looks at the screen.
4. `list_network_calls` with `sinceTimestamp: <time just before the deeplink>`: the calls it triggered.

**Result** (illustrative)

```text
Deeplink opened. The screen shows the profile of "Marie" with the Posts tab selected.
Calls since: GET /users/42 -> 200 (180 ms), GET /users/42/posts?limit=20 -> 200 (240 ms).
```

**Tips**

- Fill every parameter of the link before opening it.
- Android uses `adb`, iOS the booted Simulator.
- `list_deeplink_history` shows what was already opened from Flocon. See [Deeplinks](deeplink.md).

---

## 10. Install Flocon in an existing project { #setup-flocon }

**Goal:** add Flocon to an app (Kotlin, Swift or Flutter), or migrate it from 1.x, with the agent doing the edits.

This one uses the **`flocon-setup` skill** of the [Flocon plugin](mcp.md#the-two-skills), not the MCP tools. Install the plugin first.

**Prompt**

> Set up Flocon in this project. I want network and database, and it must not be in release builds.

**What the agent does**

1. **Detects the platform** from the project files: Android (`com.android.application`), Kotlin Multiplatform, Swift (`.xcodeproj`, `Package.swift`) or Flutter (`pubspec.yaml`).
2. **Detects the version** if Flocon is already there, and picks a path: install, migrate from 1.x to 2.0, or only add the missing plugins.
3. **Adds the dependencies** following the matching setup: [Kotlin](setup.md) (modules in every variant, `flocon-no-op` for release), Swift Package Manager (`FloconSwift`), or `flutter pub add flocon` and the plugin packages.
4. **Starts Flocon** in the app, and installs **only the plugins the project really uses** (it searches the code instead of guessing).
5. **Keeps it out of release**: it inspects the real build variants and tells you exactly what it did (SDK inactive vs absent from the binary).
6. **Builds** the touched targets and says what was **not** verified (running on a device, connection to Flocon Desktop, release build).

**Result** (illustrative)

```text
Kotlin SDK: added flocon and flocon-no-op (release), network and database plugins.
Compiled debug and release. Not verified: the app running on a device and showing in Flocon Desktop.
```

**Tips**

- Then run the app with Flocon Desktop open, and ask: "List my network calls". If a tool returns nothing, the agent can tell which wiring is missing.
- The skill follows the docs of each SDK: if the skill and the docs disagree, the docs win, and it tells you.
- It works on a branch and does not touch your unrelated uncommitted changes.
- See [Connect your app](setup.md) and [Migrating from 1.x](migration.md).
