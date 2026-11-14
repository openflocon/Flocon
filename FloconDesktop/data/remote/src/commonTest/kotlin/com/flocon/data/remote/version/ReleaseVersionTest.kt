package com.flocon.data.remote.version

import com.flocon.data.remote.version.datasource.latestReleaseVersion
import com.flocon.data.remote.version.datasource.toReleaseVersion
import com.flocon.data.remote.version.model.GithubReleaseRemote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ReleaseVersionTest {
    @Test
    fun `a tag gives its version`() {
        assertEquals("2.0.0", "2.0.0".toReleaseVersion())
        assertEquals("2.0.0", "v2.0.0".toReleaseVersion())
        assertEquals("2.0.1", "flocon-v2.0.1".toReleaseVersion())
    }

    @Test
    fun `the pre-release part is kept and the build part is dropped`() {
        assertEquals("2.1.0-rc.1", "2.1.0-rc.1".toReleaseVersion())
        assertEquals("2.1.0-beta.2", "v2.1.0-beta.2+build.5".toReleaseVersion())
        assertEquals("2.1.0", "2.1.0+build.5".toReleaseVersion())
    }

    @Test
    fun `the latest release is the newest one that is not a draft, pre-releases included`() {
        val releases = listOf(
            GithubReleaseRemote(tagName = "v2.2.0", draft = true),
            GithubReleaseRemote(tagName = "v2.1.0-rc.1", prerelease = true),
            GithubReleaseRemote(tagName = "v2.0.0"),
        )
        assertEquals("2.1.0-rc.1", releases.latestReleaseVersion())
        assertNull(emptyList<GithubReleaseRemote>().latestReleaseVersion())
    }

    @Test
    fun `a tag that is not a version is kept`() {
        assertEquals("nightly", "nightly".toReleaseVersion())
    }
}
