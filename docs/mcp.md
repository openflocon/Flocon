---
title: AI Agents (MCP)
description: Let Claude Code, Cursor or Codex inspect the network calls captured by Flocon, through the Model Context Protocol.
---

# 🤖 AI Agents (MCP)

Flocon Desktop embeds an **MCP server** ([Model Context Protocol](https://modelcontextprotocol.io)), so AI coding agents like **Claude Code**, **Cursor** or **Codex** can inspect the network calls made by your mobile app, without any copy / paste.

> *"List the failed calls of the last launch and explain why the login fails"*

The server starts automatically with Flocon Desktop and listens on:

```
http://localhost:9042/mcp
```

!!! info "Local only"
    The MCP server only listens on `127.0.0.1`: it is not reachable from your local network.
    It is **read only**: agents can't modify, delete or replay the captured calls.

---

## Setup

No plugin is needed: declare the Flocon server in your agent configuration. Flocon Desktop must be running.

=== "Claude Code"

    For you only:

    ```bash
    claude mcp add --transport http flocon http://localhost:9042/mcp
    ```

    Or for the whole team, add a `.mcp.json` file at the root of your mobile project:

    ```json
    {
      "mcpServers": {
        "flocon": {
          "type": "http",
          "url": "http://localhost:9042/mcp"
        }
      }
    }
    ```

=== "Cursor"

    In `.cursor/mcp.json` (project) or `~/.cursor/mcp.json` (global):

    ```json
    {
      "mcpServers": {
        "flocon": {
          "url": "http://localhost:9042/mcp"
        }
      }
    }
    ```

=== "Codex"

    In `~/.codex/config.toml`:

    ```toml
    [mcp_servers.flocon]
    url = "http://localhost:9042/mcp"
    ```

---

## Available tools

By default, every tool targets the **device and app currently selected in Flocon Desktop**. Use `list_devices` then pass `deviceId` / `packageName` to target another one.

| Tool | Description |
|---|---|
| `list_devices` | Devices and apps known by Flocon, the selected one, and whether they are connected. |
| `list_network_calls` | Network calls (HTTP, GraphQL, gRPC, WebSocket), newest first, without bodies. Filters: full text, url, methods, HTTP status codes, errors only, type, start timestamp, previous sessions. |
| `get_network_call` | Full details of a call: request & response headers and bodies (truncated if too long), GraphQL query, gRPC status, error, and the equivalent cURL command. |
| `search_network_calls` | Searches a text in headers, bodies, url, method or status. |

---

## Testing the server

You can browse the tools with the [MCP Inspector](https://github.com/modelcontextprotocol/inspector):

```bash
npx @modelcontextprotocol/inspector
```

Then connect to `http://localhost:9042/mcp` with the **Streamable HTTP** transport.

!!! warning "Port already used"
    If the port `9042` is already used by another program, Flocon Desktop displays an error and retries every 10 seconds.
