# Telemetry & Privacy

Flocon Desktop can send a small amount of **usage statistics** so maintainers know which features are actually used and on which platforms. This page lists exactly what is collected.

!!! info "Nothing from your apps is ever sent"
    Flocon is a debugging tool: it sees network calls, databases, preferences and files of the apps you inspect. **None of it leaves your computer.** No URLs, payloads, headers, package names, device names or database content are ever part of an event.

## Levels

On first launch, a card lets you pick a level. You can change it at any time in **Settings → Privacy**.

| Level | What it means |
| --- | --- |
| **Anonymous** *(default)* | A random identifier is generated in memory at each launch and **never written to disk**. Flocon can count sessions and feature usage, but cannot recognise you across launches. |
| **Full** *(opt-in)* | A random identifier is stored on your computer so returning users can be counted. Switching back to Anonymous or Disabled deletes it. |
| **Disabled** | Nothing is sent. |

Telemetry is also fully disabled when:

- the `DO_NOT_TRACK` environment variable is set (any value other than empty, `0` or `false`), following [consoledonottrack.com](https://consoledonottrack.com);
- you build Flocon yourself or use a fork: only official releases contain the analytics key.

## Events

| Event | When | Extra properties |
| --- | --- | --- |
| `app_started` | Once per launch | — |
| `screen_viewed` | A menu entry is opened | `screen` (e.g. `Network`, `Database`) |

Every event also carries: Flocon version, OS name and architecture, the telemetry level, and the per-launch session id.

## Where the data goes

Events are sent to [PostHog Cloud](https://posthog.com) in the **EU region**. No person profiles are created, IP geolocation is disabled, and the PostHog project is configured to discard client IP addresses.

The full implementation lives in [`FloconDesktop/composeApp/.../telemetry`](https://github.com/openflocon/Flocon/tree/main/FloconDesktop/composeApp/src/commonMain/kotlin/io/github/openflocon/flocondesktop/telemetry).

## For contributors

- Add events only through `TelemetryManager.track(...)` and list them in `TelemetryEvents` **and** on this page.
- Never pass data coming from the inspected apps.
- The key is injected at build time from the `POSTHOG_API_KEY` environment variable (a GitHub secret on the release workflow). Without it, the app compiles and telemetry is a no-op. To test locally against your own PostHog project: `POSTHOG_API_KEY=phc_xxx ./gradlew run`.
