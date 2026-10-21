package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FileRootsTest {

    private val files = FileRootDataModel(id = "files", name = "Files", path = "/data/app/files")
    private val caches = FileRootDataModel(id = "caches", name = "Caches", path = "/data/app/cache")
    private val exports = FileRootDataModel(id = "/sdcard/exports", name = "Exports", path = "/sdcard/exports")

    // "/data/app/files/link" is a symbolic link to "/etc"
    private val canonicalPath: (String) -> String = { path ->
        if (path == "/data/app/files/link" || path.startsWith("/data/app/files/link/")) {
            "/etc" + path.removePrefix("/data/app/files/link")
        } else {
            path
        }
    }

    private val roots = FileRoots.build(
        defaultRoots = listOf(files, caches),
        registeredRoots = listOf(exports),
        canonicalPath = canonicalPath,
    )

    @Test
    fun resolvesTheIdOfARoot() {
        assertEquals("/data/app/files", roots.resolve("files", isConstantPath = true))
        assertEquals("/data/app/cache", roots.resolve("caches", isConstantPath = true))
        assertEquals("/sdcard/exports", roots.resolve("/sdcard/exports", isConstantPath = true))
    }

    @Test
    fun refusesAnUnknownId() {
        assertNull(roots.resolve("documents", isConstantPath = true))
        assertNull(roots.resolve("/data/app/files", isConstantPath = true))
    }

    @Test
    fun resolvesAPathInsideARoot() {
        assertEquals("/data/app/files/a/b.txt", roots.resolve("/data/app/files/a/b.txt", isConstantPath = false))
        assertEquals("/data/app/files", roots.resolve("/data/app/files", isConstantPath = false))
        assertEquals("/sdcard/exports/x", roots.resolve("/sdcard/exports/x", isConstantPath = false))
    }

    @Test
    fun refusesAPathOutsideTheRoots() {
        assertNull(roots.resolve("/data/app", isConstantPath = false))
        assertNull(roots.resolve("/data/app/files-other/a", isConstantPath = false))
        assertNull(roots.resolve("/data/app/files/../databases/db", isConstantPath = false))
        assertNull(roots.resolve("/data/app/files/link/passwd", isConstantPath = false))
        assertNull(roots.resolve("files/a.txt", isConstantPath = false))
        assertNull(roots.resolve("/../data/app/files", isConstantPath = false))
    }

    @Test
    fun neverDeletesARoot() {
        assertFalse(roots.canDelete("/data/app/files"))
        assertFalse(roots.canDelete("/data/app/files/"))
        assertFalse(roots.canDelete("/data/app/files/a/.."))
        assertFalse(roots.canDelete("/sdcard/exports"))
        assertFalse(roots.canDelete("/data/app"))
        assertFalse(roots.canDelete("/data/app/files/link"))
        assertTrue(roots.canDelete("/data/app/files/a.txt"))
        assertTrue(roots.canDelete("/data/app/cache/a/b"))
    }

    @Test
    fun aRegisteredFolderRenamesTheDefaultOneAtTheSameLocation() {
        val renamed = FileRootDataModel(id = "/data/app/files", name = "My files", path = "/data/app/files")
        val roots = FileRoots.build(
            defaultRoots = listOf(files, caches),
            registeredRoots = listOf(renamed),
            canonicalPath = canonicalPath,
        )
        assertEquals(listOf(caches, renamed), roots.roots)
    }

    @Test
    fun normalizesPaths() {
        assertEquals("/a/c", normalizePath("/a/./b/../c/"))
        assertEquals("/", normalizePath("/"))
        assertNull(normalizePath("a/b"))
        assertNull(normalizePath("/.."))
    }
}
