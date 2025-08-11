package io.github.openflocon.flocondesktop.features.sharedpreferences.ui

import com.flocon.library.domain.models.SharedPreferenceRowDomainModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.ui.model.SharedPreferencesRowUiModel

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
