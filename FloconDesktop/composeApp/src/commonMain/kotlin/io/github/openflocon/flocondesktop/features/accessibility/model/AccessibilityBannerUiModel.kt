package io.github.openflocon.flocondesktop.features.accessibility.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

/**
 * What the device says about its assistive technologies, to explain why events may be missing
 */
@Immutable
sealed interface AccessibilityBannerUiModel {
    /** the platform cannot capture the events, the app has to log them */
    @Immutable
    data object NotSupported : AccessibilityBannerUiModel

    /** no screen reader is running */
    @Immutable
    data object NotEnabled : AccessibilityBannerUiModel

    @Immutable
    data class Running(
        val services: ImmutableList<String>,
        val touchExploration: Boolean,
    ) : AccessibilityBannerUiModel
}
