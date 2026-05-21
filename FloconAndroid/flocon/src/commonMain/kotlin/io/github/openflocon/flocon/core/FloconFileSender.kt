package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.model.FloconFileInfo

interface FloconFileSender {

    @FloconMarker
    fun send(file: FloconFile, infos: FloconFileInfo)

}