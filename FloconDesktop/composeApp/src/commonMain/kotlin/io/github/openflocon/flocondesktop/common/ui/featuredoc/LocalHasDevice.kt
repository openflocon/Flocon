package io.github.openflocon.flocondesktop.common.ui.featuredoc

import androidx.compose.runtime.compositionLocalOf

/**
 * False when the selected device is not connected (or no device ever connected): every [FeatureEmptyState]
 * then explains how to connect an app instead of how to set the page up.
 *
 * Provided by the app shell from the devices state, true by default so previews and loading show the page state.
 */
val LocalHasDevice = compositionLocalOf { true }
