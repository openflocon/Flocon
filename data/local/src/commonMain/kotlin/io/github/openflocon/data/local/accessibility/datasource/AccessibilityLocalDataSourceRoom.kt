package io.github.openflocon.data.local.accessibility.datasource

import io.github.openflocon.data.core.accessibility.datasource.AccessibilityLocalDataSource
import io.github.openflocon.data.local.accessibility.dao.AccessibilityEventDao
import io.github.openflocon.data.local.accessibility.models.AccessibilityEventEntity
import io.github.openflocon.domain.accessibility.models.AccessibilityEventDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

// the list screen shows the newest events, the older ones stay in the database
private const val OBSERVE_LIMIT = 5_000

class AccessibilityLocalDataSourceRoom(
    private val accessibilityEventDao: AccessibilityEventDao,
) : AccessibilityLocalDataSource {

    override suspend fun insert(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        events: List<AccessibilityEventDomainModel>,
    ) {
        accessibilityEventDao.insertEvents(events.map { it.toEntity(deviceIdAndPackageName) })
    }

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
    ): Flow<List<AccessibilityEventDomainModel>> = accessibilityEventDao.observeEvents(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        type = type?.takeIf { it.isNotBlank() },
        text = text?.takeIf { it.isNotBlank() },
        limit = OBSERVE_LIMIT,
    )
        .map { list -> list.map { it.toDomain() } }
        .distinctUntilChanged()

    override fun observeById(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        id: String,
    ): Flow<AccessibilityEventDomainModel?> = accessibilityEventDao.observeEvent(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        id = id,
    )
        .map { it?.toDomain() }
        .distinctUntilChanged()

    override fun observeTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>> = accessibilityEventDao.observeTypes(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    ).distinctUntilChanged()

    override suspend fun search(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        type: String?,
        text: String?,
        limit: Int,
    ): List<AccessibilityEventDomainModel> = accessibilityEventDao.searchEvents(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        type = type?.takeIf { it.isNotBlank() },
        text = text?.takeIf { it.isNotBlank() },
        limit = limit,
    ).map { it.toDomain() }

    override suspend fun getTypes(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String> = accessibilityEventDao.getTypes(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    )

    override suspend fun clear(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Int = accessibilityEventDao.deleteEvents(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    )
}

private fun AccessibilityEventDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
) = AccessibilityEventEntity(
    deviceId = deviceIdAndPackageName.deviceId,
    packageName = deviceIdAndPackageName.packageName,
    id = id,
    appInstance = deviceIdAndPackageName.appInstance,
    type = type,
    text = text,
    contentDescription = contentDescription,
    className = className,
    screen = screen,
    bounds = bounds,
    extras = extras,
    createdAt = createdAt,
)

private fun AccessibilityEventEntity.toDomain() = AccessibilityEventDomainModel(
    id = id,
    type = type,
    text = text,
    contentDescription = contentDescription,
    className = className,
    screen = screen,
    bounds = bounds,
    extras = extras,
    createdAt = createdAt,
)
