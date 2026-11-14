---
title: Swift SDK (iOS & macOS)
description: Add the Flocon Swift SDK to an iOS or macOS app with Swift Package Manager, start it, and keep it out of release builds.
---

# Swift SDK (iOS & macOS)

[![FloconSwift release](https://img.shields.io/github/v/release/openflocon/FloconSwift?label=FloconSwift)](https://github.com/openflocon/FloconSwift/releases)

Replace `LAST_VERSION` with the version shown on the badge.

Requirements: iOS 15+ or macOS 12+, Xcode 16+ (Swift 6).

!!! tip "Let your AI agent do it"
    The [Flocon plugin](install-with-ai.md) adds the package, starts Flocon and keeps it out of release builds for you.

## 1. Add the package

In Xcode, choose **File > Add Package Dependencies...**, paste `https://github.com/openflocon/FloconSwift.git`, keep **Up to Next Major Version** from the latest release, and add the `Flocon` library to your app target.

With a `Package.swift`:

```swift
dependencies: [
    .package(url: "https://github.com/openflocon/FloconSwift.git", from: "LAST_VERSION"),
],
targets: [
    .target(name: "MyApp", dependencies: [
        .product(name: "Flocon", package: "FloconSwift"),
    ]),
]
```

**gRPC (optional)**: the [`FloconSwiftGrpc`](https://github.com/openflocon/FloconSwiftGrpc) package adds the interceptor for grpc-swift 1.26+ (1.x). Add `https://github.com/openflocon/FloconSwiftGrpc.git` (from the latest release) and the `FloconGrpc` library; it already includes `Flocon`. See [gRPC](grpc.md).

**Info.plist**: add `NSLocalNetworkUsageDescription` so iOS lets the app reach your computer on the local network (a physical device shows a permission prompt the first time).

```xml
<key>NSLocalNetworkUsageDescription</key>
<string>Flocon connects to the desktop debugger on your computer.</string>
```

No App Transport Security exception is needed: the SDK uses `wss://` and `https://` with the self-signed certificate of the desktop (encrypted, not authenticated).

## 2. Start Flocon

Start the SDK as early as possible, then `install` the features you use. A feature that is not installed does nothing instead of crashing, so calls to it can stay in your code.

```swift
import Flocon

@main
struct MyApp: App {
    init() {
        #if DEBUG
        startFlocon { flocon in
            flocon.install(.network)
            flocon.install(.analytics)
            flocon.install(.database) { $0.register(displayName: "App", url: databaseURL) }
        }
        #endif
    }

    var body: some Scene {
        WindowGroup { ContentView() }
    }
}
```

The plugins are `.network` (also intercepts `URLSession.shared`), `.analytics`, `.featureFlags`, `.tables`, `.preferences`, `.files`, `.database`, `.dashboard`, `.deeplinks`, `.timeline` and `.crashReporter`. Those with options take a closure. `Flocon.initialize()` is the short form that installs all of them. `Flocon.isLoggingEnabled = true`, set before starting, prints what the SDK does with the `[Flocon]` prefix.

## Release builds

Flocon is a debugging tool: once connected, the desktop can read your network calls, edit your preferences, delete files and run SQL on your databases. Keep it out of release builds.

The Swift SDK has no no-op package. Wrap the start in `#if DEBUG`: the other calls (`Flocon.analyticsPlugin`, ...) do nothing until the SDK is started, so they can stay in your code. The package is still linked in release builds, but inert.

```swift
#if DEBUG
startFlocon { flocon in flocon.install(.network) }
#endif
```

## Next: connect

Run the app with Flocon Desktop open: in the iOS Simulator or as a macOS app it connects by itself. On a physical device, see [Connect](connect.md).
