# Plan: SQLDelight support for the Flocon database plugin

Status: PR 1, PR 2 and PR 4 implemented (module, query logging, samples, docs); PR 3 (profiler fields, transactions), the `sqlDelight()` provider and the wasm sample are not done. Notes from the implementation: `SqlCursor` has no column names nor count (2.4.1), they are read through a temporary view inside a rolled back transaction, `column_N` for PRAGMA/EXPLAIN; transactions are not tracked yet (SQLDelight does not run them through `execute`).

Original status: proposal. Coordinates with `internal-docs/plans/database-profiler.md` (separate branch/PR) for the profiler fields.

## 1. Goal

Let an app that uses **SQLDelight 2.x** browse its database, run SQL from the desktop app and see its live query log,
exactly like Room 2 (`flocon-database-room`) and Room 3 (`flocon-database-room3`) today. Two new published
modules, `flocon-database-sqldelight` and `flocon-database-sqldelight-no-op`, following the same layout, publishing and
no-op rules as the Room modules.

Non-goals: designing the database profiler (see `internal-docs/plans/database-profiler.md`), changing the desktop database screen,
supporting non-SQLite SQLDelight dialects (Postgres/MySQL via R2DBC are out of scope, see open questions).

## 2. What exists today (studied)

- Core: `FloconAndroid/database/core/core` (`FloconDatabase`, `FloconDatabaseConfig.providers`, `FloconDatabasePlugin`,
  `FloconDatabaseModel { id, displayName, suspend executeQuery(query): DatabaseExecuteResponse }`,
  `FloconDatabasePluginImpl` which handles `get_databases` / `query` and exposes `logQuery(dbId, sql, bindArgs)`).
  No-op: `database/core/core-no-op`.
- A provider is a `FloconDatabaseProvider.getAllDataBases(...)`, added in `FloconDatabaseConfig` by an extension, e.g.
  `fun FloconDatabaseConfig.room(block)` in `database/room3/room3/src/roomMain/.../FloconRoom3DatabaseConfig.kt`.
  The provider scans files on Android (`FloconRoom3DatabaseProviderImpl.android.kt`, depth 7 under `getDatabasePath`)
  and returns `FloconFileDatabaseModel`; explicit registration goes through
  `FloconDatabasePlugin.register(id, displayName, database)` (`FloconRoom3DatabaseModel.kt`).
- Room 3 query logging: no callback, so `SQLiteDriver.floconLogs(id)` wraps driver, connection and statement
  (`FloconLoggingSQLiteDriver.nonWeb.kt`, `QueryLogger.kt`); Room 2 uses `QueryCallback`
  (`database/room/room/src/androidMain/.../extensions/RoomBuilderExt.kt`, `floconLogs(id)`).
- Executing arbitrary SQL from the desktop: `FloconRoom3DatabaseModel.executeQuery` switches on the first word
  (`SELECT/PRAGMA/EXPLAIN` -> `Select`, `INSERT` -> `Insert` via `last_insert_rowid()`, `UPDATE/DELETE` -> `UpdateDelete`
  via `changes()`, else `RawSuccess`; any throwable -> `Error`). Responses: `DatabaseExecuteSqlResponse` in
  `database/core/core/.../model/fromdevice`.
- Protocol: `protocol/database.md` (`get_databases`, `query`, `log_query` with `dbId`, `sqlQuery`, `bindArgs`, `timestamp`),
  both `Protocol.kt` files. Docs: `docs2/database.md` (already advertises "SQLDelight" in its description, but has no
  SQLDelight section), `docs2/setup.md` (table row ~line 173), root `README.md`, setup skill
  `plugins/flocon/skills/flocon-setup/references/android.md` (Room row, ~line 46) and `kmp.md`.
- Gradle: modules are declared in `FloconAndroid/settings.gradle.kts` (`include(":database:room3:room3")` + no-op),
  built with `flocon.kotlin.library`, `flocon.android.library`, `flocon.ios.library`, `flocon.jvm.library`,
  `flocon.wasm.library`, `flocon.publish` (`FloconAndroid/build-logic/convention`). `FloconAndroid/bom/build.gradle.kts`
  adds every published subproject automatically (constraints on subprojects with the maven-publish plugin), so the BOM
  needs no manual edit. `FloconAndroid/gradle/libs.versions.toml` has Room/androidx.sqlite/sqlite-jdbc but **no SQLDelight
  entry yet**.
