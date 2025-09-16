package io.github.openflocon.domain.deeplink.models

data class DeeplinkDomainModel(
    val id: Long,
    val label: String?,
    val link: String,
    val description: String?,
    val parameters: List<Parameter>,
) {
    data class Parameter(
        val paramName: String,
        val autoComplete: List<String>,
    )
}
