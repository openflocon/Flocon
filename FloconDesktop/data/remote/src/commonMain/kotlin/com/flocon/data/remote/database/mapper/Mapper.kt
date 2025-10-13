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
)

@Serializable
data class ReceivedQueryBodyWrapper(
    val body: String,
)

internal fun Json.decodeReceivedQuery(body: String): ResponseAndRequestIdDataModel? = try {
    val result = decodeFromString<QueryResultReceivedDataModel>(body)
    val queryWrapper = decodeFromString<ReceivedQueryWrapper>(result.result)
    when (queryWrapper.type) {
        "Error" -> {
            val bodyWrapper = decodeFromString<ReceivedQueryBodyWrapper>(result.result)
            decodeFromString<DatabaseExecuteSqlResponseDataModel.Error>(bodyWrapper.body)
        }
        "Insert" -> {
            val bodyWrapper = decodeFromString<ReceivedQueryBodyWrapper>(result.result)
            decodeFromString<DatabaseExecuteSqlResponseDataModel.Insert>(bodyWrapper.body)
        }
        "RawSuccess" -> DatabaseExecuteSqlResponseDataModel.RawSuccess
        "Select" -> {
            val bodyWrapper = decodeFromString<ReceivedQueryBodyWrapper>(result.result)
            decodeFromString<DatabaseExecuteSqlResponseDataModel.Select>(bodyWrapper.body)
        }
        "UpdateDelete" -> {
            decodeFromString<DatabaseExecuteSqlResponseDataModel.UpdateDelete>(result.result)
        }

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
