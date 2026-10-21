---
paths:
  - "FloconAndroid/**"
---

# Android / KMP SDK rules (`FloconAndroid/`)

## Multiplatform
- Put code in `commonMain` unless it truly needs a platform API. Targets are Android, iOS, JVM (desktop) and WasmJs: every new API must compile on all of them.
- Platform-specific code goes in `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` behind an `expect`/`actual` or an interface, never in common code.

## Plugin + no-op parity
- Every published module `X` has a sibling `X-no-op` (e.g. `featureflags` / `featureflags-no-op`, `network:core` / `network:core-no-op`). Both are registered in `settings.gradle.kts`.
- The no-op module exposes **exactly the same public API** (same packages, class names, function signatures, extension properties such as `Flocon.featureFlagsPlugin`). Any change to a public signature in `X` must be mirrored in `X-no-op` in the same change.
- No-op implementations do nothing: return `Unit`, empty values, or pass-through. They must not pull heavy dependencies.
- A plugin is a `FloconPluginFactory<Config, Plugin>` object with a `pluginId` matching the protocol plugin name (see `protocol/`).

## Code style
- Package root: `io.github.openflocon.flocon.<feature>`.
- Keep public API minimal; mark implementation details `internal`.
- Coroutines & Flow for async work; no blocking calls on the caller's thread.
- Manual / constructor injection only: no DI framework in the SDK.
- Dependency versions live only in `gradle/libs.versions.toml`; never hardcode a version in a `build.gradle.kts`. Shared build config goes in `build-logic/`.

## Samples
- When adding or changing a user-facing SDK feature, wire it into the sample apps (`sample/` and `sample-multiplatform/`) so it can be exercised on each platform.
