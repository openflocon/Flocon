package io.github.openflocon.flocon.myapplication.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HumanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val name: String,
)