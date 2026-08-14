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

## Database Registration

### Automatic (Android)
On Android, Flocon automatically scans your app's internal sandbox for SQLite database files without requiring additional setup.

### In-Memory / Custom Databases (Android)
To register an **in-memory Room database** or specify a custom display name:

```kotlin
val dogDatabase = Room.inMemoryDatabaseBuilder(context, DogDatabase::class.java).build()

floconRegisterDatabase(
    displayName = "In-Memory Dogs",
    openHelper = dogDatabase.openHelper
)
```

### Kotlin Multiplatform (Desktop & iOS)

For KMP projects, register the database with its absolute filesystem path:

=== "Desktop (JVM)"

    ```kotlin
    val dbFile = File(System.getProperty("java.io.tmpdir"), "app_database.db")

    floconRegisterDatabase(
        displayName = "App DB",
        absolutePath = dbFile.absolutePath,
    )
    ```

=== "iOS (Kotlin Multiplatform)"

    ```kotlin
    val dbPath = "${documentDirectory()}/app_database.db"

    floconRegisterDatabase(
        displayName = "App DB",
        absolutePath = dbPath
    )
    ```

---

## Real-Time Query Logging

Flocon can display all SQL queries executed by your app in real-time — great for verifying generated queries from Room or detecting slow multi-table joins.

<img width="1440" height="968" alt="Database Query Logging" src="https://github.com/user-attachments/assets/f7c06191-17c1-41f6-9184-86b50d6f9945" style="border-radius: 8px;" />

=== "Room Callback Integration"

    ```kotlin
    val dbName = "dogs_database"
    val database = Room.databaseBuilder(
        context.applicationContext,
        DogDatabase::class.java,
        dbName
    ).setQueryCallback({ sqlQuery, bindArgs -> 
        floconLogDatabaseQuery(
            dbName = dbName, 
            sqlQuery = sqlQuery, 
            bindArgs = bindArgs
        ) 
    }, Executors.newSingleThreadExecutor())
    .build()
    ```

=== "Manual Query Logging"

    ```kotlin
    floconLogDatabaseQuery(
        dbName = "my_custom_db",
        sqlQuery = "SELECT * FROM users WHERE id = ?",
        bindArgs = listOf(42)
    )
    ```
