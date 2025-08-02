package io.github.openflocon.flocondesktop.main.ui.model

enum class SubScreen {
    Dashboard,

    // TODO group network, grpc, networkImages
    Network,
    Images, // network images

    // storage
    Database,
    Files, // device files (context.cache, context.files)
    SharedPreferences,

    Analytics,
    Tables,

    Settings,

    Deeplinks,

    ;

    companion object {
        fun fromId(id: String): SubScreen = SubScreen.valueOf(id)
    }
}

val SubScreen.id: String
    get() {
        return this.name
    }
