package io.github.openflocon.flocondesktop.features.files

import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.flocondesktop.features.files.mapper.extensionLabelOf
import io.github.openflocon.flocondesktop.features.files.mapper.extensionOf
import io.github.openflocon.flocondesktop.features.files.mapper.fileTypeOf
import io.github.openflocon.flocondesktop.features.files.mapper.shortDevicePath
import io.github.openflocon.flocondesktop.features.files.mapper.toUi
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class FileUiMapperTest {

    @Test
    fun `device path drops the folder shared by all the roots of the app`() {
        val android = listOf(
            "/data/user/0/com.app/files",
            "/data/user/0/com.app/cache",
            "/data/user/0/com.app/shared_prefs",
        )
        assertEquals("…/files/datastore", shortDevicePath("/data/user/0/com.app/files/datastore", android))

        val container = "/Users/me/Library/Developer/CoreSimulator/Devices/D9/data/Containers/Data/Application/86BA"
        val ios = listOf("$container/Documents", "$container/Library/Caches", container)
        assertEquals("…/Library/Application Support", shortDevicePath("$container/Library/Application Support", ios))
        assertEquals("…/86BA", shortDevicePath(container, ios))
    }

    @Test
    fun `device path is kept when it can not be shortened`() {
        assertEquals("/other/place", shortDevicePath("/other/place", listOf("/data/a", "/data/b")))
        assertEquals("/a/b", shortDevicePath("/a/b", listOf("/a", "/b")))
        assertEquals("/a/b", shortDevicePath("/a/b", emptyList()))
        assertEquals("…/files/x", shortDevicePath("/data/app/files/x", listOf("/data/app/files")))
    }

    @Test
    fun `extension is the part after the last dot, none for hidden or extension less files`() {
        assertEquals("json", extensionOf("flocon_network_mocks.JSON"))
        assertEquals("preferences_pb", extensionOf("user_settings.preferences_pb"))
        assertNull(extensionOf("profileInstalled"))
        assertNull(extensionOf(".hidden"))
        assertNull(extensionOf("trailing."))
    }

    @Test
    fun `type and label are derived from the extension`() {
        assertEquals(FileTypeUiModel.Folder, fileTypeOf("datastore.json", isDirectory = true))
        assertEquals(FileTypeUiModel.Json, fileTypeOf("mocks.json", isDirectory = false))
        assertEquals(FileTypeUiModel.Proto, fileTypeOf("settings.preferences_pb", isDirectory = false))
        assertEquals(FileTypeUiModel.Database, fileTypeOf("app.db-wal", isDirectory = false))
        assertEquals(FileTypeUiModel.Image, fileTypeOf("avatar.png", isDirectory = false))
        assertEquals(FileTypeUiModel.Text, fileTypeOf("prefs.xml", isDirectory = false))
        assertEquals(FileTypeUiModel.Other, fileTypeOf("profileInstalled", isDirectory = false))

        assertEquals("{ }", extensionLabelOf("mocks.json", FileTypeUiModel.Json))
        assertEquals("PB", extensionLabelOf("settings.preferences_pb", FileTypeUiModel.Proto))
        assertEquals("DB", extensionLabelOf("app.sqlite", FileTypeUiModel.Database))
        assertEquals("PNG", extensionLabelOf("avatar.png", FileTypeUiModel.Image))
        assertEquals("PROP", extensionLabelOf("a.properties", FileTypeUiModel.Text))
        assertEquals("FILE", extensionLabelOf("profileInstalled", FileTypeUiModel.Other))
        assertEquals("", extensionLabelOf("folder", FileTypeUiModel.Folder))
    }

    @Test
    fun `size bars are the share of the total size of the displayed entries`() {
        val files = listOf(
            file("big.bin", size = 750),
            file("small.bin", size = 250),
            file("folder", size = 1_000, isDirectory = true),
        )

        val withoutFolderSizes = files.toUi(withFoldersSize = false)
        assertEquals(0.75f, withoutFolderSizes[0].sizeRatio)
        assertEquals("75%", withoutFolderSizes[0].sizeShareFormatted)
        assertEquals(0.25f, withoutFolderSizes[1].sizeRatio)
        assertNull(withoutFolderSizes[2].sizeRatio)
        assertNull(withoutFolderSizes[2].sizeFormatted)

        val withFolderSizes = files.toUi(withFoldersSize = true)
        assertEquals(0.375f, withFolderSizes[0].sizeRatio)
        assertEquals(0.5f, withFolderSizes[2].sizeRatio)
        assertEquals("50%", withFolderSizes[2].sizeShareFormatted)
    }

    @Test
    fun `tiny files still show a bit of bar, empty ones none`() {
        val ui = listOf(
            file("big.bin", size = 1_000_000),
            file("tiny", size = 1),
            file("empty", size = 0),
        ).toUi(withFoldersSize = false)
        assertEquals(0.02f, ui[1].sizeRatio)
        assertEquals("<1%", ui[1].sizeShareFormatted)
        assertEquals(0f, ui[2].sizeRatio)
        assertEquals("0%", ui[2].sizeShareFormatted)
    }

    private fun file(name: String, size: Long, isDirectory: Boolean = false) = FileDomainModel(
        name = name,
        isDirectory = isDirectory,
        path = FilePathDomainModel.Real("/data/$name"),
        size = size,
        lastModified = Instant.fromEpochMilliseconds(0),
    )
}
