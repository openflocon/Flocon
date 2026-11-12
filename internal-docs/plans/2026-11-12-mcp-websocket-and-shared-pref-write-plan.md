# MCP websocket message & shared preference edit — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add three MCP tools to Flocon Desktop: `set_shared_preference_value`, `list_websocket_clients`, `send_websocket_message`, and document them in the `flocon-mcp` skill and `docs2/mcp.md`.

**Architecture:** Each tool is registered in an existing `McpToolProvider` and calls the existing domain repositories with a target resolved by `McpTargetResolver` (never the Desktop UI selection). No SDK, protocol or domain change.

**Tech Stack:** Kotlin, Koin, MCP Kotlin SDK (`addTypedTool`, `@McpDescription` inputs), kotlinx.serialization, kotlin.test.

**Spec:** `internal-docs/plans/2026-11-12-mcp-websocket-and-shared-pref-write-design.md`

## Global Constraints

- Work on a branch from up-to-date `main` (e.g. `mcp-websocket-and-pref-edit`); the main checkout has untracked `.idea/` and `hs_err_pid*.log`: never add them.
- Only an **existing** key can be edited; no key creation or removal.
- Type names: `string`, `int`, `long`, `float`, `boolean`, `string_set` (same as `get_shared_preference_values`).
- `set_shared_preference_value`: `writeToolAnnotations(destructive = true, idempotent = true)`. `send_websocket_message`: `writeToolAnnotations(destructive = false, idempotent = false)`. `list_websocket_clients`: `readOnlyToolAnnotations`.
- Errors are thrown as `McpToolException` with a message telling the agent how to fix the call; nothing is sent to the app on error.
- Websocket injection works on Android (OkHttp `FloconWebSocket`) only; say it in the tool description and the skill.
- Do not touch `docs/` (repo rule `.claude/rules/docs-protocol-sync.md`); user docs go in `docs2/`.
- Commits end with `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- Test command, from `FloconDesktop/`: `./gradlew :mcp:desktopTest` (filter with `--tests '<fqcn>'`).

## Review Focus

1. `value` given as a JSON string for a numeric/boolean type (`"12"`, `"true"`): agents often stringify. Expected: accepted when it parses exactly into the target type, else error. Test in Task 1.
2. `int` value out of Int range (`3000000000`) or a decimal for `int`/`long`: error, not a silent truncation. Test in Task 1.
3. `string_set` given as a single string instead of an array: error. Test in Task 1.
4. The app does not answer after the set: the tool still returns `previousValue` with `confirmed = false`, it does not fail. Test in Task 1.
5. `send_websocket_message` when the app registered no websocket: error says no mockable websocket is registered and that only Android OkHttp supports it. Test in Task 2.

---

### Task 1: `set_shared_preference_value`

**Files:**
- Modify: `FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/sharedpreferences/SharedPreferencesMcpInputs.kt`
- Modify: `FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/sharedpreferences/SharedPreferencesMcpModels.kt`
- Modify: `FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/sharedpreferences/SharedPreferencesMcpToolProvider.kt`
- Test: `FloconDesktop/mcp/src/desktopTest/kotlin/io/github/openflocon/mcp/sharedpreferences/SharedPreferencesMcpToolProviderTest.kt`

**Interfaces:**
- Consumes: `SharedPreferencesRepository.editSharedPrefField(deviceIdAndPackageName, sharedPreference: DeviceSharedPreferenceDomainModel, key, value: Value)`, `observe(...)`, `getDeviceSharedPreferencesValues(...)`, `observeDeviceSharedPreferences(...)`.
- Produces:
  - `SetSharedPreferenceValueInput(sharedPreferenceId: String, key: String, value: JsonElement, type: String? = null, deviceId: String? = null, packageName: String? = null) : McpTargetArguments`
  - `SetSharedPreferenceValueMcpResult(deviceId: String, packageName: String, sharedPreferenceId: String, key: String, type: String, previousValue: JsonElement, newValue: JsonElement?, confirmed: Boolean)` (`newValue` = value read back from the app, null when not confirmed)

- [ ] **Step 1: Make the fake repository record edits and answer like the app**

In `FakeSharedPreferencesRepository`: replace the `editSharedPrefField` `error("not used")` with recording `edits += Triple(sharedPreference.id, key, value)` and, when a new flag `appAnswersEdits` (default `true`) is set, updating `knownValues` with the edited row (the real app answers a set with the full content). Expose `val edits = mutableListOf<Triple<DeviceSharedPreferenceId, String, Value>>()`.

- [ ] **Step 2: Write the failing tests**

Replace the test `all the tools are read only` by `tools and annotations`: the 3 names `list_shared_preferences`, `get_shared_preference_values`, `set_shared_preference_value`; the two first read only; `set_shared_preference_value` has `readOnlyHint == false`, `destructiveHint == true`.

Add (fixture values: `theme`=`"dark"`, `launch_count`=`12`, `onboarded`=`true`, `tags`=`{"a","b"}`):

- `set_shared_preference_value infers the type and returns the previous value`: args `sharedPreferenceId="settings", key="launch_count", value=13` → `edits == [("settings","launch_count",IntValue(13))]`, result `type == "int"`, `previousValue == 12`, `newValue == 13`, `confirmed == true`.
- `set_shared_preference_value accepts a stringified value of the type`: `key="onboarded", value="false"` → `BooleanValue(false)` sent; `key="launch_count", value="14"` → `IntValue(14)`.
- `set_shared_preference_value with an explicit type`: `key="launch_count", value=3000000000, type="long"` → `LongValue(3000000000)` sent.
- `set_shared_preference_value refuses an unknown key`: `key="them"` → `isError`, text contains `"them"` and `get_shared_preference_values`; `edits` empty.
- `set_shared_preference_value refuses a value of the wrong type`: each of `(launch_count, 3000000000)`, `(launch_count, 1.5)`, `(onboarded, "yes")`, `(tags, "a")`, `(theme, 12, type="unknown")` → `isError`, `edits` empty.
- `set_shared_preference_value unknown store`: `sharedPreferenceId="nope"` → `isError`, text contains `list_shared_preferences`.
- `set_shared_preference_value without answer`: `appAnswersEdits = false` → not an error, `confirmed == false`, `previousValue == 12`, `newValue` absent, one edit sent.

- [ ] **Step 3: Run the tests to verify they fail**

Run: `./gradlew :mcp:desktopTest --tests 'io.github.openflocon.mcp.sharedpreferences.SharedPreferencesMcpToolProviderTest'`
Expected: FAIL (unknown tool `set_shared_preference_value`).

- [ ] **Step 4: Add the input, the result model and the tool**

- Register `set_shared_preference_value` (title "Set shared preference value"). Description: edits one existing key of a preference store of the app; the type of the current value is used unless `type` is given; returns the previous value so it can be restored; ask the user to confirm, showing `key: old → new`.
- Handler `setValue(input)`: resolve target; find the store in `observeDeviceSharedPreferences` (ask the app first with the same timeout pattern as `listSharedPreferences` if unknown) → unknown id error; load current rows with the same ask + `withTimeoutOrNull(answerTimeoutMs)` as `getValues` → unknown key error; convert `input.value` to a `Value` of `input.type ?: current type` (private `toValue(type: String, json: JsonElement): Value`, exact conversion only: `intOrNull`/`longOrNull`/`floatOrNull`/`booleanOrNull` on the primitive content, string_set needs a `JsonArray` of strings); capture rows before, call `editSharedPrefField`, then wait `withTimeoutOrNull(answerTimeoutMs)` for `observe(...)` to emit rows `!= before`; `confirmed` = the key is present in the answer.
- Reuse the existing `toMcpModel()` mapping to build `previousValue` / `newValue` (extract the `type to json` part into a private helper used by both).

- [ ] **Step 5: Run the tests to verify they pass**

Run: same command. Expected: PASS. Then `./gradlew :mcp:desktopTest` (whole module, catches tool-list assertions elsewhere): PASS.

- [ ] **Step 6: Commit**

```bash
git add FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/sharedpreferences FloconDesktop/mcp/src/desktopTest/kotlin/io/github/openflocon/mcp/sharedpreferences
git commit -m "feat: [MCP] set_shared_preference_value tool"
```

### Task 2: `list_websocket_clients` and `send_websocket_message`

**Files:**
- Modify: `FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/mocks/NetworkMocksMcpInputs.kt`
- Modify: `FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/mocks/NetworkMocksMcpModels.kt`
- Modify: `FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/mocks/NetworkMocksMcpToolProvider.kt`
- Test: `FloconDesktop/mcp/src/desktopTest/kotlin/io/github/openflocon/mcp/mocks/NetworkMocksMcpToolProviderTest.kt`

**Interfaces:**
- Consumes: `NetworkMocksRepository.observeWebsocketClientsIds(deviceIdAndPackageName): Flow<List<NetworkWebsocketId>>`, `sendWebsocketMock(deviceIdAndPackageName, websocketId, message)`.
- Produces:
  - `ListWebsocketClientsInput(deviceId: String? = null, packageName: String? = null) : McpTargetArguments`
  - `SendWebsocketMessageInput(websocketId: String, message: String, deviceId: String? = null, packageName: String? = null) : McpTargetArguments`
  - `WebsocketClientsMcpResult(deviceId: String, packageName: String, websocketIds: List<String>, note: String?)` (`note` set when empty: no mockable websocket, Android OkHttp `FloconWebSocket` only)
  - `SendWebsocketMessageMcpResult(deviceId: String, packageName: String, websocketId: String, sent: Boolean)`

- [ ] **Step 1: Make the fake repository hold websocket ids and record messages**

In `FakeNetworkMocksRepository`: `var websocketIds: List<String> = emptyList()`, `val sentMessages = mutableListOf<Pair<String, String>>()`; implement the two methods with them (`flowOf(websocketIds)`).

- [ ] **Step 2: Write the failing tests**

- Update any existing assertion on the provider's tool names to include the 2 new ones; `list_websocket_clients` read only, `send_websocket_message` `readOnlyHint == false`, `destructiveHint == false`.
- `list_websocket_clients returns the registered ids`: `websocketIds = ["chat","notifications"]` → `websocketIds == ["chat","notifications"]`, no `note`.
- `list_websocket_clients explains an empty list`: → `websocketIds` empty, `note` contains `Android`.
- `send_websocket_message sends to the app`: ids `["chat"]`, args `websocketId="chat", message="{\"type\":\"ping\"}"` → `sentMessages == [("chat","{\"type\":\"ping\"}")]`, `sent == true`.
- `send_websocket_message refuses an unknown id`: ids `["chat"]`, `websocketId="chats"` → `isError`, text contains `chat` and `list_websocket_clients`; nothing sent.
- `send_websocket_message without websocket`: ids empty → `isError`, text contains `Android`; nothing sent.

- [ ] **Step 3: Run the tests to verify they fail**

Run: `./gradlew :mcp:desktopTest --tests 'io.github.openflocon.mcp.mocks.NetworkMocksMcpToolProviderTest'`
Expected: FAIL (unknown tools).

- [ ] **Step 4: Register the two tools**

`list_websocket_clients` (title "List websocket clients"): ids of the app websockets that accept injected messages, current value of `observeWebsocketClientsIds(target).first()`. `send_websocket_message` (title "Send websocket message"): injects a text message into a websocket of the app as if received from the server, Android OkHttp only; validate the id against the same list before sending.

- [ ] **Step 5: Run the tests to verify they pass**

Run: same command, then `./gradlew :mcp:desktopTest`. Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add FloconDesktop/mcp/src/desktopMain/kotlin/io/github/openflocon/mcp/mocks FloconDesktop/mcp/src/desktopTest/kotlin/io/github/openflocon/mcp/mocks
git commit -m "feat: [MCP] list_websocket_clients and send_websocket_message tools"
```

