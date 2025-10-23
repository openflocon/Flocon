package com.flocon.data.remote.version.datasource

import com.flocon.data.remote.version.model.GithubReleaseRemote
import io.github.openflocon.data.core.versions.datasource.VersionCheckerDatasource
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.success
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class VersionCheckerDatasourceImpl : VersionCheckerDatasource {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun fetchLatestVersion(): Either<Throwable, String> {
        val repo = "openflocon/flocon"
        val url = "https://api.github.com/repos/$repo/releases/latest"
        return try {
            val release: GithubReleaseRemote = client.get(url).body()
            val lastVersion = release.tagName.removePrefix("v") // au cas où le tag commence par "v"
            return lastVersion.success()
        } catch (t: Throwable) {
            println("Erreur lors de la récupération de la version GitHub: ${t.message}")
            t.failure()
        }
    }
}
