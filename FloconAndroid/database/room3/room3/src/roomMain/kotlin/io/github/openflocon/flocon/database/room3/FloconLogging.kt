package io.github.openflocon.flocon.database.room3

import androidx.sqlite.SQLiteDriver

/**
 * Shows the queries run by a Room 3 database in the desktop app: Room 3 has no query callback, so the
 * driver of the database is wrapped.
 *
 * ```kotlin
 * Room.databaseBuilder<DogDatabase>(context, "dogs_database")
 *     .setDriver(BundledSQLiteDriver().floconLogs(id = "dogs"))
 *     .build()
 *     .also { Flocon.databasePlugin.register(id = "dogs", displayName = "Dogs", database = it) }
 * ```
 *
 * Meant for the drivers that run the queries themselves (bundled, framework, native), not for the web worker driver.
 *
 * @param id the id the database is registered with, see [register]
 */
expect fun SQLiteDriver.floconLogs(id: String): SQLiteDriver
