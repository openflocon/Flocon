package io.github.openflocon.flocon.myapplication.multi.database.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val calories: Int,
)

