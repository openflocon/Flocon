package io.github.openflocon.flocondesktop.features.sharedpreferences

import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceTypeUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceValueTypeUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

internal fun SharedPreferenceRowDomainModel.Value.toUi(): SharedPreferencesRowUiModel.Value = when (this) {
    is SharedPreferenceRowDomainModel.Value.StringValue -> SharedPreferencesRowUiModel.Value.StringValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.IntValue -> SharedPreferencesRowUiModel.Value.IntValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.BooleanValue -> SharedPreferencesRowUiModel.Value.BooleanValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.FloatValue -> SharedPreferencesRowUiModel.Value.FloatValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.LongValue -> SharedPreferencesRowUiModel.Value.LongValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.StringSetValue -> SharedPreferencesRowUiModel.Value.StringSetValue(
        value,
    )
}

internal fun DeviceSharedPreferenceDomainModel.toUi(): DeviceSharedPrefUiModel = DeviceSharedPrefUiModel(
    id = id,
    name = name,
    type = type,
    typeKind = preferenceTypeKind(type),
)

// the type is a free value sent by the app: known storages get their own icon, the others a generic one
private fun preferenceTypeKind(type: String?): PreferenceTypeUiModel = when (type?.lowercase()) {
    "sharedpreferences" -> PreferenceTypeUiModel.SharedPreferences
    "datastore" -> PreferenceTypeUiModel.DataStore
    "userdefaults" -> PreferenceTypeUiModel.UserDefaults
    "keychain" -> PreferenceTypeUiModel.Keychain
    "java preferences" -> PreferenceTypeUiModel.JavaPreferences
    "localstorage" -> PreferenceTypeUiModel.LocalStorage
    else -> PreferenceTypeUiModel.Custom
}

internal fun SharedPreferenceRowDomainModel.toUi(): SharedPreferencesRowUiModel {
    val uiValue = value.toUi()
    val stringValue = uiValue as? SharedPreferencesRowUiModel.Value.StringValue
    val json = stringValue?.value?.let(::parseJson)
    val type = when (uiValue) {
        is SharedPreferencesRowUiModel.Value.StringValue -> if (json != null) PreferenceValueTypeUiModel.Json else PreferenceValueTypeUiModel.Str
        is SharedPreferencesRowUiModel.Value.IntValue -> PreferenceValueTypeUiModel.Int
        is SharedPreferencesRowUiModel.Value.LongValue -> PreferenceValueTypeUiModel.Long
        is SharedPreferencesRowUiModel.Value.FloatValue -> PreferenceValueTypeUiModel.Float
        is SharedPreferencesRowUiModel.Value.BooleanValue -> PreferenceValueTypeUiModel.Bool
        is SharedPreferencesRowUiModel.Value.StringSetValue -> PreferenceValueTypeUiModel.Set
    }
    return SharedPreferencesRowUiModel(
        key = key,
        value = uiValue,
        type = type,
        collapsible = stringValue?.value?.let { toCollapsible(it, json) },
    )
}

private const val LONG_TEXT_LENGTH = 80
private const val PREVIEW_MAX_LENGTH = 300

// a pretty printed json of at most this many lines is displayed open, not collapsible
private const val SHORT_JSON_MAX_LINES = 5

private val prettyJson = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}

// a string is a json when it is an object or an array that parses
internal fun parseJson(text: String): JsonElement? {
    val trimmed = text.trim()
    if (!(trimmed.startsWith("{") || trimmed.startsWith("["))) return null
    return try {
        Json.parseToJsonElement(trimmed).takeIf { it is JsonObject || it is JsonArray }
    } catch (_: Exception) {
        null
    }
}

private fun toCollapsible(text: String, json: JsonElement?): SharedPreferencesRowUiModel.Collapsible? {
    if (json == null && text.length <= LONG_TEXT_LENGTH && !text.contains("\n")) return null
    val bytes = byteSizeLabel(text.encodeToByteArray().size)
    return if (json != null) {
        val count = when (json) {
            is JsonArray -> json.size
            is JsonObject -> json.size
            else -> 0
        }
        val pretty = prettyJson.encodeToString(JsonElement.serializer(), json)
        SharedPreferencesRowUiModel.Collapsible(
            preview = Json.encodeToString(JsonElement.serializer(), json).take(PREVIEW_MAX_LENGTH),
            sizeLabel = "${itemsLabel(count)} · $bytes",
            expandedText = pretty,
            isJson = true,
            alwaysOpen = pretty.lines().size <= SHORT_JSON_MAX_LINES,
        )
    } else {
        SharedPreferencesRowUiModel.Collapsible(
            preview = text.replace(Regex("\\s+"), " ").trim().take(PREVIEW_MAX_LENGTH),
            sizeLabel = bytes,
            expandedText = text,
            isJson = false,
        )
    }
}

internal fun itemsLabel(count: Int): String = if (count == 1) "1 item" else "$count items"

internal fun byteSizeLabel(bytes: Int): String = when {
    bytes < 1024 -> "$bytes B"

    else -> {
        val tenths = bytes * 10 / 1024
        "${tenths / 10}.${tenths % 10} KB"
    }
}

/** 1000 -> "1s", 2500 -> "2500ms" */
internal fun delayLabel(delayMs: Int): String = if (delayMs % 1000 == 0) "${delayMs / 1000}s" else "${delayMs}ms"
