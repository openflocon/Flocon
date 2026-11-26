# Database Profiler — design plan

Status: proposal, no code yet. Parts touched later: SDK (`FloconAndroid/database/*`), Protocol, Desktop (`FloconDesktop/data`, `domain`, `composeApp`), MCP (`FloconDesktop/mcp`), docs, FloconSwift (separate repo).

## 1. Summary & motivation

Flocon already shows every SQL statement an app runs (the `log_query` method of the `database` plugin), but only *what* ran and *when*. Developers want to know *what is slow* and *what is wasteful*:

- sort **queries and transactions by execution duration**;
- find the **longest** queries (max), the **most costly** (total cumulative time) and the **most frequent** (call count), with avg / p95 / max, aggregated by **normalized query** (SQL without bind values);
- spot the classic problems: DB access on the **main thread**, **N+1** patterns, **long transactions**, **full table scans** (`EXPLAIN QUERY PLAN`);
- see the DB cost **per screen** and in the **timeline**, and ask the same questions through **MCP**.

The profiler is an extension of the existing query logs, not a new plugin: same plugin name (`database`), same `log_query` method with new **optional** fields, plus one new optional method for transactions. Old SDKs keep working (the desktop shows "—" for missing metrics); old desktops ignore the new fields.

Measured durations are meaningful even in debug builds: SQLite is native code, so the debuggable flag / interpreted ART barely changes the time spent inside SQLite. The device matters much more (an emulator on a fast laptop SSD is far quicker than a low-end phone with slow flash), and the first run of a query pays for statement compilation and a cold page cache. The UI and docs must say so ("profile on a real, low-end device; compare relative numbers").

## 2. Current state (verified)

### SDK (`FloconAndroid/`)

| What | Where | Notes |
|---|---|---|
| Plugin API `logQuery(dbId, sqlQuery, bindArgs)` | `database/core/core/src/commonMain/kotlin/io/github/openflocon/flocon/database/core/FloconDatabasePlugin.kt` | Also a `NoOpDatabasePlugin` used when the plugin is not initialized. |
| Implementation | `database/core/core/src/commonMain/.../FloconDatabasePluginImpl.kt` | `logQuery` does `scope.launch { … timestamp = currentTimeMillis() … }`: **the timestamp is taken inside the coroutine**, i.e. when the message is sent, not when the query ran. One coroutine + one websocket message per query, no batching. |
| Wire model | `database/core/core/src/commonMain/.../model/fromdevice/sql/DatabaseQueryLogModel.kt` | `dbId`, `sqlQuery`, `bindArgs: List<String>?`, `timestamp`. |
| No-op core | `database/core/core-no-op/src/commonMain/.../FloconDatabasePlugin.kt` | `FloconDatabasePluginNoOp.logQuery = Unit`; same interface signature. |
| **Room 2** hook (`androidx.room` 2.8.5, `flocon-database-room`) | `database/room/room/src/androidMain/.../room/extensions/RoomBuilderExt.kt` | `RoomDatabase.Builder.floconLogs(id, executor, queryCallback)` = `setQueryCallback`. The callback runs **before** execution, **on its own executor** → no duration, no calling thread, no row count. Android only (no hook on the iOS / JVM targets of this module). |
| Room 2 no-op | `database/room/room-no-op/src/androidMain/.../RoomBuilderExt.kt` | Keeps the app's own `queryCallback` only. |
| Room 2 browse | `database/room/room/src/commonMain/.../FloconRoomDatabaseModel.kt` | `executeQuery` via `useReaderConnection`; `SELECT`/`PRAGMA`/`EXPLAIN` go through `executeSelect` → `EXPLAIN QUERY PLAN` from the desktop already works. |
| **Room 3** hook (`androidx.room3` 3.0.3, `flocon-database-room3`) | `database/room3/room3/src/nonWebMain/.../FloconLoggingSQLiteDriver.nonWeb.kt` and `src/wasmJsMain/.../FloconLoggingSQLiteDriver.wasmJs.kt` (suspend API) | `SQLiteDriver.floconLogs(id)` wraps `SQLiteDriver` → `SQLiteConnection.prepare` → `SQLiteStatement`; logs on the first `step()` (and the first after `reset()`), captures bind args. Already the right interception point; it just doesn't time anything. Testable through the `QueryLogger` fun interface (`src/roomMain/.../QueryLogger.kt`), test in `src/jvmTest/.../FloconLoggingSQLiteDriverTest.kt`. |
| Room 3 no-op | `database/room3/room3-no-op/src/commonMain/.../FloconLoggingSQLiteDriver.kt` | `floconLogs(id) = this`. |
| SQLDelight | — | No SQLDelight module in the repo today (only mentioned in `docs2/database.md` and the MCP `list_databases` description). See §3.7. |
| Protocol constants | `FloconAndroid/flocon/flocon/src/commonMain/kotlin/io/github/openflocon/flocon/Protocol.kt` (`FromDevice.Database.Method.LogQuery = "log_query"`) and `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/Protocol.kt` | In sync today. |
| Samples | Room 2: `sample-multiplatform/src/androidMain/.../room2/CatDatabase.kt` (`.floconLogs(id = "cats_room2")`). Room 3: `sample-multiplatform/src/androidMain/.../Databases.kt`, `sample/desktop/.../Main.kt`, `sample/ios/.../Databases.kt`, `sample/wasm/.../Databases.kt`. | |

### Desktop (`FloconDesktop/`)

