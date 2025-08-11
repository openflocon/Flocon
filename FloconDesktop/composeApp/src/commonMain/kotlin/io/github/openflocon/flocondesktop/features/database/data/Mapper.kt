package io.github.openflocon.flocondesktop.features.database.data

import io.github.openflocon.flocondesktop.features.database.data.model.incoming.DatabaseExecuteSqlResponseDataModel
import io.github.openflocon.flocondesktop.features.database.data.model.incoming.DeviceDataBaseDataModel
import io.github.openflocon.flocondesktop.features.database.data.model.incoming.QueryResultReceivedDataModel
import io.github.openflocon.flocondesktop.features.database.data.model.incoming.ResponseAndRequestIdDataModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// maybe inject
private val databasesJsonParser = Json {
    ignoreUnknownKeys = true
}

@Serializable
data class ReceivedQueryWrapper(
    val type: String,
    val body: String,
)

internal fun decodeReceivedQuery(body: String): ResponseAndRequestIdDataModel? = try {
    val result = databasesJsonParser.decodeFromString<QueryResultReceivedDataModel>(body)
    val queryWrapper = databasesJsonParser.decodeFromString<ReceivedQueryWrapper>(result.result)
    when (queryWrapper.type) {
        "Error" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponseDataModel.Error>(
                queryWrapper.body,
            )

        "Insert" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponseDataModel.Insert>(
                queryWrapper.body,
            )

        "RawSuccess" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponseDataModel.RawSuccess>(
                queryWrapper.body,
            )

        "Select" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponseDataModel.Select>(
                queryWrapper.body,
            )

        "UpdateDelete" ->
            databasesJsonParser.decodeFromString<DatabaseExecuteSqlResponseDataModel.UpdateDelete>(
                queryWrapper.body,
            )

        else -> null
    }?.let {
        ResponseAndRequestIdDataModel(
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
