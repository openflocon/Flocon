package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.toUi

@Immutable
data class SharedPreferencesRowUiModel(
    val key: String,
    val value: Value,
    val type: PreferenceValueTypeUiModel,
    // only for the long values (json or long text): displayed collapsed by default
    val collapsible: Collapsible? = null,
) {
    @Immutable
    sealed interface Value {
        @Immutable
        data class StringValue(val value: String) : Value

        @Immutable
        data class IntValue(val value: Int) : Value

        @Immutable
        data class LongValue(val value: Long) : Value

        @Immutable
        data class FloatValue(val value: Float) : Value

        @Immutable
        data class BooleanValue(val value: Boolean) : Value

        @Immutable
        data class StringSetValue(val value: Set<String>) : Value
    }

    @Immutable
    data class Collapsible(
        // single line version of the value
        val preview: String,
        // ex: "3 items · 412 B"
        val sizeLabel: String,
        // the pretty printed json, or the raw text
        val expandedText: String,
        val isJson: Boolean,
    )

    fun contains(text: String): Boolean = key.contains(text, ignoreCase = true) || value.asString().contains(text, ignoreCase = true)
}

fun SharedPreferencesRowUiModel.Value.asString() = when (this) {
    is SharedPreferencesRowUiModel.Value.BooleanValue -> value.toString()
    is SharedPreferencesRowUiModel.Value.FloatValue -> value.toString()
    is SharedPreferencesRowUiModel.Value.IntValue -> value.toString()
    is SharedPreferencesRowUiModel.Value.LongValue -> value.toString()
    is SharedPreferencesRowUiModel.Value.StringValue -> value
    is SharedPreferencesRowUiModel.Value.StringSetValue -> value.toString()
}

private fun previewRow(key: String, value: SharedPreferenceRowDomainModel.Value) = SharedPreferenceRowDomainModel(key, value).toUi()

fun previewSharedPreferencesStringRowUiModel() = previewRow("key", SharedPreferenceRowDomainModel.Value.StringValue("value"))

fun previewSharedPreferencesIntRowUiModel() = previewRow("key", SharedPreferenceRowDomainModel.Value.IntValue(23))

fun previewSharedPreferencesFloatRowUiModel() = previewRow("key", SharedPreferenceRowDomainModel.Value.FloatValue(1.4f))

fun previewSharedPreferencesBooleanRowUiModel() = previewRow("key", SharedPreferenceRowDomainModel.Value.BooleanValue(true))

fun previewSharedPreferencesLongRowUiModel() = previewRow("key", SharedPreferenceRowDomainModel.Value.LongValue(1024L))

fun previewSharedPreferencesStringSetRowUiModel() = previewRow(
    "key",
    SharedPreferenceRowDomainModel.Value.StringSetValue(setOf("alpha", "beta", "gamma")),
)

fun previewSharedPreferencesJsonRowUiModel() = previewRow(
    "cached_users",
    SharedPreferenceRowDomainModel.Value.StringValue(
        """[{"id":1,"username":"user_528deafd","email":"user_528deafd@example.com"},""" +
            """{"id":2,"username":"user_91c3a07e","email":"user_91c3a07e@example.com"},""" +
            """{"id":3,"username":"user_07be44d2","email":"user_07be44d2@example.com"}]""",
    ),
)
