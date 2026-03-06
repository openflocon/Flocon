package io.github.openflocon.domain.deeplink.models

data class DeeplinkVariableDomainModel(
    val name: String,
    val mode: Mode,
    val description: String?
) {

    sealed interface Mode {

        object Input : Mode

        data class AutoComplete(val suggestions: List<String>) : Mode

    }
}
