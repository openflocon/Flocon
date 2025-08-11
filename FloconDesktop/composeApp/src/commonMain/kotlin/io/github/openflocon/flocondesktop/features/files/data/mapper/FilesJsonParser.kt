package io.github.openflocon.flocondesktop.features.files.data.mapper

import com.flocon.data.remote.files.models.FromDeviceFilesResultDataModel
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
