package io.github.openflocon.flocondesktop.common.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseFile(): File {
    val folder = File(System.getProperty("user.home"), ".flocon")
    folder.mkdirs()
    return File(folder, "flocon_room.db")
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFile = getDatabaseFile()
    // UNCOMMENT TO TEST ON AN EMPTY DB
    // dbFile.delete()
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}
