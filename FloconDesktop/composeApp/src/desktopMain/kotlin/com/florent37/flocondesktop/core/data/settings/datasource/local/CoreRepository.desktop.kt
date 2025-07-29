package com.florent37.flocondesktop.core.data.settings.datasource.local

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

actual fun createSettings(): ObservableSettings {
    return PreferencesSettings(Preferences.userRoot()) // ou Preferences.systemRoot()
}
