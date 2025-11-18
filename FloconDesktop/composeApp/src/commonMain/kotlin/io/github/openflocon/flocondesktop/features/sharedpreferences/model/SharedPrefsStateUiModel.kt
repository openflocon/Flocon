package io.github.openflocon.flocondesktop.features.sharedpreferences.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SharedPrefsStateUiModel {
    @Immutable
    data object Loading : SharedPrefsStateUiModel
    @Immutable
    data object Empty : SharedPrefsStateUiModel
    @Immutable
    data class WithContent(
        val preferences: List<DeviceSharedPrefUiModel>,
        val selected: DeviceSharedPrefUiModel,
    ) : SharedPrefsStateUiModel
}

fun previewSharedPrefsStateUiModel(): SharedPrefsStateUiModel = SharedPrefsStateUiModel.WithContent(
    preferences =
    listOf(
        previewDeviceSharedPrefUiModel(id = "id1"),
        previewDeviceSharedPrefUiModel(id = "id2"),
        previewDeviceSharedPrefUiModel(id = "id3"),
    ),
    selected = previewDeviceSharedPrefUiModel(id = "id1"),
)
