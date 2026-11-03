---
title: Timeline
description: Follow everything your app did, in order, on a single screen: network calls, analytics, database queries, crashes...
---

# 🕒 Timeline

The timeline merges the events received by Flocon Desktop into **one chronological list**, grouped by day and by app session, so you can see what happened right before a crash or an HTTP error.

---

## Overview

From the desktop UI, you can:

- **Follow the events in order**: network calls (a request line with a → arrow, then a response line with a ← arrow when it arrives; selecting or hovering one highlights both, and hovering shows a bar linking them), analytics events, WebView events (console messages, page loads and errors, bridge calls), database queries, table rows, crashes, deeplinks, ADB commands and app starts.
- **Switch between the list and the graph** (the **List** and **Graph** chips next to the filter bar, the choice is remembered), see [Graph](#graph).
- **Read the counters**: number of events, sessions, HTTP errors and crashes of what is displayed.
- **Options** (⋮ menu): auto scroll to the newest event, invert the direction of the list, pin the detail panel (it then stays next to the list instead of covering it).
- **Filter**: toggle the sources (the selection is remembered) and search a text (`Ctrl+K` / `Cmd+K` focuses the search field).
- **Open an event**: click a row to open its detail in a side panel (network call, analytics event, database query, table row, crash, deeplink, ADB command). App starts and WebView events have nothing to open: the WebViews screen shows them.

---

## Graph

The **Graph** view shows one session at a time on a time axis, with the app start at 0. The filters and the selected event are shared with the list.

- **One lane per type**: App, Network, Database, Analytics (a lane without event is hidden). The other sources only appear in the list.
- **A network call is one bar**, from its request to its response. The method and an image icon are shown when the bar is wide enough, and a failed call (HTTP 4xx or 5xx) is red. A call still waiting for its response goes up to the last event received.
- **Database queries and analytics events are diamonds**: they have no duration.
- **Events that do not overlap share the same row**, to keep the graph short.

| Action | Result |
|---|---|
| Click a bar or a diamond | Opens the event in the detail panel and selects it in the list too |
| Mouse wheel | Scrolls the lanes |
| `Ctrl` / `Cmd` + wheel, `+` / `-` buttons | Zooms around the pointer |
| `Shift` + wheel, drag | Moves along the time axis |
| **Fit** | Shows the whole session again and follows the new events |
| `‹` / `›` | Previous / next session |

---

## Setup

There is nothing to add for the timeline itself: it is built on the desktop from the events of the other plugins. Each source only shows up once its plugin is installed in your app, see:

- [Network](network.md#setup-integration)
- [Analytics](analytics.md#setup)
- [Databases](database.md#setup)
- [Data Tables](table.md#setup)
- [Crash Reporter](crashreporter.md#setup)
- [Deeplinks](deeplink.md)
- [WebViews](webview.md#setup)

!!! tip
    Start with [Getting Started](setup.md) to connect your app to Flocon Desktop.
