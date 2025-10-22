package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.model.FloconFileInfo

internal interface FloconFileSender {
    fun send(file: FloconFile, infos: FloconFileInfo)
}