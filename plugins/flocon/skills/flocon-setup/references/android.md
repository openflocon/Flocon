# Install Flocon in an Android project

Source of truth: `docs/setup.md`. Latest version: the newest release on https://github.com/openflocon/Flocon/releases.

## 1. Dependencies

Core, then one module per feature actually used (full list: `docs/migration.md` section 1). Real module in debug, `-no-op` in release:

```kotlin
dependencies {
    debugImplementation(libs.flocon)
    releaseImplementation(libs.flocon.noOp)

    debugImplementation(libs.flocon.network)           // flocon-network-core
    releaseImplementation(libs.flocon.network.noOp)    // flocon-network-core-no-op
    debugImplementation(libs.flocon.okhttp)            // flocon-okhttp-interceptor
    releaseImplementation(libs.flocon.okhttp.noOp)
}
```

Optionally import `flocon-bom` in both debug and release to pin every module to one version.

If a module that is compiled in every variant calls a plugin, give it the real module in debug and the no-op in release (and in every other non-debug variant).

## 2. Initialization

In `Application.onCreate`, as early as possible:

```kotlin
startFlocon(FloconContext(this)) {
    install(FloconNetwork)
    install(FloconAnalytics)
}
```

Always pass a `FloconContext`: `startFlocon(FloconContext(this))`, even though an overload taking the `Context` directly exists.

With debug-only dependencies, put this in a `debug` source set (and a no-op or empty counterpart in `release`), or in a `FloconInitializer` provided per variant.

## 3. Hook up each feature

| Feature | What to add |
| :--- | :--- |
| OkHttp | `okHttpClientBuilder.addInterceptor(FloconOkhttpInterceptor())` |
| gRPC | `FloconGrpcInterceptor()` from `flocon-grpc-interceptor-lite` as a client interceptor |
| Room | `install(FloconDatabase) { room() }`, `Flocon.databasePlugin.register("id", "name", db)`, and `RoomDatabase.Builder.floconLogs("id")` for query logs |
| SQLDelight | `flocon-database-sqldelight`: `driver.withFlocon("id", "name")` registers the database and logs its queries (`install(FloconDatabase)` is enough) |
| Analytics | `Flocon.analyticsPlugin.log(AnalyticsEvent(name, "table", props))` |
| Deeplinks | `install(FloconDeeplinks) { deeplink("myapp://home") }`, more later with `Flocon.deeplinksPlugin.register { }` |
| Dashboards | `install(FloconDashboard)`, then `Flocon.dashboardPlugin.dashboard(id, scope) { ... }` |

Details and options per feature: the matching `docs/<feature>.md` page.

## 4. Check

Compile debug and release. Then, with Flocon Desktop running and the app launched, `Flocon.connectionState` / `FloconLogger.enabled = true` shows whether it connects. Report if this was not run.
