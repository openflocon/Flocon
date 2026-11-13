package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel

/**
 * The root folders the desktop can access: every path it lists, downloads or deletes must be one of them or inside one.
 *
 * @param canonicalPath resolves the symbolic links of an absolute path, so a link cannot lead outside the roots.
 */
internal class FileRoots(
    val roots: List<FileRootDataModel>,
    private val canonicalPath: (String) -> String,
) {

    private val canonicalRootPaths: List<String?> by lazy {
        roots.map { canonical(it.path) }
    }

    /**
     * @param path the id of a root folder if [isConstantPath], an absolute path otherwise.
     * @return the absolute path, or null if it is not a root folder or inside one.
     */
    fun resolve(path: String, isConstantPath: Boolean): String? {
        if (isConstantPath) {
            return roots.firstOrNull { it.id == path }?.path
        }
        return path.takeIf { contains(it) }
    }

    /** Whether [path] is a root folder or inside one. */
    fun contains(path: String): Boolean {
        val candidate = canonical(path) ?: return false
        return canonicalRootPaths.any { root ->
            root != null && (candidate == root || candidate.startsWith(root.withTrailingSlash()))
        }
    }

    /** Whether [path] is a root folder itself, which is never deleted. */
    fun isRoot(path: String): Boolean {
        val candidate = canonical(path) ?: return false
        return candidate in canonicalRootPaths
    }

    /** Whether [path] can be deleted: inside a root folder, but not a root folder. */
    fun canDelete(path: String): Boolean = contains(path) && !isRoot(path)

    private fun canonical(path: String): String? {
        val normalized = normalizePath(path) ?: return null
        return try {
            normalizePath(canonicalPath(normalized))
        } catch (t: Throwable) {
            null
        }
    }

    companion object {
        /**
         * The default roots, then the registered ones.
         * A registered folder at the location of a default one replaces it, to rename it.
         */
        fun build(
            defaultRoots: List<FileRootDataModel>,
            registeredRoots: List<FileRootDataModel>,
            canonicalPath: (String) -> String,
        ): FileRoots {
            val registered = FileRoots(registeredRoots, canonicalPath)
            val defaults = defaultRoots.filterNot { registered.isRoot(it.path) }
            return FileRoots(defaults + registeredRoots, canonicalPath)
        }
    }
}

/**
 * Removes the `.`, `..` and empty segments of an absolute path.
 * @return null for a relative path, or one that goes above `/`.
 */
internal fun normalizePath(path: String): String? {
    if (!path.startsWith("/")) return null
    val segments = ArrayDeque<String>()
    path.split("/").forEach { segment ->
        when (segment) {
            "", "." -> Unit
            ".." -> segments.removeLastOrNull() ?: return null
            else -> segments.addLast(segment)
        }
    }
    return segments.joinToString(separator = "/", prefix = "/")
}

private fun String.withTrailingSlash(): String = if (endsWith("/")) this else "$this/"
