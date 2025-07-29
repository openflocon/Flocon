package com.florent37.flocondesktop.features.database.ui.model

sealed interface DatabasesStateUiModel {
    data object Loading : DatabasesStateUiModel

    data object Empty : DatabasesStateUiModel

    data class WithContent(
        val databases: List<DeviceDataBaseUiModel>,
        val selected: DeviceDataBaseUiModel,
    ) : DatabasesStateUiModel
}

fun previewDatabasesStateUiModel(): DatabasesStateUiModel = DatabasesStateUiModel.WithContent(
    databases =
    listOf(
        previewDeviceDataBaseUiModel(id = "id1"),
        previewDeviceDataBaseUiModel(id = "id2"),
        previewDeviceDataBaseUiModel(id = "id3"),
    ),
    selected = previewDeviceDataBaseUiModel(id = "id1"),
)
