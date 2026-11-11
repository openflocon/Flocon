# Telemetry: feature usage (network mocks, bad network, network detail, timeline, mascot)

Branch: `feat/desktop-telemetry` (PR openflocon/Flocon#595). Desktop only, nothing in `FloconAndroid/`.
Existing pieces: `TelemetryManager.track(event, properties)`, `TelemetryEvents`, `docs2/telemetry.md`,
`DeviceConnectionTelemetry` (example of a telemetry class kept next to the telemetry code).

## Questions to answer

1. Do people open the mocks list, the bad network list, the network detail panel?
2. Do they click "create" for a mock / a bad network config? Do they turn bad network on? How many mocks are enabled?
3. Which network detail actions are used (open externally, export markdown / html, copy cURL...)?
4. Timeline: share of graph vs list, auto-scroll on, screenshots on, merge plugins on.
5. How many people hide the mascot?

## Design

Two kinds of data, so the numbers are not biased by how often people click:

- **State, sent once per launch** as properties of `app_started` (answers "what share of launches use X").
- **Actions, sent when they happen** with two bounded events (answers "is X clicked").

### 1. `app_started` gets a snapshot of the settings

| Property | Values | Source |
|---|---|---|
| `timeline_view` | `graph`, `list` | timeline graph preference |
| `timeline_auto_scroll` | bool | timeline auto-scroll preference |
| `timeline_screenshots` | bool | timeline screenshots preference |
| `timeline_merge_plugins` | bool | timeline merge plugins preference |
| `mascot_enabled` | bool | `ObserveMascotEnabledUseCase` (first value) |
| `enabled_mocks_count` | int | enabled network mocks (current app; if the repository only exposes them per device/app, use the current device/app and say so in the doc) |
| `bad_network_enabled` | bool | a bad network config is selected as enabled |

Move the `app_started` call out of `AppViewModel` (23 constructor params already, explicit Koin wiring) into a new
`telemetry/AppStartedTelemetry.kt`: a class taking the observe/get use cases above + `TelemetryManager`, with
`suspend fun send()` reading the first value of each flow. Call it once from `TelemetryViewModel` `init`
(it lives as long as `AppScreen`, created once per launch). Keep `AppViewModel`'s `screen_viewed` tracking as is.
Each value read must not block the app: wrap in `withTimeoutOrNull` and leave the property out when it fails.

### 2. Sub-screens: reuse `screen_viewed`

Same event, new `screen` values (PascalCase like the menu ids): `NetworkMocks`, `NetworkBadQuality`, `NetworkDetail`.

- `NetworkMocksViewModel` and `BadQualityNetworkViewModel`: track in `init`.
- `NetworkDetailViewModel`: the panel opens for every clicked request, which would flood. Send it **at most once per
  launch** (a `Set<String>` of already sent screens in `TelemetryManager`, method `trackScreenOncePerLaunch(screen)`).

### 3. Actions: `feature_used`

One event, one bounded property `feature` (plus `value` when it carries a number or a bool). Values come from an enum,
never from user data:

```kotlin
enum class TelemetryFeature(val wireName: String) {
    MockCreateClicked("mock_create_clicked"),
    MockToggled("mock_toggled"),              // value = enabled (bool); also send enabled_mocks_count after the change
    MockImported("mock_imported"),
    MockExported("mock_exported"),
    BadNetworkCreateClicked("bad_network_create_clicked"),
    BadNetworkToggled("bad_network_toggled"), // value = enabled (bool)
    DetailOpenExternallyRequest("detail_open_externally_request"),
    DetailOpenExternallyResponse("detail_open_externally_response"),
    DetailShareMarkdown("detail_share_markdown"),
    DetailSaveHtml("detail_save_html"),
    DetailCopyCurl("detail_copy_curl"),
    DetailCopyText("detail_copy_text"),
    DetailCopyImage("detail_copy_image"),
    DetailSaveImage("detail_save_image"),
    DetailDiffWithClipboard("detail_diff_with_clipboard"),
    DetailJsonDetail("detail_json_detail"),
    DetailBearerJwt("detail_bearer_jwt"),
}
```

Add `TelemetryManager.featureUsed(feature: TelemetryFeature, value: Any? = null)` (value only Boolean or Int).

Every `feature_used` and `setting_changed` event also carries `screen`, with the same values as `screen_viewed`
(`NetworkMocks`, `NetworkBadQuality`, `NetworkDetail`, `Timeline`, `Settings`), so one breakdown by `screen` works
across the three events. The screen is a constructor field of each enum entry (`TelemetryFeature(wireName, screen)`,
`TelemetrySetting(wireName, screen)`), not a call-site parameter: it can't drift or be forgotten. Put the screen names
in one `TelemetryScreen` enum used by the three events. Add a test that each entry has a screen.

Where:
- `NetworkMocksViewModel.onAction`: `OnAddItemClicked`, `ChangeIsEnabled`, `OnImportClicked`/`OnExportClicked` (only on `Success`).
- `BadQualityNetworkViewModel`: `create()`, `setEnabledElement(id)` (enabled = `id != null`).
- `NetworkDetailViewModel` action handler: one feature per `NetworkDetailAction` subtype (exhaustive `when`, so a new
  action does not compile without a decision). Never the body, URL, token or text.

### 4. Settings changes: `setting_changed`

One event, `setting` (enum) + `value` (bool or the enum value):

```kotlin
enum class TelemetrySetting(val wireName: String) {
    TimelineView("timeline_view"),                    // "graph" / "list"
    TimelineAutoScroll("timeline_auto_scroll"),
    TimelineScreenshots("timeline_screenshots"),
    TimelineMergePlugins("timeline_merge_plugins"),
    MascotEnabled("mascot_enabled"),
}
```

Where:
- `TimelineViewModel.onAction`: `SetGraph`, `ToggleAutoScroll`, `SetScreenshots`, `SetMergePlugins`.
  It is built explicitly in `features/timeline/DI.kt` (more than 22 params): add `telemetryManager = get()` there.
- `SettingsViewModel.onMascotEnabledChange` (+ its explicit wiring in `app/ui/settings/DI.kt`).

### 5. Help clicks

Every help entry point opens a page of `FeatureDoc` (`common/ui/featuredoc/FeatureDoc.kt`), so the page tells the
screen. Event: `feature_used` with `feature = help_opened`, `screen = <FeatureDoc entry name>` (`Network`, `Database`,
`Timeline`...; `Setup` for `FeatureDoc.SETUP_URL`), and `source`:

| `source` | Entry point | Code |
|---|---|---|
| `top_bar` | (i) button at the top right of every page | `FeatureDocButton` (`FeatureDocViews.kt`, 13 call sites, all in top bars), and the private `DocumentationButton` of `features/files/view/FilesTopBar.kt` (overflow menu of Files) |
| `empty_state` | "How to set up" button of an empty page | `FeatureEmptyState` (`FeatureDocViews.kt`) |
| `empty_state_no_device` | "Getting started" link of the "no device connected" card | `NoDeviceNotice` (`FeatureDocViews.kt`) |

These are shared design composables: they must not get the `TelemetryManager` (Compose rules: no repository in child
composables). Provide the opening as a `CompositionLocal` instead, the same way `LocalUriHandler` works:

- `common/ui/featuredoc/FeatureDocOpener.kt`: `fun interface FeatureDocOpener { fun open(doc: FeatureDoc?, url: String, source: HelpSource) }`
  and `val LocalFeatureDocOpener = staticCompositionLocalOf<FeatureDocOpener> { ... }`. The default only opens the URL
  (previews, tests), so nothing breaks where it is not provided.
- `AppScreen` provides an opener that opens the URL with `LocalUriHandler` (keep the existing `runCatching`) and calls
  `TelemetryManager.featureUsed(...)`. Get the manager in `AppScreen` with `koinInject()` (entry point, allowed).
- The 4 composables above replace their `uriHandler.openUri(...)` with `LocalFeatureDocOpener.current.open(doc, url, source)`.
- `HelpSource` is an enum with the wire names above. `help_opened` gets its own `screen` from the doc, not from the
  `TelemetryFeature` entry: allow `featureUsed(feature, screen = ...)` to override the default screen for this one.
- Test: a fake opener records the calls; `FeatureDoc.entries` all map to a screen name.

## Privacy rules (check in review)

- Only enum wire names, booleans and counts. No mock name/URL/pattern, no bad network config values, no request data.
- Mock mode already disables telemetry (`mockModule` blanks the key): nothing to do.

## Docs and tests

- `TelemetryEvents`: add `FEATURE_USED`, `SETTING_CHANGED` with KDoc.
- `docs2/telemetry.md`: new rows for `feature_used`, `setting_changed`, the new `screen` values and the `app_started` properties.
- Tests (`composeApp/src/desktopTest/.../telemetry/`):
  - wire names are unique and snake_case (both enums);
  - `trackScreenOncePerLaunch` sends once;
  - `AppStartedTelemetry` with fake use cases: properties present, one failing source is left out, nothing sent when not opted in.
- Checks: `./gradlew :composeApp:desktopTest --tests '*Telemetry*' :composeApp:ktlintFormat` (JDK 21).

## PostHog (after the code is released, done over the PostHog MCP)

Add to the "Flocon Desktop usage" dashboard (id 1103873), one group per area:
- Network: `screen_viewed` for the 3 sub-screens; `feature_used` breakdown by `feature` filtered on `mock_*` / `bad_network_*`; detail actions bar chart.
- Timeline: pies on `app_started` by `timeline_view`, `timeline_screenshots`, `timeline_auto_scroll`, `timeline_merge_plugins`.
- Mascot: pie on `app_started` by `mascot_enabled`; `setting_changed` where `setting = mascot_enabled`.

## Out of scope

- Counting screenshots taken or viewed in the graph (only the on/off setting).
- Per-user funnels (Anonymous mode has no stable id).
