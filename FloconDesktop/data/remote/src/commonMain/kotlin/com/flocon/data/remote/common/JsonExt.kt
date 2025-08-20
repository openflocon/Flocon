package com.flocon.data.remote.common

import kotlinx.serialization.json.Json

internal inline fun <reified T> Json.safeDecodeFromString(data: String): T? = try {
    decodeFromString(data)
} catch (e: Throwable) {
    null
}
