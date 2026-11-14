package com.flocon.data.remote.version.datasource

import com.flocon.data.remote.version.model.GithubReleaseRemote
import io.github.openflocon.data.core.versions.datasource.VersionCheckerDatasource
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import io.github.z4kn4fein.semver.toVersionOrNull
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

    override suspend fun fetchLatestVersion(repository: String): Either<Throwable, String> {
        // not /releases/latest: it never returns the pre-releases, which are offered as updates
        val url = "https://api.github.com/repos/$repository/releases?per_page=10"
        return try {
            val releases: List<GithubReleaseRemote> = client.get(url).body()
            releases.latestReleaseVersion()?.success() ?: NoReleaseException(repository).failure()
        } catch (t: Throwable) {
            println("Failed to fetch the latest release of $repository: ${t.message}")
            t.failure()
        }
    }

    override suspend fun fetchReleases(): Either<Throwable, List<ReleaseNoteDomainModel>> {
        val url = "https://api.github.com/repos/$REPO/releases?per_page=50"
        return try {
            val releases: List<GithubReleaseRemote> = client.get(url).body()
            releases
                .filterNot { it.draft || it.prerelease }
                .map { it.toDomain() }
                .success()
        } catch (t: Throwable) {
            println("Failed to fetch GitHub releases: ${t.message}")
            t.failure()
        }
    }

    private fun GithubReleaseRemote.toDomain(): ReleaseNoteDomainModel {
        val version = tagName.toReleaseVersion()
        return ReleaseNoteDomainModel(
            version = version,
            title = name?.takeIf { it.isNotBlank() } ?: version,
            body = body.orEmpty(),
            publishedAt = publishedAt?.substringBefore("T"),
            link = htmlUrl ?: "https://github.com/$REPO/releases/tag/$tagName",
        )
    }

    private companion object {
        const val REPO = "openflocon/flocon"
    }
}

/** The newest published release (drafts excluded, pre-releases included: GitHub lists the newest first). */
internal fun List<GithubReleaseRemote>.latestReleaseVersion(): String? = firstOrNull { !it.draft }?.tagName?.toReleaseVersion()

private class NoReleaseException(repository: String) : Exception("No release found for $repository")

/**
 * "2.0.0", "v2.0.0" and "flocon-v2.0.0" (the Flutter tags) all give "2.0.0".
 * The pre-release part is kept ("2.1.0-rc.1"), the build part is dropped as it is ignored by the versions order.
 * A tag that is not a version is kept as is.
 */
internal fun String.toReleaseVersion(): String {
    val version = dropWhile { !it.isDigit() }.toVersionOrNull(strict = false) ?: return this
    val preRelease = version.preRelease?.let { "-$it" }.orEmpty()
    return "${version.major}.${version.minor}.${version.patch}$preRelease"
}
