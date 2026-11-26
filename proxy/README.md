# flocon-proxy

HTTP(S) MITM proxy of Flocon Desktop (see `docs/proxy.md`). It reports the traffic to the desktop as a regular Flocon client, one device per phone IP, using the `network` plugin messages.

How Flocon runs it:
1. With the Node.js (18+) of the computer: the JS bundle `composeApp/src/desktopMain/resources/proxy/flocon-proxy.mjs` (~5 MB) is shipped in the app.
2. Without Node: the standalone binary of the platform (~100 MB, Bun inside), downloaded once from the GitHub release of the same version (`flocon-proxy-<platform>` + `.sha256`) into `~/.flocon/proxy/<version>/`.

```sh
bun install
bun run dev                 # runs src/index.ts with Bun against a local Flocon Desktop (wss://127.0.0.1:9023/)
bun run dev:node            # builds the bundle and runs it with Node
bun run build:node          # the bundle shipped in the app (needed before ./gradlew :composeApp:run)
bun run build:macos-arm64   # a standalone binary into dist/ (macos-x64, windows-x64, linux-x64, linux-arm64)
```

Nothing built here is committed: the release workflow (`.github/workflows/release.yml`) builds the bundle and the binary of each platform, and publishes the binaries as release assets.

Design, status and next steps (handoff for agents): `internal-docs/plans/2026-11-22-proxy-mode.md`.
