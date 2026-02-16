package io.github.openflocon.data.core.commands.repository

import io.github.openflocon.data.core.commands.AdbCommandLocalDataSource
import io.github.openflocon.domain.commands.models.AdbCommand
import io.github.openflocon.domain.commands.repository.AdbCommandRepository
import kotlinx.coroutines.flow.Flow

internal class AdbCommandRepositoryImpl(private val local: AdbCommandLocalDataSource) :
        AdbCommandRepository {

    override suspend fun insertOrUpdate(adbCommand: AdbCommand) {
        local.insertOrReplace(adbCommand)
    }

    override suspend fun delete(adbCommand: AdbCommand) {
        local.delete(adbCommand)
    }

    override suspend fun get(id: Long): AdbCommand? = local.getById(id)

    override fun getAll(): Flow<List<AdbCommand>> = local.getAll()
}
