package io.github.openflocon.navigation

import androidx.navigation3.runtime.NavKey

interface FloconRoute : NavKey

interface PanelRoute : NavKey {
    /**
     * The group of panels sharing a pin state, see [io.github.openflocon.navigation.scene.PanelPinRegistry]:
     * null for a panel that is not pinned by a group
     */
    val pinGroup: String? get() = null
}

object PanelPinGroups {
    const val Timeline = "timeline"
}

interface WindowRoute : NavKey {
    val singleTopKey: String?
}
