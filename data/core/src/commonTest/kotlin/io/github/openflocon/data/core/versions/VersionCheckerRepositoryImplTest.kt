package io.github.openflocon.data.core.versions

import io.github.openflocon.data.core.versions.datasource.VersionCheckerDatasource
import io.github.openflocon.data.core.versions.repository.VersionCheckerRepositoryImpl
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.failure
import io.github.openflocon.domain.common.success
import io.github.openflocon.domain.versions.model.FloconSdk
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VersionCheckerRepositoryImplTest {

    private class FakeDatasource(
        var versions: Map<String, String> = emptyMap(),
    ) : VersionCheckerDatasource {
        val fetched = mutableListOf<String>()
        override suspend fun fetchLatestVersion(repository: String): Either<Throwable, String> {
            fetched += repository
            return versions[repository]?.success() ?: IllegalStateException("offline").failure()
        }

        override suspend fun fetchReleases(): Either<Throwable, List<ReleaseNoteDomainModel>> = emptyList<ReleaseNoteDomainModel>().success()
    }

    private val dispatchers = object : DispatcherProvider {
        override val ui = Dispatchers.Unconfined
        override val viewModel = Dispatchers.Unconfined
        override val domain = Dispatchers.Unconfined
        override val data = Dispatchers.Unconfined
    }

    private fun repository(datasource: FakeDatasource) = VersionCheckerRepositoryImpl(dispatchers, datasource)

    @Test
    fun `an SDK is fetched once from its own repository`() = runBlocking {
        val datasource = FakeDatasource(mapOf("openflocon/FloconSwift" to "2.0.0"))
        val repository = repository(datasource)

        repository.checkLastClientSdkVersion(FloconSdk.Swift)
        repository.checkLastClientSdkVersion(FloconSdk.Swift)

        assertEquals(listOf("openflocon/FloconSwift"), datasource.fetched)
        assertEquals("2.0.0", repository.observeLastClientSdkVersion(FloconSdk.Swift).first())
        assertNull(repository.observeLastClientSdkVersion(FloconSdk.Flutter).first())
    }

    @Test
    fun `the desktop check also gives the Kotlin SDK version`() = runBlocking {
        val datasource = FakeDatasource(mapOf("openflocon/Flocon" to "2.1.0"))
        val repository = repository(datasource)

        repository.checkIsLastVersion()
        repository.checkLastClientSdkVersion(FloconSdk.Kotlin)

        assertEquals(listOf("openflocon/Flocon"), datasource.fetched)
        assertEquals("2.1.0", repository.observeLastClientSdkVersion(FloconSdk.Kotlin).first())
    }

    @Test
    fun `an SDK without repository is never fetched`() = runBlocking {
        val datasource = FakeDatasource()
        val repository = repository(datasource)

        repository.checkLastClientSdkVersion(FloconSdk.Other("react-native"))

        assertEquals(emptyList(), datasource.fetched)
    }

    @Test
    fun `a failed fetch is not retried right away`() = runBlocking {
        val datasource = FakeDatasource()
        val repository = repository(datasource)

        repository.checkLastClientSdkVersion(FloconSdk.Flutter)
        repository.checkLastClientSdkVersion(FloconSdk.Flutter)

        assertEquals(listOf("openflocon/FloconFlutter"), datasource.fetched)
        assertNull(repository.observeLastClientSdkVersion(FloconSdk.Flutter).first())
    }
}
