package io.github.openflocon.flocon.plugins.deeplinks.model

data class DeeplinkModel(
    val link: String,
    val label: String? = null,
    val description: String? = null,
    val parameters: List<Parameter>,
) {

    sealed interface Parameter {
        val paramName: String

        data class AutoComplete(
            override val paramName: String,
            val autoComplete: List<String>
        ) : Parameter

        data class Variable(
            override val paramName: String,
            val variableName: String
        ) : Parameter

    }
}