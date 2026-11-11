# Analytics: nested properties from a `Map<String, Any?>`

Issue: [#484](https://github.com/openflocon/Flocon/issues/484) (related discussion #459).

## Intent
Some analytics solutions (Adobe, Amplitude…) model event properties as a nested structure (`application.app.version`). Today the SDK only accepts flat `name analyticsProperty "value"` pairs, so the app has to flatten its own data before logging. Let the app pass a `Map<String, Any?>` and have the SDK flatten it.

## Goals
- A new overload takes a `Map<String, Any?>` of properties, nested maps allowed.
- Source-agnostic: Moshi (`adapter(Any::class.java)`), Gson (`fromJson(json, Map::class.java)`) and hand-built maps all work, with no serialization dependency added to the SDK.
- Zero change on the wire, in the desktop database and in the desktop UI.

## Non-goals
- Hierarchical display on the desktop (top-level chips like `user: [2]`, expandable tree in the detail). Possible later, see "Approaches" option B.
- Accepting `org.json.JSONObject` or kotlinx `JsonElement` directly: not multiplatform / not library-neutral. Callers convert to a `Map` (`JSONObject.toMap()`, Moshi, Gson).
- Formatting numbers: a `Double` stays a `Double` (`2048.0` is shown as `2048.0`). Documented, not hidden.

## Anti-goals
- A crash in the host app: the SDK is a debug tool, an unsupported value must degrade to `toString()`, never throw.
- A silent data loss: `null` and unknown types stay visible.

## Constraints
- `FloconAndroid/` KMP conventions per `.claude/rules/android-sdk.md`: `commonMain` only, plugin / no-op signature parity (`analytics` and `analytics-no-op`).
- Backward compatible: existing `AnalyticsEvent`, `analyticsProperty` and `sender.log(...)` overloads untouched. `Protocol.kt` (both copies) is not modified, so no protocol or `internal-docs/protocol/analytics.md` change.

## Approaches
A. **Flatten in the SDK to dotted keys (chosen).** `application.app.version = "2025.17.0"`. The protocol, Room (`propertiesColumnsNames` / `propertiesValues`), the SQL `LIKE` filter, CSV export, MCP (`Map<String, String>`) and UI keep working unchanged. Desktop shows one chip per flattened key.
B. **Carry the hierarchy to the desktop.** Optional protocol field, Room migration (schema is at 95+), changes to filter, CSV, MCP and a collapsible UI. Heavier, and only worth it if A turns out to be insufficient for users. Not done here.
C. `kotlinx.serialization.json.JsonObject` as input. Favors one library; a `Map` is the common denominator. Rejected.

## Design
- **API**
  - `FloconAnalyticsSender.log(eventName: String, properties: Map<String, Any?>)`.
  - `AnalyticsEvent` secondary constructor `(eventName, analyticsTableId, properties: Map<String, Any?>)`, so `plugin.log(...)` and `sender.log(event)` get it too.
  - The overload has no default value, so existing `log("name")` calls keep resolving as today.
- **Flattening** (internal function in `analytics`, `commonMain`, output `List<AnalyticsPropertiesConfig>`):

  | Value | Result |
  |---|---|
  | `String`, `Number`, `Boolean`, `Char`, `Enum` | `toString()` (`Enum`: `name`) |
  | `null` | `"null"` |
  | `Map<*, *>` | recursive, key joined with `.` (non-`String` keys use `toString()`) |
  | `Iterable<*>`, `Array<*>` | **one** property: `[a, b]` (elements rendered with `toString()`; a map inside a list is not unfolded) |
  | anything else | `toString()` |

  Depth is capped (10 levels); past the cap the remaining value is rendered with `toString()`, which also protects against a map that contains itself.
- **No-op** (`analytics-no-op`): same signatures, empty bodies (`AnalyticsEvent` constructor returns an event with no properties, the flattener is not duplicated).
- **Desktop**: unchanged.
- **Docs**: `docs2/analytics.md` gets a "Nested properties" section with Moshi and Gson examples and the `Double` note; the `README.md` analytics snippet gets the map form. No `docs/` change (previous docs), no MCP change.

## Tests
`FloconAnalyticsPluginTest` / a new flattener test in `analytics/src/jvmTest`:
- nested map → dotted keys; `null` → `"null"`; list → one `[a, b]` property.
- `Number` types incl. `Double` stay as `toString()`.
- non-`String` keys, unsupported object (`toString()`), depth cap, self-referencing map does not loop.
- `Map` coming from Gson's `LinkedTreeMap` (JVM) works.
- `:analytics:analytics-no-op:compileKotlinJvm` compiles (parity), and `:sample:android:assembleRelease` (no-op) before the PR, per `build-verification.md`.

## Open questions
- Depth cap of 10: fine, or a different value?
- Should a map inside a list be unfolded instead (`items.0.id`)? Kept out for now: lists stay one value, as decided.

## Out of scope / follow-up
Option B (hierarchical desktop display), only if A proves insufficient on the issue.
