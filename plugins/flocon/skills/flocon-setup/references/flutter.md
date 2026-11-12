# Install Flocon in a Flutter project

The Flutter SDK lives in its own repository: `https://github.com/openflocon/FloconFlutter` (packages published on pub.dev). Its README and the Dart doc comments are the source of truth for the API; if this page disagrees with them, they win: tell the user.

Written against the pub.dev release `0.0.2` (tag `flocon-v0.0.2`) and `main` of FloconFlutter at `b304dbe`. Check what changed in later releases before applying a recipe (`CHANGELOG.md` of the packages, or `git log` of a fresh clone). A `2.0.0` is planned for the "official" release: when it exists, add a migration page in `migrations/` and bump the revision above.

Topic pages, read only the one you need:

- [flutter-network.md](flutter-network.md): Dio, `package:http`, WebSockets, images, mocks.
- [flutter-data.md](flutter-data.md): analytics, feature flags, timeline, deeplinks, dashboard, tables, databases, preferences, files, crash reporter, custom plugins.

## 1. Detect what is there

Answer from the files before asking the user anything:

| Look for | Tells you |
| :--- | :--- |
| `pubspec.yaml` with `sdk: flutter` | Flutter project (its `android/`, `ios/`, `macos/` folders are generated host apps: do not apply the Android or Swift recipes to them) |
| `flocon`, `flocon_*` in `pubspec.yaml` / `pubspec.lock` | Flocon already used: `pubspec.lock` gives the exact version (or the git `resolved-ref`) |
| `startFlocon(` / `import 'package:flocon/flocon.dart'` | Where the SDK is started |
| `dio`, `http`, `shared_preferences`, `sqflite` / `drift` in `pubspec.yaml` | Packages to install (see section 3) |
| `kotlin("multiplatform")` / a Kotlin framework providing Flocon | Not Flutter: follow [kmp.md](kmp.md) |

A project depending on `flocon` through git, or on a version older than the one this page is written for, is not "legacy": compare its API with the README of that ref before changing anything.

## 2. Compatibility

| Item | Value |
| :--- | :--- |
| Platforms | Android, iOS, macOS (tested in `sample/`); the packages also declare Linux and Windows |
| Toolchain | Dart `^3.10`, Flutter `>=3.38.1` (`pubspec.yaml` of `flocon`) |
| Distribution | pub.dev: `flutter pub add flocon` (and the other packages below). Before a version is published, a `git:` dependency with `path: packages/<name>` on `https://github.com/openflocon/FloconFlutter.git` |
| Transport | `wss://host:9023` (WebSocket) and `https://host:9024` (file uploads/downloads), self-signed certificate of the desktop accepted without verification. Ports are configurable in the desktop settings and in `connect(webSocketPort:, httpPort:)` |
| Flocon Desktop | Must accept the encrypted connection (same requirement as the Swift SDK, see [swift.md](swift.md) section 2) |
| MCP | Flocon Desktop serves MCP on `http://localhost:9042/mcp`. Not used by the app |

Never add a cleartext-traffic or App Transport Security exception to "fix" a connection: the SDK does not need one. A connection problem is a Desktop version, host, port or local-network permission problem.

## 3. Add the packages

Core, then one package per feature actually used (find them in `pubspec.yaml` and the code, do not guess):

| Package | Add it when the app uses |
| :--- | :--- |
| `flocon` | always: connection, app icon, network core, analytics, tables, timeline, feature flags, deeplinks, dashboard, custom plugins |
| `flocon_dio` | `dio` |
| `flocon_http` | `package:http` |
| `flocon_shared_preferences` | `shared_preferences` |
| `flocon_files` | files the developer wants to browse from the desktop |
| `flocon_database` | SQLite (`sqflite`, Drift, plain files) |
| `flocon_crash_reporter` | uncaught Dart / Flutter errors |
| `flocon_qr_scanner` | connecting a physical device by scanning the QR code (adds `mobile_scanner`, camera permissions, Android `minSdk` 23 / `compileSdk` 36 / AGP 8.9.1) |

```bash
flutter pub add flocon flocon_dio
```

They stay in `dependencies`: a `dev_dependency` cannot be imported from `lib/`.

## 4. Start the SDK

As early as possible, before the network clients you want to see are created. `startFlocon` is async, calling it twice does nothing, and every Flocon call is a no-op before it.

```dart
import 'package:flocon/flocon.dart';
import 'package:flutter/foundation.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  if (kDebugMode) await initializeFlocon();
  runApp(const MyApp());
}

Future<void> initializeFlocon() async {
  await startFlocon((flocon) => flocon
    ..connect(host: Platform.isAndroid ? '10.0.2.2' : 'localhost')
    ..install(FloconNetwork())
    ..install(FloconFiles())
    ..install(FloconDatabase())
    ..install(FloconCrashReporter())
    ..install(FloconSharedPreferences(prefs)));
}
```

Install only the plugins whose package was added. Plugins of the `flocon` package (analytics, tables, timeline, feature flags, deeplinks, dashboard) need no `install`; `FloconNetwork` only to give it options.

| Where the app runs | Host |
| :--- | :--- |
| iOS Simulator, macOS | `localhost` (default) |
| Android emulator | `10.0.2.2` |
| Physical device | the local IP of the computer running Flocon Desktop, given in code or typed / scanned at runtime with `Flocon.connect(address)` |

## 5. Platform setup

- **Android**: the `INTERNET` permission in `AndroidManifest.xml`.
- **iOS**: `NSLocalNetworkUsageDescription` in `Info.plist`.
- **macOS**: the `com.apple.security.network.client` entitlement, in **both** `DebugProfile.entitlements` and `Release.entitlements`.
- With `flocon_qr_scanner`: `NSCameraUsageDescription` (iOS, macOS), `CAMERA` permission (Android), `com.apple.security.device.camera` (macOS).

`sample/` of FloconFlutter shows a working configuration of the three platforms.

## 6. Debug only

Two different requirements hide behind "only in debug" (same distinction as the Swift page):

1. **SDK inactive in release**: `if (kDebugMode)` around `initializeFlocon()`. `kDebugMode` is a constant, so the call is removed from release builds by the compiler. Every other call (`Flocon.analytics`, `withFlocon()`, ...) does nothing when Flocon was not started, so it can stay in the code. Use `!kReleaseMode` to keep it in profile builds too.
2. **Flocon absent from the release binary**: not possible with `dependencies` alone: the packages, their native code and their dependencies (`device_info_plus`, `sqflite`, `mobile_scanner`...) are still linked. To keep at least the Flocon Dart code out, start Flocon from a debug-only entry point (`lib/main_debug.dart` calling `app.main()`, run with `flutter run -t lib/main_debug.dart`) and keep every Flocon import in files only that entry point reaches. This changes how the app is launched: ask once if the user's wording does not settle it.

In the final report say which one was delivered.

## 7. Hook up each feature

| Feature | Page |
| :--- | :--- |
| Dio, `package:http`, WebSockets, mocks, images | [flutter-network.md](flutter-network.md) |
| Everything else | [flutter-data.md](flutter-data.md) |

## 8. Check

`flutter pub get`, `flutter analyze`, then build the targets the project ships (`flutter build apk --debug`, `flutter build ios --debug --no-codesign`, `flutter build macos --debug`, and the release builds). With Flocon Desktop running and the app launched, `Flocon.isLoggingEnabled = true` prints what the SDK does, and `Flocon.connectionState` shows whether it connects. Report what was not run (device, Desktop connection, release build).
