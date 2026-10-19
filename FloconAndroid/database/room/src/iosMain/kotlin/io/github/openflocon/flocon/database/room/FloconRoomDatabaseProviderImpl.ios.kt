@file:OptIn(ExperimentalForeignApi::class)

package io.github.openflocon.flocon.database.room

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.FloconFileDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.cinterop.BooleanVar
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLibraryDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

/**
 * Looks for the databases in the folders given with `path(...)`.
 * Without any path, the Documents and Library folders of the app are scanned.
 */
@OptIn(markerClass = [FloconMarker::class])
internal actual class FloconRoomDatabaseProviderImpl actual constructor(
    private val context: FloconContext,
    private val paths: List<String>
) : FloconRoomDatabaseProvider {

    private val fileManager = NSFileManager.defaultManager

    actual override fun register() {
        val databases = getAllDataBases(emptyList())
        databases.forEach { Flocon.databasePlugin.register(it) }
    }

    @FloconMarker
    actual override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<FloconDatabaseModel> {
        val roots = paths.ifEmpty {
            listOf(NSDocumentDirectory, NSLibraryDirectory).mapNotNull {
                NSSearchPathForDirectoriesInDomains(it, NSUserDomainMask, true).firstOrNull() as? String
            }
        }
        val found = mutableListOf<String>()
        roots.forEach { scan(it, depth = 0, found = found) }
        return found.distinct().map { path ->
            FloconFileDatabaseModel(
                id = path,
                displayName = path.substringAfterLast('/'),
                absolutePath = path,
            )
        }
    }

    private fun scan(path: String, depth: Int, found: MutableList<String>) {
        if (depth >= MAX_DEPTH) return
        if (isDirectory(path)) {
            fileManager.contentsOfDirectoryAtPath(path, error = null)
                ?.mapNotNull { it as? String }
                ?.forEach { scan("$path/$it", depth + 1, found) }
        } else if (fileManager.fileExistsAtPath(path) && isDatabaseFile(path)) {
            found.add(path)
        }
    }

    private fun isDirectory(path: String): Boolean = memScoped {
        val isDir = alloc<BooleanVar>()
        fileManager.fileExistsAtPath(path, isDirectory = isDir.ptr) && isDir.value
    }

    private fun isDatabaseFile(path: String) = DATABASE_EXTENSIONS.any { path.endsWith(it) }

    companion object {
        private const val MAX_DEPTH = 7
        private val DATABASE_EXTENSIONS = listOf(".db", ".sqlite", ".sqlite3")
    }
}
