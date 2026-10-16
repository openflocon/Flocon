package io.github.openflocon.flocon.pluginsold.dashboard.model.config

data class TextFieldConfig(
    val id: String,
    val label: String,
    val placeHolder: String?,
    val value: String,
    val onSubmitted: (String) -> Unit,
) : ElementConfig