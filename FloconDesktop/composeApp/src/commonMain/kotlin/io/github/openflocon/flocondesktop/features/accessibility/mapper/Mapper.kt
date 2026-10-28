package io.github.openflocon.flocondesktop.features.accessibility.mapper

import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.accessibility.models.AccessibilityStateDomainModel
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityBannerUiModel
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityDetailUiModel
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityRowUiModel
import kotlinx.collections.immutable.toImmutableList

internal fun AccessibilityEventDomainModel.mapToUi() = AccessibilityRowUiModel(
    id = id,
    timeFormatted = formatTimestamp(createdAt),
    type = type,
    text = text?.takeIf { it.isNotBlank() } ?: contentDescription.orEmpty(),
    className = className,
    missingLabel = extras["missingLabel"] == "true",
)

internal fun AccessibilityEventDomainModel.mapToDetailUi() = AccessibilityDetailUiModel(
    id = id,
    title = listOf(text, contentDescription, className).firstNotNullOfOrNull { it?.takeIf { value -> value.isNotBlank() } } ?: type,
    type = type,
    timeFormatted = formatTimestamp(createdAt),
    text = text,
    contentDescription = contentDescription,
    className = className,
    screen = screen,
    bounds = bounds,
    extras = extras.toList().sortedBy { it.first }.toImmutableList(),
)

internal fun AccessibilityStateDomainModel.mapToBannerUi(): AccessibilityBannerUiModel = when {
    !supported -> AccessibilityBannerUiModel.NotSupported

    !enabled -> AccessibilityBannerUiModel.NotEnabled

    else -> AccessibilityBannerUiModel.Running(
        services = services.toImmutableList(),
        touchExploration = touchExploration,
    )
}
