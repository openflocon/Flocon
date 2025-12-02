package io.github.openflocon.data.local.dashboard.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
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

@Serializable
internal sealed interface LocalDashboardElement {

    @Serializable
    data class Button(
        val text: String,
        val actionId: String,
    ) : LocalDashboardElement

    @Serializable
    data class Text(
        val label: String,
        val value: String,
        val color: Int?,
    ) : LocalDashboardElement

    @Serializable
    data class Label(
        val label: String,
        val color: Int?,
    ) : LocalDashboardElement

    @Serializable
    data class PlainText(
        val label: String,
        val value: String,
        @SerialName("plaintext_type") val type: String,
    ) : LocalDashboardElement

    @Serializable
    data class TextField(
        val actionId: String,
        val label: String,
        val value: String,
        val placeHolder: String?,
    ) : LocalDashboardElement

    @Serializable
    data class CheckBox(
        val actionId: String,
        val label: String,
        val value: Boolean,
    ) : LocalDashboardElement
}