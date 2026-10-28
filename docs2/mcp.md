---
title: AI Agents (MCP)
description: Let Claude Code, Cursor, Codex, Gemini CLI or Antigravity inspect the network calls captured by Flocon, mock them, and read the files, analytics events, feature flags, crashes and databases of the app, through the Model Context Protocol.
---

# 🤖 AI Agents (MCP)

Flocon Desktop embeds an **MCP server** ([Model Context Protocol](https://modelcontextprotocol.io)), so AI coding agents like **Claude Code**, **Cursor**, **Codex**, **Gemini CLI** or **Antigravity** can inspect the network calls made by your mobile app, mock them, and read its files, analytics events, feature flags and databases, without any copy / paste.

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

Just ask your agent in plain text. Here is what you can ask and how the agent responds:

> Responses are illustrative: endpoints, ids and values depend on your app.

---

**Prompt**

> My article page doesn't display, is it caused by a network call?

**Response**

Yes. When the article page opened, `GET /articles/42` failed with a **500 Internal Server Error** (`{"error":"article_service_unavailable"}`). The app received no article data, which explains the empty page. Retrying the call would not help until the backend is fixed. I can mock a valid response if you want to check the UI in the meantime.

---

**Prompt**

> Among the posts displayed on screen, is there one created by "florent"?

**Response**

Yes. The last `GET /posts` response contains 12 posts, and one of them was written by `florent`: "Why I switched to Compose" (`id: post_7`). It is also stored locally: the `posts` table has a matching row with `author = "florent"`.

---

**Prompt**

> Add latency to all calls.

**Response**

Done. Every network call from the app now has an extra **2 s** of latency. Navigate in the app to check your loading states. Ask me to remove it whenever you're done.

---

**Prompt**

> Transform the response of `user/me` so the username is "jean.jacques.nom.a.rallonge".

**Response**

Done. I created a mock on `GET /user/me`. It returns the last real response with only `username` replaced by `jean.jacques.nom.a.rallonge`. Reopen the profile screen to check how the long name is displayed.

---

**Prompt**

> Does the article with id "ID1" exist in the database? If so, what is its price?

**Response**

Yes, the `articles` table contains `ID1`: "Wireless Headphones", with a price of **59.90 €**.

---

**Prompt**

> Was the `postViewed` analytics event triggered?

**Response**

Yes, `postViewed` was sent 3 times since the app started:

- `postViewed` → `{ postId: "post_7" }`
- `postViewed` → `{ postId: "post_3" }`
- `postViewed` → `{ postId: "post_12" }`

---

**Prompt**

> Which variant of the `new_checkout` feature flag is the app using?

**Response**

`new_checkout` (provider `firebase`) is on the `treatment` variant, with the payload `{ "buttonColor": "#FF5722", "steps": 3 }`.

---

**Prompt**

> Show me the slow calls in the app.

**Response**

3 calls took more than 1 second:

| Method | URL | Duration |
|--------|-----|----------|
| GET | `/posts/search?q=compose` | 3.4 s |
| GET | `/articles/ID1/reviews` | 2.1 s |
| POST | `/orders` | 1.3 s |

The search call is the slowest. It may be worth adding a loading state or debouncing the input.

---

**Prompt**

> Make the articles endpoint fail with a 500 so I can check my error screen.

**Response**

Mock created for `GET /articles/*`: status **500**, body `{"error":"internal_error"}`. Open an article in the app to see the error screen. I can disable the mock when you're done.

---

**Prompt**

> Record the checkout flow and show me what the screen looks like when the payment fails.

**Response**

Recording started on the Pixel 9. I tapped through the checkout, then stopped the recording: the video is saved at `~/Documents/Flocon/ScreenRecord/record_io.flocon.sample_1736000000000.mp4`. A screenshot of the failure shows an empty payment form with no error message, while `POST /payments` returned a **402**. The error is not displayed by the app.

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
| `list_feature_flags` | Feature flags of the app (provider, name, state on / off / custom, variant, payload preview), the providers. Filters: provider, text in the name / variant / payload. |
| `get_feature_flag` | A feature flag with its state, variant and full payload. |
| `list_accessibility_events` | Accessibility events received from the app (what TalkBack / VoiceOver get: focus, announcements, content changes), newest first. Filters: type, text. |
| `get_accessibility_state` | Whether automatic capture is supported and enabled, touch exploration, running accessibility services. |
| `get_accessibility_focused_node` | Asks the device which element TalkBack is on (text, content description, class, bounds, position in the focus order). Android only. |
| `move_accessibility_focus` | Moves the accessibility focus to the `next` or `previous` element (like a TalkBack swipe) and returns the element it landed on. Android only, needs TalkBack on. |
| `clear_accessibility_events` | Deletes the accessibility events received from the app. |
| `list_dashboards` | Ids of the dashboards sent by the app, and the one selected in Flocon Desktop. |
| `get_dashboard` | A dashboard with its containers and elements (texts, values, check boxes, text fields, buttons). Read only: nothing is triggered. |
| `list_images` | Images loaded by the app (url, load time), newest first. Filters: text, start timestamp. Optionally the headers. |
| `list_crash_reports` | Crashes reported by the app (exception type and message), newest first. Filters: text, start timestamp. |
| `get_crash_report` | Full details of a crash, including its stack trace. |
| `list_databases` | SQLite databases of the app (Room, SQLDelight...), with their `databaseId`. |
| `list_database_tables` | Schema of a database: tables, columns, types, primary keys. |
| `query_database` | Executes a read only SQL query and returns the rows (100 by default, long values truncated). |
| `list_deeplinks` | Deeplinks declared by the app (link, label, description, parameters) and their variables. |
| `list_deeplink_history` | Deeplinks recently opened from Flocon. |
| `open_deeplink` | Opens a deeplink on the device (`adb` on Android, the booted Simulator on iOS) and adds it to the history. Not read only: it acts on the device. |
| `take_screenshot` | Screenshot of the device screen (`adb` on Android, the booted Simulator on iOS), saved as a PNG in the Flocon documents folder (`Screenshot/`). Returns the path and the image, so the agent sees the screen. Not read only: it acts on the device. |
| `start_screen_recording` | Starts recording the device screen. One recording per device; on Android, `screenrecord` stops by itself after 3 minutes. |
| `stop_screen_recording` | Stops the recording and returns the path of the mp4 saved in `ScreenRecord/`. Without target, stops the only running recording. |
| `open_flocon_folder` | Opens the Flocon documents folder (screenshots, screen records, exports), or a sub folder (`Screenshot`, `ScreenRecord`), in the file manager of the computer, and returns its path. |
| `list_shared_preferences` | SharedPreferences files (Android) / UserDefaults suites (iOS) of the app, with their `sharedPreferenceId`. |
| `get_shared_preference_values` | Keys and typed values of a SharedPreferences file, as stored. Optional text filter (key or value) and limit. |
| `list_tables` | Custom tables sent by the app (Table plugin), with their `tableId`. |
| `list_table_items` | Rows of a table as column -> value maps, newest first. Filters: text, order, limit. |
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