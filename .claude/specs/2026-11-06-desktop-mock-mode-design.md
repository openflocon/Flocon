# Flocon Desktop: mock mode

Status: approved, implemented on `feat/desktop-mock-mode`
Date: 2026-11-06

## Intent

Launch Flocon Desktop with no phone connected and get a **reproducible, pre-filled state**: a fake device, network calls, timeline logs, crashes, analytics events and feature flags, timestamped relative to the launch time.

Uses, all driven by Claude as well as by humans:

1. **Doc screenshots**: one scenario per `docs2/` page; relaunching gives the same screen.
2. **UI checks on specific configurations**: lots of data, empty lists, several devices, Android + iOS mixed.
3. **MCP tool testing** against known data with predictable results.
4. **Weird scenarios**: huge bodies, invalid JSON, unicode, 500 storms, out-of-order timestamps.

## Goals

- `--mock=<scenario>` launch argument loads a scenario at startup; without it the app behaves exactly as today.
- Scenarios are written in a **Kotlin DSL**, compiled with the app: type-checked, can use loops to generate volume.
- A set of default scenarios ships in the repo.
- v1 plugins: **device, network, timeline, crash reporter, analytics, feature flags**.
- A project skill tells Claude how to launch mock mode, pick or write a scenario, and take screenshots.

## Non-goals

- Injecting events while the app runs (no control channel, no live edits). The scenario is read once at launch.
- Database and Files plugins: they are request/response with the device; the sample app covers them.
- Faithfully simulating the SDK, or running a real Android device / adb.
- Loading scenarios from external files (JSON, `.kts`) at runtime.

## Anti-goals

