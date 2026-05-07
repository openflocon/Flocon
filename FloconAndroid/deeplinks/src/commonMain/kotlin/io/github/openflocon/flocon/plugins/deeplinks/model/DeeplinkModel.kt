package io.github.openflocon.flocon.plugins.deeplinks.model

@ConsistentCopyVisibility
data class DeeplinkModel internal constructor(
    val link: String,
    val label: String? = null,
    val description: String? = null,
    val parameters: List<Parameter>
) {
    sealed interface Parameter {
        val name: String

        @ConsistentCopyVisibility
        data class AutoComplete internal constructor(
            override val name: String,
            val autoComplete: List<String>
        ) : Parameter

        @ConsistentCopyVisibility
        data class Variable internal constructor(
            override val name: String,
            val variableName: String
        ) : Parameter
    }
}
