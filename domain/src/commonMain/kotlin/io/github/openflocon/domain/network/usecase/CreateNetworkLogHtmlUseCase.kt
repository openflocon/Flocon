package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.JsonFormatter
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.responseBody
import io.github.openflocon.domain.network.models.responseByteSizeFormatted
import io.github.openflocon.domain.network.models.responseHeaders
import io.github.openflocon.domain.network.repository.NetworkRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

class CreateNetworkLogHtmlUseCase(
    private val networkRepository: NetworkRepository,
    private val jsonFormatter: JsonFormatter,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(callId: String) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val call = networkRepository.observeRequest(
            deviceIdAndPackageName = deviceIdAndPackageName,
            requestId = callId
        )
            .firstOrNull()
            ?: return
        val folder = FileKit.openDirectoryPicker(
            dialogSettings = FileKitDialogSettings(
                title = "Choose to create the file on"
            )
        ) ?: return
        val file = File(folder.file, "$callId.html")

        val statusLabel = call.response?.statusFormatted ?: "Pending"
        val statusClass = statusBadgeClass(statusLabel)
        val methodClass = methodBadgeClass(call.request.method)

        file.writer().use { writer ->
            writer.appendLine("<!DOCTYPE html>")
            writer.appendLine("<html lang=\"en\">")
            writer.appendLine("<head>")
            writer.appendLine("<meta charset=\"UTF-8\">")
            writer.appendLine("<title>${escapeHtml(call.request.method)} ${escapeHtml(call.request.url)}</title>")
            writer.appendLine("<style>${styleSheet()}</style>")
            writer.appendLine("</head>")
            writer.appendLine("<body>")
            writer.appendLine("<div class=\"card\">")

            // Title row: URL + copy button
            writer.appendLine("<div class=\"title-row\">")
            writer.appendLine("<h1 class=\"url\" id=\"url-text\">${escapeHtml(call.request.url)}</h1>")
            writer.appendLine(copyButton(targetId = "url-text", label = "Copy URL"))
            writer.appendLine("</div>\n")

            // Badges row: method + status + meta
            writer.appendLine("<div class=\"meta\">")
            writer.appendLine("<span class=\"method-badge $methodClass\">${escapeHtml(call.request.method)}</span>")
            writer.appendLine("<span class=\"badge $statusClass\">${escapeHtml(statusLabel)}</span>")
            writer.appendLine("<span class=\"meta-item\"><strong>Time:</strong> ${escapeHtml(call.request.startTimeFormatted)}</span>")
            writer.appendLine("<span class=\"meta-item\"><strong>Duration:</strong> ${escapeHtml(call.response?.durationFormatted ?: "-")}</span>")
            writer.appendLine("<span class=\"meta-item\"><strong>Size:</strong> ${escapeHtml(call.responseByteSizeFormatted() ?: "-")}</span>")
            writer.appendLine("</div>")

            // Tabs
            writer.appendLine("<div class=\"tabs\">")
            writer.appendLine("<button class=\"tab-btn active\" data-tab=\"request-tab\" onclick=\"showTab(this)\">Request</button>")
            writer.appendLine("<button class=\"tab-btn\" data-tab=\"response-tab\" onclick=\"showTab(this)\">Response</button>")
            writer.appendLine("</div>")

            // Request tab
            writer.appendLine("<div id=\"request-tab\" class=\"tab-content active\">")
            writer.appendLine(
                sectionWithCopy(
                    title = "Request Headers",
                    rawId = "request-headers-raw",
                    rawText = headersToRawText(call.request.headers),
                    visibleContent = headersTable(call.request.headers)
                )
            )
            if (!call.request.body.isNullOrBlank()) {
                val prettyBody = jsonFormatter.toPrettyJson(call.request.body)
                writer.appendLine(
                    sectionWithCopy(
                        title = "Request Body",
                        rawId = "request-body-raw",
                        rawText = prettyBody,
                        visibleContent = codeBlock(prettyBody)
                    )
                )
            }
            writer.appendLine("</div>")

            // Response tab
            writer.appendLine("<div id=\"response-tab\" class=\"tab-content\">")
            call.responseHeaders()?.let { headers ->
                writer.appendLine(
                    sectionWithCopy(
                        title = "Response Headers",
                        rawId = "response-headers-raw",
                        rawText = headersToRawText(headers),
                        visibleContent = headersTable(headers)
                    )
                )
            }
            if (!call.responseBody().isNullOrBlank()) {
                val prettyBody = call.responseBody()?.let { jsonFormatter.toPrettyJson(it) } ?: ""
                writer.appendLine(
                    sectionWithCopy(
                        title = "Response Body",
                        rawId = "response-body-raw",
                        rawText = prettyBody,
                        visibleContent = codeBlock(prettyBody)
                    )
                )
            } else if (call.response is FloconNetworkCallDomainModel.Response.Failure) {
                val issue = call.response.issue
                writer.appendLine(
                    sectionWithCopy(
                        title = "Error",
                        rawId = "response-error-raw",
                        rawText = issue,
                        visibleContent = codeBlock(issue),
                        variant = "error"
                    )
                )
            }
            writer.appendLine("</div>")

            writer.appendLine("</div>") // card
            writer.appendLine("<script>${script()}</script>")
            writer.appendLine("</body>")
            writer.appendLine("</html>")
        }
    }

    private fun headersToRawText(headers: Map<String, String>): String = headers.entries.joinToString("\n") { (key, value) -> "$key: $value" }

    private fun headersTable(headers: Map<String, String>): String = buildString {
        appendLine("<table class=\"headers\">")
        headers.forEach { (key, value) ->
            appendLine(
                "<tr><td class=\"key\">${escapeHtml(key)}</td><td class=\"value\">${
                    escapeHtml(
                        value
                    )
                }</td></tr>"
            )
        }
        appendLine("</table>")
    }

    private fun codeBlock(content: String): String = "<pre class=\"code\"><code>${escapeHtml(content)}</code></pre>"

    private fun copyButton(targetId: String, label: String = "Copy"): String = "<button class=\"copy-btn\" onclick=\"copyToClipboard('$targetId', this)\">$label</button>"

    private fun sectionWithCopy(
        title: String,
        rawId: String,
        rawText: String,
        visibleContent: String,
        variant: String = "default",
    ): String = buildString {
        appendLine("<details class=\"section $variant\" open>")
        appendLine("<summary>")
        appendLine("<span>$title</span>")
        appendLine(copyButton(targetId = rawId, label = "Copy"))
        appendLine("</summary>")
        appendLine("<div class=\"section-content\">$visibleContent</div>")
        // Hidden element holding the exact raw text to copy, decoupled from visible styling
        appendLine("<pre id=\"$rawId\" style=\"display:none\">${escapeHtml(rawText)}</pre>")
        appendLine("</details>")
    }

    private fun methodBadgeClass(method: String): String = when (method.uppercase()) {
        "GET" -> "method-get"
        "POST" -> "method-post"
        "PUT" -> "method-put"
        "PATCH" -> "method-patch"
        "DELETE" -> "method-delete"
        else -> "method-default"
    }

    private fun statusBadgeClass(status: String): String {
        val code = status.toIntOrNull()
        return when {
            status == "Pending" -> "badge-pending"
            code == null -> "badge-pending"
            code in 200..299 -> "badge-success"
            code in 300..399 -> "badge-redirect"
            else -> "badge-error"
        }
    }

    private fun escapeHtml(text: String): String = text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")

    private fun styleSheet(): String = """
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background-color: #1e1e1e;
            color: #e0e0e0;
            padding: 24px;
            line-height: 1.5;
        }
        .card {
            max-width: 900px;
            margin: 0 auto;
            background: #252526;
            border-radius: 8px;
            padding: 24px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.4);
        }
        .title-row {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 14px;
        }
        .url {
            font-size: 18px;
            word-break: break-all;
            color: #9cdcfe;
            margin: 0;
            flex: 1;
        }
        .meta {
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            align-items: center;
            margin-bottom: 20px;
            font-size: 14px;
        }
        .meta-item { color: #bbbbbb; }
        .badge, .method-badge {
            padding: 4px 10px;
            border-radius: 12px;
            font-weight: bold;
            font-size: 13px;
        }
        .badge-success { background-color: #1e4620; color: #4caf50; }
        .badge-redirect { background-color: #4a3f1a; color: #ffc107; }
        .badge-error { background-color: #4a1e1e; color: #f44336; }
        .badge-pending { background-color: #333; color: #aaaaaa; }
        .method-get { background-color: #1a3a4a; color: #4fc3f7; }
        .method-post { background-color: #1e4620; color: #66bb6a; }
        .method-put { background-color: #4a3a1a; color: #ffb74d; }
        .method-patch { background-color: #3a1a4a; color: #ba68c8; }
        .method-delete { background-color: #4a1e1e; color: #e57373; }
        .method-default { background-color: #333; color: #aaaaaa; }
        .tabs {
            display: flex;
            gap: 4px;
            border-bottom: 1px solid #3a3a3a;
            margin-bottom: 16px;
        }
        .tab-btn {
            background: none;
            border: none;
            color: #aaaaaa;
            padding: 10px 16px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            border-bottom: 2px solid transparent;
        }
        .tab-btn.active {
            color: #e0e0e0;
            border-bottom: 2px solid #4fc3f7;
        }
        .tab-content { display: none; }
        .tab-content.active { display: block; }
        details.section {
            margin-bottom: 12px;
            border: 1px solid #3a3a3a;
            border-radius: 6px;
            overflow: hidden;
        }
        details.section.error { border-color: #f44336; }
        summary {
            cursor: pointer;
            padding: 10px 14px;
            background-color: #2d2d2d;
            font-weight: bold;
            font-size: 14px;
            list-style: none;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        summary::-webkit-details-marker { display: none; }
        .section-content { padding: 12px 14px; background-color: #1e1e1e; }
        table.headers { width: 100%; border-collapse: collapse; font-size: 13px; }
        table.headers td { padding: 4px 8px; vertical-align: top; border-bottom: 1px solid #333; }
        table.headers .key { color: #9cdcfe; white-space: nowrap; font-weight: bold; }
        table.headers .value { color: #ce9178; word-break: break-all; }
        pre.code {
            background-color: #0d0d0d;
            padding: 12px;
            border-radius: 6px;
            overflow-x: auto;
            font-family: 'Consolas', monospace;
            font-size: 13px;
            white-space: pre-wrap;
            word-break: break-word;
        }
        .copy-btn {
            background-color: #333;
            color: #e0e0e0;
            border: 1px solid #555;
            border-radius: 4px;
            padding: 4px 10px;
            font-size: 12px;
            cursor: pointer;
        }
        .copy-btn:hover { background-color: #444; }
    """.trimIndent()

    private fun script(): String = """
        function showTab(btn) {
            const targetId = btn.getAttribute('data-tab');
            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));
            document.getElementById(targetId).classList.add('active');
            btn.classList.add('active');
        }

        function copyToClipboard(targetId, btn) {
            const el = document.getElementById(targetId);
            const text = el.innerText;
            navigator.clipboard.writeText(text).then(() => {
                const original = btn.textContent;
                btn.textContent = 'Copied!';
                setTimeout(() => { btn.textContent = original; }, 1500);
            });
        }
    """.trimIndent()
}
