package io.github.openflocon.flocondesktop.core.data.settings

import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.flocondesktop.core.data.settings.datasource.local.SettingsDataSourcePrefs
import kotlin.test.Test
import kotlin.test.assertEquals

class ParseSavedServerPortsTest {

    @Test
    fun `saved ports are parsed with the defaults as fallback`() {
        assertEquals(ServerPortsDomainModel(9300, 9301), SettingsDataSourcePrefs.parseServerPorts("9300:9301"))
        assertEquals(ServerPortsDomainModel(), SettingsDataSourcePrefs.parseServerPorts(null))
        assertEquals(ServerPortsDomainModel(), SettingsDataSourcePrefs.parseServerPorts("garbage"))
    }
}
