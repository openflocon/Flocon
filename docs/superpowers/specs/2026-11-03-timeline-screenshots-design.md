# Timeline screenshots: design

Status: draft, to be approved. Date: 2026-11-03. Area: FloconDesktop (timeline, graph and list). No SDK or protocol change.

## Intent

When reading the timeline, the user wants to see what the app looked like when a network call, a database insert or an analytics event happened. The desktop takes a screenshot of the connected app at a regular interval while the feature is on, and the timeline shows them next to the events: a strip above the time axis in the graph, marker lines in the list.

The goal is debugging a live session ("what was on screen when this call left"), not producing a recording or a bug report.

## Goals

- A screenshot of the current device every 5 to 30 s (default 10 s), only while the feature is on.
- The graph shows them in a strip above the axis, positioned by time. Hovering the graph shows the closest earlier screenshot in a popover.
- The list shows them as marker rows, in chronological order with the events, with a line saying when each was taken.
- On or off, and the interval, can be changed at any time from the timeline header and apply immediately. Default: off.
- The feature stays cheap: JPEG, downscaled, capped in count, nothing kept after the next app launch.

## Non-goals

- No screenshot taken from the SDK, no new protocol message, no change to `Protocol.kt` (either copy).
- No exact "screenshot at the time of this event". Captures are periodic, so the link is by timestamp and can be several seconds off. The UI does not claim otherwise.
- No capture on navigation: the navigation events do not exist yet. Revisit when they do.
- No click on an event to open its screenshots (explicitly declined). No full-size viewer, no "save as screenshot" in v1.
- No video or screen recording, no MCP tool, no export of the captures, no sharing.
- No capture of devices other than the one selected in the app.

## Anti-goals

- A slowdown of the app or of the device while capturing. If a capture takes longer than the interval, ticks are skipped, never queued.
- Screenshots that outlive the session. They may contain private data from the debugged app and must never land in the user's Documents folder, the exports or the MCP.
- Mixing them with the screenshots the user asks for (`Documents/Flocon/Screenshot`).
- Hiding the fact that `screencap` captures the whole device screen, not only the debugged app: with the app in the background the captures show other apps. Accepted for v1; the temp directory and the purge at launch are the mitigation, and the feature is off by default.

## Constraints

- Capture is `TakeScreenshotUseCase` today: `adb shell screencap -p`, `adb pull`, `adb shell rm` on Android, the booted simulator on iOS (see `IosSimulatorRepository`). One capture costs around half a second, which is why the minimum interval is 5 s.
- It always writes a PNG to `<Flocon documents>/Screenshot/`, and is used by the app (`AppViewModel`) and the MCP (`ScreenCapturer`). Their behavior must not change.
- The graph draws one session at once (sessions are cut by app start); its time unit is `startMs` since the session start. Lanes today are Network, Database, Analytics only. Events come as a capped list, newest first. The list is a `PagingData`, newest first, with separators inserted by `insertSeparators`.
- Timeline settings live in `TimelineSettingsDomainModel`, stored by `TimelinePreferencesRepositoryImpl` over `SettingsDataSource.timelineSettings`. New fields must default so old stored settings still load.
- Layering and DI follow `.claude/rules/desktop-app.md` and `compose.md`; builds need JDK 21.

## Approaches considered

1. **Desktop-side recorder, matched by timestamp (chosen).** A job on the desktop reuses the existing capture and stores JPEGs. No SDK change, works with every SDK version already in the field, iOS simulator comes for free. Cost: periodic, so not tied to an event.
2. **SDK-triggered capture on important events.** Precise, but needs a protocol change in both `Protocol.kt`, a new SDK plugin with its no-op, and would only work for updated apps. Rejected for v1; it is the way to add event-triggered captures later.
3. **Screen recording (adb `screenrecord` / simulator video) and extracting frames.** Exact time, but heavy: adb screenrecord is capped at 3 minutes per file and costs far more than a screenshot every 10 s. Rejected.

