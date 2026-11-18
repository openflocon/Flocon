package io.github.openflocon.domain.mcp.repository

/**
 * Model Context Protocol server, exposing Flocon data to AI agents (Claude Code, Cursor, Codex...)
 */
interface McpServerRepository {
    fun startServer()
}
