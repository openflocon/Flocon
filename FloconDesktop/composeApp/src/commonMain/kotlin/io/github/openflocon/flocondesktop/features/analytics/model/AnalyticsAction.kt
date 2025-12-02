package io.github.openflocon.flocondesktop.features.analytics.model

sealed interface AnalyticsAction {
    data class OnClick(
        val item: AnalyticsRowUiModel
    ) : AnalyticsAction

    data class Remove(
        val item: AnalyticsRowUiModel
    ) : AnalyticsAction

    data class RemoveLinesAbove(
        val item: AnalyticsRowUiModel
    ) : AnalyticsAction

    data object ClosePanel : AnalyticsAction

    data object ClearOldSession : AnalyticsAction

    data object ToggleAutoScroll : AnalyticsAction

    data class InvertList(val value: Boolean) : AnalyticsAction

    data object ExportCsv : AnalyticsAction
}
