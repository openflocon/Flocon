package io.github.openflocon.flocon.plugins.deeplinks.model

data class DeeplinkModel(
    val link: String,
    val label: String? = null,
    val description: String? = null,
    val parameters: List<Parameter>,
) {
    data class Parameter(
        val paramName: String,
        val autoComplete: List<String>,
    )
}