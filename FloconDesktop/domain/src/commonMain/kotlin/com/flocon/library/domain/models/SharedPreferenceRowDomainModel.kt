package com.flocon.library.domain.models

data class SharedPreferenceRowDomainModel(
    val key: String,
    val value: Value,
) {
    sealed interface Value {
        data class StringValue(val value: String) : Value
        data class IntValue(val value: Int) : Value
        data class LongValue(val value: Long) : Value
        data class FloatValue(val value: Float) : Value
        data class BooleanValue(val value: Boolean) : Value
        data class StringSetValue(val value: Set<String>) : Value
    }
}
