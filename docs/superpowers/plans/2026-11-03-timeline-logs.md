# Timeline logs Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let the mobile app push navigation, user action, lifecycle and custom log events into the Flocon timeline (list + graph) through a new `Flocon.timelinePlugin`.

**Architecture:** New SDK plugin `timeline` (+ no-op) sends a `log` message. The desktop stores it in a new `TimelineLogEntity` table, and the existing timeline `UNION ALL` gets one `KIND_LOG` branch exposing `TimelineSource.Log`, with `LogType` and `LogLevel` facets. Level reaches the row through the facet column (`u.facetLogLevel AS level`) so the other union branches stay untouched. Reference pattern: the feature flags plugin.

**Tech Stack:** Kotlin Multiplatform, kotlinx.serialization, Room, Compose Desktop, Koin, Gradle (JDK 21).

**Spec:** `docs/superpowers/specs/2026-11-03-timeline-logs-design.md`

## Global Constraints

- Plugin id `timeline`, method `log`, in BOTH `Protocol.kt` files (`FloconAndroid/flocon/flocon/src/commonMain/kotlin/io/github/openflocon/flocon/Protocol.kt`, `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/Protocol.kt`), identical names.
- Wire payload: `type`, `value`, `params` (optional, default empty), `level` (optional, default `info`), `timestamp`. Only new optional fields; older SDK/desktop must keep working.
- Reserved types `navigation`, `userAction`, `lifecycle`; anything else is a custom tag. Matching is case-insensitive on the desktop.
- Two separate calls, never mixed: typed `log(type: FloconTimelineType, value, params)` (no level; shortcuts `logNavigation`, `logUserAction`, `logLifecycle` call it with `FloconTimelineType.Navigation/UserAction/Lifecycle`) and tagged `log(tag: String, value, params, level = Info)`. Level enum `Debug, Info, Warning, Error` exists only on the tagged call.
- `FloconTimelineType` is a light wrapper of a `name: String` (value class on Kotlin, a struct `TimelineType` on Swift); its `name` is what goes on the wire.
- SDK plugin and no-op have identical packages, signatures and default parameters.
- Entities/wire models must not leak into `composeApp`; follow `.claude/rules/android-sdk.md` and `desktop-app.md`.
- Docs/protocol ship in the same PR (`.claude/rules/docs-protocol-sync.md`). No MCP change (no MCP tool exposes the timeline).
- Commits: `<type>: [AREA] summary`, no Claude attribution lines. Branch `feat/timeline-logs`. Build with JDK 21.

## Review Focus

- A `log` tag equal to `Navigation` / `NAVIGATION` is treated as the reserved navigation type (test in Task 2 mapper).
- Unknown or missing `level` string (older/newer SDK) falls back to `info`, no crash (Task 2).
- `params` with quotes, newlines and unicode round-trips through the JSON column unchanged (Task 2).
- Empty `value` and very long `value` still produce a row and do not break the union query/search (Task 3).
- Two logs with the same timestamp keep a stable order and both appear (Task 3).
- Log rows of another device/package/session are excluded (Task 3).

---

### Task 1: Protocol and SDK plugin (+ no-op, samples)

**Files:**
- Modify: both `Protocol.kt` (add `FromDevice.Timeline`), `FloconAndroid/settings.gradle.kts` (~l.43), `FloconAndroid/sample/android/build.gradle.kts` (~l.61 dependency, ~l.145 no-op mapping), samples `sample/{ios,desktop,wasm}` and `sample-multiplatform` build files and main files (`install(FloconTimeline)`), `sample-multiplatform/.../ui/SampleFeatures.kt:145` (a navigation example).
- Create: `FloconAndroid/timeline/timeline/` and `FloconAndroid/timeline/timeline-no-op/` (copy the `featureflags` layout and `build.gradle.kts`), `.../FloconTimelinePlugin.kt` in each.
- Test: `FloconAndroid/timeline/timeline/src/jvmTest/kotlin/.../FloconTimelinePluginTest.kt`.

**Interfaces:**
- Produces: `object Timeline { const val Plugin = "timeline"; object Method { const val Log = "log" } }` under `Protocol.FromDevice` in both files.
- Produces (SDK, both modules): `enum class FloconTimelineLevel { Debug, Info, Warning, Error }`; `@JvmInline value class FloconTimelineType(val name: String)` with companion `Navigation = FloconTimelineType("navigation")`, `UserAction = FloconTimelineType("userAction")`, `Lifecycle = FloconTimelineType("lifecycle")`; `interface FloconTimelinePlugin : FloconPlugin { fun log(type: FloconTimelineType, value: String, params: Map<String, String> = emptyMap()); fun log(tag: String, value: String, params: Map<String, String> = emptyMap(), level: FloconTimelineLevel = FloconTimelineLevel.Info); fun logNavigation(value: String, params: Map<String, String> = emptyMap()); fun logUserAction(...same); fun logLifecycle(...same) }` (the three shortcuts delegate to the typed `log`); `object FloconTimeline : FloconPluginFactory<FloconTimelineConfig, FloconTimelinePlugin>`; `val Flocon.Companion.timelinePlugin: FloconTimelinePlugin`. The no-op uses the literal `"timeline"` as plugin id.

