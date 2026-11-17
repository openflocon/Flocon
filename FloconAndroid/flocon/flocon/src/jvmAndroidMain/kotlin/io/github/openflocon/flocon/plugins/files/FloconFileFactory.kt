package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconFile
import java.io.File

// FloconFile is an `expect` class here (its constructor is only visible per platform): this builds it
// from a java.io.File, for the code shared by Android and the JVM.
internal expect fun floconFileOf(file: File): FloconFile
