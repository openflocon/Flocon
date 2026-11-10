package io.github.openflocon.flocondesktop.messages.ui

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.domain.messages.usecase.HandleIncomingMessagesUseCase
import io.github.openflocon.domain.messages.usecase.HandleReceivedFilesUseCase
import io.github.openflocon.domain.messages.usecase.StartServerUseCase
import io.github.openflocon.domain.messages.usecase.StopServerUseCase
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MessagesServerDelegate(
    private val startServerUseCase: StartServerUseCase,
    private val stopServerUseCase: StopServerUseCase,
    private val observeServerPortsUseCase: ObserveServerPortsUseCase,
    private val handleIncomingMessagesUseCase: HandleIncomingMessagesUseCase,
    private val handleReceivedFilesUseCase: HandleReceivedFilesUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val dispatcherProvider: DispatcherProvider,
) : CloseableScoped by closeableDelegate {

    fun initialize() {
        coroutineScope.launch {
            handleIncomingMessagesUseCase()
                .collect()
        }

        coroutineScope.launch {
            handleReceivedFilesUseCase()
                .collect()
        }

        // try to start the server on the configured ports
        // if fails -> try again in 3s
        // if success, just re-check again in 20s if it's still alive
        // when the ports change, the previous block is cancelled (collectLatest), the servers are stopped then started again
        coroutineScope.launch {
            observeServerPortsUseCase().collectLatest { ports ->
                runCatching { stopServerUseCase() }
                var errorDisplayed = false
                while (isActive) {
                    startServer(ports, displayError = !errorDisplayed).fold(
                        doOnSuccess = {
                            delay(20.seconds)
                        },
                        doOnFailure = {
                            // the error is displayed once per ports change, not at each retry
                            errorDisplayed = true
                            delay(3.seconds)
                        },
                    )
                }
            }
        }
    }

    private fun startServer(ports: ServerPortsDomainModel, displayError: Boolean): Either<Throwable, Unit> = try {
        startServerUseCase(
            websocketPort = ports.websocketPort,
            httpPort = ports.httpPort,
        )
        Success(Unit)
    } catch (t: Throwable) {
        if (displayError) {
            feedbackDisplayer.displayMessage(
                buildString {
                    append("Cannot start server on ports ${ports.websocketPort} (websocket) / ${ports.httpPort} (http)")
                    t.message?.let {
                        append(" : ")
                        append(it)
                    }
                },
                type = FeedbackDisplayer.MessageType.Error,
            )
        }
        Failure(t)
    }
}
