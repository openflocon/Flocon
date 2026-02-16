package io.github.openflocon.data.local.commands.datasource

import io.github.openflocon.data.core.commands.AdbCommandLocalDataSource
import io.github.openflocon.data.local.commands.dao.AdbCommandDao
import io.github.openflocon.data.local.commands.mapper.toDomain
import io.github.openflocon.data.local.commands.mapper.toLocal
import io.github.openflocon.data.local.commands.model.AdbCommandEntity
import io.github.openflocon.domain.commands.models.AdbCommand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AdbCommandLocalDataSourceImpl(
    private val dao: AdbCommandDao
) : AdbCommandLocalDataSource {

    override suspend fun insertOrReplace(adbCommand: AdbCommand) {
        dao.upsertAll(listOf(adbCommand.toLocal()))
    }

    override suspend fun delete(adbCommand: AdbCommand) {
        dao.delete(adbCommand.toLocal())
    }

    override fun getAll(): Flow<List<AdbCommand>> = dao.getAll()
        .map { list -> list.map(AdbCommandEntity::toDomain) }

}
