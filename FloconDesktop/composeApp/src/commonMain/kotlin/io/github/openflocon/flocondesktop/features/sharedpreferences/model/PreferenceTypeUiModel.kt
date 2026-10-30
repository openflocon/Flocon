package io.github.openflocon.flocondesktop.features.sharedpreferences.model

// storage of a preference, each kind has its own icon in the selector
enum class PreferenceTypeUiModel {
    SharedPreferences,
    DataStore,
    UserDefaults,
    Keychain,
    JavaPreferences,
    LocalStorage,
    Custom,
}
