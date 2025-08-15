package com.flocon.data.remote.database.mapper

import com.flocon.data.remote.database.models.DatabaseExecuteSqlResponseDataModel
import com.flocon.data.remote.database.models.DeviceDataBaseDataModel
import com.flocon.data.remote.database.models.QueryResultReceivedDataModel
import com.flocon.data.remote.database.models.ResponseAndRequestIdDataModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ReceivedQueryWrapper(
    val type: String,
    val body: String,
)

internal fun Json.decodeReceivedQuery(body: String): ResponseAndRequestIdDataModel? = try {
    val result = decodeFromString<QueryResultReceivedDataModel>(body)
    val queryWrapper = decodeFromString<ReceivedQueryWrapper>(result.result)
    when (queryWrapper.type) {
        "Error" -> decodeFromString<DatabaseExecuteSqlResponseDataModel.Error>(queryWrapper.body)
        "Insert" -> decodeFromString<DatabaseExecuteSqlResponseDataModel.Insert>(queryWrapper.body)
        "RawSuccess" -> decodeFromString<DatabaseExecuteSqlResponseDataModel.RawSuccess>(queryWrapper.body)
        "Select" -> decodeFromString<DatabaseExecuteSqlResponseDataModel.Select>(queryWrapper.body)
        "UpdateDelete" -> decodeFromString<DatabaseExecuteSqlResponseDataModel.UpdateDelete>(queryWrapper.body)

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

internal fun Json.decodeDeviceDatabases(body: String): List<DeviceDataBaseDataModel>? = try {
    decodeFromString<List<DeviceDataBaseDataModel>>(body)
} catch (t: Throwable) {
    t.printStackTrace()
    null
}
