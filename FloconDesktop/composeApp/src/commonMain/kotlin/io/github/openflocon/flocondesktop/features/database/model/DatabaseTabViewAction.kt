package io.github.openflocon.flocondesktop.features.database.model

sealed interface DatabaseTabViewAction {
    data class OnTabSelected(val tab: DatabaseTabState) : DatabaseTabViewAction
    data class OnCloseClicked(val tab: DatabaseTabState) : DatabaseTabViewAction
    data class OnCloseOtherClicked(val tab: DatabaseTabState) : DatabaseTabViewAction
    data class OnCloseAllClicked(val tab: DatabaseTabState) : DatabaseTabViewAction
    data class OnCloseOnRightClicked(val tab: DatabaseTabState) : DatabaseTabViewAction
    data class OnCloseOnLeftClicked(val tab: DatabaseTabState) : DatabaseTabViewAction
}
