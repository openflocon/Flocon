---
title: Migration
description: Breaking changes between Flocon versions.
---

# Migration

## Encrypted connection (TLS)

The SDK now connects to the desktop with `wss://` / `https://` instead of `ws://` / `http://`.

- The desktop no longer serves the plain `ws://` and `http://` ports: **update the SDK and Flocon Desktop together**. An old SDK cannot connect to a new desktop, and a new SDK cannot connect to an old desktop.
- The cleartext rule for `localhost` / `127.0.0.1`, and the one `flocon-connect` used to add for any IP address, are no longer needed. You can remove them from your `network_security_config.xml`.
- The desktop certificate is self-signed and is not verified by the SDK: the connection is encrypted, not authenticated.
- `curl` on the upload endpoint now needs `-k` (see [protocol/files.md](https://github.com/openflocon/Flocon/blob/main/protocol/files.md)).
