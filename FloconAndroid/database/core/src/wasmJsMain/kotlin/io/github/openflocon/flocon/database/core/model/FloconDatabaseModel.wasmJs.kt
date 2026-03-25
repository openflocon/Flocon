package io.github.openflocon.flocon.database.core.model

import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse

actual fun openDbAndExecuteQuery(
    path: String,
    query: String
): DatabaseExecuteSqlResponse {
    return DatabaseExecuteSqlResponse.Error(
        message = "SQLite is not supported on WasmJS yet",
        originalSql = query
    )
}
