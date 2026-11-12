# Flutter: analytics, flags, timeline, storage and other plugins

Source of truth: the matching section of the FloconFlutter README. Start the SDK first ([flutter.md](flutter.md) section 4).

## Two API generations

The facade API below (`Flocon.analytics`, `Flocon.featureFlags`, ...) is on `main` of FloconFlutter, after the `0.0.2` pub.dev release. `0.0.2` has top-level functions instead. Read the version in `pubspec.lock` and use the matching column; do not mix them.

| Feature | `main` (next release) | pub.dev `0.0.2` |
| :--- | :--- | :--- |
| Analytics | `Flocon.analytics.sender('firebase').logEvents([...])` | `floconAnalytics('firebase').logEvents([...])` |
| Feature flags | `Flocon.featureFlags.register('firebase', [...])` | `floconFeatureFlags('firebase').register([...])` |
| Timeline | `Flocon.timeline.logNavigation(...)` | `floconTimeline.logNavigation(...)` |
| Deeplinks | `Flocon.deeplinks.register([...], variables: [...])` | `floconRegisterDeeplinks([...], variables: [...])` |
| Dashboard | `Flocon.dashboard.dashboard('id', [...])` | `floconDashboard('id', [...])` |
| Tables | `Flocon.table.table('users').log([...])` | `floconTable('users').log([...])` |
| Databases | `Flocon.database.register(name, path)`, `Flocon.database.logQuery(path, sql, args)` | `floconRegisterDatabase(name, path)`, `floconLogDatabaseQuery(path, sql, args)` |
| Preferences | `Flocon.preferences.register(prefs, name:)` | `floconRegisterSharedPreferences(prefs, name:)` |
| Files | `Flocon.files.register(name, directory)` | `floconRegisterFilesFolder(name, directory)` |
| WebSocket | `Flocon.network.logWebSocketEvent(event)` | `floconLogWebSocketEvent(event)` |

On `main`, `Flocon.database`, `Flocon.preferences` and `Flocon.files` are extensions declared by the plugin packages: import both `package:flocon/flocon.dart` and the plugin package.

The rest (`startFlocon`, `install`, `withFlocon`, `FloconNetwork`) is the same. Everything is a no-op before `startFlocon`.

## Bridge, do not duplicate

Analytics, feature flags and the timeline are fed from the app's existing abstractions: find the analytics wrapper, the remote-config wrapper, the router, and add the Flocon call there, once. Do not add calls at each call site, and do not depend on the provider SDKs from Flocon.

| Feature | What to add |
| :--- | :--- |
| Analytics | one stream per provider (`'firebase'`, `'amplitude'`): `AnalyticsEvent(eventName:, properties: [AnalyticsProperty(name, value)])` |
| Feature flags | register the flags the app evaluated per provider, again after each refresh: each call replaces the flags of the provider. `FloconFeatureFlag(name:, state: on / off / custom, variant:, payload:)` |
| Timeline | `MaterialApp(navigatorObservers: [FloconNavigatorObserver()])` (name the routes: `RouteSettings.name`, otherwise the route type is used), `FloconLifecycleObserver.start()`, and `logUserAction` / `logTagged` for the app's own events. With a router package that does not use `NavigatorObserver`, log navigation from its listener |
| Deeplinks | list the app's deeplinks, parts to fill between brackets (`myapp://user/[userId]`), with `FloconDeeplinkParameter` / `FloconDeeplinkVariable`. Find them in the router and the manifest / `Info.plist` |
| Dashboard | `FloconSection`, `FloconSection.listen(ValueListenable)`, `FloconSection.fromStream`, elements (`FloconText`, `FloconButton`, `FloconTextField`, `FloconCheckBox`, `FloconForm`...). Keep the returned handle and call `dispose()` when done. Element ids must be unique in a dashboard |
| Tables | any structured data: `TableColumnConfig(name, value)`, columns are created from the names |

## Storage plugins

| Plugin | Package | Notes |
| :--- | :--- | :--- |
| `FloconSharedPreferences(prefs)` | `flocon_shared_preferences` | edit `String`, `bool`, `int`, `double`, `List<String>`; more instances with `Flocon.preferences.register(prefs, name:)` |
| `FloconFiles()` | `flocon_files` | `Documents`, `Application Support`, `Caches`, `tmp` and the app data folder shown by default; `Flocon.files.register(name, Directory)` for others. The Desktop can delete files: say so |
| `FloconDatabase()` | `flocon_database` | SQLite files of the databases / documents / support folders are listed without setup, whatever the library (sqflite, Drift, plain). `Flocon.database.register(name, path)` for others. Query logs only appear if the app calls `Flocon.database.logQuery(path, statement, args)` from its library hook (for Drift, a `QueryInterceptor`) |
| `FloconCrashReporter()` | `flocon_crash_reporter` | hooks `FlutterError.onError` and `PlatformDispatcher.instance.onError`; existing handlers (Crashlytics, Sentry) keep working. Native crashes are not reported. Install it after the other crash tools if the project replaces the handlers itself |

Because the Desktop can edit preferences, run SQL and delete files, these plugins are the reason Flocon must not start in release builds ([flutter.md](flutter.md) section 6).

## Custom plugins

Implement `FloconPlugin` (`pluginName`, `onConnectedToServer`, `onMessageReceived`) and `Flocon.register(MyPlugin())` after `startFlocon`, or give it a `FloconPluginFactory` to `install` it. Messages go through `Flocon.messageSender`.

## Check

List every Flocon call added and the package providing it, and check each package is in `pubspec.yaml` and each plugin that needs `install` is installed (an uninstalled plugin does nothing at runtime, it does not fail the build). Flags, analytics and deeplinks only show up after the app ran and connected: say if this was not run.
