package com.florent37.flocondesktop.features.sharedpreferences.domain.model

data class SharedPreferenceValuesResponseDomainModel(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<SharedPreferenceRowDomainModel>,
)
