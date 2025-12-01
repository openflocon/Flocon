package io.github.openflocon.data.local.dashboard.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
        foreignKeys =
                [
                        ForeignKey(
                                entity = DashboardContainerEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["containerId"],
                                onDelete = ForeignKey.CASCADE,
                        ),
                ],
        indices =
                [
                        Index(value = ["containerId"]),
                        Index(value = ["containerId", "elementOrder"], unique = true),
                ],
)
data class DashboardElementEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val containerId: Long,
        val elementOrder: Int,
        val elementAsJson: String,
)

@Serializable sealed class DashboardElement

@Serializable
data class DashboardElementButton(
        val text: String,
        val actionId: String,
) : DashboardElement()

@Serializable
data class DashboardElementText(
        val label: String,
        val value: String,
        val color: Int?,
) : DashboardElement()

@Serializable
data class DashboardElementLabel(
        val label: String,
        val color: Int?,
) : DashboardElement()

@Serializable
data class DashboardElementPlainText(
        val label: String,
        val value: String,
        val type: String,
) : DashboardElement()

@Serializable
data class DashboardElementTextField(
        val actionId: String,
        val label: String,
        val value: String,
        val placeHolder: String?,
) : DashboardElement()

@Serializable
data class DashboardElementCheckBox(
        val actionId: String,
        val label: String,
        val value: Boolean,
) : DashboardElement()
