---
title: Timeline
description: Follow everything your app did, in order, on a single screen: network calls, analytics, database queries, crashes...
---

# 🕒 Timeline

The timeline merges the events received by Flocon Desktop into **one chronological list**, grouped by day and by app session, so you can see what happened right before a crash or an HTTP error.

---

## Overview

From the desktop UI, you can:

- **Follow the events in order**: network calls (a request line with a → arrow, then a response line with a ← arrow when it arrives; selecting or hovering one highlights both, and hovering shows a bar linking them), analytics events, database queries, table rows, crashes, deeplinks, ADB commands and app starts.
- **Read the counters**: number of events, sessions, HTTP errors and crashes of what is displayed.
- **Filter**: toggle the sources and search a text (`Ctrl+K` / `Cmd+K` focuses the search field).
- **Open an event**: click a row to open its detail in a side panel (network call, analytics event, database query, table row, crash, deeplink, ADB command). App starts have nothing to open.

---

## Setup

There is nothing to add for the timeline itself: it is built on the desktop from the events of the other plugins. Each source only shows up once its plugin is installed in your app, see:

- [Network](network.md#setup-integration)
- [Analytics](analytics.md#setup)
- [Databases](database.md#setup)
- [Data Tables](table.md#setup)
- [Crash Reporter](crashreporter.md#setup)
- [Deeplinks](deeplink.md)

!!! tip
    Start with [Getting Started](setup.md) to connect your app to Flocon Desktop.
