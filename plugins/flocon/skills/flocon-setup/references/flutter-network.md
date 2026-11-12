# Flutter: network

Source of truth: the "Network" section of the FloconFlutter README. Start the SDK first ([flutter.md](flutter.md) section 4).

## Inventory the HTTP clients

Search the code for every client the app creates: `Dio(`, `http.Client(`, `http.get(` (top-level functions use a client the SDK cannot wrap), generated clients (Retrofit, Chopper...) and the `Dio` they receive. Only a client wrapped with Flocon is captured; one that is not is silently absent from the Desktop.

## Dio (`flocon_dio`)

```dart
import 'package:flocon_dio/flocon_dio.dart';

final dio = Dio(BaseOptions(baseUrl: baseUrl)).withFlocon();
```

`withFlocon()` adds `FloconDioInterceptor`: call it after adding the app's own interceptors, so it sees the final request and the raw response. Mocks and "bad network" are applied by this interceptor, so it must be added last for a mock not to be seen by interceptors added after it. Apply it where the `Dio` is created (DI module, factory), not at each call site.

## `package:http` (`flocon_http`)

```dart
import 'package:flocon_http/flocon_http.dart';

final client = http.Client().withFlocon(); // a FloconHttpClient wrapping it
```

The response body is read in full to be logged, then returned unchanged: avoid it for very long streams. Top-level `http.get(...)` is not instrumented: replace it with a client.

## Options

```dart
await startFlocon((flocon) => flocon
  ..install(FloconNetwork(
    shouldLog: (request) => request.url.host != 'analytics.example.com',
    isImage: (request, response) => request.url.path.startsWith('/images/'),
  )));
```

Applies to every client. Responses with `Content-Type: image/*` are always images (body not sent, the Desktop downloads them from their URL).

## WebSockets

Not intercepted automatically: log the events of the connections to inspect.

```dart
Flocon.network.logWebSocketEvent(FloconWebSocketEvent(
  websocketUrl: url,
  event: FloconWebSocketEventType.sendMessage, // open, closing, closed, error, sendMessage, receiveMessage
  message: text,
));
```

Mocks of WebSocket messages are not supported.

## Mocks and bad network

Configured from Flocon Desktop, applied by `FloconDioInterceptor` and `FloconHttpClient`, nothing to code. They are saved on the device and apply from the next launch, before the connection. Exception names sent by the Desktop are Java class names, mapped to Dio / `http` errors by the SDK.

## Check

For each client found in the inventory, say whether it is wrapped, and that the function creating it runs in the app target (not only in a demo or a test). Network calls only show up after the app ran and connected: say if this was not run.
