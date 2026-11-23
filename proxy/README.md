# flocon-proxy

HTTP(S) MITM proxy shipped with Flocon Desktop (see `docs/proxy.md`). It reports the traffic to the desktop as a regular Flocon client, one device per phone IP, using the `network` plugin messages.

```sh
bun install
bun run dev        # runs against a local Flocon Desktop (wss://127.0.0.1:9023/)
bun run build:all  # compiles the binaries into composeApp/appResources/<platform>/
bun run build:macos-arm64  # or only one platform (macos-x64, windows-x64, linux-x64, linux-arm64)
```

The binaries are not committed: the release workflow (`.github/workflows/release.yml`) runs `bun run build:<platform>` on each runner before packaging. In development, run it once before `./gradlew :composeApp:run` (the binary is copied into the app resources when the run starts).

Design, status and next steps (handoff for agents): `internal-docs/plans/2026-11-22-proxy-mode.md`.
