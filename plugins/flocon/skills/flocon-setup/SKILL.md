---
name: flocon-setup
description: Use when installing Flocon in an Android, Kotlin Multiplatform, Swift (iOS) or Flutter project, or when migrating an existing Flocon integration to a newer SDK version (for example 1.x to 2.0) - adding dependencies, debug-only builds, initialization, plugin installation, and checking that each feature is really captured.
---

# Setting up or migrating Flocon in a project

Flocon is a debugging SDK: the app talks to Flocon Desktop. This skill puts the SDK in a project, or moves an existing integration to a newer version. It does not debug a running app (that is `flocon-mcp`).

Always follow the docs of the SDK used when they are available: for Kotlin, the repository docs `docs/setup.md` (install), `docs/migration.md` (1.x to 2.0) and the feature pages; for Swift, the README of `openflocon/FloconSwift` and its DocC comments; for Flutter, the README of `openflocon/FloconFlutter` and the Dart doc comments of the packages. If this skill and those docs disagree, the docs win; tell the user.

## 1. Detect the platform first

Answer from the project files; ask the user only what the files cannot tell.

| Look for | Platform |
| :--- | :--- |
| `com.android.application` / `com.android.library`, no `kotlin("multiplatform")` | Android |
| `kotlin("multiplatform")` with `commonMain` / `iosMain` | Kotlin Multiplatform (an iOS app consuming the KMP framework is still KMP for Flocon) |
| `*.xcodeproj` / `*.xcworkspace` / `Package.swift`, no Kotlin framework providing Flocon | Swift (iOS / macOS) |
| `pubspec.yaml` with `sdk: flutter` | Flutter (its `android/` and `ios/` folders are generated host apps: use the Flutter rules, not the Android or Swift ones) |

A repository can hold several apps: handle each app with its platform rules.

## 2. Then detect the version, with that platform's rules

**Android / KMP** (Kotlin SDK, `io.github.openflocon` artifacts):

| Look for | Tells you |
| :--- | :--- |
| `io.github.openflocon` in `libs.versions.toml` / `*.gradle.kts` | Flocon already used: read its version |
| `Flocon.initialize(` or `import io.github.openflocon.flocon.plugins.` | Kotlin SDK 1.x |
| `startFlocon(` | Kotlin SDK 2.x |

**Swift** (`openflocon/FloconSwift`): read the version from `Package.resolved` / `project.pbxproj` (revision or version of `FloconSwift`, and `FloconSwiftGrpc`). Do not use the start function as a version marker: the Swift SDK has both `startFlocon { }` and `Flocon.initialize()`. Details in [references/swift.md](references/swift.md).

**Flutter** (`flocon*` packages on pub.dev): read the version of `flocon` in `pubspec.lock`. Do not infer it from the start function. `0.0.x` is the current API, not a legacy one: the future `2.0.0` will have its own migration page in `references/migrations/`. Details in [references/flutter.md](references/flutter.md).

Then pick one path, and read only the file you need:

- No Flocon → install: [references/android.md](references/android.md), [references/kmp.md](references/kmp.md), [references/swift.md](references/swift.md) or [references/flutter.md](references/flutter.md).
- Kotlin Flocon 1.x → migrate: [references/migrations/v1-to-v2.md](references/migrations/v1-to-v2.md).
- Already on the target version → only add the missing plugins from the install page.

A future migration is a new file in `references/migrations/`.

## 3. Rules for every platform

- Work on a branch; do not touch unrelated uncommitted changes (use a separate worktree if the tree is dirty).
- Flocon must never be active in release builds. Say precisely what was delivered: **SDK inactive** in a configuration (nothing starts or intercepts) is not the same as **SDK absent from the binary**. Inspect the real build configurations / variants of the project (they are rarely only Debug and Release).
- Install only the plugins the project really uses; find them by searching the code, do not guess.
- A plugin that is not installed does nothing at runtime, it does not fail the build. After the change, list every plugin call and check each one is installed.
- After finding a configuration point (network client factory, image downloader, analytics provider…), search its callers and check it runs in the app target being tested. A hook in a function only the demo calls covers nothing.
- Do not wrap SDK calls in background tasks "for performance": the logging calls only queue values and send in the background. Measure the app's own mapping cost instead.
- Ending with a green compile is not proof it works: say what was not run (app on a device, connection to Flocon Desktop, Release build, camera…).
- Keep the user's device / simulator data, and do not start or restart Flocon Desktop if the user manages it.
- Never put project, company or app names in docs or skill files you write for Flocon itself.

## 4. Platform rules

**Android / KMP**
- Every real module has a `-no-op` twin with the same API, used in release and in any non-debug variant (dependency substitution for KMP).
- Compile every variant you touched (debug, release, and others such as an internal build). List the Gradle tasks first, their names depend on flavors.

**Swift**
- There is no no-op product. "Inactive in Release" is done with the project's debug compilation condition around imports, calls, types, hooks and menu entries; SPM still links the framework in every configuration. Removing it from the Release binary is a separate, architectural choice: ask once if the user's wording does not settle it.
- `.network` only covers `URLSession.shared`; every other session needs `withFlocon()` on its configuration before the session is created. Background sessions are not instrumented.
- Analytics, feature flags, gRPC, App Group storage and dashboards need explicit wiring: see the topic pages listed in [references/swift.md](references/swift.md).
- Build the touched schemes and configurations with `xcodebuild` (list them with `xcodebuild -list`).

**Flutter**
- There is no no-op package. "Inactive in release" is `if (kDebugMode)` around the start; the packages and their native code stay in the release binary. Removing the Flocon Dart code from it needs a debug-only entry point: ask once if the user's wording does not settle it. See [references/flutter.md](references/flutter.md).
- Network is only captured for clients wrapped with `withFlocon()`; top-level `http.get(...)` and clients created elsewhere are not. Inventory them.
- Use the API of the installed version (`pubspec.lock`): the facade (`Flocon.analytics`, `Flocon.featureFlags`...) and the top-level functions (`floconAnalytics(...)`) are two generations, see [references/flutter-data.md](references/flutter-data.md).
- The host differs per target (`10.0.2.2` Android emulator, `localhost` iOS Simulator / macOS, the computer's IP on a device), and each target needs its permission (`INTERNET`, `NSLocalNetworkUsageDescription`, macOS network entitlement).
- Run `flutter analyze` and build the targets the project ships, debug and release.
