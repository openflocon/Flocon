package io.github.openflocon.flocondesktop.features.proxy

import co.touchlab.kermit.Logger
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * How Flocon runs the proxy, from the lightest to the heaviest:
 * 1. With the Node.js of the computer (most developers have it): the JS bundle shipped in the app (~5 MB).
 * 2. Without Node: the standalone binary (~100 MB, Bun inside), downloaded once from the GitHub release
 *    of this version of Flocon, checked against its SHA-256, and kept in the Flocon folder.
 */
internal object ProxyRuntime {

    /** The JS bundle in the app resources, built by `bun run build:node` in proxy/. */
    private const val BUNDLE_RESOURCE = "/proxy/flocon-proxy.mjs"
    private const val MIN_NODE_MAJOR = 18
    private const val RELEASES_URL = "https://github.com/openflocon/Flocon/releases/download"

    /** The command to run the proxy with Node, or null when Node or the bundle is missing. */
    fun nodeCommand(versionDir: File): List<String>? {
        val node = findNode() ?: return null
        val bundle = extractBundle(versionDir) ?: return null
        return listOf(node, bundle.absolutePath)
    }

    /** The standalone binary of this version, already downloaded, or null. */
    fun downloadedBinary(versionDir: File): File? = File(versionDir, binaryAssetName()).takeIf { it.isFile && it.length() > 0 }

    /**
     * Downloads the standalone binary of [appVersion] into [versionDir], checks its SHA-256 and makes it executable.
     * [onProgress] gets 0..1, or null while the size is unknown.
     */
    fun downloadBinary(appVersion: String, versionDir: File, onProgress: (Float?) -> Unit): File {
        val asset = binaryAssetName()
        val url = "$RELEASES_URL/$appVersion/$asset"
        val client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build()

        val expectedSha = client.send(HttpRequest.newBuilder(URI("$url.sha256")).build(), HttpResponse.BodyHandlers.ofString())
            .also { check(it.statusCode() == 200) { "No proxy for Flocon $appVersion on this platform (HTTP ${it.statusCode()})" } }
            .body()
            .let(::parseSha256)

        versionDir.mkdirs()
        val temp = File(versionDir, "$asset.part")
        val response = client.send(HttpRequest.newBuilder(URI(url)).build(), HttpResponse.BodyHandlers.ofInputStream())
        check(response.statusCode() == 200) { "Cannot download the proxy (HTTP ${response.statusCode()})" }
        val total = response.headers().firstValueAsLong("content-length").orElse(-1L)
        val digest = MessageDigest.getInstance("SHA-256")
        response.body().use { input ->
            temp.outputStream().use { output ->
                val buffer = ByteArray(64 * 1024)
                var read = 0L
                onProgress(if (total > 0) 0f else null)
                while (true) {
                    val n = input.read(buffer)
                    if (n < 0) break
                    output.write(buffer, 0, n)
                    digest.update(buffer, 0, n)
                    read += n
                    if (total > 0) onProgress(read.toFloat() / total)
                }
            }
        }
        val actualSha = digest.digest().joinToString("") { "%02x".format(it) }
        if (actualSha != expectedSha) {
            temp.delete()
            error("The downloaded proxy is corrupted (SHA-256 mismatch)")
        }
        val binary = File(versionDir, asset)
        Files.move(temp.toPath(), binary.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        binary.setExecutable(true)
        return binary
    }

    /** `flocon-proxy-macos-arm64`, `flocon-proxy-windows-x64.exe`…: the names of the release assets. */
    fun binaryAssetName(platform: String = currentPlatformFolder()): String = "flocon-proxy-$platform" + if (platform.startsWith("windows")) ".exe" else ""

    /** Copies the bundle out of the app (Node cannot read inside a jar), once per version. */
    private fun extractBundle(versionDir: File): File? {
        val stream = ProxyRuntime::class.java.getResourceAsStream(BUNDLE_RESOURCE) ?: return null
        val bundle = File(versionDir, "flocon-proxy.mjs")
        stream.use { input ->
            val bytes = input.readBytes()
            if (!bundle.isFile || bundle.length() != bytes.size.toLong()) {
                versionDir.mkdirs()
                bundle.writeBytes(bytes)
            }
        }
        return bundle
    }

    /**
     * Node from the PATH, then from the usual install folders: an app started from the Dock or the Start menu
     * does not get the PATH of the shell, where nvm, volta or Homebrew add it.
     */
    private fun findNode(): String? = nodeCandidates().firstOrNull { isUsableNode(it) }

    private fun nodeCandidates(): List<String> {
        val home = System.getProperty("user.home")
        val windows = currentPlatformFolder().startsWith("windows")
        val fixed = if (windows) {
            listOf(
                "node",
                System.getenv("ProgramFiles")?.let { "$it\\nodejs\\node.exe" },
                System.getenv("LOCALAPPDATA")?.let { "$it\\Programs\\nodejs\\node.exe" },
                System.getenv("NVM_SYMLINK")?.let { "$it\\node.exe" },
                "$home\\AppData\\Local\\Volta\\bin\\node.exe",
            )
        } else {
            listOf(
                "node",
                "/opt/homebrew/bin/node",
                "/usr/local/bin/node",
                "/usr/bin/node",
                "$home/.volta/bin/node",
                "$home/.local/share/fnm/aliases/default/bin/node",
            )
        }
        // nvm: the most recent version installed
        val nvm = File(home, ".nvm/versions/node").listFiles()
            ?.sortedByDescending { nodeMajor(it.name) ?: 0 }
            ?.map { File(it, "bin/node").path }
            .orEmpty()
        return (fixed.filterNotNull() + nvm).distinct()
    }

    private fun isUsableNode(path: String): Boolean = try {
        val process = ProcessBuilder(path, "--version").redirectErrorStream(true).start()
        val output = process.inputStream.bufferedReader().readText().trim()
        process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0 && (nodeMajor(output) ?: 0) >= MIN_NODE_MAJOR
    } catch (e: Exception) {
        Logger.d { "No usable Node at $path: ${e.message}" }
        false
    }
}

/** "v20.11.1" → 20. */
internal fun nodeMajor(version: String): Int? = version.trim().removePrefix("v").substringBefore('.').toIntOrNull()

/** The first word of a `sha256sum` line (or the bare hash), lower case. */
internal fun parseSha256(text: String): String = text.trim().substringBefore(' ').lowercase()