- Samples: `FloconAndroid/sample/android/build.gradle.kts` (module + no-op swapping map for release, ~lines 51 and 150),
  `FloconAndroid/sample-multiplatform` (Room 2/3 in `src/androidMain/.../multi/Databases.kt`, `room2/CatDatabase.kt`),
  `sample/desktop/.../Main.kt`, `sample/ios/.../Databases.kt`, `sample/wasm/.../Databases.kt`.

## 3. Design

### 3.1 Modules and layout

```
FloconAndroid/database/sqldelight/
  sqldelight/            artifactId flocon-database-sqldelight
    src/commonMain       config (sqlDelight() provider), model, logging driver, QueryLogger
    src/androidMain      file-scan provider (same scan as Room 3)
    src/jvmMain, iosMain provider actuals (no scan, like room3)
    src/wasmJsMain       provider actual (empty), async handling
    src/jvmTest          tests with JdbcSqliteDriver.IN_MEMORY
  sqldelight-no-op/      artifactId flocon-database-sqldelight-no-op, same public API, empty bodies
```

Package: `io.github.openflocon.flocon.database.sqldelight` (Android namespace of the no-op ends with `.noop`, like room3-no-op).
Register both in `settings.gradle.kts` (`:database:sqldelight:sqldelight`, `:database:sqldelight:sqldelight-no-op`).
Dependencies: `api(projects.flocon.flocon)`, `api(projects.database.core.core)`, `api(libs.sqldelight.runtime)`
(runtime only; the SQLDelight Gradle plugin and generated code stay in the app). No-op: `api(projects.database.core.coreNoOp)`
and `api(libs.sqldelight.runtime)`, mirroring `room3-no-op`.

Version catalog additions: `sqldelight` version, `sqldelight-runtime`, driver artifacts for the samples only
(`android-driver`, `sqlite-driver` for JVM, `native-driver`, `web-worker-driver`, `async-extensions`), and the Gradle plugin
id for the samples. Pin the latest stable 2.x at implementation time.

KMP targets: Android, JVM, iOS (native driver), wasmJs (web worker driver, async). Same set as `room3`
(which also applies `flocon.wasm.library`). Kotlin/JS (non-wasm) is not a Flocon target.

### 3.2 Integration API

Primary, explicit and cross-platform (SQLDelight drivers are plain objects, there is no builder hook like Room's):

```kotlin
val driver = AndroidSqliteDriver(AppDatabase.Schema, context, "app.db")
    .withFlocon(id = "app", displayName = "App database")   // io.github.openflocon.flocon.database.sqldelight
val database = AppDatabase(driver)
```

`withFlocon(id, displayName = id, logQueries = true)` returns a delegating `SqlDriver` and, as a side effect, calls
`Flocon.databasePlugin.register(FloconSqlDelightDatabaseModel(id, displayName, driver))`. The model is a `data class`
over the wrapped driver, like `FloconRoom3DatabaseModel`, so core's `register` (which tolerates an equal model and
rejects a different one with the same id) behaves the same.

Also exposed, for apps that want to separate the two concerns: `FloconDatabasePlugin.register(id, displayName, driver)`
(same shape as the Room `register`) and `SqlDriver.floconLogs(id)` (same name as Room 3, logging only).
`withFlocon` = `floconLogs` + `register`.

Provider: `install(FloconDatabase) { sqlDelight() }` adds a `FloconSqlDelightDatabaseProvider`, as `room()` does. It
matters because (1) the module is visibly part of the install, with no-op parity, and (2) on Android it can scan database
files like the Room providers (`FloconFileDatabaseModel`, read through `openDbAndExecuteQuery` in core, so databases appear
before the app creates the driver; id = absolute path). A database registered with `withFlocon("app")` would then also
show up as a file entry: de-duplicate by absolute path (the explicit model keeps the file path when the driver is
Android/JDBC) so only the explicit registration is shown. Room 3 has the same potential duplicate: check how the desktop
treats it today and align both. `sqlDelight { path("...") }` mirrors `FloconRoom3DatabaseConfig.path`.

Auto-detection vs explicit: on Android the file scan gives zero-config browsing (no logs); explicit `withFlocon` gives logs
and uses the live driver, which matters for in-memory databases and for SQLCipher/custom drivers where opening the file
from outside cannot work. Document both, recommend explicit.

### 3.3 Executing SQL from the desktop (`executeQuery`)

`FloconSqlDelightDatabaseModel.executeQuery(query)` runs against the **unwrapped** driver, so desktop queries are not
logged as app queries. Same first-word switch as Room 3 for the result type.

