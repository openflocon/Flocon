package io.github.openflocon.domain.device.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DevicePluginTest {

    @Test
    fun `everything is installed when the plugins are unknown`() {
        DevicePlugin.entries.forEach {
            assertTrue(it.isInstalled(null), "$it should be displayed for an unknown list")
        }
    }

    @Test
    fun `only the installed plugins are displayed`() {
        val installed = setOf("network", "database", "table")

        assertTrue(DevicePlugin.Network.isInstalled(installed))
        assertTrue(DevicePlugin.Database.isInstalled(installed))
        assertTrue(DevicePlugin.Tables.isInstalled(installed))
        assertFalse(DevicePlugin.Files.isInstalled(installed))
        assertFalse(DevicePlugin.Analytics.isInstalled(installed))
        assertFalse(DevicePlugin.Deeplinks.isInstalled(installed))
    }

    @Test
    fun `feature flags need the feature flags plugin`() {
        assertTrue(DevicePlugin.FeatureFlags.isInstalled(setOf("featureflags")))
        assertFalse(DevicePlugin.FeatureFlags.isInstalled(setOf("network")))
    }

    @Test
    fun `accessibility needs the accessibility plugin`() {
        assertTrue(DevicePlugin.Accessibility.isInstalled(setOf("accessibility")))
        assertFalse(DevicePlugin.Accessibility.isInstalled(setOf("network")))
    }

    @Test
    fun `images are available with the network plugin`() {
        assertTrue(DevicePlugin.Images.isInstalled(setOf("network")))
        assertFalse(DevicePlugin.Images.isInstalled(setOf("database")))
    }

    @Test
    fun `nothing is displayed when the sdk has no plugin`() {
        assertEquals(emptyList(), DevicePlugin.entries.filter { it.isInstalled(emptySet()) })
    }
}
