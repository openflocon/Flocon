---
title: AI Agents (MCP)
description: Let Claude Code, Cursor, Codex, Gemini CLI, Antigravity, GitHub Copilot, Android Studio, Windsurf, Zed or OpenCode see what Flocon sees (network, databases, preferences, crashes, timeline...) and act on your app, through the Model Context Protocol.
---

# 🤖 AI Agents (MCP)

Flocon Desktop embeds an **MCP server** ([Model Context Protocol](https://modelcontextprotocol.io)). Once your AI coding agent is connected to it, the agent sees what you see in Flocon, without any copy / paste, and can act on your running app.

> *"List the failed calls of the last launch and explain why the login fails"*

> *"Make the login call return a 500 so I can check the error screen"*

> *"What happened in the app just before it crashed?"*

## What your agent gets

**It can see** everything Flocon captured from the connected app: the [network calls](network.md) (HTTP, GraphQL, gRPC, WebSocket) with bodies and a ready cURL, the [databases](database.md), the [preferences](sharedpref.md), the app [files](files.md), the [analytics events](analytics.md), the [feature flags](featureflags.md), the [crashes](crashreporter.md), the [images](image.md), the [accessibility](accessibility.md) and [WebView](webview.md) events, the [dashboards](dashboard.md) and [tables](table.md), and a single [timeline](timeline.md) of what the app did around a moment.

**It can act**:

- **On app behavior**: create and edit [network mocks](mocking.md), simulate a [bad network](bad-quality.md), inject a WebSocket message, edit the value of an existing preference key.
- **On the device**: open a [deeplink](deeplink.md), take a screenshot, record the screen, move the accessibility focus.
- **On Flocon Desktop**: show a zone of the timeline or a screen in the window, change the server ports.

All the tools are listed in the [Tools Reference](mcp-tools.md), and real workflows are in the [Cookbook](mcp-cookbook.md).

!!! info "Local only"
    The MCP server listens on `127.0.0.1` only: it is not reachable from your local network. It has no authentication, so any program running on your computer can call it while Flocon Desktop is open.

    Captured data is read-only: an agent cannot modify, delete or replay network calls, files, databases, analytics events, feature flags or crashes. The one exception is the value of an **existing** preference key, with `set_shared_preference_value`. Only read-only SQL (`SELECT`, `PRAGMA` without value, `EXPLAIN`) runs on databases.

    Every tool is flagged read-only or not in the [Tools Reference](mcp-tools.md#legend), and agents are told to name what they changed (mocks left enabled, active bad network config...). Flocon only counts, anonymously, which tool is called and the kind of client: see [Telemetry](telemetry.md).

## Which apps feed which data

The tools are the same for every SDK, but each tool returns what **your app's SDK sends**. An empty answer for a feature your app does not use is normal, not a bug.

| Data | Kotlin (Android / KMP) | Swift | Flutter |
|---|:---:|:---:|:---:|
| Network, mocks, bad network | ✅ | ✅ (instrumented `URLSession`s) | ✅ (Dio, `package:http`) |
| Databases, preferences, files | ✅ | ✅ | ✅ |
| Analytics, feature flags, tables, dashboards, deeplinks, images | ✅ | ✅ | ✅ |
| Timeline logs, crashes | ✅ | ✅ | ✅ (Dart and Flutter errors) |
| WebView events, accessibility events | ✅ | ❌ | ❌ |
| WebSocket message injection | ✅ (OkHttp) | ❌ | ❌ |

See the [platform support matrix](index.md#platform-support) for the details per feature. Screenshots, recordings and deeplinks need `adb` on Android, or a booted Simulator on iOS.

## Setup

Flocon Desktop must be running. The server starts with it and listens on:

```
http://localhost:9042/mcp
```

It uses the **Streamable HTTP** transport (stateless). Declare it in your agent:

=== "Claude Code"

    With the Flocon plugin (recommended), it declares the server and adds two skills ([see below](#the-two-skills)):

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

    With the Flocon extension (it declares the server only, not the skills), from a clone of this repository:

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

    Or manually: in the Agent panel, open **⋯ → MCP Servers → Manage MCP Servers → View raw config**, and add:

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

=== "VS Code / Copilot"

    In `.vscode/mcp.json` (project), or with **MCP: Open User Configuration** (global):

    ```json
    {
      "servers": {
        "flocon": {
          "type": "http",
          "url": "http://localhost:9042/mcp"
        }
      }
    }
    ```

    !!! note
        VS Code uses `servers`, not `mcpServers`. The tools are then available in the Copilot Chat agent mode.

=== "Android Studio"

    Gemini in Android Studio (Agent mode): in the IDE settings, enable MCP servers in the AI section, then paste in the configuration field:

    ```json
    {
      "mcpServers": {
        "flocon": {
          "httpUrl": "http://localhost:9042/mcp"
        }
      }
    }
    ```

    Type `/mcp` in the chat to check that the Flocon tools are available.

    !!! note
        Android Studio uses `httpUrl` and only supports remote HTTP servers, which is the case of Flocon.

=== "Windsurf"

    Edit `~/.codeium/windsurf/mcp_config.json` (or use the MCP settings of the Cascade panel):

    ```json
    {
      "mcpServers": {
        "flocon": {
          "serverUrl": "http://localhost:9042/mcp"
        }
      }
    }
    ```

    !!! note
        Windsurf uses `serverUrl` for remote HTTP servers.

=== "Zed"

    In `settings.json` (**zed: open settings file**):

    ```json
    {
      "context_servers": {
        "flocon": {
          "url": "http://localhost:9042/mcp"
        }
      }
    }
    ```

    !!! note
        Zed uses `context_servers`, not `mcpServers`.

=== "OpenCode"

    In `opencode.json` (project) or `~/.config/opencode/opencode.json` (global):

    ```json
    {
      "$schema": "https://opencode.ai/config.json",
      "mcp": {
        "flocon": {
          "type": "remote",
          "url": "http://localhost:9042/mcp"
        }
      }
    }
    ```

## The two skills

The Flocon plugin (Claude Code, Codex, Cursor) also installs two skills: short instructions your agent loads when relevant.

| Skill | What it does |
|---|---|
| `flocon-mcp` | Teaches the agent how to debug with Flocon: which tool to use for what (start with `get_timeline`, then the detail tools), how targeting works, what to say when a call fails (Desktop not running, no device connected, unknown app), what each SDK sends, and the rules: prefer disabling a mock over deleting it, always turn bad network off, ask before editing a preference, tell you everything it changed. |
| `flocon-setup` | Installs Flocon in an existing project, or migrates it from 1.x to 2.0. It detects the platform (Android, Kotlin Multiplatform, Swift, Flutter), adds the dependencies, keeps Flocon out of release builds, installs only the plugins the project really uses, and checks that each feature is captured. See the [Cookbook](mcp-cookbook.md#setup-flocon). |

Without the plugin, the MCP server alone works too: the server sends its own usage instructions to the agent.

## A few examples

Just ask your agent in plain text.

**"My article page doesn't display, is it caused by a network call?"** The agent lists the failed calls, opens the one for the article, and tells you that `GET /articles/42` returned a **500**. See [Why does this request fail](mcp-cookbook.md#why-request-fails).

**"Make the articles endpoint fail with a 500 so I can check my error screen."** The agent creates a mock from a real call, changes only the status code, and offers to disable it afterwards. See [Mock an endpoint](mcp-cookbook.md#mock-error-state).

**"What happened in the app just before it crashed?"** The agent reads the timeline around the latest crash: a loop of calls, a 401, an analytics event two seconds before. See [Crash triage](mcp-cookbook.md#crash-triage).

## Testing the server

You can browse the tools with the [MCP Inspector](https://github.com/modelcontextprotocol/inspector):

```bash
npx @modelcontextprotocol/inspector
```

Then connect to `http://localhost:9042/mcp` with the **Streamable HTTP** transport. The **Tools** tab lists the 57 tools and lets you call them by hand.

## Port 9042

The MCP server always uses port **9042**: it cannot be changed from Flocon. This is not the port your apps use: they connect to Flocon Desktop on the websocket port (default 9023) and HTTP port (default 9024). Those two can be changed in the Flocon settings, or by an agent with `set_server_ports`. Port 9042 is reserved, so you cannot use it for them.

!!! warning "Port already used"
    If port `9042` is already used by another program, Flocon Desktop displays an error and retries every 10 seconds. Close the other program: the server starts by itself once the port is free.

If your agent says it cannot connect, check that Flocon Desktop is running, then see [Troubleshooting](troubleshooting.md).
