package io.github.openflocon.data.local.adbcommander.datasource

import io.github.openflocon.data.core.adbcommander.datasource.AdbCommanderLocalDataSource
import io.github.openflocon.data.local.adbcommander.dao.AdbCommanderDao
import io.github.openflocon.data.local.adbcommander.mapper.toDomainModel
import io.github.openflocon.data.local.adbcommander.mapper.toEntity
import io.github.openflocon.data.local.adbcommander.models.AdbCommandHistoryEntity
import io.github.openflocon.data.local.adbcommander.models.AdbFlowEntity
import io.github.openflocon.domain.adbcommander.models.AdbCommandDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbCommandHistoryDomainModel
import io.github.openflocon.domain.adbcommander.models.AdbFlowDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class LocalAdbCommanderDataSourceRoom(
    private val dao: AdbCommanderDao,
) : AdbCommanderLocalDataSource {

    override fun observeSavedCommands(deviceId: String): Flow<List<AdbCommandDomainModel>> =
        dao.observeSavedCommands(deviceId)
            .map { entities -> entities.map { it.toDomainModel() } }
            .distinctUntilChanged()

    override suspend fun saveCommand(deviceId: String, command: AdbCommandDomainModel) {
        dao.insertSavedCommand(command.toEntity(deviceId))
    }

    override suspend fun deleteSavedCommand(id: Long) {
        dao.deleteSavedCommand(id)
    }

    override suspend fun updateSavedCommand(command: AdbCommandDomainModel, deviceId: String) {
        dao.updateSavedCommand(command.toEntity(deviceId))
    }

    override fun observeHistory(deviceId: String): Flow<List<AdbCommandHistoryDomainModel>> =
        dao.observeHistory(deviceId)
            .map { entities -> entities.map { it.toDomainModel() } }
            .distinctUntilChanged()

    override suspend fun addToHistory(
        deviceId: String,
        command: String,
        output: String,
        isSuccess: Boolean,
    ) {
        dao.insertHistory(
            AdbCommandHistoryEntity(
                deviceId = deviceId,
                command = command,
                output = output,
                isSuccess = isSuccess,
                executedAt = System.currentTimeMillis(),
            )
        )
    }

    override suspend fun clearHistory(deviceId: String) {
        dao.clearHistory(deviceId)
    }

    override fun observeFlows(deviceId: String): Flow<List<AdbFlowDomainModel>> =
        dao.observeFlowsWithSteps(deviceId)
            .map { entities -> entities.map { it.toDomainModel() } }
            .distinctUntilChanged()

    override suspend fun getFlowWithSteps(flowId: Long): AdbFlowDomainModel? {
        val flow = dao.getFlowById(flowId) ?: return null
        val steps = dao.getFlowSteps(flowId)
        return flow.toDomainModel(steps)
    }

    override suspend fun saveFlow(deviceId: String, flow: AdbFlowDomainModel): Long {
        val flowId = dao.insertFlow(
            AdbFlowEntity(
                deviceId = deviceId,
                name = flow.name,
                description = flow.description,
                createdAt = System.currentTimeMillis(),
            )
        )
        dao.insertFlowSteps(
            flow.steps.map { it.toEntity(flowId) }
        )
        return flowId
    }

    override suspend fun deleteFlow(id: Long) {
        dao.deleteFlow(id)
    }

    override suspend fun updateFlow(flow: AdbFlowDomainModel, deviceId: String) {
        dao.updateFlow(
            AdbFlowEntity(
                id = flow.id,
                deviceId = deviceId,
                name = flow.name,
                description = flow.description,
                createdAt = System.currentTimeMillis(),
            )
        )
        dao.replaceFlowSteps(
            flowId = flow.id,
            steps = flow.steps.map { it.toEntity(flow.id) },
        )
    }
}
