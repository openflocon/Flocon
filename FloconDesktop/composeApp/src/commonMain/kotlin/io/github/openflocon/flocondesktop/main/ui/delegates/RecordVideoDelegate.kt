package io.github.openflocon.flocondesktop.main.ui.delegates

import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.RecordingDomainModel
import io.github.openflocon.domain.device.usecase.StartRecordingVideoUseCase
import io.github.openflocon.domain.device.usecase.StopRecordingVideoUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableDelegate
import io.github.openflocon.flocondesktop.common.coroutines.closeable.CloseableScoped
import io.github.openflocon.flocondesktop.main.ui.model.RecordVideoStateUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class RecordVideoDelegate(
    private val dispatcherProvider: DispatcherProvider,
    private val feedbackDisplayer: FeedbackDisplayer,
    private val startRecordingVideoUseCase: StartRecordingVideoUseCase,
    private val stopRecordingVideoUseCase: StopRecordingVideoUseCase,
    private val closeableDelegate: CloseableDelegate,
) : CloseableScoped by closeableDelegate {

    private val recording = MutableStateFlow<RecordingDomainModel?>(null)
    val state: StateFlow<RecordVideoStateUiModel> = recording.map {
        it?.let { RecordVideoStateUiModel.Recording } ?: RecordVideoStateUiModel.Idle
    }.stateIn(
        coroutineScope,
        WhileSubscribed(5_000),
        RecordVideoStateUiModel.Idle
    )

    suspend fun toggleRecording() {
        recording.value?.let {
            stopRecording(it)
        } ?: run {
            startRecording()
        }
    }

    private suspend fun stopRecording(record: RecordingDomainModel) {
        stopRecordingVideoUseCase(record).fold(
            doOnFailure = {
                feedbackDisplayer.displayMessage(it.message ?: "Unknown error")
            },
            doOnSuccess = {
                recording.update { null }
            }
        )
    }

    private suspend fun startRecording() {
        startRecordingVideoUseCase().fold(
            doOnFailure = {
                recording.update { null }
                feedbackDisplayer.displayMessage(it.message ?: "Unknown error")
            },
            doOnSuccess = { recordInstance ->
                recording.update { recordInstance }
            }
        )
    }
}
