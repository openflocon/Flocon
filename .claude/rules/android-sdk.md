---
paths:
  - "FloconAndroid/**"
---

# Android / KMP SDK rules (`FloconAndroid/`)

## Multiplatform
- Put code in `commonMain` unless it truly needs a platform API. Targets are Android, iOS, JVM (desktop) and WasmJs: every new API must compile on all of them.
- Platform-specific code goes in `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` behind an `expect`/`actual` or an interface, never in common code.

## Modules
- Each feature lives in a folder named after it: `featureflags/featureflags` (Gradle `:featureflags:featureflags`), registered in `settings.gradle.kts`. The Maven `artifactId` is set explicitly in each module's `mavenPublishing { coordinates(...) }`, so moving a module never changes its published coordinates.
- A plugin is a `FloconPluginFactory<Config, Plugin>` object with a `pluginId` matching the protocol plugin name (see `internal-docs/protocol/`).

## Turning Flocon off (`flocon-no-op`)
- Apps use the real modules in every variant, plus `flocon-no-op` in release. It turns `FloconRuntime.isEnabled()` off at runtime, and its consumer R8 rule (`-assumevalues`) lets R8 remove the code behind the switch.
- So every public entry point must check the switch first, before touching any implementation class:
  - a `Flocon.xxxPlugin` accessor goes through `floconPlugin(name, noOp = { ... }, installed = { ... })`, with a private no-op implementation of the plugin interface;
  - an interceptor, listener or receiver passes the call through when `!FloconRuntime.isEnabled()`.
- `FloconRuntime`, `floconPlugin` and `@InternalFloconApi` are internal to the SDK; the opt-in is enabled for the Flocon modules in `build-logic`.
- Don't add new `-no-op` modules. The only ones left are `accessibility-no-op` and `qrscanner-no-op`, because their Android manifest entries (a permission, ML Kit) can't be removed by `flocon-no-op`; they keep **exactly the same public API** as their real module, mirrored in the same change. A new module that needs Android manifest entries should avoid them, or ask before adding a no-op.
- `scripts/check-release-noop.sh` (run in CI after the sample release build) fails if R8 kept a plugin implementation or the connection, or if the release manifest has entries of the debug modules.

## Code style
- Package root: `io.github.openflocon.flocon.<feature>`.
- Keep public API minimal; mark implementation details `internal`.
- Coroutines & Flow for async work; no blocking calls on the caller's thread.
- Manual / constructor injection only: no DI framework in the SDK.
- Dependency versions live only in `gradle/libs.versions.toml`; never hardcode a version in a `build.gradle.kts`. Shared build config goes in `build-logic/`.

## Samples
- When adding or changing a user-facing SDK feature, wire it into the sample apps (`sample/` and `sample-multiplatform/`) so it can be exercised on each platform.