| What | Where | Notes |
|---|---|---|
| Wire model | `data/remote/src/commonMain/kotlin/com/flocon/data/remote/database/models/DatabaseQueryLogModel.kt` | Decoded with `ignoreUnknownKeys = true` (`data/remote/.../DI.kt`) → new fields are safe for old desktops. |
| Mapping | `data/remote/.../database/datasource/QueryDatabaseRemoteDataSourceImpl.kt#getQueryLogs` | Computes `isTransaction` from the SQL text (`BEGIN TRANSACTION`, `COMMIT`, `END TRANSACTION`, `TRANSACTION SUCCESSFUL`, …). |
| Dispatch | `data/core/.../database/repository/DatabaseRepositoryImpl.kt` (`when(method)` with no `else`) | Unknown methods are ignored → a new `log_transaction` method is safe for old desktops. One Room insert per message. |
| Entity | `data/local/.../database/models/DatabaseQueryLogEntity.kt` | `id, dbId, dbName, sqlQuery, bindArgs, timestamp, isTransaction, deviceId, packageName, appInstance`; index `(deviceId, packageName, dbId, timestamp)`. |
| DAO | `data/local/.../database/dao/DatabaseQueryLogDao.kt` | Raw queries (paging, observe, count, get), `deleteAll`. Query building in `data/local/.../database/datasource/LocalDatabaseDataSourceRoom.kt`. |
| Domain | `domain/.../database/models/DatabaseQueryLogDomainModel.kt`, use cases `Observe/Get/Count/ClearDatabaseQueryLogsUseCase` | |
| Room DB | `composeApp/.../common/db/AppDatabase.kt` (`version = 92`, schemas under `composeApp/schemas/`) | |
| Retention | `composeApp/.../common/db/DatabaseMaintenanceRepositoryImpl.kt` + `data/local/.../maintenance/dao/DatabaseMaintenanceDao.kt#purgeDatabaseQueryLogs(before)` | Time-based auto purge + `VACUUM`. |
| UI | `composeApp/.../features/database/` (`DatabaseQueryLogsViewModel`, `view/logs/DatabaseQueryLogs.kt`, `DatabaseQueryLogItem.kt`, panel `DatabaseQueryLogPanel.kt`, tabs: `model/DatabaseTabState.isQueryLogs`), CSV / Markdown exports in `processor/` | Query logs are a tab of the database screen. |
| Timeline | `data/local/.../timeline/datasource/TimelineQueryBuilder.kt` | DB logs are already a timeline source (`DatabaseQueryLogEntity`, facets `DatabaseName` / `DatabaseStatement`, capped by `TIMELINE_MAX_DATABASE_LOGS = 20_000`). The union already has a `durationMs` column, `NULL` for non-network rows (`NO_NETWORK_COLUMNS`). Click → `DatabaseRoutes.QueryLogPanel(fromTimeline = true)` (`features/timeline/mapper/Destination.kt`). Navigation events live in `TimelineLogEntity` (`type = navigation`). |
| MCP | `mcp/src/desktopMain/.../mcp/database/DatabaseMcpToolProvider.kt` | Tools `list_databases`, `list_database_tables`, `query_database` (read only: SELECT / PRAGMA / EXPLAIN), `list_database_query_logs`. Tests in `mcp/src/desktopTest/.../database/`. |

### FloconSwift (separate repo `openflocon/FloconSwift`, read at commit `2e822e1`)

- `Sources/Flocon/Plugins/Database/FloconDatabasePlugin.swift`: `logQuery(dbId:sqlQuery:bindArgs:)` and `logQuery(database: URL, …)`, manual only; sends `log_query` with `DatabaseQueryLogRemote` (`Model/DatabaseRemote.swift`: `dbId`, `sqlQuery`, `bindArgs`, `timestamp`). The timestamp is taken at call time (`Date()`), the encoding is done off the calling thread.
- Databases are discovered on disk (`FloconDatabaseDiscovery.swift`) or registered by URL; the `dbId` is the absolute path. Desktop queries run through raw `SQLite3` (`FloconSQLite.swift`, `sqlite3_open_v2`), so `EXPLAIN QUERY PLAN` already works there too.
- No ORM hook (no GRDB / Core Data / SQLite.swift integration). No-op = the same plugin with a dropping sender (`Core/FloconNoOpPlugins.swift`).

## 3. Technical design

### 3.1 What we measure (per statement)

| Metric | How |
|---|---|
| `startTimestamp` | wall clock (epoch ms) when the statement **starts** (first `step` / `query` call), captured on the calling thread. Fixes the current "timestamp = send time" drift. |
| `durationMs` | monotonic clock (`System.nanoTime` / `TimeSource.Monotonic`), as a `Double` with µs precision. Statement time = from the first `step()` to the step that returns `false`, or to `reset()` / `close()` (whatever comes first). Includes the time the app spends between steps when it iterates lazily — documented; Room reads all rows eagerly, so in practice it is the SQLite time. |
| `rowCount` | number of `step()` returning `true` (SELECT). For writes: `changes()` is not exposed by `SQLiteStatement`; leave `null` (or, opt-in, run `SELECT changes()` — rejected by default, it doubles statement count). |
| `threadName`, `isMainThread` | captured on the thread calling `step()` / `query()`. `isMainThread` via an `expect fun isMainThread()` in core (Android `Looper.getMainLooper().isCurrentThread`, iOS `NSThread.isMainThread`, JVM: thread name `main` or `AWT-EventQueue*`, wasm: always `true` → sent as `null`, meaningless there). |
| `transactionId` | id of the enclosing transaction on this connection, `null` outside one. |
| `error` | SQLite error message if the statement threw (statement still logged, with duration). |
| `callSite` (opt-in) | short stack: first N (default 3) frames outside `androidx.room*`, `androidx.sqlite*`, `io.github.openflocon*`, `kotlinx.coroutines*` → typically the DAO impl + caller. Only on JVM/Android (`Throwable().stackTrace`); `null` on iOS / wasm. Off by default (cost, see §5). With Room's suspend DAOs the stack is the dispatcher thread's, so it often shows only the generated `_Impl`: still useful to identify the DAO. |

### 3.2 SDK — Room 3 (`database/room3/room3`)

Hook: keep the existing `SQLiteDriver.floconLogs(id)` (`FloconLoggingSQLiteDriver`), extend it. No new public API needed for the basic profile.

- `FloconLoggingStatement`: record `startNanos` and wall clock on the first `step()`, count `true` steps, stop the clock when `step()` returns `false` or throws, or on `reset()` / `close()`; emit **one** log **after** the statement finished (today it logs before). If the statement is never stepped (prepared then closed), don't log (unchanged behavior).
- `FloconLoggingConnection`: per-connection transaction state. Room 3 opens transactions by executing SQL on the connection (`BEGIN IMMEDIATE/DEFERRED/EXCLUSIVE TRANSACTION`, `SAVEPOINT`, `END TRANSACTION` / `COMMIT`, `ROLLBACK TRANSACTION`, `RELEASE SAVEPOINT`, `ROLLBACK TO`). Detect them in `prepare(sql)` (cheap prefix match, uppercase first token), assign a `transactionId` (`"$dbId-$connectionCounter-$txCounter"`) on the outermost `BEGIN`, track nesting depth for savepoints, and on the outermost end emit a `log_transaction` (start, duration, status, statement count, total statement time, thread). `inTransaction()` on `SQLiteConnection` is a cross-check.
- Thread: Room 3 runs statements on its connection pool dispatcher, so `isMainThread` is mostly `false`; still record it (apps with `allowMainThreadQueries`, or blocking `runBlocking` usages).
- wasm variant (`FloconLoggingSQLiteDriver.wasmJs.kt`, suspend API): same timing logic; the web worker driver runs SQL in a worker, so the duration includes the worker round trip — document it, and set `isMainThread = null`.
- Share the timing/transaction logic in a common, non-suspend `StatementProfiler` / `ConnectionProfiler` (in `roomMain` or `commonMain`) used by both the blocking and suspend wrappers, so it is tested once.

