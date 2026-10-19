package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel
import java.io.File

internal actual fun fileDataSource(context: FloconContext): FileDataSource = JavaIoFileDataSource(
    defaultRoots = {
        listOf(
            FileRootDataModel(id = "workingDir", name = "Working directory", path = systemFolder("user.dir")),
            FileRootDataModel(id = "temp", name = "Temp", path = systemFolder("java.io.tmpdir")),
            FileRootDataModel(id = "home", name = "Home", path = systemFolder("user.home")),
        )
    }
)

private fun systemFolder(property: String): String = File(System.getProperty(property)).absolutePath
