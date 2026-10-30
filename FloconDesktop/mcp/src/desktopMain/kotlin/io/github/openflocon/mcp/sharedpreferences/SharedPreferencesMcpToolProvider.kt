package io.github.openflocon.mcp.sharedpreferences

import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel.Value
import io.github.openflocon.domain.sharedpreference.repository.SharedPreferencesRepository
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
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
    }

    private suspend fun listSharedPreferences(input: ListSharedPreferencesInput): SharedPreferencesMcpResult {
        val target = targetResolver.resolve(input)

        // the app answers asynchronously, the known files are returned if it doesn't answer in time
        sharedPreferencesRepository.askForDeviceSharedPreferences(target)
        val preferences = withTimeoutOrNull(answerTimeoutMs) {
            sharedPreferencesRepository.observeDeviceSharedPreferences(target).first { it.isNotEmpty() }
        } ?: sharedPreferencesRepository.observeDeviceSharedPreferences(target).first()

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

        // the app answers asynchronously, the last known values are used if it doesn't answer in time
        val known = sharedPreferencesRepository.observe(target, id).first()
        sharedPreferencesRepository.getDeviceSharedPreferencesValues(target, id)
        val rows = withTimeoutOrNull(answerTimeoutMs) {
            sharedPreferencesRepository.observe(target, id).first { it != known }
        } ?: known

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

    private fun SharedPreferenceRowDomainModel.toMcpModel(): SharedPreferenceEntryMcpModel {
        val (type, json) = when (val v = value) {
            is Value.StringValue -> "string" to JsonPrimitive(v.value)
            is Value.IntValue -> "int" to JsonPrimitive(v.value)
            is Value.LongValue -> "long" to JsonPrimitive(v.value)
            is Value.FloatValue -> "float" to JsonPrimitive(v.value)
            is Value.BooleanValue -> "boolean" to JsonPrimitive(v.value)
            is Value.StringSetValue -> "string_set" to JsonArray(v.value.sorted().map { JsonPrimitive(it) })
        }
        return SharedPreferenceEntryMcpModel(key = key, type = type, value = json as JsonElement)
    }
}
