package io.github.openflocon.flocon.core

import io.github.openflocon.flocon.model.FloconFileInfo
import java.io.File

interface FloconFileSender {
    fun send(file: File, infos: FloconFileInfo)
}