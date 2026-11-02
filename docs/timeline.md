# Timeline

The Timeline shows every event of the app in one place: network calls, database queries, analytics events and more, grouped by day and by session (one session per start of the app).

## List and Graph

Switch between the two views with the **List** and **Graph** chips next to the filter bar. The choice is remembered.

The **Graph** shows one session at a time on a time axis, with `App start` at 0:

- one lane per type: App, Network, Database, Analytics (a lane without event is hidden);
- a network call is one bar, from its request to its response; the method and the image icon are shown when the bar is wide enough;
- database queries and analytics events have no duration: they are diamonds;
- a failed call (HTTP 4xx or 5xx) is red;
- events that do not overlap share the same row, to keep the graph short.

The filters (search text, sources, network filters) apply to both views. The other sources (accessibility, WebView, crashes, tables, deeplinks, ADB) only appear in the list.

| Action | Result |
|---|---|
| Click a bar or a diamond | Opens the event in the detail panel, and selects it in the list too |
| Mouse wheel | Scrolls the lanes |
| Ctrl / Cmd + wheel, `+` / `-` buttons | Zooms around the pointer |
| Shift + wheel, drag | Moves along the time axis |
| **Fit** | Shows the whole session again and follows the new events |
| `‹` / `›` | Previous / next session |
