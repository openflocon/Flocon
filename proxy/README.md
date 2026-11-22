# flocon-proxy

HTTP(S) MITM proxy shipped with Flocon Desktop (see `docs/proxy.md`). It reports the traffic to the desktop as a regular Flocon client, one device per phone IP, using the `network` plugin messages.

```sh
bun install
bun run dev        # runs against a local Flocon Desktop (wss://127.0.0.1:9023/)
bun run build:all  # compiles the binaries into composeApp/appResources/<platform>/
```

The binaries are not committed: the release build must run `bun run build:all` before packaging, and sign the macOS binaries with the app.
