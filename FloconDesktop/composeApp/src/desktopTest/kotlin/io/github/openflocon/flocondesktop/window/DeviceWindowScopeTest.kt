package io.github.openflocon.flocondesktop.window

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.usecase.CurrentDeviceIdProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DeviceWindowScopeTest {

    private val koin: Koin = koinApplication {
        modules(
            module {
                single<CurrentDeviceIdProvider> {
                    object : CurrentDeviceIdProvider {
                        override fun observe(): Flow<DeviceId?> = flowOf("selected")
                    }
                }
            },
            deviceWindowModule,
        )
    }.koin

    @AfterTest
    fun tearDown() = koin.close()

    private fun Koin.deviceWindow(deviceId: String) = createScope(scopeId = "device-window-$deviceId", qualifier = DeviceWindowScope)
        .also { it.declare(DeviceWindowTarget(deviceId = deviceId)) }

    @Test
    fun `the root follows the selected device`() = runBlocking {
        assertEquals("selected", koin.get<CurrentDeviceIdProvider>().observe().first())
    }

    @Test
    fun `a device window is pinned to its device`() = runBlocking {
        val scope = koin.deviceWindow("A")

        assertEquals("A", scope.get<CurrentDeviceIdProvider>().observe().first())
        // the root is not changed by the window
        assertEquals("selected", koin.get<CurrentDeviceIdProvider>().observe().first())
    }

    @Test
    fun `two device windows do not share their device`() = runBlocking {
        val first = koin.deviceWindow("A")
        val second = koin.deviceWindow("B")

        assertNotEquals(
            first.get<CurrentDeviceIdProvider>().observe().first(),
            second.get<CurrentDeviceIdProvider>().observe().first(),
        )
    }
}