- [ ] **Step 1: Write the failing tests** in `FloconTimelinePluginTest` (same shape as `FloconFeatureFlagsPluginTest`: fake `FloconMessageSender`, `Dispatchers.Unconfined`, assert plugin, method and exact JSON): `logNavigation_sendsReservedTypeAndNoLevel` (type `navigation`, value, params, no `level`), same for `userAction` and `lifecycle`; `log_typed_sendsTypeNameAndNoLevel` (`FloconTimelineType("billing")` → type `billing`, no `level`); `log_tag_sendsTagAndLevel` (type = tag, `level = "warning"`); `log_tag_defaultLevelIsInfo`; `log_emptyParamsOmittedOrEmpty` (params default empty).
- [ ] **Step 2: Run** `cd FloconAndroid && ./gradlew :timeline:timeline:jvmTest` — expect compile failure (module missing).
- [ ] **Step 3: Add the protocol constants** in both `Protocol.kt` (after FeatureFlags), register both modules in `settings.gradle.kts`, create the plugin: internal `@Serializable` payload (`type`, `value`, `params`, `level?`, `timestamp`), sent with `sender.send(plugin = Protocol.FromDevice.Timeline.Plugin, method = Protocol.FromDevice.Timeline.Method.Log, ...)` on a single-parallelism scope, like `FloconFeatureFlagsPluginImpl`.
- [ ] **Step 4: Create the no-op module** with the same public API, empty bodies; wire both modules into the samples (debug = real, release = no-op), add `install(FloconTimeline)` and one `logNavigation` call.
- [ ] **Step 5: Run** `./gradlew :timeline:timeline:jvmTest :timeline:timeline-no-op:compileKotlinJvm` — expect PASS.
- [ ] **Step 6: Commit** `feat: [TIMELINE] SDK plugin to log navigation, user actions, lifecycle and custom events` (stage the files by name).

### Task 2: Desktop ingestion (domain, data, Room)

**Files:**
- Create, mirroring the feature flags plugin file by file: `FloconDesktop/domain/.../timelinelog/` (`TimelineLogDomainModel`, `TimelineLogLevel`, `TimelineLogRepository`, `ObserveTimelineLogUseCase`, `DI.kt`, included from `domain/DI.kt`); `FloconDesktop/data/remote/.../timelinelog/` (`TimelineLogDataModel`, remote data source copied from `FeatureFlagsRemoteDataSourceImpl`, `DI.kt` included from the parent `DI.kt`); `FloconDesktop/data/core/.../timelinelog/` (`TimelineLogRepositoryImpl`, data source interfaces in `datasource/`, `DI.kt` like `data/core/featureflags/DI.kt`); `FloconDesktop/data/local/.../timelinelog/` (`TimelineLogEntity`, DAO, local data source, `DI.kt` included from the parent `DI.kt`).
- Modify: desktop `Protocol.kt` (Task 1), `composeApp/.../common/db/AppDatabase.kt` (add entity + DAO, `version = 92`, commit the new schema json), `RoomModule.kt`.
- Test: `data/core` mapper/repository test (`allTests`), `data/local` DAO/mapper test.

**Interfaces:**
- Produces: `data class TimelineLogDomainModel(id: Long, timestamp: Long, type: String, level: TimelineLogLevel, value: String, params: Map<String, String>)`; `enum class TimelineLogLevel { Debug, Info, Warning, Error }`; `TimelineLogRepository.observeLog(id: Long): Flow<TimelineLogDomainModel?>`; `TimelineLogEntity(id autoGenerate, deviceId, packageName, appInstance, timestamp, type, level, value, paramsJson)` with a CASCADE foreign key to `DeviceAppEntity` (like `AnalyticsItemEntity`) and an index on (deviceId, packageName, timestamp); `TimelineLogRepositoryImpl : TimelineLogRepository, MessagesReceiverRepository` with `pluginName = listOf(Protocol.FromDevice.Timeline.Plugin)`.

