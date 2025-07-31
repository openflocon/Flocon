package io.github.openflocon.flocondesktop.features.database.data

import com.florent37.flocondesktop.features.database.data.model.incoming.DatabaseExecuteSqlResponse
import com.florent37.flocondesktop.features.database.data.model.incoming.DeviceDataBaseDataModel
import com.florent37.flocondesktop.features.database.data.model.incoming.QueryResultReceivedDataModel
import com.florent37.flocondesktop.features.database.data.model.incoming.ResponseAndRequestId
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// maybe inject
private val databasesJsonParser =
    Json {
        ignoreUnknownKeys = true
    }

@Serializable
data class ReceivedQueryWrapper(
    val type: String,
    val body: String,
)

internal fun decodeReceivedQuery(body: String): ResponseAndRequestId? = try {
    val result = databasesJsonParser.decodeFromString<QueryResultReceivedDataModel>(body)
    val queryWrapper = databasesJsonParser.decodeFromString<ReceivedQueryWrapper>(result.result)
    when (queryWrapper.type) {
        "Error" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponse.Error>(
                queryWrapper.body,
            )

        "Insert" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponse.Insert>(
                queryWrapper.body,
            )

        "RawSuccess" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponse.RawSuccess>(
                queryWrapper.body,
            )

        "Select" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponse.Select>(
                queryWrapper.body,
            )

        "UpdateDelete" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponse.UpdateDelete>(
                queryWrapper.body,
            )

        else -> null
    }?.let {
        ResponseAndRequestId(
            requestId = result.requestId,
            response = it,
        )
    }
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

internal fun decodeDeviceDatabases(body: String): List<DeviceDataBaseDataModel>? = try {
    databasesJsonParser.decodeFromString<List<DeviceDataBaseDataModel>>(body)
} catch (t: Throwable) {
    t.printStackTrace()
    null
}
