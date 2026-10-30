package io.github.openflocon.flocondesktop.features.sharedpreferences

import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.PreferenceTypeUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel

internal fun SharedPreferenceRowDomainModel.Value.toUi(): SharedPreferencesRowUiModel.Value = when (this) {
    is SharedPreferenceRowDomainModel.Value.StringValue -> SharedPreferencesRowUiModel.Value.StringValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.IntValue -> SharedPreferencesRowUiModel.Value.IntValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.BooleanValue -> SharedPreferencesRowUiModel.Value.BooleanValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.FloatValue -> SharedPreferencesRowUiModel.Value.FloatValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.LongValue -> SharedPreferencesRowUiModel.Value.LongValue(
        value,
    )

    is SharedPreferenceRowDomainModel.Value.StringSetValue -> SharedPreferencesRowUiModel.Value.StringSetValue(
        value,
    )
}

internal fun DeviceSharedPreferenceDomainModel.toUi(): DeviceSharedPrefUiModel = DeviceSharedPrefUiModel(
    id = id,
    name = name,
    type = type,
    typeKind = preferenceTypeKind(type),
)

// the type is a free value sent by the app: known storages get their own icon, the others a generic one
private fun preferenceTypeKind(type: String?): PreferenceTypeUiModel = when (type?.lowercase()) {
    "sharedpreferences" -> PreferenceTypeUiModel.SharedPreferences
    "datastore" -> PreferenceTypeUiModel.DataStore
    "userdefaults" -> PreferenceTypeUiModel.UserDefaults
    "keychain" -> PreferenceTypeUiModel.Keychain
    "java preferences" -> PreferenceTypeUiModel.JavaPreferences
    "localstorage" -> PreferenceTypeUiModel.LocalStorage
    else -> PreferenceTypeUiModel.Custom
}
