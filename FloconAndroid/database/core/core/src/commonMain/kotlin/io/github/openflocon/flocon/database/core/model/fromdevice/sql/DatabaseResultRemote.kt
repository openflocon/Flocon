package io.github.openflocon.flocon.database.core.model.fromdevice.sql

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import kotlinx.serialization.Serializable

/**
 * Format expected by the desktop: the type, and the response itself as a json string
 */
@Serializable
internal class DatabaseResultRemote(
    val type: String,
    val body: String,
)

@Serializable
internal class DatabaseErrorRemote(
    val message: String,
    val originalSql: String,
)

internal fun DatabaseExecuteResponse.toRemoteResult(encoder: FloconEncoder): String {
    val remote = when (this) {
        is DatabaseExecuteSqlResponse.Select -> DatabaseResultRemote(type = "Select", body = encoder.encode(this))
        is DatabaseExecuteSqlResponse.Insert -> DatabaseResultRemote(type = "Insert", body = encoder.encode(this))
        is DatabaseExecuteSqlResponse.UpdateDelete -> DatabaseResultRemote(type = "UpdateDelete", body = encoder.encode(this))
        is DatabaseExecuteSqlResponse.RawSuccess -> DatabaseResultRemote(type = "RawSuccess", body = "{}")
        is DatabaseExecuteSqlResponse.Error -> DatabaseResultRemote(
            type = "Error",
            body = encoder.encode(DatabaseErrorRemote(message = message, originalSql = originalSql)),
        )
    }
    return encoder.encode(remote)
}
