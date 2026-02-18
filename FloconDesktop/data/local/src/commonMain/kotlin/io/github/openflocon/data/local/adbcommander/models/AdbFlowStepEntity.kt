package io.github.openflocon.data.local.adbcommander.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["flowId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = AdbFlowEntity::class,
            parentColumns = ["id"],
            childColumns = ["flowId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class AdbFlowStepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flowId: Long,
    val orderIndex: Int,
    val command: String,
    val delayAfterMs: Long,
    val label: String?,
)
