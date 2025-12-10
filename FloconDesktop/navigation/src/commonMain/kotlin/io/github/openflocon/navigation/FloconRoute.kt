package io.github.openflocon.navigation

import androidx.navigation3.runtime.NavKey

interface FloconRoute : NavKey

interface PanelRoute : NavKey

interface WindowRoute : NavKey {
    val singleTopKey: String?
}
