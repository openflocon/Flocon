package io.github.openflocon.mcp.sharedpreferences

import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel.Value
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.writeToolAnnotations
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

internal const val SHARED_PREFERENCES_TIMEOUT_MS = 3_000L

internal class SharedPreferencesMcpToolProvider(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    // not a constructor parameter: koin would try to inject it, it's only changed by the tests
    internal var answerTimeoutMs: Long = SHARED_PREFERENCES_TIMEOUT_MS

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_shared_preferences",
            title = "List shared preferences",
            description = "Lists the SharedPreferences files (Android) / UserDefaults suites (iOS) of the mobile app, with their id " +
                "(to give to get_shared_preference_values) and their storage type (SharedPreferences, DataStore, UserDefaults, Keychain...).",
            annotations = readOnlyToolAnnotations,
            handler = ::listSharedPreferences,
        )

        server.addTypedTool(
            name = "get_shared_preference_values",
            title = "Get shared preference values",
            description = "Reads the keys and values of a SharedPreferences file of the mobile app, as stored, with their type. " +
                "Optionally filtered by text (key or value).",
            annotations = readOnlyToolAnnotations,
            handler = ::getValues,
        )

        server.addTypedTool(
            name = "set_shared_preference_value",
            title = "Set shared preference value",
            description = "Edits the value of one existing key of a SharedPreferences file (Android) / UserDefaults suite (iOS) " +
                "of the mobile app. The type of the current value is used unless type is given. Keys cannot be created or removed. " +
                "The previous value is returned so it can be restored. The edit can lose data: before calling it, read the current " +
                "value with get_shared_preference_values and ask the user to confirm, showing key: old value -> new value.",
            annotations = writeToolAnnotations(destructive = true, idempotent = true),
            handler = ::setValue,
        )
    }

    private suspend fun listSharedPreferences(input: ListSharedPreferencesInput): SharedPreferencesMcpResult {
        val target = targetResolver.resolve(input)
        val preferences = loadFiles(target)

        return SharedPreferencesMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            sharedPreferences = preferences.map { SharedPreferenceMcpModel(sharedPreferenceId = it.id, name = it.name, type = it.type) },
        )
    }

    private suspend fun getValues(input: GetSharedPreferenceValuesInput): SharedPreferenceValuesMcpResult {
        val target = targetResolver.resolve(input)
        val id = input.sharedPreferenceId
        val limit = input.limit.coerceIn(1, MAX_LIMIT)
        val rows = loadRows(target, id)

        val text = input.text?.takeIf { it.isNotBlank() }
        val filtered = rows
            .filter { text == null || it.matches(text) }
            .sortedBy { it.key }

        return SharedPreferenceValuesMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            sharedPreferenceId = id,
            total = filtered.size,
            returned = minOf(filtered.size, limit),
            hasMore = filtered.size > limit,
            entries = filtered.take(limit).map { it.toMcpModel() },
        )
    }

    private suspend fun setValue(input: SetSharedPreferenceValueInput): SetSharedPreferenceValueMcpResult {
        val target = targetResolver.resolve(input)
        val id = input.sharedPreferenceId
        val sharedPreference = loadFiles(target).firstOrNull { it.id == id }
            ?: throw McpToolException("Unknown sharedPreferenceId '$id': use the ids returned by list_shared_preferences.")
        val rows = loadRows(target, id)
        val current = rows.firstOrNull { it.key == input.key }?.value
            ?: throw McpToolException(
                "Unknown key '${input.key}' in '$id': only existing keys can be edited, see get_shared_preference_values.",
            )
        val type = input.type ?: current.typeName()
        val value = input.value.toValue(type)

        // the app answers an edit with the full content of the file: wait for the requested value
        // (an unchanged value is never emitted again, the values flow only emits changes)
        sharedPreferencesRepository.editSharedPrefField(target, sharedPreference, input.key, value)
        val confirmed = value == current || withTimeoutOrNull(answerTimeoutMs) {
            sharedPreferencesRepository.observe(target, id).first { rows -> rows.any { it.key == input.key && it.value == value } }
        } != null

        return SetSharedPreferenceValueMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            sharedPreferenceId = id,
            key = input.key,
            type = type,
            previousValue = current.toJson(),
            newValue = value.toJson().takeIf { confirmed },
            confirmed = confirmed,
        )
    }

    /**
     * The app answers asynchronously, the known files are returned if it doesn't answer in time
     */
    private suspend fun loadFiles(target: DeviceIdAndPackageNameDomainModel): List<DeviceSharedPreferenceDomainModel> {
        sharedPreferencesRepository.askForDeviceSharedPreferences(target)
        return withTimeoutOrNull(answerTimeoutMs) {
            sharedPreferencesRepository.observeDeviceSharedPreferences(target).first { it.isNotEmpty() }
        } ?: sharedPreferencesRepository.observeDeviceSharedPreferences(target).first()
    }

    /**
     * The app answers asynchronously, the last known values are used if it doesn't answer in time
     */
    private suspend fun loadRows(
        target: DeviceIdAndPackageNameDomainModel,
        id: DeviceSharedPreferenceId,
    ): List<SharedPreferenceRowDomainModel> {
        val known = sharedPreferencesRepository.observe(target, id).first()
        sharedPreferencesRepository.getDeviceSharedPreferencesValues(target, id)
        return withTimeoutOrNull(answerTimeoutMs) {
            sharedPreferencesRepository.observe(target, id).first { it != known }
        } ?: known
    }

    /**
     * Exact conversion only: a value that doesn't fit the type is refused, never truncated
     */
    private fun String.toValue(type: String): Value {
        val value: Value? = when (type) {
            TYPE_STRING -> Value.StringValue(this)
            TYPE_INT -> toIntOrNull()?.let { Value.IntValue(it) }
            TYPE_LONG -> toLongOrNull()?.let { Value.LongValue(it) }
            TYPE_FLOAT -> toFloatOrNull()?.let { Value.FloatValue(it) }
            TYPE_BOOLEAN -> lowercase().toBooleanStrictOrNull()?.let { Value.BooleanValue(it) }
            TYPE_STRING_SET -> (runCatching { Json.parseToJsonElement(this) }.getOrNull() as? JsonArray)
                ?.map { (it as? JsonPrimitive)?.takeIf { p -> p.isString }?.content }
                ?.takeIf { items -> items.none { it == null } }
                ?.let { items -> Value.StringSetValue(items.filterNotNull().toSet()) }
            else -> throw McpToolException("Unknown type '$type': use one of ${TYPES.joinToString()}.")
        }
        return value ?: throw McpToolException("The value '$this' is not a valid $type.")
    }

    private fun Value.typeName(): String = when (this) {
        is Value.StringValue -> TYPE_STRING
        is Value.IntValue -> TYPE_INT
        is Value.LongValue -> TYPE_LONG
        is Value.FloatValue -> TYPE_FLOAT
        is Value.BooleanValue -> TYPE_BOOLEAN
        is Value.StringSetValue -> TYPE_STRING_SET
    }

    private fun Value.toJson(): JsonElement = when (this) {
        is Value.StringValue -> JsonPrimitive(value)
        is Value.IntValue -> JsonPrimitive(value)
        is Value.LongValue -> JsonPrimitive(value)
        is Value.FloatValue -> JsonPrimitive(value)
        is Value.BooleanValue -> JsonPrimitive(value)
        is Value.StringSetValue -> JsonArray(value.sorted().map { JsonPrimitive(it) })
    }

    private fun SharedPreferenceRowDomainModel.matches(text: String): Boolean = key.contains(text, ignoreCase = true) ||
        value.asText().contains(text, ignoreCase = true)

    private fun Value.asText(): String = when (this) {
        is Value.StringValue -> value
        is Value.IntValue -> value.toString()
        is Value.LongValue -> value.toString()
        is Value.FloatValue -> value.toString()
        is Value.BooleanValue -> value.toString()
        is Value.StringSetValue -> value.joinToString(",")
    }

    private fun SharedPreferenceRowDomainModel.toMcpModel() = SharedPreferenceEntryMcpModel(
        key = key,
        type = value.typeName(),
        value = value.toJson(),
    )

    private companion object {
        const val TYPE_STRING = "string"
        const val TYPE_INT = "int"
        const val TYPE_LONG = "long"
        const val TYPE_FLOAT = "float"
        const val TYPE_BOOLEAN = "boolean"
        const val TYPE_STRING_SET = "string_set"
        val TYPES = listOf(TYPE_STRING, TYPE_INT, TYPE_LONG, TYPE_FLOAT, TYPE_BOOLEAN, TYPE_STRING_SET)
    }
}
