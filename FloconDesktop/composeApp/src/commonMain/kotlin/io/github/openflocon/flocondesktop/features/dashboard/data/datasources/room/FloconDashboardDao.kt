package io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.mapper.toEntity
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementEntity
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardEntity
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardSectionEntity
import io.github.openflocon.flocondesktop.features.dashboard.domain.model.DashboardDomainModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FloconDashboardDao {

    @Transaction
    @Query(
        """
        SELECT * FROM DashboardEntity 
        WHERE deviceId = :deviceId AND dashboardId = :dashboardId
        LIMIT 1
    """,
    )
    fun observeDashboardWithSectionsAndElements(
        deviceId: String,
        dashboardId: String,
    ): Flow<DashboardWithSectionsAndElements?>

    @Transaction
    @Query(
        """
        SELECT dashboardId
        FROM DashboardEntity 
        WHERE deviceId = :deviceId 
    """,
    )
    fun observeDeviceDashboards(deviceId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDashboard(dashboard: DashboardEntity): Long // Returns generated ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<DashboardSectionEntity>): List<Long> // Returns generated IDs

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
        dashboard: DashboardDomainModel,
    ) {
        // 1. Clear old data
        clearDashboardByDeviceAndId(deviceId = deviceId, dashboard = dashboard.dashboardId)

        // 2. insert the new dashboatd data
        insertDashboard(
            dashboard = dashboard.toEntity(
                deviceId = deviceId,
            ),
        )

        // 3. Prepare sections with the new dashboardId, insert them, and get their new IDs
        val insertedSectionsIds = insertSections(
            sections = dashboard.sections
                .mapIndexed { index, section ->
                    section.toEntity(
                        dashboardId = dashboard.dashboardId,
                        index = index,
                    )
                },
        )

        // 4. Prepare elements with new sectionIds and insert them
        val allElementsToInsert = mutableListOf<DashboardElementEntity>()
        dashboard.sections.forEachIndexed { index, section ->
            insertedSectionsIds.getOrNull(index)?.let { sectionId ->
                section.elements.forEachIndexed { index, element ->
                    allElementsToInsert.add(
                        element.toEntity(
                            sectionId = sectionId,
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