- Storage format (must match the facet known values exactly, Task 3): `level` is stored as the upper-case enum name (`DEBUG|INFO|WARNING|ERROR`); a reserved type is stored in its canonical casing (`navigation`, `userAction`, `lifecycle`), never lower-cased.
- Retention: after each insert, trim the table to the newest `MAX_TIMELINE_LOGS` (20_000) rows per (deviceId, packageName), in the DAO.

- [ ] **Step 1: Write failing tests**: `mapsReservedTypeCaseInsensitively` (`"NAVIGATION"` and `"useraction"` → stored `navigation` and `userAction`), `levelStoredAsUpperCaseEnumName`, `unknownLevelFallsBackToInfo`, `missingLevelFallsBackToInfo`, `customTagIsKeptAsIs`, `paramsRoundTripWithQuotesNewlinesUnicode`, `insertTrimsOldestRowsBeyondCap`.
- [ ] **Step 2: Run** `cd FloconDesktop && ./gradlew :data:core:allTests :data:local:allTests` — expect FAIL.
- [ ] **Step 3: Implement** the domain, remote decoding (`params` default empty, `level` optional), repository (insert one entity per message), entity/DAO/local data source; register in `AppDatabase` (destructive fallback is used, no migration) and `RoomModule`, add Koin bindings.
- [ ] **Step 4: Run** the same command plus `./gradlew :composeApp:compileKotlinDesktop` — expect PASS.
- [ ] **Step 5: Commit** `feat: [TIMELINE] Store the logs sent by the app`.

### Task 3: Timeline query, mapper, facets

**Files:**
- Modify: `FloconDesktop/data/local/.../timeline/datasource/TimelineQueryBuilder.kt` (add `KIND_LOG = "log"`, `MAX_TIMELINE_LOGS = 20_000`, the branch with `orderLimit`, `appInstanceColumn = "appInstance"`, `timeColumn = "timestamp"`, `textColumns` value + type, title = value, subtitle = type, facet columns type and level; add `"TimelineLogEntity"` to `SESSION_TABLES`; `NULL AS level` in `EMPTY_ROWS`; `u.facetLogLevel AS level` in the outer select ~l.446), `TimelineRowEntity.kt` (+`level: String? = null`), `Mapper.kt`, `TimelineDao.kt` (add `TimelineLogEntity::class` to every `observedEntities` list incl. `observeFacetCounts`), domain `TimelineSource` (+`Log`), `TimelineFacet` (+`LogType` with navigation/userAction/lifecycle, `LogLevel` with DEBUG/INFO/WARNING/ERROR), `TimelineEventDomainModel` (+`level: TimelineLogLevel? = null`). The log branch computes `hasError` in SQL (`level = 'ERROR'`), so the list and the graph share it.
- Test: `TimelineQueryBuilderTest` (session params 3 → 4 pairs at l.22, `FROM TimelineLogEntity`), `MapperTest`.

**Interfaces:**
- Consumes: Task 2 entity. Produces: `TimelineSource.Log`; `TimelineEventDomainModel.level: TimelineLogLevel?`.

- [ ] **Step 1: Write failing tests**: `logBranchPresentWhenSourceSelected`, `logRowsScopedToDeviceAndPackage`, `logRowsUseTitleValueSubtitleType`, `levelFacetFiltersRows` (stored `ERROR` matches the `LogLevel` known value), `typeFacetFiltersRows` (stored `userAction` matches), `errorLevelRowHasError`, `customTagAppearsAsLogTypeFacetValueAndFilters` (a `billing` tag is listed in the facet counts and filters its rows), `emptyValueStillProducesRow`, `sameTimestampBothRowsReturnedInStableOrder`, `mapperMapsLogKindToSourceLogWithLevel`.
- [ ] **Step 2: Run** `./gradlew :data:local:allTests` — expect FAIL.
- [ ] **Step 3: Implement** the branch, session handling, mapping and DAO observation exactly as listed in Files.
- [ ] **Step 4: Run** `./gradlew :data:local:allTests :domain:allTests` — expect PASS.
- [ ] **Step 5: Commit** `feat: [TIMELINE] Log source in the timeline query`.

### Task 4: Timeline UI (list, filters, detail panel)

**Files:**
- Modify: `FloconColors.kt:105` (+`EventCategory.Log`), `TimelineCategory.kt:25`, `TimelineRow.kt:71` and `chipSubtitles` ~l.89, `TimelineFilterPanel.kt:596`, `mapper/Destination.kt:29` and `RowNavigation.kt`, `features/timeline/Navigation.kt` (register `TimelineRoutes.LogDetail` with `panel()` + `keepMenu()`), `features/timeline/DI.kt` (register the detail ViewModel), `RowNavigation.kt` `linkKey()` (log rows fall into the default `else`: no linking of repeated logs, on purpose), `TimelineRowUiModel` (log UI model: icon per type, color per level, default icon for custom tags), `strings.xml`, `featuredoc` only if it enumerates sources.
- Create: `TimelineLogDetailScreen.kt` and its ViewModel in `features/timeline/` (value, type, level, params, timestamp).
- Test: `DestinationTest`, `FilterPanelMapperTest`, `SummaryMapperTest`, a mapper test for icon/color.

