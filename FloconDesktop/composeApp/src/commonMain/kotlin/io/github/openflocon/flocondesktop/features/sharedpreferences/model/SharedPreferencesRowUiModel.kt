package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable

@Immutable
data class SharedPreferencesRowUiModel(
    val key: String,
    val value: Value,
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

fun previewSharedPreferencesStringRowUiModel() = SharedPreferencesRowUiModel(
    key = "key",
    value = SharedPreferencesRowUiModel.Value.StringValue(value = "value"),
)

fun previewSharedPreferencesIntRowUiModel() = SharedPreferencesRowUiModel(
    key = "key",
    value = SharedPreferencesRowUiModel.Value.IntValue(value = 23),
)

fun previewSharedPreferencesFloatRowUiModel() = SharedPreferencesRowUiModel(
    key = "key",
    value = SharedPreferencesRowUiModel.Value.FloatValue(value = 1.4f),
)

fun previewSharedPreferencesBooleanRowUiModel() = SharedPreferencesRowUiModel(
    key = "key",
    value = SharedPreferencesRowUiModel.Value.BooleanValue(value = true),
)

fun previewSharedPreferencesLongRowUiModel() = SharedPreferencesRowUiModel(
    key = "key",
    value = SharedPreferencesRowUiModel.Value.LongValue(value = 1024L),
)
