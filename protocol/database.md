# Database

`plugin`: `database`

| Direction        | `method`       | Body                   |
|------------------|----------------|------------------------|
| Desktop → Mobile | `get_databases` | `""` (empty)           |
| Mobile → Desktop | `get_databases` | `List<Database>`       |
| Desktop → Mobile | `query`        | `QueryRequest`         |
| Mobile → Desktop | `query`        | `QueryResult`          |
| Mobile → Desktop | `log_query`     | `QueryLog`             |

## `get_databases`

Desktop → Mobile: empty body. Mobile → Desktop: list of available databases. Also sent by the mobile on connection and when a database is registered.

### `Database`

| Key    | Type     | Description                       |
|--------|----------|-----------------------------------|
| `id`   | `String` | Database id (used in `query`)     |
| `name` | `String` | Display name                      |

```json
[
  { "id": "app_database", "name": "app_database" },
  { "id": "cache.db", "name": "cache.db" }
]
```

## `query` (Desktop → Mobile)

### `QueryRequest`

| Key         | Type     | Description             |
|-------------|----------|-------------------------|
| `requestId` | `String` | Request id              |
| `database`  | `String` | Database `id`           |
| `query`     | `String` | SQL to execute          |

```json
{ "requestId": "3f2b…", "database": "app_database", "query": "SELECT id, name FROM user LIMIT 2" }
```

## `query` (Mobile → Desktop)

### `QueryResult`

| Key         | Type     | Description                                    |
|-------------|----------|------------------------------------------------|
| `requestId` | `String` | Same as the request                            |
| `result`    | `String` | `QueryResultWrapper` **serialized as a string** |

### `QueryResultWrapper`

| Key    | Type     | Description                                                     |
|--------|----------|-----------------------------------------------------------------|
| `type` | `String` | `Select`, `Insert`, `UpdateDelete`, `RawSuccess`, `Error`        |
| `body` | `String` | Result object for this `type`, **serialized as a string**        |

### Result `body` per `type`

| `type`         | Key            | Type                   | Description                         |
|----------------|----------------|------------------------|-------------------------------------|
| `Select`       | `columns`      | `List<String>`         | Column names                        |
|                | `values`       | `List<List<String?>>`  | Rows; each row = values in `columns` order |
| `Insert`       | `insertedId`   | `Long`                 | Inserted row id                     |
| `UpdateDelete` | `affectedCount`| `Int`                  | Number of affected rows             |
| `RawSuccess`   | –              | –                      | Empty object                        |
| `Error`        | `message`      | `String`               | Error message                       |
|                | `originalSql`  | `String`               | SQL that failed                     |

The inner `body` may also contain a `type` key holding the Kotlin class name; it can be ignored.

### Example (nested strings decoded)

```json
{
  "requestId": "3f2b…",
  "result": {
    "type": "Select",
    "body": {
      "columns": ["id", "name"],
      "values": [
        ["1", "Florent"],
        ["2", null]
      ]
    }
  }
}
```

On the wire:

```json
{"requestId":"3f2b…","result":"{\"type\":\"Select\",\"body\":\"{\\\"columns\\\":[\\\"id\\\",\\\"name\\\"],\\\"values\\\":[[\\\"1\\\",\\\"Florent\\\"],[\\\"2\\\",null]]}\"}"}
```

Error example (`result.body` decoded):

```json
{ "message": "no such table: usr (code 1 SQLITE_ERROR)", "originalSql": "SELECT * FROM usr" }
```

## `log_query` (Mobile → Desktop)

Sent for each query executed by the app (when query logging is enabled).

### `QueryLog`

| Key         | Type            | Description                      |
|-------------|-----------------|----------------------------------|
| `dbName`    | `String`        | Database name                    |
| `sqlQuery`  | `String`        | Executed SQL                     |
| `bindArgs`  | `List<String>?` | Bound arguments (`toString()`)   |
| `timestamp` | `Long`          | Execution timestamp (ms)         |

```json
{
  "dbName": "app_database",
  "sqlQuery": "SELECT * FROM user WHERE id = ?",
  "bindArgs": ["42"],
  "timestamp": 1729260000000
}
```
