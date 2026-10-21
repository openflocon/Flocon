package io.github.openflocon.flocondesktop.features.accessibility.model

sealed interface AccessibilityAction {
    data class OnClick(val item: AccessibilityRowUiModel) : AccessibilityAction
}
