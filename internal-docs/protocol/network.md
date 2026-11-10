# Network

`plugin`: `network`

| Direction        | `method`                 | Body                     |
|------------------|--------------------------|--------------------------|
| Mobile → Desktop | `log_network_call_request`  | `NetworkRequest`         |
| Mobile → Desktop | `log_network_call_response` | `NetworkResponse`        |
| Mobile → Desktop | `log_websocket_event`      | `WebSocketEvent`         |
| Mobile → Desktop | `register_websocket_ids`   | `List<String>`           |
| Desktop → Mobile | `setup_mocks`             | `List<Mock>`             |
| Desktop → Mobile | `setup_bad_network_config`  | `BadNetworkConfig`       |
| Desktop → Mobile | `websocket_mock_message`   | `WebSocketMockMessage`   |

## `log_network_call_request` (Mobile → Desktop)

Sent when a call starts. Request and response are linked by `floconCallId`.

### `NetworkRequest`

| Key                 | Type                  | Description                              |
|---------------------|-----------------------|------------------------------------------|
| `floconCallId`      | `String`              | Unique call id (UUID)                    |
| `floconNetworkType` | `String`              | `http` or `grpc`                         |
| `isMocked`          | `Boolean`             | `true` if answered by a mock             |
| `url`               | `String`              | Full url (gRPC: server authority)        |
| `method`            | `String`              | `GET`, `POST`, … (gRPC: full method name) |
| `startTime`         | `Long`                | Start timestamp (ms)                     |
| `requestHeaders`    | `Map<String, String>` | Request headers                          |
| `requestBody`       | `String?`             | Request body                             |
| `requestSize`       | `Long?`               | Body size in bytes                       |

GraphQL is not a separate type: the desktop detects it from an `http` request body (`query`, `operationName`, persisted queries).

```json
{
  "floconCallId": "1e7b6f2c-0a0e-4d9a-9d3b-2f4c5a6b7c8d",
  "floconNetworkType": "http",
  "isMocked": false,
  "url": "https://api.example.com/users?page=1",
  "method": "GET",
  "startTime": 1729260000000,
  "requestBody": null,
  "requestHeaders": { "Accept": "application/json", "Authorization": "Bearer eyJ…" },
  "requestSize": 0
}
```

## `log_network_call_response` (Mobile → Desktop)

Sent when a call ends (success or failure), ~200 ms after the request message.

### `NetworkResponse`

| Key                   | Type                   | Description                                         |
|-----------------------|------------------------|-----------------------------------------------------|
| `floconCallId`        | `String`               | Same as the request                                 |
| `floconNetworkType`   | `String`               | `http` or `grpc`                                    |
| `isMocked`            | `Boolean`              | `true` if answered by a mock                        |
| `durationMs`          | `Double`               | Call duration                                       |
| `responseHttpCode`    | `Int?`                 | HTTP status (`http`)                                |
| `responseGrpcStatus`  | `String?`              | gRPC status, ex: `OK` (`grpc`)                      |
| `responseContentType` | `String?`              | Content type                                        |
| `responseBody`        | `String?`              | Response body                                       |
| `responseSize`        | `Long?`                | Body size in bytes                                  |
| `responseHeaders`     | `Map<String, String>`  | Response headers                                    |
| `requestHeaders`      | `Map<String, String>?` | Final request headers, if known only at this point  |
| `responseError`       | `String?`              | Error message when the call failed (no response)    |
| `isImage`             | `Boolean`              | `true` if the body is an image                      |

Success:

```json
{
  "floconCallId": "1e7b6f2c-0a0e-4d9a-9d3b-2f4c5a6b7c8d",
  "durationMs": 182.4,
  "floconNetworkType": "http",
  "isMocked": false,
  "responseHttpCode": 200,
  "responseGrpcStatus": null,
  "responseContentType": "application/json",
  "responseBody": "{\"users\":[{\"id\":1}]}",
  "responseSize": 20,
  "responseHeaders": { "Content-Type": "application/json" },
  "requestHeaders": null,
  "responseError": null,
  "isImage": false
}
```

Failure:

```json
{
  "floconCallId": "5a6b7c8d-0a0e-4d9a-9d3b-2f4c1e7b6f2c",
  "durationMs": 10012.0,
  "floconNetworkType": "http",
  "isMocked": false,
  "responseHttpCode": null,
  "responseGrpcStatus": null,
  "responseContentType": null,
  "responseBody": null,
  "responseSize": null,
  "responseHeaders": {},
  "requestHeaders": null,
  "responseError": "java.net.SocketTimeoutException: timeout",
  "isImage": false
}
```

## `log_websocket_event` (Mobile → Desktop)

### `WebSocketEvent`

| Key         | Type      | Description                                                        |
|-------------|-----------|--------------------------------------------------------------------|
| `id`        | `String`  | Unique event id                                                    |
| `event`     | `String`  | `open`, `sent`, `received`, `closing`, `closed`, `error`           |
| `url`       | `String`  | WebSocket url                                                      |
| `size`      | `Long`    | Message size in bytes                                              |
| `timestamp` | `Long`    | Event timestamp (ms)                                               |
| `message`   | `String?` | Message content (`sent` / `received`)                              |
| `error`     | `String?` | Error message (`error`)                                            |

