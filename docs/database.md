### 🧩 Database (kotlin multi platform compatible)

<img width="1726" height="1080" alt="Screenshot 2025-10-14 at 23 40 58" src="https://github.com/user-attachments/assets/47360e06-43af-4713-b0ed-a6728a6b49ad" />

<img width="1728" height="1077" alt="Screenshot 2025-10-14 at 23 44 16" src="https://github.com/user-attachments/assets/f351970f-0511-4b54-af5e-55dcd209f2e2" />

Flocon gives you direct access to your app’s **local databases** (SQLite, Room, etc.), with a clean interface for exploring and querying data.

Features include:

- Listing all available databases
- Display all database tables & schemas
- Running **custom SQL queries** in tabs
- Auto update queries
- Save queries as favorite
- Generate Insert & Delete queries
  

This makes it easy to debug persistent storage issues, verify migrations, or test app behavior with specific data sets — all without leaving your IDE.

On android there's nothing to add, but on a multi platform project, you need to provide the database's path to flocon

```kotlin
// on a desktop project
val dbFile = File(System.getProperty("java.io.tmpdir"), "flocon_food_database.db")

floconRegisterDatabase(
    displayName = "food",
    absolutePath = dbFile.absolutePath,
)

return Room.databaseBuilder<FoodDatabase>(
        name = dbFile.absolutePath,
    )
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
```


```kotlin
// on an ios project
val dbFile = "${documentDirectory()}/dog_database.db"

floconRegisterDatabase(
    absolutePath = dbFile, 
    displayName = "Dog Database"
)

return Room.databaseBuilder<DogDatabase>(
        name = dbFile,
    )
    .setDriver(NativeSQLiteDriver())
    .build()
```