Picked by the user in the conversation: approach 1, with the constraints "not below 5 or 10 s, JPEG, deleted at next launch, stored apart from the user's screenshots".

## Design

### Settings (domain + data)

- `TimelineSettingsDomainModel` gains `screenshots: Boolean = false` and `screenshotIntervalSeconds: Int = 10`.
- Allowed intervals: 5, 10, 15, 30 (a constant list in domain; a stored value outside it falls back to 10).
- Matching fields, with the same defaults, in the stored model of `SettingsDataSource.timelineSettings`, mapped in `TimelinePreferencesRepositoryImpl`. Use cases `SetTimelineScreenshotsUseCase` and `SetTimelineScreenshotIntervalUseCase`, same shape as `SetTimelineGraphUseCase`, registered in `timelineDomainModule`.

### Capture

- `TakeScreenshotUseCase` gets an optional `outputFile: File? = null`; when set it writes there instead of `Documents/Screenshot`. Default behavior unchanged.
- New domain package `timeline/screenshot`:
  - `TimelineScreenshotDomainModel(timestamp: Long, filePath: String, deviceId: String, packageName: String)`.
  - `TimelineScreenshotRepository`: `observe(deviceIdAndPackageName): Flow<List<...>>` (oldest first), `add(...)`, `purgeAll()`.
  - `RecordTimelineScreenshotsUseCase`: combines the settings (enabled, interval), the current device and "app is active" (`ObserveIsCurrentDeviceIAndAppIsActiveUseCase`) with `flatMapLatest`. When enabled and live, it loops: take a screenshot, convert, store, then `delay(interval - elapsed)` (at least 1 s). The loop is sequential, so a slow capture skips ticks. Changing the interval, the device or the toggle cancels and restarts the loop at once, so changes apply immediately. It takes the first capture right when the loop starts.
- Conversion: the PNG written by adb is read with `ImageIO`, scaled to 640 px high (aspect kept) and written as JPEG at quality 0.6 (about 30 to 60 KB), then the PNG is deleted. This runs on the IO dispatcher.
- Failure: a failed capture is logged and the loop goes on. After 3 failures in a row the header control shows a warning dot with a tooltip ("Screenshots failing: is adb available?"); the next success clears it.

### Storage and lifetime

- Directory: `<java.io.tmpdir>/flocon-timeline-screenshots/<deviceId>/`, files named `<package>_<epochMs>.jpg`. This is not under the Flocon documents path.
- The whole directory is deleted at app launch (before the recorder can start), by `purgeAll()`. A debugged app's captures therefore never survive a restart of Flocon.
- Cap: 300 files per device, oldest deleted first (about 50 min at 10 s, under 20 MB). The repository keeps the in-memory index (timestamp and path); there is no database table, since nothing persists across launches.
- Turning the feature off stops the loop and keeps the existing captures visible until the next launch. It does not delete them.
- Where the recorder is started: as a long-lived app-scope job, the way other background jobs are started in `composeApp` (the builder follows the existing pattern around `AppViewModel` / DI start-up). It runs whatever the open screen is, so the strip is already filled when the user opens the timeline.

### Graph UI

- `TimelineGraph` gets a strip row above the time axis. Each screenshot is placed at `(timestamp - sessionStart)` on the same x scale as the bars. Only screenshots inside the selected session are drawn.
- Thinning: when two thumbnails would overlap at the current width, only every n-th is drawn so none overlap (zooming in brings the others back). This is pure layout code next to `GraphLayout`, unit-tested like it.
- Thumbnails decode lazily at thumbnail size (Coil is already set up in `Main.kt`); only the ones in view are decoded.
- Hover: the existing vertical cursor line, plus a popover with the closest screenshot at or before the cursor time (large version, its time, and "N s before the cursor"). No popover when none is within 2 intervals before the cursor.
- A gap larger than 2 intervals between two captures (feature off for a while, app paused) is shown as empty space, not stretched.
- Off state: the strip is replaced by a dashed one-line hint with a "Turn on" button. Lanes and bars are unchanged.
- Header control (all views): a camera icon, a switch, and an interval menu (5, 10, 15, 30 s). It is wired to the two use cases above and to `settings` in `TimelineViewModel`.

