package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import kotlin.test.Test
import kotlin.test.assertEquals

class BonjourDataModelTest {

    private class FakePlugin(override val key: String) : FloconPlugin {
        override suspend fun onMessageReceived(method: String, body: String) {}
        override suspend fun onConnectedToServer() {}
    }

    @Test
    fun `bonjour lists the installed plugins in lowercase, without empty or duplicated keys`() {
        val plugins = listOf(
            FakePlugin(Protocol.ToDevice.Network.Plugin),
            FakePlugin(Protocol.ToDevice.SharedPreferences.Plugin),
            FakePlugin(Protocol.ToDevice.Table.Plugin),
            FakePlugin(Protocol.FromDevice.Deeplink.Plugin),
            FakePlugin(""),
            FakePlugin("NETWORK"),
        )

        assertEquals(
            "{\"plugins\":[\"network\",\"shared_preferences\",\"table\",\"deeplink\"],\"connectionType\":\"default\"}",
            plugins.toBonjourBody(FloconEncoder(), FloconConnectionType.Default),
        )
    }

    @Test
    fun `bonjour tells when the connection is made through the connect module`() {
        assertEquals(
            "{\"plugins\":[\"network\"],\"connectionType\":\"wifi\"}",
            listOf(FakePlugin(Protocol.ToDevice.Network.Plugin)).toBonjourBody(FloconEncoder(), FloconConnectionType.Wifi),
        )
    }
}