### 3.3 SDK — Room 2 (`database/room/room`)

Room 2.8 has two runtime paths, and the hook differs:

1. **Android default path, `SupportSQLiteOpenHelper`** (what `CatDatabase` and most apps use). New public extension in `androidMain`:
   ```kotlin
   fun <T : RoomDatabase> RoomDatabase.Builder<T>.floconProfile(
       id: String,
       factory: SupportSQLiteOpenHelper.Factory = FrameworkSQLiteOpenHelperFactory(),
   ): RoomDatabase.Builder<T> = openHelperFactory(FloconProfilingOpenHelperFactory(id, factory))
   ```
   Wrappers: `Factory` → `SupportSQLiteOpenHelper` → `SupportSQLiteDatabase` → `SupportSQLiteStatement`, plus a `Cursor` wrapper.
   - `query(SupportSQLiteQuery)` / `query(String, …)`: `SQLiteCursor` is **lazy** (the first window fills on `getCount()` / first `moveTo*`), so timing only `query()` would measure compilation only. Return a `CursorWrapper` that measures from `query()` to `close()` and counts rows by tracking the max position reached (no extra `getCount()`). Log on `close()` (Room always closes its cursors).
   - `execSQL`, `SupportSQLiteStatement.execute / executeInsert / executeUpdateDelete / simpleQueryFor*`: time the call; `executeUpdateDelete` gives `rowCount` (affected rows) for free.
   - Bind args: capture via `bindString/Long/Double/Blob/Null` on the statement wrapper and via `SupportSQLiteQuery.bindTo` with a recording `SupportSQLiteProgram`.
   - Transactions: `beginTransaction*` / `setTransactionSuccessful` / `endTransaction` are explicit methods on `SupportSQLiteDatabase` → exact begin/end, status (committed if `setTransactionSuccessful` was called), nesting depth. Thread-local transaction id (framework SQLite transactions are bound to the thread holding the connection).
   - Should also emit the `BEGIN`/`COMMIT` pseudo-statements the `QueryCallback` used to send? No: transactions become `log_transaction` events; the logs tab keeps showing them as rows by mapping the transaction to begin/end rows on the desktop (see §3.5), so we don't double-count.
   - Must not be combined with `floconLogs` (would log twice). `floconProfile` **replaces** `floconLogs` (it logs everything `floconLogs` did, plus metrics). Keep `floconLogs` as is, deprecated with a `ReplaceWith`, for apps that already use their own `openHelperFactory` (e.g. SQLCipher's `SupportOpenHelperFactory`) and don't want to wrap it — though wrapping works since `floconProfile(factory = …)` takes the delegate.
   - Room requires `openHelperFactory` **xor** `setDriver`. If the app called `setDriver`, see 2.
2. **Room 2 with `setDriver(...)`** (KMP targets iOS / JVM, or Android apps opting into the driver API): exactly the Room 3 driver wrapper; the `androidx.sqlite.SQLiteDriver` interface is the same artifact (`androidx.sqlite` 2.7.1 for both). Today `room` has no driver hook at all, so iOS/JVM Room 2 apps can't log queries. Proposal: move the driver wrapper + profiler to a new small shared module `database/sqlite` (`flocon-database-sqlite`, depends on `androidx.sqlite` + `database/core/core`) with its no-op twin, and make both `room` and `room3` depend on it (re-exporting `floconLogs` for source compatibility in room3). **Open question** (§5): new artifact vs duplicating the wrapper in `room`.

Metrics availability:

| Metric | Room 2 / OpenHelper (Android) | Room 2 / driver | Room 3 / driver | Manual `logQuery` |
|---|---|---|---|---|
| duration | yes (incl. cursor fill) | yes | yes | if the app passes it |
| row count (SELECT) | yes (cursor) | yes (steps) | yes (steps) | optional |
| affected rows (writes) | yes (`executeUpdateDelete`) | no | no | optional |
| thread / main thread | yes | yes | yes (mostly pool threads) | optional |
| transactions | exact (API calls) | parsed SQL | parsed SQL | via `logTransaction` |
| bind args | yes | yes | yes | yes |
| call site | opt-in | opt-in (JVM/Android) | opt-in (JVM/Android) | — |

### 3.4 SDK — core API, config, sampling, no-op

`FloconDatabasePlugin` (core + core-no-op, same signatures):

```kotlin
fun logQuery(dbId: String, sqlQuery: String, bindArgs: List<Any?>) // unchanged

fun logQuery(
    dbId: String,
    sqlQuery: String,
    bindArgs: List<Any?>,
    profile: FloconQueryProfile, // new, data class in core (and core-no-op)
)

fun logTransaction(dbId: String, transaction: FloconTransactionProfile)
```

`FloconQueryProfile(startTimestamp, durationMs, rowCount?, threadName?, isMainThread?, transactionId?, error?, callSite?)`; `FloconTransactionProfile(transactionId, startTimestamp, durationMs, status, statementCount, statementsDurationMs, threadName?, isMainThread?)`. The default `logQuery` overload keeps `timestamp = now` but captured **before** `scope.launch` (bug fix).

Config (`FloconDatabaseConfig`, `database/core/core/.../FloconDatabaseConfig.kt`), applied in the wrappers, read once at start:

```kotlin
floconDatabase {
    profiling {
        enabled = true                 // default true: timing is cheap
        captureCallSite = false        // default false: stack walk per statement
        callSiteDepth = 3
        sampleRate = 1.0               // 0..1, per statement; transactions always sent
        slowQueryThresholdMs = 16.0    // statements faster than this AND sampled-out are dropped; slower ones always sent
        maxLogsPerSecond = 2_000       // safety valve, excess counted and reported as a "dropped" counter
    }
}
```

Also an SDK-side **batching** of logs (see §3.5 protocol `log_queries`) to avoid one coroutine + one websocket frame per statement: buffer in a lock-free queue, flush every 250 ms or 200 items.

No-op parity: `core-no-op` gets the same new types/overloads (empty bodies), `room-no-op` gets `floconProfile(id, factory)` that just calls `openHelperFactory(factory)` only if a non-default factory was passed (otherwise returns `this`), `room3-no-op` already returns `this`; the new `sqlite-no-op` (if created) returns `this`. Config DSL `profiling {}` exists in no-op as an accepting no-op. Release builds (no-op) must add zero wrapping.

### 3.5 Protocol

All new fields optional with defaults; both `Protocol.kt` updated together.

#### `log_query` — `QueryLog`, new optional fields

| Key | Type | Default | Description |
|---|---|---|---|
| `durationMs` | `Double?` | `null` | Statement execution time (monotonic), µs precision |
| `rowCount` | `Long?` | `null` | Rows returned (SELECT) or affected (writes, when known) |
| `threadName` | `String?` | `null` | Thread that ran the statement |
| `isMainThread` | `Boolean?` | `null` | `null` when unknown / not applicable (wasm) |
| `transactionId` | `String?` | `null` | Enclosing transaction, see `log_transaction` |
| `error` | `String?` | `null` | Error message if the statement failed |
| `callSite` | `List<String>?` | `null` | Opt-in, top frames outside framework code |

`timestamp` keeps its meaning (epoch ms) but is now the **start** of the statement.

```json
{
  "dbId": "dogs",
  "sqlQuery": "SELECT * FROM dog WHERE ownerId = ?",
  "bindArgs": ["42"],
  "timestamp": 1729260000000,
  "durationMs": 3.412,
  "rowCount": 12,
  "threadName": "arch_disk_io_1",
  "isMainThread": false,
  "transactionId": "dogs-1-17"
}
```

#### New `log_transaction` (Mobile → Desktop) — `TransactionLog`

| Key | Type | Description |
|---|---|---|
| `dbId` | `String` | Database id |
| `transactionId` | `String` | Unique per app launch |
| `timestamp` | `Long` | Begin (epoch ms) |
| `durationMs` | `Double` | Begin → commit/rollback |
| `status` | `String` | `committed`, `rolledBack` (unknown value → `unknown`) |
| `statementCount` | `Int` | Statements run inside |
| `statementsDurationMs` | `Double?` | Sum of statement durations (the gap = time the app held the transaction doing other work) |
| `threadName` / `isMainThread` | `String?` / `Boolean?` | Thread that began it |

```json
{ "dbId": "dogs", "transactionId": "dogs-1-17", "timestamp": 1729260000000, "durationMs": 48.2,
  "status": "committed", "statementCount": 120, "statementsDurationMs": 31.0,
  "threadName": "arch_disk_io_1", "isMainThread": false }
```

#### Optional `log_queries` (batch)

Body `List<QueryLog>`. Only sent by an SDK that knows the desktop supports it. **Decision needed** (§5): the desktop has no capability negotiation for plugins today, so a new SDK talking to an old desktop would lose everything sent as a batch. Options: (a) keep one `log_query` per statement and batch only on the desktop side (insert in a buffered transaction), (b) gate batching on the desktop version if the SDK learns it. Recommendation: (a) for v1.

#### Compatibility

- Old SDK → new desktop: fields absent → `null` → UI shows "—"; profile aggregations ignore rows with `durationMs IS NULL` for time metrics but still count them in "count".
- New SDK → old desktop: `ignoreUnknownKeys = true` on the desktop decoder; `log_transaction` ignored by the `when` without `else` in `DatabaseRepositoryImpl`.
- FloconSwift: same fields (see §3.9).

### 3.6 Desktop — data / domain

**Entity changes** (`DatabaseQueryLogEntity`, bump `AppDatabase.version` 92 → 93, commit `composeApp/schemas/.../93.json`; columns nullable → auto-migration):

- `durationMs REAL NULL`, `rowCount INTEGER NULL`, `threadName TEXT NULL`, `isMainThread INTEGER NULL`, `transactionId TEXT NULL`, `error TEXT NULL`, `callSite TEXT NULL` (json).
- `normalizedSql TEXT NOT NULL DEFAULT ''` and `queryHash INTEGER NOT NULL DEFAULT 0` (64-bit hash of `normalizedSql`), computed **on the desktop at insert** (one place, works for every SDK incl. Swift and old SDKs). Existing rows: normalized lazily? No: the migration leaves `''`; aggregation uses `CASE WHEN queryHash = 0 THEN …` → simpler: the migration can't run Kotlin, so old rows are excluded from the profile (documented; logs are purged anyway).
- New index for aggregation: `(deviceId, packageName, dbId, appInstance, queryHash)`; keep the existing time index.

**New entity** `DatabaseTransactionLogEntity` (`id, dbId, dbName, transactionId, timestamp, durationMs, status, statementCount, statementsDurationMs, threadName, isMainThread, deviceId, packageName, appInstance`), index `(deviceId, packageName, dbId, appInstance, timestamp)`. Purged with the query logs (`DatabaseMaintenanceDao`, `deleteAll`).

**Normalization** (`domain/.../database/utils/SqlNormalizer.kt`, pure Kotlin, unit-tested, reuse the tokenizer approach of the existing `SqlScanner` in MCP if it can move to `domain`): see §4.

**Aggregation queries** (DAO `DatabaseProfileDao`, raw query built in `data/local`):

```sql
SELECT queryHash, MIN(normalizedSql) AS sql, COUNT(*) AS count,
       SUM(durationMs) AS totalMs, AVG(durationMs) AS avgMs, MAX(durationMs) AS maxMs,
       SUM(CASE WHEN isMainThread = 1 THEN 1 ELSE 0 END) AS mainThreadCount,
       SUM(rowCount) AS totalRows, MIN(timestamp) AS firstSeen, MAX(timestamp) AS lastSeen
FROM DatabaseQueryLogEntity
WHERE deviceId = ? AND packageName = ? AND dbId = ? AND appInstance = ? AND isTransaction = 0
  [AND timestamp BETWEEN ? AND ?]
GROUP BY queryHash
ORDER BY <totalMs|avgMs|p95Ms|maxMs|count> DESC
LIMIT ?
```

p95 per group with a window function (desktop SQLite supports them):

```sql
WITH ranked AS (
  SELECT queryHash, durationMs,
         ROW_NUMBER() OVER (PARTITION BY queryHash ORDER BY durationMs) AS rn,
         COUNT(*)     OVER (PARTITION BY queryHash) AS n
  FROM DatabaseQueryLogEntity WHERE <same filter> AND durationMs IS NOT NULL)
SELECT queryHash, MIN(durationMs) AS p95Ms FROM ranked WHERE rn >= CEIL(0.95 * n) GROUP BY queryHash
```

(`CEIL` may not exist in the bundled SQLite → use `(95 * n + 99) / 100` in integer math.) Joined in the repository or as one CTE. Sorting by p95 is done after the join. The profile is computed on demand (button / debounce 500 ms on new logs while the tab is visible), **not** observed as a live Flow on every insert (millions of rows).

Domain models: `DatabaseQueryProfileDomainModel` (queryHash, normalizedSql, sampleSql, count, totalMs, avgMs, p95Ms, maxMs, mainThreadCount, totalRows), `DatabaseTransactionDomainModel`, `DatabaseProfileSort { TOTAL, AVG, P95, MAX, COUNT }`, `DatabaseProfileFilter(dbId, appInstance?, timeRange?, mainThreadOnly, minDurationMs?)`, `NPlusOneFindingDomainModel`. Use cases: `GetDatabaseQueryProfileUseCase`, `GetDatabaseTransactionsUseCase`, `GetSlowestQueryLogsUseCase` (raw rows sorted by `durationMs DESC`), `DetectNPlusOneUseCase`, `ExplainQueryPlanUseCase`.

**Ingestion perf**: today one `insert` per message. Add a small buffered writer in `DatabaseRepositoryImpl` (channel, flush every 200 ms / 500 rows inside one `useWriterConnection` transaction). Cap per device/app (like the timeline's 20 000 rows): keep a configurable max (default 200 000 query logs per device+app, oldest dropped by a periodic `DELETE … WHERE id < (SELECT id … LIMIT 1 OFFSET max)`) on top of the existing time-based auto purge.

**Remote**: extend `DatabaseQueryLogModel` (data/remote) with the optional fields; new `DatabaseTransactionLogModel`; `QueryDatabaseRemoteDataSource.getTransactionLog(message)`; dispatch `Protocol.FromDevice.Database.Method.LogTransaction` in `DatabaseRepositoryImpl`. `isTransaction` detection stays for old SDKs (they still send `BEGIN TRANSACTION` rows from the Room callback).

### 3.7 SQLDelight (out of scope here)

A separate plan covers a new `flocon-database-sqldelight` module. The profiler's hook for SQLDelight is a wrapping `SqlDriver` (`executeQuery` / `execute` / `newTransaction`) that will live **in that module** and must emit exactly the same `log_query` profile fields and `log_transaction` events defined in §3.5, through the same `FloconDatabasePlugin.logQuery(…, profile)` / `logTransaction` API of §3.4. Not designed here.

### 3.8 Desktop — UI (`composeApp/.../features/database/`)

Following `compose.md` / `desktop-app.md` (stateful + stateless overloads, `@Immutable *UiModel` in `model/`, `ImmutableList`, `FloconTheme` tokens, `stringResource`, previews with `preview<UiModel>()`).

- **Query logs tab** (existing `DatabaseQueryLogs`): add `Duration`, `Rows`, `Thread` columns ("—" when null), a main-thread badge (warning color), a duration color scale (< 16 ms neutral, 16–100 warning, > 100 error; thresholds in settings later), sort by duration toggle (currently time-ordered), filter "slower than X ms" and "main thread only". Panel `DatabaseQueryLogPanel` shows all fields + call site + the transaction it belongs to + "Explain" button.
- **New "Profile" tab** per database (new `DatabaseTabState` flag `isProfile`, or a sub-tab of the logs tab): table of normalized queries with columns count, total, avg, p95, max, rows, main-thread count; sort chips **Total time (default) / Avg / p95 / Max / Count**; scope selector: current session / all sessions / time range / current screen (§3.10). Row → panel with distribution (min/avg/p95/max, small histogram), the slowest N executions (link to log panel), sample SQL with args, call sites seen, "Explain query plan".
- **Transactions view** (section in the Profile tab): list sorted by duration desc, columns duration, statements, statement time vs held time, status, thread; click → the statements of the transaction (filter logs by `transactionId`). Highlight "long transactions" (> 100 ms, or held time >> statement time).
- **Findings strip** at the top of Profile: "N queries on main thread", "N+1 suspects", "full scans", "long transactions" — each a filter shortcut.
- **EXPLAIN QUERY PLAN**: button sends `EXPLAIN QUERY PLAN <sample SQL with bind args injected>` through the existing `query` method (both Room models already route `EXPLAIN` to `executeSelect`; FloconSwift too). Parse the `detail` column: `SCAN <table>` (without `USING … INDEX`) → red "full scan"; `SEARCH … USING (COVERING) INDEX` → green; `USE TEMP B-TREE` → warning. Results cached per queryHash for the session. Only offered for statements whose first token is `SELECT`/`WITH`/`UPDATE`/`DELETE`/`INSERT … SELECT`; arguments are injected with the existing `injectSqlArgs` (`domain/.../database/utils`).
- Export: extend existing CSV / Markdown processors with the new columns; add "Export profile" (CSV).

New ViewModel `DatabaseProfileViewModel`, mapper `mapper/DatabaseProfileUiMapper.kt`, views `view/profile/DatabaseProfile.kt`, `DatabaseProfileRow.kt`, `DatabaseTransactionRow.kt`, panel route `DatabaseRoutes.QueryProfilePanel(dbId, queryHash)` and `DatabaseRoutes.TransactionPanel(id)` in `Navigation.kt`.

### 3.9 FloconSwift (separate repo)

- Extend `DatabaseQueryLogRemote` with the same optional fields (`durationMs`, `rowCount`, `threadName`, `isMainThread`, `transactionId`, `error`) — Swift `Encodable` omits `nil` optionals, which matches the "absent = null" contract.
- API: `logQuery(dbId:sqlQuery:bindArgs:durationMs:rowCount:transactionId:)` with defaults `nil`; `threadName` / `isMainThread` captured automatically at the call (`Thread.isMainThread`, `Thread.current.name` / queue label via `__dispatch_queue_get_label(nil)`), timestamp at the call (already the case). Plus `logTransaction(...)`.
- Automatic profiling for apps that own a raw `sqlite3*` handle (and GRDB, which exposes it): `Flocon.databasePlugin.profile(connection: OpaquePointer, dbId:)` installing `sqlite3_trace_v2` with `SQLITE_TRACE_PROFILE` (gives the statement and its duration in ns, called on the executing thread → valid `isMainThread`) and `SQLITE_TRACE_ROW` (row count). Transactions: detect `BEGIN`/`COMMIT`/`ROLLBACK`/`SAVEPOINT` in the traced SQL, per connection. Docs snippet for GRDB (`db.trace(options: .profile)` forwarding to `logQuery`). Core Data / SwiftData: not hookable (private connection); docs mention `-com.apple.CoreData.SQLDebug` only.
- Caveat: `sqlite3_trace_v2` replaces any previous trace callback on that connection; document it.
- No-op: nothing to add (the no-op plugin is the same class with a dropping sender), but `profile(connection:)` must not install the trace callback when Flocon is not started.

### 3.10 Timeline & per-screen breakdown

- `TimelineQueryBuilder`: the DB source fills `durationMs` from `DatabaseQueryLogEntity.durationMs` instead of `NULL` (today via `NO_NETWORK_COLUMNS`) → the timeline row shows the duration like network rows. Add a "main thread" facet and maybe a "slow (> X ms)" facet. Transactions as a timeline source (`DatabaseTransactionLogEntity`, kind `database_transaction`) with their duration → click opens `TransactionPanel`.
- Per-screen breakdown: the screens are the `navigation` events of `TimelineLogEntity` for the same device/app/appInstance. Compute screen windows `[navigation_i.timestamp, navigation_{i+1}.timestamp)` (SQL `LEAD()` window function) and join DB logs on `timestamp` into the windows: per screen = count, total ms, main-thread count, top 3 queries. New use case `GetDatabaseProfileByScreenUseCase`; shown in the Profile tab scope selector ("By screen") and reusable by MCP. Screens without navigation logs → one "unknown" bucket. Limitation: navigation is only known when the app logs it (`logNavigation`), documented.

### 3.11 MCP (`FloconDesktop/mcp/.../database/`)

- New tool **`get_database_profile`**: inputs `databaseId`, `sortBy` (`total` default | `avg` | `p95` | `max` | `count`), `limit` (default 20, max 100), `includeOldSessions` (default false), `sinceMs` / `untilMs`, `mainThreadOnly`, `minAvgMs`, `screen` (optional, from the timeline). Output per normalized query: `normalizedSql`, `sampleSql`, `count`, `totalMs`, `avgMs`, `p95Ms`, `maxMs`, `rowsTotal`, `mainThreadCount`, plus `findings` (N+1 suspects, main-thread queries, long transactions) and `profiledShare` (share of rows with a duration — tells the agent when the SDK is too old).
- New tool **`list_database_transactions`**: sorted by duration, `limit`, filters as above.
- New tool **`explain_database_query`**: runs `EXPLAIN QUERY PLAN` through the existing read-only path (`query_database` already accepts `EXPLAIN`), returns the plan rows and a `fullScans: [table]` summary. Could also be just documented as `query_database` usage; a dedicated tool gives the parsed verdict. Decide in implementation.
- `list_database_query_logs`: add `durationMs`, `rowCount`, `isMainThread`, `transactionId` to `DatabaseQueryLogMcpModel` and a `sortBy = time | duration` input.
- Tests in `mcp/src/desktopTest/.../database/`: `DatabaseProfileMcpToolProviderTest` (sorting by each key, p95 correctness on known data, old-SDK rows without duration, session filter, limit clamping), transactions tool test, explain parsing test.

### 3.12 Samples

- `sample-multiplatform` Room 2 `CatDatabase.kt`: switch `.floconLogs(id = "cats_room2")` to `.floconProfile(id = "cats_room2")`.
- Room 3 samples (Android, desktop, iOS, wasm): nothing to change in setup (`floconLogs` gains the metrics).
- Add a "Database profiler" demo screen in the Android sample: deliberate N+1 (loop of `getDogById`), a main-thread query (`allowMainThreadQueries` on a dedicated DB), a long transaction (bulk insert with `delay`-free busy work), a full scan on an unindexed column vs an indexed one.
- FloconSwift `FloconDemo/SampleDatabase.swift`: use `profile(connection:)`.

## 4. Metrics definitions

- **Normalized query**: the SQL with every literal replaced by `?`, computed on the desktop: strip comments, collapse whitespace, uppercase keywords (not identifiers), replace string literals `'…'`, numeric literals, `X'…'` blobs and `?NNN` / `:name` / `@name` / `$name` parameters by `?`, collapse `IN (?, ?, ?)` lists to `IN (?)` and multi-row `VALUES (?,?),(?,?)` to `VALUES (?)`. Room already uses `?` placeholders, so most queries are unchanged; the rules matter for manual `logQuery` and string-built SQL. `queryHash` = 64-bit FNV-1a of the normalized string.
- **Count**: number of executions (rows with `isTransaction = 0`) in scope, including rows without duration.
- **Total time ("cost")**: `SUM(durationMs)` over rows with a duration. The default sort: what to optimize first.
- **Avg**: `totalMs / countWithDuration`.
- **p95**: nearest-rank: sort durations ascending, take the value at rank `ceil(0.95 × n)` (1-based). For n < 20 it equals the max; shown with a "low sample" hint when n < 20.
- **Max**: `MAX(durationMs)`. "Longest" = sort by max.
- **Most frequent**: sort by count.
- **Main-thread access**: `isMainThread = 1`. Any count > 0 is a finding (Android: StrictMode-like warning; iOS: hang risk).
- **N+1 suspect**: within one app session, ≥ `N` (default 10) executions of the same `queryHash` whose consecutive start timestamps are each ≤ `W` ms apart (default 50 ms), and with **different** bind args (same args = a cache problem, reported separately as "repeated identical query"). Computed with `LAG(timestamp) OVER (PARTITION BY queryHash ORDER BY timestamp)` to build bursts. Reported with burst size, total burst time and the query run just before the burst (likely the "1").
- **Long transaction**: `durationMs > 100` (configurable) or on the main thread at all. "Held time" = `durationMs − statementsDurationMs` (time spent outside SQLite while holding the write lock).
- **Full scan**: an `EXPLAIN QUERY PLAN` `detail` starting with `SCAN` and not containing `USING INDEX` / `USING COVERING INDEX` / `USING INTEGER PRIMARY KEY`; small tables (row count < 1 000 via `SELECT count(*)` on demand) downgraded to info.

## 5. Open questions / trade-offs / risks

1. **Overhead of wrapping**: per statement ≈ 2 `nanoTime` calls, a few allocations (profile object, bind arg strings — already done today) and one enqueue. Negligible vs SQLite I/O (µs range vs ms). Must be measured on the sample (benchmark 10 000 inserts with/without wrapper) before release; target < 5 % overhead.
2. **Call-site capture cost**: `Throwable().stackTrace` costs 10–50 µs on Android; with sampling at 1.0 and thousands of queries it's noticeable. Default off; consider capturing only for statements over the slow threshold (requires capturing at start → can't know yet; alternative: capture lazily only for the first K executions of each new `queryHash`). Decision needed.
3. **Room 2: new `floconProfile` vs changing `floconLogs`**: changing `floconLogs` to `openHelperFactory` silently replaces an app's own factory (SQLCipher) and conflicts with `setDriver`. Proposal: new function, deprecate `floconLogs` later. Confirm naming.
4. **Room 2 + driver / shared module**: create `flocon-database-sqlite` (new artifact to publish, add to BOM) or duplicate the driver wrapper in `room`? Shared module preferred; needs an `architect` review (layering change).
5. **Room 3 API stability**: `androidx.room3` 3.0.3 / `androidx.sqlite` 2.7.1 driver API is stable but the wasm variant is suspend; transaction detection relies on the SQL Room emits (`BEGIN IMMEDIATE TRANSACTION`, `SAVEPOINT`, …), which could change across Room versions → tests must pin the observed SQL and `inTransaction()` is the fallback.
6. **Lazy iteration semantics**: in driver mode the duration spans first step → last step; an app iterating slowly inflates it. Room DAOs read eagerly so it is fine for generated code; manual raw usage may be misleading. Document.
7. **Batching on the wire** (`log_queries`) needs capability negotiation that doesn't exist; v1 keeps one message per statement + desktop-side buffered inserts.
8. **Desktop storage volume**: profiling encourages leaving logs on; 1M rows ≈ 200–300 MB with SQL text. Options: per device/app row cap (proposed), or store `normalizedSql` once in a `DatabaseQueryShapeEntity` table and reference it by `queryHash` (smaller, faster GROUP BY). Start with the cap; revisit if the GROUP BY is too slow at 1M rows (benchmark).
9. **p95 in SQL** with window functions over large partitions: fine up to ~1M rows; otherwise compute in Kotlin on the top-N groups only.
10. **Old rows** have no `normalizedSql` after migration → excluded from Profile (or backfilled in a background job). Proposal: exclude, they're purged soon anyway.
11. **Clock**: `timestamp` (epoch, device clock) vs timeline navigation events (also device clock, same SDK) → consistent per device; no desktop-clock correction needed.
12. **FloconSwift**: `sqlite3_trace_v2` is per connection and exclusive; GRDB users would rather use GRDB's `trace`. Which to ship first? Also confirm with the Swift maintainers that adding a `logTransaction` API is wanted.
13. **EXPLAIN on writes**: `EXPLAIN QUERY PLAN UPDATE …` is safe (doesn't execute) but the MCP read-only checker must accept it — verify `ReadOnlySql` allows `EXPLAIN QUERY PLAN` + write statement.

## 6. TODO checklist

Suggested PR split (each PR shippable on its own, backward compatible):

1. **PR 1 — Protocol + desktop ingestion/storage** (desktop accepts and stores the fields; nothing sends them yet). Protocol docs, both `Protocol.kt`, entity/migration, remote models, buffered writer, show new columns in logs tab.
2. **PR 2 — SDK core + Room 3 driver profiling** (+ no-op), samples unchanged.
3. **PR 3 — SDK Room 2** (`floconProfile` OpenHelper wrapper; shared sqlite module decision) + Room 2 sample.
4. **PR 4 — Desktop Profile tab** (aggregation, sorts, transactions view, findings, EXPLAIN).
5. **PR 5 — Timeline** (durations, transactions source, per-screen breakdown).
6. **PR 6 — MCP tools + tests** (can go right after PR 4).
7. **PR 7 — Docs pass** (if not done in each PR) + demo screen.
8. **FloconSwift PR** (separate repo), any time after PR 1.

### Protocol
- [ ] Add `LogTransaction = "log_transaction"` to `FromDevice.Database.Method` in `FloconAndroid/flocon/flocon/src/commonMain/kotlin/io/github/openflocon/flocon/Protocol.kt` **and** `FloconDesktop/domain/src/commonMain/kotlin/io/github/openflocon/domain/Protocol.kt`.
- [ ] Document new optional `QueryLog` fields, `timestamp` = statement start, `TransactionLog`, compatibility in `protocol/database.md` (method table + sections + JSON examples).
- [ ] Get the plan reviewed by `architect` (protocol change + possible new module).

### SDK — core (`database/core/core`)
- [ ] `FloconQueryProfile` / `FloconTransactionProfile` data classes.
- [ ] `logQuery(…, profile)` and `logTransaction(…)` on `FloconDatabasePlugin` + `NoOpDatabasePlugin` + `FloconDatabasePluginImpl`.
- [ ] Fix: capture `timestamp` before `scope.launch` in `logQuery`.
- [ ] Extend `DatabaseQueryLogModel` with optional fields (`encodeDefaults`/explicit nulls omitted); new `DatabaseTransactionLogModel`.
- [ ] `expect fun isMainThread(): Boolean?` + `currentThreadName()` (android, ios, jvm, wasm actuals).
- [ ] `profiling { }` DSL in `FloconDatabaseConfig` (enabled, captureCallSite, callSiteDepth, sampleRate, slowQueryThresholdMs, maxLogsPerSecond) and sampling/rate limiter.
- [ ] Call-site capture helper (JVM/Android), filtered frames.
- [ ] Tests in `database/core/core/src/jvmTest` (`FloconDatabasePluginTest`): payload with/without profile, timestamp at call time, sampling, rate limit.

### SDK — no-op (`database/core/core-no-op`, `room/room-no-op`, `room3/room3-no-op`)
- [ ] Same public types, overloads and `profiling {}` DSL in `core-no-op`.
- [ ] `floconProfile(id, factory)` in `room-no-op` (keeps a non-default factory, no wrapping).
- [ ] `room3-no-op`: nothing new unless the API changes; if a `sqlite` module is created, its `sqlite-no-op` twin.
- [ ] `./gradlew :sample:android:assembleDebug :sample:android:assembleRelease` (release = no-op).

### SDK — Room 3 (`database/room3/room3`)
- [ ] Shared `StatementProfiler` / `ConnectionProfiler` (timing, row count, transaction tracking with savepoint nesting).
- [ ] `FloconLoggingStatement` (nonWeb + wasm): log after completion with duration, rows, thread, error; keep "no step = no log".
- [ ] `FloconLoggingConnection`: transaction detection from prepared SQL, `log_transaction` on outermost end.
- [ ] Extend `QueryLogger` to carry the profile; update `DatabasePluginQueryLogger`.
- [ ] Tests in `FloconLoggingSQLiteDriverTest` (jvmTest, bundled driver): duration > 0, row count, error path, reset re-logs, nested savepoints, rollback status, Room-emitted transaction SQL.

### SDK — Room 2 (`database/room/room`)
- [ ] Decide: shared `database/sqlite` module (driver wrapper for Room 2 `setDriver` + Room 3) vs duplicate.
- [ ] `FloconProfilingOpenHelperFactory` + wrappers (`SupportSQLiteOpenHelper`, `SupportSQLiteDatabase`, `SupportSQLiteStatement`, `CursorWrapper`) in `androidMain`.
- [ ] Transactions from `beginTransaction*` / `setTransactionSuccessful` / `endTransaction` (thread-local id, nesting).
- [ ] Public `RoomDatabase.Builder.floconProfile(id, factory)`; KDoc on "replaces floconLogs, don't use both".
- [ ] (if shared module) `SQLiteDriver.floconLogs(id)` available for Room 2 on iOS/JVM.
- [ ] Tests: Robolectric or JVM test with a fake `SupportSQLiteOpenHelper` (cursor fill timing, row count, `executeUpdateDelete` count, transaction status).

### Desktop — data / domain
- [ ] Remote: extend `data/remote/.../database/models/DatabaseQueryLogModel.kt`; new `DatabaseTransactionLogModel`; `getTransactionLog` in `QueryDatabaseRemoteDataSource(Impl)`; tests in `QueryDatabaseRemoteDataSourceImplTest` (old payload → nulls).
- [ ] Domain: extend `DatabaseQueryLogDomainModel`; new profile/transaction/finding models, `DatabaseProfileSort`, filters; use cases `GetDatabaseQueryProfileUseCase`, `GetDatabaseTransactionsUseCase`, `GetSlowestQueryLogsUseCase`, `DetectNPlusOneUseCase`, `ExplainQueryPlanUseCase`, `GetDatabaseProfileByScreenUseCase`; register in `domain/.../database/DI.kt`.
- [ ] `SqlNormalizer` (+ hash) in domain with unit tests (literals, IN lists, VALUES, comments, params).
- [ ] Local: new columns on `DatabaseQueryLogEntity` + index; new `DatabaseTransactionLogEntity` + DAO; `DatabaseProfileDao` raw aggregation (sum/avg/max/count, p95 window query, N+1 bursts with `LAG`).
- [ ] `AppDatabase` version 92 → 93, auto-migration, commit `composeApp/schemas/.../93.json`.
- [ ] Repository: dispatch `LogTransaction`; buffered batch insert; per device/app row cap; purge transactions in `DatabaseMaintenanceDao` / `DatabaseMaintenanceRepositoryImpl` and in `deleteAllQueryLogs`.
- [ ] Tests in `data/local` / `data/core` (`allTests`): aggregation correctness, p95 nearest-rank, null durations, N+1 detection.

### Desktop — UI (`composeApp/.../features/database/`)
- [ ] Logs tab: duration / rows / thread columns, main-thread badge, duration color, sort by duration, "slower than" + "main thread only" filters; update `DatabaseQueryLogItem`, `DatabaseQueryLogsViewModel`.
- [ ] `DatabaseQueryLogPanel`: new fields, call site, transaction link, Explain button.
- [ ] Profile tab: `DatabaseProfileViewModel`, UI models + `preview*()` factories, sort chips (Total / Avg / p95 / Max / Count), scope selector (session / all / range / screen), findings strip.
- [ ] Transactions section + `TransactionPanel` route; `QueryProfilePanel` route in `Navigation.kt`.
- [ ] EXPLAIN QUERY PLAN result view with SCAN / SEARCH / TEMP B-TREE badges.
- [ ] Strings in `composeResources/values/strings.xml`; previews; `ktlintFormat`.
- [ ] Exports: new columns in CSV / Markdown processors; "Export profile".

### Timeline
- [ ] `TimelineQueryBuilder`: fill `durationMs` for the database source; main-thread facet; update `TimelineQueryBuilderTest` / `TimelineLogQueryTest`.
- [ ] Transactions as a timeline source + destination mapping in `features/timeline/mapper/Destination.kt` (+ `DestinationTest`).
- [ ] Per-screen windows from `navigation` events (`LEAD()`), shared with the Profile tab and MCP.

### MCP + tests (`FloconDesktop/mcp`)
- [ ] `get_database_profile` (sortBy, limit, session/time/screen filters, mainThreadOnly, findings, profiledShare).
- [ ] `list_database_transactions`.
- [ ] `explain_database_query` (or documented `query_database` usage) + plan parsing; verify `ReadOnlySql` accepts `EXPLAIN QUERY PLAN <write>`.
- [ ] `list_database_query_logs`: new fields + `sortBy = duration`.
- [ ] Tests in `mcp/src/desktopTest/.../database/` (sorts, p95, old-SDK rows, limits, explain parsing); `./gradlew :mcp:allTests`.

### Samples
- [ ] `sample-multiplatform` Room 2 `CatDatabase.kt` → `floconProfile`.
- [ ] Android sample "Database profiler" demo screen (N+1, main thread, long transaction, full scan vs index).
- [ ] Check Room 3 samples (Android, desktop, iOS, wasm) still log with metrics.

### Docs
- [ ] `protocol/database.md` (fields, `log_transaction`, compat).
- [ ] `docs2/database.md`: "Database profiler" section (setup per ORM: Room 2 `floconProfile`, Room 3 `floconLogs`, manual `logQuery` with profile, Swift), metrics definitions, device-vs-emulator note, config / sampling, screenshots.
- [ ] `docs2/timeline.md`: DB durations, transactions, per-screen breakdown.
- [ ] `docs2/mcp.md` + `plugins/flocon/skills/flocon-mcp/SKILL.md`: new tools and fields.
- [ ] `docs2/setup.md` + root `README.md` snippet if the Room 2 setup changes (`floconProfile`); `docs2/migration.md` if `floconLogs` is deprecated.

### FloconSwift (separate repo `openflocon/FloconSwift`, separate PR)
- [ ] `DatabaseQueryLogRemote`: optional `durationMs`, `rowCount`, `threadName`, `isMainThread`, `transactionId`, `error`.
- [ ] `logQuery(dbId:sqlQuery:bindArgs:durationMs:rowCount:transactionId:)` with `nil` defaults; thread / main-thread captured at the call.
- [ ] `logTransaction(...)` + `DatabaseTransactionLogRemote` (`log_transaction`).
- [ ] `profile(connection: OpaquePointer, dbId:)` with `sqlite3_trace_v2` (`SQLITE_TRACE_PROFILE | SQLITE_TRACE_ROW`), transaction detection; inactive when Flocon isn't started.
- [ ] Tests in `Tests/FloconTests/FloconDatabaseTests.swift`; demo in `FloconDemo/SampleDatabase.swift`; README / GRDB snippet.
