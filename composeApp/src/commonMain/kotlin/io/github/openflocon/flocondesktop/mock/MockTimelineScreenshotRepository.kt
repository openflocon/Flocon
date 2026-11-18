package io.github.openflocon.flocondesktop.mock

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.awt.Color
import java.awt.Font
import java.awt.GradientPaint
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

private const val MockScreenshotCount = 14
private const val MockScreenshotEveryMs = 45_000L
private const val ImageWidth = 270
private const val ImageHeight = 585

/**
 * In mock mode the screenshots are not taken with adb: a few images are drawn at the first request, one every
 * [MockScreenshotEveryMs] before the launch, for any device, so the list of the timeline has screenshots between its events.
 */
internal class MockTimelineScreenshotRepository(
    private val rootDir: File = File(System.getProperty("java.io.tmpdir"), "flocon-mock-timeline-screenshots"),
    private val launchedAt: Long = System.currentTimeMillis(),
) : TimelineScreenshotRepository {

    private val shotsByTarget = mutableMapOf<String, List<TimelineScreenshotDomainModel>>()

    override fun observe(target: DeviceIdAndPackageNameDomainModel): Flow<List<TimelineScreenshotDomainModel>> = flow {
        emit(shotsFor(target))
    }

    override fun newJpegFile(target: DeviceIdAndPackageNameDomainModel, timestamp: Long): File {
        rootDir.mkdirs()
        return File(rootDir, "${target.packageName}_$timestamp.jpg")
    }

    override suspend fun add(screenshot: TimelineScreenshotDomainModel) = Unit

    override suspend fun purgeAll() = Unit

    @Synchronized
    private fun shotsFor(target: DeviceIdAndPackageNameDomainModel): List<TimelineScreenshotDomainModel> = shotsByTarget.getOrPut("${target.deviceId}/${target.packageName}") {
        // oldest first
        (MockScreenshotCount - 1 downTo 0).mapNotNull { age ->
            val timestamp = launchedAt - age * MockScreenshotEveryMs
            val file = newJpegFile(target, timestamp)
            runCatching { drawScreen(file, index = MockScreenshotCount - age) }.getOrNull()?.let {
                TimelineScreenshotDomainModel(
                    timestamp = timestamp,
                    filePath = file.absolutePath,
                    deviceId = target.deviceId,
                    packageName = target.packageName,
                )
            }
        }
    }

    // a fake screen: a colored background, a header and a few cards, with its number
    private fun drawScreen(file: File, index: Int) {
        val image = BufferedImage(ImageWidth, ImageHeight, BufferedImage.TYPE_INT_RGB)
        val g = image.createGraphics()
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            val hue = (index * 0.13f) % 1f
            g.paint = GradientPaint(0f, 0f, Color.getHSBColor(hue, 0.45f, 0.35f), 0f, ImageHeight.toFloat(), Color.getHSBColor(hue, 0.55f, 0.18f))
            g.fillRect(0, 0, ImageWidth, ImageHeight)
            g.color = Color(255, 255, 255, 40)
            g.fillRoundRect(16, 40, ImageWidth - 32, 28, 14, 14)
            repeat(4) { card ->
                g.fillRoundRect(16, 96 + card * 110, ImageWidth - 32, 92, 18, 18)
            }
            g.color = Color.WHITE
            g.font = Font(Font.SANS_SERIF, Font.BOLD, 22)
            g.drawString("Screen $index", 28, 62)
        } finally {
            g.dispose()
        }
        file.parentFile.mkdirs()
        ImageIO.write(image, "jpg", file)
    }
}
