package io.github.openflocon.flocondesktop.common.db

import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MediaFilesTest {

    private val root: File = Files.createTempDirectory("flocon-docs").toFile().also { it.deleteOnExit() }
    private val mediaFiles = MediaFiles { root }

    private fun file(folder: String, name: String, lastModified: Long, size: Int = 10): File {
        val dir = File(root, folder).also { it.mkdirs() }
        return File(dir, name).also {
            it.writeBytes(ByteArray(size))
            it.setLastModified(lastModified)
        }
    }

    @Test
    fun `sizes the screenshots and the recordings`() {
        file("Screenshot", "a.png", 1_000, size = 10)
        file("ScreenRecord", "b.mp4", 1_000, size = 30)
        file("Other", "c.txt", 1_000, size = 500)

        assertEquals(10L, mediaFiles.screenshotsBytes())
        assertEquals(30L, mediaFiles.recordingsBytes())
    }

    @Test
    fun `a missing documents folder is empty`() {
        assertEquals(0L, MediaFiles { File(root, "nope") }.screenshotsBytes())
        assertEquals(0L, MediaFiles { File(root, "nope") }.recordingsBytes())
        MediaFiles { File(root, "nope") }.deleteModifiedBefore(Long.MAX_VALUE)
    }

    @Test
    fun `only the files older than the limit are deleted`() {
        val old = file("Screenshot", "old.png", 1_000)
        val oldRecord = file("ScreenRecord", "old.mp4", 1_000)
        val recent = file("Screenshot", "recent.png", 5_000)

        mediaFiles.deleteModifiedBefore(2_000)

        assertFalse(old.exists())
        assertFalse(oldRecord.exists())
        assertTrue(recent.exists())
    }

    @Test
    fun `files outside the media folders and the folders themselves are kept`() {
        val other = file("Notes", "keep.txt", 1_000)
        val atRoot = File(root, "root.txt").also {
            it.writeText("x")
            it.setLastModified(1_000)
        }
        val sub = File(root, "Screenshot/sub").also { it.mkdirs() }
        val nested = File(sub, "nested.png").also {
            it.writeText("x")
            it.setLastModified(1_000)
        }

        mediaFiles.deleteModifiedBefore(Long.MAX_VALUE)

        assertTrue(other.exists())
        assertTrue(atRoot.exists())
        assertTrue(sub.isDirectory)
        assertTrue(nested.exists())
        assertTrue(File(root, "Screenshot").isDirectory)
    }

    @Test
    fun `a symlink is left alone`() {
        val target = File(root, "Notes").also { it.mkdirs() }.resolve("precious.txt").also { it.writeText("x") }
        val dir = File(root, "Screenshot").also { it.mkdirs() }
        val link = dir.toPath().resolve("link.png")
        try {
            Files.createSymbolicLink(link, target.toPath())
        } catch (e: UnsupportedOperationException) {
            return // no symlink on this file system
        }
        target.setLastModified(1_000)

        mediaFiles.deleteModifiedBefore(Long.MAX_VALUE)

        assertTrue(target.exists())
        assertTrue(Files.exists(link, java.nio.file.LinkOption.NOFOLLOW_LINKS))
    }
}
