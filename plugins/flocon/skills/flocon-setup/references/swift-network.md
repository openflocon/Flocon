# Swift: network, images and gRPC

Prerequisite: [swift.md](swift.md) (package added, `startFlocon` with `.network`).

## 1. What `.network` covers

`install(.network)` registers `FloconURLProtocol` globally (`URLProtocol.registerClass`). That only reaches sessions built on the **shared** configuration: `URLSession.shared` and code that uses it. Any session created with its own `URLSessionConfiguration` (`.default`, `.ephemeral`, a copy) ignores globally registered protocols and needs `withFlocon()`.

## 2. Inventory the clients

Before editing, list every place that creates a session, and for each one note who calls it:

```bash
rg -n "URLSession\(configuration|URLSessionConfiguration\.(default|ephemeral|background)|sessionConfiguration|Session\(configuration" --type swift
rg -n "Kingfisher|ImageDownloader|SDWebImageDownloader|ImagePipeline|DataLoader" --type swift
```

Classify each: shared session · private session · library session (Alamofire, Kingfisher, SDWebImage, Nuke…) · prefetch/download pipeline with its own session · background session.

## 3. `withFlocon()`

```swift
let configuration = URLSessionConfiguration.ephemeral.withFlocon()
let session = URLSession(configuration: configuration)
```

