package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconSharedPreferenceModel

internal actual fun buildFloconSharedPreferenceDataSource(context: FloconContext): FloconSharedPreferenceDataSource = object : FloconSharedPreferenceDataSource {
    override fun getSharedPreferences(): List<FloconSharedPreferenceModel> = emptyList()
    override fun getSharedPreferenceValue(fileName: String, key: String): String? = null
    override fun setSharedPreferenceValue(fileName: String, key: String, value: String) {}
}
