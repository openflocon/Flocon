package io.github.openflocon.flocondesktop.mock

import com.flocon.data.remote.mock.MockScenarios
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MockLaunchOptionTest {

    @Test
    fun `no flag is None`() {
        assertEquals(MockLaunchOption.None, MockLaunchOption.parse(listOf("--websocket-port=1")))
    }

    @Test
    fun `known name is a Scenario`() {
        assertEquals(
            MockLaunchOption.Scenario(MockScenarios.byName("default")!!),
            MockLaunchOption.parse(listOf("--websocket-port=1", "--mock=default")),
        )
    }

    @Test
    fun `unknown name lists the available scenarios`() {
        assertEquals(
            MockLaunchOption.UnknownScenario("nope", MockScenarios.all.map { it.name }),
            MockLaunchOption.parse(listOf("--mock=nope")),
        )
    }

    @Test
    fun `mock without a value or list lists the scenarios`() {
        assertEquals(MockLaunchOption.ListScenarios, MockLaunchOption.parse(listOf("--mock")))
        assertEquals(MockLaunchOption.ListScenarios, MockLaunchOption.parse(listOf("--mock=list")))
        assertEquals(MockLaunchOption.ListScenarios, MockLaunchOption.parse(listOf("--mock=")))
    }

    @Test
    fun `resetMockDatabase deletes db, wal and shm`() {
        val folder = Files.createTempDirectory("flocon-mock").toFile()
        val db = File(folder, "mock/flocon_room.db")
        db.parentFile.mkdirs()
        val files = listOf(db, File("${db.path}-wal"), File("${db.path}-shm"))
        files.forEach { it.writeText("x") }

        resetMockDatabase(db)

        files.forEach { assertFalse(it.exists(), "${it.name} still exists") }
        assertTrue(db.parentFile.exists())
        folder.deleteRecursively()
    }

    @Test
    fun `resetMockDatabase creates the folder`() {
        val folder = Files.createTempDirectory("flocon-mock").toFile()
        val db = File(folder, "mock/flocon_room.db")

        resetMockDatabase(db)

        assertTrue(db.parentFile.exists())
        folder.deleteRecursively()
    }
}
