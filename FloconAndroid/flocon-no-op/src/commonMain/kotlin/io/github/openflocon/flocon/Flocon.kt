package io.github.openflocon.flocon

/**
 * No-op version of Flocon: nothing is started, no connection is made to the desktop app.
 */
class Flocon private constructor() {

    fun setConnectionMode(mode: FloconConnectionMode) {
        // no op
    }

    companion object {
        val instance: Flocon = Flocon()
    }
}
