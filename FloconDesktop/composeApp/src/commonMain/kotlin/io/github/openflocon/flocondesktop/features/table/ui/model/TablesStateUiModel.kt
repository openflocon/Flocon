package io.github.openflocon.flocondesktop.features.table.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface TablesStateUiModel {
    @Immutable
    data object Empty : TablesStateUiModel

    @Immutable
    data object Loading : TablesStateUiModel

    @Immutable
    data class WithContent(
        val tables: List<DeviceTableUiModel>,
        val selected: DeviceTableUiModel,
    ) : TablesStateUiModel
}

fun previewTablesStateUiModel() = TablesStateUiModel.WithContent(
    tables = listOf(
        previewDeviceTableUiModel(),
        previewDeviceTableUiModel(),
        previewDeviceTableUiModel(),
    ),
    selected = previewDeviceTableUiModel(),
)
