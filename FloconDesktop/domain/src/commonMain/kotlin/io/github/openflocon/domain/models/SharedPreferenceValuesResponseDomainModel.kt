package io.github.openflocon.domain.models

data class SharedPreferenceValuesResponseDomainModel(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<SharedPreferenceRowDomainModel>,
)
