# Install Flocon in a Swift (iOS) project

The Swift SDK lives in its own repository: `https://github.com/openflocon/FloconSwift` (Swift package `FloconSwift`, product and module `Flocon`). gRPC support is a separate package: `https://github.com/openflocon/FloconSwiftGrpc` (product and module `FloconGrpc`). Their READMEs and DocC comments are the source of truth for the API; if this page disagrees with them, they win: tell the user.

Verified against FloconSwift `2e822e1` (main, after tag `0.4.0`; `Flocon.sdkVersion` still reports `"0.3.0"`) and FloconSwiftGrpc `c44cb3a` (main). The snippets of these pages were compiled for the iOS Simulator (Debug and Release, Xcode 27) against those revisions, grpc-swift 1.27, Kingfisher 8.15 and SDWebImage 5.21. Check what changed since before applying a recipe (`git log` of the package checkout in `DerivedData/SourcePackages/checkouts/` or of a fresh clone).

Topic pages, read only the one you need:

- [swift-network.md](swift-network.md): `URLSession` coverage, image libraries, interceptor conflicts, Images tab, gRPC.
- [swift-analytics-flags.md](swift-analytics-flags.md): bridging the app analytics and feature flags.
- [swift-storage.md](swift-storage.md): files, App Groups, preferences, keychain, SQLite / Core Data, dashboards.
- [swift-connection.md](swift-connection.md): debug menu with manual address, status and QR scanner.

## 1. Detect what is there

Answer from the files before asking the user anything:

| Look for | Tells you |
| :--- | :--- |
| `FloconSwift` / `FloconSwiftGrpc` in `Package.resolved` (`*.xcodeproj/project.xcworkspace/xcshareddata/swiftpm/`, `*.xcworkspace/xcshareddata/swiftpm/`, or next to a `Package.swift`) | Swift SDK already used: its `revision` / `version` is the SDK version |
| `XCRemoteSwiftPackageReference` for FloconSwift in `project.pbxproj` | Same, and its requirement (`branch = main`, a version rule…) |
| `import Flocon` / `import FloconGrpc` | Where the SDK is already called |
| A Kotlin framework (`shared.framework`, `embedAndSignAppleFrameworkForXcode`, `kotlin("multiplatform")`) that depends on `io.github.openflocon` | Flocon comes from Kotlin Multiplatform: follow [kmp.md](kmp.md) instead |
| `grpc-swift` in `Package.resolved` (and its major version) | gRPC recipe applies, see [swift-network.md](swift-network.md) |
| `Kingfisher`, `SDWebImage`, `Nuke`, `Alamofire` in `Package.resolved` / Podfile | Network clients to inventory |

Do not infer the version from the name of the start function: the Swift SDK has both `startFlocon { }` (plugins chosen) and `Flocon.initialize()` (every plugin). There is no Swift no-op product: never add one, and never write `releaseImplementation`-style rules for Swift.

## 2. Compatibility

| Item | Value |
| :--- | :--- |
| Platforms | iOS 15+, macOS 12+ (`Package.swift`) |
| Toolchain | Manifest is `swift-tools-version: 6.0` with the upcoming feature `ApproachableConcurrency`; the README says Swift 6.4+ / Xcode 26+. Builds were checked with Xcode 27 / Swift 6.4. Treat the README requirement as the safe one until both are aligned upstream |
| Distribution | Swift Package Manager only, no tagged release for the current API: depend on `branch: "main"` (or pin a `revision`) |
| Transport | `wss://host:9023` (WebSocket) and `https://host:9024` (file uploads/downloads), self-signed certificate of the desktop accepted without verification |
| Flocon Desktop | Must accept the encrypted connection: openflocon/Flocon#700 (merged 2026-10-29). Desktop 1.10.0 and older only speak `ws`/`http` and will not connect. Use a Desktop built from `main` after #700, or the first release that includes it |
| MCP | Flocon Desktop serves MCP on `http://localhost:9042/mcp` (loopback). Not used by the app; independent of 9023/9024 |
| gRPC | FloconSwiftGrpc needs grpc-swift 1.26+ **1.x**. grpc-swift 2 is not supported: do not promise it |

Never add a general App Transport Security exception (`NSAllowsArbitraryLoads`) to "fix" a connection: the SDK does not need one. A connection problem is a Desktop version, host, port or local-network permission problem.

