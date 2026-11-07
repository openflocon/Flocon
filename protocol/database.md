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
| `id`   | `String` | Unique database id (used in `query` and `log_query`) |
| `name` | `String` | Display name, never used to identify a database |

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
| `dbId`      | `String`        | Id of the database (the `id` sent in `GetDatabases`) |
| `sqlQuery`  | `String`        | Executed SQL                     |
| `bindArgs`  | `List<String>?` | Bound arguments (`toString()`)   |
| `timestamp` | `Long`          | Execution timestamp (ms)         |
| `transactionId` | `String?`   | Optional. Same value for all the queries of a transaction, `BEGIN` and `COMMIT` / `ROLLBACK` included, the ones of the transactions nested in it too. Absent outside of a transaction, and with SDKs that don't send it |
| `nestedTransactionId` | `String?` | Optional. Id of the transaction nested in `transactionId` (a savepoint, or a nested `BEGIN` of Room 2) the query runs in, its `SAVEPOINT` and `RELEASE` included. Absent in the outermost transaction |
| `parentTransactionId` | `String?` | Optional. Id of the transaction that holds `nestedTransactionId`: another nested transaction, or `transactionId` for a transaction nested directly in the outermost one. Sent with `nestedTransactionId` |
| `transactionDepth` | `Int?` | Optional. `1` in a transaction nested in the outermost one, `2` in one nested in it... Absent in the outermost transaction |

```json
{
  "dbId": "app_database",
  "sqlQuery": "SELECT * FROM user WHERE id = ?",
  "bindArgs": ["42"],
  "timestamp": 1729260000000
}
```

The desktop groups the logs sharing a `transactionId` in one row, and the logs sharing a `nestedTransactionId` in a row
of that row. The ids are only opaque identifiers (the SDK sends random UUIDs), unique per transaction, so a client must
not give them a meaning. The three nested fields are optional and independent of `transactionId`: an older desktop
ignores them and shows a nested transaction's queries with the outermost one's.

```json
{
  "dbId": "app_database",
  "sqlQuery": "INSERT INTO user (name) VALUES (?)",
  "bindArgs": ["flo"],
  "timestamp": 1729260000100,
  "transactionId": "0b8f4c6e-5c3b-4f0e-9a52-3f1d2f7a9a10"
}
```

A query run in a savepoint of that transaction:

```json
{
  "dbId": "app_database",
  "sqlQuery": "INSERT INTO post (title) VALUES (?)",
  "bindArgs": ["hello"],
  "timestamp": 1729260000200,
  "transactionId": "0b8f4c6e-5c3b-4f0e-9a52-3f1d2f7a9a10",
  "nestedTransactionId": "6d1c0f52-72b2-4a53-b0a3-2f6a7d3f9c11",
  "parentTransactionId": "0b8f4c6e-5c3b-4f0e-9a52-3f1d2f7a9a10",
  "transactionDepth": 1
}
```
