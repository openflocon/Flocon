package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.database.models.DeviceDataBaseId

@Immutable
data class DeviceSharedPrefUiModel(
    val id: DeviceDataBaseId,
    val name: String,
    val type: String?,
    val typeKind: PreferenceTypeUiModel,
)

fun previewDeviceSharedPrefUiModel(id: String = "id") = DeviceSharedPrefUiModel(
    id = id,
    name = "sharedPref_$id",
    type = "SharedPreferences",
    typeKind = PreferenceTypeUiModel.SharedPreferences,
)
