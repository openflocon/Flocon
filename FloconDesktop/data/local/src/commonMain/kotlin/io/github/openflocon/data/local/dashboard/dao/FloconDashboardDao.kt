package io.github.openflocon.data.local.dashboard.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.data.local.dashboard.mapper.toEntity
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.data.local.dashboard.models.DashboardEntity
import io.github.openflocon.data.local.dashboard.models.DashboardContainerEntity
import io.github.openflocon.data.local.dashboard.models.DashboardWithContainersAndElements
import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconDashboardDao {

    @Transaction
    @Query(
        """
        SELECT * FROM DashboardEntity 
        WHERE deviceId = :deviceId AND dashboardId = :dashboardId
        AND packageName = :packageName
        LIMIT 1
    """,
    )
    fun observeDashboardWithContainersAndElements(
        deviceId: String,
        packageName: String,
        dashboardId: String,
    ): Flow<DashboardWithContainersAndElements?>

    @Transaction
    @Query(
        """
        SELECT dashboardId
        FROM DashboardEntity 
        WHERE deviceId = :deviceId 
        AND packageName = :packageName
    """,
    )
    fun observeDeviceDashboards(
        deviceId: String,
        packageName: String,
    ): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDashboard(dashboard: DashboardEntity): Long // Returns generated ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContainers(containers: List<DashboardContainerEntity>): List<Long> // Returns generated IDs

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDashboardElements(elements: List<DashboardElementEntity>)

    @Query(
        """
        DELETE FROM DashboardEntity 
        WHERE deviceId = :deviceId AND dashboardId = :dashboard
        """,
    )
    suspend fun clearDashboardByDeviceAndId(deviceId: String, dashboard: String)

    @Transaction
    suspend fun saveFullDashboard(
        deviceId: DeviceId,
        packageName: String,
        dashboard: DashboardDomainModel,
    ) {
        // 1. Clear old data
        clearDashboardByDeviceAndId(deviceId = deviceId, dashboard = dashboard.dashboardId)

        // 2. insert the new dashboatd data
        insertDashboard(
            dashboard = dashboard.toEntity(
                packageName = packageName,
                deviceId = deviceId,
            ),
        )

        // 3. Prepare containers with the new dashboardId, insert them, and get their new IDs
        val insertedContainersIds = insertContainers(
            containers = dashboard.containers
                .mapIndexed { index, container ->
                    container.toEntity(
                        dashboardId = dashboard.dashboardId,
                        index = index,
                    )
                },
        )

        // 4. Prepare elements with new containerIds and insert them
        val allElementsToInsert = mutableListOf<DashboardElementEntity>()
        dashboard.containers.forEachIndexed { index, container ->
            insertedContainersIds.getOrNull(index)?.let { containerId ->
                container.elements.forEachIndexed { index, element ->
                    allElementsToInsert.add(
                        element.toEntity(
                            containerId = containerId,
                            index = index,
                        ),
                    )
                }
            }
        }

        if (allElementsToInsert.isNotEmpty()) {
            insertDashboardElements(allElementsToInsert)
        }
    }
}
