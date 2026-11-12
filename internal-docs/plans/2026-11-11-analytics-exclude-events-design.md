# Analytics: exclude events by name

## Intent
Analytics lists every event the app sends; noisy ones (heartbeats, scroll, etc.) bury the useful ones. Let the user hide events by name, from a right-click on a row, and manage/restore them from the top bar. Exclusions must survive an app restart.

## Goals
- Right-click a row → "Exclude '<eventName>'" hides all events with that name.
- Top bar chip "N excluded" (only when N > 0) opens a popup listing excluded names, each with a restore button, plus "Restore all".
- Persisted across restarts, saved per app (package name): switching app shows that app's own exclusions.

## Non-goals
- Excluding by property value (names only, decided).
- Per-device scoping: exclusions are per app package, shared across devices running that app.
- Include/exclude modes, regex, or wildcards.

## Anti-goals
- Exclusions silently deleting data: events are only hidden from the list, never removed from the database.
- A list that is hard to review: hidden names must always be visible and reversible from the top bar.

## Constraints
- Layering per `.claude/rules/desktop-app.md`: domain repository + use cases, impl in `composeApp/.../core/data/settings`, Koin registration.
- Compose rules per `.claude/rules/compose.md`: strings in resources, `FloconTheme` spacing, `ImmutableList`, previews.
- The list is currently filtered in the data layer (`observeCurrentDeviceAnalyticsContentUseCase(filter = ...)`, paged), so the exclusion has to reach that query; filtering after paging would break page sizes.

## Approaches
1. **In-memory in `AnalyticsViewModel`** (like Network). Simplest, but lost on restart: rejected, persistence is required.
2. **Persisted in settings, as a set of names** (like `hiddenMenuItems` in `SettingsDataSourcePrefs`). Small, no Room migration. **Chosen.**
3. **Persisted in Room.** Overkill for a flat list of strings, needs a schema bump: rejected.

## Design
- **Domain**: `AnalyticsPreferencesRepository` with `observeExcludedEventNames(packageName): Flow<Set<String>>`, `setExcludedEventNames(packageName, names)`; use cases `ObserveExcludedAnalyticsEventNamesUseCase`, `ExcludeAnalyticsEventNameUseCase`, `RestoreAnalyticsEventNamesUseCase` (one name or all).
- **Data**: impl next to `MenuPreferencesRepositoryImpl`; new key in `SettingsDataSource` / `SettingsDataSourcePrefs`. Stored as one JSON map `packageName -> set of names` (as `timeline_settings` does), updated under a mutex; not `ID_SEPARATOR` joins, since names can contain commas. The package comes from `observeCurrentDeviceIdAndPackageNameUseCase`, so `rows` re-collects when the app or the set changes.
- **Query**: pass the excluded set into the analytics content query (`NOT IN`), combined with the existing text filter. The flow of `rows` in `AnalyticsViewModel` re-collects when the set changes.
- **UI**: new `AnalyticsAction.ExcludeEvent(name)` and a "Exclude" item in `contextualActions()` (`AnalyticsRow.kt`); an excluded-count chip next to `FilterBar` in `AnalyticsScreen.kt` opening a popup (restore one / all). New strings in `composeResources`.
- **Docs**: `docs2/analytics.md` gets a short section.

## Tests
- Settings store round-trip (incl. names with commas, and two packages kept separate).
- Query test: excluded names are filtered and combine with the text filter.

## Left to the builder
- Exact DAO query shape and where the exclusion parameter is threaded through (use case → repository → datasource).
- Chip/popup visual details, using existing design system components.
