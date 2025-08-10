package io.github.openflocon.flocon.plugins.deeplinks

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.plugins.deeplinks.model.Deeplink

fun FloconApp.deeplinks(deeplinks: List<Deeplink>) {
    this.client?.deeplinksPlugin?.registerDeeplinks(deeplinks)
}

interface FloconDeeplinksPlugin : FloconPlugin {
    fun registerDeeplinks(deeplinks: List<Deeplink>)
}