package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.sharedprefs.model.SharedPreferencesDescriptor
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.SharedPreferenceRowDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceEditSharedPreferenceValueMessage

internal actual fun buildFloconSharedPreferencesDataSource(context: FloconContext): FloconSharedPreferencesDataSource {
    return FloconSharedPreferencesDataSourceIOs()
}

// TODO try to bind with ios storage
internal class FloconSharedPreferencesDataSourceIOs : FloconSharedPreferencesDataSource {
    override fun getAllSharedPreferences(): List<SharedPreferencesDescriptor> {
        return emptyList()
    }

    override fun getSharedPreferenceContent(sharedPreferencesName: String): List<SharedPreferenceRowDataModel> {
        return emptyList()
    }

    override fun setSharedPreferenceRowValue(
        sharedPreferencesName: String,
        preferenceName: String,
        message: ToDeviceEditSharedPreferenceValueMessage
    ) {
        // TODO
    }

}