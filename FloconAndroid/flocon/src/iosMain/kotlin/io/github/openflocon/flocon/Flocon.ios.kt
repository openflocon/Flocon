package io.github.openflocon.flocon

object Flocon : FloconCore() {
    fun initialize() {
        super.initializeFlocon(context = FloconContext())
    }
}

