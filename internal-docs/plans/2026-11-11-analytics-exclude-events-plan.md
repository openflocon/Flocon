# Analytics: exclude events by name — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Hide Analytics events by name from a row right-click, restore them from a top-bar chip, persisted per app package.

**Architecture:** Exclusions live in the settings store (one JSON blob `packageName -> names`, like `timeline_settings`), exposed through a domain `AnalyticsPreferencesRepository` implemented in `composeApp/core/data/settings` (like `MenuPreferencesRepositoryImpl`). `ObserveCurrentDeviceAnalyticsContentUseCase` combines the current package's excluded names into the paged Room query (`NOT IN`). The UI adds a context-menu item and a chip + popup.

**Tech Stack:** Kotlin Multiplatform, Compose Desktop, Room paging, multiplatform-settings (`flowSettings`), Koin, kotlinx.serialization.

**Spec:** `internal-docs/plans/2026-11-11-analytics-exclude-events-design.md`

## Global Constraints

- Layering per `.claude/rules/desktop-app.md`; every new class registered in its layer's `DI.kt`.
- Compose per `.claude/rules/compose.md`: strings in `composeResources/values/strings.xml`, `FloconTheme.spacing/sizes/colors`, `@Immutable` UI models, `ImmutableList`, private `@Preview` using `FloconTheme { }`.
- Exclusions only hide rows; never delete data. Export CSV and MCP tools keep seeing every event (my call: `getItems`/`searchAnalyticsItems` untouched).
- Names only (no property values). Per package, shared across devices.
- Settings JSON, not `ID_SEPARATOR` joins (names may contain commas).
- JDK 21 for Gradle (`JAVA_HOME` = zulu-21). Do not commit unless the user asks; branch/commit format via the `git-setup` skill (current branch is `claude/...`, which must be renamed `feat/analytics-exclude-events`).
- User docs go in `docs2/` only, never `docs/`.

## Review Focus

- Event name with commas, quotes or unicode: round-trips through settings and still excludes exactly that name.
- Excluding the last visible event: list shows the existing empty state, chip still visible so it can be restored.
- Same name excluded in app A: still visible in app B.
- Switching app/device: list and chip follow the new package without restart.
- Empty excluded set: query behaves exactly as before (`NOT IN ()` valid in SQLite; verify).

---

### Task 1: Persisted exclusions (settings + domain repository + use cases)

**Files:**
- Create: `FloconDesktop/composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/core/data/settings/models/AnalyticsSettingsLocal.kt`
- Create: `.../core/data/settings/AnalyticsPreferencesRepositoryImpl.kt`
- Modify: `.../core/data/settings/datasource/local/SettingsDataSource.kt` (next to `timelineSettings`, l.54-57) and `SettingsDataSourcePrefs.kt` (next to l.105-120)
- Modify: `.../core/data/di/CoreDataModule.kt` (bind `AnalyticsPreferencesRepository`)
- Create: `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/analytics/repository/AnalyticsPreferencesRepository.kt`
- Create in `domain/.../analytics/usecase/`: `ObserveExcludedAnalyticsEventNamesUseCase.kt`, `ExcludeAnalyticsEventNameUseCase.kt`, `RestoreAnalyticsEventNameUseCase.kt`, `RestoreAllAnalyticsEventNamesUseCase.kt`
- Modify: `domain/.../analytics/DI.kt` (`factoryOf` for the four use cases)
- Test: `composeApp/src/commonTest/kotlin/io/github/openflocon/flocondesktop/core/data/settings/models/AnalyticsSettingsLocalTest.kt`

**Interfaces:**
- Produces:
  - `@Serializable data class AnalyticsSettingsLocal(val excludedEventNamesByPackage: Map<String, Set<String>> = emptyMap())` with `fun excluded(packageName: String): Set<String>`, `fun withExcluded(packageName: String, eventName: String): AnalyticsSettingsLocal`, `fun withRestored(packageName: String, eventName: String): AnalyticsSettingsLocal`, `fun withAllRestored(packageName: String): AnalyticsSettingsLocal` (a package with no names left is dropped from the map).
  - `SettingsDataSource.analyticsSettings: Flow<AnalyticsSettingsLocal>` and `suspend fun updateAnalyticsSettings(transform: (AnalyticsSettingsLocal) -> AnalyticsSettingsLocal)`; key `"analytics_settings"`, own `Mutex`, decode failure → default (copy the timeline implementation).
  - `interface AnalyticsPreferencesRepository { fun observeExcludedEventNames(packageName: String): Flow<Set<String>>; suspend fun excludeEventName(packageName: String, eventName: String); suspend fun restoreEventName(packageName: String, eventName: String); suspend fun restoreAllEventNames(packageName: String) }`
  - `ObserveExcludedAnalyticsEventNamesUseCase`: `operator fun invoke(): Flow<Set<String>>` (current package; `emptySet()` when no device selected).
  - `ExcludeAnalyticsEventNameUseCase`, `RestoreAnalyticsEventNameUseCase`: `suspend operator fun invoke(eventName: String)`; `RestoreAllAnalyticsEventNamesUseCase`: `suspend operator fun invoke()`. All no-op when no current device/package.

