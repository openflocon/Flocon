package io.github.openflocon.flocondesktop.app.ui.model

sealed interface SubScreen {
    data object Dashboard : SubScreen

    // TODO group network, grpc, networkImages
    data object Network : SubScreen
    data object Images : SubScreen // network images

    // storage
    data object Database : SubScreen
    data object Files : SubScreen // device files (context.cache, context.files)
    data object SharedPreferences : SubScreen

    data object Analytics : SubScreen
    data object Tables : SubScreen

    data object Settings : SubScreen

    data object Deeplinks : SubScreen
    data object CrashReporter : SubScreen

    data object Commands : SubScreen
}

val SubScreen.id: String
    get() {
        return javaClass.simpleName
    }
