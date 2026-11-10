package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DeeplinkScreenAction {
    data class Submit(
        val item: DeeplinkViewState,
        val values: Map<DeeplinkPart.TextField, String>,
    ) : DeeplinkScreenAction

    data object SubmitFreeform : DeeplinkScreenAction

    data class SetFreeformLink(val value: String) : DeeplinkScreenAction

    data class RemoveFromHistory(val item: DeeplinkViewState) : DeeplinkScreenAction

    data class SetVariable(val name: String, val value: String) : DeeplinkScreenAction

    data class OpenTab(val tab: DeeplinkTabUiModel) : DeeplinkScreenAction

    data class SelectTab(val id: String) : DeeplinkScreenAction

    data class CloseTab(val id: String) : DeeplinkScreenAction
}