- [ ] **Step 1: Write `AnalyticsSettingsLocalTest`** with tests: `excluding a name with commas and quotes round-trips through Json` (encode/decode with `Json { ignoreUnknownKeys = true }`, assert `excluded("a.b") == setOf("x,y", "say \"hi\"")`); `two packages are kept apart` (exclude "e" in `app.a`, assert `excluded("app.b")` is empty); `restoring the last name drops the package key` (assert map is empty); `restoring all clears only that package`.
- [ ] **Step 2: Run** `cd FloconDesktop && ./gradlew :composeApp:desktopTest --tests '*AnalyticsSettingsLocalTest*'` — expect FAIL (class missing).
- [ ] **Step 3: Implement `AnalyticsSettingsLocal`** as specified, then the `SettingsDataSource`/`Prefs` additions mirroring `timelineSettings`/`updateTimelineSettings`.
- [ ] **Step 4: Implement `AnalyticsPreferencesRepositoryImpl(localSettingsDataSource: SettingsDataSource)`** mapping each method to `analyticsSettings` / `updateAnalyticsSettings` (observe: `map { it.excluded(packageName) }.distinctUntilChanged()`); bind it in `CoreDataModule` with `singleOf(::...) { bind<AnalyticsPreferencesRepository>() }`.
- [ ] **Step 5: Implement the four use cases** resolving the current package through `ObserveCurrentDeviceIdAndPackageNameUseCase` the same way `RemoveOldSessionsAnalyticsUseCase` does; register in `DI.kt`.
- [ ] **Step 6: Run** the test again plus `./gradlew :domain:compileKotlinDesktop :composeApp:compileKotlinDesktop` — expect PASS / BUILD SUCCESSFUL. Fix any `SettingsDataSource` fake the compiler reports.

### Task 2: Apply exclusions to the paged query

**Files:**
- Modify: `FloconDesktop/data/local/.../analytics/dao/FloconAnalyticsDao.kt` — `observeAnalyticsItems` only (l.~60-87)
- Modify: `data/local/.../analytics/datasource/AnalyticsLocalDataSourceRoom.kt` (`observe`, l.~47) and its interface in `data/core/.../analytics/datasource/`
- Modify: `data/core/.../analytics/repository/AnalyticsRepositoryImpl.kt:57` and `domain/.../analytics/repository/AnalyticsRepository.kt:13`
- Modify: `domain/.../analytics/usecase/ObserveCurrentDeviceAnalyticsContentUseCase.kt`
- Test: `domain/src/commonTest/kotlin/io/github/openflocon/domain/analytics/usecase/ObserveCurrentDeviceAnalyticsContentUseCaseTest.kt`

**Interfaces:**
- Consumes: `AnalyticsPreferencesRepository.observeExcludedEventNames(packageName)` (Task 1).
- Produces: `AnalyticsRepository.observeAnalytics(deviceIdAndPackageName, analyticsTableId, filter: String?, excludedEventNames: Set<String>)`; same extra parameter on the datasource `observe`; DAO `observeAnalyticsItems(..., filter: String?, excludedEventNames: List<String>)` with `AND eventName NOT IN (:excludedEventNames)` before `ORDER BY`.
- `ObserveCurrentDeviceAnalyticsContentUseCase` gains constructor param `analyticsPreferencesRepository: AnalyticsPreferencesRepository`; for the current package it `flatMapLatest`s over `observeExcludedEventNames(packageName)` and passes the set down, so a change of the set re-queries.

- [ ] **Step 1: Write `ObserveCurrentDeviceAnalyticsContentUseCaseTest`** (fake `AnalyticsRepository` recording the last `excludedEventNames`, fake `AnalyticsPreferencesRepository` backed by a `MutableStateFlow<Set<String>>`): `passes the excluded names of the current package to the repository` (assert recorded set equals `setOf("heartbeat")`) and `re-queries when the excluded set changes` (update the flow to `emptySet()`, assert the recorded set is empty). Build the real `ObserveCurrentDeviceIdAndPackageNameUseCase` over a fake of its dependency; copy how other domain tests do it.
- [ ] **Step 2: Run** `./gradlew :domain:desktopTest --tests '*ObserveCurrentDeviceAnalyticsContentUseCaseTest*'` — expect FAIL (signature).
- [ ] **Step 3: Thread the parameter** DAO → datasource (Room + interface) → repository (impl + interface) → use case as specified; convert `Set` to `List` at the datasource boundary. `getAnalyticsItems`, `searchAnalyticsItems` and CSV export stay unchanged.
- [ ] **Step 4: Run** the test again, then `./gradlew :data:core:allTests :data:local:allTests :domain:allTests` — expect PASS (Room validates the new SQL at compile time; fix any fake of `AnalyticsRepository`/datasource).

