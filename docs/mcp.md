---
title: AI Agents (MCP)
description: Let Claude Code, Cursor, Codex, Gemini CLI or Antigravity inspect the network calls captured by Flocon, mock them, and read the files, analytics events and databases of the app, through the Model Context Protocol.
---

# 🤖 AI Agents (MCP)

Flocon Desktop embeds an **MCP server** ([Model Context Protocol](https://modelcontextprotocol.io)), so AI coding agents like **Claude Code**, **Cursor**, **Codex**, **Gemini CLI** or **Antigravity** can inspect the network calls made by your mobile app, mock them, and read its files, analytics events and databases, without any copy / paste.

> *"List the failed calls of the last launch and explain why the login fails"*

> *"Make the login call return a 500 so I can check the error screen"*

> *"Read the json cached by the app for the feed and check if it's up to date"*

> *"Check that the `post_liked` event is sent with the right properties when I like a post"*

> *"Check in the database if the user I just created is saved with the right email"*

The server starts automatically with Flocon Desktop and listens on:

```
http://localhost:9042/mcp
```

!!! info "Local only"
    The MCP server only listens on `127.0.0.1`: it is not reachable from your local network.
    Agents can't modify, delete or replay the captured calls, nor modify or delete the files of the app: the only tools modifying Flocon are the **network mocks** ones.
    Only read only SQL queries (`SELECT`, `PRAGMA` without value, `EXPLAIN`) can be executed on the databases.

---

## Setup

Declare the Flocon server in your agent configuration. Flocon Desktop must be running.

=== "Claude Code"

    With the Flocon plugin (recommended), it declares the server and adds a skill explaining the tools to Claude:

    ```
    /plugin marketplace add openflocon/Flocon
    /plugin install flocon@flocon
    ```

    Or manually, for you only:

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

    With the Flocon plugin: in the Cursor dashboard, open **Plugins & MCPs → Add Marketplace → Import from Repo**, paste `https://github.com/openflocon/Flocon`, then install **flocon** from **Customize**.

    Or manually, in `.cursor/mcp.json` (project) or `~/.cursor/mcp.json` (global):

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

    With the Flocon plugin:

    ```bash
    codex plugin marketplace add openflocon/Flocon
    ```

    Then install **flocon** from the `/plugins` browser.

    Or manually, in `~/.codex/config.toml`:

    ```toml
    [mcp_servers.flocon]
    url = "http://localhost:9042/mcp"
    ```

=== "Gemini CLI"

    With the Flocon extension, from a clone of this repository:

    ```bash
    gemini extensions install ./plugins/flocon
    ```

    Or manually:

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

    With the Flocon plugin:

    ```bash
    agy plugin install https://github.com/openflocon/Flocon/tree/main/plugins/flocon
    ```

    Or manually:

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

## Examples

Just ask your agent in plain English. Here is what you can ask and how the agent responds:

| You ask | Agent response |
|---|---|
## Examples

Just ask your agent in plain English. Here is what you can ask and how the agent responds:

| You ask | Agent response |
|---|---|
| *"Which calls failed since the last app launch?"* | *"The call `POST /v1/auth/login` failed with status `401 Unauthorized`. The response body returned: `{"error": "invalid_credentials"}`."* |
| *"Show me the last 10 requests on /login"* | *"Found 10 calls: 8 succeeded with `200 OK` (average 120 ms) and 2 failed with `401 Unauthorized` at 14:22:01 and 14:21:45."* |
| *"Did the app get any 401 or 403 errors?"* | *"Yes, 2 calls returned a `401` on `/v1/auth/refresh` around 10:14 AM. No `403` status codes were recorded."* |
| *"Find the call that returned product ID 98412"* | *"Found call `GET /v2/products/featured`: the string `"id": 98412` is present in the response body."* |
| *"Make the checkout call return a 500 error"* | *"Mock created: subsequent calls to `POST /v1/checkout` will now return an HTTP `500 Internal Server Error`. You can now trigger the action in the app."* |
| *"Simulate a 2-second timeout on /feed"* | *"Mock created: all requests matching `/feed` will now wait 2,000 ms and throw a `SocketTimeoutException`."* |
| *"Return an empty list with a 3-second delay on /notifications"* | *"Mock created: `GET /notifications` will respond after 3,000 ms with HTTP `200 OK` and body `[]`."* |
| *"Simulate a DNS failure on the payment endpoint"* | *"Mock created: all requests matching `.*/checkout/pay` will immediately throw a `java.net.UnknownHostException`."* |
| *"Which mocks are currently active?"* | *"Two mocks are active: `POST /v1/checkout` returning HTTP `500`, and `GET /notifications` returning `200 OK` with a 3,000 ms delay."* |
| *"Disable the mock on the checkout endpoint"* | *"Disabled mock for `POST /v1/checkout`. Live network calls will now hit your backend normally."* |
| *"Simulate a flaky network with random errors"* | *"Network simulation active: 30% of outgoing requests will randomly fail with either a timeout or an HTTP 503."* |
| *"Turn off the bad network simulation"* | *"Network simulation disabled. All network traffic is back to normal latency and reliability."* |
| *"Download the cached avatar of the logged-in user"* | *"Downloaded image from `/caches/avatars/user_42.png` (84 KB). Local copy saved to `.flocon/downloads/user_42.png`."* |
| *"Was the post_liked event sent when I liked the post?"* | *"Yes, `post_liked` was recorded 4 seconds ago in Amplitude with properties: `post_id: "anOoozPo4GM2"`, `source: "feed"`."* |
| *"How many analytics events were sent since launch?"* | *"Recorded 24 events: 14 `screen_view`, 6 `button_click`, 3 `scroll_feed`, and 1 `app_open`."* |
| *"Did Amplitude track the checkout event?"* | *"Checked both trackers: `checkout_completed` was recorded in Amplitude (16:04:12) with matching `order_id: 1042`."* |
| *"Check in the database if the user was saved with the right email"* | *"Found record in SQLite table `users`: `id: 42`, `email: "john.doe@example.com"`, `created_at: 1791903486`."* |
| *"Which queries did the app run on the users table?"* | *"Last queries executed: `SELECT * FROM users WHERE id = 42` followed by `UPDATE users SET last_login = 1791903500 WHERE id = 42`."* |
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
| `list_bad_network_configs` | Bad network configs of the app (latency, random errors) and the active one. |
| `create_bad_network_config` | Creates a config: random latency, and errors (HTTP response or exception) triggered with a probability. Optionally active right away. |
| `update_bad_network_config` | Updates some fields of a config. |
| `set_active_bad_network_config` | Activates a config (only one at a time), or turns the simulation off when no `configId` is given. |
| `delete_bad_network_config` | Deletes a config. |
| `list_files` | Content of a directory of the app: `files`, `caches`, or the absolute path of a sub directory. |
| `read_file` | Downloads a file of the app: text files returned as text (truncated if too long), images as images, metadata only for the other binary files. A copy is saved on your computer (`localPath`). |
| `list_analytics_tables` | Analytics tables of the app: one per analytics SDK / tracker (ex: `amplitude`). |
| `list_analytics_events` | Analytics events with their properties, newest first. Filters: table, exact event names, text, start timestamp, previous sessions. |
| `count_analytics_events` | Number of events per event name, the most frequent first (same filters). |
| `list_databases` | SQLite databases of the app (Room, SQLDelight...), with their `databaseId`. |
| `list_database_tables` | Schema of a database: tables, columns, types, primary keys. |
| `query_database` | Executes a read only SQL query and returns the rows (100 by default, long values truncated). |
| `list_database_query_logs` | SQL queries executed by the app, newest first, with their arguments ([query logging](database.md#real-time-query-logging) must be set up in the app). |

---

## Testing the server

You can browse the tools with the [MCP Inspector](https://github.com/modelcontextprotocol/inspector):

```bash
npx @modelcontextprotocol/inspector
```

Then connect to `http://localhost:9042/mcp` with the **Streamable HTTP** transport.

!!! warning "Port already used"
    If the port `9042` is already used by another program, Flocon Desktop displays an error and retries every 10 seconds.