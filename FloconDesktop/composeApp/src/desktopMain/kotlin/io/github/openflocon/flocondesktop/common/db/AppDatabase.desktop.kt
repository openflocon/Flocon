package io.github.openflocon.flocondesktop.common.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "flocon_room.db")
    // UNCOMMENT TO TEST ON AN EMPTY DB
    // dbFile.delete()
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}
