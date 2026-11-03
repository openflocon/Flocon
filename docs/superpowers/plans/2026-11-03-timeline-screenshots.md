# Timeline screenshots Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Take a screenshot of the connected app every 5 to 30 s while an optional setting is on, and show them in the timeline graph (strip above the time axis, hover popover) and list (marker rows).

**Architecture:** A desktop-side recorder loop (domain) reuses `TakeScreenshotUseCase`, converts to JPEG and stores files in a temp directory through a repository; settings live with the other timeline settings; the graph and list read the captures by timestamp. No SDK or protocol change.

**Tech Stack:** Kotlin Multiplatform (desktop/JVM), Compose Desktop, Koin, kotlinx-coroutines, Coil (already set up), `javax.imageio`.

**Spec:** `docs/superpowers/specs/2026-11-03-timeline-screenshots-design.md` (mockups: https://claude.ai/artifact/2VQ9J7j3muS3Y6yV4EztLW, but ignore their "Navigation" lane: it does not exist).

## Global Constraints

- Work in `FloconDesktop/`, JDK 21 (`JAVA_HOME` = the zulu-21 install; the default JDK 22 breaks `compileKotlinDesktop`).
- Branch: rename the current branch to `feat/timeline-screenshots` before the first commit (`git branch -m feat/timeline-screenshots`); never `claude/...`.
- Commits: `feat: [TIMELINE] <imperative summary>` (other prefixes: `test`, `refact`); one logical change each; no attribution lines; never push or open a PR unless asked.
- No change to `Protocol.kt` (either copy), the SDK, the MCP, or `protocol/`. No page in `docs/`.
- `TakeScreenshotUseCase` default behavior must not change (used by `AppViewModel` and the MCP `ScreenCapturer`).
- Intervals: 5, 10, 15, 30 s; default 10 s; feature default off. A stored interval outside the list means 10.
- JPEG: 640 px high (aspect kept), quality 0.6. Cap: 300 files per device, oldest deleted first.
- Files: `<java.io.tmpdir>/flocon-timeline-screenshots/<deviceId>/<package>_<epochMs>.jpg`; whole directory deleted at app launch, before the recorder starts. Never under the Flocon documents path.
- Turning the feature off stops capturing but keeps existing captures visible until next launch.
- Warning dot in the header after 3 failed captures in a row, cleared by the next success.
- Graph lanes are Network, Database, Analytics only. List marker line leaves from the center of the thumbnail (one constant).
- Hover popover only when a capture exists within 2 intervals before the cursor.
- New behavior gets tests; never skip or `@Ignore` a test. If a check cannot run here (adb, simulator, real device), say so in the final report.
- `ktlintFormat` before each commit (the commit hook formats staged `.kt` files).

## Review Focus

- A capture started for device A that finishes after the user switched to device B is stored under A, never shown for B (Task 3 test).
- A capture slower than the interval skips ticks instead of piling up (Task 3 test).
- A file deleted from disk behind the app's back (cap, manual cleanup, temp cleaner) does not crash the strip, the popover or the list: the thumbnail is just empty (Task 2 test for the repository index; Task 7/8 builder check by hand).
- A list or graph with captures but no events in range (new session, filters hiding everything) shows no orphan marker and does not crash (Tasks 6 and 8 tests).
- A stored interval outside 5/10/15/30, or an old settings file without the new keys, loads as off / 10 s (Task 1 tests).

---

### Task 1: Screenshot settings (domain + storage)

**Files:**
- Modify: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/models/TimelineSettingsDomainModel.kt`
- Modify: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/repository/TimelinePreferencesRepository.kt`
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/usecase/SetTimelineScreenshotsUseCase.kt`
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/usecase/SetTimelineScreenshotIntervalUseCase.kt`
- Modify: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/DI.kt`
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/core/data/settings/models/TimelineSettingsLocal.kt`
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/core/data/settings/TimelinePreferencesRepositoryImpl.kt`
- Test: `domain/src/commonTest/kotlin/io/github/openflocon/domain/timeline/usecase/TimelineSettingsUseCasesTest.kt` (update its fake repository), `composeApp/src/commonTest/kotlin/io/github/openflocon/flocondesktop/core/data/settings/models/TimelineSettingsLocalTest.kt`

**Interfaces:**
- Produces:
  - `TimelineSettingsDomainModel(..., screenshots: Boolean = false, screenshotIntervalSeconds: Int = DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS)`
  - top-level in the same file: `val TimelineScreenshotIntervalsSeconds: List<Int> = listOf(5, 10, 15, 30)` and `const val DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS = 10`
  - `TimelinePreferencesRepository.setScreenshots(screenshots: Boolean)` and `setScreenshotIntervalSeconds(seconds: Int)` (both `suspend`)
  - `SetTimelineScreenshotsUseCase.invoke(enabled: Boolean)`, `SetTimelineScreenshotIntervalUseCase.invoke(seconds: Int)` (ignores a value not in `TimelineScreenshotIntervalsSeconds`)
  - `TimelineSettingsLocal` keys `screenshots` (default false) and `screenshot_interval_seconds` (default 10)

- [ ] **Step 1: Write the failing tests.** In `TimelineSettingsUseCasesTest`: `screenshots are off with a 10 s interval by default`; `the screenshots are set without changing the other options`; `the interval is set when it is one of 5, 10, 15, 30`; `an interval that is not allowed is ignored` (e.g. 7 and 0, state stays at 10). In `TimelineSettingsLocalTest`: `a settings file without the screenshot keys loads as off and 10 s` (decode `{}` and an old JSON with `graph` only through `Json { ignoreUnknownKeys = true }`); `a stored interval that is not allowed loads as 10 s`; `the screenshot options round trip through toLocal and toDomain`.
- [ ] **Step 2: Run to verify they fail.** `cd FloconDesktop && ./gradlew :domain:allTests :composeApp:allTests` (compile errors on the missing members are the expected failure).
- [ ] **Step 3: Implement** the model fields and constants, the two repository methods, the two use cases (same shape as `SetTimelineGraphUseCase`, registered with `factoryOf` in `timelineDomainModule`), the local fields with `@SerialName`, and both mappers (`toDomain` falls back to the default interval when the stored one is not allowed). `TimelinePreferencesRepositoryImpl` updates the two fields through `updateTimelineSettings`. Add the two methods to the test fake.
- [ ] **Step 4: Run the same command; expect all PASS.**
- [ ] **Step 5: Commit** `feat: [TIMELINE] Screenshot on/off and interval settings`

### Task 2: Screenshot storage and JPEG conversion

**Files:**
- Modify: `domain/src/commonMain/kotlin/io/github/openflocon/domain/device/usecase/TakeScreenshotUseCase.kt` (add `outputFile`)
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/screenshot/TimelineScreenshotDomainModel.kt`
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/screenshot/TimelineScreenshotRepository.kt`
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/screenshot/JpegConverter.kt`
- Create: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/core/data/timeline/TimelineScreenshotRepositoryImpl.kt`
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/core/data/di/CoreDataModule.kt`
- Test: `domain/src/commonTest/kotlin/io/github/openflocon/domain/timeline/screenshot/JpegConverterTest.kt`, `composeApp/src/commonTest/kotlin/io/github/openflocon/flocondesktop/core/data/timeline/TimelineScreenshotRepositoryImplTest.kt`

**Interfaces:**
- Produces:
  - `TakeScreenshotUseCase.invoke(target: DeviceIdAndPackageNameDomainModel? = null, outputFile: File? = null)`: when `outputFile` is set the PNG is written there instead of `Documents/Screenshot`; the returned path is that file
  - `data class TimelineScreenshotDomainModel(val timestamp: Long, val filePath: String, val deviceId: String, val packageName: String)`
  - `interface TimelineScreenshotRepository { fun observe(target: DeviceIdAndPackageNameDomainModel): Flow<List<TimelineScreenshotDomainModel>> /* oldest first */; fun newJpegFile(target: DeviceIdAndPackageNameDomainModel, timestamp: Long): File; suspend fun add(screenshot: TimelineScreenshotDomainModel); suspend fun purgeAll() }`
  - `fun convertPngToJpeg(png: File, jpeg: File, maxHeightPx: Int = 640, quality: Float = 0.6f)` (top-level in `JpegConverter.kt`; deletes nothing)
  - `TimelineScreenshotRepositoryImpl(rootDir: File = File(System.getProperty("java.io.tmpdir"), "flocon-timeline-screenshots"), maxPerDevice: Int = 300)`

- [ ] **Step 1: Write the failing tests.** `JpegConverterTest`: `a tall image is scaled to 640 px high keeping its ratio`; `an image shorter than 640 px is not enlarged`; `the output is a readable jpeg` (generate a `BufferedImage`, write a PNG to a temp file, convert, read back with `ImageIO`). `TimelineScreenshotRepositoryImplTest` (temp `rootDir`): `added screenshots are observed oldest first per device`; `the oldest files are deleted from disk and from the index past the cap` (cap 3, add 5); `a screenshot whose file was deleted from disk is dropped from the index on the next add` (Review Focus); `purgeAll deletes the directory and empties the index`; `two devices do not see each other's screenshots`.
- [ ] **Step 2: Run to verify they fail.** `./gradlew :domain:allTests :composeApp:allTests`.
- [ ] **Step 3: Implement.** `TakeScreenshotUseCase`: build the file path from `outputFile` when given, everything else unchanged. `convertPngToJpeg` with `ImageIO` and a `JPEGImageWriteParam` (draw into an RGB `BufferedImage` first so alpha does not break JPEG). Repository impl: index kept in a `MutableStateFlow<Map<deviceId+package, List<...>>>`, files under `rootDir/<deviceId>/`; `add` appends, drops entries whose file no longer exists, then enforces the cap deleting the oldest files. Bind it in `CoreDataModule.kt` like `TimelinePreferencesRepositoryImpl` (`singleOf` + `bind<TimelineScreenshotRepository>()`).
- [ ] **Step 4: Run the same command; expect PASS.** Also confirm `:mcp:allTests` still passes (`TakeScreenshotUseCase` signature change).
- [ ] **Step 5: Commit** `feat: [TIMELINE] Temporary screenshot storage and JPEG conversion`

### Task 3: Recorder

**Files:**
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/screenshot/TimelineScreenCapturer.kt`
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/screenshot/TimelineScreenshotRecorder.kt`
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/usecase/ObserveTimelineScreenshotsUseCase.kt`
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/usecase/ObserveTimelineScreenshotsFailingUseCase.kt`
- Create: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/usecase/PurgeTimelineScreenshotsUseCase.kt`
- Modify: `domain/src/commonMain/kotlin/io/github/openflocon/domain/timeline/DI.kt`
- Test: `domain/src/commonTest/kotlin/io/github/openflocon/domain/timeline/screenshot/TimelineScreenshotRecorderTest.kt`

**Interfaces:**
- Consumes: Task 1 settings (`TimelinePreferencesRepository.settings`), Task 2 repository and `convertPngToJpeg`, `ObserveCurrentDeviceIdAndPackageNameUseCase`, `ObserveIsCurrentDeviceIAndAppIsActiveUseCase`, `TakeScreenshotUseCase`.
- Produces:
  - `interface TimelineScreenCapturer { suspend fun capture(target: DeviceIdAndPackageNameDomainModel, jpegFile: File): Either<Throwable, Unit> }` and its production implementation `TimelineScreenCapturerImpl(takeScreenshotUseCase, ...)` that takes the PNG into a temp file next to `jpegFile`, converts, deletes the PNG
  - `class TimelineScreenshotRecorder(...)` with `val isFailing: StateFlow<Boolean>` and `suspend fun run()` (never returns; cancel to stop)
  - `ObserveTimelineScreenshotsUseCase.invoke(): Flow<List<TimelineScreenshotDomainModel>>` (current device's list, oldest first; empty when no device), `ObserveTimelineScreenshotsFailingUseCase.invoke(): StateFlow<Boolean>`, `PurgeTimelineScreenshotsUseCase.invoke()` (suspend)

- [ ] **Step 1: Write the failing tests** with fakes for the capturer, repository, settings and device flows, in virtual time (`kotlinx-coroutines-test` `runTest`; if the dependency is not in `domain`'s `commonTest`, add it through the version catalog first): `nothing is captured while the feature is off`; `nothing is captured while the device is not live`; `a capture is taken right away then every interval`; `changing the interval restarts the loop with the new one immediately`; `turning the feature off stops the loop and keeps stored screenshots`; `a capture slower than the interval skips ticks instead of queueing` (capture takes 2.5 intervals, count calls over 10 intervals); `a capture started for device A that finishes after switching to device B is stored under A` (Review Focus); `isFailing turns true after 3 failures in a row and back to false after a success`; `a failed capture does not stop the loop`.
- [ ] **Step 2: Run to verify they fail.** `./gradlew :domain:allTests`.
- [ ] **Step 3: Implement.** Recorder `run()`: combine settings (`screenshots`, `screenshotIntervalSeconds`), the current device and the live flag, `distinctUntilChanged`, `collectLatest`; when enabled and live, loop `capture -> add -> delay((interval - elapsed).coerceAtLeast(1 s))` sequentially. The screenshot is built from the target the capture was *started* for. Count consecutive failures into `isFailing` (>= 3). Register recorder as `singleOf`, capturer impl and the use cases in `timelineDomainModule` (`factoryOf` for use cases).
- [ ] **Step 4: Run the same command; expect PASS.**
- [ ] **Step 5: Commit** `feat: [TIMELINE] Periodic screenshot recorder`

### Task 4: Start the recorder with the app

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/app/AppViewModel.kt` (the `init` block near line 141)

**Interfaces:**
- Consumes: `PurgeTimelineScreenshotsUseCase`, `TimelineScreenshotRecorder` from Task 3 (inject the recorder directly: it is a long-lived singleton, not a use case).

- [ ] **Step 1: Wire.** In `init`, launch on `dispatcherProvider.data` (or the project's convention for such jobs): `purge()` first, then `recorder.run()`, in that order in the same coroutine so the recorder never starts before the purge ends.
- [ ] **Step 2: Compile and run existing tests.** `./gradlew :composeApp:allTests` (expect PASS, nothing new to assert here: the loop itself is covered in Task 3).
- [ ] **Step 3: Manual check** (note in the final report whether it was run): start the desktop app with a device connected, turn the setting on by editing the stored settings or after Task 5, see files appear under `<tmpdir>/flocon-timeline-screenshots/<deviceId>/`, restart the app, see the directory is gone.
- [ ] **Step 4: Commit** `feat: [TIMELINE] Start the screenshot recorder and purge old ones at launch`

### Task 5: Header control and view model state

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/model/TimelineUiModel.kt`
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/TimelineViewModel.kt`
- Create: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/view/ScreenshotsControl.kt`
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/view/TimelineHeader.kt`
- Modify: the string resources file of `composeApp` (where the `timeline_*` strings live; add to every locale file that has them)

**Interfaces:**
- Consumes: `SetTimelineScreenshotsUseCase`, `SetTimelineScreenshotIntervalUseCase`, `ObserveTimelineScreenshotsFailingUseCase`, `TimelineScreenshotIntervalsSeconds`.
- Produces:
  - `TimelineSettingsUiModel(..., screenshots: Boolean = false, screenshotIntervalSeconds: Int = 10)` and the matching fields in `previewTimelineSettingsUiModel()`
  - `TimelineAction.SetScreenshots(value: Boolean)`, `TimelineAction.SetScreenshotInterval(seconds: Int)`
  - `TimelineViewModel.screenshotsFailing: StateFlow<Boolean>`
  - `@Composable internal fun ScreenshotsControl(enabled: Boolean, intervalSeconds: Int, failing: Boolean, onAction: (TimelineAction) -> Unit, modifier: Modifier = Modifier)`

- [ ] **Step 1: Implement** the UI model fields, actions and their handling in `onAction` (`viewModelScope.launch { ... }` like `SetGraph`), `screenshotsFailing` (`WhileSubscribed(5_000)`, default false). `ScreenshotsControl`: camera icon, label, switch, interval dropdown listing 5/10/15/30 s (disabled look when off), warning dot with tooltip when `failing`. Place it in `TimelineHeader` next to the existing controls; follow `compose.md` (state hoisting, design system components, a `@Preview` for on, off and failing).
- [ ] **Step 2: Verify.** `./gradlew :composeApp:allTests` (PASS) and open the previews or run the app: toggling and changing the interval changes the stored settings immediately (Task 3 reacts without a restart).
- [ ] **Step 3: Commit** `feat: [TIMELINE] Screenshots switch and interval in the timeline header`

### Task 6: Graph: screenshot mapping, thinning, nearest (pure logic)

**Files:**
- Create: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/graph/GraphScreenshots.kt`
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/graph/GraphUiModel.kt` (add `screenshots`)
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/TimelineViewModel.kt` (the `graph` flow)
- Test: `composeApp/src/commonTest/kotlin/io/github/openflocon/flocondesktop/features/timeline/graph/GraphScreenshotsTest.kt`

**Interfaces:**
- Consumes: `ObserveTimelineScreenshotsUseCase` (Task 3), `GraphViewport.xOf(ms: Long): Float` (existing, `GraphLayout.kt`).
- Produces:
  - `@Immutable internal data class GraphScreenshotUiModel(val timeMs: Long, val filePath: String)`
  - `GraphUiModel(..., val screenshots: List<GraphScreenshotUiModel> = emptyList())`
  - `internal fun screenshotsOfSession(shots: List<TimelineScreenshotDomainModel>, sessionStart: Long, nextSessionStart: Long?): List<GraphScreenshotUiModel>` (shots with `timestamp` in `[sessionStart, nextSessionStart)`, ascending, `timeMs = timestamp - sessionStart`)
  - `internal fun thinScreenshots(shots: List<GraphScreenshotUiModel>, viewport: GraphViewport, minGapPx: Float): List<GraphScreenshotUiModel>` (keeps the first, then each one whose `xOf` is at least `minGapPx` after the last kept)
  - `internal fun screenshotBefore(shots: List<GraphScreenshotUiModel>, timeMs: Long, maxGapMs: Long): GraphScreenshotUiModel?` (latest at or before `timeMs`; null when none within `maxGapMs`)

- [ ] **Step 1: Write the failing tests** (style of `GraphLayoutTest`): `only the screenshots of the session are kept`; `timeMs is relative to the session start`; `a screenshot before the session start is dropped`; `thinning keeps all when there is room`; `thinning drops the ones that would overlap and keeps the first`; `zooming in brings the dropped ones back`; `the nearest before the cursor is the latest at or before it`; `none is returned when the closest is older than the max gap`; `an empty list gives null and an empty strip` (Review Focus: no screenshots, none in range).
- [ ] **Step 2: Run to verify they fail.** `./gradlew :composeApp:allTests`.
- [ ] **Step 3: Implement** the three functions; in the VM `graph` flow combine the built graph with the screenshots flow and the session bounds (`sessionStart` and the next start in `starts`) to fill `GraphUiModel.screenshots`.
- [ ] **Step 4: Run the same command; expect PASS.**
- [ ] **Step 5: Commit** `feat: [TIMELINE] Map screenshots onto the graph session`

### Task 7: Graph: strip, hover popover, off hint

**Files:**
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/graph/TimelineGraph.kt` (and `GraphLayout.kt` for the new constants)
- Create: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/graph/ScreenshotStrip.kt` (strip and popover composables, keeps `TimelineGraph.kt` from growing)
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/TimelineScreen.kt` (pass `settings.screenshots`, interval and `onAction` down)
- Modify: string resources (the hint, popover texts)

**Interfaces:**
- Consumes: Task 5 settings and actions, Task 6 `GraphUiModel.screenshots`, `thinScreenshots`, `screenshotBefore`; existing `GraphViewport`, `hovered` handling and cursor in `TimelineGraph`.
- Produces: `ScreenshotStrip(...)` and `ScreenshotPopover(...)` composables; constants `ScreenshotStripHeightPx` (122 + padding), `ScreenshotThumbWidthPx` (58).

- [ ] **Step 1: Implement.** Strip row above the axis, same x scale (`viewport.xOf`) as the bars, thumbnails centered on their time, thinned with `ScreenshotThumbWidthPx + gap`; images loaded lazily with Coil at thumbnail size from `filePath`, empty box when the file is missing or fails to load. Vertical cursor line through strip and lanes at the hovered time; popover with the large capture, its `m:ss` time and "N s before the cursor" from `screenshotBefore(..., maxGapMs = 2 * interval)`. When `screenshots` is off, the strip is replaced by a dashed one-line hint with a "Turn on" button (`TimelineAction.SetScreenshots(true)`); when on but empty, a quiet "waiting for the first capture" line. Lanes, bars and zoom keep working; shift the lane area down by `ScreenshotStripHeightPx` only when the strip or hint is shown. Add `@Preview`s (on with captures, off).
- [ ] **Step 2: Verify.** `./gradlew :composeApp:allTests`; run the app on a device or simulator, look at: strip aligned with the axis at several zoom levels, hover popover, off hint, a deleted capture file (no crash). Say in the report if no device was available.
- [ ] **Step 3: Commit** `feat: [TIMELINE] Screenshot strip and hover preview in the graph`

### Task 8: List: marker rows

**Files:**
- Create: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/mapper/ScreenshotMarker.kt`
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/model/TimelineUiModel.kt` (new `TimelineItemUiModel.Screenshot`)
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/TimelineViewModel.kt` (the `items` flow)
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/mapper/` day separator code (`separatorBetween`) only as needed to accept the new item
- Create: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/features/timeline/view/ScreenshotMarkerRow.kt`
- Modify: `TimelineScreen.kt` (render the new item type)
- Test: `composeApp/src/commonTest/kotlin/io/github/openflocon/flocondesktop/features/timeline/mapper/ScreenshotMarkerTest.kt`

**Interfaces:**
- Consumes: `ObserveTimelineScreenshotsUseCase`, settings (`screenshots` is not needed to show existing captures), existing `separatorBetween(before, after, inverted)` and `insertSeparators` usage in `items`.
- Produces:
  - `internal fun screenshotBetween(shots: List<TimelineScreenshotDomainModel>, newerTimestamp: Long?, olderTimestamp: Long?): TimelineScreenshotDomainModel?`: the latest shot with `olderTimestamp <= timestamp < newerTimestamp`; `newerTimestamp = null` means "after the newest event" (`timestamp >= olderTimestamp`), `olderTimestamp = null` means "before the oldest" (`timestamp < newerTimestamp`); null when none; shots are ascending
  - `TimelineItemUiModel.Screenshot(val timestamp: Long, val filePath: String, override val key: String)`

- [ ] **Step 1: Write the failing tests:** `the latest capture between two events is returned`; `several captures in one gap give only the latest`; `a capture after the newest event is returned for the top of the list`; `a capture before the oldest event is returned for the bottom of the list`; `no capture in the gap gives null`; `no events at all means no marker` (Review Focus); `an inverted list swaps newer and older without changing which capture is picked`.
- [ ] **Step 2: Run to verify they fail.** `./gradlew :composeApp:allTests`.
- [ ] **Step 3: Implement** `screenshotBetween` and the glue in `items`: combine the paging flow with the screenshots flow, apply the marker `insertSeparators` first and the existing day-header `insertSeparators` second, making `separatorBetween` take the timestamp of a `Screenshot` neighbor so day headers stay correct. Map `before`/`after` to newer/older according to `invertList`. Confirm scroll position and the selected row survive a new capture arriving (the transform re-runs at most once per interval); if the selection resets, keep `cachedIn` after the combine.
- [ ] **Step 4: Implement `ScreenshotMarkerRow`:** thumbnail in a left gutter (58 px wide, extends below the line for the normal order, mirrored for `invertList`), a line starting at the center of the thumbnail (single constant `ScreenshotLineFromCenter = true`) across the row, label "Screenshot · m:ss"; events stay in their own column. Add a `@Preview`.
- [ ] **Step 5: Run the same test command; expect PASS.** Run the app and check the list with the feature on, off with existing captures, inverted list, and a filter that hides everything (no orphan marker).
- [ ] **Step 6: Commit** `feat: [TIMELINE] Screenshot markers in the timeline list`

### Task 9: Final checks

**Files:** none new.

- [ ] **Step 1:** `./gradlew ktlintFormat` then `./gradlew :domain:allTests :composeApp:allTests :mcp:allTests` and `./gradlew allTests assemble` (CI's command); expect PASS.
- [ ] **Step 2:** Review the diff against the spec's non-goals: no `Protocol.kt`, SDK or MCP change; `Documents/Flocon/Screenshot` untouched; nothing written outside the temp directory.
- [ ] **Step 3:** Report what was not verified (adb on a real Android device, iOS simulator, long-run memory/disk behavior) and what the manual checks showed.
- [ ] **Step 4:** Commit any formatting changes (`chore: [TIMELINE] Format`). Do not push or open a PR unless asked.
