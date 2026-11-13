# Install Flocon in an Android project

Source of truth: `docs/install-kotlin.md`. Latest version: the newest release on https://github.com/openflocon/Flocon/releases.

## 1. Dependencies

Core, then one module per feature actually used (full list: `docs/migration.md` section 1). Real modules in `implementation`, plus `flocon-no-op` in release:

```kotlin
dependencies {
    implementation(libs.flocon)
    implementation(libs.flocon.network)                // flocon-network-core
    implementation(libs.flocon.okhttp)                 // flocon-okhttp-interceptor

    releaseImplementation(libs.flocon.no.op)          // flocon-no-op (alias "flocon-no-op"): turns Flocon off
}
```

Optionally import `flocon-bom` with `implementation(platform(...))` to pin every module to one version.

Add `flocon-no-op` to every build type that ships (e.g. an internal distribution build), not only `release`. The per-module `-no-op` artifacts no longer exist, except `flocon-accessibility-no-op` and `flocon-qr-scanner-no-op`: for these two, real module in `debugImplementation` and the no-op in release (their manifest entries can't be removed by `flocon-no-op`).

`flocon-no-op` makes Flocon inactive in any build; its R8 rule removes the Flocon code only when the release is minified. Report which one the project gets (inactive vs absent from the binary).

## 2. Initialization

In `Application.onCreate`, as early as possible:

```kotlin
startFlocon(FloconContext(this)) {
    install(FloconNetwork)
    install(FloconAnalytics)
}
```

Always pass a `FloconContext`: `startFlocon(FloconContext(this))`, even though an overload taking the `Context` directly exists.

The modules are in every variant, so this call can stay in `main`: with `flocon-no-op`, `startFlocon` starts nothing.

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
