package io.github.openflocon.flocon.plugins.dashboard.model

sealed interface DashboardCallback {
    val id: String

    data class ButtonCallback(
        override val id: String,
        val action: () -> Unit,
    ) : DashboardCallback

    data class TextFieldCallback(
        override val id: String,
        val action: (String) -> Unit,
    ) : DashboardCallback

    data class CheckBoxCallback(
        override val id: String,
        val action: (Boolean) -> Unit,
    ) : DashboardCallback
}