### Task 3: UI (context menu item, chip, popup)

**Files:**
- Modify: `FloconDesktop/composeApp/.../features/analytics/model/AnalyticsAction.kt`
- Modify: `.../analytics/AnalyticsViewModel.kt` (inject the four use cases from Task 1; `onAction` branches l.~144-170)
- Modify: `.../analytics/view/AnalyticsRow.kt` (`contextualActions()`, l.~139)
- Modify: `.../analytics/view/AnalyticsScreen.kt` (top bar actions near `FilterBar`, l.~157-195)
- Create: `.../analytics/view/AnalyticsExcludedEvents.kt`
- Modify: `composeApp/src/commonMain/composeResources/values/strings.xml`

**Interfaces:**
- Consumes: Task 1 use cases.
- Produces:
  - `AnalyticsAction.ExcludeEvent(val item: AnalyticsRowUiModel)`, `AnalyticsAction.RestoreEvent(val eventName: String)`, `data object AnalyticsAction.RestoreAllEvents`.
  - `AnalyticsViewModel.excludedEventNames: StateFlow<ImmutableList<String>>` (sorted, `stateInWhileSubscribed(persistentListOf())`), built from `ObserveExcludedAnalyticsEventNamesUseCase`.
  - `@Composable fun AnalyticsExcludedEvents(eventNames: ImmutableList<String>, onRestore: (String) -> Unit, onRestoreAll: () -> Unit, modifier: Modifier = Modifier)`: renders nothing when the list is empty; otherwise a chip "N excluded" opening a popup with one row per name (restore button) and a "Restore all" button. Uses design system components (`library/designsystem/components`) and `FloconTheme` tokens.
  - Strings: `analytics_exclude_event` (`Exclude "%1$s"`), `analytics_excluded_count` (`%1$d excluded`), `analytics_restore_all`, `analytics_restore_event` (content description).

- [ ] **Step 1: Add the actions and VM wiring** (`onAction` calls `ExcludeAnalyticsEventNameUseCase(action.item.<eventName field>)`, `RestoreAnalyticsEventNameUseCase`, `RestoreAllAnalyticsEventNamesUseCase`); register new ctor params via the existing `DI.kt` (`viewModelOf`, check it).
- [ ] **Step 2: Add the "Exclude" item** to `contextualActions()` in `AnalyticsRow.kt`, using the same item builder as Remove, label from `analytics_exclude_event`, dispatching `ExcludeEvent(item)`.
- [ ] **Step 3: Create `AnalyticsExcludedEvents`** with a private `AnalyticsExcludedEventsPreview` (a few names, inside `FloconTheme { }`); place it in `AnalyticsScreen.kt` right after `FilterBar`, collecting `excludedEventNames` in the stateful overload and passing `viewModel::onAction`-based lambdas down.
- [ ] **Step 4: Verify** `./gradlew :composeApp:compileKotlinDesktop ktlintFormat` — expect BUILD SUCCESSFUL.
- [ ] **Step 5: Manual check** with the `flocon-mock-mode` skill on an analytics scenario: right-click → Exclude hides all rows of that name, chip shows the count, restore brings them back, restart keeps them, another app package is unaffected, excluding every event shows the empty state with the chip still visible.

### Task 4: Docs

**Files:**
- Modify: `docs2/analytics.md`

- [ ] **Step 1: Add a short "Exclude events" section** (right-click → Exclude, chip to restore, saved per app, export CSV still includes everything). No `docs/` changes, no protocol change (desktop only).
- [ ] **Step 2: Run** `mkdocs build --strict` — expect no errors.
- [ ] **Step 3: Final checks:** `cd FloconDesktop && ./gradlew :domain:allTests :data:core:allTests :data:local:allTests :composeApp:allTests ktlintCheck` — expect PASS.

## Self-review
- Spec coverage: right-click item (T3.2), chip + popup + restore all (T3.3), per-package persistence (T1), query-level filtering (T2), docs (T4). Name-only, no deletion, export untouched (Global Constraints).
- Deviation from the design doc: repository exposes `excludeEventName`/`restoreEventName`/`restoreAllEventNames` instead of a single `setExcludedEventNames`, which keeps the read-modify-write inside the settings `Mutex`.
