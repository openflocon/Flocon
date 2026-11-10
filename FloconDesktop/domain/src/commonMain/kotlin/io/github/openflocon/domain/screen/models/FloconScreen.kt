package io.github.openflocon.domain.screen.models

/**
 * The screens of the Flocon desktop app that can be requested from outside the UI (ex: the MCP).
 * The settings are not part of it.
 */
enum class FloconScreen {
    Dashboard,
    Network,
    Images,
    Database,
    Files,
    SharedPreferences,
    Analytics,
    FeatureFlags,
    Accessibility,
    Tables,
    Deeplinks,
    AdbCommander,
    CrashReporter,
    Timeline,
    WebView,
}
