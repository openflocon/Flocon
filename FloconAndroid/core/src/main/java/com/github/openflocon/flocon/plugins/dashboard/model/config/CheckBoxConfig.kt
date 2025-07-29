package com.github.openflocon.flocon.plugins.dashboard.model.config

data class CheckBoxConfig(
    val id: String,
    val label: String,
    val value: Boolean,
    val onUpdated: (Boolean) -> Unit,
) : ElementConfig