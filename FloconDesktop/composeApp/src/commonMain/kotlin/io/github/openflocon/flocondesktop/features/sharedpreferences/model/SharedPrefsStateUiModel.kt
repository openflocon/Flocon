package io.github.openflocon.flocondesktop.features.sharedpreferences.model

sealed interface SharedPrefsStateUiModel {
    data object Loading : SharedPrefsStateUiModel

    data object Empty : SharedPrefsStateUiModel

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
