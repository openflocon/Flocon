# Analytics

`plugin`: `analytics`

| Direction        | `method`   | Body                    |
|------------------|------------|-------------------------|
| Mobile → Desktop | `AddItems` | `List<AnalyticsItem>`   |

No message from desktop to mobile.

## `AddItems` (Mobile → Desktop)

Body is a **JSON array** of `AnalyticsItem`. The SDK currently sends one message per event (array of size 1), but the desktop accepts any size.

### `AnalyticsItem`

| Key                | Type                      | Description                                   |
|--------------------|---------------------------|-----------------------------------------------|
| `id`               | `String`                  | Unique event id                               |
| `analyticsTableId` | `String`                  | Analytics table the event belongs to (ex: `firebase`) |
| `eventName`        | `String`                  | Event name                                    |
| `createdAt`        | `Long`                    | Event timestamp (ms)                          |
| `properties`       | `List<AnalyticsProperty>` | Event properties                              |

### `AnalyticsProperty`

| Key     | Type     | Description    |
|---------|----------|----------------|
| `name`  | `String` | Property name  |
| `value` | `String` | Property value |

### Example

```json
[
  {
    "id": "6c1a2b0e-5c0d-4b5e-9f0a-1e2d3c4b5a69",
    "analyticsTableId": "firebase",
    "eventName": "screen_view",
    "createdAt": 1729260000000,
    "properties": [
      { "name": "screen_name", "value": "Home" },
      { "name": "user_id", "value": "42" }
    ]
  }
]
```
