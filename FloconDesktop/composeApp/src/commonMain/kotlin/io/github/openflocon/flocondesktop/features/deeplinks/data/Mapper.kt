package io.github.openflocon.flocondesktop.features.deeplinks.data

import kotlinx.serialization.json.Json

// maybe inject
private val deeplinkJsonParser =
    Json {
        ignoreUnknownKeys = true
    }

