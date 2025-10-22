package io.github.openflocon.flocon

object Flocon : FloconCore() {

    fun initialize() {
        super.initializeFlocon(context = FloconContext(
            appName = "Flocon-sample",
            packageName = "io.github.openflocon.flocon",
        ))
    }
}
