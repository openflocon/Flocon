package io.github.openflocon.domain.mcp.usecase

import io.github.openflocon.domain.mcp.repository.McpServerRepository

class StartMcpServerUseCase(
    private val mcpServerRepository: McpServerRepository,
) {
    operator fun invoke() = mcpServerRepository.startServer()
}
