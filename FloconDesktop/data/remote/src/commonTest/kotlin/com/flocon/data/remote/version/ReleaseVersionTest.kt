package com.flocon.data.remote.version

import com.flocon.data.remote.version.datasource.isStable
import com.flocon.data.remote.version.datasource.latestStableVersion
import com.flocon.data.remote.version.datasource.toReleaseVersion
import com.flocon.data.remote.version.model.GithubReleaseRemote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReleaseVersionTest {
    @Test
    fun `a tag gives its version`() {
        assertEquals("2.0.0", "2.0.0".toReleaseVersion())
        assertEquals("2.0.0", "v2.0.0".toReleaseVersion())
        assertEquals("2.0.1", "flocon-v2.0.1".toReleaseVersion())
    }

    @Test
    fun `pre-release and build parts are dropped`() {
        assertEquals("2.1.0", "2.1.0-rc.1".toReleaseVersion())
        assertEquals("2.1.0", "v2.1.0-beta.2+build.5".toReleaseVersion())
    }

    @Test
    fun `a tag that is not a version is kept`() {
        assertEquals("nightly", "nightly".toReleaseVersion())
    }

    @Test
    fun `the latest stable release is compared, not the newest one which can be a release candidate`() {
        val releases = listOf(
            GithubReleaseRemote(tagName = "v2.1.0-rc.1"), // not flagged as a pre-release on GitHub
            GithubReleaseRemote(tagName = "v2.2.0", draft = true),
            GithubReleaseRemote(tagName = "v2.1.0-beta.2", prerelease = true),
            GithubReleaseRemote(tagName = "v1.9.0"),
            GithubReleaseRemote(tagName = "v2.0.0"),
            GithubReleaseRemote(tagName = "nightly"),
        )
        assertEquals("2.0.0", releases.latestStableVersion())
        assertNull(emptyList<GithubReleaseRemote>().latestStableVersion())
        assertNull(listOf(GithubReleaseRemote(tagName = "v2.1.0-rc.1")).latestStableVersion())
    }

    @Test
    fun `a release is stable when it is not a draft, a pre-release or a tagged candidate`() {
        assertTrue(GithubReleaseRemote(tagName = "flocon-v2.0.1").isStable())
        assertFalse(GithubReleaseRemote(tagName = "2.1.0-alpha").isStable())
        assertFalse(GithubReleaseRemote(tagName = "2.1.0", prerelease = true).isStable())
        assertFalse(GithubReleaseRemote(tagName = "2.1.0", draft = true).isStable())
    }
}
