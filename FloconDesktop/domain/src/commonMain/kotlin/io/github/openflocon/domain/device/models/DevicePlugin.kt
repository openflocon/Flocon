package io.github.openflocon.domain.device.models

/**
 * The features of the desktop app that depend on a plugin installed in the SDK of the app.
 * [sdkNames] are the (lowercase) plugin names sent by the SDK in the bonjour message.
 */
enum class DevicePlugin(val sdkNames: Set<String>) {
    Network(setOf("network")),
    // the images come from the network calls
    Images(setOf("network", "images")),
    Database(setOf("database")),
    SharedPreferences(setOf("sharedpreferences")),
    Files(setOf("files")),
    Dashboard(setOf("dashboard")),
    Analytics(setOf("analytics")),
    Tables(setOf("table")),
    CrashReporter(setOf("crashreporter")),
    Deeplinks(setOf("deeplink")),
    FeatureFlags(setOf("featureflags")),
    ;

    /**
     * @param installedPlugins the plugins reported by the SDK, null when unknown: everything is considered installed
     */
    fun isInstalled(installedPlugins: Set<String>?): Boolean =
        installedPlugins == null || sdkNames.any { it in installedPlugins }
}
