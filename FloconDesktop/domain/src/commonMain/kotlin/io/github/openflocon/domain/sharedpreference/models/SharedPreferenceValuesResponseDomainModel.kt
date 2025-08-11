package io.github.openflocon.domain.sharedpreference.models

data class SharedPreferenceValuesResponseDomainModel(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<SharedPreferenceRowDomainModel>,
)
