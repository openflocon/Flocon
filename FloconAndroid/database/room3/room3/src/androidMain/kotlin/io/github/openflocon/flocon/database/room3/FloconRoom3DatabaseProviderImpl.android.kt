package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.FloconFileDatabaseModel
import java.io.File

internal actual class FloconRoom3DatabaseProviderImpl actual constructor(
    private val context: FloconContext,
    paths: List<String>
) : FloconRoom3DatabaseProvider {

    actual override fun register() {
        val databases = getAllDataBases(emptyList())
        databases.forEach { Flocon.databasePlugin.register(it) }
    }

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
                    !file.name.endsWith("-journal") && // Older journaling mode
                    !file.name.endsWith(".lck") // Lock files (e.g. created by KMP Room)
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