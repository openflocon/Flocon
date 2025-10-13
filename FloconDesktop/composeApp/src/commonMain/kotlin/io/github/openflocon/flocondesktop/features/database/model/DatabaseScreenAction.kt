package io.github.openflocon.flocondesktop.features.database.model

import io.github.openflocon.domain.database.models.DeviceDataBaseId

sealed interface DatabaseScreenAction {
    data class OnDatabaseSelected(val id: DeviceDataBaseId) : DatabaseScreenAction
    data class OnDatabaseDoubleClicked(val database: DeviceDataBaseUiModel) : DatabaseScreenAction
    data class OnTableDoubleClicked(val id: DeviceDataBaseId, val table: TableUiModel) : DatabaseScreenAction
    data class OnTableColumnClicked(val column: TableUiModel.ColumnUiModel) : DatabaseScreenAction
    data class OnTabSelected(val tab: DatabaseTabState) :DatabaseScreenAction
    data class OnTabCloseClicked(val tab: DatabaseTabState) : DatabaseScreenAction
    data class OnFavoriteClicked(val favoriteQuery: DatabaseFavoriteQueryUiModel) : DatabaseScreenAction
    data class DeleteFavorite(val favoriteQuery: DatabaseFavoriteQueryUiModel) : DatabaseScreenAction
    data class OnDeleteContentClicked(val databaseId: DeviceDataBaseId, val table: TableUiModel) : DatabaseScreenAction
}
