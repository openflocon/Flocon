package io.github.openflocon.flocon.myapplication.database.model

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["humanId", "dogId"],
    foreignKeys = [
        ForeignKey(
            entity = HumanEntity::class,
            parentColumns = ["id"],
            childColumns = ["humanId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = DogEntity::class,
            parentColumns = ["id"],
            childColumns = ["dogId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class HumanWithDogEntity(
    val humanId: Long,
    val dogId: Long,
)