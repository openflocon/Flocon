package io.github.openflocon.data.local.dashboard.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = DashboardContainerEntity::class,
            parentColumns = ["id"],
            childColumns = ["containerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["containerId"]),
        Index(value = ["containerId", "elementOrder"], unique = true),
    ],
)
data class DashboardElementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val containerId: Long,

    val elementOrder: Int,

    @Embedded(prefix = "button_")
    val button: DashboardElementButton?,
    @Embedded(prefix = "text_")
    val text: DashboardElementText?,
    @Embedded(prefix = "label_")
    val label: DashboardElementLabel?,
    @Embedded(prefix = "plainText_")
    val plainText: DashboardElementPlainText?,
    @Embedded(prefix = "textField_")
    val textField: DashboardElementTextField?,
    @Embedded(prefix = "checkBox_")
    val checkBox: DashboardElementCheckBox?,
)

data class DashboardElementButton(
    val text: String,
    val actionId: String,
)

data class DashboardElementText(
    val label: String,
    val value: String,
    val color: Int?,
)

data class DashboardElementLabel(
    val label: String,
    val color: Int?,
)

data class DashboardElementPlainText(
    val label: String,
    val value: String,
    val type: String,
)

data class DashboardElementTextField(
    val actionId: String,
    val label: String,
    val value: String,
    val placeHolder: String?,
)

data class DashboardElementCheckBox(
    val actionId: String,
    val label: String,
    val value: Boolean,
)
