package io.github.openflocon.flocon.myapplication.multi.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val breed: String,
    val pictureUrl: String,
    val age: Int,
)