**Interfaces:**
- Consumes: `ObserveTimelineLogUseCase` (Task 2), `TimelineSource.Log` and facets (Task 3). Produces: `TimelineRoutes.LogDetail(id: Long) : PanelRoute` with `pinGroup = PanelPinGroups.Timeline`.

- [ ] **Step 1: Write failing tests**: `destinationForLogRowIsLogDetail`, `filterPanelListsLogTypeAndLevelFacets`, `summaryCountsLogRows`, `fixedTypesGetDedicatedIconCustomGetsDefault`, `warningAndErrorLevelsGetDistinctColors`.
- [ ] **Step 2: Run** `./gradlew :composeApp:allTests` — expect FAIL.
- [ ] **Step 3: Implement** every exhaustive `when` listed above, the detail screen and strings; follow `.claude/rules/compose.md` (stateless screen, previews).
- [ ] **Step 4: Run** `./gradlew :composeApp:allTests ktlintFormat` — expect PASS; launch the desktop app with the sample and check the list and panel visually.
- [ ] **Step 5: Commit** `feat: [TIMELINE] Show logs in the list with a detail panel`.

### Task 5: Graph lane

**Files:**
- Modify: `graph/GraphUiModel.kt:11` (+`Log(TimelineSource.Log)` in `GraphLane`), `GraphMapper.kt` (instants via the existing `else` branch; `hasError` already comes from the SQL, Task 3), `previewGraphEvents()`.
- Test: `GraphPackingTest` (l.34 all lanes present), a `GraphMapper` test.

- [ ] **Step 1: Write failing tests**: `logLaneIsPresent`, `logEventBecomesInstantMarker`, `errorLevelLogMarkedAsError`.
- [ ] **Step 2: Run** `./gradlew :composeApp:allTests` — expect FAIL.
- [ ] **Step 3: Implement** the lane and mapping.
- [ ] **Step 4: Run** the same command — expect PASS; check the graph in the running app.
- [ ] **Step 5: Commit** `feat: [TIMELINE] Logs as instants in the graph`.

### Task 6: Docs

**Files:**
- Create: `protocol/timeline.md` (payload, reserved types, defaults, compatibility), `docs/timeline.md`.
- Modify: `protocol/README.md` (plugin table), `mkdocs.yml` (nav), `docs/setup.md`, root `README.md` snippet.

- [ ] **Step 1: Write the docs** from the spec (API, examples for a navigation listener, levels, reserved types, v1 limits, and how to define a reusable custom type such as `billing` with a `FloconTimelinePlugin.logBilling(...)` extension calling `log("billing", ...)`, plus the Swift equivalent).
- [ ] **Step 2: Run** `mkdocs build --strict` — expect no warnings.
- [ ] **Step 3: Commit** `docs: [TIMELINE] Document the timeline logs plugin`.

### Task 7: iOS SDK (separate repository `openflocon/FloconSwift`)

Not in this repo: a separate clone, branch and PR (the user asked not to forget it). Do it after Tasks 1 to 3 so the wire format is final.

- Add a `.timeline` plugin (installed like `.featureFlags` via `flocon.install(.timeline)`) and the accessor `Flocon.timelinePlugin`, with the same API as the Kotlin one: a struct `TimelineType(name:)` with static `.navigation`, `.userAction`, `.lifecycle`; `log(type:, value:, params:)` (no level), the shortcuts `logNavigation(_ value:, params:)`, `logUserAction`, `logLifecycle`, and `log(tag:, value:, params:, level:)` with `level` default `.info` (`debug | info | warning | error`). Calls on a non-installed plugin do nothing, like the others.
- Send plugin `timeline`, method `log`, payload exactly as in `protocol/timeline.md` (`type`, `value`, `params`, `level` only for `log`, `timestamp`).
- Tests in the repo's own style: helpers send the reserved type and no level; `log` sends the tag and level; default level is info.
- Update that repo's README (plugin list and install snippet). Mention the iOS API in this repo's `docs/timeline.md` and `docs/setup.md` (Task 6).

### Final checks (before any PR)

- [ ] `cd FloconAndroid && ./gradlew :sample:android:assembleDebug :sample:android:assembleRelease` (debug = real, release = no-op).
- [ ] Run the `reviewer` agent on the diff; fix or justify findings.
- [ ] Do not open a PR unless asked (draft by default).
