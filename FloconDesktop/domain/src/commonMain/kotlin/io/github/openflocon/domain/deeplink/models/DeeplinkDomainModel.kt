package io.github.openflocon.domain.deeplink.models

data class DeeplinkDomainModel(
    val id: Long,
    val label: String?,
    val link: String,
    val description: String?,
    val parameters: List<Parameter>,
) {
    sealed interface Parameter {
        val name: String

        data class AutoComplete(
            override val name: String,
            val autoComplete: List<String>
        ) : Parameter

        data class Variable(
            override val name: String,
            val variableName: String
        ) : Parameter

    }
}
