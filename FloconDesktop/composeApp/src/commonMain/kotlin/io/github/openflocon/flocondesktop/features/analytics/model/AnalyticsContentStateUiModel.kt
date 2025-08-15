package io.github.openflocon.flocondesktop.features.analytics.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface AnalyticsContentStateUiModel {
    @Immutable
    data object Loading : AnalyticsContentStateUiModel

    @Immutable
    data object Empty : AnalyticsContentStateUiModel

    @Immutable
    data class WithContent(
        val rows: List<AnalyticsRowUiModel>,
    ) : AnalyticsContentStateUiModel
}

fun AnalyticsContentStateUiModel.items(): List<AnalyticsRowUiModel> = when (this) {
    is AnalyticsContentStateUiModel.Empty,
    is AnalyticsContentStateUiModel.Loading,
    -> emptyList()
    is AnalyticsContentStateUiModel.WithContent -> rows
}

fun previewAnalyticsContentStateUiModel(): AnalyticsContentStateUiModel = AnalyticsContentStateUiModel.WithContent(
    rows = listOf(
        previewAnalyticsRowUiModel(),
        previewAnalyticsRowUiModel(),
        previewAnalyticsRowUiModel(),
    ),
)
