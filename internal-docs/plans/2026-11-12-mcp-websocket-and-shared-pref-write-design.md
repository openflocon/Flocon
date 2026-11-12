# MCP: send a websocket message, edit a shared preference

## Intent

Let an AI agent connected to the Flocon MCP server act on the app in two ways Flocon Desktop already supports in its UI:

- inject a message into a websocket of the app, as if received from the server;
- change the value of an existing shared preference (SharedPreferences, DataStore, UserDefaults, Keychain...).

The only concern is data loss: a user must not lose a preference value by mistake. The confirmation is left to the agent client (tool permission prompt), no dialog in Desktop.

## Goals

- `set_shared_preference_value`: edits **one existing key** of a preference store, with a typed value.
- `list_websocket_clients`: ids of the websockets of the app that accept mocked messages.
- `send_websocket_message`: injects a text message into one of them.
- The `flocon-mcp` skill and `docs2/mcp.md` document the three tools and their rules.

## Non-goals

- Creating or removing a preference key (the protocol has `remove_shared_preference_value`, not exposed).
- Websocket support in the Swift and Flutter SDKs: only Android (OkHttp `FloconWebSocket`) receives `websocket_mock_message` today. Separate work, per SDK.
- Dashboard actions, network call replay.
- A confirmation dialog in Flocon Desktop.

## Anti-goals

- An edit that silently overwrites a value with no way back: the previous value is always returned.
- Creating a key by mistake through a typo in its name.

## Approaches

1. **Wire the existing Desktop code (chosen).** Three MCP tools on top of `SharedPreferencesRepository.editSharedPrefField` and `NetworkMocksRepository.observeWebsocketClientsIds` / `sendWebsocketMock`, no SDK or protocol change. Small, testable with the existing fakes.
2. Same, plus websocket reception in the Swift and Flutter SDKs: full coverage, but three repositories and the Flutter websockets are only logged by hand. Rejected for now.
3. A single `send_websocket_message` picking the websocket when there is only one: lighter, but breaks as soon as two websockets are registered. Rejected.

## Design

### `set_shared_preference_value` (in `SharedPreferencesMcpToolProvider`)

- Input: target (`deviceId`, `packageName`, optional as for the other tools), `sharedPreferenceId`, `key`, `value` (JSON), optional `type` (`string`, `int`, `long`, `float`, `boolean`, `string_set`, same names as `get_shared_preference_values`).
- Reads the current values first (same ask + wait as `get_shared_preference_values`). Unknown key: error, nothing sent. No `type`: the type of the current value is used. `value` not convertible to the type: error, nothing sent.
- Calls the repository directly with the resolved target (not `EditSharedPrefFieldUseCase`, which uses the Desktop selection).
- The app answers a set with the full content (`get_shared_preference_value`): wait for it with the same timeout and return `previousValue`, `newValue` as read back, and `confirmed: false` if the app did not answer in time.
- Annotations: `writeToolAnnotations(destructive = true, idempotent = true)`, so the clients ask for confirmation by default.

### `list_websocket_clients` / `send_websocket_message` (next to the network mocks tools)

- `list_websocket_clients`: target input; returns the ids from `observeWebsocketClientsIds` (read only). Empty list: the message says the app registered no mockable websocket (Android OkHttp `FloconWebSocket` only for now).
- `send_websocket_message`: target, `websocketId`, `message` (text). Unknown id: error listing the known ones. Annotations: write, not destructive, not idempotent.

### Skill and docs

- `flocon-mcp/SKILL.md`: rows in "What to use for what"; rules: before `set_shared_preference_value`, read the current value and show the user `key: old → new`; tell the user the previous value after the edit; websocket injection works on Android only for now. Add the tools to the list of tools that change the app.
- `docs2/mcp.md`: the three tools in the table.

## Left to the builder

- Exact JSON field names of inputs and results, following the existing `*McpInputs.kt` / `*McpModels.kt` style.
- Error wording, tests (provider tests with the existing fakes: unknown key, type mismatch, type inferred, previous value returned, timeout; websocket unknown id, empty list).