SQLDelight 2.x `SqlDriver` API (verify exact signatures against the pinned version):
`execute(identifier: Int?, sql: String, parameters: Int, binders: (SqlPreparedStatement.() -> Unit)?): QueryResult<Long>`,
`executeQuery(identifier, sql, mapper: (SqlCursor) -> QueryResult<R>, parameters, binders): QueryResult<R>`,
`newTransaction(): QueryResult<Transacter.Transaction>`, `currentTransaction()`, `addListener/removeListener/notifyListeners`, `close()`.

- `SELECT/PRAGMA/EXPLAIN`: `driver.executeQuery(null, sql, mapper, 0)`; the mapper loops on `SqlCursor.next()` and reads each
  column with `getString(i)` (null stays null), like `executeSelect` in Room 3. **Column names**: `SqlCursor` has not always
  exposed them. Spike first: if the pinned 2.x has no column name accessor, fall back to a small `expect` hook reaching the
  platform handle (JDBC `ResultSetMetaData`, androidx `SQLiteStatement.getColumnName`), and finally `column_0..n`.
  This is the largest technical risk (section 8).
- `INSERT`: `execute(...)` then `SELECT last_insert_rowid()`. `UPDATE/DELETE`: `execute(...)` already returns the affected
  row count (`QueryResult<Long>`). Other statements: `RawSuccess`. Any `Throwable` -> `DatabaseExecuteSqlResponse.Error(message, originalSql)`.
- **Sync vs async drivers**: `QueryResult<T>` is `Value<T>` (sync) or `AsyncValue<T>` (suspend `await()`). The model's
  `executeQuery` is already `suspend`, so always `await()` (free on sync drivers). On web (`WebWorkerDriver`) only async
  results exist: never use `.value`. `async-extensions` is only needed by the web sample.
- Threading: sync drivers block, so run on a background dispatcher on non-web targets (check what Room 3 does with
  `floconConfig.scope`).
- Writes from the desktop do not notify SQLDelight query listeners (Flows do not refresh). Optionally call
  `driver.notifyListeners(...)` when table names can be parsed cheaply; otherwise document it.

### 3.4 Schema listing

The desktop lists tables by running SQL itself (`sqlite_master`, `PRAGMA table_info`), so **no SDK schema API is needed**:
`executeQuery` handles it as long as `PRAGMA` and `sqlite_master` selects go through the `Select` path.

### 3.5 Query logging

`FloconLoggingSqlDriver(delegate: SqlDriver, dbId, logger: QueryLogger) : SqlDriver by delegate`. `QueryLogger` and
`DatabasePluginQueryLogger` are copied from room3 (`QueryLogger.kt`) rather than moved to core, unless a third consumer appears.

- `execute(...)` and `executeQuery(...)`: wrap `binders` in a recording `SqlPreparedStatement` (`bindBytes/bindLong/bindDouble/
  bindString/bindBoolean` write into an index -> value map; SQLDelight statements are write-only, so recording at bind time is the
  only way to get the arguments), run the delegate, then log. For selects, wrap `mapper` to count rows.
- `newTransaction()`: returns a wrapped `Transacter.Transaction` carrying a `transactionId` (and commit/rollback outcome through
  `endTransaction(successful)`); statements read the wrapper's `currentTransaction()` to find their id.
- `addListener/removeListener/notifyListeners/close`: pure delegation.
- Async delegates: when the result is an `AsyncValue`, log when it resolves (wrap in `QueryResult.AsyncValue { ... await() ... }`)
  and never turn async into sync. Sync delegates: measure inline.
- Log once per execution (when the result is produced), not on prepare.
- Logging must never break a query: `try/catch` around every logger call, like Room 3.
- `logQueries = false` registers the database for browsing only.

### 3.6 Profiler fields (coordination with `internal-docs/plans/database-profiler.md`)

That plan owns the protocol and desktop design of the optional `durationMs`, `threadName`, `isMainThread`, `rowCount`,
`transactionId` on `log_query`. This module only makes them **measurable in the wrapper** and passes them through the plugin
entry point once it accepts them. Proposed contract, to settle with that plan:

