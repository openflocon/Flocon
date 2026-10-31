package io.github.openflocon.flocondesktop.window

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.usecase.CurrentDeviceIdProvider
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.app.MenuNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.koin.core.Koin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class DeviceWindowScopeTest {

    // like the domain: the use cases are factories of the root, built from the provider
    private val koin: Koin = koinApplication {
        modules(
            module {
                single<CurrentDeviceIdProvider> {
                    object : CurrentDeviceIdProvider {
                        override fun observe(): Flow<DeviceId?> = flowOf("selected")
                        override suspend fun get(): DeviceId? = "selected"
                    }
                }
                singleOf(::MenuNavigator)
                factoryOf(::ObserveCurrentDeviceIdUseCase)
                factoryOf(::GetCurrentDeviceIdUseCase)
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
        assertEquals("selected", koin.get<ObserveCurrentDeviceIdUseCase>()().first())
        assertEquals("selected", koin.get<GetCurrentDeviceIdUseCase>()())
    }

    @Test
    fun `the use cases of the root follow the device of the window`() = runBlocking {
        val scope = koin.deviceWindow("A")

        assertEquals("A", scope.get<ObserveCurrentDeviceIdUseCase>()().first())
        assertEquals("A", scope.get<GetCurrentDeviceIdUseCase>()())
        // the root is not changed by the window
        assertEquals("selected", koin.get<ObserveCurrentDeviceIdUseCase>()().first())
    }

    @Test
    fun `a device window has its own menu requests`() {
        val first = koin.deviceWindow("A")
        val second = koin.deviceWindow("B")

        assertNotSame(koin.get<MenuNavigator>(), first.get<MenuNavigator>())
        assertNotSame(first.get<MenuNavigator>(), second.get<MenuNavigator>())
        assertSame(first.get<MenuNavigator>(), first.get<MenuNavigator>())
    }

    @Test
    fun `two device windows do not share their device`() = runBlocking {
        val first = koin.deviceWindow("A")
        val second = koin.deviceWindow("B")

        assertEquals("A", first.get<ObserveCurrentDeviceIdUseCase>()().first())
        assertEquals("B", second.get<ObserveCurrentDeviceIdUseCase>()().first())
    }
}