```json
{
  "id": "0f9e8d7c-6b5a-4c3d-2e1f-0a9b8c7d6e5f",
  "event": "received",
  "url": "wss://ws.example.com/chat",
  "size": 27,
  "timestamp": 1729260000000,
  "message": "{\"type\":\"ping\",\"id\":12}",
  "error": null
}
```

## `register_websocket_ids` (Mobile → Desktop)

Ids of websockets that accept mocked messages (see `websocket_mock_message`). Sent on connection and on each new listener. Body is a **JSON array of strings**.

```json
["chat", "notifications"]
```

## `setup_mocks` (Desktop → Mobile)

Replaces **all** the mocks on the device (persisted by the mobile). Body is a **JSON array** of `Mock`; `[]` removes all mocks.

### `Mock`

| Key           | Type          | Description       |
|---------------|---------------|-------------------|
| `expectation` | `Expectation` | Matching rule     |
| `response`    | `MockResponse`| Returned response |

### `Expectation`

| Key          | Type     | Description                            |
|--------------|----------|----------------------------------------|
| `urlPattern` | `String` | Regex matched against the full url     |
| `method`     | `String` | `GET`, `POST`, … or `*` for any method |

### `MockResponse`

Either a **body response** (`httpCode` set) or an **exception** (`errorException` set).

| Key              | Type                   | Description                                         |
|------------------|------------------------|-----------------------------------------------------|
| `httpCode`       | `Int?`                 | HTTP status                                         |
| `body`           | `String?`              | Response body                                       |
| `mediaType`      | `String?`              | Content type                                        |
| `headers`        | `Map<String, String>?` | Response headers                                    |
| `delay`          | `Long?`                | Delay before answering (ms)                         |
| `errorException` | `String?`              | Exception class to throw, ex: `java.io.IOException` |

```json
[
  {
    "expectation": { "urlPattern": "https://api\\.example\\.com/users.*", "method": "GET" },
    "response": {
      "httpCode": 200,
      "body": "{\"users\":[]}",
      "mediaType": "application/json",
      "delay": 500,
      "headers": { "X-Mocked": "true" },
      "errorException": null
    }
  },
  {
    "expectation": { "urlPattern": ".*/login", "method": "*" },
    "response": {
      "httpCode": null,
      "body": null,
      "mediaType": null,
      "delay": 0,
      "headers": null,
      "errorException": "java.net.UnknownHostException"
    }
  }
]
```

## `setup_bad_network_config` (Desktop → Mobile)

Simulates a bad network (latency + random errors). Body `{}` disables it.

### `BadNetworkConfig`

| Key                | Type              | Description                                 |
|--------------------|-------------------|---------------------------------------------|
| `latency`          | `Latency`         | Latency settings                            |
| `errorProbability` | `Double`          | Probability (0..1) to return an error       |
| `errors`           | `List<BadError>`  | Possible errors, one is picked by `weight`  |

### `Latency`

| Key                         | Type     | Description                          |
|-----------------------------|----------|--------------------------------------|
| `latencyTriggerProbability` | `Float`  | Probability (0..1) to add latency    |
| `minLatencyMs`              | `Long`   | Min added latency                    |
| `maxLatencyMs`              | `Long`   | Max added latency                    |

### `BadError`

Either a **body error** (`errorCode` …) or an **exception** (`errorException` set).

| Key                | Type      | Description                                  |
|--------------------|-----------|----------------------------------------------|
| `weight`           | `Float`   | Relative probability vs the other errors     |
| `errorCode`        | `Int?`    | HTTP status                                  |
| `errorBody`        | `String?` | Response body                                |
| `errorContentType` | `String?` | Content type                                 |
| `errorException`   | `String?` | Exception class to throw                     |

```json
{
  "latency": { "latencyTriggerProbability": 0.5, "minLatencyMs": 200, "maxLatencyMs": 2000 },
  "errorProbability": 0.1,
  "errors": [
    {
      "weight": 2.0,
      "errorException": null,
      "errorCode": 500,
      "errorBody": "{\"error\":\"server\"}",
      "errorContentType": "application/json"
    },
    {
      "weight": 1.0,
      "errorException": "java.net.SocketTimeoutException",
      "errorCode": null,
      "errorBody": null,
      "errorContentType": null
    }
  ]
}
```

## `websocket_mock_message` (Desktop → Mobile)

Injects a message into a registered websocket, as if received from the server.

### `WebSocketMockMessage`

| Key       | Type     | Description                            |
|-----------|----------|----------------------------------------|
| `id`      | `String` | Websocket id (from `register_websocket_ids`) |
| `message` | `String` | Message content                        |

```json
{ "id": "chat", "message": "{\"type\":\"message\",\"text\":\"hello\"}" }
```
