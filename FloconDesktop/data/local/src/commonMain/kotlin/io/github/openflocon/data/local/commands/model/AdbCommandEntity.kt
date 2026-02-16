package io.github.openflocon.data.local.commands.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AdbCommandEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val command: String

)
