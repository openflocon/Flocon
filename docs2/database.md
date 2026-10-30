---
title: Database Explorer & SQL Editor
description: Explore Room, SQLite, and SQLDelight databases with live query execution and logging.
---

# 🧩 Database Explorer & SQL Editor

Flocon provides direct access to your application's **local databases** (SQLite, Room, SQLDelight), with an interactive desktop interface for exploring schemas, querying data, and logging live SQL statements.

---

## Overview

<img width="1726" height="1080" alt="Database Explorer Schema" src="https://github.com/user-attachments/assets/47360e06-43af-4713-b0ed-a6728a6b49ad" style="border-radius: 8px;" />

<img width="1728" height="1077" alt="SQL Editor" src="https://github.com/user-attachments/assets/f351970f-0511-4b54-af5e-55dcd209f2e2" style="border-radius: 8px; margin-top: 1rem;" />

Key capabilities:
- **Automatic Detection**: Automatically locates all SQLite databases on Android.
- **Schema Visualizer**: Lists tables, primary keys, indexes, and column types.
- **Interactive SQL Workspace**: Write and execute custom SQL queries with formatted tabular results.
- **Saved Queries**: Bookmark frequently used queries.
- **Live Query Logging**: Monitor queries executed by your app and ORMs in real time.

---

## Setup

Add the database core and the module matching your Room version:

=== "Room 2 (androidx.room)"

    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon-database-core:LAST_VERSION")
        debugImplementation("io.github.openflocon:flocon-database-room:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-database-core-no-op:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-database-room-no-op:LAST_VERSION")
    }
    ```

=== "Room 3 (androidx.room3)"

    ```kotlin
    dependencies {
        debugImplementation("io.github.openflocon:flocon-database-core:LAST_VERSION")
        debugImplementation("io.github.openflocon:flocon-database-room3:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-database-core-no-op:LAST_VERSION")
        releaseImplementation("io.github.openflocon:flocon-database-room3-no-op:LAST_VERSION")
    }
    ```

Then install the plugin. `room()` (from `io.github.openflocon.flocon.database.room`, or `.room3`) adds the Room provider:

```kotlin
startFlocon(context) {
    install(FloconDatabase) {
        room()
    }
}
```

---

## Database Registration

### Automatic (Android)
With `room()`, Flocon automatically scans your app's databases folder for SQLite database files on Android.

### Room Databases (all platforms)
To register a Room database (in-memory databases included) with a display name:

```kotlin
val dogDatabase = Room.inMemoryDatabaseBuilder(context, DogDatabase::class.java).build()

Flocon.databasePlugin.register(
    id = "inmemory_dogs",
    displayName = "In-Memory Dogs",
    database = dogDatabase,
)
```

The `id` is required, stable and **unique**: registering two different databases with the same id throws an
`IllegalArgumentException`. It is the id the desktop app uses for everything on this database (browsing, queries,
query logs), so the query logs of a database must be sent with the same id (see below).

`register(id, displayName, database)` comes from `io.github.openflocon.flocon.database.room` (Room 2) or `io.github.openflocon.flocon.database.room3` (Room 3).

### Room 3 on the web (Wasm)

Room 3 runs on Wasm with `WebWorkerSQLiteDriver` (`androidx.sqlite:sqlite-web`), which talks to a SQLite
Web Worker. There is no file system, so the databases are not discovered: register each Room database
with `register(id, displayName, database)` (the plugin still has to be installed with `install(FloconDatabase) { room() }`):

```kotlin
val dogDatabase = Room.databaseBuilder<DogDatabase>(name = "dogs.db")
    .setDriver(WebWorkerSQLiteDriver(createWorker()))
    .build()

Flocon.databasePlugin.register(id = "dogs", displayName = "dogs", database = dogDatabase)
```

See `FloconAndroid/sample/wasm` for a complete setup (worker, npm package and KSP for wasmJs).

### SQLite Files (Kotlin Multiplatform)

Any SQLite file can be registered with its absolute path:

```kotlin
Flocon.databasePlugin.register(
    FloconFileDatabaseModel(
        id = "app_db",
        displayName = "App DB",
        absolutePath = dbFile.absolutePath,
    )
)
```

---

## Real-Time Query Logging

Flocon can display all SQL queries executed by your app in real-time — great for verifying generated queries from Room or detecting slow multi-table joins.

<img width="1440" height="968" alt="Database Query Logging" src="https://github.com/user-attachments/assets/f7c06191-17c1-41f6-9184-86b50d6f9945" style="border-radius: 8px;" />

=== "Room 2 (Android)"

    ```kotlin
    val database = Room.databaseBuilder(
        context.applicationContext,
        DogDatabase::class.java,
        "dogs_database"
    )
        .floconLogs(id = "dogs") // io.github.openflocon.flocon.database.room.extensions.floconLogs
        .build()
        .also { Flocon.databasePlugin.register(id = "dogs", displayName = "dogs", database = it) }
    ```

    The `id` given to `floconLogs` must be the one the database is registered with, otherwise the desktop app
    cannot show the queries with the database.

=== "Room 3"

    Room 3 has no query callback: wrap its SQLite driver instead.

    ```kotlin
    Room.databaseBuilder<DogDatabase>(context, "dogs_database")
        .setDriver(AndroidSQLiteDriver().floconLogs(id = "dogs")) // io.github.openflocon.flocon.database.room3.floconLogs
        .build()
        .also { Flocon.databasePlugin.register(id = "dogs", displayName = "dogs", database = it) }
    ```

    Wraps any driver (`BundledSQLiteDriver`, `NativeSQLiteDriver`, `WebWorkerSQLiteDriver`...), checked on Android with `AndroidSQLiteDriver`. Each statement
    is logged when it runs, with its arguments. Same rule as Room 2: the `id` is the one the database is registered with.

=== "Manual Query Logging"

    ```kotlin
    Flocon.databasePlugin.logQuery(
        dbId = "my_custom_db", // the id the database was registered with
        sqlQuery = "SELECT * FROM users WHERE id = ?",
        bindArgs = listOf(42)
    )
    ```

    Room only accepts one query callback per database. If your app already sets its own
    `setQueryCallback`, call `logQuery` from it to forward the queries to Flocon:

    ```kotlin
    Room.databaseBuilder(context, DogDatabase::class.java, "dogs_database")
        .setQueryCallback({ sqlQuery, bindArgs ->
            Flocon.databasePlugin.logQuery("dogs", sqlQuery, bindArgs)
            myOwnLogger.log(sqlQuery)
        }, Executors.newSingleThreadExecutor())
        .build()
    ```
