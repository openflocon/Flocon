package io.github.openflocon.flocondesktop.mock

import com.flocon.data.remote.mock.MockScenario
import com.flocon.data.remote.mock.MockScenarios
import io.github.openflocon.flocondesktop.common.db.getMockDatabaseFile
import java.io.File

private const val MOCK_OPTION = "--mock"
private const val LIST = "list"

/** The `--mock[=<name>|list]` launch option: starts the app with the data of a [MockScenario]. */
sealed interface MockLaunchOption {
    data object None : MockLaunchOption

    data object ListScenarios : MockLaunchOption

    data class Scenario(val scenario: MockScenario) : MockLaunchOption

    data class UnknownScenario(val name: String, val available: List<String>) : MockLaunchOption

    companion object {
        fun parse(
            args: List<String>,
            scenarios: List<MockScenario> = MockScenarios.all,
        ): MockLaunchOption {
            val arg = args.lastOrNull { it == MOCK_OPTION || it.startsWith("$MOCK_OPTION=") } ?: return None
            val name = arg.substringAfter("=", missingDelimiterValue = "")
            if (name.isEmpty() || name == LIST) return ListScenarios
            return scenarios.firstOrNull { it.name == name }
                ?.let(::Scenario)
                ?: UnknownScenario(name = name, available = scenarios.map { it.name })
        }
    }
}

/** Each mock launch starts from an empty database, so the same scenario always gives the same screens. */
fun resetMockDatabase(file: File = getMockDatabaseFile()) {
    listOf(file, File("${file.path}-wal"), File("${file.path}-shm")).forEach { it.delete() }
    file.parentFile?.mkdirs()
}
