package io.github.openflocon.flocondesktop.features.sharedpreferences.domain.model

data class SharedPreferenceValuesResponseDomainModel(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<SharedPreferenceRowDomainModel>,
)