| Field | Measured in | How |
|-------|-------------|-----|
| `durationMs` | `FloconLoggingSqlDriver.execute/executeQuery` | monotonic clock (`TimeSource.Monotonic`) around the delegate call; for `AsyncValue` around `await()`; for selects, stop after the mapper finishes (documented). |
| `threadName` | same | expect/actual helper (JVM/Android `Thread.currentThread().name`, iOS `NSThread`, wasm `"main"`). Reuse the one from the profiler plan/core if it exists, do not duplicate. |
| `isMainThread` | same | Android `Looper.getMainLooper() == Looper.myLooper()`, iOS `NSThread.isMainThread`, JVM false, wasm true. |
| `rowCount` | select mapper wrapper | number of `next()` returning true; for `execute`, the affected rows from `QueryResult<Long>`. |
| `transactionId` | `newTransaction()` wrapper | counter per wrapped driver stored in the wrapped `Transaction`; null outside a transaction. |

Implementation rule: the wrapper builds an internal `QueryLogEvent(sql, bindArgs, durationMs, threadName, isMainThread, rowCount,
transactionId)` and calls `logger.log(event)`. Until the profiler plan lands, the logger maps it to today's
`Flocon.databasePlugin.logQuery(dbId, sql, bindArgs)` and ignores the extra fields. New optional params on
`FloconDatabasePlugin.logQuery` (with defaults, backward compatible) are owned by the profiler plan; PR 3 below depends on it.

## 4. Protocol

**No protocol change.** `get_databases`, `query` and `log_query` are reused as is: both `Protocol.kt` files and
`protocol/database.md` stay untouched. The only additive, backward-compatible additions (optional, default null) are the
profiler fields on `log_query`, owned by `internal-docs/plans/database-profiler.md`. Optional idea, not required: a provider label
(e.g. `"sqldelight"`) on `Database` in `get_databases`, only if the desktop wants a badge.

## 5. Desktop

Nothing required: the desktop treats any registered database as an id + display name + SQL executor. A provider label would be
the only change. Manually verify the Database screen with the explicit registration and the file-scanned duplicate (3.2).

## 6. No-op parity, BOM, publishing

- `sqldelight-no-op` exposes the same public API: `FloconDatabaseConfig.sqlDelight(block)`, `SqlDriver.withFlocon(...)` (returns
  the driver unchanged), `SqlDriver.floconLogs(id)` (unchanged), `FloconDatabasePlugin.register(id, displayName, driver)` (empty),
  and the config class.
- Coordinates `flocon-database-sqldelight` / `flocon-database-sqldelight-no-op` through `flocon.publish`, pom name
  "Flocon Database SQLDelight". The BOM picks them up automatically (verified in `bom/build.gradle.kts`).
- Add the pair to the release swap map in `FloconAndroid/sample/android/build.gradle.kts` (~line 150).
- No reflection, so no ProGuard/R8 keep rules expected.

## 7. Samples, tests, docs

Samples (one small SQLDelight schema, e.g. `Dog`, with a `.sq` file):
- `sample-multiplatform`: SQLDelight on Android (`AndroidSqliteDriver`) and iOS (`NativeSqliteDriver`), registered in `Databases.kt`.
- `sample/desktop` (`JdbcSqliteDriver`), `sample/ios`, `sample/wasm` (`WebWorkerDriver`, async), `sample/android` (+ release swap).
- The SQLDelight Gradle plugin is applied in sample modules only.

Tests (`database/sqldelight/sqldelight/src/jvmTest`, `JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)`, mirroring
`room3/room3/src/jvmTest/.../FloconLoggingSQLiteDriverTest.kt`):
- logging driver: execute/executeQuery logged with sql and bind args (each bind type, null), one log per call, `logQueries=false`.
- measured fields: duration >= 0, rowCount for select and update, transactionId stable in a transaction and different across two.
- model `executeQuery`: select (columns, values, nulls), PRAGMA, insert id, update/delete count, raw DDL, syntax error -> `Error`.
- `register`: same model ok, different model with same id throws.
- async: a fake `SqlDriver` returning `QueryResult.AsyncValue`, asserts the log happens after `await()`.
- Commands: `./gradlew :database:sqldelight:sqldelight:jvmTest`, `:database:sqldelight:sqldelight-no-op:compileKotlinJvm`, and before the PR
  `:sample:android:assembleDebug :sample:android:assembleRelease`.

Docs (same PR as the code, `docs2/` only, never `docs/`):
- `docs2/database.md`: SQLDelight tab for dependencies, registration and logging, plus a web (async) note.
- `docs2/setup.md`: extend the `FloconDatabase` row (~line 173). Root `README.md` snippet.
- `plugins/flocon/skills/flocon-setup/references/android.md` (row next to Room) and `kmp.md`.
- `protocol/` and MCP docs: no change.

