package io.github.openflocon.flocon.database.sqldelight

import app.cash.sqldelight.db.SqlDriver

fun SqlDriver.floconLogs(id: String): SqlDriver = this // no op

fun SqlDriver.withFlocon(id: String, displayName: String = id, logQueries: Boolean = true): SqlDriver = this // no op
