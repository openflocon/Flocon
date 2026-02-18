package io.github.openflocon.domain.adbcommander.usecase

import io.github.openflocon.domain.adb.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.model.AdbCommandTargetDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowExecutionState
import io.github.openflocon.domain.adbcommander.models.AdbFlowExecutionState.FlowStatus
import io.github.openflocon.domain.adbcommander.models.AdbFlowExecutionState.StepStatus
import io.github.openflocon.domain.adbcommander.repository.AdbCommanderRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExecuteFlowUseCase(
    private val executeAdbCommandUseCase: ExecuteAdbCommandUseCase,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
    private val adbCommanderRepository: AdbCommanderRepository,
) {
    operator fun invoke(flow: AdbFlowDomainModel): Flow<AdbFlowExecutionState> = flow {
        val deviceId = getCurrentDeviceIdUseCase()
        val storageDeviceId = deviceId ?: ExecuteAdbCommanderCommandUseCase.DEFAULT_DEVICE_ID
        val target = if (deviceId != null) {
            AdbCommandTargetDomainModel.Device(deviceId)
        } else {
            AdbCommandTargetDomainModel.AllDevices
        }

        val stepStates = flow.steps.sortedBy { it.orderIndex }.map { step ->
            AdbFlowExecutionState.StepState(
                step = step,
                status = StepStatus.Pending,
                output = null,
            )
        }.toMutableList()

        fun currentState(index: Int, status: FlowStatus) = AdbFlowExecutionState(
            flowName = flow.name,
            steps = stepStates.toList(),
            currentStepIndex = index,
            status = status,
        )

        emit(currentState(0, FlowStatus.Running))

        try {
            for (i in stepStates.indices) {
                currentCoroutineContext().ensureActive()

                stepStates[i] = stepStates[i].copy(status = StepStatus.Running)
                emit(currentState(i, FlowStatus.Running))

                val step = stepStates[i].step
                val cleanCommand = step.command.trimStart().removePrefix("adb ").trimStart()

                val result = executeAdbCommandUseCase(
                    target = target,
                    command = cleanCommand,
                )

                result.fold(
                    doOnFailure = { error ->
                        val output = error.message ?: "Unknown error"
                        stepStates[i] = stepStates[i].copy(
                            status = StepStatus.Failed,
                            output = output,
                        )
                        adbCommanderRepository.addToHistory(storageDeviceId, cleanCommand, output, false)
                        // Mark remaining as skipped
                        for (j in (i + 1) until stepStates.size) {
                            stepStates[j] = stepStates[j].copy(status = StepStatus.Skipped)
                        }
                        emit(currentState(i, FlowStatus.Failed))
                        return@flow
                    },
                    doOnSuccess = { output ->
                        stepStates[i] = stepStates[i].copy(
                            status = StepStatus.Completed,
                            output = output,
                        )
                        adbCommanderRepository.addToHistory(storageDeviceId, cleanCommand, output, true)
                    },
                )

                emit(currentState(i, FlowStatus.Running))

                if (step.delayAfterMs > 0 && i < stepStates.size - 1) {
                    stepStates[i] = stepStates[i].copy(status = StepStatus.WaitingDelay)
                    emit(currentState(i, FlowStatus.Running))
                    delay(step.delayAfterMs)
                    stepStates[i] = stepStates[i].copy(status = StepStatus.Completed)
                    emit(currentState(i, FlowStatus.Running))
                }
            }

            emit(currentState(stepStates.lastIndex, FlowStatus.Completed))
        } catch (_: kotlinx.coroutines.CancellationException) {
            for (j in stepStates.indices) {
                if (stepStates[j].status == StepStatus.Pending || stepStates[j].status == StepStatus.Running) {
                    stepStates[j] = stepStates[j].copy(status = StepStatus.Skipped)
                }
            }
            emit(currentState(stepStates.lastIndex.coerceAtLeast(0), FlowStatus.Cancelled))
            throw kotlinx.coroutines.CancellationException()
        }
    }
}
