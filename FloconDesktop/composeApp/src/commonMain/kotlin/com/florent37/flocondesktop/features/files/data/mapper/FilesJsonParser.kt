package com.florent37.flocondesktop.features.files.data.mapper

import com.florent37.flocondesktop.features.files.data.model.incoming.FromDeviceFilesResultDataModel
import kotlinx.serialization.json.Json

private val filesJsonParser =
    Json {
        ignoreUnknownKeys = true
    }

internal fun decodeListFilesResult(body: String): FromDeviceFilesResultDataModel? = try {
    filesJsonParser.decodeFromString<FromDeviceFilesResultDataModel>(body)
} catch (t: Throwable) {
    t.printStackTrace()
    null
}
