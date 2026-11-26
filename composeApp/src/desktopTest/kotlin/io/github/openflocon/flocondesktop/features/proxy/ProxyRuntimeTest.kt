package io.github.openflocon.flocondesktop.features.proxy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProxyRuntimeTest {

    @Test
    fun `reads the major version of node`() {
        assertEquals(20, nodeMajor("v20.11.1"))
        assertEquals(18, nodeMajor("v18.0.0\n"))
        assertNull(nodeMajor("node: command not found"))
    }

    @Test
    fun `reads a sha256 file`() {
        assertEquals("abc123", parseSha256("ABC123  flocon-proxy-macos-arm64\n"))
        assertEquals("abc123", parseSha256("abc123"))
    }

    @Test
    fun `names the release asset of each platform`() {
        assertEquals("flocon-proxy-macos-arm64", ProxyRuntime.binaryAssetName("macos-arm64"))
        assertEquals("flocon-proxy-linux-arm64", ProxyRuntime.binaryAssetName("linux-arm64"))
        assertEquals("flocon-proxy-windows-x64.exe", ProxyRuntime.binaryAssetName("windows-x64"))
    }
}
