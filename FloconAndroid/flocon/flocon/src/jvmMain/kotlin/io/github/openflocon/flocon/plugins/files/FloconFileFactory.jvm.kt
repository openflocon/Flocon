package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconFile
import java.io.File

internal actual fun floconFileOf(file: File): FloconFile = FloconFile(file)
