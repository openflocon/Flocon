package io.github.openflocon.flocon.pluginsold.database.model

interface FloconDatabaseModel {
    val displayName: String
}

data class FloconFileDatabaseModel(
    override val displayName: String,
    val absolutePath: String
) : FloconDatabaseModel