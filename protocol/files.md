# Files

`plugin`: `files`

| Direction        | `method`              | Body                   |
|------------------|-----------------------|------------------------|
| Desktop → Mobile | `listFiles`           | `ListFilesRequest`     |
| Desktop → Mobile | `deleteFile`          | `DeleteFileRequest`    |
| Desktop → Mobile | `deleteFiles`         | `DeleteFilesRequest`   |
| Desktop → Mobile | `deleteFolderContent` | `DeleteFolderContentRequest` |
| Desktop → Mobile | `getFile`             | `GetFileRequest`       |
| Mobile → Desktop | `listFiles`           | `FilesResult`          |
| Mobile → Desktop | HTTP `POST /upload`   | multipart (`getFile` response) |

### Constant paths

When `isConstantPath` / `isConstantParentPath` is `true`, the path is an alias resolved by the mobile:

| Path     | Android         |
|----------|-----------------|
| `files`  | `context.filesDir` |
| `caches` | `context.cacheDir` |

Otherwise the path is an absolute path on the device.

## `listFiles` (Desktop → Mobile)

### `ListFilesRequest`

| Key               | Type      | Description                               |
|-------------------|-----------|-------------------------------------------|
| `requestId`       | `String`  | Request id                                |
| `path`            | `String`  | Folder to list                            |
| `isConstantPath`  | `Boolean` | See [Constant paths](#constant-paths)     |
| `withFoldersSize` | `Boolean` | Compute folder sizes (default `false`)    |

```json
{ "requestId": "b1e9…", "path": "files", "isConstantPath": true, "withFoldersSize": true }
```

## `deleteFile` (Desktop → Mobile)

### `DeleteFileRequest`

| Key                    | Type      | Description                                    |
|------------------------|-----------|------------------------------------------------|
| `requestId`            | `String`  | Request id                                     |
| `parentPath`           | `String`  | Folder listed again after deletion             |
| `filePath`             | `String`  | Absolute path of the file to delete            |
| `isConstantParentPath` | `Boolean` | Applies to `parentPath`                        |

```json
{
  "requestId": "b1e9…",
  "parentPath": "files",
  "filePath": "/data/user/0/com.example.app/files/log.txt",
  "isConstantParentPath": true
}
```

## `deleteFiles` (Desktop → Mobile)

### `DeleteFilesRequest`

| Key                    | Type           | Description                          |
|------------------------|----------------|--------------------------------------|
| `requestId`            | `String`       | Request id                           |
| `parentPath`           | `String`       | Folder listed again after deletion   |
| `filePaths`            | `List<String>` | Absolute paths of files to delete    |
| `isConstantParentPath` | `Boolean`      | Applies to `parentPath`              |

```json
{
  "requestId": "b1e9…",
  "parentPath": "/data/user/0/com.example.app/files/logs",
  "filePaths": [
    "/data/user/0/com.example.app/files/logs/1.txt",
    "/data/user/0/com.example.app/files/logs/2.txt"
  ],
  "isConstantParentPath": false
}
```

## `deleteFolderContent` (Desktop → Mobile)

### `DeleteFolderContentRequest`

| Key              | Type      | Description                            |
|------------------|-----------|----------------------------------------|
| `requestId`      | `String`  | Request id                             |
| `path`           | `String`  | Folder to empty (then listed again)    |
| `isConstantPath` | `Boolean` | See [Constant paths](#constant-paths)  |

```json
{ "requestId": "b1e9…", "path": "caches", "isConstantPath": true }
```

## `listFiles` (Mobile → Desktop)

Response to `listFiles`, `deleteFile`, `deleteFiles` and `deleteFolderContent` (content of the folder after the operation).

### `FilesResult`

| Key         | Type         | Description         |
|-------------|--------------|---------------------|
| `requestId` | `String`     | Same as the request |
| `files`     | `List<File>` | Folder content      |

### `File`

| Key            | Type      | Description                                   |
|----------------|-----------|-----------------------------------------------|
| `name`         | `String`  | File name                                     |
| `path`         | `String`  | Absolute path                                 |
| `isDirectory`  | `Boolean` | `true` for folders                            |
| `size`         | `Long`    | Size in bytes (folders: `0` unless `withFoldersSize`) |
| `lastModified` | `Long`    | Last modification timestamp (ms)              |

```json
{
  "requestId": "b1e9…",
  "files": [
    {
      "name": "logs",
      "isDirectory": true,
      "path": "/data/user/0/com.example.app/files/logs",
      "size": 20480,
      "lastModified": 1729260000000
    },
    {
      "name": "config.json",
      "isDirectory": false,
      "path": "/data/user/0/com.example.app/files/config.json",
      "size": 512,
      "lastModified": 1729250000000
    }
  ]
}
```

## `getFile` (Desktop → Mobile)

### `GetFileRequest`

| Key         | Type     | Description                       |
|-------------|----------|-----------------------------------|
| `requestId` | `String` | Request id                        |
| `path`      | `String` | Absolute path of the file         |

```json
{ "requestId": "c4d2…", "path": "/data/user/0/com.example.app/files/config.json" }
```

### Response: HTTP upload (Mobile → Desktop)

The file is **not** sent through the websocket but uploaded with `POST http://<host>:9024/upload` (`multipart/form-data`, header `User-Agent: Flocon`).

| Part             | Type        | Description                         |
|------------------|-------------|-------------------------------------|
| `remotePath`     | form field  | `path` from the request             |
| `requestId`      | form field  | `requestId` from the request        |
| `deviceId`       | form field  | Same as the websocket envelope      |
| `appPackageName` | form field  | Same as the websocket envelope      |
| `appInstance`    | form field  | Same as the websocket envelope      |
| `file`           | file        | File content (`application/octet-stream`) |

```bash
curl -X POST http://localhost:9024/upload \
  -H "User-Agent: Flocon" \
  -F remotePath=/data/user/0/com.example.app/files/config.json \
  -F requestId=c4d2… \
  -F deviceId=emulator-5554 \
  -F appPackageName=com.example.app \
  -F appInstance=1729260000000 \
  -F file=@config.json
```
