package io.github.openflocon.mcp.images

import io.github.openflocon.domain.images.repository.ImagesRepository
import io.github.openflocon.mcp.McpOrder
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlin.time.Instant

internal class ImagesMcpToolProvider(
    private val imagesRepository: ImagesRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTypedTool(
            name = "list_images",
            title = "List images",
            description = "Lists the images loaded by the mobile app and sent to Flocon: url and load time, newest first. " +
                "Only the url is known, not the image content.",
            annotations = readOnlyToolAnnotations,
            handler = ::listImages,
        )
    }

    private suspend fun listImages(input: ListImagesInput): ImagesMcpResult {
        val target = targetResolver.resolve(input)
        val limit = input.limit.coerceIn(1, MAX_LIMIT)
        val text = input.text?.takeIf { it.isNotBlank() }

        val images = imagesRepository.searchImages(
            deviceIdAndPackageName = target,
            sinceTimestamp = input.sinceTimestamp,
            text = text,
            newestFirst = input.order == McpOrder.NewestFirst,
            limit = limit + 1,
        )
        val returned = images.take(limit)

        return ImagesMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            returned = returned.size,
            hasMore = images.size > limit,
            images = returned.map {
                ImageMcpModel(
                    url = it.url,
                    timestamp = Instant.fromEpochMilliseconds(it.time).toString(),
                    timestampMs = it.time,
                    headers = it.headers.takeIf { input.includeHeaders },
                )
            },
        )
    }
}
