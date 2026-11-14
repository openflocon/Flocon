package com.flocon.data.remote.version

import com.flocon.data.remote.version.datasource.toReleaseVersion
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