- Mock data leaking into the user's real `~/.flocon` Room database.
- A mock path that bypasses the real handlers/mappers, so screenshots show something the real app would not.
- A second process or tool to keep alive alongside the app.
- Mock code shipped as a user-facing feature of the release app (it's a developer tool; being present in the binary is fine, being advertised is not).

## Approaches

### How data reaches the app

| | How | Trade-off |
|---|---|---|
| **A. Fake `Server` (chosen)** | In mock mode Koin binds a `MockServer` instead of `ServerJvm`. It exposes the scenario's devices on `activeDevices` and emits protocol messages on `receivedMessages`, like a real SDK. | Whole real pipeline runs (mappers, handlers, Room, UI). Only depends on the protocol, which is already kept backward compatible. Needs a translation from DSL entries to protocol payloads. |
| B. Seed Room directly | Write entities into a dedicated DB at startup. | Breaks on every Room migration, bypasses handlers, device connection state isn't in Room anyway, one writer per plugin. |
| C. External fake device | Separate program connecting over websocket and replaying the scenario. | No app change, but one more tool to maintain and two processes to coordinate. |

Picked **A**: simplest to build and to maintain (one piece of code, coupled only to the protocol), extensible to Database/Files later by answering `sendMessageToClient`.

### Scenario format

| | Trade-off |
|---|---|
| JSON files loaded at runtime | No recompile, one-off scenarios anywhere on disk. But no loops (volume must be written by hand), needs a parser and a duration syntax, and bundled files drift silently when the model changes. |
| **Kotlin DSL (chosen)** | Loops/generation, compile-checked, `Duration` for times, no parser. A new scenario needs a recompile, which `./gradlew :composeApp:run` does anyway (incremental). |

Picked **Kotlin DSL** (user's choice).

## Design decisions

### Activation
- `--mock=<name>` parsed in `Main.kt` next to `serverPortsLaunchOptions`.
- Unknown name → print the error with the list of available scenario names and exit non-zero (same style as `exitWithError`).
- `--mock` with no value (or `--mock=list`) prints the available scenarios and exits. *(My call: lets Claude discover scenarios without reading code.)*
- Command Claude runs: `./gradlew :composeApp:run --args="--mock=doc-network"`.
- No visual indicator of mock mode: the UI looks exactly like a real session (user's decision).

### Isolation
- Room uses a separate file, `~/.flocon/mock/flocon_room.db`, **deleted at every mock launch** so each run starts from the same state.
- `MockServer.startWebsocket` / `starHttp` do not open ports. The MCP server still starts as today on its fixed port (9042); no special handling if a real Flocon already holds it (user's decision).
- Settings (multiplatform-settings prefs: theme, scale, menus, columns, auto-purge…) are shared with the normal app, as today (user's decision).

### The DSL
Times are `Duration`s before launch (`5.minutes.ago`), so the screen always looks "recent". Sketch (exact signatures left to the builder):

```kotlin
val docNetwork = scenario("doc-network", description = "A Pixel with successful and failing REST calls") {
    android("Pixel 8", app = "Flocon Sample", packageName = "io.github.openflocon.sample", sdkVersion = "1.4.0", appStartedAt = 15.minutes.ago) {
        network(at = 5.minutes.ago, method = "GET", url = "https://api.example.com/users", status = 200, duration = 120.milliseconds) {
            responseHeader("content-type", "application/json")
            responseBody("""[{"id":1}]""")
        }
        network(at = 4.minutes.ago, method = "POST", url = "https://api.example.com/login", status = 500)
        network(at = 3.minutes.ago, method = "GET", url = "https://api.example.com/slow", pending = true)
        timeline(at = 6.minutes.ago, message = "App started")
        crash(at = 2.minutes.ago, exception = "java.lang.IllegalStateException", message = "boom")
        analytics(at = 1.minutes.ago, event = "screen_view", "screen" to "home")
        featureFlag("new_checkout", true, description = "New checkout flow")
        repeat(200) { i -> network(at = (300 - i).seconds.ago, method = "GET", url = "https://api.example.com/items/$i") }
        raw(at = 30.seconds.ago, plugin = "network", method = "log_network_call_request", body = "{not json")
    }
    ios("iPhone 16", app = "Flocon Sample", packageName = "io.github.openflocon.sample") { /* … */ }
}
```

- Each entry exposes every field the corresponding UI shows; most have defaults (status 200, duration 100 ms, empty headers…) so scenarios stay short. `protocol/*.md` is the reference for what each payload carries.
- **`raw(plugin, method, body)`** is the escape hatch for weird scenarios: sent as-is, body not validated. Also covers out-of-order or odd timestamps.
- **Multiple devices / apps** per scenario.
- `appStartedAt` (default: 1 s before the device's first event) gives the envelope's `appInstance`, which the protocol defines as the SDK start time: the timeline session starts there.
- Messages are emitted sorted by time; payload timestamps are `launchTime - duration`.

### Scenario registry
- All scenarios are listed in a `MockScenarios` registry (one Kotlin file per scenario, plus the list). Adding one = new file + one line in the list.
- **Every scenario is tracked in git**, including the ones Claude writes to test something specific (user's decision). They become part of the shared library of scenarios, no gitignored folder.

### MockServer behavior
- Devices are registered by the message envelope itself (`HandleIncomingMessagesUseCase`), not by `register_device` (Android-only, maps an adb serial). Each device is marked in `activeDevices`; an app icon message is sent if the UI needs it.
- The envelope's `versionName` is the **Flocon SDK version**, not the app version (the protocol has no app version). The DSL defaults it to the current SDK version so the "client not on last version" banner doesn't show; a scenario can set an old one to show it. The DSL's `version` parameter from the sketch above is renamed `sdkVersion`.
- **Emit once.** `MessagesServerDelegate` calls `startWebsocket` again every 20 s and stop/start on port changes: `MockServer` must emit the scenario a single time per process, and only once `HandleIncomingMessagesUseCase` is collecting (`ServerJvm` uses a rendezvous `Channel`).
- **No side effects on the real machine.** In mock mode, `AdbRepository` and `IosSimulatorRepository` are bound to no-op implementations: otherwise a new fake Android device triggers `adb shell am broadcast` on real phones, and real devices/simulators appear on screen. *(My call: required by reproducibility and the anti-goals.)*
- `MockServer` never writes to `~/.flocon/Files` (`receivedFiles` is empty), so the user's files stay untouched.
- `sendMessageToClient` (e.g. a feature flag toggled in the UI): accept and ignore. No response needed for v1 plugins.
- `receivedFiles`: empty flow.
- Translation DSL entry → `FloconIncomingMessageDataModel`: one small mapper per plugin, unit-tested.

### Where the code lives
- DSL, `MockServer`, encoder, registry and scenarios: `FloconDesktop/data/remote/.../mock/` (package `com.flocon.data.remote.mock`). *(Changed during planning from "a new `mock` module": data/remote already holds `Server` and the wire models, some `internal`; no layering change.)*
- `composeApp`: `--mock` parsing (`mock/MockLaunchOption.kt`), the Koin override module (`mock/MockModule.kt`, added last in `App()`), the DB file provided by Koin (`DatabaseFile`, overridden in mock mode).
- Mock DB: `~/.flocon/mock/flocon_room.db`.

### Default scenarios (v1)
- `default`: one Android device, a bit of everything (the general screenshot state).
- One per covered doc page: `doc-network`, `doc-timeline`, `doc-crashreporter`, `doc-analytics`, `doc-featureflags`.
- `empty`: connected device, no data (empty states).
- `multi-device`: Android + iOS, two apps.
- `weird`: large bodies, invalid JSON, unicode, 500 storms, very long URLs, out-of-order timestamps.

### Claude skill
`.claude/skills/flocon-mock-mode/SKILL.md` (project skill for working on this repo): when to use it, the launch command, `--mock=list`, the bundled scenarios, a DSL example, how to add a new scenario (file + registry line, committed with the change it tests), and how to screenshot/drive the desktop app. Developer docs only: no `docs2/` end-user page.

### Tests
- Each mapper produces a payload the real remote data source accepts: run `MockServer` output through the existing remote mappers and assert the domain models.
- Every registered scenario builds without error and has a unique name.
- Launch option parsing (`--mock=name`, unknown name, `list`).

## Left to the builder
- Exact DSL signatures and the field list per entry, matched to the protocol payloads.
