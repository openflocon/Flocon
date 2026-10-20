---
name: flocon-debug
description: Use when debugging a mobile app (Android, iOS, KMP) connected to Flocon Desktop - inspecting network calls, mocking a response or error, reading app files, analytics events, crashes or SQLite databases.
---

# Debugging a mobile app with Flocon

Flocon Desktop receives what happens in the mobile app connected to it and exposes it through the `flocon` MCP server (tools `mcp__flocon__*`).

## Prerequisites

- Flocon Desktop must be running (the MCP server listens on `http://localhost:9042/mcp`, loopback only).
- The app must be connected to Flocon. If a tool fails to connect or returns no device, ask the user to start Flocon Desktop and the app.

## Targeting

By default every tool targets the device and app selected in Flocon Desktop. Call `list_devices` to see the others, then pass `deviceId` and `packageName`.

## What to use for what

| Goal | Tools |
|---|---|
| Find a failing or slow call | `list_network_calls`, `search_network_calls` (summaries), then `get_network_call` with a `callId` for headers, bodies and the cURL command |
| Make the app receive a fake response or exception | `create_network_mock` (`fromCallId` starts from a captured call), `update_network_mock`, `list_network_mocks`, `get_network_mock`, `delete_network_mock` |
| Check a file written by the app | `list_files` ("files" / "caches" directories), `read_file` |
| Read a custom dashboard of the app | `list_dashboards`, `get_dashboard` for its elements and values |
| Investigate a crash | `list_crash_reports` for the exception type and message, `get_crash_report` for the full stack trace |
| Check analytics events | `count_analytics_events` for an overview per event name, `list_analytics_events` for the properties, `list_analytics_tables` |
| Check which images the app loaded | `list_images` (urls and load times) |
| Check persisted data | `list_databases` (gives the `databaseId`), `list_database_tables` (schema), `query_database`, `list_database_query_logs` (queries run by the app) |
| Check custom tables | `list_tables` (gives the `tableId`), `list_table_items` (rows, optional text filter) |

## Rules

- Mocks change the app behavior: prefer disabling a mock (`update_network_mock`) over deleting it, and tell the user which mocks you left enabled.
- `query_database` is read only (`SELECT`, `PRAGMA` without value, `EXPLAIN`): always add a `LIMIT`.
- Files, calls and databases cannot be modified, only mocks can.
- Start from summaries and fetch details only for the calls you need, bodies can be large.
