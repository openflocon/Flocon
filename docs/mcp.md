---
title: AI Agents (MCP)
description: Let Claude Code, Cursor, Codex, Gemini CLI or Antigravity inspect the network calls captured by Flocon and mock them, through the Model Context Protocol.
---

# 🤖 AI Agents (MCP)

Flocon Desktop embeds an **MCP server** ([Model Context Protocol](https://modelcontextprotocol.io)), so AI coding agents like **Claude Code**, **Cursor**, **Codex**, **Gemini CLI** or **Antigravity** can inspect the network calls made by your mobile app and mock them, without any copy / paste.

> *"List the failed calls of the last launch and explain why the login fails"*

> *"Make the login call return a 500 so I can check the error screen"*

The server starts automatically with Flocon Desktop and listens on:

```
http://localhost:9042/mcp
```

!!! info "Local only"
    The MCP server only listens on `127.0.0.1`: it is not reachable from your local network.
    Agents can't modify, delete or replay the captured calls: the only tools modifying Flocon are the **network mocks** ones.

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

=== "Gemini CLI"

    ```bash
    gemini mcp add --transport http flocon http://localhost:9042/mcp
    ```

    Or in `.gemini/settings.json` (project) or `~/.gemini/settings.json` (global):

    ```json
    {
      "mcpServers": {
        "flocon": {
          "httpUrl": "http://localhost:9042/mcp"
        }
      }
    }
    ```

    !!! note
        Gemini CLI uses `httpUrl` for Streamable HTTP servers: `url` is for the older SSE transport.

=== "Antigravity"

    In the Agent panel, open **⋯ → MCP Servers → Manage MCP Servers → View raw config**, and add:

    ```json
    {
      "mcpServers": {
        "flocon": {
          "serverUrl": "http://localhost:9042/mcp"
        }
      }
    }
    ```

    Then click **Refresh** in the MCP servers list.

    !!! note
        Antigravity uses `serverUrl` for remote HTTP servers, not `url` nor `httpUrl`.

---

## Available tools

By default, every tool targets the **device and app currently selected in Flocon Desktop**. Use `list_devices` then pass `deviceId` / `packageName` to target another one.

| Tool | Description |
|---|---|
| `list_devices` | Devices and apps known by Flocon, the selected one, and whether they are connected. |
| `list_network_calls` | Network calls (HTTP, GraphQL, gRPC, WebSocket), newest first, without bodies. Filters: full text, url, methods, HTTP status codes, errors only, type, start timestamp, previous sessions. |
| `get_network_call` | Full details of a call: request & response headers and bodies (truncated if too long), GraphQL query, gRPC status, error, and the equivalent cURL command. |
| `search_network_calls` | Searches a text in headers, bodies, url, method or status. |
| `list_network_mocks` | Network mocks of the app (and the ones shared with all apps), without bodies. |
| `get_network_mock` | Full details of a mock, including the mocked body and headers. |
| `create_network_mock` | Creates a mock returning a body or throwing an exception, from scratch or from a captured call (`fromCallId`). |
| `update_network_mock` | Updates some fields of a mock, or enables / disables it. |
| `delete_network_mock` | Deletes a mock. |

---

## Examples

Just ask your agent in natural language, it picks the tool and the filters. Some examples, with the arguments the agent sends:

| You ask | Tool | Arguments |
|---|---|---|
| *"Which calls failed since the last app launch?"* | `list_network_calls` | `{"onlyErrors": true}` |
| *"Show me the last 10 POST calls on /login"* | `list_network_calls` | `{"methods": ["POST"], "urlContains": "/login", "limit": 10}` |
| *"Did the app get any 401 or 403?"* | `list_network_calls` | `{"statusCodes": [401, 403]}` |
| *"List the GraphQL calls, oldest first"* | `list_network_calls` | `{"type": "graphql", "order": "oldest_first"}` |
| *"Which calls were made in the previous sessions too?"* | `list_network_calls` | `{"includeOldSessions": true}` |
| *"Which request sends this token in its headers?"* | `search_network_calls` | `{"query": "eyJhbGci", "scopes": ["RequestHeader"]}` |
| *"Find the response that contains the user `john`"* | `search_network_calls` | `{"query": "john", "scopes": ["ResponseBody"]}` |
| *"Explain why this call failed"* | `get_network_call` | `{"callId": "..."}` |
| *"Give me the full body of this call"* | `get_network_call` | `{"callId": "...", "maxBodyChars": 200000}` |
| *"Make this call return a 500"* | `create_network_mock` | `{"fromCallId": "...", "httpCode": 500}` |
| *"Simulate a timeout on every call to /feed"* | `create_network_mock` | `{"urlPattern": ".*/feed.*", "exceptionClassPath": "java.net.SocketTimeoutException"}` |
| *"Return an empty list of users, after 2 seconds"* | `create_network_mock` | `{"urlPattern": ".*/users", "method": "GET", "body": "[]", "delayMs": 2000}` |
| *"Which mocks are enabled?"* | `list_network_mocks` | `{"onlyEnabled": true}` |
| *"Disable this mock"* | `update_network_mock` | `{"mockId": "...", "enabled": false}` |
| *"Same on my iPhone"* | any tool | `{"deviceId": "...", "packageName": "..."}` (from `list_devices`) |

Available filters of `list_network_calls`:

| Argument | Description |
|---|---|
| `text` | Full text filter: url, method, status, duration and bodies. |
| `urlContains` | Url contains this text (case insensitive). |
| `methods` | HTTP methods, ex: `["GET", "POST"]`. |
| `statusCodes` | HTTP status codes, ex: `[401, 500]`. |
| `onlyErrors` | Exceptions, HTTP codes >= 400, GraphQL errors, gRPC status != `OK`. |
| `type` | `http`, `graphql`, `grpc` or `websocket`. |
| `sinceTimestamp` | Calls started after this epoch timestamp, in milliseconds. |
| `includeOldSessions` | Include previous app launches (default `false`). |
| `order` | `newest_first` (default) or `oldest_first`. |
| `limit` | Max number of calls, default 50, max 500. |

`search_network_calls` scopes: `RequestHeader`, `RequestBody`, `ResponseHeader`, `ResponseBody`, `Url`, `Method`, `Status` (all by default).

Arguments of `create_network_mock` and `update_network_mock`:

| Argument | Description |
|---|---|
| `fromCallId` | *(create only)* Captured call used as a template: its exact url, method and response are copied. |
| `urlPattern` | Regex matching the **whole** url, query included, ex: `.*/users/[0-9]+`. |
| `method` | `GET`, `POST`... or `*` for any method (default). |
| `httpCode`, `body`, `mediaType`, `headers` | Mocked response (default `200`, empty body, `application/json`). |
| `exceptionClassPath` | Throws this exception instead of returning a response, ex: `java.net.SocketTimeoutException`, `java.net.UnknownHostException`, `java.io.IOException`. |
| `delayMs` | Delay before the response, in milliseconds. |
| `enabled` | Whether the mock is active (default `true`). |
| `shared` | Applies the mock to all the devices and apps (default `false`: only the targeted app). |
| `displayName` | Name displayed in Flocon. |

The mocks are sent to the app as soon as they change: no need to restart it.

---

## Testing the server

You can browse the tools with the [MCP Inspector](https://github.com/modelcontextprotocol/inspector):

```bash
npx @modelcontextprotocol/inspector
```

Then connect to `http://localhost:9042/mcp` with the **Streamable HTTP** transport.

!!! warning "Port already used"
    If the port `9042` is already used by another program, Flocon Desktop displays an error and retries every 10 seconds.
