# Migrate Flocon 2.0.0 per-module no-ops to `flocon-no-op`

Source of truth: `docs/migration.md`, section "After 2.0.0: one `flocon-no-op` for release builds", and `docs/install-kotlin.md`, "Release builds". Read them first; this file is the procedure.

## Procedure

1. **Inventory.** Search `*.gradle.kts` and `libs.versions.toml` for `openflocon` and `-no-op`. Note for each hit: module, configuration (`debugImplementation`, `releaseImplementation`, other build types, dependency substitution).
2. **Update the version** to the new one.
2b. **Merged modules**: remove `flocon-network-core`, `flocon-analytics`, `flocon-files`, `flocon-sharedprefs`, `flocon-deeplinks`, `flocon-feature-flags`, `flocon-timeline` and `flocon-crashreporter` (catalog entries and dependencies): they are now part of `flocon`, with the same packages and APIs. Keep the interceptors, `flocon-datastores`, `flocon-tables` and `flocon-database-*`.
3. **Real modules**: `debugImplementation` of each Flocon module becomes `implementation`, except `flocon-accessibility` and `flocon-qr-scanner`, which stay in `debugImplementation`.
4. **No-ops**: remove every module `-no-op` (catalog entries and dependencies), except `flocon-accessibility-no-op` and `flocon-qr-scanner-no-op`. Keep `flocon-no-op` in `releaseImplementation`, and add it to every other build type that ships.
5. **KMP**: in the dependency substitution of the app, keep only the lines for `flocon-accessibility` and `flocon-qr-scanner` (remove the block if neither is used).
6. **Initialization** can stay as is. A `startFlocon` call that was moved to a `debug` source set because the modules were debug-only may now live in `main`; only move it if the user asks.
7. **Compile** debug and release (list the Gradle tasks first: names depend on flavors).
8. **Report**: what changed, what compiled, and whether the release is minified (R8 removes the Flocon code) or not (Flocon inactive, its code stays in the binary).

## Do not

- Keep a removed `-no-op` artifact: it is no longer published and the build fails to resolve it.
- Commit, push or open a PR unless asked.
- Touch files outside the Flocon integration.
