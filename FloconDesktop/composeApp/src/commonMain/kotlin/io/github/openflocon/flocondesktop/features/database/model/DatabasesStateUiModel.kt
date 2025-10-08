package io.github.openflocon.flocondesktop.features.database.model

sealed interface DatabasesStateUiModel {
    data object Loading : DatabasesStateUiModel

    data object Empty : DatabasesStateUiModel

    data class WithContent(
        val databases: List<DeviceDataBaseUiModel>,
    ) : DatabasesStateUiModel
}

fun previewDatabasesStateUiModel(): DatabasesStateUiModel = DatabasesStateUiModel.WithContent(
    databases =
    listOf(
        previewDeviceDataBaseUiModel(id = "id1"),
        previewDeviceDataBaseUiModel(id = "id2"),
        previewDeviceDataBaseUiModel(id = "id3"),
    ),
)

fun DatabasesStateUiModel.selectedDatabase(): DeviceDataBaseUiModel? {
    return when (this) {
        DatabasesStateUiModel.Empty,
        DatabasesStateUiModel.Loading -> null
        is DatabasesStateUiModel.WithContent -> {
            this.databases.firstOrNull { it.isSelected }
        }
    }
}