- It mutates the configuration (inserts `FloconURLProtocol` first in `protocolClasses`, once: calling it twice adds nothing) and returns the **same** instance.
- `URLSession` copies its configuration when it is created. Changing a configuration after `URLSession(configuration:)` does not change that session.
- So a library that stores a configuration and builds its session from it must be given the instrumented configuration **before** it creates the session, or have its configuration property re-assigned so it rebuilds the session (Kingfisher's `downloader.sessionConfiguration` setter does this).

Wrap it in the project's debug condition:

```swift
func makeConfiguration() -> URLSessionConfiguration {
    let configuration = URLSessionConfiguration.default
    #if DEBUG
    _ = configuration.withFlocon()
    #endif
    return configuration
}
```

### Background sessions

Never add a `URLProtocol` to `URLSessionConfiguration.background(withIdentifier:)`: custom protocols are not supported there and can break transfers that continue while the app is suspended. Leave them as they are and say these transfers are not visible in Flocon.

## 4. Check the path that really runs

Finding a configuration function is not proof of coverage. For every function you instrument, search its callers and check it is executed by the app target being tested (not only a demo, a test target, an extension, or a dead branch). A `withFlocon()` in a package is not coverage until a request through it shows up in Desktop.

### Kingfisher

Configure the downloader actually used: `KingfisherManager.shared.downloader` unless the app builds its own `ImageDownloader` and passes it through `KingfisherOptionsInfo` (`.downloader(...)`) or a custom `KingfisherManager`.

```swift
#if DEBUG
let downloader = KingfisherManager.shared.downloader
downloader.sessionConfiguration = downloader.sessionConfiguration.withFlocon()
#endif
```

Assigning `sessionConfiguration` (even the same, mutated instance) invalidates the current session, cancelling its downloads, and creates a new one: do it at startup, after the app's own downloader setup. Keep the existing configuration as above: Kingfisher's default is `.ephemeral`, and the README form `.default.withFlocon()` silently switches it to `.default`.

### SDWebImage

`SDWebImageDownloader.shared` builds its session from `SDWebImageDownloaderConfig.default` the first time the singleton is created. Configure it before any image load touches the singleton (early in app start), and keep the existing settings:

```swift
#if DEBUG
let config = SDWebImageDownloaderConfig.default
let sessionConfiguration = config.sessionConfiguration ?? .default
config.sessionConfiguration = sessionConfiguration.withFlocon()
#endif
```

If the app creates its own `SDWebImageDownloader(config:)`, instrument that config.

### Prefetch / download pipelines

Prefetchers and media caches often download data with their own `URLSession` then hand the bytes to the image library. Instrument that session too, or the Images tab will miss those loads.

## 5. Interceptor conflicts

A configuration can contain `withFlocon()` in the source and still not run Flocon. Look for:

- Code that replaces `protocolClasses` after `withFlocon()` (`configuration.protocolClasses = [MyProtocol.self]`).
- Mock / stub frameworks and swizzles that override `URLSessionConfiguration.protocolClasses` globally (OHHTTPStubs, Mocker, RealHTTP stubber, custom `method_exchangeImplementations`): `rg -n "protocolClasses|method_exchangeImplementations|class_replaceMethod|registerClass" --type swift`, and read their enable/disable code. Example seen in practice: a stubber hook replaced the `protocolClasses` getter by its own protocol only, hiding Flocon although the code called `withFlocon()`.
- Initialization order: Flocon configured after the client was created, or a stubber enabled after Flocon.

Inspect the **effective** configuration at runtime, not the source:

```swift
#if DEBUG
print(session.configuration.protocolClasses ?? [])  // expect Flocon.FloconURLProtocol first, then the Foundation protocols
#endif
```

Fix per project: keep the stubs that are really used (tests, demo mode); for example only enable a stubber hook when stubs are registered, or add `FloconURLProtocol` after it. Do not disable mocking frameworks wholesale.

## 6. Images tab

At this revision:

- There is no `.images` plugin. Images come from intercepted network responses.
- A response is an image when its `Content-Type` starts with `image/`, or when `options.isImage(request, response)` returns `true` (set in `install(.network) { $0.isImage = … }`, useful for `application/octet-stream` CDNs).
- The image bytes are not sent as a text body: Desktop downloads the image from its URL to show the thumbnail. A signed URL that expired, or a URL that needs auth headers, can be listed with no thumbnail.
- Cache hits (memory or disk of the image library, or `URLCache`) make no network call, so nothing new is captured.

Diagnose step by step; do not wipe the app data to test, change the URL or clear only the library cache from a debug action if needed:

1. Did a network download happen (cold cache)? Check the library's cache type in its completion, or a breakpoint.
2. Is the call in Network (`list_network_calls` / `search_network_calls`)? If not: the session is not instrumented or an interceptor conflict (sections 3–5).
3. Is it classified as image? Check its `Content-Type`; add `isImage` if needed.
4. Is it in `list_images`?
5. Listed but no thumbnail: a Desktop display / URL access issue, not an SDK one.

## 7. gRPC (grpc-swift 1.x)

Package `https://github.com/openflocon/FloconSwiftGrpc.git` (`from: "2.0.0"`), product `FloconGrpc`. It depends on FloconSwift and re-exports `Flocon`. Requires grpc-swift 1.26+ 1.x; grpc-swift 2 is not supported.

- Install `.network` in `startFlocon` (gRPC calls are logged through the network plugin). `Flocon.initialize()` installs everything.
- Add `FloconGrpcClientInterceptor(authority:)` to the interceptor factories the clients already use, keeping the existing interceptors. If the project has a shared helper returning the common interceptors, add it there once.
- `authority` (for example `"api.example.com:443"`) is displayed as the URL: an interceptor does not know its channel.
- Streams: only the first request message and the last response message are shown.
- No change or regeneration of the generated `.pb.swift` / `.grpc.swift` files is needed.

```swift
import GRPC
#if DEBUG
import FloconGrpc
#endif

func commonInterceptors<Request, Response>() -> [ClientInterceptor<Request, Response>] {
    var interceptors: [ClientInterceptor<Request, Response>] = [AuthInterceptor(), LoggingInterceptor()]
    #if DEBUG
    interceptors.append(FloconGrpcClientInterceptor(authority: "api.example.com:443"))
    #endif
    return interceptors
}

struct GreeterInterceptors: Helloworld_GreeterClientInterceptorFactoryProtocol {
    func makeSayHelloInterceptors() -> [ClientInterceptor<Helloworld_HelloRequest, Helloworld_HelloReply>] {
        commonInterceptors()
    }
}
```

`AuthInterceptor` / `LoggingInterceptor` stand for whatever the project already has. Put the Flocon interceptor where it sees the final request (usually last, after the ones that add metadata).
