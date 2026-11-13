---
title: Flutter SDK
description: Add the Flocon Flutter packages to an Android, iOS or macOS Flutter app, start Flocon, and keep it out of release builds.
---

# Flutter SDK

[![pub package](https://img.shields.io/pub/v/flocon.svg)](https://pub.dev/packages/flocon)

Requirements: Dart 3.10 / Flutter 3.38.1 or later. Android, iOS and macOS are supported.

!!! tip "Let your AI agent do it"
    The [Flocon plugin](install-with-ai.md) adds the packages, starts Flocon and keeps it out of release builds for you.

## 1. Add the packages

```bash
flutter pub add flocon
```

Then add only the packages you need:

| Package | Adds | Depends on |
| :--- | :--- | :--- |
| `flocon` | connection, [Network](network.md) (WebSocket events, mocks, bad network), [Analytics](analytics.md), [Feature flags](featureflags.md), [Tables](table.md), [Dashboards](dashboard.md), [Deeplinks](deeplink.md), [Timeline](timeline.md) | `path_provider` |
| `flocon_dio` | HTTP calls of [Dio](https://pub.dev/packages/dio): `dio.withFlocon()` | `dio` |
| `flocon_http` | HTTP calls of [`package:http`](https://pub.dev/packages/http): `client.withFlocon()` | `http` |
| `flocon_database` | [Databases](database.md) (SQLite) | `sqflite` |
| `flocon_shared_preferences` | [Preferences](sharedpref.md) | `shared_preferences` |
| `flocon_files` | [Files](files.md) | `path_provider` |
| `flocon_crash_reporter` | [Crash reporter](crashreporter.md) | `path_provider` |
| `flocon_qr_scanner` | `FloconQrScannerView` to connect by scanning a QR code | `mobile_scanner` |

```bash
flutter pub add flocon_dio flocon_database flocon_shared_preferences
```

**Platform setup** (see `sample/` in the [repository](https://github.com/openflocon/FloconFlutter) for a working configuration):

- **Android**: the `INTERNET` permission in `AndroidManifest.xml`.
- **iOS**: `NSLocalNetworkUsageDescription` in `Info.plist`, so the app can reach your computer on the local network.
- **macOS**: the `com.apple.security.network.client` entitlement.

No cleartext or App Transport Security exception is needed: the SDK uses `wss://` and `https://` with the self-signed certificate of the desktop (encrypted, not authenticated).

## 2. Start Flocon

Start the SDK as early as possible, then `install` the features you use. A feature that is not installed does nothing instead of crashing, so calls to it can stay in your code.

```dart
import 'package:flocon/flocon.dart';
import 'package:flocon_database/flocon_database.dart';
import 'package:flocon_shared_preferences/flocon_shared_preferences.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  if (kDebugMode) await initializeFlocon(); // debug builds only
  runApp(const MyApp());
}

Future<void> initializeFlocon() async {
  final prefs = await SharedPreferences.getInstance();
  await startFlocon((flocon) => flocon
    ..install(FloconNetwork())
    ..install(FloconDatabase())
    ..install(FloconSharedPreferences(prefs)));
}
```

The plugins of the `flocon` package (network, analytics, tables, timeline, feature flags, deeplinks, dashboard) are always available through `Flocon.analytics`, `Flocon.timeline`, ...; install `FloconNetwork` only to give it options. The plugins of the other packages must be installed: `FloconDatabase`, `FloconSharedPreferences`, `FloconFiles`, `FloconCrashReporter`. Calling `startFlocon` more than once has no effect. `Flocon.isLoggingEnabled = true`, set before starting, prints what the SDK does.

## Release builds

Flocon is a debugging tool: once connected, the desktop can read your network calls, edit your preferences, delete files and run SQL on your databases. Keep it out of release builds.

Start Flocon only in debug builds with `kDebugMode` (a constant, removed from release builds by the compiler). Every other Flocon call (`Flocon.analytics`, `withFlocon()`...) does nothing when Flocon was not started. Use `!kReleaseMode` to also keep profile builds.

The packages stay in your `dependencies`, so their native code is still in the release binary. To keep the Flocon Dart code out of it too, start Flocon from a debug-only entry point and keep every Flocon import there:

```dart
// lib/main_debug.dart
import 'package:flocon/flocon.dart';
import 'package:flutter/widgets.dart';
import 'main.dart' as app;

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await startFlocon((flocon) => flocon..install(FloconNetwork()));
  app.main();
}
```

```bash
flutter run -t lib/main_debug.dart   # debug, with Flocon
flutter build apk                    # release, from lib/main.dart, without Flocon
```

## Next: connect

Run the app with Flocon Desktop open: in the iOS Simulator or as a macOS app it connects by itself, on an Android emulator use the host `10.0.2.2`. See [Connect](connect.md).