### Task 3: Skill and docs

**Files:**
- Modify: `plugins/flocon/skills/flocon-mcp/SKILL.md`
- Modify: `docs2/mcp.md`

**Interfaces:**
- Consumes: the tool names, arguments and rules of Tasks 1 and 2.

- [ ] **Step 1: Update the skill**

- "Check SharedPreferences" row: add `set_shared_preference_value` (existing key only, returns the previous value).
- New row "Inject a server message into a websocket of the app": `list_websocket_clients`, `send_websocket_message` (Android OkHttp only).
- Rules: before `set_shared_preference_value`, read the value with `get_shared_preference_values` and get the user's confirmation showing `key: old → new`; after it, tell the user the previous value (and `confirmed: false` means the app did not answer). Add the 2 write tools to the list of tools changing the app state (if PR #834 is not merged yet, rebase on it first: it introduces that list).
- Platform coverage paragraph: websocket injection not supported by the Swift and Flutter SDKs.

- [ ] **Step 2: Update `docs2/mcp.md`**

Add the three tools to the tools table, and to the write tools mentioned in the "Local only" box.

- [ ] **Step 3: Verify**

Run, from the repo root: a check that every tool registered in `FloconDesktop/mcp/src/desktopMain` (excluding test fixtures) appears backticked in the skill:
`bash -c 'S=plugins/flocon/skills/flocon-mcp/SKILL.md; for t in $(grep -rhoE "name = \"[a-z_]+\"" FloconDesktop/mcp/src/desktopMain | sed -E "s/.*\"(.*)\"/\1/" | sort -u); do grep -q "\`$t\`" $S || echo MISSING $t; done'`
Expected: only `MISSING flocon` (the server name). Then `mkdocs build --strict -f <config using docs2>` if the repo has one for docs2, else skip and say so.

- [ ] **Step 4: Commit and open the PR**

```bash
git add plugins/flocon/skills/flocon-mcp/SKILL.md docs2/mcp.md internal-docs/plans/2026-11-12-mcp-websocket-and-shared-pref-write-*.md
git commit -m "docs: [PLUGIN] Websocket message and shared preference edit MCP tools"
git push -u origin mcp-websocket-and-pref-edit
gh pr create --base main --title "feat: [MCP] Send a websocket message, edit a shared preference"
```
PR body ends with `🤖 Generated with [Claude Code](https://claude.com/claude-code)`.
