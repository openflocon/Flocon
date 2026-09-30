package io.github.openflocon.flocon.myapplication.multi.database.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class HumanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val name: String,
)

