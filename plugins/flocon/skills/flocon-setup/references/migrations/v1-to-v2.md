# Migrate Flocon 1.x to 2.0

Source of truth: `docs/migration.md` (dependencies, initialization, API mapping, behavior changes, pitfalls). Read it first; this file is the procedure.

## Procedure

1. **Inventory 1.x usage.** Search the whole project (all source sets, `*.gradle.kts`, `libs.versions.toml`, manifests, `res/xml`) for `openflocon`, `Flocon.`, `floconDashboard`, `floconAnalytics`, `floconTable`, `floconRegister`, `floconLog`. Note for each hit: module, source set (debug only or every variant), feature.
2. **Choose modules** from the feature each hit uses (table in `docs/migration.md` section 1). `flocon-base` no longer exists: replace it by the module of the feature it served (network, database).
3. **Update the catalog and gradle files.** Version to the new one; one entry per module in `implementation`, and `flocon-no-op` in release and in every other build type that ships (only accessibility and QR scanner keep their own `-no-op`, see `docs/setup.md`, "Release builds"). Version 2.0.0 itself had one `-no-op` per module instead; do not add those, they are no longer published.
4. **Rewrite the initialization**: `Flocon.initialize(ctx)` becomes `startFlocon(FloconContext(ctx)) { install(...) }` with one `install` per feature found in step 1, including `FloconDashboard` if dashboards are used. Move deeplink registration to `Flocon.deeplinksPlugin.register { }` if it happens after startup.
5. **Rewrite the call sites** with the mapping table of `docs/migration.md` section 3, then fix the imports (packages moved with the modules). If a project class has the same name as an SDK object, import the SDK one with an alias.
6. **Check what the compiler cannot**: every `Flocon.xxxPlugin` used has its `install`; if the project used databases, `install(FloconDatabase) { room() }` with the `flocon-database-room` (Room 2) or `-room3` dependency, and `logQuery` called with the id the desktop lists for that database; remove `flocon-device` and `install(FloconDevice)` if present (the module no longer exists, the app icon is sent without any plugin); remove the obsolete `localhost` / `127.0.0.1` cleartext exceptions only if the user agrees.
7. **Compile** every touched variant (list the Gradle tasks first: names depend on flavors). Run the module tests if there are any.
8. **Report**: what changed, what compiled, and what was not run (app on a device, connection to Flocon Desktop 2.0; the SDK and desktop must be updated together because the connection is now encrypted).

## Do not

- Keep both 1.x and 2.0 artifacts on the classpath.
- Commit, push or open a PR unless asked.
- Touch files outside the Flocon integration.
