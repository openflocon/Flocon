package io.github.openflocon.flocondesktop.app.ui.settings

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ServerPortsFormUiModelTest {

    @Test
    fun `nothing to reset with the default ports`() {
        assertFalse(previewServerPortsFormUiModel().canReset)
    }

    @Test
    fun `can reset when the websocket port is not the default one`() {
        assertTrue(previewServerPortsFormUiModel(websocketPort = "10023").canReset)
    }

    @Test
    fun `can reset when the http port is not the default one`() {
        assertTrue(previewServerPortsFormUiModel(httpPort = "10024").canReset)
    }

    @Test
    fun `a port forced by a launch option does not count`() {
        val form = previewServerPortsFormUiModel(websocketPort = "10023", websocketPortOverridden = true)

        assertFalse(form.canReset)
    }

    @Test
    fun `can still reset the other port when one is forced by a launch option`() {
        val form = previewServerPortsFormUiModel(
            websocketPort = "10023",
            websocketPortOverridden = true,
            httpPort = "10024",
        )

        assertTrue(form.canReset)
    }
}
