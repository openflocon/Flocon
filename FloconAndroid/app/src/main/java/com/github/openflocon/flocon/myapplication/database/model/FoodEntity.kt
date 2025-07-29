package com.github.openflocon.flocon.myapplication.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Pour la base de données "FoodEntity"
@Entity
data class FoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // e.g., "fruit", "vegetable", "meat"
    val calories: Int,
)