package io.github.openflocon.flocondesktop.features.deeplinks.data

import io.github.openflocon.flocondesktop.features.deeplinks.data.model.incoming.DeeplinksReceivedDataModel
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import kotlinx.serialization.json.Json

// maybe inject
private val deeplinkJsonParser =
    Json {
        ignoreUnknownKeys = true
    }

internal fun decodeListDeeplinks(body: String): DeeplinksReceivedDataModel? = try {
    deeplinkJsonParser.decodeFromString<DeeplinksReceivedDataModel>(body)
} catch (t: Throwable) {
    t.printStackTrace()
    null
}

internal fun DeeplinksReceivedDataModel.toDomain(): List<DeeplinkDomainModel> = this.deeplinks.map {
    DeeplinkDomainModel(
        label = it.label,
        link = it.link,
        description = it.description,
    )
}
