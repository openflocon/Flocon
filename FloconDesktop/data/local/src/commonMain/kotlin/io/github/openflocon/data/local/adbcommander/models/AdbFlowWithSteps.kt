package io.github.openflocon.data.local.adbcommander.models

import androidx.room.Embedded
import androidx.room.Relation

data class AdbFlowWithSteps(
    @Embedded val flow: AdbFlowEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "flowId",
    )
    val steps: List<AdbFlowStepEntity>,
)
