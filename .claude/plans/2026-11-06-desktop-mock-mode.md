# Flocon Desktop mock mode Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `./gradlew :composeApp:run --args="--mock=<scenario>"` launches Flocon Desktop with a fake connected device and pre-filled network, timeline, crash, analytics and feature-flag data, defined by a Kotlin DSL.

**Architecture:** A `MockServer` implements the existing `Server` interface and emits protocol messages (`FloconIncomingMessageDataModel`) built from a scenario, so the real handlers, mappers, Room and UI run unchanged. In mock mode Koin overrides `Server`, `AdbRepository`, `IosSimulatorRepository` and `AppDatabase` (separate DB file, wiped each launch).

**Tech Stack:** Kotlin Multiplatform (jvm "desktop"), Compose Desktop, Koin, kotlinx.serialization, Room, kotlin.time.

**Spec:** `.claude/specs/2026-11-06-desktop-mock-mode-design.md`

## Global Constraints

- Gradle runs from `FloconDesktop/` with JDK 21: `export JAVA_HOME=$(/usr/libexec/java_home -v 21)` (default JDK 22 breaks `compileKotlinDesktop`).
- Without `--mock`, the app behaves exactly as today (no code path change).
- v1 plugins only: device (envelope), network, timeline, crash reporter, analytics, feature flags. No Database/Files.
- No visual indicator of mock mode in the UI.
- Settings (multiplatform-settings) and the MCP port (9042) are left as today.
- Mock Room DB: `~/.flocon/mock/flocon_room.db`, deleted (with `-wal`/`-shm`) at every mock launch. Never touch `~/.flocon/flocon_room.db` or `~/.flocon/Files` in mock mode.
- Every scenario is tracked in git and listed in the registry.
- Envelope `versionName` is the Flocon **SDK** version; default = desktop `BuildConfig.APP_VERSION`.
- Commits: `feat: [MOCK] …` format from `.claude/rules/git-workflow.md`, on branch `feat/desktop-mock-mode`, no Claude attribution lines. Run `./gradlew ktlintFormat` before each commit.

## Review Focus

1. Mock launched twice in a row → second launch shows exactly the same data (DB wiped, not appended). Pinned by Task 5 `resetMockDatabase` test.
2. The server restart loop (`MessagesServerDelegate` calls `startWebsocket` every 20 s) must not re-emit the scenario → duplicate rows. Pinned by Task 3 `emits scenario only once across restarts`.
3. A `raw` message with an invalid body must not crash the app; the real handler drops it. Pinned by Task 2 `raw body is passed through untouched` + Task 4 `weird` scenario builds.
4. A pending network call (request, no response) shows as in progress, not as a failure. Pinned by Task 2 `pending call emits request only`.
5. Two apps on one device / two devices: messages carry the right envelope each. Pinned by Task 2 `each device gets its own envelope`.

## File Structure

All mock code lives in `FloconDesktop/data/remote/src/commonMain/kotlin/com/flocon/data/remote/mock/` (package `com.flocon.data.remote.mock`), next to `Server`, so it reuses the `@Serializable` payload models (some are `internal`) and tests can run payloads through the real (internal) remote data sources. **This replaces the spec's "new `mock` module"**: same behavior, no new module, no layering change.

| File | Responsibility |
|---|---|
| `mock/MockScenario.kt` | Scenario model (`MockScenario`, `MockDevice`, `MockEvent` sealed) |
| `mock/MockScenarioDsl.kt` | DSL builders (`scenario { android { … } }`) and `Duration.ago` |
| `mock/MockMessageEncoder.kt` | Scenario → sorted `List<FloconIncomingMessageDataModel>` |
| `mock/MockServer.kt` | `Server` implementation |
| `mock/MockScenarios.kt` | Registry (`all`, `byName`) |
| `mock/scenarios/*.kt` | One file per scenario |
| `composeApp/src/desktopMain/.../mock/MockLaunchOption.kt` | `--mock` parsing |
| `composeApp/src/commonMain/.../mock/MockModule.kt` | Koin override module + no-op Adb/iOS repos |
| `.claude/skills/flocon-mock-mode/SKILL.md` | Claude skill |

---

### Task 1: Scenario model and DSL

**Files:**
- Create: `data/remote/src/commonMain/kotlin/com/flocon/data/remote/mock/MockScenario.kt`
- Create: `data/remote/src/commonMain/kotlin/com/flocon/data/remote/mock/MockScenarioDsl.kt`
- Test: `data/remote/src/desktopTest/kotlin/com/flocon/data/remote/mock/MockScenarioDslTest.kt`