## 3. Decide what "debug only" means (ask once if it changes the dependencies)

Two different requirements hide behind "only in debug":

1. **SDK inactive in Release**: nothing starts, nothing is intercepted, no debug UI. Done with `#if DEBUG` (or the project's own flag) around every `import Flocon`, call, hook, type and menu entry. The framework is still linked and embedded: SPM links a package product into every configuration.
2. **SDK absent from the Release binary**: the framework is not shipped at all. SPM has no per-configuration dependency, so this needs a different setup: a debug-only target/scheme, a wrapper package only linked by a debug app target, or `OTHER_LDFLAGS`/build-phase tricks. It changes the project architecture.

Rules:

- Ask the user which one they want only when they have not said so and the answer changes the dependency setup (2). Otherwise apply 1 and state it.
- Inspect the real configurations (`xcodebuild -list -project … ` / `-workspace …`, `buildConfigurations` in `project.pbxproj`, `.xcconfig` files): projects often have `Beta`, `Staging`, `Internal`, `Enterprise`… Find which ones define `DEBUG` in `SWIFT_ACTIVE_COMPILATION_CONDITIONS`, and which flag the project already uses for its own debug tools.
- A menu called "Debug" may be reachable in some Release builds (internal / TestFlight): gate Flocon entries by the same condition as the SDK start, not by the menu.
- In the final report say which of the two was delivered: "SDK inactive in X, Y" vs "SDK absent from the X binary".

## 4. Add the package

### Xcode project

File › Add Package Dependencies… › `https://github.com/openflocon/FloconSwift.git` › Dependency Rule **Branch** `main` › add the `Flocon` library to the app target only (or to the package that owns the debug tooling, see below).

### Package.swift

```swift
dependencies: [
    .package(url: "https://github.com/openflocon/FloconSwift.git", branch: "main"),
],
targets: [
    .target(
        name: "DebugTools",
        dependencies: [.product(name: "Flocon", package: "FloconSwift")]
    ),
]
```

### Modular apps (several local packages)

- Add `.product(name: "Flocon", package: "FloconSwift")` to every target that has `import Flocon`, and only those. A transitive dependency is not enough: Swift requires a direct dependency to import a module.
- Prefer one place that owns the integration (an app-level or debug-tools target) and call the plugin APIs from the other modules through the app's existing abstractions (analytics protocol, network client factory…). Do not spread `import Flocon` in every feature module.
- A module that creates its own `URLSession` either imports Flocon itself (behind `#if DEBUG`) or receives an already-instrumented `URLSessionConfiguration` from the app. Choose the one that matches how the project already injects configurations.
- All packages must resolve FloconSwift to the same requirement; the root `Package.resolved` wins.

## 5. Start the SDK

As early as possible, before the network clients you want to see are created. `startFlocon` runs its closure synchronously; calling it twice does nothing.

### SwiftUI

```swift
import SwiftUI
#if DEBUG
import Flocon
#endif

@main
struct MyApp: App {
    init() {
        #if DEBUG
        startFlocon { flocon in
            flocon.install(.network)      // also intercepts URLSession.shared
            flocon.install(.analytics)
            flocon.install(.featureFlags)
            flocon.install(.preferences)
            flocon.install(.files)
            flocon.install(.database)
            flocon.install(.dashboard)
            flocon.install(.deeplinks)
            flocon.install(.timeline)
            flocon.install(.crashReporter)
        }
        #endif
    }

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
```

### UIKit

```swift
import UIKit
#if DEBUG
import Flocon
#endif

@main
final class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        willFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        #if DEBUG
        startFlocon { flocon in
            flocon.install(.network)
            flocon.install(.analytics)
        }
        #endif
        return true
    }
}
```

If the app has a bootstrap / dependency container, call it from there instead, but check that it runs before the clients you instrument are built.

### Plugins

Available: `.network`, `.analytics`, `.featureFlags`, `.tables`, `.preferences`, `.files`, `.database`, `.dashboard`, `.deeplinks`, `.timeline`, `.crashReporter`. Install only what the project uses (search the code, do not guess). Some take options:

```swift
startFlocon { flocon in
    flocon.install(.network) { options in
        options.shouldLog = { request in request.url?.host != "telemetry.example.com" }
    }
    flocon.install(.preferences) { $0.userDefaults(sharedDefaults, suiteName: "group.com.example.app") }
    flocon.install(.files) { $0.appGroup("group.com.example.app") }
    flocon.install(.database) { $0.register(displayName: "Main", url: storeURL) }
    flocon.install(.deeplinks) { $0.register([FloconDeeplink("myapp://home", label: "Home")]) }
}
```

`Flocon.xxxPlugin` (static) is never `nil`: before start, or when the plugin is not installed, it is a no-op. So a missing `install` fails silently. After the change, list every `Flocon.<name>Plugin` call and check its feature is installed. (`Flocon.shared.xxxPlugin` is the optional variant.)

Do not wrap Flocon calls in a `Task` "to keep them off the main thread": the logging calls only queue their values; encoding and sending happen in the background. Measure the cost of the app's own mapping instead, if any.

Note: the FloconSwift README shows `floconRegisterDeeplinks(...)`, which does not exist in this revision. Use `install(.deeplinks) { $0.register(...) }` or `Flocon.deeplinksPlugin.register(deeplinks:variables:)`.

### Physical device

The default host `localhost` reaches the Mac only from the Simulator. On a device either set `flocon.config(host: "192.168.1.20")`, or connect later with `Flocon.connect(address:)` from a debug menu ([swift-connection.md](swift-connection.md)). Add `NSLocalNetworkUsageDescription` to the Info.plist of the debug configurations. Turn on `Flocon.isLoggingEnabled = true` before `startFlocon` to see `[Flocon]` logs in the console.

## 6. Feature hooks

| Feature | Native | Needs explicit wiring |
| :--- | :--- | :--- |
| HTTP via `URLSession.shared` | yes, with `.network` | – |
| HTTP via private sessions, Alamofire, image libraries | – | `withFlocon()` on their configuration, before the session is created ([swift-network.md](swift-network.md)) |
| Background `URLSession` | not supported | do not instrument |
| WebSocket | – | `Flocon.networkPlugin.logWebSocketEvent(...)` |
| gRPC (grpc-swift 1.x) | – | `FloconGrpc` interceptor ([swift-network.md](swift-network.md)) |
| Images tab | from network responses | the session must be instrumented |
| Analytics | – | adapter in the app analytics layer ([swift-analytics-flags.md](swift-analytics-flags.md)) |
| Feature flags | – | `Flocon.featureFlagsPlugin.sender(provider).register(...)` |
| `UserDefaults.standard`, app container files, SQLite in Documents / Application Support | yes | – |
| App Group files / defaults / databases, keychain | – | register them ([swift-storage.md](swift-storage.md)) |
| SQL queries run by the app | – | log from the database library trace hook |
| Dashboards, tables, deeplinks, timeline | – | app code |
| Crashes | yes, with `.crashReporter` (not while the debugger is attached) | – |
| WebView, accessibility, screen recording via the SDK | not in the Swift SDK at this revision | the MCP tools exist but get nothing from a Swift app; `take_screenshot` / recording work on the booted Simulator through the Desktop |

## 7. Verify

A green compile is not proof. Inspect the real schemes and configurations and run what fits; reuse results that are still valid (no need to rebuild Release for a cosmetic menu change). Keep the simulator data (do not erase it to test), and do not start or restart Flocon Desktop if the user said they manage it.

1. Debug build: the SDK starts before the instrumented clients (breakpoint or `Flocon.isLoggingEnabled`), the app appears in Desktop.
2. A request from a private session (not only `URLSession.shared`) shows in Network.
3. An image downloaded cold shows in Images; a second load from cache does not create a new entry (expected).
4. An analytics event shows in Analytics while the remote provider is disabled in Debug, once.
5. gRPC call shows in Network, if the project uses gRPC.
6. App Group files / databases, and stores created after login, are listed.
7. Manual connection, an invalid address error, and the status, if a debug menu was added.
8. Release (and the other non-debug configurations): builds, and the SDK never starts.
9. Release binary has no `Flocon.framework` / symbols, only if requirement 2 of section 3 was chosen (`find <App>.app -name "Flocon*"`, `nm`).

Report what was not run (device, camera, Desktop connection, Release archive) instead of claiming it.
