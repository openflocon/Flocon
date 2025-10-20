package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.model.FloconFileInfo
import java.io.File

internal interface FloconFileSender {
    fun send(file: File, infos: FloconFileInfo)
}