package io.github.openflocon.flocondesktop.menu.ui.settings

sealed interface SettingsAction {

    data class FontSizeMultiplierChange(val value: Float) : SettingsAction

}
