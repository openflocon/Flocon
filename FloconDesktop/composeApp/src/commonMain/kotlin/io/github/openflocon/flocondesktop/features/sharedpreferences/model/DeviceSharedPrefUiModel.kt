package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.domain.database.models.DeviceDataBaseId

@Immutable
data class DeviceSharedPrefUiModel(
    val id: DeviceDataBaseId,
    val name: String,
    val type: String?,
    // depends on the type, generic for a custom or unknown one
    val typeIcon: ImageVector,
)

fun previewDeviceSharedPrefUiModel(id: String = "id") = DeviceSharedPrefUiModel(
    id = id,
    name = "sharedPref_$id",
    type = "SharedPreferences",
    typeIcon = Icons.Outlined.Android,
)
