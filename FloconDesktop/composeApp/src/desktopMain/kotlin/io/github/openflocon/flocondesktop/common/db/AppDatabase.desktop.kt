package io.github.openflocon.flocondesktop.common.db

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabaseFile(): File {
    val folder = File(System.getProperty("user.home"), ".flocon")
    folder.mkdirs()
    return File(folder, "flocon_room.db")
}

actual fun getMockDatabaseFile(): File = File(File(System.getProperty("user.home"), ".flocon/mock"), "flocon_room.db")

actual fun getDatabaseBuilder(file: File): RoomDatabase.Builder<AppDatabase> {
    // UNCOMMENT TO TEST ON AN EMPTY DB
    // file.delete()
    return Room.databaseBuilder<AppDatabase>(
        name = file.absolutePath,
    )
}