### List UI

- Marker rows are inserted by timestamp into the existing paged list through the `insertSeparators` step already used for session separators. In each gap between two adjacent events, if one or more captures fall between them, a single marker row is inserted holding the latest one (idle stretches do not stack markers). Captures newer than the newest event, or older than the oldest, are handled with the same step at the list ends.
- Row: the thumbnail in a left gutter, and a line across the row labeled "Screenshot · m:ss". The line leaves from the center of the image (default, a single constant; the top of the image is the alternative the user is still deciding between). Events stay in their own column to the right of the gutter, so a thumbnail may overlap the rows below its line.
- The list has to re-emit when a capture arrives: the screenshots flow is combined with the paging flow, which re-runs the transform at most once per interval. The builder checks that this does not reset scroll position or the selected row.
- `invertList` (newest at the bottom) mirrors the marker: the line stays at the capture time, the thumbnail extends away from the list's reading direction so it does not cover events above the line.

### What does not change

- Event sources, `Protocol.kt` (both), SDK modules, the network, database and analytics plugins, the MCP.
- Note: the mockups show a "Navigation" lane and rows. Those events do not exist, so the builder uses only Network, Database, Analytics (and what the real graph and list already show).

## Mockups

Canvas: https://claude.ai/artifact/2VQ9J7j3muS3Y6yV4EztLW. A: graph with the strip on top and the hover popover. B: list with marker lines (first marker from the top of the image, second from the center, to compare). C: settings popover. D: feature off. They show abstract screens, not real captures, and the Navigation lane described above.

## Tests

- Domain: recorder loop with virtual time (interval honored, ticks skipped when a capture is slower, restart on interval or device change, stops when the feature is off or the device is not live, 3-failures warning), retention cap and `purgeAll`, settings use cases (the interval fallback to 10), in `TimelineSettingsUseCasesTest` style.
- composeApp: strip thinning in the style of `GraphLayoutTest`; marker insertion in the list (gap holds one marker, ends of the list, inverted list) in the style of the mapper tests.
- Run with JDK 21: `./gradlew :domain:allTests`, `./gradlew allTests` for the desktop, `./gradlew ktlintFormat`. The capture itself (adb, simulator, ImageIO on real PNGs) is not unit-testable here and is checked by hand on one Android device or the iOS simulator; say so if it was not run.

## Docs and sync

- No protocol change, so `protocol/` and `Protocol.kt` are untouched.
- The timeline has no page in `docs/` today. Not added by this change unless the builder finds a timeline page elsewhere; if it adds one, it also goes in the `nav:` of `mkdocs.yml`.
- No MCP change, so `docs/mcp.md` and the MCP skill are untouched.

## Calls made by the author (not decided with the user)

- Recorder runs whatever the open screen is, as long as the feature is on and the device is live (alternative: only while the timeline is open, cheaper but the strip would be empty for the time before opening it).
- Cap of 300 files per device; 640 px high, JPEG quality 0.6.
- Warning dot after 3 failures in a row.
- Line from the center of the image in the list.
- Intervals limited to 5, 10, 15, 30 s.
- One marker per gap in the list, latest capture only.
- Hover popover only within 2 intervals of the cursor.

## Left to the builder

- Exact names and files, the DI wiring, where the app-scope job starts.
- The thinning algorithm, thumbnail sizes, popover placement and styling (from the mockups and the design system).
- How the combined list flow avoids resetting scroll and selection.
- Whether `ImageIO` needs any extra handling for the PNGs `screencap` produces on some devices.
