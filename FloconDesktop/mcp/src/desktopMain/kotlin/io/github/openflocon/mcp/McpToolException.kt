package io.github.openflocon.mcp

/**
 * Error displayed as is to the AI agent (tool result with isError = true),
 * it should explain how to fix the call
 */
class McpToolException(message: String) : Exception(message)