**Interfaces:**
- Produces:
  ```kotlin
  data class MockScenario(val name: String, val description: String, val devices: List<MockDevice>)
  data class MockDevice(
      val id: String, val name: String, val platform: String, // "android" | "ios"
      val appName: String, val packageName: String, val appInstance: Long,
      val sdkVersion: String?, // null = desktop version, filled by MockServer
      val events: List<MockEvent>,
  )
  sealed interface MockEvent {
      val at: Duration // time BEFORE launch
      data class NetworkCall(override val at: Duration, val method: String, val url: String,
          val status: Int = 200, val duration: Duration = 100.milliseconds,
          val requestHeaders: Map<String, String> = emptyMap(), val requestBody: String? = null,
          val responseHeaders: Map<String, String> = emptyMap(), val responseBody: String? = null,
          val contentType: String? = null, val error: String? = null, val pending: Boolean = false) : MockEvent
      data class TimelineLog(override val at: Duration, val value: String, val type: String = "log",
          val level: String? = "info", val params: Map<String, String> = emptyMap()) : MockEvent
      data class Crash(override val at: Duration, val exceptionType: String, val message: String,
          val stackTrace: String = "") : MockEvent
      data class AnalyticsEvent(override val at: Duration, val eventName: String,
          val tableId: String = "analytics", val properties: Map<String, String> = emptyMap()) : MockEvent
      data class FeatureFlags(override val at: Duration, val provider: String, val flags: List<Flag>) : MockEvent {
          data class Flag(val name: String, val state: String, val variant: String, val payload: String? = null)
      }
      data class Raw(override val at: Duration, val plugin: String, val method: String, val body: String) : MockEvent
  }
  val Duration.ago: Duration // returns this; readability only
  fun scenario(name: String, description: String = "", block: MockScenarioBuilder.() -> Unit): MockScenario
  // MockScenarioBuilder: android(name, app, packageName, id = name.slug, appInstance = 1L, sdkVersion = null) { … }, ios(…)
  // MockDeviceBuilder: network(at, method, url, status, duration, error, pending) { requestHeader(k,v); requestBody(s); responseHeader(k,v); responseBody(s); contentType(s) }
  //                    timeline(at, value, type, level, params), crash(at, exceptionType, message, stackTrace),
  //                    analytics(at, event, vararg properties: Pair<String,String>, tableId),
  //                    featureFlags(provider, at = Duration.ZERO) { flag(name, state, variant, payload) },
  //                    raw(at, plugin, method, body)
  ```

- [ ] **Step 1: Write the failing tests** — `MockScenarioDslTest`:
  - `builds android device with defaults`: `scenario("s") { android("Pixel 8", app = "App", packageName = "a.b") {} }` → one device, `platform == "android"`, `appInstance == 1L`, `sdkVersion == null`, no events.
  - `network call defaults`: `network(5.minutes.ago, "GET", "https://x")` → `status == 200`, `duration == 100.milliseconds`, `pending == false`, `at == 5.minutes`.
  - `network block sets headers and bodies`: `responseHeader("content-type","application/json"); responseBody("[]")` land in the event.
  - `repeat generates events`: `repeat(200) { network(it.seconds.ago, "GET", "u/$it") }` → 200 events.
  - `ios device`: `ios(...)` → `platform == "ios"`.
- [ ] **Step 2: Run** `./gradlew :data:remote:desktopTest --tests "*MockScenarioDslTest"` → FAIL (unresolved references).
- [ ] **Step 3: Implement** the model and builders above (`@DslMarker` on the builders).
- [ ] **Step 4: Run** the same command → PASS.
- [ ] **Step 5: Commit** `feat: [MOCK] Scenario DSL for the desktop mock mode`.

### Task 2: Encode a scenario into protocol messages

**Files:**
- Create: `data/remote/src/commonMain/kotlin/com/flocon/data/remote/mock/MockMessageEncoder.kt`
- Test: `data/remote/src/desktopTest/kotlin/com/flocon/data/remote/mock/MockMessageEncoderTest.kt`

