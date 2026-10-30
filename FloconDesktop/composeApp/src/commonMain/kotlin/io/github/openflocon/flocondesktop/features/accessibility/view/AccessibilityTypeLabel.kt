package io.github.openflocon.flocondesktop.features.accessibility.view

import androidx.compose.runtime.Composable
import io.github.openflocon.flocondesktop.features.accessibility.mapper.accessibilityTypeLabelRes
import io.github.openflocon.flocondesktop.features.accessibility.mapper.fallbackTypeLabel
import org.jetbrains.compose.resources.stringResource

/** The readable name of an event type for the list and the filters; the detail keeps the raw type. */
@Composable
internal fun accessibilityTypeLabel(type: String): String = accessibilityTypeLabelRes(type)?.let { stringResource(it) } ?: fallbackTypeLabel(type)
