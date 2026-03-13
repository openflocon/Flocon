package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.FloconFileDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.DeviceDataBaseDataModel
import io.github.openflocon.flocon.dsl.FloconMarker
import java.io.File

@OptIn(markerClass = [FloconMarker::class])
internal actual class FloconRoomDatabaseProviderImpl actual constructor(
    private val context: FloconContext,
    paths: List<String>
) : FloconRoomDatabaseProvider {

    actual override fun register() {
        TODO("Not yet implemented")
    }

    @FloconMarker
    actual override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> {
        val databasesDir = context.context.getDatabasePath("dummy_db")
            .parentFile
            ?: return emptyList()

        val foundDatabases = mutableListOf<FloconDatabaseModel>()
        // Start the recursive search from the base databases directory
        scanDirectoryForDatabases(
            directory = databasesDir,
            depth = 0,
            foundDatabases = foundDatabases
        )

        return foundDatabases
    }

    private fun scanDirectoryForDatabases(
        directory: File,
        depth: Int,
        foundDatabases: MutableList<FloconDatabaseModel>
    ) {
        if (depth >= MAX_DEPTH) {
            return
        }
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                // If it's a directory, recursively call this function
                scanDirectoryForDatabases(
                    directory = file,
                    depth = depth + 1,
                    foundDatabases = foundDatabases,
                )
            } else {
                // If it's a file, check if it's a database file
                if (file.isFile &&
                    !file.name.endsWith("-wal") && // Write-Ahead Log
                    !file.name.endsWith("-shm") && // Shared-Memory
                    !file.name.endsWith("-journal") // Older journaling mode
                ) {
                    foundDatabases.add(
                        FloconFileDatabaseModel(
                            id = file.absolutePath, // Use absolute path for unique ID
                            displayName = file.name,
                            absolutePath = file.absolutePath
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val MAX_DEPTH = 7
    }
}