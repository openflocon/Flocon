package io.github.openflocon.flocondesktop.messages.ui

import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.domain.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.domain.common.ui.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.messages.domain.HandleIncomingMessagesUseCase
import io.github.openflocon.flocondesktop.messages.domain.StartServerUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MessagesServerDelegate(
    private val startServerUseCase: StartServerUseCase,
    private val handleIncomingMessagesUseCase: HandleIncomingMessagesUseCase,
    private val closeableDelegate: CloseableDelegate,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val dispatcherProvider: DispatcherProvider,
) : CloseableScoped by closeableDelegate {

    fun initialize() {
        coroutineScope.launch {
            handleIncomingMessagesUseCase()
                .collect()
        }

        // try to start the server
        // if fails -> try again in 3s
        // if success, just re-check again in 20s if it's still alive
        coroutineScope.launch {
            while (isActive) {
                startServer().fold(
                    doOnSuccess = {
                        delay(20.seconds)
                    },
                    doOnFailure = {
                        delay(3.seconds)
                    },
                )
            }
        }
    }

    private fun startServer(): Either<Throwable, Unit> = try {
        startServerUseCase()
        Success(Unit)
    } catch (t: Throwable) {
        feedbackDisplayer.displayMessage(
            buildString {
                append("Cannot start server on port ${Constant.SERVER_PORT}")
                t.message?.let {
                    append(" : ")
                    append(it)
                }
            },
            type = FeedbackDisplayer.MessageType.Error,
        )
        Failure(t)
    }
}
