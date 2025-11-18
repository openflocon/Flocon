package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconApp
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference

fun floconRegisterPreference(preference: FloconPreference) {
    FloconApp.instance?.client?.preferencesPlugin?.register(preference)
}

interface FloconPreferencesPlugin {
    fun register(preference: FloconPreference)
}