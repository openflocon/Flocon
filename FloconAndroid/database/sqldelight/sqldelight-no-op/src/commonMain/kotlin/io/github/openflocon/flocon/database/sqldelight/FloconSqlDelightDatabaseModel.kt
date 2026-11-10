package io.github.openflocon.flocon.database.sqldelight

import app.cash.sqldelight.db.SqlDriver
import io.github.openflocon.flocon.database.core.FloconDatabasePlugin

fun FloconDatabasePlugin.register(id: String, displayName: String, driver: SqlDriver) {
    // no op
}
