package com.github.openflocon.flocon.plugins.dashboard.model.config

data class TextFieldConfig(
    val id: String,
    val label: String,
    val placeHolder: String?,
    val value: String,
    val onSubmitted: (String) -> Unit,
) : ElementConfig