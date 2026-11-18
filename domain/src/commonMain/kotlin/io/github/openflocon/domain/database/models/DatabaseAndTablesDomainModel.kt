package io.github.openflocon.domain.database.models

data class DatabaseAndTablesDomainModel(
    val database: DeviceDataBaseDomainModel,
    val tables: List<DatabaseTableDomainModel>,
)
