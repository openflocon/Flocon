package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed interface DatabasesStateUiModel {
    data object Loading : DatabasesStateUiModel

    data object Empty : DatabasesStateUiModel

    data class WithContent(
        val databases: ImmutableList<DeviceDataBaseUiModel>,
    ) : DatabasesStateUiModel
}

fun previewDatabasesStateUiModel(): DatabasesStateUiModel = DatabasesStateUiModel.WithContent(
    databases =
    persistentListOf(
        previewDeviceDataBaseUiModel(id = "id1"),
        previewDeviceDataBaseUiModel(id = "id2"),
        previewDeviceDataBaseUiModel(id = "id3"),
    ),
)

fun DatabasesStateUiModel.selectedDatabase(): DeviceDataBaseUiModel? = when (this) {
    DatabasesStateUiModel.Empty,
    DatabasesStateUiModel.Loading -> null
    is DatabasesStateUiModel.WithContent -> {
        this.databases.firstOrNull { it.isSelected }
    }
}
