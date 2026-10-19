package io.github.openflocon.flocon.database.room3

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.FloconFileDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker
import java.io.File

/**
 * There is no default database folder on the JVM: the databases are found in the folders
 * (or files) given with `path(...)`.
 */
@OptIn(markerClass = [FloconMarker::class])
internal actual class FloconRoom3DatabaseProviderImpl actual constructor(
    private val context: FloconContext,
    private val paths: List<String>
) : FloconRoom3DatabaseProvider {

    actual override fun register() {
        val databases = getAllDataBases(emptyList())
        databases.forEach { Flocon.databasePlugin.register(it) }
    }

    @FloconMarker
    actual override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> {
        return paths
            .flatMap { File(it).walkTopDown().maxDepth(MAX_DEPTH).toList() }
            .filter { it.isFile && isDatabaseFile(it.name) }
            .distinctBy { it.absolutePath }
            .map {
                FloconFileDatabaseModel(
                    id = it.absolutePath,
                    displayName = it.name,
                    absolutePath = it.absolutePath,
                )
            }
    }

    companion object {
        private const val MAX_DEPTH = 7

        internal fun isDatabaseFile(name: String): Boolean =
            DATABASE_EXTENSIONS.any { name.endsWith(it) }

        private val DATABASE_EXTENSIONS = listOf(".db", ".sqlite", ".sqlite3")
    }
}