**Interfaces:**
- Consumes: Task 1 model. Payload models `FloconNetworkRequestDataModel`, `FloconNetworkResponseDataModel`, `TimelineLogDataModel`, `CrashReportDataModel`, `AnalyticsItemDataModel`/`AnalyticsPropertyDataModel`, `FeatureFlagsDataModel`/`FeatureFlagDataModel`; method names from `io.github.openflocon.domain.Protocol.FromDevice.*`.
- Produces:
  ```kotlin
  internal class MockMessageEncoder(private val json: Json) {
      fun encode(scenario: MockScenario, launchTime: Instant, defaultSdkVersion: String): List<FloconIncomingMessageDataModel>
  }
  ```
  Rules: timestamp = `launchTime - event.at` in epoch ms. Envelope from the device (`deviceName`, `deviceId`, `appName`, `appPackageName`, `appInstance`, `platform`, `versionName = sdkVersion ?: defaultSdkVersion`). Output sorted by timestamp ascending (stable). Network: call id `"mock-<deviceId>-<index>"`; request message (`startTime` = timestamp) and, unless `pending`, a response message (`durationMs`, `responseHttpCode = status` or `responseError = error`) at the same position. Crash and analytics bodies are **lists of one item** (that's what the remote data sources decode). Feature flags: one `register` message per `FeatureFlags` event. `Raw`: body copied as-is. Use the same `Json` config as `data/remote/.../DI.kt:45`.

- [ ] **Step 1: Write the failing tests** — decode the output with the real remote data sources (`NetworkRemoteDataSourceImpl`, `TimelineLogRemoteDataSourceImpl`, `CrashReportRemoteDataSourceImpl`, `AnalyticsRemoteDataSourceImpl`, `FeatureFlagsRemoteDataSourceImpl`, after `toDomain()` of the envelope; pass a fake `Server` where needed):
  - `network call round-trips`: GET 200, 120 ms, body `[]` → `getRequestData` url/method/startTime == launch − 5 min; `getResponseData` http 200, durationMs 120.0, body `[]`.
  - `network error round-trips`: `error = "timeout"` → response is `Response.Failure` with issue `"timeout"`.
  - `pending call emits request only`: exactly one message, method `log_network_call_request`.
  - `timeline, crash, analytics, feature flags round-trip`: each decodes to non-null domain models with the scenario values and timestamps.
  - `raw body is passed through untouched`: `raw(…, body = "{not json")` → message body `"{not json"`, plugin/method as given.
  - `messages are sorted by time`: events at 1 min, 5 min, 3 min → order 5, 3, 1.
  - `each device gets its own envelope`: two devices → messages carry their own `deviceId`/`appPackageName`.
  - `sdk version defaults to desktop version`: `sdkVersion = null` → `versionName == defaultSdkVersion`; `sdkVersion = "1.0.0"` → `"1.0.0"`.
- [ ] **Step 2: Run** `./gradlew :data:remote:desktopTest --tests "*MockMessageEncoderTest"` → FAIL.
- [ ] **Step 3: Implement** `MockMessageEncoder` per the rules above.
- [ ] **Step 4: Run** → PASS.
- [ ] **Step 5: Commit** `feat: [MOCK] Encode mock scenarios into protocol messages`.

### Task 3: MockServer

**Files:**
- Create: `data/remote/src/commonMain/kotlin/com/flocon/data/remote/mock/MockServer.kt`
- Test: `data/remote/src/desktopTest/kotlin/com/flocon/data/remote/mock/MockServerTest.kt`

**Interfaces:**
- Consumes: `MockMessageEncoder` (Task 2), `Server` interface.
- Produces:
  ```kotlin
  class MockServer(scenario: MockScenario, json: Json, defaultSdkVersion: String, launchTime: Instant = Clock.System.now()) : Server
  ```
  `startWebsocket`/`starHttp`/`stop`: no-op, no ports. `activeDevices`: `StateFlow` of one `FloconDeviceIdAndPackageNameDataModel(deviceId, packageName, appInstance)` per device, set at construction. `receivedMessages`: on first collection emits the encoded messages once per `MockServer` instance, then suspends forever; later collections emit nothing. `sendMessageToClient`: no-op. `receivedFiles`: `emptyFlow()`.

- [ ] **Step 1: Write the failing tests** — `MockServerTest`:
  - `exposes scenario devices as active`: two devices → `activeDevices.first()` has 2 entries with the right ids.
  - `emits all scenario messages`: `receivedMessages.take(n).toList()` equals the encoder output.
  - `emits scenario only once across restarts`: collect `take(n)`, call `stop(); startWebsocket(9023)`, collect again with `withTimeoutOrNull(200.milliseconds)` → no message.
  - `sendMessageToClient does not throw`.
- [ ] **Step 2: Run** `./gradlew :data:remote:desktopTest --tests "*MockServerTest"` → FAIL.
- [ ] **Step 3: Implement** `MockServer` (single-emission guard shared by all collectors).
- [ ] **Step 4: Run** → PASS.
- [ ] **Step 5: Commit** `feat: [MOCK] MockServer replaying a scenario as a connected device`.

### Task 4: Scenario registry and bundled scenarios

**Files:**
- Create: `data/remote/src/commonMain/kotlin/com/flocon/data/remote/mock/MockScenarios.kt`
- Create: `data/remote/src/commonMain/kotlin/com/flocon/data/remote/mock/scenarios/{Default,DocNetwork,DocTimeline,DocCrashReporter,DocAnalytics,DocFeatureFlags,Empty,MultiDevice,Weird}Scenario.kt`
- Test: `data/remote/src/desktopTest/kotlin/com/flocon/data/remote/mock/MockScenariosTest.kt`

**Interfaces:**
- Produces:
  ```kotlin
  object MockScenarios {
      val all: List<MockScenario>
      fun byName(name: String): MockScenario?
  }
  ```
  Names: `default`, `doc-network`, `doc-timeline`, `doc-crashreporter`, `doc-analytics`, `doc-featureflags`, `empty`, `multi-device`, `weird`.
  Content (realistic, one Android "Pixel 8" app `io.github.openflocon.sample` unless stated):
  - `default`: ~30 network calls (mix 200/201/404/500, one pending), ~20 timeline logs (navigation/userAction/lifecycle + levels), 2 crashes, ~15 analytics events, 1 provider with ~6 flags.
  - `doc-*`: rich data for that one plugin only (e.g. `doc-network` ~40 calls incl. JSON bodies, headers, error, pending, GraphQL-looking POST).
  - `empty`: one connected device, no events.
  - `multi-device`: Android Pixel 8 + iOS iPhone 16, two apps on the Pixel, a few events each.
  - `weird`: 5 MB response body, invalid JSON body, unicode/emoji/RTL URL and values, 50 consecutive 500s, a 2 000-char URL, events at 0 s and 30 days ago, `raw` invalid network body, an unknown timeline level, an old `sdkVersion = "1.0.0"`.

- [ ] **Step 1: Write the failing tests** — `MockScenariosTest`:
  - `names are unique`, `byName finds each scenario`, `byName returns null for unknown`.
  - `every scenario encodes`: `MockMessageEncoder(json).encode(it, now, "9.9.9")` doesn't throw for each in `all`; `empty` encodes to an empty list.
- [ ] **Step 2: Run** `./gradlew :data:remote:desktopTest --tests "*MockScenariosTest"` → FAIL.
- [ ] **Step 3: Implement** the registry and the 9 scenarios.
- [ ] **Step 4: Run** `./gradlew :data:remote:desktopTest` → PASS (whole module).
- [ ] **Step 5: Commit** `feat: [MOCK] Bundled mock scenarios`.

### Task 5: Launch option and app wiring

**Files:**
- Create: `composeApp/src/desktopMain/kotlin/io/github/openflocon/flocondesktop/mock/MockLaunchOption.kt`
- Create: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/mock/MockModule.kt` (module + `NoOpAdbRepository`, `NoOpIosSimulatorRepository`)
- Modify: `composeApp/src/desktopMain/kotlin/io/github/openflocon/flocondesktop/Main.kt` (parse, list/exit, reset DB, pass scenario to `App`)
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/AppWindow.kt` (`App(serverPortsOverrides, mockScenario: MockScenario? = null)`; when non-null, add `mockModule(mockScenario)` **last** in `modules(...)` so it overrides)
- Modify: `composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/common/db/AppDatabase.kt` (`getRoomDatabase(file: File = getDatabaseFile())`, `expect fun getDatabaseBuilder(file: File)`, `expect fun getMockDatabaseFile(): File`) and `composeApp/src/desktopMain/.../common/db/AppDatabase.desktop.kt` (actuals; mock file `~/.flocon/mock/flocon_room.db`)
- Test: `composeApp/src/desktopTest/kotlin/io/github/openflocon/flocondesktop/mock/MockLaunchOptionTest.kt`, `MockModuleTest.kt`

**Interfaces:**
- Consumes: `MockScenarios`, `MockServer` (Tasks 3–4).
- Produces:
  ```kotlin
  sealed interface MockLaunchOption {
      data object None : MockLaunchOption
      data object ListScenarios : MockLaunchOption
      data class Scenario(val scenario: MockScenario) : MockLaunchOption
      data class UnknownScenario(val name: String, val available: List<String>) : MockLaunchOption
      companion object { fun parse(args: List<String>, scenarios: List<MockScenario> = MockScenarios.all): MockLaunchOption }
  }
  fun resetMockDatabase(file: File = getMockDatabaseFile()) // deletes file, -wal, -shm; creates parent dir
  fun mockModule(scenario: MockScenario): Module
  // binds: single<Server> { MockServer(scenario, get(), BuildConfig.APP_VERSION) },
  //        single<AdbRepository> { NoOpAdbRepository }, single<IosSimulatorRepository> { NoOpIosSimulatorRepository },
  //        single<AppDatabase> { getRoomDatabase(getMockDatabaseFile()) }
  ```
  Parsing: `--mock=<name>` → `Scenario` or `UnknownScenario`; `--mock` or `--mock=list` → `ListScenarios`; absent → `None`. `Main.kt`: `ListScenarios` prints `name — description` per line and exits 0; `UnknownScenario` → `exitWithError("unknown mock scenario '<name>'. Available: …")`; `Scenario` → `resetMockDatabase()` before `application {}`. No-op repos: `devicesWithSerial = flowOf(emptyList())`, `getAdbSerial` → null, `saveAdbSerial` no-op, `execute*` → `Either` failure `"adb disabled in mock mode"`, `findAdbPath` → null; iOS methods → failure `"iOS simulator disabled in mock mode"`.

- [ ] **Step 1: Write the failing tests**:
  - `MockLaunchOptionTest`: `no flag → None`; `--mock=default → Scenario(default)`; `--mock=nope → UnknownScenario("nope", all names)`; `--mock and --mock=list → ListScenarios`; other args (`--websocket-port=1`) are ignored.
  - `MockModuleTest`: `koinApplication { allowOverride(true); modules(module { single<Server> { FakeServer } … }, mockModule(MockScenarios.byName("empty")!!)) }` → `get<Server>()` is `MockServer`, `get<AdbRepository>()` is `NoOpAdbRepository`, `get<IosSimulatorRepository>()` is `NoOpIosSimulatorRepository`. (Don't resolve `AppDatabase` here.)
  - `resetMockDatabase deletes db, wal and shm` in a temp dir.
- [ ] **Step 2: Run** `./gradlew :composeApp:desktopTest --tests "*Mock*"` → FAIL.
- [ ] **Step 3: Implement** the files above. Check Koin's override setting used by `KoinApplication` in `AppWindow.kt`; if overrides are disabled, enable `allowOverride(true)` only when `mockScenario != null`.
- [ ] **Step 4: Run** the same command → PASS, then `./gradlew :composeApp:desktopTest` → PASS.
- [ ] **Step 5: Manual check** with the `run` skill: `./gradlew :composeApp:run --args="--mock=default"` → device "Pixel 8" connected, network/timeline/crash/analytics/feature-flag screens filled; relaunch → same data, no duplicates; `--args="--mock=list"` prints 9 scenarios; plain `./gradlew :composeApp:run` still shows the real DB.
- [ ] **Step 6: Commit** `feat: [MOCK] --mock launch option for Flocon Desktop`.

### Task 6: Claude skill

**Files:**
- Create: `.claude/skills/flocon-mock-mode/SKILL.md`
- Modify: `CLAUDE.md` (one line under Workflow pointing to the skill for screenshots / UI checks without a device)

Content of the skill: frontmatter `name: flocon-mock-mode`, `description:` "Use when you need Flocon Desktop running with data and no phone connected: doc screenshots, checking a UI state, testing MCP tools, edge cases." Body: launch command (with JDK 21 note), `--mock=list`, table of the 9 scenarios, a DSL example (Task 1 surface), how to add a scenario (file in `mock/scenarios/` + line in `MockScenarios.all`, committed with the change it tests), limits (no Database/Files, MCP on 9042 so close the real Flocon first, settings are shared), and how to screenshot (the `run` skill / desktop screenshot).

- [ ] **Step 1: Write the skill** and the `CLAUDE.md` line.
- [ ] **Step 2: Verify** by following the skill from scratch: launch `--mock=doc-network`, take a screenshot of the network screen.
- [ ] **Step 3: Commit** `docs: [MOCK] Claude skill for the desktop mock mode`.

### Task 7: Final checks

- [ ] `cd FloconDesktop && ./gradlew ktlintFormat :data:remote:desktopTest :composeApp:desktopTest` → PASS.
- [ ] Update the spec's "Where the code lives" to say `data/remote/.../mock` instead of a new module.
- [ ] Run the `reviewer` agent on the branch diff; fix or answer each point.
