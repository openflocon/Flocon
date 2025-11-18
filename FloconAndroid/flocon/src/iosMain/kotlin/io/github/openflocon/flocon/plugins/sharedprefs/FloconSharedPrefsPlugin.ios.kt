package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.PreferenceRowDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceEditSharedPreferenceValueMessage

internal actual fun buildFloconPreferencesDataSource(context: FloconContext): FloconPreferencesDataSource {
    return FloconPreferencesDataSourceIOs()
}

// TODO try to bind with ios storage
internal class FloconPreferencesDataSourceIOs : FloconPreferencesDataSource {
    override fun detectLocalPreferences(): List<FloconPreference> {
        return emptyList()
    }
}