## 8. Open questions and risks

1. **Column names for arbitrary SELECT**: depends on what `SqlCursor` exposes in the pinned 2.x. Spike first; fallbacks in 3.3.
2. **Async drivers (web)**: `WebWorkerDriver` returns `AsyncValue` only: `await()` everywhere, no `.value`, no blocking. Transactions on
   async drivers are limited in SQLDelight: test them.
3. **Timing accuracy**: on async drivers `durationMs` includes the worker round-trip, not just SQLite time. Document it.
4. **R2DBC / non-SQLite dialects**: out of scope; the first-word switch and `last_insert_rowid()` are SQLite. Return a clear `Error`.
5. **Coroutines extensions** (Flow on top of driver listeners): logging works unchanged, but queries re-run on notification log again.
6. **Driver lifecycle**: the model holds the driver; if the app closes it, desktop queries return `Error`. Core has no `unregister`:
   add one only if needed, otherwise document.
7. **Multiple drivers / re-registration**: core `register` throws when the same id gets a different model, which breaks "recreate the
   driver" flows (tests, hot reload). Decide whether driver models compare by `id` only or core allows replacement.
8. **File-scan duplicate** with an explicit registration (3.2), shared with Room 3.
9. **Overhead**: per-statement allocations (arg map, timing). Compute profiler fields only when the plugin is connected.

## 9. TODO checklist

### SDK
- [x] Add SQLDelight 2.x entries to `FloconAndroid/gradle/libs.versions.toml` (runtime, sample drivers, plugin)
- [x] Spike: `SqlCursor` column-name access in the pinned version, pick the fallback (3.3)
- [x] Create `database/sqldelight/sqldelight` (Android, JVM, iOS, wasmJs), publish `flocon-database-sqldelight`
- [x] Create `database/sqldelight/sqldelight-no-op` with the same public API, publish `flocon-database-sqldelight-no-op`
- [x] Include both modules in `FloconAndroid/settings.gradle.kts`
- [x] `FloconSqlDelightDatabaseModel` (`executeQuery`, sync and async `QueryResult`)
- [x] `FloconDatabasePlugin.register(id, displayName, driver)`, `SqlDriver.withFlocon(...)`, `SqlDriver.floconLogs(id)`
- [ ] `sqlDelight()` provider on `FloconDatabaseConfig`, Android file scan, path de-duplication
- [x] `FloconLoggingSqlDriver` (recording `SqlPreparedStatement`, wrapped transactions, async-safe)
- [ ] Measure `durationMs`, `threadName`, `isMainThread`, `rowCount`, `transactionId` (internal `QueryLogEvent`)
- [ ] Plug the event into `logQuery` once the profiler plan adds the optional params
- [ ] Settle open questions 6 and 7 (unregister / re-register), core change only if needed

### Tests
- [x] `src/jvmTest`: logging driver, bind args, measured fields, transactions
- [x] `src/jvmTest`: model `executeQuery` (select, PRAGMA, insert, update/delete, raw, error)
- [x] `src/jvmTest`: async `AsyncValue` fake driver, duplicate registration
- [x] No-op `compileKotlinJvm`

### Samples
- [x] `sample-multiplatform` (Android + iOS) registered in `Databases.kt`
- [x] `sample/android` (+ release swap), `sample/desktop`, `sample/ios`, `sample/wasm`
- [x] `:sample:android:assembleDebug :sample:android:assembleRelease` passes

### Docs
- [x] `docs2/database.md` SQLDelight tab, `docs2/setup.md` row, README snippet
- [x] Setup skill references `android.md` and `kmp.md`
- [x] Confirm `protocol/` and MCP docs are unchanged

### Desktop (only if needed)
- [ ] Verify the Database screen with explicit + scanned registrations; optional provider label

## 10. Suggested PR split

1. **Core spike + skeleton**: catalog entries, both modules compiling, model + `register` + `executeQuery`, tests, no logging. Resolves risk 1.
2. **Logging**: `FloconLoggingSqlDriver`, `withFlocon`/`floconLogs`, async handling, tests. Logs sql + args as today.
3. **Profiler fields**: `durationMs`, `threadName`, `isMainThread`, `rowCount`, `transactionId`, after `internal-docs/plans/database-profiler.md` lands its protocol/SDK changes.
4. **Samples + docs + skill**: all samples, `docs2`, README, setup skill (can be folded into PR 2 if small).
