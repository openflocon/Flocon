package com.flocon.library.domain.models

data class SharedPreferenceValuesResponseDomainModel(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<SharedPreferenceRowDomainModel>,
)
