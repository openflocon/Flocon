# WebView

`plugin`: `WebView`

| Direction        | `method`         | Body                  |
|------------------|------------------|-----------------------|
| Mobile → Desktop | `AddConsoleLogs` | `List<ConsoleLog>`    |
| Mobile → Desktop | `AddPageEvents`  | `List<PageEvent>`     |
| Mobile → Desktop | `AddBridgeCalls` | `List<BridgeCall>`    |

No message from desktop to mobile.

Requests made by the page (`fetch` / `XMLHttpRequest`) are not part of this plugin: they are sent with the [Network](network.md) plugin.

Every item carries `web_view_id`, a stable id chosen by the app for each attached WebView, so the desktop can filter per WebView.

## `AddConsoleLogs` (Mobile → Desktop)

Body is a **JSON array** of `ConsoleLog`.

### `ConsoleLog`

| Key           | Type      | Description                                                |
|---------------|-----------|------------------------------------------------------------|
| `id`          | `String`  | Unique id                                                  |
| `web_view_id` | `String`  | Id of the WebView                                          |
| `url`         | `String?` | Url of the page when the message was emitted               |
| `level`       | `String`  | `log`, `debug`, `info`, `warn`, `error`                    |
| `message`     | `String`  | Console message                                            |
| `source_id`   | `String?` | Script that emitted the message                            |
| `line_number` | `Int?`    | Line in `source_id`                                        |
| `created_at`  | `Long`    | Timestamp (ms)                                             |

## `AddPageEvents` (Mobile → Desktop)

Body is a **JSON array** of `PageEvent`.

### `PageEvent`

| Key           | Type      | Description                                                                          |
|---------------|-----------|--------------------------------------------------------------------------------------|
| `id`          | `String`  | Unique id                                                                            |
| `web_view_id` | `String`  | Id of the WebView                                                                    |
| `url`         | `String?` | Url concerned by the event                                                           |
| `type`        | `String`  | `page_started`, `page_finished`, `navigation`, `error`, `http_error`, `ssl_error`    |
| `message`     | `String?` | Error description                                                                    |
| `status_code` | `Int?`    | HTTP status, for `http_error`                                                        |
| `created_at`  | `Long`    | Timestamp (ms)                                                                       |

## `AddBridgeCalls` (Mobile → Desktop)

Body is a **JSON array** of `BridgeCall`: a call from the page to a native object exposed to JavaScript.

### `BridgeCall`

| Key              | Type      | Description                              |
|------------------|-----------|------------------------------------------|
| `id`             | `String`  | Unique id                                |
| `web_view_id`    | `String`  | Id of the WebView                        |
| `url`            | `String?` | Url of the page                          |
| `interface_name` | `String`  | Name of the exposed object, ex: `Android` |
| `method`         | `String`  | Called method                            |
| `arguments`      | `String?` | Arguments, as text                       |
| `result`         | `String?` | Returned value, as text                  |
| `created_at`     | `Long`    | Timestamp (ms)                           |

### Example

```json
[
  {
    "id": "6c1a2b0e-5c0d-4b5e-9f0a-1e2d3c4b5a69",
    "web_view_id": "checkout",
    "url": "https://example.com/pay",
    "level": "error",
    "message": "Uncaught TypeError: x is undefined",
    "source_id": "https://example.com/app.js",
    "line_number": 42,
    "created_at": 1729260000000
  }
]
```
