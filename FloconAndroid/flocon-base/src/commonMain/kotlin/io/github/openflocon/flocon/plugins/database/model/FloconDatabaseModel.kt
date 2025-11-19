package io.github.openflocon.flocon.plugins.database.model

interface FloconDatabaseModel {
    val displayName: String
}

data class FloconFileDatabaseModel(
    override val displayName: String,
    val absolutePath: String
) : FloconDatabaseModel