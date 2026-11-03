# Timeline logs (navigation, user actions, lifecycle, custom)

## Intent
Let the mobile app push its own events into the Flocon timeline, to read what
happened around a bug: which screen was open, what the user did, what the app
lifecycle was, and any app specific milestone. The first need was
**navigation**; the API is generic so any event can use it.

## Goals
- `Flocon.timelinePlugin` SDK plugin (KMP, with a no-op module), same shape as
  the other plugins (`Flocon.featureFlagsPlugin`).
- Two separate kinds of call, never mixed:
  - **Typed, no level:** `log(type: FloconTimelineType, value, params)`, with
    the shortcuts `logNavigation(value, params)`, `logUserAction(value, params)`
    and `logLifecycle(value, params)`. `FloconTimelineType` is a light wrapper
    of a name; the SDK declares `Navigation`, `UserAction` and `Lifecycle` with
    it, and the dev declares and reuses their own (e.g.
    `FloconTimelineType("billing")`).
  - **Tagged, with a level:** `log(tag: String, value, params, level = Info)`.
- `level` is an enum `Debug, Info, Warning, Error`, only on the tagged `log`.
  A typed log has no level.
- `value` is the main text shown in the row (e.g. a screen name). `params` is a
  `Map<String, String>` shown in the detail.
- Desktop: events show up in the existing timeline as a new source, filterable
  by type and by level.

## Non-goals
- No automatic detection of navigation (no Navigation 3 / Compose / UIKit
  integration). The app calls the SDK from its own listener; samples show how.
- No custom icon or color per custom tag. Custom tags use a default style.
- No nested or typed `params` (text only).
- No further dedicated helpers (`Debug`, `Permission`, `Connectivity`,
  `Notification`, `BackgroundTask`, `Session` were discussed and left to
  `log("tag", ...)`).
- Not a replacement for analytics events, which keep their own plugin/table.

## Anti-goals
- Breaking an older SDK or desktop: every new field is optional with a default.
- A timeline that needs the SDK updated to show a new custom tag.

## Constraints
- The timeline stores no rows: it is a read-only SQL `UNION ALL` over one Room
  table per feature (`TimelineQueryBuilder.buildUnion`). A new source needs its
  own table and a branch in that union.
- Both `Protocol.kt` files (SDK and desktop domain) must stay identical in
  plugin/method names; `protocol/<plugin>.md`, `docs/`, `mkdocs.yml`, README and
  the MCP docs/skill follow `.claude/rules/docs-protocol-sync.md`.
- Plugin / no-op parity (`FloconAndroid/` rules). New behaviour gets tests
  (SDK in `src/jvmTest`).

## Approaches considered
1. **One `Log` source, type and level as filters (chosen).** One table
   (`TimelineLog`), one union branch, `TimelineSource.Log`. Type
   (`navigation`, `userAction`, `lifecycle`, custom tags) and level are facets.
   Smallest; seeing only navigation goes through the filter panel.
2. One `TimelineSource` per fixed type (own chip for Navigation, etc.). Nicer
   visibility, but several union branches on one table, more mapper/test code,
   a more crowded source bar. Can be done later desktop-side without any
   protocol or SDK change.
3. Reuse the analytics table. No new table, but it mixes two different usages
   and the filters contradict each other. Rejected.

## Design

### Wire (mobile -> desktop)
New plugin id `Timeline`, one method `log` (names go in both `Protocol.kt`).
Payload fields:
- `type`: string. Reserved values `navigation`, `userAction`, `lifecycle`;
  anything else is a custom tag, sent as is.
- `value`: string.
- `params`: map string -> string, optional, default empty.
- `level`: string `debug|info|warning|error`, optional, default `info`.
- `timestamp`.

A typed log (`log(type, ...)` and the three shortcuts) sends `type = type.name`
and no level. A tagged log (`log(tag, ...)`) sends `type = tag` and its level.
The desktop recognizes the three SDK names (case-insensitively) to give them
their icon; any other name is a custom type. A tag equal to one of those names
is shown as that type (documented). The same applies on iOS (`TimelineType`).

### SDK
- New module `FloconAndroid/timeline/timeline` and `timeline-no-op`, registered
  in `settings.gradle.kts`, the BOM and the sample apps (debug = real,
  release = no-op).
- Plugin interface, factory, impl and the `Flocon.Companion.timelinePlugin`
  accessor modelled on `FloconFeatureFlagsPlugin`.

- **iOS:** the native Swift SDK lives in a separate repo
  (`https://github.com/openflocon/FloconSwift`). It gets the same plugin
  (`.timeline`, `Flocon.timelinePlugin`) and the same API and wire payload, in a
  separate PR there.

### Desktop
- Domain: `TimelineSource.Log`; `TimelineFacet` entries for log type and log
  level; a `TimelineLog` domain model; use case/repository to store incoming
  messages.
- Data: `TimelineLogEntity` Room table (device, package, app instance,
  timestamp, type, level, value, params JSON), DAO, registration in
  `AppDatabase`; ingestion from the websocket message handler.
- `TimelineQueryBuilder`: new `KIND_LOG`, branch in `buildUnion` (with session
  and device/package scoping like the other sources), `Mapper.kt` mapping.
- UI: row presentation per type (icon for the three fixed types, default icon
  for custom tags), color by level, params in the row detail; filter panel
  entries for type and level.
- Clearing: whatever already clears timeline data (session / clear-all) must
  also clear the new table.

### MCP / docs
- `docs/timeline.md` (new, plus `mkdocs.yml` nav), `protocol/timeline.md` and
  the plugin table in `protocol/README.md`, `docs/setup.md`, README snippet.
- MCP: not part of this change unless the builder finds the timeline already
  exposed there (then update `docs/mcp.md` and the flocon-debug skill).

### Timeline integration (decided after the builder check)
- Logs live in the timeline only for v1: **no dedicated Logs screen** (can come
  later, the table and protocol already allow it).
- Logs appear in **both the list and the graph**. In the graph they are
  instants (one marker per log, a new lane next to Network/Database/Analytics),
  not spans; screen-duration spans for navigation are a possible follow-up.
- Clicking a log row opens a **detail panel** (value, type, level, params,
  timestamp), reachable from the timeline like the other sources' panels. This
  is my call, the user only said "timeline for now".
- **No clear button** in v1; the log table is bounded by a retention cap and
  removed with the device/app data. This is my call.

## Left to the builder
- Exact module/package layout inside `FloconAndroid/timeline`.
- Icons and colors, empty/default styles.
- Whether existing docs live in `docs/` or `docs2/` (both exist in the repo;
  `docs/` is the one referenced by the project rules).
- Room schema/migration mechanics, retention cap for log rows (other tables cap
  their size, e.g. database logs).
- Test layout (SDK `jvmTest`, desktop `TimelineQueryBuilderTest`, `MapperTest`).
- Sample app wiring: a navigation example per platform.

## My calls (not discussed)
- Default level `Info` is added to the enum even though the user listed Debug,
  Warning, Error.
- Reserved-tag collision rule above.
- No custom styling and text-only params in v1.
- Detail panel only (no inline expand), no clear button, graph markers as
  instants (see Timeline integration).
- Builder-level decisions delegated: whether log rows count toward sessions,
  whether params are searchable by the text filter, whether Error level counts
  as an error in the summary.
