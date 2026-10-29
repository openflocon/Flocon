package io.github.openflocon.flocondesktop.features.network.options

import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkItemOptionsTest {

    @Test
    fun `an http call has every option, the url first`() {
        assertEquals(
            listOf(
                NetworkItemOption.CopyUrl,
                NetworkItemOption.CopyCurl,
                NetworkItemOption.CreateMock,
                NetworkItemOption.Replay,
                NetworkItemOption.ShareAsMarkdown,
            ),
            networkItemOptions(httpActions = true),
        )
    }

    @Test
    fun `a call without http request can only be copied as an url`() {
        assertEquals(listOf(NetworkItemOption.CopyUrl), networkItemOptions(httpActions = false))
    }
}
