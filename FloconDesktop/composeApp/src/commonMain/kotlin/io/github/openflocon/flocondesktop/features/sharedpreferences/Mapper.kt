package io.github.openflocon.flocondesktop.features.sharedpreferences

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
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
    typeIcon = preferenceTypeIcon(type),
)

// the type is a free value sent by the app: known storages get their own icon, the others a generic one
private fun preferenceTypeIcon(type: String?): ImageVector = when (type?.lowercase()) {
    "sharedpreferences" -> Icons.Outlined.Android
    "datastore" -> Icons.Outlined.Storage
    "userdefaults" -> Icons.Outlined.PhoneIphone
    "keychain" -> Icons.Outlined.Key
    "java preferences" -> Icons.Outlined.Coffee
    "localstorage" -> Icons.Outlined.Language
    else -> Icons.Outlined.Tune
}
