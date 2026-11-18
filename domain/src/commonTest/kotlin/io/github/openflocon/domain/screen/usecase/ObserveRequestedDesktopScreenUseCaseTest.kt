package io.github.openflocon.domain.screen.usecase

import io.github.openflocon.domain.screen.models.FloconScreen
import io.github.openflocon.domain.screen.models.ScreenRequestDomainModel
import io.github.openflocon.domain.screen.repository.DesktopNavigationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveRequestedDesktopScreenUseCaseTest {

    private class FakeRepository(
        private val requests: List<ScreenRequestDomainModel>,
    ) : DesktopNavigationRepository {
        val consumed = mutableListOf<ScreenRequestDomainModel>()

        override fun requestScreen(screen: FloconScreen) = Unit

        override fun observeRequestedScreen(): Flow<ScreenRequestDomainModel> = flowOf(*requests.toTypedArray())

        override fun consumeRequestedScreen(request: ScreenRequestDomainModel) {
            consumed += request
        }
    }

    @Test
    fun `the requested screen is delivered and its request consumed`() = runBlocking {
        val request = ScreenRequestDomainModel(FloconScreen.Timeline, requestedAtMs = 1L)
        val repository = FakeRepository(listOf(request))

        val screen = ObserveRequestedDesktopScreenUseCase(repository)().first()

        assertEquals(FloconScreen.Timeline, screen)
        assertEquals(listOf(request), repository.consumed)
    }
}
