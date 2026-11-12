# iOS (Swift)

The native Swift SDK lives in its own repositories and is installed with Swift Package Manager.

| Package | Repository | Product |
|---|---|---|
| Flocon | [openflocon/FloconSwift](https://github.com/openflocon/FloconSwift) | `Flocon` |
| Flocon gRPC (optional) | [openflocon/FloconSwiftGrpc](https://github.com/openflocon/FloconSwiftGrpc) | `FloconGrpc` |

Requirements: iOS 15+ or macOS 12+, Xcode 26+, and the Flocon desktop app running on your Mac.

## Install

In Xcode: **File › Add Package Dependencies…**, paste `https://github.com/openflocon/FloconSwift.git`, keep the default rule (**Up to Next Major** from `2.0.0`), and add the `Flocon` library to your app target.

With a `Package.swift`:

```swift
dependencies: [
    .package(url: "https://github.com/openflocon/FloconSwift.git", from: "2.0.0"),
],
targets: [
    .target(name: "MyApp", dependencies: [
        .product(name: "Flocon", package: "FloconSwift"),
    ]),
]
```

## Start

```swift
import Flocon

Flocon.initialize()
```

`Flocon.initialize()` installs every plugin. To pick plugins, use `startFlocon { flocon in flocon.install(.network) }`. The README of the repository lists the plugins and their options, and the API is documented with DocC comments.

On the iOS Simulator, the app reaches the desktop at `localhost`. On a physical device, see *On a physical device* in the [FloconSwift README](https://github.com/openflocon/FloconSwift#readme).

## gRPC

Add `https://github.com/openflocon/FloconSwiftGrpc.git` (from `2.0.0`) and the `FloconGrpc` library. It includes FloconSwift and needs grpc-swift 1.26+ (1.x). See [gRPC](grpc.md) for the desktop side and the [FloconSwiftGrpc README](https://github.com/openflocon/FloconSwiftGrpc#readme) for the interceptor.

## Features available in Swift

Network (instrumented `URLSession`s, gRPC with FloconSwiftGrpc), images, analytics, feature flags, tables, preferences, files, databases, dashboards, deeplinks, timeline and crash reporter. WebView and accessibility events are not sent by the Swift SDK yet.
