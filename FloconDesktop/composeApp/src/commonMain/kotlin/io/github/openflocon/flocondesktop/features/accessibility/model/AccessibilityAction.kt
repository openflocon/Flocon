package io.github.openflocon.flocondesktop.features.accessibility.model

sealed interface AccessibilityAction {
    data class OnClick(val item: AccessibilityRowUiModel) : AccessibilityAction
    data object OnFocusNextClicked : AccessibilityAction
    data object OnFocusPreviousClicked : AccessibilityAction
    data class OnTalkBackToggled(val enabled: Boolean) : AccessibilityAction
    data object OnRefreshFocusedNodeClicked : AccessibilityAction
}
