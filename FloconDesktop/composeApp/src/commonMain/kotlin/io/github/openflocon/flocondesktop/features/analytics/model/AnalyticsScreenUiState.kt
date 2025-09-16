package io.github.openflocon.flocondesktop.features.analytics.model

import androidx.compose.runtime.Immutable

@Immutable
data class AnalyticsScreenUiState(
    val invertList: Boolean,
    val autoScroll: Boolean
)

fun previewAnalyticsScreenUiState() = AnalyticsScreenUiState(
    invertList = false,
    autoScroll = false
)
