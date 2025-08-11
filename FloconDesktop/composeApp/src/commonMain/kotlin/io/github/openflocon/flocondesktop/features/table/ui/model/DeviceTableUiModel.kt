package io.github.openflocon.flocondesktop.features.table.ui.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.models.TableId

@Immutable
data class DeviceTableUiModel(
    val id: TableId,
    val name: String,
)

fun previewDeviceTableUiModel() = DeviceTableUiModel(
    id = 0,
    name = "name",
)
