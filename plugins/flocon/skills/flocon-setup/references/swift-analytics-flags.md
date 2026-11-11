# Swift: analytics and feature flags

Prerequisite: [swift.md](swift.md), with `.analytics` / `.featureFlags` installed.

## 1. Analytics: choose the capture point

Installing `.analytics` captures nothing by itself: the app must send its events to `Flocon.analyticsPlugin`. Find the app's analytics abstraction (a protocol, a tracker, a dispatcher fanning out to providers) and decide which flow Flocon shows:

- **Events requested by the app** (recommended): capture where the app asks to track, before any provider-specific filtering or opt-out. Debug builds often disable the remote providers on purpose; a hook in the remote sending path then captures nothing.
- **Events actually sent to a provider**: capture inside each provider adapter, one Flocon stream per provider. Only useful when the user wants to check what reaches a provider, and only shows something when that provider is enabled.

Rules:

- Never re-enable a remote provider in Debug just to feed Flocon.
- Capture in one layer only, or each event shows twice.
- Include identity and user property operations (identify, set user id, set/unset properties, reset) as events too, if the app has them, so the timeline is complete.
- Analytics events go to Analytics. A Timeline log is not a replacement.

Acceptance: an event tracked in Debug with the remote provider disabled shows once in Flocon, and nothing more is sent remotely.

## 2. Adapter that compiles

The SDK types are `AnalyticsEvent(eventName:properties:)`, `AnalyticsProperty(name, value)` (values are `String`) and `Flocon.analyticsPlugin.sender(stream).logEvents(...)`. Apps often have their own `AnalyticsEvent`, and the `Flocon` class has the same name as its module, so `Flocon.AnalyticsEvent` does not resolve (it looks inside the class).

Which fix works depends on where the app's type is declared:

- **In another module** (an analytics package the adapter imports): selective imports in the adapter file win over the whole-module import, as below.
- **In the same module as the adapter**: a type of the current module always shadows imported ones, selective import or not. Either move the adapter to its own target, or never spell the SDK type and let the parameter type infer it: `logEvents(.init(eventName: name, properties: props))`.

```swift
// FloconAnalyticsSink.swift
import AppAnalytics // the app module that declares its own AnalyticsEvent and AnalyticsSink
#if DEBUG
import class Flocon.Flocon
import struct Flocon.AnalyticsEvent
import struct Flocon.AnalyticsProperty

/// Forwards what the app tracks to the Analytics tab of Flocon.
struct FloconAnalyticsSink: AnalyticsSink { // AnalyticsSink: the app's own protocol
    private let stream = "app"

    func track(name: String, properties: [String: Any]) {
        Flocon.analyticsPlugin.sender(stream).logEvents(
            AnalyticsEvent(eventName: name, properties: Self.convert(properties))
        )
    }

    func identify(userId: String?) {
        Flocon.analyticsPlugin.sender(stream).logEvents(
            AnalyticsEvent(eventName: "$identify", AnalyticsProperty("user_id", userId ?? "nil"))
        )
    }

    func setUserProperties(_ properties: [String: Any]) {
        Flocon.analyticsPlugin.sender(stream).logEvents(
            AnalyticsEvent(eventName: "$set_user_properties", properties: Self.convert(properties))
        )
    }

    private static func convert(_ properties: [String: Any]) -> [AnalyticsProperty] {
        properties
            .sorted { $0.key < $1.key }
            .map { AnalyticsProperty($0.key, String(describing: $0.value)) }
    }
}
#endif
```

Adapt the method names to the app's protocol; `track`, `identify`, `setUserProperties` are placeholders, not SDK APIs. If the app's dictionary values are optionals or nested JSON, convert them explicitly (`JSONSerialization`) instead of `String(describing:)` when the content matters. Then register the sink next to the providers, under the same debug condition as `startFlocon`.

Same-module variant, without naming the SDK type:

```swift
#if DEBUG
import class Flocon.Flocon
import struct Flocon.AnalyticsProperty

struct FloconAnalyticsSink {
    func track(name: String, properties: [String: String]) {
        // `.init` is inferred as the SDK AnalyticsEvent from the parameter type of logEvents
        Flocon.analyticsPlugin.sender("app").logEvents(
            .init(eventName: name, properties: properties.map { AnalyticsProperty($0.key, $0.value) })
        )
    }
}
#endif
```

Other possible collisions: `FloconPreference`, `TableColumnConfig`, `FloconText`… import them the same way (`import struct Flocon.X`, `import class Flocon.Y`, `import protocol Flocon.Z`, `import enum Flocon.W`).

## 3. Feature flags

```swift
Flocon.featureFlagsPlugin.sender("remote-config").register(
    FloconFeatureFlag(name: "new_checkout", state: .custom, variant: "treatment", payload: #"{"steps":3}"#),
    FloconFeatureFlag(name: "dark_mode", state: .on, variant: "on")
)
```

- Each `register` replaces all the flags of that provider; they are re-sent on reconnect. `register([])` clears them.
- Read the assignments the flag system already holds (its cached values / assignment store). Do not call the evaluation API if it records an exposure: inspecting must not change experiment data.
- Keep the existing consumers of the same data (other debug tools, observability SDKs) on their current path; add Flocon next to them.
- Publish after each refresh of the flags, and clear on account reset / logout.
- `state`: `.on` / `.off` for booleans, `.custom` for an experiment arm or a value.

### Do not add a `Task` by default

`register` and `logEvents` only queue values; encoding and sending run in the background. Do not add a `Task` / `DispatchQueue.async` just to call them: it makes the order of publish and reset harder to guarantee. Only if the app's own mapping is expensive (measured), move that mapping off the main thread, and then:

- keep a single task per provider, cancel the previous one on refresh and on reset, so an old publish cannot land after a reset;
- check the captured values are `Sendable` before capturing them (Swift 6 strict concurrency), or map them to `FloconFeatureFlag` (which is `Sendable`) first.
